package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 告警流程环节行业初始化配置属性
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
@DS(TableEnum.PLATFORM)
public interface IVmFlowLinkIndustryConfigMapper {

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

}
