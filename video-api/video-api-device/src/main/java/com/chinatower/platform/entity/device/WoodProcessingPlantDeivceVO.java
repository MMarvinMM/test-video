package com.chinatower.platform.entity.device;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WoodProcessingPlantVO
 * @description: 木材加工厂摄像机传输类
 * @author: fengty
 * @create: 2024-08-13 19:53
 */
@Data
public class WoodProcessingPlantDeivceVO extends BaseDataScopeEntity {
    private Long id;
    @Excel(name = "摄像机名称")
    private String deviceName;
    @Excel(name = "摄像机编号")
    private String deviceCode;
    @Excel(name = "通道名称")
    private String channelName;
    @Excel(name = "通道编号")
    private String channelCode;
    @Excel(name = "摄像机状态",readConverterExp="0=在线,1=离线")
    private String status;
    @Excel(name = "省份")
    private String provinceName;
    @Excel(name = "地市")
    private String cityName;
    @Excel(name = "区县")
    private String countryName;

    private String provinceId;

    private String cityId;

    private String countryId;
    private String townId;

    private List<String> deviceCodes;
    private List<String> channelCodeList;
    private String queryStartTime;
    private String queryEndTime;
    private Date alarmTimeStart;
    private Date alarmTimeEnd;
    /**
     * 告警数量
     */
    private int count;

    private Integer pageNum;
    private Integer pageSize;
    private String orderByCondition;
    private String sort;
    private String orderType;
    private List<Long> ids;
    private String provinceCode;
    private String cityCode;
    private String countyCode;
    private String townCode;
    private Date createTime;
    private String creator;
    private Date updateTime;
    private String modifier;
    private Integer deleteFlag;
    private String tenantId;
    private String industryCode;
    private String appVerCode;
}
