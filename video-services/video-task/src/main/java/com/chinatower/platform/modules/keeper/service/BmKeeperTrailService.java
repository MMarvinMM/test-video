package com.chinatower.platform.modules.keeper.service;


import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.modules.keeper.entity.GridKeeperTrail;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName BmKeeperTrailService
 * @Deacription
 * @Author zhaol
 * @Date 2022/3/31
 * @Version 1.0
 **/
@Component
public class BmKeeperTrailService extends AbstractBaseMongoDAO<GridKeeperTrail> {

    /**
     * 删除网格员轨迹
     * @param type MongoDB 数据库类型（主库、副库等）
     * @return 删除的文档数量
     */
    public Long removeTrail(MongoDBType type) {
        long deletedCount=0;
        // 创建查询条件对象
        Query query = new Query();
        // 创建日历对象，用于计算 30 天前的日期
        Calendar calc =Calendar.getInstance();
        try {
            calc.setTime(DateUtils.getNowDate());
            // 计算 30 天前的日期
            calc.add(Calendar.DATE, -30);
            Date minDate = calc.getTime();
            // 构建查询条件，筛选 "createTime" 小于等于 30 天前的数据
            query.addCriteria(Criteria.where("createTime").lte(minDate));
            // 删除符合条件的数据
            deletedCount = super.getTemplate(type).remove(query,"gridKeeperTrail").getDeletedCount();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return deletedCount;
    }
}
