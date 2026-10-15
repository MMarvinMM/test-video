package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.VmAlarmTransferConfigDTO;
import com.chinatower.event.entity.vo.VmAlarmTransferConfigVO;

import java.util.List;

/**
 * 告警转移配置业务接口
 * @author lyy
 * @version v1.6.00
 * @date 2023/06/30
 */
public interface VmAlarmTransferConfigService {

    /**
     * 分页查询告警转移配置
     * @param alarmTransferConfigDTO
     * @return Result<Object>
     */
    Result<Object> getAlarmTransferConfigList(VmAlarmTransferConfigDTO alarmTransferConfigDTO);

    /**
     * 根据租户ID + 告警来源 + 告警类型 + 行业应用版本 + 行业编码查询
     * @param transferConfigDTO
     * @return Result<Object>
     */
    Result<Object> queryAlarmTransferConfigAndType(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 查看告警流程配置详情、编辑回显
     * @param transferConfigVO
     * @return Result<Object>
     */
    Result<Object> queryAlarmTransferConfigDetail(VmAlarmTransferConfigVO transferConfigVO);

    /**
     * 获取默认名称“告警转移策略+数字（数字累加）”
     * @param transferConfigDTO
     * @return Result<String>
     */
    Result<String> getAutoTransferName(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 校验告警转移配置名称是否唯一
     * @param transferConfigDTO
     * @return Result<Object>
     */
    Result<Object> getTransferNameCount(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 新增告警转移配置、告警来源+告警类型
     * @param transferConfigDTO
     * @return Result<String>
     */
    Result<String> addVmAlarmTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO) throws Exception;

    /**
     * 修改告警转移配置
     * @param transferConfigDTO
     * @return Result<String>
     */
    Result<String> updateAlarmTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO) throws Exception;

    /**
     * 新增配置时有重复告警类型则删除之前的告警类型配置
     * @param transferConfigDTO
     * @return
     */
    int deleteExistedTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 批量删除告警转移配置
     * 新增配置时，删除该转移配置对应的重复告警类型，并将已经删除了告警类型的告警转移配置进行删除
     * @param alarmTransferIds
     * @param tenantId
     * @return
     */
    int deleteTransferConfigs(String tenantId, List<Long> alarmTransferIds, String operType);

    /**
     * 删除告警转移配置
     * @param transferId
     * @param tenantId
     * @return Result<String>
     */
    Result<String> deleteAlarmTransferConfig(String tenantId, Long transferId, String operType);
}
