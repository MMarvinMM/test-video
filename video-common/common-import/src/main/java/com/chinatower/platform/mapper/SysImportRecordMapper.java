package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.SysImportRecord;

import java.util.List;

/**
 * 导入记录Mapper接口
 *
 * @author wangzic
 * @date 2020-09-07
 */
@DS(EbikeDataBaseConstant.EBIKE_EXPORT)
public interface SysImportRecordMapper extends BaseMapper<SysImportRecord> {

    /**
     * 查询导入记录
     *
     * @param importId 导入记录ID
     * @return 导入记录
     */
    SysImportRecord selectLatestRecordById(String importId);

    /**
     * 查询导入记录列表
     *
     * @param sysImportRecord 导入记录
     * @return 导入记录集合
     */
    List<SysImportRecord> selectImportRecordList(SysImportRecord sysImportRecord);

}
