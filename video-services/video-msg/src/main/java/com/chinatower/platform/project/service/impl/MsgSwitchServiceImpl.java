package com.chinatower.platform.project.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import com.chinatower.platform.project.mapper.MsgSwitchMapper;
import com.chinatower.platform.project.service.MsgSwitchService;
import com.chinatower.platform.project.utils.UUIDUtils;
import com.chinatower.platform.project.vo.MsgSwitchVO;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MsgSwitchServiceImpl implements MsgSwitchService {

    @Resource
    MsgSwitchMapper msgSwitchMapper;

    public static final String UNDER_LINE = "_";

    public static final String USER_ID_STR = "user_id";
    public static final String INDUSTRY_CODE_STR = "industry_code";
    public static final String TENANT_ID_STR = "tenant_id";
    public static final String DELETE_FLAG_STR = "delete_flag";

    public static final Integer msg_switch_open = 1;

    public static final Integer delete_flag_normal = 0;

    @Override
    public SysMsgSwitch findByUser() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        QueryWrapper<SysMsgSwitch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(USER_ID_STR, user.getUserId())
                .eq(INDUSTRY_CODE_STR, user.getIndustryCode())
                .eq(DELETE_FLAG_STR, delete_flag_normal);
        SysMsgSwitch msgSwitch = msgSwitchMapper.selectOne(queryWrapper);
        log.info("开关信息:{}", msgSwitch);
        if (ObjectUtil.isEmpty(msgSwitch)) {
            SysMsgSwitch param = new SysMsgSwitch();
            param.setId(UUIDUtils.getUUID());
            param.setUserId(user.getUserId());
            param.setTenantId(user.getTenantId());
            param.setIndustryCode(user.getIndustryCode());
            param.setAlarmSwitch(msg_switch_open);
            param.setAlarmOverdueSwitch(msg_switch_open);
            param.setNoticeSwitch(msg_switch_open);
            param.setOtherSwitch(msg_switch_open);
            param.setCreateBy(user.getUserName());
            param.setCreateTime(new DateTime());
            param.setDeleteFlag(delete_flag_normal);
            msgSwitchMapper.insert(param);
            return param;
        }
        return msgSwitch;
    }

    @Override
    public void updateByUser(MsgSwitchVO msgSwitchVO) {
        SysMsgSwitch sysMsgSwitch = new SysMsgSwitch();
        if (ObjectUtil.isNotEmpty(msgSwitchVO.getAlarmOverdueSwitch())) {
            sysMsgSwitch.setAlarmOverdueSwitch(msgSwitchVO.getAlarmOverdueSwitch());
        }
        if (ObjectUtil.isNotEmpty(msgSwitchVO.getNoticeSwitch())) {
            sysMsgSwitch.setNoticeSwitch(msgSwitchVO.getNoticeSwitch());
        }
        if (ObjectUtil.isNotEmpty(msgSwitchVO.getOtherSwitch())) {
            sysMsgSwitch.setOtherSwitch(msgSwitchVO.getOtherSwitch());
        }
        if (ObjectUtil.isNotEmpty(msgSwitchVO.getAlarmSwitch())) {
            sysMsgSwitch.setAlarmSwitch(msgSwitchVO.getAlarmSwitch());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        sysMsgSwitch.setUpdateBy(user.getUserName());
        sysMsgSwitch.setUpdateTime(new DateTime());

        UpdateWrapper<SysMsgSwitch> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(USER_ID_STR, user.getUserId())
                .eq(INDUSTRY_CODE_STR, user.getIndustryCode())
                .eq(DELETE_FLAG_STR, delete_flag_normal);
        msgSwitchMapper.update(sysMsgSwitch, updateWrapper);
    }

    @Override
    public Map<String, SysMsgSwitch> queryByUserList(List<MsgSwitchRequest> userList) {
        if (userList.isEmpty()) {
            throw new CustomException("入参为null");
        }
        List<SysMsgSwitch> list = msgSwitchMapper.queryByUserList(userList);

        Map<String, SysMsgSwitch> userMap = new HashMap<>();
        Map<String, SysMsgSwitch> collect = new HashMap<>();
        if (!list.isEmpty()) {
            collect = list.stream().collect(Collectors.toMap(item -> item.getUserId() + UNDER_LINE + item.getIndustryCode(), sysMsgSwitch -> sysMsgSwitch));
        }
        for (MsgSwitchRequest item : userList) {
            String userIdIndustryCodeKey = item.getUserId() + UNDER_LINE + item.getIndustryCode();
            if (collect.containsKey(userIdIndustryCodeKey)) {
                userMap.put(userIdIndustryCodeKey, collect.get(userIdIndustryCodeKey));
            } else {
                SysMsgSwitch sysMsgSwitch = new SysMsgSwitch();
                BeanUtils.copyProperties(item, sysMsgSwitch);
                sysMsgSwitch.setNoticeSwitch(msg_switch_open);
                sysMsgSwitch.setAlarmOverdueSwitch(msg_switch_open);
                sysMsgSwitch.setOtherSwitch(msg_switch_open);
                sysMsgSwitch.setAlarmSwitch(msg_switch_open);
                userMap.put(userIdIndustryCodeKey, sysMsgSwitch);
            }
        }
        return userMap;
    }
}
