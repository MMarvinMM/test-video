package com.chinatower.platform.framework.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;

import java.util.Arrays;


/**
 * @author WANGZIC
 */
@Configuration
public class FastJsonConverterConfig {

	@Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.PrettyFormat,
//                SerializerFeature.QuoteFieldNames, // 双引号
//                SerializerFeature.WriteMapNullValue, // 输入空值字段
//                SerializerFeature.WriteEnumUsingToString, // 枚举输出STRING
//                SerializerFeature.WriteNullBooleanAsFalse, // 布尔类型如果为null输出false
//                SerializerFeature.WriteNullListAsEmpty, // List字段如果为null输出为[]
//                //SerializerFeature.WriteNullNumberAsZero, // number类型如果为null输出0
//                //SerializerFeature.WriteNullStringAsEmpty, // 字符串类型如果为null输出""
//                SerializerFeature.SortField, // 按字段名称排序后进行输出
//                SerializerFeature.WriteDateUseDateFormat,// 设置日期格式
                SerializerFeature.DisableCircularReferenceDetect
        );
        //日期格式化
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setFastJsonConfig(fastJsonConfig);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON_UTF8,MediaType.APPLICATION_OCTET_STREAM));
        return new HttpMessageConverters(converter);
    }

}
