package com.chinatower.order.repository;


import com.chinatower.common.core.domain.OrderDeviceDO;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.platform.entity.order.FindDeviceCodeVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.system.api.model.DeviceTreeVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IOrderDeviceRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderDeviceRepository {
    
    /**
     * 功能描述 根据订单编码查询订购设备信息列表
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     * @return
     */
    List<OrderDeviceDO> getOrderDeviceByOrderCode(String orderCode, String packageCode);
    
    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceDOList]
     * @return
     */
    void saveListBatch(List<OrderDeviceDO> orderDeviceDOList);

    /**
     * 批量插入
     * @param orderDeviceDOList
     * @return
     */
    int batchInsert(List<OrderDeviceDO> orderDeviceDOList);

    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    void deleteByOrderCode(String orderCode);
    
    /**
     * 功能描述 获取订单编码
     * @author chenmf
     * @date 2022/1/19
     * [customerCode, deviceCode, channelCode]
     * @return
     */
    VideoOrderResp getOrderCode(String customerCode, String deviceCode, String channelCode, String industryCode);
    
    /**
     * 查询客户下有效的设备id
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    List<String> getValidDev(String customerCode);
    
    /**
     * 查询客户下有效的设备通道id列表
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    List<String> getValidChn(String customerCode);
    
    /**
     * 查询对应订单设备信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/3
     * @version
     */
    List<OrderDevicesVO> getDeviceInfoByList(List<String> orderCodeList, List<String> packageCodeList);
    
    /**
     * 根据用户编号查询用户订购的行业版本下的设备编号
     *
     * @return
     * @author chenmf
     * @date 2022/3/5
     * @version
     */
    List<String> findDeviceCodeByUser(FindDeviceCodeVO findDeviceCodeVO);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    List<OrderDevicesVO> getByTenantIdAndIndustryCode(String tenantId, String industryCode);

    Map<String, List<DeviceTreeVO>> getTenantDeviceMap(String orderCode);

    void assembleDeviceTree(List<DeviceTreeVO> sourceTreeVOList, List<DeviceTreeVO> targetTreeList);

    List<OrderDevicesVO> getDeviceInfoByOrderCodeAndPackageCode(QueryOrderRelationshipVO queryOrderRelationshipVO);

    List<OrderDevicesVO> getDeviceInfoByDeviceCodes(List<String> deviceCodes);

    void deleteByOrderCodeNewTransactional(String orderCode);
}
