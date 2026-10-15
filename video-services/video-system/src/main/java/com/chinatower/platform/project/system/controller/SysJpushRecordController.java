package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.domain.BsJpushRecordMongo;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;
import com.chinatower.platform.project.system.service.ISysJpushRecord;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * video-system
 *
 */
@Api(tags = "极光推送记录表")
@Slf4j
@RestController
@RequestMapping("/system/jpush")
public class SysJpushRecordController extends TowerBaseController {
    @Resource
    private ISysJpushRecord iSysJpushRecord;

    /**
     * 获取极光消息列表
     */
    @ApiOperation(value = "列表",notes = "列表")
    @PostMapping("/getJpushRecordlist")
    public TableDataInfo  getJpushRecordlist(@RequestBody SysJpushRecord vo){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        HttpServletRequest request = RequestUtils.getRequest();
        log.info("请求:{}", request);
        String clientVersion = request.getHeader("client_version");
        log.info("获取请求头client_version:{}", clientVersion);
        if (StringUtils.isNotEmpty(clientVersion)) {
            vo.setAlias(user.getUserId() + "_" + user.getIndustryCode());
        } else {
            vo.setAlias(user.getUserId());
        }
        log.info("获取请求头client_version:{}", clientVersion);
        List<BsJpushRecordMongo> list = iSysJpushRecord.getJpushRecordlist(vo);
        return getDataTable(list);
    }

    /**
     * 修改极光推送记录状态
     */
    @ApiOperation(value = "修改极光推送记录状态")
    @PostMapping(value = "/updateJpushRecordById")
    public Result<Integer> updateJpushRecordById(@RequestBody SysJpushRecord vo) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        vo.setUpdateBy(currentUser.getUser().getUserId());
        return toAjax(iSysJpushRecord.updateJpushRecordById(vo));
    }

}
