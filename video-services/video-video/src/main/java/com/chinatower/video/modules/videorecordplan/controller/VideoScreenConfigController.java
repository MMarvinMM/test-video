package com.chinatower.video.modules.videorecordplan.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.dataobject.VideoScreenConfigDO;
import com.chinatower.video.modules.videorecordplan.service.IVideoScreenConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能描述: 视频管理--视频设置Controller
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
@RestController
@Slf4j
@Api(value = "视频设置模块",tags = "视频设置模块")
public class VideoScreenConfigController {
    @Resource
    private IVideoScreenConfigService videoScreenConfigService;


    /**
     * 功能描述: 新增视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @ApiOperation(value = "新增视频屏数设置信息接口")
    @PostMapping("/video/screenConfig/addScreenConfig")
    public Result<String> addScreenConfig(@Validated @RequestBody VideoScreenConfigDO videoScreenConfigDO) {
        log.info("addScreenConfig" + VideoConstants.CONTROLLERREQ + videoScreenConfigDO);
        return videoScreenConfigService.addScreenConfig(videoScreenConfigDO);
    }


    /**
     * 功能描述: 更新视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @ApiOperation(value = "更新视频屏数设置信息接口")
    @PostMapping("/video/screenConfig/updateScreenConfig")
    public Result<String> updateScreenConfig(@Validated @RequestBody VideoScreenConfigDO videoScreenConfigDO) {
        log.info("updateScreenConfig" + VideoConstants.CONTROLLERREQ + videoScreenConfigDO);
        return videoScreenConfigService.updateScreenConfig(videoScreenConfigDO);
    }


    /**
     * 功能描述: 查询视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: List<VideoScreenConfigVO>
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    @PostMapping("/video/screenConfig/queryScreenConfig")
    public Result<List<VideoScreenConfigVO>> queryScreenConfig(@RequestBody VideoScreenConfigVO videoScreenConfigVO) {
        log.info("queryScreenConfig" + VideoConstants.CONTROLLERREQ + videoScreenConfigVO);
        return videoScreenConfigService.queryScreenConfig(videoScreenConfigVO);
    }

    /**
     * 功能描述: 查询租户行业下的水印配置
     */
    @PostMapping("/video/screenConfig/queryWaterMark")
    public Result<VideoScreenConfigVO> queryWaterMark(@RequestBody VideoScreenConfigVO videoScreenConfigVO) {
        log.info("queryWaterMark" + VideoConstants.CONTROLLERREQ + videoScreenConfigVO);
        return videoScreenConfigService.queryWaterMark(videoScreenConfigVO);
    }
}
