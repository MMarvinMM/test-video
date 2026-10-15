package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UserDeviceCollectionDTO;
import com.chinatower.device.repository.UserDeviceCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-device
 * @Name UserDevicesCollectionController
 * @Description
 * @Author trk
 * @Date 2022/04/2022/4/13 09 : 37
 */
@Api(tags = "用户设备收藏相关操作")
@RestController
public class UserDevicesCollectionController {
    @Autowired
    private UserDeviceCollectionService userDeviceCollectionService;

    /**
     * 用户设备新增/取消收藏
     * @param userDeviceCollectionDTO
     * @return
     */
    @ApiOperation(value = "新增/取消收藏")
    @PostMapping("/userCollection/addOrCancelCollections")
    Result addOrCancelCollections(@RequestBody UserDeviceCollectionDTO userDeviceCollectionDTO) {
        if("6".equals(userDeviceCollectionDTO.getCollObjType()) && CollectionUtils.isEmpty(userDeviceCollectionDTO.getCollObjCode())){
            return Result.fail("该摄像机没有查到通道信息不能收藏");
        }
        if (userDeviceCollectionDTO.getOptType() == 1) {
            userDeviceCollectionService.addCollections(userDeviceCollectionDTO.getCollObjCode(), userDeviceCollectionDTO.getCollObjType());
            return Result.success("新增收藏成功");
        } else {
            userDeviceCollectionService.cancelCollections(userDeviceCollectionDTO.getCollObjCode(), userDeviceCollectionDTO.getCollObjType());
            return Result.success("取消收藏成功");
        }
    }
}
