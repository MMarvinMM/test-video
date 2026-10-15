package com.chinatower.platform.project.tool.report.mapper;

import com.chinatower.platform.project.tool.report.domain.ReportTool;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统报表配置Mapper接口
 * 
 * @author wangzic
 * @date 2020-02-14
 */
public interface ReportToolMapper
{
    /**
     * 查询系统报表配置
     * 
     * @param id 系统报表配置ID
     * @return 系统报表配置
     */
    ReportTool selectReportConfigById(Integer id);

    /**
     * 查询系统报表配置列表
     * 
     * @param reportTool 系统报表配置
     * @return 系统报表配置集合
     */
    List<ReportTool> selectReportConfigList(ReportTool reportTool);

    /**
     * 新增系统报表配置
     * 
     * @param reportTool 系统报表配置
     * @return 结果
     */
    int insertReportConfig(ReportTool reportTool);

    /**
     * 修改系统报表配置
     * 
     * @param reportTool 系统报表配置
     * @return 结果
     */
    int updateReportConfig(ReportTool reportTool);

    /**
     * 删除系统报表配置
     * 
     * @param id 系统报表配置ID
     * @return 结果
     */
    int deleteReportConfigById(Integer id);

    /**
     * 批量删除系统报表配置
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteReportConfigByIds(Integer[] ids);


    /**
     * 根据动态sql查询数据
     * @param dynamicSql 动态sql
     * @param dataScope 权限过滤
     * @return 结果
     */
    List<Map<String,Object>> selectByDynamicSql(@Param("dynamicSql") String dynamicSql,@Param("dataScope") String dataScope);
}
