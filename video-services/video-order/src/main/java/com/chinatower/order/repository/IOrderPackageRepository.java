package com.chinatower.order.repository;


import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;

/**
 * @ClassName IOrderPackageRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface IOrderPackageRepository {

    /**
     * 功能描述 根据订单编码查询订购套餐信息列表
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     * @return
     */
    List<OrderPackageDO> getOrderPackageByOrderCodeList(List<String> orderCodeList, List<String> packageCodeList);

    /**
     * 根据订单编码获取订购套餐信息
     * @param vo 参数信息
     * @return 套餐信息
     */
    List<OrderPackageDO> getOrderPackageInfoByOrderCode(QueryOrderRelationshipVO vo);

    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    void deleteByOrderCode(String orderCode);

    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageDOList]
     * @return
     */
    void saveListBatch(List<OrderPackageDO> orderPackageDOList);

    /**
     * 查询租户,行业编码,行业版本编码有效的订单数量
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    int countOrderForApp(String tenantId, String appCode, String appVerCode);

    /**
     * 获取租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    List<String> selectTenantInfo(String tenantId);

    /**
     * 更新租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    void updateTenantInfo(String tenantId, String newInfo);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(String orderCode);

    @MapKey("ia")
    List<OrderPackageDO> getOrderPackageInfoByTenantId(String tenantId);

    List<OrderPackageDO> selectIndustryInfoByTenantId(String tenantId, String acceptNode);

    List<OrderPackageDO> selectValidIndustryInfoByTenantId(String tenantId, String filterOrderCode, String acceptNode);

    List<OrderPackageDO> selectHisIndustryInfoByTenantId(String tenantId, String acceptNode);
}
