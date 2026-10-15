package com.chinatower.platform.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.http.server.reactive.AbstractServerHttpRequest;

@Service("tencentStream")
@Slf4j
public class TencentStreamModifyFun implements RewriteFunction<String, String> {

    private static final String TIME_STAMP_KEY = "t";

    private static final String JSON_BODY_KEY_CONTENT = "content";
    private static final String JSON_BODY_KEY_BOT_APP = "bot_app_key";
    private static final String JSON_BODY_KEY_BIZ_ID = "visitor_biz_id";
    private static final String JSON_BODY_KEY_SESSION_ID = "session_id";

    @Override
    public Publisher<String> apply(ServerWebExchange serverWebExchange, String content) {

        String timeStamp = serverWebExchange.getRequest().getQueryParams().get(TIME_STAMP_KEY).toString();

        AbstractServerHttpRequest originalRequest = ((AbstractServerHttpRequest)serverWebExchange.getRequest()).getNativeRequest();

        String path = originalRequest.getPath().elements().get(originalRequest.getPath().elements().size() - 1).value().trim();

        JSONObject result = new JSONObject();

        result.put(JSON_BODY_KEY_CONTENT,content);

        log.info(path + "请求发起,问题:" + content);

        if(path.endsWith("streamHr")){
            result.put(JSON_BODY_KEY_BOT_APP,"PxxlUPTl");
            result.put(JSON_BODY_KEY_BIZ_ID,"平台创新中心综合助手");
        }else if(path.endsWith("streamSl")){
            result.put(JSON_BODY_KEY_BOT_APP,"iovhhgLC");
            result.put(JSON_BODY_KEY_BIZ_ID,"视联业务");
        }else{
            throw new RuntimeException("不支持的path" + path);
        }

        result.put(JSON_BODY_KEY_SESSION_ID,timeStamp);

        return Mono.just(result.toJSONString());
    }
}
