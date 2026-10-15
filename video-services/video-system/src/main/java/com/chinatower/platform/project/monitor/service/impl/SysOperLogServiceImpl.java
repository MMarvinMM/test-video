package com.chinatower.platform.project.monitor.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;

import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.project.monitor.domain.SysOperLog;
import com.chinatower.platform.project.monitor.mongo.SysOperLogMongoMapper;
import com.chinatower.platform.project.monitor.service.ISysOperLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 操作日志 服务层处理
 *
 * @author chinatower
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysOperLogServiceImpl implements ISysOperLogService {

    private SysOperLogMongoMapper operLogMapper;


    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLog.setOperTime(new Date());
        operLog.setOperMonth(DateUtil.format(new Date(),"yyyyMM"));
        operLog.setTraceId(IdUtil.simpleUUID());
        try {
            operLogMapper.insert(operLog, MongoDBType.PRIMARY);
        }
        catch (Exception e) {
            log.error("数据插入失败",e.getMessage());
        }
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLogDTO operLog) {
//        //获取分页参数
//        Page page = new Page(operLog.getPageNo(), operLog.getPageSize());
//        //获取排序参数
//        SortEntity sortEntity = operLog.getSortEntity();
//        //获取查询参数
//        QueryEntity query = operLog.getQueryEntity();
//        page.setSortEntity(sortEntity);
//        Page<SysOperLog> pageInfo = operLogMapper.findPage(query, page, null, MongoDBType.PRIMARY);
//        return pageInfo.getList();
        return null;
    }

    @Override
    public boolean saveOperlog(SysOperLog operLog) {
        operLog.setOperTime(new Date());
        operLog.setOperMonth(DateUtil.format(new Date(),"yyyyMM"));
        try {
            operLogMapper.insert(operLog,MongoDBType.PRIMARY);
            return true;
        }
        catch (Exception e) {
            log.error("插入日志失败:{}", e.getMessage());
        }
        return false;
    }
}
