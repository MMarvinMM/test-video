package com.chinatower.common.core.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ProcessDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/22 18:00
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class ProcessDTO implements Serializable {

    /**
     * 处理人ID
     * @required true
     */
    private String processId;
    /**
     * 处理人名称
     * @required true
     */
    private String processName;
    /**
     * 处理人用户名
     * @required false
     */
    private String processNickName;
    /**
     * 处理人组织ID
     * @required false
     */
    private String groupId;
}
