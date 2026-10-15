package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.dataobject.AlarmLableRelDO;
import com.chinatower.event.entity.dto.AddLableRelationDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import com.chinatower.event.entity.dto.TradAddLableDto;
import com.chinatower.event.mapper.AlarmLableMapper;
import com.chinatower.event.repository.IAlarmLableRelRepository;
import com.chinatower.event.repository.IAlarmLableRelService;
import com.chinatower.event.repository.IAlarmLableRepository;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class AlarmLableRelServiceImpl implements IAlarmLableRelService {

    @Autowired
    private IAlarmLableRepository alarmLableRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    @Autowired
    private AlarmLableMapper alarmLableMapper;

    /**
     * 告警对应标签查询
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmLableRelDTO> getLableByWaringOrderId(String waringOrderId) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }

        return alarmLableRelRepository.getTagsByAlarmType(waringOrderId);
    }

    /**
     * 告警关联标签保存
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLableRelation(AddLableRelationDTO addLableRelationDTO) {
        log.info("告警关联标签保存入参：{}", JSON.toJSONString(addLableRelationDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }

        SysUser user = loginUser.getUser();
        String waringOrderId = addLableRelationDTO.getWaringOrderId();

        //获取当前操作人
        String operator = SecurityUtils.getOperator();

        //先删除再新增   这里需要根据标签来删， 因为这个表没有行业版本字段
        alarmLableRelRepository.deleteLableRel(waringOrderId);
        Map<Long,AlarmLableRelDO> lableMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(addLableRelationDTO.getLables())) {
            for (ModifyLableDTO tag : addLableRelationDTO.getLables()) {
                AlarmLableRelDO alarmLableRelDO = new AlarmLableRelDO();
                alarmLableRelDO.setLableId(tag.getLableId());
                alarmLableRelDO.setLableName(tag.getLableName());
                alarmLableRelDO.setTableTime(waringOrderId.substring(0, 6));
                alarmLableRelDO.setWaringOrderId(waringOrderId);
                alarmLableRelDO.setCreator(operator);
                alarmLableRelDO.setCreateTime(new Date());
                alarmLableRelDO.setModifier(operator);
                alarmLableRelDO.setUpdateTime(new Date());
                //过滤重复标签id
                lableMap.put(tag.getLableId(), alarmLableRelDO);
            }

            List<AlarmLableRelDO> list = new ArrayList();
            List<Long> lableMapIdList = new ArrayList(lableMap.keySet());

            //查询标签id是否存在
            List<Long> realIdList = alarmLableRepository.getByIds(lableMapIdList, user.getTenantId());
            if (CollectionUtils.isNotEmpty(realIdList)) {
                for (Long tagId : realIdList) {
                    list.add(lableMap.get(tagId));
                }
                alarmLableRelRepository.saveBatch(list);
            } else {
                throw new CustomException(MessageConstants.ALARM_TAG_REL_SAVE_ERROR);
            }
        }
    }

    @Override
    public void tradeSaveLableRelation(TradAddLableDto tradAddLableDto) {
        log.info("AlarmLableRelServiceImpl-tradeSaveLableRelation-req:{}",JSON.toJSONString(tradAddLableDto));
        if(StringUtils.isEmpty(tradAddLableDto.getTenantId()) || StringUtils.isEmpty(tradAddLableDto.getIndustryCode()) ||
                CollectionUtils.isEmpty(tradAddLableDto.getWarningOrderIds()) || CollectionUtils.isEmpty(tradAddLableDto.getLableNames())){
            throw new CustomException(MessageConstants.ALARM_NO_PARAMS);
        }
        List<AlarmLableDO> lableList = alarmLableMapper.getByLableNames(tradAddLableDto.getLableNames(),tradAddLableDto.getTenantId(),tradAddLableDto.getIndustryCode());
        boolean flag =false;
        if(!CollectionUtils.isEmpty(lableList) && lableList.size() == tradAddLableDto.getLableNames().size()){
            //所传标签均在该租户配置中，可直接关联
            log.info("所传标签均在该租户配置中，可直接关联");
            flag =true;
        }else{
            List<AlarmLableDO> alarmLableList = new ArrayList<>();
            //所传标签有不在该租户配置中，先将未配置标签新增
            log.info("所传标签有不在该租户配置中，先将未配置标签新增");
            List<String> lableNameList = lableList.stream().map(a -> a.getLableName()).collect(Collectors.toList());
            for (String lableName:tradAddLableDto.getLableNames()) {
                if(!lableNameList.contains(lableName)){
                    //新增标签
                    AlarmLableDO alarmLableDO = new AlarmLableDO();
                    alarmLableDO.setLableName(lableName);
                    alarmLableDO.setTenantId(tradAddLableDto.getTenantId());
                    alarmLableDO.setIndustryCode(tradAddLableDto.getIndustryCode());
                    alarmLableDO.setDeleteFlag(Constants.ZERO);
                    alarmLableDO.setCreator("admin");
                    alarmLableDO.setCreateTime(new Date());
                    alarmLableList.add(alarmLableDO);
                }
            }
            alarmLableRepository.saveBatch(alarmLableList);
        }
        if(!flag){
            lableList = alarmLableMapper.getByLableNames(tradAddLableDto.getLableNames(),tradAddLableDto.getTenantId(),tradAddLableDto.getIndustryCode());
        }

        AddLableRelationDTO addLableRelationDTO = new AddLableRelationDTO();
        addLableRelationDTO.setWaringOrderIdList(tradAddLableDto.getWarningOrderIds());
        addLableRelationDTO.setLableIdList(lableList.stream().map(a ->a.getLableId()).collect(Collectors.toList()));
        //先删除再新增
        alarmLableRelRepository.deleteLableRelBatch(addLableRelationDTO);

        List<AlarmLableRelDO> list = new ArrayList();
        if (CollectionUtils.isNotEmpty(addLableRelationDTO.getLableIdList())) {
            for (String waringOrderId : addLableRelationDTO.getWaringOrderIdList()) {
                for (AlarmLableDO tag : lableList) {
                    AlarmLableRelDO alarmLableRelDO = new AlarmLableRelDO();
                    alarmLableRelDO.setLableId(tag.getLableId());
                    alarmLableRelDO.setLableName(tag.getLableName());
                    alarmLableRelDO.setTableTime(waringOrderId.substring(0, 6));
                    alarmLableRelDO.setWaringOrderId(waringOrderId);
                    alarmLableRelDO.setCreator("admin");
                    alarmLableRelDO.setCreateTime(new Date());
                    list.add(alarmLableRelDO);
                }

            }
            alarmLableRelRepository.saveBatch(list);
        }
    }


    /**
     * 新增告警关联标签
     *
     * @return
     * @author cuisj
     * @date 2024/07/23
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addNewTagRelation(AddLableRelationDTO addLableRelationDTO) {
        log.info("新增告警关联标签入参：{}", JSON.toJSONString(addLableRelationDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }

        SysUser user = loginUser.getUser();
        //获取当前操作人
        String operator = SecurityUtils.getOperator();

        List<Long> longList = addLableRelationDTO.getLables().stream().map(v -> v.getLableId()).collect(Collectors.toList());
        addLableRelationDTO.setLableIdList(longList);
        //先删除再新增
        alarmLableRelRepository.deleteLableRelBatch(addLableRelationDTO);
        List<AlarmLableRelDO> list = new ArrayList();
        if (CollectionUtils.isNotEmpty(addLableRelationDTO.getLables())) {
            for (String waringOrderId : addLableRelationDTO.getWaringOrderIdList()) {
                for (ModifyLableDTO tag : addLableRelationDTO.getLables()) {
                    AlarmLableRelDO alarmLableRelDO = new AlarmLableRelDO();
                    alarmLableRelDO.setLableId(tag.getLableId());
                    alarmLableRelDO.setLableName(tag.getLableName());
                    alarmLableRelDO.setTableTime(waringOrderId.substring(0, 6));
                    alarmLableRelDO.setWaringOrderId(waringOrderId);
                    alarmLableRelDO.setCreator(operator);
                    alarmLableRelDO.setCreateTime(new Date());
                    alarmLableRelDO.setModifier(operator);
                    alarmLableRelDO.setUpdateTime(new Date());
                    list.add(alarmLableRelDO);
                }

            }
            //查询标签id是否存在
            List<Long> realIdList = alarmLableRepository.getByIds(longList, user.getTenantId());
            if (CollectionUtils.isNotEmpty(realIdList)) {
                //去除不存在的标签
                list = list.stream().filter(v -> realIdList.contains(v.getLableId())).collect(Collectors.toList());
                alarmLableRelRepository.saveBatch(list);
            } else {
                throw new CustomException(MessageConstants.ALARM_TAG_REL_SAVE_ERROR);
            }
        }
    }
}
