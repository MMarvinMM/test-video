package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderPackageMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderPackageMapper extends BaseMapper<OrderPackageDO> {

    /**
     * 查询租户,行业编码,行业版本编码有效的订单数量
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    int countOrderForApp(@Param("tenantId")String tenantId, @Param("appCode")String appCode, @Param("appVerCode")String appVerCode);

    /**
     * 获取租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    List<String> selectTenantInfo(@Param("tenantId")String tenantId);

    /**
     * 更新租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    void updateTenantInfo(@Param("tenantId")String tenantId, @Param("newInfo")String newInfo);

    /**
     * 根据订单号码获取订购套餐信息
     * @param vo 参数信息
     * @return 订购套餐信息
     */
    List<OrderPackageDO> getOrderPackageInfoByOrderCode(QueryOrderRelationshipVO vo);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode")String orderCode);

    List<OrderPackageDO> getOrderPackageInfoByTenantId(@Param("tenantId")String tenantId);

    List<OrderPackageDO> selectIndustryInfoByTenantId(@Param("tenantId")String tenantId, @Param("acceptNode")String acceptNode);

    List<OrderPackageDO> selectValidIndustryInfoByTenantId(@Param("tenantId")String tenantId,
                                                           @Param("filterOrderCode")String filterOrderCode,
                                                           @Param("acceptNode")String acceptNode);

    List<OrderPackageDO> selectHisIndustryInfoByTenantId(@Param("tenantId")String tenantId, @Param("acceptNode")String acceptNode);
}
