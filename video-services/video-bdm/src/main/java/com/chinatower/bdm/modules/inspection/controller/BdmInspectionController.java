package com.chinatower.bdm.modules.inspection.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspection.entity.DeviceGridAreaTreeVO;
import com.chinatower.bdm.modules.inspection.entity.DeviceGridReqDTO;
import com.chinatower.bdm.api.entity.QueryCountTrend;
import com.chinatower.bdm.modules.inspection.service.BdmInspectionService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * video-bdm
 * @Author：zl
 * @Description：巡查计划相关接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
 */
@Api(value = "巡查计划相关接口-Api",tags = "巡查计划菜单后端接口")
@Slf4j
@RestController
public class BdmInspectionController extends TowerBaseController {
    @Resource
    BdmInspectionService bdmInspectionService;

    /**
     * 巡查计划配置保存与编辑接口
     * @Author：zl
     * @param inspectionConfVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    @ApiOperation(value = "巡查计划配置保存与编辑接口",notes = "巡查计划配置保存与编辑接口")
    @PostMapping("/inspectionConf/add")
    public Result addConfInfo(@RequestBody InspectionConfVO inspectionConfVO) throws ParseException {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        inspectionConfVO.setTenantId(user.getTenantId());
        inspectionConfVO.setCreator(SecurityUtils.getOperator());
        inspectionConfVO.setAppVerCode(user.getAppVerCode());
        inspectionConfVO.setIndustryCode(user.getIndustryCode());
        return bdmInspectionService.addConfInfo(inspectionConfVO);
    }

    /**
     * 巡查计划单条数据获取
     * @Author：zl
     * @param confId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    @ApiOperation(value = "巡查计划单条数据获取",notes = "巡查计划单条数据获取")
    @GetMapping("/inspectionConf/findOne")
    public Result findOneInfo(String confId,String flag) {
        return bdmInspectionService.findOneInfo(confId,flag);
    }

    /**
     * 巡查计划获取列表数据
     * @Author：zl
     * @param inspectionConfParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
        @ApiOperation(value = "巡查计划获取列表数据",notes = "巡查计划获取列表数据")
    @PostMapping(value = "/inspectionConf/getConfList")
    public TableDataInfo getConfList(@RequestBody InspectionConfParam inspectionConfParam) {
        startPage();
        return getDataTable(bdmInspectionService.getConfList(inspectionConfParam));
    }

    /**
     * 获取租户-网格-组织-设备下的行政区域
     * @Author：zl
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "获取租户-网格-组织-设备下的行政区域",notes = "巡查任务获取列表数据")
    @PostMapping(value = "/inspectionConf/queryGridAreaTree")
    public Result<List<DeviceGridAreaTreeVO>> queryGridAreaTree(DeviceGridReqDTO deviceGridReqDTO) {
        //1，获取当前登录人所属网格
        //2. 网格关联组织-设备关系表 获取所有设备
        //3. 关联设备信息表 获取设备所在的行政区域
        List<DeviceGridAreaTreeVO> list = bdmInspectionService.queryGridAreaTree(deviceGridReqDTO);
        return Result.data(list);
    }


    /**
     * 巡查计划删除接口
     * @Author：zl
     * @param inspctionConfStatusParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "巡查计划删除接口",notes = "巡查计划删除接口")
    @PostMapping("/inspectionConf/deleteConf")
    public Result deleteConf(@RequestBody InspctionConfStatusParam inspctionConfStatusParam) throws ParseException {
        inspctionConfStatusParam.setModifier(SecurityUtils.getLoginUserId());
        return bdmInspectionService.deleteConf(inspctionConfStatusParam);
    }

    /**
     * 巡查历史列表数据
     * @Author：zl
     * @param inspectionTaskParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "巡查历史列表数据",notes = "巡查历史列表数据")
    @PostMapping(value = "/inspectionTask/getTaskList")
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "a.keeper_id")
    public TableDataInfo getTaskList(@RequestBody InspectionTaskParam inspectionTaskParam) {
        Integer pageNum = inspectionTaskParam.getPageNum();
        Integer pageSize = inspectionTaskParam.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        return getDataTable(bdmInspectionService.getTaskList(inspectionTaskParam));
    }


    /**
     * 我的待办接口
     * @Author：zl
     * @param inspectionTaskNoPageParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "我的待办",notes = "我的待办")
    @PostMapping(value = "/inspectionTask/getToDoTaskList")
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public Result<List<InspectionTaskListVO>> getToDoTaskList(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam) {
        return  Result.data(bdmInspectionService.getToDoTaskList(inspectionTaskNoPageParam));
    }

    /**
     * 巡查历史列表数据(扩展)
     * @Author：dencycheng
     * @param inspectionTaskParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "巡查历史列表数据扩展",notes = "巡查历史列表数据扩展")
    @PostMapping(value = "/inspectionTask/getTaskListExtend")
    public Result<TableDataInfo> getTaskListExtend(@RequestBody InspectionTaskParamExtend inspectionTaskParam) {
        startPage();
        return Result.data(getDataTable(bdmInspectionService.getTaskListExtend(inspectionTaskParam)));
    }

    /**
     * 巡查历史列表数据(扩展)
     * @Author：dencycheng
     * @param queryCountTrend
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__按日期统计用户巡查次数趋势
     */
    @ApiOperation(value = "按日期统计用户巡查次数趋势",notes = "按日期统计用户巡查次数趋势")
    @PostMapping(value = "/inspectionTask/inspectionCountTrend")
    public Result<List<InspectionCountResponse>> inspectionCountTrend(@RequestBody QueryCountTrend queryCountTrend) {
        return Result.data(bdmInspectionService.inspectionCountTrend(queryCountTrend));
    }

    /**
     * 巡查历史详情
     * @Author：liuxt
     * @param taskId
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "巡查历史详情",notes = "巡查历史详情")
    @GetMapping("/inspectionTask/getTaskInfo")
    public Result<InspectionTaskVO> getTaskInfo(String taskId){
        return bdmInspectionService.getTaskInfo(taskId);
    }


    /**
     * 根据网格id查询巡查任务详情(取巡查日期最新的一条)
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "根据网格id查询巡查任务详情",notes = "巡查历史详情")
    @PostMapping("/inspectionTask/getTaskInfoByCondition")
    public Result<InspectionTaskVO> getTaskInfoByCondition(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam){
        return bdmInspectionService.getTaskInfoByCondition(inspectionTaskNoPageParam);
    }


    /**
     * 查询巡查轨迹
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "查询巡查轨迹",notes = "查询巡查轨迹")
    @PostMapping("/inspectionTask/getTaskTrail")
    public Result<List<List<GridKeeperTrailNew>>> getTaskTrail(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam){
        return bdmInspectionService.getTaskTrail(inspectionTaskNoPageParam);
    }

    /**
     * app端/pc端获取巡查任务列表
     * @Author：liuxt
     * @param inspectionTaskParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "app端/pc端获取巡查任务列表")
    @PostMapping(value = "/inspectionTask/getTaskListByKeeper")
    public TableDataInfo getTaskListByKeeper(@RequestBody InspectionTaskParam inspectionTaskParam){
        return getDataTable(bdmInspectionService.getTaskListByKeeper(inspectionTaskParam));
    }


    /**
     * 行业大屏获取巡查任务列表
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "行业大屏获取巡查任务列表")
    @PostMapping(value = "/inspectionTask/getTaskListByCondition")
    public Result<List<InspectionTaskListVO>> getTaskListByCondition(@RequestBody InspectionTaskNoPageParam inspectionTaskNoPageParam){
        return Result.data(bdmInspectionService.getTaskListByCondition(inspectionTaskNoPageParam));
    }

    /**
     * 认领任务
     * @Author：zl
     * @param taskId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "认领任务")
    @GetMapping("/inspectionTask/claimTask")
    public Result claimTask(String taskId){
        return bdmInspectionService.claimTask(taskId);
    }

    /**
     * 处理巡查任务
     * @Author：zl
     * @param
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "处理巡查任务")
    @PostMapping("/inspectionTask/claimTask")
    public Result claimTask(@RequestBody InspectionTaskParamVO inspectionTaskParamVO){
        return bdmInspectionService.claimTask(inspectionTaskParamVO);
    }


    /**
     * 查询巡查人列表
     * @Author：liuxt
     * @param inspectionTaskVO
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @ApiOperation(value = "查询巡查人列表",notes = "查询巡查人列表")
    @PostMapping("/inspectionTask/getUserInfoList")
    public Result<List<InspectionTaskVO>> getUserInfoList(@RequestBody InspectionTaskVO inspectionTaskVO){
        log.info("查询巡查人列表:"+ JSONObject.toJSONString(inspectionTaskVO));
        return bdmInspectionService.getUserInfoList(inspectionTaskVO);
    }
}
