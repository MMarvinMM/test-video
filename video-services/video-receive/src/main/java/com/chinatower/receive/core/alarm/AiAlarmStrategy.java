package com.chinatower.receive.core.alarm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.AlarmPoint;
import com.chinatower.common.core.dto.BoxInfo;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.text.Convert;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.common.core.utils.ImageDrawBoxUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.api.ITradeExportWarningService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.dto.HandleAlarmImgDTO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaListVO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.DirectionVO;
import com.chinatower.platform.entity.device.ThreadLineVO;
import com.chinatower.platform.entity.device.TripThreadVO;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.AlarmConstants;
import com.chinatower.receive.constant.CommonConstant;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.alarm.dto.LargeModelDrawBoxResult;
import com.chinatower.receive.core.alarm.util.LargeModelAlarmFieldSplitUtil;
import com.chinatower.receive.core.alarm.util.SubScenesWriteHelper;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.kafka.KafkaProducer;
import com.chinatower.receive.mapper.IDeviceMapper;
import com.chinatower.receive.mapper.SysSceneAlarmMapper;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.IAlarmSuppressConfigRepository;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.util.BASE64DecodedMultipartFile;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.SceneAlarmRelationVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.receive.core.alarm.ModelConfigDTO.*;

/**
 * AI告警策略实现类
 *
 * @version release_v1.6.90
 * @Author: songxinfeng
 * @Date: 2024/8/6 9:48
 */

@Component("aiAlarmStrategy")
@Slf4j
public class AiAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    @Autowired
    private ITradeExportWarningService iTradeExportWarningService;
    @Autowired
    private SysSceneAlarmMapper sysSceneAlarmMapper;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private FileFeignClient fileFeignClient;
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private IDeviceMapper deviceMapper;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        boolean flag = (StringUtils.isEmpty(receiveAlarmVO.getAlarmSource()) || WarningSourceEnum.AI_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource()));
        return flag;
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("AI告警上报入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

            // ###1、获取并验证告警租户信息
            List<OrderCustomerVO> customers = getCustomers(receiveAlarmVO);
            if (CollectionUtils.isEmpty(customers)) {
                log.info("未查询到租户信息，告警编码：{}", receiveAlarmVO.getAlarmCode());
                return;
            }
            // 更新租户信息以兼容历史版本
            warningOrderUtil.updateCustomerVersions(customers);
            receiveAlarmDTO.setCustomers(customers);
            log.info("匹配接收告警的租户信息：{}", JSON.toJSONString(customers));

            // 大模型场景告警处理逻辑
            Boolean processed = processLargeModelSceneAlarm(receiveAlarmDTO);
            if (processed) {
                // 大模型告警 且 报文未处理，处理完后发送kafka无需后续流程
                log.info("大模型场景告警报文重组完成，告警编码：{}", receiveAlarmVO.getAlarmCode());
                return;
            }

            // ###2、处理告警的地址和经纬度数据
            getAddress(receiveAlarmVO);
            log.info("通过经纬度获取省市区乡镇编码：{}", JSON.toJSONString(receiveAlarmVO));

            // 根据经纬度获取网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);

            // ###3、填充告警工单相关参数
            warningOrderUtil.populateAlarmWorkOrderParams(receiveAlarmVO);

            // ###4、处理告警工单、告警压制及运营研判
            List<ReceiveAlarmVO> alarms = aiWarningOrderHandler(receiveAlarmDTO);

            // ###5、处理AI告警油气管线合成图片
            aiPipelineHandler(receiveAlarmDTO);
            if (CollectionUtils.isEmpty(alarms)) {
                log.info("告警工单处理失败，告警编码：{}", receiveAlarmVO.getAlarmCode());
                return;
            }


        } catch (Exception e) {
            log.info("接收AI告警时发生异常：" + e);
        }
    }

    private Boolean processLargeModelSceneAlarm(ReceiveAlarmDTO receiveAlarmDTO) {
        // 1. 判断是否是大模型告警类型
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
        log.info("processLargeModelSceneAlarm判断是否是大模型告警类型,告警编码：{}", receiveAlarmVO.getAlarmCode());
        if (receiveAlarmVO.getDutyLargeModelFlag() != null && receiveAlarmVO.getDutyLargeModelFlag()) {
            log.info("processLargeModelSceneAlarm大模型报文已处理，不再进行后续逻辑，告警编码：{}", receiveAlarmVO.getAlarmCode());
            return Boolean.FALSE;
        }
        String algorithmCode = receiveAlarmVO.getAlgorithmCode();
        int count = deviceMapper.countByAlgorithmCodeAndModelType(algorithmCode, "2");
        if (count == 0) {
            log.info("processLargeModelSceneAlarm未查询到大模型算法数据，不是大模型告警，告警编码：{}", receiveAlarmVO.getAlarmCode());
            return Boolean.FALSE;
        }
        log.info("processLargeModelSceneAlarm当前告警类型为大模型告警类型，告警编码：{}", receiveAlarmVO.getAlarmCode());
        // 2. 提取告警子类型
        log.info("processLargeModelSceneAlarm提取告警子类型,告警编码：{}", receiveAlarmVO.getAlarmCode());
        Object extendField = receiveAlarmVO.getExtendField();
        if (extendField == null) {
            log.info("processLargeModelSceneAlarm获取告警扩展字段为空，告警编码：{}", receiveAlarmVO.getAlarmCode());
            throw new CustomException("processLargeModelSceneAlarm获取告警扩展字段为空");
        }
        List<ReceiveAlarmVO> subAlarmInfoList = parseExtendField(extendField);
        log.info("parseExtendField: {}", JSONUtil.toJsonStr(subAlarmInfoList));
        List<String> alarmTypeList = subAlarmInfoList.stream().map(ReceiveAlarmVO::getAlarmType).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(alarmTypeList)) {
            log.info("processLargeModelSceneAlarm获取告警子类型为空，告警编码：{}", receiveAlarmVO.getAlarmCode());
            throw new CustomException("processLargeModelSceneAlarm获取告警子类型为空");
        }
        // 3. 匹配场景，获取场景和告警子项关系
        String taskId = receiveAlarmVO.getTaskId();
        log.info("processLargeModelSceneAlarm匹配场景,taskId:{},alarmTypeList:{}", taskId, alarmTypeList);
        List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();
        List<SceneAlarmRelationVO> sceneAlarmRelations = sysSceneAlarmMapper.selectSceneAlarmRelationByTaskId(taskId, alarmTypeList, customers);
        if (CollectionUtil.isEmpty(sceneAlarmRelations)) {
            log.info("processLargeModelSceneAlarm未匹配到场景，taskId:{},alarmTypeList:{}", taskId, alarmTypeList);
            throw new CustomException("processLargeModelSceneAlarm未匹配到场景");
        }
        // 4. 重组报文、画框（一个场景一个告警）
        List<ReceiveAlarmVOReq> alarmVOReqList = new ArrayList<>();
        Map<String, List<SceneAlarmRelationVO>> groupSceneMap = sceneAlarmRelations.stream().collect(Collectors.groupingBy(SceneAlarmRelationVO::getSceneCode));
        groupSceneMap.forEach((sceneCode, sceneAlarmRelationValue) -> {
            log.info("processLargeModelSceneAlarm-groupSceneMap,sceneCode:{},sceneAlarmRelationValue:{}", sceneCode, JSON.toJSONString(sceneAlarmRelationValue));
            SceneAlarmRelationVO sceneAlarmRelationVO = sceneAlarmRelationValue.get(0);
            ReceiveAlarmVOReq alarmVOReq = new ReceiveAlarmVOReq();
            ReceiveAlarmVO newReceiveAlarmVO = BeanUtil.copyProperties(receiveAlarmVO, ReceiveAlarmVO.class);
            newReceiveAlarmVO.setAlarmBody(sceneAlarmRelationVO.getSceneName());
            newReceiveAlarmVO.setAlarmType(sceneAlarmRelationVO.getAlarmType());
            newReceiveAlarmVO.setAlgorithmCode(sceneAlarmRelationVO.getAlgorithmCode());
            newReceiveAlarmVO.setTaskId(taskId);
            // 告警子项编码
            List<String> subAlarmTypeList = sceneAlarmRelationValue.stream().map(SceneAlarmRelationVO::getAlgorithmAlarmType).collect(Collectors.toList());
            // 处理标绘区域框/屏蔽区域框
            String modelConfig = sceneAlarmRelationVO.getModelConfig();
            // 解析当前场景标绘框 monitorAreaId，用于拆分 extendField/extraField 及过滤 objectCount
            List<String> drawAreaConfigCodes = getDrawAreaConfigCodesFromDict();
            List<String> sceneMonitorAreaIds = LargeModelAlarmFieldSplitUtil.extractSceneMonitorAreaIds(
                    modelConfig, subAlarmTypeList, sceneCode, drawAreaConfigCodes);
            Set<String> monitorAreaIdSet = new HashSet<>(sceneMonitorAreaIds);
            log.info("processLargeModelSceneAlarm-sceneMonitorAreaIds,sceneCode:{},monitorAreaIds:{}",
                    sceneCode, JSON.toJSONString(sceneMonitorAreaIds));
            // 按场景拆分 extendField / extraField（含内层 extendField / extraField 的 attrList）
            Object splitExtendField = LargeModelAlarmFieldSplitUtil.splitExtendField(
                    receiveAlarmVO.getExtendField(), subAlarmTypeList, monitorAreaIdSet);
            newReceiveAlarmVO.setExtendField(splitExtendField);
            newReceiveAlarmVO.setExtraField(LargeModelAlarmFieldSplitUtil.splitExtraField(
                    receiveAlarmVO.getExtraField(), subAlarmTypeList, monitorAreaIdSet));
            // 从拆分后的 extendField 重组告警区域，保证与 objectCount 一致
            List<ReceiveAlarmVO> filteredSubAlarmList = LargeModelAlarmFieldSplitUtil.parseSubAlarmsFromExtendField(splitExtendField);
            List<AlarmOriginalAreaVO> alarmAreaList = LargeModelAlarmFieldSplitUtil.flattenAlarmAreaList(filteredSubAlarmList);
            newReceiveAlarmVO.setAlarmArea(alarmAreaList);
            newReceiveAlarmVO.setAlarmAreaList(LargeModelAlarmFieldSplitUtil.mergeAlarmAreaList(filteredSubAlarmList));
            log.info("processLargeModelSceneAlarm-extendField拆分完成,sceneCode:{},filteredSubAlarmSize:{},alarmAreaSize:{}",
                    sceneCode, filteredSubAlarmList.size(), alarmAreaList.size());
            // 5. 告警图片画框
            String encryptUrl = fileUrlEncryptUtil.dealObsFileUrl(newReceiveAlarmVO.getOriginalPictureUrl());
            log.info("processLargeModelSceneAlarm-告警图片画框,告警编码：{},encryptUrl={}", receiveAlarmVO.getAlarmCode(), encryptUrl);
            List<BoxInfo> alarmBoxList = dealBoxInfo(alarmAreaList);
            log.info("dealBoxInfo,alarmAreaList:{} ,boxList:{},boxList.size():{}", JSONUtil.toJsonStr(alarmAreaList), JSONUtil.toJsonStr(alarmBoxList), alarmBoxList.size());
            // 处理标绘区域框/屏蔽区域框/绊线，并按规则过滤是否产生告警
            log.info("processLargeModelSceneAlarm-dealModelConfigBoxInfo,sceneCode:{},subAlarmTypeList:{},modelConfig:{}", sceneCode, subAlarmTypeList, modelConfig);
            List<BoxInfo> modelConfigBoxList = dealModelConfigBoxInfo(modelConfig, subAlarmTypeList, sceneCode);
            LargeModelDrawBoxResult drawBoxResult = buildLargeModelDrawBoxes(
                    receiveAlarmVO.getAlarmCode(), sceneCode, alarmBoxList, modelConfigBoxList);
            if (!drawBoxResult.isShouldAlarm()) {
                log.info("processLargeModelSceneAlarm-不满足告警条件,跳过本场景告警,alarmCode:{},sceneCode:{},reason:{}",
                        receiveAlarmVO.getAlarmCode(), sceneCode, drawBoxResult.getSkipReason());
                return;
            }
            List<BoxInfo> boxList = drawBoxResult.getBoxList();
            log.info("processLargeModelSceneAlarm-画框列表,sceneCode:{},boxList.size():{},boxList:{}",
                    sceneCode, boxList.size(), GsonUtils.toJSONString(boxList));
            byte[] imageBytes;
            try {
                imageBytes = ImageDrawBoxUtil.drawMultipleBoxes(encryptUrl, boxList);
            } catch (IOException e) {
                log.info("processLargeModelSceneAlarm-告警图片画框异常,告警编码：{}", receiveAlarmVO.getAlarmCode(), e);
                throw new CustomException("告警图片画框异常");
            }
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            //获取到图片，转成MultipartFile流
            String base64DataUri = "data:image/png;base64," + base64;
            MultipartFile multipartFile = BASE64DecodedMultipartFile.base64ToMultipart(base64DataUri);
            //上传图片地址到obs
            AjaxResult ajaxResult = fileFeignClient.uploadFile(multipartFile);
            log.info("processLargeModelSceneAlarm-上传图片地址到obs,告警编码：{},上传结果:{}", receiveAlarmVO.getAlarmCode(), JSON.toJSONString(ajaxResult));
            if (ajaxResult != null && ajaxResult.ok()) {
                SysFileInfo sysFileInfo = ajaxResult.parseData(SysFileInfo.class);
                String fileUrl = sysFileInfo.getOriginUrl();
                newReceiveAlarmVO.setOriginalPictureUrl(fileUrl);
            }
            // Kafka 发送前写入实际触发子任务编码（基于裁剪后的 extraField）
            SubScenesWriteHelper.fillSubScenes(newReceiveAlarmVO);
            alarmVOReq.setData(newReceiveAlarmVO);
            alarmVOReqList.add(alarmVOReq);
        });
        // 6. 发送kafka
        if (CollectionUtil.isNotEmpty(alarmVOReqList)) {
            alarmVOReqList.forEach(alarmVOReq -> {
                alarmVOReq.getData().setDutyLargeModelFlag(Boolean.TRUE);
                kafkaProducer.sendToKafka(AlarmConstants.LARGE_MODEL_SCENE_ALARM_TOPIC, JSON.toJSONString(alarmVOReq));
            });
            log.info("processLargeModelSceneAlarm-发送kafka完成,告警编码：{}", receiveAlarmVO.getAlarmCode());
        }
        return Boolean.TRUE;
    }

    /**
     * 合并大模型告警框与配置框，并按标绘区域规则过滤
     * <p>
     * 规则：
     * 1. 标绘区域框内无告警框 → 不画该标绘框
     * 2. 整张图无标绘框，或仅有标绘框而无告警框 → 不产生告警
     * </p>
     *
     * @param alarmCode           告警编码
     * @param sceneCode           场景编码
     * @param alarmBoxList        AI 检测告警框
     * @param modelConfigBoxList  配置框（标绘/屏蔽/绊线）
     * @return 画框结果及是否应告警
     */
    private LargeModelDrawBoxResult buildLargeModelDrawBoxes(String alarmCode, String sceneCode,
                                                             List<BoxInfo> alarmBoxList,
                                                             List<BoxInfo> modelConfigBoxList) {
        List<BoxInfo> safeAlarmBoxList = CollectionUtils.isEmpty(alarmBoxList) ? Collections.emptyList() : alarmBoxList;
        List<BoxInfo> safeModelConfigBoxList = CollectionUtils.isEmpty(modelConfigBoxList)
                ? Collections.emptyList() : modelConfigBoxList;

        List<BoxInfo> drawAreaBoxList = safeModelConfigBoxList.stream()
                .filter(this::isDrawAreaBox)
                .collect(Collectors.toList());
        List<BoxInfo> otherConfigBoxList = safeModelConfigBoxList.stream()
                .filter(box -> !isDrawAreaBox(box))
                .collect(Collectors.toList());

        log.info("buildLargeModelDrawBoxes-初始统计,alarmCode:{},sceneCode:{},告警框数:{},标绘框数:{},其他配置框数:{}",
                alarmCode, sceneCode, safeAlarmBoxList.size(), drawAreaBoxList.size(), otherConfigBoxList.size());

        // 标绘框内无告警框则不画该标绘框
        List<BoxInfo> validDrawAreaBoxList = filterDrawAreasContainingAlarms(drawAreaBoxList, safeAlarmBoxList);
        log.info("buildLargeModelDrawBoxes-标绘框过滤,alarmCode:{},sceneCode:{},过滤前标绘框数:{},过滤后标绘框数:{}",
                alarmCode, sceneCode, drawAreaBoxList.size(), validDrawAreaBoxList.size());

        // 仅保留落在有效标绘框内的告警框
        List<BoxInfo> validAlarmBoxList = safeAlarmBoxList.stream()
                .filter(alarm -> isBoxCenterInAnyPolygon(alarm, validDrawAreaBoxList))
                .collect(Collectors.toList());
        log.info("buildLargeModelDrawBoxes-告警框过滤,alarmCode:{},sceneCode:{},过滤前告警框数:{},过滤后告警框数:{}",
                alarmCode, sceneCode, safeAlarmBoxList.size(), validAlarmBoxList.size());

        if (CollectionUtils.isEmpty(validDrawAreaBoxList)) {
            String reason = CollectionUtils.isEmpty(drawAreaBoxList)
                    ? "整张图无标绘框"
                    : "标绘框内均无告警框";
            log.info("buildLargeModelDrawBoxes-不产生告警,alarmCode:{},sceneCode:{},reason:{}",
                    alarmCode, sceneCode, reason);
            return new LargeModelDrawBoxResult(Collections.emptyList(), false, reason);
        }
        if (CollectionUtils.isEmpty(validAlarmBoxList)) {
            log.info("buildLargeModelDrawBoxes-不产生告警,alarmCode:{},sceneCode:{},reason:仅有标绘框而无告警框",
                    alarmCode, sceneCode);
            return new LargeModelDrawBoxResult(Collections.emptyList(), false, "仅有标绘框而无告警框");
        }

        List<BoxInfo> finalBoxList = new ArrayList<>();
        finalBoxList.addAll(validAlarmBoxList);
        finalBoxList.addAll(validDrawAreaBoxList);
        finalBoxList.addAll(otherConfigBoxList);
        log.info("buildLargeModelDrawBoxes-产生告警,alarmCode:{},sceneCode:{},最终画框数:{},其中告警框:{},标绘框:{},其他配置框:{}",
                alarmCode, sceneCode, finalBoxList.size(), validAlarmBoxList.size(),
                validDrawAreaBoxList.size(), otherConfigBoxList.size());
        return new LargeModelDrawBoxResult(finalBoxList, true, null);
    }

    /**
     * 过滤标绘区域框：仅保留内部包含至少一个告警框中心点的标绘框
     */
    private List<BoxInfo> filterDrawAreasContainingAlarms(List<BoxInfo> drawAreaBoxList, List<BoxInfo> alarmBoxList) {
        if (CollectionUtils.isEmpty(drawAreaBoxList) || CollectionUtils.isEmpty(alarmBoxList)) {
            return Collections.emptyList();
        }
        return drawAreaBoxList.stream()
                .filter(drawArea -> alarmBoxList.stream().anyMatch(alarm -> isBoxCenterInDrawArea(alarm, drawArea)))
                .collect(Collectors.toList());
    }

    /**
     * 判断告警框中心点是否落在指定标绘区域多边形内
     */
    private boolean isBoxCenterInDrawArea(BoxInfo alarmBox, BoxInfo drawAreaBox) {
        if (alarmBox == null || drawAreaBox == null) {
            return false;
        }
        double[] center = getBoxCenter(alarmBox);
        if (center == null || CollectionUtils.isEmpty(drawAreaBox.getAlarmPointList())) {
            return false;
        }
        return isPointInPolygon(center[0], center[1], drawAreaBox.getAlarmPointList());
    }

    /**
     * 处理模型配置中的标绘/屏蔽区域、绊线信息
     * <p>
     * 1. 绊线：过滤 code = dict-waterCvModeType 的配置，解析 tripThread 并绘制（无 itemCode/sceneCode 等额外条件）
     * 2. 标绘/屏蔽区域：从字典读取 modelConfig code，匹配配置后处理 pointsRelation：
     * - itemCode 在 subAlarmTypeList 中
     * - 当前时间在 analysisPeriod 的时间区间内
     * - 解析 points 字段 JSON，过滤 sceneCode 等于入参 sceneCode 且 isShow != "0"（为空视为展示）
     * 3. 将符合条件的 points / tripThread 转为 BoxInfo 列表（用于画框/画线）
     * <p>
     * 说明：为提升可读性和可维护性，这里通过 DTO 对象来承载 JSON 结构，
     * 不再直接通过 JsonObject/JsonArray 访问字段。
     *
     * @param modelConfig      大模型配置 JSON 字符串
     * @param subAlarmTypeList 告警子项编码集合
     * @param sceneCode        场景编码
     * @return 符合条件的 BoxInfo 列表
     */
    private List<BoxInfo> dealModelConfigBoxInfo(String modelConfig, List<String> subAlarmTypeList, String sceneCode) {
        if (StringUtils.isEmpty(modelConfig) || CollectionUtils.isEmpty(subAlarmTypeList) || StringUtils.isEmpty(sceneCode)) {
            log.info("dealModelConfigBoxInfo-入参校验未通过,modelConfigEmpty:{},subAlarmTypeListEmpty:{},sceneCodeEmpty:{}",
                    StringUtils.isEmpty(modelConfig), CollectionUtils.isEmpty(subAlarmTypeList), StringUtils.isEmpty(sceneCode));
            return Collections.emptyList();
        }
        log.info("dealModelConfigBoxInfo-开始处理");

        List<BoxInfo> resultBoxList = new ArrayList<>();
        try {
            // 将顶层 modelConfig JSON 数组解析为 DTO 列表
            Type type = GsonUtils.getTypeToken(new TypeToken<List<ModelConfigItemDTO>>() {
            });
            List<ModelConfigItemDTO> configList = GsonUtils.parseObject(modelConfig, type);
            if (CollectionUtils.isEmpty(configList)) {
                log.info("dealModelConfigBoxInfo-modelConfig解析为空,sceneCode:{}", sceneCode);
                return Collections.emptyList();
            }
            List<String> configCodeList = configList.stream()
                    .map(ModelConfigItemDTO::getCode)
                    .collect(Collectors.toList());
            log.info("dealModelConfigBoxInfo-解析完成,sceneCode:{},subAlarmTypeList:{},configCodes:{}",
                    sceneCode, GsonUtils.toJSONString(subAlarmTypeList), GsonUtils.toJSONString(configCodeList));

            // 绊线：从 dict-waterCvModeType 配置读取 tripThread
            ModelConfigItemDTO waterCvModeConfig = configList.stream()
                    .filter(item -> CommonConstant.DICT_CODE_WATER_CVMODETYPE.equals(item.getCode()))
                    .findFirst()
                    .orElse(null);
            if (waterCvModeConfig == null) {
                log.info("dealModelConfigBoxInfo-未找到水利视觉配置,code:{},sceneCode:{}",
                        CommonConstant.DICT_CODE_WATER_CVMODETYPE, sceneCode);
            } else if (CollectionUtils.isEmpty(waterCvModeConfig.getPointsRelation())) {
                log.info("dealModelConfigBoxInfo-水利视觉配置pointsRelation为空,sceneCode:{}", sceneCode);
            } else {
                log.info("dealModelConfigBoxInfo-开始解析绊线,sceneCode:{},pointsRelationSize:{},pointsRelation:{}",
                        sceneCode, waterCvModeConfig.getPointsRelation().size(),
                        GsonUtils.toJSONString(waterCvModeConfig.getPointsRelation()));
                appendTripThreadBoxInfo(resultBoxList, waterCvModeConfig.getPointsRelation());
            }
            log.info("dealModelConfigBoxInfo-绊线解析完成,sceneCode:{},绊线Box数:{}", sceneCode, countTripThreadBox(resultBoxList));

            // 标绘/屏蔽区域：从字典读取 modelConfig code，匹配后处理 points
            List<String> drawAreaConfigCodes = getDrawAreaConfigCodesFromDict();
            log.info("dealModelConfigBoxInfo-标绘/屏蔽区域字典code,sceneCode:{},drawAreaConfigCodes:{}",
                    sceneCode, GsonUtils.toJSONString(drawAreaConfigCodes));
            List<ModelConfigItemDTO> drawAreaConfigList = configList.stream()
                    .filter(item -> item != null && drawAreaConfigCodes.contains(item.getCode()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(drawAreaConfigList)) {
                log.info("dealModelConfigBoxInfo-未匹配到标绘/屏蔽区域配置,sceneCode:{},resultBoxListSize:{}",
                        sceneCode, resultBoxList.size());
                return resultBoxList;
            }

            LocalTime now = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(CommonConstant.HH_MM_SS);

            for (ModelConfigItemDTO drawAreaConfig : drawAreaConfigList) {
                if (drawAreaConfig == null || CollectionUtils.isEmpty(drawAreaConfig.getPointsRelation())) {
                    log.info("dealModelConfigBoxInfo-标绘/屏蔽区域pointsRelation为空,code:{},sceneCode:{}",
                            drawAreaConfig != null ? drawAreaConfig.getCode() : null, sceneCode);
                    continue;
                }
                appendDrawAreaBoxInfo(resultBoxList, drawAreaConfig.getPointsRelation(), subAlarmTypeList, sceneCode, now, timeFormatter);
            }

            log.info("dealModelConfigBoxInfo-处理完成,sceneCode:{},总Box数:{},绊线Box数:{}",
                    sceneCode, resultBoxList.size(), countTripThreadBox(resultBoxList));
            return resultBoxList;
        } catch (Exception e) {
            log.error("dealModelConfigBoxInfo 处理模型配置异常, modelConfig:{}, sceneCode:{}, subAlarmTypeList:{}, 已解析Box数:{}, 异常:{}",
                    modelConfig, sceneCode, GsonUtils.toJSONString(subAlarmTypeList), resultBoxList.size(), e.getMessage(), e);
            return resultBoxList;
        }
    }

    /**
     * 统计绊线 Box 数量
     */
    private long countTripThreadBox(List<BoxInfo> boxList) {
        if (CollectionUtils.isEmpty(boxList)) {
            return 0;
        }
        return boxList.stream()
                .filter(box -> BoxInfo.DRAW_TYPE_TRIP_THREAD.equals(box.getDrawType()))
                .count();
    }

    /**
     * 从字典读取标绘/屏蔽区域对应的 modelConfig code 列表
     */
    private List<String> getDrawAreaConfigCodesFromDict() {
        List<SysDictDataResponse> dictList = warningOrderUtil.getDict(AlarmConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE);
        if (CollectionUtils.isEmpty(dictList)) {
            log.info("标绘/屏蔽区域配置字典为空，dictType:{}, 使用默认值: {}",
                    AlarmConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE, CommonConstant.DICT_CODE_CVMODETYPE);
            return Collections.singletonList(CommonConstant.DICT_CODE_CVMODETYPE);
        }else {
            log.info("标绘/屏蔽区域配置字典: {}", JSON.toJSONString(dictList));
        }
        List<String> configCodes = dictList.stream()
                .map(SysDictDataResponse::getDictValue)
                .filter(v -> !StringUtils.isEmpty(v))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configCodes)) {
            log.info("标绘/屏蔽区域配置字典值为空，dictType:{}, 使用默认值: {}",
                    AlarmConstants.LARGE_MODEL_DRAW_AREA_CONFIG_CODE, CommonConstant.DICT_CODE_CVMODETYPE);
            return Collections.singletonList(CommonConstant.DICT_CODE_CVMODETYPE);
        }
        return configCodes;
    }

    /**
     * 解析标绘/屏蔽区域 points 并追加 BoxInfo
     */
    private void appendDrawAreaBoxInfo(List<BoxInfo> resultBoxList, List<PointsRelationDTO> pointsRelation,
                                       List<String> subAlarmTypeList, String sceneCode,
                                       LocalTime now, DateTimeFormatter timeFormatter) {
        for (PointsRelationDTO relation : pointsRelation) {
            if (relation == null || StringUtils.isEmpty(relation.getItemCode())) {
                continue;
            }
            if (!subAlarmTypeList.contains(relation.getItemCode())) {
                continue;
            }

            // 时间段校验：当前时间必须在任一 analysisPeriod 段内
            if (!StringUtils.isEmpty(relation.getAnalysisPeriod())) {
                List<AnalysisPeriodDTO> periodList = GsonUtils.parseArray(relation.getAnalysisPeriod(), AnalysisPeriodDTO.class);
                if (!isInAnyPeriod(now, periodList, timeFormatter)) {
                    continue;
                }
            }

            if (StringUtils.isEmpty(relation.getPoints())) {
                continue;
            }

            List<ModelConfigPointDTO> points = GsonUtils.parseArray(relation.getPoints(), ModelConfigPointDTO.class);
            if (CollectionUtils.isEmpty(points)) {
                continue;
            }

            // 过滤出当前场景且未隐藏的标绘/屏蔽区域，并转为 BoxInfo
            for (ModelConfigPointDTO pointCfg : points) {
                if (pointCfg == null || (StrUtil.isNotBlank(pointCfg.getSceneCode()) && !StrUtil.contains(pointCfg.getSceneCode(), sceneCode))) {
                    continue;
                }
                // isShow 字段为 "0" 表示不展示；缺失或其他值都认为需要展示
                if (Constants.ZERO.equals(pointCfg.getIsShow())) {
                    continue;
                }
                List<ModelConfigMonitorPointDTO> moniPointList = pointCfg.getMoniPointList();
                if (CollectionUtils.isEmpty(moniPointList)) {
                    continue;
                }

                // 将配置点位转换为 AlarmPoint，用于画框
                List<AlarmPoint> alarmPointList = moniPointList.stream()
                        .map(mp -> {
                            AlarmPoint alarmPoint = new AlarmPoint();
                            alarmPoint.setAlarmPointId(Convert.toInt(mp.getMonitorPointId(), 0));
                            alarmPoint.setPercentX(mp.getPercentX());
                            alarmPoint.setPercentY(mp.getPercentY());
                            return alarmPoint;
                        })
                        .collect(Collectors.toList());

                BoxInfo boxInfo = new BoxInfo();
                boxInfo.setAlarmPointList(alarmPointList);
                // 颜色与粗细：标绘区域/屏蔽区域颜色，默认线宽 3px
                boxInfo.setColor(getColorByMonitorType(pointCfg.getMonitorType()));
                boxInfo.setStrokeWidth(3);
                boxInfo.setMonitorType(pointCfg.getMonitorType());
                resultBoxList.add(boxInfo);
            }
        }
    }

    /**
     * 解析 dict-waterCvModeType 下的 tripThread 并追加绊线 BoxInfo
     * <p>有 tripThread 字段即绘制，不做 itemCode/sceneCode/isShow/时间段 等过滤。</p>
     */
    private void appendTripThreadBoxInfo(List<BoxInfo> resultBoxList, List<PointsRelationDTO> pointsRelation) {
        if (CollectionUtils.isEmpty(pointsRelation)) {
            log.info("appendTripThreadBoxInfo-pointsRelation为空");
            return;
        }
        for (PointsRelationDTO relation : pointsRelation) {
            if (relation == null) {
                log.info("appendTripThreadBoxInfo-跳过空relation");
                continue;
            }
            if (StringUtils.isEmpty(relation.getTripThread())) {
                log.info("appendTripThreadBoxInfo-跳过无tripThread,itemCode:{}", relation.getItemCode());
                continue;
            }
            log.info("appendTripThreadBoxInfo-开始解析tripThread,itemCode:{},tripThread:{}",
                    relation.getItemCode(), relation.getTripThread());
            List<TripThreadVO> tripThreadList = GsonUtils.parseArray(relation.getTripThread(), TripThreadVO.class);
            if (CollectionUtils.isEmpty(tripThreadList)) {
                log.info("appendTripThreadBoxInfo-tripThread解析为空,itemCode:{}", relation.getItemCode());
                continue;
            }
            List<BoxInfo> tripThreadBoxList = convertTripThreadToBoxInfo(tripThreadList);
            log.info("appendTripThreadBoxInfo-解析完成,itemCode:{},tripThreadSize:{},boxSize:{}",
                    relation.getItemCode(), tripThreadList.size(), tripThreadBoxList.size());
            resultBoxList.addAll(tripThreadBoxList);
        }
    }

    /**
     * 将 tripThread 配置转为绊线 BoxInfo 列表
     * <p>绊线由 threadLine（一条线）和 direction（方向箭头）组成，有字段即绘制。</p>
     */
    private List<BoxInfo> convertTripThreadToBoxInfo(List<TripThreadVO> tripThreadList) {
        if (CollectionUtils.isEmpty(tripThreadList)) {
            return Collections.emptyList();
        }
        // 绊线颜色：红色，与前端展示样式一致
        Color tripThreadColor = new Color(237, 81, 88, 255);
        List<BoxInfo> boxInfoList = new ArrayList<>();
        for (TripThreadVO tripThread : tripThreadList) {
            if (tripThread == null || CollectionUtils.isEmpty(tripThread.getThreadLine())
                    || tripThread.getThreadLine().size() < 2) {
                log.info("convertTripThreadToBoxInfo-跳过无效绊线,threadId:{},threadLineSize:{}",
                        tripThread != null ? tripThread.getThreadId() : null,
                        tripThread != null && tripThread.getThreadLine() != null ? tripThread.getThreadLine().size() : 0);
                continue;
            }
            List<AlarmPoint> threadLinePoints = tripThread.getThreadLine().stream()
                    .sorted(Comparator.comparing(t -> Convert.toInt(t.getPointId(), 0)))
                    .map(this::convertThreadLineToAlarmPoint)
                    .collect(Collectors.toList());

            BoxInfo boxInfo = new BoxInfo();
            boxInfo.setDrawType(BoxInfo.DRAW_TYPE_TRIP_THREAD);
            boxInfo.setAlarmPointList(threadLinePoints);
            boxInfo.setColor(tripThreadColor);
            boxInfo.setStrokeWidth(3);

            // direction 可选，存在且至少两个点时绘制方向箭头
            if (!CollectionUtils.isEmpty(tripThread.getDirection()) && tripThread.getDirection().size() >= 2) {
                List<AlarmPoint> directionPoints = tripThread.getDirection().stream()
                        .sorted(Comparator.comparing(d -> Convert.toInt(d.getPointId(), 0)))
                        .map(this::convertDirectionToAlarmPoint)
                        .collect(Collectors.toList());
                boxInfo.setDirectionPointList(directionPoints);
            }
            log.info("convertTripThreadToBoxInfo-生成绊线Box,threadId:{},threadLine:{},direction:{}",
                    tripThread.getThreadId(), GsonUtils.toJSONString(threadLinePoints),
                    GsonUtils.toJSONString(boxInfo.getDirectionPointList()));
            boxInfoList.add(boxInfo);
        }
        return boxInfoList;
    }

    /**
     * 绊线端点转 AlarmPoint
     */
    private AlarmPoint convertThreadLineToAlarmPoint(ThreadLineVO threadLine) {
        AlarmPoint alarmPoint = new AlarmPoint();
        alarmPoint.setAlarmPointId(Convert.toInt(threadLine.getPointId(), 0));
        alarmPoint.setPercentX(threadLine.getPercentX() != null ? threadLine.getPercentX().doubleValue() : 0D);
        alarmPoint.setPercentY(threadLine.getPercentY() != null ? threadLine.getPercentY().doubleValue() : 0D);
        return alarmPoint;
    }

    /**
     * 绊线方向点转 AlarmPoint
     */
    private AlarmPoint convertDirectionToAlarmPoint(DirectionVO direction) {
        AlarmPoint alarmPoint = new AlarmPoint();
        alarmPoint.setAlarmPointId(Convert.toInt(direction.getPointId(), 0));
        alarmPoint.setPercentX(direction.getPercentX() != null ? direction.getPercentX().doubleValue() : 0D);
        alarmPoint.setPercentY(direction.getPercentY() != null ? direction.getPercentY().doubleValue() : 0D);
        return alarmPoint;
    }

    /**
     * 判断当前时间是否落在任一时间段内
     *
     * @param now        当前时间
     * @param periodList 分析时间段列表
     * @param formatter  时间格式
     * @return true 表示在任一时间段内
     */
    private boolean isInAnyPeriod(LocalTime now, List<AnalysisPeriodDTO> periodList, DateTimeFormatter formatter) {
        if (CollectionUtils.isEmpty(periodList)) {
            return true;
        }
        for (AnalysisPeriodDTO period : periodList) {
            if (period == null || StringUtils.isEmpty(period.getStartTime()) || StringUtils.isEmpty(period.getEndTime())) {
                continue;
            }
            LocalTime start = LocalTime.parse(period.getStartTime(), formatter);
            LocalTime end = LocalTime.parse(period.getEndTime(), formatter);
            if (!now.isBefore(start) && !now.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据 monitorType 获取对应颜色
     * monitorType=0 标绘区域（蓝色）："rgba(79,159,255,1)"
     * monitorType=1 屏蔽区域（红色）："rgba(237,81,88,1)"
     */
    private Color getColorByMonitorType(Integer monitorType) {
        if (monitorType != null && monitorType == 1) {
            // 屏蔽区域框：红色
            return new Color(237, 81, 88, 255);
        }
        // 默认标绘区域框：蓝色
        return new Color(79, 159, 255, 255);
    }

    /**
     * 判断是否为标绘区域框（绊线等非多边形类型不参与）
     */
    private boolean isDrawAreaBox(BoxInfo boxInfo) {
        if (boxInfo == null || BoxInfo.DRAW_TYPE_TRIP_THREAD.equals(boxInfo.getDrawType())) {
            return false;
        }
        return boxInfo.getMonitorType() != null && boxInfo.getMonitorType() == 0;
    }

    /**
     * 判断告警框中心点是否落在任一标绘区域多边形内
     */
    private boolean isBoxCenterInAnyPolygon(BoxInfo alarmBox, List<BoxInfo> drawAreaBoxList) {
        if (alarmBox == null || CollectionUtils.isEmpty(drawAreaBoxList)) {
            return false;
        }
        double[] center = getBoxCenter(alarmBox);
        if (center == null) {
            return false;
        }
        double x = center[0];
        double y = center[1];
        for (BoxInfo polygonBox : drawAreaBoxList) {
            if (polygonBox == null || CollectionUtils.isEmpty(polygonBox.getAlarmPointList())) {
                continue;
            }
            if (isPointInPolygon(x, y, polygonBox.getAlarmPointList())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取告警框中心点（percentX/percentY）
     * <p>以点集的外接矩形中心作为框中心。</p>
     */
    private double[] getBoxCenter(BoxInfo boxInfo) {
        if (boxInfo == null || CollectionUtils.isEmpty(boxInfo.getAlarmPointList())) {
            return null;
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (AlarmPoint p : boxInfo.getAlarmPointList()) {
            if (p == null) {
                continue;
            }
            double x = p.getPercentX();
            double y = p.getPercentY();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        if (minX == Double.MAX_VALUE || minY == Double.MAX_VALUE) {
            return null;
        }
        return new double[]{(minX + maxX) / 2.0, (minY + maxY) / 2.0};
    }

    /**
     * 射线法判断点是否在多边形内
     */
    private boolean isPointInPolygon(double x, double y, List<AlarmPoint> polygon) {
        if (CollectionUtils.isEmpty(polygon) || polygon.size() < 3) {
            return false;
        }
        boolean inside = false;
        int n = polygon.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            AlarmPoint pi = polygon.get(i);
            AlarmPoint pj = polygon.get(j);
            if (pi == null || pj == null) {
                continue;
            }
            double xi = pi.getPercentX();
            double yi = pi.getPercentY();
            double xj = pj.getPercentX();
            double yj = pj.getPercentY();
            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi + 0.0) + xi);
            if (intersect) {
                inside = !inside;
            }
        }
        return inside;
    }


    private static List<ReceiveAlarmVO> parseExtendField(Object extendField) {
        List<ReceiveAlarmVO> result = new ArrayList<>();

        JsonArray extendFieldJsonArray = GsonUtils.parseJsonArray(GsonUtils.toJSONString(extendField));
        extendFieldJsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray attrList = jsonObject.get("attrList").getAsJsonArray();

            attrList.forEach(attrJsonElement -> {
                JsonObject attrJsonObject = attrJsonElement.getAsJsonObject();

                String attrValueString = attrJsonObject.get("attrValue").getAsString();

                // 使用 TypeToken 指定完整的泛型类型
                List<ReceiveAlarmVO> receiveAlarmVOS = GsonUtils.parseArray(attrValueString, ReceiveAlarmVO.class);

                result.addAll(receiveAlarmVOS);
            });
        });
        return result;
    }

    /**
     * 将告警区域列表转换为画框信息列表
     * 每个告警区域对应一个BoxInfo，包含该区域的所有告警点
     *
     * @param alarmAreaList 告警区域列表
     * @return 画框信息列表
     */
    private List<BoxInfo> dealBoxInfo(List<AlarmOriginalAreaVO> alarmAreaList) {
        if (CollectionUtils.isEmpty(alarmAreaList)) {
            return new ArrayList<>();
        }

        List<BoxInfo> boxInfoList = new ArrayList<>();

        for (AlarmOriginalAreaVO area : alarmAreaList) {
            List<AlarmOriginalPointVO> pointList = area.getAlarmPointList();
            if (CollectionUtils.isEmpty(pointList)) {
                continue;
            }

            // 将告警点转换为AlarmPoint列表，直接使用字段赋值转换
            List<AlarmPoint> alarmPointList = pointList.stream()
                    .map(point -> {
                        AlarmPoint alarmPoint = new AlarmPoint();
                        alarmPoint.setAlarmPointId(Convert.toInt(point.getAlarmPointId(), 0));
                        alarmPoint.setPercentX(Convert.toDouble(point.getPercentX(), 0.0));
                        alarmPoint.setPercentY(Convert.toDouble(point.getPercentY(), 0.0));
                        return alarmPoint;
                    })
                    .collect(Collectors.toList());

            // 创建BoxInfo对象
            BoxInfo boxInfo = new BoxInfo();
            boxInfo.setAlarmPointList(alarmPointList);
            // 设置框内展示文本：包含目标类别与置信度信息
            StringBuilder labelBuilder = new StringBuilder();
            if (area.getConfidenceLevel() != null) {
                // 置信度保留两位小数，四舍五入
                BigDecimal confidence = new BigDecimal(area.getConfidenceLevel())
                        .setScale(2, RoundingMode.HALF_UP);
                labelBuilder.append(confidence.toPlainString());
            }
            if (!StringUtils.isEmpty(area.getClassName())) {
                labelBuilder.append("  ").append(area.getClassName());
            }
            boxInfo.setLabel(labelBuilder.toString());
            boxInfoList.add(boxInfo);
        }

        return boxInfoList;
    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        String taskId = receiveAlarmVO.getTaskId();
        String deviceCode = receiveAlarmVO.getDeviceCode();
        String algorithmCode = receiveAlarmVO.getAlgorithmCode();
        String alarmSource = receiveAlarmVO.getAlarmSource();
        String localNode = receiveAlarmVO.getLocalNode();
        if (!StringUtils.isEmpty(receiveAlarmVO.getTenantId())) {
            OrderCustomerVO customerVO = new OrderCustomerVO();
            List<OrderCustomerVO> customers = new ArrayList<>();
            customerVO.setTenantId(receiveAlarmVO.getTenantId());
            customerVO.setTenantName(receiveAlarmVO.getTenantName());
            customerVO.setIndustryCode(receiveAlarmVO.getIndustryCode());
            customerVO.setAppVerCode(receiveAlarmVO.getAppVerCode());
            customers.add(customerVO);
            return customers;
        }
        // 根据运管推送taskId过滤租户,否则，按照配置的设备、算法过滤
        if (!StringUtils.isEmpty(taskId)) {
            return deviceRepository.getCustomersList(null, null, taskId, alarmSource, localNode);
        } else if (!StringUtils.isEmpty(deviceCode) && !StringUtils.isEmpty(algorithmCode)) {
            List<OrderCustomerVO> customers = deviceRepository.getCustomersList(deviceCode, algorithmCode, null, alarmSource, localNode);
            if (customers.isEmpty()) {
                log.error("---------AI告警查询error---------,algorithmCode:{}", algorithmCode);
            }
            return customers;
        }
        return Collections.emptyList();
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        this.setAlarmLocationAndAddress(receiveAlarmVO);
    }


    /**
     * 处理摄像机告警工单，包括告警压制、运营研判等操作。
     *
     * @param receiveAlarmDTO 包含告警数据和相关信息的传输对象
     * @return 处理后的告警对象列表
     */
    private List<ReceiveAlarmVO> aiWarningOrderHandler(ReceiveAlarmDTO receiveAlarmDTO) {
        log.info("生成AI告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        List<ReceiveAlarmVO> successDatas = new ArrayList<>();
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            String alarmTime = receiveAlarmDTO.getReceiveAlarmVO().getAlarmTime();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            // 运营研判告警集合
            List<ReceiveAlarmVO> operations = new ArrayList<>();
            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();

            // 告警压制开关是否开启
            String isIntercept = warningOrderUtil.getInterceptSwitch();
            List<String> tenantIdList = customers.stream().map(OrderCustomerVO::getTenantId).collect(Collectors.toList());
            //联合告警压制需求代码合并
            Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap = getAlarmSuppressStrategy(receiveAlarmVO, customers, isIntercept);
            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = warningOrderUtil.getDict(Constants.APP_VER_CODE_TOPIC);
            //先把涉及的租户的雨天告警压制配置数据查询出来,防止后面循环查询
            Map<String, AlarmSuppressConfigDTO> configDTOMap = getAlarmSuppressConfig(customers);

            for (OrderCustomerVO customerObj : customers) {
                String[] appVers = customerObj.getAppVerCode().split(",");
                for (String appVer : appVers) {
                    //构建告警对象
                    ReceiveAlarmVO alarmVo = buildAlarmVo(receiveAlarmVO, customerObj, appVer);

                    //处理特殊行业版本
                    if (warningOrderUtil.judgeSpecialAppVer(dictList, alarmVo)) {
                        continue;
                    }

                    //判断并处理告警压制
                    if (this.judgeIntercept(alarmVo, isIntercept, strategyMap, configDTOMap, intercepts) && !Constants.ZERO.equals(alarmVo.getIsPresetMakeAlarm())) {
                        continue;
                    }

                    //处理运营研判,首先判断是否开启研判，并行研判
                    if (warningOrderUtil.doOperate(alarmVo, alarms, operations) && !Constants.ZERO.equals(alarmVo.getIsPresetMakeAlarm())) {
                        continue;
                    }
                    if (alarmVo != null && StringUtils.isEmpty(alarmVo.getJudgeType())) {
                        alarmVo.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                    }
                    alarms.add(alarmVo);
                }
            }
            receiveAlarmVODB.setIntercepts(intercepts);
            // 批量保存告警信息
            successDatas = batchCreateWarningOrder(alarms, receiveAlarmVODB);
            receiveAlarmVODB.setList(alarms);
            receiveAlarmVODB.setOperations(operations);
            receiveAlarmDTO.setList(alarms);
            receiveAlarmDTO.setOperations(operations);
            receiveAlarmDTO.setIntercepts(intercepts);
            String originalJson = JSON.toJSONString(receiveAlarmVODB.getList());
            // 告警信息入库
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, Constants.ONE, originalJson, receiveAlarmDTO.getAlarmPointList());
            // ###5、推送告警消息
            try {
                commonService.batchPushMessage(alarms);
                commonService.batchPushMessage(operations);
            } catch (Exception e) {
                log.error("批量推送告警消息时发生异常：" + e);
            }
            // 回填研判信息
            successDatas.addAll(operations);
        } catch (Exception e) {
            log.info("生成AI告警事件异常：" + e);
        }
        return successDatas;
    }


    private void aiPipelineHandler(ReceiveAlarmDTO receiveAlarmDTO) {
        // 批量保存告警信息
        try {
            if ("1".equals(receiveAlarmDTO.getReceiveAlarmVO().getAlarmSource())) {
                List<ReceiveAlarmVO> alarms = receiveAlarmDTO.getList();
                List<ReceiveAlarmVO> operations = receiveAlarmDTO.getOperations();
                List<ReceiveAlarmVO> intercepts = receiveAlarmDTO.getIntercepts();
                List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();
                String pipelines = "";
                if (!CollectionUtils.isEmpty(customers)) {
                    pipelines = customers.get(0).getPipelines();
                }
                if (!StringUtils.isEmpty(pipelines) && !"[]".equals(pipelines)) {
                    HandleAlarmImgDTO handleAlarmImgDTO = new HandleAlarmImgDTO();
                    handleAlarmImgDTO.setPipelines(pipelines);
                    handleAlarmImgDTO.setListRemark(false);
                    handleAlarmImgDTO.setInterceptsRemark(false);
                    handleAlarmImgDTO.setOperationsRemark(false);
                    if (!CollectionUtils.isEmpty(alarms)) {
                        handleAlarmImgDTO.setListRemark(true);
                        this.setHandleImg(alarms, handleAlarmImgDTO);
                    }
                    if (!CollectionUtils.isEmpty(operations)) {
                        handleAlarmImgDTO.setOperationsRemark(true);
                        this.setHandleImg(operations, handleAlarmImgDTO);
                    }
                    if (!CollectionUtils.isEmpty(intercepts)) {
                        handleAlarmImgDTO.setInterceptsRemark(true);
                        this.setHandleImg(intercepts, handleAlarmImgDTO);
                    }
                    log.info("开始调用api-export AI生成告警后原图/框图复合油管线图处理：{}", JSON.toJSONString(handleAlarmImgDTO));
                    Function<HandleAlarmImgDTO, HandleAlarmImgDTO> fun1 = this::handleReceiveAlarmImg;
                    Map<Function, Object> paramMap = new ConcurrentHashMap<>();
                    paramMap.put(fun1, handleAlarmImgDTO);
                    List<Function> listFunc = Arrays.asList(fun1);
                    ThreadParallelUtil.run(listFunc, f -> {
                        Object o = paramMap.get(f);
                        return f.apply(o);
                    }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
                }
            }
        } catch (Exception e) {
            log.error("AI生成告警后原图/框图复合油管线图失败!", e);
        }
    }

    public HandleAlarmImgDTO handleReceiveAlarmImg(HandleAlarmImgDTO handleAlarmImgDTO) {
        iTradeExportWarningService.handleReceiveAlarmImg(handleAlarmImgDTO);
        return handleAlarmImgDTO;
    }

    public void setHandleImg(List<ReceiveAlarmVO> list, HandleAlarmImgDTO handleAlarmImgDTO) {
        handleAlarmImgDTO.setFileImgUrlSrc(list.get(0).getOriginalPictureUrl());
        handleAlarmImgDTO.setFileImgUrlIcon(list.get(0).getAlarmPictureUrl());
        handleAlarmImgDTO.setWarningOrderId(list.get(0).getWarningOrderId());
    }

    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms, ReceiveAlarmDTO receiveAlarmVODB) {
        List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarms)) {
            log.info("批量保存告警信息入参：{}", JSON.toJSONString(alarms));
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms, recordList);
          /*  alarms.forEach(item -> {
                if(!WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(item.getAlarmSource())){
                    setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                }
            });*/
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            receiveAlarmVODB.setAlarmPointList(alarmPointList);
            return alarms;
        }
        return new ArrayList<>();
    }

    /**
     * 构建告警对象
     *
     * @param receiveAlarmVO
     * @param customerObj
     * @param appVer
     * @return
     */
    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, OrderCustomerVO customerObj, String appVer) {
        ReceiveAlarmVO alarmVo = new ReceiveAlarmVO();
        customerObj.setDeviceCode(receiveAlarmVO.getDeviceCode());
        BeanUtils.copyProperties(receiveAlarmVO, alarmVo);
        BeanUtils.copyProperties(customerObj, alarmVo);
        alarmVo.setOperationNode(customerObj.getOperationNode());
        alarmVo.setParallelOperationNode(customerObj.getParallelOperationNode());
        alarmVo.setAlarmNode(getPlatformNode());
        alarmVo.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(alarmVo.getAlarmNode()));
        if (!StringUtils.isEmpty(customerObj.getOperationDept())) {
            alarmVo.setOperationDept(customerObj.getOperationDept() + Constants.COMMA);
        }
        if (!StringUtils.isEmpty(customerObj.getParallelOperationDept())) {
            alarmVo.setParallelOperationDept(customerObj.getParallelOperationDept() + Constants.COMMA);
        }
        //更改工单编码生成规则 add by hued 20211103
        alarmVo.setWarningOrderId(IPKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime()));
        alarmVo.setAppVerCode(appVer);
        alarmVo.setNotifyType(Constants.YES_FLAG);
        // 获取雷达告警和AI告警和摄像机告警的透传字段  报文中透传字段按照接口文档传json字符串
        if (alarmVo.getExtendField() != null && !Constants.ONE_BLANK.equals(JSON.toJSONString(alarmVo.getExtendField()))
                && !Constants.TWO_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))) {
            alarmVo.setExtraField(JSON.toJSONString(alarmVo.getExtendField()));
        }
        // 预置位定位法>特征库定位法>摄像机标定定位法>定位到摄像机  判断是否符合预置位定位
        if (!StringUtils.isEmpty(alarmVo.getPoints()) && Constants.ONE.equals(alarmVo.getAlarmSource())) {
            dealPresetLocation(alarmVo);
        }

        //二次研判转化,去除原有判定逻辑，若没有上报是否二次研判，则默认二次研判为否。
        if (StringUtils.isEmpty(alarmVo.getIsSecondJudge()) && Constants.FOUR.equals(alarmVo.getAlarmSource())) {
            alarmVo.setIsSecondJudge(Constants.ZERO);
        }
        return alarmVo;
    }

    /**
     * 获取当前环境节点
     *
     * @return
     */
    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if (Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {
            JsonObject jsonObject = GsonUtils.parseJsonObject(GsonUtils.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            if (jsonObject != null && jsonObject.has(Constants.PARAM_CONFIG_VALUE) && !jsonObject.get(Constants.PARAM_CONFIG_VALUE).isJsonNull()) {
                sdhNode = jsonObject.get(Constants.PARAM_CONFIG_VALUE).getAsString();
            }
        }
        return sdhNode;
    }


    /**
     * 处理每个租户对该算法配置的预置位定位
     *
     * @param alarmVo
     */
    private void dealPresetLocation(ReceiveAlarmVO alarmVo) {
        try {
            List<AlgorithmMonitorAreaVO> areaVOList = GsonUtils.parseArray(alarmVo.getPoints(), AlgorithmMonitorAreaVO.class);
            //如果算法任务只有一个电子围栏并且设置了定位点，则定位到定位点，否则预置位定位法不生效。
            if (!CollectionUtils.isEmpty(areaVOList) && areaVOList.size() == 1 && ObjectUtil.isNotNull(areaVOList.get(0).getLatitude()) && ObjectUtil.isNotNull(areaVOList.get(0).getLongitude())) {
                alarmVo.setLongitude(areaVOList.get(0).getLongitude());
                alarmVo.setLatitude(areaVOList.get(0).getLatitude());
                // 通过经纬度重新获取街道信息以及地址
                gisUtil.getAddressFromAmap(alarmVo, null);
            }
        } catch (Exception e) {
            log.error("预置位定位异常: 入参：{} , 异常信息:{}", JSON.toJSONString(alarmVo), e);
        }
    }

    /**
     * 获取联合告警压制策略
     *
     * @return
     */
    private Map<String, List<VmAlarmSuppressStrategyVO>> getAlarmSuppressStrategy(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customers, String isIntercept) {
        // 告警压制开关是否开启
        if (Constants.YES_FLAG.equals(isIntercept)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyVoList = strategyService.queryStrategyList(receiveAlarmVO, customers);
            if (strategyVoList != null && !strategyVoList.isEmpty()) {
                return strategyVoList.stream().filter(item -> item.getTenantId() != null && !"".equals(item.getTenantId())).collect(Collectors.groupingBy(temp -> temp.getTenantId() + temp.getIndustryCode()));
            }
        }
        return null;
    }

    /**
     * 获取告警压制配置
     *
     * @param customers
     * @return
     */
    private Map<String, AlarmSuppressConfigDTO> getAlarmSuppressConfig(List<OrderCustomerVO> customers) {
        List<String> tenantIdsList = customers.stream().collect(Collectors.mapping(OrderCustomerVO::getTenantId, Collectors.toList()));
        if (!CollectionUtils.isEmpty(tenantIdsList)) {
            List<AlarmSuppressConfigDTO> configDTOList = configRepository.querySuppressConfigByTenantIds(tenantIdsList);
            return configDTOList.stream().collect(Collectors.toMap(k -> k.getTenantId() + "_" + k.getDeviceCode(), configDTO -> configDTO));
        }
        return null;
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.AI_ALARM.getAlarmStrategyBean();
    }
}
