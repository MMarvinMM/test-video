package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.dto.request.AirPointToFlyReq;
import com.chinatower.airline.dto.request.UavRealTimeAlgorithmReq;
import com.chinatower.airline.service.AirLineService;
import com.chinatower.airline.util.Base64ToMultipartFile;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/3 21:13
 */

@RestController
@Api(value = "航线航点模块", tags = "航线航点模块")
@Slf4j
@RequestMapping("/air")
public class AirLineController extends BaseController {
    @Resource
    private AirLineService airLineService;


    /**
     * 根据航线id 查询算法信息
     * @param
     * @return
     */
    @PostMapping("/getAlgorithmInfoByLineId")
    public Result getAlgorithmInfoByLineId(@RequestParam("flightRouteId") String flightRouteId) {
        log.info("AirPlanController-findAirPlanById-req:{}", JSON.toJSONString(flightRouteId));
        return airLineService.getAlgorithmInfoByLineId(flightRouteId);
    }

    /**
     * 无人机实时航线算法任务下发
     */
    @PostMapping(path = "/uavRealTimeAlgorithm")
    public Result uavRealTimeAlgorithm(@RequestBody UavRealTimeAlgorithmReq uavRealTimeAlgorithmReq) {
        log.info("UavFlightController-uavRealTimeAlgorithm-req:{}", JSON.toJSONString(uavRealTimeAlgorithmReq));
        return airLineService.uavRealTimeAlgorithm(uavRealTimeAlgorithmReq);
    }

    /**
     * 获取无人机算法配置
     *
     * @param alarmTypeDTO
     * @return
     */
    @PostMapping("/getAirAlarmType")
    public Result getAirAlarmType(@RequestBody AirAlarmTypeReq alarmTypeDTO) {
        log.info("AirLineController-getAirAlarmType-req:{}", JSON.toJSONString(alarmTypeDTO));
        return airLineService.getAirAlarmType(alarmTypeDTO);
    }

    /**
     * 查询设备下的航线
     */
    @PostMapping("/getAirLineByDeviceCodes")
    public Result getAirLineByDevics(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLineByDeviceCodes-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.getAirLineByDeviceCodes(airEditDto);
    }

    /**
     * 新增/编辑航线（包含上传文件）
     *
     * @param
     * @return
     */
    @PostMapping(path = "/editAirLine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result editAirLine(@Validated AirEditDto airEditDto, @RequestPart("kmzFile") MultipartFile kmzFile) {
        log.info("AirLineController-editAirLine-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.editAirLine(airEditDto, kmzFile);
    }

    /**
     * 上传航线文件
     * @param kmzFile
     * @return
     */
    @PostMapping(path = "/uploadKmzFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadKmzFile(@RequestPart("kmzFile") MultipartFile kmzFile) {
        return airLineService.uploadKmzFile(kmzFile);
    }

    /**
     * 复制航线
     *
     * @param
     * @return
     */
    @PostMapping(path = "/copyAirLine")
    public Result copyAirLine(@RequestBody AirEditDto airEditDto) {
        return airLineService.copyAirLine(airEditDto);
    }

    /**
     * 校验航线名称唯一性
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/checkAirLineName")
    public Result checkAirLineName(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-checkAirLineName-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.checkAirLineName(airEditDto);
    }

    /**
     * 校验航线是否被关联
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/checkAirLineBind")
    public Result checkAirLineBind(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-checkAirLineBind-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.checkAirLineBind(airEditDto);
    }

    /**
     * 查询默认航线名称
     *
     * @param
     * @return
     */
    @PostMapping("/queryDefultAirLineName")
    public Result queryDefultAirLineName(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-queryDefultAirLineName-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.queryDefultAirLineName(airEditDto);
    }

    /**
     * 航线详情接口
     *
     * @param
     * @return
     */
    @PostMapping("/getAirLineInfo")
    public Result getAirLineInfo(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLineInfo-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.getAirLineInfo(airEditDto);
    }

    /**
     * 航线详情接口
     *
     * @param
     * @return
     */
    @PostMapping("/getAirLineInfoByTaskRecordId")
    public Result getAirLineInfoByTaskRecordId(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLineInfoByTaskRecordId-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.getAirLineInfo(airEditDto);
    }

    /**
     * 航线详情接口(航线所在行业)
     *
     * @param
     * @return
     */
    @PostMapping("/getAirLineInfoByFlightRouteId")
    public Result getAirLineInfoByFlightRouteId(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLineInfoByFlightRouteId-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.getAirLineInfoByFlightRouteId(airEditDto);
    }

    /**
     * 根据任务ID查询航线信息
     *
     * @param airTaskReq 任务请求参数
     * @return 航线详情
     */
    @PostMapping("/queryAirLineByTaskId")
    public Result<AirLineVo> queryAirLineByTaskId(@RequestBody AirTaskReq airTaskReq) {
        log.info("AirLineController-queryAirLineByTaskId-req:{}", JSON.toJSONString(airTaskReq));
        return airLineService.queryAirLineByTaskId(airTaskReq);
    }

    /**
     * 修改航线名称
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/editAirLineName")
    public Result editAirLineName(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-editAirLineName-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.editAirLineName(airEditDto);
    }

    /**
     * 删除航线
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/delAirLine")
    public Result delAirLine(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-delAirLine-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.delAirLine(airEditDto);
    }

    /**
     * 航线新增临时算法
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/addTempAlgorithm")
    public Result addTempAlgorithm(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-addTempAlgorithm-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.addTempAlgorithm(airEditDto);
    }

    /**
     *
     * 查询航线算法
     * @param airEditDto
     * @return
     */
    @PostMapping("/queryAirLineAlgorithm")
    public Result queryAirLineAlgorithm(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-queryAirLineAlgorithm-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.queryAirLineAlgorithm(airEditDto);
    }

    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/getAirLineList")
    public TableDataInfo getAirLineList(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLineList-req:{}", JSON.toJSONString(airEditDto));
        return getDataTable(airLineService.getAirLineList(airEditDto));
    }

    /**
     * 查询租户行业下航线下拉列表sdk
     */
    @PostMapping("/getAirLinesList")
    public Result getAirLinesList(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLinesList-req:{}", JSON.toJSONString(airEditDto));
        return Result.data(airLineService.getAirLinesList(airEditDto));
    }

    /**
     * 查询租户行业下航线下拉列表sdk
     */
    @PostMapping("/getAirLinesListSdk")
    public Result getAirLinesListSdk(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getAirLinesListNologin-req:{}", JSON.toJSONString(airEditDto));
        return Result.data(airLineService.getAirLinesListNologin(airEditDto));
    }


    /**
     * 添加航线收藏
     *
     * @return
     */
    @PostMapping("/addAirLineCollect")
    public Result addAirLineCollect(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-addAirLineCollect-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.addAirLineCollect(airEditDto);
    }

    /**
     * 查询航线收藏列表
     *
     * @return
     */
    @PostMapping("/queryAirLineCollectList")
    public TableDataInfo queryAirLineCollectList(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-queryAirLineCollectList-req:{}", JSON.toJSONString(airEditDto));
        return getDataTable(airLineService.queryAirLineCollectList(airEditDto));
    }

    /**
     * 取消航线收藏
     *
     * @return
     */
    @PostMapping("/deleteAirLineCollect")
    public Result deleteAirLineCollect(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-deleteAirLineCollect-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.deleteAirLineCollect(airEditDto);
    }


    /**
     * 获取设备详细信息
     *
     * @param airDeviceInfoDto
     * @return
     */
    @PostMapping("/getAirDeviceInfo")
    public Result getAirDeviceInfo(@RequestBody AirDeviceInfoReq airDeviceInfoDto) {
        log.info("AirLineController-getAirDeviceInfo-req:{}", JSON.toJSONString(airDeviceInfoDto));
        return Result.data(airLineService.getAirDeviceInfo(airDeviceInfoDto));
    }

    /**
     * 获取航点航线详细信息(航线规划使用)
     *
     * @param airEditDto
     * @return
     */
    @PostMapping("/getPointInfo")
    public Result getPointInfo(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-getPointInfo-req:{}", JSON.toJSONString(airEditDto));
        return airLineService.getPointInfo(airEditDto);
    }

    /**
     * 指点飞行(从机场起飞)
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/pointToFly")
    public Result pointToFly(@RequestBody AirPointToFlyReq airPointToFlyReq) {
        log.info("AirLineController-pointToFly-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.pointToFly(airPointToFlyReq);
    }

    /**
     * 批量指点飞行(从机场起飞)
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/pointToFlyBatch")
    public Result pointToFlyBatch(@RequestBody List<AirPointToFlyReq> airPointToFlyReq) {
        log.info("AirLineController-pointToFlyBatch-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.pointToFlyBatch(airPointToFlyReq);
    }

    /**
     * 新指点飞行
     */
    @PostMapping(path = "/pointToFlyNew",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result pointToFlyNew(AirPointToFlyReq airPointToFlyReq, @RequestPart("kmzFile") MultipartFile kmzFile) {
        log.info("AirLineController-pointToFlyNew-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.pointToFlyNew(airPointToFlyReq,kmzFile);
    }

    /**
     * 一键起飞
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/immediatelyFly")
    public Result immediatelyFly(@RequestBody AirPointToFlyReq airPointToFlyReq) {
        log.info("AirLineController-immediatelyFly-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.immediatelyFly(airPointToFlyReq);
    }

    /**
     *  一键返航
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/flyReturn")
    public Result flyReturn(@RequestBody AirPointToFlyReq airPointToFlyReq) {
        log.info("AirLineController-flyReturn-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.flyReturn(airPointToFlyReq);
    }

    /**
     *  一键返航批量
     *
     * @param airPointToFlyReq
     * @return
     */
    @PostMapping("/flyReturnBatch")
    public Result flyReturnBatch(@RequestBody List<AirPointToFlyReq> airPointToFlyReq) {
        log.info("AirLineController-flyReturnBatch-req:{}", JSON.toJSONString(airPointToFlyReq));
        return airLineService.flyReturnBatch(airPointToFlyReq);
    }

    /**
     * 退订设备后 关闭设备下的航线任务和算法任务
     * @param airTaskOperateReq
     * @return
     */
    @PostMapping("/closeAirTaskAndAlgorithms")
    public Result closeAirTaskAndAlgorithms(@RequestBody AirTaskOperateReq airTaskOperateReq) {
        log.info("AirLineController-closeAirTaskAndAlgorithms-req:{}", JSON.toJSONString(airTaskOperateReq));
        return airLineService.closeAirTaskAndAlgorithms(airTaskOperateReq);
    }

    /**
     * 续订设备后 重新开启设备下的航线任务和算法任务
     * @param airTaskOperateReq
     * @return
     */
    @PostMapping("/openAirTaskAndAlgorithms")
    public Result openAirTaskAndAlgorithms(@RequestBody AirTaskOperateReq airTaskOperateReq) {
        log.info("AirLineController-openAirTaskAndAlgorithms-req:{}", JSON.toJSONString(airTaskOperateReq));
        return airLineService.openAirTaskAndAlgorithms(airTaskOperateReq);
    }

    /**
     * 一键 复飞
     * @param airEditDto
     * @return
     */
    @PostMapping("/reFly")
    public Result reFly(@Validated AirEditDto airEditDto, @RequestPart("kmzFile") MultipartFile kmzFile) {
        log.info("AirLineController-reFly-req:{}", JSON.toJSONString(airEditDto));
        log.info("pc一键复飞kmzjson:{}", JSON.toJSONString(airEditDto.getKmzJson()));
        return airLineService.reFly(airEditDto,kmzFile);
    }

    /**
     * 一键 复飞(APP)
     * @param airEditDto
     * @return
     */
    @PostMapping("/reFlyApp")
    public Result reFlyApp(@RequestBody AirEditDto airEditDto) {
        log.info("AirLineController-reFlyApp-req:{}", JSON.toJSONString(airEditDto));
        if(StringUtil.isEmpty(airEditDto.getBase64())){
            return Result.fail("app一键复飞文件流base64不能为空");
        }
        log.info("app一键复飞kmzjson:{}", JSON.toJSONString(airEditDto.getKmzJson()));
        byte[] fileBytes = Base64.getDecoder().decode(airEditDto.getBase64());
        MultipartFile multipartFile =  new Base64ToMultipartFile(fileBytes, "file.kmz");
        airEditDto.setBase64("");
        return airLineService.reFly(airEditDto,multipartFile);
    }

    /**
     * 查询设备订购的租户用户信息
     * @param telemetryReq
     * @return
     */
    @PostMapping("/queryUserDeviceOrder")
    public Result queryUserDeviceOrder(@RequestBody TelemetryReq telemetryReq) {
        log.info("AirLineController-queryUserDeviceOrder:{}", JSON.toJSONString(telemetryReq));
        return airLineService.queryUserDeviceOrder(telemetryReq);
    }
}
