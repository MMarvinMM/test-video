package com.chinatower.bdm.modules.unionTargetJs.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * video-video公共服务类
 * Created by zhujun on 2022/1/14.
 */
@Service
public class UnionTargetJsCommonService {


    /**
     * 接口方法路径集合
     */
    public static final Map<String, String> urlMap;

    static {
        urlMap = new HashMap<String, String>();

        //摄光电联动信令下发
        urlMap.put("targetFollow", "/api/target/external/targetFollow");
    }



}
