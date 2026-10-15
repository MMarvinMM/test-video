package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetWeChatSignatureReq implements Serializable {

    private static final long serialVersionUID = 1L;
    private String appId;
    private String appSecret;
    private String url;
}
