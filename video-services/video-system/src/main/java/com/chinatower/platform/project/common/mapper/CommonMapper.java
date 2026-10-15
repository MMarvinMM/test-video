package com.chinatower.platform.project.common.mapper;

import com.chinatower.device.entity.dto.UserCommonCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WANGZIC
 */
@Mapper
public interface CommonMapper {

    /**
     * 获取序列id
     * @param name 序列名
     * @return 序列id
     */
    String getSeqByName(@Param("name") String name);

    List<String> getSeqByNameList(@Param("name") String name,@Param("list") List<String> list);

    int isExistTable(@Param("tableName") String tableName);

    List<String> getOldCollection(UserCommonCollection userCommonCollection);

    void insertBatchCollection(List<UserCommonCollection> insertList);

    void removeCollection(UserCommonCollection commonCollection);

}
