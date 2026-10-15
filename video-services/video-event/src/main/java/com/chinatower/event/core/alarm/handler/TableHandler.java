package com.chinatower.event.core.alarm.handler;

import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IWarningOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description 处理分月表
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("warningOrderTableHandler")
@Scope("prototype")
public class TableHandler extends AbstractHandler{

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        String alarmTime = receiveAlarmDTO.getReceiveAlarmVO().getAlarmTime();

        //如果分月表不存在，则根据告警时间按创建分月表
        warningOrderRepository.createNewTable(alarmTime);

        return this.excuteNextHandler(receiveAlarmDTO);
    }

}
