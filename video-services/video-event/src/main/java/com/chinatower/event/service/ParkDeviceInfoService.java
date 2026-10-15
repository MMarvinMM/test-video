package com.chinatower.event.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.vo.ParkDeviceInfoReq;
import com.chinatower.event.vo.ParkDeviceInfoVo;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface ParkDeviceInfoService {
    /**
     * 公园摄像机列表
     * @param req
     * @return
     */
    List<ParkDeviceInfoVo> getParkDeviceList(ParkDeviceInfoReq req);

    /**
     * 新增公园摄像机列表
     * @param req
     * @return
     */
    Result addParkDeviceInfo(ParkDeviceInfoReq req);

    /**
     * 新增公园摄像机列表
     * @param req
     * @return
     */
    @PostMapping("/updateParkDeviceInfo")
    Result updateParkDeviceInfo(ParkDeviceInfoReq req);
}
