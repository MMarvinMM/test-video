package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.OrderAlgorithmsDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.OrderAlgorithmsMapper;
import com.chinatower.order.repository.IOrderAlgorithmsRepository;
import com.chinatower.order.vo.AlgorithmByOrderVO;
import com.chinatower.order.vo.AlgorithmOneVO;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.device.AlgorithmAreaVO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.chinatower.system.api.model.AlgoTreeVO;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OrderAlgorithmsRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class OrderAlgorithmsRepositoryImpl extends ServiceImpl<OrderAlgorithmsMapper, OrderAlgorithmsDO> implements IOrderAlgorithmsRepository {


    @Autowired
    private OrderAlgorithmsMapper orderAlgorithmsMapper;

    /**
     * 功能描述 批量保存
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderAlgorithmsDOList]
     */
    @Override
    public void saveListBatch(List<OrderAlgorithmsDO> orderAlgorithmsDOList) {

        this.saveBatch(orderAlgorithmsDOList, 5000);
    }

    /**
     * 功能描述 删除订单数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderPackageList]
     */
    @Override
    public void deleteByOrderCode(String orderCode) {

        QueryWrapper<OrderAlgorithmsDO> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_code", orderCode);
        orderAlgorithmsMapper.delete(queryWrapper);
    }

    /**
     * 功能描述 查询订单订购的算法数据
     *
     * @return
     * @author chenmf
     * @date 2022/1/10
     * [orderCode, packageCode]
     */
    @Override
    public List<OrderAlgorithmsDO> getOrderAlgorithmsByList(List<String> orderCodeList, List<String> packageCodeList) {

        QueryWrapper<OrderAlgorithmsDO> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("order_code", orderCodeList);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(packageCodeList)) {
            queryWrapper.in("package_code", packageCodeList);
        }
        queryWrapper.eq("delete_flag", OrderConstants.DELETE_FLAG_NORMAL);
        return orderAlgorithmsMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述 查询算法配置区域
     *
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    @Override
    public List<AlgorithmOneVO> getAlgorithmAreaByOrder(AlgorithmByOrderVO algorithmByOrder) {
        return orderAlgorithmsMapper.getAlgorithmAreaByOrder(algorithmByOrder);
    }

    /**
     * 功能描述  更新算法区域表
     *
     * @return
     * @author chenmf
     * @date 2022/2/10
     * @version
     */
    @Override
    public List<Map<String, String>> getPresetIdAndNameByTaskId(List<String> taskIdList) {
        return orderAlgorithmsMapper.getPresetIdAndNameByTaskId(taskIdList);
    }

    @Override
    public List<OrderAlgorithmsVO> getCustomerAlgorithm(String tenantId, String deviceCode, String industryCode) {
        return baseMapper.getCustomerAlgorithm(tenantId, deviceCode, industryCode);
    }

    /**
     * 更新算法状态
     *
     * @return
     * @author chenmf
     * @date 2022/4/13
     * @version
     */
    @Override
    public void updateAlgorithmStatus(String status, List<AlgorithmOneVO> areaList) {
        if (!CollectionUtils.isEmpty(areaList)) {
            List<String> configIdList = areaList.stream().map(AlgorithmOneVO::getConfigId).distinct().collect(Collectors.toList());
            orderAlgorithmsMapper.updateAlgorithmStatus(configIdList, AlgorithmAreaVO.ALGORITHMLOADINGSTATUS, status);
        }
    }

    @Override
    public void updateAlgorithmStatusByConfigId(String status, List<String> configIdList) {
        if (!CollectionUtils.isEmpty(configIdList)) {
            if (StringUtils.equals(status, "0")) {
                orderAlgorithmsMapper.updateAlgorithmStatus2Enable(configIdList, AlgorithmAreaVO.ALGORITHMLOADINGSTATUS, status);
            } else if (StringUtils.equals(status, "1")) {
                orderAlgorithmsMapper.updateAlgorithmStatus2Disable(configIdList, AlgorithmAreaVO.ALGORITHMLOADINGSTATUS, status);
            }
        }
    }

    @Override
    public int cancelByOrderCode(String orderCode) {
        return orderAlgorithmsMapper.cancelByOrderCode(orderCode);
    }


    /**
     * 行业+版本为key
     *
     * @param orderCode
     * @return
     */
    @Override
    public Map<String, List<AlgoTreeVO>> getTenantAlgoMap(String orderCode) {
        List<AlgoTreeVO> tenantAlgorithmList = orderAlgorithmsMapper.queryTenantAlgorithmList(orderCode);
        Map<String, List<AlgoTreeVO>> deviceTreeVOMap = Maps.newHashMap();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(tenantAlgorithmList)) {
            Map<String, List<AlgoTreeVO>> collect = tenantAlgorithmList.stream().collect(Collectors.groupingBy(x -> x.getIndustryCode() + ":" + x.getAppVersion()));
            collect.forEach((x, y) -> {
                List<AlgoTreeVO> list = Lists.newArrayList();
                list.addAll(appendTreeRootNode(getTreeList(y)));
                deviceTreeVOMap.put(x, list);
            });
        }
        return deviceTreeVOMap;
    }

    private List<AlgoTreeVO> getTreeList(List<AlgoTreeVO> sourceAlgorithmList) {
        List<AlgoTreeVO> tempAlgorithmList =Lists.newArrayList();
        Map<String, Integer> map = new HashMap<>();
        sourceAlgorithmList.forEach(vo -> {
            String typeCode = vo.getAlgoTypeCode();
            String typeName = vo.getAlgoTypeName();
            Integer typeCodeIndex = map.get(typeCode);
            // provinceIndex 不是 null，说明已在list里
            if (ObjectUtils.isEmpty(typeCodeIndex)) {
                AlgoTreeVO tree = new AlgoTreeVO();
                tree.setCode(typeCode);
                tree.setName(typeName);
                tree.setLevel(Constants.ONE);
                tree.setUniqueCode(typeCode);
                tree.setAlgoTypeCode(typeCode);
                tree.setAlgoTypeName(typeName);
                tempAlgorithmList.add(tree);
                typeCodeIndex = tempAlgorithmList.size() - Constants.ONE_NUM;
                map.put(typeCode, typeCodeIndex);
            }
            String algoCode = vo.getAlgorithmCode();
            String algoKey = typeCode + Constants.UNDER_LINE + algoCode;
            Integer cityIndex = map.get(algoKey);
            if (ObjectUtils.isEmpty(cityIndex)) {
                List<AlgoTreeVO> list = tempAlgorithmList.get(typeCodeIndex).getList();
                AlgoTreeVO tree = new AlgoTreeVO();
                tree.setCode(algoCode);
                tree.setName(vo.getAlgorithmName());
                tree.setLevel(Constants.TWO);
                tree.setUniqueCode(algoCode);
                tree.setAlgorithmCode(algoCode);
                tree.setAlgorithmName(vo.getAlgorithmName());
                tree.setAlgoTypeCode(typeCode);
                tree.setAlgoTypeName(typeName);
                tree.setParentCode(vo.getAlgoTypeCode());
                tree.setUniqueCode(algoKey);
                list.add(tree);
                cityIndex = list.size() - Constants.ONE_NUM;
                map.put(algoKey, cityIndex);
            }
        });
        return tempAlgorithmList;
    }

    private List<AlgoTreeVO> appendTreeRootNode(List<AlgoTreeVO> list) {
        List<AlgoTreeVO> rootTree = new ArrayList<>(1);
        if (!list.isEmpty()) {
            // 补充中国根节点
            AlgoTreeVO root = new AlgoTreeVO();
            root.setCode("0");
            root.setName("全部");
            root.setList(list);
            rootTree.add(root);
            list.get(0).setFirst(1);
        }
        return rootTree;
    }
}
