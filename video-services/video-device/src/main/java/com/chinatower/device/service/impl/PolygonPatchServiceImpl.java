package com.chinatower.device.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.InspectionRouteAPI;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.PolygonPatchConstants;
import com.chinatower.device.dto.PolygonNoByDeviceAndChannelDto;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import com.chinatower.device.mapper.PolygonPatchAlgoAreaMapper;
import com.chinatower.device.mapper.PolygonPatchConfigMapper;
import com.chinatower.device.service.IPolygonPatchService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.DevicePointTurn;
import com.chinatower.event.entity.dto.DevicePointVo;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.AlgorithmMonitorPointVO;
import com.chinatower.platform.entity.device.PolygonPatchAlgoAreaVO;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yy
 */
@Service
@Slf4j
public class PolygonPatchServiceImpl implements IPolygonPatchService {


    @Resource
    PolygonPatchConfigMapper polygonPatchConfigMapper;

    @Resource
    AlgoPolygonAreaService algoPolygonAreaService;

    @Resource
    PolygonPatchAlgoAreaMapper polygonMapper;

    @Resource
    InspectionRouteAPI inspectionRouteAPI;

    @Resource
    ITradeWarningOrderService tradeWarningOrderService;

    public static final String POLYGON_PATCH_REPEAT_MSG = "同一个设备、通道、预置位下、一个图斑只能绑定一个电子围栏，请修改后重新添加";


    @Override
    public List<PolygonPatchConfig> queryPolygonPatchList(PolygonPatchVo polygonPatchVo) {
        if (PolygonPatchConstants.PAGING_SWITCH_OPEN.equals(polygonPatchVo.getPagingSwitch())) {
            if (null == polygonPatchVo.getPageNum() || null == polygonPatchVo.getPageSize()) {
                throw new CustomException("分页参数不能为null", 400);
            }
            PageHelper.startPage(polygonPatchVo.getPageNum(), polygonPatchVo.getPageSize());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(user.getUserType())){
            polygonPatchVo.setTenantId(user.getTenantId());
            polygonPatchVo.setIndustryCode(user.getIndustryCode());
        }else{
            polygonPatchVo.setTenantId(polygonPatchVo.getTenantId());
            polygonPatchVo.setIndustryCode(polygonPatchVo.getIndustryCode());
        }

        return polygonPatchConfigMapper.queryPolygonPatchList(polygonPatchVo);

    }

    @Override
    public PolygonPatchConfig queryPolygonPatch(PolygonPatchVo polygonPatchVo) {
        return polygonPatchConfigMapper.queryPolygonPatch(polygonPatchVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PolygonPatchConfig> addOrUpdatePolygonPatch(List<PolygonPatchConfig> polygonPatchList, boolean isAlgorithm) {

        //根据设备、通道、预置位、图层、图斑、去重
        Map<String, List<String>> polygonMap = new HashMap<>();
        polygonPatchList.forEach(item -> {
            String key = item.getDeviceCode() + item.getChannelCode() + item.getPresetId() + item.getLayerId() + item.getFeatureId();
            if (polygonMap.containsKey(key)) {
                List<String> configs = polygonMap.get(key);
                if (configs.contains(item.getConfigId())) {
                    throw new CustomException(POLYGON_PATCH_REPEAT_MSG);
                } else {
                    configs.add(item.getConfigId());
                    polygonMap.put(key, configs);
                }
            } else {
                List<String> configs = new ArrayList<>();
                configs.add(item.getConfigId());
                polygonMap.put(key, configs);
            }
        });


        //区分新增-编辑的电子围栏
        List<PolygonPatchConfig> addPolygonPatchList = new ArrayList<>();
        List<PolygonPatchConfig> updatePolygonPatchList = new ArrayList<>();

        SysUser user = SecurityUtils.getLoginUser().getUser();
        DateTime dateTime = new DateTime();

        //构建map  LayerId + FeatureId : polygonId
        Map<String, String> configAreaIdAndPolygonIdMap = new HashMap<>();

        //去重
        Set<PolygonPatchConfig> uniqueList = new HashSet<>();
        polygonPatchList.forEach(item -> {
            PolygonPatchConfig config = new PolygonPatchConfig();
            BeanUtil.copyProperties(item, config, "configId");
            uniqueList.add(config);
        });

        uniqueList.forEach(item -> {
            if (StringUtils.isEmpty(item.getPresetId())) {
                throw new CustomException("预置位不能为空");
            }
            item.setPolygonPoints(JSON.toJSONString(item.getPolygonPointList()));
            item.setTenantId(user.getTenantId());
            item.setIndustryCode(user.getIndustryCode());
            item.setAppVerCode(user.getAppVerCode());
            item.setDeleteFlag(0);
            item.setLayerIdFeatureIdConcat(item.getLayerId() + item.getFeatureId());
            if (StringUtils.isEmpty(item.getPolygonId())) {
                item.setPolygonId(UUID.randomUUID().toString().replace("-", ""));
                item.setCreateBy(user.getUserName());
                item.setCreateTime(dateTime);
                addPolygonPatchList.add(item);
            } else {
                item.setUpdateBy(user.getUserName());
                item.setUpdateTime(dateTime);
                updatePolygonPatchList.add(item);
            }
            configAreaIdAndPolygonIdMap.put(item.getPresetId() + item.getLayerId() + item.getFeatureId(), item.getPolygonId());
        });

        if (!addPolygonPatchList.isEmpty()) {
            log.info("查询图斑是否绑定:{}", addPolygonPatchList);
            //判断新增的设备、通道、预置位下图斑是否被绑定
            List<String> list = polygonPatchConfigMapper.isExistLayerPatchList(addPolygonPatchList, user.getIndustryCode(), user.getTenantId());
            if (!list.isEmpty()) {
                throw new CustomException("当前设备、通道、预置位下该图斑已被绑定，请删除绑定关系或编辑电子围栏");
            }
            //获取不同设备、通道、预置位下不同的最大名称序号
            List<PolygonNoByDeviceAndChannelDto> polygonNoByDeviceAndChannel = polygonPatchConfigMapper.getPolygonNoByDeviceAndChannel(addPolygonPatchList);
            //构建map {deviceCode+channelCode+presetId+tenantId+industryCode: polygonNo}
            Map<String, Integer> map = new HashMap<>();
            polygonNoByDeviceAndChannel.forEach(item -> map.put(item.getDeviceCode() + item.getChannelCode() + item.getPresetId() + item.getTenantId() + item.getIndustryCode(), item.getPolygonNo()));
            //排序，确保名称序号增加正确
            addPolygonPatchList.sort(Comparator.comparing(PolygonPatchConfig::getMonitorAreaId));
            addPolygonPatchList.forEach(item -> {
                Integer maxNo = map.get(item.getDeviceCode() + item.getChannelCode() + item.getPresetId() + item.getTenantId() + item.getIndustryCode());
                int polygonNo = null == maxNo ? 1 : maxNo + 1;
                item.setPolygonNo(polygonNo);
                map.put(item.getDeviceCode() + item.getChannelCode() + item.getPresetId() + item.getTenantId() + item.getIndustryCode(), polygonNo);
            });

            //调用微创接口将points转成经纬度，构造成map格式
            this.turn(addPolygonPatchList, Constants.ZERO);
            polygonPatchConfigMapper.addPolygonPatchBatch(addPolygonPatchList);

        }
        if (!updatePolygonPatchList.isEmpty()) {
            //调用微创接口将points转成经纬度，构造成map格式
            this.turn(updatePolygonPatchList, Constants.ZERO);
            polygonPatchConfigMapper.updatePolygonPatchBatch(updatePolygonPatchList);
            //更新绑定的算法
            algoPolygonAreaService.handlePolygonAreaBindAlgo(updatePolygonPatchList);
        }
        if (isAlgorithm) {
            //设置电子围栏id  并返回给算法
            polygonPatchList.forEach(item -> item.setPolygonId(configAreaIdAndPolygonIdMap.get(item.getPresetId() + item.getLayerId() + item.getFeatureId())));
            log.info("list:{}", polygonPatchList);
            return polygonPatchList;
        }
        return null;
    }

    /**
     * 调用微创接口将points转成经纬度，构造成map格式
     * @param polygonPatchList
     * @param flag 是否自定义ptz  0：否，1：是
     */
    private void turn(List<PolygonPatchConfig> polygonPatchList, String flag){
        try{
            this.turnLonLatCenter(polygonPatchList,flag);
            this.turnLonLat(polygonPatchList,flag);
        }catch (Exception e){
            log.error("百分比转经纬度失败",e);
        }

    }

    /**
     * 电子围栏中心点百分比坐标转为经纬度
     * @param polygonPatchList
     * @param flag 是否自定义ptz  0：否，1：是
     */
    private void turnLonLatCenter(List<PolygonPatchConfig> polygonPatchList, String flag){
        if(!CollectionUtils.isEmpty(polygonPatchList)){
            String deviceCode = null;
            String channelCode = null;
            String paramPan = null;
            String paramTitl = null;
            String paramZoom = null;
            List<DevicePointVo> points = new ArrayList<>();
            for (PolygonPatchConfig polygonPatchConfig : polygonPatchList) {
                deviceCode = polygonPatchConfig.getDeviceCode();
                channelCode = polygonPatchConfig.getChannelCode();
                paramPan = polygonPatchConfig.getPan();
                paramTitl = polygonPatchConfig.getTilt();
                paramZoom = polygonPatchConfig.getZoom();

                List<AlgorithmMonitorPointVO> polygonPointList = polygonPatchConfig.getPolygonPointList();
                DevicePointVo centerPercent = this.findCenterPercent(polygonPointList);
                points.add(centerPercent);
                polygonPatchConfig.setCenterPercentX(centerPercent.getPercentX());
                polygonPatchConfig.setCenterPercentY(centerPercent.getPercentY());
            }
            DevicePointTurn devicePointTurn = new DevicePointTurn();
            devicePointTurn.setDeviceCode(deviceCode);
            devicePointTurn.setChannelCode(channelCode);
            devicePointTurn.setPoints(points);
            if(Constants.ONE.equals(flag)){
                devicePointTurn.setParamPan(paramPan);
                devicePointTurn.setParamTitl(paramTitl);
                devicePointTurn.setParamZoom(paramZoom);
            }

            //调用微创接口将points转成经纬度，构造成map格式
            Map<String, String> map = this.turnLatitudeByPoint(devicePointTurn);

            for (PolygonPatchConfig polygonPatchConfig : polygonPatchList) {
                polygonPatchConfig.setCenterLongitude(map.get(String.valueOf(polygonPatchConfig.getCenterPercentX())));
                polygonPatchConfig.setCenterLatitude(map.get(String.valueOf(polygonPatchConfig.getCenterPercentY())));
            }
        }
    }

    /**
     * 电子围栏百分比坐标转为经纬度
     * @param polygonPatchList
     * @param flag 是否自定义ptz  0：否，1：是
     */
    private void turnLonLat(List<PolygonPatchConfig> polygonPatchList, String flag){
        if(!CollectionUtils.isEmpty(polygonPatchList)){
            String deviceCode = null;
            String channelCode = null;
            String paramPan = null;
            String paramTitl = null;
            String paramZoom = null;
            List<DevicePointVo> points = new ArrayList<>();
            for (PolygonPatchConfig polygonPatchConfig : polygonPatchList) {
                deviceCode = polygonPatchConfig.getDeviceCode();
                channelCode = polygonPatchConfig.getChannelCode();
                paramPan = polygonPatchConfig.getPan();
                paramTitl = polygonPatchConfig.getTilt();
                paramZoom = polygonPatchConfig.getZoom();

                List<AlgorithmMonitorPointVO> polygonPointList = polygonPatchConfig.getPolygonPointList();

                for (AlgorithmMonitorPointVO algorithmMonitorPointVO : polygonPointList) {
                    DevicePointVo devicePointVo = new DevicePointVo();
                    devicePointVo.setPercentX(String.valueOf(algorithmMonitorPointVO.getPercentX()));
                    devicePointVo.setPercentY(String.valueOf(algorithmMonitorPointVO.getPercentY()));
                    points.add(devicePointVo);
                }
            }
            DevicePointTurn devicePointTurn = new DevicePointTurn();
            devicePointTurn.setDeviceCode(deviceCode);
            devicePointTurn.setChannelCode(channelCode);
            devicePointTurn.setPoints(points);
            if(Constants.ONE.equals(flag)){
                devicePointTurn.setParamPan(paramPan);
                devicePointTurn.setParamTitl(paramTitl);
                devicePointTurn.setParamZoom(paramZoom);
            }

            //调用微创接口将points转成经纬度，构造成map格式
            Map<String, String> map = this.turnLatitudeByPoint(devicePointTurn);

            for (PolygonPatchConfig polygonPatchConfig : polygonPatchList) {
                List<AlgorithmMonitorPointVO> polygonPointList = polygonPatchConfig.getPolygonPointList();
                JSONArray ja = new JSONArray();
                for (AlgorithmMonitorPointVO algorithmMonitorPointVO : polygonPointList) {
                    if(!StringUtils.isEmpty(String.valueOf(algorithmMonitorPointVO.getPercentX())) &&
                            !StringUtils.isEmpty(String.valueOf(algorithmMonitorPointVO.getPercentY())) &&
                            !StringUtils.isEmpty(map.get(String.valueOf(algorithmMonitorPointVO.getPercentX()))) &&
                            !StringUtils.isEmpty(map.get(String.valueOf(algorithmMonitorPointVO.getPercentY())))){
                        JSONObject jo = new JSONObject();
                        jo.put("longitude",map.get(String.valueOf(algorithmMonitorPointVO.getPercentX())));
                        jo.put("latitude", map.get(String.valueOf(algorithmMonitorPointVO.getPercentY())));
                        ja.add(jo);
                    }
                }
                if(!CollectionUtils.isEmpty(ja)){
                    polygonPatchConfig.setPolygonPointsLonLat(ja.toJSONString());
                }
            }
        }
    }

    public Map<String,String> turnLatitudeByPoint(DevicePointTurn devicePointTurn){
        Map<String,String> map = new HashMap<>();
        log.info("tradeWarningOrderService.turnLatitudeByPoint入参：" + JSON.toJSONString(devicePointTurn));
        Result<DevicePointTurn> devicePointTurnResult = tradeWarningOrderService.turnLatitudeByPoint(devicePointTurn);
        log.info("tradeWarningOrderService.turnLatitudeByPoint出参：" + JSON.toJSONString(devicePointTurnResult));
        if(devicePointTurnResult.getCode() == ResultCode.SUCCESS.getCode()){
            DevicePointTurn data = devicePointTurnResult.getData();
            List<DevicePointVo> resultPoints = data.getPoints();
            for (DevicePointVo resultPoint : resultPoints) {
                map.put(resultPoint.getPercentX(),resultPoint.getLongitude());
                map.put(resultPoint.getPercentY(),resultPoint.getLatitude());
            }
        }
        return map;
    }

    public DevicePointVo findCenterPercent(List<AlgorithmMonitorPointVO> monitorPoints) {
        DevicePointVo devicePointVo = new DevicePointVo();
        if (monitorPoints == null || monitorPoints.isEmpty()) {
            System.out.println("The list is empty or null.");
            return devicePointVo;
        }

        Float maxPercentX = monitorPoints.get(0).getPercentX();
        Float minPercentX = monitorPoints.get(0).getPercentX();
        Float maxPercentY = monitorPoints.get(0).getPercentY();
        Float minPercentY = monitorPoints.get(0).getPercentY();

        for (AlgorithmMonitorPointVO point : monitorPoints) {
            Float currentPercentX = point.getPercentX();
            Float currentPercentY = point.getPercentY();

            if (currentPercentX > maxPercentX) {
                maxPercentX = currentPercentX;
            }
            if (currentPercentX < minPercentX) {
                minPercentX = currentPercentX;
            }
            if (currentPercentY > maxPercentY) {
                maxPercentY = currentPercentY;
            }
            if (currentPercentY < minPercentY) {
                minPercentY = currentPercentY;
            }
        }
        devicePointVo.setPercentX(String.format("%.3f", (minPercentX + maxPercentX) / 2));
        devicePointVo.setPercentY(String.format("%.3f", (minPercentY + maxPercentY) / 2));
        return devicePointVo;
    }

    @Override
    public void deletePolygonPatch(PolygonPatchVo polygonPatchVo) {
        PolygonPatchConfig polygonPatchConfig = polygonPatchConfigMapper.queryByPolygonId(polygonPatchVo.getPolygonId());
        if (polygonPatchConfig == null) {
            throw new CustomException("不存在当前电子围栏");
        }
        //查询 电子围栏是否被算法使用
        PolygonPatchAlgoAreaVO polygonPatchAlgoAreaVO = new PolygonPatchAlgoAreaVO();
        polygonPatchAlgoAreaVO.setPolygonId(polygonPatchConfig.getPolygonId());
        polygonPatchAlgoAreaVO.setMonitorAreaId(polygonPatchConfig.getMonitorAreaId());
        List<PolygonPatchAlgoAreaVO> polygonAlgoAreaInfo = polygonMapper.getPolygonAlgoAreaInfo(polygonPatchAlgoAreaVO);
        if (!polygonAlgoAreaInfo.isEmpty()) {
            throw new CustomException(PolygonPatchConstants.HAVE_ALGORITHM_MSG);
        }
        Result<String> stringResult = inspectionRouteAPI.deletePolygonChecked(polygonPatchConfig.getPolygonId());
        log.info("请求/inspection/deletePolygonChecked,入参:{}, 返回:{}", polygonPatchConfig.getPolygonId(), JSON.toJSONString(stringResult));
        if (ObjectUtil.isNotEmpty(stringResult) && ResultCode.SUCCESS.getCode() != (stringResult.getCode())) {
            throw new CustomException(PolygonPatchConstants.HAVE_INSPECTION_MSG);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setUpdateBy(user.getUserName());
        polygonPatchVo.setUpdateTime(new DateTime());
        polygonPatchConfigMapper.updateDeleteFlagById(polygonPatchVo);

    }

    @Override
    @DataPermissions(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Integer isExistByPatchId(PolygonPatchVo polygonPatchVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        return polygonPatchConfigMapper.isExistByPatchId(polygonPatchVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<PolygonPatchConfig> dealPolygonPatchByAlgorithm(List<PolygonPatchConfig> polygonPatchConfig) {
        return addOrUpdatePolygonPatch(polygonPatchConfig, true);
    }

    @Override
    @DataPermissions(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Integer queryPolygonByLayerId(PolygonPatchVo polygonPatchVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        return polygonPatchConfigMapper.queryPolygonByLayerId(polygonPatchVo);
    }

    @Override
    @DataPermissions(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code")
    public PolygonPatchConfig queryByLayerIdAndFeatureId(PolygonPatchVo polygonPatchVo) {
        if (StringUtils.isEmpty(polygonPatchVo.getFeatureId())) {
            throw new CustomException("图斑id不能为null", 400);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        return polygonPatchConfigMapper.queryByLayerIdAndFeatureId(polygonPatchVo);
    }

    @Override
    @DataPermissions(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code",layerColumn = "t2.id")
    public List<PolygonPatchConfig> queryPolygonPatchShow(PolygonPatchVo polygonPatchVo) {
    	List<PolygonPatchConfig> resultList = new ArrayList<>();
        if(polygonPatchVo.getQueryType() == 2){
            if (StringUtils.isEmpty(polygonPatchVo.getFeatureId())) {
                throw new CustomException("图斑id不能为null", 400);
            }
        }else{
            if( StringUtils.isEmpty(polygonPatchVo.getPresetId()) || StringUtils.isEmpty(polygonPatchVo.getLayerId()) ) {
                return resultList;
            }
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        return polygonPatchConfigMapper.queryPolygonPatchShow(polygonPatchVo);
    }


    @Override
    public List<PolygonPatchConfig> queryPolygonPatchShowBatch(PolygonPatchVo polygonPatch) {
        List<PolygonPatchConfig> resultList = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Iterator<PolygonPatchVo> iterator = polygonPatch.getPolygonPatchs().iterator();
        while (iterator.hasNext()) {
            PolygonPatchVo polygonPatchVo = iterator.next();
            if (StringUtils.isEmpty(polygonPatchVo.getPresetId()) || StringUtils.isEmpty(polygonPatchVo.getLayerId())) {
                iterator.remove();
            }else{
                polygonPatchVo.setTenantId(user.getTenantId());
                polygonPatchVo.setIndustryCode(user.getIndustryCode());
            }
        }
        if(!CollectionUtils.isEmpty(polygonPatch.getPolygonPatchs())){
            resultList = polygonPatchConfigMapper.queryPolygonPatchShowBatch(polygonPatch.getPolygonPatchs(),polygonPatch.getDataPermissions());
        }
        return resultList;
    }


    @Override
    public int queryMaxNumber(PolygonPatchVo polygonPatchVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        Integer integer = polygonPatchConfigMapper.queryMaxNumber(polygonPatchVo);
        return null == integer ? 0 : integer;
    }

    @Override
    @DataPermissions(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Integer isExistByLayer(PolygonPatchVo polygonPatchVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        polygonPatchVo.setTenantId(user.getTenantId());
        polygonPatchVo.setIndustryCode(user.getIndustryCode());
        return polygonPatchConfigMapper.isExistByLayer(polygonPatchVo);
    }

    @Override
    public String initPolygonLonLat(){
        //生产2046条
        List<PolygonPatchConfig> polygonPatchConfigs = polygonPatchConfigMapper.queryPendingPolygonList();
        Map<String, List<PolygonPatchConfig>> reduceDevMap = polygonPatchConfigs.stream().collect(Collectors.groupingBy(item ->
                item.getDeviceCode() + "_" + item.getPresetId() + "_" + item.getPan() + "_" + item.getTilt() + "_" + item.getZoom()
        ));

        for (Map.Entry<String, List<PolygonPatchConfig>> entry : reduceDevMap.entrySet()) {
            List<PolygonPatchConfig> value = entry.getValue();
            for (PolygonPatchConfig polygonPatchConfig : value) {
                String polygonPoints = polygonPatchConfig.getPolygonPoints();
                polygonPatchConfig.setPolygonPointList(JSONArray.parseArray(polygonPoints,AlgorithmMonitorPointVO.class));
            }
            //调用微创接口将points转成经纬度，构造成map格式
            this.turn(value, Constants.ONE);
            List<PolygonPatchConfig> collect = value.stream().filter(e -> !StringUtils.isEmpty(e.getCenterLongitude())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(collect)){
                polygonPatchConfigMapper.updatePolygonPatchBatchNew(collect);
            }
        }
        return "更新成功！" + polygonPatchConfigs.size() + "条";
    }

    @Override
    public List<String> getDeviceCodeByLayerId(List<String> layerIds, String industryCode, String tenantId) {
        return polygonPatchConfigMapper.getDeviceCodeByLayerId(layerIds,industryCode,tenantId);
    }

}
