package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.project.system.domain.SysSmsRecord;

import java.util.List;

public interface ISysSmsRecord {

    List<BsSmsRecord> list(SysSmsRecord vo);
}
