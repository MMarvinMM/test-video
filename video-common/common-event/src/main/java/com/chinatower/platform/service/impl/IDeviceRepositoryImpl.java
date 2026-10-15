package com.chinatower.platform.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.VmDeviceSiteVO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.VmDeviceMapper;
import com.chinatower.platform.service.VmDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:14
 */

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class IDeviceRepositoryImpl implements VmDeviceRepository {

    @Autowired
    private VmDeviceMapper vmDeviceMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getGridUserByGridId(String gridId) {
        return vmDeviceMapper.getGridUserByGridId(gridId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(List<Long> roleIdList, String tenantId, String industryCode) {
        return vmDeviceMapper.getUserInfoByLongRoleIdsAndIndustryCode(roleIdList,tenantId,industryCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<VmDeviceSiteVO> queryDeviceList(List<String> deviceCodeList) {
        return vmDeviceMapper.queryDeviceList(deviceCodeList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getUserInfoByUserIds(List<String> userIdList) {
        if (!CollectionUtils.isEmpty(userIdList)) {
            return vmDeviceMapper.getUserInfoByUserIds(userIdList);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getWarningTypeName(String warningTypeId) {
        return vmDeviceMapper.getWarningTypeName(warningTypeId);
    }
}
