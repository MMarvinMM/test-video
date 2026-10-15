package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.ExceptionUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.constants.RedisOperationResult;
import com.chinatower.platform.project.system.constants.RedisSourceType;
import com.chinatower.platform.project.system.constants.RedisValueType;
import com.chinatower.platform.project.system.domain.SysRedis;
import com.chinatower.platform.project.system.domain.vo.EditRedisParam;
import com.chinatower.platform.project.system.domain.vo.SysRedisQuery;
import com.chinatower.platform.project.system.mapper.OperateTableMapper;
import com.chinatower.platform.project.system.mapper.SysRedisMapper;
import com.chinatower.platform.project.system.service.ISysRedisService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysRedisServiceImpl implements ISysRedisService {

    @Autowired
    private SysRedisMapper redisMapper;

    @Autowired
    private OperateTableMapper operateTableMapper;

    @Override
    public SysRedis detail(Long id) {
        return redisMapper.selectById(id);
    }

    @Override
    public List<SysRedis> list(SysRedisQuery query) {
        return redisMapper.selectList(query);
    }

    @Override
    public int add(EditRedisParam param) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysRedis sysRedis = BeanUtil.toBean(param,SysRedis.class);

        sysRedis.setResultType(RedisOperationResult.SUCCESS.getValue());
        try {
            this.saveRedisData(sysRedis);
        }catch (Exception e){
            e.printStackTrace();
            String error = ExceptionUtil.getExceptionMessage(e);
            sysRedis.setErrorReason(error);
            sysRedis.setResultType(RedisOperationResult.FAIL.getValue());
        }
        sysRedis.setCreateBy(user.getUserName());
        sysRedis.setCreateTime(new Date());
        return redisMapper.insert(sysRedis);

    }

    /**
     * 数据保存到redis
     * @param param
     */
    private void saveRedisData(SysRedis param){

        Object value =null;

        if(RedisSourceType.SQL.getValue().equals(param.getSourceType())){
            value = this.executeSql(param.getContent(),param.getValueType());
        }else {
            if(RedisValueType.STRING.getValue().equals(param.getValueType())){
                value = param.getContent();
                if(value==null||(value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))){
                    throw new RuntimeException("无法获取sql执行结果");
                }
            }else if(RedisValueType.LIST.getValue().equals(param.getValueType())){
                value = this.jsonObjToHash(JSON.parseArray(param.getContent()));
            }else if(RedisValueType.HASH.getValue().equals(param.getValueType())){
                value = JSON.parseObject(param.getContent()).getInnerMap();

            }
        }

        if(param.getDueTime()!=null){
            if (RedisValueType.HASH.getValue().equals(param.getValueType())) {
                Map<String, Object> innerMap = JSON.parseObject(param.getContent()).getInnerMap();
                Object o = innerMap.get("value");
                RedisUtil.hset(param.getKey(), ((String) innerMap.get("filed")),JSON.toJSONString(o),
                        RedisDBType.PRIMARY);
            }else{
                RedisUtil.set(param.getKey(), value, param.getDueTime().getTime(), RedisDBType.PRIMARY);
            }
        } else {
            RedisUtil.set(param.getKey(),value, RedisDBType.PRIMARY);
        }

    }

    private Object executeSql(String sql,String valueType){
        Object value =null;
        if(RedisValueType.STRING.getValue().equals(valueType)){
            value = operateTableMapper.executeSelectString(sql);
        }else if(RedisValueType.LIST.getValue().equals(valueType)){
            value = operateTableMapper.executeSelectList(sql);
        }else if(RedisValueType.HASH.getValue().equals(valueType)){
            value = operateTableMapper.executeSelectMap(sql);
        }

        if(value==null){
            throw new RuntimeException("执行sql查询结果为空，无法保存到redis！");
        }

        return value;
    }

    private List<Map<String,Object>> jsonObjToHash(JSONArray array){
        if(array==null){
            return null;
        }

        return array.stream().map(e->((JSONObject)e).getInnerMap()).collect(Collectors.toList());
    }

}
