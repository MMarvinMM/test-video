package com.chinatower.event.mapper;

import java.util.List;

import com.chinatower.event.dto.VmFlowTemplateLinkModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.chinatower.event.dto.FlowTemplateModel;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 14:27
 */
@Mapper
public interface FlowTemplateMapper {
    /**
     * 新增流程模板
     * @param model
     * @return
     */
    Integer addFlowTemplate(FlowTemplateModel model);

    /**
     * 批量新增流程环节配置
     * 表：addFlowTemplateLink
     * @author lyy
     * @param linkList
     * @return Integer
     */
    Integer addFlowTemplateLink(@Param("linkList") List<VmFlowTemplateLinkModel> linkList);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<FlowTemplateModel> queryFlowTemplate(FlowTemplateModel model);

    /**
     *
     *功能描述： 根据模板名称、id校验是否有重复模板
     *
     * @param: model
     * @return: int
     * @author: zheng.yan
     * @date: 2022-06-13
     * @version:
     */
    int countSameFlowTemplate(FlowTemplateModel model);

    /**
     * 根据流程ID集合查询
     * @param flowIdList
     * @return
     */
    List<FlowTemplateModel> queryByFlowIds(@Param("list") List<Long> flowIdList);

    /**
     * 修改
     * @param model
     * @return
     */
    Integer modifyFlowTemplate(FlowTemplateModel model);

    /**
     * 根据模板ID删除流程视图模板
     * @author lyy
     * @param templateId
     * @return Integer
     */
    Integer deleteFlowTemplate(Long templateId);

    /**
     * 根据模板ID删除流程模板环节
     * @author lyy
     * @param templateId
     * @return Integer
     */
    Integer deleteFlowTemplateLink(Long templateId);
}
