package com.chinatower.receive.vo.entity;

import lombok.Data;

@Data
public class AuthRegisterPush {

    private String tenantId;

    private String callbackUrl;
}
