package com.chinatower.framework.cloud.autoconfigure;


import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 87957
 */
@Configuration
@EnableConfigurationProperties(ApplicationNameProperties.class)
public class MeterAutoconfigure {


    @Bean
    @ConditionalOnMissingBean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            ApplicationNameProperties applicationNameProperties) {

//        System.out.println(applicationNameProperties.getName());
        return (registry) -> registry.config().commonTags("application", applicationNameProperties.getName());
    }


}
