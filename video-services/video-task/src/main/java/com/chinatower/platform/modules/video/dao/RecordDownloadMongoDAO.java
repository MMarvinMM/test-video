package com.chinatower.platform.modules.video.dao;

import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.video.api.entity.RecordDownloadDTO;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class RecordDownloadMongoDAO extends AbstractBaseMongoDAO<RecordDownloadDTO> {

    /**
     * 删除记录
     * @param type
     * @param query
     * @return
     */
    public Long removeRecord(MongoDBType type, Query query) {
        long deletedCount=0;
//        Calendar calc =Calendar.getInstance();
        try {
//            calc.setTime(DateUtils.getNowDate());
//            calc.add(Calendar.DATE, date);
//            Date minDate = calc.getTime();
//            query.addCriteria(Criteria.where("createTime").lte(minDate));
//            List<Integer> list = Arrays.asList(2, 3);
//            query.addCriteria(Criteria.where("operateSourceType").in(list.toArray()));
            deletedCount = super.getTemplate(type).remove(query,"vm_record_download").getDeletedCount();
            System.out.println(deletedCount);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return deletedCount;
    }
}
