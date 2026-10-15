package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.VmAlarmFlowConfigDO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.Map;

/**
 * 告警流程配置mapper层
 * @author lyy
 * @version v1.5
 * @date 2022/7/20 10:28
 */
@DS(EbikeDataBaseConstant.PLATFORM)
@Mapper
public interface VmAlarmFlowConfigMapper extends BaseMapper<VmAlarmFlowConfigDO> {
    /**
     * 根据条件查询告警流程配置信息
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return List<VmAlarmFlowConfigDO>
     */
    List<VmAlarmFlowConfigDO> getAlarmFlowConfigList(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO);

    /**
     * 根据租户+告警来源+告警类型查询告警流程配置
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return List<VmAlarmFlowConfigDTO>
     */
    List<VmAlarmFlowConfigDTO> queryAlarmFlowConfigAndType(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO);

    /**
     * 根据ID获取告警流程配置
     * @author lyy
     * @param id
     * @return VmAlarmFlowConfigModel
     */
    VmAlarmFlowConfigModel queryAlarmFlowConfigById(@Param("id") Long id);

    /**
     * 根据行业应用应用版本获取行业初始化流程配置
     * @author liusq
     * @param param
     * @return VmAlarmFlowConfigDO
     */
    VmAlarmFlowConfigDO selectIndustryDefaultFlow(VmAlarmFlowConfigModel param);

    /**
     * 根据告警流程配置ID获取关联的告警类型
     * @author lyy
     * @param alarmFlowId
     * @return List<VmAlarmFlowTemplateTypeDO>
     */
    List<VmAlarmFlowTemplateTypeDO> getAlarmTypeByFlowId(@Param("alarmFlowId") Long alarmFlowId);

    /**
     * 根据告警流程配置ID获取流程环节属性
     * @author lyy
     * @param alarmFlowId
     * @return List<VmFlowLinkAttrPageDTO>
     */
    List<VmFlowLinkAttrPageDTO> queryFlowLinkAttrList(@Param("alarmFlowId") Long alarmFlowId);

    /**
     * 根据行业应用版本、行业编码获取流程环节配置
     * @author lyy
     * @param param
     * @return
     */
    List<VmFlowLinkAttrPageDTO> getFlowLinkIndustryConfig(Map<String, String> param);

    /**
     * 根据行业应用版本、行业编码获取流程环节配置(界面回显)
     * @author lyy
     * @param param
     * @return
     */
    List<VmFlowLinkAttrPageDTO> getIndustryAttr(Map<String, String> param);

    /**
     * 查询流程环节用户（ReceiveRepositoryImpl流程环节在使用）
     * @author lyy
     * @param attrId
     * @return List<VmFlowLinkUserDO>
     */
    List<VmFlowLinkUserDO> queryFlowLinkUserList(@Param("attrId") Long attrId);

    /**
     * 查询流程环节角色（ReceiveRepositoryImpl流程环节在使用）
     * @author lyy
     * @param attrId
     * @return List<VmFlowLinkRoleDO>
     */
    List<VmFlowLinkRoleDO> queryFlowLinkRoleList(@Param("attrId") Long attrId);

    /**
     * 查询流程环节用户
     * @author lyy
     * @param attrList
     * @return List<VmFlowLinkUserDO>
     */
    List<VmFlowLinkUserDO> queryFlowLinkUserListNew(List<VmFlowLinkAttrPageDTO> attrList);

    /**
     * 查询流程环节角色
     * @author lyy
     * @param attrList
     * @return List<VmFlowLinkRoleDO>
     */
    List<VmFlowLinkRoleDO> queryFlowLinkRoleListNew(List<VmFlowLinkAttrPageDTO> attrList);

    /**
     * 查询流程环节处理超期提醒
     * @author lyy
     * @version v1.5.75
     * @param attrList
     * @return List<VmFlowLinkOverdueRemindDO>
     */
    List<VmFlowLinkOverdueRemindDO> queryFlowLinkOverdueRemind(List<VmFlowLinkAttrPageDTO> attrList);

    /**
     * 查询流程环节用户
     * @param attrId
     * @return
     */
    List<VmFlowLinkUserDO> queryFlowLinkUserListByParam(@Param("attrId") Long attrId,@Param("linkId") Long linkId);

    /**
     * 查询流程环节角色
     * @param attrId
     * @return
     */
    List<VmFlowLinkRoleDO> queryFlowLinkRoleListByParam(@Param("attrId") Long attrId,@Param("linkId") Long linkId);

    /**
     * 查询用户名
     * @author lyy
     * @param userIdList
     * @return List<SysUser>
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysUser> queryUsers(@Param("list") List<String> userIdList, @Param("userName") String userName);

    /**
     * 根据用户名查询用户信息
     * @param sysUser
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map> getUserInfoByName(SysUser sysUser);

    /**
     * 查询第三方告警类型
     * @param tenantId
     * @param industryCode
     * @return
     */
    List<AlarmTypeVO> getThirdPartyAlarmTypes(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 根据租户id查询租户信息
     * @author lyy
     * @param tenantIdList
     * @return List<SysTenantInfo>
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysTenantInfo> queryTenantList(List<String> tenantIdList);

    /**
     * 获取租户下最新自动生成的名称（告警流程配置+数字）
     * @author lyy
     * @param model
     * @return String
     */
    String getAutoConfigName(VmAlarmFlowConfigModel model);

    /**
     * 校验流程配置名称是否已存在
     * @author lyy
     * @param model
     * @return int
     */
    int getConfigNameCount(VmAlarmFlowConfigModel model);

    /**
     * 新增告警流程配置
     * @author lyy
     * @param configModel
     * @return Integer
     */
    Integer addVmAlarmFlowConfig(VmAlarmFlowConfigModel configModel);

    /**
     * 批量新增流程配置关联告警类型信息
     * @author lyy
     * @param templateTypeList
     * @return Integer
     */
    Integer batchAddFlowTemplateType(List<VmAlarmFlowTemplateTypeDO> templateTypeList);

    /**
     * 批量新增流程环节属性
     * @author lyy
     * @param flowLinkAttrList
     * @return Integer
     */
    Integer batchAddFlowLinkAttr(List<VmFlowLinkAttrPageDTO> flowLinkAttrList);

    /**
     * 批量新增流程环节用户
     * @author lyy
     * @param flowLinkUserList
     * @return Integer
     */
    Integer batchAddFlowLinkUser(List<VmFlowLinkUserDO> flowLinkUserList);

    /**
     * 批量新增流程环节角色
     * @author lyy
     * @param flowLinkRoleList
     * @return Integer
     */
    Integer batchAddFlowLinkRole(List<VmFlowLinkRoleDO> flowLinkRoleList);

    /**
     * 批量新增流程环节处理超期提醒
     * @author lyy
     * @version v1.5.75
     * @param overdueRemindList
     * @return Integer
     */
    Integer batchAddFlowLinkOverdueRemind(List<VmFlowLinkOverdueRemindDO> overdueRemindList);

    /**
     * 根据ID修改告警流程配置
     * @author lyy
     * @param model
     * @return Integer
     */
    Integer modifierAlarmFlowConfigById(VmAlarmFlowConfigModel model);

    /**
     * 删除告警流程配置中的指定告警类型
     * @author lyy
     * @param param
     * @return int
     */
    int deleteAlarmTypes(VmAlarmFlowConfigModel param);

    /**
     * 根据告警类型获取告警配置id列表
     * @author lyy
     * @param param
     * @return List<String>
     */
    List<String> getConfigIdByAlarmType(VmAlarmFlowConfigModel param);

    /**
     * 查询租户告警类型为空的告警流程配置
     * @author lyy
     * @param param
     * @return List<Long>
     */
    List<Long> selectEmptyConfigIds(@Param("model") VmAlarmFlowConfigModel param, @Param("flowConfigIds") List<String> flowConfigIds);

    /**
     * 逻辑删除告警流程配置（表：vm_alarm_flow_config）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteFlowConfig(Map param);

    /**
     * 逻辑删除告警流程配置对应的alarmType表配置（表：vm_flow_template_alarm_type）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteAlarmTypeByConfigId(Map param);

    /**
     * 逻辑删除告警流程配置对应的attr表配置（表：vm_flow_link_attr）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteAttrByConfigId(Map param);

    /**
     * 物理删除告警流程配置对应的attr表配置（表：vm_flow_link_attr）
     * @author yhb
     * @param param
     * @return int
     */
    int deleteRealAttrByConfigId(Map param);

    /**
     * 逻辑删除告警流程配置中的user表配置（表：vm_flow_link_user）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteUserByConfigId(Map param);

    /**
     * 逻辑删除告警流程配置中的role表配置（表：vm_flow_link_role）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteRoleByConfigId(Map param);

    /**
     * 逻辑删除流程环节超期提醒配置（表：vm_flow_link_overdue_remind）
     * @author lyy
     * @version v1.5.75
     * @param param
     * @return int
     */
    int deleteRemindByConfigId(Map param);

    /**
     * 物理删除告警流程配置中的user表配置（表：vm_flow_link_user）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteRealUserByConfigId(Map param);

    /**
     * 物理删除告警流程配置中的role表配置（表：vm_flow_link_role）
     * @author lyy
     * @param param
     * @return int
     */
    int deleteRealRoleByConfigId(Map param);

    /**
     * 物理删除流程环节超期提醒配置（表：vm_flow_link_overdue_remind）
     * @author lyy
     * @version v1.5.75
     * @param param
     * @return int
     */
    int deleteRealRemindByConfigId(Map param);

    /**
     * 根据告警类型获取已存在的告警类型
     * @author liusq
     * @param param
     * @return List<String>
     */
    List<String> getExistAlarmType(VmAlarmFlowConfigModel param);

    /**
     * 获取租户下自定义流程配置的告警类型
     */
    List<VmAlarmFlowTemplateTypeDO> getTenantConfigAlarmType(VmAlarmFlowConfigModel param);

    /**
     * 查询配置了某角色的流程
     * @param param 角色id
     * @return
     */
    List<Map> getFlowByRoleId(Map param);

    /**
     * 查询配置了某用户的流程
     * @param param 用户id
     * @return
     */
    List<Map> getFlowByUserId(Map param);

    /**
     * 获取已经配置了某角色的行业初始化配置
     */
    List<Map> getIndustryConfigByRoleId(Map param);

    /**
     * 查询配置了某角色/某用户正在流转中的流程
     * @param param 配置了某角色/某用户的流程list
     * @return
     */
    List<Map> getFlowInUse(@Param("flows") List<Map> param, @Param("tableName") String tableName, @Param("templateId") Long templateId,
                           @Param("userId") String userId,@Param("industryCode") String industryCode,@Param("tenantId") String tenantId);
    /**
     * 根据id集合查询所有对应的告警流程配置信息
     * @author yhb
     * @param
     * @return List<VmFlowLinkAttrPageDTO>
     */
    List<VmFlowLinkAttrPageDTO> queryFlowLinkAttrListByIds(VmAlarmFlowConfigModel flowConfigModel);

    /**
     * 根据告警时间声音id集合查询所有对应的告警流程配置信息
     * @author yhb
     * @param
     * @return List<VmFlowLinkAttrPageDTO>
     */
    List<VmFlowLinkAttrPageDTO> queryFlowLinkAttrListByVoice(VmFlowLinkAttrPageDTO flowLinkAttrPageDTO);
}
