package com.chinatower.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.dto.SysLogininfoDTO;
import com.chinatower.platform.entity.SysLogininfoEntity;
import com.chinatower.platform.mongo.SysLogininfoMongoMapper;
import com.chinatower.platform.service.ISysLogininfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author chinatower
 */
@Service
public class SysLogininfoServiceImpl implements ISysLogininfoService
{

    @Autowired
    private SysLogininfoMongoMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfoEntity logininfor)
    {
        logininfor.setLoginTime(new Date());
        logininfor.setMsg(StrUtil.sub(logininfor.getMsg(),0,200));
        logininforMapper.insert(logininfor, MongoDBType.PRIMARY);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public Page<SysLogininfoEntity> selectLogininforList(SysLogininfoDTO logininfor)
    {
        //获取分页参数
        Page page = new Page(logininfor.getPageNo(), logininfor.getPageSize());
        //获取排序参数
        SortEntity sortEntity = logininfor.getSortEntity();
        //获取查询参数
        QueryEntity query = logininfor.getQueryEntity();
        page.setSortEntity(sortEntity);
        Page<SysLogininfoEntity> pageInfo = logininforMapper.findPage(query, page, null, MongoDBType.PRIMARY);
        return pageInfo;
    }

    @Override
    public Long countLogininfor(SysLogininfoDTO logininfor) {
        QueryEntity query = logininfor.getQueryEntity();
        return logininforMapper.count(query,MongoDBType.PRIMARY);
    }


    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @Override
    public int deleteLogininforByIds(String[] infoIds)
    {
        QueryEntity query = new QueryEntity(SysLogininfoEntity.TABLE_NAME,SysLogininfoEntity.class);
        query.in("_id",infoIds);
        return logininforMapper.remove(query,MongoDBType.PRIMARY).intValue();
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        QueryEntity query = new QueryEntity(SysLogininfoEntity.TABLE_NAME, SysLogininfoEntity.class);
        logininforMapper.remove(query,MongoDBType.PRIMARY);
    }
}
