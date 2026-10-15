package com.chinatower.platform.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.SysImportDetail;

import java.util.List;

/**
 * 导入记录明细Mapper接口
 *
 * @author wangzic
 * @date 2020-09-07
 */
@DS(EbikeDataBaseConstant.EBIKE_EXPORT)
public interface SysImportDetailMapper extends BaseMapper<SysImportDetail> {

    int insertImportDetail(SysImportDetail sysImportDetail);


    int batchInsertImportDetail(List<SysImportDetail> list);
}
