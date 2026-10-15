package com.chinatower.auth.api.factory;

import com.chinatower.auth.api.AuthFeignClient;
import com.chinatower.auth.api.entity.LoginFeignVo;
import com.chinatower.auth.api.entity.SwitchLoginFeignVo;
import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.auth.api.entity.WxUnionIdVo;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthFallbackFactory implements FallbackFactory<AuthFeignClient> {
    @Override
    public AuthFeignClient create(Throwable throwable) {
        return new AuthFeignClient() {
            @Override
            public Result userIdLogin(String token, String Authorization) {
                return Result.fail("根据userId登录报错"+throwable.getMessage());
            }

            @Override
            public Result<String> colonyBind(WxColonyCodeVo vo) {
                return Result.fail("调用中心节点属地化绑定微信信息报错"+throwable.getMessage());
            }

            @Override
            public Result wxLogin(WxUnionIdVo vo) {
                return Result.fail("调用属地节点wxLogin信息报错"+throwable.getMessage());
            }
            @Override
            public Result<String> doSwitchLogin(Map<String, String> param){
                return Result.fail("调用属地节点doSwitchLogin信息报错"+throwable.getMessage());
            }

            @Override
            public Result<SwitchLoginFeignVo> checkSwitchId(SwitchLoginFeignVo switchLoginVO) {
                return Result.fail("调用属地节点短token获取信息报错"+throwable.getMessage());
            }

            @Override
            public Result chooseTrade(LoginFeignVo vo, String token) {
                return Result.fail("调用属地节点选择行业报错"+throwable.getMessage());
            }
        };
    }
}
