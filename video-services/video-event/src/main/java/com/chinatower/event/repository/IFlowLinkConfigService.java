package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.FlowLinkConfig;
import com.chinatower.event.dto.FlowLinkConfigModel;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:20
 */
public interface IFlowLinkConfigService {
    /**
     * 新增流程环节配置
     * @param model
     * @return Result<FlowLinkConfigModel>
     */
    Result<FlowLinkConfigModel> addLinkInfo(FlowLinkConfigModel model);

    /**
     * 根据条件查询流程环节配置列表
     * @author lyy
     * @param model
     * @return Result<Object>
     */
    Result<Object> queryLinkInfo(FlowLinkConfigModel model);

    /**
     * 修改流程环节配置
     * @param model
     * @return Result<String>
     */
    Result<String> modifyLinkInfo(FlowLinkConfigModel model);

    /**
     * 删除流程环节配置
     * 表：vm_flow_link_config
     * @param linkId
     * @return Result<String>
     */
    Result<String> deleteLinkInfo(Long linkId);

    /**
     * 根据ID集合查询流程环节配置
     * @param idList
     * @return List<FlowLinkConfig>
     */
    List<FlowLinkConfig> queryById(List<String> idList);
}
