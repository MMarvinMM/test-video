package com.chinatower.event.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.repository.IAlarmLableRelService;
import com.chinatower.event.repository.IAlarmLableService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-event
 * @ClassName AlarmLableController
 * @Deacription 告警标签
 * @Author yhb
 * @Date 2022/12/29 16:20
 * @Version 1.0
 **/
@Api(tags = "告警标签模块")
@RestController
@Slf4j
public class AlarmLableController {

    @Autowired
    private IAlarmLableService alarmLableService;

    @Autowired
    private IAlarmLableRelService alarmLableRelService;

    /**
     * 功能描述：告警标签新增
     *
     * @param addLableDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:40
     * @version:
     */
    @TowerLog(title = "告警标签新增", businessType = BusinessType.INSERT)
    @ApiOperation(value = "告警标签新增")
    @PostMapping("alarm/lable/addLable")
    public Result addTag(@Validated @RequestBody AddLableDTO addLableDTO) {
        try {
            alarmLableService.addLable(addLableDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_SUCCESS);
    }

    /**
     * 功能描述：告警类型标签修改
     *
     * @param modifyTagDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:42
     * @version:
     */
    @TowerLog(title = "告警标签修改", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "告警标签修改")
    @PostMapping("alarm/lable/modifyLable")
    public Result modifyTag(@Validated @RequestBody ModifyLableDTO modifyTagDTO) {
        try {
            alarmLableService.modifyLable(modifyTagDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_MOD_SUCCESS);
    }

    /**
     * 功能描述：告警标签删除
     *
     * @param deleteLableDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:41
     * @version:
     */
    @TowerLog(title = "告警标签删除", businessType = BusinessType.DELETE)
    @ApiOperation(value = "告警标签删除")
    @PostMapping("alarm/lable/deleteLable")
    public Result deleteTag(@Validated @RequestBody DeleteLableDTO deleteLableDTO) {
        try {
            alarmLableService.deleteLable(deleteLableDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_DEL_SUCCESS);
    }

    /**
     * 功能描述：告警对应标签查询
     *
     * @param waringOrderId
     * @return Result<List<AlarmLableRelDTO>>
     * @author hb.yang
     * @date 2022/12/29 13:41
     * @version:
     */
    @ApiOperation(value = "告警对应标签查询")
    @GetMapping("alarm/lable/getLableByAlarm/{waringOrderId}")
    public Result<List<AlarmLableRelDTO>> getAlarmTagsByOrderId(@PathVariable(value="waringOrderId") String waringOrderId) {
        log.info("告警对应标签查询，入参：{}",waringOrderId);
        List<AlarmLableRelDTO> list = alarmLableRelService.getLableByWaringOrderId(waringOrderId);
        return Result.data(list);
    }

    /**
     * 功能描述：获取全部告警类型标签
     *
     * @return Result<List<AlarmLableRelDTO>>
     * @author hb.yang
     * @date 2022/12/29 13:42
     * @version:
     */
    @ApiOperation(value = "获取全部告警标签")
    @GetMapping("alarm/lable/getAllLables")
    public Result<List<AlarmLableRelDTO>> getAllAlarmTags() {
        List<AlarmLableRelDTO> list = alarmLableService.getAllTagsByTenantId();
        return Result.data(list);
    }

    /**
     * 功能描述：告警关联应标签保存
     *
     * @param addLableRelationDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:42
     * @version:
     */
    @TowerLog(title = "告警关联标签保存", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "告警关联标签保存")
    @PostMapping("alarm/lable/saveLableRelation")
    public Result saveTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO) {
        try {
            alarmLableRelService.saveLableRelation(addLableRelationDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_JOIN_SUCCESS);
    }

    /**
     * 功能描述：行业告警关联应标签保存
     *
     * @param tradAddLableDto
     * @version:
     */
    @TowerLog(title = "行业告警关联标签保存", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "行业告警关联标签保存")
    @PostMapping("trade/alarm/lable/saveLableRelation")
    public Result tradeSaveTagRelation(@RequestBody TradAddLableDto tradAddLableDto) {
        try {
            alarmLableRelService.tradeSaveLableRelation(tradAddLableDto);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_JOIN_SUCCESS);
    }

    /**
     * 功能描述：新增告警关联应标签
     *
     * @param addLableRelationDTO
     * @return Result
     * @author hb.yang
     * @date 2022/12/29 13:42
     * @version:
     */
    @TowerLog(title = "新增告警关联应标签", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增告警关联应标签")
    @PostMapping("alarm/lable/addNewTagRelation")
    public Result addNewTagRelation(@Validated @RequestBody AddLableRelationDTO addLableRelationDTO) {
        try {
            alarmLableRelService.addNewTagRelation(addLableRelationDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success(MessageConstants.ALARM_LABLE_JOIN_SUCCESS);
    }

}
