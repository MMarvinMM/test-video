package com.chinatower.video.modules.videorecordplan.service;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.dataobject.VideoScreenConfigDO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 功能描述: 视频管理--视频设置服务类
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
public interface IVideoScreenConfigService {

    /**
     * 功能描述: 新增视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    Result<String> addScreenConfig(VideoScreenConfigDO videoScreenConfigDO);

    /**
     * 功能描述: 更新视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: String
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    Result<String> updateScreenConfig(VideoScreenConfigDO videoScreenConfigDO);

    /**
     * 功能描述: 查询视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: List<VideoScreenConfigVO>
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    Result<List<VideoScreenConfigVO>> queryScreenConfig(@RequestBody VideoScreenConfigVO videoScreenConfigVO);

    Result<VideoScreenConfigVO> queryWaterMark(VideoScreenConfigVO videoScreenConfigVO);
}
