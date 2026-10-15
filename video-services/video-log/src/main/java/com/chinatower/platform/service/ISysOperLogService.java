package com.chinatower.platform.service;

import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.dto.SysOperLogDTO;
import com.chinatower.platform.entity.InterfaceOpeLog;
import com.chinatower.platform.entity.SysOperLogEntity;
import com.chinatower.platform.entity.log.SysOperLog;

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
    Boolean insertOperlog(SysOperLogEntity operLog);


    Boolean handleLog(InterfaceOpeLog operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    Page<SysOperLogEntity> selectOperLogList(SysOperLogDTO operLog);


}
