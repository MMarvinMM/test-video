package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.core.dto.AlarmTypeClass;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.core.dto.ModifyTagDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.dto.*;
import com.chinatower.event.mapper.AlarmClassParamMapper;
import com.chinatower.event.mapper.AlarmTypeClassMapper;
import com.chinatower.event.mapper.AlarmTypeLevelMapper;
import com.chinatower.event.mapper.AlarmTypeMapper;
import com.chinatower.event.repository.IAlarmTypeClassService;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelService;
import com.chinatower.event.repository.ISysService;
import com.chinatower.common.core.vo.AlarmClassParamVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 告警类型业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 11:32
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class AlarmTypeClassServiceImpl implements IAlarmTypeClassService {
    @Autowired
    private AlarmTypeMapper alarmTypeMapper;
    @Autowired
    private AlarmClassParamMapper alarmClassParamMapper;
    @Autowired
    private AlarmTypeClassMapper alarmTypeClassMapper;
    @Autowired
    private ISysAlarmTagsTypeRelService sysAlarmTagsTypeRelService;
    @Autowired
    private ISysService sysService;

    @Autowired
    private AlarmTypeLevelMapper alarmTypeLevelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> updateType(AlarmTypeClassDTO body) {
        try {
            List<AlarmType> typeList = body.getTypeList();
            if(!CollectionUtils.isEmpty(typeList)){
                List<ModifyTagDTO> tagList = body.getTagList();
                // 循环保存关联标签
                if(!CollectionUtils.isEmpty(tagList)){
                    List<AddTagRelationDTO> tagRelationList = new ArrayList<>();
                    for (AlarmType alarmType : typeList) {
                        AddTagRelationDTO addTagRelationDTO = new AddTagRelationDTO();
                        addTagRelationDTO.setAlarmTypeId(alarmType.getAlarmTypeId());
                        addTagRelationDTO.setTags(tagList);
                        addTagRelationDTO.setTypeValue(alarmType.getTypeValue());
                        tagRelationList.add(addTagRelationDTO);
                    }
                    if (!CollectionUtils.isEmpty(tagRelationList)){
                        sysAlarmTagsTypeRelService.saveTagRelationBatch(tagRelationList);
                    }
                }
            }
            // 处理告警等级信息
            modifyAlarmLevel(body);

            return Result.success("新增成功");
        } catch (Exception e) {
            log.error("updateType异常信息" + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Result<AlarmClassParamVO> getAlarmParamInfo(AlarmClassParamVO alarmClassParamVO) {
        AlarmClassParamVO result = new AlarmClassParamVO();
        if (!StringUtils.isEmpty(alarmClassParamVO.getAlarmTypeClassId())) {
            result = alarmClassParamMapper.getInfo(alarmClassParamVO);
            if (result != null){
                if(!StringUtils.isEmpty(result.getAlgorithmTimeJosn())){
                    result.setAlgorithmTime(result.getAlgorithmTimeJosn().split(","));
                }
            }
        }
        Result<AlarmClassParamVO> res = Result.success("查询成功");
        res.setData(result);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> saveAlarmParamInfo(AlarmClassParamVO alarmClassParamVO) {
        if(StringUtils.isEmpty(alarmClassParamVO.getAlarmTypeId())){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "alarmTypeId为空！");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = alarmClassParamVO.getTenantId();
        String userId = "";
        if (null != loginUser) {
            tenantId = loginUser.getUser().getTenantId();
            userId = sysService.getLoginUserId();
        }
        AlarmTypeClass alarmTypeClass = new AlarmTypeClass();
        alarmTypeClass.setAlarmTypeId(alarmClassParamVO.getAlarmTypeId());
        alarmTypeClass.setTenantId(tenantId);
        List<String> alarmTypeClassIdList = alarmTypeClassMapper.findAlarmTypeClassIdExist(alarmTypeClass);
        String alarmTypeClassId;
        if (!CollectionUtils.isEmpty(alarmTypeClassIdList)) {
            alarmTypeClassId = alarmTypeClassIdList.get(0);
            //清空
            alarmClassParamMapper.delete(alarmTypeClassId);
        } else {
            alarmTypeClassId = UUID.randomUUID().toString();
            alarmTypeClass.setTypeClassId(alarmTypeClassId);
            alarmTypeClass.setCreateBy(userId);
            alarmTypeClass.setTenantor(tenantId);
            alarmTypeClassMapper.insertAlarmTypeClass(alarmTypeClass);
        }

        alarmClassParamVO.setAlarmTypeClassId(alarmTypeClassId);

        //新增
        if (!ObjectUtils.isEmpty(alarmClassParamVO.getAlgorithmTime())) {
            String time = algorithmTimeDeal(alarmClassParamVO.getAlgorithmTime());
            if(StringUtils.isEmpty(time)){
                alarmClassParamVO.setAlgorithmTimeJosn(null);
            }else{
                alarmClassParamVO.setAlgorithmTimeJosn(time.substring(0,time.length()-1));
            }
        }
        if(StringUtils.isEmpty(alarmClassParamVO.getAlarmInterval())){
            alarmClassParamVO.setAlarmInterval(null);
        }
        if(StringUtils.isEmpty(alarmClassParamVO.getThresh())){
            alarmClassParamVO.setThresh(null);
        }
        alarmClassParamVO.setTenantId(tenantId);
        List<AlarmClassParamVO> alarmClassParamVOS = new ArrayList<>();
        alarmClassParamVOS.add(alarmClassParamVO);
        alarmClassParamMapper.save(alarmClassParamVOS);
        return Result.success("保存成功");
    }

    @Override
    public Result<String> modifyAlarmLevel(AlarmTypeClassDTO body) {
        // 处理告警等级信息
        String alarmLevel = body.getAlarmLevel();
        List<String> typeValueList = body.getTypeList().stream().map(AlarmType::getTypeValue).distinct().collect(Collectors.toList());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String appVerCode = user.getAppVerCode();
        // 批量删除原来的告警等级信息
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(typeValueList)) {
            alarmTypeLevelMapper.deleteAlarmTypeLevelBatch(tenantId, industryCode, typeValueList);
        }
        // 批量新增新的告警等级信息
        List<AlarmTypeLevelDO> list = new ArrayList<>();
        for (String typeValue : typeValueList) {
            AlarmTypeLevelDO alarmTypeLevelDO = new AlarmTypeLevelDO();
            alarmTypeLevelDO.setTypeValue(typeValue);
            alarmTypeLevelDO.setAlarmLevel(alarmLevel);
            alarmTypeLevelDO.setTenantId(tenantId);
            alarmTypeLevelDO.setIndustryCode(industryCode);
            alarmTypeLevelDO.setAppVerCode(appVerCode);
            alarmTypeLevelDO.setCreator(user.getUserName());
            alarmTypeLevelDO.setCreateTime(new Date());
            alarmTypeLevelDO.setDeleteFlag(0);
            list.add(alarmTypeLevelDO);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            alarmTypeLevelMapper.addAlarmTypeLevelBatch(list);
        }
        return Result.success("告警等级修改成功");
    }

    /**
     * @Author：hued
     * @Description：将数组转换为字符串
     * @Date：2021/12/9 17:02
     */
    private String algorithmTimeDeal(String[] algorithmTime){
        StringBuilder time = new StringBuilder();
        if (!ObjectUtils.isEmpty(algorithmTime)) {
            for(String t : algorithmTime){
                String str = t + ",";
                time.append(str);
            }
        }
        return time.toString();
    }
}
