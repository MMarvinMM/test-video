package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.AisDetailsInfo;
import com.chinatower.outdata.api.entity.FixedTargetDTO;
import com.chinatower.outdata.api.entity.FixedTargetRecord;
import com.chinatower.outdata.api.entity.GlobalTrackDTO;
import com.chinatower.outdata.api.entity.GlobalTrackTrackMap;
import com.chinatower.outdata.api.entity.RadarReviewInfo;
import com.chinatower.outdata.api.entity.RadarTrackDTO;
import com.chinatower.outdata.api.entity.ThermalMapInfo;
import com.chinatower.outdata.api.entity.TrackAllInfo;
import com.chinatower.outdata.api.entity.UpdateFixedTargetIdList;
import com.chinatower.platform.consumer.service.RadarTargetFollowService;
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
 * video-out-data
 * @ClassName TargetTrajectoryController
 * @Description 目标轨迹相关接口
 */
@RestController
@Slf4j
@Api(value = "目标轨迹相关接口")
@RequestMapping("/external")
public class TargetTrajectoryController {

    @Autowired
    private RadarTargetFollowService radarTargetFollowService;

    /**
     * 雷达-AIS目标搜索
     * @param dto
     * @return
     */
    @ApiOperation(value = "AIS目标搜索")
    @PostMapping("/target/search")
    public Result<List<RadarReviewInfo>> search(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryAisSearch(dto);
    }

    /**
     * 雷达-目标信息查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "目标信息查询")
    @PostMapping("/target/getDetails")
    public Result<AisDetailsInfo> getDetails(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryAisDetails(dto);
    }

    /**
     * 雷达-轨迹查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "轨迹查询")
    @PostMapping("/trajectory/track")
    public Result<List<TrackAllInfo>> queryTrajectoryTrack(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryTrajectoryTrack(dto);
    }

    /**
     * 雷达-轨迹点信息
     * @param dto
     * @return
     */
    @ApiOperation(value = "轨迹点信息")
    @PostMapping("/trajectory/trackPoint")
    public Result<TrackAllInfo> queryTrajectoryTrackPoint(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryTrajectoryTrackPoint(dto);
    }

    /**
     * 雷达-轨迹分析
     * @param dtoList
     * @return
     */
    @ApiOperation(value = "轨迹分析")
    @PostMapping("/trajectory/trackPointChartQuery")
    public Result<List<TrackAllInfo>> trackPointChartQuery(@RequestBody List<RadarTrackDTO> dtoList){
        return radarTargetFollowService.trackPointChartQuery(dtoList);
    }

    /**
     *
     * 雷达-热力图
     * @param dto
     * @return
     */
    @ApiOperation(value = "热力图")
    @PostMapping("/trajectory/track/aisDaily/thermalMap")
    public Result<List<ThermalMapInfo>> queryTrajectoryThermalMap(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryTrajectoryThermalMap(dto);
    }


    /**
     *
     * 固定目标信息查询 doc-4.29
     * @param dto
     * @return
     */
    @ApiOperation(value = "雷达固定目标信息查询")
    @PostMapping("/target/fixedTarget")
    public Result<FixedTargetRecord> queryFixedTarget(@RequestBody FixedTargetDTO dto){
        return radarTargetFollowService.queryFixedTarget(dto);
    }


    /**
     *
     * 修改固定目标状态 doc-4.30
     * @param dto
     * @return
     */
    @ApiOperation(value = "4.30.修改固定目标状态")
    @PostMapping("/target/updateFixedTarget")
    public Result<UpdateFixedTargetIdList> updateFixedTarget(@RequestBody FixedTargetDTO dto){
        return radarTargetFollowService.updateFixedTarget(dto);
    }


    /**
     *
     * 4.31.获取全局轨迹 doc-4.31
     * @param dto
     * @return
     */
    @ApiOperation(value = "4.31.获取全局轨迹")
    @PostMapping("/trajectory/globalTrack")
    public Result<GlobalTrackTrackMap> globalTrack(@RequestBody GlobalTrackDTO dto){
        return radarTargetFollowService.globalTrack(dto);
    }



}
