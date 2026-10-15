package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.FlowLinkUserModel;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 09:14
 */
public interface IFlowLinkUserService {
    /**
     * 新增
     * @param model
     * @return
     */
    Result addFlowLinkUser(FlowLinkUserModel model);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    Result queryFlowLinkUser(FlowLinkUserModel model);

    /**
     * 修改
     * @param model
     * @return
     */
    Result modifyFlowLinkUser(FlowLinkUserModel model);

    /**
     * 删除
     * @param linkUserId
     * @return
     */
    Result deleteFlowLinkUser(Long linkUserId);
}
