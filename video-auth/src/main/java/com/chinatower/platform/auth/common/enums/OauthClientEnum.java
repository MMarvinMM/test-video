package com.chinatower.platform.auth.common.enums;


import lombok.Getter;

@Getter
public enum OauthClientEnum {

    /**
     * 登录端区分
     */
    AAP_CLIENT("app","123456"),
    WEB_CLIENT("web","123456");

    private String clientId;
    private String clientSecret;


    OauthClientEnum(String clientId, String clientSecret){
        this.clientId = clientId ;
        this.clientSecret = clientSecret ;
    }
    public static String getSecretByClientId(String code){
        for(OauthClientEnum cEnum: OauthClientEnum.values()){
            if(code.equals(cEnum.getClientId())){
                return cEnum.getClientSecret();
            }
        }
        return  null;
    }
}
