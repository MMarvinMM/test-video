package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.airline.dto.request.AirResultTaskReq;
import com.chinatower.airline.dto.request.AirVideoReq;
import com.chinatower.airline.service.AirMedinumService;
import com.chinatower.airline.service.AirResultService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirHistoryReq;
import com.chinatower.device.factory.DeviceServiceFactory;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/6/25 9:47
 */

@RestController
@Api(value = "飞行管理第三方API模块", tags = "飞行管理第三方API模块")
@Slf4j
@RequestMapping("/airThirdApi")
public class AirThirdApiController {
    @Resource
    private UavOkhttpUtil uavOkhttpUtil;
    @Resource
    private DeviceServiceFactory deviceServiceFactory;
    @Resource
    private AirResultService airResultService;
    @Resource
    private AirMedinumService airMedinumService;

    /**
     * 获取无人机区域树API
     */
    @PostMapping("/getDeviceAreaTreeApi")
    public Result<List<DeviceTreeVO>> getDeviceAreaTreeApi(@RequestBody DeviceReqDTO deviceReqDTO, HttpServletRequest request) {
        log.info("AirThirdApiController-getDeviceAreaTreeApi-req:{}", JSON.toJSONString(deviceReqDTO));
        // 从header中获取行业和租户信息
        String industry = request.getHeader("industryCode");
        String tenant = request.getHeader("tenantId");
        if(StringUtils.isNotEmpty(industry) && StringUtils.isNotEmpty(tenant)){
            deviceReqDTO.setIndustryCode(industry);
            deviceReqDTO.setTenantId(tenant);
            deviceReqDTO.setQueryType(Integer.valueOf(Constants.ONE));
            deviceReqDTO.setOrderStatus(Constants.ONE);
            deviceReqDTO.setNeedDevice(true);
        }else {
            return Result.fail("header行业和租户信息不能为空");
        }
        List<DeviceTreeVO> list = deviceServiceFactory.getBean(deviceReqDTO.getQueryType()).getDeviceAreaTree(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 获取飞行结果任务API
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskListApi")
    public TableDataInfo queryTaskListApi(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirThirdApiController-queryTaskListApi-req:{}", JSON.toJSONString(airResultTaskReq));
        // 从header中获取行业和租户信息
        this.getInfoFromHeader(airResultTaskReq,request);
        return airResultService.queryTaskList(airResultTaskReq, "0");
    }

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskPhotosApi")
    public TableDataInfo queryTaskPhotosApi(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirThirdApiController-queryTaskPhotosApi-req:{}", JSON.toJSONString(airResultTaskReq));
        // 从header中获取行业和租户信息
        this.getInfoFromHeader(airResultTaskReq,request);
        return airResultService.queryTaskPhotos(airResultTaskReq);
    }

    /**
     * 根据任务获取视频集合
     *
     * @param airResultTaskReq
     * @return
     */
    @PostMapping("/queryTaskVideosApi")
    public TableDataInfo queryTaskVideosApi(@RequestBody AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        log.info("AirThirdApiController-queryTaskVideosApi-req:{}", JSON.toJSONString(airResultTaskReq));
        // 从header中获取行业和租户信息
        this.getInfoFromHeader(airResultTaskReq,request);
        return airResultService.queryTaskVideos(airResultTaskReq);
    }

    /**
     * 查询历史轨迹
     *
     * @param airHistoryReq
     * @return
     */
    @PostMapping("/queryHisRecordsApi")
    public Result queryHisRecordsApi(@RequestBody AirHistoryReq airHistoryReq) {
        log.info("AirResultController-queryHisRecord-req:{}", JSON.toJSONString(airHistoryReq));
        AirResultTaskReq resultTaskReq = new AirResultTaskReq();
        resultTaskReq.setTaskRecordId(airHistoryReq.getTaskRecordId());
        resultTaskReq.setAccessNode(airHistoryReq.getAccessNode());
        JSONArray jsonArray =  airResultService.queryHisRecord(resultTaskReq);
        return Result.data(jsonArray);
    }

    /**
     * 获取视频实时地址
     *
     * @param
     * @return
     */
    @PostMapping("/getVideoRealtimeUrlApi")
    public Result getVideoRealtimeUrlApi(@RequestBody AirVideoReq airVideoReq) {
        log.info("AirThirdApiController-getVideoRealtimeUrlApi-req:{}", JSON.toJSONString(airVideoReq));
        // 获取平台节点
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airVideoReq.setAcceptNode(platformCode);
        return airMedinumService.getVideoRealtimeUrl(airVideoReq);
    }

    /**
     * 从header中获取行业和租户信息
     * @param airResultTaskReq
     * @param request
     */
    private void getInfoFromHeader(AirResultTaskReq airResultTaskReq, HttpServletRequest request) {
        String industry = request.getHeader("industryCode");
        String tenant = request.getHeader("tenantId");
        if(StringUtils.isNotEmpty(industry) && StringUtils.isNotEmpty(tenant)){
            airResultTaskReq.setIndustryCode(industry);
            airResultTaskReq.setTenantId(tenant);
        }else {
            throw new RuntimeException("header行业和租户信息不能为空");
        }
        String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
        airResultTaskReq.setAcceptNode(platformCode);
    }
}
