package com.chinatower.order.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.domain.*;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderGatewayDTO.OrderPackages;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.order.dto.OrderTransferConfigDTO;
import com.chinatower.order.handler.strategy.IOrderRelationOutOfTransactionStrategy;
import com.chinatower.order.handler.strategy.IOrderRelationStrategy;
import com.chinatower.order.handler.strategy.impl.OrderRelationLocationStrategy;
import com.chinatower.order.handler.strategy.impl.OrderRelationRenewCancelStrategy;
import com.chinatower.order.kafka.KafkaProducer;
import com.chinatower.order.mapper.SysTenantDeptMapper;
import com.chinatower.order.repository.*;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.AlgoTreeVO;
import com.chinatower.system.api.model.DeviceTreeVO;
import com.chinatower.system.api.model.OrderGatewayRspDataVO;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName OrderRelationHandler
 * @Deacription 订购关系处理器类
 * @Author chenmf
 * @Date 2022/1/7 9:57
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderRelationHandler {

    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String TOPIC = "tenantAdministrativeRegionRelationship";

    @Autowired
    private List<IOrderRelationStrategy> orderRelationStrategyList;

    @Autowired
    private List<IOrderRelationOutOfTransactionStrategy> orderRelationOutOfTransactionStrategyList;

    @Autowired
    private IOrderDeviceRepository orderDeviceRepository;

    @Autowired
    private IOrderSensorDeviceRepository orderSensorDeviceRepository;

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Autowired
    private IOrderServiceRelRepository orderServiceRelRepository;

    @Autowired
    private IOrderCustomerRepository orderCustomerRepository;

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Autowired
    private IOrderUavDeviceRepository orderUavDeviceRepository;

    @Autowired
    private IOrderUavNestDeviceRepository orderUavNestDeviceRepository;

    @Autowired
    private IOrderHornDeviceRepository orderHornDeviceRepository;

    @Autowired
    private IOrderRecorderDeviceRepository orderRecorderDeviceRepository;

    @Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;

    @Autowired
    private OrderRelationRenewCancelStrategy orderRelationRenewCancelStrategy;

    @Autowired
    private OrderRelationLocationStrategy orderRelationLocationStrategy;

    /**
     * 功能描述 处理订购关系主逻辑
     *
     * @return
     * @author chenmf
     * @date 2022/1/7
     * []
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> dealOrderRelationHandler(OrderGatewayDTO orderGateway) {
        String originalJson = JSON.toJSONString(orderGateway);
        log.info("**************订购关系推送请求报文**************：{}", originalJson);
        //校验参数信息
        checkParam(orderGateway, true);
        int status = orderGateway.getStatus();
        String customerCode = orderGateway.getCustomer().getCustomerCode();
        if (!CollectionUtils.isEmpty(orderRelationStrategyList)) {
            for (IOrderRelationStrategy orderRelationStrategy : orderRelationStrategyList) {
                if (orderRelationStrategy.judgeProcess(status)) {
                    orderRelationStrategy.orderRelationship(orderGateway);
                }
            }
        } else {
            log.error(Constants.ORDER_ERROR_MSG);
            return Result.fail(ResultCode.ERROR, Constants.ORDER_ERROR_MSG);
        }

        //更新租户名下其他信息
        authRegister(customerCode);

        //推送kafka
        try {
            List<String> industryCodeList = orderGateway.getPackages().stream().map(x -> {
                if (x.getApplication() == null) {
                    return null;
                }
                return x.getApplication().getAppCode();
            }).filter(Objects::nonNull).collect(Collectors.toList());

            JSONObject json = new JSONObject();
            json.put("tenantId", customerCode);
            json.put("industryCodes", Joiner.on(",").join(industryCodeList));
            json.put("ordStatus", orderGateway.getStatus());
            kafkaProducer.sendToKafka(TOPIC, json.toJSONString());
        } catch (Exception e) {
            log.error("send to tenantAdministrativeRegionRelationship error", e);
        }

        return Result.success(ResultCode.SUCCESS, "订购关系处理成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<String> dealOrderRelationHandler4LocationCode(OrderGatewayDTO orderGateway) {
        String originalJson = JSON.toJSONString(orderGateway);
        log.info("**************订购关系推送属地化请求报文**************：{}", originalJson);
        // 校验参数信息
        checkParam(orderGateway, false);
        // 属地化订购处理 1、中心节点记录订购订单、客户、账户信息 2、向受理节点及上级节点【除去中心节点】推送订购信息
        orderRelationLocationStrategy.orderRelationship(orderGateway);
        return Result.success(ResultCode.SUCCESS, "订购关系处理成功");
    }

    public void dealOrderRelationOutOfTransactionHandler(OrderGatewayDTO orderGateway) {
        int status = orderGateway.getStatus();
        if (!CollectionUtils.isEmpty(orderRelationOutOfTransactionStrategyList)) {
            for (IOrderRelationOutOfTransactionStrategy orderRelationStrategy : orderRelationOutOfTransactionStrategyList) {
                if (orderRelationStrategy.judgeProcess(status)) {
                    orderRelationStrategy.orderRelationship(orderGateway);
                }
            }
        } else {
            log.error(Constants.ORDER_ERROR_MSG);
        }
    }

    private void checkParam(OrderGatewayDTO orderGateway, boolean isAcceptNodeOrder) {
        int status = orderGateway.getStatus();
        List<OrderGatewayDTO.OrderPackages> packages = orderGateway.getPackages();
        if (CollectionUtils.isEmpty(packages)) {
            throw new CustomException("订购信息缺失套餐信息!");
        }
        // 受理节点
        String acceptNode = orderGateway.getAcceptNode();
        // 订购设备有效接入节点必须是受理节点及其下级节点
        List<String> accessNodeList = Lists.newArrayList(acceptNode);
        List<String> accessSubNodeList = orderCustomerRepository.queryValidAccessNodeList(acceptNode);
        if (CollectionUtils.isNotEmpty(accessSubNodeList)) {
            accessNodeList.addAll(accessSubNodeList);
        }
        // 本次订购的行业、版本信息 key -> industryCode value -> appVerCode
        Map<String, String> newIndustryInfoMap = Maps.newHashMap();
        for (OrderPackages orderPackage : packages) {
            //获取行业信息
            String appCode = orderPackage.getApplication().getAppCode();
            String productCode = orderPackage.getApplication().getProductCode();
            //验证行业编码和行业版本编码是否有效
            boolean flag = orderCustomerRepository.validAppCode(appCode, productCode, acceptNode);
            // 应用版本校验没通过，且当前节点是中心节点，且受理节点不是当前节点
            if (!flag && BooleanUtils.isTrue(orderGateway.getIsCenterButNotAcceptNode())) {
                flag = orderCustomerRepository.validLocationAppCode(appCode, productCode, acceptNode);
            }
            if (!flag) {
                log.error("appCode:{},productCode:{}无效!", appCode, productCode);
                throw new CustomException("订购信息对应行业编码[" + appCode + "]和行业版本编码[" + productCode + "]无效!");
            }
            // 校验摄像机设备接入节点必须是受理节点或其下级节点
            if (CollectionUtils.isNotEmpty(orderPackage.getDevices())) {
                for (OrderGatewayDTO.Device device : orderPackage.getDevices()) {
                    if (!accessNodeList.contains(device.getAccessNode())) {
                        throw new CustomException("订购信息-摄像机设备接入节点不是受理节点或其下级节点，不允许订购!");
                    }
                }
            }
            // 校验物联设备接入节点必须是受理节点或其下级节点、雷达设备接入节点必须是受理节点
            if (CollectionUtils.isNotEmpty(orderPackage.getSensorDevice())) {
                for (OrderGatewayDTO.SensorDevice device : orderPackage.getSensorDevice()) {
                    if (Objects.equals(device.getCategoryCode(), OrderConstants.ORDER_RADAR_CODE)) {
                        if (!Objects.equals(device.getAccessNode(), acceptNode)) {
                            throw new CustomException("订购信息-雷达设备接入节点不是受理节点，不允许订购!");
                        }
                    } else {
                        if (!accessNodeList.contains(device.getAccessNode())) {
                            throw new CustomException("订购信息-物联设备接入节点不是受理节点或其下级节点，不允许订购!");
                        }
                    }
                }
            }
            // 校验无人机设备接入节点必须是受理节点或其下级节点
            if (CollectionUtils.isNotEmpty(orderPackage.getUavDevice())) {
                for (OrderGatewayDTO.Device device : orderPackage.getUavDevice()) {
                    if (!accessNodeList.contains(device.getAccessNode())) {
                        throw new CustomException("订购信息-无人机设备接入节点不是受理节点或其下级节点，不允许订购!");
                    }
                }
            }
            // 校验喇叭设备接入节点必须是订受理节点或其下级节点
            if (CollectionUtils.isNotEmpty(orderPackage.getHornDevice())) {
                for (OrderGatewayDTO.SensorDevice device : orderPackage.getHornDevice()) {
                    if (!accessNodeList.contains(device.getAccessNode())) {
                        throw new CustomException("订购信息-喇叭设备接入节点不是受理节点或其下级节点，不允许订购!");
                    }
                }
            }

            if (newIndustryInfoMap.containsKey(appCode)) {
                // 如果新订单订购存在单行业多版本，则提示订购失败
                if (!StringUtils.equals(newIndustryInfoMap.get(appCode), productCode)) {
                    String errorMsg = String.format("订购信息同行业编码[%s]存在多版本[%s、%s], 不允许订购!", appCode, productCode, newIndustryInfoMap.get(appCode));
                    throw new CustomException(errorMsg);
                }
            } else {
                newIndustryInfoMap.put(appCode, productCode);
            }
        }
        // 只有受理节点是当前节点的订单才进行订单状态的校验，不是当前受理节点的订单，不进行订单状态的校验
        if (isAcceptNodeOrder) {
            //校验订单
            List<OrderCustomerDO> customerDOList = orderCustomerRepository.getOrderCustomerByOrderCode(orderGateway.getOrderCode());
            if (Objects.equals(OrderConstants.STATUS_FOR_ADD, status)) {
                if (!CollectionUtils.isEmpty(customerDOList)) {
                    log.error("新增订购订单编码{}已经存在", orderGateway.getOrderCode());
                    throw new CustomException("新增订购订单编码" + orderGateway.getOrderCode() + "已经存在!");
                }
            } else {
                if (CollectionUtils.isEmpty(customerDOList)) {
                    log.error("通过订单编码{}未查询到订购用户信息", orderGateway.getOrderCode());
                    throw new CustomException("通过订单编码未查询到订购用户信息!");
                }
            }
        }

        // 有效状态的订单需要判断当前租户下的行业、版本，不能出现一行业多版本的情况
        if (Objects.equals(OrderConstants.STATUS_FOR_ADD, status)
                || Objects.equals(OrderConstants.STATUS_FOR_UPDATE, status)
                || Objects.equals(OrderConstants.STATUS_FOR_ENABLE, status)) {
            List<OrderPackageDO> existOrderPackageList
                    = orderPackageRepository.selectValidIndustryInfoByTenantId(
                            orderGateway.getCustomer().getCustomerCode(), orderGateway.getOrderCode(), acceptNode);
            if (CollectionUtils.isNotEmpty(existOrderPackageList)) {
                for (OrderPackageDO orderPackageDO : existOrderPackageList) {
                    // 如果该客户已有订单存在和新订单存在相同行业不同版本号的情况，则不允许订购
                    if (newIndustryInfoMap.containsKey(orderPackageDO.getIndustryCode())
                            && !StringUtils.equals(orderPackageDO.getAppVerCode(), newIndustryInfoMap.get(orderPackageDO.getIndustryCode()))) {
                        String errorMsg = String.format("该客户行业编码[%s]已存在其他版本有效订购[%s], 不允许订购!", orderPackageDO.getIndustryCode(), orderPackageDO.getAppVerCode());
                        throw new CustomException(errorMsg);
                    }
                }
            }
        }
    }

    /**
     * 续订订购逻辑
     *
     * @param orderGateway 参数
     * @return 返回值
     */
    @Transactional(rollbackFor = Exception.class)
    public int dealRenewOrderRelationHandler(OrderGatewayDTO orderGateway) {
        // 1、校验【1、新订单不存在 2、新订单只有一个行业 + 版本 3、旧订单只有一个行业 + 版本】
        log.info("**************续订订购关系推送请求报文**************：{}", JSON.toJSONString(orderGateway));
        //校验参数信息
        checkRenewParam(orderGateway);
        // 2、旧订单配置切换到新订单上面
        transferConfig(orderGateway);
        // 3、旧订单更改订购状态
        int result = cancelOldOrder(orderGateway);
        // 4、新订单新增
        orderGateway.setStatus(OrderConstants.STATUS_FOR_ADD);
        dealOrderRelationHandler(orderGateway);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<String> dealRenewOrderRelationHandler4Location(OrderGatewayDTO orderGateway) {
        // 1、校验【1、新订单不存在 2、新订单只有一个行业 + 版本 3、旧订单只有一个行业 + 版本】
        log.info("**************续订订购关系属地化推送请求报文**************：{}", JSON.toJSONString(orderGateway));
        //校验参数信息
        checkRenewParam(orderGateway);
        // 2、旧订单配置切换到新订单上面
//        transferConfig(orderGateway);
        // 3、旧订单更改订购状态
        cancelOldOrder(orderGateway);
        // 4、新订单新增
        orderGateway.setStatus(OrderConstants.STATUS_FOR_ADD);
        return dealOrderRelationHandler4LocationCode(orderGateway);
    }

    public Result dealCancelOrderRelationHandler(OrderGatewayDTO orderGateway) {
        //标记删除订购
        cancelOldOrder(orderGateway);
        //退订同步算法和设备(巡航和预置位)
        assembleOldOrderAndCancel(orderGateway);
        return Result.success("退订处理成功");
    }

    private int cancelOldOrder(OrderGatewayDTO orderGateway) {
        String orderCode = orderGateway.getOldOrderCode();
        if (StringUtils.isEmpty(orderCode)) {
            return 0;
        }
        // 将旧订单订购表都改成退订状态
        orderPackageRepository.cancelByOrderCode(orderCode);
        orderDeviceRepository.cancelByOrderCode(orderCode);
        orderSensorDeviceRepository.cancelByOrderCode(orderCode);
        orderServiceRelRepository.cancelByOrderCode(orderCode);
        orderAlgorithmsRepository.cancelByOrderCode(orderCode);
        orderUavDeviceRepository.cancelByOrderCode(orderCode);
        orderUavNestDeviceRepository.cancelByOrderCode(orderCode);
        orderHornDeviceRepository.cancelByOrderCode(orderCode);
        orderRecorderDeviceRepository.cancelByOrderCode(orderCode);
        return orderCustomerRepository.cancelByOrderCode(orderCode);
    }

    public Result assembleOldOrderAndCancel(OrderGatewayDTO orderGateway) {
        try {
            String orderCode = orderGateway.getOldOrderCode();
            log.info("订单续订退订逻辑处理，orderCode：{}", orderCode);
            List<OrderCustomerDO> customerInfoList = orderCustomerRepository.getOrderCustomerByOrderCode(orderCode);
            if (CollectionUtils.isEmpty(customerInfoList)) {
                return Result.fail("退订查询订单失败");
            }
            OrderCustomerDO customerInfo = customerInfoList.get(0);

            OrderGatewayDTO oldGatewayDTO = assembleBaseGatewayDTO(customerInfo);
            oldGatewayDTO.setCustomer(assembleGatewayCustomer(customerInfo));
            oldGatewayDTO.setPackages(assembleGatewayPackage(orderCode));
            oldGatewayDTO.setOrderCode(orderCode);
            // oldOrderCode为后续推送无人机计划、算法接口做判断，如果是续订则不推送
            oldGatewayDTO.setOldOrderCode(orderCode);
            log.info("订单续订退订逻辑处理，param：{}", JSON.toJSONString(oldGatewayDTO));
            orderRelationRenewCancelStrategy.orderRelationship(oldGatewayDTO);
            return Result.success(ResultCode.SUCCESS, "退订关系处理成功");
        } catch (Exception e) {
            log.error("订单续订退订处理失败, e:", e);
            return Result.fail("退订关系处理失败");
        }
    }

    public Result<List<OrderGatewayRspDataVO>> dealOrderDetail(List<String> orderCodes) {
        if (CollectionUtils.isEmpty(orderCodes)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        try {
            List<OrderGatewayRspDataVO> dtoList = Lists.newArrayList();
            for (String orderCode : orderCodes) {
                log.info("查询详情订单开始 ，orderCode：{}", orderCode);
                List<OrderCustomerDO> customerInfoList = orderCustomerRepository.getOrderCustomerByOrderCode(orderCode);
                if (CollectionUtils.isEmpty(customerInfoList)) {
                    return Result.fail("查询订单详情失败");
                }
                OrderCustomerDO customerInfo = customerInfoList.get(0);
                OrderGatewayDTO orderGatewayDTO = assembleBaseGatewayDTO(customerInfo);
                orderGatewayDTO.setCustomer(assembleGatewayCustomer(customerInfo));
                OrderGatewayRspDataVO vo = new OrderGatewayRspDataVO();
                BeanUtils.copyBeanProp(vo, orderGatewayDTO);
                BeanUtils.copyBeanProp(vo.getCustomer(),orderGatewayDTO.getCustomer());
                //设备和算法单独处理
                vo.setPackages(assembleTenantPackage(orderCode));
                vo.setOrderCode(orderCode);
                assembleGatewayDeviceAndAlgo(vo);
                dtoList.add(vo);
            }
            return Result.data(dtoList);
        } catch (Exception e) {
            log.error("dealOrderDetail error,orderCodes:" + orderCodes.toString(), e);
            return Result.fail(ResultCode.ERROR);
        }

    }


    private List<OrderPackages> assembleGatewayPackage(String orderCode) {
        List<OrderPackages> packagesList = Lists.newArrayList();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderPackageDO> packageInfoList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(packageInfoList)) {
            for (OrderPackageDO orderPackageDO : packageInfoList) {
                OrderPackages orderPackages = new OrderPackages();
                orderPackages.setPackageCode(orderPackageDO.getPackageCode());
                orderPackages.setPackageName(orderPackageDO.getPackageName());
                orderPackages.setStorageTime(orderPackageDO.getStorageTime());
                orderPackages.setDescription(orderPackageDO.getDescription());
                OrderGatewayDTO.Application application = new OrderGatewayDTO.Application();
                application.setAppCode(orderPackageDO.getIndustryCode());
                application.setProductCode(orderPackageDO.getAppVerCode());
                application.setProductName(orderPackageDO.getAppVerName());
                application.setCapabilities(Splitter.on(",").splitToList(orderPackageDO.getCapabilitiesStr()));
                orderPackages.setApplication(application);
                orderPackages.setDevices(assembleGatewayDevice(orderCode, orderPackageDO.getPackageCode()));
                packagesList.add(orderPackages);
            }
        }
        return packagesList;
    }

    private List<OrderGatewayRspDataVO.OrderPackage> assembleTenantPackage(String orderCode) {
        List<OrderGatewayRspDataVO.OrderPackage> packagesList = Lists.newArrayList();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        List<OrderPackageDO> packageInfoList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(packageInfoList)) {
            for (OrderPackageDO orderPackageDO : packageInfoList) {
                OrderGatewayRspDataVO.OrderPackage orderPackage = new OrderGatewayRspDataVO.OrderPackage();
                orderPackage.setPackageCode(orderPackageDO.getPackageCode());
                orderPackage.setPackageName(orderPackageDO.getPackageName());
                orderPackage.setStorageTime(orderPackageDO.getStorageTime());
                orderPackage.setDescription(orderPackageDO.getDescription());
                OrderGatewayRspDataVO.Application application = new OrderGatewayRspDataVO.Application();
                application.setAppCode(orderPackageDO.getIndustryCode());
                application.setProductCode(orderPackageDO.getAppVerCode());
                application.setProductName(orderPackageDO.getAppVerName());
                orderPackage.setApplication(application);
                packagesList.add(orderPackage);
            }
        }
        return packagesList;
    }

    private void assembleGatewayDeviceAndAlgo(OrderGatewayRspDataVO vo) {
        if (CollectionUtils.isNotEmpty(vo.getPackages())) {
            String orderCode = vo.getOrderCode();
            Map<String, List<DeviceTreeVO>> tenantDeviceMap = orderDeviceRepository.getTenantDeviceMap(orderCode);
            Map<String, List<AlgoTreeVO>> tenantAlgoMap = orderAlgorithmsRepository.getTenantAlgoMap(orderCode);

            // 物联设备
            Map<String, List<DeviceTreeVO>> iotDeviceMap = orderSensorDeviceRepository.getTenantSensorDeviceMap(orderCode, Constants.TWO_NUM);
            // 雷达设备
            Map<String, List<DeviceTreeVO>> radarDeviceMap = orderSensorDeviceRepository.getTenantSensorDeviceMap(orderCode, Constants.ONE_NUM);
            // 无人机设备
            Map<String, List<DeviceTreeVO>> uavDeviceMap = orderUavDeviceRepository.getTenantUavDeviceMap(orderCode);
            // 喇叭设备
            Map<String, List<DeviceTreeVO>> hornDeviceMap = orderHornDeviceRepository.getTenantHornDeviceMap(orderCode);
            // 执法记录仪设备
            Map<String, List<DeviceTreeVO>> recorderDeviceMap = orderRecorderDeviceRepository.getTenantRecorderDeviceMap(orderCode);


            for (OrderGatewayRspDataVO.OrderPackage packages : vo.getPackages()) {
                if (MapUtils.isNotEmpty(tenantAlgoMap)) {
                    packages.setAlgorithms(tenantAlgoMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(tenantDeviceMap)) {
                    packages.setDevices(tenantDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(iotDeviceMap)) {
                    packages.setIotDevices(iotDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(radarDeviceMap)) {
                    packages.setRadarDevices(radarDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(uavDeviceMap)) {
                    packages.setUavDevices(uavDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(hornDeviceMap)) {
                    packages.setHornDevices(hornDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
                if (MapUtils.isNotEmpty(recorderDeviceMap)) {
                    packages.setRecorderDevices(recorderDeviceMap.get(packages.getApplication().getAppCode() + ":" + packages.getApplication().getProductCode()));
                }
            }
        }
    }

    private List<OrderGatewayDTO.Device> assembleGatewayDevice(String orderCode, String packageCode) {
        List<OrderGatewayDTO.Device> deviceList = Lists.newArrayList();
        List<OrderDeviceDO> deviceInfoList = orderDeviceRepository.getOrderDeviceByOrderCode(orderCode, packageCode);
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            for (OrderDeviceDO orderDeviceDO : deviceInfoList) {
                OrderGatewayDTO.Device device = new OrderGatewayDTO.Device();
                device.setDeviceCode(orderDeviceDO.getDeviceCode());
                device.setDevName(orderDeviceDO.getDeviceName());
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    private OrderGatewayDTO assembleBaseGatewayDTO(OrderCustomerDO customerInfo) {
        OrderGatewayDTO oldGatewayDTO = new OrderGatewayDTO();
        oldGatewayDTO.setBusinessOrg(customerInfo.getBusinessOrg());
        oldGatewayDTO.setInvalidTime(customerInfo.getInvalidTime());
        oldGatewayDTO.setOrderTime(customerInfo.getOrderTime());
        oldGatewayDTO.setSubscriberCode(customerInfo.getSubscriberCode());
        oldGatewayDTO.setSubscriberName(customerInfo.getSubscriberName());
        oldGatewayDTO.setValidTime(customerInfo.getValidTime());
        return oldGatewayDTO;
    }

    private OrderGatewayDTO.Customer assembleGatewayCustomer(OrderCustomerDO customerInfo) {
        OrderGatewayDTO.Customer customer = new OrderGatewayDTO.Customer();
        customer.setCustomerCode(customerInfo.getTenantId());
        customer.setCustomerName(customerInfo.getCustomerName());
        customer.setAdmArea(customerInfo.getAdmArea());
        customer.setAdmin(customerInfo.getAdmin());
        customer.setCusRegistryCity(customerInfo.getCusRegistryCity());
        customer.setCusRegistryProvince(customerInfo.getCusRegistryProvince());
        customer.setPassword(customerInfo.getPassword());
        return customer;
    }

    private void transferConfig(OrderGatewayDTO orderGateway) {
        // 更改配置信息，将旧订单的配置信息更新到新订单上
        for (OrderPackages packageInfo : orderGateway.getPackages()) {
            if (CollectionUtils.isEmpty(packageInfo.getDevices())) {
                continue;
            }
            OrderTransferConfigDTO param = new OrderTransferConfigDTO();
            param.setOldOrderCode(orderGateway.getOldOrderCode());
            param.setOrderCode(orderGateway.getOrderCode());
            param.setIndustryCode(packageInfo.getApplication().getAppCode());
            param.setAppVerCode(packageInfo.getApplication().getProductCode());
            param.setPackageCode(packageInfo.getPackageCode());

            List<String> deviceCodeList = packageInfo.getDevices().stream()
                    .map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toList());
            param.setDeviceCodeList(deviceCodeList);

            List<String> algorithmCodeList = packageInfo.getAlgorithms().stream()
                    .map(OrderGatewayDTO.Algorithm::getAlgorithmCode).collect(Collectors.toList());
            param.setAlgorithmCodeList(algorithmCodeList);

            log.info("订购续订逻辑，将旧订单配置转移到新订单上，param:{}", JSON.toJSONString(param));
            //预置位 vm_video_preset
            sysTenantDeptMapper.updateVideoPreset(param);
            //巡航计划vm_patrol_info_t、vm_patrol_preset_rela_t
            sysTenantDeptMapper.updatePatrolInfo(param);
            //看守位 vm_video_preset.is_guard
            //录像计划vm_channel_video_plan_t、vm_plan_time_section_t
            sysTenantDeptMapper.updateChannelVideoPlan(param);
            //前端算法配置vm_algorithm_give_config
            sysTenantDeptMapper.updateNotSecondSwitchAlgorithmGiveConfig(param);
            sysTenantDeptMapper.updateSecondSwitchAlgorithmGiveConfig(param);
            //后端算法配置vm_algorithm_config
            sysTenantDeptMapper.updateAlgorithmConfig(param);
            //订购算法配置vm_order_algorithm_config
            sysTenantDeptMapper.updateOrderAlgorithmConfig(param);

            // 将后端算法配置 vm_algorithm_config 中自动删除【modifier = "system"】的算法删除状态改为正常
            sysTenantDeptMapper.updateAlgorithmConfigStatus(orderGateway.getOrderCode());
        }


    }

    private void checkRenewParam(OrderGatewayDTO orderGateway) {
        List<OrderGatewayDTO.OrderPackages> packages = orderGateway.getPackages();
        if (CollectionUtils.isEmpty(packages)) {
            throw new CustomException("续订订购信息缺失套餐信息!");
        }
        // 受理节点
        String acceptNode = orderGateway.getAcceptNode();
        Set<String> newOrderIndustryInfoSet = Sets.newHashSet();
        for (OrderPackages orderPackage : packages) {
            if (orderPackage.getApplication() == null) {
                throw new CustomException("续订订购信息部分套餐没有行业相关信息！");
            }
            //获取行业信息
            String appCode = orderPackage.getApplication().getAppCode();
            String productCode = orderPackage.getApplication().getProductCode();
            //验证行业编码和行业版本编码是否有效
            boolean flag = orderCustomerRepository.validAppCode(appCode, productCode, acceptNode);
            // 应用版本校验没通过，且当前节点是中心节点，且受理节点不是当前节点
            if (!flag && BooleanUtils.isTrue(orderGateway.getIsCenterButNotAcceptNode())) {
                flag = orderCustomerRepository.validLocationAppCode(appCode, productCode, acceptNode);
            }
            if (!flag) {
                log.error("appCode:{},productCode:{}无效!", appCode, productCode);
                throw new CustomException("续订订购信息对应行业编码[" + appCode + "]和行业版本编码[" + productCode + "]无效!");
            }
            newOrderIndustryInfoSet.add(appCode + "#" + productCode);
        }
        //校验续订订单
        List<OrderCustomerDO> customerDOList = orderCustomerRepository.getOrderCustomerByOrderCode(orderGateway.getOrderCode());
        if (!CollectionUtils.isEmpty(customerDOList)) {
            log.error("续订新增订购订单编码{}已经存在", orderGateway.getOrderCode());
            throw new CustomException("续订新增订购订单编码" + orderGateway.getOrderCode() + "已经存在!");
        }

        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderGateway.getOldOrderCode());
        List<OrderPackageDO> oldPackageDoList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, null);
        Set<String> oldOrderIndustryInfoSet = oldPackageDoList.stream()
                .map(x -> (x.getIndustryCode() + "#" + x.getAppVerCode())).collect(Collectors.toSet());
        // 目前续订新、旧订单【行业 + 版本】必须完全一样
        if (!CollectionUtils.isEqualCollection(newOrderIndustryInfoSet, oldOrderIndustryInfoSet)) {
            log.error("续订新、旧订单行业/版本不完全一样，不进行处理, 新订单号：{}， 新订单行业信息：{}， 旧订单号：{}， 旧订单行业信息：{}",
                    orderGateway.getOrderCode(), JSON.toJSONString(newOrderIndustryInfoSet),
                    orderGateway.getOldOrderCode(), JSON.toJSONString(oldOrderIndustryInfoSet));
            throw new CustomException("续订新、旧订单行业/版本不完全一样，不进行处理，订单号:" + orderGateway.getOrderCode());
        }
    }

    /**
     * 更新租户名下其他信息
     *
     * @return void
     * @param: customerCode
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    private void authRegister(String customerCode) {
        //查询客户下有效的设备、通道列表，替换redis缓存内容 start
        List<String> devList = orderDeviceRepository.getValidDev(customerCode);
        if (!CollectionUtils.isEmpty(devList)) {
            RedisUtil.set(customerCode + OrderConstants._DEVLIST, JSON.toJSONString(devList), RedisDBType.PRIMARY);
        }
        List<String> chnList = orderDeviceRepository.getValidChn(customerCode);
        if (!CollectionUtils.isEmpty(chnList)) {
            RedisUtil.set(customerCode + OrderConstants._CHNLIST, JSON.toJSONString(chnList), RedisDBType.PRIMARY);
        }
    }

}
