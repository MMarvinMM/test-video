package com.chinatower.platform.project.mapper;


import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.project.vo.SmsRecordQuery;

import java.util.List;

public interface BsSmsRecordMapper {

    int insertSelective(BsSmsRecord record);

    int updateByMsgId(BsSmsRecord record);

    List<BsSmsRecord> selectList(SmsRecordQuery query);

    void batchInsert(List<BsSmsRecord> list);
}
