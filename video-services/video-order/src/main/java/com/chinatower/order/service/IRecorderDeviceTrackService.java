package com.chinatower.order.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;

import java.util.List;

/**
 * 执法记录仪遥测上报接口类
 */
public interface IRecorderDeviceTrackService {

    /**
     * 执法记录轨迹上报
     * @param param
     * @return
     */
    Result receiveRecorderTrack(RecorderDeviceTrackDTO param);

    /**
     * 执法记录仪轨迹查询
     * @param param
     * @return
     */
    Result<List<RecorderDeviceTrackDTO>> getRecorderDeviceTrackList(UavDeviceTrackParam param);
}
