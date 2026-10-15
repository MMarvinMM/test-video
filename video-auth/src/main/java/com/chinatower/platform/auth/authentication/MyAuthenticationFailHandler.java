package com.chinatower.platform.auth.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author bruce
 * @version V1.0
 * @date 2021/11/16
 */
@Component
public class MyAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        logger.debug("登录失败");
//        //设置状态码
//        response.setStatus(500);
//        response.setContentType("application/json;charset=UTF-8");
//        BaseResponse<String> br = new BaseResponse<>();
//        br.setResponseStatus(ResponseEnum.STATUS_CODE_401);
//        //将 登录失败 信息打包成json格式返回
//        response.getWriter().write(JSONUtil.toJsonStr(br));
    }
}
