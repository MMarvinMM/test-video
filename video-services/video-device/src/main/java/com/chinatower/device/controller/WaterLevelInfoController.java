package com.chinatower.device.controller;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.dto.WaterLevelInfo;
import com.chinatower.device.service.IWaterLevelInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelInfoController
 * @description: 水位管理控制器
 * @author: fengty
 * @create: 2024-12-12 11:29
 */
@RestController
@Slf4j
public class WaterLevelInfoController extends TowerBaseController {

    @Autowired
    IWaterLevelInfoService waterLevelInfoService;

    @GetMapping("/video/waterLevel/initWaterLevelDeviceInfo")
    public Result<List<TreeSelect>> initWaterLevelDeviceInfo () {
        WaterLevelInfo waterLevelInfo = new WaterLevelInfo();
        return Result.data(waterLevelInfoService.initWaterLevelDeviceInfo(waterLevelInfo));
    }


    /**
     * 功能描述：根据条件分页查询卡口设备信息  增加industryCode appVerCode
     * @Author：zl
     * @param waterLevelInfo
     * @return TableDataInfo
     * @requestID  中国铁塔_铁塔视联平台_广东属地，查询水位球设备列表
     */
    @GetMapping("/video/waterLevel/queryWaterLevelDeviceInfoPage")
    public TableDataInfo queryWaterLevelDeviceInfoPage(WaterLevelInfo waterLevelInfo) {
        log.info("查询水位球设备信息参数：{}", waterLevelInfo);
        List<WaterLevelInfo> list = waterLevelInfoService.queryWaterLevelDeviceInfoPage(waterLevelInfo);
        return getDataTable(list);
    }

    @PostMapping("/video/waterLevel/addWaterDevice")
    public Result<String> addWaterDevice(@RequestBody List<String> deviceCodes) {
        log.info("添加水位球设备信息参数：{}", deviceCodes);
        try {
            int num = waterLevelInfoService.addWaterDevice(deviceCodes);
            return Result.success("共" + num + "条数据添加成功!");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/video/waterLevel/deleteWaterDevice")
    public Result<String> deleteWaterDevice(@RequestBody List<Integer> ids) {
        log.info("删除水位球设备信息参数：{}", ids);
        if(CollectionUtils.isEmpty(ids)){
            return Result.fail("删除数据不能为空!");
        }
        try {
            int num = waterLevelInfoService.deleteWaterDevice(ids);
            return Result.success("共" + num + "条数据删除成功!");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

}
