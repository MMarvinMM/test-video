package com.chinatower.bdm.modules.layer.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.constants.WpzfConstants;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.config.GeoServerProperties;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.bdm.modules.layer.domain.VmSatelliteLayerConfig;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeMapper;
import com.chinatower.bdm.modules.layer.mapper.SatelliteLawConfigMapper;
import com.chinatower.bdm.modules.layer.mapper.VmLayerTemplateConfigMapper;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.bdm.modules.layer.service.SatelliteFilmLayerService;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.vo.SatelliteLawConfigVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.DeleteLayerReq;
import com.chinatower.gis.api.entity.LayerPGDTO;
import com.chinatower.gis.api.entity.TableNameListReq;
import com.chinatower.outdata.api.SendKafkaAPI;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.AlarmTypeEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.SmsUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteFilmLayerServiceImpl implements SatelliteFilmLayerService {

    private final VmLayerTemplateConfigMapper layerTemplateConfigMapper;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final SatelliteLawConfigMapper satelliteLawConfigMapper;
    private final LayerTypeMapper layerTypeMapper;
    private final GisLayerAPI gisLayerAPI;
    private final GisPgAPI gisPgAPI;
    private final LayerPgService layerPgService;
    private final GeoServer geoServer;
    private final GeoServerProperties geoServerProperties;
    private final SendKafkaAPI sendKafkaAPI;


    /**
     * 判断图层名称是否重复
     */
    private void judgeLayerCustomName(String customName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer count = satelliteLawConfigMapper.selectCustomNameCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), customName);
        if (count > 0) {
            throw new CustomException("文件名称已存在");
        }
    }

    /**
     * 获得卫片模板
     */
    public LayerTempConfigVO getLayerType(String layerTypeId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerTempConfigVO layerType = layerTemplateConfigMapper.selectLayerTempById(layerTypeId);
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

    @Override
    public void satelliteFilmLayerUpload(SatelliteFilmLayerUploadVo dto) {
        log.info("上传卫片图层数据 {}", JSON.toJSONString(dto));

        LoginUser loginUser = SecurityUtils.getLoginUser();
        judgeLayerCustomName(dto.getCustomName());
        //图层类型
        LayerTempConfigVO layerType = getLayerType(dto.getLayerTypeId());
//        LayerTempConfigVO  layerType = new LayerTempConfigVO();
//        layerType.setAbbreviation(dto.getLayerTypeSimple());
        //图层ID
        String layerId = IdUtils.fastSimpleUUID();
        long nowTime = System.currentTimeMillis();
        String pgTableName = "polygon_" + layerType.getAbbreviation() + "_" + nowTime + "_" + SmsUtils.generateDynamic().toLowerCase();
        log.info("创建表名 {}", pgTableName);

        List<SysGisFieldsDTO> fields = layerTypeMapper.getLayerTypeFields(layerType.getAbbreviation());
        if (fields == null || fields.isEmpty()) {
            throw new CustomException("图层类型字段不能为空");
        }
        //必填字段
        List<SysGisFieldsDTO> mustFields = StreamUtils.filter(fields, field -> field.getNotNullFlag() != null && field.getNotNullFlag() != 0);
        if (mustFields == null || mustFields.isEmpty()) {
            mustFields = Lists.newArrayList();
        }

        VmSatelliteLayerConfig layer = new VmSatelliteLayerConfig();
        layer.setId(layerId);
        layer.setAlarmTypeId(layerType.getAlarmType());
        layer.setCustomName(dto.getCustomName());
        layer.setLayerTemplateId(dto.getLayerTypeId());
        layer.setFileName(dto.getFileName());
        layer.setFileUrl(dto.getFileUrl());

        LayerPGDTO pgdto = new LayerPGDTO();
        pgdto.setType(1);
        pgdto.setLayerId(layerId);
        pgdto.setAreaField(layerType.getAreaRef());
        pgdto.setFields(fields);
        pgdto.setMustFields(mustFields);
        pgdto.setLayerType(layerType.getId() + "");
        pgdto.setLayerTypeName(layerType.getLayerTempName());
        pgdto.setLayerTypeSimple(layerType.getAbbreviation());
        pgdto.setPgTableName(pgTableName);
        pgdto.setCreator(loginUser.getUser().getUserName());
        pgdto.setTenantId(loginUser.getUser().getTenantId());
        pgdto.setAppVerCode(loginUser.getUser().getAppVerCode());
        pgdto.setIndustryCode(loginUser.getUser().getIndustryCode());
        pgdto.setCreateTime(new Date());

        Result layerInfo = gisLayerAPI.createModelLayerTable(pgdto);
        if (layerInfo != null && layerInfo.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException("创建模板表失败");
        }

        //新增图斑线程池
        ThreadPoolExecutor executorService = layerPgService.getLayerInsertPg();
        //图斑数量
        try {
            long time = System.currentTimeMillis();
            layerPgService.analysisShp(pgdto, executorService, dto.getFileUrl(), dto.getFileName());
            long end = System.currentTimeMillis();
            log.info("卫片执法数据解析图层耗时测试 {} 解析耗时 {}", pgdto.getLayerId(), (end - time));
        } catch (Exception e) {
            log.error("卫片执法数据解析图层错误", e);
            DeleteLayerReq req = new DeleteLayerReq();
            req.setLayerId(pgdto.getLayerId());
            gisLayerAPI.deleteLayer(req);
            if (e instanceof CustomException) {
                throw new CustomException(e.getMessage());
            }
            throw new CustomException("文件解析错误");
        } finally {
            try {
                executorService.shutdown();
                executorService.awaitTermination(12, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("卫片执法数据关闭入库PG线程池超时", e);
            }
        }

        Result insertLayerInfo = gisLayerAPI.insertLayerInfo(pgdto);
        if (insertLayerInfo != null && insertLayerInfo.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException("插PG入图层信息表错误");
        }

        try {
            Wgs84DTO gis = geoServer.publisherPostGis(geoServerProperties.getGuotuWorkspace(), pgTableName, pgdto.getGeoType());
            insertMysqlLayerInfo(pgdto, layer, gis, dto.getCustomName());
        } catch (Exception e) {
            DeleteLayerReq req = new DeleteLayerReq();
            req.setLayerId(pgdto.getLayerId());
            gisLayerAPI.deleteLayer(req);

            SatelliteLawConfigVo vo = new SatelliteLawConfigVo();
            vo.setId(layerId);
            vo.setModifier(loginUser.getUsername());
            satelliteLawConfigMapper.deleteConfig(vo);
            throw e;
        }


        //异步同步面积
        CompletableFuture.runAsync(() -> {
            Result<UpdateAreaDTO> result = gisLayerAPI.layerArea(pgdto);
            log.info("计算面积返回 {}",JSON.toJSONString(result));
            //修改面积
            if (result.getCode() == ResultCode.SUCCESS.getCode()){
                UpdateAreaDTO data = result.getData();
                VmSatelliteLayerConfig updateLayer = new VmSatelliteLayerConfig();
                updateLayer.setId(data.getLayerId());
                updateLayer.setPolygonCount(data.getPolygonCount());
                updateLayer.setLayerArea(data.getLayerArea());
                satelliteLawConfigMapper.update(updateLayer);
            }

        }, threadPoolTaskExecutor);
    }

    @Override
    public void createAlarm(LayerIdVo vo) {
        VmSatelliteLayerConfigDTO dto = satelliteLawConfigMapper.selectByVmSatelliteLayerConfigDTO(vo.getLayerId());
        if (dto == null || dto.getPolygonCount() == null || dto.getPolygonCount() == 0) {
            throw new CustomException("字段类型错误");
        }

        String key = "create_alarm:" + vo.getLayerId();
        if (Boolean.TRUE.equals(RedisUtil.hasKey(key, RedisDBType.PRIMARY))) {
            throw new CustomException("正在生成告警请稍后再试");
        }
        try {

            log.info("准备推送告警图层信息 {}", JSON.toJSONString(dto));
            List<SysGisFieldsDTO> fields = layerTypeMapper.getLayerTypeFields(dto.getAbbreviation());
            if (fields.isEmpty()){
                throw new CustomException("告警模板已被删除");
            }
            RedisUtil.set(key, vo.getLayerId(), RedisDBType.PRIMARY);
            RedisUtil.expire(key, 5, TimeUnit.MINUTES, RedisDBType.PRIMARY);
            Integer count = dto.getPolygonCount();
            int pageSize = 1000;
            int size = count / pageSize + 1;
            for (int i = 1; i <= size; i++) {
                int pageNum = i;
                if (i > 1) {
                    threadPoolTaskExecutor.execute(() -> pushKafka(vo,fields, dto, pageNum, pageSize));
                } else {
                    pushKafka(vo,fields, dto, pageNum, pageSize);
                }
            }
            //修改为已经生成告警
            satelliteLawConfigMapper.updateIsBuildAlarm(vo.getLayerId(), "");

        }finally {
            RedisUtil.delete(key, RedisDBType.PRIMARY);
        }
    }


    private void pushKafka(LayerIdVo vo,List<SysGisFieldsDTO> fields, VmSatelliteLayerConfigDTO dto, Integer pageNum, Integer pageSize) {
        TableNameListReq req = new TableNameListReq();
        req.setLayerId(dto.getLayerId());
        req.setPageNum(pageNum);
        req.setPageSize(pageSize);
        Result<List<Map<String, Object>>> result = gisPgAPI.tableNameList(req);

        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            List<Map<String, Object>> list = result.getData();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            List<LayerAlarmCreateDTO> dtos = new ArrayList<>();
            if (AlarmTypeEnums.DM_4110101.getType().equals(dto.getAlarmType())) {
                alarmType_4110101(fields, dtos, list, dto);
            } else if (AlarmTypeEnums.DM_4110102.getType().equals(dto.getAlarmType())) {
                alarmType_4110102(fields, dtos, list, dto);
            } else if (AlarmTypeEnums.DM_4110103.getType().equals(dto.getAlarmType())) {
                alarmType_4110103(fields, dtos, list, dto);
            } else if (AlarmTypeEnums.DM_4110104.getType().equals(dto.getAlarmType())) {
                alarmType_4110104(fields, dtos, list, dto);
            } else {
                throw new CustomException("未匹配到告警类型");
            }
            dtos.forEach(alarm -> {
                alarm.setIsSecondIdentify(vo.getIsSecondIdentify());
                alarm.setLinkageAction(vo.getLinkageAction());
                alarm.setLinkageCamera(vo.getLinkageCamera());
                alarm.setSelectAlgorithm(vo.getSelectAlgorithm());
            });
            log.info("推送卫片告警数据 {}", JSON.toJSONString(dtos));
            Result alarm = sendKafkaAPI.layerAlarm(dtos);
            if (alarm.getCode() != ResultCode.SUCCESS.getCode()){
                throw new CustomException("告警推送失败");
            }
        }else {
            throw new CustomException("图斑数据不存在");
        }
    }

    /**
     * 生成告警公共参数
     *
     * @param alarmCreateDTO 告警参数
     * @param map            图斑数据
     * @param dto            图层数据
     */
    private void layerAlarmPublic(List<SysGisFieldsDTO> fields, LayerAlarmCreateDTO alarmCreateDTO, Map<String, Object> map, VmSatelliteLayerConfigDTO dto) {
        Map obj = JSON.parseObject(map.get("properties").toString(), Map.class);

        String[] codes = null;
        if (AlarmTypeEnums.DM_4110101.getType().equals(dto.getAlarmType())) {
            codes = WpzfConstants.GDWPZF_CSHTCMB;
        } else if (AlarmTypeEnums.DM_4110102.getType().equals(dto.getAlarmType())) {
            codes = WpzfConstants.KCWPZF_CSHTCMB;
        } else if (AlarmTypeEnums.DM_4110103.getType().equals(dto.getAlarmType())) {
            codes = WpzfConstants.FNHWPZF_CSHTCMB;
        } else if (AlarmTypeEnums.DM_4110104.getType().equals(dto.getAlarmType())) {
            codes = WpzfConstants.FLHWPZF_CSHTCMB;
        }
        if (codes == null) {
            throw new CustomException("未匹配到告警类型");
        }
        List<String> extendCodes = Arrays.asList(codes);

        List<LayerAlarmExtendFieldsDTO> extendFields = new ArrayList<>();
        for (SysGisFieldsDTO fieldsDTO : fields) {
            boolean b = extendCodes.stream().anyMatch(s -> s.equalsIgnoreCase(fieldsDTO.getCode()));
            if (!b) {
                LayerAlarmExtendFieldsDTO d = new LayerAlarmExtendFieldsDTO();
                d.setCode(fieldsDTO.getCode());
                d.setName(fieldsDTO.getName());
                d.setValue(obj.getOrDefault(fieldsDTO.getCode().toLowerCase(Locale.ROOT),""));
                extendFields.add(d);
            }
        }
        Double maxx = (Double) map.get("maxx");
        Double minx = (Double) map.get("minx");
        Double miny = (Double) map.get("miny");
        Double maxy = (Double) map.get("maxy");
        log.info("告警类型 {} 扩展字段参数 {}", dto.getAlarmType(), JSON.toJSONString(extendFields));
        if (!extendFields.isEmpty()){
            alarmCreateDTO.setDescription(JSON.toJSONString(extendFields));
        }
        alarmCreateDTO.setWarningTypeId(dto.getAlarmType());
        alarmCreateDTO.setAlarmTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,dto.getCreateTime()));
        if (AlarmTypeEnums.DM_4110103.getType().equals(dto.getAlarmType()) || AlarmTypeEnums.DM_4110104.getType().equals(dto.getAlarmType())){
            alarmCreateDTO.setAlarmTime(map.get("yxsj"));
        }
        alarmCreateDTO.setTenantId(dto.getTenantId());
        alarmCreateDTO.setIndustryCode(dto.getIndustryCode());
        alarmCreateDTO.setAppVerCode(dto.getAppVerCode());
        alarmCreateDTO.setLayerId(dto.getLayerId());
        alarmCreateDTO.setBsm(map.get("bsm"));
        alarmCreateDTO.setCityName(map.get("country"));
        alarmCreateDTO.setCountyName(map.get("county"));
        alarmCreateDTO.setTownName(map.get("town"));
        alarmCreateDTO.setVillageName(map.get("village"));
        alarmCreateDTO.setSpotCenterLongitude(BigDecimalUtils.round(BigDecimalUtils.add(maxx, minx), 2,14));
        alarmCreateDTO.setSpotCenterLatitude(BigDecimalUtils.round(BigDecimalUtils.add(miny, maxy), 2,14));
    }

    /**
     * 矿产卫片执法
     */
    public void alarmType_4110102(List<SysGisFieldsDTO> fields, List<LayerAlarmCreateDTO> dtos, List<Map<String, Object>> list, VmSatelliteLayerConfigDTO dto) {
        for (Map<String, Object> map : list) {
            LayerAlarmCreateDTO alarmCreateDTO = new LayerAlarmCreateDTO();

            layerAlarmPublic(fields, alarmCreateDTO, map, dto);

            alarmCreateDTO.setSpotNo(map.get("tbbh"));
            alarmCreateDTO.setRegionSource(map.get("wply"));
            alarmCreateDTO.setSpotArea(map.get("tbmj"));
            alarmCreateDTO.setSpotPerimeter(map.get("tbzc"));
            dtos.add(alarmCreateDTO);
        }
    }

    /**
     * 耕地卫片执法
     */
    public void alarmType_4110101(List<SysGisFieldsDTO> fields, List<LayerAlarmCreateDTO> dtos, List<Map<String, Object>> list, VmSatelliteLayerConfigDTO dto) {
        for (Map<String, Object> map : list) {
            LayerAlarmCreateDTO alarmCreateDTO = new LayerAlarmCreateDTO();

            layerAlarmPublic(fields, alarmCreateDTO, map, dto);

            alarmCreateDTO.setSpotNo(map.get("tbbh"));
            alarmCreateDTO.setPreType(map.get("qlx"));
            alarmCreateDTO.setPostType(map.get("hlx"));
            alarmCreateDTO.setPreChronology(map.get("qsx"));
            alarmCreateDTO.setPostChronology(map.get("hsx"));
            alarmCreateDTO.setRegionSource(map.get("wply"));
            alarmCreateDTO.setSpotArea(map.get("tbmj"));
            alarmCreateDTO.setSpotPerimeter(map.get("tbzc"));
            dtos.add(alarmCreateDTO);
        }
    }

    /**
     * 非农化卫片执法
     */
    public void alarmType_4110103(List<SysGisFieldsDTO> fields, List<LayerAlarmCreateDTO> dtos, List<Map<String, Object>> list, VmSatelliteLayerConfigDTO dto) {
        for (Map<String, Object> map : list) {
            LayerAlarmCreateDTO alarmCreateDTO = new LayerAlarmCreateDTO();

            layerAlarmPublic(fields, alarmCreateDTO, map, dto);

            alarmCreateDTO.setChangeType(map.get("bhlx"));
            alarmCreateDTO.setArea(map.get("area"));
            alarmCreateDTO.setLandUseType(map.get("tdlx"));
            alarmCreateDTO.setAlarmTime(map.get("yxsj"));
            alarmCreateDTO.setImageTime(map.get("yxsj"));
            alarmCreateDTO.setDetectionFlag(map.get("qs"));
            dtos.add(alarmCreateDTO);
        }
    }

    /**
     * 非粮化卫片执法
     */
    public void alarmType_4110104(List<SysGisFieldsDTO> fields, List<LayerAlarmCreateDTO> dtos, List<Map<String, Object>> list, VmSatelliteLayerConfigDTO dto) {
        for (Map<String, Object> map : list) {
            LayerAlarmCreateDTO alarmCreateDTO = new LayerAlarmCreateDTO();

            layerAlarmPublic(fields, alarmCreateDTO, map, dto);

            alarmCreateDTO.setChangeType(map.get("bhlx"));
            alarmCreateDTO.setArea(map.get("area"));
            alarmCreateDTO.setLandUseType(map.get("tdlx"));
            alarmCreateDTO.setAlarmTime(map.get("yxsj"));
            alarmCreateDTO.setImageTime(map.get("yxsj"));
            alarmCreateDTO.setDetectionFlag(map.get("qs"));
            dtos.add(alarmCreateDTO);
        }
    }

    /**
     * mysql 保存图层信息
     */
    private void insertMysqlLayerInfo(LayerPGDTO pgdto, VmSatelliteLayerConfig layer, Wgs84DTO gis, String customName) {
        try {
            layer.setDeleteFlag("0");
            layer.setCustomName(customName);
            layer.setTenantId(pgdto.getTenantId());
            layer.setAppVerCode(pgdto.getAppVerCode());
            layer.setIndustryCode(pgdto.getIndustryCode());
            layer.setCreator(pgdto.getCreator());
            layer.setCreateTime(new Date());
            layer.setLayerNamespace(gis.getWorkspace());
            layer.setLayerName(gis.getLayerName());
            layer.setGeoserverUrl(gis.getUrl());
            layer.setMaxx(gis.getMaxx());
            layer.setMaxy(gis.getMaxy());
            layer.setMinx(gis.getMinx());
            layer.setMiny(gis.getMiny());
            layer.setRootGeoserverUrl(gis.getRootUrl());
            layer.setGeoType(pgdto.getGeoType());
            String code = SecurityUtils.getLoginUser().getUser().getPlatformCode();
            layer.setAcceptNode(code);
            satelliteLawConfigMapper.insert(layer);
        }catch (Exception e){
            log.error("保存卫片执法数据错误");
            throw new CustomException("保存卫片执法数据错误");
        }


    }
}
