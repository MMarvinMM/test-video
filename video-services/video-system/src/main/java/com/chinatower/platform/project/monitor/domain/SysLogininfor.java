package com.chinatower.platform.project.monitor.domain;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.annotation.Excel;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author iwhalecloud
 */
@Data
public class SysLogininfor implements Serializable
{
    /** ID */
    private String infoId;

    /** 用户账号 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录状态 0成功 1失败 */
    private String status;

    /** 提示消息 */
    private String msg;

    /** 登录地点 */
    private String loginLocation;

    /** 访问时间 */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
}
