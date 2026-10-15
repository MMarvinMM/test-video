package com.chinatower.device.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.device.enums.DeviceCommonEnum;
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceConstants;
import org.springframework.stereotype.Component;

/**
 * 设备接口工厂类
 */
@Component
public class DeviceServiceFactory {

    public IDeviceCommonService getBean(int deviceType) {
        String beanName = DeviceCommonEnum.getBeanName(deviceType);
        if (StringUtils.isBlank(beanName)) {
            throw new CustomException(DeviceConstants.QUERY_DEVICE_TYPE_ERROR);
        }
        return SpringUtil.getBean(beanName);
    }
}
