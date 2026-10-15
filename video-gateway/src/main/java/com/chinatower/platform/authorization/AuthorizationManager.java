package com.chinatower.platform.authorization;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.config.security.IgnoreUrlsConfig;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.nimbusds.jose.JWSObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @Description: 鉴权管理器，用于判断是否有资源的访问权限
 * @Author: 张帅
 * @Date: 2021/11/12 15:32
 **/
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Getter
    @Setter
    private LoginUser loginUser;


    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final String AUTHORIZED_VERIFICATION_CHECK_URL =  "authorized_verification_check_url";

    private static final String CHECK_URL_SWITCH =  "check_url_switch";

    private static final Pattern BRACE_PLACEHOLDER_PATTERN = Pattern.compile("\\{.*?\\}");

    private static final Map<String, Object> concurrentCheckUrlValue = new ConcurrentHashMap<>();

    /**
     * 实现url忽略和权限过滤
     *
     * @param authenticationMono
     * @param authorizationContext
     * @return
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authenticationMono, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();

        //请求资源
        String requestPath = exchange.getRequest().getURI().getPath();

        //权限过滤
        return authenticationMono.map(auth -> new AuthorizationDecision(checkAuthorities(exchange, auth, requestPath))).defaultIfEmpty(new AuthorizationDecision(false));

    }

    /**
     * 放行ignoreUrl
     *
     * @param requestPath 请求路径
     * @return
     */
    private boolean permitAll(String requestPath) {
        return ignoreUrlsConfig.getLevel1Urls().stream().anyMatch(r -> antPathMatcher
                .match(r, requestPath));
    }


    /**
     * 权限过滤
     *
     * @param exchange
     * @param auth
     * @param requestPath
     * @return
     */
    private boolean checkAuthorities(ServerWebExchange exchange, Authentication auth, String requestPath) {
        String token = "";
        if (auth instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken athentication = (JwtAuthenticationToken) auth;
            Jwt jwt = athentication.getToken();
            token = jwt.getTokenValue();
        } else {
            return false;
        }
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(token);
        } catch (ParseException e) {
            log.error("解析token异常{}", e);
            e.printStackTrace();
            return false;
        }
        String payLoad = jwsObject.getPayload().toString();
        JSONObject json = JSONObject.parseObject(payLoad);
        String cilent_id = json.getString("client_id");
        String user_id = json.getString("userId");
        LoginUser user = null;
        //String key= CommonConstants.WEB_LOGIN_TOKEN + cilent_id+":"+user_id;
        //user=RedisUtil.get(key, LoginUser.class, RedisDBType.PRIMARY);

        String cacheKey = CacheConstants.USER_INDUSTRY_CODE + cilent_id + ":" + user_id;
        String industryCode = exchange.getRequest().getHeaders().getFirst(FeignConstants.HEADER_INDUSTRY_CODE);
        log.info("AuthorizationManager ==> checkAuthorities: 1 from header:cache key:{}, industryCode:{}", cacheKey, industryCode);

        if (StringUtils.isBlank(industryCode)) {
            // 如果请求头中不存在，则取最后一次选择的行业code
            String industryCodeKey = CacheConstants.USER_LAST_INDUSTRY_CODE + cilent_id + ":" + user_id;
            industryCode = RedisUtil.get(industryCodeKey, RedisDBType.PRIMARY);
            log.info("AuthorizationManager ==> checkAuthorities: 2 from latest choose trade:cache key:{}, industryCode:{}", cacheKey, industryCode);
        }
        // 再次判断行业code是否存在
        if (StringUtils.isBlank(industryCode)) {
            Map<Object, Object> objectObjectMap = RedisUtil.hGetAll(cacheKey, RedisDBType.PRIMARY);
            //map无序，只能随便获取了
            industryCode = new ArrayList<>(objectObjectMap.keySet()).get(0).toString();
            log.info("AuthorizationManager ==> checkAuthorities: 3 from random:cache key:{}, industryCode:{}", cacheKey, industryCode);

        }
        String userCacheStr = RedisUtil.hGet(cacheKey, industryCode, RedisDBType.PRIMARY);
        log.debug("AuthorizationManager ==> checkAuthorities 5 cacheKey={},industryCode={},userCacheStr={}", cacheKey, industryCode, userCacheStr);
        if (StrUtil.isNotBlank(userCacheStr)) {
            user = JSON.parseObject(userCacheStr, LoginUser.class);
            setLoginUser(user);
        }

        if (user != null && matchUrl(user.getPermissions(), requestPath,cilent_id)) {
            return true;
        }
        return false;
    }



    public boolean matchUrl(Set<String> urls, String requestPath , String cilentId) {

        List<String> ignoreUrls = ignoreUrlsConfig.getLevel2Urls();
        if (requestPath.contains("/base/")) {
            return true;
        }

        //排除超级管理员
        if (urls.size() == 1 && urls.contains("/**")) {
            return true;
        }

        //判断是否开启强效验及配置强效验名单
        String checkUrlValue = (String) concurrentCheckUrlValue.get(AUTHORIZED_VERIFICATION_CHECK_URL);
        int checkAuthUrl = 0;
        if (ObjectUtil.isNotNull(concurrentCheckUrlValue.get(CHECK_URL_SWITCH))) {
            checkAuthUrl = (int) concurrentCheckUrlValue.get(CHECK_URL_SWITCH);
        }
        log.info("AuthorizationManager ==> checkAuthUrl:{}", checkAuthUrl);
        if (checkAuthUrl == 1 && StringUtils.isNotBlank(checkUrlValue) && "web_manage".equals(cilentId)) {
            List<String> checkUrls = Arrays.stream(checkUrlValue.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if (!checkUrls.isEmpty()) {
                // 拆分：无占位符URL + 带占位符URL
                Map<Boolean, List<String>> urlGroup = checkUrls.stream()
                        .collect(Collectors.partitioningBy(url -> url.contains("{") || url.contains("}")));
                List<String> noBracesUrls = urlGroup.get(false);
                List<String> withBracesUrls = urlGroup.get(true);
                //判断接口是否需要效验
                if(noBracesUrls.contains(requestPath)){
                    if(!urls.contains(requestPath)){
                        return false;
                    }
                }
                // 带占位符URL校验：正则匹配+授权校验
                if (!withBracesUrls.isEmpty()) {
                    // 批量转换：强效验URL → 正则表达式（如：/user/admin/{} → /user/admin/([^/]+)）
                    List<String> checkRegexUrls = withBracesUrls.stream()
                            .map(url -> BRACE_PLACEHOLDER_PATTERN.matcher(url).replaceAll("([^/]+)"))
                            .collect(Collectors.toList());

                    // 判断请求路径是否命中强效验URL
                    boolean isCheckUrlMatched = checkRegexUrls.stream()
                            .anyMatch(regexUrl -> requestPath.matches("^" + regexUrl + "$"));

                    if (isCheckUrlMatched) {
                        // 转换授权URL为正则，校验是否有权限
                        List<String> authRegexUrls = urls.stream()
                                .filter(url-> !"/**".equals(url))
                                .map(url -> BRACE_PLACEHOLDER_PATTERN.matcher(url).replaceAll("([^/]+)"))
                                .collect(Collectors.toList());

                        boolean isAuthMatched = authRegexUrls.stream()
                                .anyMatch(regexUrl -> requestPath.matches("^" + regexUrl + "$"));

                        if (!isAuthMatched) {
                            log.warn("强效验无权限：requestPath={}, 授权列表不匹配", requestPath);
                            return false;
                        }
                    }
                }
            }
        }

        if (urls.contains("/**")) {
            return true;
        }


        urls.addAll(ignoreUrls);
        return urls.stream().anyMatch(r -> antPathMatcher
                .match(r, requestPath));
    }

    @Scheduled(initialDelay = 1000, fixedRate  = 60000 * 5)
    public void loadCheckUrl() {
        //获取强效验名单
        String sysConfig = RedisUtil.get(AUTHORIZED_VERIFICATION_CHECK_URL, String.class, RedisDBType.PRIMARY);
        int checkAuthUrl = RedisUtil.get(CHECK_URL_SWITCH, int.class, RedisDBType.PRIMARY);
        if (ObjectUtil.isNotNull(sysConfig)) {
            concurrentCheckUrlValue.put( AUTHORIZED_VERIFICATION_CHECK_URL , sysConfig);
        }
        if(ObjectUtils.isNotNull(checkAuthUrl)){
            concurrentCheckUrlValue.put( CHECK_URL_SWITCH , checkAuthUrl);
        }else{
            concurrentCheckUrlValue.put( CHECK_URL_SWITCH , 0);
        }

    }
}
