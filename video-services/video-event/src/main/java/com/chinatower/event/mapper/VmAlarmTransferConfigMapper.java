package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.VmAlarmTransferConfigVO;
import com.chinatower.event.entity.vo.VmAlarmTypeTransferVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 告警转移配置mapper层
 * @author lyy
 * @version v1.6.00
 * @date 2023/06/30
 */
@Mapper
public interface VmAlarmTransferConfigMapper {
    /**
     * 根据条件查询告警转移配置信息
     * @param alarmTransferConfigDTO
     * @return List<VmAlarmTransferConfigVO>
     */
    List<VmAlarmTransferConfigVO> getAlarmTransferConfigList(VmAlarmTransferConfigDTO alarmTransferConfigDTO);

    /**
     * 根据租户+告警来源+告警类型查询告警转移配置
     * @param transferConfigDTO
     * @return List<VmAlarmTransferConfigVO>
     */
    List<VmAlarmTransferConfigVO> queryAlarmTransferConfigAndType(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 根据ID获取告警转移配置
     * @param id
     * @return VmAlarmTransferConfigVO
     */
    VmAlarmTransferConfigVO queryAlarmTransferConfigById(@Param("id") Long id);

    /**
     * 根据告警转移配置ID获取关联的告警类型
     * @param alarmTransferId
     * @return List<VmAlarmTypeTransferVO>
     */
    List<VmAlarmTypeTransferVO> getAlarmTypeByTransferId(@Param("alarmTransferId") Long alarmTransferId);

    /**
     * 查询用户名
     * @param userIdList
     * @return List<SysUser>
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysUser> queryUsers(List<String> userIdList);

    /**
     * 根据租户id查询租户信息
     * @param tenantIdList
     * @return List<SysTenantInfo>
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysTenantInfo> queryTenantList(List<String> tenantIdList);

    /**
     * 获取租户下最新自动生成的名称（告警转移策略+数字）
     * @param transferConfigDTO
     * @return String
     */
    String getAutoTransferName(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 校验转移配置名称是否已存在
     * @param transferConfigDTO
     * @return int
     */
    int getTransferNameCount(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 新增告警转移配置
     * @param transferConfigDTO
     * @return Integer
     */
    Integer addVmAlarmTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 批量新增转移配置关联的告警类型信息
     * @param transferTypeList
     * @return Integer
     */
    Integer batchAddAlarmTypeTransfer(List<VmAlarmTypeTransferVO> transferTypeList);

    /**
     * 根据ID修改告警转移配置
     * @param transferConfigDTO
     * @return Integer
     */
    Integer modAlarmTransferConfigById(VmAlarmTransferConfigDTO transferConfigDTO);

    /**
     * 删除告警转移配置关联的告警类型
     * @param param
     * @return int
     */
    int delAlarmTypeList(VmAlarmTransferConfigDTO param);

    /**
     * 根据告警类型获取告警转移配置id集合
     * @param param
     * @return List<String>
     */
    List<String> getTransferIdByAlarmType(VmAlarmTransferConfigDTO param);

    /**
     * 查询告警类型为空的告警转移配置
     * @param alarmTransferIds
     * @return List<Long>
     */
    List<Long> selectEmptyTransferIds(@Param("model") VmAlarmTransferConfigDTO transferConfigDTO, @Param("alarmTransferIds") List<String> alarmTransferIds);


    /**
     * 逻辑删除告警转移配置
     * @param param
     * @return int
     */
    int delAlarmTransferConfig(Map param);

    /**
     * 逻辑删除告警转移配置关联的告警类型配置
     * @param param
     * @return int
     */
    int delAlarmTypeByTransferId(Map param);

}
