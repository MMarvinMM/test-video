package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.resp.SysDictDataResponse;

import java.util.List;
import java.util.Map;

/**
 * 告警流程配置业务接口
 * @author lyy
 * @version v1.5
 * @date 2022/7/20
 */
public interface VmAlarmFlowConfigService {

    /**
     * 分页查询告警流程配置
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     */
    Result<Object> getAlarmFlowConfigList(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO);

    /**
     * 根据租户ID + 告警来源 + 告警类型 + 行业应用版本 + 行业编码查询
     * @author lyy
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     */
    Result<Object> queryAlarmFlowConfigAndType(VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO);

    /**
     * 查看告警流程配置详情、编辑回显
     * @author lyy
     * @param configModel
     * @return Result<Object>
     */
    Result<Object> queryAlarmFlowConfigDetail(VmAlarmFlowConfigModel configModel);

    /**
     * 获取默认名称“告警流程名称+数字（数字累加）”
     * @author lyy
     * @param configModel
     * @return Result<String>
     */
    Result<String> getAutoConfigName(VmAlarmFlowConfigModel configModel);

    /**
     * 校验告警流程配置名称是否唯一
     * @author lyy
     * @param configModel
     * @return Result<Object>
     */
    Result<Object> getConfigNameCount(VmAlarmFlowConfigModel configModel);

    /**
     * 新增告警流程配置、流程属性、用户或者角色
     * @author lyy
     * @param model
     * @return Result<String>
     */
    Result<String> addVmAlarmFlowConfig(VmAlarmFlowConfigModel model) throws Exception;

    /**
     * 修改告警流程配置
     * @author lyy
     * @param flowConfigModel
     * @return Result<String>
     */
    Result<String> updateAlarmFlowConfig(VmAlarmFlowConfigModel flowConfigModel) throws Exception;

    /**
     * 新增配置时有重复告警类型则删除之前的告警类型配置
     * @author lyy
     * @param model
     * @return
     */
    int deleteExistedAlarmType(VmAlarmFlowConfigModel model);

    /**
     * 批量删除告警流程配置
     * 新增配置时有重复告警类型则删除之前的告警类型配置，删除后如果告警流程配置中没有告警类型了，就要对这些空的告警流程配置进行删除
     * @author lyy
     * @param alarmFlowIds
     * @param tenantId
     * @return
     */
    int deleteFlowConfigs(List<Long> alarmFlowIds, String tenantId,String operType);

    /**
     * 删除告警流程配置的除config表以外的其他配置
     * 编辑告警流程配置时 对其他配置采用先删后加的操作
     * @author lyy
     * @param alarmFlowId
     * @param tenantId
     * @return int
     */
    int deleteConfigAnotherInfo(List<Map> alarmFlowId, String tenantId);

    /**
     * 删除告警流程配置
     * @author lyy
     * @param alarmFlowId
     * @param tenantId
     * @return Result<String>
     */
    Result<String> deleteFlowConfig(Long alarmFlowId, String tenantId,String operType);


    /**
     * 校检告警声音是否可修改或者删除
     * @author yhb
     * @param model
     * @return Result<Map>
     */
    Result<Map> selectAlarmSoundOperation(AlarmSoundDTO model);

    /**
     * 根据用户名查询用户信息
     * @param sysUser
     * @return
     */
    List<Map> getUserInfoByName(SysUser sysUser);

    /**
     * 查询所有告警来源
     * @return
     */
    Result<List<SysDictDataResponse>> findAlarmSourceAll();

    /**
     * 查询该租户行业下选择的第三方告警类型
     * @param subTypeDTO
     * @return
     */
    List<AlarmSourceSubTypeVO> getThirdPartyAlarmTypes(AlarmSourceSubTypeDTO subTypeDTO);
}
