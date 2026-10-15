package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.industry.api.IndustryAlgorithmService;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.order.mapper.OrderAlgorithmsHisMapper;
import com.chinatower.order.vo.SceneAlarmRelVO;
import com.chinatower.order.vo.SceneConfigVO;
import com.chinatower.platform.entity.device.ModelConfigVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HandleAlarmSceneDeal extends AbstractOrderDeal {

    @Autowired
    private OrderAlgorithmsHisMapper orderAlgorithmsHisMapper;

    @Autowired
    private IndustryAlgorithmService industryAlgorithmService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    private final static String SYSTEM_STR = "system";

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        // 初始化告警类型场景
        initSysAlarmSceneInfo(orderGateway);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 初始化告警类型逻辑
     * 1）新增默认场景与告警子类型关系数据（sys_scene_alarm_rel、sys_scene_config）
     */
    private void initSysAlarmSceneInfo(OrderGatewayDTO orderGateway) {
        OrderGatewayDTO.Customer customer = orderGateway.getCustomer();
        String customerCode = customer.getCustomerCode();
        List<OrderGatewayDTO.OrderPackages> packageList = orderGateway.getPackages();

        // 大模型算法编码
//        List<String> algorithmCodeList = Lists.newArrayList("BJDX-1717134001", "BJDX-1717135001");
//        Result<List<SysDictDataResponse>> dictDataType = systemFeignClient.dictDataType("model_config_list");
//        if (dictDataType != null && CollectionUtils.isNotEmpty(dictDataType.getData())) {
//            log.info("assembleModelConfigAlarmTypeList dictDataType data:{}", GsonUtils.toJSONString(dictDataType.getData()));
//            algorithmCodeList = dictDataType.getData().stream().map(SysDictDataResponse::getDictValue).collect(Collectors.toList());
//        }

        // 大模型算法编码
        List<String> algorithmCodeList = orderAlgorithmsHisMapper.selectModelAlgorithmCodeList();
        if (CollectionUtils.isEmpty(algorithmCodeList)) {
            return;
        }

        /*
         * 1、查询大模型算法有哪些
         * 2、查询订购的算法信息有没有大模型的
         * 3、查询2中行业订购的大模型算法有没有初始化过
         */
        // key -> industryCode value -> needInitAlgorithmCodeList
        Map<String, List<String>> industryAlgorithmCodeMap = Maps.newHashMap();

        Map<String, String> indusryMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackages : packageList) {
            List<OrderGatewayDTO.Algorithm> algorithmList = orderPackages.getAlgorithms();
            String industryCode = orderPackages.getApplication().getAppCode();
            String appVerCode = orderPackages.getApplication().getProductCode();
            if (CollectionUtils.isNotEmpty(algorithmList)) {

                // 根据客户+行业查询订购历史表过滤掉之前订购过的大模型算法，只初始化第一次
//                List<String> orderedModelAlgorithmCodeHisList = orderAlgorithmsHisMapper.selectOrderedModelAlgorithmCodeHis(customerCode, industryCode, algorithmCodeList);
                // 根据客户+行业查询订购表过滤掉之前订购过的大模型算法，只初始化第一次
//                List<String> orderedModelAlgorithmCodeList = orderAlgorithmsHisMapper.selectOrderedModelAlgorithmCode(customerCode, industryCode, orderCode, algorithmCodeList);
                // 根据客户+行业查询算法场景表过滤掉之前初始化过的大模型算法，只初始化第一次
                List<String> alreadyInitAlgorithmCodeList = orderAlgorithmsHisMapper.selectAlreadyInitAlgorithmCode(customerCode, industryCode, algorithmCodeList);

                // 本行业订购的大模型算法
                List<String> needInitAlgorithmCodeList = algorithmList.stream()
                        .map(OrderGatewayDTO.Algorithm::getAlgorithmCode)
                        .filter(x -> (algorithmCodeList.contains(x)
//                                && !orderedModelAlgorithmCodeHisList.contains(x)
//                                && !orderedModelAlgorithmCodeList.contains(x)
                                && !alreadyInitAlgorithmCodeList.contains(x))).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(needInitAlgorithmCodeList)) {
                    industryAlgorithmCodeMap.put(industryCode, needInitAlgorithmCodeList);

                    indusryMap.put(industryCode, appVerCode);
                }
            }
        }

        // 初始化场景信息
        List<SceneConfigVO> initSceneConfigList = Lists.newArrayList();
        // 初始化场景关联信息
        List<SceneAlarmRelVO> initSceneAlarmRelList = Lists.newArrayList();

        // 查询大模型的子模型信息
        Map<String, List<AlarmTypeVO>> modelConfigAlarmTypeMap = assembleModelConfigAlarmTypeList(algorithmCodeList);

        Date now = new Date();

        for (Map.Entry<String, List<String>> initEntry : industryAlgorithmCodeMap.entrySet()) {
            String industryCode = initEntry.getKey();
            // 大算法列表【xxx大模型、xxx大模型】
            List<String> needInitAlgorithmCodeList = initEntry.getValue();
            for (String needInitAlgorithmCode : needInitAlgorithmCodeList) {
                List<AlarmTypeVO> subAlarmTypeList = modelConfigAlarmTypeMap.get(needInitAlgorithmCode);
                if (CollectionUtils.isNotEmpty(subAlarmTypeList)) {
                    handleInitSceneInfo(subAlarmTypeList, needInitAlgorithmCode, now, customerCode, industryCode, indusryMap.get(industryCode),
                            initSceneConfigList, initSceneAlarmRelList);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(initSceneConfigList)) {
            orderAlgorithmsHisMapper.initSceneConfigInfo(initSceneConfigList);
        }
        if (CollectionUtils.isNotEmpty(initSceneAlarmRelList)) {
            orderAlgorithmsHisMapper.initSceneAlarmRelInfo(initSceneAlarmRelList);
        }
    }

    private void handleInitSceneInfo(List<AlarmTypeVO> subAlarmTypeList, String algorithmCode, Date now, String tenantId,
                                     String industryCode, String appVerCode,
                                     List<SceneConfigVO> initSceneConfigList, List<SceneAlarmRelVO> initSceneAlarmRelList) {
        if (CollectionUtils.isEmpty(subAlarmTypeList)) {
            return;
        }
        for (AlarmTypeVO alarmTypeVO : subAlarmTypeList) {
            SceneConfigVO configData = new SceneConfigVO();
            String sceneId = UUID.randomUUID().toString();
            String subAlarmType = alarmTypeVO.getTypeValue();
            String subAlarmName = alarmTypeVO.getTypeName();

            // 组装场景配置信息
            configData.setSceneId(sceneId);
            configData.setSceneName(subAlarmName);
            configData.setTypeValue(subAlarmType);
            configData.setAlgorithmCode(algorithmCode);
            configData.setCreator(SYSTEM_STR);
            configData.setCreateTime(now);
            configData.setDeleteFlag(0);
            configData.setTenantId(tenantId);
            configData.setIndustryCode(industryCode);
            configData.setAppVerCode(appVerCode);
            initSceneConfigList.add(configData);

            // 组装场景告警类型关联信息
            SceneAlarmRelVO alarmRelData = new SceneAlarmRelVO();
            alarmRelData.setSceneId(sceneId);
            alarmRelData.setAlarmType(subAlarmType);
            alarmRelData.setAlarmTypeName(subAlarmName);
            alarmRelData.setAlgorithmCode(algorithmCode);
            alarmRelData.setCreator(SYSTEM_STR);
            alarmRelData.setCreateTime(now);
            alarmRelData.setDeleteFlag(0);
            alarmRelData.setTenantId(tenantId);
            alarmRelData.setIndustryCode(industryCode);
            alarmRelData.setAppVerCode(appVerCode);
            initSceneAlarmRelList.add(alarmRelData);
        }
    }

    private Map<String, List<AlarmTypeVO>> assembleModelConfigAlarmTypeList(List<String> orderModelAlgorithmCodeList) {
        Map<String, List<AlarmTypeVO>> modelConfigAlarmTypeMap = Maps.newHashMap();
        try {
            if (CollectionUtils.isNotEmpty(orderModelAlgorithmCodeList)) {
                Result<List<AlgorithmInfoDO>> algorithmInfoResult = industryAlgorithmService.getAlgorithmInfoBatch(orderModelAlgorithmCodeList);
                log.info("assembleModelConfigAlarmTypeList param:{} result:{}",
                        GsonUtils.toJSONString(orderModelAlgorithmCodeList), GsonUtils.toJSONString(algorithmInfoResult));
                if (algorithmInfoResult != null && CollectionUtils.isNotEmpty(algorithmInfoResult.getData())) {
                    algorithmInfoResult.getData().stream().filter(x -> org.apache.commons.lang3.StringUtils.isNoneBlank(x.getModelConfig())).forEach(algorithmInfo -> {
                        String modelConfig = algorithmInfo.getModelConfig();
                        if (StringUtils.isNotEmpty(modelConfig)) {
                            List<ModelConfigVO> modelConfigList = JSONObject.parseArray(modelConfig, ModelConfigVO.class);
                            if (CollectionUtils.isNotEmpty(modelConfigList)) {
                                for (ModelConfigVO modelConfigVO : modelConfigList) {
                                    if (StringUtils.isEmpty(modelConfigVO.getCode())
                                            || !modelConfigVO.getCode().toLowerCase().contains("modetype")
                                            || CollectionUtils.isEmpty(modelConfigVO.getDictList())) {
                                        continue;
                                    }
                                    List<AlarmTypeVO> modelConfigSubList = modelConfigVO.getDictList().stream()
                                            .filter(x -> org.apache.commons.lang3.StringUtils.isNoneBlank(x.getItemCode()))
                                            .map(x -> {
                                                AlarmTypeVO alarmType = new AlarmTypeVO();
                                                alarmType.setTypeValue(x.getItemCode());
                                                alarmType.setTypeName(x.getItemName());
                                                return alarmType;
                                            }).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(modelConfigSubList)) {
                                        modelConfigAlarmTypeMap.put(algorithmInfo.getAlgorithmCode(), modelConfigSubList);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("AlarmTypeRepositoryImpl assembleModelConfigAlarmTypeList error:", e);
        }
        return modelConfigAlarmTypeMap;
    }
}
