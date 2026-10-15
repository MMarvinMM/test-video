package com.chinatower.platform.project.monitor.service;


import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.project.monitor.domain.SysOperLog;

import java.util.List;

/**
 * 操作日志 服务层
 *
 * @author chinatower
 */
public interface ISysOperLogService {
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    void insertOperlog(SysOperLog operLog);


    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    List<SysOperLog> selectOperLogList(SysOperLogDTO operLog);


    /**
     * 新增操作日志,远程调用
     *
     * @param operLog 操作日志对象
     */
    boolean saveOperlog(SysOperLog operLog);

}
