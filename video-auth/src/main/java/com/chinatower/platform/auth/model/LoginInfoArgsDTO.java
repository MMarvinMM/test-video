package com.chinatower.platform.auth.model;

import com.chinatower.common.annotation.Excel;
import lombok.Builder;
import lombok.Data;

/**
 * @desription: 登录日志扩展参数
 * @data 创建时间：2023/3/8 10:25
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class LoginInfoArgsDTO {

    /**
     * 手机号
     */
    private String  phonenumber;
    /**
     * 用户类型0铁塔 2-客户
     */
    private String  userType;

    /**
     * 租户ID
     */
    private String  tenantId;

    /**
     * 部门ID
     */
    private String  deptId;

    /**
     * 版本编码
     */
    private String  industryCode;
    /**
     * 用户昵称
     */
    private String nickName;
}
