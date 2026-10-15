package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
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
public interface IFlowLinkAttrMapper {

    /**
     * 查询流程环节属性配置
     * @param flowLinkAttrDto
     * @return
     */
    VmFlowLinkAttrDto getFlowLinkAttrByCondition(VmFlowLinkAttrDto flowLinkAttrDto);

    /**
     * 功能说明：根据用户id查环节信息
     *
     * @author: zhang.xionghui
     * @param: userId
     * @return:
     * @date: 2022/8/10 14:54
     * @requestID  需求单号及需求名称
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<VmFlowLinkAttrDto> getFlowLinkByUserId(@Param("userId") String userId);

    /**
     * 功能说明：根据角色id查环节信息
     *
     * @author: zhang.xionghui
     * @param: userId
     * @return:
     * @date: 2022/8/10 14:54
     * @requestID  需求单号及需求名称
     */
    List<VmFlowLinkAttrDto> getFlowLinkByRoleId(@Param("roleId") Long roleId,@Param("appVerCode") String appVerCode,@Param("industryCode") String industryCode);

    /**
     * 功能说明：查询流程环节属性配置信息（单条）
     *
     * @author: zhang.xionghui
     * @param:
     * @return:
     * @date: 2022/8/21 17:45
     * @requestID  需求单号及需求名称
     */
    VmFlowLinkAttrDto getFlowLinkAttr(VmFlowLinkAttrDto flowLinkAttrDto);

    /**
     * @description: 查询环节用户和角色信息
     * @author: yongqi.han
     * @date: 2022/8/22
     * @param vmFlowLinkAttrDto
     * @return: List<Map>
     */
    List<Map> getAttrUserOrRoleInfo(VmFlowLinkAttrDto vmFlowLinkAttrDto);

    /**
     * @description: 根据角色id查询 环节信息
     * @author: yongqi.han
     * @date: 2022/10/25
     * @param roleParam
     * @return: List<VmFlowLinkAttrDto>
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<VmFlowLinkAttrDto> getFlowLinkByRoleIdList(@Param("param") Map roleParam);


    /**
     * 查询流租户管理员信息
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysInfo queryAdminUser(@Param("tenantId")String tenantId);
}
