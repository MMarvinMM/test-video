package com.chinatower.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.ApiInterceptOrderEntity;
import com.chinatower.event.api.model.ApiWarningOrderEntity;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.platform.entity.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.chinatower.platform.mapper.ISysMapper;
import com.chinatower.platform.mapper.WorkbenchMapper;
import com.chinatower.platform.service.ExportWarningInfoService;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/4 10:37
 */
@Service
@Slf4j
public class ExportWarningInfoServiceimpl implements ExportWarningInfoService {

    @Autowired
    private ITowerEventWarningService iTowerEventWarningService;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private ISysMapper iSysMapper;

    @Autowired
    private WorkbenchMapper workbenchMapper;

    @Autowired
    private ITowerDeviceService iTowerDeviceService;

    private static final DecimalFormat format = new DecimalFormat("#.000000");
    private final String warning_source = "1,4,8,9,10";
    private final String warning_type_kakou = "1819000";
    @Override
    public AlarmOrderInfoExportDTO getExportWarningDto(ExportOrderInfoReq req) {
        AlarmOrderInfoExportDTO result =  new AlarmOrderInfoExportDTO();

        switch(req.getExportSource()){
            case 1:
                //事件详情
                TradeAlarmOrderDTO dto = new TradeAlarmOrderDTO();
                dto.setWarningOrderId(req.getWarningOrderId());
                Result<WarningOrderInfoVO> OrderResultVo= iTradeWarningOrderService.getOrderInfo(dto);
                log.info("导出详情getOrderInfo返回参数:{}", JSON.toJSONString(OrderResultVo));
                if (req.getExportView() == 1 ){
                    this.handleDeviceLabel(OrderResultVo.getData().getOrder());
                }
                this.getOrderResult(OrderResultVo.getData().getOrder(),result,req);
                if (!CollectionUtils.isEmpty(OrderResultVo.getData().getImgs())){
                    result.setAlarmPicList(OrderResultVo.getData().getImgs());
                }
                break;
            case 2:
                //回收站详情
                Result<Map> interceotResultVo = iTradeWarningOrderService.getInterceptInfoApi(req);
                log.info("导出详情getInterceptInfoApi返回参数:{}", JSON.toJSONString(interceotResultVo));
                OrderInfoVO orderInfoVO = JSONObject.parseObject(JSON.toJSONString(interceotResultVo.getData().get(Constants.PARAM_ORDER)),OrderInfoVO.class);
                if (req.getExportView() == 1 ){
                    this.handleDeviceLabel(orderInfoVO);
                }
                this.getOrderResult(orderInfoVO,result,req);
                if (interceotResultVo.getData().get("imgs") != null){
                    List<Map> imgs = (List<Map>) interceotResultVo.getData().get("imgs");
                        result.setAlarmPicList(imgs);
                }

                break;
            case 3:
              /*  //运营研判详情
                Result operateResultVo = iTowerEventWarningService.getOrderInfoApi(req);
                if(operateResultVo != null){
                    Map map = JSONObject.parseObject(JSON.toJSONString(operateResultVo.getData()),Map.class);
                    Map orderData = JSONObject.parseObject(JSON.toJSONString(map.get(Constants.PARAM_ORDER)),Map.class);
                }*/
               break;
            case 4:
                //属地事件详情
                ApiWarningOrderEntity apiOrderDto = new ApiWarningOrderEntity();
                apiOrderDto.setWarningOrderId(req.getWarningOrderId());
                if (req.getExportType() !=null && req.getExportType() == 1){
                    apiOrderDto.setDeleteFlag(Constants.TWO);
                }else if (req.getExportType() !=null && req.getExportType() == 2){
                    apiOrderDto.setDeleteFlag(Constants.ONE);
                }
                apiOrderDto.setPageNum(1);
                apiOrderDto.setPageSize(50);
                Result apiOrderResultVo = iTowerEventWarningService.getOrderListApi(apiOrderDto);
                log.info("导出详情getOrderListApi返回参数:{}", JSON.toJSONString(apiOrderDto));
                WarningOrderResp orderResp = JSON.parseObject(JSON.toJSONString(apiOrderResultVo.getData()),WarningOrderResp.class);
                WarningOrderEntity orderEntity = orderResp.getList().get(0);
                this.localOrderResult(orderEntity,result,req);
                break;
            case 5:
                //属地回收站详情
                ApiInterceptOrderEntity apiInterceptDto = new ApiInterceptOrderEntity();
                apiInterceptDto.setWarningOrderId(req.getWarningOrderId());
                apiInterceptDto.setPageNum(1);
                apiInterceptDto.setPageSize(50);
                Result apiInterceptResultVo = iTowerEventWarningService.getInterceptListApi(apiInterceptDto);
                log.info("导出详情getInterceptListApi返回参数:{}", JSON.toJSONString(apiInterceptResultVo));
                InterceptResp interceptResp = JSON.parseObject(JSON.toJSONString(apiInterceptResultVo.getData()),InterceptResp.class);
                InterceptOrderEntity interceptOrderEntity = interceptResp.getList().get(0);
                this.localInterceptResult(interceptOrderEntity,result,req);
                break;
            case 6:
               /* ApiOperateOrderEntity apiOperateDto = new ApiOperateOrderEntity();
                apiOperateDto.setWarningOrderId(req.getWarningOrderId());
                Result apiOperateResultVo = iTowerEventWarningService.getOperateListApi(apiOperateDto);
             */   break;
            default: break;

        }
        if(warning_source.contains(result.getWarningSource()) || warning_type_kakou.equals(result.getWarningTypeId())){
            result.setDescription(this.getDescribe(result,result.getExtraField(),result.getWarningSource(),result.getWarningTypeId(),result.getDescription()));
        }
        //告警类型名称
        if(StringUtils.isEmpty(result.getWarningTypeName())){
            String alarmType = workbenchMapper.getAlarmTypeById(result.getWarningTypeId());
            result.setWarningTypeName(alarmType);

        }
        //行业名称
        if(StringUtils.isEmpty(result.getIndustryName())){
            String industryName = workbenchMapper.getIndustryNameById(result.getIndustryCode());
            result.setIndustryName(industryName);

        }

        return result;
    }

    //处理摄像机标签
    public void handleDeviceLabel(OrderInfoVO orderInfoVO) {
        try {
            log.info("handleDeviceLabel-入参{}",JSON.toJSONString(orderInfoVO));
            List<String> deviceCodeList = new ArrayList<>();
            String warningSource = orderInfoVO.getWarningSource();
            //AI，一键，摄像机 直接取deviceCode
            if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_3.equals(warningSource) || Constants.WARNING_SOURCE_4.equals(warningSource)){
                String deviceCode = orderInfoVO.getDeviceCode();
                deviceCodeList.add(deviceCode);
            }else if (Constants.WARNING_SOURCE_8.equals(warningSource)){
                //物联告警
                for (IotDeviceInfoVO iotDeviceInfoVO : orderInfoVO.getIotDevInfoList()) {
                    if (!CollectionUtils.isEmpty(iotDeviceInfoVO.getIotDeviceList())){
                        deviceCodeList.addAll(iotDeviceInfoVO.getIotDeviceList().stream().map(v -> v.getDeviceCode()).distinct().collect(Collectors.toList()));
                    }
                }
            }else if (Constants.WARNING_SOURCE_9.equals(warningSource)){
                //卫星告警
                if (ObjectUtil.isNotNull(orderInfoVO.getSatelliteDeviceList())){
                    deviceCodeList.addAll(orderInfoVO.getSatelliteDeviceList().stream().map(v -> v.getDeviceCode()).distinct().collect(Collectors.toList()));
                }
            }else if (Constants.WARNING_SOURCE_10.equals(warningSource)){
                //雷达告警
                if (ObjectUtil.isNotNull(orderInfoVO.getRadarDevice())){
                    deviceCodeList.add(orderInfoVO.getRadarDevice().getDeviceCode());
                }
            }
            //查询摄像机标签并匹配赋值
            selectAndSetDeviceLabel(deviceCodeList,orderInfoVO);
        }catch (Exception e){
            log.info("handleDeviceLabel处理摄像机标签失败{}",e);
        }
    }


    //查询摄像机标签并匹配赋值
    public void selectAndSetDeviceLabel(List<String> deviceCodeList,OrderInfoVO orderInfoVO) {
        orderInfoVO.setDeviceLabel(new ArrayList<>());
        if (!CollectionUtils.isEmpty(deviceCodeList)){
            log.info("selectAndSetDeviceLabel-调用queryDeviceLabelBatch入参{}",JSON.toJSONString(deviceCodeList));
            Result<List<DeviceLabelInfoVO>> result = iTowerDeviceService.queryDeviceLabelBatch(deviceCodeList);
            log.info("selectAndSetDeviceLabel-调用queryDeviceLabelBatch返回结果{}",JSON.toJSONString(result));
            if (result != null && result.getCode() == 200 && result.getData() != null){
                List<String> labelList = new ArrayList<>();
                List<DeviceLabelInfoVO> data = result.getData();
                String warningSource = orderInfoVO.getWarningSource();
                //AI，一键，摄像机 直接取deviceCode
                if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_3.equals(warningSource) || Constants.WARNING_SOURCE_4.equals(warningSource)){
                    labelList = data.get(0).getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                    orderInfoVO.setDeviceLabel(labelList);
                }else if (Constants.WARNING_SOURCE_8.equals(warningSource)){
                    //物联告警取联动摄像机信息
                    for (IotDeviceInfoVO iotDeviceInfoVO : orderInfoVO.getIotDevInfoList()) {
                        if (!CollectionUtils.isEmpty(iotDeviceInfoVO.getIotDeviceList())){
                            for (IotRelDeviceVo deviceInfoVO : iotDeviceInfoVO.getIotDeviceList()) {
                                for (DeviceLabelInfoVO vo : data) {
                                    if (vo.getDeviceCode().equals(deviceInfoVO.getDeviceCode())){
                                        labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                        deviceInfoVO.setDeviceLabel(labelList);
                                    }
                                }
                            }
                        }
                    }
                }else if (Constants.WARNING_SOURCE_9.equals(warningSource)){
                    for (SatelliteRelDeviceVo satelliteRelDeviceVo : orderInfoVO.getSatelliteDeviceList()) {
                        for (DeviceLabelInfoVO vo : data) {
                            if (vo.getDeviceCode().equals(satelliteRelDeviceVo.getDeviceCode())){
                                labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                satelliteRelDeviceVo.setDeviceLabel(labelList);
                            }
                        }
                    }
                }else if (Constants.WARNING_SOURCE_10.equals(warningSource)){
                    for (DeviceLabelInfoVO vo : data) {
                        if (vo.getDeviceCode().equals(orderInfoVO.getRadarDevice().getDeviceCode())){
                            labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                            orderInfoVO.getRadarDevice().setDeviceLabel(labelList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 事件/回收站详情封装报告入参
     * @param result
     * @return
     */
    public AlarmOrderInfoExportDTO getOrderResult(OrderInfoVO order,AlarmOrderInfoExportDTO result,ExportOrderInfoReq req){
        BeanUtil.copyProperties(order,result);
        BeanUtil.copyProperties(req,result);
        if(!StringUtils.isEmpty(order.getLongitude())){
            //经纬度保留六位逗号拼接
            result.setLotAndLat(format.format(Double.valueOf(order.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getLatitude())));
        }
        if(!StringUtils.isEmpty(order.getIncidentDistance())){
            if (StringUtil.isNotEmpty(order.getAlarmLocation()) && Constants.ONE.equals(order.getAlarmLocation()) && !StringUtils.isEmpty(req.getHoriAzimuthAngle())){
                result.setIncidentDistance(order.getIncidentDistance()+"m");
            }else {
                result.setIncidentDistance("");
            }
        }
        //不开启告警定位 为空
        if(!Constants.ONE.equals(order.getAlarmLocation())){
            req.setDeviceHorizontal("");
            req.setDevicePitch("");
        }

        if ("10".equals(order.getWarningSource()) || "10".equals(order.getRealAlarmSource())){
            List<DeviceSiteInfoVO> siteList = order.getDeviceSiteList();
            if(!CollectionUtils.isEmpty(siteList) && !StringUtils.isEmpty(siteList.get(0).getLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(siteList.get(0).getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(siteList.get(0).getLatitude())));
                result.setDeviceName(siteList.get(0).getDeviceName());
                result.setDeviceCode(siteList.get(0).getDeviceCode());
                result.setModelFactoryName(siteList.get(0).getModelName());
                result.setDeviceAddress(siteList.get(0).getLocation());
            }
        }else {
            if(!StringUtils.isEmpty(order.getVideoLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(order.getVideoLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getVideoLatitude())));
            }
        }

        if(!StringUtils.isEmpty(order.getIsSecondIdentify())){
            if (Constants.ONE.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_NO);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondIdentifyResult())){
            if (Constants.ZERO.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.ONE.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.TWO.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.SECOND_JUDGE_RESULT_3);
            }
        }
        if(!StringUtils.isEmpty(order.getIsSecondJudge())){
            if (Constants.ONE.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_NO);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondJudgeResult())){
            if (Constants.ONE.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.ZERO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.TWO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.SECOND_JUDGE_RESULT_3);
            }
        }



        //联动摄像机
        List<RelDeviceVo> relDeviceList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(order.getIotDeviceList())){
            for (IotRelDeviceVo rel:order.getIotDeviceList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(!CollectionUtils.isEmpty(order.getSatelliteDeviceList())){
            for (SatelliteRelDeviceVo rel:order.getSatelliteDeviceList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setSnapShotTime(rel.getSnapShotTime());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(order.getRadarDevice() != null && StringUtils.isNotEmpty(order.getRadarDevice().getDeviceCode())){
                RelDeviceVo relDeviceVo = new RelDeviceVo();
            SatelliteRelDeviceVo rel = order.getRadarDevice();
            relDeviceVo.setDeviceCode(rel.getDeviceCode());
            relDeviceVo.setDeviceName(rel.getDeviceName());
            relDeviceVo.setHeight(rel.getHeight()+"m");
            relDeviceVo.setModelFactoryName(rel.getModelCodeName());
            relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
            relDeviceVo.setDeviceAddress(rel.getLocation());
            relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
            relDeviceList.add(relDeviceVo);
        }
        result.setRelDeviceList(relDeviceList);
        if(!StringUtils.isEmpty(order.getHeight())){
            result.setHeight(order.getHeight()+"m");
        }
        if((Constants.NINE.equals(order.getWarningSource()) || Constants.NINE.equals(order.getRealAlarmSource())) &&
                (Constants.WARNING_TYPE_ID_GDWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_KCWP.equals(order.getWarningTypeId()) ||
                        Constants.WARNING_TYPE_ID_FNHWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_FLHWP.equals(order.getWarningTypeId()))){
            //卫片告警
            if(!StringUtils.isEmpty(order.getExtraField())){
                RegionAlarmExtraField regionAlarmExtraField = JSONObject.parseObject(order.getExtraField(), RegionAlarmExtraField.class);
                if(regionAlarmExtraField != null){
                    BeanUtil.copyProperties(regionAlarmExtraField,result);
                }
            }
        }
        return result;
    }

    /**
     * 属地事件查询封装入参
     * @param order
     * @param result
     * @param req
     * @return
     */
    public AlarmOrderInfoExportDTO localOrderResult(WarningOrderEntity order,AlarmOrderInfoExportDTO result,ExportOrderInfoReq req){
        //处理WarningOrderEntity与AlarmOrderInfoExportDTO中的polygonOperate类型不一致，避免导出报错
        BeanUtils.copyProperties(order, result, "polygonOperate");
        //BeanUtil.copyProperties(order,result);
        BeanUtil.copyProperties(req,result);
        //1.8.20 手动处理polygonOperate
        if (!StringUtils.isEmpty(order.getPolygonOperate())) {
            try {
                List<Map> polygonList = JSON.parseArray(order.getPolygonOperate(), Map.class);
                result.setPolygonOperate(polygonList);
                log.debug("polygonOperate 解析成功，大小: {}", polygonList.size());
            } catch (Exception e) {
                log.warn("polygonOperate 解析失败，将设为空列表: {}", order.getPolygonOperate(), e);
                result.setPolygonOperate(new ArrayList<>()); // 设为空列表，避免导出时报错
            }
        } else {
            result.setPolygonOperate(new ArrayList<>());
        }
        if(!StringUtils.isEmpty(order.getLongitude())){
            //经纬度保留六位逗号拼接
            result.setLotAndLat(format.format(Double.valueOf(order.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getLatitude())));
        }
        //不开启告警定位 为空
        if(!Constants.ONE.equals(order.getAlarmLocation())){
            req.setDeviceHorizontal("");
            req.setDevicePitch("");
        }
        if ("10".equals(order.getWarningSource()) || "10".equals(order.getRealAlarmSource())){
            List<DeviceInfoEntity> siteList = order.getDeviceSiteList();
            if(!CollectionUtils.isEmpty(siteList) && !StringUtils.isEmpty(siteList.get(0).getLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(siteList.get(0).getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(siteList.get(0).getLatitude())));
                result.setDeviceName(siteList.get(0).getDeviceName());
                result.setDeviceCode(siteList.get(0).getDeviceCode());
                result.setModelFactoryName(siteList.get(0).getModelName());
                result.setDeviceAddress(siteList.get(0).getLocation());
            }
        }else {
            if(!StringUtils.isEmpty(order.getVideoLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(order.getVideoLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getVideoLatitude())));
            }
        }
        if(!StringUtils.isEmpty(order.getIncidentDistance())){
            if (StringUtil.isNotEmpty(order.getAlarmLocation()) && Constants.ONE.equals(order.getAlarmLocation()) && !StringUtils.isEmpty(req.getHoriAzimuthAngle())){
                result.setIncidentDistance(order.getIncidentDistance()+"m");
            }else {
                result.setIncidentDistance("");
            }
        }
        if(!StringUtils.isEmpty(order.getPlatformName())){
            result.setAcceptNodeName(order.getPlatformName());
        }
        if(!StringUtils.isEmpty(order.getIsSecondIdentify())){
            if (Constants.ONE.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_NO);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondIdentifyResult())){
            if (Constants.ZERO.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.ONE.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.TWO.equals(order.getSecondIdentifyResult())){
                result.setSecondIdentifyResult(Constants.SECOND_JUDGE_RESULT_3);
            }
        }
        if(!StringUtils.isEmpty(order.getIsSecondJudge())){
            if (Constants.ONE.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_NO);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondJudgeResult())){
            if (Constants.ONE.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.ZERO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.TWO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.SECOND_JUDGE_RESULT_3);
            }
        }
        //属地流程信息
        // 创建 SimpleDateFormat 对象，指定输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatNodeDate ="";
        String formatEndDate ="";
        if(!CollectionUtils.isEmpty(order.getFlowList())){
            List<OrderFlowRecordEntity> recordEntities = order.getFlowList().stream().filter(v -> v.getFlowEndTime() != null && !"".equals(String.valueOf(v.getFlowEndTime()))).sorted(Comparator.comparing(v -> v.getFlowEndTime())).collect(Collectors.toList());
            List<Map> list = new ArrayList<>();
            for (OrderFlowRecordEntity record : recordEntities) {
                Map map = new HashMap();
                Map fileMap  = new HashMap();
                List<Map> fileList = new ArrayList<>();
                List<Map> fileHandleList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(record.getOrderFileList()) && record.getOrderFileList().size()>0){
                    for (OrderFileDO orderFileDO : record.getOrderFileList()) {
                        fileMap.put(Constants.PARAM_RESOURCE_URL,orderFileDO.getResourceUrl());
                        fileMap.put(Constants.PARAM_FILE_NAME,orderFileDO.getFileName());
                        fileList.add(fileMap);
                        map.put("orderFileList", fileList);
                    }
                }
                if (!CollectionUtils.isEmpty(record.getOrderHandleFileList()) && record.getOrderHandleFileList().size()>0){
                    for (Map maps : record.getOrderHandleFileList()) {
                        fileMap.put(Constants.PARAM_RESOURCE_URL,maps.get(Constants.PARAM_RESOURCE_URL));
                        fileMap.put(Constants.PARAM_FILE_NAME,maps.get(Constants.PARAM_FILE_NAME));
                        fileHandleList.add(fileMap);
                        map.put("files", fileHandleList);
                    }
                }
                map.put("enableAcceptanceMode",StringUtil.isNotEmpty(record.getEnableAcceptanceMode())?record.getEnableAcceptanceMode():"");
                if (record.getFlowNodeTime() !=null){
                    formatNodeDate = dateFormat.format(record.getFlowNodeTime());
                }
                if (record.getFlowEndTime() !=null){
                    formatEndDate = dateFormat.format(record.getFlowEndTime());
                }

                map.put("flowNodeTime",formatNodeDate);
                map.put("flowEndTime",formatEndDate);
                map.put("flowLinkType",StringUtil.isNotEmpty(record.getFlowLinkType())?record.getFlowLinkType():"");
                map.put("linkId",StringUtil.isNotEmpty(record.getLinkId())?record.getLinkId():"");
                map.put("linkName",StringUtil.isNotEmpty(record.getLinkName())?record.getLinkName():"");
                map.put("nextLinkId",StringUtil.isNotEmpty(record.getNextLinkId())?record.getNextLinkId():"");
                map.put("nextLinkName",StringUtil.isNotEmpty(record.getNextLinkName())?record.getNextLinkName():"");
                map.put("nextLinkProcessName",StringUtil.isNotEmpty(record.getNextLinkProcessName())?record.getNextLinkProcessName():"");
                map.put("realLinkType",StringUtil.isNotEmpty(record.getRealLinkType())?record.getRealLinkType():"");
                map.put("remark",StringUtil.isNotEmpty(record.getRemark())?record.getRemark():"");
                map.put("status",StringUtil.isNotEmpty(record.getStatus())?record.getStatus():"");
                map.put("realLinkType",StringUtil.isNotEmpty(record.getRealLinkType())?record.getRealLinkType():"");
                map.put("processName",StringUtil.isNotEmpty(record.getProcessName())?record.getProcessName():"");
                map.put("orderFlowId",StringUtil.isNotEmpty(record.getOrderFlowId())?record.getOrderFlowId():"");
                list.add(map);
            }
            result.setFlowLinkList(list);
        }

        //告警图片
        if(!CollectionUtils.isEmpty(order.getImgList())){
            List<Map> list = new ArrayList<>();
            for (String img : order.getImgList()) {
                Map map = new HashMap();
                map.put("fileUrl",img);
                list.add(map);
            }
            result.setAlarmPicList(list);
        }


        //联动摄像机
        List<RelDeviceVo> relDeviceList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(order.getRelDevice())){
            for (DeviceInfoEntity rel:order.getRelDevice()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(!CollectionUtils.isEmpty(order.getSatelliteDeviceList())){
            for (DeviceInfoEntity rel:order.getSatelliteDeviceList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setSnapShotTime(rel.getSnapShotTime());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(!CollectionUtils.isEmpty(order.getDeviceSiteList())){
            for (DeviceInfoEntity rel:order.getDeviceSiteList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        result.setRelDeviceList(relDeviceList);
        if(!StringUtils.isEmpty(order.getHeight())){
            result.setHeight(order.getHeight()+"m");
        }
        if((Constants.NINE.equals(order.getWarningSource()) || Constants.NINE.equals(order.getRealAlarmSource())) &&
                (Constants.WARNING_TYPE_ID_GDWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_KCWP.equals(order.getWarningTypeId()) ||
                        Constants.WARNING_TYPE_ID_FNHWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_FLHWP.equals(order.getWarningTypeId()))){
            //卫片告警
            if(!StringUtils.isEmpty(order.getExtraField())){
                RegionAlarmExtraField regionAlarmExtraField = JSONObject.parseObject(order.getExtraField(), RegionAlarmExtraField.class);
                if(regionAlarmExtraField != null){
                    BeanUtil.copyProperties(regionAlarmExtraField,result);
                }
            }
        }
        return result;
    }


    /**
     * 属地事件查询封装入参
     * @param order
     * @param result
     * @param req
     * @return
     */
    public AlarmOrderInfoExportDTO localInterceptResult(InterceptOrderEntity order,AlarmOrderInfoExportDTO result,ExportOrderInfoReq req){
        BeanUtils.copyProperties(order, result, "polygonOperate");
        //BeanUtil.copyProperties(order,result);
        BeanUtil.copyProperties(req,result);
        if(!StringUtils.isEmpty(order.getLongitude())){
            //经纬度保留六位逗号拼接
            result.setLotAndLat(format.format(Double.valueOf(order.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getLatitude())));
        }
        //1.8.20 手动处理polygonOperate
        if (!StringUtils.isEmpty(order.getPolygonOperate())) {
            try {
                List<Map> polygonList = JSON.parseArray(order.getPolygonOperate(), Map.class);
                result.setPolygonOperate(polygonList);
                log.debug("polygonOperate 解析成功，大小: {}", polygonList.size());
            } catch (Exception e) {
                log.warn("polygonOperate 解析失败，将设为空列表: {}", order.getPolygonOperate(), e);
                result.setPolygonOperate(new ArrayList<>()); // 设为空列表，避免导出时报错
            }
        } else {
            result.setPolygonOperate(new ArrayList<>());
        }
        //不开启告警定位 为空
        if(!Constants.ONE.equals(order.getAlarmLocation())){
            req.setDeviceHorizontal("");
            req.setDevicePitch("");
        }
        if ("10".equals(order.getWarningSource())){
            List<DeviceInfoEntity> siteList = order.getDeviceSiteList();
            if(!CollectionUtils.isEmpty(siteList) && !StringUtils.isEmpty(siteList.get(0).getLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(siteList.get(0).getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(siteList.get(0).getLatitude())));
                result.setDeviceName(siteList.get(0).getDeviceName());
                result.setDeviceCode(siteList.get(0).getDeviceCode());
                result.setModelFactoryName(siteList.get(0).getModelName());
                result.setDeviceAddress(siteList.get(0).getLocation());
            }
        }else {
            if(!StringUtils.isEmpty(order.getVideoLongitude())){
                result.setDeviceLotAndLat(format.format(Double.valueOf(order.getVideoLongitude()))+ Constants.COMMA+format.format(Double.valueOf(order.getVideoLatitude())));
            }
        }

        if(!StringUtils.isEmpty(order.getIncidentDistance())){
            if (StringUtil.isNotEmpty(order.getAlarmLocation()) && Constants.ONE.equals(order.getAlarmLocation()) && !StringUtils.isEmpty(req.getHoriAzimuthAngle())){
                result.setIncidentDistance(order.getIncidentDistance()+"m");
            }else {
                result.setIncidentDistance("");
            }
        }
        if(!StringUtils.isEmpty(order.getPlatformName())){
            result.setAcceptNodeName(order.getPlatformName());
        }
        if(!StringUtils.isEmpty(order.getIsSecondIdentify())){
            if (Constants.ONE.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondIdentify())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_NO);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondIdentifyResult())){
            if (Constants.ZERO.equals(order.getSecondIdentifyResult())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.ONE.equals(order.getSecondIdentifyResult())){
                result.setIsSecondIdentify(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.TWO.equals(order.getSecondIdentifyResult())){
                result.setIsSecondIdentify(Constants.SECOND_JUDGE_RESULT_3);
            }
        }
        if(!StringUtils.isEmpty(order.getIsSecondJudge())){
            if (Constants.ONE.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_YES);
            }else if (Constants.ZERO.equals(order.getIsSecondJudge())){
                result.setIsSecondJudge(Constants.RESULT_PARAM_NO);
            }else if (Constants.TWO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_FALSE);
            }
        }
        if(!StringUtils.isEmpty(order.getSecondJudgeResult())){
            if (Constants.ONE.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_TRUE);
            }else if (Constants.ZERO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.RESULT_PARAM_FALSE);
            }else if (Constants.TWO.equals(order.getSecondJudgeResult())){
                result.setSecondJudgeResult(Constants.SECOND_JUDGE_RESULT_3);
            }
        }
        //联动摄像机
        List<RelDeviceVo> relDeviceList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(order.getRelDevice())){
            for (DeviceInfoEntity rel:order.getRelDevice()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(!CollectionUtils.isEmpty(order.getSatelliteDeviceList())){
            for (DeviceInfoEntity rel:order.getSatelliteDeviceList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setSnapShotTime(rel.getSnapShotTime());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        if(!CollectionUtils.isEmpty(order.getDeviceSiteList())){
            for (DeviceInfoEntity rel:order.getDeviceSiteList()) {
                RelDeviceVo relDeviceVo = new RelDeviceVo();
                relDeviceVo.setDeviceCode(rel.getDeviceCode());
                relDeviceVo.setDeviceName(rel.getDeviceName());
                relDeviceVo.setHeight(rel.getHeight()+"m");
                relDeviceVo.setModelFactoryName(rel.getModelCodeName());
                relDeviceVo.setDeviceLotAndLat(format.format(Double.valueOf(rel.getLongitude()))+ Constants.COMMA+format.format(Double.valueOf(rel.getLatitude())));
                relDeviceVo.setDeviceAddress(rel.getLocation());
                relDeviceVo.setDeviceLabel(rel.getDeviceLabel());
                relDeviceList.add(relDeviceVo);
            }
        }
        result.setRelDeviceList(relDeviceList);
        if(!StringUtils.isEmpty(order.getHeight())){
            result.setHeight(order.getHeight()+"m");
        }

        if(Constants.NINE.equals(order.getWarningSource()) &&
                (Constants.WARNING_TYPE_ID_GDWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_KCWP.equals(order.getWarningTypeId()) ||
                        Constants.WARNING_TYPE_ID_FNHWP.equals(order.getWarningTypeId()) || Constants.WARNING_TYPE_ID_FLHWP.equals(order.getWarningTypeId()))){
            //卫片告警
            if(!StringUtils.isEmpty(order.getExtraField())){
                RegionAlarmExtraField regionAlarmExtraField = JSONObject.parseObject(order.getExtraField(), RegionAlarmExtraField.class);
                if(regionAlarmExtraField != null){
                    BeanUtil.copyProperties(regionAlarmExtraField,result);
                }
            }
        }
        return result;
    }

    /**
     * 根据不同告警来源告警类型得透传字段返回对应得时间描述话术
     * @param extraFiled
     * @param warningSource
     * @param warningTypeId
     * @return
     */
    public String getDescribe(AlarmOrderInfoExportDTO result,String extraFiled,String warningSource,String warningTypeId,String description) {
        try{
            if ("10".equals(warningSource) && !StringUtils.isEmpty(extraFiled)) {
                //雷达
                RadarParamExportVo param = JSONObject.parseObject(extraFiled, RadarParamExportVo.class);
                extraFiled = "";
                if(!StringUtils.isEmpty(param.getTargetId())){
                    extraFiled = extraFiled+ "目标ID:" + param.getTargetId()+",";
                }
                if(!StringUtils.isEmpty(param.getTargetType())){
                    extraFiled = extraFiled+ "目标类型:" + param.getTargetType()+",";
                }
                if(!StringUtils.isEmpty(param.getMmsi())){
                    extraFiled = extraFiled+"目标MMSI:" + param.getMmsi()+",";
                }
                if(!StringUtils.isEmpty(param.getTargetName())){
                    extraFiled = extraFiled+ "目标名称:" + param.getTargetName()+",";
                }
                if(!StringUtils.isEmpty(param.getSpeed())){
                    extraFiled = extraFiled+ "目标速度:" + param.getSpeed()+",";
                }
                if(!StringUtils.isEmpty(param.getCourse())){
                    extraFiled = extraFiled+ "目标航向:" + param.getCourse()+",";
                }
                if(!StringUtils.isEmpty(param.getTargetLength())){
                    extraFiled = extraFiled+ "目标长度:" + param.getTargetLength()+",";
                }
                extraFiled = extraFiled.substring(0, extraFiled.length() - 1);

                /*extraFiled = "目标ID:" + param.getTargetId() + " 目标类型:" + param.getTargetType() + " 目标MMSI:" + param.getMmsi() + " 目标名称:" + param.getTargetName() +
                        " 目标速度:" + param.getSpeed() + " 目标航向:" + param.getCourse() + " 目标长度:" + param.getTargetLength();*/
            }
            if (Constants.ONE.equals(warningSource) || Constants.FOUR.equals(warningSource) || warning_type_kakou.equals(warningTypeId)) {
                if (!StringUtils.isEmpty(extraFiled)){
                    AiAndDeviceParam param = new AiAndDeviceParam();
                    //AI、摄像机
                    JSONArray a = JSON.parseArray(extraFiled);
                    if (a != null && !a.isEmpty()) {
                        param = JSONObject.parseObject(JSON.toJSONString(a.get(0)), AiAndDeviceParam.class);
                    }
                    extraFiled = "";
                    String valueOne = "";
                    String valueTwo = "";
                    String valueThree = "";
                    String valueFour = "";
                    String valueFive = "";
                    String valueSix = "";
                    String valueSeven = "";
                    String valueEight = "";
                    String valueNine = "";
                    String valueTen = "";
                    String valueEleven = "";
                    if (ObjectUtil.isNotNull(param) && !CollectionUtils.isEmpty(param.getAttrList())){
                        for (AiAndDeviceParam.CarDto dto:param.getAttrList()) {
                            if ("carType".equals(dto.getAttrType())) {
                                valueOne = valueOne+"车辆分类:" + dto.getAttrValue()+",";
                            }
                            if ("carNumber".equals(dto.getAttrType())) {
                                valueTwo = valueTwo+"车牌号码:" + dto.getAttrValue()+",";
                            }
                            if ("plateColor".equals(dto.getAttrType())) {
                                valueThree = valueThree+ "车牌颜色:" + dto.getAttrValue()+",";
                            }
                            if ("objectId".equals(dto.getAttrType())) {
                                valueFour = valueFour+ "监测目标ID:" + dto.getAttrValue()+",";
                            }
                            if ("objectCount".equals(dto.getAttrType())) {
                                valueFive = valueFive+ "监测目标数量:" + dto.getAttrValue()+",";
                            }
                            if ("objectTime".equals(dto.getAttrType())) {
                                valueSix = valueSix+ "目标停留时长（秒）:" + dto.getAttrValue()+",";
                            }
                            if ("alarmCause".equals(dto.getAttrType())) {
                                valueSeven = valueSeven+ "告警原因:" + dto.getAttrValue()+",";
                            }
                            if ("vehicleNumber".equals(dto.getAttrType())) {
                                valueEight = valueEight+ "车牌号:" + dto.getAttrValue()+",";
                            }
                            if ("vehicleType".equals(dto.getAttrType())) {
                                valueNine = valueNine+ "车辆类型:" + dto.getAttrValue()+",";
                            }
                            if ("vehicleNumColor".equals(dto.getAttrType())) {
                                valueTen = valueTen+ "车牌颜色:" + dto.getAttrValue()+",";
                            }
                            if ("vehicleColor".equals(dto.getAttrType())) {
                                valueEleven = valueEleven+ "车辆颜色:" + dto.getAttrValue()+",";
                            }
                        }
                    }
                    //拼接顺序
                    extraFiled = valueOne+valueTwo+valueThree+valueFour+valueFive+valueSix+valueSeven+valueEight+valueNine+valueTen+valueEleven;
                    if (extraFiled.length() > 0){
                        extraFiled = extraFiled.substring(0, extraFiled.length() - 1);
                    }
                }

                //AI 事件描述（算法配置参数）
                if (Constants.ONE.equals(warningSource) && !StringUtils.isEmpty(description)){
                    JSONArray alarmArray = JSON.parseArray(description);
                    InfoAlgorithmConfigExportParam params = new InfoAlgorithmConfigExportParam();
                    if (alarmArray != null && !alarmArray.isEmpty()) {
                        params = JSONObject.parseObject(JSON.toJSONString(alarmArray.get(0)), InfoAlgorithmConfigExportParam.class);
                    }
                    description ="";
                    String timeUnit = "";
                    String time = "";
                    String timeInterval = "";
                    String pipeline = "";
                    if (!StringUtils.isEmpty(params.getTimeInterval())){
                        if ("1".equals(params.getTimeIntervalUnit())){
                            timeUnit = "小时";
                        }else if ("2".equals(params.getTimeIntervalUnit())){
                            timeUnit = "分钟";
                        }else if ("3".equals(params.getTimeIntervalUnit())){
                            timeUnit = "秒";
                        }
                        time = "识别频率:"+params.getTimeInterval()+timeUnit+",";
                    }
                    if (!CollectionUtils.isEmpty(params.getAnalysisPeriod()) && params.getAnalysisPeriod().size()>0){
                        timeInterval = timeInterval+"算法运行时段:";
                        for (InfoAlgorithmConfigExportParam.AnalysisPeriod analysisPeriod : params.getAnalysisPeriod()) {
                            timeInterval = timeInterval+analysisPeriod.getStartTime()+"-"+analysisPeriod.getEndTime()+",";
                        }
                    }
                    if (!CollectionUtils.isEmpty(params.getModelConfig()) && params.getModelConfig().size()>0){
                        for (InfoAlgorithmConfigExportParam.ModelConfig modelConfig : params.getModelConfig()) {
                            if (!CollectionUtils.isEmpty(modelConfig.getDictList()) && modelConfig.getDictList().size()>0){
                                pipeline = pipeline+modelConfig.getName()+":"+modelConfig.getSelectValue()+",";
                            } else if (Constants.VISUAL_PARAMETERS.equals(modelConfig.getName())){
                                pipeline = pipeline+modelConfig.getName()+":"+(StringUtils.isEmpty(modelConfig.getSelectValue()) ? modelConfig.getValue() : modelConfig.getSelectValue()) +Constants.CRLF;
                            } else {
                                pipeline = pipeline+modelConfig.getName()+":"+modelConfig.getValue()+",";
                            }
                        }
                    }

                    description = timeInterval+time+pipeline;
                    description = description.substring(0, description.length() - 1);
                    result.setAlgorithmDescription(description);
                }

            }

                if ("8".equals(warningSource) && !StringUtils.isEmpty(extraFiled)) {
                ExportDocSensorMonitorVo param = JSONObject.parseObject(extraFiled, ExportDocSensorMonitorVo.class);
                extraFiled = "";
                for (SensorMonitorParam vo:param.getMonitors()) {
                    String value = "";
                    List<OneFactoryVO> valueList = vo.getNewMonitors();
                    for (OneFactoryVO oneFactoryVO : valueList) {
                        if (value !=""){
                            value = value +",";
                        }
                        value = value+" "+oneFactoryVO.getItemName()+": "+oneFactoryVO.getItemCode();
                    }
                    extraFiled = extraFiled+value;
                }
           }
            if (Constants.NINE.equals(warningSource)) {
                if ("[]".equals(description) || StringUtils.isEmpty(description)) {
                    return "";
                } else {
                    JSONArray arrays = JSON.parseArray(description);
                    extraFiled ="";
                    if (!"4110102".equals(warningTypeId) && !"4110101".equals(warningTypeId) && !"4110103".equals(warningTypeId) && !"4110104".equals(warningTypeId)){
                        for (Object array:arrays) {
                            Map map = JSONObject.parseObject(JSON.toJSONString(array));
                            if (extraFiled !=""){
                                extraFiled = extraFiled +",";
                            }
                            extraFiled = extraFiled+ " "+map.get("name")+":"+map.get(Constants.VALUE);
                        }
                    }else {
                        for (Object array:arrays) {
                            Map map = JSONObject.parseObject(JSON.toJSONString(array));
                            if (extraFiled !=""){
                                extraFiled = extraFiled +",";
                            }
                            extraFiled = extraFiled+ " "+map.get("name")+":"+(StringUtils.isEmpty(String.valueOf(map.get(Constants.VALUE))) || map.get(Constants.VALUE) ==null ?"-":map.get(Constants.VALUE));
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("获取事件描述异常-getDescribe:",e);
        }
        return extraFiled;
    }
}
