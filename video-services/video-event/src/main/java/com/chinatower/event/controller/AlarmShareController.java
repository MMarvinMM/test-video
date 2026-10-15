package com.chinatower.event.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AddShareDTO;
import com.chinatower.event.entity.dto.AlarmShareDTO;
import com.chinatower.event.repository.IAlarmShareService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * video-event
 * @ClassName AlarmShareController
 * @Deacription 告警分享模块
 * @Author yhb
 * @Date 2022/12/29 16:20
 * @Version 1.0
 **/
@Api(tags = "告警分享模块")
@RestController
@Slf4j
public class AlarmShareController {

    @Autowired
    private IAlarmShareService alarmShareService;

    /**
     * 功能描述：告警分享新增
     *
     * @param addShareDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:40
     * @version:
     */
    @TowerLog(title = "告警分享新增", businessType = BusinessType.INSERT)
    @ApiOperation(value = "告警分享新增")
    @PostMapping("alarm/share/addShare")
    public Result<AlarmShareDTO> addShare(@Validated @RequestBody AddShareDTO addShareDTO) {
        AlarmShareDTO alarmShareDTO = new AlarmShareDTO();
        try {
            alarmShareDTO = alarmShareService.addAlarmShare(addShareDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.data(alarmShareDTO);
    }

}
