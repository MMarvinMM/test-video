package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.dataobject.TenantIotConfRelDO;
import com.chinatower.device.dataobject.TenantIotDeviceConfDO;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.device.mapper.TenantIotDeviceConfMapper;
import com.chinatower.device.repository.ITenantIotConfRelRepository;
import com.chinatower.device.repository.ITenantIotDeviceConfRepository;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物联设备联动配置表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Slf4j
@Service
public class TenantIotDeviceConfRepositoryImpl extends ServiceImpl<TenantIotDeviceConfMapper, TenantIotDeviceConfDO> implements ITenantIotDeviceConfRepository {

    @Autowired
    private ITenantIotConfRelRepository tenantIotConfRelRepository;

    @Override
    public TenantIotDeviceConfVO getTenantIotDevConf(TenantIotDeviceConfVO qryCond) {
        TenantIotDeviceConfVO result = new TenantIotDeviceConfVO();
        List<TenantIotDeviceConfVO> confs = this.baseMapper.getTenantIotDevConf(qryCond);
        if (!confs.isEmpty()) {
            result = confs.get(0);
        }
        return result;
    }

    /**
     * 批量查询物联设备数据
     * @param qryCond
     * @return
     */
    @Override
    //@DataScope(deviceColumn = "t.device_code", tenantColumn = "t.tenant_id", industryCode = "t.industry_code", alarmSourceVal = "8,10")
    public List<TenantIotDeviceConfVO> getTenantIotDevConfBatch(TenantIotDeviceConfVO qryCond) {
        return this.baseMapper.getTenantIotDevConf(qryCond);
    }

    @Override
    @DataScope(deviceColumn = "t.device_code", tenantColumn = "t.tenant_id", industryCode = "t.industry_code", alarmSourceVal = "8,10")
    public List<TenantIotDeviceConfVO> getTenantIotDevConfBatchV2(TenantIotDeviceConfVO qryCond) {
        log.info("getTenantIotDevConfBatchV2 param:{}, dataScope:{}", JSON.toJSONString(qryCond), qryCond.getDataScope());
        return this.baseMapper.getTenantIotDevConfV2(qryCond);
    }

    @Override
    @DataScope(deviceColumn = "d.device_code", tenantColumn = "t.tenant_id", industryCode = "t.industry_code", alarmSourceVal = "4")
    public List<TenantIotConfRelVO> getTenantIotRelConf(TenantIotDeviceConfVO qryCond) {
        return this.baseMapper.getTenantIotRelConf(qryCond);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveTenantIotDevConf(TenantIotDeviceConfVO tenIotDevConf) {
        TenantIotDeviceConfDO tenIotDevConfDO = new TenantIotDeviceConfDO();
        Date now = new Date();
        BeanUtils.copyProperties(tenIotDevConf,tenIotDevConfDO);
        tenIotDevConfDO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        tenIotDevConfDO.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
        if (tenIotDevConfDO.getIotDeviceConfId() == null) {
            tenIotDevConfDO.setCreateTime(now);
            tenIotDevConfDO.setCreator(SecurityUtils.getUsername());
            this.save(tenIotDevConfDO);
        }
        else {
            tenIotDevConfDO.setUpdateTime(now);
            tenIotDevConfDO.setModifier(SecurityUtils.getUsername());
            this.updateById(tenIotDevConfDO);
            // 修改时候先删再增关系
            LambdaQueryWrapper<TenantIotConfRelDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TenantIotConfRelDO::getIotDeviceConfId,tenIotDevConfDO.getIotDeviceConfId());
            tenantIotConfRelRepository.remove(queryWrapper);
        }
        if (tenIotDevConf.getConfRels() != null && !tenIotDevConf.getConfRels().isEmpty()) {
            List<TenantIotConfRelDO> rels = new ArrayList<>(tenIotDevConf.getConfRels().size());
            tenIotDevConf.getConfRels().forEach(e -> {
                TenantIotConfRelDO rel = new TenantIotConfRelDO();
                BeanUtils.copyProperties(e, rel);
                rel.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
                rel.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
                rel.setIotDeviceConfId(tenIotDevConfDO.getIotDeviceConfId());
                rel.setCreateTime(now);
                rel.setCreator(SecurityUtils.getUsername());
                rels.add(rel);
            });
            tenantIotConfRelRepository.saveBatch(rels);
        }
    }
}
