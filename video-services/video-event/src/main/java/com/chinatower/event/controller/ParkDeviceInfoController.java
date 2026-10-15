package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.service.impl.ParkDeviceInfoServiceImpl;
import com.chinatower.event.vo.ParkDeviceInfoReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-event
 * 公园关联摄像机API
 *
 * @author csj
 * @date 2022-10-08
 */
@RequestMapping("/alarm/parkDevice")
@RestController
public class ParkDeviceInfoController extends TowerBaseController
{
        @Autowired
        ParkDeviceInfoServiceImpl parkDeviceInfoService;
        /**
         * 功能描述：公园摄像机列表
         * @param req
         * @return
         */
        @PostMapping("/getParkDeviceList")
        TableDataInfo getParkDeviceList(@RequestBody ParkDeviceInfoReq req){
                return getDataTable(parkDeviceInfoService.getParkDeviceList(req));
        }

        /**
         * 功能描述：新增公园摄像机列表
         * @param req
         * @return
         */
        @PostMapping("/addParkDeviceInfo")
        Result addParkDeviceInfo(@RequestBody ParkDeviceInfoReq req){
                return Result.data(parkDeviceInfoService.addParkDeviceInfo(req));
        }

        /**
         * 功能描述：新增公园摄像机列表
         * @param req
         * @return
         */
        @PostMapping("/updateParkDeviceInfo")
        Result updateParkDeviceInfo(@RequestBody ParkDeviceInfoReq req){
                return Result.data(parkDeviceInfoService.updateParkDeviceInfo(req));
        }

}
