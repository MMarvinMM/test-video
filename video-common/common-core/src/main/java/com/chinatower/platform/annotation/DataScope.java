package com.chinatower.platform.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 *
 * @author chinatower
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 部门字段名称
     */
    String deptColumn() default "";



    /**
     * 创建用户字段名称
     */
    String userColumn() default "";



    /**
     * 租户字段名称
     */
    String tenantColumn() default "";


    /**
     * 摄像头编码字段名称
     */
    String deviceColumn() default "";

    /**
     * 铁塔视角用
     * 要过滤管理区域表的别名  u.area_code
     */
    String areaPro() default "";
    /**
     * 铁塔视角用
     * 要过滤管理区域表的别名  u.area_code
     */
    String areaCity() default "";
    /**
     * 铁塔视角用
     * 要过滤管理区域表的别名  u.area_code
     */
    String areaCode() default "";

    /**
     * 数据源
     */
    String dbType() default "";

    /**
     * 告警来源 表加字段名
     * 暂为卫星告警租户普通用户查询告警列表权限过滤使用
     * 2022.05.16 查询告警列表，卫星告警目前无法绑定租户部门和设备的关系，添加告警来源用于区分权限 guyuefei
     */
    String alarmSourceColumn() default "";

    /**
     * 告警来源值
     */
    String alarmSourceVal() default "";

    /**
     * 行业编码
     * 2022.08.03新增
     */
    String industryCode() default "";

    /**
     * 行业版本编码
     * 2022.08.03新增
     * @deprecated useless since 2023/03/31
     */
    @Deprecated
    String appVerCode() default  "";

    /**
     * 指定用户人
     * @return
     */
    String processIdColumn() default  "";

    /**
     * 受理节点
     * @return
     */
    String acceptColumn() default  "";

    /**
     * 网格
     * @return
     */
    String gridColumn() default "";

    /**
     * 网格员
     * @return
     */
    String gridKeeperColumn() default "";

    /**
     * 资源
     * @return
     */
    String resourceColumn() default "";

    /**
     * 告警类型
     * @return
     */
    String typeValueColumn() default "";
}
