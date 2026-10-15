package com.example.metricsdemo;

import com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


/**
 * @author 87957
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class ,SecurityAutoConfiguration.class, MybatisPlusAutoConfiguration.class , IdentifierGeneratorAutoConfiguration.class})
public class MetricsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsDemoApplication.class, args);
    }

}
