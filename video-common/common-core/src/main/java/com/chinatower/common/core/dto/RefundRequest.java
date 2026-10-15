package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jwk
 * @version 1.0
 * @date 2021/6/10 14:13
 * @description
 **/
@Data
public class RefundRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String custId;

    private String orderCode;

    private String operatorName;

    private long payAmount;

}
