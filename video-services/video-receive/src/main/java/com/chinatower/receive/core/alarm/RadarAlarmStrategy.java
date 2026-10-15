package com.chinatower.receive.core.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import com.chinatower.receive.vo.entity.RadarParam;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 雷达告警接收策略实现类
 * @Author: songxinfeng
 * @Date: 2024/8/6 9:56
 * @version release_v1.6.90
 */

@Component("radarAlarmStrategy")
@Slf4j
public class RadarAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("雷达告警上报入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

            // ###1、接收告警的租户信息
            List<OrderCustomerVO> customers = getCustomers(receiveAlarmVO);
            if(CollectionUtils.isEmpty(customers)){
                log.info("未查询到租户信息，告警编码：{}", receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                return;
            }
            // 更新租户信息以兼容历史版本
            warningOrderUtil.updateCustomerVersions(customers);
            receiveAlarmDTO.setCustomers(customers);
            log.info("匹配接收告警的租户信息：{}", JSON.toJSONString(customers));

            // ###2、处理地址和经纬度数据信息
            getAddress(receiveAlarmVO);
            //设置告警网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);

            // ###3、填充告警工单参数
            warningOrderUtil.populateAlarmWorkOrderParams(receiveAlarmVO);

            // ###4、处理告警工单、告警压制、运营研判
            List<ReceiveAlarmVO> alarms = radarWarningOrderHandler(receiveAlarmDTO);
            if (CollectionUtils.isEmpty(alarms)) {
                log.info("告警工单处理失败，告警编码：{}", receiveAlarmVO.getAlarmCode());
                return;
            }

            // ###5、告警消息推送
            try{
                commonService.batchPushMessage(alarms);
            }catch(Exception e){
                log.error("批量推送雷达告警消息时发生异常：" + e);
            }
        } catch(Exception e) {
            log.info("接收雷达告警时发生异常：" + e);
        }
    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        List<OrderCustomerVO> customers = new ArrayList<>();
        String alarmSource = receiveAlarmVO.getAlarmSource();
        String localNode = receiveAlarmVO.getLocalNode();
        String taskId = receiveAlarmVO.getTaskId();
        if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(alarmSource) && receiveAlarmVO.getExtendField() != null) {
            //查询该taskId下是否有雷达告警模板
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_TASK_ID_FLAG);
            //默认开关为0，关闭，1开启
            String flag = suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()
                    ? suppressDictData.getData().get(0).getDictValue()
                    : Constants.ZERO;
            // 获取雷达告警的透传字段  雷达报文中透传字段没有按照接口文档传json字符串
            receiveAlarmVO.setExtraField(JSON.toJSONString(receiveAlarmVO.getExtendField()));
            RadarParam extraField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtraField()), RadarParam.class);
            if (extraField.getRadarCodes() != null && extraField.getRadarCodes().length > 0 && !StringUtils.isEmpty(extraField.getRadarCodes()[0])) {
                if(Constants.ZERO.equals(flag)){
                    customers = deviceRepository.getCustomersBySensorDevice(extraField.getRadarCodes(), null, OrderConstants.ORDER_RADAR_CODE, null,alarmSource,localNode);
                }else{
                    if(!StringUtils.isEmpty(taskId)){
                        customers = deviceRepository.getCustomersBySensorDevice(extraField.getRadarCodes(), null, OrderConstants.ORDER_RADAR_CODE, taskId,alarmSource,localNode);
                    }
                }
            }
        }
        return customers;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
        String latitude = receiveAlarmVO.getLatitude();
        String longitude = receiveAlarmVO.getLongitude();
        RadarParam extraField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtraField()), RadarParam.class);
        // 获取雷达设备信息   ---取第一个雷达设备的经纬度来当做事发地点的经纬度
        DeviceSiteReceiveVO deviceSiteReceiveVO = deviceRepository.querySensorDevice(extraField.getRadarCodes()[0], OrderConstants.ORDER_RADAR_CODE);
        if (StringUtils.isEmpty(latitude) || StringUtils.isEmpty(longitude) || "null".equals(latitude) || "null".equals(longitude)) {
            if (deviceSiteReceiveVO != null) {
                receiveAlarmVO.setLatitude(deviceSiteReceiveVO.getLatitude());
                receiveAlarmVO.setLongitude(deviceSiteReceiveVO.getLongitude());
                receiveAlarmVO.setAddress(deviceSiteReceiveVO.getLocation());
            }
        }
        //设置告警点相关信息
        warningOrderUtil.setAlarmLocation(deviceSiteReceiveVO, receiveAlarmVO);
        //设置乡镇信息
        gisUtil.getAddressFromAmap(receiveAlarmVO,null);
        log.info("通过经纬度获取省市区乡镇编码:" + JSONObject.toJSONString(receiveAlarmVO));
    }

    /**
     * 处理雷达告警工单，包括告警压制及运营研判。
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @return 处理后的告警对象列表
     */
    private List<ReceiveAlarmVO> radarWarningOrderHandler(ReceiveAlarmDTO receiveAlarmDTO) {
        log.info("生成雷达告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        List<ReceiveAlarmVO> successDatas = new ArrayList<>();
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            String alarmTime = receiveAlarmDTO.getReceiveAlarmVO().getAlarmTime();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            // 运营研判告警集合
            List<ReceiveAlarmVO> operations = new ArrayList<>();
            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();

            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = warningOrderUtil.getDict(Constants.APP_VER_CODE_TOPIC);
            for (OrderCustomerVO customerObj : customers) {
                String[] appVers = customerObj.getAppVerCode().split(",");
                for (String appVer : appVers) {
                    //构建告警对象
                    ReceiveAlarmVO alarmVo = buildAlarmVo(receiveAlarmVO, customerObj, appVer);

                    //处理特殊行业版本
                    if (warningOrderUtil.judgeSpecialAppVer(dictList, alarmVo)) {
                        continue;
                    }

                    //二次研判转化
                    //warningOrderUtil.setSecondJudge(alarmVo);

                    //判断并处理告警压制
                    if (warningOrderUtil.judgeOperationIntercept(alarmVo, intercepts)) {
                        continue;
                    }

                    if(alarmVo!=null&& StringUtils.isEmpty(alarmVo.getJudgeType())){
                        alarmVo.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                    }
                    alarms.add(alarmVo);
                }
            }
            receiveAlarmVODB.setIntercepts(intercepts);
            // 批量保存告警信息
            successDatas = batchCreateWarningOrder(alarms, receiveAlarmVODB);
            receiveAlarmVODB.setList(alarms);
            receiveAlarmVODB.setOperations(operations);
            String originalJson = JSON.toJSONString(receiveAlarmVODB.getList());
            // 告警信息入库
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, Constants.TEN, originalJson, receiveAlarmDTO.getAlarmPointList());
            // 回填研判信息
            successDatas.addAll(operations);
        } catch(Exception e) {
            log.info("雷达生成告警事件异常:"+ e);
        }
        return successDatas;
    }

    /**
     * 构建告警对象
     *
     * @param receiveAlarmVO
     * @param customerObj
     * @param appVer
     * @return
     */
    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, OrderCustomerVO customerObj, String appVer) {
        ReceiveAlarmVO alarmVo = new ReceiveAlarmVO();
        customerObj.setDeviceCode(receiveAlarmVO.getDeviceCode());
        BeanUtils.copyProperties(receiveAlarmVO, alarmVo);
        BeanUtils.copyProperties(customerObj, alarmVo);
        alarmVo.setOperationNode(customerObj.getOperationNode());
        alarmVo.setParallelOperationNode(customerObj.getParallelOperationNode());
        alarmVo.setAlarmNode(getPlatformNode());
        alarmVo.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(alarmVo.getAlarmNode()));
        if(!StringUtils.isEmpty(customerObj.getOperationDept())){
            alarmVo.setOperationDept(customerObj.getOperationDept()+Constants.COMMA);
        }
        if(!StringUtils.isEmpty(customerObj.getParallelOperationDept())){
            alarmVo.setParallelOperationDept(customerObj.getParallelOperationDept()+Constants.COMMA);
        }
        //更改工单编码生成规则
        alarmVo.setWarningOrderId(IPKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime()));
        alarmVo.setAppVerCode(appVer);
        alarmVo.setNotifyType(Constants.YES_FLAG);
        alarmVo.setFileImgUrlIcon(receiveAlarmVO.getAlarmPictureUrl());
        alarmVo.setFileImgUrlSrc(receiveAlarmVO.getOriginalPictureUrl());

        return alarmVo;
    }

    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms, ReceiveAlarmDTO receiveAlarmVODB) {
        List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarms)) {
            log.info("批量保存告警信息入参：{}", JSON.toJSONString(alarms));
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms,recordList);
            /*alarms.forEach(item -> {
                if(!WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(item.getAlarmSource())){
                    setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                }
            });*/
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            receiveAlarmVODB.setAlarmPointList(alarmPointList);
            return alarms;
        }
        return new ArrayList<>();
    }

    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }





    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.RADAR_ALARM.getAlarmStrategyBean();
    }
}
