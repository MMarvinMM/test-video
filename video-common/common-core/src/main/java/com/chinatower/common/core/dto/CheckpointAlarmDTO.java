package com.chinatower.common.core.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.admin.SysDictData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询车辆进出记录列表入参
 * @author zl
 */
@Data
@SuppressWarnings("all")
public class CheckpointAlarmDTO extends BaseDataScopeEntity implements Serializable {

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
    /** 车牌类型 未知 1 客车 2 货车 3 轿车 4 面包车 5 小货车 6 行人 7 二轮车 8 三轮车 9 SUV/MPV 10 中型客车 11 机动车 12 非机动车 13 小型轿车 14 微型轿车 15 皮卡车 16 集装箱卡车 17 微卡，栏板卡 18 渣土车 19 吊车，工程车 20 油罐车，槽罐车 21 混凝土搅拌车 22 平板拖车 23 两厢轿车 24 三厢轿车 25 轿跑 26 小型客车 27 公交车 28 自行车 29 摩托车 30 大货车 31 中货车 32 挖掘车 33 推土车 34 泵车 35 叉车 36 二轮电瓶车 37 */
    private String vehicleType;
    /** 省市县全名 */
    private String areaName;

    private String vehicleColor;

    private String vehicleNumColor;

    private List<String> ids;
    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    protected Object list;
    private String sort;
    private String sortName;

    private List<String> deviceCodes;
    private List<String> vehicleNumbers;
    private String queryStartTime;
    private String queryEndTime;
    private List<SysDictData> sysDictDataList;
}
