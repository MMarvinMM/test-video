package com.chinatower.platform.modules.alarm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @className: AlarmRecycleDAO
 * @description: 告警回收定时清理
 * @author: chao
 * @date: 2022/06/13
 */
@Mapper
public interface AlarmRecycleDAO {

    /**
     * 根据告警时间查询回收站告警数量
     * @param alarmTime
     * @return
     */
    int countRecycleNum(@Param("alarmTime") Date alarmTime);

    /**
     * 根据告警时间删除回收站告警
     * @param alarmTime
     * @return
     */
    int delRecycleAlarm(@Param("alarmTime") Date alarmTime);

    /**
     * 插入回收站历史表
     * @param alarmTime
     * @return
     */
    int addHisRecycleAlarm(@Param("alarmTime") Date alarmTime);
}
