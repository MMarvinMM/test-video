package com.chinatower.platform.auth.service;

import lombok.SneakyThrows;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * see JdbcClientDetailsService
 */

public class MonitorClientDetailsService extends JdbcClientDetailsService {

    public MonitorClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 重写原生方法支持redis缓存
     *
     * @param clientId
     * @return
     */
    @Override
    @SneakyThrows
//    @Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientId" , unless = "#result == null")
//    @Cacheable(value = "oauth:client:details", key = "#clientId" , unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId) {

        ClientDetails clientDetails= super.loadClientByClientId(clientId);


        System.out.println(  clientDetails.getClientId());
        System.out.println(  clientDetails.getClientSecret());
        System.out.println(  clientDetails.getAuthorizedGrantTypes());
        System.out.println(  clientDetails.getScope());

        return clientDetails;
    }

}
