package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
/**
 * 巡查计划配置vo
 * @author zl
 */
@Data
@ToString
public class InspectionConfVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 计划ID
     */
    private String confId;

    /**
     * 计划名称
     */
    private String cnofName;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 是否重复 1-重复；0-不重复
     */
    private String isRepeat;

    /**
     * 巡查级别
     */
    private String inspectionLevel;

    /**
     * 巡查类型
     */
    private String inspectionType;

    /**
     * 巡查提醒
     */
    private String inspectionWarn;

    /**
     * 不重复选择时间
     */
    private String inspectionTime;

    private String creator;

    /**
     * 开始时间
     */
    private String levelStartTime;

    /**
     * 结束时间
     */
    private String levelEndTime;
    /**
     * 天数
     */
    private String levelCount;
    /**
     * 级别天数
     */
    private String levelDayCount;
    /**
     * 指派类型
     */
    private String isAppoint;
    /**
     * 网格员名称
     */
    private String keeperName;
    /**
     * 网格员编码
     */
    private String keeperId;
    /**
     * 配置描述
     */
    private String confDesc;
    /**
     * 状态
     */
    private String confStatus;
    /**
     * 是否超时
     */
    private String confFjId;

    /**
     * 不重复类型插入完成标识
     */
    private String noRepeatOver;

    /**
     * 网格员编码数组
     */
    private String[] gridIds;

    private static final long serialVersionUID = 1L;

    /**
     * 下发时间
     */
    private String dataTime;

    /**
     * 是否指派
     */
    private String isAssign;

    private String taskId;

    //巡查时段集合
    private List<InspectionConfPeriodVO> confPeriodList;
    //巡查时段
    private String inspectionPeriod;


    //巡查时段开始时间
    private String inspectionStartTime;
    //巡查时段结束时间
    private String inspectionEndTime;

}
