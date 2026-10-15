package com.chinatower.order.service;

import com.chinatower.system.api.request.tenant.AddTenantInfoRequest;
import com.chinatower.system.api.request.user.AddUserRequest;

import java.util.Map;

/**
 * @ClassName ISystemService
 * @Deacription 公共组件服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
public interface IOrderSystemService {

    /**
     * 新增租户管理
     * @param: addTenantInfoRequest
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void addTenantInfo(AddTenantInfoRequest addTenantInfoRequest,String userName, Boolean handleOrderUserInfoOnly);

    /**
     * 新增用户信息
     * * @param: addUserRequest
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    String addUser(AddUserRequest addUserRequest, boolean isAcceptNode);

    /**
     * 查询userId
     *
     * @param userName 用户名称
     * @return 结果
     */
    String findUserIdByUserName(String userName);

    /**
     * 根据用户id更新组织id
     * @param userId 用户id
     * @param deptId 组织id
     * @return 影响条数
     */
    int updateDeptByUserId(String userId, String deptId, boolean isAcceptNode, String currentAcceptNode);

    /**
     * 新增用户角色信息
     * @param userId
     * @param industryCode
     * @param appVerCode
     */
    void insertUserRole(String userId, String industryCode, String appVerCode);

    void insertUserRoleBatch(String userId, Map<String,String> industryMap);

    /**
     * 查询客户下管理员账号数量
     * @param tenantId 客户id
     * @return 管理员账号数量
     */
    int countAdminUserNameByTenantId(String tenantId);
}
