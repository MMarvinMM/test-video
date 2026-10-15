package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 巡查计划VO
 * @author zhaozj
 */
@Data
@ToString
public class InspectionConfVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 巡查任务编号
     */
    private String confId;
    /**
     * 任务名称
     */
    @Excel(name = "计划名称")
    private String cnofName;

    /**
     * 巡查网格
     */
    @Excel(name = "巡查网格")
    private String gridName;

    /**
     * 巡查人
     */
    @Excel(name = "巡查人")
    private String keeperName;

    /**
     * 创建人姓名
     */
    @Excel(name = "创建人")
    private String creator;

    @Excel(name = "创建时间")
    private String createTime;

    @Excel(name = "修改人")
    private String modifier;

    @Excel(name = "修改时间")
    private String updateTime;

    /**
     * 完成状态 1已完成 0 未完成
     */
    @Excel(name = "状态",readConverterExp = "1=已完成,0=未完成")
    private String confStatus;

    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataScope;
}
