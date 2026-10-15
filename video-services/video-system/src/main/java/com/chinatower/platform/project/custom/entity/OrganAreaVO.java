package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pei.g
 * @date 2021/6/17 17:37
 * @description 组织和管理区域关系表
 */
@Data
@ToString
public class OrganAreaVO implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String streetId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creator;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date creatorDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String modifier;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date modifierDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deleteFlag;//删除状态：0正常 1删除
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String areaFlag;//行政区划标识：1省份2地市3区县
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyName;

}
