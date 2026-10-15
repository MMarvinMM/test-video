package com.chinatower.receive.core.alarm;

import lombok.Data;

import java.util.List;

/**
 * 大模型配置相关 DTO 定义
 * 对应 modelConfig 字段的层级结构
 */
public class ModelConfigDTO {

    /**
     * 顶层配置项 DTO，映射 modelConfig 数组中的每个对象
     */
    @Data
    public static class ModelConfigItemDTO {
        /**
         * 配置编码，例如 dict-cvModeType
         */
        private String code;
        /**
         * 标绘/屏蔽区域与算法子项的关系集合
         */
        private List<PointsRelationDTO> pointsRelation;

    }

    /**
     * pointsRelation DTO，对应示例中的每个 itemCode 相关配置
     */
    @Data
    public static class PointsRelationDTO {
        /**
         * 算法子项编码，对应示例中的 itemCode
         */
        private String itemCode;
        /**
         * 点位配置 JSON 字符串，对应示例中的 points 字段
         */
        private String points;
        /**
         * 分析时间段 JSON 字符串，对应示例中的 analysisPeriod 字段
         */
        private String analysisPeriod;

        /**
         * 绊线配置 JSON 字符串，与 points 同级
         */
        private String tripThread;

    }

    /**
     * points 字段中每个监控区域 DTO
     */
    @Data
    public static class ModelConfigPointDTO {
        private String monitorAreaId;
        private List<ModelConfigMonitorPointDTO> moniPointList;
        private Integer monitorType;
        private String sceneCode;
        /**
         * 是否展示，"0" 表示不展示，其余/空表示展示
         */
        private String isShow;

    }

    /**
     * 单个监控点 DTO
     */
    @Data
    public static class ModelConfigMonitorPointDTO {
        private String monitorPointId;
        private Double percentX;
        private Double percentY;

    }

    /**
     * analysisPeriod 中的时间范围 DTO
     */
    @Data
    public static class AnalysisPeriodDTO {
        private String periodId;
        private String startTime;
        private String endTime;

    }
}

