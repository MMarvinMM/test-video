package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.SubScenesBackfillRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 历史 sub_scenes 刷数 DAO：按月表游标批量扫描 + 幂等更新。
 */
@Mapper
public interface SubScenesBackfillMapper {

    /**
     * 按 warning_order_id 升序游标扫描未删除行。
     *
     * @param tableName 月表名（调用方已白名单校验）
     * @param lastId    上一批最大 warning_order_id；空串表示从头扫
     * @param batchSize 批大小
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<SubScenesBackfillRow> selectBatch(@Param("tableName") String tableName,
                                           @Param("lastId") String lastId,
                                           @Param("batchSize") int batchSize);

    /**
     * 幂等更新：仅当 sub_scenes 为空或与目标值不同时写入。
     *
     * @return 影响行数（0 表示已是目标值）
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateSubScenes(@Param("tableName") String tableName,
                        @Param("warningOrderId") String warningOrderId,
                        @Param("subScenes") String subScenes);
}
