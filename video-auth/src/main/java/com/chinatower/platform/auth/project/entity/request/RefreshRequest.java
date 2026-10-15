package com.chinatower.platform.auth.project.entity.request;

import com.chinatower.platform.common.utils.security.RsaUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: 刷新token请求
 * @Author: 张帅
 * @Date: 2021/12/30 11:42
 **/
@Data
public class RefreshRequest implements Serializable {

    /**
     * 请求来源：1：pc 2：app 3：wx小程序 4：pad 5：win客户端
     */
    private String source;

    /**
     * 刷新token
     */
    @NotBlank(message="刷新token不能为空")
    private String refreshToken;
    /**
     * appId
     */
    @NotBlank(message="appId不能为空")
    private String appId;


    public String getAppId(){
        return RsaUtils.decryptByPrivateKey(appId);
    }

    public static void main(String[] args) {
        System.out.println(RsaUtils.decryptByPrivateKey("123456"));
    }

}
