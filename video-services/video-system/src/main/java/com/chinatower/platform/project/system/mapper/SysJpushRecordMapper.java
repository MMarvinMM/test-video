package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.msg.BsJpushRecord;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;

import java.util.List;

public interface SysJpushRecordMapper {
    List<BsJpushRecord> getJpushRecordlist(SysJpushRecord vo);


    int countJpushRecordUnread(SysJpushRecord vo);

    /**
     * 修改极光推送记录状态
     *
     * @param vo
     * @return 结果
     */
    int updateJpushRecordById(SysJpushRecord vo);


    int countJpushRecord(SysJpushRecord vo);


    /**
     * 修改极光推送记录状态
     * @Author liuxt
     * @param vo
     * @return 结果
     */
    int updateJpushRecord(SysJpushRecord vo);

}
