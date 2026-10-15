package com.chinatower.event.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.dto.CheckpointWarningOrderAddDTO;
import com.chinatower.event.repository.ICheckpointAlarmRepository;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * video-event
 * @Author：zl
 * @Description：出入口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__出入口管理
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/checkpoint")
@Api(tags = "出入口管理")
public class CheckpointAlarmController extends TowerBaseController {

    @Autowired
    private ICheckpointAlarmRepository checkpointAlarmRepository;

    private static final int EXCEL_EXPORT_LIMIT = 5000;

    /**
     * 功能描述：查询车辆进出记录
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:34
     * @version:
     */
    @PostMapping("/getCheckpointAlarmList")
    public TableDataInfo getCheckpointAlarmList(@RequestBody CheckpointAlarmDTO dto) {
        List<CheckpointAlarmDTO> list = new ArrayList<>();
        try{
            list = checkpointAlarmRepository.getCheckpointAlarmList(dto);
            log.info("查询车辆进出记录---返回报文------" + JSONObject.toJSONString(list));
        }catch (Exception e){
            log.error("业务处理异常",e);
        }
        return getDataTable(list);
    }

    /**
     * 功能描述：林业卡口大屏，根据设备分组查询进出口告警数量
     *
     * @param dto
     * @return TableDataInfo
     * @author hued
     * @date 2023年12月28日14:18:53
     * @version:
     */
    @PostMapping("/getCheckAlarmByDevice")
    public Result<List<CheckAlarmByDeviceVO>> getCheckAlarmByDevice(@RequestBody CheckAlarmByDeviceVO dto) {
        List<CheckAlarmByDeviceVO> list = checkpointAlarmRepository.getCheckAlarmByDevice(dto);
        log.info("getCheckAlarmByDevice---返回报文------" + JSONObject.toJSONString(list));
        return Result.data(list);
    }

    /**
     * 功能描述：批量添加车辆进出记录
     * 如果传入空列表，则读取车辆演示字典配置生成记录
     *
     * @param records 批量添加记录列表（可选，为空时读取字典配置）
     * @return 批量添加结果
     * @author OpenCode
     * @date 2026-07-29
     * @version:
     */
    @PostMapping("/batchAddWarningOrder")
    public Result<String> batchAddWarningOrder(@RequestBody(required = false) List<CheckpointWarningOrderAddDTO> records) {
        if (records == null || records.isEmpty()) {
            log.info("传入空列表，使用车辆演示字典配置生成记录");
        } else {
            log.info("批量添加车辆进出记录，数量: {}", records.size());
        }
        String result = checkpointAlarmRepository.batchAddWarningOrder(records);
        return Result.success(result);
    }

}
