package com.chinatower.event.mapper;

import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.dto.FlowLinkUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 17:04
 */
@Mapper
public interface FlowLinkUserMapper {
    /**
     * 新增
     * @param model
     * @return
     */
    Integer addFlowLinkUser(FlowLinkUserModel model);

    /**
     * 批量添加
     * @param list
     * @return
     */
    Integer batchAddFlowLinkUser(@Param("list") List<FlowLinkUserModel> list);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<FlowLinkUser> queryFlowLinkUser(FlowLinkUserModel model);

    /**
     * 修改
     * @param model
     * @return
     */
    Integer modifyFlowLinkUser(FlowLinkUserModel model);

    /**
     * 删除
     * @param flowLinkId
     * @return
     */
    Integer deleteFlowLinkUser(Long flowLinkId);

    /**
     * 根据流程环节属性ID集合删除
     * @param flowLinkIdList
     * @return
     */
    Integer deleteByFlowLinkIds(List<Long> flowLinkIdList);
}
