package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.JudgeAlarmSmsNoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @className: AlarmStudyAndJudgeSmsNoticeDAO
 * @description: 研判环节告警短信统计通知服务
 * @author: peng.haibo
 * @date: 2022/6/24
 */
@Mapper
public interface AlarmStudyAndJudgeSmsNoticeDAO {

    /**
     * @Description 当前告警
     * @author peng.haibo
     * @date 2022/6/24
     * @param judgeAlarmSmsNoticeVO 参数描述
     * @return java.util.List<com.chinatower.platform.modules.alarm.entity.JudgeAlarmSmsNoticeVO>
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<JudgeAlarmSmsNoticeVO> getJudgeAlarmNumList(JudgeAlarmSmsNoticeVO judgeAlarmSmsNoticeVO);

    /**
     * @Description 根据告警类型ID查询告警类型名称
     * @author peng.haibo
     * @date 2022/6/24
     * @param warningTypeId 参数描述
     * @return java.lang.String
     */
    @DS("base")
    String queryWarningTypeNameByWarningTypeId(@Param("warningTypeId") String warningTypeId);

    /**
     * 通过用户id获取用户相关信息
     * @param userId
     * @return
     */
    @DS("base")
    Map<String, String> getUserInfoById(@Param("userId") String userId);
}
