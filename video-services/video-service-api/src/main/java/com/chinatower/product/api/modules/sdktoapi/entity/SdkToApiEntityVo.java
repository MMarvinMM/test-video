package com.chinatower.product.api.modules.sdktoapi.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @Author: hued
 * @Date: 2024年4月8日10:15:55
 */
@Data
@Document(indexName = "vm_sdk_api_his")
public class SdkToApiEntityVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String userId;
    private String tenantId;
    private String pathUrl;
    private String requestTime;
}
