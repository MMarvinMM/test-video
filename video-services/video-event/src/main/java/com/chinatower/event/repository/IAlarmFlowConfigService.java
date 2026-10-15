package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.AlarmFlowConfig;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.dto.FlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;

import java.util.List;

/**
 * 告警流程配置业务接口
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:48
 */
public interface IAlarmFlowConfigService {
    /**
     * 新增告警流程配置、流程属性、用户
     * @param list
     * @return
     */
    Result<String> addAlarmFlowConfig(List<AlarmFlowConfigModel> list) throws Exception;

    /**
     * 根据条件查询告警流程配置
     * @param model
     * @return
     */
    Result<List<AlarmFlowConfig>> queryAlarmFlowConfig(AlarmFlowConfigModel model);

    /**
     * 修改告警流程配置、流程属性、用户
     * @param list
     * @return
     */
    //Result modifyAlarmFlowConfig(List<AlarmFlowConfigModel> list);

    /**
     * 删除
     * @param alarmFlowId
     * @return
     */
    Result<String> deleteAlarmFlowConfig(Long alarmFlowId);

    /**
     * 根据条件查询流程开始环节
     * @param model
     * @return
     */
    Result<FlowModel> queryFlowStartLink(AlarmFlowConfigModel model);

    /**
     * 根据条件查询流程开始环节
     * @param model
     * @return
     */
    Result<FlowModel> queryFlowStartLinkNew(AlarmFlowConfigModel model);

    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    Result<List<FlowModel>> queryFlowLink(AlarmFlowConfigModel model);


    /**
     * 获取告警流程配置
     * @param tenantId
     * @param alarmCode
     * @param warningSourceList
     * @return
     */
    Result<List<AlarmFlowConfig>> getAlarmFlowConfig(String tenantId, String alarmCode, List<String> warningSourceList);

    /**
     * 获取告警流程配置（新）
     * @param tenantId
     * @param alarmCode
     * @param warningSourceList
     * @return
     */
    Result<List<AlarmFlowConfig>> getAlarmFlowConfigNew(String tenantId, String alarmCode, List<String> warningSourceList);

    /**
     * 批量更新流程配置类型
     * @param flowConfigModel
     * @return
     * @throws Exception
     */
    Result<String> batchConfigFlow(FlowConfigModel flowConfigModel) throws Exception;
}
