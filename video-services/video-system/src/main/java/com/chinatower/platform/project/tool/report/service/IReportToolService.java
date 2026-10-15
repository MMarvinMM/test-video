package com.chinatower.platform.project.tool.report.service;


import com.chinatower.platform.framework.web.page.TableDataInfo;
import com.chinatower.platform.project.tool.report.domain.ReportCondition;
import com.chinatower.platform.project.tool.report.domain.ReportTool;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * 系统报表配置Service接口
 * 
 * @author wangzic
 * @date 2020-02-14
 */
public interface IReportToolService
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
     * 批量删除系统报表配置
     * 
     * @param ids 需要删除的系统报表配置ID
     * @return 结果
     */
    int deleteReportConfigByIds(Integer[] ids);

    /**
     * 删除系统报表配置信息
     * 
     * @param id 系统报表配置ID
     * @return 结果
     */
    int deleteReportConfigById(Integer id);

    /**
     * 执行动态sql并返回查询结果
     * @param condition 条件
     * @return 查询结果
     */
    TableDataInfo selectByDynamicSql(ReportCondition condition);

    /**
     * 报表数据导出
     * @param condition 查询条件
     * @param response 响应流
     * @throws Exception 异常抛出
     */
    void exportReport(ReportCondition condition, HttpServletResponse response) throws Exception;

    Set<String> getCollNames(String dbName);
}
