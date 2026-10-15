package com.chinatower.device.entity.dto;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.platform.common.utils.security.RsaUtils;
import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/5/20 9:48
 */
@Data
public class AirPwdReq extends PageDTO {
    private String deviceCode;
    private String pwdStr;
    private String tenantId;

    public String getPassword() {
        try {
            if (StrUtil.isNotEmpty(pwdStr)) {
                return RsaUtils.decryptByPrivateKey(pwdStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pwdStr;
    }
}
