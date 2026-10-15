package com.chinatower.platform.project.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.platform.project.custom.entity.ThirdPartyAlarmTypeSyncDataVO;
import com.chinatower.system.api.request.alarmType.ThirdPartyAlarmTypeVO;
//import com.chinatower.sync.consumer.entity.AlgorithmConfigDO;
//import com.chinatower.sync.consumer.entity.DeviceSyncErrorDto;

import java.util.List;

/**
 * @ClassName IAlgorithmConfigService
 * @Description
 * @Author chenshaowu
 * @Date 2022/1/27
 **/
public interface IAlarmTypeSyncService  {


    void alarmTypeMathFactoryConsumerList(List<String> messages);

//    void thirdPartyAlarmMathFactoryConsumerList(List<String> messages);

    Result<String> thirdPartyAlarmSync(ThirdPartyAlarmTypeVO vo);

}
