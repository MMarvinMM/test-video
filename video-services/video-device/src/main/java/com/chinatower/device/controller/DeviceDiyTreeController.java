package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.dto.DeviceDiyTreeRequestVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeResultVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeVO;
import com.chinatower.device.service.IDeviceDiyTreeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * video-device
 * 功能描述: 租户视角--自定义树管理Controller
 *
 * @auther: hued
 * @date: 2024年1月8日15:06:45
 * @version: releae_v1.6.40
 * @requestID:
 */
@RestController
@Slf4j
@Api(value = "自定义树管理",tags = "自定义树管理")
public class DeviceDiyTreeController extends TowerBaseController {

    @Autowired
    private IDeviceDiyTreeService deviceDiyTreeService;

    /**
     * 自定义树新增
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/save")
    public Result save(@Valid @RequestBody DeviceDiyTreeVO deviceInfoVO) {
        return deviceDiyTreeService.save(deviceInfoVO);
    }

    /**
     * 自定义树编辑
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/update")
    public Result update(@Valid @RequestBody DeviceDiyTreeVO deviceInfoVO) {
        return deviceDiyTreeService.update(deviceInfoVO);
    }

    /**
     * 自定义树启用/停用
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/updateStatus")
    public Result updateStatus(@RequestBody DeviceDiyTreeVO deviceInfoVO) {
        return deviceDiyTreeService.updateStatus(deviceInfoVO);
    }

    /**
     * 自定义树删除
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/delete")
    public Result delete(@Valid @RequestBody DeviceDiyTreeRequestVO deviceDiyTreeRequestVO) {
        return deviceDiyTreeService.delete(deviceDiyTreeRequestVO);
    }

    /**
     * 自定义树查询
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/getList")
    public Result<List<DeviceDiyTreeVO>> getList(@RequestBody DeviceDiyTreeVO deviceInfoVO) {
        return deviceDiyTreeService.getList(deviceInfoVO);
    }

    /**
     * 自定义树详情查询
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/getOriginalDiyTree")
    public Result<List<DeviceDiyTreeResultVO>> getOriginalDiyTree(@RequestBody DeviceDiyTreeVO deviceInfoVO) {
        return deviceDiyTreeService.getOriginalDiyTree(deviceInfoVO);
    }

    /**
     * 自定义树查询
     *
     * @auther: hued
     * @date: 2024年1月8日15:06:45
     * @version: releae_v1.6.40
     * @requestID:
     */
    @PostMapping("/deviceDiy/getDiyTree")
    public Result<List<DeviceDiyTreeResultVO>> getDiyTree(@RequestBody DeviceDiyTreeResultVO deviceInfoVO) {
        return deviceDiyTreeService.getDiyTree(deviceInfoVO);
    }
}
