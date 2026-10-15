package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.FlowLinkAttrDO;
import com.chinatower.event.entity.dto.SysInfo;

/**
 * 流程属性mapper
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:28
 */
@Mapper
public interface FlowLinkAttrMapper extends BaseMapper<FlowLinkAttrDO> {
    /**
     * 新增
     * @param model
     * @return
     */
    Integer addFlowLinkAttr(FlowLinkAttrModel model);

    /**
     * 批量添加
     * @param list
     * @return
     */
    Integer batchAddFlowLinkAttr(@Param("list") List<FlowLinkAttrModel> list);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<FlowLinkAttrModel> queryFlowLinkAttr(FlowLinkAttrModel model);
    /**
     * 根据条件查询下一环节
     * @param model
     * @return
     */
    List<FlowLinkAttrModel> queryNextFlowLinkAttr(FlowLinkAttrModel model);
    /**
     * 根据条件查询 临时给查询default默认值
     * @param model
     * @return
     */
    List<FlowLinkAttrModel> queryFlowLinkAttr4Temp(FlowLinkAttrModel model);

    /**
     * 根据流程ID集合查询
     * @param flowIdList
     * @return
     */
    List<FlowLinkAttrModel> queryByFlowIds(@Param("list") List<Long> flowIdList);

    /**
     * 根据主键集合查询
     * @param curLinkList
     * @param alarmFlowId
     * @return
     */
    List<FlowLinkAttrModel> queryByCurLinks(@Param("list") List<Long> curLinkList, @Param("alarmFlowId") Long alarmFlowId);

    /**
     * 修改
     * @param model
     * @return
     */
    Integer modifyFlowLinkAttr(FlowLinkAttrModel model);

    /**
     * 根据模板ID删除
     * @param flowId
     * @return
     */
    Integer deleteByFlowId(Long flowId);

    /**
     * 根据alarmFlowId删除
     * @param alarmFlowId
     * @return
     */
    Integer deleteByAlarmFlowId(Long alarmFlowId);

    /**
     * 检查流程环节是否在用
     * @param linkId
     * @return
     */
    List<Map<String, String>> checkLinkUse(Long linkId);

    /**
     * 查询组织列表
     * @param deptIdList
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryDepts(@Param("deptList") List<String> deptIdList, @Param("tenantId") String tenantId);
    /**
     * 查询组织列表
     * @param deptIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryDeptsNew(@Param("deptList") List<String> deptIdList, @Param("param") FlowLinkAttrModel model);

    /**
     * 查询组织列表（非权限切面）
     * @param deptIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryOrderInfoDepts(@Param("deptList") List<String> deptIdList, @Param("param") OrderInfoUserTreeReq model);


    /**
     * 根据角色，部门查询用户
     * @param deptIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryUserByRoleId(@Param("deptList") List<String> deptIdList,@Param("model")FlowLinkAttrModel model,@Param("userList") List<String> userList,@Param("roleList") List<String> roleList);

    /**
     * 根据角色，部门查询用户（新）
     * @param deptIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryUserByRoleIdNew(@Param("deptList") List<String> deptIdList,@Param("model")FlowLinkAttrModel model,@Param("userList") List<String> userList,@Param("roleList") List<Long> roleList);

    /**
     * 查询流程环节配置得用户Id
     * @param flowLinkId
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<String>  queryUserId(@Param("flowLinkId")Long flowLinkId);

    /**
     * 查询流租户管理员信息
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysInfo  queryAdminUser(@Param("tenantId")String tenantId);

    /**
     * 查询组织所属用户
     * @param deptIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryUserByDeptIds(@Param("deptList") List<String> deptIdList);

    /**
     * 根据组织ID获取用户所属角色树
     * @param
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryRoles(@Param("deptList") List<String> deptIdList);

    /**
     * 查询工单对应的流程环节
     *
     * @return
     * @author chenmf
     * @date 2022/6/9
     * @version
     */
    List<FlowLinkAttrModel> queryOrderFlowLink(@Param("warnTableName")String warnTableName, @Param("orderId")String orderId);

    /**
     * 查询工单对应的流程环节（初始化配置）
     *
     * @return
     * @author chenmf
     * @date 2022/6/9
     * @version
     */
    List<FlowLinkAttrModel> queryOrderIndustryFlowLink(@Param("warnTableName")String warnTableName, @Param("orderId")String orderId);

    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryDeptUsers(@Param("deptIdList") List<String> deptIdList);
}
