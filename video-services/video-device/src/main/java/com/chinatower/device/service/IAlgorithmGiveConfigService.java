package com.chinatower.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.AlgorithmGiveConfigDO;
import com.chinatower.platform.entity.device.*;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;


public interface IAlgorithmGiveConfigService extends IService<AlgorithmGiveConfigDO> {
    Result<AlgorithmGiveConfigSaveVO> queryById(int id);
    Result<Integer> add(AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO);
    Result<String> edit(AlgorithmGiveConfigUpdateVO algorithmGiveConfigUpdateVO);
    Result<String> status(AlgorithmGiveConfigStatusVO algorithmGiveConfigStatusVO);
    Result<String> delete(int id);
    List<AlarmTypeInfoVO> queryAlarmType(AlarmTypeReq alarmTypeReq);
    Result<String> checkAlarmType(AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO);
    void algorithmGiveConfigMerge(List<DeviceChannelVO> deviceChannelList);
    void algorithmGiveMergeOrder( AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO);
    ReturnT<String> algorithmGiveConfigMergeHandle(String param);
    List<BlackWhiteVO> getBlackWhiteList(BlackWhiteReq blackWhiteReq);

    Result<String> batchAddOrUpdate(AlgorithmGiveBatchSaveVO algorithmGiveBatchSave);
    List<AlgorithmGiveConfigSaveVO> getAlgorithmGiveDetailMCP(AlgorithmGiveConfigSaveVO algorithmInfoDO);
    Result<List<AlgorithmGiveConfigSaveVO>> insertGivenAlgoConfig(List<AlgorithmGiveConfigAdd> algorithmGiveConfigAdd);
}
