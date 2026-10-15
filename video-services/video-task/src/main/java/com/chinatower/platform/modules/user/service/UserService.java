package com.chinatower.platform.modules.user.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.modules.user.dao.UserDAO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 用户定时
 */
@Component
@Slf4j
public class UserService {
    @Resource
    private UserDAO userDAO;

    /**
     * 用户停用定时（到了账号有效期的铁塔用户）
     */
    @XxlJob("uptUserStatusHandler")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> uptUserStatusHandler(String param) {
        log.info("用户停用定时（到了账号有效期的铁塔用户）");
        XxlJobLogger.log("用户停用定时（到了账号有效期的铁塔用户）");
        userDAO.updateUserStatus();
        return ReturnT.SUCCESS;
    }
}
