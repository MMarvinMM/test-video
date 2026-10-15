package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelConfigVO {

    //参数名称 特殊参数的中文名称，如：“僵尸车”停留时间（分钟）
    private String name;

    //参数编码 固定值，需要监测目标时长的算法定义为durationMinute,需要监测目标物数量的算法定义为triggerNumber
    private String code;

    //参数类型  字典表,用于校验并限定用户录入的参数（value）类型，包括：int、float等类型
    private String type;

    //参数值 客户算法配置时录入，当前任务触发告警的条件阈值，如：“僵尸车”监控设置的停留时长，人员密度监控设置的告警人数
    private String value;

    //参数最小值 参数值的下限，取闭区间，用于控制用户录入参数值(value)的范围并给平台提供参数校验依据
    private String minValue;

    //参数最大值 参数值的上限，取闭区间，用于控制用户录入参数值(value)的范围并给平台提供参数校验依据。
    private String maxValue;

    //排序 参数在平台展示时的顺序
    private Integer order;

    //参数说明 对当前参数含义、使用方式的详细解释
    private String descreption;

    //默认值 默认触发告警的条件阈值  
    private String defaultValue;

    /*算法高级参数字典:当type为dict_开头必填，用作高级参数值的配置*/
    private List<DictListVO> dictList;

    //有下拉框参数时：参数值对应名称
    private String selectValue;

    //1单选；2复选；默认复选。
    private String selectMode;

    /**
     * 大模型算法，子类型标绘区域
     */
    List<PointsRelationVO> pointsRelation;

    /**
     * 是否展示标志：1 展示，0 不展示
     */
    private Integer showFlag;

    /**
     * 子高参数组，元素结构同父元素，可选。
     * 承载从顶层迁移的子级高参定义（如 dict-cvModeTripThreadType、triggerNumber）。
     * 旧报文不存在该字段时反序列化为 null，序列化时不输出，100% 兼容。
     */
    private List<ModelConfigVO> extraValue;
}
