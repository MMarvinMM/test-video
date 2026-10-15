package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 无人机型号实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UavModelVO extends BaseDataScopeEntity {

    /**
     * 设备型号ID
     */
    @ApiModelProperty("设备型号ID")
    private Long id;

    /**
     * 型号编码
     */
    @ApiModelProperty("型号编码")
    private String modelCode;

    /**
     * 型号名称
     */
    @ApiModelProperty("型号名称")
    private String modelName;

    /**
     * 厂家编码
     */
    @ApiModelProperty("厂家编码")
    private String manufacturerCode;

    /**
     * 负载枚举值(domain)
     */
    @ApiModelProperty("负载枚举值(domain)")
    private String domain;

    /**
     * 负载枚举值类型
     */
    @ApiModelProperty("负载枚举值(type)")
    private String type;

    /**
     * 负载枚举值父类型
     */
    @ApiModelProperty("负载枚举值(subType)")
    private String subType;

    /**
     * 飞行器类别
     */
    @ApiModelProperty("飞行器类别")
    private String aircraftCategory;

    /**
     * 飞行器类型
     */
    @ApiModelProperty("飞行器类型")
    private String aircraftType;

    /**
     * 起飞全重（kg）
     */
    @ApiModelProperty("起飞全重（kg）")
    private String weight;

    /**
     * 最大飞行时间（min）
     */
    @ApiModelProperty("最大飞行时间（min）")
    private String maxFlyTime;

    /**
     * 最大水平速度（m/s）
     */
    @ApiModelProperty("最大水平速度（m/s）")
    private String maxLevelSpeed;

    /**
     * 分页每页数量
     */
    @ApiModelProperty("分页每页数量")
    private Integer pageSize;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum;

    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage = true;

    /**
     * 查询创建开始时间
     */
    @ApiModelProperty("查询创建开始时间")
    private String createTimeBegin;

    /**
     * 查询创建结束时间
     */
    @ApiModelProperty("查询创建结束时间")
    private String createTimeEnd;

    /**
     * 查询修改开始时间
     */
    @ApiModelProperty("查询修改开始时间")
    private String updateTimeBegin;

    /**
     * 查询修改结束时间
     */
    @ApiModelProperty("查询修改结束时间")
    private String updateTimeEnd;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String orderColumn;

    /**
     * 排序类型 asc:升序  desc:降序
     */
    @ApiModelProperty("排序类型 asc:升序  desc:降序")
    private String sort;

    /**
     * 厂家编码合计
     */
    @ApiModelProperty("厂家编码合计")
    private List<String> manufacturerCodeList;

    /**
     * 飞行器类别编码合计
     */
    @ApiModelProperty("飞行器类别编码合计")
    private List<String> aircraftCategoryList;

    /**
     * 飞行器类型编码合计
     */
    @ApiModelProperty("飞行器类型编码合计")
    private List<String> aircraftTypeList;
}
