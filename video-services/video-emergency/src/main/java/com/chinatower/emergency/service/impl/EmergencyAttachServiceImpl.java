package com.chinatower.emergency.service.impl;

import com.chinatower.emergency.dto.EmergencyAttachVO;
import com.chinatower.emergency.mapper.EmergencyAttachMapper;
import com.chinatower.emergency.service.EmergencyAttachService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @program: video-platform
 * @ClassName: EmergencyAttachServiceImpl
 * @description: 应急相关的服务接口实现
 * @author: fengty
 * @create: 2024-10-31 16:02
 */
@Service
public class EmergencyAttachServiceImpl extends BaseEventInfoService implements EmergencyAttachService {


    @Autowired
    private EmergencyAttachMapper emergencyAttachMapper;

    @Override
    public Map<Integer, List<EmergencyAttachVO>>  getAllEmergencyAttaches(EmergencyAttachVO emergencyAttach) {
        List<EmergencyAttachVO> list = emergencyAttachMapper.selectAllEmergencyAttaches(emergencyAttach);
        if(list != null && list.size() > 0) {
            return list.stream().collect(groupingBy(EmergencyAttachVO::getRecordType));
        }
        return null;
    }

    @Override
    public void addEmergencyAttach(Map<Integer, List<EmergencyAttachVO>>  map, String businessCode, Integer businessType) {
        List<EmergencyAttachVO> paramForSave = new ArrayList<>();
        for (Integer s : map.keySet()) {
            List<EmergencyAttachVO> emergencyAttachList = map.get(s);
            if (CollectionUtils.isEmpty(emergencyAttachList)) {
                continue;
            }
            for(EmergencyAttachVO vo : emergencyAttachList) {
                SysUser user = SecurityUtils.getLoginUser().getUser();
                vo.setRecordType(s);
                vo.setBusinessCode(businessCode);
                vo.setBusinessType(businessType);
                vo.setCreator(user.getUserName());
            }
            paramForSave.addAll(emergencyAttachList);
        }
        if(CollectionUtils.isEmpty(paramForSave)) {
            return;
        }
        emergencyAttachMapper.insertEmergencyAttach(paramForSave);
    }

    @Override
    public void deleteEmergencyAttach(EmergencyAttachVO emergencyAttach) {
        emergencyAttachMapper.deleteEmergencyAttach(emergencyAttach);
    }
}
