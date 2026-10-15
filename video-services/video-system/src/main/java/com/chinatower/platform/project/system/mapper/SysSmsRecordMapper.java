package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.project.system.domain.SysSmsRecord;

import java.util.List;

public interface SysSmsRecordMapper {
    List<BsSmsRecord> list(SysSmsRecord vo);
}
