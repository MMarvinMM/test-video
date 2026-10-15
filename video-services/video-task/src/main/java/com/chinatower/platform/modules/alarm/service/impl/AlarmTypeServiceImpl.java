package com.chinatower.platform.modules.alarm.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.modules.alarm.dao.AlarmStudyAndJudgeSmsNoticeDAO;
import com.chinatower.platform.modules.alarm.service.AlarmTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
@DS("base")
public class AlarmTypeServiceImpl implements AlarmTypeService {

    @Resource
    private AlarmStudyAndJudgeSmsNoticeDAO alarmStudyAndJudgeSmsNoticeDAO;

    /**
     * @param warningTypeId 参数描述
     * @return java.lang.String
     * @Description 根据告警类型ID查询告警类型名称
     * @author peng.haibo
     * @date 2022/6/24
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String queryWarningTypeNameByWarningTypeId(String warningTypeId) {
        return alarmStudyAndJudgeSmsNoticeDAO.queryWarningTypeNameByWarningTypeId(warningTypeId);
    }

    /**
     * @param userId 参数描述
     * @return Map<String, String>
     * @Description 通过用户id获取用户相关信息
     * @author peng.haibo
     * @date 2022/6/24
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, String> getUserInfoById(String userId) {
        return alarmStudyAndJudgeSmsNoticeDAO.getUserInfoById(userId);
    }
}
