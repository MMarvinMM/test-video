package com.chinatower.event.entity.vo;

import java.io.Serializable;
import java.util.List;

import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.ToString;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警事件原始图片区域表
 * <p>
 * 类名称: AlarmOriginalAreaVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 15:57
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */

@Data
@ToString
public class AlarmOriginalAreaVO extends BaseMongoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originalId;

    private String alarmAreaId;

    private String confidenceLevel;

    private String warningOrderId;

    private String alarmPictureUrl;

    private String className;

    /** 告警点列表 */
    private List<AlarmOriginalPointVO> alarmPointList;
}
