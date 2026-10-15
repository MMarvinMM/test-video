package com.chinatower.gis.common.service;

import com.chinatower.gis.common.dao.GisCommonDAO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class GisCommonService {
    @Autowired
    GisCommonDAO gisCommonDAO;
    /**
     * 接口方法路径集合
     */
    private static final Map<String, String> urlMap;

    static {
        urlMap = new HashMap<String, String>();
        urlMap.put("bdKeyWord", "");

    }

    /**
     * 获取接口地址
     *
     * @param method 接口code
     * @return
     */
    public String getInterfaceUrl(String method, String... params) {
        // 数据库接口地址
        String urlInDb = null;
        // 获取缓存内数据
        Object redisObj = RedisUtil.get(method, RedisDBType.PRIMARY);
        if (redisObj != null) {
            urlInDb = (String) redisObj;
        } else {
            // 获取数据库接口地址
            urlInDb = gisCommonDAO.getInterfaceUrl(method);
//            urlInDb = gisCommonDAO.getInterfaceUrl(method);
            RedisUtil.set(method, urlInDb, RedisDBType.PRIMARY);
        }
        return urlInDb;
        // 拼接替换变量
//        return urlInDb + MessageFormat.format(urlMap.get(method), params);
    }
}
