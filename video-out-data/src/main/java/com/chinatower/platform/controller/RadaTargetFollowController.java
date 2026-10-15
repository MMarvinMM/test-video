package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.*;
import com.chinatower.platform.consumer.service.RadarTargetFollowService;
import com.chinatower.platform.consumer.service.SensorStatusService;
import com.chinatower.platform.consumer.service.UavDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-out-data
 * @ClassName RadaTargetFollowController
 * @Description 雷达目标相关接口
 */
@RestController
@Slf4j
@Api(value = "雷达目标相关接口")
@RequestMapping("/radar/target")
public class RadaTargetFollowController {

    @Autowired
    private RadarTargetFollowService radarTargetFollowService;

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private SensorStatusService sensorStatusService;

    /**
     * 获取正在接力的设备
     * @param dto
     * @return
     */
    @ApiOperation(value = "获取正在接力的设备")
    @PostMapping("/queryRelayRadarList")
    public Result<RelayDeviceInfo> queryRelayRadarList(@RequestBody RadarDeviceStatusVO dto){
        return radarTargetFollowService.queryRelayDeviceList(dto);
    }

    /**
     * 目标回放数据查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "目标回放数据查询")
    @PostMapping("/queryRadarReviewInfoList")
    public Result<List<RadarReviewInfo>> queryRadarReviewInfoList(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryRadarReviewInfoList(dto);
    }

    /**
     * 目标历史轨迹查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "目标历史轨迹查询")
    @PostMapping("/queryRadarTrackInfoList")
    public Result<List<RadarTrackInfo>> queryRadarTrackInfoList(@RequestBody RadarTrackDTO dto){
        return radarTargetFollowService.queryRadarTrackInfoList(dto);
    }

    /**
     * 摄像机接入节点信息查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "摄像机接入节点信息查询")
    @PostMapping("/queryDeviceAccessNodeInfoList")
    public Result<List<DeviceAccessNodeVO>> queryDeviceAccessNodeInfoList(@RequestBody DeviceAccessNodeDTO dto){
        return uavDeviceService.queryDeviceAccessNodeInfoList(dto);
    }

    /**
     * 雷达告警目标信息查询
     * @return
     */
    @ApiOperation(value = "雷达告警目标信息查询")
    @GetMapping("/queryWarningTargetInfoList")
    public Result<List<WarningTargetVO>> queryWarningTargetInfoList() {
        return sensorStatusService.queryWarningTargetInfoList();
    }

    /**
     * AIS船舶画像
     * @param req
     * @return
     */
    @ApiOperation(value = "AIS船舶画像")
    @PostMapping("/shipPortrait/ais")
    public Result<RadarShipPortraitAisRsp> shipPortraitAis(@RequestBody RadarShipPortraitAisReq req){
        return radarTargetFollowService.shipPortraitAis(req);
    }


    /**
     * 雷达船舶画像
     * @param req
     * @return
     */
    @ApiOperation(value = "雷达船舶画像")
    @PostMapping("/shipPortrait/radar")
    public Result<RadarShipPortraitRadarRsp> shipPortraitRadar(@RequestBody RadarShipPortraitRadarReq req){
        return radarTargetFollowService.shipPortraitRadar(req);
    }


    /**
     * 获取海图瓦片接口
     */
    @ApiOperation(value = "获取海图瓦片接口")
    @GetMapping("/chart/fishArea/getTile")
    public Result<byte[]> getTile(@RequestParam("node") String node,
                                  @RequestParam("userName")String userName,
                                  @RequestParam("level")Integer level,
                                  @RequestParam("col")Integer col,
                                  @RequestParam("row")Integer row,
                                  @RequestParam("skipCode")String skipCode) {

        return radarTargetFollowService.getTile(node, userName, level, col, row, skipCode);
    }

    /**
     * 获取渔区瓦片接口
     */
    @ApiOperation(value = "获取渔区瓦片接口")
    @GetMapping("/chart/fishArea/getExtendTile")
    public Result<byte[]> getExtendTile(@RequestParam("tileType") String tileType,
                                        @RequestParam("userName")String userName,
                                        @RequestParam("level")Integer level,
                                        @RequestParam("col")Integer col,
                                        @RequestParam("row")Integer row) {
        return radarTargetFollowService.getExtendTile(tileType, userName, level, col, row);
    }
}
