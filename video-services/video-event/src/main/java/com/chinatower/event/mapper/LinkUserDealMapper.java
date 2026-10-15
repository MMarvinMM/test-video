package com.chinatower.event.mapper;

import com.chinatower.event.dataobject.LinkUserDeal;
import com.chinatower.event.dto.LinkUserDealModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程环节用户处理Mapper
 * @author chmod
 * @version 1.0
 * @date 2022/2/14 11:28
 */
@Mapper
public interface LinkUserDealMapper {
    /**
     * 批量新增流程环节用户处理
     * @param list
     * @return
     */
    Integer addLinkUserDeal(@Param("list") List<LinkUserDealModel> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据条件查询流程环节用户处理
     * @param model
     * @return
     */
    List<LinkUserDeal> queryLinkUserDeal(LinkUserDealModel model);

    /**
     * 修改流程环节用户处理
     * @param model
     * @return
     */
    Integer modifyLinkUserDeal(LinkUserDealModel model);

    /**
     * 检查表是否存在
     * @param tableSchema
     * @param tableName
     * @return
     */
    Integer checkTableExist(@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

    /**
     * 创建流程环节用户处理表
     * @param tableSuffix
     */
    void createTable(@Param("tableSuffix") String tableSuffix);

    /**
     * 创建表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

}
