package com.chinatower.order.service;

import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.dto.TransferIndustryDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.*;
import com.chinatower.common.core.dto.VideoOrderResp;

import java.util.List;

/**
 * 功能描述
 * @author chenmf
 * @date 2022/1/6
 *
 * @return
 */
public interface IVideoOrderInfoService {
    
    /**
     * 功能描述 获取订单编码
     * @author chenmf
     * @date 2022/1/19
     * [videoOrderReq]
     * @return
     */
    VideoOrderResp getVideoOrderInfo(VideoOrderReq videoOrderReq);
    
    
    /**
     * 功能描述 分页获取租户订购信息
     * @author chenmf
     * @date 2022/1/27
     * [queryOrderRelationshipVO]
     * @return
     */
    List<OrderInfoVO> getOrderRelationshipListByTenantId(QueryOrderRelationshipVO queryOrderRelationshipVO);

    /**
     * 功能描述 查询订购算法列表
     * @author chensw
     * @date 2022/2/11
     * @return
     */
    List<OrderAlgorithmsVO> queryAlgorithmList(CustomerDTO customerDTO);
    
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
     * 新增客户
     * @param customerAddVO
     * @return
     */
    Result<Integer> addCustomer(CustomerAddVO customerAddVO);

    /**
     * 删除客户
     * @param tenantId
     * @return
     */
    Result deleteCustomer(String tenantId);

    /**
     *
     * @param tenantId
     * @return
     */
    Result getCustomerInfo(String tenantId);

    Result updateCustomer(CustomerAddVO customerAddVO);

    /**
     * 新增订单
     * @param params
     * @return
     */
    Result dealOrder(List<OrderRelationshipDTO> params);

    Result checkOrderExist(String tenantId);

    Result<List<OrderPackageDO>> getOrderCodesByTenantId(String tenantId);

    /**
     * 行业应用、版本变更接口
     * @param param 参数
     */
    void transferIndustry(TransferIndustryDTO param);

    /**
     * 行业应用、版本变更接口【事务外】
     * @param param 参数
     */
    void transferIndustryOutOfTransaction(TransferIndustryDTO param);

    /**
     * 行业应用、版本订单信息变更【父节点】
     * @param param 参数
     */
    void transferOrderIndustry(TransferIndustryDTO param, boolean pushFlag);

    /**
     * 分页获取租户订购信息（不包含具体的订购应用、订购摄像机、订购物联设备等详情列表）
     */
    List<OrderInfoVO> getSimpleOrderRelationshipListByTenantId(QueryOrderRelationshipVO queryOrderRelationshipVO);


    /**
     * 分页获取订购摄像机
     */
    List<OrderDevicesVO> getOrderDeviceInfos(QueryOrderRelationshipVO queryOrderRelationshipVO);

    /**
     * 分页获取订购物联设备
     */
    List<OrderDevicesVO> getOrderSensorDeviceInfos(QueryOrderRelationshipVO queryOrderRelationshipVO);

    /**
     * 根据设备编码列表、类型、订单号查询订购算法配置及算法名称
     * @param deviceCodeList 设备编码列表
     * @param type 算法类型 1-摄像机 2-无人机
     * @param orderCode 订单号
     * @return 设备编码、算法编码、算法名称
     */
    List<OrderAlgorithmConfigVO> getAlgorithmConfigByDeviceCodesAndType(
            List<String> deviceCodeList, Integer type, String orderCode);
}
