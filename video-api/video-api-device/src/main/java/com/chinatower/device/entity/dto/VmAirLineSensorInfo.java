package com.chinatower.device.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author cuibaoluo
 * @date 2025/12/16
 * @description：
 */
@Data
public class VmAirLineSensorInfo {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 点位索引
     */
    private Integer pointIndex;
    /**
     * 动作索引
     */
    private Integer actIndex;

    /**
     * 无人机设备编码
     */
    private String deviceCode;

    /**
     * 快照表id
     */
    private Long snapshotId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 航点编号
     */
    private String airPointId;

    /**
     * 任务类型 1文件广播（传url），2文本广播（传文本），3选择广播
     */
    private String hornType;

    /**
     * 类型 0 音频，1 文本
     */
    private String audioStatus;

    /**
     * 播放类型 1按次数播放，2循环播放
     */
    private String hornNumType;

    /**
     * 播放次数
     */
    private Integer hornNum;

    /**
     * 播放地址
     */
    private String hornTypeUrl;

    /**
     * 播放文本
     */
    private String contentValue;

    /**
     * 全局应用 1是 0否
     */
    private String isHornGlobal;

    /**
     * 指令 1开启 0关闭
     */
    private String hornStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private String deleteFlag;

    /**
     * 探照灯亮度
     */
    private Integer brightness;

    /**
     * 1 喇叭 2探照灯
     */
    private String sensorType;

    /**
     * 喇叭文件md5
     */
    private String md5;

}
