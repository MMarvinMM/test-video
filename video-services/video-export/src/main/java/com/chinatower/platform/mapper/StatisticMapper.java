package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.platform.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticMapper {
    @DS(TableEnum.BASE)
    List<Map> getAlarmType(StatisticsVO statisticsVO);

    @DS(TableEnum.STATISTIC)
    List<Map> getEventStatisticalAnalysis(StatisticsVO statisticsVO);

    @DS(TableEnum.STATISTIC)
    Map getEventHandleTimeNum(StatisticsVO statisticsVO);

    @DS(TableEnum.STATISTIC)
    List<Map> getEventNumByHandle(AnalysisVO analysisVO);

    String getAreaNameById(String areaId);

    List<String> getAreaCodeByPCode(String parentCode);

    /**
     * 通道算法任务排行
     */
    List<Map<String,String>> getAllAlgorithmTaskRank(AnalysisVO analysisVO);

    List<String> getDeviceCodeListByDeptIds(AnalysisVO analysisVO);

    /**
     * 用户登录次数及时长排行
     * @param customVO
     * @return
     */
    @DS(TableEnum.STATISTIC)
    List<Map> getLoginDurationRankAndNum(CustomVO customVO);

}
