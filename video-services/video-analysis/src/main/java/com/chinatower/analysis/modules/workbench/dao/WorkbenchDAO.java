package com.chinatower.analysis.modules.workbench.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkbenchDAO {

    /**
     * 获取事件统计数量
     *
     * @param analysisVO (create_time 创建时间)
     * @return EventNumVO (todayAll 今日事件总数)
     * @return EventNumVO (todayProcess 今日处理中数量)
     * @return EventNumVO (todayComplete 今日已完结数量)
     */
    EventNumVO getEventNum(AnalysisVO analysisVO);

    /**
     * 获取事件统计数量不区分日期
     *
     * @param analysisVO
     * @return EventNumVO (todayAll 事件总数)
     */
    EventNumVO getEventNumTotal(AnalysisVO analysisVO);

    /**
     * 获取摄像机下事件统计数量
     *
     * @param statisticsVO
     * @return statisticsVO (num 事件总数)
     */
    List<Map> getEventNumDev(StatisticsVO statisticsVO);

    Integer getEventNumDevCount(StatisticsVO statisticsVO);

    /**
     * 获取事件统计 已完结数量
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    EventNumVO getEventNumComplete(AnalysisVO analysisVO);

    /**
     * 近7日事件数量
     *
     * @param analysisVO create_date 创建时间
     * @return eventDate 创建时间
     * @return name 名称
     * @return num 数量
     */
    List<Map> getEventNumBySource(AnalysisVO analysisVO);

    /**
     * 统计监控设备数量
     *
     * @param analysisVO
     * @return
     */
    DeviceStatisticsVO getStatisticsDevices(AnalysisVO analysisVO);

    /**
     * 无人机统计数量
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    UavDeviceStatisticsVo getUavDeviceStatistics(AnalysisVO analysisVO);

    /**
     * 统计喇叭设备数量
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    HornDeviceStatisticsVo getHornDeviceStatistics(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getHornDeviceStatisticsByArea(AnalysisVO analysisVO);

    /**
     * 无人机通道统计数量
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    UavDeviceStatisticsVo getUavNestStatistics(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getUavDeviceStatisticsByArea(AnalysisVO analysisVO);

    /**
     * 统计监控设备数量
     *
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getStatisticsDevicesUser(AnalysisVO analysisVO);

    /**
     * 统计监控设备数量
     *
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Map getStatisticsDevicesUserCount(AnalysisVO analysisVO);
    /**
     * 通道信息
     *
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getStatisticsChannelUser(AnalysisVO analysisVO);

    /**
     * 通道信息
     *
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    Map getStatisticsChannelUserCount(AnalysisVO analysisVO);

    /**
     * 通道配置算法任务数量 启用数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="od.tenant_id",industryCode="od.industry_code",deviceColumn = "od.device_code",acceptColumn="od.accept_node")
    Map getStatisticsChannelAlgorithm(AnalysisVO analysisVO);


    /**
     * 通道配置算法任务数量 启用数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="od.tenant_id",industryCode="od.industry_code",deviceColumn = "od.device_code",acceptColumn="od.accept_node")
    Map getStatisticsChannelAfterAlgorithm(AnalysisVO analysisVO);

    /**
     * 通道配置算法任务数量 启用数量
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="od.tenant_id",industryCode="od.industry_code",deviceColumn = "od.device_code",acceptColumn="od.accept_node")
    Map getStatisticsChannelForeAlgorithm(AnalysisVO analysisVO);


    /**
     * 事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    List<Map> getEventNumByArea(AnalysisVO analysisVO);

    /**
     * 告警数量按照区县及告警类型排行
     *
     * @param analysisVO
     */
    List<AnalysisAreaTypeVO> getEventNumByAreaAndType(AnalysisVO analysisVO);

    Integer getEventNumByAreaTotal(AnalysisVO analysisVO);

    /**
     * 查询告警来源统计
     * */
    List<Map> getEventAlarmNumSource(StatisticsVO statisticsVO);

    /**
     * 查询告警来源统计
     * */
    List<Map> getEventAlarmNumSourceIndustry(AnalysisVO analysisVO);

    /**
     * 查询告警类型统计
     * */
    List<Map> getEventAlarmNumTypeIndustry(AnalysisVO analysisVO);

    Integer getEventAlarmNumSourceTotal(StatisticsVO statisticsVO);


    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    List<Map> getInitDevTypeYearMonth(StatisticsVO statisticsVO);

    /**查询告警类型事件数量*/
    List<Map> getInitDevTypeYearMonthApp(StatisticsVO statisticsVO);

    Integer getInitDevTypeYearMonthAppTotal(StatisticsVO statisticsVO);

    /**
     * 获取事件统计 数量
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<Map> getEventNumListApp(AnalysisVO analysisVO);

    /**
     * 获取事件统计数量日期折线图(含告警等级对应数量)
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<Map> getEventNumLevelDayList(AnalysisVO analysisVO);


    /**
     * 获取事件统计数量小时折线图(含告警等级对应数量)
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<Map> getEventNumLevelHourList(AnalysisVO analysisVO);


    /**
     * 获取事件统计数量日期折线图(含告警类型对应数量)
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<AlarmNumByDateAndTypeVO> getEventNumTypeDayList(AnalysisVO analysisVO);


    /**
     * 获取事件统计数量小时折线图(含告警类型对应数量)
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<AlarmNumByDateAndTypeVO> getEventNumTypeHourList(AnalysisVO analysisVO);

    /**
     * 查询设备未处理最高告警等级
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<Map> getDeviceMaxLevelList(AnalysisVO analysisVO);

    /**
     * 获取事件统计 月份 数量
     *
     * @param analysisVO
     * @return EventNumVO (todayComplete 已完结数量)
     */
    List<Map> getEventNumListAppMonth(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getAlarmType();

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getAlarmTypeIndustry();

    /**
     * 大模型场景编码（sys_scene_config），类型准确率/排行有效类型须排除。
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getSceneIds();

    /**
     * 真实下游任务编码（sys_scene_alarm_rel.alarm_type）；
     * 与场景 type_value 可重叠，剔除场景时须保留这些编码。
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getDownstreamAlarmTypeIds();

    /**
     * 今日告警等级
     *
     * @param analysisVO  (year 年 month 月 day 日)
     * @return emergency_level 等级
     * @return num 数量
     */
    List<Map> getEventNumByLevel(AnalysisVO analysisVO);
    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return
     */
    List<Map> getEventNumByHour(AnalysisVO analysisVO);
    /**
     * 24小时告警趋势
     *
     * @param analysisVO 查询对象
     * @return
     */
    List<Map> getAlarmEventTrendNumByHour(AnalysisVO analysisVO);

    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return
     */
    List<Map> getEventNumDetailByHour(AnalysisVO analysisVO);

    /**
     * 24h告警压制统计
     *
     * @param analysisVO
     * @return
     */
    List<Map> getEventNumByInterceptHour(AnalysisVO analysisVO);

    /**
     * 24h告警压制统计
     *
     * @param analysisVO
     * @return
     */
    List<Map> getEventNumDetailByInterceptHour(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getEmergencyLevel();

    List<Map> getEventNumForApp(AnalysisVO analysisVO);
    /**
     * 算法任务数量
     * @param analysisVO
     * @return AlgorithmStatisticsVO(totalNum:算法任务数量)
     * @return AlgorithmStatisticsVO(enableNum:启用算法任务数量)
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    AlgorithmStatisticsVO getAlgorithmNum(AnalysisVO analysisVO);
    /**
     * 按年或月查询告警标签事件
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    List<Map> getAlarmTagYearMonth(StatisticsVO statisticsVO);

    List<StatisticsResp> getAlarmNumByLabel(StatisticsVO statisticsVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getTags(@Param("tenantId") String tenantId);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getDevices(StatisticsVO statisticsVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getIotDevices(StatisticsVO statisticsVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    String getAlarmByCode(String areaCode);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getAlarmByCodes(List<String> areaCode);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getGrids();

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> getGridsByGrid(@Param("grids") List<String> grids);

    @DS(EbikeDataBaseConstant.SLAVE)
    String getAlarmByLevel(String areaCode);

    /**
     * 查询告警总览
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    EventNumVO getNumAll(StatisticsVO statisticsVO);

    /**
     * 查询告警总览
     *
     * @param statisticsVO
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    EventNumVO getSuppressNum(StatisticsVO statisticsVO);

    /**
     * 查询摄像机告警统计
     *
     * @param statisticsVO
     */
    List<EventNumVO> getDeviceNumAll(StatisticsVO statisticsVO);

    /**
     * 查询摄像机告警统计
     *
     * @param statisticsVO 查询参数
     */
    List<DeviceAreaDistributionNumVO> getCamaraDeviceAreaDistributionNum(StatisticsVO statisticsVO);

    /**
     * 查询摄像机告警统计
     *
     * @param statisticsVO
     */
    List<EventNumVO> getIotDeviceNumAll(StatisticsVO statisticsVO);


    /**
     * 查询摄像机告警统计
     *
     * @param statisticsVO 查询参数
     */
    List<DeviceAreaDistributionNumVO> getIotDeviceAreaDistributionNum(StatisticsVO statisticsVO);
    /**
     * 查询告警总览
     *
     * @param statisticsVO 查询参数
     */
    AlarmStatisticsProcessingNumVO getAlarmStatisticsProcessingNum(StatisticsVO statisticsVO);


    /**
     * 查询设备昼夜数量(物联设备)
     *
     * @param statisticsVO
     */
    List<Map> getDeviceDayAndNightNum(StatisticsVO statisticsVO);


    /**
     * 查询摄像机告警统计压制
     *
     * @param statisticsVO
     */
    List<EventNumVO> getDeviceSuppressNum(StatisticsVO statisticsVO);

    /**
     * 查询物联告警统计压制
     *
     * @param statisticsVO
     */
    List<EventNumVO> getIotDeviceSuppressNum(StatisticsVO statisticsVO);

    /**
     * 获取事件统计 数量
     *
     * @param analysisVO
     */
    List<Map> getEventInterceptNumApp(AnalysisVO analysisVO);

    /**
     * 获取事件统计 数量
     *
     * @param analysisVO
     */
    List<Map> getEventDetailInterceptNumApp(AnalysisVO analysisVO);

    /**
     * 查询告警准确率
     *
     * @param statisticsVO
     */
    EventNumVO getEvenAccuracy(StatisticsVO statisticsVO);

    /**查询告警区域准确率排行*/
    List<Map> getEvenAreaAccuracy(AnalysisVO analysisVO);

    /**查询告警类型准确率排行*/
    List<Map> getEvenTypeAccuracy(AnalysisVO analysisVO);

    /**查询告警来源准确率排行*/
    List<Map> getEvenSourceAccuracy(AnalysisVO analysisVO);

    /**网格完结率统计*/
    List<Map> getEvenGridCompletion(AnalysisVO analysisVO);

    /**行政区域完结率统计*/
    List<Map> getEvenAreaCompletion(AnalysisVO analysisVO);

    /**行政区域完结率统计行业*/
    List<Map> getEvenAreaCompletionIndustry(AnalysisVO analysisVO);

    /**app告警来源详情*/
    List<Map> getEventDetailBySource(AnalysisVO analysisVO);

    /**app告警类型详情*/
    List<Map> getEventDetailByType(AnalysisVO analysisVO);

    /**行政区域完结率统计*/
    List<Map> getEvenAreaCompletionPc(AnalysisVO analysisVO);

    /**AI,一键,物联,摄像机告警组织完结率统计*/
    List<Map> getEvenDeptCompletion(AnalysisVO analysisVO);

    /**卫星组织完结率统计*/
    List<Map> getEvenDeptCompletionSatellite(@Param("vo") AnalysisVO analysisVO,@Param("sysDept")SysDeptDeviceVO sysDeptDeviceVO);

    /**雷达组织完结率统计*/
    List<Map> getEvenDeptCompletionRadars(@Param("vo") AnalysisVO analysisVO,@Param("sysDept")SysDeptDeviceVO sysDeptDeviceVO);

   /**人工组织完结率统计*/
    List<Map> getEvenDeptCompletionManual(AnalysisVO analysisVO);

    /**
     * 查询组织
     * @param industryCode
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String,String>> queryDepts(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 查询组织
     * @param sysUser
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", industryCode = "d.INDUSTRY_CODE")
    List<Map<String,String>> queryDeptIds(SysUser sysUser);


    /**
     * 查询用户所能看到的组织
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    @DataScope(tenantColumn = "d.tenant_id", industryCode = "d.industry_code", deptColumn = "d.dept_id")
    List<String> selectDepts(SysDept dept);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<SysDeptDeviceVO> getRaders(SysDept dept);


    @DS(EbikeDataBaseConstant.SLAVE)
    List<SysDeptDeviceVO> getSatellites(SysDept dept);

    @DS(EbikeDataBaseConstant.SLAVE)
    IotDeviceStatisticsVo getIotDeviceStatistics(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getIotDeviceStatisticsByArea(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    RadarDeviceStatisticsVo getRadarDeviceStatistics(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getRadarDeviceStatisticsByArea(AnalysisVO analysisVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getRecorderDeviceStatisticsByArea(AnalysisVO analysisVO);

    /**
     * 执法记录仪统计数量
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    DeviceStatisticsVO getRecorderDeviceStatistics(AnalysisVO analysisVO);

    /**
     * 执法记录仪通道统计数量
     * @param analysisVO
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    DeviceStatisticsVO getRecorderChannelStatistics(AnalysisVO analysisVO);

    /**
     * 查询无人机设备区域分布排行
     * @param statisticsVO
     * @return
     */
    List<DeviceAreaDistributionNumVO> getUavAreaDistributionNum(StatisticsVO statisticsVO);

    /**
     * 查询无人机设备
     * @param vo
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getUavDevices(StatisticsVO vo);

    /**
     * 根据标签ID列表查询标签信息（包含设备编码）
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map<String, Object>> getLabelInfoByIds(StatisticsVO statisticsVO);

    /**
     * 查询所有高点位设备编码
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getAllHighPointDevices(String tenantId, String industryCode);

    /**
     * 查询所有低点位设备编码
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getAllLowPointDevices(String tenantId, String industryCode);

    /**
     * 统计点位告警数量
     * @param statisticsVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    Integer getAlarmCountByDevices(StatisticsVO statisticsVO);
}
