
package com.chinatower.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
public class AgentController {

    @GetMapping(value = "/v1/agent/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JSONObject getNodes() {
        JSONObject configJson = new JSONObject();
//       创建新的json对象  configJson  添加键值对 {数据中心 ,defalut}
        configJson.put("Datacenter", "default");
        JSONObject json = new JSONObject();
//        创建新的json对象  json  添加键值对{配置, {数据中心,defalut}}
        json.put("Config", configJson);
        return json;
    }
}
