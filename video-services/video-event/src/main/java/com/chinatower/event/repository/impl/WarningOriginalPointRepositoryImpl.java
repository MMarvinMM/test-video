package com.chinatower.event.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.WarningOriginalPointDO;
import com.chinatower.event.mapper.WarningOriginalPointMapper;
import com.chinatower.event.repository.IWarningOriginalPointRepository;

/**
 * <p>
 * 告警事件原始图片告警点表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@Slf4j
@Service
public class WarningOriginalPointRepositoryImpl extends ServiceImpl<WarningOriginalPointMapper, WarningOriginalPointDO> implements IWarningOriginalPointRepository {

}
