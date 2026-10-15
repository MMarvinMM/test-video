package com.chinatower.emergency.mapper;

import com.chinatower.emergency.dto.HrtcRecordVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface HrtcRecordMapper {

    void addBatch(List<HrtcRecordVo> list);

    HrtcRecordVo findById(HrtcRecordVo vo);

    void delete(HrtcRecordVo vo);

    void update(HrtcRecordVo vo);
}
