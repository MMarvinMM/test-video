package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.system.api.model.AlgoTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderAlgorithmsMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderAlgorithmsMapper extends BaseMapper<OrderAlgorithmsDO> {

    /**
     * 功能描述 查询算法配置区域
     *
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    List<AlgorithmOneVO> getAlgorithmAreaByOrder(AlgorithmByOrderVO algorithmByOrder);

    /**
     * 功能描述  获取算法区域数据
     *
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    List<Map<String, String>> getPresetIdAndNameByTaskId(@Param("taskIdList") List<String> taskIdList);

    /**
     * 功能描述 查询订购算法列表
     *
     * @param tenantId,deviceCode
     * @return List<OrderAlgorithmsVO>
     * @author chensw
     * @date 2022/2/11
     * @version
     */
    List<OrderAlgorithmsVO> getCustomerAlgorithm(@Param("tenantId") String tenantId,
                                                 @Param("deviceCode") String deviceCode,
                                                 @Param("industryCode") String industryCode);

    /**
     * 更新算法状态
     *
     * @return
     * @author chenmf
     * @date 2022/4/13
     * @version
     */
    void updateAlgorithmStatus(@Param("configIdList")List<String> configIdList, @Param("onstatus")String onstatus , @Param("status")String status);

    void updateAlgorithmStatus2Enable(@Param("configIdList")List<String> configIdList, @Param("onstatus")String onstatus , @Param("status")String status);

    void updateAlgorithmStatus2Disable(@Param("configIdList")List<String> configIdList, @Param("onstatus")String onstatus , @Param("status")String status);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode")String orderCode);

    List<AlgoTreeVO> queryTenantAlgorithmList(String orderCode);
}
