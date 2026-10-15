package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * @ClassName: RegionVO
 * @Description: 行政区域实体
 * @Author: zhaol
 * @Date: 2022-02-11 11:51
 **/
@Data
@ToString
public class DeptStreetVO extends BaseDataScopeEntity {

    private String areaCode;
    private String areaName;
    /**
     * 区域等级
     */
    private String areaLevel;
    /**
     * 父级编码
     */
    private String areaParentCode;
    private String longitude;
    private String latitude;

    private String keyWords;


}
