package com.chinatower.system.api.feign.client;


import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.AuthConfig;
import com.chinatower.system.api.feign.factory.WxFallbackFactory;
import com.chinatower.system.api.model.WxLogoutVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = ServiceNameConstants.VIDEO_AUTH_SERVICE, fallbackFactory = WxFallbackFactory.class)
public interface WxFeignClient {

    /**
     * 将用户失效，并发送重新登录的微信提示
     * @apiNote 将用户失效，并发送重新登录的微信提示
     * @apiVersion 6.81
     * @tag 六、基础功能能力
     * @RequestDemo
     * {
     *   "userId": "userId_c957b6da5f10",
     *   "userName": "userName_113ef127f85c"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "data": null,
     *     "msg":"操作成功",
     *     "time":1654754979434
     * }
     */
    @PostMapping("/wx/invalidUser")
    public Result invalidUser(@RequestBody WxLogoutVo vo);


    /**
     * 获取登录配置
     * @apiNote 获取登录配置
     * @apiVersion 6.82
     * @tag 六、基础功能能力
     * @ResponseDemo
     * webTokenValiditySeconds web token失效时间
     * appTokenValiditySeconds app token失效时间
     * webRefreshTokenValiditySeconds app 刷新token失效时间
     * appRefreshTokenValiditySeconds web 刷新token失效时间
     * {
     *     "code":200,
     *     "data": {
     *   "webTokenValiditySeconds": 0,
     *   "appTokenValiditySeconds": 0,
     *   "webRefreshTokenValiditySeconds": 0,
     *   "appRefreshTokenValiditySeconds": 0
     * },
     *     "msg":"操作成功",
     *     "time":1654754979434
     * }
     */
    @PostMapping("/getConfig")
    Result<AuthConfig> getConfig();
}
