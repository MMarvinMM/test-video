package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达告警区域查询实体类
 */
@Data
public class RadarAlarmRegionDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警区域ID
     */
    private String regionId;

    /**
     * 告警区域id列表
     */
    private List<String> regionIdList;

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警区域类型
     */
    private String regionType;

    /**
     * 是否关联模板
     */
    private String isUnionTemplate;

    /**
     * 创建时间开始
     */
    private String createTimeBegin;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 修改时间开始
     */
    private String updateTimeBegin;

    /**
     * 修改时间结束
     */
    private String updateTimeEnd;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 是否开启分页
     */
    private Boolean isEnablePage;

    /**
     * 排序名称
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;
}
