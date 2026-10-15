package com.chinatower.emergency.service;

import com.alibaba.fastjson.JSONObject;
import reactor.core.publisher.Flux;

public interface ITencentOperatorService {
    JSONObject reply2Tencent(String content, int type) throws Exception;
}
