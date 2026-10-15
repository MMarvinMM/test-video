package com.chinatower.product.api.modules.sdktoapi.config;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描video-api下所有携带@FeignClient注解的方sdk法
 */
@Component
@Slf4j
public class ScanAllAnnotation {
    private final String[] BASE_PACKAGE_ARRAY = new String[]{
            "com.chinatower.**.api",
            "com.chinatower.file.feign.client",
            "com.chinatower.platform.fegin.client"
    };

    private final String[] FEIGN_CLASS = new String[]{
            "com.chinatower.device.api.IAlgorithmInfoService",
            "com.chinatower.device.api.IHornDeviceService",
            "com.chinatower.device.api.IPolygonPatchConfigService",
            "com.chinatower.device.api.IReorderDeviceService",
            "com.chinatower.device.api.ISensorDeviceService",
            "com.chinatower.device.api.IUavDeviceService",
            "com.chinatower.device.api.IVideoDeviceMgmtService",
            "com.chinatower.device.api.IVideoDeviceService",
            "com.chinatower.industry.api.IIndustryDeviceService",
            "com.chinatower.industry.api.IIotDeviceService",
            "com.chinatower.video.api.VideoPlayerAPI",
            "com.chinatower.video.api.VideoQueryAPI"
    };


    private final String RESOURCE_PATTERN = "/**/*.class";
    public static final Map<String, Method> HANDLER_MAP = new HashMap<>();

    @PostConstruct
    public void getAllClass() {
        //spring工具类，可以获取指定路径下的全部类
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            for (String BASE_PACKAGE : BASE_PACKAGE_ARRAY) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
                org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources(pattern);
                //MetadataReader 的工厂类
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (org.springframework.core.io.Resource resource : resources) {
                    //用于读取类信息
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    //扫描到的class
                    String classname = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(classname);
                    //判断是否有指定主解
                    FeignClient anno = clazz.getAnnotation(FeignClient.class);
                    if (anno != null || Arrays.asList(FEIGN_CLASS).contains(classname)) {
                        // {POST /api/reflect}
                        //将注解中的类型值作为key，对应的类作为 value
                        Method[] methods = clazz.getMethods();
                        Arrays.stream(methods).forEach(method -> {
                            HANDLER_MAP.put(clazz.getName() + "#" + method.getName(), method);
//                            Class<?> returnType = method.getReturnType();
//                            if(!returnType.toString().equals("class com.chinatower.common.entity.Result")){
//                                log.info("方法名{}，返回类型{}" ,clazz.getName() + "#" + method.getName(), returnType.toString());
////                                System.out.println(returnType.toString());
//                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("getAllClass异常=",e);
        }

        log.info("=================== start =====================");
        log.info(JSON.toJSONString(HANDLER_MAP.keySet()));
//        log.info(JSON.toJSONString(HANDLER_MAP.entrySet()));
        log.info("=================== end =====================");
    }
}
