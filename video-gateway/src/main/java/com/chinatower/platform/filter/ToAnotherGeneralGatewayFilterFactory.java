
package com.chinatower.platform.filter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.NodeNotFoundException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
@Slf4j
public class ToAnotherGeneralGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    public static final String URL = "uri";

    public static final String GENERAL_INTERNET_URL = "generalInternetUrl";

    public static final String COLONY_CODE = "colonyCode";

    public static final String TO_NODE = "toNode";

    @Override
    public GatewayFilter apply(NameValueConfig config) {

        return (exchange, chain) -> {
            log.info("进入自定义过滤器:{}", config);
            log.info("uri:{}", exchange.getRequest().getURI());
            Route route = (Route) exchange.getAttributes().get(GATEWAY_ROUTE_ATTR);
            String toNode = exchange.getRequest().getQueryParams().getFirst(TO_NODE);
            Map<String, String> urlMap = this.getGeneralUrlByNode(toNode);
            String currentNodeCode = urlMap.get(COLONY_CODE);
            String uri = urlMap.get(URL);
            if (StringUtils.isEmpty(uri)) {
                throw new NodeNotFoundException("节点错误，请重新输入", 400);
            }
            log.info("透传uri:{}", uri);
            Route newRoute = new Route.AsyncBuilder().
                    id(route.getId()).uri(URI.create(uri)).
                    asyncPredicate(route.getPredicate()).
                    metadata(route.getMetadata()).
                    filters(route.getFilters()).
                    order(route.getOrder()).
                    build();
            exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRoute);

            SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
            if (ObjectUtil.isNotNull(sysConfig) && ObjectUtil.isNotNull(toNode)) {
                String currentFlag = currentNodeCode.equals(sysConfig.getConfigValue()) ? "1" : "0";
                exchange.getRequest().mutate().header("current-flag", currentFlag);
                exchange.getRequest().mutate().header("preToNode", toNode);
            }

            //后边   设置response-header会用到
            String generalUrlByNode = urlMap.get(GENERAL_INTERNET_URL);
            Map<String, String> variables = new HashMap<>();
            variables.put("generalAppUrl", generalUrlByNode);
            ServerWebExchangeUtils.putUriTemplateVariables(exchange, variables);
            return chain.filter(exchange);
        };
    }

    public Map<String, String> getGeneralUrlByNode(String node) {
        Map<String, String> map = new HashMap<>();
        //根据node直接取redis
        if (RedisUtil.hasKey(Constants.COLONY_RELATION_ALIAS_ALL + node, RedisDBType.PRIMARY)) {
            String str = RedisUtil.get(Constants.COLONY_RELATION_ALIAS_ALL + node, RedisDBType.PRIMARY);
            ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
            map.put(COLONY_CODE, cascadeConfigSyncVO.getColonyCode());
            for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                if ("general".equals(app.getAppId())) {
                    map.put(URL, app.getAppAddr());
                    String extra = app.getExtra();
                    JSONObject jsonObject = JSON.parseObject(extra);
                    if (ObjectUtil.isNotNull(jsonObject)) {
                        String generalInternetUrl = (String) jsonObject.get(GENERAL_INTERNET_URL);
                        if (StringUtils.isNotEmpty(generalInternetUrl)) {
                            map.put(GENERAL_INTERNET_URL, generalInternetUrl);
                            map.put(URL, generalInternetUrl);
                        }
                    }
                }
            }
        }
        return map;
    }
}
