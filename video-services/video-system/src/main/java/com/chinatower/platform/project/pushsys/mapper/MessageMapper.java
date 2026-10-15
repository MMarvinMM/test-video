package com.chinatower.platform.project.pushsys.mapper;


import com.chinatower.system.api.model.CustomerVO;
import com.chinatower.system.api.model.TenantVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {



    /**
     * 更新
     * @param customerVO
     * @return
     */
    int updateCustomer(CustomerVO customerVO);


    /**
     * 更新租户信息
     * @param tenantVO
     * @return
     */
    int updateTenant(TenantVO tenantVO);

    /**
     * 更新用户信息
     * @param tenantVO
     * @return
     */
    int updateUser(TenantVO tenantVO);



    /**
     * 获取客户订购客户信息表
     * @param customerCode
     * @return
     */
    CustomerVO getCustomer(String customerCode);








}
