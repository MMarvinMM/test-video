package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CheckpointAlarmVoTT extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 5047046406252940896L;
    /** 告警ID */
    private int alarmId;
    /** 告警时间（通过时间） */
    private String alarmTime;
    /** 告警开始时间 */
    private String alarmTimeStart;
    /** 告警结束时间 */
    private String alarmTimeEnd;
    /** 设备编码 */
    private String deviceCode;
    /** 设备名称 */
    private String deviceName;
    /** 原始图片文件url */
    private String originalPictureUrl;
    /** 报警框图url */
    private String alarmPictureUrl;
    /** 市编码 */
    private String cityCode;
    /** 县编码 */
    private String countyCode;
    /** 省编码 */
    private String provinceCode;
    /** 市名称 */
    private String cityName;
    /** 县名称*/
    private String countyName;
    /** 省名称 */
    private String provinceName;
    /** 车牌号 */
    private String vehicleNumber;
    /** 车牌类型 */
    private String vehicleType;
    /** 省市县全名 */
    private String areaName;
    /** 租户Name */
    private String tenantName;

    private List<String> ids;

    private List<String> deviceCodes;
    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    private String industryName;
    private String sort;
    private String sortName;
    private List<SysIndustryConf> sysIndustryConfList;
    private List<SysDictData> sysDictDataList;

}
