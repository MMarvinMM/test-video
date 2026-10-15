package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName: StatisticsVO
 * @Description: 实体
 * @Author: zzh
 * @Date: 2022-02-18
 **/
@Data
@ToString
public class StatisticsVO extends PageDTO {
    private static final long serialVersionUID = 1L;
    /**
     * 客户编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customerCode;

    /**
     * 维度标识:1当年；2当月；3前一天;4当天
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dimensionFlag;

    /**
     * 趋势标识:year年；month月；day天
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trendFlag;

    /**
     * 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryStartTime;

    /**
     * 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryEndTime;

    /**
     * 告警事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderStatus;

    /**
     * 权限类型：//1全部摄像头权限;2组织下摄像头权限;3仅本人可见
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String anthType;

    /**
     * 租户ID
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantId;

    /**
     * 区县编码集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List countyIdList;

    /**
     * 省编码集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List ttProvinceIdList;

    /**
     * 市编码集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List ttCityIdList;

    /**
     * 告警类型ID列表
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> typeList;

    /**
     * 来源标识：1租户表；2铁塔表
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sourceFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otherFlag; //1包含其他

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantListAuthority; //1省份地市无下属租户或租户下无设备

    /**
     * 省编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceId;//省id

    /**
     * 市编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityId;//市id

    /**
     * 区县编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyId;//区县id

    /**
     * 乡镇编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String townId;//乡镇id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> checkList;//选择导出的sheet页

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String totalRemark;


    /**
     * 查询起始时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String beginDate;

    /**
     * 查询结束时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String endDate;

    /**
     * 告警类型编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alarmType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String warningTypeId;

    /**
     * 算法编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String algorithmCode;

    /**
     * 页码
     *
     */
    private Integer pageNums;

    /**
     * 每页条数
     *
     */
    private Integer pageSizes;

    /**
     * 设备编码集合
     */
    private List<String>  deviceCodeList;

    /**
     * 铁塔管理区域查询数据
     */
    private List<AreaCodesReq> areaCodeList;

    /**
     *  卫星区域集合
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  雷达区域集合
     */
    private List<SysDeptDeviceVO> radarList;

    /**
     * 组织ID
     */
    private List<String> orgIdList;

    /**
     * 排序字段
     *
     */
    private String sort;

    /**
     * 排序方式
     *
     */
    private String sortOrder;

    /**
     * 时间范围天数
     */
    private long dayNum;

    /**
     * 省市区乡镇编码集合
     * */
    private List<String> areaCodeLists;

    /**
     * 告警类型集合
     * */
    private List<String> warningTypeList;

    /**
     * 告警来源集合
     * */
    private List<String> warningSourceList;

    /**
     * 摄像机编码
     * */
    private String deviceCode;

    /**
     * 摄像机编码
     * */
    private List<String> selectDeviceCodeList;

    /**
     * 0:省；1:市；2:区；3:乡镇
     */
    private Integer areaBoxNum;

    /**
     * 区域排行纬度标识0:省；1:市；2:区；3:乡镇
     */
    private Integer areaSelect;

    /**
     * 区域准确率排行纬度标识
     */
    private Integer areaAccuracySelect;

    /**
     * 行政区域完结率纬度标识
     */
    private Integer areaCompletionSelect;

    /**
     * 研判结果多选
     * */
    private List<String> judgeResultList;

    /**
     * 网格id多选
     * */
    private List<String> gridIdList;

    private List<String> labelIds;

    /**
     * 昼时间段 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    private String dayQueryStartTime;

    /**
     * 昼时间段 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    private String dayQueryEndTime;

    /**
     * 夜时间段 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    private String nightQueryStartTime;

    /**
     * 夜时间段 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     */
    private String nightQueryEndTime;


    /**
     * 告警状态 6:已完结 1：待研判 2:待调度 4:待处置 5:待核实 9:待受理
     * @required false
     */
    private List<String> orderStatusList;

    /**
     *  设备编码集合
     *  @required false
     */
    private List<String> deviceCodeIndustryList;

    /**
     *  判断是否展示全部 0是 1否
     *  @required false
     */
    private Integer isTotal;

    /**
     * 摄像机标签集合
     */
    private List<String> deviceLabelIdList;

    private String labelName;

    /**
     * 统计维度：TYPE=告警类型（默认）；SUB_TASK=下游任务（方案 A）
     */
    private String statDimension;
}
