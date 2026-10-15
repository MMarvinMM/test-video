package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;

import java.util.List;

public interface IAlarmSuppressConfigRepository {

    /**
     * 保存/修改告警压制规则
     * @param dto
     * @return
     */
    Result saveOrUpdateConfig(AlarmSuppressConfigDTO dto);

    /**
     * 删除告警压制规则
     * @param deviceCode
     * @return
     */
    Result delete(String deviceCode);

    /**
     * 查询租户下设备生效告警压制规则
     * @param deviceCode 设备编码
     * @return
     */
    AlarmSuppressConfigDTO querySuppressConfig(String deviceCode);

    /**
     * 根据租户id获取告警压制规则
     * @param tenantIdList
     * @return
     */
    List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(List<String> tenantIdList);
}
