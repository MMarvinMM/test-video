package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 行政区域表信息
 * @author guyuefei
 * @date 2022-05-13 15:58:34
 * @version v1.1
 */
@Data
public class SysAreaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 区域编码 */
    private String areaCode;
    /** 区域名称 */
    private String areaName;
    /** 区域等级 */
    private String areaLevel;
    /** 父级编码 */
    private String areaParentCode;
    /** 父级名称 */
    private String areaParentName;
    /** 状态（0正常 1停用） */
    private String status;
    /** 经度 */
    private String longitude;
    /** 纬度 */
    private String latitude;

}
