package com.chinatower.receive.mapper;

import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 18:44
 */
@Mapper
public interface IVmFlowLinkIndustryConfigMapper {

    /**
     * 查询流程环节行业初始化属性配置
     * 根据 行业编码、行业应用编码、环节id查询属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkIndustryConfigByCondition(VmFlowLinkAttrDto flowLinkAttrDto);

    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLinkByFlowId(AlarmFlowConfigModel model);


    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLinkIndustryConfig(AlarmFlowConfigModel model);
}
