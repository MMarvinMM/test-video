package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 告警流程环节属性
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Mapper
@DS(TableEnum.PLATFORM)
public interface IVmFlowLinkAttrMapper {

    /**
     * 功能说明：根据用户id查环节信息
     *
     * @author: zhang.xionghui
     * @param: userId
     * @return:
     * @date: 2022/8/10 14:54
     * @requestID  需求单号及需求名称
     */
    List<VmFlowLinkAttrDto> getFlowLinkByUserId(@Param("userId") String userId);

    /**
     * @description: 根据角色id查询 环节信息
     * @author: yongqi.han
     * @date: 2022/10/25
     * @param roleParam
     * @return: List<VmFlowLinkAttrDto>
     */
    List<VmFlowLinkAttrDto> getFlowLinkByRoleIdList(@Param("param") Map roleParam);
}
