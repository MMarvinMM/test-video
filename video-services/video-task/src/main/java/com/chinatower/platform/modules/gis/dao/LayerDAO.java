package com.chinatower.platform.modules.gis.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LayerDAO {

//    @DS("base")
//    void executeSql(@Param("executeSql") String executeSql);

//    @DS("base")
//    List<String> selectSql(@Param("selectSql") String selectSql);

//    @DS("base")
//    void insertSql(@Param("insertSql") String insertSql);

    /**
     * 备份表sys_layer_type
     * @param typetableback
     */
    @DS("base")
    void executeTypetableback(@Param("typetableback") String typetableback);

    /**
     * 备份表sys_layer_type_fields
     * @param typefieldstableback
     */
    @DS("base")
    void executeTypefieldstableback(@Param("typefieldstableback") String typefieldstableback);

    /**
     * 查询图层类型
     * @return
     */
    @DS("base")
    List<String> selectAbbreviation();

    @DS("base")
    void insertAbbreviation1(@Param("abbs") List<String> abbs);
    @DS("base")
    void insertAbbreviation2(@Param("abbs") List<String> abbs);
    @DS("base")
    void insertAbbreviation3(@Param("abbs") List<String> abbs);
    @DS("base")
    void insertAbbreviation4(@Param("abbs") List<String> abbs);
    @DS("base")
    void insertAbbreviation5(@Param("abbs") List<String> abbs);
    @DS("base")
    void insertAbbreviation6(@Param("abbs") List<String> abbs);
}
