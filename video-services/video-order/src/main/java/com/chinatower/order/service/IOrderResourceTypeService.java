package com.chinatower.order.service;

import com.chinatower.order.dto.ResourceTypeInitDTO;

import java.util.List;

/**
 * @ClassName IOrderResourceType
 * @Deacription 资源类型数据服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/

public interface IOrderResourceTypeService {
    
    /**
     * 资源类型数据初始化
     * @param: resourceTypeInt
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void initResourceType(List<ResourceTypeInitDTO> resourceTypeInitList);
}
