package com.chinatower.video.modules.videopreset.mapper;

import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订购算法配置表 Mapper 接口（设备-算法订购关系，运管同步）
 * </p>
 *
 * @author chenshaowu
 * @since 2026-07-28
 */
@Mapper
public interface OrderAlgorithmConfigMapper extends BaseMapper<OrderAlgorithmConfigDO> {

    /**
     * 校验摄像机与算法的订购关系是否存在（algorithm_type=1 摄像机算法配置）
     *
     * @param tenantId      租户id
     * @param acceptNode    受理节点（可为空）
     * @param industryCode  行业编码（可为空）
     * @param deviceCode    设备编码
     * @param algorithmCode 算法编码
     * @return 关系记录数
     */
    int countCameraAlgorithmRelation(@Param("tenantId") String tenantId,
                                     @Param("acceptNode") String acceptNode,
                                     @Param("industryCode") String industryCode,
                                     @Param("deviceCode") String deviceCode,
                                     @Param("algorithmCode") String algorithmCode);

    /**
     * 查询 vm_order_algorithm_config 表中满足条件的算法编码集合（结果集2）
     *
     * @param tenantId     租户id
     * @param acceptNode   受理节点
     * @param industryCode 行业编码
     * @param algorithmType 算法类型（1摄像机 2无人机）
     * @return 算法编码列表
     */
    List<String> getOrderAlgorithmConfigCodes(@Param("tenantId") String tenantId,
                                              @Param("acceptNode") String acceptNode,
                                              @Param("industryCode") String industryCode,
                                              @Param("algorithmType") Integer algorithmType);

    /**
     * 查询 vm_order_algorithm_config 表中满足条件的 设备编码+算法编码 对（结果集2，按设备区分）
     *
     * @param tenantId     租户id
     * @param acceptNode   受理节点
     * @param industryCode 行业编码
     * @param algorithmType 算法类型（1摄像机 2无人机）
     * @return 设备-算法关系记录列表（仅含 deviceCode 和 algorithmCode）
     */
    List<OrderAlgorithmConfigDO> getOrderAlgorithmConfigDeviceCodes(@Param("tenantId") String tenantId,
                                                                    @Param("acceptNode") String acceptNode,
                                                                    @Param("industryCode") String industryCode,
                                                                    @Param("algorithmType") Integer algorithmType);
}
