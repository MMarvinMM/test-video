package com.chinatower.emergency.service.impl;

import com.chinatower.emergency.dto.Constant;
import com.chinatower.emergency.dto.EmergencyWarningEventRelVO;
import com.chinatower.emergency.mapper.EmergencyWarningEventRelMapper;
import com.chinatower.emergency.service.EmergencyWarningEventRelService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class EmergencyWarningEventRelServiceImpl extends BaseEventInfoService implements EmergencyWarningEventRelService {

    @Autowired
    private EmergencyWarningEventRelMapper eventRelMapper;
    @Override
    public int delete(EmergencyWarningEventRelVO emergencyWarningEventRelVO) {
        return eventRelMapper.delete(emergencyWarningEventRelVO);
    }

    @Override
    public List<EmergencyWarningEventRelVO> selectByCondition(EmergencyWarningEventRelVO emergencyWarningEventRelVO) {
        return eventRelMapper.selectByCondition(emergencyWarningEventRelVO);
    }

    /**
     * 关联事件
     * @param emergencyWarningEventRelVO
     * @return
     */
    @Override
    public void connectedEvent(EmergencyWarningEventRelVO emergencyWarningEventRelVO) {
        try {
            setUserInfo(emergencyWarningEventRelVO, true);
            if (eventRelMapper.insert(emergencyWarningEventRelVO) == 0) {
                throw new RuntimeException(Constant.CONNECTED_ERROR);
            }
        } catch (Exception e) {
            log.error(Constant.CONNECTED_ERROR, e);
            throw new RuntimeException(Constant.CONNECTED_ERROR);
        }
    }

    @Override
    public boolean checkConnectedEvent(EmergencyWarningEventRelVO emergencyWarningEventRelVO) {
        try {
            List<EmergencyWarningEventRelVO> list = selectByCondition(emergencyWarningEventRelVO);
            if (CollectionUtils.isEmpty(list)) {
                return true;
            }
        } catch (Exception e) {
            log.error(Constant.CONNECTED_ERROR, e);
            throw new RuntimeException(Constant.CONNECTED_ERROR);
        }
        return false;
    }
}
