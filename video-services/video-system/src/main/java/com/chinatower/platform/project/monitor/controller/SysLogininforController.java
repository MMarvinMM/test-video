package com.chinatower.platform.project.monitor.controller;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.PageDomain;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.page.TableSupport;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.dto.SysLogininfoDTO;

import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * video-system
 * 系统访问记录
 *
 * @author chinatower
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends TowerBaseController
{
    @Autowired
    private LogFeign logFeign;

    @Value("${maxPageSize.oracle:1000}")
    private Integer maxPageSize;

    /**
     * 登录日志列表查询
     * @param logininfor
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfoDTO logininfor)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        logininfor.setPageNo(pageNum);
        logininfor.setPageSize(pageSize);
        return logFeign.list(logininfor);
    }

    /**
     * 登录次数查询
     */
    @GetMapping("/count")
    public Result<Long> count(){
        SysLogininfoDTO logininfor = new SysLogininfoDTO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        logininfor.setUserName(user.getUserName());
        Date loginDate = new Date();
        logininfor.setBeginTime(loginDate);
        logininfor.setEndTime(loginDate);
        return logFeign.getLoginNum(logininfor);
    }

    private void copy(List<SysLogininfoEntity> result ,List<SysLogininfoEntity> list){
        if(null == list || list.size() == 0){
            return;
        }
        JSON.parseArray(JSON.toJSONString(list)).forEach(s->{
            SysLogininfoEntity e = JSON.parseObject(s.toString(),SysLogininfoEntity.class);
            result.add(e);
        });
    }

//    @TowerLog(title = "登陆日志", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(@RequestBody  SysLogininfoDTO logininfor, HttpServletResponse response) {
//        logininfor.setPageNo(1);
//        logininfor.setPageSize(maxPageSize);
//        List<SysLogininfoEntity> result = new ArrayList<>();
//        List<SysLogininfoEntity> list = (List<SysLogininfoEntity>) logFeign.list(logininfor).getRows();
//        copy(result,list);
//        PageInfo<SysLogininfoEntity> pageInfo =new PageInfo<>(list);
//        if(pageInfo.getPages()>1){
//            for(int i=2;i<=pageInfo.getPages();i++){
//                logininfor.setPageNo(1);
//                logininfor.setPageSize(maxPageSize);
//                List<SysLogininfoEntity> list2 = (List<SysLogininfoEntity>) logFeign.list(logininfor).getRows();
//                copy(result,list2);
//            }
//        }
//        byte[] data =new ExcelUtil<>(SysLogininfoEntity.class).exportBytes(result);
//        FileUtils.download(response, data);
//    }
}
