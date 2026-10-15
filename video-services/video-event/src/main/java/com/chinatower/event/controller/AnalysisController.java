package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.dto.AnalysisDTO;
import com.chinatower.event.dto.EventNumByAreaDTO;
import com.chinatower.event.dto.EventNumByHourDTO;
import com.chinatower.event.dto.EventNumNewDTO;
import com.chinatower.event.entity.vo.AreaEventVO;
import com.chinatower.event.repository.IStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * video-event
 * @author chenshaowu
 * @ClassName AnalysisController
 * @Description
 * @Date 2022/2/18
 **/

@RestController
@Slf4j
public class AnalysisController extends TowerBaseController {

    @Autowired
    private IStatisticsRepository statisticsRepository;

    /**
     * 功能描述：告警事件统计-事件高发区域
     *
     * @param: analysisDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @PostMapping("/workbench/getEventNumByArea")
    public Result<List<EventNumByAreaDTO>> getEventNumByArea(@RequestBody AnalysisDTO analysisDTO) {
        return Result.data(statisticsRepository.getEventNumByArea(analysisDTO));
    }

    /**
     * 功能描述：获取事件数量-曲线图用
     *
     * @param: analysisDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @PostMapping("/workbench/getEventNumByHour")
    public Result<List<EventNumByHourDTO>> getEventNumByHour(@RequestBody AnalysisDTO analysisDTO) {
        return Result.data(statisticsRepository.getEventNumByHour(analysisDTO));
    }

    /**
     * 功能描述：获取事件统计数量
     *
     * @param: analysisDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    @PostMapping("/workbench/getEventNumNew")
    public Result<EventNumNewDTO> getEventNumNew(@RequestBody AnalysisDTO analysisDTO) {
        EventNumNewDTO data = statisticsRepository.getEventNumNew(analysisDTO);
        return Result.data(data);
    }

    /**
     * 功能描述：告警事件统计-事件高发区域全景TOP5
     *
     * @param analysisDTO
     * @return Result<List<AreaEventVO>>
     * @author Chensw
     * @date 2022/4/19 17:22
     * @version:
     */
    @PostMapping("/workbench/getEventNumListByArea")
    public Result<List<AreaEventVO>> getEventNumListByArea(@RequestBody AnalysisDTO analysisDTO) {
        return Result.data(statisticsRepository.getEventNumListByArea(analysisDTO));

    }

}