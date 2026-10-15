package com.chinatower.device.aspect;

import com.chinatower.device.annotation.DeviceOrdAuthScope;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceSouthCommonDTO;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备订购判断切面
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/16
 * @version:
 */
@Aspect
@Component
public class DeviceOrdAuthAspect {

    @Autowired
    DeviceInfoMapper deviceInfoMapper;

    @Before("@annotation(deviceOrdAuthScope)")
    public void doBefore(JoinPoint point, DeviceOrdAuthScope deviceOrdAuthScope) {
        handleDataScope(point, deviceOrdAuthScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DeviceOrdAuthScope deviceOrdAuthScope) {
        if (deviceOrdAuthScope == null) {
            return;
        }
        DeviceSouthCommonDTO commonDTO = (DeviceSouthCommonDTO) joinPoint.getArgs()[0];
        String userType = VideoSystemEnums.UserType.TENANT_USER.getIndex();
        String tenantId = "";
        String userName = "";
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            tenantId = commonDTO.getTenantId();
        }
        else {
            SysUser currentUser = loginUser.getUser();
            /* 用户类型 0铁塔 2租户 */
            userType = currentUser.getUserType();
            commonDTO.setUserCode(currentUser.getUserName());
            tenantId = currentUser.getTenantId();
            userName = currentUser.getUserName();
        }

        //租户订购设备信息添加
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userType)) {
            commonDTO.setTenantId(tenantId);
            List<String> orderCodes = deviceInfoMapper.listDeviceOrderCode(commonDTO.getChannelCode(), tenantId);
            if (orderCodes.size() > 0) {
                commonDTO.setOrderCode(orderCodes.get(0));
            }
        }
        else {
            commonDTO.setTowerCode(userName);
        }
    }
}