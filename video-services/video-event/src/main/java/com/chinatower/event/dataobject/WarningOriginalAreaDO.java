package com.chinatower.event.dataobject;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 告警事件原始图片区域表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@Getter
@Setter
@TableName("vm_warning_original_area_t")
public class WarningOriginalAreaDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
