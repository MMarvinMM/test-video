package com.chinatower.platform.mapper;

import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:02
 */
public interface FlowLinkIndustryConfigMapper {


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
    List<FlowModel> queryFlowLinkIndustryConfig(AlarmFlowConfigModel model);


    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLinkByFlowId(AlarmFlowConfigModel model);

    /**
     * 根据条件查询所有下一个流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryAllNextFlowLinkIndustryConfig(AlarmFlowConfigModel model);
}
