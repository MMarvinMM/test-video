package com.chinatower.bdm.modules.aroundAnalysis.controller;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.aroundAnalysis.service.AroundAnalysisService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;


/**
 * video-bdm
 * @Author：liuxt
 * @Description：周边分析相关接口
 * @Date： 2023/7/31
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__周边分析相关接口
 */
@Api(value = "周边分析相关接口-Api",tags = "周边分析后端接口")
@Slf4j
@RestController
public class AroundAnalysisController extends TowerBaseController {
    @Resource
    AroundAnalysisService aroundAnalysisService;


    /**
     * 周边分析，当前经纬度中心范围内的告警/无人机/摄像机/云广播/网格员
     * @Author：liuxt
     * @param aroundAnalysisParamVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    @ApiOperation(value = "周边分析获取列表数据",notes = "周边分析获取列表数据")
    @PostMapping(value = "/aroundAnalysis/getAroundAnalysisList")
    public Result<List<AroundAnalysisResultVO>> getAroundAnalysisList(@RequestBody AroundAnalysisParamVO aroundAnalysisParamVO) {
        return aroundAnalysisService.getAroundAnalysisList(aroundAnalysisParamVO);
    }




}
