package com.chinatower.platform.project.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.tool.redis.domain.RedisReceiver;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.mapper.SysDictTypeMapper;
import com.chinatower.platform.project.system.service.ISysDictTypeService;

import javax.annotation.Resource;

import static com.chinatower.common.core.constant.Constants.SYS_DICT_KEY;

/**
 * 字典 业务层处理
 *
 * @author iwhalecloud
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService {
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;
    @Resource
    RedisTemplate redisTemplate;
    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 通过字典ID删除字典信息
     *
     * @param dictId 字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeById(Long dictId) {
        SysDictType dictType = selectDictTypeById(dictId);
        int res = dictTypeMapper.deleteDictTypeById(dictId);
        if(res>0 && ObjectUtil.isNotNull(dictType)){
            RedisUtil.delete(SYS_DICT_KEY+dictType.getDictType(),RedisDBType.PRIMARY);
            dictDataMapper.deleteDictDataByTypes(new String[]{dictType.getDictType()});

            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictType);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);

        }
        return res;
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public int deleteDictTypeByIds(Long[] dictIds) {
        int count = 0;
        for(Long dictId:dictIds){
            count += deleteDictTypeById(dictId);
        }
        return count;
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dictType) {
        dictType.setCreateTime(new Date());

        int i = dictTypeMapper.insertDictType(dictType);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictType);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dictType) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        dictType.setUpdateTime(new Date());
        int i = dictTypeMapper.updateDictType(dictType);
        if (i > 0){
            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysDictType);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);
        }
        return i;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void batchRefreshDictCache(Long[] dictIds) {
        List<SysDictData> sysDictDatas = dictDataMapper.selectDictDataByDictIds(dictIds);
        Map<String,List<SysDictData>> cacheMap = new HashMap<>();
        sysDictDatas.forEach(data->{
            if(cacheMap.containsKey(data.getDictType())){
                cacheMap.get(data.getDictType()).add(data);
            }else {
                cacheMap.put(data.getDictType(), CollUtil.newArrayList(data));
            }
        });
        for(String cacheKey:cacheMap.keySet()){
            RedisUtil.set(SYS_DICT_KEY+cacheKey, cacheMap.get(cacheKey), RedisDBType.PRIMARY);
        }
    }
}
