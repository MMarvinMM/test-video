package com.chinatower.order.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.factory.UavDeviceApiFallbackFactory;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import com.chinatower.platform.entity.order.UavDeviceTrackVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * @ClassName IUavDeviceApiService
 * @Deacription 无人机信息接口
 * @Author luo.zhenghong
 * @Date 2023/7/31 15:21
 **/
@FeignClient(contextId = "uavDeviceApiService", value = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = UavDeviceApiFallbackFactory.class)
public interface IUavDeviceApiService {

    @PostMapping("/device/uav/qryUavDeviceTrackList")
    Result<List<UavDeviceTrackVO>> qryUavDeviceTrackList(@RequestBody @Validated UavDeviceTrackParam param);


    /**
     * 无人机轨迹上报
     * @param req
     * @return
     */
    @PostMapping("/device/uav/track")
    Result receiveUavTrack(@RequestBody String req);
}
