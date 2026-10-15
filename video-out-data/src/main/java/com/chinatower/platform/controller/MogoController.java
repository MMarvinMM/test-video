package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.annotation.Trace;
import com.chinatower.platform.data.sdk.entity.BasicResponse;
import com.chinatower.platform.data.sdk.entity.ScriptContextEntity;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * video-out-data
 */
@Slf4j
@RestController
public class MogoController {
    @Resource(name = "primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;

    /**
     * 执行mogo脚本
     * @param ql
     * @return
     */
    @Trace
    @PostMapping("/mogo/executeScript")
    public BasicResponse<Map<String, Object>> executeScript(@RequestBody ScriptContextEntity ql) {
        Document document = null;
        try {
            JSONObject object = JSON.parseObject(ql.getRequestBody());
            String scriptEL = object.getString("scriptEL");
            document = primaryMongoTemplate.executeCommand(scriptEL);
        } catch (Exception e) {
            log.error("执行脚本失败",e);
            return BasicResponse.createWithError("101", "mogo 执行脚本失败");
        }
        return buildResponse(document);
    }

    private static BasicResponse<Map<String, Object>> buildResponse(Document document) {
        BasicResponse<Map<String, Object>> response = new BasicResponse<>();
        Map<String, Object> map = new HashMap<>();
        Set<String> keySet = document.keySet();
        for (String key : keySet) {
            map.put(key, document.get(key));
        }
        response.setResponseBody(map);
        return response;
    }
}
