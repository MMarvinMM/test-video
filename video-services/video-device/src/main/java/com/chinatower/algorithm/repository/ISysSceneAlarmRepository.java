package com.chinatower.algorithm.repository;

import com.chinatower.algorithm.dataobject.*;
import com.chinatower.common.page.TableDataInfo;

import java.util.List;

/**
 * <p>
 * 场景告警关系表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
public interface ISysSceneAlarmRepository {

    /**
     * 根据id查询场景告警关系表
     *
     * @param id 主键id
     * @return 场景告警关系表
     */
    SysSceneConfigDO selectSysSceneAlarmById(String id);

    /**
     * 查询场景告警关系表列表
     *
     * @param sysSceneAlarmDO 查询条件
     * @return 场景告警关系表集合
     */
    List<SysSceneAlarmDO> selectSysSceneAlarmList(SysSceneAlarmDO sysSceneAlarmDO);

    /**
     * 查询场景告警关系表列表（支持分页和alarmType列表）
     *
     * @param queryDTO 查询条件
     * @return 场景告警关系表集合
     */
    List<SysSceneAlarmDO> selectSysSceneAlarmListWithPage(SysSceneAlarmQueryDTO queryDTO);

    /**
     * 查询场景告警树状列表（按场景分组，支持分页）
     *
     * @param queryDTO 查询条件
     * @return 场景告警树状结构列表
     */
    TableDataInfo selectSysSceneAlarmTreeList(SysSceneAlarmQueryDTO queryDTO);

    /**
     * 查询场景告警树状结构总数（用于内存分页）
     *
     * @param queryDTO 查询条件
     * @return 总数
     */
    long selectSysSceneAlarmTreeListTotal(SysSceneAlarmQueryDTO queryDTO);


    /**
     * 批量新增场景告警关系表
     *
     * @param addDTO 新增DTO
     * @return 结果
     */
    int insertSysSceneAlarmBatch(SysSceneAlarmAddDTO addDTO);


    /**
     * 批量修改场景告警关系表
     *
     * @param updateDTO 修改DTO
     * @return 结果
     */
    int updateSysSceneAlarmBatch(SysSceneAlarmUpdateDTO updateDTO);

    /**
     * 删除场景告警关系表（逻辑删除）
     *
     * @param id 主键id
     * @return 结果
     */
    int deleteSysSceneAlarmById(Long id);

    /**
     * 根据sceneId删除场景告警关系表（逻辑删除）
     *
     * @param sceneId 场景ID
     * @return 结果
     */
    int deleteSysSceneAlarmBySceneId(String sceneId);

    /**
     * 批量删除场景告警关系表（逻辑删除）
     *
     * @param ids 需要删除的数据ID数组
     * @return 结果
     */
    int deleteSysSceneAlarmByIds(Long[] ids);

    /**
     * 根据告警名称查询某客户行业下的告警类型
     *
     * @param queryDTO 告警名称（可为空）
     * @return 告警类型列表
     */
    List<AlarmTypeVO> selectAlarmTypesByName(AlarmTypeQueryDTO queryDTO);

    /**
     * 根据场景名称查询某客户行业下的场景
     *
     * @param queryDTO 场景名称（可为空）
     * @return 场景列表
     */
    List<SceneVO> selectScenesByName(SceneQueryDTO queryDTO);

    /**
     * 查询当前用户包含的大模型算法列表
     *
     * @return 大模型算法列表
     */
    List<ModelAlgorithmVO> getModelAlgorithms();

    /**
     * 根据算法编码查询场景告警树状结构
     *
     * @param queryDTO 查询参数，包含algorithmCode
     * @return 场景告警树状结构列表
     */
    List<SceneTreeVO> treeByAlgoCode(SceneAlarmQueryDTO queryDTO);

    /**
     * 检查场景是否在算法配置表中使用
     *
     * @param sceneId 场景ID
     * @return 是否存在（true表示存在，不允许修改/删除）
     */
    Boolean checkSceneInAlgorithmConfig(String sceneId);

    /**
     * 判断场景ID是否在算法配置表的scene_code字段中
     *
     * @param queryDTO 查询参数，包含sceneId
     * @return 场景是否在算法配置中
     */
    SceneInAlgorithmConfigVO checkSceneInAlgorithmConfigBySceneId(SceneInAlgorithmConfigQueryDTO queryDTO);

    /**
     * 校验场景名称（检查是否重复和是否与告警类型名称冲突）
     *
     * @param sceneName 场景名称
     * @param sceneId 场景ID（可为空，用于修改时排除自己）
     * @return 错误信息（如果校验通过返回null）
     */
    String validateSceneName(String sceneName, String sceneId);

    /**
     * 根据 tenantId、industryCode 和 algorithmCode 判断 sys_scene_config 表中是否有有效数据，
     * 如果没有，则根据 vm_algorithm_info_t 表中的 model_config 字段生成数据
     *
     * @param algorithmCode 算法编码
     * @return 生成的数据数量
     */
    int generateSceneAlarmFromModelConfig(String algorithmCode);

    /**
     * 根据 tenantId、industryCode、appVerCode 和 algorithmCode 判断 sys_scene_config 表中是否有有效数据，
     * 如果没有，则根据 vm_algorithm_info_t 表中的 model_config 字段生成数据
     *
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param algorithmCode 算法编码
     * @return 生成的数据数量
     */
    int generateSceneAlarmFromModelConfigWithParams(String tenantId, String industryCode, String algorithmCode);
}

