package com.chinatower.platform.project.custom.entity;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: VideoPlatformGit
 * @description: 铁塔组织与行政区划关系
 * @author: hued@strongdata.com.cn
 * @create: 2021/07/09 17:54
 */
@Data
@ToString
public class OrgAreaVO extends SystemBaseEntity {

    private static final long serialVersionUID = 1L;

    //组织省份编码
    private String orgProvinceId;
    //组织省份名称
    private String orgProvinceName;
    //组织地市编码
    private String orgCityId;
    //组织地市名称
    private String orgCityName;
    //行政区划省份
    private String provinceId;
    //行政区划地市
    private String cityId;
    //行政区划区县
    private String countyId;

    private String creator;
    private String creatorDate;
    private String modifier;
    private String modifierDate;
    private String deleteFlag;

    private List<String> provinceIdList;
    private List<String> cityIdList;
}
