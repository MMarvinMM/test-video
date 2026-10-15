package com.chinatower.platform.project.system.mapper;

import java.util.List;

import com.chinatower.mapper.TowerBaseMapper;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.project.system.domain.dto.SwithUserDTO;
import com.chinatower.platform.project.system.domain.vo.TenantUserSelectVO;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.resp.SysIndustryViewRelResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoBaseResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import org.apache.ibatis.annotations.Param;

/**
 * 租户Mapper接口
 *
 * @author liuhuanhuan
 * @date 2022-01-19
 */
public interface SysTenantInfoMapper extends TowerBaseMapper<SysTenantInfo> {
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
     * 批量删除租户
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysTenantInfoByIds(Long[] ids);

    /**
     * 查询所有租户
     * @return
     */
    List<SysTenantInfo> selectAllSysTenantInfo();

    List<SysTenantInfo> selectTenantInfoByTenantId(@Param("tenantId") String tenantId);

    int selectCountByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据租户ID查询出租户信息
     * @param tenantId
     * @return
     */
    SysTenantInfo selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据部门集合查询租户信息
     * @param deptIds
     * @author 李峥
     * @return
     */
    List<SysTenantInfo> selectTenantByDeptIds(@Param("deptIds") List deptIds);

    List<SysTenantInfo> selectByTenantNameAuser(SysTenantInfo sysTenantInfo);
    List<SysTenantInfoBaseResponse> selectByTenantIdAuser(SysTenantInfo sysTenantInfo);

    List<SysTenantTrade> selectTenantTradeByTenantId(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode,@Param("platformCode") String platformCode);

    List<SysIndustryViewRelResponse> selectAppVerCodeByTenId(@Param("tenantId") String tenantId, @Param("acceptNode") String acceptNode);

    List<SysIndustryViewRelResponse> selectLocalizationAppVerCodeByTenId(@Param("tenantId") String tenantId,
                                                                         @Param("acceptNode") String acceptNode);

    List<TenantUserSelectVO> selectTenantUserByKeyword(SwithUserDTO dto);

    //void updateTradeInfoByTenantId(@Param("tenantId") String tenantId, @Param("newTradeInfo") String newTradeInfo);

    int deleteByTenantId(@Param("tenantId") String tenantId);

    int insertSysTenant(@Param("param") SysTenantInfo param);

    int updateSysTenantById(@Param("param") SysTenantInfo param);
}
