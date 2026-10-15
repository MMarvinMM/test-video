package com.chinatower.platform.project.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.project.service.PushClientEquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-msg
 */
@Api(tags = "极光推送接口")
@Slf4j
@RestController
@RequestMapping("/bussiness")
public class JpushController {

    @Autowired
    private PushClientEquipmentService pushClientEquipmentService;

    /**
     * 根据用户id推送消息
     * @param pushNews
     * @return
     */
    @ApiOperation(value = "根据用户id推送消息", produces = "application/json")
    @PostMapping("/jush/checkShortMessage")
    public Result pushNewsByUserId(@RequestBody JgPushRequest pushNews) {
        int result = pushClientEquipmentService.pushPersonByUserId(pushNews);
        return result >0 ? Result.success("发送成功") : Result.fail("发送失败");
    }


    /**
     * 批量推送消息 (通知和消息)
     * 批量单推
     * @param pushNews 推送内容
     * @return success/fail
     */
    @ApiOperation(value = "根据用户id集合 批量推送通知", produces = "application/json")
    @PostMapping("/jush/batchPushNews")
    public Result batchPushNews(@RequestBody List<JgPushRequest> pushNews) {
        pushClientEquipmentService.batchPushNews(pushNews);
        log.info("推送成功");
        return Result.success("发送成功");
    }

}
