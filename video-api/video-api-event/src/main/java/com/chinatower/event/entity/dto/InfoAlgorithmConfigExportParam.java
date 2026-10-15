package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: cuisj
 * @Date: 2024/6/28 14:46
 */

@Data
public class InfoAlgorithmConfigExportParam {

    //识别频率单位 1:小时 2:分钟 3:秒
    private String timeIntervalUnit;

    //识别频率
    private String timeInterval;

    //算法分析时段
    private List<AnalysisPeriod> analysisPeriod;

    //参数配置
    private List<ModelConfig> modelConfig;

    @Data
    public class AnalysisPeriod{
        private String periodId;

        private String startTime;
        private String endTime;
    }

    @Data
    public class ModelConfig{
        private String selectValue;
        private String value;
        private String name;
        private List<Map> dictList;
    }
}
