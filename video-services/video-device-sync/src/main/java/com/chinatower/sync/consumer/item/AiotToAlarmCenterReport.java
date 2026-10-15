package com.chinatower.sync.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;


@Data
public class AiotToAlarmCenterReport extends BaseMongoDTO {
    /**
     * {
     *   "AlarmID": "05017001",
     *   "AlarmLevel": "3",
     *   "AlarmStatus": "1",
     *   "AlarmTitle": "过压告警",
     *   "DeviceID": "YJDJ19064980",
     *   "DeviceName": "YJDJ19064980",
     *   "EventTime": "2025-05-15 18:12:31",
     *   "ObjectID": "YJDJ19064980",
     *   "ObjectName": "YJDJ19064980",
     *   "ObjectTypeID": "0202",
     *   "ObjectTypeName": "低速充电主机",
     *   "PrivateID": "8bab8ff5-bd8c-48e3-9da0-448d167f2ab1",
     *   "StationDeviceId": "0546020201100011",
     *   "SystemID": "03",
     *   "glcity": "三亚市分公司",
     *   "glcitycode": "460200",
     *   "glcounnty": "吉阳区",
     *   "glcounntycode": "460203",
     *   "glprovince": "海南分公司",
     *   "glprovincecode": "460000",
     *   "sub_business": "4"
     * }
     */

    private String AlarmID;
    private String AlarmLevel;
    private String AlarmStatus;
    private String AlarmTitle;
    private String DeviceID;
    private String DeviceName;
    private String EventTime;
    private String ObjectID;
    private String ObjectName;
    private String ObjectTypeID;
    private String ObjectTypeName;

    private String PrivateID;
    private String StationDeviceId;
    private String SystemID;
    private String glcity;
    private String glcitycode;
    private String glcounnty;
    private String glcounntycode;
    private String glprovince;
    private String glprovincecode;
    private String sub_business;
}
