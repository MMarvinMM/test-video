package com.chinatower.video.modules.videopreset.dataobject;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoPresetSnapshotVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;

    private int id;

    private List<Integer> idList;

    /**
     * 预置点id
     */
    private Long presetId;

    /**
     * 预置点名称
     */
    private String presetName;

    /**
     * 预置点编码
     */
    private String presetCode;

    private String algorithmName;

    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    private String creator;

    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date modifyTime;

    /**
     * 抓图周期 0:无、1:每天、2:每周、3:每月
     */
    private Integer snapshotCycle;

    private String snapshotCycleName;

    /**
     * 哪天抓图
     */
    private String snapshotDay;

    /**
     * 抓图时间
     */
    private String snapshotTime;

    /**
     * 页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 算法编码列表
     */
    private List<String> algorithmCodeList;

    private String orderBy;

    private String sort;

    private String algorithmCode;

    private String isAlgorithmIdentify;

    /**
     * 是否开启双通道并行分析 0：否，1：是
     */
    private String doubleImgAnalysis;

    /**
     * 算法基本信息扩展参数
     */
    private String extendJson;

    /**
     * 是否支持双成像并析 1-支持 0-不支持，默认不支持
     */
    private String supportDoubleImgAnalysis;

    /**
     * 是否可见光通道 true:可见光 false:非可见光
     */
    private boolean ifVisibleLight;
}
