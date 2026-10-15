package com.chinatower.platform.auth.model;


import com.google.common.collect.Sets;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 认证中心 客户端对象实体
 *
 * @author bruce
 * @version v1.0
 * @date 2021/11/19
 */
public class OauthClientInfo implements ClientDetails {

    @Setter
    private String clientId;

    @Setter
    private String clientSecret;

    @Setter
    private Set<String> authorizedGrantTypes;

    @Setter
    private Set<String> ResourceIds;

    @Setter
    private boolean SecretRequired = true;

    @Setter
    private Integer clientTokenValidity;

    @Setter
    private Integer clientRefreshValidity;

    public OauthClientInfo(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        // 默认具备所有授权类型
        this.authorizedGrantTypes = Sets.newHashSet("authorization_code","refresh_token","password");
    }



    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return ResourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return SecretRequired;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        // 默认不启用
        return false;
    }

    @Override
    public Set<String> getScope() {
        // 默认范围：all
        return Sets.newHashSet("all");
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return this.authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return clientTokenValidity;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return clientRefreshValidity;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        //默认自动授权
        return true;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
