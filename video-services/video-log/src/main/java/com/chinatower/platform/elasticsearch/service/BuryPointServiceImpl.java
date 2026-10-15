package com.chinatower.platform.elasticsearch.service;

import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.platform.elasticsearch.repository.BuryPointRepository;
import com.chinatower.platform.entity.BuryPointEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class BuryPointServiceImpl implements BuryPointService{

    @Value("${url.blacklist}")
    private Set<String> blacklist;

    @Value("${url.whitelist}")
    private Set<String> whitelist;

    @Autowired
    private BuryPointRepository buryPointRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public BuryPointEntity save(BuryPointDTO buryPointDTO) {
        // 黑名单过滤
        log.info("url.blacklist:{}", blacklist);
        if (blacklist.contains(buryPointDTO.getOperUrl())) {
            return null;
        }
        for(String black : blacklist) {
            if(black.contains("*")){
                Pattern r = Pattern.compile(black);
                Matcher m = r.matcher(buryPointDTO.getOperUrl());
                if (m.find()) {
                    return null;
                }
            }
        }
        BuryPointEntity buryPointEntity = new BuryPointEntity();
        BeanUtils.copyProperties(buryPointDTO, buryPointEntity);
        // 根据 operTime 生成动态索引名
        String indexName = generateIndexName(buryPointEntity.getOperTime());

        // 执行插入操作
        try {

            Map<String, Object> map = asMap(buryPointEntity);
            map.put("operTime", buryPointEntity.getOperTime().getTime());
            // 使用 ElasticsearchRestTemplate 插入数据
            IndexRequest indexRequest = new IndexRequest(indexName)
                    .source(map);  // 转换为 Map（不设置 ID）

            // 使用 RestHighLevelClient 插入数据
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);  // 执行原生插入操作

            // 获取 Elasticsearch 自动生成的 ID
            String generatedId = response.getId();

            // 将生成的 ID 设置到 buryPointEntity
            buryPointEntity.setId(generatedId);
        } catch (Exception e) {
            log.error("Error while inserting data into Elasticsearch", e);
        }

        return buryPointEntity;
    }

    @Override
    public Page<BuryPointEntity> pageable() {

        Pageable pageable= PageRequest.of(0,4);
        Page<BuryPointEntity> pages = buryPointRepository.findAll(pageable);
        return pages;
    }

    private String generateIndexName(Date operTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String monthStr = sdf.format(operTime);
        return "bury_point_" + monthStr;
    }

    // 通用的 asMap 方法，可以根据字段动态生成 Map
    public Map<String, Object> asMap(Object object) {
        // 创建一个 Map 来存储字段名及其值
        Map<String, Object> fieldMap = new HashMap<>();

        // 获取对象的所有字段
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);  // 确保能访问私有字段
            try {
                // 获取字段值并将字段名和值存入 Map
                fieldMap.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return fieldMap;
    }
}
