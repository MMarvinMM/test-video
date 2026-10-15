package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description 填充告警工单参数
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Slf4j
@Component("setAlarmOrderParamsHandler")
@Scope("prototype")
public class SetAlarmOrderParamsHandler  extends AbstractHandler{

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("填充告警工单参数入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            receiveAlarmVO.setWarningSource(receiveAlarmVO.getAlarmSource());
            //从redis获取告警类型名称列表
            /*List<Map> alarmTypeList = warningOrderRepository.getAlarmTypeList();
            alarmTypeList.stream().filter(record -> receiveAlarmVO.getAlarmType().equals(record.get(Constants.PARAM_TYPE_VALUE_))).forEach(record -> {
                receiveAlarmVO.setWarningTypeName((String) record.get(Constants.PARAM_TYPENAME));
            });*/
            receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
            receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());
            //receiveAlarmVO.setLinkName(WarningSourceEnum.getTypeName(receiveAlarmVO.getWarningSource()));
            if (StringUtils.isEmpty(receiveAlarmVO.getImportanceLevel()) || Constants.PARAM_NULL.equals(receiveAlarmVO.getImportanceLevel())) {
                receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
            }
            receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        }catch(Exception e){
            log.info("填充告警工单参数异常：{}", e);
        }


        return this.excuteNextHandler(receiveAlarmDTO);
    }
}
