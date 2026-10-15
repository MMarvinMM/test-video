package com.chinatower.emergency.service.impl;

import com.chinatower.emergency.dto.HrtcRecordVo;
import com.chinatower.emergency.mapper.HrtcRecordMapper;
import com.chinatower.emergency.service.IHrtcRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HrtcRecordServiceImpl implements IHrtcRecordService {

    @Resource
    HrtcRecordMapper hrtcRecordMapper;


    @Override
    public void addBatch(List<HrtcRecordVo> list) {
        hrtcRecordMapper.addBatch(list);
    }

    @Override
    public HrtcRecordVo findById(HrtcRecordVo vo) {
        return hrtcRecordMapper.findById(vo);
    }

    @Override
    public void delete(HrtcRecordVo vo) {
        hrtcRecordMapper.delete(vo);
    }

    @Override
    public void update(HrtcRecordVo vo) {
        hrtcRecordMapper.update(vo);
    }
}
