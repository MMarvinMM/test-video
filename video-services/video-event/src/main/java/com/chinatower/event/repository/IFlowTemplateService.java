package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.FlowTemplateModel;
import com.chinatower.event.entity.dto.FlowModel;

import java.util.Map;

/**
 * 流程视图模板接口
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 14:42
 */
public interface IFlowTemplateService {
    /**
     *
     *功能描述： 添加流程模板
     *
     * @param: model
     * @return: Result<String>
     * @author: zheng.yan
     * @date: 2022-06-12
     * @version:
     */
    Result<String> addFlowTemplate(FlowTemplateModel model) throws Exception;

    /**
     * 根据条件查询流程视图模板
     * @author lyy
     * @param model
     * @return Result<Object>
     * @version v1.5
     */
    Result<Object> queryFlowTemplate(FlowTemplateModel model);

    /**
     * 查询流程及对应属性
     * @param model
     * @return Result<FlowTemplateModel>
     */
    Result<FlowTemplateModel> queryFlowAndAttr(FlowTemplateModel model);

    /**
     * 查询流程及对应属性（新）
     * @param model
     * @return Result<FlowTemplateModel>
     */
    Result<FlowTemplateModel> queryFlowAndAttrNew(FlowTemplateModel model);

    /**
     * 修改流程视图模板（旧）
     * @param model
     * @return Result<String>
     */
    Result<String> modifyFlowTemplate(FlowModel model);

    /**
     * 删除流程视图模板
     * @param flowId
     * @return Result<String>
     * @throws Exception
     */
    Result<String> deleteFlowTemplate(Long flowId) throws Exception;

    /**
     * 生成AI告警, 人工上报,一键生成,三个默认流程
     * @param model
     * @return Result<Map<String, Long>>
     * @throws Exception
     */
    Result<Map<String, Long>> genDefaultFlowInfo(FlowModel model) throws Exception;

    /**
     * 修改流程模板状态
     * @author lyy
     * @param model
     * @return Result<String>
     */
    Result<String> modifyFlowTemplateNew(FlowTemplateModel model);

}
