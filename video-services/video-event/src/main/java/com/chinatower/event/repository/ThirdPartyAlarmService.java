package com.chinatower.event.repository;

import com.chinatower.event.entity.vo.ThirdAlarmVo;
import com.chinatower.event.entity.vo.ThirdPartyAlarmVo;

import java.util.List;

/**
 * 第三方告警配置Service
 */
public interface ThirdPartyAlarmService {


    /**
     * 批量插入
     * @param list
     */
    Integer insertBatch(List<ThirdPartyAlarmVo> list);

    /**
     * 根据 租户ID、行业编码、删除标识查询详情
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 查询详情
     */
    List<ThirdAlarmVo> details(
            String tenantId,
            String industryCode
    );

    /**
     * 根据租户id逻辑删除
     * @param tenantId 租户id
     * @return
     */
    Integer updateByTenantId (String tenantId,String industryCode);


    /**
     * 判断该租户是否开启第三方告警
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    boolean isOpenThirdPartyAlarm (String tenantId, String industryCode);

    /**
     * 判断第三方告警配置表中 该租户是否有第三方告警数据
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    boolean tenantThirdPartyAlarm (String tenantId, String industryCode);

    /**
     * 根据告警类型、租户ID和行业编码在流程模板关联告警类型表中查找对应的数据
     * @param alarmType 告警类型
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    boolean isThirdAlarmType(String alarmType, String tenantId, String industryCode);
}
