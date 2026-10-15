package com.chinatower.platform.auth.project.mapper;

import com.chinatower.auth.api.entity.WhiteIpDto;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.platform.auth.model.QrLoginDTO;
import com.chinatower.platform.auth.model.SwitchLoginVo;
import com.chinatower.platform.auth.model.SwitchUserDTO;
import com.chinatower.platform.auth.model.SysAuthTokenVo;
import com.chinatower.platform.auth.project.entity.dto.RolePermUrlDto;
import com.chinatower.platform.entity.admin.SysAreaStreet;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysDeptStreetRel;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author bruce
 */
public interface SysUserMapper {


    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     *
     * @param tenantId
     * @return 获得租户名称
     */
    String getTenantName(@Param("tenantId")String tenantId);

    /**
     *
     * @param userName
     * @return
     */
    SysUser selectUserOnlyByUserName(String userName);




    SysUser selectUserWithPwdByUserName(@Param("userName") String userName, @Param("tradeId") String tradeId);
    SysUser selectUserWithPwdByUserId(@Param("userId") String userId, @Param("tradeId") String tradeId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(@Param("user") SysUser user);
    List<RolePermUrlDto> selectMenuPermsByUser(@Param("user") SysUser user);

    /**
     * 修改用户信息
     *
     * @param sysUser
     * @return
     */
    int updateSysUser(SysUser sysUser);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 获取铁塔管理员下对应的租户管理员信息
     *
     * @param sysUser
     * @return
     */
    List<SysUser> getTenantUsers(SysUser sysUser);

    /**
     * 根据userId查询用户
     *
     * @param userId
     * @return
     */
    SysUser selectUserByUserId(String userId);


    //List<SysIndustryViewRel> selectAppVerCodes(Map<String, Object> map);

    /**
     * 查询当前用户选择的组织
     * @param deptIds
     * @param industryCode
     * @param appVerCode
     * @return
     */
    public SysDept selectCurrentDept(@Param("deptIds") String deptIds,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode);

    /**
     * 查询用户关联的行业信息
     * @param deptIds
     * @return
     */
    List<SysIndustryViewRel> selectUserAppVerCodesByDeptId(@Param("deptIds") String deptIds,@Param("platformCode") String platformCode);

    /**
     * 获取登录账号数据权限
     * @return
     */
    List<Map<String,Object>> getPermissions(@Param("userId") String userId);

    List<SysAreaStreet> selectStreeByDeptId(String deptId);

    List<SysAreaStreet> selectParent(List<String> list);

    List<SysDeptStreetRel> selectAreaListByDeptId(String deptId);

    List<Map> selectALLDeptListByDeptId(SysDept dept);

    List<Map> selectALLStreetListByDeptId(SysDept dept);

    List<Map> selectALLDeviceListByDeptId(SysDept dept);

    List<Map> selectALLWeixingListByDeptId(SysDept dept);

    void updatePassword(@Param("userId") String userId,@Param("password")String password,@Param("username")String username);

    Long validateSwitchUser(SwitchUserDTO dto);

    SysAuthTokenVo selectAuthTokenInfo(@Param("uuid")  String uuid);

    int insertQrLogin(QrLoginDTO qrLoginDTO);

    QrLoginDTO getQrLoginInfo(String code);

    int updateQrLoginInfo(QrLoginDTO qrLoginDTO);

    int deleteByDate();

    int insertSwitchLogin(SwitchLoginVo switchLoginVo);

    SwitchLoginVo getSwitchLogin(String uuid);

    int updateSwitchLogin(String uuid);

    String checkPlatformCode(String tenantId);

    List<WhiteIpDto> findWhiteIpList(String username);
}
