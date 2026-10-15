package com.chinatower.platform.controller;

import com.chinatower.common.constant.ProviderUrlConstant;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.PageDomain;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.page.TableSupport;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.dto.SysLogininfoDTO;
import com.chinatower.platform.entity.SysLogininfoEntity;
import com.chinatower.platform.service.ISysLogininfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * tower-log
 * 系统访问记录
 *
 * @author chinatower
 */
@RefreshScope
@RestController
@RequestMapping(ProviderUrlConstant.PROVIDER + "/logininfo")
public class SysLogininfoController extends TowerBaseController {
    @Autowired
    private ISysLogininfoService logininfoService;

    @Value("${maxPageSize.oracle:5000}")
    private Integer maxPageSize;
    @Value("${maxPageSize.export:5000}")
    private Integer exportMaxPageSize;

    /**
     * 查询系统登录日志
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SysLogininfoDTO logininfor) {
        Integer pageNum = logininfor.getPageNo() - 1;
        Integer pageSize = logininfor.getPageSize();
        logininfor.setPageNo(pageNum);
        logininfor.setPageSize(pageSize);
        Page<SysLogininfoEntity> sysLogininfoEntityPage = logininfoService.selectLogininforList(logininfor);
        List<SysLogininfoEntity> list = sysLogininfoEntityPage.getList();
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setTotal(sysLogininfoEntityPage.getCount());
        return rspData;
    }

    /**
     * 导出系统登录日志（返回list）
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @PostMapping("/exportList")
    public Result<List<SysLogininfoEntity>> exportList(@RequestBody SysLogininfoDTO logininfor) {
        logininfor.setPageNo(0);
        logininfor.setPageSize(maxPageSize);
        Page<SysLogininfoEntity> page = logininfoService.selectLogininforList(logininfor);
        if (page.getCount() > exportMaxPageSize){
            return Result.fail("导出数据量最多"+ exportMaxPageSize +"条，请按照查询条件导出。");
        }
        List<SysLogininfoEntity> list = page.getList();
        PageInfo<SysLogininfoEntity> pageInfo = new PageInfo<>(list);
        if (pageInfo.getPages() > 1) {
            for (int i = 1; i <= pageInfo.getPages(); i++) {
                logininfor.setPageNo(i);
                logininfor.setPageSize(maxPageSize);
                list.addAll(logininfoService.selectLogininforList(logininfor).getList());
            }
        }
        if (list.isEmpty()){
            return Result.fail("根据条件查询数据为空");
        }
        return Result.data(list);
    }

    /**
     * 统计登录次数
     * @param logininfor
     * @return
     */
    @PostMapping("/count")
    public Result<Long> getLoginNum(@RequestBody SysLogininfoDTO logininfor) {
        Long count = logininfoService.countLogininfor(logininfor);
        return Result.data(count);
    }

    /**
     * 导出系统登录日志
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @TowerLog(title = "登陆日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody SysLogininfoDTO logininfor, HttpServletResponse response) {
        logininfor.setPageNo(0);
        logininfor.setPageSize(maxPageSize);
        Page<SysLogininfoEntity> page = logininfoService.selectLogininforList(logininfor);
        if (page.getCount() > exportMaxPageSize){
            throw new CustomException("导出数据量最多"+ exportMaxPageSize +"条，请按照查询条件导出。");
        }
        List<SysLogininfoEntity> list = page.getList();
        PageInfo<SysLogininfoEntity> pageInfo = new PageInfo<>(list);
        if (pageInfo.getPages() > 1) {
            for (int i = 1; i <= pageInfo.getPages(); i++) {
                logininfor.setPageNo(i);
                logininfor.setPageSize(maxPageSize);
                list.addAll(logininfoService.selectLogininforList(logininfor).getList());
            }
        }
        if (list.isEmpty()){
            throw new CustomException("根据条件查询数据为空");
        }
        byte[] data = new ExcelUtil<>(SysLogininfoEntity.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @TowerLog(title = "登陆日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public Result remove(@PathVariable String[] infoIds) {
        return toAjax(logininfoService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @TowerLog(title = "登陆日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        logininfoService.cleanLogininfor();
        return AjaxResult.success();
    }


    /**
     * 新增系统登录日志
     *
     * @param logininfo 访问日志对象
     */
    @PostMapping("record")
    public Result<Boolean> recordLoginInfo(@RequestBody SysLogininfoEntity logininfo) {
        logininfoService.insertLogininfor(logininfo);
        return Result.success(ResultCode.SUCCESS);
    }
}
