package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.bdm.api.entity.ComputeTewcLayerResultVO;
import com.chinatower.bdm.api.entity.MineralsLayerReqVO;
import com.chinatower.bdm.api.entity.MineralsLayerVO;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.GeoUtils;
import com.chinatower.bdm.kafka.MessageKafkaProducer;
import com.chinatower.bdm.modules.grid.config.GeoServerProperties;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeFieldsStyleMapper;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.core.dto.bdm.LayerResponseDTO;
import com.chinatower.common.core.dto.bdm.UpdateAreaDTO;
import com.chinatower.common.core.enums.TkqCkqDMEnums;
import com.chinatower.common.core.text.Convert;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.DeleteLayerReq;
import com.chinatower.gis.api.entity.LayerInfoAddDTO;
import com.chinatower.gis.api.entity.LayerPGRes;
import com.chinatower.gis.api.entity.gis.EditLayerVO;
import com.chinatower.gis.api.entity.layer.MineralsLayerReq;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.SmsUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.common.ShapeUtil;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeMapper;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.common.core.dto.bdm.LayerAddDTO;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.gis.api.entity.LayerPGDTO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.file.FileUtils;
import com.chinatower.platform.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;
import static com.chinatower.gis.api.entity.layer.LayerConstants.*;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 国土图层管理
 * @data 创建时间：2023/8/11 21:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LayerPgServiceImpl implements LayerPgService {


    private final WgsAnalysisService wgsAnalysisService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final LayerTypeMapper layerTypeMapper;
    private final LayerMapper layerMapper;
    private final GisLayerAPI gisLayerAPI;
    private final GeoServer geoServer;
    private final SystemFeignClient systemFeignClient;
    private final GeoServerProperties geoServerProperties;
    private final MessageKafkaProducer kafkaProducer;
    private static final int pageSize = 500;//每次数量
    private static final int threadPoolQueue = 10000;//线程池队列
    private static final int totalSize = 10 * 1024 * 1024;//每次插入最大数据
    private static String ERROR_MSG = "为必填项，请补充数据后上传.";

    private static final String LAYER_NUM_1 = "1";
    private static final String LAYER_NUM_0 = "0";


    /**
     * 判断图层名称是否重复
     */
    private void judgeLayerCustomName(LayerAddDTO dto) {
        LoginUser loginUser = getLoginUser(dto);
        Integer count = layerMapper.selectCustomNameCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), dto.getCustomName(), "1");
        if (count > 0) {
            throw new CustomException("图层名称已存在，请重新输入！");
        }
    }

    /**
     * 保留原始的
     * @param layerTypeId
     * @return
     */
    public LayerType getLayerType(String layerTypeId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerType layerType = layerTypeMapper.selectById(layerTypeId);
        if (layerType != null && layerType.getCategory() == 0) {
            return layerType;
        }
        if (layerType == null
                || !layerType.getTenantId().equals(loginUser.getUser().getTenantId())
                || !layerType.getIndustryCode().equals(loginUser.getUser().getIndustryCode())
        ) {
            throw new CustomException("图层类型错误");
        }
        return layerType;
    }

    public LayerType getLayerType(String layerTypeId,String layerTypeSimple,LoginUser loginUser) {
        LayerType layerType = layerTypeMapper.selectById(layerTypeId);
        if (layerType == null) {
            layerType = layerTypeMapper.selectByLayerTypeSimple(layerTypeSimple);
        }
        if (layerType != null && layerType.getCategory() == 0) {
            return layerType;
        }
        if (layerType == null
                || !layerType.getTenantId().equals(loginUser.getUser().getTenantId())
                || !layerType.getIndustryCode().equals(loginUser.getUser().getIndustryCode())
        ) {
            throw new CustomException("图层类型错误");
        }
        return layerType;
    }

    /**
     * 修改图层
     */
    private String modifyLayer(Layer copy, LayerAddDTO dto) {
        if (!copy.getCustomName().equals(dto.getCustomName())) {
            judgeLayerCustomName(dto);
            copy.setCustomName(dto.getCustomName());
        }
        copy.setUpdateTime(new Date());
        copy.setModifier(SecurityUtils.getOperator());
        copy.setNotes(dto.getNotes());
        layerMapper.updateById(copy);
        return copy.getId();
    }

    @Override
    public String editLayer(LayerAddDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Layer copy = layerMapper.selectById(dto.getId());
        if (copy == null){
            throw new CustomException("图层不存在");
        }

        if (!copy.getCustomName().equals(dto.getCustomName())) {
            judgeLayerCustomName(dto);
        }
        //获得图层类型
        LayerType layerType = getLayerType(copy.getLayerTypeId());
        //判断是否存在必填bsm
        List<SysGisFieldsDTO> fields = layerTypeMapper.getLayerTypeFields(layerType.getAbbreviation());
        if (CollectionUtils.isEmpty(fields)) {
            throw new CustomException("图层类型字段不能为空");
        }
        List<SysGisFieldsDTO> mustFields = StreamUtils.filter(fields, field -> field.getNotNullFlag() != null && field.getNotNullFlag() != 0);
        if (mustFields == null || mustFields.isEmpty()) {
            mustFields = Lists.newArrayList();
        }
        LayerPGDTO pgdto = getLayerPGDTO(copy.getId(), copy.getLayerName(), fields, layerType, loginUser, mustFields);

        //备份PG图斑表名称
        String bakLayerName = copy.getLayerName();
        Integer maxNumber = layerMapper.bakMaxLayerNameNumber(copy.getLayerName() + "_");
        if (maxNumber != null) {
            bakLayerName = bakLayerName + "_" +  (maxNumber + 1);
        }else {
            bakLayerName = bakLayerName + "_" + 1;
        }
        EditLayerVO vo = new EditLayerVO();
        vo.setLayerName(copy.getLayerName());
        vo.setBakLayerName(bakLayerName);
        //留存旧表
        Result bakResult = gisLayerAPI.editLayer(vo);
        if (bakResult == null || bakResult.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException("备份表失败");
        }
        log.info("备份表结果：{}",JSON.toJSONString(bakResult));

        //新增图斑线程池
        ThreadPoolExecutor executorService = getLayerInsertPg();
        try {
            long time = System.currentTimeMillis();
            //图斑数量
            analysisShp(pgdto, executorService, dto.getFileUrl(), dto.getFileName(),dto.getGeoJsonFlag());
            long end = System.currentTimeMillis();
            log.info("编辑图层-解析图层耗时测试 {} 解析耗时 {}", pgdto.getLayerId(), (end - time));
        } catch (Exception e) {
            log.error("解析图层错误", e);
            gisLayerAPI.editBackLayer(vo);
            if (e instanceof CustomException) {
                throw new CustomException(e.getMessage());
            }
            throw new CustomException("文件解析错误");
        } finally {
            shutdownExecutor(executorService);
        }


        try {
            //删除geoserver 旧图层，在发布
            geoServer.deleteLayer(geoServerProperties.getGuotuWorkspace(), copy.getLayerName());
            Wgs84DTO gis = geoServer.publisherPostGis(geoServerProperties.getGuotuWorkspace(), copy.getLayerName(), pgdto.getGeoType());
            updateMysqlLayerInfo(dto, copy, pgdto, gis, dto.getCustomName(), bakLayerName);
        } catch (Exception e) {
            log.error("更新图层信息错误", e);
            gisLayerAPI.editBackLayer(vo);
            throw e;
        }



        //异步同步面积
        CompletableFuture.runAsync(() -> {
            Result<UpdateAreaDTO> result = gisLayerAPI.layerArea(pgdto);
            log.info("计算面积返回 {}", JSON.toJSONString(result));
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                UpdateAreaDTO data = result.getData();

                Layer updateLayer = new Layer();
                updateLayer.setId(data.getLayerId());
                updateLayer.setPolygonCount(data.getPolygonCount());
                updateLayer.setLayerArea(data.getLayerArea());
                layerMapper.updateById(updateLayer);
            }
        }, threadPoolTaskExecutor);
        return copy.getId();
    }

    private LayerPGDTO getLayerPGDTO(String layerId,
                                     String pgTableName,
                                     List<SysGisFieldsDTO> fields,
                                     LayerType layerType,
                                     LoginUser loginUser,
                                     List<SysGisFieldsDTO> mustFields) {
        LayerPGDTO pgdto = new LayerPGDTO();
        pgdto.setLayerId(layerId);
        pgdto.setAreaField(layerType.getAreaRef());
        pgdto.setFields(fields);
        pgdto.setMustFields(mustFields);
        pgdto.setLayerType(layerType.getId());
        pgdto.setLayerTypeName(layerType.getTypeName());
        pgdto.setLayerTypeSimple(layerType.getAbbreviation());
        pgdto.setInitialTypeName(layerType.getInitialTypeName());
        pgdto.setPgTableName(pgTableName);
        pgdto.setCreator(loginUser.getUsername());
        pgdto.setTenantId(loginUser.getUser().getTenantId());
        pgdto.setAppVerCode(loginUser.getUser().getAppVerCode());
        pgdto.setIndustryCode(loginUser.getUser().getIndustryCode());
        pgdto.setCreateTime(new Date());
        return pgdto;
    }

    private LoginUser getLoginUser(LayerAddDTO dto) {
        LoginUser loginUser = null;
        if (Boolean.FALSE.equals(dto.getGeoJsonFlag())){
            return SecurityUtils.getLoginUser();
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserName(dto.getUserName());
        sysUser.setTenantId(dto.getTenantId());
        sysUser.setIndustryCode(dto.getIndustryCode());
        SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
        if (ObjectUtil.isNotNull(sysConfig)) {
            String platformCode = sysConfig.getConfigValue();
            sysUser.setPlatformCode(platformCode);
        }
        loginUser = new LoginUser(sysUser,null);
        return loginUser;
    }

    @Override
    public String addLayer(LayerAddDTO dto) {
        StopWatch st = new StopWatch("addLayer");
        log.info("新增PG图层 {}", JSON.toJSONString(dto));
        LoginUser loginUser = getLoginUser(dto);
        //获得图层类型
        LayerType layerType = getLayerType(dto.getLayerTypeId(),dto.getLayerTypeSimple(),loginUser);

        if (PGLayerTempEnums.TEWC.getLayerType().equalsIgnoreCase(layerType.getAbbreviation())) {
            throw new CustomException("探而未采图层类型不允许新增");
        }
        if (StringUtils.isEmpty(dto.getLayerTypeId())){
            dto.setLayerTypeId(layerType.getId());
        }

        Layer copy = layerMapper.selectById(dto.getId());

        if (copy != null) {
            if (StringUtils.isEmpty(dto.getFileUrl())){
                return modifyLayer(copy, dto);
            }else {
                return editLayer(dto);
            }
        }

        Layer layer = BeanCopyUtils.copy(dto, Layer.class);
        if (layer == null) {
            throw new CustomException("参数错误");
        }
        //判断图层名称是否重复
        judgeLayerCustomName(dto);

        //图层ID
        String layerId = IdUtils.fastSimpleUUID();
        long nowTime = System.currentTimeMillis();
        String pgTableName = "polygon_" + layerType.getAbbreviation() + "_" + nowTime + "_" + SmsUtils.generateDynamic().toLowerCase();
        log.info("创建表名 {}", pgTableName);

        List<SysGisFieldsDTO> fields = layerTypeMapper.getLayerTypeFields(layerType.getAbbreviation());
        if (fields == null || fields.isEmpty()) {
            throw new CustomException("图层类型字段不能为空");
        }
        List<SysGisFieldsDTO> mustFields = StreamUtils.filter(fields, field -> field.getNotNullFlag() != null && field.getNotNullFlag() != 0);
        if (mustFields == null || mustFields.isEmpty()) {
            mustFields = Lists.newArrayList();
        }
        LayerPGDTO pgdto = getLayerPGDTO(layerId, pgTableName, fields, layerType, loginUser, mustFields);

        st.start("创建模板表");
        Result layerInfo = gisLayerAPI.createModelLayerTable(pgdto);
        if (layerInfo != null && layerInfo.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException("创建模板表失败");
        }
        st.stop();
        //新增图斑线程池
        ThreadPoolExecutor executorService = getLayerInsertPg();
        //图斑数量
        try {
            st.start("图层解析");
            long time = System.currentTimeMillis();
            long count = analysisShp(pgdto, executorService, layer.getFileUrl(), layer.getFileName(),dto.getGeoJsonFlag());
            long end = System.currentTimeMillis();
            log.info("解析图层耗时测试 {} 解析耗时 {} count {}", pgdto.getLayerId(), (end - time),count);
        } catch (Exception e) {
            log.error("解析图层错误", e);
            DeleteLayerReq req = new DeleteLayerReq();
            req.setLayerId(pgdto.getLayerId());
            gisLayerAPI.deleteLayer(req);
            if (e instanceof CustomException) {
                throw new CustomException(e.getMessage());
            }
            throw new CustomException("文件解析错误");
        } finally {
            shutdownExecutor(executorService);
        }
        st.stop();

        st.start("插PG入图层信息表");
        Result insertLayerInfo = gisLayerAPI.insertLayerInfo(pgdto);
        if (insertLayerInfo != null && insertLayerInfo.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException("插PG入图层信息表错误");
        }
        st.stop();

        try {
            st.start("图层发布");
            Wgs84DTO gis = geoServer.publisherPostGis(geoServerProperties.getGuotuWorkspace(), pgTableName, pgdto.getGeoType());
            st.stop();
            st.start("在mysql 保存图层信息");
            insertMysqlLayerInfo(loginUser,pgdto, layer, gis, dto.getCustomName());
            st.stop();
        } catch (Exception e) {
            st.stop();//避免出现异常未关闭
            DeleteLayerReq req = new DeleteLayerReq();
            req.setLayerId(pgdto.getLayerId());
            gisLayerAPI.deleteLayer(req);
            layerMapper.deleteBatchIds(Collections.singletonList(layerId));
            throw e;
        }

        //异步同步面积
        CompletableFuture.runAsync(() -> {
            Result<UpdateAreaDTO> result = gisLayerAPI.layerArea(pgdto);
            log.info("计算面积返回 {}", JSON.toJSONString(result));
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                UpdateAreaDTO data = result.getData();

                Layer updateLayer = new Layer();
                updateLayer.setId(data.getLayerId());
                updateLayer.setPolygonCount(data.getPolygonCount());
                updateLayer.setLayerArea(data.getLayerArea());
                layerMapper.updateById(updateLayer);
            }
        }, threadPoolTaskExecutor);


        log.info("总时间：" + st.prettyPrint());
        return pgdto.getLayerId();
    }

    @Override
    public void sendGeoJson(String value) {
        try {
            kafkaProducer.sendToKafka2("geo_json_layer",value);
        }catch (Exception e){
            log.error("发送信息错误",e);
        }
    }

    /**
     * mysql 保存图层信息
     */
    private void insertMysqlLayerInfo(LoginUser loginUser,LayerPGDTO pgdto, Layer layer, Wgs84DTO gis, String customName) {
        layer.setId(pgdto.getLayerId());
        layer.setDeleteFlag(0);
        layer.setCustomName(customName);
        layer.setTenantId(pgdto.getTenantId());
        layer.setAppVerCode(pgdto.getAppVerCode());
        layer.setIndustryCode(pgdto.getIndustryCode());
        try {
            layer.setCreator(SecurityUtils.getOperator());
        }catch (Exception e){
            log.error("异步上传",e);
            layer.setCreator(loginUser.getUsername());
        }

        layer.setCreateTime(new Date());
        layer.setLayerNamespace(gis.getWorkspace());
        layer.setLayerName(gis.getLayerName());
        layer.setGameKey(gis.getThe_geom());
        layer.setGeoserverUrl(gis.getUrl());
        layer.setViewUrl(gis.getViewUrl());
        layer.setMaxx(gis.getMaxx());
        layer.setMaxy(gis.getMaxy());
        layer.setMinx(gis.getMinx());
        layer.setMiny(gis.getMiny());
        layer.setRootGeoserverUrl(gis.getRootUrl());
        layer.setRootLayerName(gis.getRootName());
        layer.setGeoType(pgdto.getGeoType());
        String code = loginUser.getUser().getPlatformCode();
        layer.setNodeCode(code);
        layer.setCategory(LAYER_NUM_1);
        //判断是否套合图层 判断是否是当前图层类型下的第一个 如果是 nestFlag = 1 如果不是  nestFlag = 0
        List<Layer> layers = layerMapper.selectByTypeIdsAndTenant(Collections.singletonList(layer.getLayerTypeId()), layer.getTenantId(), layer.getIndustryCode(), layer.getNodeCode());
        if (layers.isEmpty()) {
            log.info("设置nest flag = 1");
            layer.setNestFlag(LAYER_NUM_1);
        } else {
            log.info("设置nest flag = 0");
            layer.setNestFlag(LAYER_NUM_0);
        }
        layerMapper.insert(layer);
        //权限增加
        layerMapper.addDataPermissions(layer);
        //推送kafka
        if (LAYER_NUM_1.equals(layer.getNestFlag())) {
            try {
                JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(Collections.singletonList(layer)));
                kafkaProducer.sendToKafka(Constants.LAYER_NEST_KAFKA_TOPIC, jsonArray,3);
                log.info("套合图层推送kafka:{}", JSON.toJSONString(jsonArray));
            } catch (Exception e) {
                log.info("套合图层推送kafka失败,", e);
            }
        }
    }

    private void updateMysqlLayerInfo(LayerAddDTO dto,Layer layer,LayerPGDTO pgdto,Wgs84DTO gis, String customName,String bakLayerName) {
        Layer copy = new Layer();
        copy.setId(pgdto.getLayerId());
        copy.setDeleteFlag(0);
        copy.setFileName(dto.getFileName());
        copy.setFileUrl(dto.getFileUrl());
        copy.setCustomName(customName);
        copy.setTenantId(pgdto.getTenantId());
        copy.setAppVerCode(pgdto.getAppVerCode());
        copy.setIndustryCode(pgdto.getIndustryCode());
        copy.setLayerTypeId(layer.getLayerTypeId());
        copy.setCreator(layer.getCreator());
        copy.setCreateTime(layer.getCreateTime());
        copy.setModifier(SecurityUtils.getOperator());
        copy.setUpdateTime(new Date());
        copy.setLayerNamespace(gis.getWorkspace());
        copy.setLayerName(gis.getLayerName());
        copy.setGameKey(gis.getThe_geom());
        copy.setGeoserverUrl(gis.getUrl());
        copy.setViewUrl(gis.getViewUrl());
        copy.setMaxx(gis.getMaxx());
        copy.setMaxy(gis.getMaxy());
        copy.setMinx(gis.getMinx());
        copy.setMiny(gis.getMiny());
        copy.setRootGeoserverUrl(gis.getRootUrl());
        copy.setRootLayerName(gis.getRootName());
        copy.setGeoType(pgdto.getGeoType());
        String code = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        copy.setNodeCode(code);
        copy.setCategory(LAYER_NUM_1);

        //旧表留痕
        layerMapper.editLayer(copy.getId(),IdUtils.fastSimpleUUID(), bakLayerName);

        //判断是否套合图层 判断是否是当前图层类型下的第一个 如果是 nestFlag = 1 如果不是  nestFlag = 0
        List<Layer> layers = layerMapper.selectByTypeIdsAndTenant(Collections.singletonList(layer.getLayerTypeId()), layer.getTenantId(), layer.getIndustryCode(), layer.getNodeCode());
        if (layers.isEmpty()) {
            copy.setNestFlag(LAYER_NUM_1);
        } else {
            copy.setNestFlag(LAYER_NUM_0);
        }


        layerMapper.insert(copy);
    }


    /**
     * 入库PG 线程池
     */
    public ThreadPoolExecutor getLayerInsertPg() {
        int corePoolSize = 4;
        int maximumPoolSize = 4;
        try {
            AjaxResult corePoolSizeResult = systemFeignClient.getConfValue(CONST_CORE_POOL_SIZE_RESULT);
            AjaxResult maximumPoolResult = systemFeignClient.getConfValue(CONST_MAXIMUM_POOL_RESULT);
            if ("200".equals(String.valueOf(corePoolSizeResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(corePoolSizeResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig != null) {
                    corePoolSize = Integer.parseInt(sysConfig.getConfigValue());
                }
            }
            if ("200".equals(String.valueOf(maximumPoolResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(maximumPoolResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig != null) {
                    maximumPoolSize = Integer.parseInt(sysConfig.getConfigValue());
                }
            }
        } catch (Exception e) {
            log.error(CONST_PARSING_THREAD_POOL_CONFIGURATION_ERROR, e);
        }


        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(threadPoolQueue),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("insert pg layer");
                    return thread;
                });
    }

    /**
     * 安全关闭线程池，避免长时间阻塞
     */
    private void shutdownExecutor(ThreadPoolExecutor executor) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                log.warn("线程池未在5分钟内完成，强制关闭");
                executor.shutdownNow();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("线程池强制关闭后仍未终止");
                }
            }
        } catch (InterruptedException e) {
            log.error("等待线程池关闭被中断", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("关闭入库PG线程池异常", e);
            executor.shutdownNow();
        }
    }


    /**
     * 调用新增入库PG
     *
     * @param list  图斑数据
     * @param pgdto 图层信息
     */
    public void insertPg(List<Map<String, Object>> list, LayerPGDTO pgdto) {
        LayerPGDTO copy = BeanCopyUtils.copy(pgdto, LayerPGDTO.class);
        String uuid = UUID.randomUUID().toString();
        copy.setUuid(uuid);
        StopWatch stopWatch = new StopWatch("analysisAndInsertPg");
        stopWatch.start(" bdm analysisAndInsertPg");
        copy.setList(list);
        Result<LayerPGRes> result = gisLayerAPI.addLayer(copy);
        log.info("图层插入PG库 {}", JSON.toJSONString(result));
        stopWatch.stop();
        log.info("bdm analysisAndInsertPg uuid:{} time:{} ", uuid, stopWatch.getTotalTimeMillis());
    }

    /**
     * 调用新增入库PG
     *
     * @param pgdto 图层信息
     */
    public void analysisAndInsertPg(List<SimpleFeature> featureList, LayerPGDTO pgdto) {
        if (CollectionUtils.isEmpty(featureList)) {
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        //大小限制
        int size = 0;
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(pgdto.getLayerTypeSimple());
        if (PGLayerTempEnums.QTLXDK == tempEnums && StringUtils.isNotEmpty(pgdto.getInitialTypeName())) {
            tempEnums = PGLayerTempEnums.get(pgdto.getInitialTypeName());
        }
        for (SimpleFeature feature : featureList) {
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();
            dataMap.put("id", feature.getID());
            for (Property p : feature.getProperties()) {
                if (p == null || p.getValue() == null) {

                    continue;
                }
                //保存字段及其对应数据
                if (p.getValue() instanceof Date) {
                    Date d = (Date) p.getValue();
                    dataMap.put(p.getName().toString().toLowerCase(Locale.ROOT), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, d));
                } else {
                    String value = Convert.utf8Str(p.getValue());
                    if (StringUtils.isNotEmpty(value)) {
                        value = value.trim();
                    }
                    dataMap.put(p.getName().toString().toLowerCase(Locale.ROOT), value);
                }
                //用于判断大小
                if (!(p.getValue() instanceof MultiPolygon)
                        && !(p.getValue() instanceof Polygon)
                        && !(p.getValue() instanceof MultiPoint)
                        && !(p.getValue() instanceof Point)
                        && !(p.getValue() instanceof LineString)
                        && !(p.getValue() instanceof MultiLineString)
                ) {
                    if (p.getValue() instanceof Date) {
                        try {
                            Date d = (Date) p.getValue();
                            properties.put(p.getName().toString().toLowerCase(Locale.ROOT), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, d));
                        } catch (Exception e) {
                            log.error("时间转换错误", e);
                            properties.put(p.getName().toString().toLowerCase(Locale.ROOT), Convert.utf8Str(p.getValue()));
                        }
                    } else {
                        String value = Convert.utf8Str(p.getValue());
                        if (StringUtils.isNotEmpty(value)) {
                            value = value.trim();
                        }
                        properties.put(p.getName().toString().toLowerCase(Locale.ROOT), value);
                    }
                } else {
                    size += p.getValue().toString().length() * 2;
                }
            }
            if (tempEnums == PGLayerTempEnums.SLZY) {
                Object xbmj = dataMap.get("xbmj");
                if (ObjectUtil.isNull(xbmj)) {
                    dataMap.put("xbmj", dataMap.get("tbmj"));
                }
            } else if (tempEnums == PGLayerTempEnums.XZTB_CESHITABLE) {
                dataMap.put("gdpdj", dataMap.getOrDefault("gdpdjb", ""));
                dataMap.put("gdpdjb2", dataMap.getOrDefault("gdpdjb", ""));
            } else if (tempEnums == PGLayerTempEnums.TKQ) {
                TkqCkqDMEnums kckzdm = TkqCkqDMEnums.get(dataMap.getOrDefault("kckzdm", "").toString());
                if (kckzdm != null) {
                    dataMap.put("category", kckzdm.getAbbreviation());
                }
            } else if (tempEnums == PGLayerTempEnums.CKQ) {
                TkqCkqDMEnums kckzdm = TkqCkqDMEnums.get(dataMap.getOrDefault("kczkzdm", "").toString());
                if (kckzdm != null) {
                    dataMap.put("category", kckzdm.getAbbreviation());
                }
            }

            dataMap.put("properties", JSON.toJSONString(properties));
            list.add(dataMap);
            if (size >= totalSize) {
                log.info("超出大小 当前数据 {} 当前大小(字节) {} 表名 {} ", list.size(), size, pgdto.getPgTableName());
                List<Map<String, Object>> maps = new ArrayList<>(list);
                insertPg(maps, pgdto);
                list.clear();
                size = 0;
            }
        }
        if (!list.isEmpty()) {
            insertPg(list, pgdto);
        }
    }

    public int analysisShp(LayerPGDTO pgdto, ThreadPoolExecutor executorService, String fileUrl, String fileName) {
        return analysisShp(pgdto, executorService, fileUrl, fileName, false);
    }
    /**
     * @return 解析坐标网格
     */
    public int analysisShp(LayerPGDTO pgdto, ThreadPoolExecutor executorService, String fileUrl, String fileName,boolean geoJsonFlag) {
        StopWatch st = new StopWatch();
        log.info("解析网格地址 【{}】", fileUrl);
        //下载保存文件地址
        String saveFileAddress = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + fileName;
        String str = UUID.randomUUID().toString().replace("-", "");
        String filePath = "geojson_" + str;
        if (geoJsonFlag){
            saveFileAddress = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + filePath + GeoServer.PATH + pgdto.getPgTableName() + ".json";
        }
        if (StringUtils.isEmpty(fileUrl)) {
            throw new CustomException("文件地址不能为空");
        }
        if (StringUtils.isEmpty(fileName) && !geoJsonFlag) {
            throw new CustomException("文件名称不能为空");
        }
        log.info("saveFileAddress == " + saveFileAddress);
        Path path = null;
        String geojsonPath = null;
        try {
            st.start("下载网络文件到本地");
            wgsAnalysisService.createDir(wgsAnalysisService.getWgs84Root());
            File file = new File(saveFileAddress);
            geojsonPath = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + filePath;
            if (!file.exists()) {
                if (geoJsonFlag){
                    wgsAnalysisService.createDir(geojsonPath);
                    file.createNewFile();
                    file = FileUtils.getFile(saveFileAddress);
                }
                GeoServer.downloadNet(fileUrl, saveFileAddress);
            }
            st.stop();
            if (!file.exists()) {
                throw new CustomException("文件下载失败");
            }

            st.start("判断文件是否符合规则");
            int count = 0;
            if (geoJsonFlag){
                String shpPath = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + filePath + GeoServer.PATH + pgdto.getPgTableName() + ShapeUtil.SHP;
                GeoUtils.transformGeoJsonToShp(saveFileAddress,shpPath);
                count = readShpGis(pgdto, executorService, shpPath, ShapeUtil.DEF_ENCODE);

            }else {
                //判断文件是否符合规则
                wgsAnalysisService.wgs84ZipRule(saveFileAddress);
                st.stop();
                st.start("解析图层");
                count = getShpData(pgdto, executorService, fileName, saveFileAddress);
                //解析文件数据
                path = Paths.get(saveFileAddress);
            }
            st.stop();
            return count;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析错误", e);
        } finally {
            if (path != null) {//使用完成删除文件
                try {
                    Files.delete(path);
                } catch (Exception e) {
                    log.error("清除文件报错", e);
                }
            }
            if (StringUtils.isNotEmpty(geojsonPath)){
                try {
                    FileUtils.deleteDir(geojsonPath);
                } catch (Exception ee) {
                    log.error("删除解析shp目录报错", ee);
                }
            }
            log.info("解析图层判断:{}", st.prettyPrint());
        }
        throw new CustomException("上传文件不符合规则，请重新上传。");
    }

    /**
     * 解压文件
     *
     * @param fileName        文件名称
     * @param saveFileAddress 文件地址
     */
    private int getShpData(LayerPGDTO pgdto, ThreadPoolExecutor executorService, String fileName, String saveFileAddress) {
        //解压文件
        String filePath = UUID.randomUUID().toString();
        //解压上传的文件
        String fileBaseName = FilenameUtils.getBaseName(fileName);
        String decompressZipPath = wgsAnalysisService.getWgs84Root() + ShapeUtil.PATH + filePath + ShapeUtil.PATH;
        try {
            //解压路径
            log.info("正在解压文件地址 {} 保存路径 {}", saveFileAddress, decompressZipPath);
            ShapeUtil.decompressZip(saveFileAddress, decompressZipPath);
            File file = new File(decompressZipPath + fileBaseName + ShapeUtil.SHP);
            log.info("解析地址 {}", decompressZipPath + fileBaseName + ShapeUtil.SHP);
            if (!file.exists()) {
                throw new CustomException("文件名称需要和解压后的文件名称一致");
            }
            return readShpGis(pgdto, executorService, decompressZipPath + fileBaseName + ShapeUtil.SHP, ShapeUtil.DEF_ENCODE);
        } finally {
            try {
                FileUtils.deleteDir(decompressZipPath);
            } catch (Exception ee) {
                log.error("删除解析shp目录报错", ee);
            }
        }
    }


    /**
     * 读取shape文件
     *
     * @param shpPath shape文件地址，如：D:\data\tmp\test.shp
     * @param encode  文件编码
     * @return shape文件解析后的数据集合
     */
    public int readShpGis(LayerPGDTO pgdto, ThreadPoolExecutor executorService, String shpPath, String encode) {
        DataStore ds = null;
        FeatureIterator<SimpleFeature> features = null;
        //图斑数量
        int count = 0;
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(shpPath)) {
                throw new CustomException("shp文件的路径不能为空，shpPath: " + shpPath);
            }
            if (org.apache.commons.lang3.StringUtils.isEmpty(encode)) {
                throw new CustomException("shp文件的编码不能为空，encode: " + encode);
            }
            long time = System.currentTimeMillis();
            //加载shape文件
            File file = new File(shpPath);
            Map<String, Object> map = new HashMap<>();
            map.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            map.put(ShapefileDataStoreFactory.DBFCHARSET.key, Charset.forName(encode));
            //加载数据源
            ds = DataStoreFinder.getDataStore(map);
            //获取要素源
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = ds.getFeatureSource(ds.getTypeNames()[0]);
            FeatureCollection<SimpleFeatureType, SimpleFeature> collections = fs.getFeatures();
            features = collections.features();
            SimpleFeatureType schema = collections.getSchema();
            String geoTypeStr = schema.getGeometryDescriptor().getType().getName().toString().toUpperCase();
            pgdto.setGeoType(geoTypeStr);
            log.info("文件样式类型 {} {}", shpPath, geoTypeStr);
            PgLayerStyleEnums styleEnums = PgLayerStyleEnums.get(geoTypeStr);
            if (styleEnums == null) {
                throw new CustomException("不存在的类型名称-" + geoTypeStr);
            }
            try {
                CoordinateReferenceSystem srs = schema.getCoordinateReferenceSystem();
                Integer epsg = CRS.lookupEpsgCode(srs, true);
                if (epsg == null || epsg != 4326) {
                    throw new CustomException("上传文件地图数据编码不是4326，请重新上传。");
                }
            } catch (CustomException e) {
                log.error("获取编码错误", e);
                throw e;
            }
            //申明返回结果
            List<SimpleFeature> list = new ArrayList<>();
            //循环获取feature
            if (!features.hasNext()) {
                throw new CustomException("导入数据不能为空！");
            }
            while (features.hasNext()) {

                SimpleFeature feature = features.next();
                list.add(feature);
                //数据校验
                if (count == 0) {
                    authLayerData(pgdto, feature);
                }
                if (list.size() >= pageSize) {
                    List<SimpleFeature> copyList = new ArrayList<>(list);
                    list.clear();
                    executorService.execute(() -> analysisAndInsertPg(copyList, pgdto));
                }
                count++;
            }

            //剩余(最后小于500条)的数据
            if (!list.isEmpty()) {
                List<SimpleFeature> copyList = new ArrayList<>(list);
                executorService.execute(() -> analysisAndInsertPg(copyList, pgdto));
            }
            long end = System.currentTimeMillis();
            log.info("解析图层耗时测试 {} for耗时 {}", pgdto.getLayerId(), (end - time));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析错误", e);
            throw new CustomException("上传文件不符合规则，请重新上传。");
        } finally {
            if (features != null) {
                features.close();
            }
            if (ds != null) {
                ds.dispose();
            }
        }
        return count;
    }

    /**
     * 判断图层对应的必填字段是否有值
     */
    public void authLayerData(LayerPGDTO pgdto, SimpleFeature feature) {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", feature.getID());
        for (Property p : feature.getProperties()) {
            if (p == null
                    || p.getValue() instanceof MultiPolygon
                    || p.getValue() instanceof Polygon
                    || p.getValue() instanceof MultiPoint
                    || p.getValue() instanceof Point
                    || p.getValue() instanceof LineString
                    || p.getValue() instanceof MultiLineString) {
                continue;
            }
            //单个字段值 判断是否上传图层类型
            if (p.getValue() instanceof Date) {
                Date d = (Date) p.getValue();
                dataMap.put(p.getName().toString().toLowerCase(Locale.ROOT), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, d));
            } else {
                String value = Convert.utf8Str(p.getValue());
                if (StringUtils.isNotEmpty(value)) {
                    value = value.trim();
                }
                dataMap.put(p.getName().toString().toLowerCase(Locale.ROOT), value);
            }
        }
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(pgdto.getLayerTypeSimple());
        if (PGLayerTempEnums.QTLXDK == tempEnums && StringUtils.isNotEmpty(pgdto.getInitialTypeName())) {
            tempEnums = PGLayerTempEnums.get(pgdto.getInitialTypeName());
        }
        List<SysGisFieldsDTO> fields = pgdto.getFields();
        Map<String, SysGisFieldsDTO> map = StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, d -> d);
        for (Map.Entry<String, SysGisFieldsDTO> entry : map.entrySet()) {
            Object o = dataMap.get(entry.getKey());
            if (Objects.isNull(o)) {
                continue;
            }
            SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(entry.getValue().getType());
            if (SysLayerTypeFieldsTypeEnums.ONE == typeEnums) {
                try {
                    int v = (int) Double.parseDouble(o.toString());
                } catch (Exception e) {
                    throw new CustomException(entry.getValue().getName() + "字段的数据类型有误，请修改后上传");
                }
            }

            if (SysLayerTypeFieldsTypeEnums.TWO == typeEnums) {
                try {
                    Double.parseDouble(o.toString());
                } catch (Exception e) {
                    throw new CustomException(entry.getValue().getName() + "字段的数据类型有误，请修改后上传");
                }
            }
        }
        //必填字段判断
        Map<String, SysGisFieldsDTO> mustMap = StreamUtils.toMap(pgdto.getMustFields(), SysGisFieldsDTO::getCode, d -> d);
        boolean isFail = false;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, SysGisFieldsDTO> entry : mustMap.entrySet()) {
            if (!"bsm".equalsIgnoreCase(entry.getKey())) {
                Object o = dataMap.get(entry.getKey());
                if (tempEnums == PGLayerTempEnums.SLZY && "xbmj".equals(entry.getKey()) && ObjectUtil.isNull(o)) {
                    o = dataMap.get("tbmj");
                }
                if (ObjectUtil.isNull(o)) {
                    isFail = true;
                    if (pgdto.getType() == 0) {
                        sb.append("(" + entry.getValue().getName() + ")(" + entry.getKey() + ") ");
                    } else {
                        sb.append(entry.getValue().getName()).append(",");
                    }

                } else if (StringUtils.isEmpty(o.toString())) {
                    throw new CustomException(entry.getValue().getName() + "不能为空");
                }

                if (isFail && pgdto.getType() == 0) {
                    throw new CustomException(sb + ERROR_MSG);
                }
            }
        }
        if (isFail && pgdto.getType() == 1) {
            if (sb.length() >= 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            throw new CustomException("当前导入的文件中缺少" + sb + "字段");
        }
    }

    /**
     * @param totalCount 可计算的最大数量
     * @return 探而未采图层ID
     */
    @Override
    public MineralsLayerVO mineralsLayer(int totalCount, MineralsLayerReqVO req) {
        if (req == null) {
            req = new MineralsLayerReqVO();
        }
        if (StringUtils.isEmpty(req.getTenantId())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            //租户ID
            String tenantId = loginUser.getUser().getTenantId();
            String industryCode = loginUser.getUser().getIndustryCode();
            String userName = loginUser.getUser().getUserName();
            String code = loginUser.getUser().getPlatformCode();
            String appVerCode = loginUser.getUser().getAppVerCode();

            req.setTenantId(tenantId);
            req.setIndustryCode(industryCode);
            req.setUserName(userName);
            req.setNodeCode(code);
            req.setAppVerCode(appVerCode);
        }


        log.info("探而未采参数 {}", JSON.toJSONString(req));

        if (StringUtils.isEmpty(req.getTenantId()) || StringUtils.isEmpty(req.getIndustryCode())) {
            throw new CustomException("租户数据不能为空");
        }
        //探而未采图层ID
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setTenantId(req.getTenantId());
        dto.setIndustryCode(req.getIndustryCode());
        dto.setAbbreviation(PGLayerTempEnums.TEWC.getLayerType());
        String layerId = layerMapper.mineralsLayerId(dto);

        dto.setAbbreviation(null);
        Integer layerCount = layerMapper.layerNameCount(dto, Arrays.asList(req.getTkq(), req.getCkq()));
        if (layerCount != 2) {
            throw new CustomException("探矿权/采矿权图层不存在");
        }


        {
            //删除原有的探而未采图层
            Layer layer = layerMapper.selectById(layerId);
            if (StringUtils.isNotEmpty(layerId)) {
                DeleteLayerReq deleteLayerReq = new DeleteLayerReq();
                deleteLayerReq.setLayerId(layerId);
                gisLayerAPI.deleteLayer(deleteLayerReq);
            }
            if (layer != null) {
                geoServer.deleteLayer(geoServerProperties.getGuotuWorkspace(), layer.getLayerName());
            }
            layerMapper.mineralsLayerDelete(req.getTenantId(), req.getIndustryCode());
        }


        log.info("默认的 探矿权 {} 采矿权 {} 租户 {} 行业 {}", req.getTkq(), req.getCkq(), req.getTenantId(), req.getIndustryCode());
        String layerTc = "layer_tc_v2";
        String key = layerTc + ":" + req.getTenantId() + ":" + req.getIndustryCode();
        String tewcLatestKey = layerTc + ":" + req.getTenantId() + ":" + req.getIndustryCode() + ":tewc";

        String count = RedisUtil.get(layerTc, RedisDBType.PRIMARY);

        MineralsLayerVO vo = new MineralsLayerVO();

        //是否超出计算数量
        if (StringUtils.isNotEmpty(count) && Integer.parseInt(count) > totalCount) {
            vo.setStatus(CalculateLayersEnums.THREE.getStatus());
            return vo;
        }

        //SDK 请求判断
        String tkqAndCkq = RedisUtil.get(key, RedisDBType.PRIMARY);
        //需要计算的探而未采是否和正在计算的一样
        boolean exist = (req.getTkq() + "," + req.getCkq()).equals(tkqAndCkq);
        if (exist) {//计算中
            vo.setStatus(CalculateLayersEnums.ZERO.getStatus());
        } else if (StringUtils.isNotEmpty(tkqAndCkq)) {//数据变更
            RedisUtil.set(key, req.getTkq() + "," + req.getCkq(), RedisDBType.PRIMARY);
            //最新应该计算的图层
            RedisUtil.set(tewcLatestKey, JSON.toJSONString(req), RedisDBType.PRIMARY);
            RedisUtil.expire(tewcLatestKey,3,TimeUnit.HOURS, RedisDBType.PRIMARY);

            log.info("正在计算的探而未采 {}", tkqAndCkq);
            vo.setStatus(CalculateLayersEnums.TWO.getStatus());
            return vo;
        }


        String nodeCode = req.getNodeCode();
        String appVerCode = req.getAppVerCode();
        if (vo.getStatus() == null) {
            MineralsLayerReq layerReq = new MineralsLayerReq();
            layerReq.setExploreLayerName(req.getTkq());
            layerReq.setCollectingLayerName(req.getCkq());
            layerReq.setTenantId(req.getTenantId());
            layerReq.setIndustryCode(req.getIndustryCode());
            layerReq.setCreateBy(req.getUserName());
            layerReq.setCount(1);

            log.info("准备计算探而采图层{}", JSON.toJSONString(layerReq));
            ComputeTewcLayerResultVO map = new ComputeTewcLayerResultVO();
            map.setTkq(req.getTkq());
            map.setCkq(req.getCkq());
            map.setTenantId(layerReq.getTenantId());
            map.setIndustryCode(layerReq.getIndustryCode());
            map.setType(2);
            map.setAbbreviation(PGLayerTempEnums.TEWC.getLayerType());
            map.setLayerNameSpace(geoServerProperties.getGuotuWorkspace());
            CompletableFuture.runAsync(() -> {
                Result<LayerInfoAddDTO> result = gisLayerAPI.calculateLayer(layerReq);
                log.info("探而未采图层计算返回 {}", JSON.toJSONString(result));

                if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                    log.info("探而未采图层计算 tkq {} ckq {} tewc {}", layerReq.getExploreLayerName(), layerReq.getCollectingLayerName(), result.getData().getPgTableName());
                    Result mineralsLayerSave = mineralsLayerSave(result.getData(), nodeCode,appVerCode);
                    if (mineralsLayerSave.getCode() == ResultCode.SUCCESS.getCode()) {
                        map.setTewc(result.getData().getPgTableName());
                        map.setStatus(CalculateLayersEnums.ONE.getStatus());
                        map.setLayerId(result.getData().getLayerId());
                        kafkaProducer.sendToKafka(Constants.LAYER_NEST_KAFKA_TOPIC, JSON.toJSONString(map));
                    }else {
                        map.setMsg(result.getMsg());
                        mineralsLayerRedis(key, layerTc, tewcLatestKey, map);
                    }
                } else {
                    map.setMsg(result.getMsg());
                    mineralsLayerRedis(key, layerTc, tewcLatestKey, map);
                }
            }, threadPoolTaskExecutor).exceptionally(e -> {
                log.error("计算探而未采图层失败, e :", e);
                map.setMsg(e.getMessage());
                mineralsLayerRedis(key, layerTc, tewcLatestKey, map);
                return null;
            });

        }
        if (vo.getStatus() == null) {
            vo.setStatus(CalculateLayersEnums.ZERO.getStatus());
        }
        return vo;
    }

    /**
     * 探而未采计算失败
     */
    private void mineralsLayerRedis(String key, String layerTc, String tewcLatestKey,ComputeTewcLayerResultVO map) {
        RedisUtil.delete(key, RedisDBType.PRIMARY);
        RedisUtil.delete(tewcLatestKey, RedisDBType.PRIMARY);
        String tcCount = RedisUtil.get(layerTc, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(tcCount)) {
            if (Integer.parseInt(tcCount) <= 1) {
                RedisUtil.delete(layerTc, RedisDBType.PRIMARY);
            } else {
                RedisUtil.set(layerTc, Integer.parseInt(tcCount) - 1, RedisDBType.PRIMARY);
            }
        }
        map.setStatus(CalculateLayersEnums.FIVE.getStatus());
        kafkaProducer.sendToKafka(Constants.LAYER_NEST_KAFKA_TOPIC, JSON.toJSONString(map));
    }


    @Override
    public Result mineralsLayerSave(LayerInfoAddDTO req, String nodeCode,String appVerCode) {
        log.info("添加探而未采图层数据{}", JSON.toJSONString(req));
        Wgs84DTO gis = null;
        String layerTc = "layer_tc_v2";
        String key = layerTc + ":" + req.getTenantId() + ":" + req.getIndustryCode();
        boolean pushGeoLayer = false;
        try {
            gis = geoServer.publisherPostGis(geoServerProperties.getGuotuWorkspace(), req.getPgTableName(), req.getGeoType());
            pushGeoLayer = true;
        } catch (Exception e) {
            log.error("发布图层失败", e);
        } finally {
            RedisUtil.delete(key, RedisDBType.PRIMARY);
        }

        if (!pushGeoLayer || gis == null) {
            //删除pg图层
            DeleteLayerReq deleteLayerReq = new DeleteLayerReq();
            deleteLayerReq.setLayerId(req.getLayerId());
            gisLayerAPI.deleteLayer(deleteLayerReq);
            return Result.fail("发布图层失败");
        }

        String layerTypeId = layerMapper.mineralsLayerTypeId(req.getTenantId(), req.getIndustryCode());
        log.info("探而未采图层图层ID {}", layerTypeId);
        if (StringUtils.isEmpty(layerTypeId)) {
            return Result.success("探而未采类型为空");
        }

        //删除原有的探而未采图层
        layerMapper.mineralsLayerDelete(req.getTenantId(), req.getIndustryCode());
        //新增图层信息
        Layer layer = BeanCopyUtils.copy(req, Layer.class);
        layer.setId(req.getLayerId());
        layer.setLayerName(req.getPgTableName());
        layer.setLayerTypeId(layerTypeId);
        layer.setCustomName(PGLayerTempEnums.TEWC.getRemark());
        layer.setCreator(req.getCreateBy());
        layer.setPolygonCount(req.getPolygonCount());
        layer.setLayerArea(req.getLayerArea());
        layer.setCreateTime(new Date());
        layer.setDeleteFlag(0);
        layer.setTenantId(req.getTenantId());
        layer.setIndustryCode(req.getIndustryCode());
        layer.setGeoType(req.getGeoType());
        layer.setNestFlag("1");
        layer.setCategory("1");
        layer.setNodeCode(nodeCode);
        layer.setAppVerCode(appVerCode);
        layer.setLayerNamespace(gis.getWorkspace());
        layer.setLayerName(gis.getLayerName());
        layer.setGameKey(gis.getThe_geom());
        layer.setGeoserverUrl(gis.getUrl());
        layer.setViewUrl(gis.getViewUrl());
        layer.setMaxx(gis.getMaxx());
        layer.setMaxy(gis.getMaxy());
        layer.setMinx(gis.getMinx());
        layer.setMiny(gis.getMiny());
        layer.setRootGeoserverUrl(gis.getRootUrl());
        layer.setRootLayerName(gis.getRootName());
        layerMapper.insert(layer);
        layerMapper.addDataPermissions(layer);
        log.info("保存探而未采图层成功 tenant = {} , industryCode = {}", req.getTenantId(), req.getIndustryCode());

        return Result.success("添加成功");
    }
}
