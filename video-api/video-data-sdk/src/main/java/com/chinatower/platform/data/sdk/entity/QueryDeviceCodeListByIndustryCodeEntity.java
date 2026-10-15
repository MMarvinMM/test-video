package com.chinatower.platform.data.sdk.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
@SuppressWarnings("all")
public class QueryDeviceCodeListByIndustryCodeEntity extends AbstactEntity implements Serializable {
    private String tenantId;

    public QueryDeviceCodeListByIndustryCodeEntity() {
        this.setBindMapping(URL.QUERY_DEVICE_CODE_LIST_BY_INDUSTRY_CODE);
        this.setRequestBody(JSON.toJSONString(new HashMap<String, Object>() {
            {
                this.put("tenantId", tenantId);
            }
        }));
    }

}
