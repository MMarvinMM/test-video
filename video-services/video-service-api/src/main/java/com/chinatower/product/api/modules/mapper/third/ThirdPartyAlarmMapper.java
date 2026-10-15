package com.chinatower.product.api.modules.mapper.third;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方告警配置Mapper
 */
@Mapper
public interface ThirdPartyAlarmMapper {

    /**
     * 查询客户第三房告警得SK   表：sys_tenant_third_alarm_config
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    String getSkForThirdOperate (
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode);

    int countOrder(@Param("tenantId")String tenantId,@Param("industryCode") String industryCode,@Param("acceptNode") String acceptNode);

}
