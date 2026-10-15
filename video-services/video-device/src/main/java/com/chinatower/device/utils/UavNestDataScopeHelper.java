package com.chinatower.device.utils;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 无人机卡片列表——单机巢分支数据权限片段生成器。
 *
 * <p>{@code queryUavInfoList} 的 SQL 是一条 {@code UNION ALL}：上半部分（无人机）用别名
 * {@code m}(无人机)/{@code ord}(无人机订单)，下半部分（单机巢）用别名 {@code s}(机巢信息)/
 * {@code z}(机巢订单)。{@code @DataScope} 切面每次只会把片段写进入参的 {@code dataScope}
 * 一个字段，且注解里的列别名固定，无法同时生成两种别名的片段；又因为切面是 Spring AOP
 * 前置代理，本类自调用不会触发。</p>
 *
 * <p>因此单独用这个 Bean 生成"机巢口径"的数据权限片段：调用方传入一个副本 DTO，切面按下方
 * 注解把片段写入副本的 {@code dataScope}，调用方再拷贝到主 DTO 的 {@code nestDataScope}，
 * 供 XML 下半部分的 {@code ${param.nestDataScope}} 使用。</p>
 */
@Slf4j
@Component
public class UavNestDataScopeHelper {

    /**
     * 触发数据权限切面，生成"单机巢口径"的数据权限 SQL 片段。
     *
     * <p>方法体为空——{@code DataScopeAspect} 前置切面会在进入方法前，按下方注解把片段写入
     * {@code param.dataScope}。别名对齐 {@code queryUavInfoList} SQL 下半部分：
     * {@code s = vm_uav_nest_device_info}、{@code z = vm_order_uav_nest_devices}；
     * {@code alarmSourceVal = "15"} 对应组织设备关系表 {@code vm_organ_device_rel_t}
     * 中的机巢设备类型，用于把 {@code s.device_code} 收敛到当前用户有权限的机巢。</p>
     *
     * @param param 承载数据权限片段的副本 DTO，调用前需 setDataScope("")
     */
    @DataScope(deviceColumn = "s.device_code", tenantColumn = "z.tenant_id",
            industryCode = "z.industry_code", acceptColumn = "z.accept_node",
            alarmSourceVal = "15")
    public void fillNestDataScope(DeviceReqDTO param) {
        // no-op：dataScope 由 DataScopeAspect 前置切面填充
    }
}
