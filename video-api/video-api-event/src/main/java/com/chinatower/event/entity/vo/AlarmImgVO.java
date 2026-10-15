package com.chinatower.event.entity.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警事件图片信息表
 * <p>
 * 类名称: AlarmImgVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 16:11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@ToString
public class AlarmImgVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileCode;

    private String fileName;

    private String fileUrl;

    //文件类型：1框图；2原图
    private String fileType;

    private String tagPictureUrl;

    private String warningOrderId;

    private String originalPictureUrl;

    private List<AlarmOriginalAreaVO> alarmArea;
}
