package com.chinatower.sync.consumer.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceTableConstants;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.sync.consumer.dao.CommonDAO;
import com.chinatower.sync.consumer.entity.*;
import com.chinatower.sync.consumer.service.*;
import com.chinatower.sync.consumer.dao.SceneAlarmMapper;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 *
 * video-device-sync
 * @ClassName AlgorithmInfoSyncController
 * @Description 算法基本信息kafka同步
 * @Author hued
 * @Date 2024年7月10日15:12:22
 */
@RestController
@Slf4j
public class AlgorithmInfoSyncController {

    /**
     * 系统创建者
     */
    private static final String CREATOR_SYSTEM = "system";


    /**
     * 配置项编码：视觉模式类型
     */
    private static final String CONFIG_CODE_CV_MODE_TYPE = "modetype";

    /**
     * 字典列表字段名
     */
    private static final String FIELD_DICT_LIST = "dictList";

    /**
     * 配置项编码字段名
     */
    private static final String FIELD_CODE = "code";

    /**
     * 告警编码字段名
     */
    private static final String FIELD_ITEM_CODE = "itemCode";

    /**
     * 告警名称字段名
     */
    private static final String FIELD_ITEM_NAME = "itemName";

    /**
     * 告警来源：AI告警
     */
    private static final String ALARM_SOURCE_AI = "1";

    /**
     * 告警来源：初始化
     */
    private static final String ALARM_SOURCE_INIT = "0";

    /**
     * 空字符串（用于不按版本区分）
     */
    private static final String EMPTY_STRING = "";

    @Resource
    IAlgorithmInfoRepository algorithmInfoRepositoryImpl;
    @Resource
    IAlgorithmTypeRepository algorithmTypeRepositoryImpl;
    @Resource
    IAlgorithmAlarmRepository algorithmAlarmRepositoryImpl;
    @Resource
    IFunctionTypeRepository functionTypeRepositoryImpl;

    @Autowired
    private IAlgorithmConfigInfoService algorithmConfigInfoService;

    @Resource
    CommonDAO commonDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private SceneAlarmMapper sceneAlarmMapper;

    /**
     *
     * 算法基本信息推送
     * 7.1.	算法基本信息推送
     *
     * @param: req
     * @return: com.chinatower.common.entity.Result
     * @author: zheng.yan
     * @date: 2022/2/17
     * @version:
     */
    @PostMapping("/video/algorithm/pushAlgorithmInfo")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> pushAlgorithmInfo(@RequestBody AlgoCommonReq<AlgoInfoReq> req) {
        log.info("算法基本信息推送：" + JSON.toJSONString(req.getData()));
        try {
            List<AlgorithmInfoDO> insertList = new ArrayList<>(req.getData().size());
            List<AlgorithmInfoDO> updateList = new ArrayList<>(req.getData().size());
            List<Long> deleteList = new ArrayList<>(req.getData().size());

            List<AlgorithmAlarmDO> addAlgoAlms = new ArrayList<>(req.getData().size() * 3);

            List<String> algoCodes = req.getData().stream().map(AlgoInfoReq::getAlgorithmCode).collect(Collectors.toList());
            QueryWrapper<AlgorithmInfoDO> algoQuery = new QueryWrapper<>();
            algoQuery.in("algorithm_code", algoCodes);
            algoQuery.eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
            List<AlgorithmInfoDO> existingAlgoes = algorithmInfoRepositoryImpl.list(algoQuery);

            List<String> algoMinTypeCodes = req.getData().stream().map(AlgoInfoReq::getAlgoMinTypeCode).collect(Collectors.toList());
            QueryWrapper<AlgorithmTypeDO> algoTypeQuery = new QueryWrapper<>();
            algoTypeQuery.in("algo_min_type_code", algoMinTypeCodes);
            algoTypeQuery.eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
            List<AlgorithmTypeDO> existingAlgoTypes = algorithmTypeRepositoryImpl.list(algoTypeQuery);
            Date now = new Date();
            for (AlgoInfoReq algoInfoReq : req.getData()) {
                AlgorithmInfoDO algoInfo = this.getAlgoByCode(existingAlgoes, algoInfoReq.getAlgorithmCode());
                AlgorithmTypeDO algoType = this.getAlgoTypeByMinTypCode(existingAlgoTypes, algoInfoReq.getAlgoMinTypeCode());
                if (algoInfoReq.getOptType() == 1 || algoInfoReq.getOptType() == 2) {
                    // 新增情况下
                    if (algoInfo == null) {
                        AlgorithmInfoDO addAlgoInfo = new AlgorithmInfoDO();
                        BeanUtils.copyProperties(algoInfoReq, addAlgoInfo);
                        addAlgoInfo.setChipPlatform(String.valueOf(algoInfoReq.getChipPlatform()));
                        addAlgoInfo.setAlgorithmShape(String.valueOf(algoInfoReq.getAlgorithmShape()));
                        addAlgoInfo.setDeleteFlag(DeviceConstants.VALID_STATE);
                        addAlgoInfo.setCreateTime(now);
                        addAlgoInfo.setUpdateTime(now);
                        if (algoType != null) {
                            BeanUtils.copyProperties(algoType, addAlgoInfo);
                            addAlgoInfo.setStatus(String.valueOf(algoType.getStatus()));
                            addAlgoInfo.setFunctionCode(algoInfoReq.getAlgoMinTypeCode());
                        }
                        if (!CollectionUtils.isEmpty(algoInfoReq.getModelConfig())) {
                            addAlgoInfo.setModelConfig(JSON.toJSONString(algoInfoReq.getModelConfig()));
                        }else{
                            addAlgoInfo.setModelConfig(null);
                        }
                        addAlgoInfo.setExtendJson(JSON.toJSONString(this.setExtendJson(algoInfoReq)));
                        insertList.add(addAlgoInfo);
                    } else {
                        // 算法小类变动时
                        if (!algoInfoReq.getAlgoMinTypeCode().equals(algoInfo.getAlgoMinTypeCode())) {
                            BeanUtils.copyProperties(algoType, algoInfo);
                            algoInfo.setStatus(String.valueOf(algoType.getStatus()));
                        }
                        algoInfo.setUpdateTime(now);
                        BeanUtils.copyProperties(algoInfoReq, algoInfo);
                        algoInfo.setFunctionCode(algoInfoReq.getAlgoMinTypeCode());
                        if (!CollectionUtils.isEmpty(algoInfoReq.getModelConfig())) {
                            algoInfo.setModelConfig(JSON.toJSONString(algoInfoReq.getModelConfig()));
                        }else{
                            algoInfo.setModelConfig(null);
                        }
                        algoInfo.setExtendJson(JSON.toJSONString(this.setExtendJson(algoInfoReq)));
                        updateList.add(algoInfo);
                    }
                    // 构造算法告警类型关系
                    if (!CollectionUtils.isEmpty(algoInfoReq.getAlarmTypeCodeList())) {
                        for (AlarmTypeData alarmTypeData : algoInfoReq.getAlarmTypeCodeList()) {
                            AlgorithmAlarmDO newAlgoAlm = new AlgorithmAlarmDO();
                            newAlgoAlm.setAlgoMinType(algoInfoReq.getAlgoMinTypeCode());
                            newAlgoAlm.setAlarmType(alarmTypeData.getAlarmTypeCode());
                            newAlgoAlm.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                            newAlgoAlm.setCreateDate(now);
                            addAlgoAlms.add(newAlgoAlm);
                        }
                    }
                } else if (algoInfoReq.getOptType() == 3) {
                    if (algoInfo != null) {
                        deleteList.add(algoInfo.getAlgorithmId());
                    }
                }
            }

            // 处理 modelConfig 数据，同步到场景配置表
            processModelConfig(req);

            if (insertList.size() > 0) {
                algorithmInfoRepositoryImpl.saveBatch(insertList);

            }
            if (updateList.size() > 0) {
//                algorithmInfoRepositoryImpl.updateBatchById(updateList);
                commonDAO.updateAlgrithmInfoBatch(updateList);
            }
            if (deleteList.size() > 0) {
                algorithmInfoRepositoryImpl.removeByIds(deleteList);
            }
            // 算法与告警类型关系 先删再增
            QueryWrapper<AlgorithmAlarmDO> algoAlarmQuery = new QueryWrapper<>();
            algoAlarmQuery.in("algo_min_type", algoMinTypeCodes);
            algorithmAlarmRepositoryImpl.remove(algoAlarmQuery);
            if (addAlgoAlms.size() > 0) {
                algorithmAlarmRepositoryImpl.saveBatch(addAlgoAlms);
            }
        } catch (Exception e) {
            log.error("pushAlgorithmInfo error:", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(e.getMessage());
        }

        // 异步调用
        algorithmConfigInfoService.synOrderAlgorithm(req);


        return Result.success("");
    }

    /**
     * 处理 modelConfig 数据，同步到场景配置表
     *
     * @param req 算法信息请求
     */
    private void processModelConfig(AlgoCommonReq<AlgoInfoReq> req) {
        try {
            // 2. 遍历每个算法信息
            for (AlgoInfoReq algoInfoReq : req.getData()) {
                String algorithmCode = algoInfoReq.getAlgorithmCode();

                // 检查算法编码是否是大模型算法
                if (!Constants.TWO.equals(algoInfoReq.getAlgorithmModelType())) {
                    continue;
                }

                // 检查是否有 modelConfig 数据
                if (CollectionUtils.isEmpty(algoInfoReq.getModelConfig())) {
                    continue;
                }

                // 3. 从 vm_algorithm_info_t 表中查询现有的 model_config
                AlgorithmInfoDO algorithmInfoDO = sceneAlarmMapper.selectModelConfigFromAlgorithmInfo(algorithmCode);
                String existingModelConfigJson = algorithmInfoDO.getModelConfig();
                String algorithmModelType = Optional.ofNullable(algorithmInfoDO.getAlgorithmModelType()).orElse("1");

                // 4. 解析传入的 modelConfig 数据，提取告警编码
                Set<String> newAlarmCodes = parseAlarmCodesFromModelConfig(algoInfoReq.getModelConfig());
                log.info("新传入的:{}", JSON.toJSON(newAlarmCodes));
                if (CollectionUtils.isEmpty(newAlarmCodes)) {
                    continue;
                }

                // 5. 解析现有的 model_config，提取告警编码
                Set<String> existingAlarmCodes = new HashSet<>();
                if (StringUtils.isNotEmpty(existingModelConfigJson)) {
                    try {
                        List<Map<String, Object>> existingModelConfig = JSON.parseObject(existingModelConfigJson, List.class);
                        existingAlarmCodes = parseAlarmCodesFromModelConfig(existingModelConfig);
                        log.info("数据库旧数据:{}", JSON.toJSON(existingAlarmCodes));
                    } catch (Exception e) {
                        log.warn("解析现有 model_config 失败，algorithmCode: {}", algorithmCode, e);
                    }
                }

                // 6. 找出缺少的告警编码
                Set<String> missingAlarmCodes = new HashSet<>(newAlarmCodes);
                if(Constants.TWO.equals(algorithmModelType)){
                    missingAlarmCodes.removeAll(existingAlarmCodes);
                    if (CollectionUtils.isEmpty(missingAlarmCodes)) {
                        log.info("算法 {} 没有新增的告警编码，跳过处理", algorithmCode);
                        continue;
                    }
                }

                // 7. 查询 sys_scene_config 表中该算法的所有客户、行业组合
                List<com.chinatower.sync.consumer.entity.SceneAlarmTenantDTO> tenantList = sceneAlarmMapper.selectSceneAlarmTenants(algorithmCode);

                if(Constants.ONE.equals(algorithmModelType)){
                    tenantList.addAll(sceneAlarmMapper.getOrderAlgList(algorithmCode));
                }

                if (CollectionUtils.isEmpty(tenantList)) {
                    log.info("算法 {} 在 sys_scene_config 表中没有客户、行业组合，跳过处理", algorithmCode);
                    continue;
                }

                // 8. 为每个客户、行业组合添加缺少的告警编码
                for (com.chinatower.sync.consumer.entity.SceneAlarmTenantDTO tenant : tenantList) {
                    String tenantId = tenant.getTenantId();
                    String industryCode = tenant.getIndustryCode();

                    // 查询现有场景告警关系
                    List<com.chinatower.sync.consumer.entity.SceneAlarmRelDTO> existingAlarmRels = sceneAlarmMapper.selectSceneAlarmRelByAlgorithmCode(
                            algorithmCode, tenantId, industryCode, null);

                    // 找出该客户、行业下缺少的告警编码
                    Set<String> existingAlarmTypes = existingAlarmRels.stream()
                            .map(com.chinatower.sync.consumer.entity.SceneAlarmRelDTO::getAlarmType)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    Set<String> toInsert = new HashSet<>(missingAlarmCodes);
                    toInsert.removeAll(existingAlarmTypes);

                    if (!CollectionUtils.isEmpty(toInsert)) {
                        // 从传入的 modelConfig 中获取告警编码对应的名称
                        Map<String, String> alarmCodeToNameMap = getAlarmCodeToNameMap(algoInfoReq.getModelConfig());

                        // 插入新的场景告警数据
                        insertNewSceneAlarmData(algorithmCode, tenantId, industryCode,
                                toInsert, alarmCodeToNameMap);
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理 modelConfig 数据异常", e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 解析 modelConfig 数据，提取告警编码集合
     *
     * @param modelConfig modelConfig 数据
     * @return 告警编码集合
     */
    private Set<String> parseAlarmCodesFromModelConfig(List<Map<String, Object>> modelConfig) {
        Set<String> alarmCodes = new HashSet<>();

        log.info("modelConfig:{}", JSON.toJSON(modelConfig));
        for (Map<String, Object> config : modelConfig) {
            // 查找 code 为 "dict-cvModeType" 的配置项
            String code = (String) config.get(FIELD_CODE);
            if (!code.toLowerCase().contains(CONFIG_CODE_CV_MODE_TYPE)) {
                continue;
            }

            // 获取 dictList
            List<Map<String, Object>> dictList = (List<Map<String, Object>>) config.get(FIELD_DICT_LIST);
            if (CollectionUtils.isEmpty(dictList)) {
                continue;
            }

            // 提取告警编码
            for (Map<String, Object> dict : dictList) {
                String itemCode = (String) dict.get(FIELD_ITEM_CODE);
                if (StringUtils.isNotEmpty(itemCode)) {
                    alarmCodes.add(itemCode);
                }
            }
        }
        log.info("alarmCodes:{}", JSON.toJSON(alarmCodes));

        return alarmCodes;
    }

    /**
     * 获取告警编码到名称的映射
     *
     * @param modelConfig modelConfig 数据
     * @return 告警编码到名称的映射
     */
    private Map<String, String> getAlarmCodeToNameMap(List<Map<String, Object>> modelConfig) {
        Map<String, String> codeToNameMap = new HashMap<>();

        for (Map<String, Object> config : modelConfig) {
            String code = (String) config.get(FIELD_CODE);
            if (!code.toLowerCase().contains(CONFIG_CODE_CV_MODE_TYPE)) {
                continue;
            }

            List<Map<String, Object>> dictList = (List<Map<String, Object>>) config.get(FIELD_DICT_LIST);
            if (CollectionUtils.isEmpty(dictList)) {
                continue;
            }

            for (Map<String, Object> dict : dictList) {
                String itemCode = (String) dict.get(FIELD_ITEM_CODE);
                String itemName = (String) dict.get(FIELD_ITEM_NAME);
                if (StringUtils.isNotEmpty(itemCode) && StringUtils.isNotEmpty(itemName)) {
                    codeToNameMap.put(itemCode, itemName);
                }
            }
        }

        return codeToNameMap;
    }

    /**
     * 插入新的场景告警数据
     *
     * @param algorithmCode 算法编码
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @param missingAlarmCodes 缺少的告警编码集合
     * @param alarmCodeToNameMap 告警编码到名称的映射
     */
    private void insertNewSceneAlarmData(String algorithmCode, String tenantId, String industryCode,
                                         Set<String> missingAlarmCodes,
                                         Map<String, String> alarmCodeToNameMap) {
        if (CollectionUtils.isEmpty(missingAlarmCodes)) {
            return;
        }

        // 为每个缺少的告警编码创建场景配置和关系
        List<Map<String, Object>> sceneConfigList = new ArrayList<>();
        List<Map<String, Object>> sceneAlarmRelList = new ArrayList<>();

        for (String alarmTypeCode : missingAlarmCodes) {
            String alarmTypeName = alarmCodeToNameMap.get(alarmTypeCode);
            if (StringUtils.isEmpty(alarmTypeName)) {
                alarmTypeName = alarmTypeCode; // 如果找不到名称，使用编码作为名称
            }

            String sceneId = UUID.randomUUID().toString().replace("-", "");

            // 场景配置
            Map<String, Object> sceneConfig = new HashMap<>();
            sceneConfig.put("sceneId", sceneId);
            sceneConfig.put("sceneName", alarmTypeName); // 使用告警类型名称作为场景名称
            sceneConfig.put("typeValue", alarmTypeCode);
            sceneConfig.put("algorithmCode", algorithmCode);
            sceneConfig.put("creator", CREATOR_SYSTEM);
            sceneConfig.put("tenantId", tenantId);
            sceneConfig.put("industryCode", industryCode);
            sceneConfig.put("appVerCode", EMPTY_STRING); // 不按版本区分
            sceneConfigList.add(sceneConfig);

            // 场景告警关系
            Map<String, Object> sceneAlarmRel = new HashMap<>();
            sceneAlarmRel.put("sceneId", sceneId);
            sceneAlarmRel.put("alarmType", alarmTypeCode);
            sceneAlarmRel.put("alarmTypeName", alarmTypeName);
            sceneAlarmRel.put("algorithmCode", algorithmCode);
            sceneAlarmRel.put("creator", CREATOR_SYSTEM);
            sceneAlarmRel.put("tenantId", tenantId);
            sceneAlarmRel.put("industryCode", industryCode);
            sceneAlarmRel.put("appVerCode", EMPTY_STRING); // 不按版本区分
            sceneAlarmRelList.add(sceneAlarmRel);

        }

        // 批量插入
        if (CollectionUtil.isNotEmpty(sceneConfigList)) {
            sceneAlarmMapper.insertSceneConfigBatch(sceneConfigList);
        }
        if (CollectionUtil.isNotEmpty(sceneAlarmRelList)) {
            sceneAlarmMapper.insertSceneAlarmRelBatch(sceneAlarmRelList);
        }

        log.info("为算法 {} 的客户 {} 行业 {} 新增了 {} 个场景告警配置",
                algorithmCode, tenantId, industryCode, missingAlarmCodes.size());
    }

    /**
     * set扩展字段
     * @param algoInfoReq
     * @return
     */
    private ExtendJson setExtendJson(AlgoInfoReq algoInfoReq){
        ExtendJson extendJson = new ExtendJson();

        extendJson.setSupportFrameColor(
                StringUtils.isEmpty(algoInfoReq.getSupportFrameColor()) ? Constants.ZERO : algoInfoReq.getSupportFrameColor());
        extendJson.setSupportBaselinePic(
                StringUtils.isEmpty(algoInfoReq.getSupportBaselinePic()) ? Constants.ZERO : algoInfoReq.getSupportBaselinePic());
        extendJson.setSupportDoubleImgAnalysis(
                StringUtils.isEmpty(algoInfoReq.getSupportDoubleImgAnalysis()) ? Constants.ZERO : algoInfoReq.getSupportDoubleImgAnalysis());
        extendJson.setDefaultAnalysisPeriod(algoInfoReq.getDefaultAnalysisPeriod());
        extendJson.setAlgDescribeFile(algoInfoReq.getAlgDescribeFile());
        extendJson.setAlgDescribeFileName(algoInfoReq.getAlgDescribeFileName());
        extendJson.setGraphics(algoInfoReq.getGraphics());
        extendJson.setSupportAlarmInterval(Optional.ofNullable(algoInfoReq.getSupportAlarmInterval()).orElse(Constants.ZERO_NUM));
        return extendJson;
    }

    /**
     *
     * 算法类别信息推送
     * 7.2.	算法类别信息推送
     *
     * @param: req
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/17
     * @version:
     */
    @PostMapping("/video/algorithm/pushAlgorithmType")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> pushAlgorithmType(@RequestBody AlgoCommonReq<AlgoTypeReq> req) {
        log.info("算法类别信息推送：" + JSON.toJSONString(req.getData()));
        try {
            List<AlgorithmTypeDO> insertList = new ArrayList<>(req.getData().size());
            List<AlgorithmTypeDO> updateList = new ArrayList<>(req.getData().size());
            List<Long> deleteList = new ArrayList<>(req.getData().size());

            List<String> algoMinTypeCodes = req.getData().stream().map(AlgoTypeReq::getAlgoMinTypeCode).collect(Collectors.toList());
            QueryWrapper<AlgorithmTypeDO> algoTypeQuery = new QueryWrapper<>();
            algoTypeQuery.in("algo_min_type_code", algoMinTypeCodes);
            algoTypeQuery.eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
            List<AlgorithmTypeDO> existingAlgoTypes = algorithmTypeRepositoryImpl.list(algoTypeQuery);

            for (AlgoTypeReq algoTypeReq : req.getData()) {
                AlgorithmTypeDO algoType = this.getAlgoTypeByMinTypCode(existingAlgoTypes, algoTypeReq.getAlgoMinTypeCode());
                if (algoTypeReq.getOptType() == 1 || algoTypeReq.getOptType() == 2) {
                    AlgorithmTypeDO newAlgoTypeInfo = new AlgorithmTypeDO();
                    BeanUtils.copyProperties(algoTypeReq, newAlgoTypeInfo);
                    newAlgoTypeInfo.setStatus(String.valueOf(algoTypeReq.getStatus()));
                    newAlgoTypeInfo.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    // 新增情况下
                    if (algoType == null) {
                        newAlgoTypeInfo.setCreateTime(new Date());
                        newAlgoTypeInfo.setUpdateTime(new Date());
                        insertList.add(newAlgoTypeInfo);
                    } else {
                        newAlgoTypeInfo.setUpdateTime(new Date());
                        newAlgoTypeInfo.setAlgorithmTypeId(algoType.getAlgorithmTypeId());
                        updateList.add(newAlgoTypeInfo);
                    }
                } else if (algoTypeReq.getOptType() == 3) {
                    if (algoType != null) {
                        deleteList.add(algoType.getAlgorithmTypeId());
                    }
                }
            }
            if (insertList.size() > 0) {
                algorithmTypeRepositoryImpl.saveBatch(insertList);
            }
            if (updateList.size() > 0) {
                algorithmTypeRepositoryImpl.updateBatchById(updateList);
            }
            if (deleteList.size() > 0) {
                algorithmTypeRepositoryImpl.removeByIds(deleteList);
            }
        } catch (Exception e) {
            log.error("pushAlgorithmType error:", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(e.getMessage());
        }
        return Result.success("");
    }

    /**
     *
     * 功能类别信息推送
     * 7.4.	功能类别信息推送
     *
     * @param: req
     * @return: com.chinatower.common.entity.Result
     * @author: zheng.yan
     * @date: 2022/2/17
     * @version:
     */
    @PostMapping("/video/algorithm/pushFunctionType")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> pushFunctionType(@RequestBody AlgoCommonReq<FuncTypeReq> req) {
        log.info("功能类别信息推送：" + JSON.toJSONString(req.getData()));
        try {
            List<FunctionTypeDO> insertList = new ArrayList<>(req.getData().size());
            List<FunctionTypeDO> updateList = new ArrayList<>(req.getData().size());
            List<Long> deleteList = new ArrayList<>(req.getData().size());
            List<FuncTypeReq> uptFuncTypes = new ArrayList<>(req.getData().size());

            List<String> funcMinTypeCodes = req.getData().stream().map(FuncTypeReq::getFuncMinTypeCode).collect(Collectors.toList());
            QueryWrapper<FunctionTypeDO> algoTypeQuery = new QueryWrapper<>();
            algoTypeQuery.in("function_code", funcMinTypeCodes);
            algoTypeQuery.eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
            List<FunctionTypeDO> existingFuncTypes = functionTypeRepositoryImpl.list(algoTypeQuery);

            for (FuncTypeReq funcTypeReq : req.getData()) {
                FunctionTypeDO funcType = this.getFuncTypeByMinTypCode(existingFuncTypes, funcTypeReq.getFuncMinTypeCode());
                if (funcTypeReq.getOptType() == 1 || funcTypeReq.getOptType() == 2) {
                    FunctionTypeDO newFuncTypeInfo = buildFuncTypeByReq(funcTypeReq);
                    // 新增情况下
                    if (funcType == null) {
                        newFuncTypeInfo.setCreateDate(new Date());
                        newFuncTypeInfo.setUpdateDate(new Date());
                        newFuncTypeInfo.setIconCode(funcTypeReq.getLogoImageName());
                        insertList.add(newFuncTypeInfo);
                    } else {
                        newFuncTypeInfo.setId(funcType.getId());
                        newFuncTypeInfo.setUpdateDate(new Date());
                        newFuncTypeInfo.setIconCode(funcTypeReq.getLogoImageName());
                        updateList.add(newFuncTypeInfo);
                    }
                    uptFuncTypes.add(funcTypeReq);
                } else if (funcTypeReq.getOptType() == 3) {
                    if (funcType != null) {
                        deleteList.add(funcType.getId());
                    }
                }
            }
            if (insertList.size() > 0) {
                functionTypeRepositoryImpl.saveBatch(insertList);
                List uptFunCodeList = new ArrayList();
                for (FuncTypeReq uptFuncType : uptFuncTypes) {
                    if (!CollectionUtils.isEmpty(uptFuncType.getAlgoMinTypeCodeList())) {
                        uptFunCodeList.add(uptFuncType);
                    }
                }
                // 执行批量更新
                if (!CollectionUtils.isEmpty(uptFunCodeList)) {
                    algorithmInfoRepositoryImpl.updateFuncCodeByMinTypeCodes(uptFunCodeList);
                }
            }
            if (updateList.size() > 0) {
                functionTypeRepositoryImpl.updateBatchById(updateList);
            }
            if (deleteList.size() > 0) {
                functionTypeRepositoryImpl.removeByIds(deleteList);
            }
        } catch (Exception e) {
            log.error("pushFunctionType error:", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(e.getMessage());
        }
        return Result.success("");
    }

    private AlgorithmInfoDO getAlgoByCode(List<AlgorithmInfoDO> existingAlgoes, String algorithmCode) {
        AlgorithmInfoDO algo = null;
        for (AlgorithmInfoDO algoInfo : existingAlgoes) {
            if (algorithmCode.equals(algoInfo.getAlgorithmCode())) {
                algo = algoInfo;
                break;
            }
        }
        return algo;
    }

    private AlgorithmTypeDO getAlgoTypeByMinTypCode(List<AlgorithmTypeDO> algoTypes, String algoMinTypeCode) {
        AlgorithmTypeDO algoType = null;
        for (AlgorithmTypeDO algoTypeInfo : algoTypes) {
            if (algoMinTypeCode.equals(algoTypeInfo.getAlgoMinTypeCode())) {
                algoType = algoTypeInfo;
                break;
            }
        }
        return algoType;
    }

    private FunctionTypeDO getFuncTypeByMinTypCode(List<FunctionTypeDO> funcTypes, String funcMinTypeCode) {
        FunctionTypeDO funcType = null;
        for (FunctionTypeDO funcTypeInfo : funcTypes) {
            if (funcMinTypeCode.equals(funcTypeInfo.getFunctionCode())) {
                funcType = funcTypeInfo;
                break;
            }
        }
        return funcType;
    }

    private FunctionTypeDO buildFuncTypeByReq(FuncTypeReq funcTypeReq) {
        FunctionTypeDO functionType = new FunctionTypeDO();
        functionType.setFunctionType(funcTypeReq.getFuncTypeCode());
        functionType.setFunctionTypeName(funcTypeReq.getFuncTypeName());
        functionType.setSubFunctionType(funcTypeReq.getFuncSubTypeCode());
        functionType.setSubFunctionTypeName(funcTypeReq.getFuncSubTypeName());
        functionType.setFunctionCode(funcTypeReq.getFuncMinTypeCode());
        functionType.setFunctionCodeName(funcTypeReq.getFuncMinTypeName());
        functionType.setIconUrl(funcTypeReq.getLogoUrl());
        functionType.setFunctionDetail(funcTypeReq.getFunctionDiscribtion());
        functionType.setUrl(funcTypeReq.getUnAlgoFunctionUrl());
        functionType.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
        if (!CollectionUtils.isEmpty(funcTypeReq.getDesImageUrlList())) {
            List<String> imageUrlList = funcTypeReq.getDesImageUrlList().stream().map(FuncImageReq::getImageUrl)
                    .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            functionType.setDesImageUrl(Joiner.on(",").join(imageUrlList));
        }
        if (!CollectionUtils.isEmpty(funcTypeReq.getDesVideoUrlList())) {
            List<String> videoUrlList = funcTypeReq.getDesVideoUrlList().stream().map(FuncVideoReq::getVideoUrl)
                    .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            functionType.setDesVideoUrl(Joiner.on(",").join(videoUrlList));
        }
        return functionType;
    }
}
