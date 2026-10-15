package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.ThirdPartyAlarmVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方告警配置Mapper
 */
@Mapper
public interface ThirdPartyAlarmMapper {

    /**
     * 批量插入
     * @param list
     */
    Integer insertBatch(List<ThirdPartyAlarmVo> list);

    /**
     * 根据 租户ID、行业编码查询详情
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 查询详情
     */
    List<ThirdPartyAlarmVo> details(
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode
    );


    /**
     * 根据租户id逻辑删除
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    Integer updateByTenantId (@Param("tenantId") String tenantId,
                              @Param("industryCode") String industryCode);

    /**
     * 判断该客户是否开启第三方告警    表：sys_tenant_third_alarm_config
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    Integer isOpenThirdPartyAlarm (
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode);

    /**
     * 判断第三方告警配置表中 该租户是否有第三方告警数据  表：vm_third_party_alarm
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    Integer tenantThirdPartyAlarm (
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode);

    /**
     * 根据告警类型、租户ID和行业编码在流程模板关联告警类型表中查找对应的数据
     * @param alarmType 告警类型
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    Integer isThirdAlarmType(
            @Param("alarmType") String alarmType,
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode);
}
