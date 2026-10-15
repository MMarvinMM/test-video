package com.chinatower.event.constant;

/**
 * 大模型事件描述字段组装相关常量
 */
public final class BigModelDescConstants {
    private BigModelDescConstants() {
    }

    /**
     * modelConfig JSON 字段名
     */
    public static final class JsonField {
        private JsonField() {
        }

        public static final String CODE = "code";
        public static final String VALUE = "value";
        public static final String SELECT_VALUE = "selectValue";
        public static final String POINTS_RELATION = "pointsRelation";

        public static final String ANALYSIS_PERIOD = "analysisPeriod";
        public static final String PERIOD_ID = "periodId";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";

        public static final String TIME_INTERVAL = "timeInterval";
        /** 车辆密度阈值 */
        public static final String TRIGGER_NUMBER = "triggerNumber";
    }

    /**
     * 大模型视觉分析/标绘 modelConfig code 字典类型
     * dict_value 示例：dict-cvModeType、dict-emergencyCvModeType、dict-agricultureCvModeType、dict-waterCvModeType
     */
    public static final String LARGE_MODEL_DRAW_AREA_CONFIG_CODE = "large_model_draw_area_config_code";

    /**
     * modelConfig 中的配置项 code
     */
    public static final class ModelConfigCode {
        private ModelConfigCode() {
        }

        public static final String PROVINCE = "dict-province";
        public static final String CV_MODE_TYPE = "dict-cvModeType";
        /** 经纬应急视觉参数 */
        public static final String EMERGENCY_CV_MODE_TYPE = "dict-emergencyCvModeType";
        /** 经纬农业视觉大模型分析类型 */
        public static final String AGRICULTURE_CV_MODE_TYPE = "dict-agricultureCvModeType";
        /** 经纬水利视觉大模型分析类型 */
        public static final String WATER_CV_MODE_TYPE = "dict-waterCvModeType";
    }

    /**
     * 事件描述-算法配置参数（前端展示）name 字段
     */
    public static final class DisplayName {
        private DisplayName() {
        }

        public static final String PROVINCE = "省份";
        public static final String TIME_INTERVAL = "识别频率";
        /** 应急视觉-车辆密度阈值 */
        public static final String VEHICLE_DENSITY_THRESHOLD = "车辆密度阈值";
        /** 农业视觉-人员密度阈值 */
        public static final String PERSON_DENSITY_THRESHOLD = "人员密度阈值";
    }

    /**
     * 识别频率单位文案
     */
    public static final class TimeIntervalUnitLabel {
        private TimeIntervalUnitLabel() {
        }

        public static final String HOUR = "小时";
        public static final String MINUTE = "分钟";
        public static final String SECOND = "秒";
    }
}

