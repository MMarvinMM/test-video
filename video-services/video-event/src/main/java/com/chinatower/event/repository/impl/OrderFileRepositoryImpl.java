package com.chinatower.event.repository.impl;

import com.chinatower.event.entity.dto.OrderFileDO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.mapper.OrderFileMapper;
import com.chinatower.event.repository.IOrderFileRepository;

/**
 * <p>
 * 附件表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-04-27
 */
@Service
public class OrderFileRepositoryImpl extends ServiceImpl<OrderFileMapper, OrderFileDO> implements IOrderFileRepository {

}
