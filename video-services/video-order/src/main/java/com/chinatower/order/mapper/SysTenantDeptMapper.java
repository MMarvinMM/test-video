package com.chinatower.order.mapper;

import com.chinatower.bdm.api.entity.GridVO;
import com.chinatower.order.dto.OrderTransferConfigDTO;
import com.chinatower.order.dto.SysDeptHisMongoDTO;
import com.chinatower.platform.entity.admin.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @auther liuhuanhuan
 * @date 2022/3/17
 */
public interface SysTenantDeptMapper {

    List<String> selectDeptStreetCodeByDeptId(String deptId);

    List<String> selectDeptIdByTenantId(@Param("tenantId") String tenantId);

    List<SysDept> selectByParentId(String parentId);

    /**
     * 新增部门信息
     *
     * @param deptList 部门信息
     * @return 结果
     */
    int insertDeptBatch(@Param("list") List<SysDept> deptList);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(String deptId);

    int countByDeptId(SysDept sysDept);

    List<SysDept> countByDeptIdBatch(@Param("tenantId") String tenantId,@Param("list") List<String> industryCodeList);

    int updateAppVerCodeBatch(@Param("list") List<SysDept> deptList);

    List<GridVO> findFirstGridBatch(@Param("tenantId") String tenantId,@Param("list") List<String> industryCodeList);

    int insertGridInfoList(List<GridVO> list);

    /**
     * 更改预置位配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateVideoPreset(OrderTransferConfigDTO param);

    /**
     * 更改巡航计划配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updatePatrolInfo(OrderTransferConfigDTO param);

    /**
     * 更改录像计划配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateChannelVideoPlan(OrderTransferConfigDTO param);

    /**
     * 更改非二次研判前端算法配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateNotSecondSwitchAlgorithmGiveConfig(OrderTransferConfigDTO param);

    /**
     * 更改二次研判前端算法配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateSecondSwitchAlgorithmGiveConfig(OrderTransferConfigDTO param);

    /**
     * 更改后端算法配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateAlgorithmConfig(OrderTransferConfigDTO param);

    /**
     * 更改订购算法配置信息
     * @param param 更改参数
     * @return 影响条数
     */
    int updateOrderAlgorithmConfig(OrderTransferConfigDTO param);

    /**
     * 更新后端算法删除状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int updateAlgorithmConfigStatus(@Param("orderCode") String orderCode);

    List<SysDeptHisMongoDTO> selectByTenantIdAndIndustryCode(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int physicalDeleteByIds(@Param("idList")List<Long> ids);
}
