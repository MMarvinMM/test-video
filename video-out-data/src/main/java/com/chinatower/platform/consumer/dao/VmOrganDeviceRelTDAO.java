package com.chinatower.platform.consumer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.consumer.item.OrganDeviceRelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface VmOrganDeviceRelTDAO {

    /**
     * 批量新增组织与设备关系信息
     * @param dataList
     * @return
     */
    @DS("statistic")
    int batchInsert(@Param("list") List<OrganDeviceRelDTO> dataList);

    /**
     * 批量修改组织与设备关系信息
     * @param dataList
     * @return
     */
    @DS("statistic")
    int batchUpdate(@Param("list") List<OrganDeviceRelDTO> dataList);

    /**
     * 累计租户与设备关系
     * @param id
     * @return
     */
    @DS("statistic")
    int countById(@Param("id") Long id);

}
