package com.chinatower.common.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ExtraInfoVO
 * @Deacription 算法透传字段
 * @Author chenmf
 * @Date 2022/3/4 14:10
 * @Version 1.0
 **/
@Data
public class ExtraInfoVO implements Serializable {

    //请求唯一标识
    private String msgId;

    //通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
    private int noticeResource;

    //前端告警图片
    private String frontAlarmPicUrl;

    //前端告警视频
    private String frontAlarmVedioUrl;

    //预置位ID
    private String presetId;
}
