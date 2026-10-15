package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisSerial {
    /**
     * 序列名称
     * */
    private String name;
    /**
     * 统计类型枚举
     * */
    private String type;
    /**
     * 统计结果
     * */
    private List<TaoheAnalysisSerialItem> serialDatas;
}
