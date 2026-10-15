package com.chinatower.platform.project.mapper;


import com.chinatower.platform.entity.msg.BsJpushRecord;

public interface BsJpushRecordMapper {

    int insertSelective(BsJpushRecord record);

    int getNoReadByUser(String alias);
}
