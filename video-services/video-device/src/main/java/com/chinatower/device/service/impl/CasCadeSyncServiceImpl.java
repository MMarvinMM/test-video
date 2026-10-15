package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dto.*;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.service.ICasCadeService;
import com.chinatower.device.service.ICasCadeSyncService;
import com.chinatower.device.utils.DeviceCommonService;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.VideoConstants;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CasCadeSyncServiceImpl implements ICasCadeSyncService {

    @Autowired
    private ICasCadeService casCadeService;

    @Autowired
    private ICasCadeInfoService iCasCadeInfoService;

    @Resource
    private NetConnectUtils connectUtils;

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    @Autowired
    private SysAreaRootService sysAreaRootService;

    /**
     * 6.51.	删除访问配置权限
     * @param req
     * @return
     */
    @Override
    public Result<String> deleteCascade(CasCadeDeleteVO req) {
        log.info(DeviceConstants.DELETE_LOG_REQ_MSG, req);
        try{
            NetConnectUtils.RequestParamter<CasCadeDeleteVO> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            parameter.setBody(req);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_CASCADE);
            parameter.setPath(path);

            // 查询请求头逻辑
            Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
            String platformCode = platformCodeResult.getData();

            ResultEntity resultEntity = connectUtils.post(parameter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(parameter)));
            // 获取返回日志
            log.info(DeviceConstants.DELETE_LOG_RESP_MSG, resultEntity);
            return dealRes(resultEntity);
        } catch (Exception e) {
            log.error(DeviceConstants.DELETE_ERROR_MSG, e);
        }
        return Result.fail("下级平台配置访问权限删除下发失败！");
    }

    /**
     *6.52.	下级平台配置访问权限下发
     * @param request
     * @return
     */
    @Override
    public Result<String> allocation(CasCadeSyncReq request) {
        request.setPermissionFlag(IdUtils.fastSimpleUUID());
        request.setRegionType("3");
        return this.targetAllocation(request);
    }

    /**
     * 请求基础接口进行下发
     * @param req
     * @return
     */
    public Result<String> targetAllocation(CasCadeSyncReq req) {
        log.info(DeviceConstants.ALLOCATION_LOG_REQ_MSG, req);
        try{
            // 查询请求头逻辑
            Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
            String platformCode = platformCodeResult.getData();

            NetConnectUtils.RequestParamter<CasCadeSyncReq> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数 受理节点、接入节点都填充当前节点
            req.setAcceptNode(platformCode);
            req.setAccessNode(platformCode);
            parameter.setBody(req);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ALLOCATION);
            parameter.setPath(path);
            ResultEntity resultEntity = connectUtils.post(parameter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(parameter)));

            // 获取返回日志
            log.info(DeviceConstants.ALLOCATION_LOG_RESP_MSG, resultEntity);
            return dealRes(resultEntity);
        } catch (Exception e) {
            log.error(DeviceConstants.ALLOCATION_LOG_ERROR_MSG, e);
        }
        return Result.fail("下级平台配置访问权限下发失败！");
    }

    /**
     * 解析基础返回报文
     * @param cr
     * @return
     */
    private Result<String> dealRes(ResultEntity cr) {
        if (DeviceCommonService.crIsSuccess(cr)) {
            return Result.success(Constants.SUCCESS_MSG);
        } else {
            return Result.fail(Constants.ALLOCATION + "操作失败:" + cr.getErrorCode() +cr.getErrorMsg());
        }
    }

    /**
     * 基础平台同步级联信息
     * @param reqList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result<String> synCascadePlatform(List<SynCobtainVO> reqList) {
        Result<String> result = Result.success("同步级联信息成功");
        log.info(DeviceConstants.SYNC_CASCADE_PLATFORM_REQ_MSG, reqList);
        //新增的级联平台集合
        List<SynCobtainVO> addOrEditList = reqList.stream().filter(item ->
                DeviceConstants.OPT_TYPE_ADD == item.getOptType()
                        || DeviceConstants.OPT_TYPE_UPDATE == item.getOptType()).collect(Collectors.toList());
        //批量插入
        if (!CollectionUtils.isEmpty(addOrEditList)) {
            //先校验库里是否存在
            List<SynCobtainVO> existList = casCadeService.findCascadeByPlatCodeList(addOrEditList);
            if (CollectionUtils.isEmpty(existList)) {
                casCadeService.batchInsert(addOrEditList);
            } else {
                List<String> existCodeList = existList.stream().map(SynCobtainVO::getPlatCode).collect(Collectors.toList());
                List<SynCobtainVO> addList = addOrEditList.stream().filter(item -> !existCodeList.contains(item.getPlatCode())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(addList)) {
                    casCadeService.batchInsert(addList);
                }
                List<SynCobtainVO> editList = addOrEditList.stream().filter(item -> existCodeList.contains(item.getPlatCode())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(editList)) {
                    casCadeService.batchUpdate(editList);
                }
            }
        }
        //删除的级联平台集合
        List<SynCobtainVO> deleteList = reqList.stream().filter(item ->
                DeviceConstants.OPT_TYPE_DELETE == item.getOptType()).collect(Collectors.toList());
        //批量删除
        if (!CollectionUtils.isEmpty(deleteList)) {
            //校验是否存在关联 `sys_tenant_cascade_device_rel`
            List<String> effPlatCodeList = casCadeService.getEffPlatCode(deleteList.stream().map(SynCobtainVO::getPlatCode).collect(Collectors.toList()));
            casCadeService.deleteByIds(deleteList);

            // 平台绑定的租户编码列表
            List<String> tenantIdList = casCadeService.getRelTenantIdByPlatCode(deleteList);
            //删除 sys_tenant_cascade_device_rel
            casCadeService.delCascadeRel(deleteList);
            //删除 sys_tenant_cascade_org_rel
            casCadeService.delCascadeOrgRel(deleteList);

            if (!CollectionUtils.isEmpty(tenantIdList)) {
                // 删除指定级联平台关联之后还有其他平台绑定关系的租户编码列表，不能对改租户进行级联关闭操作
                List<String> existsPlatTenantIdList = casCadeService.getRelTenantIdByTenantIdList(tenantIdList);
                if (!CollectionUtils.isEmpty(existsPlatTenantIdList)) {
                    // 还有平台
                    tenantIdList.removeAll(existsPlatTenantIdList);
                }
                //清空sys_tenant_info里的级联信息
                casCadeService.clearCascadeTenant(tenantIdList);
            }
            //调用删除下发
            if (!CollectionUtils.isEmpty(effPlatCodeList)) {
                CasCadeDeleteVO req = new CasCadeDeleteVO();
                req.setPlatAsIds(effPlatCodeList);
                result = deleteCascade(req);
            }
        }
        log.info(DeviceConstants.SYNC_CASCADE_PLATFORM_SUCCESS_MSG, reqList);
//        if (ResultCode.SUCCESS.getCode() != result.getCode()) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        }
        return result;
    }

    /**
     * 订购变更后需要重新同步级联平台信息
     * 根据tenantId编码查询0，1，2状态的订购的设备deviceCode编码，
     * 与sys_tenant_cascade_device_rel.deviceCode比较，减少的deviceCode，调用6.52下发
     */
    @Override
    public Result<String> synDelCascadeByOrder(CasCadeSyncVO cadeSyncReq) {
        Result<String> result = Result.success("更新成功");
        String tenantId = cadeSyncReq.getTenantId();

        List<String> relPlatCodeList = casCadeService.queryCascadeRelPlatCodeByTenantId(tenantId);

        if (CollectionUtils.isEmpty(relPlatCodeList)) {
            log.info("CasCadeSyncServiceImpl synDelCascadeByOrder 客户没有关联的级联平台，不需处理");
            return result;
        }

        //根据tenantId查询0，1，2状态的deviceCode
        List<String> orderDeviceCodeList = casCadeService.qryOrderDevCodeByTenantId(tenantId);
        log.info("根据tenantId查询0，1，2状态的deviceCode:{}",orderDeviceCodeList);

        List<CasCadeDto> relPlatTenantInfoList = casCadeService.queryCascadeRelInfoByPlatCodeList(relPlatCodeList);

        Map<String, List<String>> platCodeToTenantIdsMap = relPlatTenantInfoList.stream()
                .collect(Collectors.groupingBy(
                        CasCadeDto::getPlatCode,
                        Collectors.mapping(CasCadeDto::getTenantId, Collectors.toList())
                ));
        /*
         * 根据客户编码查询所有关联的平台编码
         * 1、如果客户退订，把平台客户关联表该客户删除关联，如果这个平台只有这一个客户，关闭级联配置
         * 2、如果客户变更或者退订
         */
        Map<String, List<String>> openSyncPlatCodeMap = Maps.newHashMap();
        Map<String, List<String>> closeSyncPlatCodeMap = Maps.newHashMap();

        if (CollectionUtils.isEmpty(orderDeviceCodeList)) {

            for (String relPlatCode : relPlatCodeList) {
                List<String> platTenantIdList = platCodeToTenantIdsMap.get(relPlatCode);
                if (CollectionUtils.isNotEmpty(platTenantIdList)) {
                    platTenantIdList = platTenantIdList.stream().filter(x -> !StringUtils.equals(x, tenantId)).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(platTenantIdList)) {
                    openSyncPlatCodeMap.put(relPlatCode, platTenantIdList);
                } else {
                    closeSyncPlatCodeMap.put(relPlatCode, Lists.newArrayList(tenantId));
                }
            }
            // 删除所有关联
            casCadeService.deleteCascadeRelByTenantId(tenantId);
        } else {
            openSyncPlatCodeMap = platCodeToTenantIdsMap;
        }

        Date now = new Date();
        String operator = "system-yg";

        log.info("CasCadeSyncServiceImpl synDelCascadeByOrder openSyncPlatCodeMap:{}, closeSyncPlatCodeMap:{}",
                JSON.toJSONString(openSyncPlatCodeMap), JSON.toJSONString(closeSyncPlatCodeMap));

        // 批量处理开启逻辑
        if (MapUtils.isNotEmpty(openSyncPlatCodeMap)) {
            for (Map.Entry<String, List<String>> entry : openSyncPlatCodeMap.entrySet()) {
                String platCode = entry.getKey();
                List<String> tenantIdList = entry.getValue();
                CasCadeSyncVO cascadeParam = new CasCadeSyncVO();
                cascadeParam.setPlatCode(platCode);
                cascadeParam.setTenantIdList(tenantIdList);
                Result<CasCadeBindPlatInfoVO> bindInfo = queryCascadeInfoList(cascadeParam);
                CasCadeBindPlatInfoVO cascadePlatInfo = bindInfo.getData();

                // 根据平台递维度归获取设备子列表
                List<CasCadeDeviceBindVO> cascadeDeviceList = getCascadeDeviceList(cascadePlatInfo, platCode, operator, now);
                // 根据平台递维度归获取组织子列表
                List<CasCadeOrgBindVO> cascadeOrgList = getCascadeOrgList(cascadePlatInfo, platCode, operator, now);
                cascadePlatInfo.setCascadeStatus(DeviceConstants.ONE);
                // 同步基础平台
                result = syncPlatCodeInfo(platCode, cascadePlatInfo, cascadeDeviceList, cascadeOrgList);
            }
        }

        // 批量处理删除逻辑
        if (MapUtils.isNotEmpty(closeSyncPlatCodeMap)) {
            // 处理某客户级联平台关闭逻辑
            for (Map.Entry<String, List<String>> entry : closeSyncPlatCodeMap.entrySet()) {
                String platCode = entry.getKey();
                List<String> tenantIdList = entry.getValue();
                CasCadeBindPlatInfoVO closeParam = new CasCadeBindPlatInfoVO();
                closeParam.setPlatCode(platCode);
                closeParam.setTenantIdList(tenantIdList);
                closeParam.setCascadeStatus(DeviceConstants.ZERO);
                closeParam.setOperator(operator);
                closeParam.setOperatorName(operator);
                closeParam.setModifyTime(now);
                result = handleCloseCascadeRelLogic(closeParam);
            }
        }

        return result;
    }

    /**
     * 获取未被选择的级联平台信息
     */
    @Override
    public Result<List<SynCobtainVO>> findCascadeList(CasCadeSyncVO cadeSyncReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        List<SynCobtainVO> synCobtainVOS = casCadeService.findCascadeList(platformCode);
        return Result.data(synCobtainVOS);
    }

    @Override
    @Transactional
    public Result<String> cascadeBinding(CasCadeBindPlatInfoVO param) {
        Result<String> result;
        // 级联配置开启
        if (DeviceConstants.ONE.equals(param.getCascadeStatus())) {
            result = handleOpenCascadeRelLogic(param);
        }
        // 级联配置关闭
        else {
            result = handleCloseCascadeRelLogic(param);
        }
        return result;
    }

    @Override
    public Result<List<String>> queryCascadeBindingTenantId(String platCode) {
        return Result.data(casCadeService.queryCascadeBindingTenantId(platCode));
    }

    private List<DeviceTreeVO> getDeviceChannelMonitorTree(List<String> tenantIdList) {
        DeviceReqDTO orderParam = new DeviceReqDTO();
        orderParam.setTenantIdList(tenantIdList);
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        return iotDeviceRepository.getDeviceChannelMonitorTree(orderParam, platformCode);
    }

    private List<CasCadeBindDeviceInfoTreeVO> getOrderTreeList(List<DeviceTreeVO> orderList, List<String> orderDeviceCodeList) {
        List<CasCadeBindDeviceInfoTreeVO> orderTreeList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(orderList)) {
            return orderTreeList;
        }
        for (DeviceTreeVO provinceInfo : orderList) {
            CasCadeBindDeviceInfoTreeVO province = new CasCadeBindDeviceInfoTreeVO();
            province.setLevel(2);
            province.setOrgType(1);
            province.setParentCode(DeviceConstants.TOWER_ROOT_AREA);
            province.setCode(provinceInfo.getCode());
            province.setName(provinceInfo.getName());
            province.setNum(0);
            if (CollectionUtils.isNotEmpty(provinceInfo.getList())) {
                List<CasCadeBindDeviceInfoTreeVO> cityList = Lists.newArrayList();
                for (DeviceTreeVO cityInfo : provinceInfo.getList()) {
                    CasCadeBindDeviceInfoTreeVO city = new CasCadeBindDeviceInfoTreeVO();
                    city.setLevel(3);
                    city.setOrgType(1);
                    city.setParentCode(provinceInfo.getCode());
                    city.setCode(cityInfo.getCode());
                    city.setName(cityInfo.getName());
                    city.setNum(0);
                    if (CollectionUtils.isNotEmpty(cityInfo.getList())) {
                        List<CasCadeBindDeviceInfoTreeVO> countyList = Lists.newArrayList();
                        for (DeviceTreeVO countyInfo : cityInfo.getList()) {
                            CasCadeBindDeviceInfoTreeVO county = new CasCadeBindDeviceInfoTreeVO();
                            county.setLevel(4);
                            county.setOrgType(1);
                            county.setParentCode(cityInfo.getCode());
                            county.setCode(countyInfo.getCode());
                            county.setName(countyInfo.getName());
                            county.setNum(0);
                            countyList.add(county);


                            // 过滤掉没有在离线的设备
                            List<String> deviceCodeList = countyInfo.getList().stream().filter(iotDevice -> StringUtils.isNotEmpty(iotDevice.getStatus())
                                    && (iotDevice.getStatus().equals(com.chinatower.platform.constants.Constants.ZERO) || iotDevice.getStatus().equals(com.chinatower.platform.constants.Constants.ONE))
                            ).map(DeviceTreeVO::getDeviceCode).distinct().collect(Collectors.toList());

                            if (CollectionUtils.isNotEmpty(deviceCodeList)) {
                                orderDeviceCodeList.addAll(deviceCodeList);
                            }
                        }
                        city.setList(countyList);
                    }
                    cityList.add(city);
                }
                province.setList(cityList);
            }
            orderTreeList.add(province);
        }
        return orderTreeList;
    }

    @Override
    public Result<CasCadeBindPlatInfoVO> queryCascadeInfoList(CasCadeSyncVO param) {
        if (param == null || CollectionUtils.isEmpty(param.getTenantIdList()) || StringUtils.isEmpty(param.getPlatCode())) {
            throw new CustomException("平台编码、客户编码参数不能为空");
        }
        String platCode = param.getPlatCode();
        CasCadeBindPlatInfoVO casCadeInfo = new CasCadeBindPlatInfoVO();
        casCadeInfo.setPlatCode(platCode);
        casCadeInfo.setCascadeStatus("0");
        List<CasCadeDto> casCadeDtoList = casCadeService.qryCasCadeByPlatCode(platCode);
        List<CasCadeBindDeviceInfoTreeVO> cascadeOrgList = casCadeService.queryCascadeOrgInfoByPlatCode(param.getPlatCode());

        if (CollectionUtils.isNotEmpty(casCadeDtoList) && CollectionUtils.isNotEmpty(cascadeOrgList)) {
            List<DeviceTreeVO> orderList = getDeviceChannelMonitorTree(param.getTenantIdList());
            // 订购设备编码列表
            List<String> orderDeviceCodeList = Lists.newArrayList();
            // 客户订购设备树
            List<CasCadeBindDeviceInfoTreeVO> orderTreeList = getOrderTreeList(orderList, orderDeviceCodeList);
            Map<String, CasCadeDto> channelMap = casCadeDtoList.stream()
                    .filter(x -> orderDeviceCodeList.contains(x.getDeviceCode()))
                    .collect(Collectors.toMap(x -> (x.getDeviceCode() + "#" + x.getChannelCode()),
                            (p) -> p, (a, b) -> a));

            List<CasCadeDto> deviceList = new ArrayList<>(channelMap.values());

            log.info("queryCascadeInfoList casCadeDtoList:{} channelMap:{}, deviceList:{} orderDeviceCodeList:{}",
                    JSON.toJSONString(casCadeDtoList), JSON.toJSONString(channelMap), JSON.toJSONString(deviceList), JSON.toJSONString(orderDeviceCodeList));

            if (BooleanUtils.isTrue(param.isNeedDevice())) {
                // 设备map key -> deviceCode
                Map<String, List<CasCadeDto>> deviceChannelMap = deviceList.stream().collect(Collectors.groupingBy(CasCadeDto::getDeviceCode));
                // 设备map key -> deviceCode
                Map<String, CasCadeDto> deviceInfoMap = deviceList.stream().collect(Collectors.toMap(CasCadeDto::getDeviceCode, (p) -> p, (a, b) -> a));
                List<CasCadeBindDeviceInfoTreeVO> deviceBindList = getCasCadeBindDeviceInfoTreeVOS(deviceChannelMap, deviceInfoMap);
                if (CollectionUtils.isNotEmpty(deviceBindList)) {
                    cascadeOrgList.addAll(deviceBindList);
                }
                log.info("queryCascadeInfoListV2 deviceBindList:{}, cascadeOrgList:{}", JSON.toJSONString(deviceBindList), JSON.toJSONString(cascadeOrgList));
            }
            List<CasCadeBindDeviceInfoTreeVO> treeList = buildOrgTree(cascadeOrgList);
            List<CasCadeBindDeviceInfoTreeVO> rootTreeList = new ArrayList<>(1);
            if (!treeList.isEmpty()) {
                log.info("queryCascadeInfoList before treeList:{} orderTreeList:{}", JSON.toJSONString(treeList), JSON.toJSONString(orderTreeList));
                // 和订购取交集
                treeList = filterOrderTree(treeList, orderTreeList);

                log.info("queryCascadeInfoList after treeList:{}", JSON.toJSONString(treeList));

                // 补充中国根节点
                CasCadeBindDeviceInfoTreeVO root = new CasCadeBindDeviceInfoTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOrgType(1);
                root.setList(treeList);
                root.setNum(sum(root, null, 0));
                rootTreeList.add(root);
            }
            casCadeInfo.setCascadeStatus("1");
            casCadeInfo.setDeviceInfoTreeList(rootTreeList);
            casCadeInfo.setPermissionTypeList(Splitter.on(",").splitToList(casCadeDtoList.get(0).getPermissionType()));
        }
        return Result.data(casCadeInfo);
    }

    private static final SecureRandom random = new SecureRandom();;

    @Override
    public Result<String> generateCascadeOrgCode(CasCadeGenerateOrgCodeVO param) {
        if (param == null
                || StringUtils.isEmpty(param.getCountyCode())
                || !(Objects.equals(param.getType(), 1) || Objects.equals(param.getType(), 2))) {
            throw new CustomException("必填参数错误");
        }
        String industryCode = "00";
        String netFlag = "0";
        String orgCode = Objects.equals(param.getType(), 1) ? "215" : "216";
        // 生成一个6位的随机数，确保首位不是0
        int random6 = 100000 + random.nextInt(900000);
        return Result.data(String.format("%s00%s%s%s%s", param.getCountyCode(), industryCode, orgCode, netFlag, random6));
    }

    private List<CasCadeBindDeviceInfoTreeVO> filterOrderTree(List<CasCadeBindDeviceInfoTreeVO> treeList, List<CasCadeBindDeviceInfoTreeVO> orderTreeList) {
        List<CasCadeBindDeviceInfoTreeVO> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(orderTreeList)) {
            return resultList;
        }
        if (CollectionUtils.isEmpty(treeList)) {
            // 订购数转为组织树
            return orderTreeList;
        }
        // 两棵树都不为空，则取交集
        Map<String, CasCadeBindDeviceInfoTreeVO> orderProvinceMap = Maps.newHashMap();
        Map<String, CasCadeBindDeviceInfoTreeVO> orderCityMap = Maps.newHashMap();
        Map<String, CasCadeBindDeviceInfoTreeVO> orderCountyMap = Maps.newHashMap();
        for (CasCadeBindDeviceInfoTreeVO provinceInfo : orderTreeList) {
            if (CollectionUtils.isNotEmpty(provinceInfo.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cityInfo : provinceInfo.getList()) {
                    if (CollectionUtils.isNotEmpty(cityInfo.getList())) {
                        for (CasCadeBindDeviceInfoTreeVO countyInfo : cityInfo.getList()) {
                            orderCountyMap.put(countyInfo.getCode(), countyInfo);
                        }
                    }
                    orderCityMap.put(cityInfo.getCode(), cityInfo);
                }
            }
            orderProvinceMap.put(provinceInfo.getCode(), provinceInfo);
        }

        /*
         * 1、遍历级联平台配置树
         * 2、如果订购省节点不包含当前级联平台树省节点，则跳过
         * 2、如果
         */

        for (CasCadeBindDeviceInfoTreeVO provinceInfo : treeList) {
            if (!orderProvinceMap.containsKey(provinceInfo.getCode())) {
                continue;
            }
            List<CasCadeBindDeviceInfoTreeVO> resultCityList = Lists.newArrayList();
            List<String> treeCityCodeList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(provinceInfo.getList())) {

                for (CasCadeBindDeviceInfoTreeVO cityInfo : provinceInfo.getList()) {
                    if (!orderCityMap.containsKey(cityInfo.getCode())) {
                        continue;
                    }
                    List<CasCadeBindDeviceInfoTreeVO> resultCountyList = Lists.newArrayList();
                    List<String> treeCountyCodeList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(cityInfo.getList())) {
                        for (CasCadeBindDeviceInfoTreeVO countyInfo : cityInfo.getList()) {
                            if (orderCountyMap.containsKey(countyInfo.getCode())) {
                                resultCountyList.add(countyInfo);
                            }
                        }
                        treeCountyCodeList = cityInfo.getList().stream().map(CasCadeBindDeviceInfoTreeVO::getCode).collect(Collectors.toList());
                    }
                    CasCadeBindDeviceInfoTreeVO orderCityInfo = orderCityMap.get(cityInfo.getCode());
                    if (orderCityInfo != null && CollectionUtils.isNotEmpty(orderCityInfo.getList())) {
                        for (CasCadeBindDeviceInfoTreeVO orderCountyInfo : orderCityInfo.getList()) {
                            if (!treeCountyCodeList.contains(orderCountyInfo.getCode())) {
                                resultCountyList.add(orderCountyInfo);
                            }
                        }
                    }
                    cityInfo.setList(resultCountyList);
                    resultCityList.add(cityInfo);
                }
                treeCityCodeList = provinceInfo.getList().stream().map(CasCadeBindDeviceInfoTreeVO::getCode).collect(Collectors.toList());
            }

            CasCadeBindDeviceInfoTreeVO orderProvinceInfo = orderProvinceMap.get(provinceInfo.getCode());
            if (orderProvinceInfo != null && CollectionUtils.isNotEmpty(orderProvinceInfo.getList())) {
                for (CasCadeBindDeviceInfoTreeVO orderCityInfo : orderProvinceInfo.getList()) {
                    if (!treeCityCodeList.contains(orderCityInfo.getCode())) {
                        resultCityList.add(orderCityInfo);
                    }
                }
            }

            provinceInfo.setList(resultCityList);
            resultList.add(provinceInfo);
        }

        // 追加订购树中存在，级联树中没有的省份节点
        List<String> treeProvinceCodeList = treeList.stream().map(CasCadeBindDeviceInfoTreeVO::getCode).collect(Collectors.toList());
        for (Map.Entry<String, CasCadeBindDeviceInfoTreeVO> entry : orderProvinceMap.entrySet()) {
            String provinceCode = entry.getKey();
            if (!treeProvinceCodeList.contains(provinceCode)) {
                resultList.add(entry.getValue());
            }
        }
        return resultList;
    }

    private static List<CasCadeBindDeviceInfoTreeVO> getCasCadeBindDeviceInfoTreeVOS(Map<String, List<CasCadeDto>> deviceChannelMap, Map<String, CasCadeDto> deviceInfoMap) {
        List<CasCadeBindDeviceInfoTreeVO> deviceBindList = Lists.newArrayList();
        for (Map.Entry<String, List<CasCadeDto>> deviceChannelEntry : deviceChannelMap.entrySet()) {
            CasCadeBindDeviceInfoTreeVO deviceInfo = new CasCadeBindDeviceInfoTreeVO();
            String deviceCode = deviceChannelEntry.getKey();
            deviceInfo.setCode(deviceCode);
            CasCadeDto deviceDataInfo = deviceInfoMap.get(deviceCode);
            if (deviceDataInfo != null) {
                deviceInfo.setName(deviceDataInfo.getDeviceName());
                deviceInfo.setParentCode(deviceDataInfo.getOrgCode());
            }
            deviceInfo.setOrgType(2);
            deviceBindList.add(deviceInfo);
            for (CasCadeDto channelData : deviceChannelEntry.getValue()) {
                CasCadeBindDeviceInfoTreeVO channelInfo = new CasCadeBindDeviceInfoTreeVO();
                channelInfo.setCode(channelData.getChannelCode());
                channelInfo.setName(channelData.getChannelName());
                channelInfo.setParentCode(deviceChannelEntry.getKey());
                channelInfo.setCustomCode(channelData.getCustomChannelCode());
                channelInfo.setOrgType(2);
                deviceBindList.add(channelInfo);
            }
        }
        return deviceBindList;
    }

    public List<CasCadeBindDeviceInfoTreeVO> buildOrgTree(List<CasCadeBindDeviceInfoTreeVO> cascadeOrgList) {
        List<CasCadeBindDeviceInfoTreeVO> treeList = new ArrayList<>();
        Map<String, CasCadeBindDeviceInfoTreeVO> nodeMap = new HashMap<>();
        // 第一遍：将所有节点放入map中
        for (CasCadeBindDeviceInfoTreeVO node : cascadeOrgList) {
            nodeMap.put(node.getCode(), node);
        }

        // 第二遍：建立父子关系
        for (CasCadeBindDeviceInfoTreeVO node : cascadeOrgList) {
            String parentCode = node.getParentCode();
            if (parentCode == null || parentCode.isEmpty()) {
                // 根节点
                treeList.add(node);
            } else {
                // 找到父节点并添加当前节点为其子节点
                CasCadeBindDeviceInfoTreeVO parent = nodeMap.get(parentCode);
                if (parent != null) {
                    if (parent.getList() == null) {
                        parent.setList(new ArrayList<>());
                    }
                    parent.getList().add(node);
                }
            }
        }
        return treeList;
    }

    private int sum(CasCadeBindDeviceInfoTreeVO data, String parentCode, int level) {
        int num = 0;
        if (data == null) {
            return num;
        }

        if (Objects.equals(data.getOrgType(), 1)) {
            if (CollectionUtils.isNotEmpty(data.getList())) {
                for (CasCadeBindDeviceInfoTreeVO subData : data.getList()) {
                    num += sum(subData,  data.getCode(), level + 1);
                }
            }
            data.setParentCode(parentCode);
            data.setLevel(level);
            data.setNum(num);
        }

        if (Objects.equals(data.getOrgType(), 2)) {
            num = 1;
            data.setParentCode(parentCode);
            data.setLevel(level);
            data.setNum(num);
        }
        return num;
    }


    private void checkCustomChannelCodeUnique(CasCadeBindPlatInfoVO param) {
        List<String> customChannelCodeList = getCustomChannelCodeList(param);
        List<String> distinctCustomChannelCodeListList = customChannelCodeList.stream().distinct().collect(Collectors.toList());

        if (customChannelCodeList.size() != distinctCustomChannelCodeListList.size()) {
            log.info("CasCadeSyncServiceImpl checkCustomChannelCodeUnique 自定义通道编号重复, 平台编码:{}," +
                            " 参数自定义通道编码数量:{}, 参数通道编码数量:{}",
                    param.getPlatCode(), customChannelCodeList.size(), distinctCustomChannelCodeListList.size());
            throw new CustomException("一个级联平台内的自定义通道编号不允许重复，请修改");
        }
    }

    private List<String> getCustomChannelCodeList(CasCadeBindPlatInfoVO platInfo) {
        List<String> customChannelCodeList = Lists.newArrayList();
        if (platInfo != null && CollectionUtils.isNotEmpty(platInfo.getDeviceInfoTreeList())) {
            for (CasCadeBindDeviceInfoTreeVO data : platInfo.getDeviceInfoTreeList()) {
                getAllCustomChannelCodeFromTree(data, customChannelCodeList);
            }
        }
        return customChannelCodeList;
    }

    private void getAllCustomChannelCodeFromTree(CasCadeBindDeviceInfoTreeVO data, List<String> customChannelCodeList) {
        if (data == null) {
            return;
        }

        // 如果是组织，遍历子节点
        if (Objects.equals(data.getOrgType(), 1)) {
            if (CollectionUtils.isNotEmpty(data.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cascadeSubTreeInfo : data.getList()) {
                    getAllCustomChannelCodeFromTree(cascadeSubTreeInfo, customChannelCodeList);
                }
            }
        }
        // 如果是设备，则添通道信息
        if (Objects.equals(data.getOrgType(), 2)) {
            if (CollectionUtils.isNotEmpty(data.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cascadeChannelInfo : data.getList()) {
                    if (StringUtils.isNoneBlank(cascadeChannelInfo.getCustomCode())) {
                        customChannelCodeList.add(cascadeChannelInfo.getCustomCode());
                    }
                }
            }
        }
    }

    private Result<String> handleOpenCascadeRelLogic(CasCadeBindPlatInfoVO cascadePlatInfo) {
        log.info("CasCadeSyncServiceImpl handleOpenCascadeRelLogic param:{}", JSON.toJSONString(cascadePlatInfo));
        // 校验同平台下是否有自定义通道相同的场景
        checkCustomChannelCodeUnique(cascadePlatInfo);
        Result<String> result = Result.success("更新成功");
        // 操作人
        String operator = cascadePlatInfo.getOperator();
        String operatorName = cascadePlatInfo.getOperatorName();
        // 当前时间
        Date modifyTime = cascadePlatInfo.getModifyTime();

        String platCode = cascadePlatInfo.getPlatCode();

        // 根据平台递维度归获取设备子列表
        List<CasCadeDeviceBindVO> cascadeDeviceList = getCascadeDeviceList(cascadePlatInfo, platCode,
                operator, modifyTime);
        // 根据平台递维度归获取组织子列表
        List<CasCadeOrgBindVO> cascadeOrgList = getCascadeOrgList(cascadePlatInfo, platCode,
                operator, modifyTime);
        // 同步基础平台
        Result<String> syncResult = syncPlatCodeInfo(platCode, cascadePlatInfo, cascadeDeviceList, cascadeOrgList);
        if (ResultCode.SUCCESS.getCode() == syncResult.getCode()) {
            // 删除平台关联关系，先删后插
            deleteCascadeRelInfoByPlatCode(platCode);
            // 新增关联表数据 sys_tenant_cascade_device_rel
            if (CollectionUtils.isNotEmpty(cascadeDeviceList)){
                casCadeService.insertCascadeDeviceRelList(cascadeDeviceList);
            }

            if (CollectionUtils.isNotEmpty(cascadeOrgList)) {
                casCadeService.insertCascadeOrgRelList(cascadeOrgList);
            }

            // 更新数据 sys_tenant_info
            casCadeService.updateCascadeBindingBatch(cascadePlatInfo.getTenantIdList(), cascadePlatInfo.getCascadeStatus(), operator, operatorName, modifyTime);

            casCadeService.deleteCascadePushRel(platCode);
            casCadeService.insertCascadePushRelBatch(cascadePlatInfo.getTenantIdList(), platCode, operator);
        } else {
            result = Result.fail(String.format("%s 级联平台信息处理失败", platCode));
        }
        return result;
    }

    private List<CasCadeDeviceBindVO> getCascadeDeviceList(CasCadeBindPlatInfoVO cascadePlatInfo, String platCode,
                                                           String operator, Date now) {
        List<CasCadeDeviceBindVO> cascadeDeviceList = Lists.newArrayList();
        if (cascadePlatInfo != null) {
            String permissionType = Joiner.on(",").join(cascadePlatInfo.getPermissionTypeList());

            for (CasCadeBindDeviceInfoTreeVO cascadeDeviceTree : cascadePlatInfo.getDeviceInfoTreeList()) {
                getAllDeviceInfoFromTree(cascadeDeviceTree, null, cascadeDeviceList);
            }
            log.info("getCascadeDeviceList cascadePlatInfo:{} cascadeDeviceList:{}", JSON.toJSONString(cascadePlatInfo), JSON.toJSONString(cascadeDeviceList));
            if (CollectionUtils.isNotEmpty(cascadeDeviceList)) {
                for (CasCadeDeviceBindVO bindVO : cascadeDeviceList) {
                    bindVO.setPlatCode(platCode);
                    bindVO.setTenantId(Joiner.on(",").join(cascadePlatInfo.getTenantIdList()));
                    bindVO.setPermissionType(permissionType);
                    bindVO.setCreator(operator);
                    bindVO.setCreateTime(now);
                }
            }
        }
        return cascadeDeviceList;
    }

    private List<CasCadeOrgBindVO> getCascadeOrgList(CasCadeBindPlatInfoVO cascadePlatInfo, String platCode,
                                                     String operator, Date now) {
        List<CasCadeOrgBindVO> cascadeOrgList = Lists.newArrayList();
        if (cascadePlatInfo != null) {
            for (CasCadeBindDeviceInfoTreeVO cascadeDeviceTree : cascadePlatInfo.getDeviceInfoTreeList()) {
                getAllOrgInfoFromTree(cascadeDeviceTree, null, cascadeOrgList);
            }
            if (CollectionUtils.isNotEmpty(cascadeOrgList)) {
                for (CasCadeOrgBindVO bindVO : cascadeOrgList) {
                    bindVO.setPlatCode(platCode);
                    bindVO.setTenantId(Joiner.on(",").join(cascadePlatInfo.getTenantIdList()));
                    bindVO.setCreator(operator);
                    bindVO.setCreateTime(now);
                }
            }
        }
        return cascadeOrgList;
    }

    private void deleteCascadeRelInfo(String tenantId, List<String> platCodeList) {
        casCadeService.deleteCascadeDeviceRelList(tenantId, platCodeList);
        casCadeService.deleteCascadeOrgRelList(tenantId, platCodeList);
    }

    private void deleteCascadeRelInfoByPlatCode(String platCode) {
        casCadeService.deleteCascadeDeviceRelListByPlatCode(platCode);
        casCadeService.deleteCascadeOrgRelListByPlatCode(platCode);
    }

    private void getAllDeviceInfoFromTree(CasCadeBindDeviceInfoTreeVO cascadeDeviceTreeInfo, String parentCode,
                                          List<CasCadeDeviceBindVO> cascadeDeviceList) {
        if (cascadeDeviceTreeInfo == null) {
            return;
        }

        // 如果是组织，遍历子节点
        if (Objects.equals(cascadeDeviceTreeInfo.getOrgType(), 1)) {
            if (CollectionUtils.isNotEmpty(cascadeDeviceTreeInfo.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cascadeSubTreeInfo : cascadeDeviceTreeInfo.getList()) {
                    getAllDeviceInfoFromTree(cascadeSubTreeInfo, cascadeDeviceTreeInfo.getCode(), cascadeDeviceList);
                }
            }
        }
        // 如果是设备，则添加设备信息
        if (Objects.equals(cascadeDeviceTreeInfo.getOrgType(), 2)) {
            if (CollectionUtils.isNotEmpty(cascadeDeviceTreeInfo.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cascadeChannelInfo : cascadeDeviceTreeInfo.getList()) {
                    CasCadeDeviceBindVO bindVO = new CasCadeDeviceBindVO();
                    bindVO.setDeviceCode(cascadeDeviceTreeInfo.getCode());
                    bindVO.setChannelCode(cascadeChannelInfo.getCode());
                    bindVO.setCustomChannelCode(cascadeChannelInfo.getCustomCode());
                    bindVO.setOrgCode(parentCode);
                    cascadeDeviceList.add(bindVO);
                }
            }
        }
    }

    private void getAllOrgInfoFromTree(CasCadeBindDeviceInfoTreeVO cascadeDeviceTreeInfo, String parentCode,
                                          List<CasCadeOrgBindVO> cascadeOrgList) {
        if (cascadeDeviceTreeInfo == null || Objects.equals(cascadeDeviceTreeInfo.getOrgType(), 2)) {
            return;
        }

        // 如果是组织，遍历子节点
        if (Objects.equals(cascadeDeviceTreeInfo.getOrgType(), 1)) {
            CasCadeOrgBindVO orgBindVO = new CasCadeOrgBindVO();
            orgBindVO.setOrgCode(cascadeDeviceTreeInfo.getCode());
            orgBindVO.setOrgName(cascadeDeviceTreeInfo.getName());
            orgBindVO.setParentOrgCode(parentCode);
            cascadeOrgList.add(orgBindVO);
            if (CollectionUtils.isNotEmpty(cascadeDeviceTreeInfo.getList())) {
                for (CasCadeBindDeviceInfoTreeVO cascadeSubTreeInfo : cascadeDeviceTreeInfo.getList()) {
                    // 递归下一层
                    getAllOrgInfoFromTree(cascadeSubTreeInfo, cascadeDeviceTreeInfo.getCode(), cascadeOrgList);
                }
            }
        }
    }

    private Result<String> handleCloseCascadeRelLogic(CasCadeBindPlatInfoVO cascadePlatInfo) {
        log.info("CasCadeSyncServiceImpl handleCloseCascadeRelLogic cascadePlatInfo:{}",
                JSON.toJSONString(cascadePlatInfo));
        Result<String> result = Result.success("更新成功");
        String platCode = cascadePlatInfo.getPlatCode();
        // 调用级联平台处理信息
        CasCadeBindPlatInfoVO platParam = new CasCadeBindPlatInfoVO();
        platParam.setCascadeStatus(cascadePlatInfo.getCascadeStatus());
        Result<String> syncResult = syncPlatCodeInfo(platCode, platParam, null, null);
        if (ResultCode.SUCCESS.getCode() == syncResult.getCode()) {
            deleteCascadeRelInfoByPlatCode(platCode);
            // 更新数据 sys_tenant_info
            casCadeService.updateCascadeBindingBatch(cascadePlatInfo.getTenantIdList(), cascadePlatInfo.getCascadeStatus(),
                    cascadePlatInfo.getOperator(), cascadePlatInfo.getOperatorName(), cascadePlatInfo.getModifyTime());
            // 删除客户级联平台关联信息
            casCadeService.deleteCascadePushRel(platCode);
        } else {
            result = Result.fail(String.format("%s 级联平台信息处理失败", platCode));
        }
        return result;
    }

    private Result<String> syncPlatCodeInfo(String platCode, CasCadeBindPlatInfoVO platParam,
                                            List<CasCadeDeviceBindVO> cascadeDeviceList,
                                            List<CasCadeOrgBindVO> cascadeOrgList) {
        Result<String> result = Result.success("更新成功");
        // 处理handelPlatCodeList数据，统一向基础平台发送
        if (StringUtils.isNotEmpty(platCode)) {
            if (DeviceConstants.ONE.equals(platParam.getCascadeStatus())) {
                CasCadeSyncReq request = new CasCadeSyncReq();
                request.setPermissions(platParam.getPermissionTypeList().toArray(new String[0]));
                request.setPlatAsId(platCode);
                request.setDeviceList(convert2SyncDeviceList(cascadeDeviceList));
                request.setOrgList(convert2SyncOrgList(cascadeOrgList));
                result = allocation(request);
            } else {
                //如果需要处理的平台编码数据库中都不存在了，所有处理的平台编码都进行停用操作
                CasCadeDeleteVO req = new CasCadeDeleteVO();
                req.setPlatAsIds(Lists.newArrayList(platCode));
                result = deleteCascade(req);
            }
        }
        return result;
    }

    private List<CasCadeSyncDeviceReq> convert2SyncDeviceList(List<CasCadeDeviceBindVO> cascadeDeviceList) {
        if (CollectionUtils.isEmpty(cascadeDeviceList)) {
            return Lists.newArrayList();
        }
        log.info("convert2SyncDeviceList cascadeDeviceList:{}", JSON.toJSONString(cascadeDeviceList));
        Map<String, List<CasCadeDeviceBindVO>> deviceMap = cascadeDeviceList.stream().collect(Collectors.groupingBy(CasCadeDeviceBindVO::getDeviceCode));
        List<CasCadeSyncDeviceReq> resultList = Lists.newArrayList();
        for (Map.Entry<String, List<CasCadeDeviceBindVO>> entry : deviceMap.entrySet()) {
            CasCadeSyncDeviceReq syncDeviceReq = new CasCadeSyncDeviceReq();
            syncDeviceReq.setDeviceCode(entry.getKey());
            syncDeviceReq.setChannelList(entry.getValue().stream().map(x -> {
                CasCadeSyncDeviceChannelReq channelReq = new CasCadeSyncDeviceChannelReq();
                channelReq.setChannelCode(x.getChannelCode());
                channelReq.setChannelAlia(x.getCustomChannelCode());
                channelReq.setOrgCode(x.getOrgCode());
                return channelReq;
            }).collect(Collectors.toList()));
            resultList.add(syncDeviceReq);
        }

        return resultList;
    }

    private List<CasCadeSyncOrgDTO> convert2SyncOrgList(List<CasCadeOrgBindVO> cascadeOrgList) {
        if (CollectionUtils.isEmpty(cascadeOrgList)) {
            return Lists.newArrayList();
        }
        return cascadeOrgList.stream().map(x -> {
            CasCadeSyncOrgDTO syncOrgInfo = new CasCadeSyncOrgDTO();
            syncOrgInfo.setOrgCode(x.getOrgCode());
            syncOrgInfo.setOrgName(x.getOrgName());
            syncOrgInfo.setParentOrgCode(x.getParentOrgCode());
            return syncOrgInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SysImportDetail> importData(ImportParams<CascadeBindingImportVO> params, String platCode, String tenantIds) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        Stopwatch sw = Stopwatch.createStarted();
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        List<CascadeBindingImportVO> successList = Lists.newArrayList();

        List<String> tenantIdList = Arrays.asList(tenantIds.split(","));

        DeviceReqDTO orderParam = new DeviceReqDTO();
        orderParam.setTenantIdList(tenantIdList);
        Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
        String platformCode = platformCodeResult.getData();
        List<DeviceTreeVO> orderDeviceList = iotDeviceRepository.getDeviceChannelList(orderParam, platformCode);

        log.info("traceId:{} CasCadeSyncServiceImpl importDataV2 step1, sw:{}", traceId, sw.elapsed(TimeUnit.MILLISECONDS));

        Map<String, String> provinceMap = orderDeviceList.stream().collect(Collectors.toMap(DeviceTreeVO::getProvinceName, DeviceTreeVO::getProvinceId, (a, b) -> a));
        Map<String, String> cityMap = orderDeviceList.stream().collect(Collectors.toMap(DeviceTreeVO::getCityName, DeviceTreeVO::getCityId, (a, b) -> a));
        Map<String, String> countyMap = orderDeviceList.stream().collect(Collectors.toMap(DeviceTreeVO::getCountyName, DeviceTreeVO::getCountyId, (a, b) -> a));

        Map<String, String> deviceMap = orderDeviceList.stream().filter(x -> StringUtils.isNotEmpty(x.getDeviceCode())).collect(Collectors.toMap(DeviceTreeVO::getDeviceCode, DeviceTreeVO::getDeviceName, (a, b) -> a));
        Map<String, String> channelMap = orderDeviceList.stream().filter(x -> StringUtils.isNotEmpty(x.getChannelCode())).collect(Collectors.toMap(DeviceTreeVO::getChannelCode, DeviceTreeVO::getChannelName, (a, b) -> a));

        List<String> orderDeviceCodeList = orderDeviceList.stream().map(DeviceTreeVO::getDeviceCode).distinct().collect(Collectors.toList());

        log.info("traceId:{} CasCadeSyncServiceImpl importDataV2 step2, sw:{}", traceId, sw.elapsed(TimeUnit.MILLISECONDS));
        for (CascadeBindingImportVO importVO : params.getList()) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(), rowNum);
            try {
                //校验 1空值
                String errorStr = checkImportData(importVO, orderDeviceCodeList, provinceMap.keySet(), cityMap.keySet(), countyMap.keySet());
                if (StringUtils.isNotEmpty(errorStr)) {
                    detail.setFailReason(errorStr);
                    continue;
                }
                importVO.setDeviceName(deviceMap.get(importVO.getDeviceCode()));
                importVO.setProvinceCode(provinceMap.get(importVO.getProvinceName()));
                importVO.setCityCode(cityMap.get(importVO.getCityName()));
                importVO.setCountyCode(countyMap.get(importVO.getCountyName()));
                successList.add(importVO);
                detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);
            } catch (Exception e) {
                detail.setFailReason("处理异常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                rowNum++;
                details.add(detail);
            }
        }
        log.info("traceId:{} CasCadeSyncServiceImpl importDataV2 step3, sw:{}", traceId, sw.elapsed(TimeUnit.MILLISECONDS));
        try {
            // 处理successList为查询的结果信息
            String redisKey = getCascadeImportKey(params.getImportId());
            CasCadeBindPlatInfoVO casCadeBindVO = assembleCasCadeBindVO(successList, platCode, channelMap);
            log.info("CasCadeSyncServiceImpl importDataV2 casCadeBindVO:{}", JSON.toJSONString(casCadeBindVO));
            RedisUtil.set(redisKey, casCadeBindVO, 5, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("CasCadeSyncServiceImpl importDataV2 error:", e);
        }
        log.info("traceId:{} CasCadeSyncServiceImpl importDataV2 step4, sw:{}", traceId, sw.elapsed(TimeUnit.MILLISECONDS));
        return details;
    }

    @Override
    public Result<CasCadeBindPlatInfoVO> queryCascadeBindingImportList(String importId) {
        String redisKey = getCascadeImportKey(importId);
        return Result.data(RedisUtil.get(redisKey, CasCadeBindPlatInfoVO.class, RedisDBType.PRIMARY));
    }

    private String getCascadeImportKey(String importId) {
        return String.format("cascade_import_%s", importId);
    }

    private String checkImportData(CascadeBindingImportVO deviceBatchAddVO,
                                   List<String> orderDeviceCodeList, Set<String> provinceNameSet,
                                   Set<String> cityNameSet, Set<String> countyNameSet) {
        StringBuilder errorStr = new StringBuilder();
//        if (StringUtils.isEmpty(deviceBatchAddVO.getPlatformName())) {
//            errorStr.append("级联平台称为空,");
//        } else if (!platformNameList.contains(deviceBatchAddVO.getPlatformName())) {
//            errorStr.append("级联平台不存在,");
//        }
        if (StringUtils.isEmpty(deviceBatchAddVO.getProvinceName())) {
            errorStr.append("省份为空,");
        } else if (!provinceNameSet.contains(deviceBatchAddVO.getProvinceName())){
            errorStr.append("省份不在订购范围内,");
        }
        if (StringUtils.isEmpty(deviceBatchAddVO.getCityName())) {
            errorStr.append("地市为空,");
        } else if (!cityNameSet.contains(deviceBatchAddVO.getCityName())){
            errorStr.append("地市不在订购范围内,");
        }
        if (StringUtils.isEmpty(deviceBatchAddVO.getCountyName())) {
            errorStr.append("区县为空,");
        } else if (!countyNameSet.contains(deviceBatchAddVO.getCountyName())){
            errorStr.append("区县不在订购范围内,");
        }

        if (StringUtils.isNotEmpty(deviceBatchAddVO.getDeviceCode()) && !orderDeviceCodeList.contains(deviceBatchAddVO.getDeviceCode())) {
            errorStr.append("设备不在订购范围内,");
        }

        return errorStr.toString();
    }

    private CasCadeBindPlatInfoVO assembleCasCadeBindVO(List<CascadeBindingImportVO> successList, String platCode, Map<String, String> channelMap) {
        CasCadeBindPlatInfoVO platInfoVO = new CasCadeBindPlatInfoVO();
        platInfoVO.setPlatCode(platCode);
        platInfoVO.setCascadeStatus("1");
        if (CollectionUtils.isNotEmpty(successList)) {
            List<String> permissionTypeList = Lists.newArrayList("3");
            Optional<CascadeBindingImportVO> ptzPermissionOptional = successList.stream().filter(x -> StringUtils.equals(x.getPtzPermission(), "是")).findAny();
            if (ptzPermissionOptional.isPresent()) {
                permissionTypeList.add("2");
            }
            Optional<CascadeBindingImportVO> playPermissionOptional = successList.stream().filter(x -> StringUtils.equals(x.getPlayPermission(), "是")).findAny();
            if (playPermissionOptional.isPresent()) {
                permissionTypeList.add("1");
            }

            List<CasCadeBindDeviceInfoTreeVO> treeList = convert2CasCadeBindDeviceInfoTreeVOList(successList, true, channelMap);
            if (!treeList.isEmpty()) {
                List<CasCadeBindDeviceInfoTreeVO> rootTreeList = new ArrayList<>(1);
                // 补充中国根节点
                CasCadeBindDeviceInfoTreeVO root = new CasCadeBindDeviceInfoTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOrgType(1);
                root.setList(treeList);
                root.setNum(sum(root, null, 0));
                rootTreeList.add(root);
                platInfoVO.setDeviceInfoTreeList(rootTreeList);
            }
            platInfoVO.setPermissionTypeList(permissionTypeList);
        }

        return platInfoVO;
    }

    private void addToResultListIfNotExists(String code, String name, String parentCode, List<CasCadeBindDeviceInfoTreeVO> resultList, Set<String> existsOrgCodeSet) {
        if (!existsOrgCodeSet.contains(code)) {
            resultList.add(assembleCasCadeBindDeviceInfoTreeVO(code, name, 1, parentCode));
            existsOrgCodeSet.add(code);
        }
    }

    private List<CasCadeBindDeviceInfoTreeVO> convert2CasCadeBindDeviceInfoTreeVOList(List<CascadeBindingImportVO> platDeviceList,
                                                                                      boolean needDevice, Map<String, String> channelMap) {
        List<CasCadeBindDeviceInfoTreeVO> resultList = Lists.newArrayList();
        Set<String> existsOrgCodeSet = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(platDeviceList)) {
            for (CascadeBindingImportVO importVO : platDeviceList) {
                addToResultListIfNotExists(importVO.getProvinceCode(), importVO.getProvinceName(), "", resultList, existsOrgCodeSet);
                addToResultListIfNotExists(importVO.getCityCode(), importVO.getCityName(), importVO.getProvinceCode(), resultList, existsOrgCodeSet);
                addToResultListIfNotExists(importVO.getCountyCode(), importVO.getCountyName(), importVO.getCityCode(), resultList, existsOrgCodeSet);

                String currentParentCode = importVO.getCountyCode();
                if (StringUtils.isNotEmpty(importVO.getBusinessOrgCode())) {
                    currentParentCode = importVO.getBusinessOrgCode();
                    addToResultListIfNotExists(importVO.getBusinessOrgCode(), importVO.getBusinessOrgName(), importVO.getCountyCode(), resultList, existsOrgCodeSet);
                }

                String[] virtualOrgCodes = {
                        importVO.getVirtualOrgLevel1Code(),
                        importVO.getVirtualOrgLevel2Code(),
                        importVO.getVirtualOrgLevel3Code(),
                        importVO.getVirtualOrgLevel4Code(),
                        importVO.getVirtualOrgLevel5Code(),
                        importVO.getVirtualOrgLevel6Code()
                };

                String[] virtualOrgNames = {
                        importVO.getVirtualOrgLevel1Name(),
                        importVO.getVirtualOrgLevel2Name(),
                        importVO.getVirtualOrgLevel3Name(),
                        importVO.getVirtualOrgLevel4Name(),
                        importVO.getVirtualOrgLevel5Name(),
                        importVO.getVirtualOrgLevel6Name()
                };

                for (int i = 0; i < virtualOrgCodes.length; i++) {
                    if (StringUtils.isNotEmpty(virtualOrgCodes[i])) {
                        addToResultListIfNotExists(virtualOrgCodes[i], virtualOrgNames[i], currentParentCode, resultList, existsOrgCodeSet);
                        currentParentCode = virtualOrgCodes[i];
                    } else {
                        break;
                    }
                }
                fillDeviceAndChannelInfo(importVO, currentParentCode, resultList, needDevice, existsOrgCodeSet, channelMap);
            }

            log.info("convert2CasCadeBindDeviceInfoTreeVOList platDeviceList:{} resultList:{}", JSON.toJSONString(platDeviceList), JSON.toJSONString(resultList));

            Map<String, CasCadeBindDeviceInfoTreeVO> nodeMap = resultList.stream()
                    .collect(Collectors.toMap(CasCadeBindDeviceInfoTreeVO::getCode, node -> node, (a, b) -> a));

            resultList = resultList.stream()
                    .filter(node -> {
                        CasCadeBindDeviceInfoTreeVO parent = nodeMap.get(node.getParentCode());
                        if (parent != null) {
                            if (parent.getList() == null) {
                                parent.setList(new ArrayList<>());
                            }
                            parent.getList().add(node);
                            return false;
                        }
                        return true; // 根节点
                    }).collect(Collectors.toList());
        }
        return resultList;
    }

    private CasCadeBindDeviceInfoTreeVO assembleCasCadeBindDeviceInfoTreeVO(String code, String name, int orgType, String parentCode) {
        CasCadeBindDeviceInfoTreeVO data = new CasCadeBindDeviceInfoTreeVO();
        data.setCode(code);
        data.setName(name);
        data.setOrgType(orgType);
        data.setParentCode(parentCode);
        return data;
    }

    private void fillDeviceAndChannelInfo(CascadeBindingImportVO importVO, String parentCode,
                                          List<CasCadeBindDeviceInfoTreeVO> resultList, boolean needDevice,
                                          Set<String> existsOrgCodeSet, Map<String, String> channelMap) {
        if (needDevice && StringUtils.isNotEmpty(importVO.getDeviceCode()) && !existsOrgCodeSet.contains(importVO.getDeviceCode())) {
            // 增加设备层级
            resultList.add(assembleCasCadeBindDeviceInfoTreeVO(importVO.getDeviceCode(), importVO.getDeviceName(), 2, parentCode));
            existsOrgCodeSet.add(importVO.getDeviceCode());
            if (StringUtils.isNotEmpty(importVO.getChannelCodes())) {
                String[] channelArr = importVO.getChannelCodes().split(",");
                String[] customChannelArr = importVO.getCustomChannelCodes().split(",");
                for (int i = 0; i < channelArr.length; i++) {
                    String channelCode = channelArr[i];
                    CasCadeBindDeviceInfoTreeVO channelData = assembleCasCadeBindDeviceInfoTreeVO(channelCode, channelMap.get(channelCode), 2, importVO.getDeviceCode());
                    if (customChannelArr.length > i) {
                        channelData.setCustomCode(customChannelArr[i]);
                    } else {
                        channelData.setCustomCode("");
                    }
                    resultList.add(channelData);
                }
            }
        }
    }
}
