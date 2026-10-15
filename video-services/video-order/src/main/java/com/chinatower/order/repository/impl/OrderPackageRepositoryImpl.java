package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.order.mapper.OrderPackageMapper;
import com.chinatower.order.repository.IOrderPackageRepository;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName OrderPackageRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderPackageRepositoryImpl extends ServiceImpl<OrderPackageMapper, OrderPackageDO> implements IOrderPackageRepository {

    @Autowired
    private OrderPackageMapper orderPackageMapper;

    /**
     * 功能描述 根据订单编码查询订购套餐信息列表
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    @Override
    public List<OrderPackageDO> getOrderPackageByOrderCodeList(List<String> orderCodeList, List<String> packageCodeList) {
        QueryWrapper<OrderPackageDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_code", orderCodeList);
        if (CollectionUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderPackageMapper.selectList(queryWrapper);
    }

    @Override
    public List<OrderPackageDO> getOrderPackageInfoByOrderCode(QueryOrderRelationshipVO vo) {
        return orderPackageMapper.getOrderPackageInfoByOrderCode(vo);
    }

    /**
     * 功能描述 删除订单数据
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     * @return
     */
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderPackageDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        orderPackageMapper.delete(queryWrapper);
    }

    /**
     * 功能描述 批量保存
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageDOList]
     * @return
     */
    @Override
    public void saveListBatch(List<OrderPackageDO> orderPackageDOList) {
        this.saveBatch(orderPackageDOList);
    }

    /**
     * 查询租户,行业编码,行业版本编码有效的订单数量
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    @Override
    public int countOrderForApp(String tenantId, String appCode, String appVerCode) {
        return orderPackageMapper.countOrderForApp(tenantId, appCode, appVerCode);
    }

    /**
     * 获取租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    @Override
    public List<String> selectTenantInfo(String tenantId) {
       return orderPackageMapper.selectTenantInfo(tenantId);
    }

    /**
     * 更新租户行业信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    @Override
    public void updateTenantInfo(String tenantId, String newInfo) {
        orderPackageMapper.updateTenantInfo(tenantId, newInfo);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderPackageMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public List<OrderPackageDO> getOrderPackageInfoByTenantId(String tenantId) {
        return orderPackageMapper.getOrderPackageInfoByTenantId(tenantId);
    }

    @Override
    public List<OrderPackageDO> selectIndustryInfoByTenantId(String tenantId, String acceptNode) {
        return orderPackageMapper.selectIndustryInfoByTenantId(tenantId, acceptNode);
    }

    @Override
    public List<OrderPackageDO> selectValidIndustryInfoByTenantId(String tenantId, String filterOrderCode, String acceptNode) {
        return orderPackageMapper.selectValidIndustryInfoByTenantId(tenantId, filterOrderCode, acceptNode);
    }

    @Override
    public List<OrderPackageDO> selectHisIndustryInfoByTenantId(String tenantId, String acceptNode) {
        return orderPackageMapper.selectHisIndustryInfoByTenantId(tenantId, acceptNode);
    }
}
