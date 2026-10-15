package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName SysPersonalityMapper
 * @Description 个性化配置表 数据层
 * @Author D
 * @Date 2022/2/23 17:02
 */
public interface SysPersonalityMapper {
    /**
     * @Description 获取平台配置
     * @author wjd
     * @Date 15:40 2021/7/7
     * @Param [sysInfoSetVO]
     **/
    public List<SysInfoSetVO> getInfoSetList(@Param("vo") SysInfoSetVO sysInfoSetVO);
    /**
     * @Description 获取平台配置two
     * @author wjd
     * @Date 15:40 2021/7/7
     * @Param [sysInfoSetVO]
     **/
    public List<SysInfoSetVO> getInfoSetListTwo(@Param("vo") SysInfoSetVO sysInfoSetVO);
    /**
     * @Description 保存设置
     * @author wjd
     * @Date 15:41 2021/7/7
     * @Param [voList]
     **/
    public int saveSets(@Param("volist") List<SysInfoSetVO> voList,@Param("createBy") String createBy);

    /**
     * @Description 新增
     * @author wjd
     * @Date 15:41 2021/7/7
     * @Param [vo]
     **/
    public int insert(SysInfoSetVO vo);
    /**
     * @Description 更新
     * @author wjd
     * @Date 16:37 2021/7/7
     * @Param [vo]
     **/
    @DataScope(tenantColumn = "tenant_id",industryCode = "industry_code",appVerCode = "app_ver_code")
    public int updateById(SysInfoSetVO vo);

    /**
     * 删除
     * @param createBy
     * @return
     */
    int delete(@Param("createBy")String createBy,@Param("type")String type,@Param("appVerCode") String appVerCode,@Param("industryCode") String industryCode);

    /**
     * 根据租户id删除
     * @param tenantId
     * @return
     */
    int deleteByTenantId(@Param("tenantId")String tenantId,@Param("type")String type,@Param("appVerCode") String appVerCode,@Param("industryCode") String industryCode);

    /**
     *
     * @param type 类型 查看 SysInfoSetVOType
     * @param createBy 创建人
     * @param tenantId 租户ID
     * @param appVerCode 版本编码
     * @param industryCode 行业编码
     * @param deptId 组织ID
     * @return 清楚个性化配置
     */
    int deleteBySysInfoSetVO(@Param("type")String type,
                        @Param("createBy")String createBy,
                        @Param("tenantId")String tenantId,
                        @Param("appVerCode") String appVerCode,
                        @Param("industryCode") String industryCode,
                        @Param("deptId")String deptId);

    SysInfoSetVO selectOne(@Param("tenantId")String tenantId,@Param("type")String type,@Param("setName")String setName,@Param("appVerCode") String appVerCode,@Param("industryCode") String industryCode);

    String findBannerTitle(@Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode, @Param("platformCode") String platformCode);
}
