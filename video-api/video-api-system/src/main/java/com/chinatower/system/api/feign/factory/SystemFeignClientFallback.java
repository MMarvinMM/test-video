package com.chinatower.system.api.feign.factory;

import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SystemFeignClientFallback implements SystemFeignClient {


    @Setter
    private Throwable cause;

    private static final String ERROR_MSG = "查询租户列表失败";

    private static final String GET_ALL_USER_FAIL_MSG = "获取租户所有用户失败";
    /**
     * 查询租户列表
     *
     * @param quTenantInfoRequest
     * @return Result
     */
    @Override
    public Result<Map<String, Object>> getTenantInfoList(QuTenantInfoRequest quTenantInfoRequest) {
        log.error("调用video-system服务查询租户列表失败:{}", quTenantInfoRequest.toString());
        return Result.fail(ERROR_MSG);
    }

//    @Override
//    public Result<UserAuthData> getUserAuthData() {
//        log.error("调用getUserAuthData失败!");
//        return Result.fail("调用getUserAuthData失败");
//    }

    /**
     * 新增租户
     *
     * @param addTenantInfoRequest 入参
     * @return Result
     */
    @Override
    public Result addTenantInfo(AddTenantInfoRequest addTenantInfoRequest) {
        log.error("调用video-system服务新增租户失败:{}", addTenantInfoRequest.toString());
        return Result.fail("新增租户失败");
    }

    @Override
    public Result<Integer> addUser(AddUserRequest addUserRequest) {
        log.error("调用video-system服务新增用户失败:{}", addUserRequest.toString());
        return Result.fail("新增用户失败");
    }

    /**
     * 修改用户,视联使用
     *
     * @param request
     * @return
     */
    @Override
    public Result<Integer> updateVideoUser(VideoUpdateUserRequest request) {
        log.error("修改用户接口, 视联调用异常：服务降级:{}", cause);
        log.error("服务名：video-system，接口地址：/updateVideoUser，参数：{}，调用失败！", request.toString());
        return Result.fail("修改用户失败");
    }

    @Override
    public Result<List<DeptUserGroupVO>> getDeptUserTreeList(SysUser sysUserNew) {
        log.error("调用video-system获取权限范围内的部门用户树:{}", sysUserNew.toString());
        return Result.fail("获取权限范围内的部门用户树失败");
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType
     * @return
     */
    @Override
    public Result<List<SysDictData>> getDictDataByType(String dictType) {
        log.error("调用video-system服务根据字典类型查询字典数据信息失败:{}", dictType);
        return Result.fail("根据字典类型查询字典数据信息失败");
    }

    @Override
    public Result<List<SysDictData>> getDictDataByTypeSdk(String dictType) {
        log.error("调用video-system服务根据字典类型查询字典数据信息sdk失败:{}", dictType);
        return Result.fail("根据字典类型查询字典数据信息sdk失败");
    }

    /**
     * 新增租户组织树根节点
     *
     * @param createTenantRootDeptRequest 创建租户组织树根节点入参
     * @return 结果
     */
    @Override
    public Result addTenantRootDept(CreateTenantRootDeptRequest createTenantRootDeptRequest) {
        log.error("调用video-system服务新增租户组织树根节点失败:{}", createTenantRootDeptRequest.toString());
        return Result.fail("新增租户组织树根节点失败");
    }

    /**
     * 查询角色列表
     *
     * @param queryRoleRequest
     * @return
     */
    @Override
    public Result<List<SysRoleResponse>> getRoleList(QueryRoleRequest queryRoleRequest) {
        log.error("调用video-system服务查询角色列表失败:{}", queryRoleRequest.toString());
        return Result.fail("查询角色列表失败");
    }

    /**
     * 获取租户部门列表(顶级结构)
     *
     * @param quTenantDeptTopRequest
     * @return
     */
    @Override
    public Result<Map<String, Object>> selectTenantTop(QuTenantDeptTopRequest quTenantDeptTopRequest) {
        log.error("调用video-system服务获取租户部门列表(顶级结构)失败:{}", quTenantDeptTopRequest.toString());
        return Result.fail("获取租户部门列表(顶级结构)失败");
    }

    /**
     * 查询租户部门子列表(树形)
     *
     * @param quTenantDeptSubsetRequest
     * @return
     */
    @Override
    public Result<List<SysDeptInfoResponse>> listTenantSubsetDept(QuTenantDeptSubsetRequest quTenantDeptSubsetRequest) {
        log.error("调用video-system服务查询租户部门子列表(树形)失败:{}", quTenantDeptSubsetRequest.toString());
        return Result.fail("查询租户部门子列表(树形)失败");
    }

    @Override
    public Result addRole(SysRole SysRole) {
        log.error("调用video-system服务添加角色失败:{}", SysRole.toString());
        return Result.fail("添加角色失败");
    }

    @Override
    public Result<List<SysAreaResponse>> queryAllAreaCode() {
        log.error("调用video-system服务查询行政区域失败:{}", "");
        return Result.fail("查询行政区域失败");
    }

    @Override
    public Result remove(String deptId) {
        log.error("调用video-system服务删除组织失败:{}", deptId);
        return Result.fail("删除组织失败");
    }


    @Override
    public Result<List<SysUserResponse>> selectUserByTenantId(String tenantId) {
        log.error("调用video-system服务根据租户id查询用户信息失败:{}", tenantId);
        return Result.fail(ERROR_MSG);
    }

    @Override
    public Result<List<SysUserTenantInfoResponse>> selectUserListByTenantId(Integer pageNum, Integer pageSize) {
        log.error("调用video-system服务根据pageNum {} 查询用户信息失败:{}", pageNum,pageSize);
        return Result.fail(ERROR_MSG);
    }

    @Override
    public Result<Long> selectUserListByTenantCount() {
        log.error("调用video-system服务根据租户id查询用户数量失败:{}", 0);
        return Result.fail();
    }

    @Override
    public Result<Integer> checkTenantId(String tenantId) {
        log.error("调用video-system服务查询租户编码是否存在失败:{}", tenantId);
        return Result.fail("查询租户编码是否存在失败");
    }

    @Override
    public Result<List<SysDictDataResponse>> dictDataType(String dictType) {
        log.error("调用system服务根据字典类型查询字典数据信息失败:{}", dictType);
        return Result.fail("根据字典类型查询字典数据信息失败");
    }

    @Override
    public Result<Map<String, List<TownShipDictData>>> dictType(String[] dictTypes) {
        log.error("调用system服务根据字典类型集合查询字典数据信息失败:{}", dictTypes);
        return Result.fail("根据字典类型集合查询字典数据信息失败");
    }

    @Override
    public Result<Map<String, List<TownShipDictData>>> dictTypeSdk(String[] dictTypes) {
        log.error("调用system服务根据字典类型集合查询字典数据信息失败:{}", dictTypes);
        return Result.fail("根据字典类型集合查询字典数据信息失败");
    }

    @Override
    public Result<List<SysTenantInfoResponse>> selectByTenantId(String tenantId) {
        log.error("根据租户Id查询租户信息失败:{}", tenantId);
        return Result.fail("根据租户Id查询租户信息失败");
    }

    @Override
    public AjaxResult getListByIds(SysUser user) {
        log.error("根据用户编码批量查询用户信息失败:{}", user.toString());
        return AjaxResult.error();
    }

    @Override
    public AjaxResult getListByUserNames(SysUser user) {
        log.error("根据用户账号批量查询用户信息失败:{}", user.getUserName());
        return AjaxResult.error();
    }

    @Override
    public Result<List<SysUser>> getUserList(SysUser user) {
        log.error("查询用户列表失败:{}", user.getTenantId());
        return Result.fail("查询用户列表失败");
    }

    @Override
    public Result<List<TreeSelectBySystem>> getTenantTree(SysUser sysUser) {
        log.error("查询组织人员树失败:{}", sysUser.getUserId());
        return Result.fail("查询组织人员树失败");
    }

    @Override
    public AjaxResult getInfo(String userId) {
        log.error("根据用户编号获取详细信息失败:{}", userId);
        return AjaxResult.error();
    }

    @Override
    public AjaxResult getConfigInfo(SysConfig config) {
        log.error("查询参数配置失败:{}", config.getConfigKey());
        return AjaxResult.error();
    }

    @Override
    public Result<List<DeviceTreeVO>> getPermissions(SysUser user) {
        log.error("获取登录账号数据权限失败:{}", user.getUserId());
        return Result.fail("获取登录账号数据权限失败");
    }

    @Override
    public Result getPermissionsForString(SysUser user) {
        log.error("获取登录账号数据权限(返回liststring)失败:{}", user.getUserId());
        return Result.fail("获取登录账号数据权限(返回liststring)失败");
    }

    @Override
    public Result<List<VmCommonWordsVO>> getCommonWords(wordsRequest words) {
        log.error("获取常用语失败:{}", words.getWordsId());
        return Result.fail("获取常用语失败");
    }

    @Override
    public Result<SysCapitalVO> getSysCapital(SysCapitalVO vo) {
        log.error("获取省会失败:{}", vo.getProvCode());
        return Result.fail("获取省会失败");
    }

    @Override
    public Result<List<SysDeptStreetRelVO>> getDeptStreetRel(SysDeptStreetRelVO vo) {
        log.error("组织和行政区域关联关系查询失败:{}", vo.getDeptId());
        return Result.fail("组织和行政区域关联关系查询失败");
    }

    @Override
    public Result insertDeptStreetRel(List<SysDeptStreetRelVO> list) {
        log.error("增加组织和行政区域关联关系失败:{}");
        return Result.fail("增加组织和行政区域关联关系失败");
    }

    @Override
    public Result deleteDeptStreetRel(SysDeptStreetRelVO vo) {
        log.error("删除组织和行政区域关联关系失败:{}", vo.getDeptId());
        return Result.fail("删除组织和行政区域关联关系失败");
    }

    @Override
    public Result addCommonWords(wordsRequest words) {
        log.error("增加常用语失败:{}", words.getTenantId());
        return Result.fail("增加常用语失败");
    }

    @Override
    public Result addCommonWordsBatch(List<wordsRequest> commonWordsDTOList) {
        log.error("批量增加常用语失败");
        return Result.fail("增加常用语失败");
    }

    @Override
    public Result<Integer> deleteCommonWordsByWordsId(wordsRequest words) {
        log.error("删除常用语失败:{}", words.getWordsId());
        return Result.fail("删除常用语失败");
    }

    @Override
    public Result<Integer> deleteCommonWordsByWordsIdBatch(wordsRequest words) {
        log.error("批量删除常用语失败:{}", words.getWordsId());
        return Result.fail("批量删除常用语失败");
    }

    @Override
    public Result<Integer> updateCommonWords(wordsRequest words) {
        log.error("修改常用语失败:{}", words.getWordsId());
        return Result.fail("修改常用语失败");
    }

    @Override
    public Result<String> updateCommonWordsBatch(wordsRequest words) {
        log.error("批量修改常用语失败:{}", words.getWordsId());
        return Result.fail("批量修改常用语失败");
    }

    @Override
    public Result<List<DeviceTreeVO>> getRegionTree(SysUser user) {
        log.error("查询登录账号权限范围内的行政区划树失败:{}", user.getUserId());
        return Result.fail("查询登录账号权限范围内的行政区划树失败");
    }

    @Override
    public Result<List<StreetTreeVO>> getAllStreetList(StreetTreeVO vo) {
        log.error("获取街道列表失败:{}", vo.getAreaCode());
        return Result.fail("获取街道列表失败");
    }


    @Override
    public Result<List<TreeSelect>> selectTreeSelect() {
        log.error("获取部门下拉树列表失败:{}", "");
        return Result.fail("获取部门下拉树列表失败");
    }

    @Override
    public TableDataInfo list(SysNoticeReq notice) {
        log.error("获取通知公告列表失败:{}", notice);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(400);
        return tableDataInfo;
    }

    @Override
    public TableDataInfo getList(SysNotice notice) {
        log.error("获取通知公告列表失败/分权限:{}", notice);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(400);
        return tableDataInfo;
    }

    @Override
    public AjaxResult getInfo(Long noticeId) {
        log.error("根据通知公告编号获取详细信息:{}", noticeId.toString());
        return AjaxResult.error();
    }

    @Override
    public Result<List<AreaDataVO>> getAreaByTenantId(SysUser user) {
        log.error("查询登录账号全部的行政区划树失败:{}", user.getTenantId());
        return Result.fail("查询登录账号全部的行政区划树失败");
    }

    @Override
    public Result<List<SysUser>> getPhoneByRegion(SysUser user) {
        log.error("根据行业编码获取区县人员的手机号码失败:{}", user.getUserId());
        return Result.fail("根据行业编码获取区县人员的手机号码失败");
    }

    @Override
    public Result<List<SysArea>> getRegion(SysArea sysArea) {
        log.error("获取行政区划信息失败:{}", sysArea.getAreaCode());
        return Result.fail("获取行政区划信息失败");
    }

    @Override
    public AjaxResult getConfValue(String dictType) {
        log.error("获取字典值异常：服务降级:{}", cause);
        log.error("服务名：chinatower-energy-exchange-web-admin，接口地址：/base/system/dict/data/dictType，参数：{}，调用失败！", dictType);
        return AjaxResult.error();
    }

    @Override
    public AjaxResult getConfValueSdk(String configKey) {
        return null;
    }

    /**
     * 查询租户树接口,视联调用
     *
     * @param quTenantDeptTopRequest
     * @return
     */
    @Override
    public Result<List<SysDeptResponse>> tenantTreeList2Video(QuTenantDeptTopRequest quTenantDeptTopRequest) {
        log.error("查询租户树接口,视联调用异常：服务降级:{}", cause);
        log.error("服务名：video-system，接口地址：/tenantTreeList2Video，参数：{}，调用失败！", quTenantDeptTopRequest.toString());
        return Result.fail("查询租户树接口失败");
    }

    @Override
    public Result<Map<String, Object>> selectTenantTop2Video(QuTenantDeptTopRequest quTenantDeptTopRequest) {
        log.error("查询租户树根节点接口,视联调用异常：服务降级:{}", cause);
        log.error("服务名：video-system，接口地址：/selectTenantTop2Video，参数：{}，调用失败！", quTenantDeptTopRequest.toString());
        return Result.fail("查询租户树根节点接口失败");
    }

    /**
     * 新增文件信息,视联使用
     *
     * @param uploadFile 文件信息
     * @return 结果
     * @auth 李峥
     */
    @Override
    public Result uploadFile2Video(MultipartFile uploadFile) {
        log.error("新增文件信息接口,视联调用异常：服务降级:{}", cause);
        log.error("服务名：video-system，接口地址：/common/uploadFileVideo，参数：{}，调用失败！", uploadFile.getOriginalFilename());
        return Result.fail("新增文件信息接口失败");
    }

    /**
     * 下载文件接口,视联使用
     *
     * @param fileId 文件id
     * @return 结果
     * @auth 李峥
     */
//    @Override
//    public Result<SysFileInfoResponse> downloadById2Video(String fileId) {
//        log.error("下载文件信息接口,视联调用异常：服务降级:{}", cause);
//        log.error("服务名：video-system，接口地址：/common/downloadById2Video，参数：{}，调用失败！", fileId);
//        return Result.fail("下载文件信息接口失败");
//    }

    @Override
    public Result<Object> downloadByUrl(String url) {
        log.error("下载文件信息接口,视联调用异常：服务降级:{}", cause);
        log.error("服务名：video-system，接口地址：/common/downloadByUrl，参数：{}，调用失败！", url);
        return Result.fail("下载文件信息接口失败");
    }

    @Override
    public Result<AuthObjDataVO> getDataScopeObj() {
        log.error("获取权限组织设备区域集合:{}", cause);
        log.error("服务名：video-system，接口地址：/admin/system/business/getDataScopeObj，参数：{}，调用失败！");
        return Result.fail("获取权限组织设备区域集合失败");
    }

    @Override
    public Result<AuthObjDataVO> getDataScopeObjBySysUser(SysUser user) {
        return Result.data(null);
    }

    @Override
    public Result<AuthObjDataVO> getDataPermissionsObj() {
        log.error("获取权限组织设备区域集合:{}", cause);
        log.error("服务名：video-system，接口地址：/admin/system/business/getDataPermissionsObj，参数：{}，调用失败！");
        return Result.fail("获取新权限失败");
    }

    @Override
    public Result<AuthObjDataVO> getDataPermissionsObjBySysUser(SysUser user) {
        return Result.data(null);
    }

    @Override
    public Result<String> queryFalseWords(String falseCode) {
        log.error("查询失败描述:{}", cause);
        log.error("服务名：video-system，接口地址：/system/commonWords/queryFalseWords，参数：{}，调用失败！");
        return Result.fail("获取查询失败描述失败");
    }

    @Override
    public Result<List<TreeSelect>> getVideoTreeselect() {
        log.error("获取租户部门下拉树列表失败:{}", "");
        return Result.fail("获取租户部门下拉树列表失败");
    }


    @Override
    public AjaxResult getFileInfo(String fileId) {
        log.error("获取文件信息详细信息:{}", cause);
        log.error("获取文件信息详细信息调用失败！", fileId);
        return AjaxResult.error();
    }


    @Override
    public AjaxResult selectSysFileInfoByIds(String[] fileIds) {
        log.error("获取文件信息详细信息:{}", cause);
        log.error("获取文件信息详细信息调用失败！");
        return AjaxResult.error();
    }

    @Override
    public Result<List<SysArea>> getAllArea(SysArea sysArea) {
        log.error("获取全部省市区信息失败cause:{}", cause);
        log.error("获取全部省市区信息失败:{}");
        return Result.fail("获取全部省市区信息失败");
    }

    @Override
    public Result<SysArea> getCenterLonLat(String code) {
        log.error("获取行政区域中心点坐标失败:{}", cause);
        return Result.fail("获取行政区域中心点坐标失败");
    }

    @Override
    public Result<List<SysAreaVO>> getAreaStreet(String code) {
        log.error("根据区县获取省市区县街道失败:{}", cause);
        return Result.fail("根据区县获取省市区县街道失败");
    }

    @Override
    public Result<SysUserWhiteListVO> getUserWhite(String userName) {
        return Result.data(null);
    }

    /**
     * 查询设备范围
     *
     * @param deptId
     * @return
     */
    @Override
    public Result<SysDeptDetailVO> getDeptDeviceDetail(String deptId) {
        log.error("查询设备范围失败:{}");
        return Result.fail("查询设备范围");
    }

    @Override
    public Result<SysDeptDetailVO> deptDeviceDetailBatch(@RequestBody List<String> list) {
        log.error("批量查询组织权限:{}");
        return Result.fail("批量查询组织权限");
    }

    @Override
    public AjaxResult uploadFile(MultipartFile file) {
        return AjaxResult.error("未知异常");
    }

    /**
     * 获取管理区域树
     */
    @Override
    public Result<List<SysDeptAreaVO>> getUserDeptTreeselect() {
        log.error("获取管理区域树:{}", cause);
        return Result.fail("获取管理区域树失败");
    }

    @Override
    public Result<List<SysUser>> getUserByDeptAndDeviceScope(GetUserByDeptAndDeviceScopeRequest request) {
        log.error("根据设备编号获取有权限的用户列表:{}", cause);
        return Result.fail("根据设备编号获取有权限的用户列表失败");
    }

    @Override
    public Result<List<SysArea>> selectByAreaName(List<String> areaNames) {
        log.error("根据区划名称查询区划信息失败", cause);
        return Result.fail("根据区划名称查询区划信息失败");
    }

    @Override
    public Result<Map<String, String>> getAreaNameCaffeineMap(List<String> areaCodes) {
        log.error("根据区划编码查询区划缓存名称失败", cause);
        return Result.fail("根据区划编码查询区划缓存名称失败");
    }

    @Override
    public Result forceLogout(String userId, String userName) {
        log.error("强制退出用户失败", cause);
        return Result.fail("强制退出用户失败");
    }

    @Override
    public Result<List<SysUser>> getTenantUsers() {
        log.error(GET_ALL_USER_FAIL_MSG, cause);
        return Result.fail(GET_ALL_USER_FAIL_MSG);
    }

    @Override
    public Result<List<SysUser>> getTenantAllUsers() {
        log.error("获取租户所有用户失败--包括删除", cause);
        return Result.fail(GET_ALL_USER_FAIL_MSG);
    }


    /**
     * 删除角色大屏关系(暴露给浩鲸调用)
     *
     * @param sysRoleScreenVOList
     * @return
     */
    @Override
    public Result<Integer> delRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList) {
        log.error("调用video-system服务删除角色大屏关系接口失败:{}",cause);
        return Result.fail("删除角色大屏关系失败");
    }

    /**
     * 查询角色大屏关系接口
     *
     * @param sysRoleScreenVO
     * @return
     */
    @Override
    public Result<List<SysRoleScreenVO>> queryRoleScreenRef(SysRoleScreenVO sysRoleScreenVO) {
        log.error("调用video-system服务查询角色大屏关系失败:{}", cause);
        return Result.fail("查询角色大屏关系失败");
    }


    /**
     * 获取当前用户大屏列表
     *
     * @param sysRoleScreenVO
     * @return
     */
    @Override
    public Result<SysRoleScreenVO> getCurrentRoleScreen(SysRoleScreenVO sysRoleScreenVO) {
        log.error("调用video-system服务获取当前用户大屏列表失败:{}", cause);
        return Result.fail("获取当前用户大屏列表失败");
    }

    @Override
    public Result<List<SysDept>> listSubsetDept(QuTenantDeptSubsetRequest quTenantDeptSubsetRequest) {
        log.error("调用video-system获取用户组织失败:{}", cause);
        return Result.fail("获取用户组织失败");
    }

    @Override
    public Result<List<VmCommonWordsVO>> getCommonWordsForInspection(wordsRequest wordsVO) {
        log.error("巡查任务常用语查询失败:{}", cause);
        return Result.fail("批量修改常用语失败");
    }

    @Override
    public Result updateProcessCommonWordsForInspection(List<wordsRequest> commonWordsDTOList) {
        log.error("编辑保存巡查任务常用语失败:{}", cause);
        return Result.fail("编辑保存巡查任务常用语失败");
    }

    @Override
    public Result<String> userPush4A(SysUser4A sysUser4A) {
        log.error("调用推送4a用户（客户）接口失败:{}", cause);
        return Result.fail("推送4a用户（客户）接口失败");
    }

    @Override
    public Result queryManagementAreaTreeByTenant(String tenantId) {
        log.error("调用租户查询管理区域接口失败:{}", cause);
        return Result.fail("租户查询管理区域接口失败");
    }

    @Override
    public Result<List<SysDeptDeviceTreeVO>> getDeptStreetTree(String deptId) {
        log.error("获取管理区域列表接口失败:{}", cause);
        return Result.fail("获取管理区域列表接口失败");
    }

    @Override
    public TableDataInfo list(SysUser user) {
        log.error("获取用户列表失败:{}", cause);
        return null;
    }

    @Override
    public Result<String> thirdPartyAlarmSync(ThirdPartyAlarmTypeVO vo) {
        log.error("调用thirdPartyAlarmSync接口失败:{}", cause);
        return Result.fail("推送thirdPartyAlarmSync接口失败");
    }

    @Override
    public TableDataInfo getSsoList(SysSso vo) {
        log.error("调用获取单点登录列表接口失败:{}", cause);
        return null;
    }

    @Override
    public AjaxResult getSsoInfo(SysSso vo) {
        log.error("调用根据id获取单点登录详细信息接口失败:{}", cause);
        return AjaxResult.error("未知异常");
    }


    public Result addSsoInfo(SysSso vo) {
        log.error("调用新增单点登录配置接口失败:{}", cause);
        return Result.fail("新增单点登录配置接口失败");
    }


    public Result editSsoInfo(SysSso vo) {
        log.error("调用更新单点登录配置接口失败:{}", cause);
        return Result.fail("更新单点登录配置接口失败");
    }

    public Result delSsoInfo(SysSso vo) {
        log.error("调用删除单点登录配置接口失败:{}", cause);
        return Result.fail("删除单点登录配置接口失败");
    }

    public Result getAlarmTypeMap() {
        log.error("调用获取告警类型map接口失败:{}", cause);
        return Result.fail("获取告警类型map接口失败");
    }

}
