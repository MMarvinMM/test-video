package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AreaCodeReq.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2022/11/29 14:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AreaCodeReq implements Serializable {

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     *  子节点区域编码集合
     */
    private List<String> childrenAreaCodeList;

    /**
     * 是否直辖市
     */
    private String isDirectly;
}
