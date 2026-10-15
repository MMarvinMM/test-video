package com.chinatower.platform.project.numtwin.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.numtwin.entity.NumTwinsConfigDto;
import com.chinatower.platform.project.numtwin.entity.NumTwinsUser;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OfflinUserListReq;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OnlineUserListDto;
import com.chinatower.platform.project.numtwin.service.NumTwinsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:08
 */

@Slf4j
@RestController
@RequestMapping("/numTwin")
public class NumTwinsController {

    @Autowired
    private NumTwinsService numTwinsService;


    /**
     * 数字孪生大屏新增编辑
     * @param req
     * @return
     */
    @PostMapping("/addOrUpdate")
    public Result addOrUpdate(@RequestBody NumTwinsConfigDto req) {
        return numTwinsService.addOrUpdate(req);
    }

    /**
     * 根据租户行业查询数字孪生配置
     * @param req
     * @return
     */
    @PostMapping("/getInfo")
    public Result getInfo(@RequestBody NumTwinsConfigDto req) {
        return numTwinsService.getInfo(req);
    }

    /**
     * 查询用户优先级列表
     * @param req
     * @return
     */
    @PostMapping("/getUserLevelList")
    public Result getUserLevelList(@RequestBody NumTwinsUser req) {
        return numTwinsService.getUserLevelList(req);
    }

    /**
     * 批量下线功能
     * @param req
     * @return
     */
    @PostMapping("/offLineUserList")
    public Result offLineUserList(@RequestBody OfflinUserListReq req) {
        return numTwinsService.offLineUserList(req);
    }

    /**
     * 查询在线用户列表
     * @param req
     * @return
     */
    @PostMapping("/getOnlineUserList")
    public Result getOnlineUserList(@RequestBody OnlineUserListDto req) {
        return numTwinsService.getOnlineUserList(req);
    }
}
