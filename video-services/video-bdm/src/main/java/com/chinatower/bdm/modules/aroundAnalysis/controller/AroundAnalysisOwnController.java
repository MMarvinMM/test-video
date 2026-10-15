package com.chinatower.bdm.modules.aroundAnalysis.controller;

import com.chinatower.bdm.api.entity.AroundAnalysisParamVO;
import com.chinatower.bdm.api.entity.AroundAnalysisResultVO;
import com.chinatower.bdm.api.entity.DetectResultVO;
import com.chinatower.bdm.api.entity.DeviceRequestVO;
import com.chinatower.bdm.modules.aroundAnalysis.service.AroundAnalysisService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.utils.StreamUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * video-bdm
 * @Author：liuxt
 * @Description：周边分析相关接口
 * @Date： 2023/7/31
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__周边分析相关接口
 */
@RestController
@Api(value = "通用周边相关接口-Api",tags = "周边后端接口")
@Slf4j
@RequestMapping("/aroundAnalysisOwn")
public class AroundAnalysisOwnController extends TowerBaseController {
    @Resource
    AroundAnalysisService aroundAnalysisService;

    /**
     * 周边视频，返回周边站址及前五位通道信息
     */
    @ApiOperation(value = "周边视频",notes = "周边视频")
    @PostMapping(value = "/getVideoAndUrl")
    public Result<DetectResultVO> getVideoAndUrl(@RequestBody DeviceRequestVO deviceRequestVO) {
        return aroundAnalysisService.getVideoAndUrl(deviceRequestVO);
    }

    /**
     * 周边分析，当前经纬度中心范围内的1.摄像机 2.无人机 3.大喇叭 4.网格员 5.告警,6.资源,7.雷达,8.物联设备,9.执法记录仪
     * 通用平台使用
     */
    @ApiOperation(value = "周边分析获取列表数据",notes = "周边分析获取列表数据")
    @PostMapping(value = "/getAroundAnalysisOwnList")
    public Result<List<AroundAnalysisResultVO>> getAroundAnalysisOwnList(@RequestBody AroundAnalysisParamVO aroundAnalysisParamVO) {
        return aroundAnalysisService.getAroundAnalysisOwnList(aroundAnalysisParamVO);
    }




}
