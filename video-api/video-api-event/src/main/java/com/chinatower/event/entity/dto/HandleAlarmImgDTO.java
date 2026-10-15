package com.chinatower.event.entity.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class HandleAlarmImgDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     * */
    private String tableName;


    /**
     * 告警id
     */
    private String warningOrderId;

    /**
     * 业务生成的告警数据，用户保存告警表
     */
    private boolean listRemark;


    /**
     * 告警压制数据
     */
    private boolean interceptsRemark;

    /**
     * 运营研判数据
     */
    private boolean operationsRemark;

    /**
     * 原图
     * @required false
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     * @required false
     */
    private String fileImgUrlIcon;

    /** 管线信息json格式 */
    private String  pipelines;

}
