package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.sync.consumer.dao.RadarMongoDAO;
import com.chinatower.sync.consumer.item.Constants;
import com.chinatower.sync.consumer.item.RadarReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 保存雷达数据
 * @Author luo.zhenghong
 * @Date 2022/8/8
 * @Version release_v1.5
 **/
@Slf4j
@Component
public class RadarReportService {

    @Autowired
    private RadarMongoDAO radarMongoDAO;

    public void dealMsg(String message) {
        log.info("雷达数据监视消费信息:{}", message);
        List<RadarReportDTO> radarReportDTOList = new ArrayList<>();
        if(!StringUtils.isEmpty(message) && message.startsWith(Constants.SIGN)){
            radarReportDTOList = JSON.parseArray(message, RadarReportDTO.class);
        }else{
            RadarReportDTO radarReportDTO = JSON.parseObject(message, RadarReportDTO.class);
            radarReportDTOList.add(radarReportDTO);
        }
        if (!CollectionUtils.isEmpty(radarReportDTOList)) {
            Date date = new Date(radarReportDTOList.get(0).getReceiveTime());
            String yearMonth = DateUtils.dateToStringYM(date);
            String collectionName = Constants.TABLE_NAME_RADAR_REPORT + yearMonth;
            radarMongoDAO.insertAll(radarReportDTOList, collectionName,MongoDBType.PRIMARY);
        }
//        log.info("雷达数据处理完成");
    }

}
