package com.chinatower.video.modules.videopatrolplan.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopatrolplan.dto.PatrolAlgoRelDTO;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface IVideoPatrolPlanInfoService {


    /**
     * 增加巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "增加巡航路径", notes = "增加巡航路径")
    void addCruisePatrol(AddPatrolReq patrolDTO);

    /**
     * 删除巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "删除巡航路径", notes = "删除巡航路径")
    void delPatrol(DelPatrolReq patrolDTO);

    /**
     * 批量删除巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    void batchDelPatrolNew(DelPatrolReq patrolDTO);

    /**
     * 批量删除巡航路径
     * 该接口没有调用的地方，接口逻辑不对，后续改造需注意巡航计划id
     * @param: patrolDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/25
     * @version:
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    void batchDelPatrol(BatchDelPatrolReq patrolDTO);

    /**
     * 根据id查询巡航路径
     *
     * @param patrolDTO
     * @return
     */
    VideoPatrolVO getPatrol(DelPatrolReq patrolDTO);

    /**
     * 根据id查询巡航路径
     *
     * @param patrolDTO
     * @return
     */
    List<VideoPatrolVO> getPatrolByIdList(DelPatrolReq patrolDTO);

    /**
     * 修改巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "修改巡航路径", notes = "修改巡航路径")
    void updatePatrol(ModPatrolReq patrolDTO);

    /**
     * 停用/启用巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "停用/启用巡航路径", notes = "停用/启用巡航路径")
    Result<String> doPatrol(SttPatrolReq patrolDTO);

    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @ApiOperation(value = "查询订单巡航路径", notes = "查询订单巡航路径")
    List<PatrolDTO> queryOrderPatrolInfo(QueryOrderDeviceSetDTO presetDTO);

    /**
     * 功能描述: 查询巡航路径列表
     *
     * @param queryPatrolReq
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/28
     * @version: 1.0
     */
    @ApiOperation(value = "查询巡航路径列表", notes = "查询巡航路径列表")
    JSONObject queryPatrol(VideoQueryPatrolData queryPatrolReq);

    /**
     * 整合巡航路径
     *
     * @param vo
     * @return
     */
    @ApiOperation(value = "整合巡航路径", notes = "整合巡航路径")
    void mergePatrol(PatrolInfoVO vo);


    /**
     * 暂停恢复巡航计划接口
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "暂停恢复巡航计划接口", notes = "暂停恢复巡航计划接口")
    Result pauseResumePatrol(SttPatrolPauseReq patrolDTO);

    List<String> getExistTimePeriod(VideoBaseReq patrolDTO);


    Result<String> updateStartTimePeriod();

    Result<DeviceInfoVO> getPatrolMode(VideoBaseReq patrolDTO);

    List<Long> patrolAlgoRel(PatrolAlgoRelDTO dto);

    Long addCruisePatrolMCP(AddPatrolReq patrolDTO);
}
