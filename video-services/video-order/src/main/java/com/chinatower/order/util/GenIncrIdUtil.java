package com.chinatower.order.util;

import com.chinatower.order.mapper.SysTenantInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenIncrIdUtil {

    @Autowired
    private SysTenantInfoMapper sysTenantInfoMapper;

    public String getIncrIdByDB(String prefix) {
        String maxId = sysTenantInfoMapper.getMaxId(prefix);
        int value;
        if (NumberUtils.isNumber(maxId)) {
            value = NumberUtils.toInt(maxId)+1;
        } else {
            value = 1;
        }
        return prefix + String.format("%07d", value);
    }

}
