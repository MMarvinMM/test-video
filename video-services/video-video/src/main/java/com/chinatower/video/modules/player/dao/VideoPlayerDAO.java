package com.chinatower.video.modules.player.dao;

import com.chinatower.video.api.entity.ChannelStorageTimeReq;
import com.chinatower.video.api.entity.SnapShotSimpleRes;
import com.chinatower.video.modules.player.entity.PlayerTimesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: VideoControlDAO
 * @Description: 视频控制DAO
 * @Author: zhujun
 * @Date: 2022-01-13
 **/
@Mapper
public interface VideoPlayerDAO {

    List<PlayerTimesVO> queryVideoPlanMatch(@Param("deviceCode") String deviceCode, @Param("channelCode") String channelCode, @Param("customerCode") String customerCode, @Param("weekDay") int weekDay, @Param("industryCode") String industryCode);

    List<PlayerTimesVO> queryVideoPlanHisMatch(@Param("deviceCode") String deviceCode, @Param("channelCode") String channelCode, @Param("customerCode") String customerCode, @Param("dateTmp") String dateTmp, @Param("industryCode") String industryCode);

    String selectChannelStatus(String deviceCode,String channelCode);

    String queryRecordPlanStorageTime(ChannelStorageTimeReq channelStorageTimeReq);

    List<SnapShotSimpleRes> queryChannelInit();
}
