package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.response.AirLineSnapshotRsp;
import com.chinatower.device.entity.dto.AirEditDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author WLF
 * @description
 * @date 2025/1/2 10:38
 */
@Mapper
public interface AirLineSnapshotMapper {
    int addAirLineSnapshot(AirEditDto airEditDto);

    AirLineSnapshotRsp getAirLineSnapshot(AirEditDto airEditDto);
}
