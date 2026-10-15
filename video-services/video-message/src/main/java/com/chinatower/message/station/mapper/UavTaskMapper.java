package com.chinatower.message.station.mapper;

import com.chinatower.message.station.entity.UavNestBriefInfo;
import com.chinatower.message.station.entity.UavVideoFrameMsg;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author WLF
 * @description
 * @date 2024/12/3 11:39
 */
public interface UavTaskMapper {

    /**
     * 更新任务状态
     *
     * @param uavVideoFrameMsg
     */
    int updateTaskStatus(UavVideoFrameMsg uavVideoFrameMsg);

    int updateUavNestCode(@Param("uavDeviceCode") String uavDeviceCode, @Param("uavNestCode") String uavNestCode);

    UavNestBriefInfo selectNestInfoByCode(@Param("uavNestCode")String uavNestCode);

    int updateUavNestPosition(@Param("uavNestCode")String uavNestCode, @Param("longitude")String longitude,
                              @Param("latitude")String latitude, @Param("altitude")String altitude,
                              @Param("calibration")Integer calibration, @Param("latLonUpdateTime")Date latLonUpdateTime);

    UavVideoFrameMsg qryExtractionTaskData(String id);
}
