package com.chinatower.event.mapper;

import com.chinatower.event.dto.VmFlowTemplateIndustryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 告警流程模板关联行业应用
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
public interface VmFlowTemplateIndustryMapper {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息，默认删除标志del_flag = 0
     * 查询条件：status、template_id、warning_source、app_ver_code、industry_code
     * @param param
     * @return
     */
    List<VmFlowTemplateIndustryModel> selectFlowTemplateIndustry(VmFlowTemplateIndustryModel param);
}
