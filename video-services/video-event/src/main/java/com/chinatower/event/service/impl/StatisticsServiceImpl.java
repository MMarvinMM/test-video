package com.chinatower.event.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dto.zhongshi.AlgorithmDto;
import com.chinatower.event.dto.zhongshi.StatisticsDto;
import com.chinatower.event.mapper.AlgorithmProgressMapper;
import com.chinatower.event.mapper.StatisticsDtMapper;
import com.chinatower.event.mapper.StatisticsMapper;
import com.chinatower.event.service.StatisticsService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2025/5/12 18:02
 */

@Service
@Slf4j
@DS(EbikeDataBaseConstant.PLATFORM)
public class StatisticsServiceImpl extends ServiceImpl<StatisticsDtMapper, StatisticsDto> implements StatisticsService {
}
