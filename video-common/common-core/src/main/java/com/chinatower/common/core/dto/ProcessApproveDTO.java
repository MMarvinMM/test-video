package com.chinatower.common.core.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ProcessApproveDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/15 17:40
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class ProcessApproveDTO extends ProcessApproveDO implements Serializable {

    private String creator;

    private String approveStatusName;

    private String approveTime;

    private String approvePersonName;

    private String mobilePhone;

    /**
     * 是否会签0:不会签  1:会签
     */
    private String isSign;
}
