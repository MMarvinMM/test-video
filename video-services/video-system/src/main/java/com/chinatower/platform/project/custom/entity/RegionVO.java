package com.chinatower.platform.project.custom.entity;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * @ClassName: RegionVO
 * @Description: 行政区域实体
 * @Author: zhaol
 * @Date: 2022-02-11 11:51
 **/
@Data
@ToString
public class RegionVO extends SystemBaseEntity {


    private int currentPage;            //页码
    private int pageSize;           //每页数量
    private String regionCode; //行政区域编码
    private String regionName;          //行政区域名称
    private String regionLevel;          //行政区划等级
    private String parentCode;          //上级行政区划编码
    private String status;          //行政区划状态
    private String isEnd;          //是否末级区划
    private String creator;// 创建人
    private String creatorDate;// 创建时间
    private String modifier;// 最后修改人
    private String modifierDate;// 最后修改时间
    private String deleteFlag;// 有效状态

    private String tenantId;//租户id
    private String provinceId;
    private String cityId;
    private String countyId;
    private String streetId;

    private Set<String> regionCodeSet;

}
