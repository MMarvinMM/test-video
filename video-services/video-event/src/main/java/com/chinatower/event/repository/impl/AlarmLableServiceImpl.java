package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.entity.dto.AddLableDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.DeleteLableDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import com.chinatower.event.repository.*;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 告警类型业务接口实现
 * @author chenmf
 * @version 1.0
 * @date 2022/4/26 11:32
 */
@Slf4j
@Service
public class AlarmLableServiceImpl implements IAlarmLableService {

    @Autowired
    private IAlarmLableRepository alarmLableRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    /**
     * 告警类型标签新增
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void addLable(AddLableDTO addLableDTO) {
        log.info("告警标签新增入参：{}", JSON.toJSONString(addLableDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        String tenantId = currentUser.getTenantId();
        //获取操作人
        String operator = SecurityUtils.getOperator();

        AlarmLableDO alarmLableDO = new AlarmLableDO();
        alarmLableDO.setLableName(addLableDTO.getLableName());
        alarmLableDO.setTenantId(tenantId);
        alarmLableDO.setIndustryCode(currentUser.getIndustryCode());
        alarmLableDO.setAppVerCode(currentUser.getAppVerCode());

        // 标签名不可重复 加租户ID 判断是否重复
        int num = alarmLableRepository.countTagName(alarmLableDO);
        if (num > 0) {
            throw new CustomException(MessageConstants.ALARM_LABLE_NAME_ERROR);
        }

        // 标签个数不超过20
        int countLable = alarmLableRepository.countTag(alarmLableDO);
        if (countLable >= Constants.ALARM_TAG_MAX_NUM) {
            throw new CustomException(MessageConstants.ALARM_TAG_NUM_ERROR);
        }
        alarmLableDO.setCreator(operator);
        alarmLableDO.setCreateTime(new Date());
        alarmLableDO.setDeleteFlag(Constants.VALID_STATE);
        alarmLableRepository.save(alarmLableDO);
    }

    /**
     * 告警类型标签修改
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void modifyLable(ModifyLableDTO modifyLableDTO) {
        log.info("编辑标签入参：{}", JSON.toJSONString(modifyLableDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();

        AlarmLableDO alarmLableDO = new AlarmLableDO();
        alarmLableDO.setLableName(modifyLableDTO.getLableName());
        alarmLableDO.setLableId(modifyLableDTO.getLableId());
        alarmLableDO.setIndustryCode(currentUser.getIndustryCode());
        // 标签名不可重复 租户ID 判断是否重复
        alarmLableDO.setTenantId(currentUser.getTenantId());
        int num = alarmLableRepository.countTagName(alarmLableDO);
        if (num > 0) {
            throw new CustomException(MessageConstants.ALARM_LABLE_NAME_ERROR);
        }

        alarmLableDO.setModifier(currentUser.getUserId());
        alarmLableDO.setUpdateTime(new Date());
        alarmLableRepository.updateById(alarmLableDO);

        // 同时修改关联标签表中的标签名
        alarmLableRelRepository.modifyRelTagName(alarmLableDO);
    }

    /**
     * 告警类型标签删除
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteLable(DeleteLableDTO deleteLableDTO) {
        log.info("删除标签入参：{}", JSON.toJSONString(deleteLableDTO));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        AlarmLableDO alarmLableDO = alarmLableRepository.getById(deleteLableDTO.getLableId());
        if (null == alarmLableDO) {
            throw new CustomException(MessageConstants.ALARM_LABLE_ERROR);
        }

        // 当前标签已关联标签数量
        int relNum = alarmLableRelRepository.selectLableRelCount(deleteLableDTO.getLableId());
        if (relNum >= 1) {
            throw new CustomException(MessageConstants.ALARM_LABLE_DEL_MSG);
        }

        //标签删除
        alarmLableRepository.removeById(deleteLableDTO.getLableId());
    }

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author chenmf
     * @date 2022/11/11
     * @version
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public List<AlarmLableRelDTO> getAllTagsByTenantId() {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        return alarmLableRepository.getAllTagsByTenantId(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
    }


}
