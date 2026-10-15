package com.chinatower.analysis.modules.statistic.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceStatisticsDAO {

    /**
     * 摄像机数量区域排行(租户)
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getCameraNumByArea(AnalysisVO analysisVO);

    /**
     * 摄像机订购算法排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getOrderAlgorithmRank(AnalysisVO analysisVO);
    /**
     * 通道配置前端算法任务排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getForeAlgorithmTaskRank(AnalysisVO analysisVO);
    /**
     * 通道配置后端算法任务排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getAfterAlgorithmTaskRank(AnalysisVO analysisVO);
    /**
     * 通道配置后端算法任务排行-国产化
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getAfterAlgorithmTaskRankLocal(AnalysisVO analysisVO);
    /**
     * 通道启用前端算法任务排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getStartForeAlgorithmTaskRank(AnalysisVO analysisVO);
    /**
     * 通道启用后端算法任务排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getStartAfterAlgorithmTaskRank(AnalysisVO analysisVO);
    /**
     * 通道启用后端算法任务排行-国产化
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getStartAfterAlgorithmTaskRankLocal(AnalysisVO analysisVO);
    /**
     * 通道启用后端算法任务排行
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getDeviceCodeListByDeptIds(AnalysisVO analysisVO);

    /**
     * 通道启用后端算法任务总数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Map getStartAfterAlgorithmTaskTotalNum(AnalysisVO analysisVO);
    /**
     * 通道启用后端算法任务总数量-国产化
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Map getStartAfterAlgorithmTaskTotalNumLocal(AnalysisVO analysisVO);

    /**
     * 订购算法总数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    Map getAlgorithmTaskTotalNum(AnalysisVO analysisVO);

    /**
     * 通道启用前端算法任务总数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Map getStartForeAlgorithmTaskTotalNum(AnalysisVO analysisVO);


    /**
     * 算法名称列表
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getAlgorithmTaskList(AnalysisVO analysisVO);
    /**
     * 查询用户下订购摄像机编码
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="dataTable.accept_node")
    List<String> getDeviceCodeByUser(AnalysisVO analysisVO);

    /**
     * 查询用户下订购摄像机编码
     */
    List<Map> getDeviceNumInEvent(AnalysisVO analysisVO);

    /**
     * 根据摄像机标签查询设备
     * @param statisticsVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getDeviceCodeByLabelIds(StatisticsVO statisticsVO);

}
