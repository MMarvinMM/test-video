package com.chinatower.receive.mapper;

import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 14:14
 */

@Mapper
public interface AlarmSuppressConfigMapper {

    /**
     * 获取告警压制规则
     * @param tenantIdList 租户id集合
     * @return
     */
    List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(@Param("tenantIdList") List<String> tenantIdList);
}
