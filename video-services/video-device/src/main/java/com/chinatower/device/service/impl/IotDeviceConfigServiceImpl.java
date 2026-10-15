package com.chinatower.device.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.dataobject.VmDeviceIotRelDO;
import com.chinatower.device.dto.IotDeviceConfigVO;
import com.chinatower.device.mapper.IotDeviceConfigMapper;
import com.chinatower.device.service.IotDeviceConfigService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * @author lzt
 */
@Slf4j
@Service
public class IotDeviceConfigServiceImpl extends ServiceImpl<IotDeviceConfigMapper, VmDeviceIotRelDO> implements IotDeviceConfigService {

    @Resource
    private IotDeviceConfigMapper iotDeviceConfigMapper;

    @Override
    @DataScope(deviceColumn="t1.device_code",tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code")
    public List<IotDeviceConfigVO> queryIotDeviceConfigList(IotDeviceConfigVO iotDeviceConfigVO) {
        PageHelper.startPage(iotDeviceConfigVO.getPageNum(), iotDeviceConfigVO.getPageSize());
        List<IotDeviceConfigVO> list = iotDeviceConfigMapper.queryIotDeviceConfigList(iotDeviceConfigVO);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO) {
        //如果绑定过设备数据（设备退订了重新绑定），把以前数据更新解绑，并delete=1状态
        SysUser user = SecurityUtils.getLoginUser().getUser();
        iotDeviceConfigVO.setTenantId(user.getTenantId());
        iotDeviceConfigVO.setIndustryCode(user.getIndustryCode());
        int num = iotDeviceConfigMapper.getIotDeviceConfigNum(iotDeviceConfigVO);
        if(num > 0){
            iotDeviceConfigMapper.updateIotDeviceConfig(iotDeviceConfigVO);
        }
        // 如果是解绑重新绑定更新状态
        int sNum = iotDeviceConfigMapper.getSecondIotDeviceConfig(iotDeviceConfigVO);
        if(sNum > 0){
            // 更新绑定状态
            UpdateWrapper<VmDeviceIotRelDO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("device_code",iotDeviceConfigVO.getDeviceCode());
            updateWrapper.eq("delete_flag",0);
            updateWrapper.eq("industry_code",user.getIndustryCode());
            updateWrapper.eq("tenant_id",user.getTenantId());
            VmDeviceIotRelDO relDO = new VmDeviceIotRelDO();
            relDO.setIotDeviceCode(iotDeviceConfigVO.getIotDeviceCode());
            relDO.setBindFlag((byte) 0);
            iotDeviceConfigMapper.update(relDO,updateWrapper);
        }else{
            //新增物联设备绑定关系
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            VmDeviceIotRelDO relDO = new VmDeviceIotRelDO();
            relDO.setIotDeviceCode(iotDeviceConfigVO.getIotDeviceCode());
            relDO.setDeviceCode(iotDeviceConfigVO.getDeviceCode());
            relDO.setTenantId(sysUser.getTenantId());
            relDO.setIndustryCode(sysUser.getIndustryCode());
            relDO.setAppVerCode(sysUser.getAppVerCode());
            relDO.setCreateTime(new Date());
            relDO.setCreator(sysUser.getUserName());
            iotDeviceConfigMapper.insert(relDO);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO) {
        VmDeviceIotRelDO relDO = new VmDeviceIotRelDO();
        relDO.setId(iotDeviceConfigVO.getId());
        relDO.setBindFlag((byte) 1);
        iotDeviceConfigMapper.updateById(relDO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeIotDeviceConfig(IotDeviceConfigVO iotDeviceConfigVO) {
        SysUser user =  SecurityUtils.getLoginUser().getUser();
        UpdateWrapper<VmDeviceIotRelDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("device_code",iotDeviceConfigVO.getDeviceCode());
        updateWrapper.eq("delete_flag",0);
        updateWrapper.eq("bind_flag",0);
        updateWrapper.eq("industry_code",user.getIndustryCode());
        updateWrapper.eq("tenant_id",user.getTenantId());
        VmDeviceIotRelDO relDO = new VmDeviceIotRelDO();
        relDO.setIotDeviceCode(iotDeviceConfigVO.getIotDeviceCode());
        iotDeviceConfigMapper.update(relDO,updateWrapper);
    }

}
