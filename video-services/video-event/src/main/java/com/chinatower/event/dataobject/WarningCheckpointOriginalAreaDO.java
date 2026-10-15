package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 车辆卡口摄像机告警事件原始图片区域表
 * </p>
 */
@Getter
@Setter
@TableName("vm_checkpoint_warning_original_area_t")
public class WarningCheckpointOriginalAreaDO implements Serializable {

    private static final long serialVersionUID = -1039442528608224302L;

    /**
     * 告警区域id
     */
    private String alarmAreaId;

    /**
     * 原始图片id
     */
    private String originalId;

    /**
     * 报警信息准确率/可信程度
     */
    private String confidenceLevel;

    /**
     * 告警工单id
     */
    private String warningOrderId;


}
