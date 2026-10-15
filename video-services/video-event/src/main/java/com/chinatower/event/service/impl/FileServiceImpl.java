package com.chinatower.event.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dto.zhongshi.FileDto;
import com.chinatower.event.mapper.FileMapper;
import com.chinatower.event.service.FileService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 15:44
 */

@Service
@Slf4j
@DS(EbikeDataBaseConstant.PLATFORM)
public class FileServiceImpl extends ServiceImpl<FileMapper, FileDto> implements FileService {
}
