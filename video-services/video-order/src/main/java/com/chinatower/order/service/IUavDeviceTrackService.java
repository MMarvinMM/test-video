package com.chinatower.order.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import com.chinatower.order.vo.UavDeviceSyncVO;
import com.chinatower.platform.entity.order.UavDeviceTrackVO;

import java.util.List;

/**
 * @ClassName IUavDeviceTrackService
 * @Deacription 无人机遥控记录上报
 * @Author luo.zhenghong
 * @Date 2023/6/15 14:43
 * @Version release_v1.5.95
 **/
public interface IUavDeviceTrackService {

    /**
     * 接受无人机遥控上报信息
     * @param param 遥控报文
     * @return
     */
    Result receiveUavTrack(UavDeviceSyncVO param);

    /**
     * 查询无人机轨迹信息
     * @param param
     * @return
     */
    Result<List<UavDeviceTrackVO>> qryUavDeviceTrackList(UavDeviceTrackParam param);
}
