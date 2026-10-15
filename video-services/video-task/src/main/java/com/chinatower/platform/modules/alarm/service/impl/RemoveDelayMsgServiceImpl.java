package com.chinatower.platform.modules.alarm.service.impl;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.dao.JudgeStatisticsDAO;
import com.chinatower.platform.modules.alarm.service.RemoveDelayMsgService;
import com.chinatower.platform.mongo.OrderDelayMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class RemoveDelayMsgServiceImpl implements RemoveDelayMsgService {

    @Autowired
    OrderDelayMessageMapper orderDelayMessageMapper;
    @Resource
    private JudgeStatisticsDAO judgeStatisticsDAO;

    /**
     * 清楚延迟消息和短信数量统计
     */
    @Override
    public void removeDelayMsg() {
        try {
            //查询并删除mongo中所有已处理的数据+PC7天之前的数据\APP今日之前的数据

            Query query = new Query();
            Calendar calc = Calendar.getInstance();
            calc.set(calc.get(Calendar.YEAR),calc.get(Calendar.MONTH),calc.get(Calendar.DATE),8,0,0);
            Date minDateApp = calc.getTime();
            calc.set(calc.get(Calendar.YEAR),calc.get(Calendar.MONTH),calc.get(Calendar.DATE)-6,8,0,0);
            Date minDatePc = calc.getTime();
            query.addCriteria(
                    new Criteria().orOperator(
                                    Criteria.where(Constants.PARAM_CREATE_TIME).lte(minDateApp).and(Constants.PARAM_SOURCE).is(Constants.ONE),
                                    Criteria.where(Constants.PARAM_CREATE_TIME).lte(minDatePc).and(Constants.PARAM_SOURCE).is(Constants.PARAM_ZERO),
                                    Criteria.where(Constants.PARAM_OPERATE_STATUS).is(Constants.ONE)
                    )
            );
            // 删除PC稍后推送消息7日之前所有数据或已经处理的消息     以及   删除APP稍后推送消息今日之前所有数据或已经处理的消息
            orderDelayMessageMapper.removeData(query,MongoDBType.PRIMARY,Constants.SYS_ORDER_DELAY_TEBLE);

            //删除发送短信统计数量表中当日之前的所有数据
            String date = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,new Date());
            judgeStatisticsDAO.deleteHistoryList(date);
        }catch (Exception e){
            log.error("removeDelayMsg-清楚延迟消息和短信数量统计异常-异常信息:"+e);
        }


    }
}
