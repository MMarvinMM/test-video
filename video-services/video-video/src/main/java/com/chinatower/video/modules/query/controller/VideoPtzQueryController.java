package com.chinatower.video.modules.query.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.layer.GeojsonStruckRes;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.VideoPtzQueryCustomAPI;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.artags.dataobject.ArDeviceInfo;
import com.chinatower.video.modules.query.entity.*;
import com.chinatower.video.modules.query.service.VideoPtzQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频ptz controller
 * Created by zhujun on 2022/5/19.
 */
@Api(value = "视频PTZ操作接口-Api",tags = "视频PTZ操作接口")
@Slf4j
@RestController
public class VideoPtzQueryController implements VideoPtzQueryCustomAPI {

    @Autowired
    private VideoPtzQueryService videoPtzQueryService;

    @ApiOperation(value = "根据定位点转动Ptz",notes = "根据定位点转动Ptz")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPtzQueryReq",value = "根据定位点转动Ptz请求实体",dataType = "VideoPtzQueryReq",required = true,paramType = "query")
    })
    @Override
    public Result<PresetData> turnToPtzByPointAngle(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        log.info("turnToPtzByPointAngle:{} from {},{} to {},{}",videoPtzQueryReq.getDeviceCode(),
                videoPtzQueryReq.getCameraLon(),videoPtzQueryReq.getCameraLat(),videoPtzQueryReq.getPointLon(),videoPtzQueryReq.getPointLat());
        try{
            res = videoPtzQueryService.turnToPtzByPointAngle(videoPtzQueryReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("根据定位点转动Ptz:" , e);
        }
        log.info("根据定位点转动Ptz返回:{}", JSON.toJSONString(res));
        return res;
    }
    
    @ApiOperation(value = "根据定位点转动Ptz",notes = "根据定位点转动Ptz")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPtzQueryReq",value = "根据定位点转动Ptz请求实体",dataType = "VideoPtzQueryReq",required = true,paramType = "query")
    })
    @Override
    public Result<PresetData> turnToPtzByPointAngle2(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        log.info("turnToPtzByPointAngle:{} from {},{} to {},{}",videoPtzQueryReq.getDeviceCode(),
                videoPtzQueryReq.getCameraLon(),videoPtzQueryReq.getCameraLat(),videoPtzQueryReq.getPointLon(),videoPtzQueryReq.getPointLat());
        try{
            res = videoPtzQueryService.turnToPtzByPointAngle2(videoPtzQueryReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("根据定位点转动Ptz:" , e);
        }
        log.info("根据定位点转动Ptz返回:{}", JSON.toJSONString(res));
        return res;
    }

    @ApiOperation(value = "指哪打哪",notes = "根据定位点将x千米内的租用摄像头转向此处")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPtzQueryReq",value = "根据定位点转动Ptz请求实体",dataType = "FocusThisPointReq",required = true,paramType = "query")
    })
    @Override
    public Result<List<PresetData>> focusThisPoint(@RequestBody FocusThisPointReq focusThisPointReq) {
        Result<List<PresetData>>  res;
        log.info("指哪打哪focusThisPoint入参:{}km from {},{}",focusThisPointReq.getFocusLimit(),focusThisPointReq.getPointLon(),focusThisPointReq.getPointLat());
        try{
            res = videoPtzQueryService.focusThisPoint(focusThisPointReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("指哪打哪focusThisPoint:" , e);
        }
        log.info("指哪打哪focusThisPoint返回:{}", JSON.toJSONString(res));
        return res;
    }

    @ApiOperation(value = "指北针",notes = "获取设备ptz值并计算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "getCompass",value = "获取设备ptz值并计算",dataType = "VideoPtzQueryReq",required = true,paramType = "query")
    })
    @Override
    public Result<PresetDetailData> getCompass(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetDetailData>  res;
        log.info("指北针getCompass入参:{}",JSON.toJSONString(videoPtzQueryReq));
        try{
            res = videoPtzQueryService.getCompass(videoPtzQueryReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("指北针getCompass:" , e);
        }
        log.info("指北针getCompass返回:{}", JSON.toJSONString(res));

        return res;
    }

    @ApiOperation(value = "视图联动",notes = "根据ptz批量计算百分比坐标")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "getPercentByPtzBatch",value = "根据ptz批量计算百分比坐标",dataType = "VideoPtzRes",required = true,paramType = "query")
    })
    @Override
    public Result<VideoPtzRes> getPercentByPtzBatch(@RequestBody VideoPtzRes videoPtzRes) {
        try{
            VideoPtzRes res = videoPtzQueryService.getPercentByPtzBatch(videoPtzRes);
            return Result.data(res);
        }catch (Exception e){
            log.error("getPercentByPtzBatch异常:" , e);
            return Result.fail("根据ptz批量计算百分比坐标异常！");
        }
    }
    
    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算ptz
     *
     * @param videoPtzReq
     * @return Result<VideoPtzRes>
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    @ApiOperation(value = "视图联动",notes = "根据地块顶点/中心点的经纬度批量计算ptz")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "getPtzByMassPointBatch",value = "根据地块顶点/中心点的经纬度批量计算ptz",dataType = "VideoPtzRes",required = true,paramType = "query")
    })
    @Override
    public Result<VideoPtzRes> getPtzByMassPointBatch(@RequestBody VideoPtzReq videoPtzReq) {
        VideoPtzRes res = videoPtzQueryService.getPtzByMassPointBatch(videoPtzReq);
        res.setDeviceModel(null);
        return Result.data(res);
    }
    
    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算顶点的百分比坐标和中心点的ptz
     *
     * @param videoPtzReq
     * @return Result<VideoPtzRes>
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    @ApiOperation(value = "视图联动",notes = "根据地块顶点/中心点的经纬度批量计算顶点的百分比坐标和中心点的ptz")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "getPercentPtzByMassPointBatch",value = "根据地块顶点/中心点的经纬度批量计算顶点的百分比坐标和中心点的ptz",dataType = "VideoPtzRes",required = true,paramType = "query")
    })
    @Override
    public Result<VideoPtzRes> getPercentPtzByMassPointBatch(@RequestBody VideoPtzReq videoPtzReq) {
        VideoPtzRes res = videoPtzQueryService.getPercentPtzByMassPointBatch(videoPtzReq);
        return Result.data(res);
    }
    
    /**
     * 视图联动，判断该摄像机是否有有效参考点
     *
     * @param videoPtzReq
     * @return Result<String>
     * @author wanglei
     * @date 2023/08/23
     * @version release_v1.6.10
     */
    @ApiOperation(value = "视图联动",notes = "视图联动，判断该摄像机是否有有效参考点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pointsValid",value = "视图联动，判断该摄像机是否有有效参考点",dataType = "String",required = true,paramType = "query")
    })
    @Override
    public Result<String> pointsValid(@RequestBody VideoPtzReq videoPtzReq)
    {
    	return videoPtzQueryService.pointsValid(videoPtzReq);
    }

    @Override
    public Result<List<DeviceRefPointVO>> pointsValidSuccess(@RequestBody VideoPtzReq videoPtzReq)
    {
        return videoPtzQueryService.pointsValidSuccess(videoPtzReq);
    }

    /**
     * 批量判断摄像机是否有有效参考点
     *
     * @param list
     * @return Result<List<DevicePointsValidVO>>
     * @author hued
     * @date 2023年9月14日09:48:33
     * @version release_v1.6.10
     */
    @ApiOperation(value = "批量判断摄像机是否有有效参考点",notes = "批量判断摄像机是否有有效参考点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pointsValidBatch",value = "批量判断摄像机是否有有效参考点",dataType = "DevicePointsValidVO",required = true,paramType = "query")
    })
    @Override
    public Result<List<DevicePointsValidVO>> pointsValidBatch(List<String> list){
        return videoPtzQueryService.pointsValidBatch(list);
    }

    @ApiOperation(value = "根据定位点+无人机飞行高度，计算ptz",notes = "根据定位点+无人机飞行高度，计算ptz")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPtzQueryReq",value = "根据定位点+无人机飞行高度，计算ptz请求实体",dataType = "VideoPtzQueryReq",required = true,paramType = "query")
    })
    @Override
    public Result<PresetData> turnToPtzByPointAngleFly(@RequestBody VideoPtzQueryReq videoPtzQueryReq) {
        Result<PresetData> res;
        log.info("turnToPtzByPointAngleFly:{} from {},{} to {},{}",videoPtzQueryReq.getDeviceCode(),
                videoPtzQueryReq.getCameraLon(),videoPtzQueryReq.getCameraLat(),videoPtzQueryReq.getPointLon(),videoPtzQueryReq.getPointLat());
        try{
            res = videoPtzQueryService.turnToPtzByPointAngleFly(videoPtzQueryReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + e.getMessage());
            log.error("根据定位点+无人机飞行高度，计算ptz:" , e);
        }
        log.info("根据定位点+无人机飞行高度，计算ptz返回:{}", JSON.toJSONString(res));
        return res;
    }

    @Override
    public Result<VideoPtzRes> getPercentPtzByMassPointBatchNew(@RequestBody VideoPtzReq videoPtzReq) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        videoPtzReq.setTenantId(currentUser.getUser().getTenantId());
        VideoPtzRes res = videoPtzQueryService.getPercentPtzByMassPointBatchNew(videoPtzReq);
        return Result.data(res);
    }

    @Override
    public Result<List<ArDeviceInfo>> getArTagMoveList(@Validated @RequestBody ArTagMoveReq arTagMoveReq){
        return videoPtzQueryService.getArTagMoveList(arTagMoveReq);
    }

    @Override
    public Result<List<ArTagMoveRes>> getDeviceVisibleLonLat(@RequestBody ArTagMoveReq arTagMoveReq){
        return videoPtzQueryService.getDeviceVisibleLonLat(arTagMoveReq);
    }

    @Override
    public Result<GeojsonStruckRes> getPolygonPatchConfigList(@RequestBody ArLayerTagReq arLayerTagReq){
        return videoPtzQueryService.getPolygonPatchConfigList(arLayerTagReq);
    }

}
