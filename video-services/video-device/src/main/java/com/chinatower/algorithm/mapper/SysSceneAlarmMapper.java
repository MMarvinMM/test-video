package com.chinatower.algorithm.mapper;

import com.chinatower.algorithm.dataobject.*;
import com.chinatower.platform.entity.admin.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 场景告警关系表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Mapper
public interface SysSceneAlarmMapper {

    // ========== 场景配置表（sys_scene_config）操作 ==========
    
    /**
     * 根据sceneId查询场景配置
     *
     * @param sceneId 场景ID
     * @return 场景配置
     */
    SysSceneConfigDO selectSceneConfigBySceneId(String sceneId);

    /**
     * 新增场景配置
     *
     * @param sceneConfigDO 场景配置
     * @return 结果
     */
    int insertSceneConfig(SysSceneConfigDO sceneConfigDO);

    /**
     * 批量新增场景配置
     *
     * @param list 场景配置列表
     * @return 结果
     */
    int insertSceneConfigBatch(@Param("list") List<SysSceneConfigDO> list);

    /**
     * 修改场景配置
     *
     * @param sceneConfigDO 场景配置
     * @return 结果
     */
    int updateSceneConfig(SysSceneConfigDO sceneConfigDO);

    /**
     * 删除场景配置（逻辑删除）
     *
     * @param sceneId 场景ID
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 结果
     */
    int deleteSceneConfigBySceneId(@Param("sceneId") String sceneId,
                                    @Param("tenantId") String tenantId,
                                    @Param("industryCode") String industryCode);

    // ========== 场景告警关系表（sys_scene_alarm_rel）操作 ==========

    /**
     * 根据id查询场景告警关系表
     *
     * @param id 主键id
     * @return 场景告警关系表
     */
    List<SysSceneAlarmRelDO> selectSysSceneAlarmRelById(String sceneId);

    /**
     * 查询场景告警关系表列表（关联查询两张表）
     *
     * @param queryDTO 查询条件
     * @return 场景告警关系表集合（包含场景信息）
     */
    List<SysSceneAlarmDO> selectSysSceneAlarmList(SysSceneAlarmQueryDTO queryDTO);

    /**
     * 查询场景告警关系表列表（支持分页和alarmType列表，关联查询两张表）
     *
     * @param queryDTO 查询条件
     * @return 场景告警关系表集合（包含场景信息）
     */
    List<SysSceneAlarmDO> selectSysSceneAlarmListWithPage(SysSceneAlarmQueryDTO queryDTO);

    /**
     * 查询场景告警树状列表（使用collection构建树，支持分页）
     *
     * @param queryDTO 查询条件
     * @return 场景告警树状结构列表
     */
    List<SceneAlarmTreeVO> selectSysSceneAlarmListForTree(SysSceneAlarmQueryDTO queryDTO);

    /**
     * 新增场景告警关系表
     *
     * @param relDO 场景告警关系表
     * @return 结果
     */
    int insertSysSceneAlarmRel(SysSceneAlarmRelDO relDO);

    /**
     * 批量新增场景告警关系表
     *
     * @param list 场景告警关系表列表
     * @return 结果
     */
    int insertSysSceneAlarmRelBatch(@Param("list") List<SysSceneAlarmRelDO> list);

    /**
     * 修改场景告警关系表
     *
     * @param relDO 场景告警关系表
     * @return 结果
     */
    int updateSysSceneAlarmRel(SysSceneAlarmRelDO relDO);

    /**
     * 删除场景告警关系表（逻辑删除）
     *
     * @param id 主键id
     * @return 结果
     */
    int deleteSysSceneAlarmRelById(Long id);

    /**
     * 批量删除场景告警关系表（逻辑删除）
     *
     * @param ids 需要删除的数据ID数组
     * @return 结果
     */
    int deleteSysSceneAlarmRelByIds(@Param("ids") Long[] ids);

    /**
     * 根据场景ID删除场景告警关系表（逻辑删除）
     *
     * @param sceneId 场景ID（字符串）
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 结果
     */
    int deleteSysSceneAlarmRelBySceneId(@Param("sceneId") String sceneId,
                                        @Param("tenantId") String tenantId,
                                        @Param("industryCode") String industryCode);

    /**
     * 根据告警名称查询某客户行业下的告警类型
     *
     * @param alarmTypeName 告警名称（可为空）
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 告警类型列表
     */
    List<AlarmTypeVO> selectAlarmTypesByName(@Param("alarmTypeName") String alarmTypeName,
                                              @Param("tenantId") String tenantId,
                                              @Param("industryCode") String industryCode,
                                             @Param("algorithmCode") String algorithmCode);

    /**
     * 根据场景名称查询某客户行业下的场景（从场景配置表查询）
     *
     * @param sceneName 场景名称（可为空）
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 场景列表
     */
    List<SceneVO> selectScenesByName(@Param("sceneName") String sceneName,
                                     @Param("tenantId") String tenantId,
                                     @Param("industryCode") String industryCode,
                                     @Param("algorithmCode") String algorithmCode);

    /**
     * 查询当前用户包含的大模型算法列表
     *
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param algorithmCodeList 大模型算法编码列表
     * @return 大模型算法列表
     */
    List<ModelAlgorithmVO> selectModelAlgorithms(@Param("tenantId") String tenantId,
                                                 @Param("industryCode") String industryCode,
                                                 @Param("algorithmCodeList") List<String> algorithmCodeList);

    /**
     * 根据算法编码查询场景列表（用于树状结构，从场景配置表查询）
     *
     * @param algorithmCode 算法编码
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 场景列表
     */
    List<SceneTreeVO> selectScenesByAlgorithmCode(@Param("algorithmCode") String algorithmCode,
                                                  @Param("tenantId") String tenantId,
                                                  @Param("industryCode") String industryCode);

    /**
     * 根据算法编码查询场景告警树状结构（使用collection构建树，无需内存拼接）
     *
     * @return 场景告警树状结构列表
     */
    List<SceneTreeVO> selectSceneTreeByAlgorithmCode(SceneAlarmQueryDTO queryDTO);

    /**
     * 根据场景ID列表查询告警类型（用于树状结构，从关系表查询）
     *
     * @param sceneIds 场景ID列表
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 告警类型列表
     */
    List<AlarmTypeVO> selectAlarmTypesBySceneIds(@Param("sceneIds") List<String> sceneIds,
                                                   @Param("tenantId") String tenantId,
                                                   @Param("industryCode") String industryCode);

    /**
     * 检查场景是否在算法配置表中使用（根据scene_id、tenant_id、industry_code、delete_flag=0）
     *
     * @param sceneId 场景ID
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 数量（大于0表示存在）
     */
    int countAlgorithmConfigBySceneId(@Param("sceneId") String sceneId,
                                     @Param("tenantId") String tenantId,
                                     @Param("industryCode") String industryCode);

    /**
     * 判断场景ID是否在算法配置表的scene_code字段中
     *
     * @param sceneId 场景ID
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 数量（大于0表示存在）
     */
    int countSceneInAlgorithmConfig(@Param("sceneId") String sceneId,
                                    @Param("tenantId") String tenantId,
                                    @Param("industryCode") String industryCode);

    // ========== 告警类型表（sys_alarm_type）操作 ==========

    /**
     * 根据type_value和alarm_source查询告警类型
     *
     * @param typeValue 告警类型编码（scene_id）
     * @param alarmSource 告警来源
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 告警类型
     */
    SysAlarmTypeDO selectAlarmTypeByTypeValue(@Param("typeValue") String typeValue,
                                                                                    @Param("alarmSource") String alarmSource,
                                                                                    @Param("tenantId") String tenantId,
                                                                                    @Param("industryCode") String industryCode);

    /**
     * 新增告警类型
     *
     * @param alarmTypeDO 告警类型
     * @return 结果
     */
    int insertAlarmType(SysAlarmTypeDO alarmTypeDO);

    /**
     * 批量新增告警类型
     *
     * @param list 告警类型列表
     * @return 结果
     */
    int insertAlarmTypeBatch(@Param("list") List<SysAlarmTypeDO> list);

    /**
     * 更新告警类型
     *
     * @param alarmTypeDO 告警类型
     * @return 结果
     */
    int updateAlarmType(SysAlarmTypeDO alarmTypeDO);

    /**
     * 根据type_value和source物理删除告警类型
     *
     * @param typeValue 告警类型编码（scene_id）
     * @param source 来源
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 结果
     */
    int deleteAlarmTypeByTypeValue(@Param("typeValue") String typeValue,
                                   @Param("source") String source,
                                   @Param("tenantId") String tenantId,
                                   @Param("industryCode") String industryCode);

    /**
     * 检查场景名称是否已存在（排除指定sceneId）
     *
     * @param sceneName 场景名称
     * @param sceneId 场景ID（可为空，用于修改时排除自己）
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 数量（大于0表示已存在）
     */
    int countSceneNameExists(@Param("sceneName") String sceneName,
                            @Param("sceneId") String sceneId,
                            @Param("tenantId") String tenantId,
                            @Param("industryCode") String industryCode);

    /**
     * 检查场景名称是否与告警类型名称重复
     *
     * @param sceneName 场景名称
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 告警类型名称列表（如果重复）
     */
    List<String> checkSceneNameConflictWithAlarmType(@Param("sceneName") String sceneName,
                                                      @Param("tenantId") String tenantId,
                                                      @Param("industryCode") String industryCode);

    /**
     * 检查 sys_scene_config 表中是否有有效数据
     *
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param algorithmCode 算法编码
     * @return 数量（大于0表示存在）
     */
    int countSceneConfigExists(@Param("tenantId") String tenantId,
                               @Param("industryCode") String industryCode,
                               @Param("algorithmCode") String algorithmCode);

    /**
     * 从 vm_algorithm_info_t 表中查询 model_config 字段
     *
     * @param algorithmCode 算法编码
     * @return model_config JSON字符串
     */
    String selectModelConfigFromAlgorithmInfo(@Param("algorithmCode") String algorithmCode);

    /**
     * 根据字典值中的算法编码列表，查询vm_order_algorithms表中的行业和版本数据
     * 条件：algorithm_code在列表中，ord_status在0,1,2中，delete_flag=0
     *
     * @param algorithmCodeList 算法编码列表
     * @return 订单算法信息列表（包含tenant_id, industry_code, app_ver_code, algorithm_code）
     */
    List<OrderAlgorithmDTO> selectOrderAlgorithmsByAlgorithmCodes(
            @Param("algorithmCodeList") List<String> algorithmCodeList);

    /**
     * 根据字典类型查询字典值列表
     *
     * @param dictType 字典类型
     * @return 字典值列表
     */
    List<String> selectDictValuesByType(@Param("dictType") String dictType);

    List<String> getLargeModelAlgorithms();

    List<SysDictData> getDictDataByType(String dictType);
}

