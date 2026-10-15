package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.entity.AlgorithmInfoDO;
import com.chinatower.sync.consumer.entity.SceneAlarmRelDTO;
import com.chinatower.sync.consumer.entity.SceneAlarmTenantDTO;
import com.chinatower.sync.consumer.entity.SceneConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 场景告警关系表 Mapper
 */
public interface SceneAlarmMapper {

    /**
     * 查询 sys_scene_config 表中指定算法的所有客户、行业组合
     *
     * @param algorithmCode 算法编码
     * @return 客户、行业组合列表
     */
    List<SceneAlarmTenantDTO> selectSceneAlarmTenants(@Param("algorithmCode") String algorithmCode);

    /**
     * 查询算法信息表中的 model_config 字段
     *
     * @param algorithmCode 算法编码
     * @return model_config JSON 字符串
     */
    AlgorithmInfoDO selectModelConfigFromAlgorithmInfo(@Param("algorithmCode") String algorithmCode);

    /**
     * 查询现有场景配置（根据算法编码、客户、行业、版本）
     *
     * @param algorithmCode 算法编码
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param appVerCode 版本编码
     * @return 场景配置列表
     */
    List<SceneConfigDTO> selectSceneConfigByAlgorithmCode(@Param("algorithmCode") String algorithmCode,
                                                           @Param("tenantId") String tenantId,
                                                           @Param("industryCode") String industryCode,
                                                           @Param("appVerCode") String appVerCode);

    /**
     * 查询现有场景告警关系（根据算法编码、客户、行业、版本）
     *
     * @param algorithmCode 算法编码
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param appVerCode 版本编码
     * @return 场景告警关系列表
     */
    List<SceneAlarmRelDTO> selectSceneAlarmRelByAlgorithmCode(@Param("algorithmCode") String algorithmCode,
                                                               @Param("tenantId") String tenantId,
                                                               @Param("industryCode") String industryCode,
                                                               @Param("appVerCode") String appVerCode);

    /**
     * 批量插入场景配置
     *
     * @param list 场景配置列表
     * @return 结果
     */
    int insertSceneConfigBatch(@Param("list") List<Map<String, Object>> list);

    /**
     * 批量插入场景告警关系
     *
     * @param list 场景告警关系列表
     * @return 结果
     */
    int insertSceneAlarmRelBatch(@Param("list") List<Map<String, Object>> list);

    /**
     * 批量插入告警类型
     *
     * @param list 告警类型列表
     * @return 结果
     */
    int insertAlarmTypeBatch(@Param("list") List<Map<String, Object>> list);

    List<SceneAlarmTenantDTO> getOrderAlgList(@Param("algorithmCode") String algorithmCode);
}

