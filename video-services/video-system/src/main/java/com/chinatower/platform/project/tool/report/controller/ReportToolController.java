package com.chinatower.platform.project.tool.report.controller;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.framework.web.controller.BaseController;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.framework.web.page.TableDataInfo;
import com.chinatower.platform.project.tool.report.domain.ReportCondition;
import com.chinatower.platform.project.tool.report.domain.ReportTool;
import com.chinatower.platform.project.tool.report.service.IReportToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * video-system
 * 报表工具Controller
 *
 * @author wangzic
 * @date 2020-02-14
 */
@Slf4j
@RestController
@RequestMapping("/tool/report")
public class ReportToolController extends BaseController {

    @Autowired
    private IReportToolService reportToolService;

    /**
     * 查询系统报表配置列表
     */
    @PreAuthorize("@ss.hasPermi('tool:report:query')")
    @GetMapping("/list")
    public TableDataInfo list(ReportTool reportTool) {
        startPage();
        List<ReportTool> list = reportToolService.selectReportConfigList(reportTool);
        return getDataTable(list);
    }

    /**
     * 导出系统报表配置列表
     */
    @PreAuthorize("@ss.hasPermi('tool:report:export')")
    @TowerLog(title = "系统报表配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody  ReportTool reportTool,HttpServletResponse response) {
        List<ReportTool> list = reportToolService.selectReportConfigList(reportTool);
        byte[] data =new ExcelUtil<>(ReportTool.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 获取系统报表配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('tool:report:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        ReportTool reportTool = reportToolService.selectReportConfigById(id);
        reportTool.setReportSql(AESUtil.encryptBase64(reportTool.getReportSql()));
        return AjaxResult.success(reportTool);
    }

    /**
     * 新增系统报表配置
     */
    @PreAuthorize("@ss.hasPermi('tool:report:add')")
    @TowerLog(title = "系统报表配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "add")
    public AjaxResult add(@RequestBody ReportTool reportTool) {
        reportTool.setReportSql(AESUtil.decryptBase64(reportTool.getReportSql()));
        return toAjax(reportToolService.insertReportConfig(reportTool));
    }

    /**
     * 修改系统报表配置
     */
    @PreAuthorize("@ss.hasPermi('tool:report:edit')")
    @TowerLog(title = "系统报表配置", businessType = BusinessType.UPDATE)
    @PostMapping(value = "edit")
    public AjaxResult edit(@RequestBody ReportTool reportTool) {
        reportTool.setReportSql(AESUtil.decryptBase64(reportTool.getReportSql()));
        return toAjax(reportToolService.updateReportConfig(reportTool));
    }

    /**
     * 删除系统报表配置
     */
    @PreAuthorize("@ss.hasPermi('tool:report:remove')")
    @TowerLog(title = "系统报表配置", businessType = BusinessType.DELETE)
	@GetMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(reportToolService.deleteReportConfigByIds(ids));
    }

    /**
     * 查询动态报表数据
     * @param condition 查询条件
     * @return 查询结果
     */
    @PostMapping("/selectByReportId")
    public TableDataInfo selectReportData(@RequestBody ReportCondition condition) {
        ReportTool config = reportToolService.selectReportConfigById(condition.getReportId());
        if(config ==null){
            throw new CustomException("报表["+condition.getReportId()+"]配置不存在");
        }
        condition.setReportSql(config.getReportSql());
        condition.setIfDataFilter(config.getIfDataFilter());
        condition.setDatasourceType(StrUtil.isBlank(config.getDatasourceType())?"Oracle":config.getDatasourceType());
        return reportToolService.selectByDynamicSql(condition);
    }

    /**
     * 导出动态报表数据(兼容运维APP接口)
     * @param condition 查询条件
     */
    @TowerLog(title = "动态报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export/bySql")
    public void exportReportData(@RequestBody ReportCondition condition, HttpServletResponse response) throws Exception {
        reportToolService.exportReport(condition,response);
    }

    @GetMapping("/getCollNames/{dbName}")
    public AjaxResult getCollNames(@PathVariable("dbName") String dbName) throws Exception {
        return AjaxResult.success(reportToolService.getCollNames(dbName));
    }
}
