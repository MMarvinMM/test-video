package com.chinatower.bdm.modules.resource.service;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;

import java.util.List;

/**
 * 资源管理service
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
public interface IResourceConfService {

    /**
     * 根据id获取资源详情
     *
     * @author zhaol
     * @param id 资源id
     * @return 类型详情
     * @throws
     * @requestID
     */
     ResourceConf getInfo(Long id);

    /**
     * 根据resourceId获取资源详情
     * @author zhaol
     * @param resourceId 资源信息编码
     * @return 类型详情
     * @throws
     * @requestID       *
     */
     ResourceConf getResourceInfo(String resourceId);


    /**
     * 新增资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
     int insertResource(ResourceConf resourceConf);

    /**
     * 修改资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 修改结果
     * @throws
     * @requestID       *
     */
     int updateResource(ResourceConf resourceConf);

    /**
     * 删除资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 删除结果
     * @throws
     * @requestID       *
     */
     int deleteResourceById(ResourceConf resourceConf);

    /**
     * 是否存在资源名称相同
     * @param  resourceConf 资源
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    boolean checkResourceNameUnique(ResourceConf resourceConf);


    /**
     * 获取资源顶级列表(顶级结构)
     * @author zhaol
     * @param resourceConf 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID
     */
    List<ResourceConf> selectResourceList(ResourceConf resourceConf);


    /**
     * 获取租户资源集合(tree格式用)
     * @param vo 资源信息
     * @return 返回资源集合
     * @author zhaol
     * @throws
     * @requestID       *
     */
    List<TreeSelectByBdm> getResourceTree(ResourceSelectVO vo);

    /**
     * 资源导入
     * @author zhaol
     * @param params 导入参数
     * @return 结果
     * @throws
     * @requestID
     */
    List<SysImportDetail> importResource(ImportParams<ResourceVO> params);

    /**
     * 资源详情附件obs加密
     * @author wangshn
     * @param url 逗号间隔url
     * @Data 2023年1月4日17:43:55
     * @return
     * @throws
     * @requestID
     */
    String resourceImgVideo(String url);

    /**
     * 周边资源搜索
     * @param vo
     * @return
     */
    Result<List<ResourceConf>> getResourceByPeriphery(AroundAnalysisParamVO aroundAnalysisParamVO);

    Result<List<AllResourceTypeNumVO>> selectResourceForTypeCount(ResourceConf resourceConf);
}
