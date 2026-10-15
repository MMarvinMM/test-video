package com.chinatower.order.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.common.core.domain.OrderPackageDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.dto.TransferIndustryDTO;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.order.handler.OrderRelationHandler;
import com.chinatower.order.mapper.OrderCustomerMapper;
import com.chinatower.order.repository.IOrderAlgorithmConfigRepository;
import com.chinatower.order.repository.IOrderCustomerRepository;
import com.chinatower.order.service.IVideoOrderInfoService;
import com.chinatower.order.util.GenIncrIdUtil;
import com.chinatower.order.vo.OrderAddRsp;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.order.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysUserResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @ClassName VideoOrderController
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/11 10:07
 * @Version 1.0
 **/

/**
 * video-order
 */
@Api(tags = "订单订购模块")
@RestController
@Slf4j
public class VideoOrderController extends TowerBaseController {

    @Autowired
    private OrderRelationHandler orderRelationHandler;

    @Autowired
    private IVideoOrderInfoService videoOrderInfoService;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private GenIncrIdUtil genIncrIdUtil;
    @Autowired
    private OrderCustomerMapper orderCustomerMapper;
    @Autowired
    private IOrderCustomerRepository iOrderCustomerRepository;

    @Autowired
    private IOrderAlgorithmConfigRepository orderAlgorithmConfigRepository;

    /**
     * 订购关系全量推送接口
     * 5.4.2.订购关系全量推送
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "订购关系全量推送")
    @PostMapping("/video/message/orderRelationship")
    public Result<String> orderRelationship(@Validated @RequestBody OrderRelationshipDTO dto) {
        OrderGatewayDTO orderGateway = dto.getData();
        try {
            if (orderGateway == null) {
                log.error("订购关系全量推送失败, 参数为空");
                return Result.fail(ResultCode.ERROR, "参数为空");
            }
            log.info("**************订购关系推送请求原始报文**************：{}", JSON.toJSONString(orderGateway));
            // 处理原始订购报文，将无人机、大喇叭设备从原订购报文对象中分离出来，减少新设备对老逻辑的影响
            handleOrderGateWayInfo(orderGateway);
            // 初始化处理状态
            orderGateway.setHandleOrderUserInfoOnly(false);
            orderGateway.setIsCenterButNotAcceptNode(false);
            orderGateway.setAddDeviceMap(null);
            orderGateway.setAddSensorDeviceMap(null);
            orderGateway.setAddUavDeviceMap(null);
            orderGateway.setAddHornDeviceMap(null);
            orderGateway.setAddRecordDeviceMap(null);

            orderGateway.setReduceDeviceList(null);
            orderGateway.setReduceAlgorithmConfigDeviceList(null);
            orderGateway.setReduceAlgorithmConfigUavDeviceList(null);
            orderGateway.setReduceUavDeviceList(null);

            // 根据订单号及算法配置信息标识订单类型
            orderGateway.setOrderType(resolveOrderType(orderGateway));

            // 如果受理节点是当前节点 或 当前节点的父级【非中心节点】
            String currentLocationCode = getCurrentLocationCode();
            String orderLocationCode = orderGateway.getAcceptNode();
            // 订单受理节点父节点列表【去除中心节点】
            List<String> parentLocationCodeList = iOrderCustomerRepository.getParentCodeListByLocationCode(orderLocationCode);
            // 当前节点级别
            String currentLocationLevel = orderCustomerMapper.getLocationLevelByCode(currentLocationCode);
            // 受理节点级别
            String orderLocationLevel = orderCustomerMapper.getLocationLevelByCode(orderLocationCode);
            orderGateway.setCurrentLocationCode(currentLocationCode);

            // 如果旧订单号不为空，走续订逻辑
            if (!StringUtils.isEmpty(orderGateway.getOldOrderCode())) {

                Result<String> result;
                // 如果当前节点是受理节点，或者没有传受理节点
                if (StringUtils.isEmpty(orderLocationCode)
                        || org.apache.commons.lang3.StringUtils.equals(orderLocationCode, currentLocationCode)) {
                    // 处理全量订购续订逻辑
                    int handleResult = orderRelationHandler.dealRenewOrderRelationHandler(orderGateway);
                    // 如果旧订单是有效状态，事务外处理旧订单退订相关逻辑
                    if (handleResult > 0) {
                        orderRelationHandler.assembleOldOrderAndCancel(orderGateway);
                    }
                    result = Result.success(ResultCode.SUCCESS, "订购关系处理成功");
                }
                // 如果当前节点是受理节点的父节点，非中心节点
                else if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(parentLocationCodeList) && parentLocationCodeList.contains(currentLocationCode)) {
                    orderGateway.setHandleOrderUserInfoOnly(true);
                    result = orderRelationHandler.dealRenewOrderRelationHandler4Location(orderGateway);
                }
                // 如果当前节点是中心节点 且 受理节点不是中心节点，则记录客户、订单相关信息，向受理节点及其父节点进行推送
                else if (org.apache.commons.lang3.StringUtils.equals(currentLocationLevel, Constants.ZERO)
                        && !org.apache.commons.lang3.StringUtils.equals(orderLocationLevel, Constants.ZERO)) {
                    orderGateway.setHandleOrderUserInfoOnly(true);
                    orderGateway.setIsCenterButNotAcceptNode(true);
                    result = orderRelationHandler.dealRenewOrderRelationHandler4Location(orderGateway);
                }
                // 不满足以上受理节点条件，不进行处理
                else {
                    log.info("订购受理节点不处理, currentLocationCode:{}, orderLocationCode:{}", currentLocationCode, orderLocationCode);
                    result = Result.fail("订购受理节点不处理");
                }
                return result;
            } else {
                Result<String> result;
                // 如果当前节点是受理节点，或者没有传受理节点
                if (StringUtils.isEmpty(orderLocationCode)
                        || org.apache.commons.lang3.StringUtils.equals(orderLocationCode, currentLocationCode)) {
                    // 处理全量订购逻辑
                    result = orderRelationHandler.dealOrderRelationHandler(orderGateway);
                    // 处理全量订购事务外逻辑【调用外部接口时避免因为订购事务未提交导致查询相关数据不准确的场景】
                    orderRelationHandler.dealOrderRelationOutOfTransactionHandler(orderGateway);
                }
                // 如果当前节点是受理节点的父节点，非中心节点
                else if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(parentLocationCodeList) && parentLocationCodeList.contains(currentLocationCode)) {
                    orderGateway.setHandleOrderUserInfoOnly(true);
                    result = orderRelationHandler.dealOrderRelationHandler4LocationCode(orderGateway);
                }
                // 如果当前节点是中心节点 且 受理节点不是中心节点，则记录客户、订单相关信息，向受理节点及其父节点进行推送
                else if (org.apache.commons.lang3.StringUtils.equals(currentLocationLevel, Constants.ZERO)
                        && !org.apache.commons.lang3.StringUtils.equals(orderLocationLevel, Constants.ZERO)) {
                    orderGateway.setHandleOrderUserInfoOnly(true);
                    orderGateway.setIsCenterButNotAcceptNode(true);
                    result = orderRelationHandler.dealOrderRelationHandler4LocationCode(orderGateway);
                }
                // 不满足以上受理节点条件，不进行处理
                else {
                    log.info("订购受理节点不处理, currentLocationCode:{}, orderLocationCode:{}", currentLocationCode, orderLocationCode);
                    result = Result.fail("订购受理节点不处理");
                }
                return result;
            }
        } catch (Exception e) {
            log.error("订购关系全量推送失败, e:", e);
            return Result.fail(ResultCode.ERROR, e.getMessage());
        }
    }

    private static final String CONFIG_PLAT_CODE = "platform_code";

    /**
     * 根据 vm_order_algorithm_config 是否有该订单记录 及 algorithmConfigList 是否为空，标识订单类型：
     * 1-非精准转非精准 2-非精准转精准 3-精准转非精准 4-精准转精准
     */
    private Integer resolveOrderType(OrderGatewayDTO orderGateway) {
        boolean hasConfig = !CollectionUtils.isEmpty(orderAlgorithmConfigRepository.selectByOrderCode(orderGateway.getOrderCode()));
        boolean hasAlgorithm = !CollectionUtils.isEmpty(orderGateway.getAlgorithmConfigList());
        if (!hasConfig && !hasAlgorithm) {
            return 1;
        }
        if (!hasConfig) {
            return 2;
        }
        if (!hasAlgorithm) {
            return 3;
        }
        return 4;
    }

    private String getCurrentLocationCode() {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(CONFIG_PLAT_CODE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (Objects.isNull(sysConfig)) {
                throw new CustomException("当前平台编码未配置，请配置后重试");
            }
            return sysConfig.getConfigValue();
        }
        else {
            throw new CustomException("获取平台编码配置失败，请稍后重试！");
        }
    }

    private void handleOrderGateWayInfo(OrderGatewayDTO orderGateway) {
        if (!CollectionUtils.isEmpty(orderGateway.getPackages())) {
            for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
                // 将OrderGatewayDTO.Device中的设备分成摄像机、无人机两个设备列表
                List<OrderGatewayDTO.Device> deviceList = orderPackage.getDevices();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    // IPC 0-NVR;2-AR摄像头；3-热成像摄像头；4-人脸卡口；5-车辆卡口；6-卡口主机 7-CVR，8-执法记录仪
                    //100:无人固定翼飞机、101:无人垂直起降飞机、102:无人飞艇、103:无人直升机、104:无人多旋翼飞行器、105无人伞翼机
                    orderPackage.setDevices(deviceList.stream()
                            .filter(x -> (x.getCategoryCode() == null || (x.getCategoryCode() < 100 && !Objects.equals(x.getCategoryCode(), 8L)))).collect(Collectors.toList()));
                    orderPackage.setUavDevice(deviceList.stream()
                            .filter(x -> (x.getCategoryCode() != null && x.getCategoryCode() >= 100)).collect(Collectors.toList()));
                    orderPackage.setRecorderDevice(deviceList.stream()
                            .filter(x -> (Objects.equals(x.getCategoryCode(), 8L))).collect(Collectors.toList()));
                }

                // 将OrderGatewayDTO.SensorDevice中的设备分成物联设备、大喇叭两个设备列表
                List<OrderGatewayDTO.SensorDevice> sensorDeviceList = orderPackage.getSensorDevice();
                if (!CollectionUtils.isEmpty(sensorDeviceList)) {
                    // 1-岸基雷达；2-传感器；3-云广播
                    orderPackage.setSensorDevice(sensorDeviceList.stream()
                            .filter(x -> (Objects.equals(x.getCategoryCode(), OrderConstants.ORDER_RADAR_CODE)
                                    || Objects.equals(x.getCategoryCode(), OrderConstants.ORDER_SENSOR_CODE)))
                            .collect(Collectors.toList()));
                    orderPackage.setHornDevice(sensorDeviceList.stream()
                            .filter(x -> Objects.equals(x.getCategoryCode(), OrderConstants.ORDER_HORN_CODE))
                            .collect(Collectors.toList()));
                } else {
                    // 避免特殊情况空指针
                    orderPackage.setHornDevice(Lists.newArrayList());
                }
            }
        }
    }

    /**
     * 根据租户、设备条件获取订单信息接口
     *
     * @return
     * @author chenmf
     * @date 2022/1/19
     * [videoOrderReq]
     */
    @PostMapping(value = "/video/order/getVideoOrderInfo")
    public Result<VideoOrderResp> getVideoOrderInfo(@Validated @RequestBody VideoOrderReq videoOrderReq) {
        VideoOrderResp videoOrderResp = videoOrderInfoService.getVideoOrderInfo(videoOrderReq);
        return Result.data(videoOrderResp);
    }


    /**
     * 分页获取租户订购信息接口
     *
     * @return
     * @author chenmf
     * @date 2022/1/27
     * [videoOrderReq]
     */
    @DataScope(tenantColumn = "cp.tenant_id", industryCode = "cp.industry_code", acceptColumn = "cp.accept_node", deptColumn = "")
    @PostMapping(value = "/video/order/relationshipListByTenantId")
    public Result<TenantOrderInfoVO> getOrderRelationshipListByTenantId(@Validated @RequestBody QueryOrderRelationshipVO queryOrderRelationshipVO) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || StringUtils.isEmpty(loginUser.getUser())) {
                // 无法获取当前登录人，返回错误信息
                return Result.fail("无法获取当前登录用户信息，请重新登陆");
            }
            SysUser sysUser = loginUser.getUser();

            TenantOrderInfoVO tenantOrderInfoVO = new TenantOrderInfoVO();

            tenantOrderInfoVO.setTenantName(sysUser.getTenantInfo().getTenantName());
            tenantOrderInfoVO.setTenantId(sysUser.getTenantId());
            tenantOrderInfoVO.setTenantManager(sysUser.getUserName());
            List<SysUserResponse> sysUserList = systemFeignClient.selectUserByTenantId(sysUser.getTenantId()).getData();
            if (!CollectionUtils.isEmpty(sysUserList)) {
                Optional<SysUserResponse> optional = sysUserList.stream().filter(user -> "1".equals(user.getIsTenantAdmin())).findAny();
                if (optional.isPresent()) {
                    tenantOrderInfoVO.setTenantManager(optional.get().getUserName());
                }
            }
            // 默认只查当前客户登录节点的订购信息
            queryOrderRelationshipVO.setLocationCode(sysUser.getPlatformCode());
            List<OrderInfoVO> orderInfoVOList = videoOrderInfoService.getOrderRelationshipListByTenantId(queryOrderRelationshipVO);
            tenantOrderInfoVO.setOrderInfoVOList(this.getDataTable(orderInfoVOList));
            return Result.data(tenantOrderInfoVO);
        } catch (Exception e) {
            log.error("分页获取租户订购信息失败, e:", e);
            return Result.fail("查询租户订购信息失败");
        }
    }

    /**
     * 查询订购算法列表信息接口
     *
     * @return
     * @author chensw
     * @date 2022/2/11
     */
    @PostMapping("/video/order/queryAlgorithmList")
    public Result<List<OrderAlgorithmsVO>> queryAlgorithmList(@RequestBody CustomerDTO customerDTO) {
        try {
            List<OrderAlgorithmsVO> list = videoOrderInfoService.queryAlgorithmList(customerDTO);
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询订购算法列表失败, e:", e);
            return Result.fail("查询订购算法列表失败");
        }
    }


    /**
     * 根据用户编号查询用户订购的行业版本下的设备编号
     *
     * @param findDeviceCodeVO
     * @return 设备编号列表
     * @author chenmf
     * @date 2022/1/27
     */
    @PostMapping(value = "/video/order/findDeviceCodeByUser")
    public Result<List<String>> findDeviceCodeByUser(@Validated @RequestBody FindDeviceCodeVO findDeviceCodeVO) {
        try {
            List<String> list = videoOrderInfoService.findDeviceCodeByUser(findDeviceCodeVO);
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询用户订购的行业版本下的设备编号失败, e:", e);
            return Result.fail("查询用户订购的行业版本下的设备编号失败");
        }
    }

    /**
     * 属地化新增客户接口
     * @param customerAddVO
     * @return
     */
    @PostMapping(value = "/video/order/addCustomer")
    public Result<Integer> addCustomer(@RequestBody CustomerAddVO customerAddVO) {
        return  videoOrderInfoService.addCustomer(customerAddVO);
    }

    /**
     * 属地化删除客户接口
     * @param tenantId
     * @return
     */
    @PostMapping(value = "/video/order/deleteCustomer/{tenantId}")
    public Result deleteCustomer(@PathVariable(value = "tenantId") String tenantId) {
        return videoOrderInfoService.deleteCustomer(tenantId);
    }

    /**
     * 根据客户编码查询客户基本信息接口
     * @param tenantId
     * @return
     */
    @PostMapping(value = "/video/order/getCustomerInfo/{tenantId}")
    public Result getCustomerInfo(@PathVariable(value = "tenantId") String tenantId) {
        return videoOrderInfoService.getCustomerInfo(tenantId);
    }

    /**
     * 属地化更新客户接口
     * @param customerAddVO
     * @return
     */
    @PostMapping(value = "/video/order/updateCustomer")
    public Result updateCustomer(@RequestBody CustomerAddVO customerAddVO) {
        return videoOrderInfoService.updateCustomer(customerAddVO);
    }

    /**
     * 验证客户是否存在订单接口-1
     * @param tenantId
     * @return
     */
    @PostMapping(value = "/video/order/checkOrderExist/{tenantId}")
    public Result checkOrderExist(@PathVariable(value = "tenantId") String tenantId) {
        return videoOrderInfoService.checkOrderExist(tenantId);
    }

    /**
     * 属地化业务开通订购接口
     * @param params 参数
     * @return 返回值
     */
    @PostMapping(value = "/video/order/addOrUpdateOrder")
    public Result addOrUpdateOrder(@RequestBody List<OrderRelationshipDTO> params) {
        log.info("**************属地化插入订购报文**************：{}", params.size());
        Result result = videoOrderInfoService.dealOrder(params);
        if (result.getCode() != ResultCode.SUCCESS.getCode()) {
            return result;
        }
        // 当前节点
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        log.info("VideoOrderController addOrUpdateOrder platformCode:{}", platformCode);
        List<OrderAddRsp> list =Lists.newArrayList();
        for (OrderRelationshipDTO param : params) {
            String industryName = param.getData().getPackages().get(0).getPackageName();
            OrderAddRsp rsp = new OrderAddRsp();
            rsp.setPackageName(industryName);
            try {
                OrderGatewayDTO paramData = param.getData();
                // 订单受理节点设置为当前节点
                paramData.setAcceptNode(platformCode);
                Result<String> dealResult;
                if (StringUtils.isEmpty(paramData.getOldOrderCode())) {
                    //订购或者变更
                    dealResult = orderRelationHandler.dealOrderRelationHandler(paramData);
                } else {
                    //退订
                    dealResult = orderRelationHandler.dealCancelOrderRelationHandler(paramData);
                }
                rsp.setResult(dealResult);
            } catch (Exception e) {
                log.error("VideoOrderController.addOrUpdateOrder error", e);
                rsp.setResult(Result.fail(ExceptionUtils.getMessage(e)));
            }
            list.add(rsp);
        }
        log.info("VideoOrderController addOrUpdateOrder finish");
        return Result.data(list);
    }

    /**
     * 属地化查询客户订购信息接口
     * @param tenantId
     * @return
     */
    @PostMapping(value = "/video/order/getOrderList/{tenantId}")
    public Result getOrderListByTenantId(@PathVariable String tenantId) {
        Result<List<OrderPackageDO>> result = videoOrderInfoService.getOrderCodesByTenantId(tenantId);
        if (result.getCode() != ResultCode.SUCCESS.getCode()) {
            return result;
        }
        if(!CollectionUtils.isEmpty(result.getData())){
            return orderRelationHandler.dealOrderDetail(result.getData().stream().map(OrderPackageDO::getOrderCode).collect(Collectors.toList()));
        }
        return Result.data(Lists.newArrayList());
    }

    /**
     * 属地化自动生成客户编码接口
     * @return 返回值
     */
    @PostMapping(value = "/video/order/genCustomerId")
    public Result<String> genCustomerId() {
        return Result.data(genIncrIdUtil.getIncrIdByDB("TY"));
    }

    /**
     * 客户行业信息转换接口
     * @param param 参数
     * @return 返回值
     */
    @PostMapping(value = "/video/order/transferIndustry")
    public Result<String> transferIndustry(@RequestBody TransferIndustryDTO param) {
        try {
            // 待处理的受理节点
            String acceptNode = param.getAcceptNode();
            // 当前节点
            String currentLocationCode = getCurrentLocationCode();
            // 订单受理节点父节点列表【去除中心节点】
            List<String> parentLocationCodeList = iOrderCustomerRepository.getParentCodeListByLocationCode(acceptNode);
            // 当前节点级别
            String currentLocationLevel = orderCustomerMapper.getLocationLevelByCode(currentLocationCode);
            // 受理节点级别
            String orderLocationLevel = orderCustomerMapper.getLocationLevelByCode(acceptNode);

            // 如果当前节点是受理节点，或者没有传受理节点
            if (StringUtils.isEmpty(acceptNode)
                    || org.apache.commons.lang3.StringUtils.equals(acceptNode, currentLocationCode)) {
                // 事务执行
                videoOrderInfoService.transferIndustry(param);
                // 事务外执行
                videoOrderInfoService.transferIndustryOutOfTransaction(param);
            }
            // 如果当前节点是受理节点的父节点，非中心节点
            else if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(parentLocationCodeList)
                    && parentLocationCodeList.contains(currentLocationCode)) {
                // 处理订单数据，不推送
                videoOrderInfoService.transferOrderIndustry(param, false);
            }
            // 如果当前节点是中心节点 且 受理节点不是中心节点
            else if (org.apache.commons.lang3.StringUtils.equals(currentLocationLevel, Constants.ZERO)
                    && !org.apache.commons.lang3.StringUtils.equals(orderLocationLevel, Constants.ZERO)) {
                // 处理订单数据，向处理节点及其父节点进行推送
                videoOrderInfoService.transferOrderIndustry(param, true);
            }
            // 不满足以上受理节点条件，不进行处理
            else {
                log.info("行业受理节点不处理, currentLocationCode:{}, acceptNode:{}", currentLocationCode, acceptNode);
            }

            return Result.success("执行成功");
        } catch (Exception e) {
            log.error("VideoOrderController transferIndustry 执行失败, e:", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 分页获取租户订购信息接口
     *
     * @return
     * @author chenmf
     * @date 2022/1/27
     * [videoOrderReq]
     */
    @DataScope(tenantColumn = "cp.tenant_id", industryCode = "cp.industry_code", acceptColumn = "cp.accept_node", deptColumn = "")
    @PostMapping(value = "/video/order/relationshipListByTenantIdV2")
    public Result<TenantOrderInfoVO> getOrderRelationshipListByTenantIdV2(@Validated @RequestBody QueryOrderRelationshipVO queryOrderRelationshipVO) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || StringUtils.isEmpty(loginUser.getUser())) {
                // 无法获取当前登录人，返回错误信息
                return Result.fail("无法获取当前登录用户信息，请重新登陆");
            }
            SysUser sysUser = loginUser.getUser();

            TenantOrderInfoVO tenantOrderInfoVO = new TenantOrderInfoVO();

            tenantOrderInfoVO.setTenantName(sysUser.getTenantInfo().getTenantName());
            tenantOrderInfoVO.setTenantId(sysUser.getTenantId());
            tenantOrderInfoVO.setTenantManager(sysUser.getUserName());
            List<SysUserResponse> sysUserList = systemFeignClient.selectUserByTenantId(sysUser.getTenantId()).getData();
            if (!CollectionUtils.isEmpty(sysUserList)) {
                Optional<SysUserResponse> optional = sysUserList.stream().filter(user -> "1".equals(user.getIsTenantAdmin())).findAny();
                if (optional.isPresent()) {
                    tenantOrderInfoVO.setTenantManager(optional.get().getUserName());
                }
            }
            // 默认只查当前客户登录节点的订购信息
            queryOrderRelationshipVO.setLocationCode(sysUser.getPlatformCode());
            List<OrderInfoVO> orderInfoVOList = videoOrderInfoService.getSimpleOrderRelationshipListByTenantId(queryOrderRelationshipVO);
            tenantOrderInfoVO.setOrderInfoVOList(this.getDataTable(orderInfoVOList));
            return Result.data(tenantOrderInfoVO);
        } catch (Exception e) {
            log.error("分页获取租户订购信息失败, e:", e);
            return Result.fail("查询租户订购信息失败");
        }
    }

    /**
     * 分页获取订购摄像机
     *
     * @param queryOrderRelationshipVO 查询订单关系的请求参数对象
     * @return 包含订单设备信息的分页数据
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", acceptColumn = "a.accept_node")
    @PostMapping(value = "/video/order/getOrderDeviceInfosPage")
    public TableDataInfo getAppDataPage(@Validated @RequestBody QueryOrderRelationshipVO queryOrderRelationshipVO) {
        return this.getDataTable(videoOrderInfoService.getOrderDeviceInfos(queryOrderRelationshipVO));
    }

    /**
     * 分页获取订购物联设备
     *
     * @param queryOrderRelationshipVO 查询订单关系的请求参数对象
     * @return 包含订单设备信息的分页数据
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", acceptColumn = "a.accept_node")
    @PostMapping(value = "/video/order/getOrderSensorDeviceInfosPage")
    public TableDataInfo getOrderSensorDeviceInfosPage(@Validated @RequestBody QueryOrderRelationshipVO queryOrderRelationshipVO) {
        return this.getDataTable(videoOrderInfoService.getOrderSensorDeviceInfos(queryOrderRelationshipVO));
    }
}
