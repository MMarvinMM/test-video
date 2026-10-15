package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: VideoControlReq
 * @Description: 视频监控查询实体
 * @Author: zhujun
 * @Date: 2022/1/11
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoControlReq extends VideoBaseReq{

    /**
     * 有效时长单位秒
     */
    private int duration = 3;

    /**
     * 租户编码
     */
    private String customerCode;

    /**
     * 云台控制令牌
     */
    private String lockToken;

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
     * ⽔平参数，范围[0, 3600]
     */
    private float pan;

    /**
     * 垂直参数，范围[-1800, 1800]
     */
    private float tilt;

    /**
     * 变倍参数，范围[0, 254]
     */
    private float zoom;

    /**
     * 预置点Id；
     */
    private int presetId;

    /**
     * 倍速回放流控制标识
     * 倍速回放流控制标识,在获取录像回放地址时返回
     */
    private String streamControlId;

    /**
     * 控制类型
     * 1：暂停，2：播放，3:倍速
     */
    private int controlType;

    /**
     * 播放速度
     * 当controlType为3时，必传，取之范围为[-2,2]整数范围，
     * -2代表1/4倍速播放
     * -1代表1/2倍速播放
     * 0代表正常倍速播放
     * 1代表2倍速播放
     * 2代表4倍速播放
     */
    private int speed;

    /**
     * 巡航路径id
     * */
    private int patrolId;
    /**
     * 巡航控制类型
     *  1-停用；2-启用；
     * */
    private int patrolType;
    /**
     * 锁定控制类型
     *  1-请求控制锁定；0-释放控制；
     * */
    private int lockType = 1;
}
