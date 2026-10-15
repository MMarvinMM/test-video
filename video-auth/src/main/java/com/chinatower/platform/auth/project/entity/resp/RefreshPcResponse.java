package com.chinatower.platform.auth.project.entity.resp;

import lombok.Data;
/**
 * @Description: 刷新Pc用户登录时效
 * @Author: cuisj
 **/
@Data
public class RefreshPcResponse{

    /**
     * 刷新user唯一web端key
     */
    private String userKey;
    /**
     * 登录中连续刷新次数
     */
    private Integer userRefreshCount;
}
