package com.chinatower.platform.project.pushsys.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.pushsys.service.IPushSysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * video-system
 * 推送变更Controller
 *
 * @author zl
 * @date 2022-02-17
 */
@Api(tags = "网关平台与运管平台交互接口")
@RestController
public class PushSysController extends TowerBaseController {

    @Autowired
    private IPushSysService pushSysService;

    /**
     * 铁塔系统用户数据变更通知推送
     */
    @ApiOperation(value = "铁塔系统用户数据变更通知推送")
    @PostMapping("/system/user/userPush")
    public Result userPush(@RequestBody Map map) {
        return pushSysService.userPush(map);
    }

    /**
     * 客户账号变更推送
     */
    @ApiOperation(value = "客户账号变更推送")
    @PostMapping("/video/message/customer")
    public Result customer(@RequestBody Map map) {
        return pushSysService.customer(map);
    }

    /**
     * 管理区域变更通知推送
     */
    @ApiOperation(value = "管理区域变更通知推送")
    @PostMapping("/video/message/updateManagementArea")
    public Result updateManagementArea(@RequestBody Map map) {
        return pushSysService.updateManagementArea(map);
    }

    /**
     * 组织机构变更通知推送
     */
    @ApiOperation(value = "组织机构变更通知推送")
    @PostMapping("/system/custorgan/organInfoPush")
    public Result organInfoPush(@RequestBody Map map) {
        return pushSysService.organInfoPush(map);
    }


}
