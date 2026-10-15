package com.chinatower.platform.auth.framework.manager.factory;

import com.chinatower.common.utils.ServletUtils;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.project.service.ISysLogininforService;
import com.chinatower.platform.common.utils.LogUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.ip.IpUtils;
import com.chinatower.platform.common.utils.spring.SpringUtils;
import com.chinatower.platform.entity.admin.SysLogininfor;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author chinatower
 */
public class AsyncFactory {
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登陆信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message, final String clientType,
                                             final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        final String clientVersion = ServletUtils.getRequest().getHeader("client_version");
        final String clientModel = ServletUtils.getRequest().getHeader("client_model");
        final String clientOs = ServletUtils.getRequest().getHeader("client_os");
        final String windowsOs = ServletUtils.getRequest().getHeader("windows_os");
        return new TimerTask() {
            @Override
            public void run() {
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(IpUtils.getClientInfo());
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                s.append(LogUtils.getBlock(clientModel));
                s.append(LogUtils.getBlock(clientOs));
                s.append(LogUtils.getBlock(clientVersion));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setLoginTime(new Date());//增加登录时间
                logininfor.setUserName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(IpUtils.getClientInfo());
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                logininfor.setClientType(clientType);
                if (userAgent.getBrowserVersion() != null) {
                    logininfor.setBrowserVersion(userAgent.getBrowserVersion().getVersion());
                }
                // 日志状态
                if (OauthConstants.LOGIN_SUCCESS.equals(status) || OauthConstants.LOGOUT.equals(status)) {
                    logininfor.setStatus(OauthConstants.SUCCESS);
                } else if (OauthConstants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(OauthConstants.FAIL);
                }
                logininfor.setPhoneModel(clientModel);
                logininfor.setAppVersion(clientVersion);
                if (StringUtils.isNotEmpty(clientOs)) {
                    String[] osArr = clientOs.trim().split(" ");
                    logininfor.setOs(osArr[0]);
                    logininfor.setOsVersion(osArr.length > 1 ? osArr[1] : "");
                }
                if (StringUtils.isNotEmpty(windowsOs)){
                    logininfor.setOs(windowsOs);
                }
                // 插入数据
                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    /*public static TimerTask recordOper(final SysOperLog operLog) {
     *//* return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(IpUtils.getClientInfo());
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };*//*
    }*/
}
