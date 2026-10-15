package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.consumer.dao.DeviceIndexInfoMongoDAO;
import com.chinatower.platform.consumer.item.DeviceIndexInfoMongo;
import com.chinatower.platform.consumer.item.DeviceIndexInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lzt
 */
@Component
@Slf4j
public class DeviceIndexInfoService {

    @Resource
    private DeviceIndexInfoMongoDAO deviceIndexInfoMongoDAO;

    @Resource
    private WaterLevelService waterLevelService;

    public void delDeviceIndexInfo(List<String> messages) {
        if(!CollectionUtils.isEmpty(messages)){
            messages.forEach(message -> {
                log.info("delDeviceIndexInfo消费的message为:{}",message);
                try {
                    DeviceIndexInfoVO infoVO = JSON.parseObject(message, DeviceIndexInfoVO.class);
                    DeviceIndexInfoMongo infoMongo = new DeviceIndexInfoMongo();
                    infoMongo.setDeviceCode(infoVO.getDeviceCode());
                    infoMongo.setDeviceName(infoVO.getDeviceName());
                    infoMongo.setChannelCode(infoVO.getChannelCode());
                    infoMongo.setChannelName(infoVO.getChannelName());
                    infoMongo.setAccessNode(infoVO.getAccessNode());
                    infoMongo.setReportTime(parseDate(infoVO.getReportTime()));
                    infoMongo.setMsgId(infoVO.getMsgid());
                    if (!StringUtils.isEmpty(infoVO.getWaterQualityIndex())){
                        infoMongo.setType("1");
                        infoMongo.setExtendField(infoVO.getWaterQualityIndex());
                        deviceIndexInfoMongoDAO.insert(infoMongo,MongoDBType.PRIMARY);
                    }else if (!StringUtils.isEmpty(infoVO.getWaterLevelIndex())){
                        infoMongo.setType("2");
                        infoMongo.setExtendField(infoVO.getWaterLevelIndex());
                        //保存水位最新数据
                        waterLevelService.saveWaterLevelData(message);
                        deviceIndexInfoMongoDAO.insert(infoMongo,MongoDBType.PRIMARY);
                    }
                    log.info("delDeviceIndexInfo消费的完成");
                }catch (Exception e){
                    log.error("delDeviceIndexInfo消费异常:{}",e);
                }
            });
        }
    }

    private Date parseDate(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
