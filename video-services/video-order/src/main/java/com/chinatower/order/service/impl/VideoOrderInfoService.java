package com.chinatower.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.domain.*;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.dto.OrderRelationshipDTO;
import com.chinatower.common.core.dto.TransferIndustryDTO;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.SnowflakeIdWorker;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.BasicDeviceDTO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.order.api.IOrderRemoteService;
import com.chinatower.order.domain.HornDeviceDO;
import com.chinatower.order.domain.IotDeviceDO;
import com.chinatower.order.domain.RecorderDeviceDO;
import com.chinatower.order.domain.UavDeviceDO;
import com.chinatower.order.dto.SysDeptHisMongoDTO;
import com.chinatower.order.dto.SysTenantInfoHisMongoDTO;
import com.chinatower.order.dto.SysUserHisMongoDTO;
import com.chinatower.order.mapper.*;
import com.chinatower.order.mongodb.SysDeptHisMongoDAO;
import com.chinatower.order.mongodb.SysTenantInfoHisMongoDAO;
import com.chinatower.order.mongodb.SysUserHisMongoDAO;
import com.chinatower.order.mongodb.constants.MongoTableConstants;
import com.chinatower.order.repository.*;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.order.service.IOrderSystemService;
import com.chinatower.order.service.IVideoOrderInfoService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.order.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.order.repository.IOrderAlgorithmConfigRepository;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName VideoOrderService
 * @Deacription 订购服务类
 * @Author chenmf
 * @Date 2022/1/6 16:04
 * @Version 1.0
 **/
@Slf4j
@Service
public class VideoOrderInfoService implements IVideoOrderInfoService {

    @Autowired
    private IOrderDeviceRepository orderDeviceRepository;

    @Autowired
    private IOrderCustomerRepository orderCustomerRepository;

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Autowired
    private IOrderAlgorithmsRepository orderAlgorithmsRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IOrderSensorDeviceRepository orderSensorDeviceRepository;
    @Autowired
    private IOrderUavDeviceRepository orderUavDeviceRepository;
    @Autowired
    private IOrderUavNestDeviceRepository orderUavNestDeviceRepository;
    @Autowired
    private IOrderHornDeviceRepository orderHornDeviceRepository;
    @Autowired
    private IOrderRecorderDeviceRepository orderRecorderDeviceRepository;

    @Autowired
    private IOrderAlgorithmConfigRepository orderAlgorithmConfigRepository;

    @Autowired
    private IOrderServiceRelRepository orderServiceRelRepository;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysTenantInfoMapper sysTenantInfoMapper;
    @Autowired
    private WarningInfoMapper warningInfoMapper;
    @Autowired
    private StatisticsInfoMapper statisticsInfoMapper;
    @Autowired
    private IOrderSystemService orderSystemService;
    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;
    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;
    @Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;
    @Autowired
    private IIotDeviceRepository iIotDeviceRepository;
    @Autowired
    private IUavDeviceRepository iUavDeviceRepository;
    @Autowired
    private IHornDeviceRepository iHornDeviceRepository;
    @Autowired
    private IRecorderDeviceRepository iRecorderDeviceRepository;
    @Resource
    private SysTenantInfoHisMongoDAO sysTenantInfoHisMongoDAO;
    @Resource
    private SysUserHisMongoDAO sysUserHisMongoDAO;
    @Resource
    private SysDeptHisMongoDAO sysDeptHisMongoDAO;

    private static final String DEPT_TREE_ROOT_ID = "000000";

    /**
     * 功能描述 获取订单编码
     *
     * @return
     * @author chenmf
     * @date 2022/1/19
     * [videoOrderReq]
     */
    @Override
    public VideoOrderResp getVideoOrderInfo(VideoOrderReq videoOrderReq) {

        String customerCode = videoOrderReq.getTenantId();
        String deviceCode = videoOrderReq.getDeviceCode();
        String channelCode = videoOrderReq.getChannelCode();
        String industryCode = videoOrderReq.getIndustryCode();
        return orderDeviceRepository.getOrderCode(customerCode, deviceCode, channelCode, industryCode);
    }

    /**
     * 功能描述 分页获取租户订购信息
     *
     * @return
     * @author chenmf
     * @date 2022/1/27
     * [customerDTO]
     */
    @Override
    public List<OrderInfoVO> getOrderRelationshipListByTenantId(QueryOrderRelationshipVO queryOrderRelationshipVO) {

        if (!StringUtils.isEmpty(queryOrderRelationshipVO.getOrderStatus())) {
            String[] orderStatusArr = queryOrderRelationshipVO.getOrderStatus().split(",");
            queryOrderRelationshipVO.setOrderStatusArr(orderStatusArr);
        }
        PageMethod.startPage(queryOrderRelationshipVO.getPageNum(), queryOrderRelationshipVO.getPageSize());
        //查询用户订购订单列表
        List<OrderInfoVO> orderInfoVOList = orderCustomerRepository.getOrderCustomerByParam(queryOrderRelationshipVO);
        if (!CollectionUtils.isEmpty(orderInfoVOList)) {
            //获取赠送算法
            Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
            List<SysDictDataResponse> dictList = sysResult.getData();

            List<String> orderCodeList = orderInfoVOList.stream().map(OrderInfoVO::getOrderCode).distinct().collect(Collectors.toList());

            //查询对应订单套餐列表
            queryOrderRelationshipVO.setOrderCodeList(orderCodeList);
            //查询对应订单套餐列表
            List<OrderPackageDO> allOrderPackageList = orderPackageRepository.getOrderPackageInfoByOrderCode(queryOrderRelationshipVO);

            if (CollectionUtils.isNotEmpty(allOrderPackageList)) {
                //所有packageCode
                List<String> packageList = allOrderPackageList.stream().map(OrderPackageDO::getPackageCode).distinct().collect(Collectors.toList());

                //批量查询订单设备信息列表
                List<OrderDevicesVO> allOrderDeviceInfoList = orderDeviceRepository.getDeviceInfoByList(orderCodeList, packageList);
//          查询对应订单物联网设备信息列表  20220719 合并 单雷达列表
                List<OrderSensorDeviceDO> allOrderSensorDeviceInfoList = orderSensorDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                //查询对应订单算法列表
                List<OrderAlgorithmsDO> allOrderAlgorithmsList = orderAlgorithmsRepository.getOrderAlgorithmsByList(orderCodeList, packageList);
                //查询对应订单应用列表
                List<OrderPackageDO> allOrderAppList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, packageList);
                //查询对应订单卫星列表
                List<OrderServiceRelDO> allOrderServiceRelDOList = orderServiceRelRepository.getOrderServiceRelByList(orderCodeList, packageList);
                // 查询对应订单无人机设备列表
                List<OrderUavDeviceDO> allOrderUavDeviceList = orderUavDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                // 查询对应订单喇叭设备列表
                List<OrderHornDeviceDO> allOrderHornDeviceList = orderHornDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                // 查询对应订单执法记录仪设备列表
                List<OrderRecorderDeviceDO> allOrderRecorderDeviceList = orderRecorderDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                //全部的区县批量
                List<String> countyCodeList = allOrderServiceRelDOList.stream().map(OrderServiceRelDO::getCountyCode).distinct().collect(Collectors.toList());
                //根据区县编码批量查询
                List<OrderServiceRelVO> allAreaVOList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(countyCodeList)) {
                    allAreaVOList = orderServiceRelRepository.getAreaInfo(countyCodeList);
                }

                for (OrderInfoVO orderInfoVO : orderInfoVOList) {
                    String orderCode = orderInfoVO.getOrderCode();
                    //List<OrderPackageDO> orderPackageList = orderPackageRepository.getOrderPackageInfoByOrderCode(queryOrderRelationshipVO);
                    List<OrderPackageDO> orderPackageList = allOrderPackageList.stream().filter(item -> Objects.equals(item.getOrderCode(), orderCode)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(orderPackageList)) {
                        List<OrderPackageVO> packageData = new ArrayList<>();
                        for (OrderPackageDO orderPackageDO : orderPackageList) {

                            String packageCode = orderPackageDO.getPackageCode();
                            //查询对应订单设备信息列表
                            List<OrderDevicesVO> orderDeviceInfoList = allOrderDeviceInfoList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
                            //查询对应订单物联网设备信息列表 20220719 合并 单雷达列表
                            List<OrderSensorDeviceDO> orderSensorDeviceInfoList = allOrderSensorDeviceInfoList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());

                            //组装订单套餐信息
                            OrderPackageVO orderPackageVO = assembleSimpleOrderPackage(orderPackageDO);
                            //组装订单应用信息
                            assembleAppData(allOrderAppList, orderPackageVO, orderCode, packageCode);
                            //组装订单算法信息
                            assembleAlgorithmData(allOrderAlgorithmsList, orderPackageVO, dictList, orderCode, packageCode);
                            //组装雷达信息
                            assembleRadarData(allOrderSensorDeviceInfoList, orderPackageVO, orderCode, packageCode);
                            //组装无人机信息
                            assembleUavDeviceData(allOrderUavDeviceList, orderPackageVO, orderCode, packageCode, Maps.newHashMap());
                            //组装喇叭信息
                            assembleHornDeviceData(allOrderHornDeviceList, orderPackageVO, orderCode, packageCode);
                            //组装执法记录仪信息
                            assembleRecorderDeviceData(allOrderRecorderDeviceList, orderPackageVO, orderCode, packageCode);
                            //组装卫星信息
                            assembleSatelliteData(allOrderServiceRelDOList, orderPackageVO, allAreaVOList, orderCode, packageCode);

                            //组装订单设备信息
                            if (!CollectionUtils.isEmpty(orderDeviceInfoList)) {
                                orderDeviceInfoList = orderDeviceInfoList.stream()
                                        .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                                                new TreeSet<>(Comparator.comparing(OrderDevicesVO::getDeviceCode))), ArrayList::new));
                                orderPackageVO.setDeviceData(orderDeviceInfoList);
                            }

                            //组装物联网订单设备信息
                            if (!CollectionUtils.isEmpty(orderSensorDeviceInfoList)) {
                                List<OrderSensorDeviceDO> sensorInfoList = orderSensorDeviceInfoList.stream().filter(sensor ->
                                        Objects.equals(OrderConstants.ORDER_SENSOR_CODE, sensor.getCategoryCode())).collect(Collectors.toList());
                                List<OrderDevicesVO> sensorData = new ArrayList<>();
                                for (OrderSensorDeviceDO orderSensorDeviceDO : sensorInfoList) {
                                    OrderDevicesVO orderDevicesVO = new OrderDevicesVO();
                                    orderDevicesVO.setDeviceCode(orderSensorDeviceDO.getDeviceCode());
                                    orderDevicesVO.setDeviceName(orderSensorDeviceDO.getDeviceName());
                                    orderDevicesVO.setLatitude(orderSensorDeviceDO.getLatitude());
                                    orderDevicesVO.setLongitude(orderSensorDeviceDO.getLongitude());
                                    orderDevicesVO.setLocation(orderSensorDeviceDO.getLocation());
                                    sensorData.add(orderDevicesVO);
                                }
                                orderPackageVO.setSensorDeviceData(sensorData);
                            }
                            packageData.add(orderPackageVO);
                        }
                        orderInfoVO.setPackageData(packageData);
                    }
                }
            }

        }
        return orderInfoVOList;
    }

    /**
     * 功能描述 查询订购算法列表
     *
     * @param customerDTO
     * @return
     * @author chensw
     * @date 2022/2/11
     */
    @Override
    public List<OrderAlgorithmsVO> queryAlgorithmList(CustomerDTO customerDTO) {
        List<OrderAlgorithmsVO> resultList = new ArrayList<>();
        List<OrderAlgorithmsVO> algList = orderAlgorithmsRepository.getCustomerAlgorithm(customerDTO.getTenantId(),
                customerDTO.getDeviceCode(), customerDTO.getIndustryCode());
        List<String> enumCodeList = new ArrayList<>();
        //获取赠送算法
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
        List<SysDictDataResponse> dictList = sysResult.getData();
        if (!CollectionUtils.isEmpty(dictList)) {
            for (SysDictDataResponse sysDictData : dictList) {
                enumCodeList.add(sysDictData.getDictValue());
            }
        }
        //过滤前端算法编码
        for (OrderAlgorithmsVO vo : algList) {
            String algCode = vo.getAlgorithmCode();
            if (!StringUtils.isEmpty(algCode) && !enumCodeList.contains(algCode.substring(0, 2))) {
                resultList.add(vo);
            }
        }
        return resultList;
    }


    /**
     * 根据用户编号查询用户订购的行业版本下的设备编号
     *
     * @return
     * @author chenmf
     * @date 2022/3/5
     * @version
     */
    @Override
    public List<String> findDeviceCodeByUser(FindDeviceCodeVO findDeviceCodeVO) {
        return orderDeviceRepository.findDeviceCodeByUser(findDeviceCodeVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addCustomer(CustomerAddVO customerAddVO) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null) {
                return Result.fail(Constants.LOGIN_USER_ERROR);
            }
            SysUser user = loginUser.getUser();
            AddUserRequest addUserRequest = addSysUser(customerAddVO);
            orderSystemService.addUser(addUserRequest, true);
            //mybatisplus有坑 @TableField会影响数据插入
            SysTenantInfo sysTenantInfo = addTenantInfo(customerAddVO, user);
            sysTenantInfoMapper.insertSysTenant(sysTenantInfo);
        } catch (Exception e) {
            //user中抛出的异常和异常处理类不是一个包，这里统一处理
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
        return Result.success("");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteCustomer(String tenantId) {
        if (orderCustomerRepository.countOrder(tenantId) > 0) {
            return Result.fail("该客户下有订购的应用版本，不可删除！");
        }
        saveSysTenantInfoToMongoHis(tenantId);
        saveSysUserToMongoHis(tenantId);
        return Result.success("");
    }

    @Override
    public Result getCustomerInfo(String tenantId) {
        return Result.data(sysTenantInfoMapper.getCustomerAddInfo(tenantId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateCustomer(CustomerAddVO customerAddVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(Constants.LOGIN_USER_ERROR);
        }
        String tenantId = customerAddVO.getCustomerCode();
        int tenantIdCount = sysTenantInfoMapper.checkTenantIdUnique(tenantId);
        if (tenantIdCount != Constants.ONE_NUM) {
            return Result.fail("查询租户异常");
        }
        int sysUserCount = sysUserMapper.checkTenantAdminUnique(tenantId);
        if (sysUserCount != Constants.ONE_NUM) {
            return Result.fail("查询用户异常");
        }
        sysTenantInfoMapper.update(null, assembleTenantInfoUpdateWrap(customerAddVO, loginUser.getUsername()));
        sysUserMapper.update(null, updateSysUser(customerAddVO, loginUser.getUsername()));
        return Result.success("");
    }

    /**
     * 业务开通需要进行以下逻辑校验：同一客户同一行业下所有有效订单不能订购相同设备、同一客户下所有有效订单不能订购同一行业的多个版本。
     * 2同一客户维度下，一个【行业+版本】为一个订单，根据tenant_id查出当前订购表中的所有订单，以industry_code、app_ver_code为条件进行过滤，如果业务开通参数的行业、版本没有查到订单或订单已经失效，则按照订购新增逻辑处理；如果业务开通参数的行业、版本已有订单并且仍有效，则按照订购变更逻辑处理；如果当前订购表中存在的行业、版本在业务开通参数中不存在，则按照订购退订逻辑处理。
     * 3业务开通功能一次可能开通多个行业，对应的是需要创建多个订购订单，订购接口目前不支持批量，需要循环单次调用，为保证调用稳定采用同步调用方式，调用失败跳过不影响其他订单调用，页面提示成功、失败行业信息。
     *
     * @param params
     * @return
     */
    @Override
    public Result dealOrder(List<OrderRelationshipDTO> params) {
        try {
            if (CollectionUtils.isEmpty(params)) {
                return Result.fail("至少需要一条订单");
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null) {
                return Result.fail(Constants.LOGIN_USER_ERROR);
            }
            String tenantId = params.get(0).getData().getCustomer().getCustomerCode();
            List<OrderPackageDO> orderPackageDBList = orderPackageRepository.getOrderPackageInfoByTenantId(tenantId);
            Map<String, OrderPackageDO> orderPackageDBMap = new HashMap<>();
            Set<String> orderPackageDBSet = new HashSet<>();
            if (!CollectionUtils.isEmpty(orderPackageDBList)) {
                orderPackageDBList.forEach(x -> orderPackageDBMap.put(x.getIndustryCode() + ":" + x.getAppVerCode(), x));
                orderPackageDBSet.addAll(orderPackageDBMap.keySet());
            }
            //判断套餐行业是否重复
            Set<String> paramIndustryCodeSet = Sets.newHashSet();
            //判断套餐的状态是新增还是更新还是退订
            Set<String> paramPackageSet = Sets.newHashSet();

            // 数据库中摄像机map
            Map<String, BasicDeviceDTO> deviceDTOMap = null;
            // 数据库中传感器map
            Map<String, IotDeviceDO> sensorDTOMap = null;
            // 数据库中无人机map
            Map<String, UavDeviceDO> uavDTOMap = null;
            // 数据库中喇叭map
            Map<String, HornDeviceDO> hornDTOMap = null;
            // 数据库中执法记录仪map
            Map<String, RecorderDeviceDO> recorderDTOMap = null;

            Set<String> allDeviceCodeSet = Sets.newHashSet();
            Set<String> allSensorCodeSet = Sets.newHashSet();
            Set<String> allUavCodeSet = Sets.newHashSet();
            Set<String> allHornCodeSet = Sets.newHashSet();
            Set<String> allRecorderDevices = Sets.newHashSet();

            for (OrderRelationshipDTO param : params) {
                OrderGatewayDTO dto = param.getData();
                OrderGatewayDTO.OrderPackages orderPackages = dto.getPackages().get(0);
                List<OrderGatewayDTO.Device> devices = orderPackages.getDevices();
                if (CollectionUtils.isNotEmpty(devices)) {
                    Set<String> deviceCodeSet = devices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    allDeviceCodeSet.addAll(deviceCodeSet);
                }

                List<OrderGatewayDTO.SensorDevice> sensorDevices = orderPackages.getSensorDevice();
                if (CollectionUtils.isNotEmpty(sensorDevices)) {
                    Set<String> sensorCodeSet = sensorDevices.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                    allSensorCodeSet.addAll(sensorCodeSet);
                }

                List<OrderGatewayDTO.Device> uavDevices = orderPackages.getUavDevice();
                if (CollectionUtils.isNotEmpty(uavDevices)) {
                    Set<String> uavCodeSet = uavDevices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    allUavCodeSet.addAll(uavCodeSet);
                }

                List<OrderGatewayDTO.SensorDevice> hornDevices = orderPackages.getHornDevice();
                if (CollectionUtils.isNotEmpty(hornDevices)) {
                    Set<String> hornCodeSet = hornDevices.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                    allHornCodeSet.addAll(hornCodeSet);
                }

                List<OrderGatewayDTO.Device> recorderDevices = orderPackages.getRecorderDevice();
                if (CollectionUtils.isNotEmpty(recorderDevices)) {
                    Set<String> recorderCodeSet = recorderDevices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    allRecorderDevices.addAll(recorderCodeSet);
                }
            }
            if (CollectionUtils.isNotEmpty(allDeviceCodeSet)) {
                deviceDTOMap = deviceInfoRepository.queryByDeviceCodes(Lists.newArrayList(allDeviceCodeSet));
            }
            if (CollectionUtils.isNotEmpty(allSensorCodeSet)) {
                List<IotDeviceDO> iotDeviceDOS = iIotDeviceRepository.listIotDeviceBySet(allSensorCodeSet);
                sensorDTOMap = iotDeviceDOS.stream().collect(Collectors.toMap(IotDeviceDO::getDeviceCode, Function.identity()));
            }
            if (CollectionUtils.isNotEmpty(allUavCodeSet)) {
                List<UavDeviceDO> uavDeviceInfoList = iUavDeviceRepository.listDeviceInfoBySet(allUavCodeSet);
                uavDTOMap = uavDeviceInfoList.stream().collect(Collectors.toMap(UavDeviceDO::getDeviceCode, Function.identity()));
            }
            if (CollectionUtils.isNotEmpty(allHornCodeSet)) {
                List<HornDeviceDO> hornDeviceInfoList = iHornDeviceRepository.listDeviceInfoBySet(allHornCodeSet);
                hornDTOMap = hornDeviceInfoList.stream().collect(Collectors.toMap(HornDeviceDO::getDeviceCode, Function.identity()));
            }
            if (CollectionUtils.isNotEmpty(allRecorderDevices)) {
                List<RecorderDeviceDO> recorderDeviceInfoList = iRecorderDeviceRepository.listDeviceInfoBySet(allRecorderDevices);
                recorderDTOMap = recorderDeviceInfoList.stream().collect(Collectors.toMap(RecorderDeviceDO::getDeviceCode, Function.identity()));
            }

            //字典默认配置365天
            String storageTime = "365";
            AjaxResult ajaxResult = systemFeignClient.getConfValue(OrderConstants.SYS_LOCALIZATION_STORAGE);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                storageTime = sysConfig.getConfigValue();
            }

            for (OrderRelationshipDTO param : params) {
                OrderGatewayDTO dto = param.getData();
                if (!dto.checkParam()) {
                    return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
                }
                OrderGatewayDTO.OrderPackages orderPackages = dto.getPackages().get(0);
                OrderGatewayDTO.Application application = orderPackages.getApplication();
                String industryCode = application.getAppCode();
                String appVersion = application.getProductCode();

                List<OrderGatewayDTO.Device> devices = orderPackages.getDevices();
                Set<String> deviceCodeSet = null;

                if (paramIndustryCodeSet.contains(industryCode)) {
                    //传入的参数中存在相同行业
                    return Result.fail(String.format("行业：[%s]; 行业重复", orderPackages.getPackageName()));
                }
                paramIndustryCodeSet.add(industryCode);
                if (!CollectionUtils.isEmpty(devices)) {
                    deviceCodeSet = devices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    //判断同一行业版本是否存在重复设备
                    if (CollectionUtils.size(devices) != CollectionUtils.size(deviceCodeSet)) {
                        Set<String> repeatDeviceNameSet = devices.stream().collect(Collectors.groupingBy(OrderGatewayDTO.Device::getDevName, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return Result.fail(String.format("行业：[%s]; 版本：[%s]; 设备重复：[%s]", orderPackages.getPackageName(), appVersion, repeatDeviceNameSet));
                    }
                }
                // 也需要判断物联和雷达设备，同一行业版本是否存在重复设备
                List<OrderGatewayDTO.SensorDevice> sensorDevices = orderPackages.getSensorDevice();
                if (!CollectionUtils.isEmpty(sensorDevices)) {
                    Set<String> sensorCodeSet = sensorDevices.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                    if (CollectionUtils.size(sensorDevices) != CollectionUtils.size(sensorCodeSet)) {
                        Set<String> repeatDeviceNameSet = sensorDevices.stream().collect(Collectors.groupingBy(OrderGatewayDTO.SensorDevice::getDevName, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return Result.fail(String.format("行业：[%s]; 版本：[%s]; 物联设备重复：[%s]", orderPackages.getPackageName(), appVersion, repeatDeviceNameSet));
                    }
                }

                // 也需要判断无人机设备，同一行业版本是否存在重复设备
                List<OrderGatewayDTO.Device> uavDevices = orderPackages.getUavDevice();
                if (!CollectionUtils.isEmpty(uavDevices)) {
                    Set<String> uavCodeSet = uavDevices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    if (CollectionUtils.size(uavDevices) != CollectionUtils.size(uavCodeSet)) {
                        Set<String> repeatDeviceNameSet = uavDevices.stream().collect(Collectors.groupingBy(OrderGatewayDTO.Device::getDevName, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return Result.fail(String.format("行业：[%s]; 版本：[%s]; 无人机设备重复：[%s]", orderPackages.getPackageName(), appVersion, repeatDeviceNameSet));
                    }
                }

                // 也需要判断喇叭设备，同一行业版本是否存在重复设备
                List<OrderGatewayDTO.SensorDevice> hornDevices = orderPackages.getHornDevice();
                if (!CollectionUtils.isEmpty(hornDevices)) {
                    Set<String> hornCodeSet = hornDevices.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).collect(Collectors.toSet());
                    if (CollectionUtils.size(hornDevices) != CollectionUtils.size(hornCodeSet)) {
                        Set<String> repeatDeviceNameSet = hornDevices.stream().collect(Collectors.groupingBy(OrderGatewayDTO.SensorDevice::getDevName, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return Result.fail(String.format("行业：[%s]; 版本：[%s]; 雷达设备重复：[%s]", orderPackages.getPackageName(), appVersion, repeatDeviceNameSet));
                    }
                }

                // 也需要判断执法记录仪设备，同一行业版本是否存在重复设备
                List<OrderGatewayDTO.Device> recorderDevices = orderPackages.getRecorderDevice();
                if (!CollectionUtils.isEmpty(recorderDevices)) {
                    Set<String> recorderCodeSet = recorderDevices.stream().map(OrderGatewayDTO.Device::getDeviceCode).collect(Collectors.toSet());
                    if (CollectionUtils.size(recorderDevices) != CollectionUtils.size(recorderCodeSet)) {
                        Set<String> repeatDeviceNameSet = recorderDevices.stream().collect(Collectors.groupingBy(OrderGatewayDTO.Device::getDevName, Collectors.counting())).entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return Result.fail(String.format("行业：[%s]; 版本：[%s]; 执法记录仪设备重复：[%s]", orderPackages.getPackageName(), appVersion, repeatDeviceNameSet));
                    }
                }

                String key = industryCode + ":" + appVersion;
                if (CollectionUtils.isEmpty(orderPackageDBSet) || !orderPackageDBSet.contains(key)) {
                    //非已存在的相同行业相同版本则新增
                    dto.setOrderCode(SnowflakeIdWorker.generateSnowId(tenantId));
                    dto.setStatus(OrderPackageDO.STATUS_ADD);
                }
                else {
                    //已存在的相同行业相同版本则更新
                    dto.setOrderCode(orderPackageDBMap.get(key).getOrderCode());
                    dto.setStatus(OrderPackageDO.STATUS_UPDATE);
                }
                paramPackageSet.add(key);
                assembleOrderCustomerDO(dto, loginUser.getUser(), tenantId + "", deviceDTOMap, sensorDTOMap,
                        uavDTOMap, hornDTOMap, recorderDTOMap, storageTime);
            }
            //是否需要退订套餐
            orderPackageDBSet.removeAll(paramPackageSet);
            if (CollectionUtils.isNotEmpty(orderPackageDBSet)) {
                orderPackageDBSet.forEach(orderCode -> {
                    OrderRelationshipDTO order = new OrderRelationshipDTO();
                    OrderGatewayDTO dto = new OrderGatewayDTO();
                    order.setData(dto);
                    OrderPackageDO orderPackageDO = orderPackageDBMap.get(orderCode);
                    dto.setOldOrderCode(orderPackageDO.getOrderCode());
                    dto.setStatus(OrderPackageDO.STATUS_DEL);
                    OrderGatewayDTO.OrderPackages orderPackages = new OrderGatewayDTO.OrderPackages();
                    dto.setPackages(Collections.singletonList(orderPackages));
                    OrderGatewayDTO.Application application = new OrderGatewayDTO.Application();
                    orderPackages.setApplication(application);
                    application.setAppCode(orderPackageDO.getIndustryCode());
                    application.setProductCode(orderPackageDO.getAppVerCode());
                    orderPackages.setPackageName(orderPackageDO.getPackageName());
                    orderPackages.setPackageCode(orderPackageDO.getIndustryCode());
                    params.add(order);
                });
            }
        } catch (Exception e) {
            log.error("VideoOrderInfoService.dealOrder error", e);
            return Result.fail();
        }
        return Result.success(ResultCode.SUCCESS);
    }

    @Override
    public Result checkOrderExist(String tenantId) {
        return Result.data(orderCustomerRepository.countOrder(tenantId) > 0);
    }

    @Override
    public Result<List<OrderPackageDO>> getOrderCodesByTenantId(String tenantId) {
        return Result.data(orderPackageRepository.getOrderPackageInfoByTenantId(tenantId));
    }

    @Override
    public void transferIndustryOutOfTransaction(TransferIndustryDTO param) {
        Stopwatch sw = Stopwatch.createStarted();
        log.info("VideoOrderInfoService transferIndustryOutOfTransaction step1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        try {
            // 客户id
            String tenantId = param.getTenantId();
            // 受理节点
            String acceptNode = param.getAcceptNode();
            // 原行业编码
            String sourceIndustryCode = param.getAppInfo().getIndustryCode();
            // 原行业版本编码
            String sourceAppVerCode = param.getAppInfo().getAppVerCode();

            // 目标行业编码
            String targetIndustryCode = param.getTargetApp().getIndustryCode();
            // 目标行业版本编码
            String targetAppVerCode = param.getTargetApp().getAppVerCode();
            // 目标行业名称
            String targetIndustryName = null;
            if (!StringUtils.equals(sourceIndustryCode, targetIndustryCode)) {
                targetIndustryName = sysTenantInfoMapper.getIndustryNameByCode(targetIndustryCode);
            }
            // 更新订购套餐表数据
            sysTenantInfoMapper.transferOrderPackageInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                    targetIndustryCode, targetAppVerCode, targetIndustryName);
            sysTenantInfoMapper.transferOrderPackageHisInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                    targetIndustryCode, targetAppVerCode, targetIndustryName);
            // 更新告警模板表数据
            warningInfoMapper.transferAlarmFlowConfig(tenantId, sourceIndustryCode, sourceAppVerCode,
                    targetIndustryCode, targetAppVerCode);
            // 调用基础数据库订单相关存储过程
            sysTenantInfoMapper.callTransferOrderIndustryBaseInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                    targetIndustryCode, targetAppVerCode);
            // 调用基础数据库存储过程
            sysTenantInfoMapper.callTransferIndustryBaseInfo(tenantId, sourceIndustryCode, sourceAppVerCode,
                    targetIndustryCode, targetAppVerCode);
            log.info("VideoOrderInfoService transferIndustryOutOfTransaction step2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 如果变更前后的行业编码相同，只改行业编码的sql就不执行了
            if (!StringUtils.equals(sourceIndustryCode, targetIndustryCode)) {
                // 调用业务数据库存储过程
                warningInfoMapper.callTransferIndustryWarnInfo(tenantId, sourceIndustryCode, targetIndustryCode);
                log.info("VideoOrderInfoService transferIndustryOutOfTransaction step3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
                // 更新vm_overdue_remind_t表数据
                warningInfoMapper.transferOverDueRemindInfo(tenantId, sourceIndustryCode, targetIndustryCode);
                log.info("VideoOrderInfoService transferIndustryOutOfTransaction step4 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
                // 调用业务数据库订单表存储过程，订单表开始时间默认 2022-04-01
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = simpleDateFormat.parse("2022-04-01");
                warningInfoMapper.transferIndustryOrderInfo(tenantId, sourceIndustryCode, targetIndustryCode, startDate, new Date());
                log.info("VideoOrderInfoService transferIndustryOutOfTransaction step5 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
                // 调用统计数据库存储过程
                statisticsInfoMapper.callTransferIndustryStatisticsInfo(tenantId, sourceIndustryCode, targetIndustryCode);
            }
        } catch (Exception e) {
            log.error("VideoOrderInfoService industryTransferCommonLogic 存储过程执行失败, error: ", e);
        }
        log.info("VideoOrderInfoService transferIndustryOutOfTransaction step6 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public void transferOrderIndustry(TransferIndustryDTO param, boolean pushFlag) {
        // 客户id
        String tenantId = param.getTenantId();
        // 受理节点
        String acceptNode = param.getAcceptNode();
        // 原行业编码
        String sourceIndustryCode = param.getAppInfo().getIndustryCode();
        // 原行业版本编码
        String sourceAppVerCode = param.getAppInfo().getAppVerCode();

        // 目标行业编码
        String targetIndustryCode = param.getTargetApp().getIndustryCode();
        // 目标行业版本编码
        String targetAppVerCode = param.getTargetApp().getAppVerCode();

        // 目标行业名称
        String targetIndustryName = null;
        if (!StringUtils.equals(sourceIndustryCode, targetIndustryCode)) {
            targetIndustryName = sysTenantInfoMapper.getIndustryNameByCode(targetIndustryCode);
        }
        // 更新订购套餐表数据
        sysTenantInfoMapper.transferOrderPackageInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                targetIndustryCode, targetAppVerCode, targetIndustryName);
        sysTenantInfoMapper.transferOrderPackageHisInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                targetIndustryCode, targetAppVerCode, targetIndustryName);

        // 调用基础数据库订单相关存储过程
        sysTenantInfoMapper.callTransferOrderIndustryBaseInfo(tenantId, acceptNode, sourceIndustryCode, sourceAppVerCode,
                targetIndustryCode, targetAppVerCode);

        // 向受理节点及父节点推送
        if (pushFlag) {
            List<String> pushLocationCodeList = Lists.newArrayList(acceptNode);
            // 查询当前节点所有上级节点【除去中心节点】
            List<String> parentLocationCodeList = orderCustomerRepository.getParentCodeListByLocationCode(acceptNode);
            if (CollectionUtils.isNotEmpty(parentLocationCodeList)) {
                pushLocationCodeList.addAll(parentLocationCodeList);
            }
            for (String pushLocationCode : pushLocationCodeList) {
                //远程调用
                Result result = RemoteNodeUtils.create(pushLocationCode)
                        .getFeign(IOrderRemoteService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                        .transferIndustry(param);
                log.info("VideoOrderInfoService transferOrderIndustry pushLocationCode:{} result:{}", pushLocationCode, JSON.toJSONString(result));
            }
            log.info("VideoOrderInfoService transferOrderIndustry pushLocationCodeList:{}", JSON.toJSONString(pushLocationCodeList));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferIndustry(TransferIndustryDTO param) {
        log.info("VideoOrderInfoService transferIndustry param:{}", JSON.toJSONString(param));
        Stopwatch sw = Stopwatch.createStarted();
        // 参数校验
        checkTransferParam(param);
        // 租户id
        String tenantId = param.getTenantId();
        // 受理节点
        String acceptNode = param.getAcceptNode();
        // 原行业编码
        String sourceIndustryCode = param.getAppInfo().getIndustryCode();
        // 原行业版本编码
        String sourceAppVerCode = param.getAppInfo().getAppVerCode();
        // 租户已订购行业版本信息
        // 历史表和当前表都查，不过滤状态
        List<OrderPackageDO> orderPackageList = orderPackageRepository.selectIndustryInfoByTenantId(tenantId, acceptNode);
        if (CollectionUtils.isEmpty(orderPackageList)) {
            throw new CustomException("客户没有有效订单");
        }
        List<OrderPackageDO> orderHisPackageList = orderPackageRepository.selectHisIndustryInfoByTenantId(tenantId, acceptNode);
        // 如果历史记录不为空，则合并到当前表
        if (CollectionUtils.isNotEmpty(orderHisPackageList)) {
            orderPackageList.addAll(orderHisPackageList);
        }
        // key -> industryCode, value -> appVerCodeList
        Map<String, List<String>> existIndustryCodeMap = assembleIndustryMap(orderPackageList);
        if (!existIndustryCodeMap.containsKey(sourceIndustryCode) || !existIndustryCodeMap.get(sourceIndustryCode).contains(sourceAppVerCode)) {
            throw new CustomException("客户变更的原行业、编码未订购");
        }
        if (StringUtils.equals(sourceIndustryCode, param.getTargetApp().getIndustryCode())
                && StringUtils.equals(param.getAppInfo().getAppVerCode(), param.getTargetApp().getAppVerCode())) {
            throw new CustomException("客户变更前后版本一致，不进行变更！");
        }
        log.info("VideoOrderInfoService transferIndustry step 1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        // 单行业变更
        if (existIndustryCodeMap.size() == 1) {
            singleIndustryTransferLogic(param);
        }
        // 多行业变更
        else {
            multipleIndustryTransferLogic(param, existIndustryCodeMap);
        }
        log.info("VideoOrderInfoService transferIndustry step 2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));

        // 行业转换通用逻辑
        industryTransferCommonLogic(param);
        log.info("VideoOrderInfoService transferIndustry step 3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public List<OrderInfoVO> getSimpleOrderRelationshipListByTenantId(QueryOrderRelationshipVO queryOrderRelationshipVO) {
        if (!StringUtils.isEmpty(queryOrderRelationshipVO.getOrderStatus())) {
            String[] orderStatusArr = queryOrderRelationshipVO.getOrderStatus().split(",");
            queryOrderRelationshipVO.setOrderStatusArr(orderStatusArr);
        }
        PageMethod.startPage(queryOrderRelationshipVO.getPageNum(), queryOrderRelationshipVO.getPageSize());
        //查询用户订购订单列表
        List<OrderInfoVO> orderInfoVOList = orderCustomerRepository.getOrderCustomerByParam(queryOrderRelationshipVO);
        if (!CollectionUtils.isEmpty(orderInfoVOList)) {
            //获取赠送算法
            Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(OrderConstants.SYS_GIVE_ALGORITHM);
            List<SysDictDataResponse> dictList = sysResult.getData();

            List<String> orderCodeList = orderInfoVOList.stream().map(OrderInfoVO::getOrderCode).distinct().collect(Collectors.toList());

            //查询对应订单套餐列表
            queryOrderRelationshipVO.setOrderCodeList(orderCodeList);
            //查询对应订单套餐列表
            List<OrderPackageDO> allOrderPackageList = orderPackageRepository.getOrderPackageInfoByOrderCode(queryOrderRelationshipVO);

            if (CollectionUtils.isNotEmpty(allOrderPackageList)) {
                //所有packageCode
                List<String> packageList = allOrderPackageList.stream().map(OrderPackageDO::getPackageCode).distinct().collect(Collectors.toList());

                //单雷达列表
                List<OrderSensorDeviceDO> allOrderSensorDeviceInfoList = orderSensorDeviceRepository.getOrderRadarDeviceByOrderCodesAndPackageCodes(orderCodeList, packageList);
                //查询对应订单算法列表
                List<OrderAlgorithmsDO> allOrderAlgorithmsList = orderAlgorithmsRepository.getOrderAlgorithmsByList(orderCodeList, packageList);
                //查询对应订单应用列表
                List<OrderPackageDO> allOrderAppList = orderPackageRepository.getOrderPackageByOrderCodeList(orderCodeList, packageList);
                //查询对应订单卫星列表
                List<OrderServiceRelDO> allOrderServiceRelDOList = orderServiceRelRepository.getOrderServiceRelByList(orderCodeList, packageList);
                // 查询对应订单无人机设备列表
                List<OrderUavDeviceDO> allOrderUavDeviceList = orderUavDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                // 查询无人机关联算法配置 type=2
                Map<String, List<OrderAlgorithmConfigVO>> uavAlgorithmConfigMap = buildUavAlgorithmConfigMap(orderCodeList, allOrderUavDeviceList);
                // 查询对应订单无人机机巢设备列表
                List<OrderUavNestDeviceDO> allOrderUavNestDeviceList = orderUavNestDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                // 查询对应订单喇叭设备列表
                List<OrderHornDeviceDO> allOrderHornDeviceList = orderHornDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                // 查询对应订单执法记录仪设备列表
                List<OrderRecorderDeviceDO> allOrderRecorderDeviceList = orderRecorderDeviceRepository.getOrderDeviceByList(orderCodeList, packageList);
                //全部的区县批量
                List<String> countyCodeList = allOrderServiceRelDOList.stream().map(OrderServiceRelDO::getCountyCode).distinct().collect(Collectors.toList());
                //根据区县编码批量查询
                List<OrderServiceRelVO> allAreaVOList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(countyCodeList)) {
                    allAreaVOList = orderServiceRelRepository.getAreaInfo(countyCodeList);
                }

                for (OrderInfoVO orderInfoVO : orderInfoVOList) {
                    String orderCode = orderInfoVO.getOrderCode();
                    //List<OrderPackageDO> orderPackageList = orderPackageRepository.getOrderPackageInfoByOrderCode(queryOrderRelationshipVO);
                    List<OrderPackageDO> orderPackageList = allOrderPackageList.stream().filter(item -> Objects.equals(item.getOrderCode(), orderCode)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(orderPackageList)) {
                        List<OrderPackageVO> packageData = new ArrayList<>();
                        for (OrderPackageDO orderPackageDO : orderPackageList) {

                            String packageCode = orderPackageDO.getPackageCode();


                            //组装订单套餐信息
                            OrderPackageVO orderPackageVO = assembleSimpleOrderPackage(orderPackageDO);
                            //组装订单应用信息
                            assembleAppData(allOrderAppList, orderPackageVO, orderCode, packageCode);
                            //组装订单算法信息
                            assembleAlgorithmData(allOrderAlgorithmsList, orderPackageVO, dictList, orderCode, packageCode);
                            //组装雷达信息
                            assembleRadarData(allOrderSensorDeviceInfoList, orderPackageVO, orderCode, packageCode);
                            //组装无人机信息
                            assembleUavDeviceData(allOrderUavDeviceList, orderPackageVO, orderCode, packageCode, uavAlgorithmConfigMap);
                            //组装无人机机巢信息
                            assembleUavNestDeviceData(allOrderUavNestDeviceList, orderPackageVO, orderCode, packageCode);
                            //组装喇叭信息
                            assembleHornDeviceData(allOrderHornDeviceList, orderPackageVO, orderCode, packageCode);
                            //组装执法记录仪信息
                            assembleRecorderDeviceData(allOrderRecorderDeviceList, orderPackageVO, orderCode, packageCode);
                            //组装卫星信息
                            assembleSatelliteData(allOrderServiceRelDOList, orderPackageVO, allAreaVOList, orderCode, packageCode);
                            packageData.add(orderPackageVO);
                        }
                        orderInfoVO.setPackageData(packageData);
                    }
                }
            }

        }
        return orderInfoVOList;
    }


    @Override
    public List<OrderDevicesVO> getOrderDeviceInfos(QueryOrderRelationshipVO req) {
        checkPageParam(req);
        PageMethod.startPage(req.getPageNum(), req.getPageSize());
        List<OrderDevicesVO> orderDeviceInfoList = orderDeviceRepository.getDeviceInfoByOrderCodeAndPackageCode(req);
        if (CollectionUtils.isEmpty(orderDeviceInfoList)) {
            return Lists.newArrayList();
        }
        List<String> deviceCodes = orderDeviceInfoList.stream().map(OrderDevicesVO::getDeviceCode).distinct().collect(Collectors.toList());
        List<OrderDevicesVO> deviceInfoList = orderDeviceRepository.getDeviceInfoByDeviceCodes(deviceCodes);
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            Map<String, OrderDevicesVO> deviceCodeMap = deviceInfoList.stream().collect(Collectors.toMap(OrderDevicesVO::getDeviceCode, Function.identity()));
            for (OrderDevicesVO item : orderDeviceInfoList) {
                if (Objects.isNull(deviceCodeMap.get(item.getDeviceCode()))) {
                    continue;
                }
                item.setDeviceName(deviceCodeMap.get(item.getDeviceCode()).getDeviceName());
                item.setSiteName(deviceCodeMap.get(item.getDeviceCode()).getSiteName());
                item.setLatitude(deviceCodeMap.get(item.getDeviceCode()).getLatitude());
                item.setLongitude(deviceCodeMap.get(item.getDeviceCode()).getLongitude());
                item.setLocation(deviceCodeMap.get(item.getDeviceCode()).getLocation());
            }
        }

        // 查询摄像机关联算法配置 type=1，按 orderCode:deviceCode 组装
        Map<String, List<OrderAlgorithmConfigVO>> cameraAlgorithmConfigMap = Maps.newHashMap();
        Map<String, List<OrderDevicesVO>> deviceByOrderMap = orderDeviceInfoList.stream()
                .collect(Collectors.groupingBy(OrderDevicesVO::getOrderCode));
        for (Map.Entry<String, List<OrderDevicesVO>> entry : deviceByOrderMap.entrySet()) {
            String oc = entry.getKey();
            List<OrderDevicesVO> orderDevices = entry.getValue();
            List<OrderAlgorithmConfigVO> algConfigList = queryAlgorithmConfigWithFallback(1, oc);
            for (OrderAlgorithmConfigVO vo : algConfigList) {
                // 兜底数据无设备编码，按订单下每个摄像机设备填充
                if (StringUtils.isEmpty(vo.getDeviceCode())) {
                    for (OrderDevicesVO dev : orderDevices) {
                        String key = oc + ":" + dev.getDeviceCode();
                        OrderAlgorithmConfigVO copy = new OrderAlgorithmConfigVO();
                        copy.setDeviceCode(dev.getDeviceCode());
                        copy.setAlgorithmCode(vo.getAlgorithmCode());
                        copy.setAlgorithmName(vo.getAlgorithmName());
                        cameraAlgorithmConfigMap.computeIfAbsent(key, k -> new ArrayList<>()).add(copy);
                    }
                } else {
                    String key = oc + ":" + vo.getDeviceCode();
                    cameraAlgorithmConfigMap.computeIfAbsent(key, k -> new ArrayList<>()).add(vo);
                }
            }
        }
        for (OrderDevicesVO item : orderDeviceInfoList) {
            String key = item.getOrderCode() + ":" + item.getDeviceCode();
            item.setAlgorithmConfigList(cameraAlgorithmConfigMap.get(key));
        }

        return orderDeviceInfoList;
    }

    @Override
    public List<OrderAlgorithmConfigVO> getAlgorithmConfigByDeviceCodesAndType(
            List<String> deviceCodeList, Integer type, String orderCode) {
        return orderAlgorithmConfigRepository.selectByDeviceCodeListAndTypeAndOrderCode(deviceCodeList, type, orderCode);
    }

    /**
     * 查询订单算法配置：同一订单只要 vm_order_algorithm_config 有任一数据，则该订单该类设备
     * 的算法配置全部取自此表；仅当整个订单在 vm_order_algorithm_config 无数据时，兜底按订单号
     * 查 vm_order_algorithms，并通过 vm_algorithm_info_t.app_scenarios 区分摄像机(1)/无人机(2)，
     * type 与 app_scenarios 取值一致（1-摄像机 2-无人机）
     */
    private List<OrderAlgorithmConfigVO> queryAlgorithmConfigWithFallback(Integer type, String orderCode) {
        List<OrderAlgorithmConfigVO> algConfigList = getAlgorithmConfigByDeviceCodesAndType(null, type, orderCode);
        if (CollectionUtils.isEmpty(algConfigList)) {
            algConfigList = orderAlgorithmConfigRepository.selectAlgorithmInfoByOrderCodeAndAppScenario(
                    orderCode, String.valueOf(type));
        }
        return algConfigList;
    }

    @Override
    public List<OrderDevicesVO> getOrderSensorDeviceInfos(QueryOrderRelationshipVO req) {
        checkPageParam(req);
        PageMethod.startPage(req.getPageNum(), req.getPageSize());
        req.setCategoryCode(OrderConstants.ORDER_SENSOR_CODE);
        List<OrderDevicesVO> sensorInfoList = orderSensorDeviceRepository.getOrderSensorDeviceByOrderCodeAndPackageCode(req);
        if (CollectionUtils.isEmpty(sensorInfoList)) {
            return Lists.newArrayList();
        }
        return sensorInfoList;
    }

    private void checkPageParam(QueryOrderRelationshipVO req) {
        if (Objects.isNull(req.getPageNum())) {
            req.setPageNum(Constants.ONE_NUM);
        }
        if (Objects.isNull(req.getPageSize())) {
            req.setPageSize(Constants.FIVE_NUM);
        }
    }


    private void singleIndustryTransferLogic(TransferIndustryDTO param) {
        // 原行业编码
        String sourceIndustryCode = param.getAppInfo().getIndustryCode();
        // 目标行业编码
        String targetIndustryCode = param.getTargetApp().getIndustryCode();
        // 目标行业版本编码
        String targetAppVerCode = param.getTargetApp().getAppVerCode();

        if (!StringUtils.equals(sourceIndustryCode, targetIndustryCode)) {
            // 更新组织对应的行业编码为新行业编码
            sysTenantInfoMapper.transferDeptInfo(param.getTenantId(), sourceIndustryCode, targetIndustryCode, targetAppVerCode);
        }
    }

    private void multipleIndustryTransferLogic(TransferIndustryDTO param, Map<String, List<String>> existIndustryCodeMap) {
        Stopwatch sw = Stopwatch.createStarted();
        // 客户id
        String tenantId = param.getTenantId();
        // 原行业编码
        String sourceIndustryCode = param.getAppInfo().getIndustryCode();
        // 原行业版本编码
        String sourceAppVerCode = param.getAppInfo().getAppVerCode();
        // 目标行业编码
        String targetIndustryCode = param.getTargetApp().getIndustryCode();
        // 目标行业版本编码
        String targetAppVerCode = param.getTargetApp().getAppVerCode();

        // 变更之后是否还存在原行业订购信息，如果依然存在，则不能删除原来组织信息
        boolean isSourceIndustryLeft = false;
        // 变更后是否存在一个行业多版本，判断source行业和target行业变更后都不能存在一个行业多版本
        if (existIndustryCodeMap.containsKey(sourceIndustryCode)) {
            List<String> appVerCodeList = existIndustryCodeMap.get(sourceIndustryCode);
            appVerCodeList.remove(sourceAppVerCode);
            if (appVerCodeList.size() > 1) {
                throw new CustomException("不允许客户出现一个行业多版本情况，需要先将版本统一！");
            }
            if (CollectionUtils.isNotEmpty(appVerCodeList)) {
                isSourceIndustryLeft = true;
            }
        }
        if (existIndustryCodeMap.containsKey(targetIndustryCode)) {
            List<String> appVerCodeList = existIndustryCodeMap.get(targetIndustryCode);
            appVerCodeList.remove(targetAppVerCode);
            if (appVerCodeList.size() > 1) {
                throw new CustomException("不允许客户出现一个行业多版本情况，需要先将版本统一！");
            }
        }
        log.info("VideoOrderInfoService multipleIndustryTransferLogic step1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        // 判断是否变更前后有相同行业，如客户有通用1.0和林业1.0，将林业1.0变更为通用1.0【是否变更的行业为现已订购行业】
        // 如果变更前后行业相同，则不需要变更组织关联
        if (existIndustryCodeMap.containsKey(targetIndustryCode)
                && !StringUtils.equals(sourceIndustryCode, targetIndustryCode)) {
            // 更新用户表中管理员的组织id为变更后的组织id
            SysDept sourceDept = sysTenantInfoMapper.selectRootDeptIdByIndustryInfo(tenantId, sourceIndustryCode);
            if (sourceDept == null) {
                throw new CustomException("客户行业对应的组织信息不存在，不能变更！");
            }
            String sourceRootDeptId = sourceDept.getDeptId();
            // 获取转换目标行业组织根节点
            SysDept targetDept = sysTenantInfoMapper.selectRootDeptIdByIndustryInfo(tenantId, targetIndustryCode);
            if (targetDept == null) {
                throw new CustomException("客户目标行业组织不存在，不能变更！");
            }
            String targetRootDeptId = targetDept.getDeptId();
            log.info("multipleIndustryTransferLogic sourceRootDeptId:{}, targetRootDeptId:{}", sourceRootDeptId, targetRootDeptId);
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 只更改客户管理员的组织信息 sys_user表的deptId
            SysUser sysUser = sysUserMapper.getAdminByTenantId(tenantId);
            if (sysUser != null && StringUtils.isNotEmpty(sysUser.getDeptId())) {
                List<String> deptIdList = new ArrayList<>(Arrays.asList(sysUser.getDeptId().split(",")));
                if (!isSourceIndustryLeft) {
                    deptIdList.remove(sourceRootDeptId);
                }
                if (!deptIdList.contains(targetRootDeptId)) {
                    deptIdList.add(targetRootDeptId);
                }
                sysUserMapper.updateDeptByUserId(sysUser.getUserId(), Joiner.on(",").join(deptIdList), null);
            }
            // 更新非管理员客户组织id
            sysTenantInfoMapper.transferUserDeptInfo(tenantId, sourceRootDeptId, targetRootDeptId);
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 更新组织设备表
            handleOrganDeviceRelLogic(tenantId, sourceIndustryCode, targetIndustryCode, targetAppVerCode, targetRootDeptId);
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step4 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 组织卫星关系表
            handleOrganStreetRelLogic(tenantId, sourceIndustryCode, targetIndustryCode, targetAppVerCode, targetRootDeptId);
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step5 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 处理变更后的组织区域表sys_dept_street_rel【先删后插】
            orderOrganDeviceRelService.updateDeptStreetRel(tenantId, targetIndustryCode, targetAppVerCode);
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step6 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));

            // 变更之后如果原行业还有其他版本订购信息存在，则不进行组织删除
            if (!isSourceIndustryLeft) {
//                sysTenantInfoMapper.deleteDeptByTenantAndIndustry(tenantId, sourceIndustryCode);
                saveSysDeptToMongoHis(tenantId, sourceIndustryCode);
                // 变更之后如果原行业还有其他版本订购信息存在，则不进行组织删除
                sysTenantInfoMapper.deleteGridByTenantAndIndustry(tenantId, sourceIndustryCode);
            }
            log.info("VideoOrderInfoService multipleIndustryTransferLogic step7 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private void handleOrganDeviceRelLogic(String tenantId, String sourceIndustryCode, String targetIndustryCode,
                                           String targetAppVerCode, String targetRootDeptId) {
        // tenantId + industryCode查出来设备，挂在新行业上，去重
        List<OrganDeviceRelVO> sourceOrganDeviceRelList = sysTenantInfoMapper.selectOrganDeviceInfoByTenantAndIndustry(tenantId, sourceIndustryCode);
        List<OrganDeviceRelVO> targetOrganDeviceRelList = sysTenantInfoMapper.selectOrganDeviceInfoByTenantAndIndustry(tenantId, targetIndustryCode);
        Map<Integer, List<OrganDeviceRelVO>> sourceOrganDeviceRelMap = sourceOrganDeviceRelList.stream().collect(Collectors.groupingBy(OrganDeviceRelVO::getDeviceType));
        Map<Integer, List<OrganDeviceRelVO>> targetOrganDeviceRelMap = targetOrganDeviceRelList.stream().collect(Collectors.groupingBy(OrganDeviceRelVO::getDeviceType));
        List<OrganDeviceRelVO> organDeviceRelVoList = Lists.newArrayList();
        List<String> allSourceDeleteDeviceCodeList = Lists.newArrayList();
        for (Map.Entry<Integer, List<OrganDeviceRelVO>> entry : sourceOrganDeviceRelMap.entrySet()) {
            Integer deviceType = entry.getKey();
            List<String> sourceDeviceCodeList = entry.getValue().stream().map(OrganDeviceRelVO::getDeviceCode).collect(Collectors.toList());
            if (targetOrganDeviceRelMap.containsKey(deviceType)) {
                List<String> targetDeviceCodeList = targetOrganDeviceRelMap.get(deviceType).stream().map(OrganDeviceRelVO::getDeviceCode).collect(Collectors.toList());
                // 原行业需要删除的组织设备关联
                List<String> sourceDeleteDeviceCodeList = sourceDeviceCodeList.stream().filter(targetDeviceCodeList::contains).collect(Collectors.toList());
                // 原行业需要新增的组织设备关联【sourceDeviceCodeList - sourceDeleteDeviceCodeList】
                if (CollectionUtils.isNotEmpty(sourceDeleteDeviceCodeList)) {
                    sourceDeviceCodeList.removeAll(sourceDeleteDeviceCodeList);
                    allSourceDeleteDeviceCodeList.addAll(sourceDeleteDeviceCodeList);
                }
            }
            if (CollectionUtils.isNotEmpty(sourceDeviceCodeList)) {
                Date now = new Date();
                for (String deviceCode : sourceDeviceCodeList) {
                    OrganDeviceRelVO organDeviceRelVO = new OrganDeviceRelVO();
                    organDeviceRelVO.setCreateTime(now);
                    organDeviceRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                    organDeviceRelVO.setDeviceCode(deviceCode);
                    organDeviceRelVO.setUpdateTime(now);
                    organDeviceRelVO.setDeptId(targetRootDeptId);
                    organDeviceRelVO.setTenantId(tenantId);
                    organDeviceRelVO.setDeviceType(deviceType);
                    organDeviceRelVO.setIndustryCode(targetIndustryCode);
                    organDeviceRelVO.setAppVerCode(targetAppVerCode);
                    organDeviceRelVoList.add(organDeviceRelVO);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(allSourceDeleteDeviceCodeList)) {
            sysTenantInfoMapper.deleteOrganDeviceRel(tenantId, sourceIndustryCode, allSourceDeleteDeviceCodeList);
        }
        if (CollectionUtils.isNotEmpty(organDeviceRelVoList)) {
            orderOrganDeviceRelService.batchSaveDeviceRela(organDeviceRelVoList);
        }
    }

    private void handleOrganStreetRelLogic(String tenantId, String sourceIndustryCode, String targetIndustryCode,
                                           String targetAppVerCode, String targetRootDeptId) {
        // tenantId + industryCode查出来服务，挂在新行业上，去重
        List<OrganStreetRelVO> sourceOrganStreetRelVoList = sysTenantInfoMapper.selectOrganStreetInfoByTenantAndIndustry(tenantId, sourceIndustryCode);
        List<OrganStreetRelVO> targetOrganStreetRelVoList = sysTenantInfoMapper.selectOrganStreetInfoByTenantAndIndustry(tenantId, targetIndustryCode);

        List<String> sourceCountyCodeList = sourceOrganStreetRelVoList.stream().map(OrganStreetRelVO::getCountyCode).collect(Collectors.toList());
        List<String> targetDeviceCodeList = targetOrganStreetRelVoList.stream().map(OrganStreetRelVO::getCountyCode).collect(Collectors.toList());
        // 原行业需要删除的组织服务关联
        List<String> sourceDeleteCountyCodeList = sourceCountyCodeList.stream().filter(targetDeviceCodeList::contains).collect(Collectors.toList());
        // 原行业需要新增的组织服务关联【sourceCountyCodeList - sourceDeleteCountyCodeList】
        if (CollectionUtils.isNotEmpty(sourceDeleteCountyCodeList)) {
            sourceCountyCodeList.removeAll(sourceDeleteCountyCodeList);
            sysTenantInfoMapper.deleteOrganStreetRel(tenantId, sourceIndustryCode, sourceDeleteCountyCodeList);
        }
        if (CollectionUtils.isNotEmpty(sourceCountyCodeList)) {
            List<OrganStreetRelVO> organStreetRelVoList = Lists.newArrayList();
            sourceOrganStreetRelVoList = sourceOrganStreetRelVoList.stream().filter(x -> sourceCountyCodeList.contains(x.getCountyCode())).collect(Collectors.toList());
            Date now = new Date();
            for (OrganStreetRelVO organStreetRel : sourceOrganStreetRelVoList) {
                OrganStreetRelVO organStreetRelVO = new OrganStreetRelVO();
                organStreetRelVO.setCreateTime(now);
                organStreetRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                organStreetRelVO.setUpdateTime(now);
                organStreetRelVO.setCategoryCode(OrderConstants.CATEGORY_SATELLITE_CODE);
                organStreetRelVO.setDeptId(targetRootDeptId);
                organStreetRelVO.setTenantId(tenantId);
                organStreetRelVO.setProvinceCode(organStreetRel.getProvinceCode());
                organStreetRelVO.setCityCode(organStreetRel.getCityCode());
                organStreetRelVO.setCountyCode(organStreetRel.getCountyCode());
                organStreetRelVO.setStreetCode(organStreetRel.getStreetCode());
                organStreetRelVO.setIndustryCode(targetIndustryCode);
                organStreetRelVO.setAppVerCode(targetAppVerCode);
                organStreetRelVoList.add(organStreetRelVO);
            }
            if (CollectionUtils.isNotEmpty(organStreetRelVoList)) {
                orderOrganDeviceRelService.batchSaveStreetRela(organStreetRelVoList);
            }
        }
    }

    private void industryTransferCommonLogic(TransferIndustryDTO param) {
        Stopwatch sw = Stopwatch.createStarted();
        // 客户id
        String tenantId = param.getTenantId();
        // 原行业编码
        String sourceIndustryCode = param.getAppInfo().getIndustryCode();
        // 原行业版本编码
        String sourceAppVerCode = param.getAppInfo().getAppVerCode();

        // 目标行业编码
        String targetIndustryCode = param.getTargetApp().getIndustryCode();
        // 目标行业版本编码
        String targetAppVerCode = param.getTargetApp().getAppVerCode();
        // 1、查询租户在原行业版本下自建的角色，以菜单为关系，将authId进行变更，更新角色菜单行业权限表、删除角色对应的原行业剩余的authId
        sysTenantInfoMapper.transferRoleRelation(tenantId, sourceIndustryCode, sourceAppVerCode, targetIndustryCode, targetAppVerCode);
        log.info("VideoOrderInfoService industryTransferCommonLogic step1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        sysTenantInfoMapper.deleteSourceIndustryRoleRelation(tenantId, sourceIndustryCode, sourceAppVerCode);
        log.info("VideoOrderInfoService industryTransferCommonLogic step2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));

        // 2、数据割接流程表的sql
        sysTenantInfoMapper.deleteSourceResourceType(tenantId, sourceIndustryCode, sourceAppVerCode);
        log.info("VideoOrderInfoService industryTransferCommonLogic step3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        // 如果目标行业、版本资源已经存在就不初始化了
        if (sysTenantInfoMapper.countTargetResourceType(tenantId, targetIndustryCode, targetAppVerCode) == 0) {
            String creator = sysTenantInfoMapper.selectOldResourceTypeCreator(tenantId, sourceIndustryCode, sourceAppVerCode);
            if (StringUtils.isEmpty(creator)) {
                creator = "system";
            }
            sysTenantInfoMapper.initTargetResourceType(creator, tenantId, targetIndustryCode, targetAppVerCode);
        }

        // 3、数据割接流程表的sql
        if (sysTenantInfoMapper.countTargetGridInfo(tenantId, targetIndustryCode) == 0) {
            // 更新网格对应的行业编码为新行业编码
            sysTenantInfoMapper.transferGridInfo(tenantId, sourceIndustryCode, targetIndustryCode);
        }
        log.info("VideoOrderInfoService industryTransferCommonLogic step4 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    private Map<String, List<String>> assembleIndustryMap(List<OrderPackageDO> orderPackageList) {
        Map<String, List<String>> industryCodeMap = Maps.newHashMap();
        String industryCode;
        String appVerCode;
        for (OrderPackageDO orderPackageDO : orderPackageList) {
            industryCode = orderPackageDO.getIndustryCode();
            appVerCode = orderPackageDO.getAppVerCode();
            if (industryCodeMap.containsKey(industryCode)) {
                if (industryCodeMap.get(industryCode).contains(appVerCode)) {
                    continue;
                }
                industryCodeMap.get(industryCode).add(appVerCode);
            }
            else {
                List<String> appVerCodeList = Lists.newArrayList();
                appVerCodeList.add(appVerCode);
                industryCodeMap.put(industryCode, appVerCodeList);
            }
        }
        return industryCodeMap;
    }

    private void checkTransferParam(TransferIndustryDTO param) {
        // 参数校验
        if (param == null
                || StringUtils.isEmpty(param.getTenantId())
                || StringUtils.isEmpty(param.getAcceptNode())
                || param.getAppInfo() == null
                || StringUtils.isEmpty(param.getAppInfo().getIndustryCode())
                || StringUtils.isEmpty(param.getAppInfo().getAppVerCode())
                || param.getTargetApp() == null
                || StringUtils.isEmpty(param.getTargetApp().getIndustryCode())
                || StringUtils.isEmpty(param.getTargetApp().getAppVerCode())) {
            log.error("VideoOrderInfoService checkTransferParam 必填参数不能为空！");
            throw new CustomException("必填参数不能为空！");
        }
    }

    private SysTenantInfo addTenantInfo(CustomerAddVO customerAddVO, SysUser user) {
        String deptId = subStringDept(user.getDeptId());
        SysTenantInfo info = new SysTenantInfo();
        info.setCityId(customerAddVO.getCusRegistryCity());
        info.setProvinceId(customerAddVO.getCusRegistryProvince());
        info.setTradeInfo(null);
        info.setDeptId(deptId);
        info.setTenantName(customerAddVO.getCustomerName());
        info.setDelFlag(OrderConstants.DELETE_FLAG_NORMAL);
        info.setTenantTrades(Lists.newArrayList());
        info.setTenantId(customerAddVO.getCustomerCode());
        info.setCreateBy(user.getUserName());
        info.setCreateTime(new Date());
        return info;
    }

    private AddUserRequest addSysUser(CustomerAddVO customerAddVO) {
        //创建用户
        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setTenantId(customerAddVO.getCustomerCode());
        addUserRequest.setUserName(customerAddVO.getAdmin());
        addUserRequest.setIsTenantAdmin(OrderConstants.IS_TENANT_ADMIN);
        addUserRequest.setStatus(SysConstants.USET_STATUS_0);
        addUserRequest.setUserType(SysConstants.USET_TYPE_2);
        addUserRequest.setNickName(customerAddVO.getCustomerName());
        addUserRequest.setPhonenumber(customerAddVO.getPhoneNum());
        return addUserRequest;
    }

    private UpdateWrapper<SysTenantInfo> assembleTenantInfoUpdateWrap(CustomerAddVO customerAddVO, String loginUserName) {
        UpdateWrapper<SysTenantInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(SysTenantInfo::getCityId, customerAddVO.getCusRegistryCity())
                .set(SysTenantInfo::getTenantName, customerAddVO.getCustomerName())
                .set(SysTenantInfo::getProvinceId, customerAddVO.getCusRegistryProvince())
                .set(SysTenantInfo::getUpdateBy, loginUserName)
                .set(SysTenantInfo::getUpdateTime, DateUtils.getNowDate())
                .eq(SysTenantInfo::getTenantId, customerAddVO.getCustomerCode())
                .eq(SysTenantInfo::getDelFlag, Constants.ZERO);
        return updateWrapper;
    }

    private UpdateWrapper<SysUser> updateSysUser(CustomerAddVO customerAddVO, String loginUserName) {
        //创建用户
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(SysUser::getUserName, customerAddVO.getAdmin())
                .set(SysUser::getNickName, customerAddVO.getCustomerName())
                .set(SysUser::getPhonenumber, customerAddVO.getPhoneNum())
                .set(SysUser::getUpdateBy, loginUserName)
                .set(SysUser::getUpdateTime, DateUtils.getNowDate())
                .eq(SysUser::getTenantId, customerAddVO.getCustomerCode())
                .eq(SysUser::getDelFlag, Constants.ZERO)
                .eq(SysUser::getIsTenantAdmin, Constants.ONE_NUM);
        return updateWrapper;
    }

    private void assembleOrderCustomerDO(OrderGatewayDTO data, SysUser sysUser, String tenantId,
                                         Map<String, BasicDeviceDTO> deviceDTOMap, Map<String, IotDeviceDO> sensorDTOMap,
                                         Map<String, UavDeviceDO> uavDTOMap, Map<String, HornDeviceDO> hornDTOMap,
                                         Map<String, RecorderDeviceDO> recorderDTOMap, String storageTime) {
        data.setBusinessOrg(subStringDept(sysUser.getDeptId()));
        // vm_order_customer表中 subscriberCode 和 subscriberName 存储是相反的 -- 历史逻辑
        data.setSubscriberCode(sysUser.getUserName());
        data.setSubscriberName(sysUser.getUserId());
        data.setOrderTime(DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
        data.setValidTime(DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
        data.setInvalidTime("2099-01-01 12:00:00");
        OrderGatewayDTO.OrderPackages orderPackages = data.getPackages().get(0);
        OrderGatewayDTO.Application application = orderPackages.getApplication();
        //为了填订购的坑，这里把版本名称字段当做应用名称来用 com.chinatower.order.handler.deal.OrderCustomerDeal#addTenantInfo
        application.setProductName(orderPackages.getPackageName());

        if (application.getCapabilities() == null) {
            application.setCapabilities(Lists.newArrayList());
        }
        orderPackages.setStorageTime(storageTime);
        //添加租户和客户信息
        CustomerAddVO customerAddInfo = sysTenantInfoMapper.getCustomerAddInfo(tenantId);
        OrderGatewayDTO.Customer customer = new OrderGatewayDTO.Customer();
        BeanUtils.copyBeanProp(customer, customerAddInfo);
        data.setCustomer(customer);
        //添加设备信息
        if (MapUtils.isNotEmpty(deviceDTOMap)) {
            data.getPackages().get(0).getDevices().stream().filter(x -> deviceDTOMap.containsKey(x.getDeviceCode())).forEach(
                    x -> {
                        BasicDeviceDTO basicDeviceDTO = deviceDTOMap.get(x.getDeviceCode());
                        x.setAdmCode(basicDeviceDTO.getAdmCode());
                        x.setAreaCode(basicDeviceDTO.getAreaCode());
                        x.setHeight(basicDeviceDTO.getHeight());
                        x.setHorizonMax(basicDeviceDTO.getHorizonMax());
                        x.setHorizonMin(basicDeviceDTO.getHorizonMin());
                        x.setLatitude(basicDeviceDTO.getLatitude());
                        x.setLongitude(basicDeviceDTO.getLongitude());
                        x.setLocation(basicDeviceDTO.getLocation());
                        x.setPublicSiteCode(basicDeviceDTO.getPublicSiteCode());
                        x.setSiteCode(basicDeviceDTO.getSiteCode());
                        x.setSiteName(basicDeviceDTO.getSiteName());
                        x.setStatus(basicDeviceDTO.getStatus());
                        x.setVerticalMax(basicDeviceDTO.getVerticalMax());
                        x.setVerticalMin(basicDeviceDTO.getVerticalMin());
                        x.setVisualMax(basicDeviceDTO.getVisualMax());
                        x.setVisualMin(basicDeviceDTO.getVisualMin());
                        x.setAccessNode(basicDeviceDTO.getAccessNode());
                    }
            );
        }
        // 添加传感器（物联和雷达设备）信息
        if (MapUtils.isNotEmpty(sensorDTOMap)) {
            data.getPackages().get(0).getSensorDevice().stream().filter(x -> sensorDTOMap.containsKey(x.getDeviceCode())).forEach(
                    x -> {
                        IotDeviceDO iotDeviceDO = sensorDTOMap.get(x.getDeviceCode());
                        x.setAdmCode(iotDeviceDO.getAdmCode());
                        x.setAreaCode(iotDeviceDO.getAreaCode());
                        x.setHeight(iotDeviceDO.getHeight());
                        x.setLatitude(iotDeviceDO.getLatitude());
                        x.setLongitude(iotDeviceDO.getLongitude());
                        x.setLocation(iotDeviceDO.getLocation());
                        x.setPublicSiteCode(iotDeviceDO.getPublicSiteCode());
                        x.setSiteCode(iotDeviceDO.getSiteCode());
                        x.setSiteName(iotDeviceDO.getSiteName());
                        x.setStatus(iotDeviceDO.getDeviceStatus());
                        x.setDeviceType(iotDeviceDO.getDeviceType());
                        x.setCategoryCode(iotDeviceDO.getCategoryCode());
                        x.setAccessNode(iotDeviceDO.getAccessNode());
                    }
            );
        }

        // 添加无人机设备信息
        if (MapUtils.isNotEmpty(uavDTOMap)) {
            data.getPackages().get(0).getUavDevice().stream().filter(x -> uavDTOMap.containsKey(x.getDeviceCode())).forEach(
                    x -> {
                        UavDeviceDO uavDeviceDO = uavDTOMap.get(x.getDeviceCode());
                        x.setAdmCode(uavDeviceDO.getAdmCode());
                        x.setAreaCode(uavDeviceDO.getAreaCode());
                        x.setLatitude(uavDeviceDO.getLatitude());
                        x.setLongitude(uavDeviceDO.getLongitude());
                        x.setLocation(uavDeviceDO.getLocation());
                        x.setAccessNode(uavDeviceDO.getAccessNode());
                    }
            );
        }

        // 添加喇叭设备信息
        if (MapUtils.isNotEmpty(hornDTOMap)) {
            data.getPackages().get(0).getHornDevice().stream().filter(x -> hornDTOMap.containsKey(x.getDeviceCode())).forEach(
                    x -> {
                        HornDeviceDO hornDeviceDO = hornDTOMap.get(x.getDeviceCode());
                        x.setAdmCode(hornDeviceDO.getAdmCode());
                        x.setAreaCode(hornDeviceDO.getAreaCode());
                        x.setLatitude(hornDeviceDO.getLatitude());
                        x.setLongitude(hornDeviceDO.getLongitude());
                        x.setLocation(hornDeviceDO.getLocation());
                        x.setAccessNode(hornDeviceDO.getAccessNode());
                    }
            );
        }

        // 添加执法记录仪设备信息
        if (MapUtils.isNotEmpty(recorderDTOMap)) {
            data.getPackages().get(0).getRecorderDevice().stream().filter(x -> recorderDTOMap.containsKey(x.getDeviceCode())).forEach(
                    x -> {
                        RecorderDeviceDO recorderDeviceDO = recorderDTOMap.get(x.getDeviceCode());
                        x.setAdmCode(recorderDeviceDO.getAdmCode());
                        x.setAreaCode(recorderDeviceDO.getAreaCode());
                        x.setLatitude(recorderDeviceDO.getLatitude());
                        x.setLongitude(recorderDeviceDO.getLongitude());
                        x.setLocation(recorderDeviceDO.getLocation());
                        x.setAccessNode(recorderDeviceDO.getAccessNode());
                    }
            );
        }
    }

    private String subStringDept(String deptId) {
        if (!StringUtils.isEmpty(deptId) && deptId.length() > 6) {
            deptId.substring(0, 6);
        }
        return deptId;
    }


    private OrderPackageVO assembleSimpleOrderPackage(OrderPackageDO orderPackageDO) {
        OrderPackageVO orderPackageVO = new OrderPackageVO();
        orderPackageVO.setPackageId(orderPackageDO.getId().toString());
        orderPackageVO.setPackageCode(orderPackageDO.getPackageCode());
        orderPackageVO.setDescription(orderPackageDO.getDescription());
        orderPackageVO.setPackageName(orderPackageDO.getPackageName());
        orderPackageVO.setStorageTime(orderPackageDO.getStorageTime());
        return orderPackageVO;
    }

    private void assembleAppData(List<OrderPackageDO> allOrderAppList, OrderPackageVO orderPackageVO, String orderCode, String packageCode) {
        //查询对应订单应用列表
        List<OrderPackageDO> orderAppList = allOrderAppList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderAppList)) {
            List<OrderApplicationVO> appData = new ArrayList<>();
            for (OrderPackageDO packageAppDO : orderAppList) {

                OrderApplicationVO orderApplicationVO = new OrderApplicationVO();
                orderApplicationVO.setAppCode(packageAppDO.getIndustryCode());
                orderApplicationVO.setCapabilitiesStr(packageAppDO.getCapabilitiesStr());
                orderApplicationVO.setProductCode(packageAppDO.getAppVerCode());
                orderApplicationVO.setProductName(packageAppDO.getAppVerName());

                appData.add(orderApplicationVO);
            }
            orderPackageVO.setAppData(appData);
        }
    }

    private void assembleAlgorithmData(List<OrderAlgorithmsDO> allOrderAlgorithmsList, OrderPackageVO orderPackageVO,
                                       List<SysDictDataResponse> dictList, String orderCode, String packageCode) {
        //查询对应订单算法列表
        List<OrderAlgorithmsDO> orderAlgorithmsList = allOrderAlgorithmsList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderAlgorithmsList)) {
            orderAlgorithmsList = orderAlgorithmsList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                    -> new TreeSet<>(Comparator.comparing(OrderAlgorithmsDO::getAlgorithmCode))), ArrayList::new));
            List<OrderAlgorithmsVO> algorithmData = new ArrayList<>();

            for (OrderAlgorithmsDO orderAlgorithmsDO : orderAlgorithmsList) {
                OrderAlgorithmsVO orderAlgorithmsVO = new OrderAlgorithmsVO();

                String algorithmName = orderAlgorithmsDO.getAlgorithmName();
                String str = orderAlgorithmsDO.getAlgorithmCode().substring(0, 2);
                //获取赠送算法
                orderAlgorithmsVO.setAlgorithmName(algorithmName);

                if (!CollectionUtils.isEmpty(dictList)) {
                    String giftAlgorithmEnum = dictList.get(0).getDictValue();
                    if (giftAlgorithmEnum.contains(str)) {
                        orderAlgorithmsVO.setAlgorithmName(algorithmName + "（赠送）");
                    }
                    else {
                        orderAlgorithmsVO.setAlgorithmName(algorithmName);
                    }
                }
                orderAlgorithmsVO.setAlgorithmCode(orderAlgorithmsDO.getAlgorithmCode());
                algorithmData.add(orderAlgorithmsVO);
            }

            orderPackageVO.setAlgorithmData(algorithmData);
        }
    }

    private void assembleRadarData(List<OrderSensorDeviceDO> allOrderSensorDeviceInfoList, OrderPackageVO orderPackageVO,
                                   String orderCode, String packageCode) {
        //查询对应订单物联网设备信息列表 20220719 合并 单雷达列表
        List<OrderSensorDeviceDO> orderSensorDeviceInfoList = allOrderSensorDeviceInfoList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderSensorDeviceInfoList)) {
            List<OrderSensorDeviceDO> sensorInfoList = orderSensorDeviceInfoList.stream().filter(sensor ->
                    Objects.equals(OrderConstants.ORDER_RADAR_CODE, sensor.getCategoryCode())).collect(Collectors.toList());
            List<OrderRadarDeviceVO> radarData = new ArrayList<>();
            for (OrderSensorDeviceDO orderRadarDeviceDO : sensorInfoList) {
                OrderRadarDeviceVO radarDeviceVO = new OrderRadarDeviceVO();
                BeanUtils.copyBeanProp(radarDeviceVO, orderRadarDeviceDO);
                radarData.add(radarDeviceVO);
            }
            orderPackageVO.setRadarData(radarData);
        }
    }

    private void assembleUavDeviceData(List<OrderUavDeviceDO> allOrderUavDeviceList, OrderPackageVO orderPackageVO,
                                       String orderCode, String packageCode,
                                       Map<String, List<OrderAlgorithmConfigVO>> uavAlgorithmConfigMap) {
        // 查询对应订单无人机设备列表
        List<OrderUavDeviceDO> orderUavDeviceList = allOrderUavDeviceList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderUavDeviceList)) {
            List<OrderUavDeviceVO> uavDeviceDataList = new ArrayList<>();
            for (OrderUavDeviceDO orderUavDeviceDO : orderUavDeviceList) {
                OrderUavDeviceVO uavDeviceVO = new OrderUavDeviceVO();
                BeanUtils.copyBeanProp(uavDeviceVO, orderUavDeviceDO);
                String key = orderCode + ":" + orderUavDeviceDO.getDeviceCode();
                uavDeviceVO.setAlgorithmConfigList(uavAlgorithmConfigMap.get(key));
                uavDeviceDataList.add(uavDeviceVO);
            }
            orderPackageVO.setUavDeviceData(uavDeviceDataList);
        }
    }

    private void assembleUavNestDeviceData(List<OrderUavNestDeviceDO> allOrderUavNestDeviceList, OrderPackageVO orderPackageVO,
                                       String orderCode, String packageCode) {
        // 查询对应订单无人机设备列表
        List<OrderUavNestDeviceDO> orderUavNestDeviceList = allOrderUavNestDeviceList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderUavNestDeviceList)) {
            List<OrderUavNestDeviceVO> uavNestDeviceDataList = new ArrayList<>();
            for (OrderUavNestDeviceDO orderUavNestDeviceDO : orderUavNestDeviceList) {
                OrderUavNestDeviceVO uavNestDeviceVO = new OrderUavNestDeviceVO();
                BeanUtils.copyBeanProp(uavNestDeviceVO, orderUavNestDeviceDO);
                uavNestDeviceDataList.add(uavNestDeviceVO);
            }
            orderPackageVO.setUavNestDeviceData(uavNestDeviceDataList);
        }
    }

    private void assembleHornDeviceData(List<OrderHornDeviceDO> allOrderHornDeviceList, OrderPackageVO orderPackageVO,
                                        String orderCode, String packageCode) {
        // 查询对应订单喇叭设备列表
        List<OrderHornDeviceDO> orderHornDeviceList = allOrderHornDeviceList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderHornDeviceList)) {
            List<OrderHornDeviceVO> hornDeviceDataList = new ArrayList<>();
            for (OrderHornDeviceDO orderHornDeviceDO : orderHornDeviceList) {
                OrderHornDeviceVO hornDeviceVO = new OrderHornDeviceVO();
                BeanUtils.copyBeanProp(hornDeviceVO, orderHornDeviceDO);
                hornDeviceDataList.add(hornDeviceVO);
            }
            orderPackageVO.setHornDeviceData(hornDeviceDataList);
        }
    }

    private void assembleRecorderDeviceData(List<OrderRecorderDeviceDO> allOrderRecorderDeviceList, OrderPackageVO orderPackageVO,
                                            String orderCode, String packageCode) {
        // 查询对应订单执法记录仪设备列表
        List<OrderRecorderDeviceDO> orderRecorderDeviceList = allOrderRecorderDeviceList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderRecorderDeviceList)) {
            List<OrderRecorderDeviceVO> recorderDeviceDataList = new ArrayList<>();
            for (OrderRecorderDeviceDO orderRecorderDeviceDO : orderRecorderDeviceList) {
                OrderRecorderDeviceVO recorderDeviceVO = new OrderRecorderDeviceVO();
                BeanUtils.copyBeanProp(recorderDeviceVO, orderRecorderDeviceDO);
                recorderDeviceDataList.add(recorderDeviceVO);
            }
            orderPackageVO.setRecorderDeviceData(recorderDeviceDataList);
        }
    }

    private void assembleSatelliteData(List<OrderServiceRelDO> allOrderServiceRelDOList, OrderPackageVO orderPackageVO, List<OrderServiceRelVO> allAreaVOList,
                                       String orderCode, String packageCode) {
        //查询对应订单卫星列表
        List<OrderServiceRelDO> orderServiceRelDOList = allOrderServiceRelDOList.stream().filter(item -> (Objects.equals(orderCode, item.getOrderCode()) && Objects.equals(packageCode, item.getPackageCode()))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(orderServiceRelDOList)) {
            orderServiceRelDOList = orderServiceRelDOList.stream().filter(serviceRel ->
                    Objects.equals(OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE, serviceRel.getServiceCode())).collect(Collectors.toList());
            List<OrderServiceRelVO> satelliteData = new ArrayList<>();
            for (OrderServiceRelDO orderServiceRelDO : orderServiceRelDOList) {
                OrderServiceRelVO orderServiceRelVO = new OrderServiceRelVO();
                BeanUtils.copyBeanProp(orderServiceRelVO, orderServiceRelDO);
                //获取区县对应的省份地市
                if (!StringUtils.isEmpty(orderServiceRelDO.getCountyCode())) {
                    //List<OrderServiceRelVO> areaVOList = orderServiceRelRepository.getAreaInfo(Arrays.asList(orderServiceRelDO.getCountyCode()));
                    //获取区县对应的省份地市
                    List<OrderServiceRelVO> areaVOList = allAreaVOList.stream().filter(item -> Objects.equals(item.getCountyCode(), orderServiceRelDO.getCountyCode())).collect(Collectors.toList());

                    Set<String> provinceNameList = areaVOList.stream().map(OrderServiceRelVO::getProvinceName).collect(Collectors.toSet());
                    Set<String> cityNameList = areaVOList.stream().map(OrderServiceRelVO::getCityName).collect(Collectors.toSet());
                    Set<String> areaList = areaVOList.stream().map(OrderServiceRelVO::getCountyName).collect(Collectors.toSet());
                    orderServiceRelVO.setProvinceName(provinceNameList.stream().collect(Collectors.joining(",")));
                    orderServiceRelVO.setCityName(cityNameList.stream().collect(Collectors.joining(",")));
                    orderServiceRelVO.setCountyName(areaList.stream().collect(Collectors.joining(",")));
                }
                satelliteData.add(orderServiceRelVO);
            }
            orderPackageVO.setSatelliteData(satelliteData);
        }
    }

    private void saveSysTenantInfoToMongoHis(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            return;
        }
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectTenantInfoByTenantId(tenantId);
        List<Long> idList = sysTenantInfos.stream().map(SysTenantInfo::getId).collect(Collectors.toList());
        List<SysTenantInfoHisMongoDTO> infos = sysTenantInfoMapper.selectByIdList(idList);
        if (CollectionUtils.isEmpty(infos)) {
            return;
        }
        sysTenantInfoHisMongoDAO.insertAll(infos, MongoTableConstants.MONGO_TABLE_NAME_SYS_TENANT_INFO_HIS, MongoDBType.PRIMARY);
        sysTenantInfoMapper.deletePhysicalByIdList(idList);
    }

    private void saveSysUserToMongoHis(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            return;
        }
        List<SysUser> sysUsers = sysUserMapper.selectByTenantId(tenantId);
        List<String> userIdList = sysUsers.stream().map(SysUser::getUserId).collect(Collectors.toList());
        List<SysUserHisMongoDTO> infos = sysUserMapper.selectByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(infos)) {
            return;
        }
        sysUserHisMongoDAO.insertAll(infos, MongoTableConstants.MONGO_TABLE_NAME_SYS_USER_HIS, MongoDBType.PRIMARY);
        sysUserMapper.deletePhysicalByUserIdList(userIdList);
    }

    private Map<String, List<OrderAlgorithmConfigVO>> buildUavAlgorithmConfigMap(
            List<String> orderCodeList, List<OrderUavDeviceDO> allOrderUavDeviceList) {
        Map<String, List<OrderAlgorithmConfigVO>> uavAlgorithmConfigMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(allOrderUavDeviceList)) {
            return uavAlgorithmConfigMap;
        }
        for (String oc : orderCodeList) {
            List<OrderUavDeviceDO> orderUavDevices = allOrderUavDeviceList.stream()
                    .filter(item -> Objects.equals(oc, item.getOrderCode()))
                    .collect(Collectors.toList());
            List<String> uavDeviceCodes = orderUavDevices.stream()
                    .map(OrderUavDeviceDO::getDeviceCode)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(uavDeviceCodes)) {
                List<OrderAlgorithmConfigVO> algConfigList = queryAlgorithmConfigWithFallback(2, oc);
                for (OrderAlgorithmConfigVO vo : algConfigList) {
                    // 兜底数据无设备编码，按订单下每个无人机设备填充
                    if (StringUtils.isEmpty(vo.getDeviceCode())) {
                        for (OrderUavDeviceDO uavDeviceDO : orderUavDevices) {
                            String key = oc + ":" + uavDeviceDO.getDeviceCode();
                            OrderAlgorithmConfigVO copy = new OrderAlgorithmConfigVO();
                            copy.setDeviceCode(uavDeviceDO.getDeviceCode());
                            copy.setAlgorithmCode(vo.getAlgorithmCode());
                            copy.setAlgorithmName(vo.getAlgorithmName());
                            uavAlgorithmConfigMap.computeIfAbsent(key, k -> new ArrayList<>()).add(copy);
                        }
                    } else {
                        String key = oc + ":" + vo.getDeviceCode();
                        uavAlgorithmConfigMap.computeIfAbsent(key, k -> new ArrayList<>()).add(vo);
                    }
                }
            }
        }
        return uavAlgorithmConfigMap;
    }

    private void saveSysDeptToMongoHis(String tenantId, String industryCode) {
        List<SysDeptHisMongoDTO> infos = sysTenantDeptMapper.selectByTenantIdAndIndustryCode(tenantId, industryCode);
        if (CollectionUtils.isEmpty(infos)) {
            return ;
        }
        List<Long> ids = infos.stream().map(SysDeptHisMongoDTO::getId).collect(Collectors.toList());
        sysDeptHisMongoDAO.insertAll(infos, MongoTableConstants.MONGO_TABLE_NAME_SYS_DEPT_HIS, MongoDBType.PRIMARY);
        sysTenantDeptMapper.physicalDeleteByIds(ids);
    }
}
