package com.chinatower.order.handler.deal;

import com.chinatower.common.core.domain.OrderServiceRelDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.core.domain.OrderServiceRelHisDO;
import com.chinatower.order.repository.IOrderServiceRelHisRepository;
import com.chinatower.order.repository.IOrderServiceRelRepository;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysAreaVO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChangeOrderServiceRelSetDeal extends AbstractOrderDeal {
    @Autowired
    private IOrderServiceRelRepository orderServiceRelRepository;
    @Autowired
    private IOrderServiceRelHisRepository orderServiceRelHisRepository;
    @Autowired
    private IOrderOrganDeviceRelService orderOrganDeviceRelService;
    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("变更订单监控服务（卫星）配置关系处理模块");
        String orderCode = orderGateway.getOrderCode();
        List<String> orderCodeList = Lists.newArrayList();
        orderCodeList.add(orderCode);
        //查询最新订购的监控服务信息
        List<OrderServiceRelDO> orderServiceRelDOList = orderServiceRelRepository.getOrderServiceRelByList(orderCodeList, null);
        if (!CollectionUtils.isEmpty(orderServiceRelDOList)) {
            //获取迁移次数
            int moveSign = orderServiceRelDOList.get(0).getMoveSign() - 1;
            List<OrderServiceRelHisDO> oldServiceRelList = orderServiceRelHisRepository.getOldOrderServiceRelHisByOrderCode(orderCode, moveSign);

            List<String> orderServiceKeyList = orderServiceRelDOList.stream()
                    .map(rel -> (rel.getServiceCode() + "_" + rel.getCountyCode() + "_" + rel.getIndustryCode())).collect(Collectors.toList());
            //需要退订的监控服务信息
            List<OrderServiceRelHisDO> reduceDevList = oldServiceRelList.stream().filter(
                    item -> !orderServiceKeyList.contains(item.getServiceCode() + "_" + item.getCountyCode() + "_" + item.getIndustryCode())).collect(Collectors.toList());
            //减少的监控服务信息处理
            handleOrgStreetRela(reduceDevList);
            //新增的监控服务信息处理
            addServiceRelDeal(oldServiceRelList, orderGateway);
        } else {
            //获取迁移次数
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            List<OrderServiceRelHisDO> reduceDevList = orderServiceRelHisRepository.getOldOrderServiceRelHisByOrderCode(orderCode, moveSign);
            if (!CollectionUtils.isEmpty(reduceDevList)) {
                //减少的监控服务信息处理
                handleOrgStreetRela(reduceDevList);
            }
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 功能描述 批量删除区域组织关系表
     *
     * @return
     * @author chenmf
     * @date 2022/1/11
     * [reduceDevList]
     */
    private void handleOrgStreetRela(List<OrderServiceRelHisDO> reduceDevList) {
        reduceDevList = reduceDevList.stream().filter(reduceDev ->
                !StringUtils.isEmpty(reduceDev.getCountyCode()) && OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE.equals(reduceDev.getServiceCode())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(reduceDevList)) {
            // 卫星服务map key -> 行业编码 values -> 区域编码列表
            Map<String, Set<String>> industryServiceMap = Maps.newHashMap();
            //批量删除区域组织关系表
            Map<String, List<OrderServiceRelHisDO>> groupMap = reduceDevList.stream()
                    .collect(Collectors.groupingBy(OrderServiceRelHisDO::getIndustryCode));

            for (Map.Entry<String, List<OrderServiceRelHisDO>> entry : groupMap.entrySet()) {
                String industryCode = entry.getKey();
                Set<String> areaCodeSet = Sets.newHashSet();
                List<String> areaCodesStrList = entry.getValue().stream().map(OrderServiceRelHisDO::getCountyCode).distinct().collect(Collectors.toList());
                areaCodesStrList.forEach(areaCodes -> Collections.addAll(areaCodeSet, areaCodes.split(",")));
                if (!CollectionUtils.isEmpty(areaCodeSet)) {
                    if (industryServiceMap.containsKey(industryCode)) {
                        industryServiceMap.get(industryCode).addAll(areaCodeSet);
                    } else {
                        industryServiceMap.put(industryCode, areaCodeSet);
                    }
                }
            }

            // 处理卫星服务需要删除的服务组织关联信息
            if (industryServiceMap.size() > 0) {
                String tenantId = reduceDevList.get(0).getTenantId();
                List<String> areaCodeList = industryServiceMap.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toList());
                List<OrganStreetRelVO> organStreetRelList = orderOrganDeviceRelService.selectOrganStreetInfoByCondition(tenantId, OrderConstants.CATEGORY_SATELLITE_CODE,
                        Lists.newArrayList(industryServiceMap.keySet()), areaCodeList);
                if (CollectionUtils.isNotEmpty(organStreetRelList)) {
                    List<Long> ids = organStreetRelList.stream()
                            .filter(x -> industryServiceMap.containsKey(x.getIndustryCode()) && industryServiceMap.get(x.getIndustryCode()).contains(x.getCountyCode()))
                            .map(OrganStreetRelVO::getId).distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        orderOrganDeviceRelService.batchDelStreetRelaByIds(ids);
                    }
                }
            }
        }
    }

    private Map<String, String> getRootDeptMap(String customerCode, List<String> industryCodeList) {
        List<SysDept> rootDeptInfoList = orderOrganDeviceRelService.selectTenantRootDeptId(customerCode, industryCodeList);
        Map<String, String> resultMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(rootDeptInfoList)) {
            resultMap = rootDeptInfoList.stream().collect(Collectors.toMap(SysDept::getIndustryCode, SysDept::getDeptId, (a,b) -> a));
        }
        return resultMap;
    }

    /**
     * 新增的监控服务信息处理
     *
     * @return
     * @author chenmf
     * @date 2022/2/14
     * @version
     */
    private void addServiceRelDeal(List<OrderServiceRelHisDO> oldOrderDeviceList, OrderGatewayDTO orderGateway) {

        String customerCode = orderGateway.getCustomer().getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();
        if (!CollectionUtils.isEmpty(packageList)) {
            List<OrganStreetRelVO> orgList = new ArrayList<>();
            List<String> handleOrgList = Lists.newArrayList();
            List<String> oldServiceKeyList = oldOrderDeviceList.stream().map(
                    rel -> (rel.getServiceCode() + "_" + rel.getCountyCode() + "_" + rel.getIndustryCode())).collect(Collectors.toList());

            // 行业编码列表
            List<String> industryCodeList = packageList.stream().map(x -> x.getApplication().getAppCode()).distinct().collect(Collectors.toList());
            // 行业根节点组织map key -> 行业编码 value -> 根节点组织编码
            Map<String, String> industryDeptMap = getRootDeptMap(customerCode, industryCodeList);

            // 卫星服务区县编码列表
            Set<String> areaCodeSet = Sets.newHashSet();
            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Service> serviceList = orderPackages.getServices();
                String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
                if (!CollectionUtils.isEmpty(serviceList) && StringUtils.isNotEmpty(deptId)) {
                    for (OrderGatewayDTO.Service service : serviceList) {
                        if (StringUtils.isEmpty(service.getAreaCodes())
                                || !Objects.equals(OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE, service.getServiceCode())) {
                            continue;
                        }
                        String[] areaCodeArr = service.getAreaCodes().split(",");
                        if (areaCodeArr.length > 0) {
                            areaCodeSet.addAll(Arrays.asList(areaCodeArr));
                        }
                    }
                }
            }

            // 行政区域map key -> 区县编码 value -> 行政区域信息
            Map<String, SysAreaVO> regionMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(areaCodeSet)) {
                String areaCodes = Joiner.on(",").join(areaCodeSet);
                Result<List<SysAreaVO>> listResult = systemFeignClient.getAreaStreet(areaCodes);
                if (listResult != null && CollectionUtils.isNotEmpty(listResult.getData())) {
                    regionMap = listResult.getData().stream().collect(Collectors.toMap(SysAreaVO::getRegionCode, (p) -> p, (a, b) -> a));
                }
            }

            for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
                List<OrderGatewayDTO.Service> devList = orderPackages.getServices();
                String industryCode = orderPackages.getApplication().getAppCode();
                //交集
                List<OrderGatewayDTO.Service> intersectDev = devList.stream().filter(item ->
                        oldServiceKeyList.contains(item.getServiceCode() + "_" + item.getAreaCodes() + "_" + industryCode)).collect(Collectors.toList());

                //需要新增的监控服务信息
                List<OrderGatewayDTO.Service> addDevList = devList.stream().filter(
                        item -> !intersectDev.stream().map(service -> (service.getServiceCode() + service.getAreaCodes())).collect(Collectors.toList()).
                                contains(item.getServiceCode() + item.getAreaCodes())).collect(Collectors.toList());

                //获取租户部门列表(顶级结构)
                String deptId = industryDeptMap.get(orderPackages.getApplication().getAppCode());
                if (!CollectionUtils.isEmpty(addDevList) && !StringUtils.isEmpty(deptId)) {
                    for (OrderGatewayDTO.Service service : addDevList) {
                        if (StringUtils.isEmpty(service.getAreaCodes())
                                || !OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE.equals(service.getServiceCode())) {
                            continue;
                        }
                        for (String areaCode : service.getAreaCodes().split(",")) {
                            Optional<OrderServiceRelHisDO> optional = oldOrderDeviceList.stream().filter(oldDev ->
                                    OrderConstants.CATEGORY_SATELLITE_SERVICE_CODE.equals(oldDev.getServiceCode())
                                            && areaCode.equals(oldDev.getCountyCode())).findAny();
                            if (optional.isPresent()) {
                                continue;
                            }
                            String assembleKey = String.format("%s_%s", industryCode, areaCode);
                            if (handleOrgList.contains(assembleKey)) {
                                continue;
                            }
                            //区域调君南接口 areaCode
                            try {
                                handleOrgList.add(assembleKey);
//                                Result<List<SysAreaVO>> listResult = systemFeignClient.getAreaStreet(areaCode);
//                                List<SysAreaVO> sysAreaVOList = listResult.getData();
                                SysAreaVO sysAreaVO = regionMap.get(areaCode);
                                if (sysAreaVO != null) {
                                    for (String streetCode : sysAreaVO.getStreetCodes()) {
                                        OrganStreetRelVO organStreetRelVO = new OrganStreetRelVO();
                                        organStreetRelVO.setCreateTime(DateUtils.getNowDate());
                                        organStreetRelVO.setDeleteFlag(OrderConstants.DELETE_FLAG_NORMAL_CHAR);
                                        organStreetRelVO.setUpdateTime(DateUtils.getNowDate());
                                        organStreetRelVO.setCategoryCode(OrderConstants.CATEGORY_SATELLITE_CODE);
                                        organStreetRelVO.setDeptId(deptId);
                                        organStreetRelVO.setTenantId(customerCode);
                                        organStreetRelVO.setProvinceCode(sysAreaVO.getProvinceCode());
                                        organStreetRelVO.setCityCode(sysAreaVO.getCityCode());
                                        organStreetRelVO.setCountyCode(areaCode);
                                        organStreetRelVO.setStreetCode(streetCode);
                                        organStreetRelVO.setIndustryCode(industryCode);
                                        organStreetRelVO.setAppVerCode(orderPackages.getApplication().getProductCode());
                                        orgList.add(organStreetRelVO);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("根据区县获取省市区县街道失败", e);
                            }
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(orgList)) {
                orderOrganDeviceRelService.batchSaveStreetRela(orgList);
            }
        }
    }
}
