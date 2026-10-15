package com.chinatower.bdm.modules.unionTargetJs.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnionTargetJsMapper {

    String queryDeviceAccessNodeByCode(String deviceCode);

}
