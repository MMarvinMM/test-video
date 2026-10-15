package com.chinatower.framework.cloud.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 87957
 */
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationNameProperties {

    private String name;

    @Override
    public String toString() {
        return "ApplicationNameProperties{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
