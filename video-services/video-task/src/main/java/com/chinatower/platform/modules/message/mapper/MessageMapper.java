package com.chinatower.platform.modules.message.mapper;

import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {

    /**
     * 删除消息推送数据
     * @param oneMonthAgo
     * @return
     */
    int delDataMonthAgo(String oneMonthAgo);
}
