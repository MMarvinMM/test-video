package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.system.api.feign.factory.SystemFeignClientFallbackFactory;
import com.chinatower.system.api.model.*;
import com.chinatower.system.api.request.alarmType.ThirdPartyAlarmTypeVO;
import com.chinatower.system.api.request.dept.QuTenantDeptSubsetRequest;
import com.chinatower.system.api.request.dept.QuTenantDeptTopRequest;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.system.api.request.notice.SysNoticeReq;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import com.chinatower.system.api.request.tenant.AddTenantInfoRequest;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import com.chinatower.system.api.request.user.VideoUpdateUserRequest;
import com.chinatower.system.api.request.words.wordsRequest;
import com.chinatower.system.api.resp.*;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SystemFeignClientFallbackFactory.class)
public interface SystemFeignClient {

    String TENANT_PREFIX = "/system/tenant";
    String USER_PREFIX = "/system/user";

    String TENANT_DEPT_PREFIX = "/system/tenantDept";


    /**
     * 查询租户列表
     * @apiNote 查询租户列表
     * @apiVersion 6.2
     * @tag 六、基础功能能力
     */
    @PostMapping(path = TENANT_PREFIX + "/listTenantInfo")
    Result<Map<String, Object>> getTenantInfoList(@RequestBody QuTenantInfoRequest quTenantInfoRequest);


    /**
     * 新增租户
     */
    @PostMapping(path = TENANT_PREFIX + "/addTenantInfo")
    Result addTenantInfo(@Validated @RequestBody AddTenantInfoRequest addTenantInfoRequest);

    /**
     * 新增用户接口
     * @apiNote 新增用户接口
     * @apiVersion 6.3
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"deptId": "122035797-01",
     * 	"userName": "hanxiaodao",
     * 	"nickName": "韩小刀",
     * 	"userType": "2",
     * 	"tenantId": "122035797",
     * 	"phonenumber": "12322221111",
     * 	"sex": "0",
     * 	"status": "0",
     * 	"isTenantAdmin": "0",
     * 	"remark": "111",
     * 	"roleIds": [
     * 		"531603081142569686"
     * 	]
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1736912332607
     * }
     * @param addUserRequest
     * @return Result<Integer>
     */
    @PostMapping(path = USER_PREFIX + "/addUser")
    Result<Integer> addUser(@Validated @RequestBody AddUserRequest addUserRequest);

    /**
     * 修改用户,视联使用
     * @apiNote 修改用户,视联使用
     * @apiVersion 6.4
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"deptId": "122035807-01",
     * 	"nickName": "王小刀",
     * 	"sex": "0",
     * 	"tenantId": "122035807",
     * 	"userId": "bb7ada6b02954f4c84616a15faf94b2a"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1736912332607
     * }
     * @param request
     * @return Result<Integer>
     */
    @PutMapping(path = USER_PREFIX + "/updateVideoUser")
    Result<Integer> updateVideoUser(@Validated @RequestBody VideoUpdateUserRequest request);


    /**
     * 获取权限范围内的部门用户树(融合通信用)
     */
    @PostMapping(USER_PREFIX + "/getDeptUserTreeList")
    public Result<List<DeptUserGroupVO>> getDeptUserTreeList(@RequestBody SysUser sysUserNew);

    /**
     * 根据字典类型查询字典数据信息
     * @apiNote 根据字典类型查询字典数据信息
     * @apiVersion 6.5
     * @tag 六、基础功能能力
     * @PathVariable dictType
     * @responseDemo
     * createBy 创建人
     * createTime 创建时间
     * dictCode 字典主键
     * dictLabel 字典名称
     * dictType 字典类型
     * dictValue 字典值
     * remark 备注
     * dictSort 字典排序
     *{
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"createBy": "admin",
     * 			"createTime": "2018-03-16 11:33:00",
     * 			"default": false,
     * 			"dictCode": 1104,
     * 			"dictLabel": "显示",
     * 			"dictSort": 1,
     * 			"dictType": "sys_show_hide",
     * 			"dictValue": "0",
     * 			"isDefault": "0",
     * 			"listClass": "primary",
     * 			"orderBy": "",
     * 			"remark": "显示菜单",
     * 			"status": "0"
     *        },
     *        {
     * 			"createBy": "admin",
     * 			"createTime": "2018-03-16 11:33:00",
     * 			"default": false,
     * 			"dictCode": 1105,
     * 			"dictLabel": "隐藏",
     * 			"dictSort": 2,
     * 			"dictType": "sys_show_hide",
     * 			"dictValue": "1",
     * 			"isDefault": "0",
     * 			"listClass": "danger",
     * 			"orderBy": "",
     * 			"remark": "隐藏菜单",
     * 			"status": "0"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693811414112
     * }'
     * @return Result<List<SysDictData>>
     */
    @GetMapping(path = "/base/system/dict/data/dictType/{dictType}")
    @Cacheable(cacheNames = "cacheDictDataTypeNew",cacheManager = "customCacheManager")
    Result<List<SysDictData>> getDictDataByType(@PathVariable(value = "dictType") String dictType);

    @PostMapping(path = "/base/system/dict/data/dictTypeSdk")
    @Cacheable(cacheNames = "cacheDictDataType",cacheManager = "customCacheManager")
    Result<List<SysDictData>> getDictDataByTypeSdk(@RequestBody String dictType);

    /**
     * 新增租户组织树根节点
     * @apiNote 新增租户组织树根节点
     * @apiVersion 6.6
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"tenantId": "122035807",
     * 	"tenantName": "王小刀",
     * 	"tenantTowerDeptId": "100000"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1736933499710
     * }
     * @param createTenantRootDeptRequest 创建租户组织树根节点入参
     * @return Result
     */
    @PostMapping(path = "/system/dept/addTenantRootDept")
    Result addTenantRootDept(@Validated @RequestBody CreateTenantRootDeptRequest createTenantRootDeptRequest);

    /**
     * 查询角色列表
     * @apiNote 查询角色列表
     * @apiVersion 6.7
     * @tag 六、基础功能能力
     * @param queryRoleRequest 创建租户组织树根节点入参
     * @requestDemo
     * {
     *     "roleName":"角色名称"
     * }
     * @responseDemo
     * roleId 角色id
     * roleKey 角色权限字符串
     * roleName 角色名称
     * roleType 角色类型(0:铁塔角色1:公共角色2行业应用角色)
     * roleSort 显示顺序
     * tenantId 租户id
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"admin": false,
     * 			"createBy": "shuilics",
     * 			"createTime": "2023-08-04 13:40:50",
     * 			"delFlag": "0",
     * 			"flag": false,
     * 			"roleId": "531603081142569327",
     * 			"roleKey": "121063278_1",
     * 			"roleName": "角色名称",
     * 			"roleSort": "1",
     * 			"roleType": "2",
     * 			"status": "0",
     * 			"tenantId": "121063278",
     * 			"updateBy": "shuilics",
     * 			"updateTime": "2023-08-04 13:42:00"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693811708188
     * }
     * @return Result
     */
    @PostMapping(path = "/system/role/selectRoleList")
    Result<List<SysRoleResponse>> getRoleList(@RequestBody QueryRoleRequest queryRoleRequest);

    /**
     * 获取租户部门列表(顶级结构)
     * @apiNote 获取租户部门列表(顶级结构)
     * @apiVersion 6.8
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "tenantId":"TY0000106"
     * }
     * @responseDemo
     * deptId 组织id
     * deptLevel 组织层级
     * deptName 组织名称
     * parentId 上级组织id
     * tenantId 租户id
     * tenantName 租户名称
     * status 部门状态（0正常 1停用）
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"deptList": [
     *                        {
     * 				"ancestors": "000000",
     * 				"children": [],
     * 				"city": "TY0000106-01",
     * 				"createBy": "wucj",
     * 				"createTime": "2023-03-18 13:00:58",
     * 				"delFlag": "0",
     * 				"deptId": "TY0000106-01",
     * 				"deptLevel": "1",
     * 				"deptName": "华苏测试01-1.0-1.0",
     * 				"hasChildren": true,
     * 				"id": 140738,
     * 				"parentId": "000000",
     * 				"province": "000000",
     * 				"status": "0",
     * 				"tenantId": "TY0000106",
     * 				"tenantName": "华苏测试01",
     * 				"tenantTowerDeptId": "100000"
     *            },
     *            {
     * 				"ancestors": "000000",
     * 				"children": [],
     * 				"city": "TY0000106-02",
     * 				"createBy": "wucj",
     * 				"createTime": "2023-03-18 13:05:26",
     * 				"delFlag": "0",
     * 				"deptId": "TY0000106-02",
     * 				"deptLevel": "1",
     * 				"deptName": "华苏测试01-1.0-1.0",
     * 				"hasChildren": true,
     * 				"id": 140739,
     * 				"parentId": "000000",
     * 				"province": "000000",
     * 				"status": "0",
     * 				"tenantId": "TY0000106",
     * 				"tenantName": "华苏测试01",
     * 				"tenantTowerDeptId": "100000"
     *            }
     * 		]
     * 	},
     * 	"msg": "请求成功",
     * 	"time": 1693811871108
     * }
     * @param quTenantDeptTopRequest
     * @return  Result<Map<String, Object>>
     */
    @PostMapping(path = "/system/tenantDept/selectTenantTop")
    Result<Map<String, Object>> selectTenantTop(@RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest);


    /**
     * 查询租户部门子列表(树形)
     * @apiNote 查询租户部门子列表(树形)
     * @apiVersion 6.9
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "tenantId":"TY0000106"
     * }
     * @responseDemo
     * deptId 组织id
     * deptLevel 组织层级
     * deptName 组织名称
     * parentId 上级组织id
     * tenantId 租户id
     * tenantName 租户名称
     * status 部门状态（0正常 1停用）
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"ancestors": "000000",
     * 			"children": [],
     * 			"city": "TY0000106-02",
     * 			"createBy": "wucj",
     * 			"createTime": "2023-03-18 13:05:26",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-02",
     * 			"deptLevel": "1",
     * 			"deptName": "华苏测试01-1.0-1.0",
     * 			"id": 140739,
     * 			"parentId": "000000",
     * 			"province": "000000",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"
     *        },
     *        {
     * 			"ancestors": "000000,TY0000106-02",
     * 			"children": [],
     * 			"city": "TY0000106-0201",
     * 			"createBy": "wucj3",
     * 			"createTime": "2023-03-20 15:01:02",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-0201",
     * 			"deptLevel": "2",
     * 			"deptName": "华苏测试组织01",
     * 			"id": 140751,
     * 			"parentId": "TY0000106-02",
     * 			"province": "TY0000106-02",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"
     *        },
     *        {
     * 			"ancestors": "000000,TY0000106-02",
     * 			"children": [],
     * 			"city": "TY0000106-0202",
     * 			"createBy": "wucj3",
     * 			"createTime": "2023-03-20 15:01:29",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-0202",
     * 			"deptLevel": "2",
     * 			"deptName": "华苏测试组织02",
     * 			"id": 140752,
     * 			"parentId": "TY0000106-02",
     * 			"province": "TY0000106-02",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"
     *        },
     *        {
     * 			"ancestors": "000000,TY0000106-02",
     * 			"children": [],
     * 			"city": "TY0000106-0203",
     * 			"createBy": "wucj3",
     * 			"createTime": "2023-03-27 17:12:56",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-0203",
     * 			"deptLevel": "2",
     * 			"deptName": "华苏测试组织03",
     * 			"id": 140770,
     * 			"parentId": "TY0000106-02",
     * 			"province": "TY0000106-02",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"
     *        },
     *        {
     * 			"ancestors": "000000,TY0000106-02",
     * 			"children": [],
     * 			"city": "TY0000106-0204",
     * 			"createBy": "wucj3",
     * 			"createTime": "2023-04-23 11:18:14",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-0204",
     * 			"deptLevel": "2",
     * 			"deptName": "95995",
     * 			"id": 140861,
     * 			"parentId": "TY0000106-02",
     * 			"province": "TY0000106-02",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"tenantTowerDeptId": "100000"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693811943524
     * }
     * @param quTenantDeptSubsetRequest
     * @return Result<List<SysDeptInfoResponse>>
     */
    @PostMapping(path = "/system/tenantDept/listSubsetDeptInfo")
    Result<List<SysDeptInfoResponse>> listTenantSubsetDept(@RequestBody QuTenantDeptSubsetRequest quTenantDeptSubsetRequest);

    /**
     * 新增角色
     * @apiNote 新增角色
     * @apiVersion 6.10
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"roleName": "小刀测试333",
     * 	"roleType": "2",
     * 	"roleSort": 0,
     * 	"status": "0",
     * 	"menuIds": [
     * 		"331768"
     * 	],
     * 	"tenantId": "122035807",
     * 	"sysRoleScreenVOList": [
     *                {
     * 			"appVerCode": "2.0",
     * 			"displayType": 0,
     * 			"industryCode": "000000",
     * 			"industryViewId": 146,
     * 			"industryViewName": "全景展示",
     * 			"industryViewType": 0,
     * 			"sort": 1,
     * 			"viewType": 1
     *        }
     * 	]
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "新增成功",
     * 	"time": 1737079212709
     * }
     * @param SysRole
     * @return Result
     */
    @PostMapping(path = "/system/role/add")
    Result addRole(@RequestBody SysRole SysRole);


    /**
     * 查询行政区域列表
     * @apiNote 查询行政区域列表
     * @apiVersion 6.11
     * @tag 六、基础功能能力
     * @responseDemo
     * areaCode 区域编码
     * areaLevel 区域级别：1=国家级,2=省份级,3=地市级,4=区县级
     * areaName 区域名称
     * areaParentCode 上级区域编码
     * areaParentName 上级区域名称
     * bussnessUnit 归属主体
     * status 区域状态：0=正常,1=停用
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"areaCode": "100000",
     * 			"areaLevel": "1",
     * 			"areaName": "中国",
     * 			"areaParentCode": "1",
     * 			"areaParentName": "顶级节点",
     * 			"bussnessUnit": "02",
     * 			"children": [],
     * 			"createBy": "admin",
     * 			"id": 0,
     * 			"orderBy": "",
     * 			"status": "0",
     * 			"updateBy": "admin"
     *        }
     *        ],
     * 	"msg": "请求成功",
     * 	"time": 1693812066374
     * }
     * @return Result<List<SysAreaResponse>>
     */
    @GetMapping(path = "/system/area/queryAllAreaCode")
    Result<List<SysAreaResponse>> queryAllAreaCode();


    /**
     * 删除组织信息
     * @apiNote 删除组织信息
     * @apiVersion 6.12
     * @tag 六、基础功能能力
     * @PathVariable deptId
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "新增成功",
     * 	"time": 1737079212709
     * }
     * @return Result
     */
    @DeleteMapping(path = "/system/dept/del/{deptId}")
    Result remove(@PathVariable(value = "deptId") String deptId);


    /**
     * 查询租户编码是否存在
     * @apiNote 查询租户编码是否存在
     * @apiVersion 6.13
     * @tag 六、基础功能能力
     * @PathVariable tenantId
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"data": 1,
     * 	"msg": "请求成功",
     * 	"time": 1693812869262
     * }
     * @return Result<Integer>
     */
    @GetMapping(TENANT_PREFIX + "/checkTenantId/{tenantId}")
    Result<Integer> checkTenantId(@PathVariable(value = "tenantId") String tenantId);

    /**
     * 根据租户id查询用户信息
     * @apiNote 根据租户id查询用户信息
     * @apiVersion 6.14
     * @tag 六、基础功能能力
     * @PathVariable tenantId
     * @responseDemo
     * createBy 创建人
     * deptId 组织ID
     * isTenantAdmin 是否租户管理员(0否,1是)
     * nickName 用户昵称
     * phonenumber 手机号码
     * sex 用户性别
     * status 帐号状态（0正常 1停用）
     * tenantId 客户编码
     * userId 用户ID
     * userName 用户账号
     * userType 用户类型
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"admin": false,
     * 			"createBy": "wucj3",
     * 			"delFlag": "0",
     * 			"deptId": "TY0000106-0201",
     * 			"isTenantAdmin": "0",
     * 			"nickName": "通用组织一测试二",
     * 			"phonenumber": "15144446666",
     * 			"sex": "0",
     * 			"status": "0",
     * 			"tenantId": "TY0000106",
     * 			"userId": "08ba1b7fc775403391f61f2588c3185e",
     * 			"userName": "tytest04",
     * 			"userType": "2"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693812963216
     * }
     * @author chenmf
     * @date 2022/2/23
     */
    @GetMapping(USER_PREFIX + "/selectUserByTenantId/{tenantId}")
    Result<List<SysUserResponse>> selectUserByTenantId(@PathVariable(value = "tenantId") String tenantId);

    /**
     * 获得租户下用户信息
     * @apiNote 获得租户下用户信息（区域，角色，性别，昵称。账号，userId）
     * @apiVersion 6.15
     * @tag 六、基础功能能力
     * @responseDemo
     * userId：用户id
     * userName：用户名称
     * nickName：用户昵称
     * sex：用户性别
     * roles：角色
     * countyName：区、县
     * cityName：城市
     * provinceName：省
     * townName：街道名称
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "areas":[
     *                 {
     *                     "cityName":"北京市",
     *                     "countyName":"海淀区",
     *                     "provinceName":"北京市"
     *                 },
     *                 {
     *                     "cityName":"北京市",
     *                     "countyName":"海淀区",
     *                     "provinceName":"北京市",
     *                     "townName":"上地街道"
     *                 }
     *             ],
     *             "nickName":"新用户",
     *             "roles":"领导角色",
     *             "sex":"未知",
     *             "userId":"0275f04e14174c779435ad19c53248f1",
     *             "userName":"newuser"
     *         },
     *         {
     *             "areas":[
     *
     *             ],
     *             "nickName":"测试",
     *             "roles":"客户管理员,研判角色,调度角色,处置角色,核实角色,领导角色",
     *             "sex":"男",
     *             "userId":"0444516ce981415da5939b3f7f6bfd2e",
     *             "userName":"ceshiar02"
     *         }
     *     ],
     *     "msg":"请求成功",
     *     "time":1692264472447
     * }
     * @param pageNum 页码
     * @param pageSize  数量
     * @return
     */
    @GetMapping(USER_PREFIX + "/selectUserListByTenantId")
    Result<List<SysUserTenantInfoResponse>> selectUserListByTenantId(@RequestParam Integer pageNum, @RequestParam Integer pageSize);


    /**
     * 获得租户下用户信息总量
     * @apiNote 获得租户下用户信息总量
     * @apiVersion 6.16
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：租户下用户信息总量
     * {
     *     "code":200,
     *     "data":99,
     *     "msg":"",
     *     "time":1654754979434
     * }
     * @return
     */
    @GetMapping(USER_PREFIX + "/selectUserListByTenantCount")
    Result<Long> selectUserListByTenantCount();


    /**
     * 根据字典类型查询字典数据信息
     * @apiNote 根据字典类型查询字典数据信息
     * @apiVersion 6.17
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数data下字段解析：
     * 		dictCode：字典编码
     * 		dictSort：字典排序
     * 		dictLabel：字典标签
     * 		dictValue：字典键值
     * 		dictType：字典类型
     * 		cssClass：样式属性
     * 		listClass：表格字典样式
     * 		isDefault：是否默认（Y是 N否）
     * 		status：状态（0正常 1停用）
     * 		remark：备注
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "createBy":"admin",
     *             "createTime":"2018-03-16 11:33:00",
     *             "dictCode":1112,
     *             "dictLabel":"",
     *             "dictSort":1,
     *             "dictType":"sys_yes_no",
     *             "dictValue":"1",
     *             "isDefault":"0",
     *             "listClass":"primary",
     *             "remark":"",
     *             "status":"0"
     *         },
     *         {
     *             "createBy":"admin",
     *             "createTime":"2018-03-16 11:33:00",
     *             "dictCode":1113,
     *             "dictLabel":"",
     *             "dictSort":2,
     *             "dictType":"sys_yes_no",
     *             "dictValue":"0",
     *             "isDefault":"0",
     *             "listClass":"danger",
     *             "remark":"",
     *             "status":"0"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1654511006233
     * }
     * @param dictType 字段类型
     * @return
     */
    @GetMapping(value = "/base/system/dict/data/dictDataType/{dictType}")
    @Cacheable(cacheNames = "cacheDictDataType",cacheManager = "customCacheManager")
    Result<List<SysDictDataResponse>> dictDataType(@PathVariable(value = "dictType") String dictType);

    /**
     * 根据字典类型查询字典数据信息
     * @apiNote 根据字典类型查询字典数据信息
     * @apiVersion 6.18
     * @tag 六、基础功能能力
     * @PathVariable dictTypes
     * @responseDemo
     * dictCode：字典编码
     * dictSort：字典排序
     * dictLabel：字典标签
     * dictValue：字典键值
     * dictType：字典类型
     * listClass：表格字典样式
     * isDefault：是否默认（Y是 N否）
     * status：状态（0正常 1停用）
     * remark：备注
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"sys_show_hide": [
     *                        {
     * 				"createBy": "admin",
     * 				"createTime": "2018-03-16 11:33:00",
     * 				"default": false,
     * 				"dictCode": 1104,
     * 				"dictLabel": "显示",
     * 				"dictSort": 1,
     * 				"dictType": "sys_show_hide",
     * 				"dictValue": "0",
     * 				"isDefault": "0",
     * 				"listClass": "primary",
     * 				"orderBy": "",
     * 				"remark": "显示菜单",
     * 				"status": "0"
     *            },
     *            {
     * 				"createBy": "admin",
     * 				"createTime": "2018-03-16 11:33:00",
     * 				"default": false,
     * 				"dictCode": 1105,
     * 				"dictLabel": "隐藏",
     * 				"dictSort": 2,
     * 				"dictType": "sys_show_hide",
     * 				"dictValue": "1",
     * 				"isDefault": "0",
     * 				"listClass": "danger",
     * 				"orderBy": "",
     * 				"remark": "隐藏菜单",
     * 				"status": "0"
     *            }
     * 		],
     * 		"sys_user_sex": [
     *            {
     * 				"createBy": "admin",
     * 				"createTime": "2018-03-16 11:33:00",
     * 				"default": false,
     * 				"dictCode": 1101,
     * 				"dictLabel": "男",
     * 				"dictSort": 1,
     * 				"dictType": "sys_user_sex",
     * 				"dictValue": "0",
     * 				"isDefault": "0",
     * 				"orderBy": "",
     * 				"remark": "性别男",
     * 				"status": "0",
     * 				"updateBy": "tt-beihaitest",
     * 				"updateTime": "2022-03-25 10:57:43"
     *            },
     *            {
     * 				"createBy": "admin",
     * 				"createTime": "2018-03-16 11:33:00",
     * 				"default": false,
     * 				"dictCode": 1102,
     * 				"dictLabel": "女",
     * 				"dictSort": 2,
     * 				"dictType": "sys_user_sex",
     * 				"dictValue": "1",
     * 				"isDefault": "0",
     * 				"orderBy": "",
     * 				"remark": "性别女",
     * 				"status": "0",
     * 				"updateBy": "admin",
     * 				"updateTime": "2022-06-24 18:01:48"
     *            }
     * 		]
     * 	},
     * 	"msg": "请求成功",
     * 	"time": 1693813090553
     * }
     * @return Result<Map<String, List<TownShipDictData>>>
     */
    @GetMapping(value = "/base/system/dict/data/dictType/multiple/{dictTypes}")
    @Cacheable(cacheNames = "cacheDictDataTypeBatch",cacheManager = "customCacheManager")
    public Result<Map<String, List<TownShipDictData>>> dictType(@PathVariable(value = "dictTypes") String[] dictTypes);

    @GetMapping(value = "/base/system/dict/data/dictType/multipleSdk")
    @Cacheable(cacheNames = "cacheDictDataTypeBatch",cacheManager = "customCacheManager")
    public Result<Map<String, List<TownShipDictData>>> dictTypeSdk(@RequestBody String[] dictTypes);


    /**
     * 获取公共参数
     * @apiNote 获取公共参数
     * @apiVersion 6.19
     * @tag 六、基础功能能力
     * @PathVariable configKey
     * @responseDemo
     * configId 参数主键
     * configKey 参数键名
     * configName 参数名称
     * configType 系统内置（1是 0否）
     * configValue 参数键值
     * createTime 创建时间
     * creator 创建者
     * modifier 更新者
     * updateTime 更新时间
     * remark 备注
     * {
     * 	"msg": "操作成功",
     * 	"code": 200,
     * 	"data": {
     * 		"configId": 2,
     * 		"configKey": "sys.user.initPassword",
     * 		"configName": "用户管理-账号初始密码",
     * 		"configType": "1",
     * 		"configValue": "Abcd@1234",
     * 		"createTime": "2018-03-16 11:33:00",
     * 		"creator": "admin",
     * 		"modifier": "tower",
     * 		"orderBy": "",
     * 		"remark": "初始化密码 123456",
     * 		"updateTime": "2023-08-28 14:53:26"
     *        }
     * }
     * @return AjaxResult
     */
    @RequestMapping(value = "/system/config/base/detail/{configKey}", method = RequestMethod.GET)
    @Cacheable(cacheNames = "cacheGetConfValue",cacheManager = "customCacheManager")
    AjaxResult getConfValue(@PathVariable(value = "configKey") String configKey);

    @RequestMapping(value = "/system/config/base/detailSdk", method = RequestMethod.POST)
    @Cacheable(cacheNames = "cacheGetConfValue",cacheManager = "customCacheManager")
    AjaxResult getConfValueSdk(@RequestBody String configKey);


    /**
     * 查询租户树接口,视联调用
     * @apiNote 查询租户树接口,视联调用
     * @apiVersion 6.20
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "tenantId":"TY0000106"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *    children: 子集列表
     *    deptId: 部门id
     *    deptLevel: 部门层级
     *    deptName: 部门名称
     *    id: 唯一标识ID
     *    parentId: 父级ID
     *    tenantId: 租户ID
     * {
     * 	  "code": 200,
     * 	  "data": [
     *                {
     * 			"children": [
     *                {
     * 					"children": [],
     * 					"deptId": "TY0000106-0101",
     * 					"deptLevel": "2",
     * 					"deptName": "华苏测试01-1.0-1.1",
     * 					"id": 140747,
     * 					"parentId": "TY0000106-01",
     * 					"tenantId": "TY0000106"
     *                }
     * 			],
     * 			"deptId": "TY0000106-01",
     * 			"deptLevel": "1",
     * 			"deptName": "华苏测试01-1.0-1.0",
     * 			"id": 140738,
     * 			"parentId": "000000",
     * 			"tenantId": "TY0000106"
     *        },
     *        {
     * 			"children": [
     *                {
     * 					"children": [],
     * 					"deptId": "TY0000106-0201",
     * 					"deptLevel": "2",
     * 					"deptName": "华苏测试组织01",
     * 					"id": 140751,
     * 					"parentId": "TY0000106-02",
     * 					"tenantId": "TY0000106"
     *                },
     *                {
     * 					"children": [],
     * 					"deptId": "TY0000106-0204",
     * 					"deptLevel": "2",
     * 					"deptName": "95995",
     * 					"id": 140861,
     * 					"parentId": "TY0000106-02",
     * 					"tenantId": "TY0000106"
     *                }
     * 			],
     * 			"deptId": "TY0000106-02",
     * 			"deptLevel": "1",
     * 			"deptName": "华苏测试01-1.0-1.0",
     * 			"id": 140739,
     * 			"parentId": "000000",
     * 			"tenantId": "TY0000106"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693813295602
     * }
     */
    @PostMapping(path = TENANT_DEPT_PREFIX + "/tenantTreeList2Video")
    Result<List<SysDeptResponse>> tenantTreeList2Video(@Validated @RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest);

    /**
     * 返回平级结构信息集合
     * @apiNote 返回平级结构信息集合
     * @apiVersion 6.21
     * @tag 六、基础功能能力
     * @requestDemo
     * '{
     *     "tenantId":"TY0000106"
     * }'
     * @responseDemo
     * ancestors：祖级列表
     * children: 子部门,
     * city: 市分公司编码,
     * createBy: 创建人,
     * createTime: 创建时间,
     * delFlag: 区删除标志（0代表存在 2代表删除）域编码,
     * deptId: 部门ID,
     * deptLevel: 级别,
     * deptName: 部门名称,
     * hasChildren: 是否具有子节点,
     * id: 主键ID,
     * parentId: 父部门ID,
     * province: 省分公司编码,
     * status: 部门状态:0正常,1停用,
     * tenantId: 租户ID,
     * tenantName: 租户名称,
     * tenantTowerDeptId: 租户组织归属铁塔组织ID
     *
     * '{
     * 	"code": 200,
     * 	"data": {
     * 		"deptList": [
     *                        {
     * 				"ancestors": "000000",
     * 				"children": [],
     * 				"city": "TY0000106-01",
     * 				"createBy": "wucj",
     * 				"createTime": "2023-03-18 13:00:58",
     * 				"delFlag": "0",
     * 				"deptId": "TY0000106-01",
     * 				"deptLevel": "1",
     * 				"deptName": "华苏测试01-1.0-1.0",
     * 				"hasChildren": true,
     * 				"id": 140738,
     * 				"parentId": "000000",
     * 				"province": "000000",
     * 				"status": "0",
     * 				"tenantId": "TY0000106",
     * 				"tenantName": "华苏测试01",
     * 				"tenantTowerDeptId": "100000"
     *            },
     *            {
     * 				"ancestors": "000000",
     * 				"children": [],
     * 				"city": "TY0000106-02",
     * 				"createBy": "wucj",
     * 				"createTime": "2023-03-18 13:05:26",
     * 				"delFlag": "0",
     * 				"deptId": "TY0000106-02",
     * 				"deptLevel": "1",
     * 				"deptName": "华苏测试01-1.0-1.0",
     * 				"hasChildren": true,
     * 				"id": 140739,
     * 				"parentId": "000000",
     * 				"province": "000000",
     * 				"status": "0",
     * 				"tenantId": "TY0000106",
     * 				"tenantName": "华苏测试01",
     * 				"tenantTowerDeptId": "100000"
     *            }
     * 		]
     * 	},
     * 	"msg": "请求成功",
     * 	"time": 1693813370504
     * }'
     * @param quTenantDeptTopRequest 部门信息
     * @return Result<Map<String, Object>>
     */
    @PostMapping(path = "/system/tenantDept/selectTenantTop2Video")
    Result<Map<String, Object>> selectTenantTop2Video(@RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest);


    /**
     * 新增文件信息,视联使用
     * @apiNote 新增文件信息,视联使用
     * @apiVersion 6.22
     * @tag 六、基础功能能力
     * @requestDemo
     * uploadFile: 文件
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *  fileId 文件id
     *  fileName 文件名称
     *  fileSize 文件大小
     *  fileSuffix 文件扩展名
     *  fileUrl 文件url
     *  originUrl 文件原始url
     *   {
     *     "code": 200,
     *     "data": {
     *         "fileId": "634662cbfd7b4175b25507632407a576",
     *         "fileName": "xxxxx.xlsx",
     *         "fileSize": "9488",
     *         "fileSuffix": ".xlsx",
     *         "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/6d02d8453316121df2560e1a92fc478b/ea6ec98d1d6140ff90a43bef44752b3a.xlsx?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1736748543&Signature=qdnMwVnSrMtWz1lN%2BsRMik%2BlqeA%3D",
     *         "orderBy": "",
     *         "originUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/6d02d8453316121df2560e1a92fc478b/ea6ec98d1d6140ff90a43bef44752b3a.xlsx"
     *     },
     *     "msg": "上传成功",
     *     "time": 1736747943772
     * }
     * @return Result
     */
    @PostMapping(path = "/base/common/uploadFileVideo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadFile2Video(@RequestPart("uploadFile") MultipartFile uploadFile);

    /**
     * 下载文件接口,视联使用
     * @apiNote 下载文件接口,视联使用
     * @apiVersion 6.23
     * @tag 六、基础功能能力
     * @param fileId 文件ID
     * @return Result<SysFileInfoResponse>
     */
//    @GetMapping("/common/downloadById2Video")
//    Result<SysFileInfoResponse> downloadById2Video(@RequestParam("fileId") String fileId);


    /**
     * 下载文件接口，林业使用
     * @apiNote 下载文件接口，林业使用
     * @apiVersion 6.24
     * @tag 六、基础功能能力
     *@requestDemo
     * url: 文件地址
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data byte数组
     *   {
     *     "code": 200,
     *     "data": "",
     *     "msg": "",
     *     "time": 1736747943772
     * }
     */
    @GetMapping("/common/downloadByUrl")
    Result<Object> downloadByUrl(@RequestParam("url") String url);


    /**
     * 根据租户Id查询租户信息
     * @apiNote 根据租户Id查询租户信息
     * @apiVersion 6.25
     * @tag 六、基础功能能力
     * @PathVariable tenantId 租户ID
     * @responseDemo
     * cityId 地市id
     * cityName 地市名称
     * createBy 创建人
     * createTime 创建时间
     * id 主键id
     * provinceId 省份id
     * provinceName 省份名称
     * tenantId 客户id
     * tenantName 客户名称
     * updateBy 更新人
     * updateTime 更新时间
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"cityId": "500100",
     * 			"cityName": "重庆市",
     * 			"createBy": "tower",
     * 			"createTime": "2023-03-18 12:58:58",
     * 			"delFlag": 0,
     * 			"id": 2334,
     * 			"provinceId": "500000",
     * 			"provinceName": "重庆市",
     * 			"tenantId": "TY0000106",
     * 			"tenantName": "华苏测试01",
     * 			"updateBy": "tower",
     * 			"updateTime": "2023-03-18 13:10:37"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693813500514
     * }
     * @return Result<List<SysTenantInfoResponse>>
     */
    @GetMapping(TENANT_PREFIX + "/selectByTenantId/{tenantId}")
    Result<List<SysTenantInfoResponse>> selectByTenantId(@PathVariable(value = "tenantId") String tenantId);

    /**
     * 根据用户编码批量查询用户信息
     * @apiNote 根据用户编码批量查询用户信息
     * @apiVersion 6.26
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "userIds":[
     *         "309607",
     *         "1"
     *     ]
     * }
     * @responseDemo
     *  返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		data：返回内容
     * 		userId：用户编码
     * 		userName：用户登录账号
     * 		nickName：用户名称
     * 		phonenumber：手机号码
     * 		userType：用户类型
     * 		roleIds：角色id组
     * {
     *     "msg":"",
     *     "code":200,
     *     "data":[
     *         {
     *             "nickName":"",
     *             "userId":1,
     *             "userName":"admin",
     *             "phonenumber":"110",
     *             "userType":"0",
     *             "roleIds ":[
     *
     *             ]
     *         },
     *         {
     *             "nickName":"",
     *             "userId":309607,
     *             "userName":"bj-tenant-admin",
     *             "phonenumber":"110",
     *             "userType":"0",
     *             "roleIds ":[
     *
     *             ]
     *         }
     *     ]
     * }
     * @param user
     * @return
     */
    @PostMapping(path = "/system/user/getListByIds")
    AjaxResult getListByIds(@RequestBody SysUser user);

    /**
     * 根据用户账号批量查询用户信息
     * @apiNote 根据用户账号批量查询用户信息
     * @apiVersion 6.27
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "userNames":[
     *         "dtf_tu",
     *         "junna1"
     *     ]
     * }
     * @responseDemo
     * code：返回编码
     * msg：返回信息
     * data：返回内容
     * userId ：用户编码
     * userName：用户登录账号
     * nickName：用户名称
     * {
     *     "msg":"",
     *     "code":200,
     *     "data":[
     *         {
     *             "nickName":"",
     *             "userId":1,
     *             "userName":"admin"
     *         },
     *         {
     *             "nickName":"",
     *             "userId":309607,
     *             "userName":"bj-tenant-admin"
     *         }
     *     ]
     * }
     * @param user
     * @return
     */
    @PostMapping(path = "/system/user/getListByUserNames")
    AjaxResult getListByUserNames(@RequestBody SysUser user);


    /**
     * 查询用户列表
     * @apiNote 查询用户列表
     * @apiVersion 6.28
     * @tag 六、基础功能能力
     * @param user
     * @requestDemo
     * {
     *     "roleId":"111",
     *     "tenantId":"120011111"
     * }
     * @responseDemo
     * code：返回编码
     * msg：返回信息
     * data：返回内容
     * userId ：用户编码
     * userName：用户登录账号
     * nickName：用户名称
     * source：来源：0铁塔 1租户
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"admin": false,
     * 			"adminNew": false,
     * 			"nickName": "通用组织一测试二",
     * 			"orderBy": "",
     * 			"roles": [],
     * 			"userId": "08ba1b7fc775403391f61f2588c3185e",
     * 			"userName": "tytest04"
     *        },
     *        {
     * 			"admin": false,
     * 			"adminNew": false,
     * 			"nickName": "测试三",
     * 			"orderBy": "",
     * 			"roles": [],
     * 			"userId": "d99b9e323e01498f8d51cb1a32d025f8",
     * 			"userName": "tytest09"
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1693813552396
     * }
     * @return Result<List<SysUser>>
     */
    @PostMapping(path = "/system/user/getUserList")
    Result<List<SysUser>> getUserList(@RequestBody SysUser user);

    /**
     * 查询组织人员树
     * @apiNote 查询组织人员树
     * @apiVersion 6.29
     * @tag 六、基础功能能力
     * @requestDemo
     * {"userId":"1"}
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		data：返回内容
     * 		dept：1-组织;0-人员
     * 		id：编码
     * 		account：当dept=0时，返回人 员账号,dept=1时，为 空。
     * 		phone：当dept=0时，返回人 员手机号,dept=1时，为空。
     * 		sex：当dept=0时，返回人 员性别（0男  1女）, dept=1时，为空。
     * 		label：名称
     * 		pid：父级编码
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "account":"",
     *             "children":[
     *                 {
     *                     "account":"XiaoXin88",
     *                     "dept":"0",
     *                     "id":"faeef1e0e2694e89ad29cc718e425cde",
     *                     "label":"没摄像机的小新",
     *                     "phone":"13552339573",
     *                     "pid":"122035807-01020101"
     *                 }
     *             ],
     *             "dept":"1",
     *             "id":"122035807-01020101",
     *             "label":"345",
     *             "phone":"",
     *             "pid":"122035807-010201",
     *             "sex":""
     *         }
     *     ]
     * }
     * @param sysUser
     * @return
     */
    @PostMapping(path = "/base/system/tenantDept/getTenantTree")
    Result<List<TreeSelectBySystem>> getTenantTree(@RequestBody SysUser sysUser);

    /**
     * 根据用户编号获取详细信息
     * @apiNote 根据用户编号获取详细信息
     * @apiVersion 6.30
     * @tag 六、基础功能能力
     * @requestDemo
     * {"userId":"309609"}
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		data：返回内容
     * 		roleIds：角色编码集合
     * 		deptId：部门编码
     *         email：邮箱
     *         isTenantAdmin：是否租户管理员
     *         nickName：昵称
     *         phonenumber：手机号
     *         remark：备注
     *         sex：性别
     *         status：状态
     *         tenantId：租户编码
     *         tenantName：租户名称
     *         userId：用户编码
     *         userName：租户名称
     *         userSource：用户来源
     *         userType：用户类型（0系统用户 2租户）
     *         dept：{
     *             deptId：TEN100001-01
     *             deptLevel：部门级别
     *             deptName：部门名称
     *             parentId：上级编码
     *             status：部门状态
     *         }
     * {
     *     "msg":"",
     *     "code":200,
     *     "roleIds":[
     *         998951
     *     ],
     *     "data":{
     *         "admin":false,
     *         "createBy":"admin",
     *         "createTime":"2022-03-11 22:59:22",
     *         "delFlag":"0",
     *         "dept":{
     *             "ancestors":"000000",
     *             "children":[
     *
     *             ],
     *             "deptId":"TEN100001-01",
     *             "deptLevel":"1",
     *             "deptName":"",
     *             "parentId":"000000",
     *             "status":"0"
     *         },
     *         "deptId":"TEN100001-01",
     *         "email":"hj123456@163.com",
     *         "isTenantAdmin":"1",
     *         "nickName":"",
     *         "pageNum":0,
     *         "pageSize":0,
     *         "phonenumber":"13523302583",
     *         "remark":"hj",
     *         "roles":[
     *
     *         ],
     *         "sex":"0",
     *         "status":"0",
     *         "tenantId":"TEN100001",
     *         "tenantName":"",
     *         "tradeInfo":"100000, 400000",
     *         "userId":309609,
     *         "userName":"hjtest",
     *         "userSource":"1",
     *         "userType":"2"
     *     },
     *     "postIds":[
     *
     *     ]
     * }
     * @param userId 用户编码
     * @return
     */
    @GetMapping(value = {"/system/user/base/detail/{userId}"})
    AjaxResult getInfo(@PathVariable(value = "userId") String userId);

    /**
     * 查询参数配置
     * @apiNote 查询参数配置
     * @apiVersion 6.31
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "configKey":"sys.message.pc"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		data：返回内容
     * 		configName：参数名称
     * 		configType：系统内置（1是 0否）
     * 		configValue：参数值
     * {
     *     "msg":"",
     *     "code":200,
     *     "data":{
     *         "configId":100037,
     *         "configKey":"sys.message.pc",
     *         "configName":"PC",
     *         "configType":"1",
     *         "configValue":"1",
     *         "createBy":"admin",
     *         "createTime":"2022-03-02 15:21:51",
     *         "params":{
     *
     *         },
     *         "remark":"1-;2-",
     *         "updateBy":"admin",
     *         "updateTime":"2022-03-11 11:42:14"
     *     }
     * }
     * @param config
     * @return
     */
    @PostMapping(value = "/system/config/getConfigInfo")
    @Cacheable(cacheNames = "cacheGetConfValue",cacheManager = "customCacheManager",key = "#config.getConfigKey()")
    AjaxResult getConfigInfo(@RequestBody SysConfig config);

    /**
     * 获取登录账号数据权限(返回liststring)
     * @apiNote 获取登录账号数据权限(返回liststring)
     * @apiVersion 6.32
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "userId":"03e6ad74d2354cbab9bf2b51fdfaa079"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		data：摄像机编码集合
     * {
     *     "code": 200,
     *     "data": [
     *         "11010500011327000005-00000000832127000026",
     *         "11010600031327000001",
     *         "11010800831327000003"
     *     ],
     *     "msg": "请求成功",
     *     "time": 1747290997442
     * }
     * @param user
     * @return
     */
    @PostMapping("/system/user/getPermissionsForString")
    Result getPermissionsForString(@RequestBody SysUser user);

    /**
     * 常用语查询
     * @apiNote 常用语查询
     * 返回参数data下字段解析：
     * 		wordsId：常用语编码
     * 		typeId：常用语类型
     * 		content：常用语
     * @apiVersion 6.33
     * @tag 六、基础功能能力
     * @param words
     * @requestDemo
     * {
     *     "userId":"2324342",
     *     "tenantId":"34500807",
     *     "typeId":"1"
     * }
     * @responseDemo
     * code：状态码，表示请求的处理结果（整型，200表示成功）
     * data：数据数组，包含内容和类型信息
     * content：内容（字符串，例如“”或“1”）
     * typeId：类型ID（字符串，例如“1”）
     * wordsId：词语ID（字符串，例如“6656853376329861055”）
     * msg：响应消息，表示请求的额外信息（字符串，空字符串表示无额外信息）
     * time：时间戳，表示响应生成的时间（长整型，例如1650422238917）
     *
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "content":"",
     *             "typeId":"1",
     *             "wordsId":"6656853376329861055"
     *         },
     *         {
     *             "content":"1",
     *             "typeId":"1",
     *             "wordsId":"6656853376329861068"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1650422238917
     * }
     * @return
     */
    @PostMapping("/system/commonWords/getCommonWords")
    Result<List<VmCommonWordsVO>> getCommonWords(@RequestBody wordsRequest words);

    /**
     * 查询省会
     * @apiNote 查询省会
     * @apiVersion 6.34
     * @tag 六、基础功能能力
     * @requestDemo
     * {"provCode":"530000"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		capitalCode：省会编码
     * 		capitalName：省会名称
     * {
     *     "code":200,
     *     "data":{
     *         "capitalCode":"530100",
     *         "capitalName":""
     *     },
     *     "msg":"",
     *     "time":1652686651522
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/area/getSysCapital")
    Result<SysCapitalVO> getSysCapital(@RequestBody SysCapitalVO vo);

    /**
     * 增加常用语
     * @apiNote 增加常用语
     * @apiVersion 6.35
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "userId":"2324342",
     *     "tenantId":"34500807",
     *     "appVerCode":"1.0",
     *     "content":"常用语",
     *     "typeId":"1"
     * }
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650421841533
     * }
     * @param words
     * @return
     */
    @PostMapping("/system/commonWords/addCommonWords")
    Result addCommonWords(@RequestBody wordsRequest words);


    /**
     * 修改常用语
     * @apiNote 修改常用语
     * @apiVersion 6.36
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "userId":"2324342",
     *     "tenantId":"34500807",
     *     "appVerCode":"1.0",
     *     "content":"常用语",
     *     "wordsId":"111"
     * }
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650421841533
     * }
     * @param words
     * @return
     */
    @PostMapping("/system/commonWords/updateCommonWords")
    Result<Integer> updateCommonWords(@RequestBody wordsRequest words);

    /**
     * 修改常用语
     * @apiNote 修改常用语
     * @apiVersion 6.37
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     “关键字”
     * }
     * @ResponseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650421841533
     *     data:"关键字"
     * }
     * @return Result<String>
     */
    @PostMapping("/system/commonWords/updateCommonWordsBatch")
    Result<String> updateCommonWordsBatch(@RequestBody wordsRequest words);

    /**
     * 删除常用语
     * @apiNote 删除常用语
     * @apiVersion 6.38
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "wordsId":"111"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650421841533
     *     data:1
     * }
     * @param words
     * @return
     */
    @PostMapping("/system/commonWords/deleteCommonWordsByWordsId")
    Result<Integer> deleteCommonWordsByWordsId(@RequestBody wordsRequest words);


    /**
     * 删除常用语
     * @apiNote 删除常用语
     * @apiVersion 6.39
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "wordsId":"111"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650421841533
     *     data:1
     * }
     * @return Result<String>
     */
    @PostMapping("/system/commonWords/deleteCommonWordsByWordsIdBatch")
    Result<Integer> deleteCommonWordsByWordsIdBatch(@RequestBody wordsRequest words);



    /**
     * 组织和行政区域关联关系查询
     * @apiNote 组织和行政区域关联关系查询
     * @apiVersion 6.40
     * @tag 六、基础功能能力
     * @param vo
     * @return Result<List<SysDeptStreetRelVO>>
     * @requestDemo
     * {"tenantId": null,
     * 	"deptId": "770014994-09",
     * 	"provinceCode": null,
     * 	"cityCode": null,
     * 	"countyCode": null,
     * 	"streetAreaCode": null,
     * 	"industryCode": null,
     * 	"type": null,
     * 	"appVerCode": null}
     * 	@responseDemo
     * 	appVerCode:版本
     * 	cityCode:城市编码
     * 	countyCode:区县编码
     * 	deptId:部门编码
     * 	provinceCode:省份编码
     * 	industryCode:行业编码
     * 	streetAreaCode:街道编码
     * 	tenantId:租户编码
     *{
     * 	"code": 200,
     * 	"data": [{
     * 		"appVerCode": "1.0",
     * 		"cityCode": "440700",
     * 		"countyCode": "440705",
     * 		"deptId": "770014994-09",
     * 		"provinceCode": "440000",
     * 		"streetAreaCode": "440705110000",
     * 		"tenantId": "770014994"
     * 	    }],
     * 	"msg": "查询成功",
     * 	"time": 1737079053050
     * }
     */
    @PostMapping("/system/sysDeptStreetRel/getDeptStreetRel")
    Result<List<SysDeptStreetRelVO>> getDeptStreetRel(@RequestBody SysDeptStreetRelVO vo);


    /**
     * 新增组织和行政区域关联关系
     * @apiNote 新增组织和行政区域关联关系
     * @apiVersion 6.41
     * @tag 六、基础功能能力
     * @param list
     * @return Result
     * @requestDemo
     *  [{
     * 		"appVerCode": "1.0",
     * 		"cityCode": "440700",
     * 		"countyCode": "440705",
     * 		"deptId": "770014994-09",
     * 		"provinceCode": "440000",
     * 		"streetAreaCode": "440705110000",
     * 		"tenantId": "770014994"
     * 	    }]
     *@responseDemo
     * {
     *     "code":200,
     *     "msg":"新增成功",
     *     "time":1650437891574
     * }
     */
    @PostMapping("/system/sysDeptStreetRel/insertDeptStreetRel")
    Result insertDeptStreetRel(@RequestBody List<SysDeptStreetRelVO> list);


    /**
     * 删除组织和行政区域关联关系查询
     * @apiNote 删除组织和行政区域关联关系查询
     * @apiVersion 6.42
     * @tag 六、基础功能能力
     * @param vo
     * @return Result
     *  {
     * 		"cityCode": "440700",
     * 		"countyCode": "440705",
     * 		"deptId": "770014994-09",
     * 		"provinceCode": "440000",
     * 		"streetAreaCode": "440705110000",
     * 		"tenantId": "770014994"
     * 	    }
     *@responseDemo
     * {
     *     "code":200,
     *     "msg":"删除成功",
     *     "time":1650437891574
     * }
     */
    @PostMapping("/system/sysDeptStreetRel/deleteDeptStreetRel")
    Result deleteDeptStreetRel(@RequestBody SysDeptStreetRelVO vo);

    /**
     * 获取登录账号数据权限
     * @apiNote 获取登录账号数据权限
     * @apiVersion 6.43
     * @tag 六、基础功能能力
     * @requestDemo
     * {"userId":"1"}
     * @responseDemo
     *     * 返回参数data下字段解析：
     *      * 		provinceId：省编码
     *      * 		provinceName：省名称
     *      * 		cityId：城市编码
     *      * 		cityName：城市名
     *      * 		countyId：区县编码
     *      * 		countyName：区县名称
     *      * 		deviceCode：设备编码
     *      * 		deviceName：设备名称
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "cityId":"450500",
     *             "cityName":"",
     *             "countyId":"450521",
     *             "countyName":"",
     *             "deviceCode":"45052100831327000001",
     *             "deviceName":"-235-1",
     *             "provinceId":"450000",
     *             "provinceName":"",
     *             "tenantId":"TEN100001"
     *         },
     *         {
     *             "cityId":"110100",
     *             "cityName":"",
     *             "countyId":"360402",
     *             "countyName":"",
     *             "deviceCode":"36040200831327000005",
     *             "deviceName":"",
     *             "provinceId":"110000",
     *             "provinceName":"",
     *             "tenantId":"TEN100001"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1648718292155
     * }
     * @param user
     * @return
     */
    @PostMapping("/system/user/getPermissions")
    Result<List<DeviceTreeVO>> getPermissions(@RequestBody SysUser user);


    /**
     * 查询登录账号权限范围内的行政区划树
     * @apiNote 查询登录账号权限范围内的行政区划树
     * @apiVersion 6.44
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数data下字段解析：
     * 		code：地区编码
     * 		name：地区名称
     * 		list：下级列表
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "code":"110000",
     *             "list":[
     *                 {
     *                     "code":"110100",
     *                     "list":[
     *                         {
     *                             "code":"360402",
     *                             "list":[
     *
     *                             ],
     *                             "name":""
     *                         }
     *                     ],
     *                     "name":""
     *                 }
     *             ],
     *             "name":""
     *         },
     *         {
     *             "code":"450000",
     *             "list":[
     *                 {
     *                     "code":"450500",
     *                     "list":[
     *                         {
     *                             "code":"450521",
     *                             "list":[
     *
     *                             ],
     *                             "name":""
     *                         }
     *                     ],
     *                     "name":""
     *                 }
     *             ],
     *             "name":""
     *         }
     *     ],
     *     "msg":"",
     *     "time":1648719151954
     * }
     * @param user
     * @return
     */
    @PostMapping("/system/user/getRegionTree")
    Result<List<DeviceTreeVO>> getRegionTree(@RequestBody SysUser user);

    /**
     * 查询登录账号全部的行政区划树
     * @apiNote 查询登录账号全部的行政区划树
     * @apiVersion 6.45
     * @tag 六、基础功能能力
     * @requestDemo
     * {"tenantId":"3452807"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		provinceId：省份
     * 		cityId：地市
     * 		countyId：区域
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "cityId":"530900",
     *             "countyId":"530921",
     *             "provinceId":"530000"
     *         },
     *         {
     *             "cityId":"120100",
     *             "countyId":"120119",
     *             "provinceId":"120000"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1651544682089
     * }
     * @param user
     * @return
     */
    @PostMapping("/system/user/getAreaByTenantId")
    Result<List<AreaDataVO>> getAreaByTenantId(@RequestBody SysUser user);

    /**
     * 根据行业编码获取区县人员的手机号码
     * @apiNote 根据行业编码获取区县人员的手机号码
     * @apiVersion 6.46
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "areaCode":"110101",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		userId：用户编码
     * 		phonenumber：电话号码
     * 		tenantId：租户id
     * 		userName：用户名称
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "admin":false,
     *             "pageNum":0,
     *             "pageSize":0,
     *             "phonenumber":"11111111111",
     *             "roles":[
     *
     *             ],
     *             "tenantId":"121061973",
     *             "userId":309739,
     *             "userName":"bjshyygc"
     *         },
     *         {
     *             "admin":false,
     *             "pageNum":0,
     *             "pageSize":0,
     *             "phonenumber":"13312341234",
     *             "roles":[
     *
     *             ],
     *             "tenantId":"121061973",
     *             "userId":309748,
     *             "userName":"test1-3"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1648968100275
     * }
     * @param user
     * @return
     */
    @PostMapping("/system/user/getPhoneByRegion")
    Result<List<SysUser>> getPhoneByRegion(@RequestBody SysUser user);

    /**
     * 获取行政区划信息
     * @apiNote 获取行政区划信息
     * @apiVersion 6.47
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "areaCode":"210000",
     *     "areaLevel":"2",
     *     "areaName":"",
     *     "areaParentCode":"100000",
     *     "longitude":"",
     *     "latitude":"100000",
     *     "areaCodeList":[
     *
     *     ]
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		areaLevel：行政区划等级
     * 		areaCode：地区编码
     * 		areaName：地区名称
     * 		areaParentCode：父级地区编码
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "areaCode":"210000",
     *             "areaLevel":"2",
     *             "areaName":"",
     *             "areaParentCode":"100000",
     *             "children":[
     *
     *             ],
     *             "params":{
     *
     *             }
     *         }
     *     ],
     *     "msg":"",
     *     "time":1648968893834
     * }
     * @param sysArea
     * @return
     */
    @PostMapping("/video/getRegion")
    Result<List<SysArea>> getRegion(@RequestBody SysArea sysArea);


    /**
     * 获取切面数据接口
     * @apiNote 获取切面数据接口
     * @apiVersion 6.48
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		deptList：组织列表
     * 		radars：雷达
     * 		cameras：摄像机
     * 		sensors：传感器
     * 		satellites：卫星
     * 		areaList：管理区域
     * 		deptId：组织id
     * 		deptlName：组织名称
     * 		parentdeptId：上级组织id
     * 		deviceName：设备名
     * 		deviceCode：设备编号
     * 		deviceType：设备类型
     * 		provinceId：省
     * 		cityId：市
     * 		countyId：区
     * 		townCode：街道
     * 		provinceCode：省
     * 		cityCode：市
     * 		countyCode：区
     * 		streetCode：街道
     * {
     *     "code":200,
     *     "data":{
     *         "areaList":[
     *             {
     *                 "cityCode":"430600",
     *                 "countyCode":"430623",
     *                 "provinceCode":"430000"
     *             }
     *         ],
     *         "cameras":[
     *             {
     *                 "cityId":"430600",
     *                 "countyId":"430623",
     *                 "deviceCode":"43062300831327000030",
     *                 "deviceName":"-3km",
     *                 "deviceType":"4",
     *                 "provinceId":"430000",
     *                 "townCode":"430623115000"
     *             }
     *         ],
     *         "deptList":[
     *             {
     *                 "deptId":"122049393-0104",
     *                 "parentdeptId":"122049393-01"
     *             }
     *         ],
     *         "radars":[
     *             {
     *                 "cityId":"430600",
     *                 "countyId":"430623",
     *                 "deviceCode":"341",
     *                 "deviceName":"OLT-G",
     *                 "deviceType":"10",
     *                 "provinceId":"430000",
     *                 "townCode":"430623114000"
     *             }
     *         ],
     *         "satellites":[
     *
     *         ],
     *         "sensors":[
     *             {
     *                 "cityId":"440800",
     *                 "countyId":"440802",
     *                 "deviceCode":"aqms-1100-2021040296",
     *                 "deviceName":"",
     *                 "deviceType":"8",
     *                 "provinceId":"440000"
     *             }
     *         ]
     *     },
     *     "msg":"",
     *     "time":1687340165311
     * }
     * @return
     */
    @PostMapping("/system/business/getDataScopeObj")
    Result<AuthObjDataVO> getDataScopeObj();

    /**
     * 查询组织设备
     * @apiNote 查询组织设备
     * @apiVersion 6.49
     * @tag 六、基础功能能力
     * @param user
     * @return Result
     * @requestDemo
     *{
     * 	"tenantId": "700015421",
     * 	"industryCode": "400007",
     * 	"adminNew": false,
     * 	"admin": false,
     * 	"orderBy": ""
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		deptList：组织列表
     * 		radars：雷达
     * 		cameras：摄像机
     * 		sensors：传感器
     * 		satellites：卫星
     * 		areaList：管理区域
     * 		deptId：组织id
     * 		deptlName：组织名称
     * 		parentdeptId：上级组织id
     * 		deviceName：设备名
     * 		deviceCode：设备编号
     * 		deviceType：设备类型
     * 		provinceId：省
     * 		cityId：市
     * 		countyId：区
     * 		townCode：街道
     * 		provinceCode：省
     * 		cityCode：市
     * 		countyCode：区
     * 		streetCode：街道
     * {
     *     "code":200,
     *     "data":{
     *         "areaList":[
     *             {
     *                 "cityCode":"430600",
     *                 "countyCode":"430623",
     *                 "provinceCode":"430000"
     *             }
     *         ],
     *         "cameras":[
     *             {
     *                 "cityId":"430600",
     *                 "countyId":"430623",
     *                 "deviceCode":"43062300831327000030",
     *                 "deviceName":"-3km",
     *                 "deviceType":"4",
     *                 "provinceId":"430000",
     *                 "townCode":"430623115000"
     *             }
     *         ],
     *         "deptList":[
     *             {
     *                 "deptId":"122049393-0104",
     *                 "parentdeptId":"122049393-01"
     *             }
     *         ],
     *         "radars":[
     *             {
     *                 "cityId":"430600",
     *                 "countyId":"430623",
     *                 "deviceCode":"341",
     *                 "deviceName":"OLT-G",
     *                 "deviceType":"10",
     *                 "provinceId":"430000",
     *                 "townCode":"430623114000"
     *             }
     *         ],
     *         "satellites":[
     *
     *         ],
     *         "sensors":[
     *             {
     *                 "cityId":"440800",
     *                 "countyId":"440802",
     *                 "deviceCode":"aqms-1100-2021040296",
     *                 "deviceName":"",
     *                 "deviceType":"8",
     *                 "provinceId":"440000"
     *             }
     *         ]
     *     },
     *     "msg":"",
     *     "time":1687340165311
     * }
     */
    @PostMapping("/system/business/getDataScopeObjBySysUser")
    Result<AuthObjDataVO> getDataScopeObjBySysUser(@RequestBody SysUser user);


    /**
     * 获取切面数据接口
     * @apiNote 获取切面数据接口
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		deptList：组织列表
     * 		layers：图层
     * 		deptId：组织id
     * 		deptlName：组织名称
     * 		parentdeptId：上级组织id
     * 		deviceName：设备名
     * 		deviceCode：设备编号
     * 		deviceType：设备类型
     * @apiVersion 6.50
     * @tag 六、基础功能能力
     * @responseDemo
     * {
     *     "code":200,
     *     "data":{
     *         "layers":[
     *             {
     *                 "deviceCode":"43062300831327000030",
     *                 "deviceName":"图层",
     *                 "deviceType":"14",
     *             }
     *         ],
     *         "deptList":[
     *             {
     *                 "deptId":"122049393-0104",
     *                 "parentdeptId":"122049393-01"
     *             }
     *         ],
     *     },
     *     "msg":"",
     *     "time":1687340165311
     * }
     * @return
     */
    @PostMapping("/system/business/getDataPermissionsObj")
    Result<AuthObjDataVO> getDataPermissionsObj();

    /**
     * 查询组织设备
     * @apiNote 查询组织设备
     * @apiVersion 6.51
     * @tag 六、基础功能能力
     * @param user
     * @return Result
     */
    @PostMapping("/system/business/getDataPermissionsObjBySysUser")
    Result<AuthObjDataVO> getDataPermissionsObjBySysUser(@RequestBody SysUser user);



    /**
     * 查询失败描述
     * @apiNote 查询失败描述
     * @apiVersion 6.52
     * @tag 六、基础功能能力
     * @requestDemo "100300007"
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"data": "请求参数不合法",
     * 	"msg": "请求成功",
     * 	"time": 1692262755061
     * }
     * @param falseCode 错误码
     * @return
     */
    @PostMapping("/system/commonWords/queryFalseWords")
    Result<String> queryFalseWords(@RequestBody String falseCode);

    /**
     * 获取街道列表
     * @apiNote 获取街道列表
     * @apiVersion 6.53
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "areaParentCode":"11010200"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		areaCode：街道编码
     * 		areaName：街道名称
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "areaCode":"110102001000",
     *             "areaName":""
     *         },
     *         {
     *             "areaCode":"110102019000",
     *             "areaName":""
     *         },
     *         {
     *             "areaCode":"110102020000",
     *             "areaName":""
     *         }
     *     ],
     *     "msg":"",
     *     "time":1653373959105
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/area/getAllStreetList")
    Result<List<StreetTreeVO>> getAllStreetList(@RequestBody StreetTreeVO vo);


    /**
     * 获取部门下拉树列表
     * @apiNote 获取部门下拉树列表
     * @apiVersion 6.54
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数data下字段解析：
     * 		dept：部门编码
     * 		id：id
     * 		label：名称
     * 		pid：父id
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "children":[
     *                 {
     *                     "children":[
     *                         {
     *                             "id":"820100",
     *                             "label":"澳门市",
     *                             "pid":"820000"
     *                         }
     *                     ],
     *                     "id":"820000",
     *                     "label":"澳门特别行政区",
     *                     "pid":"100000"
     *                 }
     *             ],
     *             "id":"100000",
     *             "label":"中国",
     *             "pid":"1"
     *         }
     *     ],
     *     "msg":"查询成功",
     *     "time":1692262987678
     * }
     * @return
     */
    @PostMapping("/base/system/area/treeselectNew")
    Result<List<TreeSelect>> selectTreeSelect();


    /**
     * 获取通知公告列表
     * @apiNote 获取通知公告列表
     * @apiVersion 6.55
     * @tag 六、基础功能能力
     * @param notice
     * @return TableDataInfo
     * @responseDemo
     * 返回参数data下字段解析：
     *     appVerCode：应用版本号
     *     createBy：创建人
     *     createByName：创建人名称
     *     createTime：创建时间
     *     deptId：部门id
     *     ifAllReceive：是否全员接收
     *     industryCode：行业编码
     *     noticeContent：公告内容
     *     noticeId：公告id
     *     noticeTitle：公告标题
     *     noticeType：公告类型
     *     orderBy：排序
     *     publishTime：发布时间
     *     receiveDepts：接收部门
     *     receiveEnd：是否结束
     *     receiveUsers：接收人员
     *     status：状态
     *     tenantId：租户id
     *     tenantName：租户名称
     *     updateBy：更新人
     *     updateTime：更新时间
     *{
     * 	"code": 200,
     * 	"rows": [{
     * 		"appVerCode": "1.0",
     * 		"createBy": "haojing001",
     * 		"createByName": "浩鲸",
     * 		"createTime": "2025-02-29 10:58:46",
     * 		"deptId": "121062646-02",
     * 		"ifAllReceive": "0",
     * 		"industryCode": "000000",
     * 		"noticeContent": "<p>测试</p>",
     * 		"noticeId": 414,
     * 		"noticeTitle": "测试",
     * 		"noticeType": "WEB_101",
     * 		"orderBy": "",
     * 		"publishTime": "2024-02-29 10:59:02",
     * 		"receiveDepts": "[]",
     * 		"receiveEnd": "1",
     * 		"receiveUsers": "3c0ebececc9d478abc52c44bd850ebee",
     * 		"status": "1",
     * 		"tenantId": "121062646",
     * 		"tenantName": "河北广电信息网络集团股份有限公司宁晋分公司_备电",
     * 		"updateBy": "haojing001",
     * 		"updateTime": "2024-02-29 10:59:02"
     * 	    }],
     * 	"total": 1
     * }
     */
    @ApiOperation(value = "获取通知公告列表")
    @PostMapping("/system/notice/list")
    TableDataInfo list(@RequestBody SysNoticeReq notice);


    @ApiOperation(value = "获取通知公告列表/分权限")
    @GetMapping("/system/notice/getList")
    TableDataInfo getList(SysNotice notice);


    @ApiOperation(value = "根据通知公告编号获取详细信息")
    @GetMapping(value = "/system/notice/detail/{noticeId}")
    AjaxResult getInfo(@PathVariable Long noticeId);

    /**
     * 获取租户部门下拉树列表
     * @apiNote 获取租户部门下拉树列表
     * @apiVersion 6.56
     * @tag 六、基础功能能力
     * @return TableDataInfo
     * @responseDemo
     * 返回参数data下字段解析：
     *     children：子节点
     *     id：id
     *     label：名称
     *     pid：父id
     *     tenantId：租户id
     * {
     * 	"code": 200,
     * 	"data": [{
     * 		"children": [{
     * 			"id": "123098706-0104",
     * 			"label": "阿克苏公路管理局",
     * 			"pid": "123098706-01",
     * 			"tenantId": "123098706"
     * 		        }, {
     * 			"id": "123098706-0103",
     * 			"label": "阿克苏公路局",
     * 			"pid": "123098706-01",
     * 			"tenantId": "123098706"
     *        }, {
     * 			"id": "123098706-0102",
     * 			"label": "克州公路管理局",
     * 			"pid": "123098706-01",
     * 			"tenantId": "123098706"
     *        }, {
     * 			"id": "123098706-0101",
     * 			"label": "铁塔维护",
     * 			"pid": "123098706-01",
     * 			"tenantId": "123098706"
     *        }],
     * 		"id": "123098706-01",
     * 		"label": "新疆维吾尔自治区公路事业发展中心-路网智保版本-1.0",
     * 		"pid": "000000",
     * 		"tenantId": "123098706"    * 	}],
     * 	"msg": "请求成功",
     * 	"time": 1737080805999
     * }
     */
    @GetMapping("/base/system/tenantDept/getVideoTreeselect")
    Result<List<TreeSelect>> getVideoTreeselect();


    /**
     * 获取文件信息详细信息
     * @apiNote 获取文件信息详细信息
     * @apiVersion 6.57
     * @tag 六、基础功能能力
     * @param fileId
     * @return AjaxResult
     */
    @RequestMapping(value = "/system/fileinfo/detail/{fileId}", method = RequestMethod.GET)
    AjaxResult getFileInfo(@PathVariable(value = "fileId") String fileId);

    /**
     * 获取文件信息详细信息
     * @apiNote 获取文件信息详细信息
     * @apiVersion 6.58
     * @tag 六、基础功能能力
     * @param fileIds
     * @return AjaxResult
     */
    @RequestMapping(value = "/base/system/fileinfo/byIds/{fileIds}", method = RequestMethod.GET)
    AjaxResult selectSysFileInfoByIds(@PathVariable(value = "fileIds") String[] fileIds);

    /**
     * 获取全部省市区信息
     * @apiNote 获取全部省市区信息
     * @apiVersion 6.59
     * @tag 六、基础功能能力
     * @param sysArea
     * @return Result<List<SysArea>>
     * @requestDemo 传空对象
     * {
     * }
     * @responseDemo
     * areaCode 行政区域编码
     * areaLevel 行政区域级别
     * areaName 行政区域名称
     * areaParentCode 父级行政区域编码
     * areaParentName 父级行政区域名称
     * bussnessUnit 归属主体
     * createBy 创建人
     * id 主键
     * latitude 纬度
     * longitude 经度
     * orderBy 排序
     * status 状态
     * updateBy 更新人
     * {
     *   "code": 200,
     *   "data": [
     *     {
     *       "areaCode": "100000",
     *       "areaLevel": "1",
     *       "areaName": "中国",
     *       "areaParentCode": "1",
     *       "areaParentName": "顶级节点",
     *       "bussnessUnit": "02",
     *       "children": [
     *
     *       ],
     *       "createBy": "admin",
     *       "id": 0,
     *       "latitude": "24.18350298518886",
     *       "longitude": "120.67785339295995",
     *       "orderBy": "",
     *       "status": "0",
     *       "updateBy": "admin"
     *     },
     *     {
     *       "areaCode": "110000",
     *       "areaLevel": "2",
     *       "areaName": "北京市",
     *       "areaParentCode": "100000",
     *       "areaParentName": "中国",
     *       "children": [
     *
     *       ],
     *       "id": 0,
     *       "latitude": "39.90277565",
     *       "longitude": "116.4011447",
     *       "orderBy": "",
     *       "status": "0"
     *     },
     *     {
     *       "areaCode": "120000",
     *       "areaLevel": "2",
     *       "areaName": "天津市",
     *       "areaParentCode": "100000",
     *       "areaParentName": "中国",
     *       "children": [
     *
     *       ],
     *       "id": 0,
     *       "latitude": "39.08431231",
     *       "longitude": "117.1952119",
     *       "orderBy": "",
     *       "status": "0"
     *     }
     *   ],
     *   "msg": "查询成功",
     *   "time": 1747290987011
     * }
     */
    @PostMapping("/system/area/getAllArea")
    Result<List<SysArea>> getAllArea(@RequestBody SysArea sysArea);


    /**
     * 根据行政编码获取经纬度
     * @apiNote 根据行政编码获取经纬度
     * @apiVersion 6.60
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数data下字段解析：
     * 		latitud：纬度
     * 		longitud：纬度
     * {
     *     "code":200,
     *     "data":{
     *         "latitude":"39.929986",
     *         "longitude":"116.395645"
     *     },
     *     "msg":"",
     *     "time":1654754979434
     * }
     * @param code 行政编码
     * @return
     */
    @PostMapping("/system/area/getCenterLonLat")
    Result<SysArea> getCenterLonLat(@RequestParam("code") String code);


    /**
     * 根据区县获取省市区县街道
     * @apiNote 根据区县获取省市区县街道
     * @apiVersion 6.61
     * @tag 六、基础功能能力
     * @param regions
     * @return Result<List<SysAreaVO>>
     * @requestDemo
     * "110107"
     * @responseDemo
     * cityCode 市编码
     * provinceCode 省编码
     * regionCode 区县编码
     * streetCodes 街道编码列表
     * {
     *     "code": 200,
     *     "data": [
     *         {
     *             "cityCode": "110100",
     *             "provinceCode": "110000",
     *             "regionCode": "110107",
     *             "streetCodes": [
     *                 "110107001000",
     *                 "110107002000",
     *                 "110107003000",
     *                 "110107004000",
     *                 "110107005000",
     *                 "110107006000",
     *                 "110107009000",
     *                 "110107010000",
     *                 "110107011000"
     *             ]
     *         }
     *     ],
     *     "msg": "请求成功",
     *     "time": 1747292129455
     * }
     */
    @PostMapping("/system/business/getAreaStreet")
    Result<List<SysAreaVO>> getAreaStreet(@RequestBody String regions);

    /**
     * 根据用户名获取白名单信息
     * @apiNote 根据用户名获取白名单信息
     * @apiVersion 6.62
     * @tag 六、基础功能能力
     * @param userName
     * @return Result<SysUserWhiteListVO>
     * @responseDemo
     * aes电话号码
     * nickName 昵称
     * phoneNumberDesensitization 电话号码
     * orderBy 排序
     * source 来源
     * tenantId 租户id
     * tenantName 租户名称
     * userName 用户名
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"aesPhoneNumber": "395a7dbbe34c7c7be131d7b613140a73",
     * 		"nickName": "浩鲸",
     * 		"orderBy": "",
     * 		"phoneNumberDesensitization": "131****8010",
     * 		"source": "1",
     * 		"tenantId": "121062646",
     * 		"tenantName": "河北广电信息网络集团股份有限公司宁晋分公司_备电",
     * 		"userName": "f5mhZJLUBh9mBf7l2iRMcA=="
     * 	    },
     * 	"msg": "请求成功",
     * 	"time": 1737082243792
     * }
     */
    @GetMapping("/system/whiteList/getUser")
    public Result<SysUserWhiteListVO> getUserWhite(@RequestParam("userName") String userName);


    /**
     * 查询设备范围
     * @apiNote 查询设备范围
     * @apiVersion 6.63
     * @tag 六、基础功能能力
     * @param deptId
     * @return Result<SysDeptDetailVO>
     *  参数
     *  alarmTypeList 报警类型列表
     *  cameras 摄像头列表
     *  deptStreets 街道列表
     *  horns 报警主机列表
     *  lERs 执法记录仪列表
     *  radars 雷达列表
     *  satellites 卫星列表
     *  sensors 传感器列表
     *  uavs 无人机列表
     *
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"alarmTypeList": [{"deptId":"120046990-0134","typeValue":"WLGJ-YLGJ","warningSource":"8"}],
     * 		"cameras": [],
     * 		"deptStreets": [],
     * 		"horns": [],
     * 		"lERs": [],
     * 		"radars": [],
     * 		"satellites": [],
     * 		"sensors": [],
     * 		"uavs": []    * 	},
     * 	"msg": "请求成功",
     * 	"time": 1737081946769
     * }
     */
    @GetMapping("/system/dept/deptDeviceDetail/{deptId}")
    public Result<SysDeptDetailVO> getDeptDeviceDetail(@PathVariable("deptId") String deptId);

    /**
     * 批量查询组织权限 for 微创
     * @param list
     * @return
     */
    @PostMapping("/system/dept/deptDeviceDetailBatch")
    public Result<SysDeptDetailVO> deptDeviceDetailBatch(@RequestBody List<String> list) ;

    /**
     * 上传文件
     * @apiNote 上传文件
     * @apiVersion 6.64
     * @tag 六、基础功能能力
     * @requestDemo
     * file: 文件
     *
     * @responseDemo
     *
     * fileId 文件id
     * fileName 文件名称
     * fileSize 文件大小
     * fileSuffix 文件后缀
     * fileUrl 文件url
     * originUrl 文件原始url
     * {
     *     "msg": "操作成功",
     *     "code": 200,
     *     "data": {
     *         "fileId": "48c51e3f1967445ea0214b0399926fdf",
     *         "fileName": "xxxxx.jpg",
     *         "fileSize": "245529",
     *         "fileSuffix": ".jpg",
     *         "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/403017a7e91aeb49c2a90fb8273936d5/ada8fdd55c304f538f361824fa398547.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1736818367&Signature=ulLcdAdpBVAOoNmA0L84rE3bdOY%3D",
     *         "orderBy": "",
     *         "originUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/403017a7e91aeb49c2a90fb8273936d5/ada8fdd55c304f538f361824fa398547.jpg"
     *     }
     * }
     * @return AjaxResult
     */
    @PostMapping(path = "/base/common/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadFile(@RequestPart("file") MultipartFile file);

    /**
     * 组织树
     * @apiNote 组织树
     * @apiVersion 6.65
     * @tag 六、基础功能能力
     * @return Result<List<SysDeptAreaVO>>
     * @responseDemo
     * areaName 区划名称
     * id 区划id
     * pid 父区划id
     * children 下级区划列表
     * {
     *   "code": 200,
     *   "data": [
     *     {
     *       "areaName": "北京市",
     *       "children": [
     *         {
     *           "areaName": "北京市",
     *           "children": [
     *             {
     *               "areaName": "东城区",
     *               "id": "110101",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "西城区",
     *               "id": "110102",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "朝阳区",
     *               "id": "110105",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "丰台区",
     *               "id": "110106",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "石景山区",
     *               "id": "110107",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "海淀区",
     *               "id": "110108",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "门头沟区",
     *               "id": "110109",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "房山区",
     *               "id": "110111",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "通州区",
     *               "id": "110112",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "顺义区",
     *               "id": "110113",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "昌平区",
     *               "id": "110114",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "大兴区",
     *               "id": "110115",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "怀柔区",
     *               "id": "110116",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "平谷区",
     *               "id": "110117",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "密云区",
     *               "id": "110118",
     *               "pid": "110100"
     *             },
     *             {
     *               "areaName": "延庆区",
     *               "id": "110119",
     *               "pid": "110100"
     *             }
     *           ],
     *           "id": "110100",
     *           "pid": "110000"
     *         }
     *       ],
     *       "id": "110000",
     *       "pid": "100000"
     *     }
     *   ],
     *   "msg": "请求成功",
     *   "time": 1747292261303
     * }
     */
    @GetMapping("/system/dept/getUserDeptTreeselect")
    Result<List<SysDeptAreaVO>> getUserDeptTreeselect();

    /**
     * 查询设备
     * @apiNote 查询设备
     * @apiVersion 6.66
     * @tag 六、基础功能能力
     * @param request
     * @return Result<List<SysUser>>
     * @responseDemo
     * 管理员
     * 部门id
     * 电话号码
     * 用户id
     *     {
     * 	"code": 200,
     * 	"data": [{
     * 		"admin": false,
     * 		"adminNew": false,
     * 		"deptId": "125014708-01",
     * 		"deptIds": ["125014708-01"],
     * 		"orderBy": "",
     * 		"phonenumber": "13818522616",
     * 		"userId": "b52e33061bba42a19c9cf41c756b6393"
     * 	    }, {
     * 		"admin": false,
     * 		"adminNew": false,
     * 		"deptId": "125014708-01",
     * 		"deptIds": ["125014708-01"],
     * 		"orderBy": "",
     * 		"phonenumber": "13818522616",
     * 		"userId": "b86cec1136764737af01b807353b0f56"
     *    }],
     * 	"msg": "请求成功",
     * 	"time": 1737084275992
     * }
     */
    @PostMapping(path = USER_PREFIX + "/getUserByDeptAndDeviceScope")
    Result<List<SysUser>> getUserByDeptAndDeviceScope(@RequestBody GetUserByDeptAndDeviceScopeRequest request);

    /**
     * 根据区划名称查询信息
     * @apiNote 根据区划名称查询信息
     * @apiVersion 6.67
     * @tag 六、基础功能能力
     * @param areaNames
     * @return Result<List<SysArea>>
     */
    @PostMapping("/system/area/selectByAreaName")
    Result<List<SysArea>> selectByAreaName(@RequestBody List<String> areaNames);

    /**
     * 查询线下区域
     * @apiNote 查询线下区域
     * @apiVersion 6.68
     * @tag 六、基础功能能力
     * @requestDemo
     * areaCodes: 区划编码列表
     * @responseDemo
     * areaCode 区划编码
     * areaName 区划名称
     * areaLevel 区划层级 1=国家级,2=省份级,3=地市级,4=区县级
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"110000": {
     * 			"areaCode": "110000",
     * 			"areaLevel": "2",
     * 			"areaName": "北京市",
     * 			"areaParentCode": "100000",
     * 			"areaParentName": "中国",
     * 			"children": [],
     * 			"id": 0,
     * 			"latitude": "39.90277565",
     * 			"longitude": "116.4011447",
     * 			"orderBy": "",
     * 			"status": "0"
     *                },
     * 		"130000": {
     * 			"areaCode": "130000",
     * 			"areaLevel": "2",
     * 			"areaName": "河北省",
     * 			"areaParentCode": "100000",
     * 			"areaParentName": "中国",
     * 			"children": [],
     * 			"id": 0,
     * 			"latitude": "38.03713012",
     * 			"longitude": "114.5244857",
     * 			"orderBy": "",
     * 			"status": "0"
     *        }    * 	},
     * 	"msg": "请求成功",
     * 	"time": 1747298792224
     * }
     * @param areaCodes
     * @return Result<Map<String, String>>
     */
    @PostMapping("/system/area/getAreaNameCaffeineMap")
    Result<Map<String, String>> getAreaNameCaffeineMap(@RequestBody  List<String> areaCodes);

    /**
     * 强制退出用户
     * @apiNote 强制退出用户
     * @apiVersion 6.69
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"userId": "1234567",
     * 	"userName": "xiaodao111",
     * }
     * @param userId 用户id
     * @param userName 用户账号
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1737084275992
     * }
     * @return Result<Map<String, String>>
     */
    @PostMapping("/system/user/forceLogout")
    Result forceLogout(@RequestParam String userId,@RequestParam String userName);

    /**
     * 获取租户所有用户(租户+行业)
     * @apiNote 获取租户所有用户(租户+行业)
     * @apiVersion 6.70
     * @tag 六、基础功能能力
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：租户下用户信息总量
     * 		nickName：昵称
     * 		userName：账号
     * 		userId：用户id
     * {
     *     "code":200,
     *     "data":[
     *         {
     *             "nickName":"qq",
     *             "userId":"632d85106aef42d2a2873926b62b27c5",
     *             "userName":"qqadmin"
     *         },
     *         {
     *             "nickName":"qq",
     *             "userId":"f6901946119b43ad816f6d4af3514f33",
     *             "userName":"qqly01"
     *         }
     *     ],
     *     "msg":"",
     *     "time":1686642511314
     * }
     * @return
     */
    @GetMapping(path = "/system/user/getTenantUsers")
    Result<List<SysUser>> getTenantUsers();

    @GetMapping(path = "/system/user/getTenantAllUsers")
    Result<List<SysUser>> getTenantAllUsers();

    /**
     * 删除角色大屏关系(暴露给浩鲸调用)
     * @apiNote 删除角色大屏关系(暴露给浩鲸调用)，返回Integer类型
     * @apiVersion 6.71
     * @tag 六、基础功能能力
     * @param sysRoleScreenVOList
     * @requestDemo
     * [
     *        {
     * 		"industryViewId": "33",
     * 		"industryViewType": 1
     *    }
     * ]
     * @responseDemo
     * code：返回编码
     * msg：返回信息
     * time：时间
     * data：租户下用户信息总量
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1699501482707
     * }
     */
    @PostMapping(path = "/system/role/delRoleScreenRef")
    Result<Integer> delRoleScreenRef(@RequestBody List<SysRoleScreenVO> sysRoleScreenVOList);


    /**
     * 查询角色大屏关系接口
     * @apiNote 查询角色大屏关系接口
     * @apiVersion 6.72
     * @tag 六、基础功能能力
     * @param sysRoleScreenVO
     * @requestDemo
     * {
     * 	"roleId": "",
     * 	"industryViewId": "33",
     * 	"industryViewType": 1
     * }
     * @responseDemo
     * code：返回编码
     * msg：返回信息
     * time：时间
     * data：租户下用户信息总量
     * industryViewId 大屏id
     * industryViewType 大屏类型 0-应用大屏 1-第三方大屏
     * roleId 角色id
     * {
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"displayType": 1,
     * 			"industryViewId": "33",
     * 			"industryViewName": "小刀第三方大屏",
     * 			"industryViewType": 1,
     * 			"roleId": 531603081142569352,
     * 			"sort": 0
     *        }
     * 	],
     * 	"msg": "请求成功",
     * 	"time": 1699498115954
     * }
     */
    @PostMapping(path = "/system/role/queryRoleScreenRef")
    Result<List<SysRoleScreenVO>> queryRoleScreenRef(@RequestBody SysRoleScreenVO sysRoleScreenVO);


    /**
     * 获取当前用户大屏列表
     * @apiNote 获取当前用户大屏列表
     * @apiVersion 6.73
     * @tag 六、基础功能能力
     * @param sysRoleScreenVO
     * @requestDemo
     * {
     * 	"appVerCode": "1.0",
     * 	"industryCode": "000000",
     * 	"tenantId": "121062461",
     * 	"userName": "qytyhx1"
     * }
     * @responseDemo
     * code：返回编码
     * msg：返回信息
     * time：时间
     * data：返回数据
     * personalFlag 是否个性化配置标识 1配了个性化 0没配个性化
     * screenMenuConfVOList 个性化大屏菜单配置列表
     *      attr 大屏属性：1.菜单 2.分组
     *      groupId 分组id(前端生成)
     *      industryViewId 大屏id
     *      industryViewType 大屏类型 0-应用大屏 1-第三方大屏
     *      tabName tab名称
     *      viewType 大屏类型 0-应用大屏 1-第三方大屏
     * {
     * 	"code": 200,
     * 	"data": {
     * 		"personalFlag": "1",
     * 		"screenMenuConfVOList": [
     *                        {
     * 				"attr": "2",
     * 				"groupId": "1",
     * 				"industryViewId": "1279",
     * 				"industryViewType": "0",
     * 				"tabName": "小刀大屏",
     * 				"viewType": "1"
     *            },
     *            {
     * 				"attr": "2",
     * 				"groupId": "1",
     * 				"industryViewId": "146",
     * 				"industryViewType": "0",
     * 				"tabName": "小刀大屏",
     * 				"viewType": "1"
     *            },
     *            {
     * 				"attr": "1",
     * 				"groupId": "2",
     * 				"industryViewId": "402",
     * 				"industryViewType": "0",
     * 				"tabName": "大刀大屏",
     * 				"viewType": "1"
     *            }
     * 		]
     * 	},
     * 	"msg": "请求成功",
     * 	"time": 1719457641828
     * }
     */
    @PostMapping(path = "/system/role/getCurrentRoleScreen")
    Result<SysRoleScreenVO> getCurrentRoleScreen(@RequestBody SysRoleScreenVO sysRoleScreenVO);


    /**
      *  获取当前用户组织列表
      * @apiNote 获取当前用户组织列表
      * @apiVersion  6.74
      * @tag   六、基础功能能力
      * @requestDemo {
     *   "orgSource": "1",
     *   "orgType": "1",
     *   "tenantId": "122035797",
     *   "industryCode": "000000"
     * }
     * @responseDemo
     * deptId 组织id
     * deptLevel 组织级别
     * deptName 组织名称
     * industryCode 行业编码
     * parentId 父组织id
     * tenantId 租户id
     * tenantName 租户名称
     * tenantTowerDeptId 租户组织归属铁塔组织ID
     * status 组织状态:0正常,1停用
     * {
     *   "code": 200,
     *   "data": [
     *     {
     *       "ancestors": "000000",
     *       "appVerCode": "1.0",
     *       "children": [
     *         {
     *           "ancestors": "000000,122035797-02",
     *           "appVerCode": "1.0",
     *           "children": [],
     *           "city": "122035797-0201",
     *           "createBy": "shanyw666",
     *           "createTime": "2023-08-07 09:16:03",
     *           "delFlag": "0",
     *           "deptId": "122035797-0201",
     *           "deptLevel": "2",
     *           "deptName": "吃个桃桃~好凉凉",
     *           "id": 141042,
     *           "industryCode": "000000",
     *           "parentId": "122035797-02",
     *           "province": "122035797-02",
     *           "status": "0",
     *           "tenantId": "122035797",
     *           "tenantName": "测试客户zbz",
     *           "tenantTowerDeptId": "431200"
     *         },
     *         {
     *           "ancestors": "000000,122035797-02",
     *           "appVerCode": "1.0",
     *           "children": [],
     *           "city": "122035797-0202",
     *           "createBy": "tynew02",
     *           "createTime": "2023-08-31 15:29:14",
     *           "delFlag": "0",
     *           "deptId": "122035797-0202",
     *           "deptLevel": "2",
     *           "deptName": "测试",
     *           "id": 141058,
     *           "industryCode": "000000",
     *           "parentId": "122035797-02",
     *           "province": "122035797-02",
     *           "status": "0",
     *           "tenantId": "122035797",
     *           "tenantName": "测试客户zbz",
     *           "tenantTowerDeptId": "431200"
     *         },
     *         {
     *           "ancestors": "000000,122035797-02",
     *           "appVerCode": "1.0",
     *           "children": [],
     *           "city": "122035797-0203",
     *           "createBy": "tynew02",
     *           "createTime": "2023-09-09 18:24:17",
     *           "delFlag": "0",
     *           "deptId": "122035797-0203",
     *           "deptLevel": "2",
     *           "deptName": "测试1",
     *           "id": 141073,
     *           "industryCode": "000000",
     *           "parentId": "122035797-02",
     *           "province": "122035797-02",
     *           "status": "0",
     *           "tenantId": "122035797",
     *           "tenantName": "测试客户zbz",
     *           "tenantTowerDeptId": "431200"
     *         },
     *         {
     *           "ancestors": "000000,122035797-02",
     *           "appVerCode": "1.0",
     *           "children": [],
     *           "city": "122035797-0204",
     *           "createBy": "tynew02",
     *           "createTime": "2023-11-14 10:28:16",
     *           "delFlag": "0",
     *           "deptId": "122035797-0204",
     *           "deptLevel": "2",
     *           "deptName": "一省多市",
     *           "id": 141155,
     *           "industryCode": "000000",
     *           "parentId": "122035797-02",
     *           "province": "122035797-02",
     *           "status": "0",
     *           "tenantId": "122035797",
     *           "tenantName": "测试客户zbz",
     *           "tenantTowerDeptId": "431200"
     *         }
     *       ],
     *       "city": "122035797-02",
     *       "createBy": "tynew02",
     *       "createTime": "2023-07-20 11:29:26",
     *       "delFlag": "0",
     *       "deptId": "122035797-02",
     *       "deptLevel": "1",
     *       "deptName": "测试客户zbz-通用平台版本-1.0",
     *       "id": 141011,
     *       "industryCode": "000000",
     *       "parentId": "000000",
     *       "province": "000000",
     *       "status": "0",
     *       "tenantId": "122035797",
     *       "tenantName": "测试客户zbz",
     *       "tenantTowerDeptId": "431200"
     *     }
     *   ],
     *   "msg": "请求成功",
     *   "time": 1701134823964
     * }
     */
    @PostMapping(value = "/system/tenantDept/listSubsetDept")
    Result<List<SysDept>> listSubsetDept(@RequestBody QuTenantDeptSubsetRequest quTenantDeptSubsetRequest);

    /**
     * 批量增加常用语
     * @apiNote 批量增加常用语
     * @apiVersion 6.75
     * @tag 六、基础功能能力
     * @requestDemo
     * [{
     * 	"wordsId": "222222",
     * 	"userId":"47610796b99b4523981a351f0e401044",
     * 	"typeId":"11",
     * 	"content": "44ssds是大V",
     * 	"tenantId":"122035807",
     * 	"industryCode":"100000",
     * 	"appVerCode": "1.0"
     * }]
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     *
     * {
     * 	"code": 200,
     * 	"msg": "批量新增常用语成功!",
     * 	"time": 1705457634017
     * }
     * @param commonWordsDTOList
     * @return
     */
    @PostMapping("/system/commonWords/addCommonWordsBatch")
    Result addCommonWordsBatch(@RequestBody List<wordsRequest> commonWordsDTOList);

    /**
     * 巡查任务常用语查询
     * @apiNote 巡查任务常用语查询
     * @apiVersion 6.76
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"typeId": "9"
     * }
     * @responseDemo
     * code: 状态码
     * msg: 返回消息
     * time: 时间戳
     * data: [
     *     {
     *         wordsId: 关键字主键
     *         userId: 用户主键
     *         typeId: 类型主键
     *         content: 内容
     *         createDate: 创建日期
     *         tenantId: 租户主键
     *         appVerCode: 版本编码
     *         initWordsFlag: 初始化常用语标识
     *         updateTime: 修改事件
     *         industryCode: 行业编码
     *     }
     * ]
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1717656754514,
     * 	"data": [
     *        {
     * 			"wordsId": "6656853376329961899",
     * 			"userId": null,
     * 			"typeId": "9",
     * 			"content": "巡查发现异常情况，请协助处理！",
     * 			"createDate": null,
     * 			"tenantId": null,
     * 			"appVerCode": null,
     * 			"initWordsFlag": "3",
     * 			"updateTime": null,
     * 			"industryCode": null
     *        }
     * 	]
     * }
     * @param wordsVO
     * @return
     */
    @PostMapping("/system/commonWords/getCommonWordsForInspection")
    Result<List<VmCommonWordsVO>> getCommonWordsForInspection(@RequestBody wordsRequest wordsVO);

    /**
     * 编辑保存巡查任务常用语
     * @apiNote 编辑保存巡查任务常用语
     * @apiVersion 6.77
     * @tag 六、基础功能能力
     * @requestDemo
     * [
     *        {
     * 		"content": "巡查任务未发现异常情况。（小刀已改）",
     * 		"initWordsFlag": "1",
     * 		"typeId": "9",
     * 		"wordsId": "6656853376329961897"
     *    },
     *    {
     * 		"content": "巡查发现异常情况，已处理。",
     * 		"initWordsFlag": "2",
     * 		"typeId": "9",
     * 		"wordsId": "6656853376329961898"
     *    },
     *    {
     * 		"content": "巡查发现异常情况，请协助处理！",
     * 		"initWordsFlag": "3",
     * 		"typeId": "9",
     * 		"wordsId": "6656853376329961899"
     *    },
     *    {
     * 		"content": "巡查发现异常情况，已触发告警。",
     * 		"initWordsFlag": "4",
     * 		"typeId": "9",
     * 		"wordsId": "6656853376329961900"
     *    },
     *    {
     * 		"content": "巡查发现异常情况，已上传附件图片/视频。",
     * 		"initWordsFlag": "5",
     * 		"typeId": "9",
     * 		"wordsId": "6656853376329961901"
     *    }
     * ]
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     *
     * {
     * 	"code": 200,
     * 	"msg": "常用语修改成功！",
     * 	"time": 1717657339716
     * }
     * @param commonWordsDTOList
     * @return
     */
    @PostMapping("/system/commonWords/updateProcessCommonWordsForInspection")
    Result updateProcessCommonWordsForInspection(@RequestBody List<wordsRequest> commonWordsDTOList);

    /**
     * 推送4a用户（客户）
     * @apiNote 推送4a用户（客户）
     * @apiVersion 6.78
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *   "flag": "1",
     * 	"roleIds": [4,5,8],
     * 	"userName": "xiaodaotest001",
     * 	"nickName": "小刀园区测试",
     * 	"email": "xiaodao@qq.com",
     * 	"phonenumber": "18525356006",
     * 	"sex": "0",
     * 	"password": "1qaz!QAZ",
     * 	"deptId": "122035807-01",
     * 	"tenantId": "122035807",
     * 	"telPhone": "18525356006"
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "推送成功",
     * 	"time": 1719825660457
     * }
     * @param sysUser4A
     * @return
     */
    @PostMapping("/system/user/userPush4A")
    Result<String> userPush4A(@RequestBody SysUser4A sysUser4A);


    //根据租户查询管理区域
    @GetMapping("/system/area/queryManagementAreaTreeByTenant")
    Result queryManagementAreaTreeByTenant(@RequestParam("tenantId") String tenantId);

    /**
     * 获取管理区域列表(树形) 提供给app
     */
    @GetMapping("/system/dept/getDeptStreetTree/{deptId}")
    Result<List<SysDeptDeviceTreeVO>> getDeptStreetTree(@PathVariable("deptId") String deptId);

    @ApiOperation(value = "获取租户用户列表")
    @GetMapping("/system/user/list")
    TableDataInfo list(SysUser user);



    /**
     * 第三方告警类型同步新增/删除
     * @apiNote 第三方告警类型同步新增/删除
     * @apiVersion 6.79
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     *     "tenantld": "",
     *     "industryCode": "",
     *     "appVerCode": "",
     *     "addAlarmTypeList": [
     *         "alarmType1",
     *         "alarmType2"
     *     ],
     *     "deleteAlarmTypeList": [
     *         "alarmType1",
     *         "alarmType2"
     *     ]
     * }
     * @responseDemo
     *{
     * "code": 200,
     * "msg": "请求成功",
     * "time": 1737100136542
     * }
     * @param vo
     * @return
     */
    @PostMapping("/alarmTypeSync/thirdPartyAlarmSync")
    Result<String> thirdPartyAlarmSync(@Validated @RequestBody ThirdPartyAlarmTypeVO vo);



    /**
     * 获取单点登录列表
     * @apiNote 获取单点登录列表
     * @apiVersion 6.97
     * @tag 六、基础功能能力
     * @requestDemo
     * {"tenantId":"","createBy":"","updateBy":"","pageNum":1,"pageSize":10,"prop":"","order":""}
     * @responseDemo
     *{
     *     "code": 200,
     *     "rows": [
     *         {
     *             "address": "数字孪生平台获取token",
     *             "appkey": "N0R9sTOXilZAtIoaGRserGE7xIvD9sV31BwZ2meyIfk=",
     *             "createBy": "fanzw502",
     *             "createTime": "2025-03-21 11:52:35",
     *             "secretkey": "m4AESMuJRPQ2P5EmRpk7vmvGrY/+tKi0NeLrklKYrp8YIKV9sehVxpSQlzOlmDdkQELI0Sp3uyfCB/v0rtqsDQ==",
     *             "ssoId": "1bbb4d4e61254516a15a08188256dba1",
     *             "sysSsoUser": [
     *                 {
     *                     "createTime": "2025-03-21 11:52:35",
     *                     "nickName": "亚信",
     *                     "ssoId": "1bbb4d4e61254516a15a08188256dba1",
     *                     "userId": "99740",
     *                     "userName": "gulei502"
     *                 }
     *             ],
     *             "tenantId": "121062444",
     *             "tenantName": "衡阳市大雁地理信息有限公司"
     *         },
     *         {
     *             "address": "1",
     *             "appkey": "pB05O3N/oxYvpQI3k2nvqieWOliNJ0v8u0RJv2faMIM=",
     *             "createBy": "fxy_777",
     *             "createTime": "2025-02-24 15:37:40",
     *             "secretkey": "7XlW242WtS+qGr1DmfMWJon10tiGbXlSzh4BJBmZiMnHq5VYHf0AJnZNdtO+002cNG0H1uH/yOjwZcEWs32XgQ==",
     *             "ssoId": "c02b3964a6884b34b0344766009b025f",
     *             "sysSsoUser": [
     *                 {
     *                     "createTime": "2025-02-24 15:37:40",
     *                     "nickName": "孙欣",
     *                     "ssoId": "c02b3964a6884b34b0344766009b025f",
     *                     "userId": "99738",
     *                     "userName": "sunx857"
     *                 }
     *             ],
     *             "tenantId": "122085338",
     *             "tenantName": "20220812验证测试"
     *         }
     *     ],
     *     "total": 34
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/sso/list")
    TableDataInfo getSsoList(@RequestBody SysSso vo);


    /**
     * 根据id获取单点登录详细信息
     * @apiNote 根据id获取单点登录详细信息
     * @apiVersion 6.98
     * @tag 六、基础功能能力
     * @requestDemo
     *{
     * 	"ssoId": "1bbb4d4e61254516a15a08188256dba1"
     * }
     * @responseDemo
     *{
     * 	"msg": "操作成功",
     * 	"code": 200,
     * 	"data": {
     * 		"address": "数字孪生平台获取token",
     * 		"appkey": "ASSG8GHRLLE13A1GZ1Q329USU4NZ2H",
     * 		"createBy": "fanzw502",
     * 		"createTime": "2025-03-21 11:52:35",
     * 		"secretkey": "G8QBSP5T93ITBEB8EC9YQ4Y9WY1URQGEJKO44X7N9VWW26R5DM",
     * 		"ssoId": "1bbb4d4e61254516a15a08188256dba1",
     * 		"sysSsoUser": [
     *                        {
     * 				"nickName": "亚信",
     * 				"ssoId": "1bbb4d4e61254516a15a08188256dba1",
     * 				"userId": "99740",
     * 				"userName": "gulei502"
     *            }
     * 		],
     * 		"tenantId": "121062444",
     * 		"tenantName": "衡阳市大雁地理信息有限公司"
     * 	}
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/sso/detail")
    AjaxResult getSsoInfo(@RequestBody SysSso vo);


    /**
     * 新增单点登录配置
     * @apiNote 新增单点登录配置
     * @apiVersion 6.99
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"tenantId": "122045810",
     * 	"tenantName": "林业测试",
     * 	"address": "1",
     * 	"appkey": "Fv/JqQidi3C4UmTORLnmfNA/3LhuirKQlDBXL0d9i2M=",
     * 	"secretkey": "KgqOm5iVf0xX2TU7cag3/DgaLe8rSDSZ1PlKW23sm4eUTuv6mB6B7xD8KrYv3jVetg5n665Lyz4Q6cmqyML3+Q==",
     * 	"sysSsoUser": [{
     * 		"userName": "lycs",
     * 		"nickName": "lycs"
     *        }]
     * }
     * @responseDemo
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1764294998596
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/sso/add")
    Result addSsoInfo(@RequestBody SysSso vo);


    /**
     * 更新单点登录配置
     * @apiNote 更新单点登录配置
     * @apiVersion 6.100
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"ssoId": "1ae3a529bbd7439cbe5c626461543e0d",
     * 	"tenantId": "122045810",
     * 	"tenantName": "林业测试",
     * 	"address": "1",
     * 	"sysSsoUser": [{
     * 		"userName": "lycs",
     * 		"nickName": "lycs"
     *        }]
     * }
     * @responseDemo
     *{
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1764295449296
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/sso/update")
    Result editSsoInfo(@RequestBody SysSso vo);


    /**
     * 删除单点登录配置
     * @apiNote 删除单点登录配置
     * @apiVersion 6.101
     * @tag 六、基础功能能力
     * @requestDemo
     * {"ssoId":"1ae3a529bbd7439cbe5c626461543e0d"}
     * @responseDemo
     *{
     * "code": 200,
     * "msg": "请求成功",
     * "time": 1737100136542
     * }
     * @param vo
     * @return
     */
    @PostMapping("/system/sso/del")
    Result delSsoInfo(@RequestBody SysSso vo);


    /**
     * 获取告警类型map
     * @apiNote 获取告警类型map
     * @apiVersion 6.102
     * @tag 六、基础功能能力
     * @requestDemo
     * {}
     * @responseDemo
     *{
     * 	"code": 200,
     * 	"data": {
     * 		"1717134": [
     * 			"9212118",
     * 			"9717122"
     * 		],
     * 		"1717135": [
     * 			"8212102",
     * 			"8212127"
     * 		]
     *        },
     * 	"msg": "请求成功",
     * 	"time": 1766992715124
     * }
     */
    @PostMapping("/system/tenantDept/getAlarmTypeMap")
    Result<Map<String, Set<String>>> getAlarmTypeMap();


}
