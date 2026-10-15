package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelInfo
 * @description: 水位管理实体类
 * @author: fengty
 * @create: 2024-12-12 11:17
 */
@Data
public class WaterLevelInfo extends BaseDataScopeEntity {
    private Long id;
    @Excel(name = "摄像机名称")
    private String deviceCode;
    @Excel(name = "摄像机名称")
    private String deviceName;
    @Excel(name = "摄像机状态",readConverterExp = "0=在线,1=离线",type=Excel.Type.EXPORT)
    private String deviceStatus;

    private String provinceId;
    @Excel(name = "省份")
    private String provinceName;
    private String cityId;
    @Excel(name = "地市")
    private String cityName;
    private String countryId;
    @Excel(name = "区县")
    private String countryName;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date updateTime;
    private String acceptNode;
    private Integer deleteFlag;
    protected Integer pageNum;
    protected Integer pageSize;
    private String sort;
    private String sortName;

    //修改开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeStart;
    //修改结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeEnd;

    private List<String> areaCodes;

    private List<Long> ids;
}
