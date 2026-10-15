package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.layer.GeojsonStruckRes;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.artags.dataobject.ArDeviceInfo;
import com.chinatower.video.modules.query.entity.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName: VideoPtzQueryCustomAPI
 * @Description: 查询Ptz相关接口
 * @Author: zhujun
 * @Date: 2022/5/19
 **/
public interface VideoPtzQueryCustomAPI {
    /**
     * 根据定位点转动Ptz
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngle")
    Result<PresetData> turnToPtzByPointAngle(@RequestBody VideoPtzQueryReq videoPtzQueryReq);
    
    /**
     * 根据定位点转动Ptz，访问此接口不需要登录，tenantId、userId需要从入参传过来。此接口是给微创使用。
     * R-ZBZB-202211040970722_卫星火点告警优化：需要君南侧提供查询指定租户，指定行业，指定经纬度3公里范围内最新的摄像机信息的接口，以及将摄像机联动看向告警点接口
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngle2")
    Result<PresetData> turnToPtzByPointAngle2(@RequestBody VideoPtzQueryReq videoPtzQueryReq);

    /**
     * 指哪打哪
     * @param focusThisPointReq
     * @return
     */
    @PostMapping("/video/query/focusThisPoint")
    Result<List<PresetData>> focusThisPoint(@RequestBody FocusThisPointReq focusThisPointReq);

    /**
     * 获取设备ptz值并计算
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/getCompass")
    Result<PresetDetailData> getCompass(@RequestBody VideoPtzQueryReq videoPtzQueryReq);

    @PostMapping("/video/query/getPercentByPtzBatch")
    Result<VideoPtzRes> getPercentByPtzBatch(@RequestBody VideoPtzRes videoPtzRes);
    
    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算ptz
     *
     * @param videoPtzReq
     * @return Result<VideoPtzRes>
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    @PostMapping("/video/query/getPtzByMassPointBatch")
    Result<VideoPtzRes> getPtzByMassPointBatch(@RequestBody VideoPtzReq videoPtzReq);
    
    /**
     * 视图联动，以地找视频，根据地块顶点/中心点的经纬度批量计算顶点的百分比坐标和中心点的ptz
     *
     * @param videoPtzReq
     * @return Result<VideoPtzRes>
     * @author wanglei
     * @date 2023/08/18
     * @version release_v1.6.10
     */
    @PostMapping("/video/query/getPercentPtzByMassPointBatch")
    Result<VideoPtzRes> getPercentPtzByMassPointBatch(@RequestBody VideoPtzReq videoPtzReq);
    
    /**
     * 视图联动，判断该摄像机是否有有效参考点
     *
     * @param videoPtzReq
     * @return Result<String>
     * @author wanglei
     * @date 2023/08/23
     * @version release_v1.6.10
     */
    @PostMapping("/video/query/pointsValid")
    Result<String> pointsValid(@RequestBody VideoPtzReq videoPtzReq);

    /**
     * Ar实景大屏，查询有效参考点数据
     * @param videoPtzReq
     * @return
     */
    @PostMapping("/video/query/pointsValidSuccess")
    Result<List<DeviceRefPointVO>> pointsValidSuccess(@RequestBody VideoPtzReq videoPtzReq);

    /**
     * 批量判断摄像机是否有有效参考点
     *
     * @param list
     * @return Result<List<DevicePointsValidVO>>
     * @author hued
     * @date 2023年9月14日09:48:33
     * @version release_v1.6.10
     */
    @PostMapping("/video/query/pointsValidBatch")
    Result<List<DevicePointsValidVO>> pointsValidBatch(@RequestBody List<String> list);

    /**
     * 根据定位点+无人机飞行高度，计算ptz
     * @param videoPtzQueryReq
     * @return
     */
    @PostMapping("/video/query/turnToPtzByPointAngleFly")
    Result<PresetData> turnToPtzByPointAngleFly(@RequestBody VideoPtzQueryReq videoPtzQueryReq);

    @PostMapping("/video/query/getPercentPtzByMassPointBatchNew")
    Result<VideoPtzRes> getPercentPtzByMassPointBatchNew(@RequestBody VideoPtzReq videoPtzReq);

    /**
     * 标签随动
     * @param arTagMoveReq
     * @return
     */
    @PostMapping("/video/query/getArTagMoveList")
    Result<List<ArDeviceInfo>> getArTagMoveList(@RequestBody ArTagMoveReq arTagMoveReq);

    @PostMapping("/video/query/getDeviceVisibleLonLat")
    Result<List<ArTagMoveRes>> getDeviceVisibleLonLat(@RequestBody ArTagMoveReq arTagMoveReq);

    @PostMapping("/video/query/getPolygonPatchConfigList")
    Result<GeojsonStruckRes> getPolygonPatchConfigList(@RequestBody ArLayerTagReq arLayerTagReq);
}
