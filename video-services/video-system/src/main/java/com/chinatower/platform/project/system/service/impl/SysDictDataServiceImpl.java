package com.chinatower.platform.project.system.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.platform.framework.config.CacheRunner;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.tool.redis.domain.RedisReceiver;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.service.ISysDictDataService;

import javax.annotation.Resource;

import static com.chinatower.common.core.constant.Constants.SYS_DICT_KEY;

/**
 * 字典 业务层处理
 *
 * @author chinatower
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {
    @Autowired
    private SysDictDataMapper dictDataMapper;
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 功能说明:根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> list = CacheRunner.DICT_DATA_MAP_DICT_TYPE.get(dictType);
        if (list == null ||  list.isEmpty()){
            list = RedisUtil.getList(SYS_DICT_KEY+dictType, SysDictData.class, RedisDBType.PRIMARY);
            if(CollUtil.isEmpty(list)){
                list = dictDataMapper.selectDictDataByType(dictType);
                RedisUtil.set(SYS_DICT_KEY+dictType, list, RedisDBType.PRIMARY);
                CacheRunner.DICT_DATA_MAP_DICT_TYPE.put(dictType,list);
            }else{
                CacheRunner.DICT_DATA_MAP_DICT_TYPE.put(dictType,list);
            }
        }
        return list;
    }

    @Override
    public Map<String, String> selectDictMapByType(String dictType) {
        Map<String,String> map = new HashMap<>();
        List<SysDictData> list = selectDictDataByType(dictType);
        if(list.isEmpty()){
            return map;
        }
        for(SysDictData data:list){
            map.put(data.getDictValue(),data.getDictLabel());
        }
        return map;
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 通过字典ID删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataById(Long dictCode) {
        SysDictData sysDictData = dictDataMapper.selectDictDataById(dictCode);
        RedisUtil.delete(SYS_DICT_KEY+sysDictData.getDictType(),RedisDBType.PRIMARY);

        int i = dictDataMapper.deleteDictDataById(dictCode);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictData);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        List<String> types = dictDataMapper.selectDictTypesByCodes(dictCodes);
        if(CollUtil.isNotEmpty(types)){
            types.forEach(type-> RedisUtil.delete(SYS_DICT_KEY+type,RedisDBType.PRIMARY));
        }
        int i = dictDataMapper.deleteDictDataByIds(dictCodes);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictData);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        dictData.setCreateTime(new Date());
        RedisUtil.delete(SYS_DICT_KEY+dictData.getDictType(),RedisDBType.PRIMARY);
        int i = dictDataMapper.insertDictData(dictData);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictData);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        dictData.setUpdateTime(new Date());
        RedisUtil.delete(SYS_DICT_KEY+dictData.getDictType(),RedisDBType.PRIMARY);
        int i = dictDataMapper.updateDictData(dictData);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictData);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    @Override
    public Map<String, List<SysDictData>> selectDictDataByTypes(String[] dictTypes) {
        List<String> noCacheList = new ArrayList<>();
        Map<String, List<SysDictData>> cacheMap = new HashMap<>();
        for(String type:dictTypes){
            List<SysDictData> list = CacheRunner.DICT_DATA_MAP_DICT_TYPE.get(type);
            if (list == null || list.isEmpty()){
                if(Boolean.TRUE.equals(RedisUtil.hasKey(SYS_DICT_KEY+type,RedisDBType.PRIMARY))){
                    list = RedisUtil.getList(SYS_DICT_KEY+type,SysDictData.class,RedisDBType.PRIMARY);
                }
            }
            if (list != null && !list.isEmpty()){
                cacheMap.put(type,list);
                CacheRunner.DICT_DATA_MAP_DICT_TYPE.put(type,list);
            }else {
                noCacheList.add(type);
            }
//            if(RedisUtil.hasKey(SYS_DICT_KEY+type,RedisDBType.PRIMARY)){
//                cacheMap.put(type,RedisUtil.getList(SYS_DICT_KEY+type,SysDictData.class,RedisDBType.PRIMARY));
//            }else {
//                noCacheList.add(type);
//            }
        }
        Map<String, List<SysDictData>> dbMap = new HashMap<>();
        if(CollUtil.isNotEmpty(noCacheList)){
            List<SysDictData> list = dictDataMapper.selectDictDataByTypes(ArrayUtil.toArray(noCacheList,String.class));
            for(SysDictData data:list){
                if(dbMap.containsKey(data.getDictType())){
                    dbMap.get(data.getDictType()).add(data);
                }else {
                    dbMap.put(data.getDictType(),CollUtil.newArrayList(data));
                }
            }
            for(String key:dbMap.keySet()){
                RedisUtil.set(SYS_DICT_KEY+key, dbMap.get(key), RedisDBType.PRIMARY);
                cacheMap.put(key,dbMap.get(key));
                CacheRunner.DICT_DATA_MAP_DICT_TYPE.put(key,dbMap.get(key));
            }
        }
        return cacheMap;
    }
}
