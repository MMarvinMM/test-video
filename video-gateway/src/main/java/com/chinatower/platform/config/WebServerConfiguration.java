package com.chinatower.platform.config;

import feign.Logger;
import feign.codec.Decoder;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootConfiguration
public class WebServerConfiguration {

//    @Bean
//    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
//        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
//        //tomcatFactory.setPort(8082);
//        tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
//        return tomcatFactory;
//    }

    @Bean
    public ConfigurableTomcatWebServerFactory createEmbeddedServletContainerFactory() {
        ConfigurableTomcatWebServerFactory tomcatFactory = new TomcatServletWebServerFactory();
        tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
        return tomcatFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

}

class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

    public void customize(Connector connector) {
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        //设置最大连接数
//        protocol.setMaxConnections(2000);
//        //设置最大线程数
//        protocol.setMaxThreads(2000);
        protocol.setConnectionTimeout(20000);
    }
}