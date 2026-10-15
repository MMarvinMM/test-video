package com.chinatower.event.mapper;

import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 告警流程环节行业初始化配置属性
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
public interface VmFlowLinkIndustryConfigMapper {

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
