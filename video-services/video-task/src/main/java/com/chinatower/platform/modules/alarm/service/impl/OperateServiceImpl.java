package com.chinatower.platform.modules.alarm.service.impl;

import com.chinatower.analysis.api.entity.AlarmOrderJudgeVO;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.modules.alarm.service.OperateService;
import com.chinatower.platform.modules.statistics.dao.StatisticsAlarmDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OperateServiceImpl implements OperateService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private StatisticsAlarmDAO statisticsAlarmDao;

    /**
     * 查询运营研判数量
     * @param dateVO
     * @return
     */
    @Override
    public List<AlarmOrderJudgeVO> getOperateNumList(DateVO dateVO) {
        List<AlarmOrderJudgeVO> resultList = new ArrayList<>();
            resultList = statisticsAlarmDao.getOperateNumList(dateVO);
        return resultList;
    }

    /**
     * 根据入参查询运营研判告警数量
     * @param dateVO
     * @return
     */
    @Override
    public List<AlarmOrderJudgeVO> getOperateNumListParam(DateVO dateVO) {
            List<AlarmOrderJudgeVO> resultList = new ArrayList<>();
            resultList = statisticsAlarmDao.getOperateNumListParam(dateVO);
        return resultList;
    }
}
