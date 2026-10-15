package com.chinatower.bdm.modules.resource.service;

import com.chinatower.bdm.api.entity.ResourceTreeSelect;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.common.entity.Result;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
/**
 * 资源类型管理service
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Mapper
public interface IResourceTypeService {
    
    /**
     * 获取资源类型顶级列表(顶级结构)
     * @author zhaol
     * @param resourceType 资源类型信息
     * @return 返回资源类型集合
     * @throws
     * @requestID
     */
    List<ResourceType> selectResourceTypeList(ResourceType resourceType);

    /**
     * 构建前端所需要树结构
     * @author zhaol
     * @param list 资源类型列表
     * @return 树结构列表
     * @throws
     * @requestID
     */
     List<ResourceType> buildResourceTypeTree(List<ResourceType> list);

    /**
     * 构建前端所需要树结构
     * @author zhaol
     * @param list 资源类型列表
     * @return 树结构列表
     * @throws
     * @requestID
     */
     List<ResourceTreeSelect> buildResourceTypeTreeSelect(List<ResourceType> list);
     
    /**
     * 根据id获取资源类型详情
     * @author zhaol
     * @param id 资源类型id
     * @return 类型详情
     * @throws
     * @requestID
     */
     ResourceType getInfo(Long id);

    /**
     * 新增资源类型
     * @param resourceType 资源类型
     * @return 类型详情
     * @throws
     * @requestID
     */
     int insertResourceType(ResourceType resourceType);

    /**
     * 更新资源类型
     * @param resourceType 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
     int updateResourceType(ResourceType resourceType);
    
    /**
     * 删除资源类型
     * @param resourceType 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
     int deleteResourceTypeById(ResourceType resourceType);

    /**
     * 是否存在资源类型子节点
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
     boolean hasChildByResourceTypeId(ResourceType resourceType);
     
    /**
     * 是否存在资源类型名称相同
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
     boolean checkResourceTypeNameUnique(ResourceType resourceType);

    /**
     * 是否存在资源
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
     boolean checkResourceUnique(ResourceType resourceType);

    /**
     * 新增保存菜单信息
     * @param resourceTypeInt 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    Result initResourceType(ResourceTypeInt resourceTypeInt);


    /**
     * 获取资源类型顶级列表(顶级结构),下拉框优化
     * @author zhaol
     * @param resourceType 资源类型信息
     * @return 返回资源类型集合
     * @throws
     * @requestID
     */
    List<ResourceType> selectResourceTypeListNew(ResourceType resourceType);

}
