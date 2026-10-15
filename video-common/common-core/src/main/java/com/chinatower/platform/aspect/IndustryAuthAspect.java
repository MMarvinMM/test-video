package com.chinatower.platform.aspect;

import com.chinatower.platform.annotation.IndustryAuth;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 行业应用SDK切面
 * Created by zheng.yan on 2022/3/22.
 */
@Aspect
@Component
@Slf4j
public class IndustryAuthAspect {

    @Before("@annotation(industryAuth)")
    public void doBefore(JoinPoint point, IndustryAuth industryAuth) {
        if (industryAuth == null) {
            return;
        }
        CommonFeignEntity commonEntity = (CommonFeignEntity) point.getArgs()[0];
        if (commonEntity.getUserId() == null) {
            throw new CustomException("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(commonEntity.getAppVerCode())) {
            throw new CustomException("行业版本编码不能为空", HttpStatus.BAD_REQUEST);
        }
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            throw new CustomException("获取登入信息失败", HttpStatus.BAD_REQUEST);
        }
        SysUser currentUser = loginUser.getUser();
        /* 用户类型 0铁塔 2租户 */
        String userType = currentUser.getUserType();
        // 租户
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userType)) {
            if (!currentUser.getUserId().equals(commonEntity.getUserId())) {
                throw new CustomException("登录信息有误", HttpStatus.BAD_REQUEST);
            }
            commonEntity.setTenantId(currentUser.getTenantId());
        }
    }
}
