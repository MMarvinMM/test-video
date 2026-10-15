package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.vo.OrderCustomerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 物联设备批量处理接收告警的租户信息
 * @Author luo.zhenghong
 * @Date 2022/8/4
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotCustomersHandler extends AbstractHandler{

    @Autowired
    private IDeviceRepository deviceRepository;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备批量处理接收告警的租户信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            long beginTime = System.currentTimeMillis();
            List<ReceiveIotAlarmVO> receiveIotAlarmVOList = receiveAlarmDTO.getReceiveIotAlarmVOList();
            //物联设备上报告警信息为空不进行处理
            if (CollectionUtils.isEmpty(receiveIotAlarmVOList)) {
                log.info("物联设备批量上报告警信息为空,不处理!");
                return false;
            }
            //获取物联设备编码集合
            List<String> deviceCodeList = receiveIotAlarmVOList.stream().map(ReceiveIotAlarmVO::getDeviceCode).distinct().collect(Collectors.toList());
            List<OrderCustomerVO> customers = deviceRepository.getCustomersBySensorDeviceList(deviceCodeList);

            if(CollectionUtils.isEmpty(customers)){
                log.info("未查询到租户信息,告警编码：{}",receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                return false;
            }else {
                receiveAlarmDTO.setCustomers(customers);
            }
            long endTime = System.currentTimeMillis();
            log.info("IotCustomersHandler excute cost: {}", (endTime - beginTime));
        }catch(Exception e){
            log.error("物联设备批量处理接收告警的租户信息异常：{}", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
    }

}
