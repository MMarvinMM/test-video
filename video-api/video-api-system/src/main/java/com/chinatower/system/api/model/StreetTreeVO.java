package com.chinatower.system.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DeviceTreeVO
 * @Description 摄像头树实体
 * @Author chensw
 * @Date 2022/1/18
 */

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreetTreeVO implements Serializable {


    /**
     * 区域编码
     */
    private String areaCode;         //区域编码

    /**
     * 区域名称
     */
    private String areaName;        //区域名称

    /**
     * 区域等级
     */
    private String areaLevel;       //区域等级

    /**
     * 父级编码
     */
    private String areaParentCode;              //父级编码


    /**
     * 下级
     */
    private List<StreetTreeVO> list;

}
