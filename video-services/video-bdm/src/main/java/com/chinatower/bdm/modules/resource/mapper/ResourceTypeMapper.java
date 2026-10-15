package com.chinatower.bdm.modules.resource.mapper;

import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 资源类型管理mapper
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Mapper
public interface ResourceTypeMapper {
    
    /**
     * 更新资源类型
     * @param record 资源类型
     * @return 更新影响数据条数
     * @throws
     * @requestID
     */
    int updateByResourceTypeId(ResourceType record);
    
    /**
     * 增加资源类型
     * @param record 资源类型
     * @return 增加影响数据条数
     * @throws
     * @requestID
     */
    int insertSelective(ResourceType record);
    
    /**
     * 根据id获取资源类型详情
     * @param id 资源类型id
     * @return 结果
     * @throws
     * @requestID
     */
    ResourceType selectByPrimaryKey(Long id);
    
    /**
     * 根据id获取资源类型详情
     * @param resourceTypeId 资源类型id
     * @return 结果
     * @throws
     * @requestID
     */
    ResourceType selectByTypeId(String resourceTypeId);
    
    /**
     * 更新资源类型
     * @param record 资源类型
     * @return 更新影响数据条数
     * @throws
     * @requestID
     */
    int updateByPrimaryKeySelective(ResourceType record);
    
    /**
     * 查询资源类型
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    List<ResourceType> selectResourceTypeList(ResourceType record);
    
    /**
     * 查询资源类型
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    List<ResourceType> queryResourceTypeList(ResourceType record);
    List<ResourceType> selectResourceTypeId(ResourceType record);
    
    /**
     * 是否存在资源类型子节点
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    int hasChildByResourceTypeId(ResourceType record);
    
    /**
     * 是否存在资源类型名称相同
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    int checkResourceTypeNameUnique(ResourceType record);
    
    /**
     * 是否存在资源
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    int checkResourceUnique(ResourceType record);
    
    /**
     * 获取初始化数据
     * @param resourceTypeInt 资源类型初始化信息
     * @return 资源类型初始化数据结果
     * @throws
     * @requestID
     */
    List<ResourceTypeInt> getResourceTypeInt(ResourceTypeInt resourceTypeInt);
    
    /**
     * 增加资源类型
     * @param list 资源类型
     * @return 新增资源数据条数
     * @throws
     * @requestID
     */
    int insertResourceTypeInitData(List<ResourceTypeInt> list);


    /**
     * 查询资源类型
     * @param record 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    List<ResourceType> selectResourceTypeListNew(ResourceType record);


    List<ResourceType> selectResourceTypeList1New(List<ResourceType> list,@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("resourceProperty") String resourceProperty,@Param("appVerCode") String appVerCode);

}