package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmFlowConfig;
import com.chinatower.event.dataobject.AlarmFlowConfigDO;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:28
 */
@Mapper
public interface AlarmFlowConfigMapper extends BaseMapper<AlarmFlowConfigDO> {
    /**
     * 新增
     * @param model
     * @return
     */
    Integer addAlarmFlowConfig(AlarmFlowConfigModel model);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<AlarmFlowConfig> queryAlarmFlowConfig(AlarmFlowConfigModel model);

    /**
     * 获取告警流程配置
     * @param tenantId
     * @param alarmCode
     * @param warningSourceList
     * @return
     */
    List<AlarmFlowConfig> getAlarmFlowConfig(@Param("tenantId") String tenantId, @Param("alarmCode") String alarmCode, @Param("warningSourceList") List<String> warningSourceList);
    /**
     * 获取告警流程配置(新)
     * @param tenantId
     * @param alarmCode
     * @param warningSourceList
     * @return
     */
    List<AlarmFlowConfig> getAlarmFlowConfigNew(@Param("tenantId") String tenantId, @Param("alarmCode") String alarmCode, @Param("warningSourceList") List<String> warningSourceList);

    /**
     * 修改
     * @param model
     * @return
     */
    Integer modifyAlarmFlowConfig(AlarmFlowConfigModel model);

    /**
     * 删除
     * @param alarmFlowId
     * @return
     */
    Integer deleteAlarmFlowConfig(Long alarmFlowId);

    /**
     * 根据条件查询流程开始环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowStartLink(AlarmFlowConfigModel model);

    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLink(AlarmFlowConfigModel model);

    /**
     * 根据条件查询所有下一个流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryAllNextFlowLink(AlarmFlowConfigModel model);

    /**
     * 查询流程所有环节信息
     * @param flowId
     * @return
     */
    //List<FlowModel> queryFlowAllLink(Long flowId);

    /**
     * 查询用户名
     * @param userIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysUser> queryUsers(List<String> userIdList);

    /**
     * 根据租户ID查询租户集合
     * @param tenantIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysTenantInfo> queryTenantList(List<String> tenantIdList);

    /**
     * 根据租户ID查询有效根组织
     * @param tenantId
     * @return
     */
    /*@DS("db1")
    SysDept getTenantRootDept(@Param("tenantId") String tenantId);*/

}
