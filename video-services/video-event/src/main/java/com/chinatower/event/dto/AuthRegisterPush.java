package com.chinatower.event.dto;

import lombok.Data;

@Data
public class AuthRegisterPush {

    private String tenantId;

    private String callbackUrl;
}
