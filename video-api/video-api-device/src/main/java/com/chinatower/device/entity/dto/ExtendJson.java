package com.chinatower.device.entity.dto;

import lombok.Data;

@Data
public class ExtendJson {
    /**
     * 是否支持自定义告警框颜色 1-支持 0-不支持，默认不支持
     */
    private String supportFrameColor;

    /**
     * 是否支持分析基准图 1-支持 0-不支持，默认不支持
     */
    private String supportBaselinePic;

    /**
     * 是否支持双成像并析 1-支持 0-不支持，默认不支持
     */
    private String supportDoubleImgAnalysis;

    /**
     * 默认算法分析时段
     * "{/"startTime/":/"11:29:29/",/"endTime/":/"11:30:29/"}"
     */
    private String defaultAnalysisPeriod;

    /**
     * 算法描述文件地址
     * 鉴权后可以直接访问的URL地址（默认过期时间10年）
     */
    private String algDescribeFile;

    /**
     * 算法描述文件的名称
     */
    private String algDescribeFileName;

    /**
     * 图像要求： 0可见光、1热成像、2遥感
     */
    private String graphics;

    /**
     * 设置告警间隔时间1支持，0不支持，默认不支持;
     */
    private Integer supportAlarmInterval;
}
