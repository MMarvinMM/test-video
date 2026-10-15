package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.ThirdPartyAlarmVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/27 18:45
 */

@Mapper
public interface ThirdPartyAlarmMapper {

    /**
     * 根据 租户ID、行业编码查询详情
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 查询详情
     */
    List<ThirdPartyAlarmVo> details(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 判断该客户是否开启第三方告警    表：sys_tenant_third_alarm_config
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    Integer isOpenThirdPartyAlarm (@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);
}
