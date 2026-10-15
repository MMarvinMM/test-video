package com.chinatower.bdm.modules.layer.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Date;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.kafka.MessageKafkaProducer;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.config.GeoServerProperties;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.bdm.modules.layer.domain.layerTree;
import com.chinatower.bdm.modules.layer.mapper.*;
import com.chinatower.bdm.modules.layer.service.ILayerService;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.bdm.modules.layer.service.LayerPublishAsyncService;
import com.chinatower.bdm.modules.layer.service.LegendService;
import com.chinatower.common.core.domain.R;
import com.chinatower.common.core.comparator.ChineseComparator;
import com.chinatower.common.core.comparator.ChineseComparator;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.dto.bdm.LayerInfoDTO;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IPolygonPatchConfigService;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.DeleteLayerReq;
import com.chinatower.gis.api.entity.LayerInfoAddDTO;
import com.chinatower.gis.api.entity.LayerPGDTO;
import com.chinatower.gis.api.entity.layer.MineralsLayerReq;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.DESUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.PGLayerTempEnums;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.file.FileUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.IndustryConfDto;
import com.chinatower.system.api.request.file.SysFileInfo;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.annotations.Case;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 图层管理 服务实现类
 * </p>
 *
 * @author ch
 * @since 2023-04-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LayerServiceImpl implements ILayerService {

    private final LegendService legendService;
    private final LayerMapper layerMapper;
    private final LayerTypeMapper layerTypeMapper;
    private final WgsAnalysisService wgsAnalysisService;
    private final IPolygonPatchConfigService polygonPatchConfigService;
    private final LayerPolygonMapper layerPolygonMapper;
    private final GeoServerProperties geoServerProperties;
    private final GisLayerAPI gisLayerAPI;
    private final GeoServer geoServer;
    private final LayerPgService layerPgService;
    private final GisPgAPI gisPgAPI;

    private final LayerPublishAsyncService layerPublishAsyncService;
    private final MessageKafkaProducer kafkaProducer;


    @Value("${geoserver.tiffKeyword:GeoTIFF}")
    private String tiffFile;

    private final String LAYER_CATEGORY_1 = "1";

    private final String LAYER_CATEGORY_2 = "2";

    private final List<String> DEM_DSM_LIST = Arrays.asList("3", "4");

    private final String MIN_X = "minx";
    private final String MAX_X = "maxx";
    private final String MIN_Y = "miny";
    private final String MAX_Y = "maxy";
    /**
     * geoserver获得json数据地址
     */
    private static final String GEOSERVER_BASE_URL = "%s/%s/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=%s&maxFeatures=50&outputFormat=application/json";


    private final String LAYER_TIFF_TYPE_ERR_MSG = "请和铁塔管理员确认url是否正确";

    private final String LAYER_URL_VERSION_PRE = "version=";

    @Override
    @DataPermissions(layerColumn = "l.id")
    public List<LayerResponseDTO> list(LayerQueryDTO dto) {

        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        //标签关联查询需要
        dto.setUserName(loginUser.getUser().getUserName());
        String orderBy = dto.getOrderBy();
        if (StringUtils.isNotEmpty(orderBy)) {
            if (!"createTime".equals(dto.getOrderByColumn()) && !"updateTime".equals(dto.getOrderByColumn()) && !"customName".equals(dto.getOrderByColumn())) {
                throw new CustomException("参数错误");
            }
            if (!"asc".equals(dto.getIsAsc()) && !"desc".equals(dto.getIsAsc())) {
                throw new CustomException("参数错误");
            }
            orderBy = " l." + orderBy;
        }
        if (StringUtils.isNotEmpty(dto.getAbbreviation()) && PGLayerTempEnums.SLZY.getLayerType().equals(dto.getAbbreviation())) {
            orderBy = "l.create_time asc";
        }
        if (StringUtils.isNotEmpty(dto.getCreateBeginTime())) {
            dto.setCreateBeginTime(dto.getCreateBeginTime().trim() + " 00:00:00");
            dto.setCreateEndTime(dto.getCreateEndTime().trim() + " 23:59:59");
        }

        if (StringUtils.isNotEmpty(dto.getUpdateBeginTime())) {
            dto.setUpdateBeginTime(dto.getUpdateBeginTime().trim() + " 00:00:00");
            dto.setUpdateEndTime(dto.getUpdateEndTime().trim() + " 23:59:59");
        }
        List<LayerResponseDTO> list = layerMapper.list(dto, orderBy, loginUser.getUser().getPlatformCode());
        list.forEach(layer -> layer.setRootGeoserverUrl(geoServerProperties.getShowUrl()));
        //查询图层样式
        Map<String, List<LayerTypeStyleMappingVO>> styles = legendService.layerStyles(StreamUtils.toList(list, LayerResponseDTO::getId),loginUser);
        list.forEach(d -> d.setStyles(styles.get(d.getId())));
        return list;
    }

    @Override
    public LayerResponseDTO selectLayerById(String id,String category) {
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setId(id);
        dto.setCategory(category);
        dto.setDataPermissions("");
        log.info("selectLayerById dto图层ID{}", GsonUtils.toJSONString(dto));
        List<LayerResponseDTO> list = layerMapper.list(dto, null, null);
        log.info("selectLayerById list图层ID{}",GsonUtils.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        LayerResponseDTO layer = list.get(0);
        layer.setRootGeoserverUrl(geoServerProperties.getShowUrl());
        SysUser sysUser = new SysUser();
        sysUser.setTenantId(layer.getTenantId());
        sysUser.setPlatformCode(layer.getNodeCode());
        sysUser.setIndustryCode(layer.getIndustryCode());
        //查询图层样式
        try {
            Map<String, List<LayerTypeStyleMappingVO>> styles = legendService.layerStyles(Collections.singletonList(layer.getId()),new LoginUser(sysUser,null));
            layer.setStyles(styles.get(layer.getId()));
        }catch (Exception e){
            log.error("查询样式错误",e);
        }
        return layer;
    }

    @Override
    public Result<List<layerTree>> layerTree(LayerQueryDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        List<layerTree> layerList = layerMapper.layerList(dto);
        List<layerTree> list = new ArrayList<>();
        if (!CollectionUtils.isNotEmpty(layerList) || layerList.isEmpty()){
            return Result.data(new ArrayList<>());
        }else{
            layerTree layerTree = buildLayer(0);
            list.add(layerTree);
        }
        List<layerTree> layerTypeList = layerMapper.layerTypeList(dto);
        Set<String> typeIds = layerList.stream().map(layerTree::getParentId).collect(Collectors.toSet());
        //清除没有图层的图层类型
        layerTypeList = layerTypeList.stream().filter(e -> typeIds.contains(e.getId())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(layerTypeList) && !layerTypeList.isEmpty()){
            layerTree layerTree = buildLayer(1);
            list.add(layerTree);
            layerList.addAll(layerTypeList);
        }
        for (int i = 2; i <= 6; i++) {
            if(typeIds.contains(String.valueOf(i))){
                layerTree layerTree = buildLayer(i);
                list.add(layerTree);
            }
        }
        layerList.addAll(list);
        List<layerTree> layerTrees = buildTree(layerList);

        return Result.data(layerTrees);
    }

    private static layerTree buildLayer(int i) {
        layerTree layerTree = new layerTree();
        layerTree.setId(String.valueOf(i));
        layerTree.setParentId(Constants.ZERO);
        switch (i){
            case 0:
                layerTree.setLabel("全部");
                layerTree.setParentId(null);
                break;
            case 1 :
                layerTree.setLabel("矢量图层");
                break;
            case 2:
                layerTree.setLabel("非矢量图层");
                break;
            case 3:
                layerTree.setLabel("DEM");
                break;
            case 4:
                layerTree.setLabel("DSM");
                break;
            case 5:
                layerTree.setLabel("倾斜摄影");
                break;
            case 6:
                layerTree.setLabel("激光点云");
                break;
            default:
                break;
        }
        return layerTree;
    }

    public static List<layerTree> buildTree(List<layerTree> nodes) {
        Map<String, layerTree> nodeMap = new HashMap<>();
        layerTree rootNodes = new layerTree();

        for (layerTree node : nodes) {
            nodeMap.put(node.getId(), node);
            node.setChildren(new ArrayList<>());
        }
        for (layerTree node : nodes) {
            if (node.getParentId() == null || node.getParentId().isEmpty()) {
                rootNodes = node;
            } else {
                layerTree parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        List<layerTree> list =  new ArrayList<>();
        list.add(rootNodes);
        return list;
    }

    @Override
    public List<LayerResponseDTO> baseLayerList(BaseLayerListQueryDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        dto.setTenantId(loginUser.getUser().getTenantId());
        dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        List<LayerResponseDTO> list = layerMapper.baseLayerList(dto);
        list.forEach(layer -> layer.setRootGeoserverUrl(geoServerProperties.getShowUrl()));
        return list;
    }

    @Override
    public Result<LayerInfoDTO> info(String id) {
        Layer layer = layerMapper.selectById(id);
        if (layer == null) {
            return Result.fail("参数错误");
        }
        SysFileInfo fileDTO = new SysFileInfo();
        fileDTO.setFileUrl(layer.getFileUrl());
        fileDTO.setFileName(layer.getFileName());
        if (LAYER_CATEGORY_1.equals(layer.getCategory())) {
            LayerType layerType = layerTypeMapper.selectById(layer.getLayerTypeId());

            LayerInfoDTO infoDTO = LayerInfoDTO.builder()
                    .customName(layer.getCustomName())
                    .layerTypeId(layer.getLayerTypeId())
                    .layerTypeName(layerType.getTypeName())
                    .fileId(layer.getFileId())
                    .fileName(fileDTO.getFileName())
                    .nodeCode(layer.getNodeCode())
                    .nodeName(layer.getNodeName())
                    .category(layer.getCategory())
                    .nestFlag(layer.getNestFlag())
                    .build();
            if (null != layer.getNotes()) {
                infoDTO.setNotes(layer.getNotes());
            }
            return Result.data(infoDTO);
        } else {
            LayerInfoDTO infoDTO = new LayerInfoDTO();
            infoDTO.setCategory(layer.getCategory());
            infoDTO.setCustomName(layer.getCustomName());
            infoDTO.setNodeCode(layer.getNodeCode());
            infoDTO.setNodeName(layer.getNodeName());
            infoDTO.setCategory(layer.getCategory());
            infoDTO.setNestFlag(layer.getNestFlag());
            infoDTO.setFileUrl(layer.getFileUrl());
            infoDTO.setObsUrl(layer.getObsUrl());
            infoDTO.setOriginUrl(layer.getOriginUrl());
            infoDTO.setFileName(layer.getFileName());
            infoDTO.setPublishStatus(layer.getPublishStatus());
            infoDTO.setUploadType(layer.getUploadType());
            if (null != layer.getNotes()) {
                infoDTO.setNotes(layer.getNotes());
            }
            return Result.data(infoDTO);
        }
    }

    @Override
    @Transactional
    public Result<String> addLayer(LayerAddDTO dto) {
        log.info("新增/修改图层 {}", JSON.toJSONString(dto));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Layer layer = layerMapper.selectById(dto.getId());
        Layer copy = BeanCopyUtils.copy(dto, Layer.class);
        if (copy == null) {
            return Result.fail("参数错误");
        }
        if (layer == null) {//新增
            judgeLayerCustomName(dto, dto.getCategory());
            copy.setDeleteFlag(0);
            copy.setTenantId(loginUser.getUser().getTenantId());
            copy.setAppVerCode(loginUser.getUser().getAppVerCode());
            copy.setIndustryCode(loginUser.getUser().getIndustryCode());
            copy.setCreator(SecurityUtils.getOperator());
            copy.setCreateTime(new Date());
        } else {//修改
            if (!layer.getCustomName().equals(dto.getCustomName())) {
                judgeLayerCustomName(dto, dto.getCategory());
            }
            copy.setId(layer.getId());
            copy.setUpdateTime(new Date());
            copy.setModifier(SecurityUtils.getOperator());
        }

        String platformCode = loginUser.getUser().getPlatformCode();
        copy.setNodeCode(platformCode);
        if (StringUtils.isEmpty(dto.getFileUrl()) && !StringUtils.isEmpty(dto.getId())) {
            layerMapper.updateById(copy);
            if(LAYER_CATEGORY_2.equals(copy.getCategory()) && "2".equals(layer.getUploadType())){
                layerPublishAsyncService.publishAndSaveResult(copy.getId(), dto.getObsUrl(), dto.getFileName());
            }
            return Result.success("修改成功");
        }

        // 矢量  非矢量
        if (LAYER_CATEGORY_1.equals(copy.getCategory())) {
            //判断是否套合图层 判断是否是当前图层类型下的第一个 如果是 nestFlag = 1 如果不是  nestFlag = 0
            List<Layer> layers = layerMapper.selectByTypeIds(Collections.singletonList(copy.getLayerTypeId()));
            if (null == layers) {
                copy.setNestFlag(Constants.ONE);
            } else {
                copy.setNestFlag(Constants.ZERO);
            }

            Result result = wgsAnalysisService.analysisGrid(dto.getFileUrl(), dto.getFileName(), GeoServer.LAYER_STYLE, geoServerProperties.getLayerShpWorkspace(), true);
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                Wgs84DTO wgs84DTO = (Wgs84DTO) result.getData();
                if (wgs84DTO.getList() == null || wgs84DTO.getList().isEmpty()) {
                    return Result.fail("数据错误");
                }
                String[] split = wgs84DTO.getLayer_name().split(":");
                copy.setLayerNamespace(split[0]);
                copy.setLayerName(split[1]);
                copy.setGameKey(wgs84DTO.getThe_geom());
                copy.setGeoserverUrl(wgs84DTO.getUrl());
                copy.setMaxx(wgs84DTO.getMaxx());
                copy.setMaxy(wgs84DTO.getMaxy());
                copy.setMinx(wgs84DTO.getMinx());
                copy.setMiny(wgs84DTO.getMiny());
                if (StringUtils.isNotEmpty(copy.getId())) {
                    layerMapper.updateById(copy);
                } else {
                    String id = IdUtils.fastSimpleUUID();
                    copy.setId(id);
                    LayerType layerType = layerPgService.getLayerType(dto.getLayerTypeId());
                    copy.setLayerTypeId(layerType.getId());
                    layerMapper.insert(copy);
                    //权限增加
                    layerMapper.addDataPermissions(copy);
                }
                //解析图斑
                layerPolygonMapper.deleteLayerId(copy.getId());

                saveLayerPolygon(copy.getId(), wgs84DTO.getList());

                return Result.success("操作成功");
            } else {
                return Result.fail(result.getMsg());
            }

        } else if (LAYER_CATEGORY_2.equals(copy.getCategory())) {
            if("1".equals(copy.getUploadType())){
                String fileUrl = copy.getFileUrl() + "&REQUEST=GetCapabilities&FORMAT=";
                //请求geoserver
                Map<String, Double> xyFromXml = getXYFromXml(fileUrl);
                if (!Objects.requireNonNull(xyFromXml).isEmpty()) {
                    copy.setMinx(xyFromXml.get(MIN_X));
                    copy.setMaxx(xyFromXml.get(MAX_X));
                    copy.setMiny(xyFromXml.get(MIN_Y));
                    copy.setMaxy(xyFromXml.get(MAX_Y));
                }
            }else if("2".equals(copy.getUploadType())){
                if (layer == null) {
                    copy.setPublishStatus("0");
                }
            }
            if (StringUtils.isNotEmpty(copy.getId())) {
                boolean flag = false;
                if("2".equals(copy.getUploadType()) && StringUtils.isNotEmpty(layer.getObsUrl()) && StringUtils.isNotEmpty(copy.getObsUrl()) && !layer.getObsUrl().equals(copy.getObsUrl())){
                    copy.setPublishStatus("0");
                    flag = true;
                }
                layerMapper.updateById(copy);
                // 2. 异步调用发布图层并自动更新状态
                if("2".equals(copy.getUploadType()) && flag){
                    layerPublishAsyncService.publishAndSaveResult(copy.getId(), dto.getObsUrl(), dto.getFileName());
                }
            } else {
                copy.setId(IdUtils.fastSimpleUUID());
                layerMapper.insert(copy);
                //权限增加
                layerMapper.addDataPermissions(copy);
                // 2. 异步调用发布图层并自动更新状态
                if("2".equals(copy.getUploadType())){
                    // 等事务提交后再异步
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronizationAdapter() {
                                @Override
                                public void afterCommit() {
                                    layerPublishAsyncService.publishAndSaveResult(
                                            copy.getId(), dto.getObsUrl(), dto.getFileName());
                                }
                            });
                }

            }
            return Result.success("操作成功");
        } else {
            dsmDemSetXY(copy);

            //3-DEM 4-DSM 5-倾斜摄影 6-激光点云
            if (StringUtils.isNotEmpty(copy.getId())) {
                layerMapper.updateById(copy);
            } else {
                copy.setId(IdUtils.fastSimpleUUID());
                layerMapper.insert(copy);
                //权限增加
                layerMapper.addDataPermissions(copy);
            }
            return Result.success("操作成功");
        }
    }


    private void dsmDemSetXY(Layer copy) {
        try {
            //DEM  DSM获取valid_bounds 四至
            if (DEM_DSM_LIST.contains(copy.getCategory())) {
                String url = copy.getFileUrl();
                if (!url.endsWith(".json")) {
                    url = url + "/layer.json";
                }
                RestTemplate restTemplate = new RestTemplate();
                String forObject = restTemplate.getForObject(url, String.class);
                JSONObject jsonObject = JSON.parseObject(forObject);
                if (jsonObject.containsKey("valid_bounds")) {
                    String validBoundsStr = jsonObject.getString("valid_bounds");
                    JSONArray validBounds = JSONArray.parseArray(validBoundsStr);
                    log.info("排序前:" + JSON.toJSONString(validBounds));
                    validBounds.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.toString())));
                    log.info("排序后:" + JSON.toJSONString(validBounds));
                    copy.setMaxx(validBounds.getDouble(3));
                    copy.setMinx(validBounds.getDouble(2));
                    copy.setMaxy(validBounds.getDouble(1));
                    copy.setMiny(validBounds.getDouble(0));
                }
            }
        } catch (Exception e) {
            log.info("查询四至错误:", e);
        }

    }

    private Map<String, Double> getXYFromXml(String fileUrl) {
        try {
            //获取版本号
            String lowerCasedInput = fileUrl.toLowerCase();

            int versionStartIndex = lowerCasedInput.indexOf(LAYER_URL_VERSION_PRE);
            // 如果未找到 "version="
            if (versionStartIndex == -1) {
                // 直接在字符串末尾添加
                fileUrl += "&VERSION=1.3.0";
                log.info("Added version: " + fileUrl);
            } else {
                int versionEndIndex = lowerCasedInput.indexOf("&", versionStartIndex + LAYER_URL_VERSION_PRE.length());
                String extractedVersion = fileUrl.substring(versionStartIndex + LAYER_URL_VERSION_PRE.length(), versionEndIndex != -1 ? versionEndIndex : fileUrl.length());
                log.info("Extracted version: " + extractedVersion);
                // 替换为 1.3.0
                fileUrl = fileUrl.substring(0, versionStartIndex + LAYER_URL_VERSION_PRE.length()) + "1.3.0" +
                        (versionEndIndex != -1 ? fileUrl.substring(versionEndIndex) : "");
                log.info("Replaced version: " + fileUrl);
            }
            log.info("替换tiff版本号1.3.0后的url: {}", fileUrl);

            RestTemplate restTemplate = new RestTemplate();
            Pattern pattern = Pattern.compile("layers=([^&]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(fileUrl);
            if (matcher.find()) {
                String layersValue = matcher.group(1);
                log.info("Extracted layers value: " + layersValue);
                //请求geoserver
                String xmlObj = restTemplate.getForObject(fileUrl, String.class);
                Document document = XmlUtil.readXML(xmlObj);
                Element root = document.getDocumentElement();
                Element capability = XmlUtil.getElement(root, "Capability");
                String layerElement = "Layer";
                Element layer1 = XmlUtil.getElement(capability, layerElement);
                List<Element> layers = XmlUtil.getElements(layer1, layerElement);
                Map<String, Double> map = new HashMap<>();
                layers.forEach(item -> {
                    Element nameElement = XmlUtil.getElement(item, "Name");
                    String name = nameElement.getTextContent();
                    if (layersValue.equals(name)) {

                        Element keywordList = XmlUtil.getElement(item, "KeywordList");
                        List<Element> keyword = XmlUtil.getElements(keywordList, "Keyword");
                        //判断是否包含tiff文件后缀
                        List<String> files = Arrays.asList(tiffFile.split(","));
                        log.info("配置的tiff keyword{}", JSON.toJSONString(files));
                        System.out.println("tiff:" + tiffFile);
                        boolean foundMatch = keyword.stream()
                                .map(Node::getTextContent)
                                .anyMatch(files::contains);

                        if (foundMatch) {
                            Element exGeographicBoundingBox = XmlUtil.getElement(item, "EX_GeographicBoundingBox");
                            log.info("解析EX_GeographicBoundingBox:{}", JSON.toJSONString(exGeographicBoundingBox));
                            //minx
                            Element westBoundLongitude = XmlUtil.getElement(exGeographicBoundingBox, "westBoundLongitude");
                            //maxx
                            Element eastBoundLongitude = XmlUtil.getElement(exGeographicBoundingBox, "eastBoundLongitude");
                            //miny
                            Element southBoundLatitude = XmlUtil.getElement(exGeographicBoundingBox, "southBoundLatitude");
                            //maxy
                            Element northBoundLatitude = XmlUtil.getElement(exGeographicBoundingBox, "northBoundLatitude");

                            map.put(MIN_X, Double.valueOf(westBoundLongitude.getTextContent()));
                            map.put(MAX_X, Double.valueOf(eastBoundLongitude.getTextContent()));
                            map.put(MIN_Y, Double.valueOf(southBoundLatitude.getTextContent()));
                            map.put(MAX_Y, Double.valueOf(northBoundLatitude.getTextContent()));
                            log.info("最后获得的图层定位边界:{}", JSON.toJSONString(map));
                        } else {
                            throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);
                        }
                    }
                });
                return map;
            } else {
                System.out.println("No matching layers parameter found in the URL.");
                throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);
            }
        } catch (Exception e) {
            log.error(LAYER_TIFF_TYPE_ERR_MSG,e);
            e.printStackTrace();

            throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);

        }
    }

    @Transactional
    public void saveLayerPolygon(String layerId, List<Map<String, Object>> list) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<LayerPolygon> layerPolygons = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String featureId = map.get("id").toString();
            String[] split = featureId.split("\\\\");

            LayerPolygon polygon = new LayerPolygon();
            polygon.setId(IdUtils.fastSimpleUUID());
            polygon.setLayerId(layerId);
            polygon.setCreator(loginUser.getUser().getUserName());
            polygon.setIndustryCode(loginUser.getUser().getIndustryCode());
            polygon.setTenantId(loginUser.getUser().getTenantId());
            polygon.setAppVerCode(loginUser.getUser().getAppVerCode());
            polygon.setGeometry(map.get("coordinates").toString());
            polygon.setFeatureId(split[split.length - 1]);
            polygon.setProperties(map.get("properties").toString());
            polygon.setCreateTime(new Date());
            polygon.setUpdateTime(new Date());
            layerPolygons.add(polygon);
        }
        List<String> featureIds = layerPolygons.stream().map(LayerPolygon::getFeatureId).collect(Collectors.toList());
        log.info("featureIds = {}", JSON.toJSONString(featureIds));
        //判断图斑编号是否存在
        int count = layerPolygonMapper.selectListCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), featureIds);
        if (count > 0) {
            throw new CustomException("上传文件图斑ID重复，请重新上传。");
        }
        layerPolygonMapper.saveBatch(layerPolygons);
    }

    /**
     * 保存图斑
     *
     * @param layerId   图层ID
     * @param layerName 图层名称
     */
    @Transactional
    public void saveLayerPolygon(String layerId, String layerName) {
        String url = String.format(GEOSERVER_BASE_URL, geoServerProperties.getShowUrl(), geoServerProperties.getLayerShpWorkspace(), layerName);
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<LayerPolygon> layerPolygons = new ArrayList<>();
            String body = HttpRequest.get(url).execute().body();
            JSONObject object = JSON.parseObject(body);
            String features = object.getString("features");
            JSONArray array = JSON.parseArray(features);
            for (Object o : array) {
                JSONObject jsonObject = JSON.parseObject(o.toString());
                String id = jsonObject.getString("id");
                String properties = jsonObject.getString("properties");
                String geometry = jsonObject.getString("geometry");
                JSONObject coordinatesJsonObject = JSON.parseObject(geometry);
                String coordinates = coordinatesJsonObject.getString("coordinates");

                LayerPolygon polygon = new LayerPolygon();
                polygon.setId(IdUtils.fastSimpleUUID());
                polygon.setLayerId(layerId);
                polygon.setCreator(loginUser.getUser().getUserName());
                polygon.setIndustryCode(loginUser.getUser().getIndustryCode());
                polygon.setTenantId(loginUser.getUser().getTenantId());
                polygon.setAppVerCode(loginUser.getUser().getAppVerCode());
                polygon.setGeometry(coordinates);
                polygon.setFeatureId(id);
                polygon.setProperties(properties);
                layerPolygons.add(polygon);
            }
            List<String> featureIds = layerPolygons.stream().map(LayerPolygon::getFeatureId).collect(Collectors.toList());
            //判断图斑编号是否存在
            int count = layerPolygonMapper.selectListCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), featureIds);

            if (count > 0) {
                throw new CustomException("上传文件不符合规则，请重新上传。");
            }
            layerPolygonMapper.saveBatch(layerPolygons);
        } catch (CustomException e) {
            log.error("解析图斑报错", e);
            throw e;
        } catch (Exception e) {
            log.error("解析图斑报错", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> deleted(DeleteDTO vo) {
        if (CollectionUtils.isEmpty(vo.getIds())) {
            return Result.fail("参数不能为空");
        }
        log.info("图层数据删除 {}",JSON.toJSONString(vo));
        int count;
        if (LAYER_CATEGORY_2.equals(vo.getCategory())) {
            count = layerMapper.deleteBatchIds(vo.getIds());
            //清除权限数据
            layerMapper.delDataPermissions(vo.getIds());
        } else {

            count = layerMapper.deleteBatchIds(vo.getIds());
            //清除权限数据
            layerMapper.delDataPermissions(vo.getIds());
            //推送删除的套合图层
            List<Layer> deleteList = layerMapper.findLayerByLayerIds(vo.getIds());
            if (CollectionUtils.isNotEmpty(deleteList)){
                String modifier = vo.getModifier();
                try {
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    modifier = loginUser.getUsername();
                }catch (Exception e){
                    log.error("获取用户信息失败",e);
                }
                for (Layer layer : deleteList) {
                    layer.setModifier(modifier);
                }
                sendNestLayerToKafka(deleteList,1);
            }
            //删除的是套合图层的数据
            if (count > 0) {
                try {
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    layerPolygonMapper.deleteLayerIds(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), vo.getIds());
                }catch (Exception e){
                    log.error("获取用户信息失败",e);
                }
            }

            if (count > 0 && !vo.getIds().isEmpty()) {
                //逻辑删除电子围栏绑定算法关系
                layerMapper.deletePolygonPatchAlgoAreaByLayerIds(vo.getIds());
                //逻辑删除巡检电子围栏
                layerMapper.deleteInspectionRouteFenceByLayerIds(vo.getIds());
                //逻辑删除电子围栏
                layerMapper.deletePolygonPatchByLayerIds(vo.getIds());
            }


            DeleteLayerReq req = new DeleteLayerReq();
            req.setLayerIds(vo.getIds());
            Result<String> result = gisLayerAPI.deleteLayer(req);
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                try {
                    String[] split = result.getData().split(",");
                    for (String layerName : split) {
                        geoServer.deleteLayer(geoServerProperties.getGuotuWorkspace(), layerName);
                    }
                } catch (Exception e) {
                    log.error("删除视图图层错误", e);
                }
            }
        }

        return count > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }


    private void sendNestLayerToKafka(List<Layer> list,Integer type) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
            kafkaProducer.sendToKafka(Constants.LAYER_NEST_KAFKA_TOPIC, jsonArray,type);
            log.info("套合图层推送kafka:{}", JSON.toJSONString(jsonArray));
        } catch (Exception e) {
            log.info("套合图层推送kafka失败,", e);
        }
    }

    /**
     * 判断图层名称是否重复
     */
    private void judgeLayerCustomName(LayerAddDTO dto, String category) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer count = layerMapper.selectCustomNameCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), dto.getCustomName(), category);
        if (count > 0) {
            if (LAYER_CATEGORY_1.equals(category)) {
                throw new CustomException("图层名称已存在，请重新输入！");
            } else {
                throw new CustomException("图层名称已存在");
            }
        }
    }

    @Override
    public Result<List<PolygonAttrDictVO>> getPolygonAttrDict(PolygonAttrDictVO polygonAttrDictVO) {
        return Result.data(layerMapper.getPolygonAttrDict(polygonAttrDictVO));
    }

    @Override
    public Result<Map<String, List<LayerTypeAttrTilterVO>>> getLayerTypeAttrFilter(LayerTypeAttrTilterVO layerTypeAttrTilterVO) {
        List<LayerTypeAttrTilterVO> layerTypeAttrFilter = layerMapper.getLayerTypeAttrFilter(layerTypeAttrTilterVO);
        Map<String, List<LayerTypeAttrTilterVO>> map = layerTypeAttrFilter.stream().collect(Collectors.groupingBy(LayerTypeAttrTilterVO::getAbbreviation));
        return Result.data(map);
    }

    @Override
    public Result<List<LayerAreaVO>> getLayerArea(LayerAreaVO layerAreaVO) {
        return Result.data(layerMapper.getLayerArea(layerAreaVO));
    }

    @Override
    @DataScope(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code", deviceColumn = "t1.device_code")
    public Result<InspectionRouteRelLayerSdkVO> getDeviceByPolygon(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return Result.data(layerMapper.getDeviceByPolygon(inspectionRouteRelLayerSdkVO));
    }

    @Override
    @DataScope(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code", deviceColumn = "t1.device_code")
    public Result<List<InspectionRouteRelLayerSdkVO>> getPolygonLayers(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return Result.data(layerMapper.getPolygonLayers(inspectionRouteRelLayerSdkVO));
    }

    @Override
    @DataScope(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code", deviceColumn = "t1.device_code")
    public Result<List<InspectionRouteRelLayerSdkVO>> getPolygonFeatures(InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return Result.data(layerMapper.getPolygonFeatures(inspectionRouteRelLayerSdkVO));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNestFlag(LayerQueryDTO layerQueryDTO) {
        //查询当前图层类型下的默认套合图层id
        LoginUser loginUser = SecurityUtils.getLoginUser();
        layerQueryDTO.setTenantId(loginUser.getUser().getTenantId());
        layerQueryDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        layerQueryDTO.setNodeCode(loginUser.getUser().getPlatformCode());
        String operator = SecurityUtils.getOperator();
        Layer layer = layerMapper.selectByTypeIdAndNestFlag(layerQueryDTO);
        if (null == layer) {
            log.info("当前图层类型下无套合图层");
            layerMapper.updateNestFlag(layerQueryDTO.getId(), 1, operator);
        } else {
            if (!Objects.equals(layer.getId(), layerQueryDTO.getId())) {
                //更新
                layerMapper.updateNestFlag(layer.getId(), 0, operator);
                layerMapper.updateNestFlag(layerQueryDTO.getId(), 1, operator);
            }
        }
        Layer ly = layerMapper.selectById(layerQueryDTO.getId());
        if (ly != null) {
            //推送kafka
            sendNestLayerToKafka(Collections.singletonList(ly),3);
        }
    }

    @Override
    public Map<String, LayerResponseDTO> getNestList(LayerQueryDTO layerQueryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (null == layerQueryDTO.getTenantId()) {
            layerQueryDTO.setTenantId(user.getTenantId());
        }
        if (null == layerQueryDTO.getIndustryCode()) {
            layerQueryDTO.setIndustryCode(user.getIndustryCode());
        }
        layerQueryDTO.setNestFlag(Constants.ONE);
        List<LayerResponseDTO> list = layerMapper.list(layerQueryDTO, "createTime", user.getPlatformCode());
        list.forEach(layer -> layer.setRootGeoserverUrl(geoServerProperties.getShowUrl()));
        return list.stream().collect(Collectors.toMap(LayerResponseDTO::getAbbreviation, item -> item));
    }

    @Override
    public List<LayerAreaCountVO> layerAreaCount(List<String> layerIds) {
        if (CollectionUtils.isEmpty(layerIds)) {
            throw new CustomException("图层ID不能为空");
        }
        List<LayerAreaCountVO> vos = layerMapper.layerAreaCount(layerIds);
        return vos;
    }

    @Override
    public Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(String code, Integer ratioFlag, String abbreviation) {
        long time = System.currentTimeMillis();
        //查询图层类型
        LayerType layerType = layerTypeMapper.selectByAbbreviation(abbreviation, null, null);
        if (layerType == null) {
            throw new CustomException("图层类型不存在");
        }
        List<SysGisFieldsDTO> fields = layerTypeMapper.getLayerTypeFields(abbreviation);
        List<String> codes = StreamUtils.toList(fields, SysGisFieldsDTO::getCode);
        if (!codes.contains(code)) {
            throw new CustomException("图层类型不存在该分类字段");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String key = "layer:statistics:" + loginUser.getUser().getTenantId() + "_" + loginUser.getUser().getIndustryCode() + ":" + abbreviation + "_" + code;
        if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
            String s = RedisUtil.get(key, RedisDBType.PRIMARY);
            List<LayerSlzyStatisticsDTO> list = JSON.parseArray(s, LayerSlzyStatisticsDTO.class);
            return Result.data(list);
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setAbbreviation(abbreviation);
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        //租户 + 行业下 所有的需要统计的图层表
        List<String> tableNames = layerMapper.abbreviationLayerNames(dto, loginUser.getUser().getPlatformCode());
        log.info("深林资源类型统计 {} {} {}",dto.getTenantId(),dto.getIndustryCode(),code);
        if (CollectionUtils.isEmpty(tableNames)) {
            return Result.data(Collections.emptyList());
        }
        LayerClassifyAreaStatisticsVO vo = new LayerClassifyAreaStatisticsVO();
        vo.setCode(code);//分组需要统计的字段
        vo.setMj(layerType.getAreaRef());//面积字段
        vo.setTableNames(tableNames);
        log.info("mysql时间 {}", (System.currentTimeMillis() - time));
        Result<List<LayerSlzyStatisticsDTO>> result = gisPgAPI.layerClassifyAreaStatistics(vo);

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            List<LayerSlzyStatisticsDTO> data = result.getData();
            data = data.stream().filter(d -> d.getArea() != null && d.getValue() != null && StringUtils.isNotEmpty(d.getValue().trim())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(data)){
                return  Result.data(new ArrayList<>());
            }
            //平方米和亩的计算比例
            double m = 0.0015D;
            BigDecimal totalMj = new BigDecimal(Constants.ZERO);
            for(LayerSlzyStatisticsDTO one : data){
                Double mj = BigDecimalUtils.multiply(one.getArea(), m);
                one.setArea(Double.parseDouble(decimalFormat.format(mj)));
                totalMj = totalMj.add(BigDecimal.valueOf(mj));
            }
            if (totalMj.doubleValue() <= 0){
                totalMj = new BigDecimal(Constants.ONE);
            }
            if (ratioFlag != null && ratioFlag == 1) {
                for(LayerSlzyStatisticsDTO one : data){
                    one.setRatio(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(one.getArea(), totalMj.doubleValue()), BigDecimalUtils.DATA_10000), 0));
                }
            }
            log.info("pg时间 {}", (System.currentTimeMillis() - time));
            //降序排列
            List<LayerSlzyStatisticsDTO> sorted = StreamUtils.sorted(data, Comparator.comparing(LayerSlzyStatisticsDTO::getArea, Comparator.reverseOrder()));
            log.info("排序时间 {}", (System.currentTimeMillis() - time));
            RedisUtil.set(key, JSON.toJSONString(sorted), RedisDBType.PRIMARY);
            RedisUtil.expire(key, 20, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            return Result.data(sorted);
        }
        return result;
    }

    @Override
    public Result<LayerCountAndAreaDTO> layerCountAndArea(String abbreviation) {
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setAbbreviation(abbreviation);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        LayerCountAndAreaDTO res = layerMapper.abbreviationLayersAreaAndCount(dto, loginUser.getUser().getPlatformCode());
        if (res == null) {
            res = new LayerCountAndAreaDTO();
            res.setLayerArea(0D);
            res.setPolygonCount(0);
            return Result.data(res);
        }
        double m = 0.0015D;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Double mj = BigDecimalUtils.multiply(res.getLayerArea(), m);
        res.setLayerArea(Double.parseDouble(decimalFormat.format(mj)));
        return Result.data(res);
    }

    @Override
    public Map<String, List<LayerResponseDTO>> getTenantListMultiType(LayerQueryDTO dto) {
        List<LayerResponseDTO> list = list(dto);
        //根据list中的category字段分组，并把list中的数据放到map中
        Map<String, List<LayerResponseDTO>> map = list.stream().collect(Collectors.groupingBy(LayerResponseDTO::getCategory));
        for (Map.Entry<String, List<LayerResponseDTO>> stringListEntry : map.entrySet()) {
            stringListEntry.getValue().sort(Comparator.comparing(LayerResponseDTO::getCategory)
                    .thenComparing(LayerResponseDTO::getCategory, new ChineseComparator()));
        }
        return map;
    }

    @Override
    @DataPermissions(layerColumn = "l.id")
    public List<LayerCountRes> getLayerCount(LayerQueryDTO dto){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        dto.setTenantId(user.getTenantId());
        dto.setIndustryCode(user.getIndustryCode());
        dto.setPlatformCode(user.getPlatformCode());
        return layerMapper.getLayerCount(dto);
    }

}
