package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlgoInfoReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 算法唯一编码
     */
    private String algorithmCode;
    /** 算法名称 */
    private String algorithmName;
    /** 算法小类编码 */
    private String algoMinTypeCode;
    /** 厂家名称 */
    private String productName;
    /** 厂家编码 */
    private String productCode;
    /** 芯片平台 */
    private int chipPlatform;
    /** 算法描述 */
    private String algorithmDescription;
    /** 应用场景 1视联场景;2无人机场景 */
    private String appScenarios;
    /** 演示文档URL */
    private String documentUrl;
    /** LOGO图标 */
    private String logoUrl;
    /** 版本号 */
    private String version;
    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
    /** 算法上下架 1:下架 2:上架 0:初始化状态 */
    private int algorithmShape;
    /**
     * 1-新增；2-修改； 3-删除
     */
    private int optType;
    /** 事件告警编码集合 */
    private List<AlarmTypeData> alarmTypeCodeList;
    /** 是否支持3D放大 1支持，0不支持，默认不支持 */
    private int supportEnlarge;
    /** 置信度 算法上报告警的默认阈值 */
    private String confidence;
    /** 目标最小像素  */
    private String objectMinPixel;
    /** 是否支持绊线 1支持，0不支持，默认不支持 */
    private int supportTripThread;
    /** 是否支持多监控区域 1支持，0不支持，默认不支持 */
    private int supportAlarmAreaList;
    /** 是否支持屏蔽区域 1支持，0不支持，默认不支持 */
    private int supportShield;

    /** 分析类型：1单张图片;2短视频;3多张图片;4实时拉流;5单张+短视频；当分析类型为单张图片（analyTayp=1）时，算法支持分析时段设定 */
    private int analyType;

    /** 推送流类型：是否支持推流: 0=否（默认），1=是 */
    private String pushStreamType;
    /**算法分析类型：1=图片,2=短视频,4=实时流.（可多值，英文逗号,分隔）*/
    private String algorithmAnalysisType;
    /**
     * 特殊参数说明
     */
    transient List modelConfig;

    /**
     * 是否支持基准区域 1-支持 0-不支持，默认不支持
     */
    private String supportBenchmark;

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

    /**
     *  算法模型类型（1小模型算法 2 大模型算法）
     */
    private String algorithmModelType;

    /**
     * 行业属性，多个用逗号分隔
     */
//    private String industryAttribute;
}
