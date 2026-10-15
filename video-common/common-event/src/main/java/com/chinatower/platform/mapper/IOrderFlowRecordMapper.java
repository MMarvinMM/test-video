package com.chinatower.platform.mapper;

import com.chinatower.platform.entity.VmAlarmOrderRecordDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:37
 */
@Mapper
public interface IOrderFlowRecordMapper {

    int insertAlarmOrderRecordList(VmAlarmOrderRecordDTO dto);
}
