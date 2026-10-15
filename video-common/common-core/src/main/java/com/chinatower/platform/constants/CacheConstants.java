package com.chinatower.platform.constants;

/**
 * 功能说明: 缓存常量
 *
 * @author 李峥
 * @date
 * @requestID 需求单号及需求名称
 */
public class CacheConstants {

    public static final long YEAR_100 = 100 * 365 * 24 * 60 * 60L;
    public static final long MONTH_1 = 30 * 24 * 60 * 60L;
    public static final long MONTH_3 = MONTH_1 * 3;

    //铁塔部门权限集合前缀,铁塔部门数据权限过滤使用
    public static final String DEPT_SCOPE_LIST_PREFIX = "dept:scope:list:";

    //租户部门权限集合前缀,租户部门数据权限过滤使用
    public static final String DEPT_SCOPE_TENANT_LIST_PREFIX = "dept:scope:tenant:list:";

    //租住在行业下的所有组织
    public static final String DEPT_SCOPE_TENANT_INDUSTRYCODE_LIST_PREFIX = "dept:scope:tenant:industrycode:list:";


    //铁塔角色自定义权限集合前缀
    public static final String ROLE_SCOPE_LIST_PREFIX = "role:scope:list:";

    //租户角色自定义权限集合前缀
    public static final String ROLE_SCOPE_TENANT_LIST_PREFIX = "role:scope:tenant:list:";

    //铁塔部门对应租户集合前缀,铁塔视角使用
    public static final String TOWER_DEPT_TENANT_LIST_PREFIX = "tower:dept:tenant:list:";

    //新增客户id，需要前缀参数
    public static final String CUSTOMER_INCR_ID = "customer:incr:id:%s";


}
