package com.chinatower.event.repository.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.dataobject.MessageSendInfoDO;
import com.chinatower.event.mapper.MessageSendInfoMapper;
import com.chinatower.event.repository.IMessageSendInfoRepository;

/**
 * <p>
 * 消息发送通知信息表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-25
 */
@Service
public class MessageSendInfoRepositoryImpl extends ServiceImpl<MessageSendInfoMapper, MessageSendInfoDO> implements IMessageSendInfoRepository {

}
