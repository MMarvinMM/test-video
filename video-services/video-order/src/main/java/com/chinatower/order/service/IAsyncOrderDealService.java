package com.chinatower.order.service;

import com.chinatower.platform.entity.admin.SysTenantInfo;

/**
 * 异步处理服务
 * @return
 * @author chenmf
 * @date 2022/6/13
 * @version
 */
public interface IAsyncOrderDealService {
    
    /**
     * 插入租户时,异步更新该租户部门id对应的租户以及父部门id集合
     * @param: sysTenantInfo
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void asyncInsertSysTen2Redis(SysTenantInfo sysTenantInfo);
}
