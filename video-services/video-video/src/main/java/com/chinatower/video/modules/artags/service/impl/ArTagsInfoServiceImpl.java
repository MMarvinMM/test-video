package com.chinatower.video.modules.artags.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.api.IIndustryDeviceService;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.industry.dto.DeviceIndReqDTO;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.order.QueryWeatherDTO;
import com.chinatower.platform.entity.order.WeatherAirDailyDTO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.ArTagsInfoReq;
import com.chinatower.video.api.entity.ArVideoFileVO;
import com.chinatower.video.modules.artags.dataobject.*;
import com.chinatower.video.modules.artags.mapper.ArTagsInfoMapper;
import com.chinatower.video.modules.artags.service.IArTagsInfoService;
import com.chinatower.video.modules.query.entity.ArTagFilterVO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * AR标签信息表 服务实现类
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
@Service
@Slf4j
public class ArTagsInfoServiceImpl extends ServiceImpl<ArTagsInfoMapper, ArTagsInfo> implements IArTagsInfoService {

    @Autowired
    IIndustryDeviceService industryDeviceService;
    @Autowired
    IWeatherApiService weatherApiService;

    @Autowired
    ArTagsInfoMapper arTagsInfoMapper;

    @Resource
    FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    ArTagsScopeServiceImpl arTagsScopeServiceImpl;

    public static final String PIC = "pic";

    public static final Integer LINK_TYPE_DEVICE = 1;

    public static final Integer AR_TAG_TYPE_DEVICE = 5;

    public static final String AR_TAG_TYPE_MAP = "102";

    public static final String AR_TAG_ID = "id";
    public static final String LABEL_TREE_MAP_102 = "labelTree_map_102";

    public static final String LABEL_TREE_MAP_103 = "labelTree_map_103";

    public static final String LABEL_TREE_MAP_105 = "labelTree_map_105";

    public static final String LABEL_TREE_MAP_106 = "labelTree_map_106";

    public static final String LABEL_TREE_MAP_107 = "labelTree_map_107";

    public static final String LABEL_TREE_MAP_108 = "labelTree_map_108";


    @Override
    public void update(ArTagsInfo arTagsInfo) {
        arTagsInfoMapper.updateArTagsInfo(arTagsInfo);
    }

    @Override
    public Result<Long> add(ArTagsInfo arTagsInfo) {
        log.info("新增ar标签信息:{}", JSON.toJSONString(arTagsInfo));
         arTagsInfoMapper.add(arTagsInfo);
        Long id = arTagsInfo.getId();
        return Result.data(id, "增加AR标签成功!");
    }


    public static List<BusinessDictType> buildTreeRecursive(List<BusinessDictType> allItems, Integer parentId) {
        List<BusinessDictType> trees = new ArrayList<>();

        for (BusinessDictType item : allItems) {
            if (item.getParentId() != null && item.getParentId().equals(parentId)) {
                // 找到了当前父节点下的子节点
                List<BusinessDictType> children = buildTreeRecursive(allItems, item.getId()); // 递归查找子节点
                item.setChildren(children);
                trees.add(item);
            }
        }
        return trees;
    }

    public static List<BusinessDictType> buildTree(List<BusinessDictType> allNodes) {
        // 使用Stream API筛选出根节点，即parentId为null的节点
        List<BusinessDictType> roots = allNodes.stream()
                .filter(node -> node.getParentId() == null)
                .collect(Collectors.toList());

        // 为每个节点分配子节点
        for (BusinessDictType rootNode : roots) {
            assignChildren(rootNode, allNodes);
        }

        return roots;
    }

    private static void assignChildren(BusinessDictType node, List<BusinessDictType> allNodes) {
        List<BusinessDictType> children = allNodes.stream()
                .filter(child -> Objects.equals(child.getParentId(), node.getId()))
                .collect(Collectors.toList());

        node.setChildren(children);

        for (BusinessDictType child : children) {
            assignChildren(child, allNodes); // 递归调用为每个子节点分配其子节点
        }
    }

    @Override
    public List<BusinessDictType> arTagsTypeTree(Integer dictType) {
        BusinessDictType businessDictType = new BusinessDictType();
        businessDictType.setDictType(dictType);
        List<BusinessDictType> businessDictTypes = arTagsInfoMapper.arTagsTypeTree(businessDictType);
        return buildTree(businessDictTypes);

    }

    @Override
    public List<ArDeviceInfo> queryAll(ArTagsInfoReq arTagsInfoReq) {
        List<ArTagsInfo> arTagsInfoList = arTagsInfoMapper.queryAll(arTagsInfoReq);


        List<String> urls = new ArrayList<>();

        //批量提取pic url
        //增加图标url
        for (ArTagsInfo arTagsInfo : arTagsInfoList) {
            JSONObject extraJson = arTagsInfo.getExtraJson();
            if (null != extraJson && extraJson.containsKey(PIC)) {
                String picStr = extraJson.getString(PIC);
                String[] split = picStr.split(com.chinatower.platform.constants.Constants.COMMA);
                urls.addAll(Arrays.asList(split));
            }
            String labelIconUrl = arTagsInfo.getLabelIconUrl();
            if(StringUtils.isNotBlank(labelIconUrl)){
                urls.add(labelIconUrl);
            }
            String videoFileString = arTagsInfo.getVideoFileString();
            if(StringUtils.isNotBlank(videoFileString)){
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ArVideoFileVO>>() {}.getType();
                List<ArVideoFileVO> arVideoFileVOList = gson.fromJson(videoFileString, listType);
                for (ArVideoFileVO arVideoFileVO : arVideoFileVOList) {
                    urls.add(arVideoFileVO.getFileUrl());
                }
                arTagsInfo.setVideoFile(arVideoFileVOList);
                arTagsInfo.setVideoFileString(null);
            }
        }
        Map<String, String> fileUrlMap = new HashMap<>();
        if (!urls.isEmpty()) {
            //obs批量加密
            fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urls);
        }

        //列表返回设备通道相关信息（减少前后台交互，提升用户界面加载速度）
        List<ArDeviceInfo> list = new ArrayList<>();
        for (ArTagsInfo arTagsInfo : arTagsInfoList) {
            //添加图标url
            String labelIconUrl = arTagsInfo.getLabelIconUrl();
            if(StringUtils.isNotBlank(labelIconUrl)){
                arTagsInfo.setLabelIconUrl(fileUrlMap.get(labelIconUrl));
            }
            //设置加密视频
            List<ArVideoFileVO> videoFile = arTagsInfo.getVideoFile();
            if(!CollectionUtils.isEmpty(videoFile)){
                for (ArVideoFileVO arVideoFileVO : videoFile) {
                    arVideoFileVO.setFileUrl(fileUrlMap.get(arVideoFileVO.getFileUrl()));
                }
            }
            //设置加密图片
            JSONObject extraJson = arTagsInfo.getExtraJson();
            if (null != extraJson && extraJson.containsKey(PIC)) {
                String picStr = extraJson.getString(PIC);
                if (null != picStr) {
                    String[] split = picStr.split(com.chinatower.platform.constants.Constants.COMMA);
                    List<String> imageUrlList = new ArrayList<>();

                    for (String pic : split) {
                        String imageUrl = fileUrlMap.get(pic);
                        if (imageUrl != null) {
                            imageUrlList.add(imageUrl);
                        }
                    }
                    if (!imageUrlList.isEmpty()) {
                        extraJson.put(PIC, String.join(com.chinatower.platform.constants.Constants.COMMA, imageUrlList));
                    }
                }
            }

            ArDeviceInfo arDeviceInfoResult = new ArDeviceInfo();
            arDeviceInfoResult.setId(String.valueOf(arTagsInfo.getId()));
            BeanUtils.copyProperties(arTagsInfo,arDeviceInfoResult, AR_TAG_ID);
            list.add(arDeviceInfoResult);
        }
        //摄像机类型 查询设备通道
        Set<String> deviceCodeList = arTagsInfoList.stream().filter(item -> LINK_TYPE_DEVICE.equals(item.getLinkType()) || AR_TAG_TYPE_DEVICE.equals(item.getTagsType()))
                .map(ArTagsInfo::getDeviceCode).collect(Collectors.toSet());
        if(!CollectionUtils.isEmpty(deviceCodeList)){
            List<ArDeviceInfo> arDeviceInfo = arTagsInfoMapper.getArDeviceInfo(deviceCodeList);
            List<ArChannelInfo> arChannelInfo = arTagsInfoMapper.getArChannelInfo(deviceCodeList);
            arDeviceInfo.stream().map(item -> {
                List<ArChannelInfo> arChannelInfoList = new ArrayList<>();
                arChannelInfo.stream().filter(area-> Objects.equals(item.getDeviceCode(),area.getDeviceCode())).forEach(arChannelInfoList::add);
                item.setChildren(arChannelInfoList);
                return item;
            }).collect(Collectors.toList());

            list.stream().map(item -> {
                arDeviceInfo.stream().filter(area-> Objects.equals(item.getDeviceCode(),area.getDeviceCode())).forEach(area-> {
                    item.setDevName(area.getDevName());
                    // 没有经纬度处理
                    if(StringUtils.isEmpty(item.getLatitude()) || StringUtils.isEmpty(item.getLongitude())){
                        item.setLatitude(area.getLatitude());
                        item.setLongitude(area.getLongitude());
                    }
                    item.setChildren(area.getChildren());
                });
                return item;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Result<String> batchAddLabelIcon(ArLabelIconGallery arLabelIconGallery) {
        log.info("批量增加图标库:{}", JSON.toJSONString(arLabelIconGallery));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        arLabelIconGallery.setCreator(currentUser.getUserName());
        arLabelIconGallery.setTenantId(currentUser.getTenantId());
        arLabelIconGallery.setIndustryCode(currentUser.getIndustryCode());
        arLabelIconGallery.setAppVerCode(currentUser.getAppVerCode());
        arTagsInfoMapper.batchAddLabelIcon(arLabelIconGallery);
        return Result.success("新增成功");
    }

    @Override
    public Result<List<ArLabelIconGallery>> getLabelIconList(ArLabelIconGallery arLabelIconGallery) {
        log.info("查询图标库接口:{}", JSON.toJSONString(arLabelIconGallery));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        arLabelIconGallery.setTenantId(currentUser.getTenantId());
        arLabelIconGallery.setIndustryCode(currentUser.getIndustryCode());
        arLabelIconGallery.setAppVerCode(currentUser.getAppVerCode());
        List<ArLabelIconGallery> data = arTagsInfoMapper.getLabelIconList(arLabelIconGallery);
        if (data == null || data.isEmpty()) {
            return Result.data(Collections.emptyList());
        }
        List<String> urls = new ArrayList<>();
        for (ArLabelIconGallery item : data) {
            if (StringUtils.isNotBlank(item.getLabelIconUrl())) {
                urls.add(item.getLabelIconUrl());
            }
        }
        Map<String, String> fileUrlMap = new HashMap<>();
        if (!urls.isEmpty()) {
            //obs批量加密
            fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urls);
            log.info("批量加密后的文件地址:{}", JSON.toJSONString(fileUrlMap));
        }
        for (ArLabelIconGallery item : data) {
            item.setLabelIconUrl(fileUrlMap.get(item.getLabelIconUrl()));
        }
        return Result.data(data);
    }

    @Override
    public Result<String> delLabelIcon(ArLabelIconGallery arLabelIconGallery) {
        log.info("删除图标库接口:{}", JSON.toJSONString(arLabelIconGallery));
        if(arTagsInfoMapper.checkLabelIcon(arLabelIconGallery) == 0){
            arTagsInfoMapper.delLabelIcon(arLabelIconGallery);
        }
        return Result.success("删除成功");
    }

    @Override
    public Result<Boolean> checkLabelIcon(ArLabelIconGallery arLabelIconGallery) {
        int i = arTagsInfoMapper.checkLabelIcon(arLabelIconGallery);
        if(i>0){
            return Result.data(false);
        }else{
            return Result.data(true);
        }
    }

    @Override
    public Result<List<ArTagMoveRes>> queryMapAllTags(ArTagFilterVO tagFilterVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userId = user.getUserId();
        String industryCode = user.getIndustryCode();
        List<ArTagMoveRes> getArTagByMap=new ArrayList<>();
        List<ArTagMoveRes> getArTagByDevice=new ArrayList<>();
        List<ArTagMoveRes> getArTagByIot = new ArrayList<>();
        List<ArTagMoveRes> getArTagByUav = new ArrayList<>();
        List<ArTagMoveRes> getArTagByRadar = new ArrayList<>();
        List<ArTagMoveRes> getArTagByHorn = new ArrayList<>();
        //地图经纬度标签
        String tagMapIds =getMemoryValue(LABEL_TREE_MAP_102,industryCode,userId);
        if (!StringUtils.isEmpty(tagMapIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagMapIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByMap = arTagsScopeServiceImpl.getArTagByMap(tagFilterVO);
        }

        //摄像机标签
        tagFilterVO.setTagIdList(null);
        String tagDeviceIds = getMemoryValue(LABEL_TREE_MAP_103,industryCode,userId);
        if (!StringUtils.isEmpty(tagDeviceIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagDeviceIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByDevice = arTagsScopeServiceImpl.getArTagByDevice(tagFilterVO);
        }

        //物联设备标签
        tagFilterVO.setTagIdList(null);
        String tagIotIds = getMemoryValue(LABEL_TREE_MAP_105,industryCode,userId);
        if (!StringUtils.isEmpty(tagIotIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagIotIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByIot = arTagsScopeServiceImpl.getArTagByIot(tagFilterVO);
        }

        //无人机标签
        tagFilterVO.setTagIdList(null);
        String tagUavIds = getMemoryValue(LABEL_TREE_MAP_106,industryCode,userId);
        if (!StringUtils.isEmpty(tagUavIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagUavIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByUav = arTagsScopeServiceImpl.getArTagByUav(tagFilterVO);
        }

        //雷达标签
        tagFilterVO.setTagIdList(null);
        String tagSensorIds = getMemoryValue(LABEL_TREE_MAP_107,industryCode,userId);
        if (!StringUtils.isEmpty(tagSensorIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagSensorIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByRadar = arTagsScopeServiceImpl.getArTagBySensor(tagFilterVO);
        }

        //喇叭标签
        tagFilterVO.setTagIdList(null);
        String tagHornIds =getMemoryValue(LABEL_TREE_MAP_108,industryCode,userId);
        if (!StringUtils.isEmpty(tagHornIds)) {
            tagFilterVO.setTagIdList(Arrays.asList(tagHornIds.split(com.chinatower.platform.constants.Constants.COMMA)));
            getArTagByHorn = arTagsScopeServiceImpl.getArTagByHorn(tagFilterVO);
        }

        List<ArTagMoveRes> allTags = Stream.of(getArTagByMap, getArTagByDevice, getArTagByIot, getArTagByUav, getArTagByRadar, getArTagByHorn)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        // 根据 tagName 的首字母顺序排序
        List<ArTagMoveRes> sortedTags = allTags.stream()
                .sorted(Comparator.comparing(ArTagMoveRes::getTagsName))
                .collect(Collectors.toList());
        return Result.data(sortedTags);
    }

    @Override
    public int countMark(String id, String userId) {
        int count = baseMapper.countMark(id, userId);
        return count;
    }

    @Override
    public void addMark(String id, String userId) {
        baseMapper.addMark(id, userId);
    }

    @Override
    public void delMark(String id, String userId) {
        baseMapper.delMark(id, userId);
    }

    @Override
    public void deleteMarks(String id) {
        baseMapper.deleteMarks(id);
    }

    @Override
    public List<String> listMark(String userId) {
        return baseMapper.listMark(userId);
    }

    @Override
    public void addMarks(List<String> arTagsInfoListMarkNew, String userId) {
        baseMapper.addMarks(arTagsInfoListMarkNew, userId);
    }

    @Override
    public void delMarks(List<String> arTagsInfoListMark, String userId) {
        baseMapper.delMarks(arTagsInfoListMark, userId);
    }

    @Override
    public Result weatherInfoList(ArDeviceDTO arDeviceDTO) {

        ArDeviceDTO resDto = new ArDeviceDTO();
        //摄像机设备
        if (!CollectionUtils.isEmpty(arDeviceDTO.getCodeList())) {
            DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
            deviceIndReqDTO.setDeviceCodes(arDeviceDTO.getCodeList());
            //批量查询摄像机设备
            List<DeviceIndDTO> deviceList = baseMapper.getDeviceRegion(arDeviceDTO.getCodeList());
            resDto.setWeatherList(getWeatherInfoList(deviceList));
        }
        //物联网设备、雷达设备
        if (!CollectionUtils.isEmpty(arDeviceDTO.getIotCodeList())) {
            //批量查询物联网设备、雷达设备
            List<DeviceIndDTO> iotDeviceList = baseMapper.getIotDeviceRegion(arDeviceDTO.getIotCodeList());
            resDto.setIotWeatherList(getWeatherInfoList(iotDeviceList));
        }
        // 喇叭设备
        if (!CollectionUtils.isEmpty(arDeviceDTO.getHornCodeList())) {
            //批量查询喇叭设备
            List<DeviceIndDTO> hornDeviceList = baseMapper.getHornDeviceRegion(arDeviceDTO.getHornCodeList());
            resDto.setHornWeatherList(getWeatherInfoList(hornDeviceList));
        }
        // 无人机设备
        if (!CollectionUtils.isEmpty(arDeviceDTO.getUavCodeList())) {
            //批量查询喇叭设备
            List<DeviceIndDTO> uavDeviceList = baseMapper.getUavDeviceRegion(arDeviceDTO.getUavCodeList());
            resDto.setUavWeatherList(getWeatherInfoList(uavDeviceList));
        }
        return Result.data(resDto);
    }

    /**
    * @Author: doujc
    * @Description: 查询天气、空气质量信息
    * @Date: 2023/6/29
    * @Param: [deviceList]
    * @Return: java.util.List<com.chinatower.video.modules.artags.dataobject.ArWeatherInfo>
    * @version:
    */
    public List<ArWeatherInfo> getWeatherInfoList(List<DeviceIndDTO> deviceList) {

        List<ArWeatherInfo> infoList = new ArrayList<>();

        for(DeviceIndDTO deviceIndDTO : deviceList){
            ArWeatherInfo response = new ArWeatherInfo();
            response.setDeviceCode(deviceIndDTO.getDeviceCode());
            //查询空气质量
            QueryWeatherDTO queryWeatherDTO = new QueryWeatherDTO();
            if (!org.springframework.util.StringUtils.isEmpty(deviceIndDTO.getProvinceCode())) {
                queryWeatherDTO.setAdcode(deviceIndDTO.getProvinceCode());
            }
            if (!org.springframework.util.StringUtils.isEmpty(deviceIndDTO.getCityCode())) {
                queryWeatherDTO.setAdcode(deviceIndDTO.getCityCode());
            }
            if (!org.springframework.util.StringUtils.isEmpty(deviceIndDTO.getCountyCode())) {
                queryWeatherDTO.setAdcode(deviceIndDTO.getCountyCode());
            }
            if (org.springframework.util.StringUtils.isEmpty(queryWeatherDTO.getAdcode())) {
                continue;
            }
            log.info("/video-order/air/now 请求报文=={}", JSON.toJSONString(queryWeatherDTO));
            Result<JSONObject> airNowResult = weatherApiService.getAirNow(queryWeatherDTO);
            if (airNowResult.getCode() == Constants.SUCCESS) {
                if (null != airNowResult.getData()) {
                    String dataAirString = JSON.toJSONString(airNowResult.getData());
                    WeatherAirDailyDTO weatherAirDailyDTO = JSON.parseObject(dataAirString, WeatherAirDailyDTO.class);
                    response.setAqi(weatherAirDailyDTO.getAqi());
                    response.setLevel(weatherAirDailyDTO.getLevel());
                    response.setCategory(weatherAirDailyDTO.getCategory());
                    response.setPrimary(weatherAirDailyDTO.getPrimary());
                    response.setPm10(weatherAirDailyDTO.getPm10());
                    response.setPm2p5(weatherAirDailyDTO.getPm2p5());
                }
            }

            //查询天气信息
            log.info("/video-order/weather/now 请求报文=={}", JSON.toJSONString(queryWeatherDTO));
            Result<JSONObject> weatherResult = weatherApiService.getWeatherNow(queryWeatherDTO);
            if (weatherResult.getCode() == Constants.SUCCESS) {
                if (null != weatherResult.getData()) {
                    String weatherString = JSON.toJSONString(weatherResult.getData());
                    ArWeatherInfo arWeatherInfo = JSON.parseObject(weatherString, ArWeatherInfo.class);
                    response.setTemp(arWeatherInfo.getTemp());
                    response.setFeelsLike(arWeatherInfo.getFeelsLike());
                    response.setIcon(arWeatherInfo.getIcon());
                    response.setText(arWeatherInfo.getText());
                }
            }
            infoList.add(response);
        }
        return infoList;
    }

    public List<ArTagTree> listTree(ArTagsInfoReq arTagsInfoReq) {
        List<ArTagTree> tree = baseMapper.listTree(arTagsInfoReq);
        if (!tree.isEmpty()) {
            tree.forEach(this::updateParentMarkFlag);
        }
        return tree;
    }


    public void updateParentMarkFlag(ArTagTree tag) {
        if (tag.getChildren() != null && !tag.getChildren().isEmpty()) {
            tag.getChildren().forEach(this::updateParentMarkFlag);
            // 如果有任何子节点的markFlag为false，则父节点的markFlag也为false
            boolean allChildrenMarked = tag.getChildren().stream().allMatch(child -> 1 == child.getMarkFlag());
            tag.setMarkFlag(allChildrenMarked ? 1 : 0);
        }
    }


    @Override
    public List<ArTagsInfo> pageList(ArTagsInfoReq arTagsInfoReq) {
        PageHelper.startPage(arTagsInfoReq.getPageNum(), arTagsInfoReq.getPageSize(), "");
        List<ArTagsInfo> pageList = arTagsInfoMapper.pageList(arTagsInfoReq);
        return pageList;
    }

    @Override
    public List<ArTagMoveRes> listForCruise(ArTagFilterVO arTagFilterVO){
        List<ArTagMoveRes> list = new ArrayList<>();

        list.addAll(arTagsScopeServiceImpl.getArTagByVideo(arTagFilterVO));

        Set<String> deviceCodeList = new HashSet<>();
        deviceCodeList.add(arTagFilterVO.getArViewId());
        List<ArChannelInfo> arChannelInfo = arTagsInfoMapper.getArChannelInfo(deviceCodeList);
        List<ArChannelInfo> collect = arChannelInfo.stream().filter(
                e -> e.getChannelCode().equals(arTagFilterVO.getChannelCode())).collect(Collectors.toList());

        //枪机通道，只能选择视频手动标签，跳过下面的标签
        if(!CollectionUtils.isEmpty(collect) && !Constants.THREE.equals(collect.get(0).getFacade())){
            list.addAll(arTagsScopeServiceImpl.getArTagByMap(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagByDevice(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagByLayer(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagByIot(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagByUav(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagBySensor(arTagFilterVO));
            list.addAll(arTagsScopeServiceImpl.getArTagByHorn(arTagFilterVO));
        }
        return list;
    }

    private String getMemoryValue(String type,String industryCode,String userId){
     String memoryValue =    RedisUtil.get(com.chinatower.video.modules.memory.Constants.REDIS_MEMORY + industryCode + type
                + com.chinatower.video.modules.memory.Constants.REDIS_TYPE + userId, RedisDBType.PRIMARY);
        return memoryValue;
    }

}