package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.dto.request.AIrCarReq;
import com.chinatower.airline.dto.request.RoadsNameMilepostReq;
import com.chinatower.airline.service.AirConfigService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirPwdReq;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import com.chinatower.device.entity.vo.UavDetailVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/5/20 9:39
 */
@RestController
@Api(value = "无人机设置模块", tags = "无人机设置模块")
@Slf4j
@RequestMapping("/airConfig")
public class AirConfigController {
    @Resource
    private AirConfigService airConfigService;

    /**
     * 获取无人机密码配置
     *
     * @param airPwdReq
     * @return
     */
    @PostMapping("/getUavPwdConfig")
    public Result getUavPwdConfig(@RequestBody AirPwdReq airPwdReq) {
        log.info("AirConfigController-getUavPwdConfig-req:{}", JSON.toJSONString(airPwdReq));
        return airConfigService.getUavPwdConfig(airPwdReq);
    }

    /**
     * 校验无人机密码
     *
     * @param airPwdReq
     * @return
     */
    @PostMapping("/checkUavPwd")
    public Result checkUavPwd(@RequestBody AirPwdReq airPwdReq) {
        log.info("AirConfigController-checkUavPwd-req:{}", JSON.toJSONString(airPwdReq));
        return airConfigService.checkUavPwd(airPwdReq);
    }

    /**
     *  根据经纬度查询 道路名称+桩号
     *
     * @param roadsNameMilepostReqList
     * @return
     */
    @PostMapping("/queryRoadsNameMilepost")
    public Result queryRoadsNameMilepost(@RequestBody List<RoadsNameMilepostReq> roadsNameMilepostReqList) {
        log.info("AirConfigController-queryRoadsNameMilepost-req:{}", JSON.toJSONString(roadsNameMilepostReqList));
        return airConfigService.queryRoadsNameMilepost(roadsNameMilepostReqList);
    }


    /**
     * 通过无人机编码查询可飞行半径距离
     * @param uavCodeList 无人机编码
     * @return
     */
    @PostMapping("/queryFlightRadiusByUavCode")
    public Result<List<FlightRadiusVo>> queryFlightRadiusByUavCode(@RequestBody List<String> uavCodeList) {
        log.info("AirConfigController-queryFlightRadiusByUavCode-req:{}", JSON.toJSONString(uavCodeList));
        return airConfigService.queryFlightRadiusByUavCode(uavCodeList);
    }

    /**
     * 通过无人机型号查询无人机镜头信息
     */
    @PostMapping("/queryUavCameraInfoByModelCode")
    public Result<UavDetailVO> queryUavCameraInfoByModelCode(@RequestBody AIrCarReq aIrCarReq) {
        log.info("AirConfigController-queryUavCameraInfoByModelCode-req:{}", JSON.toJSONString(aIrCarReq));
        return airConfigService.queryUavCameraInfoByModelCode(aIrCarReq);
    }
}
