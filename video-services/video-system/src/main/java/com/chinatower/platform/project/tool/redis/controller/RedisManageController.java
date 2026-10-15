package com.chinatower.platform.project.tool.redis.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;

import com.chinatower.platform.framework.web.controller.BaseController;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.tool.redis.domain.RedisCacheInfo;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author WANGZIC
 */
@Api(tags = "缓存管理")
@RestController
@RequestMapping("/tool/redis")
public class RedisManageController extends BaseController {

//    @ApiOperation(value = "list")
//    @GetMapping("/list")
//    public AjaxResult list(RedisCacheInfo item) {
//        String patternKey = "*";
//        AjaxResult ajaxResult = AjaxResult.success();
//        if(StrUtil.isNotBlank(item.getCacheKey())){
//            patternKey ="*"+item.getCacheKey()+"*";
//        }else{
//            ajaxResult.put("rows",null);
//            ajaxResult.put("total",0);
//            return ajaxResult;
//        }
//        Set<String> keys = RedisUtil.keys("*"+patternKey+"*", RedisDBType.PRIMARY);
//        List<RedisCacheInfo> list = new ArrayList<>();
//        for (String key:keys){
//            list.add(new RedisCacheInfo(key));
//        }
//
//        ajaxResult.put("rows",list);
//        ajaxResult.put("total",keys.size());
//        return ajaxResult;
//    }

    private static final String TOTAL = "total";
    private static final String ROWS = "rows";

    /**
     * RedisUtil.keys不让使用，故暂时只支持精确查询
     * @param item
     * @return
     */
    @ApiOperation(value = "list")
    @GetMapping("/list")
    public AjaxResult list(RedisCacheInfo item) {
        AjaxResult ajaxResult = AjaxResult.success();
        if(StrUtil.isBlank(item.getCacheKey())){
            ajaxResult.put(ROWS,null);
            ajaxResult.put(TOTAL,0);
            return ajaxResult;
        }
        List<RedisCacheInfo> list = new ArrayList<>();
        String key = item.getCacheKey();
        String value = RedisUtil.get(key, RedisDBType.PRIMARY);
        if(StrUtil.isBlank(value)){
            ajaxResult.put(ROWS,null);
            ajaxResult.put(TOTAL,0);
            return ajaxResult;
        }
        RedisCacheInfo info = new RedisCacheInfo(key, RedisUtil.get(key, RedisDBType.PRIMARY));
        list.add(info);
        ajaxResult.put(ROWS,list);
        ajaxResult.put(TOTAL,list.size());
        return ajaxResult;
    }

  /*  @ApiOperation(value = "获取缓存")
    @GetMapping(value = "/key/{key}")
    public AjaxResult getInfo(@PathVariable("key") String key) {
        RedisCacheInfo info = new RedisCacheInfo(key, RedisUtil.get(key, RedisDBType.PRIMARY),RedisUtil.type(key, RedisDBType.PRIMARY).code(),RedisUtil.getExpire(key,RedisDBType.PRIMARY));
        return AjaxResult.success("获取成功",info);
    }*/

    @ApiOperation(value = "新增缓存")
    @TowerLog(title = "Redis缓存项", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public AjaxResult add(@RequestBody RedisCacheInfo item) {
        if(item.getCacheTimeout()!=null && item.getCacheTimeout()>0){
            RedisUtil.setEx(item.getCacheKey(),item.getCacheValue(),item.getCacheTimeout(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
        }else {
            RedisUtil.set(item.getCacheKey(),item.getCacheValue(), RedisDBType.PRIMARY);
        }
        return AjaxResult.success();
    }


    @ApiOperation(value = "修改缓存")
    @TowerLog(title = "Redis缓存项", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public AjaxResult edit(@RequestBody RedisCacheInfo item) {
        RedisUtil.delete(item.getCacheKey(), RedisDBType.PRIMARY);
        if(item.getCacheTimeout()!=null && item.getCacheTimeout()>0){
            RedisUtil.setEx(item.getCacheKey(),item.getCacheValue(),item.getCacheTimeout(),TimeUnit.SECONDS, RedisDBType.PRIMARY);
        }else {
            RedisUtil.set(item.getCacheKey(),item.getCacheValue(), RedisDBType.PRIMARY);
        }
        return AjaxResult.success();
    }

    @ApiOperation(value = "删除缓存")
    @TowerLog(title = "Redis缓存项", businessType = BusinessType.DELETE)
    @GetMapping("/del/{key}")
    public AjaxResult remove(@PathVariable String key) {
        RedisUtil.delete(key, RedisDBType.PRIMARY);
        return AjaxResult.success();
    }

}
