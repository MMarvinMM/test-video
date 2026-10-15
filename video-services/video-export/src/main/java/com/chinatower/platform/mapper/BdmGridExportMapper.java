package com.chinatower.platform.mapper;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BdmGridExportMapper {

    List<GridExportVO> getGridExportList(GridParam vo);

}
