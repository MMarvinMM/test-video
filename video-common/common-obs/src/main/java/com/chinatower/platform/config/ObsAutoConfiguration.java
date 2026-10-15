package com.chinatower.platform.config;

import com.chinatower.platform.props.ObsProperties;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date 2021-12-05 15:40
 * @requestID 需求单号及需求名称
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ObsProperties.class)
@ConditionalOnProperty(value = ObsProperties.PREFIX + ".enable", havingValue = "true", matchIfMissing = false)
public class ObsAutoConfiguration {

    private final ObsProperties obsProperties;

    @Bean
    public ObsClient obsClient() {
        ObsConfiguration obsConfiguration = new ObsConfiguration();
        obsConfiguration.setSocketTimeout(obsProperties.getSocketTimeOut());
        obsConfiguration.setConnectionTimeout(obsProperties.getConnectTimeOut());
        obsConfiguration.setEndPoint(obsProperties.getEndPoint());
        ObsClient obsClient = new ObsClient(obsProperties.getAk(), obsProperties.getSk(), obsConfiguration);
        return obsClient;
    }
}
