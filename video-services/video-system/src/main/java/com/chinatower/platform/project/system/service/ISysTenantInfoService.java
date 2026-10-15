package com.chinatower.platform.project.system.service;

import java.util.List;

import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.project.system.domain.dto.SwithUserDTO;
import com.chinatower.platform.project.system.domain.vo.TenantUserSelectVO;
import com.chinatower.service.TowerBaseService;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.resp.SysIndustryViewRelResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoBaseResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;

/**
 * 租户Service接口
 *
 * @author liuhuanhuan
 * @date 2022-01-19
 */
public interface ISysTenantInfoService extends TowerBaseService<SysTenantInfo> {
    /**
     * 查询租户
     *
     * @param id 租户主键
     * @return 租户
     */
    public SysTenantInfo selectSysTenantInfoById(Long id);

    /**
     * 查询租户列表
     *
     * @param quTenantInfoRequest 租户
     * @return 租户集合
     */
    public List<SysTenantInfo> selectSysTenantInfoList(QuTenantInfoRequest quTenantInfoRequest);

    /**
     * 查询所有租户
     * @return List<SysTenantInfo>
     */
    List<SysTenantInfoResponse> selectAllSysTenant();

    /**
     * 根据租户Id查询租户信息
     * @param tenantId
     * @return
     */
    List<SysTenantInfoResponse> selectTenantInfoByTenantId(String tenantId);

    /**
     * 新增租户管理
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    public int insertSysTenantInfo(SysTenantInfo sysTenantInfo);
    /**
     * 新增租户管理 视联专用
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    public int insertSysTenantInfo4Video(SysTenantInfo sysTenantInfo);

    /**
     * 修改租户管理
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    public int updateSysTenantInfo(SysTenantInfo sysTenantInfo);

    /**
     * 删除租户管理信息
     *
     * @param id 租户管理主键
     * @return 结果
     */
    public int deleteSysTenantInfoById(Long id);

    int selectCountByTenantId(String tenantId);

    /**
     * 根据部门集合查询租户信息
     * @param deptIds
     * @author 李峥
     * @return
     */
    List<SysTenantInfoResponse> selectTenantByDeptIds(List deptIds);

    List<SysTenantInfoBaseResponse> selectByTenantIdAuser(SysTenantInfo sysTenantInfo);

    List<SysTenantInfoResponse> selectByTenantNameAuser(SysTenantInfo sysTenantInfo);

    List<SysTenantTrade> selectTenantTradeByTenantId(String tenantId);

    List<SysIndustryViewRelResponse> selectAppVerCodeByTenId(String tenantId, String acceptNode);

    List<SysIndustryViewRelResponse> selectLocalizationAppVerCodeByTenId(String tenantId, String acceptNode);

    List<TenantUserSelectVO> selectTenantUserByKeyword(SwithUserDTO dto);

    /**
     * 运管同步创建客户信息
     * @param sysTenantInfo
     * @return
     */
    int insertSysTenantInfo4Sync(SysTenantInfo sysTenantInfo);

    /**
     * 运管同步更新客户信息
     * @param sysTenantInfo 客户信息
     * @return 影响行数
     */
    int updateSysTenantById(SysTenantInfo sysTenantInfo);

    /**
     * 根据客户id查询客户信息
     * @param tenantId 客户id
     * @return 客户信息
     */
    SysTenantInfo selectByTenantId(String tenantId);

    /**
     * 根据客户id删除客户信息
     * @param tenantId 客户id
     * @return 影响行数
     */
    int deleteByTenantId(String tenantId);
}
