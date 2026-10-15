package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription 套合分析相关数据库查询
 * @data 创建时间：2023/8/10 21:06
 */
@Mapper
public interface LayerTaoheAnalysisLayersInsertMapper {
    @DS("postgres")
    void updateThmanageAllcount(@Param("guid") String guid, @Param("allCountJsonStr") String allCountJsonStr);

    @DS("postgres")
    List<String> selectNotDeleteTmpTableName(@Param("inString") String inString);

    @DS("postgres")
    void updateTmpTables(@Param("updateTableNames") String updateTableNames);

    @DS("postgres")
    void insertTmpMngInfo(@Param("guid") String guid, @Param("userID") String userID, @Param("newTableName") String newTableName, @Param("type") String type, @Param("overArea") double overArea);

    @DS("postgres")
    void insertXZTBTableTmp1(@Param("newTableName") String newTableName);
    @DS("postgres")
    void insertXZTBTableTmp2(@Param("newTableName") String newTableName);
    @DS("postgres")
    void insertXZTBTableTmp3(@Param("newTableName") String newTableName);
    @DS("postgres")
    void insertXZTBTableTmp4(@Param("newTableName") String newTableName);

    @DS("postgres")
    void dropTable(@Param("tableName") String tableName);
}
