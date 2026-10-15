package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysAreaStreet;
import com.chinatower.system.api.model.StreetTreeVO;
import com.chinatower.system.api.model.SysArea;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper接口
 *
 * @date 2022-03-18
 */
@Mapper
public interface SysAreaStreetMapper
{

    List<SysAreaStreet> selectSysAreaStreetListByParentCode(String areaParentCode);

    List<StreetTreeVO> getAllStreetList(StreetTreeVO vo);

}
