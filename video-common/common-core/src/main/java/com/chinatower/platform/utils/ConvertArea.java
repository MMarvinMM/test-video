package com.chinatower.platform.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: ConvertArea
 * @description: 行政地市转换通用方法
 * @author: yongqi.han
 * @date: 2021/12/15
 * @requestID 需求单号及需求名称
 */
@SuppressWarnings("all")
@Deprecated
public class ConvertArea {
    public static boolean INIT_FLAG = false;
    private static final Map<String, String> AREA_CODE_MAP = new HashMap<>();  //以Code为key缓存地市信息
    private static final Map<String, String> AREA_NAME_MAP = new HashMap<>();  //以Name为key缓存地市信息
    private static final String AREA_LIST_KEY = "area:list:all";

    public static boolean initFormatArea(){
        List<SysArea> sysAreaList = getAllArea();
        if(sysAreaList==null&&sysAreaList.isEmpty()){
            return INIT_FLAG;
        }
        // 代码有bug，修改2022-03-16 zzh
        if (MapUtils.isEmpty(AREA_CODE_MAP)) {
            synchronized (ConvertArea.class) {
                sysAreaList.stream().forEach(item->{
                    if(StrUtil.isNotEmpty(item.getAreaCode())){
                        AREA_CODE_MAP.put(item.getAreaCode(),item.getAreaName());
                    }
                    if(StrUtil.isNotEmpty(item.getAreaName())){
                        AREA_NAME_MAP.put(item.getAreaName(),item.getAreaCode());
                    }
                });
                INIT_FLAG = true;
            }
        }
        return INIT_FLAG;
    }

    public static String convertCode(Object code){
        if(!INIT_FLAG){if(!initFormatArea()){return null;}}
        return AREA_CODE_MAP.get(String.valueOf(code));
    }

    public static String convertCode(String code){
        if(!INIT_FLAG){if(!initFormatArea()){return null;}}
        return AREA_CODE_MAP.get(code);
    }

    public static String convertName(String name){
        if(!INIT_FLAG){if(!initFormatArea()){return null;}}
        return AREA_NAME_MAP.get(name);
    }

    private static List getAllArea(){
        List<SysArea> list=new ArrayList<>();
        String areaStr = RedisUtil.get(AREA_LIST_KEY, RedisDBType.PRIMARY);
        if(StrUtil.isNotEmpty(areaStr)){
            list = JSON.parseArray(JSON.parse(areaStr).toString(),SysArea.class);
        }
        return list;
    }
}
