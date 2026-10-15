package com.chinatower.platform.modules.screen.dao;

import com.chinatower.platform.modules.screen.entity.ThirdPartyScreenConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThirdPartyScreenConfMapper {

    List<ThirdPartyScreenConf> selectSdshbtScreenConfigs();

    int updateScreenUrl(@Param("id") Long id, @Param("screenUrl") String screenUrl);
}
