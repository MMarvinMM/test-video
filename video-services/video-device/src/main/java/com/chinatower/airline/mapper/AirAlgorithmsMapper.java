package com.chinatower.airline.mapper;

import com.chinatower.device.entity.dto.AirAlarmTypeReq;
import com.chinatower.device.entity.dto.SyncAlgorithmsReq;
import com.chinatower.device.entity.vo.AirLineVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2024/11/11 12:22
 */
@Mapper
public interface AirAlgorithmsMapper {
    void edit(SyncAlgorithmsReq syncAlgorithmsReq);

    List<Map<String, Object>> getUavAlgorithmsList(AirAlarmTypeReq req);

    String queryAlId(String flightRouteId);

    /**
     * 查询算法列表
     * @param airLineVoList
     * @return
     */
    List<SyncAlgorithmsReq> queryAlList(@Param("airLineVoList") List<AirLineVo> airLineVoList, @Param("optType") String optType, @Param("status") String status);
}
