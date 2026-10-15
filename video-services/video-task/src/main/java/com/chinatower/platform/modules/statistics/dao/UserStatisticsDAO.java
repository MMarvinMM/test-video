package com.chinatower.platform.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.statistics.entity.StatisticsUserOnlineTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @className: UserStatisticsDAO
 * @description: 用户统计
 * @author: lyy
 * @date: 2023/04/08
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface UserStatisticsDAO {

    /**
     * 根据用户名获取租户信息
     * @param userNameList
     * @return
     */
    @DS("base")
    List<Map<String,String>> getUserInfos(List<String> userNameList);

    /**
     * 用户登录24小时分布统计
     * @param list
     * @return
     */
    int addUserStatistics(List<Map> list);
    int delUserStatistics(DateVO dateVO);

    /**
     * 用户登录时间统计
     * @param list
     * @return
     */
    int addCustomForType(List<Map> list);
    int delCustomForType(DateVO dateVO);

    /**
     * 用户登录次数统计
     * @param list
     * @return
     */
    int addCustomForDuration(List<Map> list);
    int delCustomForDuration(DateVO dateVO);

    /**
     * PC操作系统
     * @param list
     * @return
     */
    int addCustomForPcSystem(List<Map> list);
    int delCustomForPcSystem(DateVO dateVO);

    /**
     * PC浏览器排行
     * @param list
     * @return
     */
    int addCustomForPcBrowser(List<Map> list);
    int delCustomForPcBrowser(DateVO dateVO);

    /**
     * APP操作系统
     * @param list
     * @return
     */
    int addCustomForAppSystem(List<Map> list);
    int delCustomForAppSystem(DateVO dateVO);

    /**
     * APP手机型号排行
     * @param list
     * @return
     */
    int addCustomForIphoneModel(List<Map> list);
    int delCustomForIphoneModel(DateVO dateVO);

    /**
     * APP版本
     * @param list
     * @return
     */
    int addCustomForAppVersion(List<Map> list);
    int delCustomForAppVersion(DateVO dateVO);

    /**
     * 用户登录成功率分析
     * @param list
     * @return
     */
    int addCustomForSuccessRate(List<Map> list);
    int delCustomForSuccessRate(DateVO dateVO);

    /**
     * 用户登录失败原因分析
     * @param list
     * @return
     */
    int addCustomForFailCause(List<Map> list);
    int delCustomForFailCause(DateVO dateVO);


    /**
     * 查询在线用户基本信息
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<StatisticsUserOnlineTime> selectUserLoginInfo(Set<String> userIds);

    /**
     * 查询用户历史在线时长
     */
    List<StatisticsUserOnlineTime> selectUserOnlineTime(@Param("userIds") Set<String> userIds, @Param("loginTime") String loginTime);

    /**
     * 新增&修改用户登录时长
     * @param list
     * @return
     */
    int addUserOnlineTime(List<StatisticsUserOnlineTime> list);
    int updateUserOnlineTime(List<StatisticsUserOnlineTime> list);

}
