package com.chinatower.bdm.modules.resource.mapper;


import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceVO;
import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源管理mapper
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Mapper
public interface ResourceConfMapper {
    
    /**
     * 新增资源
     * @author zhaol
     * @param record 资源信息，接收前端参数。
     * @return 新增数据条数
     * @throws
     * @requestID
     */
    int insertSelective(ResourceConf record);
    
    /**
     * 获取资源顶级列表(顶级结构)
     * @author zhaol
     * @param record 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID
     */
    List<ResourceConf> selectResourceList(ResourceConf record);
    
    /**
     * 修改资源
     * @author zhaol
     * @param record 资源信息
     * @return 修改资源影响的数据行数
     * @throws
     * @requestID
     */
    int updateByPrimaryKeySelective(ResourceConf record);
    
    /**
     * 是否存在资源名称相同
     * @author zhaol
     * @param  record 资源
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    int checkResourceNameUnique(ResourceConf record);
    
    /**
     * 更新资源
     * @author zhaol
     * @param record 资源信息
     * @return 更新资源影响的数据行数
     * @throws
     * @requestID
     */
    int updateByResourceId(ResourceConf record);
    
    /**
     * 根据id获取资源详情
     * @author zhaol
     * @param id 资源id
     * @return 类型详情
     * @throws
     * @requestID
     */
    ResourceConf selectByPrimaryKey(Long id);
    
    /**
     * 根据resourceId获取资源详情
     * @author zhaol
     * @param resourceId 资源信息编码
     * @return 类型详情
     * @throws
     * @requestID
     */
    ResourceConf selectByResourceId(@Param("resourceId") String resourceId, @Param("userId")String userId);
    
    /**
     * 获取租户资源集合(tree格式用)
     * @author zhaol
     * @param vo 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID       *
     */
    List<TreeSelectByBdm> getResourceTree(CommonFeignEntity vo);
    
    /**
     * 资源导入
     * @author zhaol
     * @param vo 导入参数
     * @return 导入资源影响的数据行数
     * @throws
     * @requestID
     */
    int insertResourceExport(ResourceVO vo);

    int getMateResourceType(String resourceTypeName);

    int insertResourceExportBatch(List<ResourceVO> list);
    

}