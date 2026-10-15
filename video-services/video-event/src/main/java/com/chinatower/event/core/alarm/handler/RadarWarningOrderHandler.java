package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @Description 雷达生成告警事件
 * @Author luo.zhenghong
 * @Date 2022/7/18
 * @Version release_v1.5
 **/
@Component
@Scope("prototype")
@Slf4j
public class RadarWarningOrderHandler extends AbstractHandler {

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IDeviceRepository deviceRepository;


    /**
     * 执行逻辑
     *
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("雷达生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

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

                    //处理运营研判,首先判断是否开启研判，并行研判
                    if (warningOrderUtil.doOperate(alarmVo, alarms, operations)) {
                        continue;
                    }
                    if(alarmVo!=null&& StringUtils.isEmpty(alarmVo.getJudgeType())){
                        alarmVo.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                    }
                    alarms.add(alarmVo);
                }
            }

            receiveAlarmDTO.setIntercepts(intercepts);
            receiveAlarmDTO.setOperations(operations);
            receiveAlarmDTO.setList(alarms);
        }catch(Exception e){
            log.info("雷达生成告警事件异常：{}", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
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
        //更改工单编码生成规则 add by hued 20211103
        alarmVo.setWarningOrderId(PKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime()));
        alarmVo.setAppVerCode(appVer);
        alarmVo.setNotifyType(Constants.YES_FLAG);
        alarmVo.setFileImgUrlIcon(receiveAlarmVO.getAlarmPictureUrl());
        alarmVo.setFileImgUrlSrc(receiveAlarmVO.getOriginalPictureUrl());

        return alarmVo;
    }

}
