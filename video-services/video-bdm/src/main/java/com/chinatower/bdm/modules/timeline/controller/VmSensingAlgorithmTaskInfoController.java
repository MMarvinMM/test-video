package com.chinatower.bdm.modules.timeline.controller;

import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoQueryDto;
import com.chinatower.bdm.modules.timeline.service.VmSensingAlgorithmTaskInfoService;
import com.chinatower.bdm.modules.timeline.vo.VmSensingAlgorithmTaskInfoVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-bdm
 * 遥感算法任务管理
 *
 */
@Slf4j
@Api(tags = "遥感算法任务管理")
@RestController
@AllArgsConstructor
@RequestMapping("/sensingAlgorithmTaskInfo")
public class VmSensingAlgorithmTaskInfoController extends TowerBaseController {

    /**
     * 服务对象
     */
    @Resource
    private VmSensingAlgorithmTaskInfoService vmSensingAlgorithmTaskInfoService;

    /**
     * 分页查询任务列表
     *
     * @param queryDto 筛选条件
     * @return 查询结果
     */
    @ApiOperation(value = "分页查询任务列表", notes = "支持对任务名称、算法类型、任务状态、创建人、创建时间、修改人、修改时间进行查询，支持模糊搜索")
    @PostMapping("/queryTaskByPage")
    @TowerLog(title = "queryTaskByPage", businessType = BusinessType.SELECT)
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public TableDataInfo queryByPage(@RequestBody VmSensingAlgorithmTaskInfoQueryDto queryDto) {
        return getDataTable(vmSensingAlgorithmTaskInfoService.queryByPage(queryDto));
    }

    /**
     * 通过主键查询单条数据（查看详情）
     *
     * @param taskInfoDto 包含任务ID的DTO
     * @return 单条数据
     */
    @ApiOperation(value = "查看任务详情", notes = "通过算法任务id查看详情")
    @PostMapping("/queryTaskById")
    @TowerLog(title = "queryTaskById", businessType = BusinessType.SELECT)
    public Result<VmSensingAlgorithmTaskInfoVo> queryById(@RequestBody VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        return Result.data(vmSensingAlgorithmTaskInfoService.queryById(taskInfoDto.getId()));
    }

    /**
     * 新增任务数据
     *
     * @param taskInfoDto 实体
     * @return 新增结果
     */
    @ApiOperation(value = "新增任务数据", notes = "新增遥感算法任务，字段包括图层一、图层二、任务名称、是否默认。任务名称必填，最多30字符。图层一和图层二不重复。若选择默认则将其他任务设置为非默认")
    @PostMapping("/addTask")
    @TowerLog(title = "addTask", businessType = BusinessType.INSERT)
    public Result<Integer> add(@RequestBody @Validated VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        return Result.data(vmSensingAlgorithmTaskInfoService.insert(taskInfoDto));
    }

    /**
     * 编辑任务数据
     *
     * @param taskInfoDto 实体
     * @return 编辑结果
     */
    @ApiOperation(value = "编辑任务数据", notes = "编辑遥感算法任务，字段同新增")
    @PostMapping("/editTask")
    @TowerLog(title = "editTask", businessType = BusinessType.UPDATE)
    public Result<Integer> edit(@RequestBody @Validated VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        return Result.data(vmSensingAlgorithmTaskInfoService.update(taskInfoDto));
    }

    /**
     * 删除任务数据（软删除）
     *
     * @param taskInfoDto 包含任务ID的DTO
     * @return 删除是否成功
     */
    @ApiOperation(value = "删除任务数据", notes = "将delete_flag字段设置为1")
    @PostMapping("/deleteTaskById")
    @TowerLog(title = "deleteTaskById", businessType = BusinessType.DELETE)
    public Result<Boolean> deleteById(@RequestBody VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        return Result.data(vmSensingAlgorithmTaskInfoService.deleteById(taskInfoDto.getId()));
    }

    /**
     * 设置默认任务
     *
     * @param taskInfoDto 包含任务ID的DTO
     * @return 是否成功
     */
    @ApiOperation(value = "设置默认任务", notes = "对指定任务设置为默认状态，default_flag=1")
    @PostMapping("/updateDefaultTaskById")
    @TowerLog(title = "updateDefaultTaskById", businessType = BusinessType.UPDATE)
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result<Boolean> updateDefaultTaskById(@RequestBody VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        return Result.data(vmSensingAlgorithmTaskInfoService.updateDefaultTaskById(taskInfoDto));
    }
  /**
     * 更新算法仓图片识别结果
     *
     * @return 是否成功
     */
    @ApiOperation(value = "更新算法仓图片识别结果")
    @PostMapping("/updateAlgorithmPicResult")
    @TowerLog(title = "updateAlgorithmPicResult", businessType = BusinessType.UPDATE)
    public Result<Boolean> updateAlgorithmPicResult(@RequestBody AlgorithmPicResultVO algorithmPicResultVO) {
        return Result.data(vmSensingAlgorithmTaskInfoService.updateAlgorithmPicResult(algorithmPicResultVO));
    }


}