package com.chinatower.order.mapper;

import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源初始化mapper
 *
 * @return
 * @author chenmf
 * @date 2022/6/13
 * @version
 */
@Mapper
public interface ResourceTypeMapper {

    /**
     * 查询资源类型初始化数据
     * @param: resourceTypeInt
     * @return java.util.List<com.chinatower.bdm.api.entity.ResourceTypeInt>
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    List<ResourceTypeInt> getResourceTypeInt(@Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode);

    /**
     * 查询资源类型数据
     * @param: resourceType
     * @return java.util.List<com.chinatower.bdm.api.entity.ResourceType>
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    List<ResourceType> selectResourceTypeList(ResourceType resourceType);

    /**
     * 新增资源类型数据
     * @param: intList
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void insertResourceTypeInitData(List<ResourceTypeInt> intList);
}
