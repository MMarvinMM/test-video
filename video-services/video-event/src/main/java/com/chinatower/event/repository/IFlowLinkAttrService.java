package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.event.entity.vo.SysInfoVo;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:48
 */
public interface IFlowLinkAttrService {
    /**
     * 新增
     * @param model
     * @return
     */
    Result<FlowLinkAttrModel> addFlowLinkAttr(FlowLinkAttrModel model);

    /**
     * 功能描述：根据条件查询流程环节属性
     * @params [model]
     * @return com.chinatower.common.entity.Result<com.chinatower.event.dto.FlowLinkAttrModel>
     * @author su.tingning
     * @date 2022/6/10 15:57
     */
    Result<FlowLinkAttrModel> queryFlowLinkAttr(FlowLinkAttrModel model);

    /**
     * 功能描述：根据条件查询流程环节属性
     * @params [model]
     * @return com.chinatower.common.entity.Result<com.chinatower.event.dto.FlowLinkAttrModel>
     * @author su.tingning
     * @date 2022/6/10 15:57
     */
    Result<FlowLinkAttrModel> queryFlowLinkAttrNew(FlowLinkAttrModel model);

    /**
     * 修改
     * @param model
     * @return
     */
    Result<String> modifyFlowLinkAttr(FlowLinkAttrModel model);

    /**
     * 查询下一步环节信息
     * @param model
     * @return
     */
    Result<List<FlowLinkAttrModel>> queryNextLink(FlowLinkAttrModel model);

    /**
     * 查询下一步环节信息
     * @param model
     * @return
     */
    Result<List<FlowLinkAttrModel>> queryNextLinkNew(FlowLinkAttrModel model);

    /**
     * 查询可转派环节信息
     * @param model
     * @return
     */
    Result<List<FlowLinkAttrModel>> queryReassignLinkNew(FlowLinkAttrModel model);

    /**
     * 获取用户树
     * @param rootDeptIds
     * @param deptIds
     * @param roleIds
     * @return
     */
    Result<SysInfo> getUserTree(String rootDeptIds, String deptIds, String roleIds);
    
    /**
     * 根据组织ID获取用户所属角色列表
     * @param
     * @return
     */
    Result<List<SysInfo>> queryRoles(String rootDeptIds,String deptIds);

    /**
     * 根据租户ID，流程Id查询用户树(非行业使用)
     * @param model
     * @return
     */
    Result getFlowByIdUserTree(FlowLinkAttrModel model);

    /**
     * 根据租户ID，流程Id查询用户树(非行业使用)
     * @param model
     * @return
     */
    Result getAppFlowByIdUserTree(FlowLinkAttrModel model);

    /**
     * 根据租户ID，流程Id查询用户树（行业使用）
     * @param model
     * @return
     */
    Result<SysInfoVo> getFlowByIdUserTreeNew(FlowLinkAttrModel model);

    /**
     * 根据租户ID，流程Id查询用户树(非行业使用)
     * @param model
     * @return
     */
    Result getOrderInfoUserTree(OrderInfoUserTreeReq model);

}
