package com.chinatower.platform.project.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.UserCommonCollection;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.project.common.mapper.CommonMapper;
import com.chinatower.platform.project.common.service.ICommonService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WANGZIC
 */
@Slf4j
@Service
public class CommonServiceImpl implements ICommonService {

    @Autowired
    private CommonMapper commonMapper;


    @Override
    public String getSeqByName(String name,String prefix,String suffix){
        return prefix+commonMapper.getSeqByName(name)+suffix;
    }

    /**
     * 获取序列id
     *
     * @param name   序列名
     * @param number 数目
     * @return 序列id
     */
    @Override
    public String getSeqByName(String name, String number) {
        List result = new ArrayList<String>();
        for (int i = 0; i < Integer.parseInt(number); i++) {
            result.add(i);
        }
        List resultData = commonMapper.getSeqByNameList(name,result);
        String str = String.join(",", resultData);
        return str;
    }

    /**
     * 获取序列id
     *
     * @param name 序列名
     * @return 序列id
     */
    @Override
    public String getSeqByName(String name) {
        return commonMapper.getSeqByName(name);
    }

    @Override
    public int isExistTable(String tableName) {
        return commonMapper.isExistTable(tableName);
    }

    /**
     * 新增收藏
     *
     * @author trk
     * @date 2022/3/25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addCollections(List<String> collObjCode, String collObjType) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        UserCommonCollection commonCollection = new UserCommonCollection();
        commonCollection.setUserId(userId);
        commonCollection.setCollObjCodeList(collObjCode);
        //收藏表合并，资源类型变成新表14
        if(!StringUtils.isEmpty(collObjType)){
            collObjType = "1".equals(collObjType.trim()) ? "14" : collObjType;
        }
        commonCollection.setCollObjType(collObjType);
        //查询已有收藏
        List<String> list = commonMapper.getOldCollection(commonCollection);
        List<UserCommonCollection> insertList = new ArrayList<>();
        for (String code : collObjCode) {
            //排除已收藏
            if(!list.contains(code)){
                UserCommonCollection deviceCollection = new UserCommonCollection();
                deviceCollection.setUserId(userId);
                deviceCollection.setCollObjCode(code);
                deviceCollection.setCollObjType(collObjType);
                insertList.add(deviceCollection);
            }
        }
        if(!insertList.isEmpty()){
            commonMapper.insertBatchCollection(insertList);
        }
    }

    /**
     * 取消收藏
     *
     * @author trk
     * @date 2022/3/25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelCollections(List<String> collObjCode, String collObjType) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        UserCommonCollection commonCollection = new UserCommonCollection();
        commonCollection.setUserId(userId);
        commonCollection.setCollObjCodeList(collObjCode);
        //收藏表合并，资源类型变成新表14
        if(!StringUtils.isEmpty(collObjType)){
            collObjType = "1".equals(collObjType.trim()) ? "14" : collObjType;
        }
        commonCollection.setCollObjType(collObjType);
        commonMapper.removeCollection(commonCollection);
    }

}
