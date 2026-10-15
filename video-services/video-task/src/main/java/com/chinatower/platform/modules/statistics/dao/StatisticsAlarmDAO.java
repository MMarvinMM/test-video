package com.chinatower.platform.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.AlarmOrderJudgeVO;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @className: StatisticsAlarmDao
 * @description: 运营研判统计分析 Dao
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface StatisticsAlarmDAO {

    /**
     * @description: 运营研判数据批量新增
     * @author: yongqi.han
     * @date: 2022/6/20
     * @param list
     * @return: int
     */
    int addJudgeAlarm(List<JudgeVO> list);
    int addJudgeAlarmParam(List<JudgeVO> list);

    /**
     * @description: 删除当天的运营研判数据
     * @author: yongqi.han
     * @date: 2022/6/20
     * @param dateVO
     * @return: int
     */
    int delJudgeAlarm(DateVO dateVO);
    int delJudgeAlarmParam(DateVO dateVO);

    /**
     * @description: 查询告警工单表告警数量和研判数量
     * @author: yongqi.han
     * @date: 2022/6/20
      * @param dateVO
     * @return: List<JudgeVO>
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmOrderJudgeVO> getAlarmNumList(DateVO dateVO);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmOrderJudgeVO> getAlarmNumListParam(DateVO dateVO);

    /**
     * @description: 查询研判统计数量
     * @author: yongqi.han
     * @date: 2022/6/20
     * @param dateVO
     * @return: List<JudgeVO>
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmOrderJudgeVO> getOperateNumList(DateVO dateVO);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmOrderJudgeVO> getOperateNumListParam(DateVO dateVO);

    /**
     * 查询节点信息
     * @param platformCodeList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,String>> getPlatformCodeList(@Param("platformCodeList") List<String> platformCodeList);

    /**
     * 根据节点编码查询节点名称
     * @param platformCode
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    String getPlatformNameByCode(String platformCode);
}
