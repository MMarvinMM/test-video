package com.chinatower.analysis.api.entity;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class AnalysisVO extends PageDTO {
    private static final long serialVersionUID = 1L;
    private String creator;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;
    private String customerCode;

    /**
     * 0:省；1:市；2:区；3:乡镇
     * @required true
     */
    private Integer areaBoxNum;
    /**
     * 1当年；2当月；3前一天; 4当天
     */
    private String dimensionFlag;

    /**
     * 开始时间
     * @required true
     */
    private String queryStartTime;

    /**
     * 结束时间
     * @required true
     */
    private String queryEndTime;

    /**
     * 省份编码
     * @required false
     */
    private String provinceId;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 区县编码
     * @required false
     */
    private String townId;

    /**
     *  摄像机、雷达、物联设备编码集合
     *  @required true
     */
    private List<String> deviceCodeList;

    /**
     * 设备类型编码集合
     */
    private List<String> deviceTypeList;

    /**
     *  设备编码集合
     *  @required false
     */
    private List<String> deviceCodeIndustryList;

    private Integer pageNums;
    private Integer pageSizes;

    /**
     * 铁塔管理区域查询数据
     * @required false
     */
    private List<AreaCodesReq> areaCodeList;

    /**
     *  卫星区域集合
     *  @required false
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  雷达区域集合
     *  @required false
     */
    private List<SysDeptDeviceVO> radarList;

    /**
     * 组织ID
     * @required false
     */
    private List<String> orgIdList;

    /**
     * 省市区编码集合
     * @required true
     */
    private List<String> areaCodeLists;

    /**
     * 排序字段
     * @required false
     */
    private String sort;

    /**
     * 排序方式
     * @required false
     */
    private String sortOrder;

    /**
     * 摄像机编码
     * @required false
     */
    private String deviceCode;

    /**
     * 告警类型集合（场景/小模型类型；与 subSceneIdList 为 AND）
     * @required false
     */
    private List<String> warningTypeList;

    /**
     * 下游任务编码集合（实际触发 sub_scenes；与 warningTypeList 为 AND，列表内 OR）
     * @required false
     */
    private List<String> subSceneIdList;

    /**
     * 告警来源集合
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;

    /**
     * 告警类型
     * @required false
     */
    private String warningTypeId;

    /**
     * 告警状态 6:已完结 1：待研判 2:待调度 4:待处置 5:待核实 9:待受理
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * Pc 区域完结率 IN条件生成
     * @required false
     */
    private String areaList;

    /**
     *组织id
     * @required false
     */
    private String deptId;

    /**
     * 区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaSelect;

    /**
     * 区域准确率排行纬度标识
     * @required false
     */
    private Integer areaAccuracySelect;

    /**
     * 行政区域完结率纬度标识
     * @required false
     */
    private Integer areaCompletionSelect;

    /**
     * AI,一键,物联,摄像机告警数据集
     * @required false
     */
    private List<Map> completeList1;

    /**
     * 添加人工告警数据集数据集
     * @required false
     */
    private List<Map> completeList2;

    /**
     * 添加卫星数据集
     * @required false
     */
    private List<Map> completeList3;

    /**
     * 添加雷达数据集
     * @required false
     */
    private List<Map> completeList4;

    /**
     * 摄像机区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaDeviceSelect;

    /**
     * 物联设备区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaIotSelect;

    /**
     * 无人机区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaUavSelect;

    /**
     * 雷达区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaRadarSelect;

    /**
     * 喇叭区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaHornSelect;

    /**
     * 执法记录仪区域排行纬度标识0:省；1:市；2:区；3:乡镇
     * @required false
     */
    private Integer areaRecorderSelect;

    /**
     * 研判结果多选 1真警  2误报  3重复告警
     * */
    private List<String> judgeResultList;

    /**
     * 网格id多选
     * */
    private List<String> gridIdList;

    /**
     * 设备是否展示为0   1是
     * */
    private String isZero;

    /**
     * @required true
     * 时间类型   1:日期(天) 2.小时(24时)
     * */
    private String dateType;

    /**
     * 设备类型
     * */
    private String deviceType;

    /**
     * 大屏全景展示查询标识
     */
    private String screenCodeFlag;

    /**
     * 大屏全景展示标签Id
     */
    private String labelId;

    private List<String> labelIdList;

    /**
     * 订购标识 1：已订购  0 未订购
     */
    private String ifOrderFlag;

    /**
     * 统计维度：TYPE=告警类型（默认，利旧）；SUB_TASK=下游任务
     */
    private String statDimension;
}
