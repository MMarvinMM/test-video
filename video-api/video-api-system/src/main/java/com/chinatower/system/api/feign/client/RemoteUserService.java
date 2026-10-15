package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.domain.R;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.factory.RemoteUserFallbackFactory;
import com.chinatower.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    /**
     * 通过用户名查询用户信息
     * @apiNote 通过用户名查询用户信息
     * @apiVersion 6.80
     * @tag 六、基础功能能力
     * @param username 用户名
     * @return R<LoginUser>
     */
    @GetMapping(value = "/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username);




}
