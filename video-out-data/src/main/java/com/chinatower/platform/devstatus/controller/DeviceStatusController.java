package com.chinatower.platform.devstatus.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.Monitor;
import com.chinatower.outdata.api.entity.MonitorFake;
import com.chinatower.outdata.api.entity.QuerySensorCountReq;
import com.chinatower.outdata.api.entity.QueryStatusFakeReq;
import com.chinatower.platform.devstatus.service.DeviceStatusService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * video-out-data
 */
@RestController
@Slf4j
@RequestMapping("/outdata")
public class DeviceStatusController {

    @Autowired
    private DeviceStatusService deviceStatusService;

    /**
     * 传感器状态查询
     * @param deviceCodes
     * @return
     */
    @RequestMapping("/query/queryStatus")
    @ApiOperation(value = "传感器状态查询",notes = "传感器状态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCodes",value = "设备编码",dataType = "Result",required = true,paramType = "query")
    })
    @Deprecated
    public Result queryStatus(@RequestParam String deviceCodes){
        Result res;
        log.info("感器状态查询/queryStatus param:{}", deviceCodes);
        try{
            res = deviceStatusService.queryStatus(deviceCodes);
        }catch (Exception e){
            res = Result.fail("传感器状态查询失败");
            log.error("传感器状态查询失败：{}", e);
        }
        return res;
    }

    /**
     * 传感器参数查询
     * @param deviceCodes
     * @return
     */
    @RequestMapping("/query/queryAttribute")
    @ApiOperation(value = "传感器参数查询",notes = "传感器参数查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCodes",value = "设备编码",dataType = "Result",required = true,paramType = "query")
    })
    public Result<List<Monitor>> queryAttribute(@RequestParam String deviceCodes){
        Result<List<Monitor>> res;
        log.info("传感器参数查询/queryAttribute param:{}", deviceCodes);
        try{
            res = deviceStatusService.queryAttribute(deviceCodes);
        }catch (Exception e){
            res = Result.fail("传感器参数查询失败");
            log.error("传感器参数查询失败：{}", e);
        }
        return res;
    }

    /**
     * 查询传感器全量参数
     * @param deviceCodes
     * @return
     */
    @RequestMapping("/query/queryAttributeMap")
    @ApiOperation(value = "传感器参数查询",notes = "传感器参数查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCodes",value = "设备编码",dataType = "Result",required = true,paramType = "query")
    })
    public Result<List<Map<String, Object>>> queryAttributeMap(@RequestParam String deviceCodes){
        Result<List<Map<String, Object>>> res;
        log.info("传感器参数查询/queryAttributeMap param:{}", deviceCodes);
        try{
            res = deviceStatusService.queryAttributeMap(deviceCodes);
        }catch (Exception e){
            res = Result.fail("传感器参数查询失败");
            log.error("传感器参数查询失败：{}", e);
        }
        return res;
    }

    /**
     * 传感器状态站址信息查询
     * @param queryStatusFakeReq
     * @return
     */
    @RequestMapping("/query/queryStatusFake")
    @ApiOperation(value = "传感器状态站址信息查询",notes = "传感器状态站址信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commonFeignEntity",value = "传感器状态站址信息查询实体",dataType = "Result",required = true,paramType = "query")
    })
    public Result<List<MonitorFake>> queryStatusFake(@RequestBody QueryStatusFakeReq queryStatusFakeReq){
        Result<List<MonitorFake>> res;
        log.info("传感器状态站址查询/queryStatusFake param:{}", queryStatusFakeReq);
        try{
            res = deviceStatusService.queryStatusFake(queryStatusFakeReq);
        }catch (Exception e){
            res = Result.fail("传感器状态站址信息查询失败");
            log.error("传感器状态站址查询失败：", e);
        }
        return res;
    }

    /**
     * 分页查询传感器设备信息
     * @param queryStatusFakeReq
     * @return
     */
    @RequestMapping("/query/queryStatusFakePage")
    @ApiOperation(value = "传感器状态站址信息查询",notes = "传感器状态站址信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commonFeignEntity",value = "传感器状态站址信息查询实体",dataType = "Result",required = true,paramType = "query")
    })
    public Result<PageInfo<MonitorFake>> queryStatusFakePage(@RequestBody QueryStatusFakeReq queryStatusFakeReq){
        Result<PageInfo<MonitorFake>> res;
        log.info("传感器状态站址查询/queryStatusFakePage param:{}", queryStatusFakeReq);
        try{
            res = deviceStatusService.queryStatusFakePage(queryStatusFakeReq);
        }catch (Exception e){
            res = Result.fail("传感器状态站址信息查询失败");
            log.error("传感器状态站址查询失败：", e);
        }
        return res;
    }

    /**
     * 传感器数量查询
     * @param querySensorCountReq
     * @return
     */
    @RequestMapping("/query/querySensorCount")
    @ApiOperation(value = "传感器数量查询",notes = "传感器数量查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commonFeignEntity",value = "传感器数量查询实体",dataType = "Result",required = true,paramType = "query")
    })
    public Result<Integer> querySensorCount(@RequestBody QuerySensorCountReq querySensorCountReq){
        Result<Integer> res;
        log.info("传感器数量查询/querySensorCount param:{}", querySensorCountReq);
        try{
            res = deviceStatusService.querySensorCount(querySensorCountReq);
        }catch (Exception e){
            res = Result.fail("传感器数量查询失败");
            log.error("传感器数量查询失败：{}", e);
        }
        return res;
    }
}
