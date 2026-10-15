package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderDeviceDO;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.order.mapper.OrderDeviceMapper;
import com.chinatower.order.repository.IOrderDeviceRepository;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.order.FindDeviceCodeVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.DeviceTreeVO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OrderDeviceRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderDeviceRepositoryImpl extends ServiceImpl<OrderDeviceMapper, OrderDeviceDO> implements IOrderDeviceRepository {

    @Autowired
    private OrderDeviceMapper orderDeviceMapper;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private SysAreaRootService sysAreaRootService;

    /**
     * 功能描述 根据订单编码查询订购设备信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     */
    @Override
    public List<OrderDeviceDO> getOrderDeviceByOrderCode(String orderCode, String packageCode) {

        QueryWrapper<OrderDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderConstants.FIELD_ORDER_CODE, orderCode);
        if (StringUtils.isNotEmpty(packageCode)) {
            queryWrapper.eq(OrderConstants.FIELD_PACKAGE_CODE, packageCode);
        }
        queryWrapper.eq(OrderConstants.FIELD_DELETE_FLAG, OrderConstants.DELETE_FLAG_NORMAL);
        return orderDeviceMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述 批量保存
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderDeviceDOList]
     */
    @Override
    public void saveListBatch(List<OrderDeviceDO> orderDeviceDOList) {
        this.saveBatch(orderDeviceDOList, 5000);
    }

    @Override
    public int batchInsert(List<OrderDeviceDO> orderDeviceDOList) {
        return orderDeviceMapper.batchInsert(orderDeviceDOList);
    }

    /**
     * 功能描述 删除订单数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode]
     */
    @Override
    public void deleteByOrderCode(String orderCode) {
        QueryWrapper<OrderDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderConstants.FIELD_ORDER_CODE, orderCode);
        orderDeviceMapper.delete(queryWrapper);
    }

    /**
     * 功能描述 获取订单编码
     *
     * @return
     * @author chenmf
     * @date 2022/1/19
     * [tenantId, deviceCode, channelCode]
     */
    @Override
    public VideoOrderResp getOrderCode(String tenantId, String deviceCode, String channelCode, String industryCode) {

        List<VideoOrderResp> list = orderDeviceMapper.getOrderCode(tenantId, deviceCode, channelCode, industryCode);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询客户下有效的设备id
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @Override
    public List<String> getValidDev(String customerCode) {

        return orderDeviceMapper.getValidDev(customerCode);
    }

    /**
     * 查询客户下有效的设备通道id列表
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @Override
    public List<String> getValidChn(String customerCode) {
        return orderDeviceMapper.getValidChn(customerCode);
    }

    /**
     * 查询对应订单设备信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/3
     * @version
     */
    @Override
    public List<OrderDevicesVO> getDeviceInfoByList(List<String> orderCodeList, List<String> packageCodeList) {
        return orderDeviceMapper.getDeviceInfoByList(orderCodeList, packageCodeList);
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
        return orderDeviceMapper.findDeviceCodeByUser(findDeviceCodeVO);
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderDeviceMapper.cancelByOrderCode(orderCode);
    }

    @Override
    public List<OrderDevicesVO> getByTenantIdAndIndustryCode(String tenantId, String industryCode) {
        return orderDeviceMapper.getByTenantIdAndIndustryCode(tenantId, industryCode);
    }

    /**
     * 行业+版本为key
     *
     * @param orderCode
     * @return
     */
    @Override
    public Map<String, List<DeviceTreeVO>> getTenantDeviceMap(String orderCode) {
        List<DeviceTreeVO> tenantDeviceList = orderDeviceMapper.getTenantDeviceList(orderCode);
        Map<String, List<DeviceTreeVO>> deviceTreeVOMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(tenantDeviceList)) {
            Map<String, List<DeviceTreeVO>> collect = tenantDeviceList.stream().collect(Collectors.groupingBy(x -> x.getIndustryCode() + ":" + x.getAppVersion()));
            collect.forEach((x, y) -> {
                List<DeviceTreeVO> list = Lists.newArrayList();
                assembleDeviceTree(y, list);
                deviceTreeVOMap.put(x, list);
            });
        }
        return deviceTreeVOMap;
    }

    @Override
    public void assembleDeviceTree(List<DeviceTreeVO> sourceTreeVOList, List<DeviceTreeVO> targetTreeList) {
        if (!CollectionUtils.isEmpty(sourceTreeVOList)) {
            //获取行政区划全部数据
            Result<Map<String, String>> mapResult = systemFeignClient.getAreaNameCaffeineMap(new ArrayList<>());
            Map<String, String> areaMap = mapResult.getData();
            //转化成树形结构 treeList
            List<DeviceTreeVO> tempTreeList = getTreeList(sourceTreeVOList, areaMap);
            targetTreeList.addAll(appendTreeRootNode(tempTreeList));
        }
    }

    @Override
    public List<OrderDevicesVO> getDeviceInfoByOrderCodeAndPackageCode(QueryOrderRelationshipVO queryOrderRelationshipVO) {
        return orderDeviceMapper.getDeviceInfoByOrderCodeAndPackageCode(queryOrderRelationshipVO);
    }

    @Override
    public List<OrderDevicesVO> getDeviceInfoByDeviceCodes(List<String> deviceCodes) {
        return orderDeviceMapper.getDeviceInfoByDeviceCodes(deviceCodes);
    }

    /**
     * 功能描述 删除订单数（新事务）
     *
     */
    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteByOrderCodeNewTransactional(String orderCode) {
        QueryWrapper<OrderDeviceDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderConstants.FIELD_ORDER_CODE, orderCode);
        orderDeviceMapper.delete(queryWrapper);
    }


    private List<DeviceTreeVO> getTreeList(List<DeviceTreeVO> flagList, Map<String, String> areaMap) {
        List<DeviceTreeVO> treeList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        flagList.forEach(vo -> {
            String province = vo.getProvinceId();
            Integer provinceIndex = map.get(province);
            // provinceIndex 不是 null，说明已在list里
            if (ObjectUtils.isEmpty(provinceIndex)) {
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(province);
                tree.setLevel(Constants.ONE);
                tree.setProvinceId(province);
                tree.setList(new ArrayList<>());
                tree.setUniqueCode(province);
                if (MapUtils.isNotEmpty(areaMap) && areaMap.get(province) != null) {
                    tree.setName(NullProcessUtil.nvlToString(areaMap.get(province), ""));
                }
                treeList.add(tree);
                provinceIndex = treeList.size() - Constants.ONE_NUM;
                map.put(province, provinceIndex);
            }

            String city = vo.getCityId();
            String cityKey = province + Constants.UNDER_LINE + city;
            Integer cityIndex = map.get(cityKey);
            if (ObjectUtils.isEmpty(cityIndex)) {
                List<DeviceTreeVO> list = treeList.get(provinceIndex).getList();
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(city);
                tree.setLevel(Constants.TWO);
                tree.setParentCode(vo.getProvinceId());
                tree.setProvinceId(province);
                tree.setCityId(city);
                tree.setList(new ArrayList<>());
                tree.setUniqueCode(cityKey);
                if (MapUtils.isNotEmpty(areaMap) && areaMap.get(city) != null) {
                    tree.setName(NullProcessUtil.nvlToString(areaMap.get(city), ""));
                }
                list.add(tree);
                cityIndex = list.size() - Constants.ONE_NUM;
                map.put(cityKey, cityIndex);
            }

            String county = vo.getCountyId();
            String countryKey =cityKey + Constants.UNDER_LINE + county;
            Integer countyIndex = map.get(countryKey);
            if (ObjectUtils.isEmpty(countyIndex)) {
                List<DeviceTreeVO> list = treeList.get(provinceIndex).getList().get(cityIndex).getList();
                DeviceTreeVO tree = new DeviceTreeVO();
                tree.setCode(county);
                tree.setLevel(Constants.THREE);
                tree.setParentCode(vo.getCityId());
                tree.setProvinceId(province);
                tree.setCityId(city);
                tree.setCountyId(county);
                tree.setList(new ArrayList<>());
                tree.setUniqueCode(countryKey);
                if (MapUtils.isNotEmpty(areaMap) && areaMap.get(county) != null) {
                    tree.setName(NullProcessUtil.nvlToString(areaMap.get(county), ""));
                }
                list.add(tree);
                countyIndex = list.size() - Constants.ONE_NUM;
                map.put(countryKey, countyIndex);
            }
            Integer townCodeIndex = null;

            if (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getTownCode())) {
                String townCode = vo.getTownCode();
                String townCodeKey = countryKey + Constants.UNDER_LINE + townCode;
                townCodeIndex = map.get(townCodeKey);
                if (ObjectUtils.isEmpty(townCodeIndex)) {
                    List<DeviceTreeVO> list = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex).getList();
                    DeviceTreeVO tree = new DeviceTreeVO();
                    tree.setCode(townCode);
                    tree.setLevel(Constants.FOUR);
                    tree.setParentCode(vo.getCountyId());
                    tree.setProvinceId(province);
                    tree.setCityId(city);
                    tree.setCountyId(county);
                    tree.setTownCode(townCode);
                    tree.setList(new ArrayList<>());
                    tree.setUniqueCode(townCode);
                    if (MapUtils.isNotEmpty(areaMap) && areaMap.get(townCode) != null) {
                        tree.setName(NullProcessUtil.nvlToString(areaMap.get(townCode), ""));
                    }
                    list.add(tree);
                    townCodeIndex = list.size() - Constants.ONE_NUM;
                    map.put(townCodeKey, townCodeIndex);
                }
            }
            //乡镇编码是空的
            DeviceTreeVO deviceTreeVO;
            String deviceLevel = Constants.FOUR;
            if (ObjectUtils.isEmpty(townCodeIndex)) {
                //挂在区下
                deviceTreeVO = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex);
            } else {
                deviceLevel = Constants.FIVE;
                //挂在乡下
                deviceTreeVO = treeList.get(provinceIndex).getList().get(cityIndex).getList().get(countyIndex).getList().get(townCodeIndex);
            }
            if (deviceTreeVO != null) {
                DeviceTreeVO deviceVO = new DeviceTreeVO();
                String deviceCode = vo.getDeviceCode();
                deviceVO.setDeviceCode(deviceCode);
                deviceVO.setDeviceName(vo.getDeviceName());
                deviceVO.setName(vo.getDeviceName());
                deviceVO.setCode(deviceCode);
                deviceVO.setProvinceId(vo.getProvinceId());
                deviceVO.setCityId(vo.getCityId());
                deviceVO.setCountyId(vo.getCountyId());
                deviceVO.setTownCode(vo.getTownCode());
                deviceVO.setLevel(deviceLevel);
                deviceVO.setUniqueCode(deviceCode);
                deviceTreeVO.getList().add(deviceVO);
            }
        });

        return treeList;
    }

    private List<DeviceTreeVO> appendTreeRootNode(List<DeviceTreeVO> list) {
        List<DeviceTreeVO> rootTree = Lists.newArrayList();
        if (!list.isEmpty()) {
            // 补充中国根节点
            DeviceTreeVO root = new DeviceTreeVO();
            sysAreaRootService.createRootTreeVO(root);
            root.setList(list);
            rootTree.add(root);
            list.get(0).setFirst(1);
        }
        return rootTree;
    }


}
