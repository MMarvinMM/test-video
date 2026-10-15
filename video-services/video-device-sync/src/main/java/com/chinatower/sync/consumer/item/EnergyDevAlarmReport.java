package com.chinatower.sync.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;


@Data
public class EnergyDevAlarmReport extends BaseMongoDTO {
    /**
     * [
     *        {
     * 		"alarmId":"4d277be5-d691-4011-bdd4-6aadc820d9a4",
     *              "businessType": "平台类型",
     *              "stationCode": "站址code",
     *              "stationName": " 站址名称",
     *              "parentCode": "主设备code",
     *              "deviceCode": "告警设备code",
     *              "deviceName":"告警设备名称",
     * "deviceType":"设备类型",
     * "deviceTypeName":"设备类型名称",
     * "crewProvinceId  ":"设备行政省Id",
     * "crewProvinceName  ":"设备行政省名称",
     * "crewCityId  ":"设备行政市Id",
     * "crewCityName":"设备行政市名称",
     * "crewCountyId":"设备行政区Id",
     *             "crewCountyName":" 设备行政区名称",
     *  "provinceId" : "123",
     *  "cityId" : "1232",
     *     		 "countyId" : "2312",
     * "factoryCode”:"设备厂家code",
     * "factoryName”:"设备厂家名称",
     * "status": "设备入网状态",
     * "mid": "信号量id",
     * "alarmName": "告警名称 对应信号量Id",
     *             "alarmDesc ": "告警
     * 义",
     *             "alarmLevel ": "告警级别 4",
     * "alarmFlag ": "告警标识 开始/结束",
     *             "alarmTime ": "告警时间 2022-05-17 10:59:22 "，
     * "alarmValue ": "告警时间 2022-05-17 10:59:22 "，
     * "midCode":"信号量枚举01 02",
     * "midCodeDesc":”信号量枚举含义”,
     * "serialNo":"告警流水号",
     * "source":"1"
     *        }
     * ]
     */

    /**
     * deviceCode + “#” + mid
     */
    private String alarmKey;

    private String alarmId;
    private String businessType;
    private String stationCode;
    private String stationName;
    private String parentCode;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String deviceTypeName;
    private String crewProvinceId;
    private String crewProvinceName;
    private String crewCityId;
    private String crewCityName;
    private String crewCountyId;
    private String crewCountyName;
    private String provinceId;
    private String cityId;
    private String countyId;
    private String factoryCode;
    private String factoryName;
    private String status;
    private String mid;
    private String alarmName;
    private String alarmDesc;
    private String alarmLevel;
    private String alarmFlag;
    private String alarmTime;
    private String alarmClearTime;
    private String alarmValue;
    private String midCode;
    private String midCodeDesc;
    private String serialNo;
    private String source;
}
