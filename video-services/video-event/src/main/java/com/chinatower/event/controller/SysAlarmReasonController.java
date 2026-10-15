package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.dto.AlarmReasonSaveDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;
import com.chinatower.event.repository.ISysAlarmReasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-event
 * @ClassName SysAlarmReasonController
 * @Deacription 告警原因模块
 */
@Api(tags = "告警原因模块")
@RestController
@Slf4j
@RequestMapping(value = "/alarm/reason")
public class SysAlarmReasonController {

    @Autowired
    private ISysAlarmReasonService sysAlarmReasonService;

    /**
     * 功能描述：告警原因列表查询
     * @param param
     * @return
     */
    @ApiOperation(value = "告警原因列表查询")
    @PostMapping("/list")
    public Result<List<AlarmReasonVO>> list(@RequestBody AlarmReasonQueryDTO param) {
        return Result.data(sysAlarmReasonService.list(param));
    }

    /**
     * 功能描述：根据关键字查询告警原因列表
     * @param param
     * @return
     */
    @ApiOperation(value = "根据关键字查询告警原因列表")
    @PostMapping("/queryAlarmReasonList")
    public Result<List<AlarmReasonVO>> queryAlarmReasonList(@RequestBody AlarmReasonQueryDTO param) {
        return Result.data(sysAlarmReasonService.queryAlarmReasonList(param));
    }

    /**
     * 功能描述：告警原因信息保存
     * @param param
     * @return
     */
    @ApiOperation(value = "告警原因信息保存")
    @PostMapping("/save")
    public Result<String> save(@RequestBody AlarmReasonSaveDTO param) {
        try {
            sysAlarmReasonService.save(param);
            return Result.success("处理成功");
        } catch (Exception e) {
            log.error("告警原因信息保存失败,e:", e);
            return Result.fail(e.getMessage());
        }
    }
}
