package com.chinatower.event.repository.impl;

import com.chinatower.event.dto.PushThirdDto;
import com.chinatower.event.repository.ICommonRepository;
import com.chinatower.event.repository.PushThirdService;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PushThirdServiceImpl implements PushThirdService {

    @Autowired
    private ICommonRepository iCommonRepository;
    @Override
    @Async("taskExecutor")
    public void pushThirdOrder(String originalJson, List<ReceiveAlarmVO> alarmList) {
        PushThirdDto dto = new PushThirdDto();
        dto.setOriginalJson(originalJson);
        dto.setAlarmList(alarmList);
        iCommonRepository.pushThird(dto);
    }
}
