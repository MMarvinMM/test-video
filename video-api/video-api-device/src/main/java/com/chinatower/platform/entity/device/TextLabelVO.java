package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 文本标签实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextLabelVO {

    /**
     * 文本内容
     */
    private String content;

    /**
     * 字号
     */
    private String fontSize;

    /**
     * 字体颜色
     */
    private String color;

    /**
     * 标签点位
     */
    private String point;

    private SizeVO size;

    private String lon;

    private String lat;

    /**
     * 场景编码 逗号分隔
     */
    private String sceneCode;

    /**
     * 是否回显 0:否
     */
    private String isShow;
}
