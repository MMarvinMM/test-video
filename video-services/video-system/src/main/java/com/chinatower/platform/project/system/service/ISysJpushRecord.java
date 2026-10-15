package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.msg.BsJpushRecord;
import com.chinatower.platform.project.system.domain.BsJpushRecordMongo;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;

import java.util.List;

public interface ISysJpushRecord {

    List<BsJpushRecordMongo> getJpushRecordlist(SysJpushRecord vo);

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


    /**
     * 查询极光列表数量
     * @param vo
     * @return 极光列表数量
     */
    Integer getJpushRecordlistCount(SysJpushRecord vo);


}
