package com.chinatower.platform.project.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import com.chinatower.platform.project.service.MsgSwitchService;
import com.chinatower.platform.project.vo.MsgSwitchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * video-msg
 */
@Api(tags = "消息通知开关接口")
@Slf4j
@RestController
@RequestMapping("/switch")
public class MsgSwitchController {

    @Resource
    MsgSwitchService msgSwitchService;

    /**
     * 查询用户消息通知开关
     * @return
     */
    @ApiOperation(value = "查询用户消息通知开关", produces = "application/json")
    @PostMapping("/findByUser")
    public Result<SysMsgSwitch> findByUser() {
        return Result.data(msgSwitchService.findByUser());
    }

    /**
     * 更新用户消息通知开关
     * @param msgSwitchVO
     * @return
     */
    @ApiOperation(value = "更新用户消息通知开关", produces = "application/json")
    @PostMapping("/updateByUser")
    public Result<SysMsgSwitch> updateByUser(@RequestBody MsgSwitchVO msgSwitchVO) {
        msgSwitchService.updateByUser(msgSwitchVO);
        return Result.success("操作成功");
    }

    /**
     * 查询用户列表的开关状态
     * @param userList
     * @return
     */
    @ApiOperation(value = "查询用户列表的开关状态", produces = "application/json")
    @PostMapping("/queryByUserList")
    public Result<Map<String, SysMsgSwitch>> queryByUserList(@RequestBody List<MsgSwitchRequest> userList) {
        log.info("查询用户列表的通知开关状态入参:{}", JSON.toJSONString(userList));
        return Result.data(msgSwitchService.queryByUserList(userList));
    }
}
