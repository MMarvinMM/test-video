package com.chinatower.platform.controller;

import com.chinatower.common.constant.ProviderUrlConstant;
import com.chinatower.common.entity.CommonLog;
import com.chinatower.common.entity.Result;
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
import com.chinatower.platform.dto.SysOperLogDTO;
import com.chinatower.platform.entity.InterfaceOpeLog;
import com.chinatower.platform.entity.SysLogininfoEntity;
import com.chinatower.platform.entity.SysOperLogEntity;
import com.chinatower.platform.service.ISysOperLogService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * tower-log
 * 操作日志记录
 *
 * @author iwhalecloud
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping(ProviderUrlConstant.PROVIDER + "/sysLog")
public class SysOperlogController extends TowerBaseController {
    @Autowired
    private ISysOperLogService operLogService;

    @Value("${maxPageSize.oracle:1000}")
    private int maxPageSize;
    @Value("${maxPageSize.export:5000}")
    private Integer exportMaxPageSize;

    /**
     * 新增操作日志
     *
     * @param commonLog
     * @return
     */
    @PostMapping("/add")
    public Result<Boolean> addLog(@RequestBody InterfaceOpeLog commonLog) {
        log.info("SysOperlogController addLog commonLog:{}", commonLog);
        return Result.data(operLogService.handleLog(commonLog));
    }


    /**
     * 查询操作日志
     *
     * @param operLog
     * @return
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SysOperLogDTO operLog) {
        Integer pageNum = operLog.getPageNum();
        Integer pageSize = operLog.getPageSize();
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        //由于mysql的时候pageNo是1开始的，mongodb需要减一
        operLog.setPageNum(pageNum-1);
        operLog.setPageSize(pageSize);
        Page<SysOperLogEntity> page = operLogService.selectOperLogList(operLog);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(page.getList());
        rspData.setTotal(page.getCount());
        return rspData;
    }

    /**
     * 导出操作日志（返回list）
     *
     * @param logininfor
     * @return
     */
    @PostMapping("/exportList")
    public Result<List<SysOperLogEntity>> exportList(@RequestBody SysOperLogDTO logininfor) {
        log.info("导出操作日志exportList:{}", logininfor);
        logininfor.setPageNum(0);
        logininfor.setPageSize(maxPageSize);
        Page<SysOperLogEntity> page = operLogService.selectOperLogList(logininfor);
        if (page.getCount() > exportMaxPageSize){
            return Result.fail("导出数据量最多"+ exportMaxPageSize +"条，请按照查询条件导出。");
        }
        List<SysOperLogEntity> list = page.getList();
        PageInfo<SysOperLogEntity> pageInfo = new PageInfo<>(list);
        log.info("count:{}, no:{}, pagesize:{}", page.getCount(), page.getPageNo(), page.getPageSize());
        long pageNum = page.getCount()/maxPageSize;
        if (page.getCount()%maxPageSize > 0){
            pageNum++;
        }
        log.info("一共的页数pageNum:{}", pageNum);
        if (pageNum > 1) {
            for (int i = 1; i < pageNum; i++) {
                logininfor.setPageNum(i);
                logininfor.setPageSize(maxPageSize);
                list.addAll(operLogService.selectOperLogList(logininfor).getList());
            }
        }
        if (list.isEmpty()){
            return Result.fail("根据条件查询数据为空");
        }
        return Result.data(list);
    }

    /**
     * 导出操作日志
     *
     * @param operLog
     * @return
     */
    @TowerLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody SysOperLogDTO operLog, HttpServletResponse response) {

        operLog.setPageNum(0);
        operLog.setPageSize(maxPageSize);
        Page<SysOperLogEntity> page = operLogService.selectOperLogList(operLog);
        PageInfo<SysOperLogEntity> pageInfo =new PageInfo<>(page.getList());
        List<SysOperLogEntity> list = new ArrayList<>();
        list.addAll(page.getList());
        if(pageInfo.getPages()>1){
            for(int i=1;i<=pageInfo.getPages();i++){
                operLog.setPageNum(i);
                operLog.setPageSize(maxPageSize);
                list.addAll(operLogService.selectOperLogList(operLog).getList());
            }
        }
        byte[] data =new ExcelUtil<>(SysOperLogEntity.class).exportBytes(list);
        FileUtils.download(response, data);
    }
}
