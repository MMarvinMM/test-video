package com.chinatower.platform.data.sdk.entity;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@SuppressWarnings("all")
public class ScriptContextEntity extends AbstactEntity implements Serializable {

    /**
     * query 脚本/表达式
     */
    private String scriptEL;
    /**
     * 脚本执行占位值（对应占位符）
     */
    private List<Serializable> paddingValueList;
    /**
     * 附加数据透传
     */
    private Map<String, Object> context;

    public ScriptContextEntity(String scriptEL, List<Serializable> paddingValueList, Map<String, Object> context) {
        this.setBindMapping(URL.MONGODB_EXECUTE_SCRIPT);
        this.setRequestBody(JSON.toJSONString(new HashMap<String, Object>() {
            {
                this.put("scriptEL", scriptEL);
                this.put("paddingValueList", paddingValueList);
                this.put("context", context);
            }
        }));
        this.scriptEL = scriptEL;
        this.paddingValueList = paddingValueList;
        this.context = context;
    }
}
