package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.dataobject.SysAlarmTagsTypeRelDO;
import com.chinatower.event.dto.AddTagRelationDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.common.core.dto.ModifyTagDTO;
import com.chinatower.event.repository.ISysAlarmTagsRepository;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelRepository;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警类型标签关联业务接口实现
 * @author chenmf
 * @version 1.0
 * @date 2022/4/26 11:32
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SysAlarmTagsTypeRelServiceImpl implements ISysAlarmTagsTypeRelService {
    
    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private ISysAlarmTagsTypeRelRepository sysAlarmTagsTypeRelRepository;
    
    /**
     * 告警类型对应标签查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmTagsTypeRelDTO> getTagsByAlarmType(String alarmTypeId) {
    
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
    
        return sysAlarmTagsTypeRelRepository.getTagsByAlarmType(alarmTypeId,null, loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
    }
    
    /**
     * 告警类型关联应标签保存
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTagRelation(AddTagRelationDTO addTagRelationDTO) {
    
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        try {
            log.info("SysAlarmTagsTypeRelServiceImpl saveTagRelation userName:{} switchUserName:{}",
                    loginUser.getUsername(), (loginUser.getSwitchdLoginUser() != null ? loginUser.getSwitchdLoginUser().getUsername() : "not switch" ));
        } catch (Exception e) {
            log.error("SysAlarmTagsTypeRelServiceImpl saveTagRelation loginUser print error:", e);
        }
        SysUser user = loginUser.getUser();
        String alarmTypeId = addTagRelationDTO.getAlarmTypeId();

        //获取当前操作人
        String operator = SecurityUtils.getOperator();

        //先删除再新增   这里需要根据标签来删， 因为这个表没有行业版本字段
        sysAlarmTagsTypeRelRepository.deleteTagsTypeRel(alarmTypeId, user.getTenantId(), user.getIndustryCode());
        Map<Long,SysAlarmTagsTypeRelDO> tagMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(addTagRelationDTO.getTags())) {
            for (ModifyTagDTO tag : addTagRelationDTO.getTags()) {
                SysAlarmTagsTypeRelDO sysAlarmTagsTypeRelDO = new SysAlarmTagsTypeRelDO();
                sysAlarmTagsTypeRelDO.setAlarmTypeId(addTagRelationDTO.getAlarmTypeId());
                sysAlarmTagsTypeRelDO.setTenantId(user.getTenantId());
                sysAlarmTagsTypeRelDO.setTagId(tag.getTagId());
                sysAlarmTagsTypeRelDO.setTagName(tag.getTagName());
                sysAlarmTagsTypeRelDO.setTypeValue(addTagRelationDTO.getTypeValue());
                sysAlarmTagsTypeRelDO.setCreator(operator);
                sysAlarmTagsTypeRelDO.setCreateTime(new Date());
                sysAlarmTagsTypeRelDO.setModifier(operator);
                sysAlarmTagsTypeRelDO.setUpdateTime(new Date());
                sysAlarmTagsTypeRelDO.setDeleteFlag(Constants.VALID_STATE);
        
                //过滤重复标签id
                tagMap.put(tag.getTagId(), sysAlarmTagsTypeRelDO);
            }
    
            List<SysAlarmTagsTypeRelDO> list = new ArrayList();
            List<Long> idList = new ArrayList(tagMap.keySet());
    
            //查询标签id是否存在
            List<Long> realIdList = sysAlarmTagsRepository.getByIds(idList, user.getTenantId());
            if (CollectionUtils.isNotEmpty(realIdList)) {
                for (Long tagId : realIdList) {
                    list.add(tagMap.get(tagId));
                }
                sysAlarmTagsTypeRelRepository.saveBatch(list);
            } else {
                throw new CustomException(MessageConstants.ALARM_TAG_REL_SAVE_ERROR);
            }
        }
    }

    @Override
    public void saveTagRelationBatch(List<AddTagRelationDTO> addTagRelationList) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        try {
            log.info("SysAlarmTagsTypeRelServiceImpl saveTagRelation userName:{} switchUserName:{}",
                    loginUser.getUsername(), (loginUser.getSwitchdLoginUser() != null ? loginUser.getSwitchdLoginUser().getUsername() : "not switch" ));
        } catch (Exception e) {
            log.error("SysAlarmTagsTypeRelServiceImpl saveTagRelation loginUser print error:", e);
        }

        SysUser user = loginUser.getUser();
        //获取当前操作人
        String operator = SecurityUtils.getOperator();

        List<String> alarmTypeIdList = addTagRelationList.stream().map(AddTagRelationDTO::getAlarmTypeId).collect(Collectors.toList());
        //先删除再新增   这里需要根据标签来删， 因为这个表没有行业版本字段
        sysAlarmTagsTypeRelRepository.deleteTagsTypeRelBatch(alarmTypeIdList, user.getTenantId(), user.getIndustryCode());
        // 标签map key -> tagId value -> alarmTypeInfo
        Map<String, Map<Long,SysAlarmTagsTypeRelDO>> alarmTypeMap = Maps.newHashMap();
        // 所有关联的标签编码
        Set<Long> tagIdSet = Sets.newHashSet();
        for (AddTagRelationDTO addTagRelationDTO : addTagRelationList) {
            if (!CollectionUtils.isEmpty(addTagRelationDTO.getTags())) {
                // 标签map key -> tagId value -> alarmTypeInfo
                Map<Long,SysAlarmTagsTypeRelDO> tagMap = new HashMap<>();
                for (ModifyTagDTO tag : addTagRelationDTO.getTags()){
                    SysAlarmTagsTypeRelDO sysAlarmTagsTypeRelDO = new SysAlarmTagsTypeRelDO();
                    sysAlarmTagsTypeRelDO.setAlarmTypeId(addTagRelationDTO.getAlarmTypeId());
                    sysAlarmTagsTypeRelDO.setTenantId(user.getTenantId());
                    sysAlarmTagsTypeRelDO.setTagId(tag.getTagId());
                    sysAlarmTagsTypeRelDO.setTagName(tag.getTagName());
                    sysAlarmTagsTypeRelDO.setTypeValue(addTagRelationDTO.getTypeValue());
                    sysAlarmTagsTypeRelDO.setCreator(operator);
                    sysAlarmTagsTypeRelDO.setCreateTime(new Date());
                    sysAlarmTagsTypeRelDO.setModifier(operator);
                    sysAlarmTagsTypeRelDO.setUpdateTime(new Date());
                    sysAlarmTagsTypeRelDO.setDeleteFlag(Constants.VALID_STATE);

                    //过滤重复标签id
                    tagMap.put(tag.getTagId(), sysAlarmTagsTypeRelDO);
                    tagIdSet.add(tag.getTagId());
                }
                if (MapUtils.isNotEmpty(tagMap)) {
                    alarmTypeMap.put(addTagRelationDTO.getAlarmTypeId(), tagMap);
                }
            }
        }
        if (MapUtils.isNotEmpty(alarmTypeMap) && CollectionUtils.isNotEmpty(tagIdSet)){
            List<SysAlarmTagsTypeRelDO> list = new ArrayList<>();

            //查询标签id是否存在
            List<Long> realIdList = sysAlarmTagsRepository.getByIds(Lists.newArrayList(tagIdSet), user.getTenantId());
            if (CollectionUtils.isNotEmpty(realIdList)) {
                for (Long tagId : realIdList) {
                    for (Map.Entry<String, Map<Long, SysAlarmTagsTypeRelDO>> entry : alarmTypeMap.entrySet()) {
                        Map<Long, SysAlarmTagsTypeRelDO> tagMap = entry.getValue();
                        if (tagMap.containsKey(tagId)) {
                            list.add(tagMap.get(tagId));
                        }
                    }
                }
                sysAlarmTagsTypeRelRepository.saveBatch(list);
            } else {
                throw new CustomException(MessageConstants.ALARM_TAG_REL_SAVE_ERROR);
            }
        }
    }
}
