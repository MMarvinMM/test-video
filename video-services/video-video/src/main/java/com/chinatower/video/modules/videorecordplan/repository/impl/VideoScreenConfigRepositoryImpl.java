package com.chinatower.video.modules.videorecordplan.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.video.modules.videorecordplan.dataobject.VideoScreenConfigDO;
import com.chinatower.video.modules.videorecordplan.mapper.VideoScreenConfigMapper;
import com.chinatower.video.modules.videorecordplan.repository.IVideoScreenConfigRepository;
import org.springframework.stereotype.Service;

/**
 * 功能描述: 视频管理--视频设置服务实现类
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
@Service
public class VideoScreenConfigRepositoryImpl extends ServiceImpl<VideoScreenConfigMapper, VideoScreenConfigDO> implements IVideoScreenConfigRepository {

}
