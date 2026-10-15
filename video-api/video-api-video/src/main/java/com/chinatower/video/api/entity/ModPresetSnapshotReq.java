package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预置位
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("ModPresetSnapshotReqVideo")
public class ModPresetSnapshotReq extends DeviceSouthCommonDTO {
    @ApiModelProperty(value = "预置位id", required = true)
    @NotNull(message = "预置位id不能为空")
    private Long presetId;
    
    /**
     * 抓图周期 0:无、1:每天、2:每周、3:每月
     */
    @ApiModelProperty(value = "抓图周期", required = true)
    @NotNull(message = "抓图周期不能为空")
    private Integer snapshotCycle;
    
    /**
     * 哪天抓图
     */
    private String snapshotDay;

    /**
     * 抓图时间
     */
    private String snapshotTime;

    /**
     * 是否开启算法识别任务 0：否 1：是
     */
    private String isAlgorithmIdentify;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 是否开启双通道并行分析 0：否，1：是
     */
    private String doubleImgAnalysis;

}
