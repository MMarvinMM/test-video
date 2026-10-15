package com.chinatower.platform.project.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceInfoAuthMapper  {

    List<String> queryHisAuthList(String userId);

}
