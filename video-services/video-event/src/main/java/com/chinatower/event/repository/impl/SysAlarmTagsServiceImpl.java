package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.dto.AddTagDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.dto.DeleteTagDTO;
import com.chinatower.common.core.dto.ModifyTagDTO;
import com.chinatower.event.repository.ISysAlarmTagsRepository;
import com.chinatower.event.repository.ISysAlarmTagsService;
import com.chinatower.event.repository.ISysAlarmTagsTypeRelRepository;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
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
@DS(EbikeDataBaseConstant.BASE)
public class SysAlarmTagsServiceImpl implements ISysAlarmTagsService {

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private ISysAlarmTagsTypeRelRepository sysAlarmTagsTypeRelRepository;

    /**
     * 告警类型标签新增
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addTag(AddTagDTO addTagDTO) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();

        //获取操作人
        String operator = SecurityUtils.getOperator();

        SysAlarmTagsDO sysAlarmTagsDO = new SysAlarmTagsDO();
        sysAlarmTagsDO.setTagName(addTagDTO.getTagName());
        sysAlarmTagsDO.setTenantId(currentUser.getTenantId());
        sysAlarmTagsDO.setIndustryCode(currentUser.getIndustryCode());
        sysAlarmTagsDO.setAppVerCode(currentUser.getAppVerCode());

        // 标签名不可重复 福林说需要加租户ID 判断是否重复
        int num = sysAlarmTagsRepository.countTagName(sysAlarmTagsDO);
        if (num > 0) {
            throw new CustomException(MessageConstants.ALARM_TAG_NAME_ERROR);
        }

        // 标签个数不超过20
        int countTag = sysAlarmTagsRepository.countTag(currentUser.getTenantId(), currentUser.getIndustryCode());
        if (countTag >= Constants.ALARM_TAG_MAX_NUM) {
            throw new CustomException(MessageConstants.ALARM_TAG_NUM_ERROR);
        }
        sysAlarmTagsDO.setCreator(operator);
        sysAlarmTagsDO.setCreateTime(new Date());
        sysAlarmTagsDO.setModifier(operator);
        sysAlarmTagsDO.setUpdateTime(new Date());
        sysAlarmTagsDO.setDeleteFlag(Constants.VALID_STATE);
        sysAlarmTagsRepository.save(sysAlarmTagsDO);
    }

    /**
     * 告警类型标签修改
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void modifyTag(ModifyTagDTO modifyTagDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();

        //获取操作人
        String operator = SecurityUtils.getOperator();

        SysAlarmTagsDO sysAlarmTagsDO = new SysAlarmTagsDO();
        sysAlarmTagsDO.setTagName(modifyTagDTO.getTagName());
        sysAlarmTagsDO.setTagId(modifyTagDTO.getTagId());
        // 标签名不可重复 福林说需要加租户ID 判断是否重复
        sysAlarmTagsDO.setTenantId(currentUser.getTenantId());
        sysAlarmTagsDO.setIndustryCode(currentUser.getIndustryCode());
        int num = sysAlarmTagsRepository.countTagName(sysAlarmTagsDO);
        if (num > 0) {
            throw new CustomException(MessageConstants.ALARM_TAG_NAME_ERROR);
        }

        sysAlarmTagsDO.setModifier(operator);
        sysAlarmTagsDO.setUpdateTime(new Date());
        sysAlarmTagsDO.setDeleteFlag(Constants.VALID_STATE);
        sysAlarmTagsRepository.updateById(sysAlarmTagsDO);

        // 同时修改关联标签表中的标签名
        sysAlarmTagsTypeRelRepository.modifyRelTagName(sysAlarmTagsDO);
    }

    /**
     * 告警类型标签删除
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTag(DeleteTagDTO deleteTagDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysAlarmTagsDO sysAlarmTagsDO = sysAlarmTagsRepository.getById(deleteTagDTO.getTagId());
        if (null == sysAlarmTagsDO) {
            throw new CustomException(MessageConstants.ALARM_TAG_ERROR);
        }

        // 当前标签已关联标签数量
        int relNum = sysAlarmTagsTypeRelRepository.selectTagRelCount(deleteTagDTO.getTagId(), loginUser.getUser().getTenantId());
        if (relNum >= 1) {
            throw new CustomException(MessageConstants.ALARM_TAG_REL_ERROR);
        }

        sysAlarmTagsRepository.removeById(deleteTagDTO.getTagId());
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmTagsTypeRelDTO> getAllTagsByTenantId() {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        return sysAlarmTagsRepository.getAllTagsByTenantId(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode());
    }

    /**
     * 获取告警类型为可见的告警类型标签
     *
     * @return
     * @author tianhao
     * @date 2022/11/11
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AlarmTagsTypeRelDTO> getVisibleTags() {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        String appVerCode = loginUser.getUser().getAppVerCode();
        return sysAlarmTagsRepository.getVisibleTags(tenantId, industryCode, appVerCode);
    }


}
