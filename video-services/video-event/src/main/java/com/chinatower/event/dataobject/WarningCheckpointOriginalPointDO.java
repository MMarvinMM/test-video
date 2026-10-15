package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 车辆卡口摄像机事件原始图片告警点表
 * </p>
 */
@Getter
@Setter
@TableName("vm_checkpoint_warning_original_point_t")
public class WarningCheckpointOriginalPointDO implements Serializable {

    private static final long serialVersionUID = 7892682046500185794L;

    /**
     * 告警点Id
     */
    private String alarmPointId;

    /**
     * 告警区域id
     */
    private String alarmAreaId;

    /**
     * X坐标百分比
     */
    private String percentX;

    /**
     * Y坐标百分比
     */
    private String percentY;


}
