package com.chinatower.manage.repository.impl;

import com.chinatower.manage.mapper.SysIndustryViewCompoRelMapper;
import com.chinatower.manage.repository.ISysIndustryViewCompoRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Description 大屏组件关联表服务
 * @author peng.haibo
 * @date 2022/7/25
 */
@Slf4j
@Service
public class SysIndustryViewCompoRelServiceImpl implements ISysIndustryViewCompoRelService {
    @Autowired
    private SysIndustryViewCompoRelMapper sysIndustryViewCompoRelMapper;


}
