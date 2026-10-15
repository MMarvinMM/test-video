package com.chinatower.event.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dto.zhongshi.AlgorithmDto;

import com.chinatower.event.mapper.AlgorithmProgressMapper;

import com.chinatower.event.service.AlgorithmService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 16:16
 */

@Service
@Slf4j
@DS(EbikeDataBaseConstant.PLATFORM)
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmProgressMapper, AlgorithmDto> implements AlgorithmService {
}
