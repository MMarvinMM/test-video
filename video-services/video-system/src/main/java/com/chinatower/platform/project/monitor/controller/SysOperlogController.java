package com.chinatower.platform.project.monitor.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.PageDomain;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.page.TableSupport;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.api.entity.SysOperLogEntity;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;

import com.chinatower.platform.project.monitor.domain.SysOperLog;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * video-system
 * 操作日志记录
 *
 * @author iwhalecloud
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends TowerBaseController {


    @Autowired
    private LogFeign logFeign;

    @Value("${maxPageSize.oracle:1000}")
    private int maxPageSize;

    /**
     * 操作日志列表查询
     * @param operLog
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(SysOperLogDTO operLog) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        operLog.setPageNum(pageNum);
        operLog.setPageSize(pageSize);
        return logFeign.list(operLog);

    }

    private void copy(List<SysOperLogEntity> result , List<SysOperLogEntity> list){
        if(null == list || list.size() == 0){
            return;
        }
        JSON.parseArray(JSON.toJSONString(list)).forEach(s->{
            SysOperLogEntity e = JSON.parseObject(s.toString(),SysOperLogEntity.class);
            result.add(e);
        });
    }

//    @TowerLog(title = "操作日志", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(@RequestBody SysOperLogDTO operLog, HttpServletResponse response) {
//        operLog.setPageNum(1);
//        operLog.setPageSize(maxPageSize);
//        List<SysOperLogEntity> result = new ArrayList<>();
//        List<SysOperLogEntity> list = (List<SysOperLogEntity>) logFeign.list(operLog).getRows();
//        copy(result,list);
//        PageInfo<SysOperLogEntity> pageInfo =new PageInfo<>(list);
//        if(pageInfo.getPages()>1){
//            for(int i=2;i<=pageInfo.getPages();i++){
//                operLog.setPageNum(i);
//                operLog.setPageSize(maxPageSize);
//                List<SysOperLogEntity> list2 = (List<SysOperLogEntity>) logFeign.list(operLog).getRows();
//                copy(result,list2);
//            }
//        }
//        byte[] data =new ExcelUtil<>(SysOperLogEntity.class).exportBytes(result);
//        FileUtils.download(response, data);
//    }
}
