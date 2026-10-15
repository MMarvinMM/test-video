package com.chinatower.platform.modules.video.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.modules.video.dao.RecordDownloadMongoDAO;
import com.chinatower.platform.modules.video.dao.VideoDAO;
import com.chinatower.platform.modules.video.entity.ChannelVideoPlansVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 沉淀录像计划记录任务定时
 */
@Component
@Slf4j
public class VideoService {
    @Resource
    private VideoDAO videoDAO;

    @Resource
    private RecordDownloadMongoDAO downloadMongoDAO;

    /**
     * 沉淀录像计划记录任务定时
     */
    @XxlJob("saveVideoPlanHisTaskHandler")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> saveVideoPlanHisTaskHandler(String param) {
        log.info("沉淀录像计划记录任务定时");
        XxlJobLogger.log("沉淀录像计划记录任务定时");
        List<ChannelVideoPlansVO> list = videoDAO.getList();
        if(!CollectionUtils.isEmpty(list)){
            videoDAO.insert();
        }
        return ReturnT.SUCCESS;
    }


    /**
     * 删除15天前的录像下载记录，并且在每月的最后一天创建下个月的下载表
     * @param param
     * @return
     */
    @XxlJob("recordDownloadTaskHandler")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> recordDownloadTaskHandler(String param) {
        log.info("录像下载相关任务定时");
        XxlJobLogger.log("录像下载相关任务定时");
        Query query15 = new Query();
        Calendar calc =Calendar.getInstance();
        log.info("当前时间:{}", calc);
        calc.setTime(com.chinatower.common.core.utils.DateUtils.getNowDate());
        calc.add(Calendar.DATE, -15);
        log.info("15天前时间:{}", calc.getTime());
        Date min15Date = calc.getTime();

        List<Integer> list = Arrays.asList(2, 3);
        query15.addCriteria(Criteria.where("createTime").lte(min15Date));
        query15.addCriteria(Criteria.where("operateSourceType").in(list.toArray()));
        Long  remove15Count= downloadMongoDAO.removeRecord(MongoDBType.PRIMARY, query15);
        log.info("删除15天前告警下载录像数据条数：{}", remove15Count);

        log.info("-----------------------------------------------------------");
        Query query30 = new Query();
        Calendar calc30 =Calendar.getInstance();
        calc30.setTime(com.chinatower.common.core.utils.DateUtils.getNowDate());
        calc30.add(Calendar.DATE, -30);
        Date min30Date = calc30.getTime();
        log.info("30天前时间:{}", calc30.getTime());
        query30.addCriteria(Criteria.where("createTime").lte(min30Date));
        query30.addCriteria(Criteria.where("operateSourceType").is(1));

        Long  remove30Count= downloadMongoDAO.removeRecord(MongoDBType.PRIMARY, query30);
        log.info("删除30天前的前端下载录像数据条数：{}", remove30Count);
        return ReturnT.SUCCESS;
    }


}
