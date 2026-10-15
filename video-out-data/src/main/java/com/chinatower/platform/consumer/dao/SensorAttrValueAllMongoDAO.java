package com.chinatower.platform.consumer.dao;

import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.outdata.api.entity.SensorAttrValueAllDTO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SensorAttrValueMongoDAO
 * @Deacription
 * @Author zheng.yan
 * @Date 2022/5/10
 * @Version 1.0
 **/
@Component
public class SensorAttrValueAllMongoDAO extends AbstractBaseMongoDAO<SensorAttrValueAllDTO> {

    private static final String MONITOR_TIME = "monitorTime";
    private static final String DEVICE_CODE = "deviceCode";

    /**
     * 查询传感器时间段数据
     * @param: deviceReqDTO
     * @return: List<SensorAttrValueAllDTO>
     * @author: zheng.yan
     * @date: 2022/5/11
     * @version:
     */
    public List<SensorAttrMapVO> listSensorPeriodData(DeviceReqDTO deviceReqDTO,String tableDate){
        Query query = new Query();
        query.addCriteria(Criteria.where(MONITOR_TIME).gte(deviceReqDTO.getStartDate()).lte(deviceReqDTO.getEndDate()));
        query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        query.with(Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        List<SensorAttrMapVO>  attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query,SensorAttrMapVO.class,
                DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + tableDate);
        return attrValues;
    }

    /**
     * 查询传感器时间段数据--APP
     */
    public List<SensorAttrMapVO> listSensorData(DeviceReqDTO deviceReqDTO,String tableDate){
        Query query = new Query();
        query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        query.with(Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        List<SensorAttrMapVO>  attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query,SensorAttrMapVO.class,
                DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + tableDate);
        return attrValues;
    }


    /**
     * 查询传感器时间段数据
     * @param: deviceReqDTO
     * @return: List<SensorAttrValueAllDTO>
     * @author: zheng.yan
     * @date: 2022/5/11
     * @version:
     */
    public Page<SensorAttrMapVO> pageSensorPeriodData(DeviceReqDTO deviceReqDTO, String tableDate){
        Page<SensorAttrMapVO> sensorAttrValuePage = new Page<>();
        Query query = new Query();
        query.addCriteria(Criteria.where(MONITOR_TIME).gte(deviceReqDTO.getStartDate()).lte(deviceReqDTO.getEndDate()));
        query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        query.with(Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        sensorAttrValuePage.setCount(this.getTemplate(MongoDBType.PRIMARY).count(query,SensorAttrMapVO.class,
                DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + tableDate));
        PageRequest pageRequest = PageRequest.of(deviceReqDTO.getPageNo(),deviceReqDTO.getPageSize(),Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        query.with(pageRequest);

        List<SensorAttrMapVO> attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query,SensorAttrMapVO.class,
                DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + tableDate);
        sensorAttrValuePage.setList(attrValues);
        return sensorAttrValuePage;
    }


    public List<SensorAttrMapVO> listSensorPeriodDataByTimeFrequency(DeviceReqDTO deviceReqDTO,String tableDate){
        Query query = new Query();
        query.addCriteria(Criteria.where(MONITOR_TIME).gte(deviceReqDTO.getStartDate()).lte(deviceReqDTO.getEndDate()));
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())) {
            query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (!CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
            query.addCriteria(Criteria.where((DEVICE_CODE)).in(deviceReqDTO.getDeviceCodes()));
        }
        //不传该字段 默认为null--查实时
        if (StringUtils.isEmpty(deviceReqDTO.getTimeFrequency())){
            deviceReqDTO.setTimeFrequency("null");
        }
        String tableName = getTableName(deviceReqDTO.getTimeFrequency(), true, tableDate);
        query.with(Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        List<SensorAttrMapVO>  attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query,SensorAttrMapVO.class,
                tableName);
        return attrValues;
    }

    public String getTableName(String timeFrequency, Boolean isFindAllData,String tableDate){
        String tableName = "";
        switch (timeFrequency){
            case "1":
                if (Boolean.TRUE.equals(isFindAllData)){
                    //查分钟累计全量--分钟累计全量表
                    tableName = DeviceConstants.TABLE_NAME_MINUTE_ALL;
                }else {
                    //查分钟累计--分钟累计表
                    tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.MINUTE;
                }
                break;
            case "2":
                if (Boolean.TRUE.equals(isFindAllData)){
                    //查小时累计全量--小时累计全量表
                    tableName = DeviceConstants.TABLE_NAME_HOUR_ALL;
                }else {
                    //查小时累计--小时累计表
                    tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.HOUR;
                }
                break;
            case "3":
                if (Boolean.TRUE.equals(isFindAllData)){
                    //查天累计全量--按天累计全量表
                    tableName = DeviceConstants.TABLE_NAME_DAY_ALL;
                }else {
                    //查天累计--按天累计表
                    tableName = DeviceConstants.TABLE_NAME_MOR + DeviceConstants.DAY;
                }
                break;
            default:
                if (Boolean.TRUE.equals(isFindAllData)){
                    //默认查实时累计--全量原表
                    tableName = DeviceConstants.TABLE_SENSOR_ATTRIBUTE_REPORT_ALL + tableDate;
                }else {
                    //默认查实时--原表
                    tableName = DeviceConstants.TABLE_NAME;
                }

        }
        return tableName;
    }
}
