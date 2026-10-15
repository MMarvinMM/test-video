package com.chinatower.product.api.modules.sdktoapi;

import com.chinatower.product.api.modules.sdktoapi.entity.SdkToApiEntityVo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: hued
 * @Date: 2024年4月8日10:15:55
 */
public interface SdkToApiRepository extends ElasticsearchRepository<SdkToApiEntityVo,String> {
}
