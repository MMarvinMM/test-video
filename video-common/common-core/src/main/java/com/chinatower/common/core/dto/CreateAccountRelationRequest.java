package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zgf
 * @version 1.0
 * @date 2021/9/15 16:37
 * @description
 * 创建账户
 **/
@Data
public class CreateAccountRelationRequest implements Serializable {

    private String custId;  //客户编码

    private String orderCode;   //订单code

}
