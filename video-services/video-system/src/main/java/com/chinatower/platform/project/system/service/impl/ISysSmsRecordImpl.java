package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.project.system.domain.SysSmsRecord;
import com.chinatower.platform.project.system.mapper.SysSmsRecordMapper;
import com.chinatower.platform.project.system.service.ISysSmsRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ISysSmsRecordImpl implements ISysSmsRecord {
    @Resource
    private SysSmsRecordMapper sysSmsRecordMapper;

    @Override
    public List<BsSmsRecord> list(SysSmsRecord vo) {
        if(!StringUtils.isEmpty(vo.getStartTime())){
            vo.setStartTime(vo.getStartTime()+ " " + DeviceConstants.START_TIME_SUFFIX);
        }
        if(!StringUtils.isEmpty(vo.getEndTime())){
            vo.setEndTime(vo.getEndTime() + " " + DeviceConstants.END_TIME_SUFFIX);
        }
        return sysSmsRecordMapper.list(vo);
    }
}
