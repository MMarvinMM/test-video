package com.chinatower.platform.entity.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @ClassName OrderInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/27 15:56
 * @Version 1.0
 **/
@Data
@ToString
@Alias("orderAlgorithms")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderAlgorithmsVO implements Serializable {

    private String algorithmCode;
    private String algorithmName;

    /**
     * 图标名称
     */
    private String iconCode;
    /**
     * 图标颜色
     */
    private String iconColour;

    private String algorithmDescription;

    /**
     * 订购关系编码
     */
    private String orderCode;

    /**
     * 受理节点
     */
    private String acceptNode;

    private String deviceCode;

    /** 是否联动算法 0：否，1：是 */
    private String isLinkageAlgorithm;

    private String extendJson;

    /**
     * 分析类型：1单张图片(抽帧);2短视频;3多张图片;4实时拉流;5单张+短视频；当分析类型为单张图片（analyTayp=1）时，算法支持分析时段设定
     */
    private String analyType;

    /**
     *  算法模型类型（1小模型算法 2 大模型算法）
     */
    private String algorithmModelType;
}
