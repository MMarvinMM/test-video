package com.chinatower.event.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.AddTagDTO;
import com.chinatower.event.dto.AddTagRelationDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.dto.DeleteTagDTO;
import com.chinatower.common.core.dto.ModifyTagDTO;
import com.chinatower.event.repository.ISysAlarmTagsService;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-event
 * @ClassName SysAlarmTagsController
 * @Deacription 告警类型标签
 * @Author chenmf
 * @Date 2022/4/26 16:20
 * @Version 1.0
 **/
@Api(tags = "告警类型标签模块")
@RestController
@Slf4j
public class SysAlarmTagsController {

    @Autowired
    private ISysAlarmTagsService sysAlarmTagsService;

    @Autowired
    private ISysAlarmTagsTypeRelService sysAlarmTagsTypeRelService;

    /**
     * 功能描述：告警类型标签新增
     *
     * @param addTagDTO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:40
     * @version:
     */
    @TowerLog(title = "告警类型标签新增", businessType = BusinessType.INSERT)
    @ApiOperation(value = "告警类型标签新增")
    @PostMapping("alarm/tag/addTag")
    public Result addTag(@Validated @RequestBody AddTagDTO addTagDTO) {
        try {
            sysAlarmTagsService.addTag(addTagDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success("告警类型标签新增成功!");
    }

    /**
     * 功能描述：告警类型标签修改
     *
     * @param modifyTagDTO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:41
     * @version:
     */
    @TowerLog(title = "告警类型标签修改", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "告警类型标签修改")
    @PostMapping("alarm/tag/modifyTag")
    public Result modifyTag(@Validated @RequestBody ModifyTagDTO modifyTagDTO) {
        try {
            sysAlarmTagsService.modifyTag(modifyTagDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success("告警类型标签修改成功!");
    }

    /**
     * 功能描述：告警类型标签删除
     *
     * @param deleteTagDTO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:41
     * @version:
     */
    @TowerLog(title = "告警类型标签删除", businessType = BusinessType.DELETE)
    @ApiOperation(value = "告警类型标签删除")
    @PostMapping("alarm/tag/deleteTag")
    public Result deleteTag(@Validated @RequestBody DeleteTagDTO deleteTagDTO) {
        try {
            sysAlarmTagsService.deleteTag(deleteTagDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success("告警类型标签删除成功!");
    }

    /**
     * 功能描述：告警类型对应标签查询
     *
     * @param alarmTypeId
     * @return Result<List<AlarmTagsTypeRelDTO>>
     * @author zhen.yan
     * @date 2022/6/15 13:41
     * @version:
     */
    @ApiOperation(value = "告警类型对应标签查询")
    @GetMapping("alarm/tag/getTagsByAlarmType/{alarmTypeId}")
    public Result<List<AlarmTagsTypeRelDTO>> getTagsByAlarmType(@PathVariable(value="alarmTypeId") String alarmTypeId) {
        List<AlarmTagsTypeRelDTO> list = sysAlarmTagsTypeRelService.getTagsByAlarmType(alarmTypeId);
        return Result.data(list);
    }

    /**
     * 功能描述：获取全部告警类型标签
     *
     * @return Result<List<AlarmTagsTypeRelDTO>>
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @ApiOperation(value = "获取全部告警类型标签")
    @GetMapping("alarm/tag/getAllTags")
    public Result<List<AlarmTagsTypeRelDTO>> getTagsByAlarmType() {
        List<AlarmTagsTypeRelDTO> list = sysAlarmTagsService.getAllTagsByTenantId();
        return Result.data(list);
    }

    /**
     * 功能描述：获取告警类型为可见的告警类型标签
     *
     * @return Result<List<AlarmTagsTypeRelDTO>>
     * @author tianhao
     * @date 2022/11/11
     * @version:
     */
    @ApiOperation(value = "获取告警类型为可见的告警类型标签")
    @GetMapping("alarm/tag/getVisibleTags")
    public Result<List<AlarmTagsTypeRelDTO>> getVisibleTags() {
        List<AlarmTagsTypeRelDTO> list = sysAlarmTagsService.getVisibleTags();
        return Result.data(list);
    }

    /**
     * 功能描述：告警类型关联应标签保存
     *
     * @param addTagRelationDTO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @TowerLog(title = "告警类型关联标签保存", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "告警类型关联标签保存")
    @PostMapping("alarm/tag/saveTagRelation")
    public Result saveTagRelation(@Validated @RequestBody AddTagRelationDTO addTagRelationDTO) {
        try {
            sysAlarmTagsTypeRelService.saveTagRelation(addTagRelationDTO);
        } catch (Exception e) {
            return Result.fail(HttpStatus.NO_CONTENT, e.getMessage());
        }
        return Result.success("告警类型关联标签成功!");
    }

}
