package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class VmCommonWordsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String wordsId;
    private String userId;
    private String typeId;
    private String content;
    private String createDate;
    private String tenantId;
    private String appVerCode;

    private String initWordsFlag;//初始化常用语标识
    private String updateTime;//修改时间
    private String industryCode;//行业编码

}
