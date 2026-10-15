package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "RemoteUserSecurityService",
        value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteUserSecurityService {

    /**
     * 用户名称查询用户信息
     * @apiNote 用户名称查询用户信息
     * @apiVersion 6.83
     * @tag 六、基础功能能力
     * @responseDemo
     * userName 用户名称
     * {
     * 	"admin": false,
     * 	"adminNew": false,
     * 	"orderBy": "",
     * 	"userName": "wucj3"
     * }
     * @return SysUser
     */
    @PostMapping("/system/security/getUsername")
    SysUser getUsername();

    /**
     * 获得登录用户信息
     * @apiNote 获得登录用户信息
     * @apiVersion 6.84
     * @tag 六、基础功能能力
     * @responseDemo
     * password 密码
     * permissions 权限列表
     * token 登录token
     * user 用户对象
     *      chooseAppVerCode 当前行业版本编码
     *      chooseTradeId 当前行业编码
     *      createBy 创建人
     *      dept 组织对象
     *      deptId 组织id
     *      deptIds 角色id列表
     *      isTenantAdmin 是否是租户管理员 1是 0否
     *      nickName 用户昵称
     *      password 密码
     *      phonenumber 手机号
     *      pwdUpdateTime 密码更新时间
     *      roleIds 角色id列表
     *      roles 角色列表
     *      sex 性别
     *      status 状态 0正常 1停用
     *      telPhone 移动电话
     *      tenantId 客户id
     *      userId 用户id
     *      userName 用户名称
     *      userType 用户类型 0铁塔 2客户
     * {
     * 	"accountNonExpired": true,
     * 	"accountNonLocked": true,
     * 	"credentialsNonExpired": true,
     * 	"enabled": true,
     * 	"expireTime": 1693820561203,
     * 	"loginTime": 1693798961203,
     * 	"password": "F5DD7CF74EB6FB0A2BD143243BF3229D",
     * 	"permissions": [
     * 		"/ttevent/video/base/getEnumInfo"
     * 	],
     * 	"token": "b978d5f5-4d5f-4d3b-8787-b7777d1dd0ca",
     * 	"user": {
     * 		"admin": false,
     * 		"adminNew": false,
     * 		"appVerCode": "1.0",
     * 		"areaTree": [
     *                        {
     * 				"children": [
     *                    {
     * 						"children": [
     *                            {
     * 								"children": [
     *                                    {
     * 										"children": [
     *                                            {
     * 												"id": "110101001000",
     * 												"label": "东华门街道",
     * 												"pid": "110101"
     *                                            }
     * 										],
     * 										"id": "110101",
     * 										"label": "东城区",
     * 										"pid": "110100"
     *                                    }
     * 								],
     * 								"id": "110100",
     * 								"label": "北京市",
     * 								"pid": "110000"
     *                            }
     * 						],
     * 						"id": "110000",
     * 						"label": "北京市",
     * 						"pid": "100000"
     *                    }
     * 				],
     * 				"id": "100000",
     * 				"label": "中国",
     * 				"pid": "1"
     *            }
     * 		],
     * 		"chooseAppVerCode": "1.0",
     * 		"chooseTradeId": "000000",
     * 		"createBy": "wucj",
     * 		"delFlag": "0",
     * 		"dept": {
     * 			"ancestors": "000000",
     * 			"appVerCode": "1.0",
     * 			"children": [],
     * 			"createBy": "wucj",
     * 			"createTime": "2023-03-18 13:05:26",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-02",
     * 			"deptLevel": "1",
     * 			"deptName": "华苏测试01-1.0-1.0",
     * 			"id": 140739,
     * 			"industryCode": "000000",
     * 			"parentId": "000000",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"        * 		},
     * 		"deptId": "TY0000106-02",
     * 		"deptIds": [
     * 			"TY0000106-02"
     * 		],
     * 		"industryCode": "000000",
     * 		"isTenantAdmin": "1",
     * 		"nickName": "华苏测试022",
     * 		"orderBy": "",
     * 		"password": "F5DD7CF74EB6FB0A2BD143243BF3229D",
     * 		"phonenumber": "18184797596",
     * 		"pwdUpdateTime": "2023-08-24 17:49:18",
     * 		"roleIds": [
     * 			"4",
     * 			"5",
     * 			"6",
     * 			"7"
     * 		],
     * 		"roles": [
     *            {
     * 				"admin": false,
     * 				"dataScope": "4",
     * 				"flag": false,
     * 				"permissions": [
     * 					"/ttevent/video/base/getEnumInfo"
     * 				],
     * 				"roleId": "4",
     * 				"roleKey": "yanpan_role",
     * 				"roleName": "研判角色",
     * 				"roleSort": "0",
     * 				"status": "0"
     *            },
     *            {
     * 				"admin": false,
     * 				"dataScope": "4",
     * 				"flag": false,
     * 				"permissions": [
     * 					"/ttevent/video/base/getEnumInfo"
     * 				],
     * 				"roleId": "5",
     * 				"roleKey": "diaodu_role",
     * 				"roleName": "调度角色",
     * 				"roleSort": "0",
     * 				"status": "0"
     *            },
     *            {
     * 				"admin": false,
     * 				"dataScope": "4",
     * 				"flag": false,
     * 				"permissions": [
     * 					"/ttevent/video/base/getEnumInfo"
     * 				],
     * 				"roleId": "6",
     * 				"roleKey": "chuzhi_role",
     * 				"roleName": "处置角色",
     * 				"roleSort": "0",
     * 				"status": "0"
     *            },
     *            {
     * 				"admin": false,
     * 				"dataScope": "4",
     * 				"flag": false,
     * 				"permissions": [
     * 					"/ttevent/video/base/getEnumInfo"
     * 				],
     * 				"roleId": "7",
     * 				"roleKey": "check_role",
     * 				"roleName": "核实角色",
     * 				"roleSort": "0",
     * 				"status": "0"
     *            }
     * 		],
     * 		"sex": "0",
     * 		"status": "0",
     * 		"telPhone": "023-62228989",
     * 		"tenantId": "TY0000106",
     * 		"tenantInfo": {
     * 			"cityId": "500100",
     * 			"provinceId": "500000",
     * 			"tenantName": "华苏测试01"
     *        },
     * 		"userId": "d095a0e9e923435cab10f02a30d86128",
     * 		"userName": "wucj3",
     * 		"userType": "2"
     *    },
     * 	"username": "wucj3"
     * }
     * @return LoginUser
     */
    @PostMapping("/system/security/getLoginUser")
    LoginUser getLoginUser();

    /**
     * 拼接部门权限sql
     * @apiNote 拼接部门权限sql
     * @apiVersion 6.85
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"roles": [
     *                {
     * 			"roleId": 531603081142569272
     *        }
     * 	],
     * 	"deptId": "122035807-01"
     * }
     * @responseDemo
     * " or u.dept_id = '122035807-01' "
     * @param user
     * @return String
     */
    @PostMapping("/system/security/getUserDeptSql")
    String getUserDeptSql(@RequestBody SysUser user);

    /**
     * 过得登录用户ID
     * @apiNote 过得登录用户ID
     * @apiVersion 6.86
     * @tag 六、基础功能能力
     * @responseDemo
     * "web_manage:47610796b99b4523981a351f0e401044"
     * @return String
     */
    @PostMapping("/system/security/getLoginUserId")
    String getLoginUserId();

    /**
     * 加密密码结果
     * @apiNote 加密密码结果
     * @apiVersion 6.87
     * @tag 六、基础功能能力
     * @requestDemo
     * /system/security/encryptPassword?password=123456
     * @responseDemo
     * "103E702E0737327C"
     * @param password
     * @return String
     */
    @PostMapping("/system/security/encryptPassword")
    String encryptPassword(@RequestParam("password") String password);


    /**
     * 解密密码结果
     * @apiNote 解密密码结果
     * @apiVersion 6.88
     * @tag 六、基础功能能力
     * @requestDemo
     * /system/security/decryptPassword?password=103E702E0737327C
     * @responseDemo
     * "123456"
     * @param password
     * @return String
     */
    @PostMapping("/system/security/decryptPassword")
    String decryptPassword(@RequestParam("password") String password);


    /**
     * 校验密码结果
     * @apiNote 校验密码结果
     * @apiVersion 6.89
     * @tag 六、基础功能能力
     * @requestDemo
     * /system/security/matchesPassword?rawPassword=123456&encodedPassword=103E702E0737327C
     * @responseDemo
     * true
     * @param rawPassword 输入密码
     * @param encodedPassword 匹配的密码
     * @return String
     */
    @PostMapping("/system/security/matchesPassword")
    boolean matchesPassword(@RequestParam("rawPassword") String rawPassword, @RequestParam("encodedPassword") String encodedPassword);

    /**
     * 判断是否是管理
     * @apiNote 判断是否是管理
     * @apiVersion 6.90
     * @tag 六、基础功能能力
     * @requestDemo
     * /system/security/isAdmin?userId=47610796b99b4523981a351f0e401044
     * @responseDemo
     * false
     * @param userId 用户ID
     * @return boolean
     */
    @PostMapping("/system/security/isAdmin")
    boolean isAdmin(@RequestParam("userId") String userId);


    /**
     * 返回是否包含角色结果
     * @apiNote 返回是否包含角色结果
     * @apiVersion 6.91
     * @tag 六、基础功能能力
     * @requestDemo
     * /system/security/isHasRole?roleKey=3
     * @responseDemo
     * false
     * @param roleKey 角色KEY
     * @return boolean
     */
    @PostMapping("/system/security/isHasRole")
    boolean isHasRole(@RequestParam("roleKey") String roleKey);

    /**
     * 返回登录部门ID
     * @apiNote 返回登录部门ID
     * @apiVersion 6.92
     * @tag 六、基础功能能力
     * @responseDemo
     * "122035807-01"
     * @return String
     */
    @PostMapping("/system/security/getDeptId")
    String getDeptId();


    /**
     * 返回下级部门的ID
     * @apiNote 返回下级部门的ID
     * @apiVersion 6.93
     * @tag 六、基础功能能力
     * @responseDemo
     * 下级部门的ID列表
     * ["122035807-01"]
     * @return List<String>
     */
    @PostMapping("/system/security/getChildDeptIdList")
    List<String> getChildDeptIdList();


    /**
     * 返回租户下的部门集合
     * @apiNote 返回租户下的部门集合
     * @apiVersion 6.94
     * @tag 六、基础功能能力
     * @responseDemo
     * 下级部门的ID列表
     * [
     * 	"122035807-01"
     * ]
     * @return List<String>
     */
    @PostMapping("/system/security/getTenantChildDeptIdList")
    List<String> getTenantChildDeptIdList();

}
