package com.chinatower.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Name AreaEventVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/19 15 : 58
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AreaEventVO {
    private String areaCode;
    private String areaName;
    private String regionName;
    private Integer num;
    /**
     * 省
     */
    private String provinceId;
    /**
     * 市
     */
    private String cityId;
    /**
     * 区
     */
    private String countyId;
    /**
     * 乡镇
     */
    private String streetId;
}
