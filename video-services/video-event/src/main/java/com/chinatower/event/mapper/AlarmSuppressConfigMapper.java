package com.chinatower.event.mapper;

import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 告警压制配置 Mapper 接口
 * </p>
 *
 * @author luo.zhenghong
 * @since 2022-06-09
 */
@Mapper
public interface AlarmSuppressConfigMapper {

    /**
     * 保存告警压制配置
     * @return
     */
    Integer save(AlarmSuppressConfigDTO dto);

    /**
     * 获取告警压制规则
     * @param dto
     * @return
     */
    AlarmSuppressConfigDTO querySuppressConfig(AlarmSuppressConfigDTO dto);

    /**
     * 修改告警压制规则
     */
    Integer update(AlarmSuppressConfigDTO dto);

    /**
     * 逻辑删除告警压制规则
     * @param dto
     * @return
     */
    Integer delete(AlarmSuppressConfigDTO dto);

    /**
     * 获取告警压制规则
     * @param tenantIdList 租户id集合
     * @return
     */
    List<AlarmSuppressConfigDTO> querySuppressConfigByTenantIds(@Param("tenantIdList") List<String> tenantIdList);
}
