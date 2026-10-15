package com.chinatower.bdm.modules.timeline.controller;

import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmTimelineInfoQueryDto;
import com.chinatower.bdm.modules.timeline.service.VmTimelineInfoService;
import com.chinatower.bdm.modules.timeline.vo.VmTimelineInfoVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * video-bdm
 * 遥感时间轴管理
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@Slf4j
@Api(tags = "遥感时间轴管理")
@RestController
@AllArgsConstructor
@RequestMapping("/timelineInfo")
public class VmTimelineInfoController extends TowerBaseController {
    /**
     * 服务对象
     */
    @Resource
    private VmTimelineInfoService vmTimelineInfoService;

    /**
     * 分页查询时间轴
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    @ApiOperation(value = "分页查询时间轴", notes = "分页查询时间轴")
    @PostMapping("/queryTimelineByPage")
    @TowerLog(title = "queryTimelineByPage", businessType = BusinessType.SELECT)
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public TableDataInfo queryByPage(@RequestBody VmTimelineInfoQueryDto vmTimelineInfoQueryDto) {
        return getDataTable(vmTimelineInfoService.queryByPage(vmTimelineInfoQueryDto));
    }

    /**
     * 分页查询时间轴详情
     *
     * @param vmTimelineInfoQueryDto 筛选条件
     * @return 查询结果
     */
    @ApiOperation(value = "分页查询带有时间轴详情", notes = "分页查询带有时间轴详情")
    @PostMapping("/queryTimelineWithDetail")
    @TowerLog(title = "queryTimelineWithDetail", businessType = BusinessType.SELECT)
    @DataScope(tenantColumn = "d.tenant_id", industryCode = "d.industry_code")
    public TableDataInfo queryWithDetail(@RequestBody VmTimelineInfoQueryDto vmTimelineInfoQueryDto) {
        return getDataTable(vmTimelineInfoService.queryWithDetail(vmTimelineInfoQueryDto));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据", notes = "通过主键查询单条数据")
    @PostMapping("/queryTimelineById")
    @TowerLog(title = "queryTimelineById", businessType = BusinessType.SELECT)
    public Result<VmTimelineInfoVo> queryById(@RequestBody VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.queryById(vmTimelineInfoDto.getTimelineId()));
    }

    /**
     * 新增时间轴数据
     *
     * @param vmTimelineInfoDto 实体
     * @return 新增结果
     */
    @ApiOperation(value = "新增时间轴数据", notes = "新增时间轴数据")
    @PostMapping("/addTimeline")
    @TowerLog(title = "addTimeline", businessType = BusinessType.INSERT)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result<Integer> add(@RequestBody @Validated VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.insert(vmTimelineInfoDto));
    }

    /**
     * 编辑时间轴数据
     *
     * @param vmTimelineInfoDto 实体
     * @return 编辑结果
     */
    @ApiOperation(value = "编辑时间轴数据", notes = "编辑时间轴数据")
    @PostMapping("/editTimeline")
    @TowerLog(title = "editTimeline", businessType = BusinessType.UPDATE)
    public Result<Integer> edit(@RequestBody @Validated VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.update(vmTimelineInfoDto));
    }

    /**
     * 删除时间轴数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @ApiOperation(value = "删除时间轴数据", notes = "删除时间轴数据")
    @PostMapping("/deleteTimelineById")
    @TowerLog(title = "deleteTimelineById", businessType = BusinessType.DELETE)
    public Result<Boolean> deleteById(@RequestBody VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.deleteById(vmTimelineInfoDto.getTimelineId()));
    }

    /**
     * 设置默认
     *
     * @return 单条数据
     */
    @ApiOperation(value = "设置默认", notes = "设置默认")
    @PostMapping("/updateDefaultTimelineById")
    @TowerLog(title = "updateDefaultTimelineById", businessType = BusinessType.UPDATE)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result<Boolean> updateDefaultTimelineById(@RequestBody VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.updateDefaultTimelineById(vmTimelineInfoDto));
    }

    /**
     * 拖拽排序
     * 更新时间轴的排序号，兼容老数据（sortNum为NULL的情况）
     *
     * @param vmTimelineInfoDto 时间轴信息，包含timelineId和sortNum
     * @return 更新结果
     */
    @ApiOperation(value = "拖拽排序", notes = "拖拽排序，兼容老数据")
    @PostMapping("/updateSortNumTimelineById")
    @TowerLog(title = "updateSortNumTimelineById", businessType = BusinessType.UPDATE)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result<Boolean> updateSortNumTimelineById(@RequestBody VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.updateSortNumTimelineById(vmTimelineInfoDto));
    }

    /**
     * 查询默认遥感时间轴
     *
     * @return 默认遥感时间轴
     */
    @ApiOperation(value = "查询默认遥感时间轴", notes = "查询默认遥感时间轴")
    @PostMapping("/queryDefaultTimeline")
    @TowerLog(title = "queryDefaultTimeline", businessType = BusinessType.SELECT)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result<VmTimelineInfoVo> queryDefaultTimeline(@RequestBody VmTimelineInfoDto vmTimelineInfoDto) {
        return Result.data(vmTimelineInfoService.queryDefaultTimeline(vmTimelineInfoDto));
    }


}
