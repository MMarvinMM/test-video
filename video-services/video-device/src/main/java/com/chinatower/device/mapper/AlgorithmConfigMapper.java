package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.device.dto.*;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 算法配置 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface AlgorithmConfigMapper extends BaseMapper<AlgorithmConfigDO> {

    /**
     * 功能描述: 算法配置--列表分页查询算法配置
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<AlgorithmListVO> getAlgorithmConfigList(@Param("param") AlgorithmConfigDTO algorithmConfigDTO);

    List<AlgorithmListVO> getAlgorithmConfigList2(AlgorithmConfigDTO algorithmConfigDTO);

    List<AlgorithmAreaVO> getAlgorithmAreaRow2(@Param("industryCode") String industryCode, @Param("ids") List<AlgorithmConfigVO> ids);

    List<AlgorithmConfigVO> queryalgorithmConfig(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 更新算法配置区域
     *
     * @param algorithmAreaVO
     */
    void updAlgorithmArea(AlgorithmAreaVO algorithmAreaVO);


    /**
     * 行展开的标注数据
     *
     * @param algorithmConfigDTO
     * @return
     */
    List<AlgorithmAreaVO> getAlgorithmAreaRow(AlgorithmConfigDTO algorithmConfigDTO);

    List<AlgorithmConfigVO> getAlgorithmConfigRow2(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode, @Param("list") List<AlgorithmListVO> list);

    String getDeviceCode(String channelCode);


    String getAlgorithmStatusByTaskId(String taskId);

    void updateAlgorithmStatus(String pushStatus, String taskId);

    String selectTenantName(String tenantId);

    List<String> getTownCodeByCodes(List<SysAreaAddDto> sysAreaList);

    long selectMaxId();

    void insertSysAreaBatch(List<SysAreaAddDto> sysAreaList);

    DeviceModelVO getDeviceParam(String deviceCode);

    void delChannelByCodes(List<String> list);

    List<AlgorithmRankDto> getRank(AlgorithmRankVo algorithmRankVo);

    void updateDeviceDelFlagByDeviceCode(List<String> list);

    void updateChannelDelFlagByDeviceCode(List<String> list);

    void updateChannelDelFlagByChannelCode(List<String> list);

    /**
     * 功能描述: 租户视角--算法配置--查询已订购算法数量
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<AlgorithmListVO> getOrderAlgorithmTotal(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--查询已配置算法数量
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<AlgorithmListVO> getAlgorithmConfigCount(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--查询已订购算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<OrderAlgorithmsVO> getOrderAlgorithmInfo(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--查询已配置算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<OrderAlgorithmsVO> getAlgorithmConfigInfo(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--查询待配置算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<OrderAlgorithmsVO> getToBeConfigInfo(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--查询算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<AlgorithmConfigVO> getAlgoConfigInfo(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--批量删除后端算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    int delBackConfigBatch(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--批量删除前端算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    int delForeConfigBatch(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--批量停用启用后端算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    int uptBackStatusBatch(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 租户视角--算法配置--批量停用启用前端算法信息
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    int uptForeStatusBatch(AlgorithmConfigDTO algorithmConfigDTO);

    OrderAlgorithmsVO selectOrderCode(AlgorithmConfigDO algorithmConfigDO);

    List<OrderAlgorithmsVO> getCustomerAlgorithm(String tenantId, String deviceCode,String industryCode,String platformCode,String algorithmName);

    List<OrderAlgorithmsVO> getCustomerAlgorithmBatch(String tenantId, List<String> deviceCodeList,String industryCode,String platformCode);

    List<AlgorithmConfigVO> getAlgorithmConfig(CheckAlgorithmConfigVO checkAlgorithmConfigVO);

    List<FunctionAlgorthmVO> getFunctionInfo(Set<String> list);

    void updAlgoAreaBatch(List<AlgorithmAreaVO> list);

    List<OrderAlgorithmsVO> getAlgorithmConfigByDeviceList(AlgorithmIntersectionVO intersection);

    /**
     * 根据tenant_id、accept_node、industry_code、device_code、algorithm_type查询vm_order_algorithm_config表的算法编码集合（结果集2）
     *
     * @param tenantId      租户ID
     * @param deviceCode    单个设备编码（与deviceCodes二选一）
     * @param deviceCodes   设备编码列表（与deviceCode二选一）
     * @param industryCode  行业编码
     * @param acceptNode    受理节点
     * @param algorithmType 算法类型 1-摄像机 2-无人机
     * @return 算法编码集合
     */
    List<String> getOrderAlgorithmConfigCodes(@Param("tenantId") String tenantId,
                                              @Param("deviceCode") String deviceCode,
                                              @Param("deviceCodes") List<String> deviceCodes,
                                              @Param("industryCode") String industryCode,
                                              @Param("acceptNode") String acceptNode,
                                              @Param("algorithmType") Integer algorithmType);

    List<AlgorithmConfigVO> getAlgorithmByList(@Param("list") List<CheckAlgorithmConfigVO> list, @Param("industryCode") String industryCode, @Param("tenantId") String tenantId, @Param("posType") int posType, @Param("status") String status);

    List<AlgorithmInfoDO> getAlgorithmInfoBatch(List<CheckAlgorithmConfigVO> list);

    List<OrderAlgorithmsVO> selectOrderCodeList(@Param("list") List<CheckAlgorithmConfigVO> list, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    void updateAlgorithmAreaBatch(List<AlgorithmAreaVO> list);

    List<AlgorithmConfigVO> getAlgorithmPipelines();

    int updateAlgorithmPipelinesBatch(List<AlgorithmConfigVO> list);

    List<PresetVO> getPresetImgList(List<AlgorithmConfigAdd> list);

    List<String> addLargeModelAlgorithms(List<String> configIdList);

    List<String> getLargeModelAlgorithms();

    /**
     * 查询大模型算法配置数据（scene_code不为NULL且delete_flag=0）
     */
    List<AlgorithmConfigDO> selectModelConfigData();
}
