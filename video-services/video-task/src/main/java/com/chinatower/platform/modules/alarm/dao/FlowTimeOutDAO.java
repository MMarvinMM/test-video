package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowTimeOutDAO {

    /**
     * 删除历史告警
     * @param lastDate
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int deleteHistoryList(String lastDate);

    /**
     * 备份数据到历史表
     * @param threeMonthsAgo
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int bakOperate3MonthsAgo(String threeMonthsAgo);

    /**
     * 删除原表对应日期内数据
     * @param threeMonthsAgo
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int deleteOperate3MonthsAgo(String threeMonthsAgo);
}
