package com.chinatower.event.entity.vo;

import java.io.Serializable;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.ToString;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警事件原始图片告警点表
 * <p>
 * 类名称: AlarmOriginalPointVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 16:06
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@ToString
public class AlarmOriginalPointVO extends BaseMongoDTO implements Serializable  {
    private static final long serialVersionUID = 1L;

    private String alarmPointId;

    private String alarmAreaId;

    private String percentX;

    private String percentY;

    /**
     * 框图唯一标识
     */
    private String alarmPictureId;

    /**
     * 告警工单id
     */
    private String warningOrderId;

    /**
     * 框图置信度
     */
    private String confidenceLevel;
}

