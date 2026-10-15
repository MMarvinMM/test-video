package com.chinatower.platform.auth.project.service;


import com.chinatower.platform.entity.admin.SysLogininfor;

import java.util.List;


/**
 * 系统访问日志情况信息 服务层
 *
 * @author bruce
 */
public interface ISysLogininforService {
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

}
