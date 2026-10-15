package com.chinatower.sync.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.sync.consumer.entity.AlgorithmConfigDO;
import com.chinatower.sync.consumer.entity.DeviceSyncErrorDto;

import java.util.List;

/**
 * @ClassName IAlgorithmConfigService
 * @Description
 * @Author chenshaowu
 * @Date 2022/1/27
 **/
public interface IAlgorithmConfigInfoService extends IService<AlgorithmConfigDO> {
    void synOrderAlgorithm(AlgoCommonReq<AlgoInfoReq> req);

    void mathFactoryConsumerList(List<String> messages);

    void queryYgDeviceAndChannel(String[] channelCode, DeviceSyncErrorDto deviceSyncError);
}
