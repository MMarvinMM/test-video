package com.chinatower.platform.service;

import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.dto.SysLogininfoDTO;
import com.chinatower.platform.entity.SysLogininfoEntity;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author chinatower
 */
public interface ISysLogininfoService
{
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfoEntity logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public Page<SysLogininfoEntity> selectLogininforList(SysLogininfoDTO logininfor);

    /**
     * 统计登录次数
     * @param logininfor
     * @return
     */
    public Long countLogininfor(SysLogininfoDTO logininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    public int deleteLogininforByIds(String[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();
}
