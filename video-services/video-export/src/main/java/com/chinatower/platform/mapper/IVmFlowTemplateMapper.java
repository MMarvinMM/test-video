package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.VmFlowTemplateModel;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 告警流程模板
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
@DS(TableEnum.PLATFORM)
public interface IVmFlowTemplateMapper {

    /**
     * 根据传入条件查询行业版本初始化配置模板信息，默认删除标志del_flag = 0
     * 查询条件：tenant_id、app_ver_code、industry_code、flow_type、init_source
     *
     * @param param
     * @return
     */
    FlowAlarmTemplateDto selectFlowTemplate(VmFlowTemplateModel param);

}
