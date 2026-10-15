package com.chinatower.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 鉴权切面相关bean扫描
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/3
 * @version:
 */
@Configuration
@ConditionalOnProperty(prefix = "tower.authAspect", name = "enable", havingValue = "true")
@ComponentScan(basePackages = {
    "com.chinatower.platform"
})
@MapperScan("com.chinatower.platform.mapper")
public class AuthBaseConfiguration {
}
