package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @ClassName AlgorithmConfigDTO
 * @Description 算法配置 数据传输类
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class AlgorithmConfigDTO extends BaseDataScopeEntity {

    /** 省 */
    String provinceId;
    /** 市 */
    String cityId;
    /** 区 */
    String countryId;
    /** 乡镇 */
    String townCode;
    /**
     * 设备编号
     * @required true
     */
    String deviceCode;
    /**
     * 通道编号
     * @required true
     */
    String channelCode;

    /** 是否配置了算法 1是0否 */
    String hasAlgo;

    /** 算法编号 */
    String algorithmCode;
    /** 组织级别 */
    String orgLevel;
    /** 组织编码 */
    String orgIdCode;

    /**
     * 配置编号
     * 查询算法任务详情时，必填
     * 编辑算法任务时，必填
     * @required true
     */
    private String configId;

    /**预置位ptz参数 */
    AlgorithmParamPresetVO preset;

    /** 位置类型：1通道，2预置位 */
    Integer posType;

    /** 预置位编号 */
    Long presetId;

    /** 预置位名称 */
    String presetName;

    /** 算法区域列表 */
    List<AlgorithmAreaVO> algorithms;
    /** 设备名称 */
    String deviceName;
    /** 渠道名称 */
    String channelName;
    /** 标签编码 */
    String labelId;

    private Integer pageNum;
    private Integer  pageSize;

    /** 算法编码集合 */
    private List<String> algorithmCodeList;

    /** 1区域2组织3标签4自定义树 */
    String memoryType;
    /** 组织编码集合 */
    List<String> orgIdCodeList;
    /** 标签编码集合 */
    List<String> labelIdList;

    /** 1已订购算法数量;2已配置算法数量;3待配置算法数量 */
    String countType;

    /** 1后端算法;2前端算法 */
    String taskType;

    /** 算法任务状态：0:启用 1:停用 */
    String status;
    /** 算法状态 */
    String algorithmStatus;
    /**
     * 算法任务编码集合
     * 启停算法任务时，必填
     * 删除算法任务时，必填
     * @required true
     */
    private List<String> configIdList;
    /** 通道编码集合 */
    private List<String> channelCodeList;

    /**
     * 是否批量启停，1：单个算法启停，null批量。
     * 单个算法启停时，必填
     * @required true
     */
    String singleFlag;

    /* 标识 */
    private String platFlag;
    /* 自定义树唯一标识 */
    private String treeId;
    /* 节点编码 */
    private String nodeCode;

    private String orderBy;

    private String sort;

    /**
     * 批量查询算法任务，设备、通道入参集合
     */
    private List<DeviceChannelVO> deviceChannelList;

    /**
     * 是否新版本请求 1是0否
     */
    private String isNewVersion;

    /** 是否是数字人自动生成的预置位算法任务 1:是 */
    private String isAiSave;

    /**
     * 是否包含大模型算法 0:否（默认） 1：是
     */
    private int isHasBigModel;

    /**
     * 大模型算法列表
     */
    private List<String> bigModelAlgList;
}
