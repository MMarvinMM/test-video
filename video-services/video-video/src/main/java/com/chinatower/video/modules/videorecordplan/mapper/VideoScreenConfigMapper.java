package com.chinatower.video.modules.videorecordplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.video.api.entity.VideoScreenConfigVO;
import com.chinatower.video.modules.videorecordplan.dataobject.VideoScreenConfigDO;
import com.chinatower.video.modules.videorecordplan.dto.VideoScreenConfigChannelVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 功能描述: 视频管理--视频设置Mapper 接口
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
public interface VideoScreenConfigMapper extends BaseMapper<VideoScreenConfigDO> {

    /**
     * 功能描述: 查询视频屏数设置信息接口
     * @param: VideoScreenConfigVO
     * @return: List<VideoScreenConfigVO>
     * @auther: zhaozj
     * @date: 2022/10/12
     * @version: release_v1.5
     * @requestID: 视频管理--视频设置
     */
    List<VideoScreenConfigVO> queryScreenConfig(@RequestBody VideoScreenConfigVO videoScreenConfigVO);



    /**
     * 功能说明: 更新大屏设置信息表
     * @param videoScreenConfigDO
     * @return 更新影响数据条数
     * @throws
     * @requestID
     */
    int updateScreenConfig(@RequestBody VideoScreenConfigDO videoScreenConfigDO);

    VideoScreenConfigVO queryScreenConfigLast(VideoScreenConfigVO videoScreenConfigVO);

    int updateVideoInitUrl(@RequestBody VideoScreenConfigDO videoScreenConfigDO);

    int addVideoScreenConfigChannel(VideoScreenConfigDO videoScreenConfigDO);

    List<VideoScreenConfigChannelVO> queryScreenConfigChannel(VideoScreenConfigVO videoScreenConfigVO);

    int delVideoScreenConfigChannel(VideoScreenConfigDO videoScreenConfigDO);
}
