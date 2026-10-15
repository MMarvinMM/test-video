package com.chinatower.bdm.modules.aroundAnalysis.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;

import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTTVO;
import com.chinatower.bdm.modules.gridKeeper.service.BdmGridKeeperService;
import com.chinatower.bdm.modules.resource.service.IResourceConfService;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IReorderDeviceService;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.dto.WarningOrderLayerDTO;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.industry.api.IIndustryDeviceService;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.vo.IndChannelInfoVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AroundAnalysisService {
    @Autowired
    private IIndustryDeviceService industryDeviceService;

    @Resource
    BdmGridKeeperService bdmGridKeeperService;

    @Resource
    ITradeWarningOrderService iTradeWarningOrderService;

    @Resource
    IVideoDeviceService iVideoDeviceService;

    @Autowired
    IResourceConfService resourceConfService;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IReorderDeviceService recorderDeviceApiService;

    /**
     * 周边视频，返回周边站址及前五位通道信息
     * @param deviceRequestVO
     * @return
     */
    public Result<DetectResultVO> getVideoAndUrl(DeviceRequestVO deviceRequestVO) {
        String deviceCode = deviceRequestVO.getDeviceCode();
        String channelCode = deviceRequestVO.getChannelCode();
        DetectResultVO resultVO = new DetectResultVO();
        List<SiteVO> siteList = new ArrayList<>();
        List<UrlVO> urlList = new ArrayList<>();
        DeviceLayerReq deviceLayerReq = new DeviceLayerReq();
        deviceLayerReq.setLongitude(StringUtils.isEmpty(deviceRequestVO.getLongitude())?Constants.NUMBER_ZERO:Double.parseDouble(deviceRequestVO.getLongitude()));
        deviceLayerReq.setLatitude(StringUtils.isEmpty(deviceRequestVO.getLatitude())?Constants.NUMBER_ZERO:Double.parseDouble(deviceRequestVO.getLatitude()));
        deviceLayerReq.setDistance(deviceRequestVO.getRadius());
        deviceLayerReq.setScreenId(deviceRequestVO.getScreenId());
        deviceLayerReq.setScreenType(deviceRequestVO.getScreenType());
        Result<List<IndustryDeviceTree>> result = industryDeviceService.queryDeviceLayer(deviceLayerReq);
        List<IndustryDeviceTree> list = result.getData();
        if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
            for (IndustryDeviceTree siteInfo :list) {
                SiteVO siteVO = new SiteVO();
                BeanUtils.copyProperties(siteInfo, siteVO);
                //默认在线
                siteVO.setStatus(Constants.ZERO);
                //站址下只有一个设备，且该设备状态为离线，将站址状态置为离线
                if(siteInfo.getList().size() ==  Constants.ONE_NUM
                        && siteInfo.getList().get(0).getDeviceStatus().equals(Constants.ONE)){
                    siteVO.setStatus(Constants.ONE);
                }
                siteList.add(siteVO);
                //取距离最近的5个通道
                if (urlList.size() >= Constants.FIVE_NUM) {
                    continue;
                } else {
                    //循环站址下设备
                    device: for(IndustryDeviceTree deviceInfo : siteInfo.getList()) {
                        //循环站址下设备下通道
                        for(IndChannelInfoVO channelInfo : deviceInfo.getChannelInfoVOList()) {
                            if (channelInfo.getChannelStatus() == Constants.ZERO_NUM
                                    && (Constants.ZERO.equals(deviceInfo.getDeviceStatus()))) {
                                //告警事件-关联事件，去掉本身设备通道
                                if (!StringUtils.isEmpty(channelCode) && !StringUtils.isEmpty(deviceCode)) {
                                    if((deviceCode).equals(deviceInfo.getDeviceCode()) &&
                                            (channelCode).equals(channelInfo.getChannelCode())){
                                        continue ;
                                    }
                                }
                                UrlVO urlVO = new UrlVO();
                                urlVO.setDeviceCode(deviceInfo.getDeviceCode());
                                urlVO.setChannelCode(channelInfo.getChannelCode());
                                urlVO.setLatitude(siteInfo.getLatitude());
                                urlVO.setLongitude(siteInfo.getLongitude());
                                urlVO.setSiteCode(siteInfo.getSiteCode());
                                urlList.add(urlVO);
                            }
                            if (urlList.size() == Constants.FIVE_NUM) {
                                break device;
                            }
                        }
                    }
                }
            }
            if (urlList.size() == Constants.ZERO_NUM) {
                return Result.fail(Constants.VIDEO_AND_URL_MSG_1);
            }
            //校验摄像机标定 vm_device_ref_point_t point_flag 是否为参考点 0否1是

            resultVO.setSiteList(siteList);
            resultVO.setUrlList(urlList);
            return Result.data(resultVO);
        } else{
            return Result.fail(Constants.VIDEO_AND_URL_MSG_1);
        }
    }

    /**
     * 周边分析，当前经纬度中心范围内的1.摄像机 2.无人机 3.大喇叭 4.网格员 5.告警,6.资源,7.雷达,8.物联设备 9.执法记录仪
     * 通用使用
     */
    public Result<List<AroundAnalysisResultVO>> getAroundAnalysisOwnList(AroundAnalysisParamVO aroundAnalysisParamVO) {
        log.info("AroundAnalysisService getAroundAnalysisOwnList 接口入参:"+JSON.toJSONString(aroundAnalysisParamVO));
        if(aroundAnalysisParamVO.getType().indexOf(Constants.FIVE)> Constants.MINUS_ONE){
            if(StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeStart()) ||  StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeEnd())){
                throw new CustomException("告警开始时间和告警结束时间不能为空");
            }else{
                Date alarmTimeStart = DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeStart());
                Date alarmTimeEnd = DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeEnd());
                long num = intervalDaysCount(alarmTimeStart,alarmTimeEnd);
                if(num > Constants.SEVEN_NUM){
                    throw new CustomException("最多查询时间跨度为7天的告警");
                }
            }
        }
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();

        //查询摄像机图层
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun1= this::queryDeviceLayer;

        //查询无人机设备距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun2= this::queryUavDeviceDistanceInfo;

        //查询喇叭设备距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun3= this::queryHornDeviceDistanceInfo;

        //周边网格员搜索
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun4= this::getKeeperByPeriphery;

        //查询告警图层
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun5= this::getWarningOrderLayer;

        //资源距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun6= this::getResourceByPeriphery;

        //雷达距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun7= this::queryRadarDevDistanceInfo;

        //物联设备距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun8= this::queryIotDevDistanceInfo;

        //执法记录仪
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun9= this::queryRecorderDevDistanceInfo;


        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put(fun1,aroundAnalysisParamVO);
        paramMap.put(fun2,aroundAnalysisParamVO);
        paramMap.put(fun3,aroundAnalysisParamVO);
        paramMap.put(fun4,aroundAnalysisParamVO);
        paramMap.put(fun5,aroundAnalysisParamVO);
        paramMap.put(fun6,aroundAnalysisParamVO);
        paramMap.put(fun7,aroundAnalysisParamVO);
        paramMap.put(fun8,aroundAnalysisParamVO);
        paramMap.put(fun9,aroundAnalysisParamVO);

        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3,fun4,fun5,fun6,fun7,fun8,fun9);
        List<Object> list = ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.AROUND_ANALYSIS);

        if(!CollectionUtils.isEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                List<AroundAnalysisResultVO> newAroundAnalysisResultVOList = (List<AroundAnalysisResultVO>) list.get(i);
                for (int j = 0; j < newAroundAnalysisResultVOList.size(); j++) {
                    aroundAnalysisResultVOList.add(newAroundAnalysisResultVOList.get(j));
                }
            }
            //按照距离正向排序
            aroundAnalysisResultVOList = aroundAnalysisResultVOList.stream().sorted(Comparator.comparing(s -> Double.valueOf(s.getDistance()))).collect(Collectors.toList());
        }
        return Result.data(aroundAnalysisResultVOList);
    }


    /**
     * 周边分析，当前经纬度中心范围内的告警/无人机/摄像机/云广播/网格员
     * @Author：zl
     * @param aroundAnalysisParamVO
     * @return List<AroundAnalysisResultVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__周边分析接口
     */
    public Result<List<AroundAnalysisResultVO>> getAroundAnalysisList(AroundAnalysisParamVO aroundAnalysisParamVO) {
        log.info("getAroundAnalysisList接口入参:"+JSON.toJSONString(aroundAnalysisParamVO));
        if(aroundAnalysisParamVO.getType().indexOf(Constants.FIVE)> Constants.MINUS_ONE){
            if(StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeStart()) ||  StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeEnd())){
                throw new CustomException("告警开始时间和告警结束时间不能为空");
            }else{
                Date alarmTimeStart = DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeStart());
                Date alarmTimeEnd = DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeEnd());
                Long num = intervalDaysCount(alarmTimeStart,alarmTimeEnd);
                if(num > Constants.SEVEN_NUM){
                    throw new CustomException("最多查询时间跨度为7天的告警");
                }
            }
        }
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();

        //查询摄像机图层
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun1= this::queryDeviceLayer;

        //查询无人机设备距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun2= this::queryUavDeviceDistanceInfo;

        //查询喇叭设备距离信息
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun3= this::queryHornDeviceDistanceInfo;

        //周边网格员搜索
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun4= this::getKeeperByPeriphery;

        //查询告警图层
        Function<AroundAnalysisParamVO,List<AroundAnalysisResultVO>> fun5= this::getWarningOrderLayer;

        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put(fun1,aroundAnalysisParamVO);
        paramMap.put(fun2,aroundAnalysisParamVO);
        paramMap.put(fun3,aroundAnalysisParamVO);
        paramMap.put(fun4,aroundAnalysisParamVO);
        paramMap.put(fun5,aroundAnalysisParamVO);

        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3,fun4,fun5);
        List<Object> list = ThreadParallelUtil.run(listFunc, f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.AROUND_ANALYSIS);

        if(!CollectionUtils.isEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                List<AroundAnalysisResultVO> newAroundAnalysisResultVOList = (List<AroundAnalysisResultVO>) list.get(i);
                for (int j = 0; j < newAroundAnalysisResultVOList.size(); j++) {
                    aroundAnalysisResultVOList.add(newAroundAnalysisResultVOList.get(j));
                }
            }
            //按照距离正向排序
            aroundAnalysisResultVOList = aroundAnalysisResultVOList.stream().sorted(Comparator.comparing(s -> Double.valueOf(s.getDistance()))).collect(Collectors.toList());
        }
        return Result.data(aroundAnalysisResultVOList);
    }


    /**
     * 查询摄像机图层
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryDeviceLayer(AroundAnalysisParamVO aroundAnalysisParamVO){
        log.info("查询摄像机图层");
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.ONE)){
                DeviceLayerReq deviceLayerReq = new DeviceLayerReq();
                deviceLayerReq.setLongitude(StringUtils.isEmpty(aroundAnalysisParamVO.getLongitude())?Constants.NUMBER_ZERO:Double.parseDouble(aroundAnalysisParamVO.getLongitude()));
                deviceLayerReq.setLatitude(StringUtils.isEmpty(aroundAnalysisParamVO.getLatitude())?Constants.NUMBER_ZERO:Double.parseDouble(aroundAnalysisParamVO.getLatitude()));
                deviceLayerReq.setDistance(aroundAnalysisParamVO.getRadius());
                deviceLayerReq.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceLayerReq.setScreenType(aroundAnalysisParamVO.getScreenType());
                log.info("queryDeviceLayer" + Constants._PARAM_LOG_REQUEST, JSONObject.toJSONString(deviceLayerReq));
                Result<List<IndustryDeviceTree>> result = industryDeviceService.queryDeviceLayer(deviceLayerReq);
                log.info("queryDeviceLayer" + Constants._PARAM_LOG_RESPONSE, JSONObject.toJSONString(result));
                List<IndustryDeviceTree> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (IndustryDeviceTree siteInfo :list) {
                        String distance = StringUtils.isEmpty(siteInfo.getDistance()) ? Constants.ZERO : siteInfo.getDistance();
                        //不过滤同一个经纬度的数据
                        if (aroundAnalysisParamVO.getNotFilterSameLngLat()) {
                            String distance2f = String.format(Constants.STRING_FORMAT_1F,Double.parseDouble(distance)/Constants.THOUSAND_NUM);
                            for (IndustryDeviceTree devInfo : siteInfo.getList()) {
                                //过滤掉设备本身
                                if (!org.apache.commons.lang3.StringUtils.equals(aroundAnalysisParamVO.getId(), devInfo.getDeviceCode())) {
                                    this.buildDeviceLayerResponse(aroundAnalysisResultVOList, siteInfo, devInfo, distance2f);
                                }
                            }
                        }
                        else if(Double.parseDouble(distance) > 0){
                            String distance2f = String.format(Constants.STRING_FORMAT_1F,Double.parseDouble(distance)/Constants.THOUSAND_NUM);
                            distance2f = (Constants.ZERO_ZERO_FLAG).equals(distance2f) ? Constants.ZERO_ONE_FLAG : distance2f;
                            for (IndustryDeviceTree devInfo : siteInfo.getList()) {
                                this.buildDeviceLayerResponse(aroundAnalysisResultVOList, siteInfo, devInfo, distance2f);
                            }
                        }

                    }
                }
            }
        }
        catch (Exception e){
            log.error("queryDeviceLayer异常",e);
        }
        setDeviceAlarmIsCompleted(aroundAnalysisResultVOList);
        log.info("aroundAnalysisResultVOList :{}", JSON.toJSONString(aroundAnalysisResultVOList));
        return aroundAnalysisResultVOList;
    }

    /**
     * 构建摄像机周边分析返回结果
     * @param aroundAnalysisResultVOList
     */
    private void buildDeviceLayerResponse (List<AroundAnalysisResultVO> aroundAnalysisResultVOList, IndustryDeviceTree siteInfo, IndustryDeviceTree devInfo,  String distance2f) {
        AroundAnalysisResultVO aroundAnalysisResultVO = new AroundAnalysisResultVO();
        aroundAnalysisResultVO.setName(devInfo.getDeviceName());
        aroundAnalysisResultVO.setDistance(distance2f);
        aroundAnalysisResultVO.setLocation(siteInfo.getLocation());
        aroundAnalysisResultVO.setLatitude(siteInfo.getLatitude());
        aroundAnalysisResultVO.setLongitude(siteInfo.getLongitude());
        aroundAnalysisResultVO.setImageUrl(devInfo.getImageUrl());
        aroundAnalysisResultVO.setId(devInfo.getDeviceCode());
        aroundAnalysisResultVO.setStatus(devInfo.getDeviceStatus());
        aroundAnalysisResultVO.setType(Constants.ONE);
        aroundAnalysisResultVOList.add(aroundAnalysisResultVO);
    }


    /**
     * 查询无人机设备距离信息
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryUavDeviceDistanceInfo(AroundAnalysisParamVO aroundAnalysisParamVO){
        log.info("查询无人机设备距离信息");
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.TWO)){
                DeviceDistanceDTO deviceDistanceDTO = new DeviceDistanceDTO();
                deviceDistanceDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                deviceDistanceDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                deviceDistanceDTO.setRadius(aroundAnalysisParamVO.getRadius());
                deviceDistanceDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceDistanceDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                deviceDistanceDTO.setWithFlightStatus(aroundAnalysisParamVO.getWithFlightStatus());
                log.info("queryUavDeviceDistanceInfo" + Constants._PARAM_LOG_REQUEST, JSONObject.toJSONString(deviceDistanceDTO));
                Result<List<DeviceDistanceVO>> result = iVideoDeviceService.queryUavDeviceDistanceInfo(deviceDistanceDTO);
                log.info("queryUavDeviceDistanceInfo" + Constants._PARAM_LOG_RESPONSE, JSONObject.toJSONString(result));
                List<DeviceDistanceVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        DeviceDistanceVO deviceDistanceVO = list.get(i);
                        //构建周边分析无人机返回值
                        this.buildResponse(aroundAnalysisResultVOList, aroundAnalysisParamVO, deviceDistanceVO, Constants.TWO);
                    }
                }
            }
        }  catch (Exception e){
            log.error("queryUavDeviceDistanceInfo异常",e);
        }
        setDeviceAlarmIsCompleted(aroundAnalysisResultVOList);
        return aroundAnalysisResultVOList;
    }

    /**
     * 设备设备告警事件是否完结
     * @param aroundAnalysisResultVOList
     */
    private void setDeviceAlarmIsCompleted(List<AroundAnalysisResultVO> aroundAnalysisResultVOList) {
        if (!CollectionUtils.isEmpty(aroundAnalysisResultVOList)) {
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            List<String> deviceCodeList = aroundAnalysisResultVOList.stream().map(AroundAnalysisResultVO::getId).distinct().collect(Collectors.toList());
            AlarmOrderParmaDto dto = new AlarmOrderParmaDto();
            dto.setDeviceCodes(deviceCodeList);
            dto.setTenantId(sysUser.getTenantId());
            dto.setIndustryCode(sysUser.getIndustryCode());
            log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 请求参数：{}", dto);
            Result<List<OrderCompleteVO>> result = iTradeWarningOrderService.getOrderNumByDeviceCode(dto);
            log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 返回参数：{}", result);
            if(ResultCode.SUCCESS.getCode() == result.getCode()) {
                aroundAnalysisResultVOList.forEach(item -> {
                    String isCompleteFlag = Constants.ZERO;
                    if (!CollectionUtils.isEmpty(result.getData())) {
                        OrderCompleteVO orderCompleteVO = result.getData().stream().filter(vo -> Objects.equals(item.getId(), String.valueOf(vo.getDeviceCode()))).findFirst().orElse(null);
                        isCompleteFlag = Objects.nonNull(orderCompleteVO) ? String.valueOf(orderCompleteVO.getIsCompleteFlag()) : Constants.ZERO;
                    }
                    item.setIsCompleteFlag(isCompleteFlag);
                });
            }
        }
    }

    /**
     * 构建周边分析返回值
     */
    private void buildResponse(List<AroundAnalysisResultVO> aroundAnalysisResultVOList, AroundAnalysisParamVO aroundAnalysisParamVO, DeviceDistanceVO deviceDistanceVO, String type) {
        String distance = StringUtils.isEmpty(deviceDistanceVO.getDistance()) ? Constants.ZERO : deviceDistanceVO.getDistance();
        //不过滤同一经纬度数据
        if (aroundAnalysisParamVO.getNotFilterSameLngLat()) {
            //过滤掉设备本身数据
            if (!org.apache.commons.lang3.StringUtils.equals(aroundAnalysisParamVO.getId(), deviceDistanceVO.getDeviceCode())) {
                this.buildUavDeviceDistanceInfoResponse(aroundAnalysisResultVOList, deviceDistanceVO, distance, type, aroundAnalysisParamVO.getNotFilterSameLngLat());
            }
        }
        else if(Double.parseDouble(distance) > 0) {
            this.buildUavDeviceDistanceInfoResponse(aroundAnalysisResultVOList, deviceDistanceVO, distance, type, aroundAnalysisParamVO.getNotFilterSameLngLat());
        }
    }

    /**
     * 构建周边分析-无人机设备列表返回值
     * @param aroundAnalysisResultVOList
     * @param deviceDistanceVO
     * @param distance
     */
    private void buildUavDeviceDistanceInfoResponse(List<AroundAnalysisResultVO> aroundAnalysisResultVOList, DeviceDistanceVO deviceDistanceVO, String distance, String type, boolean notFilterZero) {
        String distance2f = String.format(Constants.STRING_FORMAT_1F,Double.parseDouble(distance)/Constants.THOUSAND_NUM);
        if (!notFilterZero) {
            distance2f = (Constants.ZERO_ZERO_FLAG).equals(distance2f) ? Constants.ZERO_ONE_FLAG : distance2f;
        }
        AroundAnalysisResultVO aroundAnalysisResultVO = new AroundAnalysisResultVO();
        aroundAnalysisResultVO.setName(deviceDistanceVO.getName());
        aroundAnalysisResultVO.setDistance(distance2f);
        aroundAnalysisResultVO.setLocation(deviceDistanceVO.getLocation());
        aroundAnalysisResultVO.setLatitude(deviceDistanceVO.getLatitude());
        aroundAnalysisResultVO.setLongitude(deviceDistanceVO.getLongitude());
        aroundAnalysisResultVO.setId(deviceDistanceVO.getDeviceCode());
        aroundAnalysisResultVO.setStatus(deviceDistanceVO.getDeviceStatus());
        aroundAnalysisResultVO.setType(type);
        aroundAnalysisResultVO.setAccessNode(deviceDistanceVO.getAccessNode());
        aroundAnalysisResultVO.setUavNestCode(deviceDistanceVO.getUavNestCode());
        aroundAnalysisResultVO.setUavNestName(deviceDistanceVO.getUavNestName());
        aroundAnalysisResultVO.setUavNestStatus(deviceDistanceVO.getUavNestStatus());
        aroundAnalysisResultVO.setFlyStatus(deviceDistanceVO.getFlyStatus());
        aroundAnalysisResultVOList.add(aroundAnalysisResultVO);
    }


    /**
     * 查询喇叭设备距离信息
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryHornDeviceDistanceInfo(AroundAnalysisParamVO aroundAnalysisParamVO){
        log.info("查询喇叭设备距离信息");
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.THREE)){
                DeviceDistanceDTO deviceDistanceDTO = new DeviceDistanceDTO();
                deviceDistanceDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                deviceDistanceDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                deviceDistanceDTO.setRadius(aroundAnalysisParamVO.getRadius());
                deviceDistanceDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceDistanceDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                log.info("queryHornDeviceDistanceInfo" + Constants._PARAM_LOG_REQUEST, JSONObject.toJSONString(deviceDistanceDTO));
                Result<List<DeviceDistanceVO>> result = iVideoDeviceService.queryHornDeviceDistanceInfo(deviceDistanceDTO);
                log.info("queryHornDeviceDistanceInfo" + Constants._PARAM_LOG_RESPONSE, JSONObject.toJSONString(result));
                List<DeviceDistanceVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        DeviceDistanceVO deviceDistanceVO = list.get(i);
                        //构建周边分析无人机返回值
                        this.buildResponse(aroundAnalysisResultVOList, aroundAnalysisParamVO, deviceDistanceVO, Constants.THREE);
                    }
                }
            }
        }  catch (Exception e){
            log.error("queryHornDeviceDistanceInfo异常",e);
        }

        return aroundAnalysisResultVOList;
    }


    /**
     * 周边网格员搜索
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> getKeeperByPeriphery(AroundAnalysisParamVO aroundAnalysisParamVO){
        log.info("周边网格员搜索");
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.FOUR)){
                KeeperPeripheryParam keeperPeripheryParam = new KeeperPeripheryParam();
                keeperPeripheryParam.setLatitude(aroundAnalysisParamVO.getLatitude());
                keeperPeripheryParam.setLongitude(aroundAnalysisParamVO.getLongitude());
                keeperPeripheryParam.setRadius(aroundAnalysisParamVO.getRadius());
                keeperPeripheryParam.setScreenId(aroundAnalysisParamVO.getScreenId());
                keeperPeripheryParam.setScreenType(aroundAnalysisParamVO.getScreenType());
                log.info("getKeeperByPeriphery" + Constants._PARAM_LOG_REQUEST, JSONObject.toJSONString(keeperPeripheryParam));
                Result<List<GridKeeperTTVO>> result = bdmGridKeeperService.getKeeperByPeriphery(keeperPeripheryParam);
                log.info("getKeeperByPeriphery" + Constants._PARAM_LOG_RESPONSE, JSONObject.toJSONString(result));
                List<GridKeeperTTVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        GridKeeperTTVO gridKeeperTTVO = list.get(i);
                        String distance = StringUtils.isEmpty(list.get(i).getDistance()) ? Constants.ZERO : list.get(i).getDistance();
                        //不需要过滤同一个经纬度的数据
                        if (aroundAnalysisParamVO.getNotFilterSameLngLat()) {
                            //过滤设备本身
                            if (!org.apache.commons.lang3.StringUtils.equals(aroundAnalysisParamVO.getId(), String.valueOf(gridKeeperTTVO.getKeeperId()))) {
                                this.buildGridKeeperResponse(aroundAnalysisResultVOList, gridKeeperTTVO, distance, aroundAnalysisParamVO.getNotFilterSameLngLat());
                            }
                        }
                        else if(Double.parseDouble(distance) > 0) {
                            this.buildGridKeeperResponse(aroundAnalysisResultVOList, gridKeeperTTVO, distance, aroundAnalysisParamVO.getNotFilterSameLngLat());
                        }
                    }
                }
            }
        }  catch (Exception e){
            log.error("getKeeperByPeriphery异常",e);
        }

        return aroundAnalysisResultVOList;
    }

    /**
     * 构建网格员返回值
     * @param aroundAnalysisResultVOList
     * @param gridKeeperTTVO
     * @param distance
     */
    private void buildGridKeeperResponse(List<AroundAnalysisResultVO> aroundAnalysisResultVOList, GridKeeperTTVO gridKeeperTTVO, String distance, boolean notFilterZero) {
        String distance2f = String.format(Constants.STRING_FORMAT_1F, Double.parseDouble(distance) / Constants.THOUSAND_NUM);
        if (!notFilterZero) {
            distance2f = (Constants.ZERO_ZERO_FLAG).equals(distance2f) ? Constants.ZERO_ONE_FLAG : distance2f;
        }
        AroundAnalysisResultVO aroundAnalysisResultVO = new AroundAnalysisResultVO();
        aroundAnalysisResultVO.setName(gridKeeperTTVO.getKeeperName());
        aroundAnalysisResultVO.setDistance(distance2f);
        aroundAnalysisResultVO.setLocation(gridKeeperTTVO.getKeeperAddress());
        aroundAnalysisResultVO.setLatitude(gridKeeperTTVO.getLatitude());
        aroundAnalysisResultVO.setLongitude(gridKeeperTTVO.getLongitude());
        aroundAnalysisResultVO.setType(Constants.FOUR);
        aroundAnalysisResultVO.setStatus(gridKeeperTTVO.getIsLine());
        aroundAnalysisResultVO.setId(String.valueOf(gridKeeperTTVO.getKeeperId()));
        aroundAnalysisResultVOList.add(aroundAnalysisResultVO);
    }


    /**
     * 查询告警图层
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> getWarningOrderLayer(AroundAnalysisParamVO aroundAnalysisParamVO){
        log.info("查询告警图层");
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.FIVE)){
                WarningOrderLayerDTO warningOrderLayerDTO = new WarningOrderLayerDTO();
                warningOrderLayerDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                warningOrderLayerDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                warningOrderLayerDTO.setRadius(aroundAnalysisParamVO.getRadius());
                warningOrderLayerDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                warningOrderLayerDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                if(!StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeStart()) && !StringUtils.isEmpty(aroundAnalysisParamVO.getAlarmTimeEnd())){
                    warningOrderLayerDTO.setAlarmTimeStart(DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeStart()));
                    warningOrderLayerDTO.setAlarmTimeEnd(DateUtils.string2Date(aroundAnalysisParamVO.getAlarmTimeEnd()));
                }
                Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(Constants.LIMIT_COUNT);
                String limitCount = "";
                if (respDictData.getCode() == ResultCode.SUCCESS.getCode() && !ObjectUtils.isEmpty(respDictData.getData())) {
                    limitCount = respDictData.getData().get(0).getDictValue();
                    warningOrderLayerDTO.setLimitCount(Integer.valueOf(limitCount));
                }
                long oldTime = System.currentTimeMillis();
                log.info("getWarningOrderLayer" + Constants._PARAM_LOG_REQUEST, JSONObject.toJSONString(warningOrderLayerDTO));
                Result result = iTradeWarningOrderService.getWarningOrderLayer(warningOrderLayerDTO);
                log.info("getWarningOrderLayer" + Constants._PARAM_LOG_RESPONSE, JSONObject.toJSONString(result));
                long nowTime = System.currentTimeMillis();
                log.info("查询告警图层接口返回时间 {}",(nowTime - oldTime) + "毫秒");
                List<LinkedHashMap> list = (List<LinkedHashMap>)result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    log.info("查询告警图层接口返回告警列表大小:"+ list.size());
                    for (int i = 0; i < list.size(); i++) {
                        Map jo = list.get(i);
                        String distance = StringUtils.isEmpty(jo.get(Constants.DISTANCE)) ? Constants.ZERO : (String)jo.get(Constants.DISTANCE);
                        //需要过滤同个经纬度的告警数据
                        if(!aroundAnalysisParamVO.getNotFilterSameLngLat() && Double.parseDouble(distance) <= 0) {
                            continue;
                        }
                        String distance2f = String.format(Constants.STRING_FORMAT_1F, Double.parseDouble(distance) / Constants.THOUSAND_NUM);
                        if (!aroundAnalysisParamVO.getNotFilterSameLngLat()) {
                            distance2f = (Constants.ZERO_ZERO_FLAG).equals(distance2f) ? Constants.ZERO_ONE_FLAG : distance2f;
                        }
                        AroundAnalysisResultVO aroundAnalysisResultVO = new AroundAnalysisResultVO();
                        aroundAnalysisResultVO.setName(String.valueOf(jo.get(Constants.ALARMBODY)));
                        aroundAnalysisResultVO.setDistance(distance2f);
                        aroundAnalysisResultVO.setLocation(String.valueOf(jo.get(Constants.ADDRESS)));
                        aroundAnalysisResultVO.setLatitude(String.valueOf(jo.get(Constants.LATITUDE)));
                        aroundAnalysisResultVO.setLongitude(String.valueOf(jo.get(Constants.LONGITUDE)));
                        aroundAnalysisResultVO.setType(Constants.FIVE);
                        aroundAnalysisResultVO.setId(String.valueOf(jo.get(Constants.WARNING_ORDER_ID)));
                        aroundAnalysisResultVO.setImportanceLevel(String.valueOf(jo.get(Constants.IMPORTANCE_LEVEL)));
                        aroundAnalysisResultVO.setImportanceLevelName(String.valueOf(jo.get(Constants.IMPORTANCE_LEVEL_NAME)));
                        aroundAnalysisResultVOList.add(aroundAnalysisResultVO);
                    }
                }
            }
        }  catch (Exception e){
            log.error("getWarningOrderLayer异常==",e);
        }
        return aroundAnalysisResultVOList;
    }


    /**
     * 周边资源搜索
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> getResourceByPeriphery(AroundAnalysisParamVO aroundAnalysisParamVO){
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.SIX)){
                Result<List<ResourceConf>> result = resourceConfService.getResourceByPeriphery(aroundAnalysisParamVO);
                List<ResourceConf> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        ResourceConf resourceConf = list.get(i);
                        String distance = StringUtils.isEmpty(list.get(i).getDistance()) ? Constants.ZERO : list.get(i).getDistance();
                        //需要过滤同个经纬度的资源
                        if (aroundAnalysisParamVO.getNotFilterSameLngLat()) {
                            //过滤掉资源本身
                            if (!org.apache.commons.lang3.StringUtils.equals(aroundAnalysisParamVO.getId(), resourceConf.getResourceId())) {
                                this.buildResourceResponse(aroundAnalysisResultVOList, resourceConf, distance, aroundAnalysisParamVO.getNotFilterSameLngLat());
                            }
                        }
                        else if(Double.parseDouble(distance) > 0) {
                            this.buildResourceResponse(aroundAnalysisResultVOList, resourceConf, distance, aroundAnalysisParamVO.getNotFilterSameLngLat());
                        }
                    }
                }
            }
        }  catch (Exception e){
            log.error("getResourceByPeriphery异常",e);
        }
        return aroundAnalysisResultVOList;
    }

    /**
     * 构建周边分析-资源返回值
     * @param aroundAnalysisResultVOList
     * @param resourceConf
     * @param distance
     */
    private void buildResourceResponse(List<AroundAnalysisResultVO> aroundAnalysisResultVOList, ResourceConf resourceConf, String distance, boolean notFilterZero) {
        String distance2f = String.format(Constants.STRING_FORMAT_1F, Double.parseDouble(distance) / Constants.THOUSAND_NUM);
        if (!notFilterZero) {
            distance2f = (Constants.ZERO_ZERO_FLAG).equals(distance2f) ? Constants.ZERO_ONE_FLAG : distance2f;
        }
        AroundAnalysisResultVO aroundAnalysisResultVO = new AroundAnalysisResultVO();
        aroundAnalysisResultVO.setName(resourceConf.getResourceName());
        aroundAnalysisResultVO.setDistance(distance2f);
        aroundAnalysisResultVO.setLocation(resourceConf.getAddr());
        aroundAnalysisResultVO.setLatitude(resourceConf.getLatitude());
        aroundAnalysisResultVO.setLongitude(resourceConf.getLongitude());
        aroundAnalysisResultVO.setType(Constants.SIX);
        aroundAnalysisResultVO.setStatus(resourceConf.getStatus());
        aroundAnalysisResultVO.setIcon(resourceConf.getIcon());
        aroundAnalysisResultVO.setId(resourceConf.getResourceId());
        aroundAnalysisResultVOList.add(aroundAnalysisResultVO);
    }

    /**
     * 查询雷达距离信息
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryRadarDevDistanceInfo(AroundAnalysisParamVO aroundAnalysisParamVO){
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.SEVEN)){
                DeviceDistanceDTO deviceDistanceDTO = new DeviceDistanceDTO();
                deviceDistanceDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                deviceDistanceDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                deviceDistanceDTO.setRadius(aroundAnalysisParamVO.getRadius());
                deviceDistanceDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceDistanceDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                Result<List<DeviceDistanceVO>> result = iVideoDeviceService.queryRadarDevDistanceInfo(deviceDistanceDTO);
                List<DeviceDistanceVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        DeviceDistanceVO deviceDistanceVO = list.get(i);
                        this.buildResponse(aroundAnalysisResultVOList, aroundAnalysisParamVO, deviceDistanceVO, Constants.SEVEN);
                    }
                }
            }
        }  catch (Exception e){
            log.error("queryRadarDevDistanceInfo异常",e);
        }
        setDeviceAlarmIsCompleted(aroundAnalysisResultVOList);
        return aroundAnalysisResultVOList;
    }

    /**
     * 查询物联设备距离信息
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryIotDevDistanceInfo(AroundAnalysisParamVO aroundAnalysisParamVO){
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.EIGHT)){
                DeviceDistanceDTO deviceDistanceDTO = new DeviceDistanceDTO();
                deviceDistanceDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                deviceDistanceDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                deviceDistanceDTO.setRadius(aroundAnalysisParamVO.getRadius());
                deviceDistanceDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceDistanceDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                Result<List<DeviceDistanceVO>> result = iVideoDeviceService.queryIotDevDistanceInfo(deviceDistanceDTO);
                List<DeviceDistanceVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        DeviceDistanceVO deviceDistanceVO = list.get(i);
                        this.buildResponse(aroundAnalysisResultVOList, aroundAnalysisParamVO, deviceDistanceVO, Constants.EIGHT);
                    }
                }
            }
        }  catch (Exception e){
            log.error("queryIotDevDistanceInfo异常",e);
        }
        setDeviceAlarmIsCompleted(aroundAnalysisResultVOList);
        return aroundAnalysisResultVOList;
    }

    public long intervalDaysCount(Date startDay,Date endDay) {
        long intervalDays = Constants.ZERO_NUM;
        long starTime = startDay.getTime();
        long endTime = endDay.getTime();
        long num = endTime - starTime;//时间戳相差的毫秒数
        intervalDays = num/Constants.MILLISECONDS_PER_DAY;//除以一天的毫秒数
        return intervalDays;
    }

    /**
     * 查询物联设备距离信息
     * @param aroundAnalysisParamVO
     * @return
     */
    public List<AroundAnalysisResultVO> queryRecorderDevDistanceInfo(AroundAnalysisParamVO aroundAnalysisParamVO){
        List<AroundAnalysisResultVO> aroundAnalysisResultVOList = new ArrayList<>();
        try{
            List<String> typeList = Arrays.asList(aroundAnalysisParamVO.getType().split(Constants.COMMA));
            if(typeList.contains(Constants.NINE)){
                DeviceDistanceDTO deviceDistanceDTO = new DeviceDistanceDTO();
                deviceDistanceDTO.setLatitude(aroundAnalysisParamVO.getLatitude());
                deviceDistanceDTO.setLongitude(aroundAnalysisParamVO.getLongitude());
                deviceDistanceDTO.setRadius(aroundAnalysisParamVO.getRadius());
                deviceDistanceDTO.setScreenId(aroundAnalysisParamVO.getScreenId());
                deviceDistanceDTO.setScreenType(aroundAnalysisParamVO.getScreenType());
                Result<List<DeviceDistanceVO>> result = recorderDeviceApiService.getRecorderDeviceDistanceInfo(deviceDistanceDTO);
                List<DeviceDistanceVO> list = result.getData();
                if(result.getCode() == Constants.CODE_SUCCESS && !CollectionUtils.isEmpty(list)){
                    for (int i = 0; i < list.size(); i++) {
                        DeviceDistanceVO deviceDistanceVO = list.get(i);
                        this.buildResponse(aroundAnalysisResultVOList, aroundAnalysisParamVO, deviceDistanceVO, Constants.NINE);
                    }
                }
            }
        }  catch (Exception e){
            log.error("queryRecorderDevDistanceInfo",e);
        }

        return aroundAnalysisResultVOList;
    }

}
