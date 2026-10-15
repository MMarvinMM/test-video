package com.chinatower.video.component;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.QueryPatrolData;
import com.chinatower.video.api.entity.SttPatrolPauseReq;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.api.entity.VideoQueryPatrolData;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.videopatrolplan.service.impl.VideoPatrolPlanInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Package : com.chinatower.video.component
 * @FileName : RedisKeyExpirationListener
 * @Description : Redis key过期事件监听
 * @author : zhaozj
 * @date: 2023/03/25
 * @version : V1.5.75
 */
@Component
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {
    @Autowired
    private VideoQueryService videoQueryService;
    @Autowired
    private VideoPatrolPlanInfoService videoPatrolPlanInfoService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            //获取过期的key
            String expiredKey = message.toString();
            //判断是否是想要监听的过期key
            if (expiredKey.startsWith("channelQueue:")) {
                log.info(VideoConstants.REDIS_EXPIRE_KEY_LOG, expiredKey);
            }
            //判断是否是想要监听的过期key
            if (expiredKey.startsWith(VideoConstants.PAUSERESUME_A_)) {
                log.info(VideoConstants.REDIS_EXPIRE_KEY_LOG, expiredKey);
                String[] keyArr = expiredKey.split(VideoConstants.UNDERLINE_STR);
                //查询基础平台，巡航任务状态接口
                VideoQueryPatrolData videoQueryPatrol = new VideoQueryPatrolData();
                videoQueryPatrol.setDeviceCode(keyArr[2]);
                videoQueryPatrol.setChannelCode(keyArr[3]);
                Result<List<QueryPatrolData>> listResult = videoQueryService.queryPatrolNorth(videoQueryPatrol);
                List<QueryPatrolData> listData = listResult.getData();

                SttPatrolPauseReq sttPatrolPauseReq = new SttPatrolPauseReq();
                sttPatrolPauseReq.setPatrolType(VideoConstants.FOUR_LONG);
                sttPatrolPauseReq.setDeviceCode(keyArr[2]);
                sttPatrolPauseReq.setChannelCode(keyArr[3]);
                //恢复巡航通用方法
                videoPatrolPlanInfoService.resumePatrolCommon(expiredKey, sttPatrolPauseReq, listData);
            }
        } catch (Exception e) {
            log.error(VideoConstants.DOPATROL_ERROR, e);
        }
    }
}