package com.chinatower.emergency.controller;

import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.Constant;
import com.chinatower.emergency.dto.EmergencyEventInfoVO;
import com.chinatower.emergency.dto.EmergencyWarningEventRelVO;
import com.chinatower.emergency.service.EmergencyEventInfoService;
import com.chinatower.emergency.service.EmergencyWarningEventRelService;
import com.chinatower.emergency.utils.EmergencyDateUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

/**
 * video-emergency
 * @program: video-emergency
 * @ClassName: EmergencyEventInfoController
 * @description: 事件信息控制层
 * @create: 2024-10-28 14:12
 */
@RestController
@RequestMapping("/event")
@Slf4j
public class EmergencyEventInfoController {

    @Autowired
    private EmergencyEventInfoService emergencyEventInfoService;
    @Autowired
    private EmergencyWarningEventRelService emergencyWarningEventRelService;

    /**
     * 应急事件分页查询
     * @param pageNum
     * @param pageSize
     * @param eventInfo
     * @return
     */
    @GetMapping("/eventList")
    public TableDataInfo getEmergencyEventInfoVOList(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize,
                                                     EmergencyEventInfoVO eventInfo) {
        log.info("eventList请求参数：pageNum={}, pageSize={}, eventInfo={}", pageNum, pageSize, eventInfo);
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        // 列表查询固定给倒序
        eventInfo.setSortType("desc");
        List<EmergencyEventInfoVO> list = emergencyEventInfoService.getEmergencyEventInfoList(eventInfo);
        return getDataTable(list);
    }

    /**
     * 新增事件信息
     * @param eventInfo
     * @return
     */
    @PostMapping("/saveEvent")
    public Result addEmergencyEventInfoVO(@RequestBody EmergencyEventInfoVO eventInfo) {
        log.info("saveEvent请求参数：eventInfo={}", eventInfo);
        try {
            emergencyEventInfoService.addEmergencyEventInfoVO(eventInfo);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.success(Constant.SAVE_SUCCESS);
    }

    /**
     * 更新事件信息
     * @param eventInfo
     * @return
     */
    @PostMapping("/update")
    public Result updateEmergencyEventInfoVO(@RequestBody EmergencyEventInfoVO eventInfo) {
        log.info("update请求参数：eventInfo={}", eventInfo);
        try {
            emergencyEventInfoService.updateEmergencyEventInfoVO(eventInfo);
            return Result.success(Constant.UPDATE_SUCCESS);
        } catch (Exception e) {
            return Result.fail(Constant.UPDATE_ERROR);
        }
    }

    /**
     * 应急事件详情
     * @param eventCode
     * @return
     */
    @GetMapping("/getEventDetail/{eventCode}")
    public Result<EmergencyEventInfoVO> getEmergencyEventInfoVO(@PathVariable("eventCode") String eventCode) {
        log.info("getEventDetail请求参数：eventCode={}", eventCode);
        return Result.data(emergencyEventInfoService.getEmergencyEventInfoVO(eventCode));
    }

    /**
     * 关联告警事件
     * @param emergencyWarningEventRelVO
     * @return
     */
    @PostMapping("/connectedEvent")
    public Result connectedEvent(@Validated @RequestBody EmergencyWarningEventRelVO emergencyWarningEventRelVO) {
        log.info("connectedEvent请求参数：emergencyWarningEventRelVO={}", emergencyWarningEventRelVO);
        try {
            emergencyWarningEventRelService.connectedEvent(emergencyWarningEventRelVO);
            return Result.success(Constant.CONNECTED_SUCCESS);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除事件
     * @param emergencyEventInfoVO
     * @return
     */
    @PostMapping("/deleteEvent")
    public Result deleteEvent(@RequestBody EmergencyEventInfoVO emergencyEventInfoVO) {
        log.info("deleteEvent请求参数：emergencyEventInfoVO={}", emergencyEventInfoVO);
        try {
            emergencyEventInfoService.deleteEvent(emergencyEventInfoVO);
            return Result.success(Constant.DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 检查是否已关联告警事件
     * @param warningOrderId
     * @return
     */
    @GetMapping("/checkConnectedEvent/{warningOrderId}")
    public Result checkConnectedEvent(@PathVariable("warningOrderId") String warningOrderId) {
        log.info("checkConnectedEvent请求参数：warningOrderId={}", warningOrderId);
        try {
            EmergencyWarningEventRelVO emergencyWarningEventRelVO = new EmergencyWarningEventRelVO();
            emergencyWarningEventRelVO.setWarningOrderId(warningOrderId);
            boolean checkFlag = emergencyWarningEventRelService.checkConnectedEvent(emergencyWarningEventRelVO);
            if (checkFlag) {
                return Result.data(true);
            } else {
                return Result.data(false);
            }
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据事件code查询告警事件列表
     * @param eventCode
     * @return
     */
    @GetMapping("/getEventWarningList/{eventCode}")
    public Result getEventWarningList( @PathVariable("eventCode") String eventCode) {
        log.info("getEventWarningList请求参数：eventCode={}", eventCode);
        List<EmergencyWarningEventRelVO> list = emergencyEventInfoService.getEventWarningList(eventCode);
        if(CollectionUtils.isEmpty(list)){
            return Result.data(null);
        }
        List<String> warningOrderIdList = list.stream().map(EmergencyWarningEventRelVO::getWarningOrderId).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("warningOrderIdList", warningOrderIdList);
        String [] startAndEnd = EmergencyDateUtils.getDateRange(warningOrderIdList.get(0).substring(0, 8), warningOrderIdList.get(warningOrderIdList.size() - 1).substring(0, 8));
        map.put("alarmTimeStart", startAndEnd[0]);
        map.put("alarmTimeEnd", startAndEnd[1]);
        return Result.data(map);
    }

    /**
     * 查询事件图层
     * @param eventInfo
     * @return
     */
    @PostMapping("/getLayerForEventInfo")
    public Result getLayerForEventInfo(@RequestBody EmergencyEventInfoVO eventInfo) {
        log.info("getLayerForEventInfo请求参数：eventInfo={}", eventInfo);
        return Result.data(emergencyEventInfoService.getLayerForEventInfo(eventInfo));
    }

}
