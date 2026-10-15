package com.chinatower.event.repository.impl;

import com.chinatower.event.entity.vo.AlarmTypeSecondRecogVO;
import com.chinatower.event.mapper.AlarmTypeSecondRecogMapper;
import com.chinatower.event.repository.AlarmTypeSecondRecogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmTypeSecondRecogServiceImpl implements AlarmTypeSecondRecogService {

    @Autowired
    private AlarmTypeSecondRecogMapper alarmTypeSecondRecogMapper;


    /**
     * 批量插入
     * @param typeList
     */
    @Override
    public void insertBatch(List<AlarmTypeSecondRecogVO> typeList) {
        alarmTypeSecondRecogMapper.insertBatch(typeList);
    }
}
