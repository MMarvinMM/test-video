package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeStatisticsVO;
import com.chinatower.event.entity.dto.OverdueRemindDto;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @className: JudgeStatisticsMapper
 * @description: 研判统计
 * @author: yongqi.han
 * @date: 2022/9/5
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface JudgeStatisticsDAO {

    /**
     * 查询研判环节短信间隔时间
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<JudgeStatisticsVO> getJudgeSentTimeMinute();

    /**
     * 查询默认流程中研判环节的短信间隔时间
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<JudgeStatisticsVO> getDefaultJudgeSentTimeMinute();

    /**
     * 查询告警类型
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<JudgeStatisticsVO> getJudgeAlarmTypeList(DateVO dateVO);

    /**
     * 查询告警类型对应的告警数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<JudgeStatisticsVO> getJudgeList(DateVO dateVO);

    /**
     * 查询用户信息
     * @param judgeList
     * @return
     */
    @DS("base")
    List<JudgeUserInfoVO> getUserByTenantId(@Param("list") List<JudgeStatisticsVO> judgeList);

    /**
     * 查询用户组织信息
     * @param judgeOrganList
     * @return
     */
    @DS("base")
    List<JudgeOrganVO> getParentOrgId(@Param("idList") List<JudgeOrganVO> judgeOrganList);

    /**
     * 查询用户设备信息
     * @param organResultList
     * @return
     */
    @DS("base")
    List<JudgeUserInfoVO> getDeviceList(@Param("organResultList") List<JudgeOrganVO> organResultList);

    /**
     * 查询告警类型
     * @return
     */
    @DS("base")
    List<JudgeStatisticsVO> getAlarmTypeList();

    /**
     * 根据用户和角色查询
     * @param userLists
     * @param roleLists
     * @return
     */
    @DS("base")
    List<JudgeUserInfoVO> getUserByTypeUser(@Param("userLists") List<String> userLists,@Param("roleLists")  List<Map> roleLists);

    /**
     * 查询用户角色权限
     * @param userInfoList
     * @return
     */
    @DS("base")
    List<UserDataScopeVO> getRoleDataScopeByUserId(@Param("userList") List<JudgeUserInfoVO> userInfoList);

    /**
     * 获取数据权限
     * @param userDataScopeList2
     * @return
     */
    @DS("base")
    List<DeptDataScopeVO> getDataScope(@Param("userData") List<UserDataScopeVO> userDataScopeList2);

    /**
     * 查询短信配置开关
     * @return
     */
    @DS("base")
    String getSendMsgSwitch();

    /**
     * 删除历史数据
     * @param date
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int deleteHistoryList(@Param("date")String date);

    /**
     * 获取所有网格的网格员
     * @param gridList
     * @return
     */
    @DS("base")
    List<JudgeGridInfoVO> getGridUserByGridIdList(@Param("gridList") List<String> gridList);

    /**
     * 查询未达到短信上限的客户
     * @param dto
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<SmsSendStatisticsDto> getSmsSendStatistics(SmsSendStatisticsDto dto);

    /**
     * 根据工单号查询告警来源摄像机信息
     * @param
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<OverdueRemindDto> getDeviceInfoByOrderIds(@Param("orderIds")List<String> orderIds,@Param("tableName")String tableName);

    /**
     * 批量新增短信用户发送统计表
     * @param smsSendStatisticsDtoList
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int insertSmsSendStatistics(@Param("list") List<SmsSendStatisticsDto> smsSendStatisticsDtoList);

    /**
     * 批量新增短信用户发送统计表
     * @param smsSendStatisticsDtoList
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateSmsSendStatistics(@Param("list") List<SmsSendStatisticsDto> smsSendStatisticsDtoList);


    /**
     * 创建表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

}
