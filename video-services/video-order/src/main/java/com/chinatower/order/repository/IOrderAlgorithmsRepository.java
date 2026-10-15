package com.chinatower.order.repository;


import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.system.api.model.AlgoTreeVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IOrderAlgorithmsRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderAlgorithmsRepository {

    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderAlgorithmsDOList]
     * @return
     */
    void saveListBatch(List<OrderAlgorithmsDO> orderAlgorithmsDOList);

    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageList]
     * @return
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 功能描述 查询订单订购的算法数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCodeList, packageCodeList]
     * @return
     */
    List<OrderAlgorithmsDO> getOrderAlgorithmsByList(List<String> orderCodeList, List<String> packageCodeList);

    /**
     * 功能描述
     * 查询算法配置区域
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    List<AlgorithmOneVO> getAlgorithmAreaByOrder(AlgorithmByOrderVO algorithmByOrder);

    /**
     * 功能描述
     *
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    List<Map<String,String>> getPresetIdAndNameByTaskId(List<String> taskIdList);

    List<OrderAlgorithmsVO> getCustomerAlgorithm(String tenantId, String deviceCode, String industryCode);

    /**
     * 更新算法状态
     *
     * @return
     * @author chenmf
     * @date 2022/4/13
     * @version
     */
    void updateAlgorithmStatus(String status, List<AlgorithmOneVO> areaList);

    void updateAlgorithmStatusByConfigId(String status, List<String> configIdList);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    /**
     * 根据用户返回的算法列表分组
     * @param tenantId
     * @return
     */
    Map<String, List<AlgoTreeVO>> getTenantAlgoMap(String orderCode);
}
