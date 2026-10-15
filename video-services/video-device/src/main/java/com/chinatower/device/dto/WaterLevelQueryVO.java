package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: WaterLevelVO
 * @Author: wangyt
 * @CreateTime: 2024-12-14  16:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WaterLevelQueryVO extends BaseDataScopeEntity {
    private Long id;
    private String deviceCode;
    private String deviceName;
    private String deviceAddress;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countryId;
    private String countryName;
    private String deviceStatus;
    private String deviceStatusName;
    private String waterLevel;
    private String gatherTime;
    private Integer pageNum;
    private Integer pageSize;
    private String waterLevelSort;
    private String gatherTimeSort;
    private List<String> imageList;
    private String picAddr;
    //修改开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String gatherTimeStart;
    //修改结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String gatherTimeEnd;
}
