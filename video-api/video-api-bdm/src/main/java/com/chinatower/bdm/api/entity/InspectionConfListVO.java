package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 巡查任务配置列表VO
 * @author zl
 */
@Data
@ToString
public class InspectionConfListVO extends BaseDataScopeEntity implements Serializable {
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
     * 巡查类型
     */
    private String inspectionType;

    /**
     * 网格Id
     */
    private String gridId;

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

    /**
     * 下发时间
     */
    private String dataTime;
    /**
     * 是否指派
     */
    private String isAssign;

    /**
     * 处理状态
     */
    private String taskStatus;

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    @Excel(name = "修改人")
    private String modifier;

    /**
     * 完成状态 1已完成 0 未完成
     */
    @Excel(name = "状态",readConverterExp = "1=已完成,0=未完成")
    private String confStatus;
}
