package com.chinatower.event.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 枚举值信息VO
 */
@Data
@ToString
public class EnumVO {

    private static final long serialVersionUID = 1L;

    private String enumId; //枚举值id
    private String enumCode;//枚举值编码
    private int enumTypeId; //枚举值类型id
    private String enumName;//枚举值详细名称
    private String description; //描述
    private String paramCategory;  //参数类别
    private String creator;//创建人
    private String creatorDate;// 创建时间
    private String deleteFlag;//删除标志
    private int pageNum; //页码
    private int pageSize; //每页条数
    private String[] enumTypeIdStr; //枚举类型ID列表
    private String[] enumIdStr;//枚举值ID列表

    private List<EnumVO> enumList;//枚举值信息列表
}
