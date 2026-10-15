package com.chinatower.stdu.query.dao;


import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.stdu.query.entity.HabitVO;
import com.chinatower.video.api.entity.*;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

/**
 * @ClassName: VideoQueryDAO
 * @Description: 视频查询DAO
 * @Author: ldl
 * @Date: 2022-01-27
 **/
@Mapper
public interface VideoQueryDAO {

    List<CasCadeVO> getCasCadeByDeviceCodeBatch(List<String> list);

    List<HabitVO> getUserHabitList(List<String> list);

    List<ChannelInfoVO> listChannelInfoForCustDev(DeviceInfoVO deviceInfoVO);

}
