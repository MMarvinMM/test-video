package com.chinatower.event.mapper;

import com.chinatower.event.dataobject.FlowLinkConfig;
import com.chinatower.event.dto.FlowLinkConfigModel;
import com.chinatower.event.dto.VmFlowTemplateLinkModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:07
 */
@Mapper
public interface FlowLinkConfigMapper {
    /**
     * 新增
     * @param model
     * @return
     */
    Integer addFlowLinkConfig(FlowLinkConfigModel model);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<FlowLinkConfig> queryFlowLinkConfig(FlowLinkConfigModel model);

    /**
     * 根据ID集合查询有效记录
     * @param list
     * @return
     */
    List<FlowLinkConfig> queryById(@Param("list") List<String> list);

    /**
     * 检查流程环节名称是否唯一
     * @param model
     * @return
     */
    Integer checkLinkName(FlowLinkConfigModel model);

    /**
     * 修改
     * @param model
     * @return
     */
    Integer modifyFlowLinkConfig(FlowLinkConfigModel model);

    /**
     * 删除
     * @param linkId
     * @return
     */
    Integer deleteFlowLinkConfig(Long linkId);

    /**
     * 删除
     * @param templateId
     * @return
     */
    Integer deleteFlowLinkConfigByTempId(Long templateId);

    /**
     * 批量新增流程环节
     * 表：addFlowTemplateLink
     * @author lyy
     * @param linkList
     * @return Integer
     */
    Integer addFlowLinkConfigs(@Param("linkConfigList") List<FlowLinkConfigModel> linkConfigList);
}
