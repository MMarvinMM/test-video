package com.chinatower.platform.service.impl;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.WaterLevelInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.WaterLevelMapper;
import com.chinatower.platform.service.IWaterLevelService;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelServiceImpl
 * @description: 水位球导出实现
 * @author: fengty
 * @create: 2024-12-13 16:06
 */
@Service
public class WaterLevelServiceImpl implements IWaterLevelService {

    @Autowired
    WaterLevelMapper waterLevelMapper;

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public List<WaterLevelInfo> exportWaterLevel(WaterLevelInfo waterLevelRequestVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        waterLevelRequestVO.setTenantId(user.getTenantId());
        waterLevelRequestVO.setIndustryCode(user.getIndustryCode());
        return waterLevelMapper.exportWaterLevel(waterLevelRequestVO);
    }
}
