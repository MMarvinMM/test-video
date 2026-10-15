package com.chinatower.platform.modules.statistics.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @className: AlarmJudgeVO
 * @description:
 * @author: yongqi.han
 * @date: 2022/6/21
 * @requestID 需求单号及需求名称
 */
@Data
@ToString
public class AlarmJudgeVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //页码
    protected Integer pageNum;
    //页长
    protected Integer pageSize;
    //总数
    protected Integer total;
    //告警类型名称
    @Excel(name = "告警类型")
    private String warningTypeName;
    //算法编号
    @Excel(name = "算法编号")
    private String algorithmCode;
    //算法名称
    @Excel(name = "算法名称")
    private String algorithmName;
    //算法类别
    @Excel(name = "算法类别")
    private String functionCodeName;
    //算法类别编码
    private String functionCode;
    //算法厂家
    @Excel(name = "算法厂家")
    private String productName;
    //客户ID
    @Excel(name = "客户ID")
    private String tenantId;
    //客户名称
    @Excel(name = "客户名称")
    private String tenantName;
    //省份
    @Excel(name = "省份")
    private String provinceName;
    //地市
    @Excel(name = "地市")
    private String cityName;
    //有效设备数
    @Excel(name = "有效设备数")
    private int devNum;
    //省编码
    private String provinceId;
    //市编码
    private String cityId;
    //区县编码
    private String countyId;
    //告警类型ID
    private String warningTypeId;
    //告警来源
    private String warningSource;
    //设备编码
    private String deviceCode;
    /**
     * 0：算法维度 1：客户维度
     */
    private String dimensionFlag;
    //全部告警数量
    @Excel(name = "全部告警数量")
    private int totalAlarmNum;
    //未开启运营研判告警数量
    @Excel(name = "未开启运营研判告警数量")
    private int notOpenAlarmNum;
    //串行告警数量
    @Excel(name = "串行告警数量")
    private int serialAlarmNum;
    //并行告警数量
    @Excel(name = "并行告警数量")
    private int parallelAlarmNum;
    //全部研判数量
    @Excel(name = "全部研判数量")
    private int totalJudgeNum;
    //未开启运营研判的研判数量
    @Excel(name = "未开启运营研判的研判数量")
    private int notOpenJudgeNum;
    //串行研判数量
    @Excel(name = "串行研判数量")
    private int serialJudgeNum;
    //并行研判数量
    @Excel(name = "并行研判数量")
    private int parallelJudgeNum;
    //全部真警数量
    @Excel(name = "全部真警数量")
    private int totalRealAlarmNum;
    //未开启运营研判的真警数量
    @Excel(name = "未开启运营研判的真警数量")
    private int notOpenRealAlarmNum;
    //串行真警数量
    @Excel(name = "串行真警数量")
    private int serialRealAlarmNum;
    //并行真警数量
    @Excel(name = "并行真警数量")
    private int parallelRealAlarmNum;
    //全部误报数量
    @Excel(name = "全部误报数量")
    private int totalFalseAlarmNum;
    //未开启运营研判的误报数量
    @Excel(name = "未开启运营研判的误报数量")
    private int notOpenFalseAlarmNum;
    //串行误报数量
    @Excel(name = "串行误报数量")
    private int serialFalseAlarmNum;
    //并行误报数量
    @Excel(name = "并行误报数量")
    private int parallelFalseAlarmNum;
    private String year;
    private String month;
    private String day;
    //创建时间
    private Date createDate;
    //更新时间
    private Date updateDate;
    //错误率
    @Excel(name = "误报率")
    private String errorRate;
    //准确率
    @Excel(name = "准确率")
    private String successRate;
    //开始时间
    private String beginDate;
    //结束时间
    private String endDate;
    //导出限制数量
    private int exportLimitNum;
    private List<String> codeParamList;
    private List<String> typeParamList;
    private List<String> codeList;
    private List<String> typeList;
    //算法厂家编码
    private String productCode;
    /**
     * 属地编码
     */
    private String colonyCode;
    /**
     * 属地名称
     */
    private String colonyName;
    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    private String nameSort;

    private Integer pageNums;
    private Integer pageSizes;
}
