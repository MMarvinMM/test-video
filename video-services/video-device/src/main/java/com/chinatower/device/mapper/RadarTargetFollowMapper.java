package com.chinatower.device.mapper;

import com.chinatower.industry.vo.RadarTargetFollowVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RadarTargetFollowMapper {

    List<RadarTargetFollowVO> queryByDeviceCodeList(@Param("deviceCodeList") List<String> deviceCodeList);

    List<RadarTargetFollowVO> queryByMessageId(@Param("messageId") String messageId);

    int insertBatch(@Param("list") List<RadarTargetFollowVO> list);

    int deleteByMessageId(@Param("messageId") String messageId);

    int deleteByMessageIdBatch(@Param("list") List<String> messageIdList);

    int deleteByDeviceCodeListAndTargetId(@Param("deviceCodeList")List<String> deviceCodeList, @Param("targetId")String targetId);

    String getAccessNodeByDeviceCodeList(@Param("deviceCodeList")List<String> deviceCodeList);
}