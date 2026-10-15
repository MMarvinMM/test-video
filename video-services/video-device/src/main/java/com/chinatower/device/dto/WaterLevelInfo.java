package com.chinatower.device.dto;

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
    private String deviceCode;
    private String deviceName;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countryId;
    private String countryName;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date updateTime;
    private String acceptNode;
    private Integer deleteFlag;
    private String deviceStatus;
    private String location;

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
}
