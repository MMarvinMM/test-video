package com.chinatower.video.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryGetEnumInfoReq extends CommonFeignEntity {
    private static final long serialVersionUID = 1L;

    private Long dictCode; //字典编码
    private int dictSort;//字典排序
    private String dictLabel; //字典标签
    private String dictValue;//字典键值
    private String dictType; //字典类型
    private String cssClass;  //样式属性（其他样式扩展）
    private String listClass;//表格回显样式
    private String isDefault;// 是否默认（1是 0否）
    private String status;//状态（0正常 1停用）
    private String remake;//备注

    private Integer pageNum;
    private Integer pageSize;
    
    private String dictLabelEqual;
}
