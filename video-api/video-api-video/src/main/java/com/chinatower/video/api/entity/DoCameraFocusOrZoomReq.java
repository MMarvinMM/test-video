package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoCameraFocusOrZoomReq extends VideoBaseControlReq{
    /**
     * 云台操作方向
     * PTZ_TILT_UP	1	云台上仰
     * PPTZ_TILT_DOWN	2	云台下俯
     * PPTZ_PAN_LEFT	3	云台左转
     * PPTZ_PAN_RIGHT	4	云台右转
     * PPTZ_UP_LEFT	5	云台上仰和左转
     * PPTZ_UP_RIGHT	6	云台上仰和右转
     * PPTZ_DOWN_LEFT	7	云台下俯和左转
     * PPTZ_DOWN_RIGHT,	8	云台下俯和右转
     * PPTZ_FOCUS_IN	9	焦距变大
     * PPTZ_FOCUS_OUT	10	焦距变小
     * PPTZ_ZOOM_IN	11	倍率变大
     * PPTZ_ZOOM_OUT	12	倍率变小
     */
    private int operType;

    /**
     * 转动开始/结束标识
     * 1:表示动作开始;0:表示动作结束
     */
    private int operContent;

    /**
     * 步长(控制云台转动速度）,与具体厂家设备有关；默认值为1；
     */
    private int step;

    /**
     * 操作批次编号,一次开始+停止为同一批次操作编号
     */
    private String operBatchId;

    /**
     * 指令下发时间戳，13位时间戳
     */
    private String operTimestamp;
}
