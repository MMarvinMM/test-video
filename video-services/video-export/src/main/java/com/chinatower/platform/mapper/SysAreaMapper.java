package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.admin.SysArea;

import java.util.List;

/**
 * @Entity com.chinatower.platform.entity.SysArea
 */
public interface SysAreaMapper extends BaseMapper<SysArea> {

    List<SysArea> selectSysAreaList(SysArea sysArea);
}




