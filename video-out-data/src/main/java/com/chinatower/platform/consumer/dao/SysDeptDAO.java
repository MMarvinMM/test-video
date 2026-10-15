package com.chinatower.platform.consumer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.consumer.item.SysDeptDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SysDeptDAO {

    /**
     * 批量新增部门信息
     * @param sysDeptDTOList
     * @return
     */
    @DS("statistic")
    int batchInsert(@Param("list") List<SysDeptDTO> sysDeptDTOList);

    /**
     * 批量修改部门信息
     * @param dataList
     * @return
     */
    @DS("statistic")
    int batchUpdate(@Param("list") List<SysDeptDTO> dataList);

    /**
     * 获取部门数量
     * @param id
     * @return
     */
    @DS("statistic")
    int countById(@Param("id") Long id);

}
