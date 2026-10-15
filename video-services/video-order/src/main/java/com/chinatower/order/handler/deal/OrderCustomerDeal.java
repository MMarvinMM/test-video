package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderCustomerDO;
import com.chinatower.common.core.domain.OrderCustomerHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.order.dto.ResourceTypeInitDTO;
import com.chinatower.order.mapper.SysTenantDeptMapper;
import com.chinatower.order.repository.IOrderCustomerHisRepository;
import com.chinatower.order.repository.IOrderCustomerRepository;
import com.chinatower.order.service.IOrderResourceTypeService;
import com.chinatower.order.service.IOrderSystemService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.tenant.AddTenantInfoRequest;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.request.tenant.TenantTradeRequest;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.resp.SysUserResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName OrderCustomerDeal
 * @Deacription 订单客户关系模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderCustomerDeal extends AbstractOrderDeal {

    @Autowired
    private IOrderCustomerHisRepository orderCustomerHisRepository;

    @Autowired
    private IOrderCustomerRepository orderCustomerRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IOrderSystemService orderSystemService;

    @Autowired
    private IOrderResourceTypeService orderResourceTypeService;

    @Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        log.info("OrderCustomerDeal 订购处理开始");

        //先判断订购推送的行业编码和行业版本编码是否有效
        List<OrderGatewayDTO.OrderPackages> packages = orderGateway.getPackages();
        String orderCode = orderGateway.getOrderCode();
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        //查询用户是否首次订购
        String customerCode = customer.getCustomerCode();
        QuTenantInfoRequest quTenantInfoRequest = new QuTenantInfoRequest();
        quTenantInfoRequest.setTenantId(customerCode);
        Result<Integer> result = systemFeignClient.checkTenantId(customerCode);
        String userId = null;
        if (result.getData() > 0) {
            List<SysUserResponse> sysUserList = systemFeignClient.selectUserByTenantId(customerCode).getData();
            if (!CollectionUtils.isEmpty(sysUserList)) {
                Optional<SysUserResponse> optional = sysUserList.stream().filter(user -> customer.getAdmin().equals(user.getUserName())).findAny();
                if (optional.isPresent()) {
                    userId = optional.get().getUserId();
                }
                log.info("租户多次订购!userId:" + userId);
            }
        }
        //更新租户信息 更新顶层组织
        addTenantInfo(orderGateway);
        //判断是否有用户信息
        userId = addUser(userId, orderGateway);
        // 如果只处理订单和客户信息，不处理资源初始化信息
        if (BooleanUtils.isNotTrue(orderGateway.getHandleOrderUserInfoOnly())) {
            //资源类型数据初始化
            List<ResourceTypeInitDTO> resourceTypeInitList = Lists.newArrayList();
            packages.forEach(orderPackage -> {
                ResourceTypeInitDTO resourceTypeInt = new ResourceTypeInitDTO();
                resourceTypeInt.setIndustryCode(orderPackage.getApplication().getAppCode());
                resourceTypeInt.setAppVerCode(orderPackage.getApplication().getProductCode());
                resourceTypeInt.setTenantId(customerCode);
                resourceTypeInt.setCreator(customer.getAdmin());
                resourceTypeInitList.add(resourceTypeInt);
            });

            try {
                orderResourceTypeService.initResourceType(resourceTypeInitList);
            } catch (Exception e) {
                log.error("调用资源类型数据初始化失败", e);
            }
        }

        List<OrderCustomerDO> customerDOList = orderCustomerRepository.getOrderCustomerByOrderCode(orderCode);
        int moveSign = 0;
        String oldAcceptNode = orderGateway.getAcceptNode();
        if (CollectionUtils.isEmpty(customerDOList)) {
            this.saveCustomer(orderGateway, userId, moveSign, orderGateway.getAcceptNode());
        } else {
            //数据迁移到历史表
            //迁移次数加1
            moveSign = customerDOList.get(0).getMoveSign() + 1;
            List<OrderCustomerHisDO> hisDOList = new ArrayList<>();
            customerDOList.forEach(customerDO -> {
                OrderCustomerHisDO hisDO = new OrderCustomerHisDO();
                BeanUtils.copyProperties(customerDO, hisDO);
                hisDOList.add(hisDO);

            });
            oldAcceptNode = customerDOList.get(0).getAcceptNode();
            orderCustomerHisRepository.saveCustomer(hisDOList);
            orderCustomerRepository.deleteByOrderCode(orderCode);
            this.saveCustomer(orderGateway, userId, moveSign, oldAcceptNode);
        }
        orderGateway.setOldAcceptNode(oldAcceptNode);
        orderGateway.setOrderMoveSign(moveSign);

        log.info("OrderCustomerDeal 订购处理结束");

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param orderGateway
     */
    private String addUser(String userId, OrderGatewayDTO orderGateway) {
        //判断是否有用户信息
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        List<String> existDeptIdList = sysTenantDeptMapper.selectDeptIdByTenantId(customer.getCustomerCode());
        // 当前节点是否是受理节点，如果受理节点字段为空也认为是受理节点
        boolean isAcceptNode = orderGateway.getAcceptNode() == null
                || (org.apache.commons.lang3.StringUtils.equals(orderGateway.getAcceptNode(), orderGateway.getCurrentLocationCode()));
        if (StringUtils.isEmpty(userId)) {
            //创建用户
            AddUserRequest addUserRequest = new AddUserRequest();
            addUserRequest.setTenantId(customer.getCustomerCode());
            addUserRequest.setUserName(customer.getAdmin());
            addUserRequest.setDeptId(Joiner.on(",").join(existDeptIdList));
            addUserRequest.setIsTenantAdmin(OrderConstants.IS_TENANT_ADMIN);
            addUserRequest.setStatus(SysConstants.USET_STATUS_0);
            addUserRequest.setUserType(SysConstants.USET_TYPE_2);
            addUserRequest.setNickName(customer.getCustomerName());
            addUserRequest.setPhonenumber(customer.getPhoneNum());
            log.info("OrderCustomerDeal addUser add acceptNode:{}, currentNode:{}, isAcceptNode:{}",
                    orderGateway.getAcceptNode(), orderGateway.getCurrentLocationCode(), isAcceptNode);
            userId = orderSystemService.addUser(addUserRequest, isAcceptNode);
            if (userId == null) {
                throw new CustomException("userId为空，创建用户失败");
            }
        } else {
            log.info("OrderCustomerDeal addUser update acceptNode:{}, currentNode:{}, isAcceptNode:{}",
                    orderGateway.getAcceptNode(), orderGateway.getCurrentLocationCode(), isAcceptNode);
            orderSystemService.updateDeptByUserId(userId, Joiner.on(",").join(existDeptIdList), isAcceptNode, orderGateway.getCurrentLocationCode());
            log.info("OrderCustomerDeal addUser userId:{} deptIdList:{}",
                    userId, JSON.toJSONString(existDeptIdList));
        }
        return userId;
    }

    /**
     * 更新租户信息
     */
    private void addTenantInfo(OrderGatewayDTO orderGateway) {
        //创建租户
        StringBuilder sb = new StringBuilder();
        List<OrderGatewayDTO.OrderPackages> packages = orderGateway.getPackages();
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        List<TenantTradeRequest> tenantTradeList = new ArrayList<>();
        for (OrderGatewayDTO.OrderPackages packageInfo : packages) {
            OrderGatewayDTO.Application application = packageInfo.getApplication();
            //获取行业信息
            String appCode = application.getAppCode();
            String productCode = application.getProductCode();
            TenantTradeRequest tenantTradeRequest = new TenantTradeRequest();
            tenantTradeRequest.setAppVerCode(productCode);
            tenantTradeRequest.setIndustryCode(appCode);
            //这是一个坑，小心了！！！
            tenantTradeRequest.setIndustryName(application.getProductName());
            tenantTradeList.add(tenantTradeRequest);
            sb.append(appCode).append(",");
        }
        String businessOrg = orderGateway.getBusinessOrg();
        AddTenantInfoRequest addTenantInfoRequest = new AddTenantInfoRequest();
        addTenantInfoRequest.setCityId(customer.getCusRegistryCity());
        addTenantInfoRequest.setProvinceId(customer.getCusRegistryProvince());
        addTenantInfoRequest.setTradeInfo(sb.substring(0, sb.length() - 1));
        addTenantInfoRequest.setTenantId(customer.getCustomerCode());
        addTenantInfoRequest.setDeptId(businessOrg);
        addTenantInfoRequest.setTenantName(customer.getCustomerName());
        addTenantInfoRequest.setTenantTrades(tenantTradeList);
        addTenantInfoRequest.setDelFlag(OrderConstants.DELETE_FLAG_NORMAL);
        orderSystemService.addTenantInfo(addTenantInfoRequest, customer.getAdmin(), orderGateway.getHandleOrderUserInfoOnly());
    }

    /**
     * 功能描述 保存订购用户关系数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderGateway, userId, moveSign]
     */
    private void saveCustomer(OrderGatewayDTO orderGateway, String userId, int moveSign, String oldAcceptNode) {
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        String orderCode = orderGateway.getOrderCode();
        int orderStatus = orderGateway.getStatus();
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        OrderCustomerDO orderCustomerDO = new OrderCustomerDO();
        orderCustomerDO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL);
        orderCustomerDO.setUserId(userId);
        orderCustomerDO.setOrdStatus(orderStatus);
        orderCustomerDO.setTenantId(customer.getCustomerCode());
        orderCustomerDO.setCustomerName(customer.getCustomerName());
        orderCustomerDO.setAdmArea(customer.getAdmArea());
        orderCustomerDO.setAdmin(customer.getAdmin());
        orderCustomerDO.setCusRegistryCity(customer.getCusRegistryCity());
        orderCustomerDO.setCusRegistryProvince(customer.getCusRegistryProvince());
        orderCustomerDO.setBusinessOrg(orderGateway.getBusinessOrg());
        orderCustomerDO.setInvalidTime(orderGateway.getInvalidTime());
        orderCustomerDO.setOrderCode(orderCode);
        orderCustomerDO.setOrderTime(orderGateway.getOrderTime());
        orderCustomerDO.setPassword(customer.getPassword());
        orderCustomerDO.setSubscriberCode(orderGateway.getSubscriberCode());
        orderCustomerDO.setSubscriberName(orderGateway.getSubscriberName());
        orderCustomerDO.setValidTime(orderGateway.getValidTime());
        orderCustomerDO.setMoveSign(moveSign);
        orderCustomerDO.setCreateTime(DateUtils.getNowDate());
        orderCustomerDO.setUpdateTime(DateUtils.getNowDate());
        orderCustomerDO.setOrdStatus(orderGateway.getStatus());
        orderCustomerDO.setIndustryCode(packageList.get(0).getApplication().getAppCode());
        orderCustomerDO.setAppVerCode(packageList.get(0).getApplication().getProductCode());
        // 如果订单受理节点非当前节点，不更改订单受理节点信息
        if (BooleanUtils.isTrue(orderGateway.getHandleOrderUserInfoOnly()) && StringUtils.isNotEmpty(oldAcceptNode)) {
            orderCustomerDO.setAcceptNode(oldAcceptNode);
        } else {
            orderCustomerDO.setAcceptNode(orderGateway.getAcceptNode());
        }
        orderCustomerRepository.saveOrderCustomer(orderCustomerDO);
    }
}
