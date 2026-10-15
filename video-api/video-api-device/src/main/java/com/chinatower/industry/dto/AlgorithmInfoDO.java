package com.chinatower.industry.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 算法基本信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@Getter
@Setter
@TableName("vm_algorithm_info_t")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "algorithm_id", type = IdType.AUTO)
    private Long algorithmId;

    /**
     * 算法唯一编码
     */
    private String algorithmCode;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 算法小类编码
     */
    private String algoMinTypeCode;

    /**
     * 算法小类名称
     */
    private String algoMinTypeName;

    /**
     * 算法大类编码
     */
    private String algoTypeCode;

    /**
     * 算法大类名称
     */
    private String algoTypeName;

    /**
     * 算法中类编码
     */
    private String algoSubTypeCode;

    /**
     * 算法中类名称
     */
    private String algoSubTypeName;

    /**
     * 厂家名称
     */
    private String productName;

    /**
     * 厂家编码
     */
    private String productCode;

    /**
     * 芯片平台
     */
    private String chipPlatform;

    /**
     * 算法描述
     */
    private String algorithmDescription;

    /**
     * 应用场景
     */
    private String appScenarios;

    /**
     * 演示文档URL
     */
    private String documentUrl;

    /**
     * LOGO图标
     */
    private String logoUrl;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 算法上下架 1:下架 2:上架 0:初始化状态
     */
    private String algorithmShape;

    /**
     * 1-有效；2-失效
     */
    private String status;

    /**
     * 功能编码
     */
    private String functionCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0正常1删除
     */
    private String deleteFlag;

    /**
     * 是否支持3D放大 1支持，0不支持，默认不支持
     */
    private int supportEnlarge;

    /**
     * 置信度 算法上报告警的默认阈值
     */
    private String confidence;

    /**
     * 目标最小像素 算法可识别的目标最小像素值，如10*10
     */
    private String objectMinPixel;

    /**
     * 是否支持绊线 1支持，0不支持，默认不支持
     */
    private int supportTripThread;

    /**
     * 是否支持多监控区域 1支持，0不支持，默认不支持
     */
    private int supportAlarmAreaList;

    /**
     * 是否支持屏蔽区域 1支持，0不支持，默认不支持
     */
    private int supportShield;

    /**
     * 特殊参数说明Json
     */
    private String modelConfig;

    //分析类型：1单张图片;2短视频;3多张图片;4实时拉流;5单张+短视频；当分析类型为单张图片（analyTayp=1）时，算法支持分析时段设定
    private int analyType;

    /**
     * 是否支持基准区域 1-支持 0-不支持
     */
    private int supportBenchmark;

    /**
     * 算法基础信息扩展字段
     */
    private String extendJson;

    @TableField(exist = false)
    private String supportFrameColor;

    @TableField(exist = false)
    private String supportDoubleImgAnalysis;

    @TableField(exist = false)
    private Integer supportAlarmInterval;

    /**
     * 默认算法分析时段
     * "{/"startTime/":/"11:29:29/",/"endTime/":/"11:30:29/"}"
     */
    @TableField(exist = false)
    private String defaultAnalysisPeriod;

    /**
     * 算法描述文件地址
     * 鉴权后可以直接访问的URL地址（默认过期时间10年）
     */
    @TableField(exist = false)
    private String algDescribeFile;

    /**
     * 算法描述文件的名称
     */
    @TableField(exist = false)
    private String algDescribeFileName;
}
