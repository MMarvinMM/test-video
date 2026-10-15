package com.chinatower.device.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.service.IRecorderDeviceService;
import com.chinatower.event.constants.Constants;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-device
 */
@RestController
@Api(value = "执法记录仪控制层")
@RequestMapping("/recorder/device")
@Slf4j
public class RecorderDeviceController extends BaseController {

    @Autowired
    private IRecorderDeviceService recorderDeviceService;
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;


    /**
     * 分页查询执法记录仪列表
     * @param recorderDeviceVO
     * @return
     */
    @PostMapping("/getRecorderDeviceListPage")
    public Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(@RequestBody RecorderDeviceVO recorderDeviceVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            recorderDeviceVO.setUserId(user.getUserId());
            recorderDeviceVO.setTenantId(currentUser.getUser().getTenantId());
            recorderDeviceVO.setIndustryCode(currentUser.getUser().getIndustryCode());
        }
        return recorderDeviceService.getRecorderDeviceListPage(recorderDeviceVO);
    }

    /**
     * 工作台执法记录仪设备区域树
     * @param recorderDeviceVO
     * @return
     */
    @PostMapping("/getAreaTree")
    public Result<List<DeviceTreeVO>> getAreaTree(@RequestBody RecorderDeviceVO recorderDeviceVO) {
        return Result.data(recorderDeviceService.getAreaTree(recorderDeviceVO));
    }


    /**
     * 查询执法记录仪设备列表信息
     * @param recorderDeviceVO
     * @return
     */
    @PostMapping("/getList")
    public Result<List<RecorderDeviceDTO>> getList(@RequestBody RecorderDeviceVO recorderDeviceVO) {
        List<RecorderDeviceDTO> list = recorderDeviceService
                .getRecorderDeviceList(recorderDeviceVO);
        return Result.data(list);
    }

    /**
     * 修改执法记录仪信息
     * @param recorderDeviceVO
     * @return
     */
    @PostMapping("/modify")
    public Result modify(@RequestBody @Validated RecorderDeviceVO recorderDeviceVO) {
        return recorderDeviceService.modify(recorderDeviceVO);
    }

    /**
     * 查询执法记录仪详情--合并到/deviceCommon/getDeviceInfo
     * @return Result<RecorderDeviceDTO>
     */
    @Deprecated
    @PostMapping("/detail")
    public Result<RecorderDeviceDTO> detail(@RequestBody RecorderDeviceVO recorderDeviceVO) {
        if (StringUtils.isBlank(recorderDeviceVO.getDeviceCode())) {
            return Result.fail("设备编码不能为空");
        }
        return Result.data(recorderDeviceService.detail(recorderDeviceVO));
    }

    /**
     *
     * 执法记录仪组织设备树
     *
     * @param deviceReqDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getRecorderMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getRecorderMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = recorderDeviceService.getRecorderMonitorOrgTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     *
     * 根据站址查询执法记录仪设备列表
     * @param siteRequestDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getRecorderDevicesBySiteCode")
    public Result<List<DeviceSiteVO>> getRecorderDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> deviceSiteVOS = recorderDeviceService.getRecorderDevicesBySiteCode(siteRequestDTO);
        return Result.data(deviceSiteVOS);
    }

    /**
     * 查询执法记录仪设备距离信息
     * @param param
     * @return
     */
    @Deprecated
    @ApiOperation(value = "查询执法记录仪设备距离信息")
    @PostMapping("/getRecorderDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> getRecorderDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return Result.data(recorderDeviceService.getRecorderDeviceDistanceInfo(param));
    }

    /**
     *
     * 执法记录仪区域设备树--给大屏展示用
     * @param deviceReqDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getRecorderMonitorAreaTree")
    public Result<List<DeviceTreeVO>> getRecorderMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = recorderDeviceService.getRecorderMonitorAreaTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(Constants.ZERO_NUM).setFirst(Constants.ONE_NUM);
        }
        return Result.data(list);
    }

    /**
     * 查询执法记录仪站址集合
     * @param siteRequestDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getRecorderSites")
    @ApiOperation("查询站址集合")
    public Result<List<IndustryDeviceTree>> getRecorderSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(recorderDeviceService.getRecorderSites(siteRequestDTO));
    }
}
