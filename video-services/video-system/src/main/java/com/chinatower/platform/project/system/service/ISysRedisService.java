package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.SysRedis;
import com.chinatower.platform.project.system.domain.vo.EditRedisParam;
import com.chinatower.platform.project.system.domain.vo.SysRedisQuery;

import java.util.List;

public interface ISysRedisService {

    SysRedis detail(Long id);

    List<SysRedis> list(SysRedisQuery query);

    int add(EditRedisParam param);

}
