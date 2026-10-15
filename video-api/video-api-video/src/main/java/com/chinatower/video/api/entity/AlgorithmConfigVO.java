package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;

/**
 * @ClassName AlgorithmConfigVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@Alias("AlgorithmConfigVOVideo")
public class AlgorithmConfigVO {

    // 配置编号
    String configId;
    // 设备编号
    String deviceCode;
    // 渠道编号
    String channelCode;
    // 位置类型：1通道，2预置位
    Integer posType;
    // 预置位编号
    Long presetId;
    // 客户编号
    String tenantId;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Date createTime;

    // 设备名称
    String deviceName;
    // 渠道名称
    String channelName;
    // 预置位名称
    String presetName;
    // //删除标识 0:删除 1:正常
    // String deleteFlag;

    //预置位ptz参数
    AlgorithmParamPresetVO preset;

    // 算法区域列表
    List<AlgorithmAreaVO> algorithms;

}
