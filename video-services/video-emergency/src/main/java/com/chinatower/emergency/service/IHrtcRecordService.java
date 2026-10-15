package com.chinatower.emergency.service;

import com.chinatower.emergency.dto.HrtcRecordVo;

import java.util.List;

public interface IHrtcRecordService {

    void addBatch(List<HrtcRecordVo> list);

    HrtcRecordVo findById(HrtcRecordVo vo);

    void delete(HrtcRecordVo vo);

    void update(HrtcRecordVo vo);
}
