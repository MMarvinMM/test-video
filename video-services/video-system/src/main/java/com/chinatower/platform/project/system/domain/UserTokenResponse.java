package com.chinatower.platform.project.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserTokenResponse extends LanXinBaseResponse<UserTokenResponse.UserTokenData> {
    @Data
    public static class UserTokenData {
        private String userToken;
        private Integer expiresIn;
        private String scope;
        private String state;
        private String staffId;
    }
}