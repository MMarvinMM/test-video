package com.chinatower.device.mongodb;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.device.mongodb.dto.SensorAttrValueAllDTO;
import com.chinatower.device.mongodb.dto.VmIotDeviceStatus;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
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
    private static final String UPDATE_TIME = "updateTime";

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
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())) {
            query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (!CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
            query.addCriteria(Criteria.where((DEVICE_CODE)).in(deviceReqDTO.getDeviceCodes()));
        }
        query.limit(1000);//限制查询1000条数据
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
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceCode())) {
            query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        }
        if (!CollectionUtils.isEmpty(deviceReqDTO.getDeviceCodes())) {
            query.addCriteria(Criteria.where((DEVICE_CODE)).in(deviceReqDTO.getDeviceCodes()));
        }
        query.with(Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        //不传该字段 默认为null--查实时
        if (StringUtils.isEmpty(deviceReqDTO.getTimeFrequency())){
            deviceReqDTO.setTimeFrequency("null");
        }
        String tableName = getTableName(deviceReqDTO.getTimeFrequency(), true, tableDate);
        sensorAttrValuePage.setCount(this.getTemplate(MongoDBType.PRIMARY).count(query,SensorAttrMapVO.class,
                tableName));
        PageRequest pageRequest = PageRequest.of(deviceReqDTO.getPageNo(),deviceReqDTO.getPageSize(),Sort.by(Sort.Direction.DESC,MONITOR_TIME));
        query.with(pageRequest);

        List<SensorAttrMapVO> attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query,SensorAttrMapVO.class,
                tableName);
        sensorAttrValuePage.setList(attrValues);
        return sensorAttrValuePage;
    }

    public List<VmIotDeviceStatus> listDeviceStatusData(DeviceReqDTO deviceReqDTO) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DEVICE_CODE).is(deviceReqDTO.getDeviceCode()));
        query.addCriteria(Criteria.where(UPDATE_TIME).gte(deviceReqDTO.getStartDate()).lte(deviceReqDTO.getEndDate()));
        query.with(Sort.by(Sort.Direction.DESC,UPDATE_TIME));
        query.limit(1000);
        String date = DateUtils.parseDateToStr(DeviceConstants.CURRENT_MONTH_FORMAT,
                DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,deviceReqDTO.getStartDate()));
        return this.getTemplate(MongoDBType.PRIMARY).find(query,VmIotDeviceStatus.class, DeviceConstants.TABLE_NAME_DEV + date);
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
