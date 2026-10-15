package com.chinatower.platform.project.service;


import com.chinatower.platform.entity.msg.BsJpushRecord;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.project.mongodb.domain.BsJpushRecordMongo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 短信业务
 * @Author: 王建霞
 * @Date: 2021/10/12 15:00
 **/
public interface CommonSmsDbService {

    /**
     * 功能说明：保存短信记录 (原位置：CommonSmsServiceImpl)
     * @author 王建霞
	 * @param bsSmsRecord
	 * @param type
     * @return void
     * @date 2021/10/12 15:00
     **/
   void saveSmsRecord(BsSmsRecord bsSmsRecord, String type);

   /**
    * 功能说明：保存极光推送记录 (原位置：CommonSmsServiceImpl)
    * @author 张帅
    * @return
    * @throws
    * @requestID  需求单号及需求名称
    * @date 2022/5/29 20:35
    */
   void saveJpushRecord(BsJpushRecord bsJpushRecord,String type);

   void saveJpushRecordMongo(BsJpushRecordMongo bsJpushRecord, String type);

   void saveJpushRecordMongoBatch(List<BsJpushRecordMongo> bsJpushRecords);

   int getNoReadByUser(String alias);
   Map<String, Integer> getNoReadByUserList(List<JgPushRequest> pushNews);

    void saveBatchSmsRecord(BsSmsRecord bsSmsRecord, List<String> phoneList, List<BsSmsRecord> recordList, String smsSuccess);
}
