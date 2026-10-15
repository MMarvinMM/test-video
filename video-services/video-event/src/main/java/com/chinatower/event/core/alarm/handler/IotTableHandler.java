package com.chinatower.event.core.alarm.handler;

import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IWarningOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 对于批量上报处理分月表
 * @Author luo.zhenghong
 * @Date 2022/8/6
 * @Version release_v1.5
 **/
@Component
@Scope("prototype")
public class IotTableHandler extends AbstractHandler{

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        List<String> alarmTimeList = receiveAlarmDTO.getReceiveIotAlarmVOList().stream().map(item -> item.getAlarmTime().substring(0, 10)).distinct().collect(Collectors.toList());
        //如果分月表不存在，则根据告警时间按创建分月表
        warningOrderRepository.createNewTable(alarmTimeList);
        return this.excuteNextHandler(receiveAlarmDTO);
    }

}
