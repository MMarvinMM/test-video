package com.chinatower.receive.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.receive.service.IOperationJudgeNotificationService;
import com.chinatower.receive.vo.dto.WakeUpTowerRequest;
import com.chinatower.receive.vo.entity.WakeUpTowerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-receive
 */
@RestController
@RequestMapping("/alarm/notification")
@Api(value = "小塔通知管理", tags = "小塔通知管理")
@Slf4j
public class TowerNotificationController {

    @Autowired
    private IOperationJudgeNotificationService notificationService;

    /**
     * 唤起小塔通知
     *
     * @param request 唤起小塔请求参数
     * @return 操作结果
     */
    @PostMapping("/wake-up")
    @ApiOperation(value = "唤起小塔通知", notes = "根据规则编码列表唤起小塔通知")
    public Result<WakeUpTowerDTO> wakeUpTower(@RequestBody @ApiParam("唤起小塔请求参数") WakeUpTowerRequest request) {
        try {
            WakeUpTowerDTO result = notificationService.wakeUpTowerByRule(request.getRuleCodes());
            return Result.data(result);
        } catch (Exception e) {
            log.error("唤起小塔通知异常", e);
            return Result.fail("唤起小塔通知异常：" + e.getMessage());
        }
    }


}
