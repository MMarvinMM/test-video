package com.chinatower.platform.project.numtwin.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:15
 */

@Data
public class NumTwinsConfigDto {

    /**
     * 数字孪生配置id
     */
    private Long id;

    /**
     * 大屏id
     */
    private String screenId;

    /**
     * 大屏名称
     */
    private String screenName;

    /**
     * 数字孪生地址
     */
    private String numTwinsAddress;

    /**
     * 省份code
     */
    private String provinceCode;

    /**
     * 地市code
     */
    private String cityCode;

    /**
     * 区县code
     */
    private String countyCode;

    /**
     * 省份Name
     */
    private String provinceName;

    /**
     * 地市Name
     */
    private String cityName;

    /**
     * 区县Name
     */
    private String countyName;

    /**
     * 渲染引擎
     */
    private String engineType;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标记位
     */
    private String deleteFlag;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 信令地址
     */
    private String signalAddress;

    /**
     * 并发量
     */
    private String concurrency;

    /**
     * 优先级数据集合
     */
    private List<NumTwinsPriorityDto> priorityDtoList;

}
