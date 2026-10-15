package com.chinatower.event.dataobject;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 告警事件原始图片告警点表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@Getter
@Setter
@TableName("vm_warning_original_point_t")
public class WarningOriginalPointDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
