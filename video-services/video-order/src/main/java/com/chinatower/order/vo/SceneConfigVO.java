package com.chinatower.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SceneConfigVO implements Serializable {

    private String sceneId;

    private String sceneName;

    private String typeValue;

    private String algorithmCode;

    private String creator;

    private Date createTime;

    private Integer deleteFlag;

    private String tenantId;

    private String industryCode;

    private String appVerCode;
}