package com.chinatower.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chinatower.bdm.api.entity.GridListVO;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.layer.GeometrysReq;
import com.chinatower.gis.api.entity.layer.GeometrysRes;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.dao.*;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelSubVO;
import com.chinatower.platform.entity.devicemgmt.vo.TowerDeviceLabelVO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.modules.*;
import com.chinatower.platform.service.SynchLocalizationAlarmService;
import com.chinatower.platform.utils.AngleUtils;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.chinatower.device.api.ITowerDeviceService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.event.constants.Constants.*;

/**
 * @Name SynchLocalizationAlarmServiceImpl
 * @Description 属地化下沉节点告警数据同步
 * @Author lyy
 * @Date 2023/10/30
 * @version v1.6.30
 */
@Service
@Slf4j
public class SynchLocalizationAlarmServiceImpl implements SynchLocalizationAlarmService {

    @Autowired
    private SynchLocalizationAlarmDAO localizationAlarmDAO;

    @Autowired
    private LocalizationAlarmDetailedInfoDAO alarmDetailedInfoDAO;

    @Autowired
    private GisLayerAPI gisLayerAPI;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private MongoUtilDAO mongoUtilDAO;

    @Autowired
    private SinkingNodeAlarmMongoDAO sinkingNodeAlarmMongoDAO;

    @Autowired
    private SinkingNodeOperateMongoDAO sinkingNodeOperateMongoDAO;

    @Autowired
    private SinkingNodeInterceptMongoDAO sinkingNodeInterceptMongoDAO;

    @Autowired
    private SynchLocalizationAlarmDAO synchLocalizationAlarmDAO;

    private static final String YYYYMMDD_FORMAT = "yyyyMMdd";
    //告警类型（耕地卫片执法：4110101  矿产卫片执法：4110102  非农化卫片执法：4110103  非粮化卫片执法：4110104）
    public static final String[] WPZF_WARNING_TYPE = {"4110101","4110102","4110103","4110104"};

    private static final String EXCEL_SHEET_NAME = "告警工单数据";

    private static final int PAGE_SIZE = 10000;

    @Override
    public void excuteHandler(String param) {
        /**
         * 同步前，全量查询属地化下沉节点告警相关基础信息
         * 比如：告警来源、告警类型、告警状态、网格信息、图斑信息、摄像机信息、物联信息、雷达信息、告警等级等...
         * 后续根据下沉节点工单中对应的字段进行匹配对应
         */
        log.info("====== 全量查询属地化下沉节点告警基础信息开始  ======");
        AlarmOrderInfoParam alarmOrderInfoParam = new AlarmOrderInfoParam();
        List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25);
        ThreadParallelUtil.run(paramList, o->{
            this.alarmOrderInfoAll(o,alarmOrderInfoParam);
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
        log.info("====== 全量查询属地化下沉节点告警基础信息结束  ======");

        /**
         * 对属地化下沉节点告警数据进行异步处理：
         * 涉及：工单表 vm_warning_order_t_YYYYMM、工单流程记录表 vm_order_flow_record_t_YYYYMM
         *      回收站表 vm_warning_intercept_t、运营研判表 vm_alarm_operate_t
         * 1、根据更新时间为当前时间，查询当月和上月，当天所有更新的工单表/工单流程表所有数据，如果更新时间为空，以创建时间为当前时间
         * 2、将查询到的数据进行整合，按节点+天+表名输出excel文件上传属地化ofs
         * 3、上传ofs结束后，各个下沉节点将当日同步的数据保存至对应下沉节点mongo库中
         */
        Map<Function,Object> paramMap = new ConcurrentHashMap<>();
        alarmOrderInfoParam.setDateParam(param);
        Function<AlarmOrderInfoParam, Result> fun1 = this :: getMergeOrderList;
        paramMap.put(fun1, alarmOrderInfoParam);
        Function<AlarmOrderInfoParam, Result> fun2 = this :: getMergeOperateList;
        paramMap.put(fun2, alarmOrderInfoParam);
        Function<AlarmOrderInfoParam, Result> fun3 = this :: getMergeInterceptList;
        paramMap.put(fun3, alarmOrderInfoParam);
        List<Function> listFunc=Arrays.asList(fun1,fun2,fun3);
        ThreadParallelUtil.run(listFunc,f->{
            Object o =  paramMap.get(f);
            return f.apply(o);
        }, ThreadParallelUtil.ConfigFactory.WARNING_ORDER);
    }

    /**
     * 1、工单以及工单流转数据处理
     * @param alarmOrderInfoParam
     * @return
     */
    private Result getMergeOrderList(AlarmOrderInfoParam alarmOrderInfoParam) {
        // 1、查询工单表、工单流转表符合条件的数据，并进行整合
        Date date = this.getDateMethod(alarmOrderInfoParam);
        String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(date));
        String excelFileName = getExcelFileName(Constants.WARN_ORDER_TEBLE, date);
        String tempFilePath = System.getProperty(Constants.TMP_DIR)+ File.separator  + excelFileName;
        //初始化Mongo集合
        String collectionName = Constants.WARN_ORDER_TEBLE_ + DateUtils.date2Str(date, YYYYMMDD_FORMAT);
        Boolean exists = sinkingNodeAlarmMongoDAO.collectionExists(collectionName, MongoDBType.PRIMARY);
        if(!exists){
            sinkingNodeAlarmMongoDAO.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
        }

        try (ExcelWriter excelWriter = EasyExcel.write(tempFilePath,  WarningOrderEntity.class).build())  {
            WriteSheet writeSheet = EasyExcel.writerSheet(EXCEL_SHEET_NAME).build();
            AtomicInteger pageNum = new AtomicInteger(1);
            while (true) {
                // 1. 分页查询数据
                int offset = (pageNum.get()  - 1) * PAGE_SIZE;
                List<WarningOrderEntity> mergedOrderList = mergeOrderLists(date, lastMonth, offset, PAGE_SIZE);
                if (CollectionUtils.isEmpty(mergedOrderList))  {
                    break;
                }
                // 2. 处理工单ID列表
                List<String> orderIdList = mergedOrderList.stream()
                        .map(WarningOrderEntity::getWarningOrderId)
                        .filter(warningOrderId -> !StringUtils.isEmpty(warningOrderId))
                        .distinct()
                        .collect(Collectors.toList());

                // 3. 查询并处理流转记录
                List<OrderFlowRecordEntity> mergedRecordList = mergeRecordLists(date, lastMonth, orderIdList);
                Map<String, List<OrderFlowRecordEntity>> recordMap = mergedRecordList.stream()
                        .collect(Collectors.groupingBy(OrderFlowRecordEntity::getOrderId));

                // 4. 异步处理数据
                processMergedOrderListAsync(mergedOrderList, recordMap, alarmOrderInfoParam);
                log.info("getMergeOrderList 当前写入数据行数: {}, sheetName: {}", mergedOrderList.size(), writeSheet.getSheetName());
                // 5. 分批写入Excel
                excelWriter.write(mergedOrderList,  writeSheet);
                // 6. 日志记录和内存清理
                log.info("getMergeOrderList 当前写入批次: {}, 文件大小: {} KB", pageNum.get(),
                        Files.exists(Paths.get(tempFilePath))  ? Files.size(Paths.get(tempFilePath))  / 1024 : 0);
                pageNum.getAndIncrement();
                // 7.将当日同步的数据保存至对应下沉节点mongo库
                log.info("批量保存告警工单信息到mongo库入参：{}", mergedOrderList.size());
                //获取所有的工单ID,根据工单ID先删后增
                List<List<WarningOrderEntity>> orderList = Lists.partition(mergedOrderList,Constants.FIVE_THOUSAND);
                orderList.forEach(item -> {
                    List<String> orderIds = item.stream().map(WarningOrderEntity::getWarningOrderId).collect(Collectors.toList());
                    Criteria criteria = Criteria.where(Constants.PARAM_WARN_ORDER_ID).in(orderIds);
                    Query query = Query.query(criteria);
                    sinkingNodeAlarmMongoDAO.remove(query, MongoDBType.PRIMARY, collectionName);
                    sinkingNodeAlarmMongoDAO.insertAll(item, collectionName, MongoDBType.PRIMARY);
                });
                mergedOrderList = null;
            }
            excelWriter.finish();
            // 上传到OFS
            log.info("getMergeOrderList 上传到OFS文件大小: {} KB",
                    Files.exists(Paths.get(tempFilePath))  ? Files.size(Paths.get(tempFilePath))  / 1024 : 0);
            uploadToOFS(excelFileName, Files.readAllBytes(Paths.get(tempFilePath)));

        } catch (Exception e) {
            log.error("******getMergeOrderList******Failed to upload Excel file to OFS:", e);
        } finally {
            // 删除临时文件
            try {
                Files.deleteIfExists(Paths.get(tempFilePath));
            } catch (IOException e) {
                log.warn(Constants.DELETE_TMP_FILE_ERROR, e);
            }
        }
        return Result.success("");
    }

    /**
     * 2、运营研判告警数据处理
     * @param alarmOrderInfoParam
     * @return
     */
    private Result getMergeOperateList(AlarmOrderInfoParam alarmOrderInfoParam) {
        // 1、查询运营研判符合条件数据，并进行整合
        Date date = this.getDateMethod(alarmOrderInfoParam);
        String fileName = getExcelFileName(Constants.WARN_ALARM_OPERATE, date);
        String tempFilePath = System.getProperty(Constants.TMP_DIR)+ File.separator  + fileName;
        //初始化Mongo集合
        String collectionName = Constants.WARN_ALARM_OPERATE_ + DateUtils.date2Str(date, YYYYMMDD_FORMAT);
        Boolean exists = sinkingNodeOperateMongoDAO.collectionExists(collectionName, MongoDBType.PRIMARY);
        if(!exists){
            sinkingNodeOperateMongoDAO.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
        }

        try (ExcelWriter excelWriter = EasyExcel.write(tempFilePath,  OperateOrderEntity.class).build())  {
            WriteSheet writeSheet = EasyExcel.writerSheet(EXCEL_SHEET_NAME).build();
            AtomicInteger pageNum = new AtomicInteger(1);
            while (true) {
                // 1. 分页查询数据
                int offset = (pageNum.get() - 1) * PAGE_SIZE;
                List<OperateOrderEntity> mergedOperateList = localizationAlarmDAO.queryOperateOrderList(date, offset, PAGE_SIZE);
                if (CollectionUtils.isEmpty(mergedOperateList))  {
                    break;
                }
                // 2、异步处理 mergedOperateList
                processMergedOperateListAsync(mergedOperateList, alarmOrderInfoParam);
                log.info("getMergeOperateList 当前写入数据行数: {}, sheetName: {}", mergedOperateList.size(), writeSheet.getSheetName());
                // 3. 分批写入Excel
                excelWriter.write(mergedOperateList,  writeSheet);
                // 4. 日志记录和内存清理
                log.info("getMergeOperateList 当前写入批次: {}, 文件大小: {} KB", pageNum.get(),
                        Files.exists(Paths.get(tempFilePath))  ? Files.size(Paths.get(tempFilePath))  / 1024 : 0);
                pageNum.getAndIncrement();
                // 5.将当日同步的数据保存至对应下沉节点mongo库
                log.info("批量保存运营研判告警信息到mongo库入参：{}", mergedOperateList.size());
                //获取所有的工单ID,根据工单ID先删后增
                List<List<OperateOrderEntity>> operateList = Lists.partition(mergedOperateList,Constants.FIVE_THOUSAND);
                operateList.forEach(item -> {
                    List<String> orderIds = item.stream().map(OperateOrderEntity::getWarningOrderId).collect(Collectors.toList());
                    Criteria criteria = Criteria.where(Constants.PARAM_WARN_ORDER_ID).in(orderIds);
                    Query query = Query.query(criteria);
                    sinkingNodeOperateMongoDAO.remove(query, MongoDBType.PRIMARY, collectionName);
                    sinkingNodeOperateMongoDAO.insertAll(item, collectionName, MongoDBType.PRIMARY);
                });
                log.info("异步处理运营研判告警mergedOperateList结果：{}", mergedOperateList.size());
                mergedOperateList = null;
            }
            excelWriter.finish();
            // 上传到OFS
            uploadToOFS(fileName, Files.readAllBytes(Paths.get(tempFilePath)));

        } catch (Exception e) {
            log.error("******getMergeOperateList******Failed to upload Excel file to OFS:", e);
        } finally {
            // 删除临时文件
            try {
                Files.deleteIfExists(Paths.get(tempFilePath));
            } catch (IOException e) {
                log.warn(Constants.DELETE_TMP_FILE_ERROR, e);
            }
        }
        return Result.success("");
    }

    /**
     * 3、回收站告警数据处理
     * @param alarmOrderInfoParam
     * @return
     */
    private Result getMergeInterceptList(AlarmOrderInfoParam alarmOrderInfoParam) {
        Date date = this.getDateMethod(alarmOrderInfoParam);
        String fileName = getExcelFileName(Constants.WARN_ALARM_INTERCEPT, date);
        String tempFilePath = System.getProperty(Constants.TMP_DIR)+ File.separator + fileName;
        //初始化Mongo集合
        String collectionName = Constants.WARN_ALARM_INTERCEPT_ + DateUtils.date2Str(date, YYYYMMDD_FORMAT);
        Boolean exists = sinkingNodeInterceptMongoDAO.collectionExists(collectionName, MongoDBType.PRIMARY);
        if(!exists){
            sinkingNodeInterceptMongoDAO.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
        }

        try (ExcelWriter excelWriter = EasyExcel.write(tempFilePath,  InterceptOrderEntity.class).build())  {
            WriteSheet writeSheet = EasyExcel.writerSheet(EXCEL_SHEET_NAME).build();
            AtomicInteger pageNum = new AtomicInteger(1);
            while (true) {
                int offset = (pageNum.get() - 1) * PAGE_SIZE;
                // 1、查询回收站表符合条件分页数据，并进行整合
                List<InterceptOrderEntity> mergedInterceptList = localizationAlarmDAO.queryInterceptOrderList(date, offset, PAGE_SIZE);
                if (CollectionUtils.isEmpty(mergedInterceptList))  {
                    break;
                }
                // 2、异步处理 mergedInterceptList
                processMergedInterceptListAsync(mergedInterceptList, alarmOrderInfoParam);
                log.info("getMergeInterceptList 当前写入数据行数: {}, sheetName: {}", mergedInterceptList.size(), writeSheet.getSheetName());
                // 3. 分批写入Excel
                excelWriter.write(mergedInterceptList,  writeSheet);
                // 4. 日志记录和内存清理
                log.info("getMergeInterceptList 当前写入批次: {}, 文件大小: {} KB", pageNum.get(),
                        Files.exists(Paths.get(tempFilePath))  ? Files.size(Paths.get(tempFilePath))  / 1024 : 0);
                pageNum.getAndIncrement();
                // 5.将当日同步的数据保存至对应下沉节点mongo库
                log.info("批量保存回收站告警信息到mongo库入参：{}", mergedInterceptList.size());
                //获取所有的工单ID,根据工单ID先删后增
                List<List<InterceptOrderEntity>> interceptList = Lists.partition(mergedInterceptList,Constants.FIVE_THOUSAND);
                interceptList.forEach(item -> {
                    List<String> orderIds = item.stream().map(item1 -> item1.getWarningOrderId()).collect(Collectors.toList());
                    Criteria criteria = Criteria.where(Constants.PARAM_WARN_ORDER_ID).in(orderIds);
                    Query query = Query.query(criteria);
                    sinkingNodeInterceptMongoDAO.remove(query, MongoDBType.PRIMARY, collectionName);
                    sinkingNodeInterceptMongoDAO.insertAll(item, collectionName, MongoDBType.PRIMARY);
                });
                log.info("异步处理回收站告警mergedInterceptList结果：{}", mergedInterceptList.size());
                mergedInterceptList = null;
            }
            excelWriter.finish();
            // 上传到OFS
            uploadToOFS(fileName, Files.readAllBytes(Paths.get(tempFilePath)));

        } catch (Exception e) {
            log.error("******getMergeInterceptList******Failed to upload Excel file to OFS:", e);
        } finally {
            // 删除临时文件
            try {
                Files.deleteIfExists(Paths.get(tempFilePath));
            } catch (IOException e) {
                log.warn(Constants.DELETE_TMP_FILE_ERROR, e);
            }
        }
        return Result.success("");
    }

    /**
     * 将excel文件上传ofs
     * @param fileName 文件名称
     * @param excelData 告警数据
     */
    private void uploadToOFS(String fileName, byte[] excelData) {
        log.info("====== excel文件上传ofs开始  ======");
        // 上传excel文件到OFS
        try {
            // 将 byte 数组转换为 ByteArrayInputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(excelData);
            // 创建 MockMultipartFile 对象，模拟 MultipartFile
            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    fileName, // 文件名
                    MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, // 文件类型
                    inputStream
            );
            log.info("上传excel文件到ofs req：{},{}", multipartFile,fileName);
            // 上传ofs
            AjaxResult result = fileFeignClient.uploadFileToOFS(multipartFile);
            log.info("上传excel文件到ofs resp：{}", result);
            // 处理上传结果
            if (result.ok()) {
                Object fileInfo = result.get("data");
                //String fileUrl = fileInfo.getFileUrl();
                // 继续处理上传成功后的逻辑
                log.info("File uploaded successfully. URL: ");
            }
        } catch (Exception e) {
            log.error("Failed to upload Excel file to OFS:", e);
        }
    }

    /**
     * 组装excle文件名
     * @param tableName
     * @return
     */
    private String getExcelFileName(String tableName, Date date) {
        //获取属地化节点
        String sdhNode = getPlatformNode();
        String fileName = sdhNode + Constants.SPLIT_SIGN + DateUtils.date2Str(date, YYYYMMDD_FORMAT) + Constants.SPLIT_SIGN + tableName + Constants.XLSX_SUFFIX;
        return fileName;
    }

    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 异步处理：查询属地化下沉节点告警相关基础信息
     * @param num
     * @param alarmOrderInfoParam
     */
    private void alarmOrderInfoAll(Integer num, AlarmOrderInfoParam alarmOrderInfoParam) {
        switch (num) {
            case 1:
                // 用户信息
                List<SysUser> userInfo = alarmDetailedInfoDAO.getUserInfoById(new ArrayList<>());
                Map<String, String> userInfoMap = userInfo.stream().collect(Collectors.toMap(SysUser::getUserName, SysUser::getNickName, (v1, v2) -> v1));
                Map<String, String> userInfoMap2 = userInfo.stream()
                        .collect(Collectors.toMap(SysUser::getUserName, u -> Optional.ofNullable(u.getPhonenumber()).orElse(""), (v1, v2) -> v1));
                Map<String, String> userMap = userInfo.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName, (key1, key2) -> key2));
                alarmOrderInfoParam.setUserInfoMap(userInfoMap);
                alarmOrderInfoParam.setUserInfoMap2(userInfoMap2);
                alarmOrderInfoParam.setUserMap(userMap);
                break;
            case 2:
                String localNode="";
                //获取当前节点
                SysConfig sysConfig = new SysConfig();
                sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
                AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
                if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
                    SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
                    if (sysConfigInfo != null) {
                        localNode = sysConfigInfo.getConfigValue();
                    }
                }
                // 行业应用名称
                List<SysIndustryConf> industryConf = alarmDetailedInfoDAO.getIndustryName(localNode);
                Map<String, String> industryMap = industryConf.stream().collect(Collectors.toMap(SysIndustryConf::getIndustryCode, SysIndustryConf::getIndustryName,(v1, v2) -> v1));
                alarmOrderInfoParam.setIndustryConfMap(industryMap);
                break;
            case 3:
                // 预置位名称
                List<Map> presetInfo = alarmDetailedInfoDAO.getPresetName();
                Map<String, String> presetInfoMap = presetInfo.stream().collect(Collectors.toMap(p -> p.get(Constants.PARAM_PRESET_ID).toString(), p -> p.get(Constants.PARAM_PRESET_NAME).toString(), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setPresetInfoMap(presetInfoMap);
                break;
            case 4:
                // 告警来源
                List<SysDictDataResponse> warningSourceEnum = this.getEnumList(Constants.WARNING_SOURCE_COMMON);
                Map<String, String> sourceMap = warningSourceEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setWarningSourceMap(sourceMap);
                // 第三方告警来源
                List<Map> thirdPartySource = localizationAlarmDAO.getThirdPartyAlarmSource();
                if(!CollectionUtils.isEmpty(thirdPartySource)){
                    Map<String, String> thirdPartySourceMap = thirdPartySource.stream().collect(Collectors.toMap(p -> p.get(Constants.PARAM_TENANT_ID).toString() + p.get(Constants.PARAM_INDUSTRY_CODE).toString(),
                            p -> p.get(Constants.PARAM_WARNING_SOURCE_NAME).toString(), (o, n) -> n, HashMap::new));
                    alarmOrderInfoParam.setThirdWarningSourceMap(thirdPartySourceMap);
                }
                break;
            case 5:
                // 告警类型
                List<Map> alarmTypeList = alarmDetailedInfoDAO.findAlarmTypeAll();
                Map<Object, Object> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get(Constants.PARAM_TYPE_VALUE), t -> t.get(Constants.PARAM_TYPE_NAME), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setAlarmTypeMap(typeMap);
                break;
            case 6:
                // 告警状态
                List<SysDictDataResponse> orderStatusEnum = this.getEnumList(String.valueOf(Constants.ORDER_STATUS));
                Map<String, String> orderStatusMap = orderStatusEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setOrderStatusMap(orderStatusMap);
                break;
            case 7:
                // 重要等级
                List<SysDictDataResponse> importanceLevelEnum = this.getEnumList(Constants.ALARM_LEVEL);
                Map<String, String> importanceLevelMap = importanceLevelEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setImportanceLevelMap(importanceLevelMap);
                break;
            case 8:
                // 事件等级
                List<SysDictDataResponse> emergencyLevelEnum = this.getEnumList(String.valueOf(Constants.EMERGENCY_LEVEL));
                Map<String, String> emergencyLevelMap = emergencyLevelEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setEmergencyLevelMap(emergencyLevelMap);
                break;
            case 9:
                // 消息类型名称
                List<SysDictDataResponse> msgTypeEnum = this.getEnumList(String.valueOf(Constants.MSG_TYPE));
                Map<String, String> msgTypeMap = msgTypeEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setMsgTypeMap(msgTypeMap);
                break;
            case 10:
                // 通知类型名称
                List<SysDictDataResponse> noticeResourceEnum = this.getEnumList(String.valueOf(Constants.NOTICE_RESOURCE));
                Map<String, String> noticeResourceMap = noticeResourceEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setNoticeResourceMap(noticeResourceMap);
                break;
            case 11:
                // 告警压制原因
                List<SysDictDataResponse> pressiongReasonEnum = this.getEnumList(Constants.DICT_TYPE_ALARM_PRESSING_REASON);
                Map<String, String> pressiongReasonMap = pressiongReasonEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel,(v1, v2) -> v1));
                alarmOrderInfoParam.setPressiongReasonMap(pressiongReasonMap);
                break;
            case 12:
                // 网格信息
                List<Map<String, Object>> gridInfo = this.getGrifInfo();
                Map<String, Map<String, Object>> gridInfoMap = gridInfo.stream()
                        .collect(Collectors.toMap(map -> map.get(Constants.GRID_ID).toString(), Function.identity()));
                alarmOrderInfoParam.setGridInfoMap(gridInfoMap);
                break;
            case 13:
                // 摄像机信息
                List<SysDictDataResponse> modelIdEnum = this.getEnumList(String.valueOf(Constants.MODEL_ID));
                List<DeviceInfoEntity> deviceInfo = alarmDetailedInfoDAO.getDevice();
                if (!CollectionUtils.isEmpty(deviceInfo) && !CollectionUtils.isEmpty(modelIdEnum)) {
                    for (DeviceInfoEntity device : deviceInfo) {
                        if (device.getModelId() != null) {
                            for (SysDictDataResponse item : modelIdEnum) {
                                if (device.getModelId().equals(item.getDictValue())) {
                                    device.setModelCodeName(item.getDictLabel());
                                    break;
                                }
                            }
                        }
                    }
                }
                Map<String, DeviceInfoEntity> deviceInfoMap = deviceInfo.stream().collect(Collectors.toMap(DeviceInfoEntity::getDeviceCode, deviceInfoEntity -> deviceInfoEntity, (existing, duplicate) -> existing));
                alarmOrderInfoParam.setDeviceInfoMap(deviceInfoMap);
                break;
            case 14:
                // 物联设备信息
                List<SysDictDataResponse> iotModelIdEnum = this.getEnumList(Constants.IOT_MODEL_ID);
                List<DeviceInfoEntity> iotDeviceInfo = alarmDetailedInfoDAO.getIotDevice();
                if (!CollectionUtils.isEmpty(iotDeviceInfo) && !CollectionUtils.isEmpty(iotModelIdEnum)) {
                    for (DeviceInfoEntity iot : iotDeviceInfo) {
                        if (iot.getModelId() != null) {
                            for (SysDictDataResponse item : iotModelIdEnum) {
                                if (iot.getModelId().equals(item.getDictValue())) {
                                    iot.setModelFactoryName(item.getDictLabel());
                                    break;
                                }
                            }
                        }
                    }
                }
                Map<String, DeviceInfoEntity> iotDeviceInfoMap = iotDeviceInfo.stream().collect(Collectors.toMap(DeviceInfoEntity::getDeviceCode, deviceInfoEntity -> deviceInfoEntity, (existing, duplicate) -> existing));
                alarmOrderInfoParam.setIotDeviceInfoMap(iotDeviceInfoMap);
                break;
            case 15:
                // 物联设备关联摄像机信息
                List<DeviceInfoEntity> iotDeviceConf = alarmDetailedInfoDAO.getIotDevConf();
                //查询设备收藏表
                List<String> deviceCollectList = alarmDetailedInfoDAO.getDeviceCodeCollect();
                iotDeviceConf.forEach(iotDevice -> {
                    //设置是否收藏标识（0-否、1-是）
                    iotDevice.setDeviceCodeCollect(Constants.ZERO);
                    if (!CollectionUtils.isEmpty(deviceCollectList)) {
                        deviceCollectList.forEach(item -> {
                            if(iotDevice.getDeviceCode() != null && item != null && iotDevice.getDeviceCode().equals(item)){
                                iotDevice.setDeviceCodeCollect(Constants.ONE);
                            }
                        });
                    }
                });
                Map<String, List<DeviceInfoEntity>> iotDeviceConfMap = iotDeviceConf.stream()
                        .collect(Collectors.groupingBy(device ->
                                device.getTenantId() + device.getIndustryCode() + device.getVidiconCode()));
                alarmOrderInfoParam.setIotDeviceConfMap(iotDeviceConfMap);
                break;
            case 16:
                // 雷达设备信息
                List<SysDictDataResponse> radarModelIdEnum = this.getEnumList(Constants.IOT_MODEL_ID);
                List<DeviceInfoEntity> radarDeviceInfo = alarmDetailedInfoDAO.getRadarDevice();
                if (!CollectionUtils.isEmpty(radarDeviceInfo) && !CollectionUtils.isEmpty(radarModelIdEnum)) {
                    for (DeviceInfoEntity radar : radarDeviceInfo) {
                        if (radar.getModelId() != null) {
                            for (SysDictDataResponse item : radarModelIdEnum) {
                                if (radar.getModelId().equals(item.getDictValue())) {
                                    radar.setModelName(item.getDictLabel());
                                    break;
                                }
                            }
                        }
                    }
                }
                Map<String, DeviceInfoEntity> radarDeviceInfoMap = radarDeviceInfo.stream().collect(Collectors.toMap(deviceInfoEntity -> deviceInfoEntity.getTenantId() + deviceInfoEntity.getIndustryCode() + deviceInfoEntity.getDeviceCode(),
                        Function.identity(),
                        (existing, duplicate) -> existing
                ));
                alarmOrderInfoParam.setRadarDeviceInfoMap(radarDeviceInfoMap);
                break;
            case 17:
                // 图斑信息
                List<Map> layerInfo = alarmDetailedInfoDAO.getLayerInfo();
                if(!CollectionUtils.isEmpty(layerInfo)){
                    List<String> layerIds = layerInfo.stream().map(item -> item.get(Constants.PARAM_LAYER_ID).toString()).distinct().collect(Collectors.toList());
                    //调用api获取几何实体
                    GeometrysReq geometrysReq = new GeometrysReq();
                    geometrysReq.setLayerIds(layerIds);
                    log.info("调用gisLayerAPI.getGeometrys  req:{}", JSON.toJSONString(geometrysReq));
                    try {
                        Result<List<GeometrysRes>> geometrysResult = gisLayerAPI.getGeometrys(geometrysReq);
                        log.info("调用gisLayerAPI.getGeometrys  resp:{}", JSON.toJSONString(geometrysResult));
                        if (Objects.nonNull(geometrysResult.getData())) {
                            List<GeometrysRes> geometrys = geometrysResult.getData();
                            layerInfo.forEach(layer -> {
                                String layerId = layer.get(Constants.PARAM_LAYER_ID).toString();
                                Optional<GeometrysRes> matchingGeometry = geometrys.stream()
                                        .filter(item -> layerId.equals(item.getLayerId()))
                                        .findFirst();
                                matchingGeometry.ifPresent(item -> {
                                    Object coordinates = JSONObject.parseObject(JSON.toJSONString(item.getGeometry())).get("coordinates");
                                    layer.put("geometry", JSON.toJSONString(coordinates));
                                    layer.put("geomeryType", item.getGeoType());
                                });
                            });
                        }
                        //获取电子围栏名称
                        List<Map> polygonPathInfo = alarmDetailedInfoDAO.getPolygonPatch();
                        layerInfo.forEach(layer -> polygonPathInfo.stream()
                                .filter(item -> layer.get(Constants.PARAM_LAYER_ID).equals(item.get(Constants.PARAM_LAYER_ID)))
                                .findFirst()
                                .ifPresent(item -> layer.put(Constants.PARAM_POLYGON_NAME, item.get(Constants.PARAM_POLYGON_NAME))));
                        Map<String, List<Map>> layerInfoMap = layerInfo.stream().collect(Collectors.groupingBy(item -> item.get(Constants.PARAM_TASK_ID).toString()));
                        alarmOrderInfoParam.setLayerInfoMap(layerInfoMap);
                    } catch (Exception e){
                        log.error("====调用getGeometrys方法 error", e);
                    }
                }
                break;
            case 18:
                //节点名称
                List<Map> platformDataList = alarmDetailedInfoDAO.getPlatformDataList();
                Map<String, String> platformDataMap = platformDataList.stream().collect(Collectors.toMap(p -> p.get(Constants.PARAM_PLATFORM_CODE).toString(), p -> p.get(Constants.PARAM_PLATFORM_NAME).toString(), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setPlatformDataMap(platformDataMap);
                break;
            case 19:
                //告警流程配置名称
                List<Map> alarmFlowConfigList = localizationAlarmDAO.getAlarmFlowConfigList();
                Map<String, String> flowConfigMap = alarmFlowConfigList.stream().collect(Collectors.toMap(p -> p.get(Constants.PARAM_FLOW_ID).toString(), p -> p.get(Constants.PARAM_ALARM_CONFIG_NAME).toString(), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setAlarmFlowConfigMap(flowConfigMap);
                break;
            case 20:
                //研判、调度、核实过程的附件信息
                List<OrderFileDO> orderFileList = localizationAlarmDAO.getOrderFileList();
                Map<String, List<OrderFileDO>> orderFileMap = orderFileList.stream()
                        .collect(Collectors.groupingBy(orderFile -> orderFile.getWarningOrderId() + Constants.SPLIT_SIGN + orderFile.getOrderFlowId()));
                alarmOrderInfoParam.setOrderFileMap(orderFileMap);
                break;
            case 21:
                //处置过程的附件信息
                List<Map> handleFileList = localizationAlarmDAO.getHandlePhotos();
                Map<String, List<Map>> handleFileMap = handleFileList.stream()
                        .collect(Collectors.groupingBy(map -> map.get("processOrderId").toString()));
                alarmOrderInfoParam.setHandleFileMap(handleFileMap);
                break;
            case 22:
                //获取研判、调度、核实环节的水印附件
                List<OrderFileDO> orderFiles = localizationAlarmDAO.getOrderFilesAll();
                Map<String, List<OrderFileDO>> orderFileAllMap = orderFiles.stream()
                        .collect(Collectors.groupingBy(orderFile -> orderFile.getWarningOrderId()));
                alarmOrderInfoParam.setOrderFileAllMap(orderFileAllMap);
                //获取处置环节的水印附件
                List<OrderFileDO> handleFiles = localizationAlarmDAO.getProcessOrderPhotosAll();
                Map<String, List<OrderFileDO>> processFileAllMap = handleFiles.stream()
                        .collect(Collectors.groupingBy(orderFile -> orderFile.getProcessOrderId()));
                alarmOrderInfoParam.setProcessFileAllMap(processFileAllMap);
                break;
            case 23:
                //获取联动算法名称
                List<Map> algorithmNameList = alarmDetailedInfoDAO.getAlgorithmNameAll();
                Map<String, String> algorithmNameAllMap = algorithmNameList.stream().collect(Collectors.toMap(p -> p.get("algorithmCode").toString(), p -> p.get("algorithmName").toString(), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setAlgorithmNameAllMap(algorithmNameAllMap);
                break;
            case 24:
                //获取无人机设备信息
                List<DeviceInfoEntity> uavDeviceList = alarmDetailedInfoDAO.getUavDeviceInfo();
                Map<String, List<DeviceInfoEntity>> uavDeviceMap = uavDeviceList.stream().collect(Collectors.groupingBy(vo -> vo.getDeviceCode()));
                alarmOrderInfoParam.setUavDeviceMap(uavDeviceMap);
                //获取无人机机场信息
                List<UavNestDeviceVo> uavNestDeviceInfo = alarmDetailedInfoDAO.getUavNestDevcieInfo();
                Map<String, List<UavNestDeviceVo>> uavDeviceNestMap = uavNestDeviceInfo.stream().collect(Collectors.groupingBy(p -> p.getDeviceCode()));
                alarmOrderInfoParam.setUavDeviceNestMap(uavDeviceNestMap);
                break;
            case 25:
                List<DeviceLabelInfoVO> deviceLabelInfoVOList = this.queryDeviceLabelBatch();
                Map<String, List<String>> deviceLabelMap = deviceLabelInfoVOList.stream().collect(Collectors.toMap(p -> p.getDeviceCode(), p -> p.getLabelInfoList().stream().map(v->v.getLabelName()).collect(Collectors.toList()), (o, n) -> n, HashMap::new));
                alarmOrderInfoParam.setDeviceLabelMap(deviceLabelMap);
                break;
            default: break;
        }
    }

    public List<DeviceLabelInfoVO> queryDeviceLabelBatch() {
        List<DeviceLabelInfoVO> resultList = new ArrayList<>();
        List<TowerDeviceLabelVO> towerDeviceLabelVOS = alarmDetailedInfoDAO.getDeviceLabelAll();
        Map<String, List<TowerDeviceLabelVO>> deviceLabelMap = towerDeviceLabelVOS.stream()
                .collect(Collectors.groupingBy(TowerDeviceLabelVO::getDeviceCode));

        for (Map.Entry<String, List<TowerDeviceLabelVO>> s : deviceLabelMap.entrySet()) {
            DeviceLabelInfoVO deviceLabelInfoVO = new DeviceLabelInfoVO();
            deviceLabelInfoVO.setDeviceCode(s.getKey());
            List<DeviceLabelSubVO> labelInfoList = new ArrayList<>();
            for (TowerDeviceLabelVO towerDeviceLabelVO : s.getValue()) {
                DeviceLabelSubVO deviceLabelSubVO = new DeviceLabelSubVO();
                deviceLabelSubVO.setLabelId(String.valueOf(towerDeviceLabelVO.getLabelId()));
                deviceLabelSubVO.setLabelName(towerDeviceLabelVO.getLabelName());
                labelInfoList.add(deviceLabelSubVO);
            }
            deviceLabelInfoVO.setLabelInfoList(labelInfoList);
            resultList.add(deviceLabelInfoVO);
        }
        return resultList;
    }
    /**
     * 工单表基础信息整合
     * @param mergedOrderList
     * @param recordMap
     * @param alarmOrderInfoParam
     */
    private void processMergedOrderListAsync(List<WarningOrderEntity> mergedOrderList, Map<String, List<OrderFlowRecordEntity>> recordMap, AlarmOrderInfoParam alarmOrderInfoParam) {
        Map<String, String> userInfoMap = alarmOrderInfoParam.getUserInfoMap();
        Map<String, String> userInfoMap2 = alarmOrderInfoParam.getUserInfoMap2();
        Map<String, String> userMap = alarmOrderInfoParam.getUserMap();
        Map<String, String> industryMap = alarmOrderInfoParam.getIndustryConfMap();
        Map<String, String> presetInfoMap = alarmOrderInfoParam.getPresetInfoMap();
        Map<String, String> sourceMap = alarmOrderInfoParam.getWarningSourceMap();
        Map<String, String> thirdSourceMap = alarmOrderInfoParam.getThirdWarningSourceMap();
        Map<Object, Object> typeMap = alarmOrderInfoParam.getAlarmTypeMap();
        Map<String, String> orderStatusMap = alarmOrderInfoParam.getOrderStatusMap();
        Map<String, String> importanceLevelMap = alarmOrderInfoParam.getImportanceLevelMap();
        Map<String, String> emergencyLevelMap = alarmOrderInfoParam.getEmergencyLevelMap();
        Map<String, String> msgTypeMap = alarmOrderInfoParam.getMsgTypeMap();
        Map<String, String> noticeResourceMap = alarmOrderInfoParam.getNoticeResourceMap();
        Map<String, Map<String, Object>> gridInfoMap = alarmOrderInfoParam.getGridInfoMap();
        Map<String, DeviceInfoEntity> deviceInfoMap = alarmOrderInfoParam.getDeviceInfoMap();
        Map<String, DeviceInfoEntity> iotDeviceInfoMap = alarmOrderInfoParam.getIotDeviceInfoMap();
        Map<String, List<DeviceInfoEntity>> iotDeviceConfMap = alarmOrderInfoParam.getIotDeviceConfMap();
        Map<String, DeviceInfoEntity> radarDeviceInfoMap = alarmOrderInfoParam.getRadarDeviceInfoMap();
        Map<String, List<Map>> layerInfoMap = alarmOrderInfoParam.getLayerInfoMap();
        Map<String, String> platformDataMap = alarmOrderInfoParam.getPlatformDataMap();
        Map<String, String> flowConfigMap = alarmOrderInfoParam.getAlarmFlowConfigMap();
        Map<String, List<OrderFileDO>> orderFileAllMap = alarmOrderInfoParam.getOrderFileAllMap();
        Map<String, List<OrderFileDO>> processFileAllMap = alarmOrderInfoParam.getProcessFileAllMap();
        Map<String, List<OrderFileDO>> orderFileMap = alarmOrderInfoParam.getOrderFileMap();
        Map<String, List<Map>> handleFileMap = alarmOrderInfoParam.getHandleFileMap();
        Map<String, String> algorithmNameAllMap = alarmOrderInfoParam.getAlgorithmNameAllMap();
        Map<String, List<DeviceInfoEntity>> uavDeviceMap = alarmOrderInfoParam.getUavDeviceMap();
        Map<String, List<UavNestDeviceVo>> uavNestDeviceMap = alarmOrderInfoParam.getUavDeviceNestMap();
        Map<String, List<String>> deviceLabelMap = alarmOrderInfoParam.getDeviceLabelMap();
        String platformCode = getPlatformNode();
        //查询督办记录
        List<HandleRecordVO> handleRecordList = mongoUtilDAO.find(new Query(), HandleRecordVO.class, Constants.VM_FLOW_HANDLE_RECORD_T);
        //查询算法配置信息拼接
        List<Map> map = synchLocalizationAlarmDAO.queryAlgorithmConfig();
        if (!CollectionUtils.isEmpty(mergedOrderList)) {
            Map<String, List<Map<String, Object>>> satelliteLayerInfoMap = getSatelliteLayerInfo(mergedOrderList, null);
            mergedOrderList.forEach(order -> {
                ExtendFieldVO extendFieldVO = new ExtendFieldVO();
                //获取各属地化下沉节点编码
                if (!StringUtils.isEmpty(platformCode)) {
                    order.setAccessNode(platformCode); //接入节点
                    order.setAcceptNode(platformCode); //受理节点
                    order.setPlatformName(platformDataMap.get(platformCode));
                }
                //告警流程信息
                if ((recordMap != null && !recordMap.isEmpty()) && !StringUtils.isEmpty(order.getWarningOrderId())) {
                    //办理意见
                    List<OrderFlowRecordEntity> flowRecordList = recordMap.get(order.getWarningOrderId());
                    // 根据 flowNodeTime 和 orderFlowId 属性进行升序排序，避免空值 -- 导出需要升序
                    List<OrderFlowRecordEntity> flowRecordSortList = flowRecordList.stream()
                            .sorted(Comparator.nullsLast(
                                    Comparator.comparing(OrderFlowRecordEntity::getFlowNodeTime)
                                            .thenComparing(OrderFlowRecordEntity::getOrderFlowId)
                            ))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(flowRecordSortList)) {
                        flowRecordSortList.forEach(item2 -> {
                            if(order.getWarningOrderId().equals(item2.getOrderId()) && !StringUtils.isEmpty(item2.getRealLinkType())){
                                //导出-拼接办理说明
                                handlingInstructions(order, item2, userMap);
                            }
                            //研判、调度、核实的附件信息
                            if(orderFileMap != null && !orderFileMap.isEmpty()){
                                String key = order.getWarningOrderId() + Constants.SPLIT_SIGN + item2.getOrderFlowId();
                                List<OrderFileDO> fileList = orderFileMap.get(key);
                                if(!CollectionUtils.isEmpty(fileList)){
                                    String jsonString = JSON.toJSONString(fileList);
                                    item2.setOrderFileListVO(jsonString);
                                }
                            }
                            //处置的附件
                            if(!StringUtils.isEmpty(item2.getProcessOrderId())){
                                if(handleFileMap != null && !handleFileMap.isEmpty()){
                                    List<Map> fileMap = handleFileMap.get(item2.getProcessOrderId());
                                    if(!CollectionUtils.isEmpty(fileMap)){
                                        item2.setFilesVO(JSON.toJSONString(fileMap));
                                        //如果是已受理，即返回可查看附件按钮标识
                                        if(!Constants.TWO.equals(item2.getEnableAcceptanceMode())){
                                            item2.setIfAttachment(Constants.ONE);
                                        }
                                    }
                                }
                                //处置的水印图片
                                if(processFileAllMap != null && !processFileAllMap.isEmpty()){
                                    List<OrderFileDO> handleFiles = processFileAllMap.get(item2.getProcessOrderId());
                                    order.setHandleFiles(handleFiles);
                                }
                            }
                        });
                    }
                    //倒序 --详情展示
                    List<OrderFlowRecordEntity> flowRecordDescSortList = flowRecordList.stream()
                            .sorted(Comparator.nullsLast(
                                    Comparator.comparing(OrderFlowRecordEntity::getFlowNodeTime)
                                            .thenComparing(OrderFlowRecordEntity::getOrderFlowId)
                                            .reversed()
                            ))
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(flowRecordDescSortList)){
                        order.setOrderFlowRecordJson(JSON.toJSONString(flowRecordDescSortList));
                    }
                    //导出-当前环节处理人
                    if (!StringUtils.isEmpty(order.getProcessId())) {
                        String[] processIdList = order.getProcessId().split(Constants.COMMA);
                        //只保留非空的
                        processIdList = Arrays.stream(processIdList)
                                .filter(str -> !str.isEmpty())
                                .toArray(String[]::new);
                        List<String> userNameList = new ArrayList<>();
                        for (String userId : processIdList) {
                            userNameList.add(userMap.get(userId));
                        }
                        if(!CollectionUtils.isEmpty(userNameList)){
                            if(userNameList.size() == 1){
                                order.setProcessName(userNameList.get(0));
                            } else {
                                order.setProcessName(String.join(",",userNameList));
                            }
                        }
                    }
                }
                //所有环节上传的带水印附件 --用于地图上展示缩略图+箭头
                if(!StringUtils.isEmpty(order.getWarningOrderId())){
                    List<OrderFileDO> fileDOList = orderFileAllMap.get(order.getWarningOrderId()); //研判+调度+核实的附件
                    List<OrderFileDO> handleFileList = order.getHandleFiles(); //处置的附件
                    if(!CollectionUtils.isEmpty(handleFileList)){
                        fileDOList.addAll(handleFileList);
                    }
                    if(!CollectionUtils.isEmpty(fileDOList)){
                        //根据经纬度和拍摄角度进行分组,过滤经纬度为空的附件
                        Map<String, List<OrderFileDO>> groupedFiles = fileDOList.stream().filter(file ->{
                            Map<String, String> fileWaterMarkMap = JSONObject.parseObject(file.getFileWaterMark(), Map.class);
                            String longitude = fileWaterMarkMap.get(Constants.PARAM_LONGITUDE);
                            String latitude = fileWaterMarkMap.get(Constants.PARAM_LATITUDE);
                            return !StringUtils.isEmpty(longitude) && !StringUtils.isEmpty(latitude);
                        }).collect(Collectors.groupingBy(file -> {
                            //将fileWaterMark字符串解析成一个map对象
                            Map<String, String> fileWaterMarkMap = JSONObject.parseObject(file.getFileWaterMark(), Map.class);
                            String longitude = fileWaterMarkMap.get(Constants.PARAM_LONGITUDE);
                            String latitude = fileWaterMarkMap.get(Constants.PARAM_LATITUDE);
                            String shootingAngle = fileWaterMarkMap.get("shootingAngle");
                            return String.join(Constants.SPLIT_SIGN, longitude, latitude, shootingAngle);
                        }));
                        if(!CollectionUtils.isEmpty(groupedFiles)){
                            //组装结果列表[{"longitude": "116.3357674","latitude": "39.939674","shootingAngle": "180","orderFiles":[{}]}]
                            List<Map<String, Object>> result = groupedFiles.entrySet().stream().map(entry -> {
                                String[] keyParts = entry.getKey().split(Constants.SPLIT_SIGN);
                                String longitude = keyParts[0];
                                String latitude = keyParts[1];
                                String shootingAngle = keyParts[2];
                                List<OrderFileDO> files = entry.getValue();
                                Map<String, Object> resultMap = new HashMap<>();
                                resultMap.put(Constants.PARAM_LONGITUDE, longitude);
                                resultMap.put(Constants.PARAM_LATITUDE, latitude);
                                resultMap.put("shootingAngle", shootingAngle);
                                resultMap.put("orderFiles", files);
                                return resultMap;
                            }).collect(Collectors.toList());
                            order.setFilesVo(JSON.toJSONString(result));
                        }
                    }
                }
                //告警流程配置名称
                if (!StringUtils.isEmpty(order.getFlowId())) {
                    order.setAlarmFlowName(flowConfigMap.get(order.getFlowId()));
                    if(StringUtils.isEmpty(order.getAlarmFlowName())){
                        order.setAlarmFlowName("默认流程");
                    }
                }
                //联动算法名称
                if(!StringUtils.isEmpty(order.getLinkageAlgorithm()) && !order.getWarningSource().equals(Constants.WARNING_SOURCE_11)){
                    order.setLinkageAlgorithm(algorithmNameAllMap.get(order.getLinkageAlgorithm()));
                }
                //网格信息
                if (!StringUtils.isEmpty(order.getGridId())) {
                    Map<String, Object> gridListVO = gridInfoMap.get(order.getGridId());
                    if (gridListVO != null) {
                        order.setGridName(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_NAME)) ? gridListVO.get(Constants.GRID_NAME).toString() : null);
                        order.setGridCenter(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_CENTER)) ? gridListVO.get(Constants.GRID_CENTER).toString() : null);
                        //order.setGridRange(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_RANGE)) ? gridListVO.get(Constants.GRID_RANGE).toString() : null);
                        //order.setKeeperId(!ObjectUtils.isEmpty(gridListVO.get(Constants.KEEPER_ID)) ? gridListVO.get(Constants.KEEPER_ID).toString() : null);
                        order.setKeeperName(!ObjectUtils.isEmpty(gridListVO.get(Constants.KEEPER_NAME)) ? gridListVO.get(Constants.KEEPER_NAME).toString() : null);
                        //order.setKeeperPhoneNumber(!ObjectUtils.isEmpty(gridListVO.get(Constants.KEEPER_PHONE_NUMBER)) ? gridListVO.get(Constants.KEEPER_PHONE_NUMBER).toString() : null);
                        extendFieldVO.setColor(!ObjectUtils.isEmpty(gridListVO.get(Constants.COLOR)) ? gridListVO.get(Constants.COLOR).toString() : null);
                        extendFieldVO.setBorderColor(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR)) ? gridListVO.get(Constants.BORDER_COLOR).toString() : null);
                        extendFieldVO.setBorderColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY).toString() : null);
                        extendFieldVO.setBorderColorLineThickness(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS)) ? gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS).toString() : null);
                        extendFieldVO.setColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.COLOR_TRANSPARENCY).toString() : null);
                    }
                }
                //图斑信息
                if ((layerInfoMap != null && !layerInfoMap.isEmpty()) && !StringUtils.isEmpty(order.getTaskId())) {
                    order.setPolygonOperate(JSON.toJSONString(layerInfoMap.get(order.getTaskId())));
                }
                //v1.6.60 卫片执法告警-图斑信息
                if(!StringUtils.isEmpty(order.getWarningTypeId()) && Arrays.asList(WPZF_WARNING_TYPE).contains(order.getWarningTypeId())){
                    if((satelliteLayerInfoMap != null && !satelliteLayerInfoMap.isEmpty()) && !StringUtils.isEmpty(order.getExtraField())){
                        //从扩展字段中获取到图层ID
                        ReceiveRegionAlarmDto spot = JSONObject.parseObject(order.getExtraField(), ReceiveRegionAlarmDto.class);
                        if(!StringUtils.isEmpty(spot.getLayerId())){
                            order.setPolygonOperate(JSON.toJSONString(satelliteLayerInfoMap.get(spot.getLayerId())));
                        }
                    }
                }
                //扩展字段设置默认值
                extendFieldVO.setDefaultField(Constants.ZERO);
                //督办信息根据工单ID匹配
                List<HandleRecordVO> handleRecordVOList = new ArrayList<>();
                handleRecordList.forEach(handleRecordVO -> {
                    if(handleRecordVO.getOrderId().equals(order.getWarningOrderId())){
                        handleRecordVO.setHandleUserName(userMap.get(handleRecordVO.getHandleUserId()));
                        handleRecordVOList.add(handleRecordVO);
                    }
                });
                if (!CollectionUtils.isEmpty(handleRecordList)){
                    // 使用流进行倒序排序
                    List<HandleRecordVO> sortedList = handleRecordVOList.stream()
                            .sorted((o1, o2) -> o2.getHandleTime().compareTo(o1.getHandleTime()))
                            .collect(Collectors.toList());
                    extendFieldVO.setHandleRecordVOS(sortedList);
                }
                if (!CollectionUtils.isEmpty(map)) {
                    // 使用流进行过滤，只保留符合条件的元素
                    List<Map> filteredList = map.stream()
                            .filter(m -> order.getTenantId() != null &&  order.getTenantId().equals(m.get(Constants.PARAM_TENANT_ID)) &&
                                    order.getTaskId() != null && order.getTaskId().equals(m.get(Constants.PARAM_TASK_ID))
                            ).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(filteredList) && order.getWarningSource().equals(Constants.ONE)){
                        extendFieldVO.setDescription(buildDescriptionField(filteredList));
                    }
                }
                //事件发现人姓名
                if (!StringUtils.isEmpty(order.getFindName()) && !order.getWarningSource().equals(Constants.WARNING_SOURCE_11)) {
                    order.setFindMobile(userInfoMap2.get(order.getFindName()));
                    order.setFindName(userInfoMap.get(order.getFindName()));
                }
                //删除告警-查询删除人
                String deleteUser = "";
                if(!StringUtils.isEmpty(order.getDeleteFlag()) && Constants.ONE.equals(order.getDeleteFlag())){
                    if (!StringUtils.isEmpty(order.getUserRecord())) {
                        if(order.getUserRecord().contains(Constants.COMMA)){
                            int index = order.getUserRecord().lastIndexOf(Constants.COMMA);
                            deleteUser = order.getUserRecord().substring(index+1);
                        } else {
                            deleteUser = order.getUserRecord();
                        }
                    }
                    if (!StringUtils.isEmpty(deleteUser)) {
                        order.setDeleteUser(userMap.get(deleteUser));
                    }
                }
                //行业应用名称
                if (!StringUtils.isEmpty(order.getIndustryCode())) {
                    order.setIndustryName(industryMap.get(order.getIndustryCode()));
                }
                //置信度 --先展示综合置信度，没有的话展示最大置信度
                if (!StringUtils.isEmpty(order.getCompConfidenceLevel())) {
                    order.setConfidenceLevel(order.getCompConfidenceLevel());
                } else {
                    if (!StringUtils.isEmpty(order.getConfidenceLevel())) {
                        String[] confidenceLevelList = order.getConfidenceLevel().split(Constants.COMMA);
                        Optional<String> confidenceLevelOpt =Arrays.stream(confidenceLevelList).max(String::compareTo);
                        if(confidenceLevelOpt.isPresent()){
                            String confidenceLevelNew =confidenceLevelOpt.get();
                            order.setConfidenceLevel(confidenceLevelNew);
                        }
                    }
                }
                //预置位名称
                if (!StringUtils.isEmpty(order.getPresetId())) {
                    order.setPresetName(presetInfoMap.get(order.getPresetId()));
                }
                //告警来源名称
                if (!StringUtils.isEmpty(order.getWarningSource())) {
                    order.setWarningSourceName(sourceMap.get(order.getWarningSource()));
                    //第三方告警来源名称
                    if (Constants.WARNING_SOURCE_11.equals(order.getWarningSource())) {
                        if(thirdSourceMap != null && !thirdSourceMap.isEmpty()){
                            order.setWarningSourceName(thirdSourceMap.get(order.getTenantId() + order.getIndustryCode()));
                        }
                    }
                }
                //告警类型名称
                if (!StringUtils.isEmpty(order.getWarningTypeId())) {
                    if(typeMap.containsKey(order.getWarningTypeId())){
                        order.setWarningTypeName(String.valueOf(typeMap.get(order.getWarningTypeId())));
                    }
                }
                //告警状态名称
                if (!StringUtils.isEmpty(order.getOrderStatus())) {
                    order.setOrderStatusName(orderStatusMap.get(order.getOrderStatus()));
                }
                if(EnumUtil.FlowNode.MISINFORMATION.getCode().equals(order.getOrderStatus())
                        || EnumUtil.FlowNode.DUPLICATION.getCode().equals(order.getOrderStatus())
                        || EnumUtil.FlowNode.END.getCode().equals(order.getOrderStatus())){
                    order.setOrderStatusName(EnumUtil.FlowNode.END.getName());
                }
                //重要等级名称
                if (!StringUtils.isEmpty(order.getImportanceLevel())) {
                    order.setImportanceLevelName(importanceLevelMap.get(order.getImportanceLevel()));
                }
                //告警等级名称
                if (!StringUtils.isEmpty(order.getEmergencyLevel())) {
                    order.setEmergencyLevelName(emergencyLevelMap.get(order.getEmergencyLevel()));
                }
                // 消息类型名称
                if (!StringUtils.isEmpty(order.getMsgType())) {
                    order.setMsgTypeName(msgTypeMap.get(order.getMsgType()));
                }
                // 通知类型名称
                if (!StringUtils.isEmpty(order.getNoticeResource())) {
                    order.setNoticeResourceName(noticeResourceMap.get(order.getNoticeResource()));
                }
                //物联、卫星、雷达设备信息
                if (!StringUtils.isEmpty(order.getDeviceCode())) {
                    if(WarningSourceEnum.IOT_ALARM.getTypeValue().equals(order.getWarningSource())){ //物联
                        DeviceInfoEntity iotDeviceInfo = iotDeviceInfoMap.get(order.getDeviceCode());
                        if (iotDeviceInfo != null) {
                            order.setDeviceAddress(iotDeviceInfo.getLocation());
                            order.setHeight(iotDeviceInfo.getHeight());
                            order.setModelId(iotDeviceInfo.getModelId());
                            order.setModelCode(iotDeviceInfo.getModelCode());
                            order.setVideoLatitude(iotDeviceInfo.getLatitude());
                            order.setVideoLongitude(iotDeviceInfo.getLongitude());
                            order.setDeviceLatitude(iotDeviceInfo.getLatitude());
                            order.setDeviceLongitude(iotDeviceInfo.getLongitude());
                            order.setDeviceModelCode(iotDeviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(iotDeviceInfo.getModelFactoryName());
                            order.setVertiDownNeg(iotDeviceInfo.getVertiDownNeg());
                            order.setFocusRange(iotDeviceInfo.getFocusRange());
                        }
                        //查询物联设备联动摄像机信息
                        String key = order.getTenantId() + order.getIndustryCode() + order.getDeviceCode();
                        List<IotRelDeviceVo> relDeviceList = Optional.ofNullable(iotDeviceConfMap.get(key))
                                .map(list -> list.stream()
                                        .map(item -> {
                                            IotRelDeviceVo relDevice = new IotRelDeviceVo();
                                            BeanUtil.copyProperties(item, relDevice);
                                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                                relDevice.setDeviceLabel(deviceLabelMap.get(relDevice.getDeviceCode()));
                                            }
                                            return relDevice;
                                        })
                                        .collect(Collectors.toList()))
                                .orElse(Collections.emptyList());
                        order.setRelDeviceVo(JSON.toJSONString(relDeviceList));
                        // 1.6.90 新增返回多组物联设备信息，以及每个物联设备联动的多组摄像机信息
                        extendFieldVO.setIotDevInfoList(buildMultIotDeviceInfo(order, iotDeviceInfoMap, algorithmNameAllMap,deviceLabelMap));
                    } else if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(order.getWarningSource())) { //雷达
                        SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (deviceInfo != null) {
                            BeanUtil.copyProperties(deviceInfo,radarDevice);
                            radarDevice.setDeviceCode(order.getDeviceCode());
                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                radarDevice.setDeviceLabel(deviceLabelMap.get(radarDevice.getDeviceCode()));
                            }
                        }
                        //查询雷达设备联动摄像机信息
                        if(radarDevice != null){
                            order.setRadarDeviceVo(JSON.toJSONString(radarDevice));
                        } else {
                            order.setRadarDeviceVo("");
                        }
                        //从扩展字段获取雷达编码
                        Object[] radarCodes = null;
                        List<DeviceInfoEntity> deviceSiteList = new ArrayList<>();
                        if (!StringUtils.isEmpty(order.getExtraField())) {
                            try {
                                JSONObject jsonExtraField = JSONObject.parseObject(order.getExtraField());
                                try {
                                    if (jsonExtraField.containsKey(Constants.RADAR_CODES)) {
                                        radarCodes = jsonExtraField.getJSONArray(Constants.RADAR_CODES).toArray();
                                    }
                                } catch (Exception e) {
                                    log.error(Constants.ERR_MSG_RADAR+ order.getExtraField()+ e);
                                }
                            } catch (Exception e) {
                                log.error(Constants.ERR_MSG_JSON+ order.getExtraField()+ e);
                            }
                        }
                        if (radarCodes != null) {
                            for (Object radarCode : radarCodes) {
                                if(ObjectUtil.isEmpty(radarCode)){
                                    continue;
                                }
                                DeviceInfoEntity radarDeviceInfo = radarDeviceInfoMap.get(order.getTenantId() + order.getIndustryCode() + radarCode.toString());
                                if (radarDeviceInfo != null) {
                                    order.setDeviceAddress(radarDeviceInfo.getLocation());
                                    order.setHeight(radarDeviceInfo.getHeight());
                                    order.setModelId(radarDeviceInfo.getModelId());
                                    order.setModelCode(radarDeviceInfo.getModelCode());
                                    order.setVideoLatitude(radarDeviceInfo.getLatitude());
                                    order.setVideoLongitude(radarDeviceInfo.getLongitude());
                                    order.setDeviceModelCode(radarDeviceInfo.getDeviceModelCode());
                                    order.setModelFactoryName(radarDeviceInfo.getModelName());
                                    order.setVertiDownNeg(radarDeviceInfo.getVertiDownNeg());
                                    order.setFocusRange(radarDeviceInfo.getFocusRange());
                                }
                                deviceSiteList.add(radarDeviceInfo);
                            }
                        }
                        //雷达设备信息
                        if(!CollectionUtils.isEmpty(deviceSiteList)){
                            order.setDeviceSiteListVo(JSON.toJSONString(deviceSiteList));
                        } else {
                            order.setDeviceSiteListVo("");
                        }
                    } else if (WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue().equals(order.getWarningSource())) { //无人机
                        String deviceCode = order.getDeviceCode();
                        extendFieldVO.setUavDevInfoList(buildMultUavDeviceInfo(deviceCode, uavDeviceMap, uavNestDeviceMap));
                    } else { //AI、摄像机
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (!CollectionUtils.isEmpty(deviceLabelMap)){
                            extendFieldVO.setDeviceLabel(deviceLabelMap.get(order.getDeviceCode()));
                        }
                        if (deviceInfo != null) {
                            order.setDeviceAddress(deviceInfo.getLocation());
                            order.setHeight(deviceInfo.getHeight());
                            order.setModelId(deviceInfo.getModelId());
                            order.setModelCode(deviceInfo.getModelCode());
                            order.setVideoLatitude(deviceInfo.getLatitude());
                            order.setVideoLongitude(deviceInfo.getLongitude());
                            order.setDeviceModelCode(deviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(deviceInfo.getModelCodeName());
                            order.setVertiDownNeg(deviceInfo.getVertiDownNeg());
                            order.setFocusRange(deviceInfo.getFocusRange());
                            if(Constants.ONE.equals(deviceInfo.getAlarmLocation())){
                                order.setAlarmLocation(deviceInfo.getAlarmLocation());
                            }
                            //1.8.10 添加摄像机类型、摄像机子类型
                            extendFieldVO.setDeviceType(deviceInfo.getDeviceType());
                            extendFieldVO.setDeviceTypeName(deviceInfo.getDeviceTypeName());
                            extendFieldVO.setDeviceSubType(deviceInfo.getDeviceSubType());
                            extendFieldVO.setDeviceSubTypeName(deviceInfo.getDeviceSubTypeName());
                        }
                    }
                }
                //添加卫星告警信息
                if (Constants.ALARM_SOURCE_9.equals(order.getWarningSource()) && Objects.nonNull(order.getExtraField())) {
                    try {
                        SatelliteExtendFieldVO extendFieldVo = JSON.parseObject(order.getExtraField(),SatelliteExtendFieldVO.class);
                        String snapShotTime = extendFieldVo.getSnapShotTime(); //抓图时间
                        String realDeviceCode = extendFieldVo.getRealDeviceCode(); //联动摄像机编号
                        List<DeviceInfoEntity> satelliteRelDeviceVos = new ArrayList<>();
                        if (!StringUtils.isEmpty(realDeviceCode)) {
                            List<String> deviceCodeArray = Arrays.asList(realDeviceCode.split(Constants.COMMA));
                            for(String deviceCode : deviceCodeArray){
                                DeviceInfoEntity deviceInfo = deviceInfoMap.get(deviceCode);
                                if(!StringUtils.isEmpty(snapShotTime)){
                                    deviceInfo.setSnapShotTime(snapShotTime);
                                }
                                if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                    deviceInfo.setDeviceLabel(deviceLabelMap.get(deviceCode));
                                }

                                satelliteRelDeviceVos.add(deviceInfo);
                            }
                        }
                        if(!CollectionUtils.isEmpty(satelliteRelDeviceVos)){
                            order.setSatelliteDeviceListVo(JSON.toJSONString(satelliteRelDeviceVos));
                        } else {
                            order.setSatelliteDeviceListVo("");
                        }
                        //火区面积、下垫面类型字段
                        if (!StringUtils.isEmpty(extendFieldVo.getFireArea())) {
                            order.setFireArea(extendFieldVo.getFireArea());
                        }
                        if (!org.springframework.util.StringUtils.isEmpty(extendFieldVo.getUnderlyingSurfaceType())) {
                            order.setUnderlyingSurfaceType(extendFieldVo.getUnderlyingSurfaceType());
                        }
                    } catch (Exception e) {
                        log.error("事件查询-获取卫星告警信息失败，参数：{}", order.getExtraField(), e);
                    }
                }
                //水平方位角处理转换
                if (!StringUtils.isEmpty(order.getHoriAzimuthAngle())) {
                    order.setHoriAzimuthAngle(AngleUtils.transformAngle(order.getHoriAzimuthAngle()));
                }
                //转义PTZ
                if (!StringUtils.isEmpty(order.getPan())
                        && !StringUtils.isEmpty(order.getTilt())
                        && !StringUtils.isEmpty(order.getZoom()) && !StringUtils.isEmpty(order.getModelId())){
                    try{
                        String pValue = order.getPan();
                        String tValue = order.getTilt();
                        String zValue = order.getZoom();
                        //获取摄像设备厂家编码
                        String modelId = order.getModelId();
                        //获取摄像机类型的焦距范围
                        String deviceFocusRange = order.getFocusRange();
                        //摄像机水平向下是否为负
                        String vertiDownNeg = order.getVertiDownNeg();
                        DeviceModelVO deviceModel = new DeviceModelVO();
                        deviceModel.setMfgrCode(modelId);
                        deviceModel.setVertiDownNeg(vertiDownNeg);
                        if (Constants.MODELID_DAHUA.equals(modelId) || Constants.MODELID_DHUA.equals(modelId)){
                            deviceModel.setFocusRange(deviceFocusRange);
                        }
                        String pNewValue = LongLatAngleUtils.paramTrans(pValue, deviceModel, "P");
                        String tNewValue = LongLatAngleUtils.paramTrans(tValue, deviceModel, "T");
                        String zNewValue = LongLatAngleUtils.paramTrans(zValue, deviceModel, "Z");
                        order.setPtzTransP(pNewValue);
                        order.setPtzTransT(tNewValue);
                        order.setPtzTransZ(zNewValue);
                    }catch(Exception e){
                        log.error(Constants.ERR_MSG_PTZESCAPE, order.getWarningOrderId(), e);
                    }
                }
                extendFieldVO.setTransparentFields(order.getTransparentFields());
                //填充扩展字段
                order.setExtendFields(JSON.toJSONString(extendFieldVO));
            });
        }
    }

    private List<Map<String, Object>> buildMultUavDeviceInfo(String deviceCode, Map<String, List<DeviceInfoEntity>> uavDeviceMap, Map<String, List<UavNestDeviceVo>> uavNestDeviceMap) {
        List<Map<String, Object>> uavInfoList = new ArrayList<>();
        if (uavDeviceMap == null || uavDeviceMap.isEmpty()) {
            return uavInfoList;
        }
        List<DeviceInfoEntity> uavDeviceInfo = uavDeviceMap.get(deviceCode);
        if (uavDeviceInfo == null) {
            return uavInfoList;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("uavDevInfoList", uavDeviceInfo);
        uavDeviceInfo.forEach(item -> {
            List<UavNestDeviceVo> uavNestDeviceInfo = uavNestDeviceMap.get(item.getDeviceCode());
            item.setUavNestDeviceList(uavNestDeviceInfo != null ? uavNestDeviceInfo : new ArrayList<>());
        });
        uavInfoList.add(result);
        return uavInfoList;
    }

    /**
     * 构建多组物联设备信息、及联动的多组摄像机信息
     * @param order
     * @param iotDeviceInfoMap
     * @param algorithmNameAllMap
     * @return
     */
    private List<IotDeviceInfoVO> buildMultIotDeviceInfo(WarningOrderEntity order, Map<String, DeviceInfoEntity> iotDeviceInfoMap, Map<String, String> algorithmNameAllMap,Map<String, List<String>> deviceLabelMap) {
        String extraField = buildExtraField(order);
        List<IotDeviceInfoVO> iotDeviceInfoList = new ArrayList<>();
        List<DeviceInfoExtendFieldVo> deviceInfoList = JSON.parseObject(extraField).getJSONArray(Constants.PARAM_DEVICEINFO).toJavaList(DeviceInfoExtendFieldVo.class);
        if (deviceInfoList != null && !deviceInfoList.isEmpty()) {
            // 获取关联的摄像机信息，并转换为 Map 以 deviceCode 为键
            List<IotRelDeviceVo> orderRelDevices = JSON.parseArray(order.getRelDeviceVo(), IotRelDeviceVo.class);
            Map<String, IotRelDeviceVo> orderRelDevicesMap = orderRelDevices.stream()
                    .collect(Collectors.toMap(IotRelDeviceVo::getDeviceCode, device -> device));

            for (DeviceInfoExtendFieldVo deviceInfoExtendFieldVo : deviceInfoList) {
                DeviceInfoEntity deviceSiteVO = iotDeviceInfoMap.get(deviceInfoExtendFieldVo.getIotDeviceCode());
                if (deviceSiteVO != null) {
                    IotDeviceInfoVO iotDeviceInfoVO = new IotDeviceInfoVO();
                    iotDeviceInfoVO.setDeviceCode(deviceSiteVO.getDeviceCode());
                    iotDeviceInfoVO.setDeviceName(deviceSiteVO.getDeviceName());
                    iotDeviceInfoVO.setDeviceAddress(deviceSiteVO.getLocation());
                    iotDeviceInfoVO.setModelId(deviceSiteVO.getModelId());
                    iotDeviceInfoVO.setHeight(deviceSiteVO.getHeight());
                    iotDeviceInfoVO.setVideoLatitude(deviceSiteVO.getLatitude());
                    iotDeviceInfoVO.setVideoLongitude(deviceSiteVO.getLongitude());
                    iotDeviceInfoVO.setDeviceLongitude(deviceSiteVO.getLongitude());
                    iotDeviceInfoVO.setDeviceLatitude(deviceSiteVO.getLatitude());
                    iotDeviceInfoVO.setDeviceModelCode(deviceSiteVO.getDeviceModelCode());
                    iotDeviceInfoVO.setFocusRange(deviceSiteVO.getFocusRange());
                    iotDeviceInfoVO.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
                    iotDeviceInfoVO.setModelFactoryName(deviceSiteVO.getModelFactoryName());

                    List<IotRelDeviceVo> iotRelDeviceList = new ArrayList<>();
                    List<IotRelDeviceVo> relDeviceList = deviceInfoExtendFieldVo.getRelDevice();
                    if (relDeviceList != null && !relDeviceList.isEmpty() && orderRelDevices != null && !orderRelDevices.isEmpty()) {
                        for (IotRelDeviceVo relDeviceVo : relDeviceList) {
                            IotRelDeviceVo matchedDevice = orderRelDevicesMap.get(relDeviceVo.getDeviceCode());
                            if (matchedDevice != null) {
                                matchedDevice.setIsSecondIdentify(relDeviceVo.getIsSecondIdentify());
                                matchedDevice.setSecondIdentifyResult(relDeviceVo.getSecondIdentifyResult());
                                matchedDevice.setLinkageAlgorithm(algorithmNameAllMap.get(relDeviceVo.getLinkageAlgorithm()));
                                if (!CollectionUtils.isEmpty(deviceLabelMap.get(relDeviceVo.getDeviceCode()))){
                                    matchedDevice.setDeviceLabel(deviceLabelMap.get(relDeviceVo.getDeviceCode()));
                                }
                                iotRelDeviceList.add(matchedDevice);
                            }
                        }
                    }
                    iotDeviceInfoVO.setIotDeviceList(iotRelDeviceList);
                    iotDeviceInfoList.add(iotDeviceInfoVO);
                }
            }
        }
        return iotDeviceInfoList;
    }

    /**
     * 兼容历史数据，构建扩展字段内容
     * @param order
     * @return
     */
    private String buildExtraField(WarningOrderEntity order) {
        JSONObject jsonObject = JSON.parseObject(order.getExtraField());
        JSONObject newJsonObject = new JSONObject();
        if (jsonObject.containsKey(Constants.PARAM_DEVICEINFO)) {
            return order.getExtraField();
        }
        // 构建deviceInfo
        JSONArray deviceInfoArray = new JSONArray();
        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("iotDeviceCode", order.getDeviceCode());

        // 构建联动摄像机信息
        JSONArray relDeviceArray = new JSONArray();
        List<IotRelDeviceVo> iotDeviceConf = JSON.parseArray(order.getRelDeviceVo(), IotRelDeviceVo.class);
        if (iotDeviceConf != null && !iotDeviceConf.isEmpty()) {
            boolean isFirst = true;
            for (IotRelDeviceVo deviceSiteVO : iotDeviceConf) {
                JSONObject relDevice = new JSONObject();
                relDevice.put(Constants.PARAM_DEVICE_CODE, deviceSiteVO.getDeviceCode());
                // 将二次识别结果放在第一个摄像机中
                if (isFirst) {
                    relDevice.put("isSecondIdentify", order.getIsSecondIdentify());
                    relDevice.put("secondIdentifyResult", order.getSecondIdentifyResult());
                    relDevice.put("linkageAlgorithm", order.getLinkageAlgorithm());
                    isFirst = false;
                }
                relDeviceArray.add(relDevice);
            }
        }
        deviceInfo.put(Constants.PARAM_RELDEVICE, relDeviceArray);
        deviceInfoArray.add(deviceInfo);
        JSONArray monitorsArray = jsonObject.getJSONArray(Constants.PARAM_MONITORS);

        newJsonObject.put(Constants.PARAM_DEVICEINFO, deviceInfoArray);
        newJsonObject.put(Constants.PARAM_MONITORS, monitorsArray);
        return newJsonObject.toJSONString();
    }

    /**
     * 获取卫片执法告警的图斑信息
     * @param mergedOrderList
     * @return
     */
    private Map<String, List<Map<String, Object>>> getSatelliteLayerInfo(List<WarningOrderEntity> mergedOrderList, List<InterceptOrderEntity> mergedInterceptList) {
        //从扩展字段中获取到图层ID
        List<String> layerIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mergedOrderList)) {
            layerIds = mergedOrderList.stream()
                    .filter(order -> !StringUtils.isEmpty(order.getWarningTypeId()) && Arrays.asList(WPZF_WARNING_TYPE).contains(order.getWarningTypeId()))
                    .map(order -> {
                        if (order.getExtraField() == null) {
                            return null;
                        }
                        ReceiveRegionAlarmDto spot = JSONObject.parseObject(order.getExtraField(), ReceiveRegionAlarmDto.class);
                        return spot != null ? spot.getLayerId() : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(mergedInterceptList)) {
            layerIds = mergedInterceptList.stream()
                    .filter(order -> !StringUtils.isEmpty(order.getWarningTypeId()) && Arrays.asList(WPZF_WARNING_TYPE).contains(order.getWarningTypeId()))
                    .map(order -> {
                        ReceiveRegionAlarmDto spot = JSONObject.parseObject(order.getExtraField(), ReceiveRegionAlarmDto.class);
                        return spot != null ? spot.getLayerId() : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(layerIds)) {
            try {
                GeometrysReq geometrysReq = new GeometrysReq();
                geometrysReq.setLayerIds(layerIds);
                log.info("getSatelliteLayerInfo-调用gisLayerAPI.getGeometrys  req:{}", JSON.toJSONString(geometrysReq));
                Result<List<GeometrysRes>> geometrysResult = gisLayerAPI.getGeometrys(geometrysReq);
                log.info("getSatelliteLayerInfo-调用gisLayerAPI.getGeometrys  resp:{}", JSON.toJSONString(geometrysResult));
                if (geometrysResult != null && Objects.nonNull(geometrysResult.getData())) {
                    List<String> finalLayerIds = layerIds;
                    return geometrysResult.getData().stream()
                            .filter(item -> finalLayerIds.contains(item.getLayerId()))
                            .map(item -> {
                                Object coordinates = JSONObject.parseObject(JSON.toJSONString(item.getGeometry())).get("coordinates");
                                Map<String, Object> map = new HashMap<>();
                                map.put("geometry", JSON.toJSONString(coordinates));
                                map.put("geomeryType", item.getGeoType());
                                map.put(Constants.PARAM_LAYER_ID, item.getLayerId());
                                return map;
                            })
                            .collect(Collectors.groupingBy(item -> item.get(Constants.PARAM_LAYER_ID).toString()));
                }
            } catch (Exception e) {
                log.error("====getSatelliteLayerInfo-调用getGeometrys方法 error", e);
            }
        }
        return Collections.emptyMap();
    }

    /**
     * 拼接导出说明
     * @param order
     * @param item2
     * @param allUser
     */
    private void handlingInstructions(WarningOrderEntity order, OrderFlowRecordEntity item2, Map<String, String> allUser) {
        //各环节处理状态
        String status = "";
        //环节类型处理状态转派,回退,完成,未完成判断
        if (!Constants.SIX.equals(item2.getRealLinkType()) && !Constants.SEVEN.equals(item2.getRealLinkType()) && !Constants.ZERO.equals(item2.getRealLinkType())){
            if (Constants.ONE.equals(item2.getEnableAcceptanceMode())){
                status = Constants.PENDING_ACCEPTANCE;
            }else if (Constants.TWO.equals(item2.getEnableAcceptanceMode())){
                status = Constants.ACCEPTANCE_COMPLETED;
            }else if (Constants.ONE.equals(item2.getFlowLinkType())){
                status = Constants.REASSIGNMENT;
            }else if (Constants.TWO.equals(item2.getFlowLinkType())){
                status = Constants.FALLBACK;
            }else {
                if (Constants.ZERO.equals(item2.getIfCurrentLink())){
                    status=Constants.COMPLETE;
                }else {
                    status=Constants.NOCOMPLETE;
                }
            }
        }
        //研判环节类型处理状态误报，真警，重复告警判断
        if (Constants.ONE.equals(item2.getRealLinkType())){
            order.setCheckMan(item2.getProcessName());
            if (Constants.ZERO.equals(item2.getFlowLinkType())){
                if (Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.FALSEALARM;
                }else if (Constants.SEVEN.equals(item2.getNextLinkId())){
                    status = Constants.REPEATEDALARM;
                }else if (!org.springframework.util.StringUtils.isEmpty(item2.getNextLinkId()) && !Constants.SEVEN.equals(item2.getNextLinkId()) && (!Constants.SIX.equals(item2.getNextLinkId()) || (Constants.SIX.equals(item2.getNextLinkId()) && !Constants.CLOSE_ALARM.equals(item2.getRemark())) ) && !Constants.ZERO.equals(item2.getNextLinkId())){
                    status = Constants.TRUEPOLICE;
                }
            }
        }
        String remark = "";
        String userName = "";
        userName = allUser.get(item2.getProcessId());
        //拼接办理说明
        StringBuilder builder = new StringBuilder();
        if(!"-1".equals(item2.getRealLinkType())){
            if (Constants.SIX.equals(item2.getRealLinkType()) || Constants.SEVEN.equals(item2.getRealLinkType()) || Constants.ZERO.equals(item2.getRealLinkType())){
                //完结环节拼接
                remark =  builder.append(item2.getFlowNodeTime()).append(" ").append(Constants.CLOSED).toString();
            }else {
                remark = builder.append(item2.getFlowNodeTime()).append(" ").append(item2.getLinkName()).append("-").append(status).append("\r\n").append(Constants.HANDLEDBY).append(userName==null?"":userName).append("\r\n").append(Constants.HANDLINGINSTRUCTIONS).append(item2.getRemark()==null?"":item2.getRemark()).append("\r\n").toString();
            }
        }
        if (order.getCheckRemark()==null)order.setCheckRemark("");
        order.setCheckRemark(order.getCheckRemark()+remark);
    }

    /**
     * 运营研判基础数据整合
     * @param mergedOperateList
     * @param alarmOrderInfoParam
     */
    private void processMergedOperateListAsync(List<OperateOrderEntity> mergedOperateList, AlarmOrderInfoParam alarmOrderInfoParam) {
        Map<String, String> userInfoMap = alarmOrderInfoParam.getUserInfoMap();
        Map<String, String> industryMap = alarmOrderInfoParam.getIndustryConfMap();
        Map<String, String> presetInfoMap = alarmOrderInfoParam.getPresetInfoMap();
        Map<String, String> sourceMap = alarmOrderInfoParam.getWarningSourceMap();
        Map<Object, Object> typeMap = alarmOrderInfoParam.getAlarmTypeMap();
        Map<String, String> importanceLevelMap = alarmOrderInfoParam.getImportanceLevelMap();
        Map<String, String> emergencyLevelMap = alarmOrderInfoParam.getEmergencyLevelMap();
        Map<String, String> msgTypeMap = alarmOrderInfoParam.getMsgTypeMap();
        Map<String, String> noticeResourceMap = alarmOrderInfoParam.getNoticeResourceMap();
        Map<String, Map<String, Object>> gridInfoMap = alarmOrderInfoParam.getGridInfoMap();
        Map<String, DeviceInfoEntity> deviceInfoMap = alarmOrderInfoParam.getDeviceInfoMap();
        Map<String, DeviceInfoEntity> iotDeviceInfoMap = alarmOrderInfoParam.getIotDeviceInfoMap();
        Map<String, List<DeviceInfoEntity>> iotDeviceConfMap = alarmOrderInfoParam.getIotDeviceConfMap();
        Map<String, DeviceInfoEntity> radarDeviceInfoMap = alarmOrderInfoParam.getRadarDeviceInfoMap();
        Map<String, List<Map>> layerInfoMap = alarmOrderInfoParam.getLayerInfoMap();
        Map<String, String> platformDataMap = alarmOrderInfoParam.getPlatformDataMap();
        Map<String, String> algorithmNameAllMap = alarmOrderInfoParam.getAlgorithmNameAllMap();
        String platformCode = getPlatformNode();
        Map<String, List<String>> deviceLabelMap = alarmOrderInfoParam.getDeviceLabelMap();
        //查询算法配置信息拼接
        List<Map> map = synchLocalizationAlarmDAO.queryAlgorithmConfig();
        if (!CollectionUtils.isEmpty(mergedOperateList)) {
            mergedOperateList.forEach(order -> {
                ExtendFieldVO extendFieldVO = new ExtendFieldVO();
                //获取各属地化下沉节点编码
                if (!StringUtils.isEmpty(platformCode)) {
                    order.setAccessNode(platformCode); //接入节点
                    order.setAcceptNode(platformCode); //受理节点
                    order.setPlatformName(platformDataMap.get(platformCode));
                }
                //网格信息
                if (!StringUtils.isEmpty(order.getGridId())) {
                    Map<String, Object> gridListVO = gridInfoMap.get(order.getGridId());
                    if (gridListVO != null) {
                        order.setGridId(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_ID)) ? gridListVO.get(Constants.GRID_ID).toString() : null);
                        order.setGridName(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_NAME)) ? gridListVO.get(Constants.GRID_NAME).toString() : null);
                        order.setKeeperName(!ObjectUtils.isEmpty(gridListVO.get(Constants.KEEPER_NAME)) ? gridListVO.get(Constants.KEEPER_NAME).toString() : null);
                        //v1.6.60 新增字段放入扩展字段
                        extendFieldVO.setColor(!ObjectUtils.isEmpty(gridListVO.get(Constants.COLOR)) ? gridListVO.get(Constants.COLOR).toString() : null);
                        extendFieldVO.setBorderColor(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR)) ? gridListVO.get(Constants.BORDER_COLOR).toString() : null);
                        extendFieldVO.setBorderColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY).toString() : null);
                        extendFieldVO.setBorderColorLineThickness(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS)) ? gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS).toString() : null);
                        extendFieldVO.setColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.COLOR_TRANSPARENCY).toString() : null);
                    }
                }
                //图斑信息
                if ((layerInfoMap != null && !layerInfoMap.isEmpty()) && !StringUtils.isEmpty(order.getTaskId())) {
                    order.setPolygonOperate(JSON.toJSONString(layerInfoMap.get(order.getTaskId())));
                }
                //行业应用名称
                if (!StringUtils.isEmpty(order.getIndustryCode())) {
                    order.setIndustryName(industryMap.get(order.getIndustryCode()));
                }
                //研判人
                if (!StringUtils.isEmpty(order.getCreateUserName())) {
                    order.setJudgeUserName(userInfoMap.get(order.getCreateUserName()));
                }
                //预置位名称
                if (!StringUtils.isEmpty(order.getPresetId())) {
                    order.setPresetName(presetInfoMap.get(order.getPresetId()));
                }
                //告警来源名称
                if (!StringUtils.isEmpty(order.getWarningSource())) {
                    order.setWarningSourceName(sourceMap.get(order.getWarningSource()));
                }
                //联动算法名称
                if (!StringUtils.isEmpty(order.getLinkageAlgorithm())){
                    order.setLinkageAlgorithm(algorithmNameAllMap.get(order.getLinkageAlgorithm()));
                }
                //告警类型名称
                if (!StringUtils.isEmpty(order.getWarningTypeId())) {
                    if(typeMap.containsKey(order.getWarningTypeId())){
                        order.setWarningTypeName(String.valueOf(typeMap.get(order.getWarningTypeId())));
                    }
                }
                //重要等级名称
                if (!StringUtils.isEmpty(order.getImportanceLevel())) {
                    order.setImportanceLevelName(importanceLevelMap.get(order.getImportanceLevel()));
                }
                //告警等级名称
                if (!StringUtils.isEmpty(order.getEmergencyLevel())) {
                    order.setEmergencyLevelName(emergencyLevelMap.get(order.getEmergencyLevel()));
                }
                // 消息类型名称
                if (!StringUtils.isEmpty(order.getMsgType())) {
                    order.setMsgTypeName(msgTypeMap.get(order.getMsgType()));
                }
                // 通知类型名称
                if (!StringUtils.isEmpty(order.getNoticeResource())) {
                    order.setNoticeResourceName(noticeResourceMap.get(order.getNoticeResource()));
                }
                //物联、卫星、雷达设备信息
                if (!StringUtils.isEmpty(order.getDeviceCode())) {
                    if(WarningSourceEnum.IOT_ALARM.getTypeValue().equals(order.getWarningSource())){ //物联
                        DeviceInfoEntity iotDeviceInfo = iotDeviceInfoMap.get(order.getDeviceCode());
                        if (iotDeviceInfo != null) {
                            order.setDeviceAddress(iotDeviceInfo.getLocation());
                            order.setHeight(iotDeviceInfo.getHeight());
                            order.setModelId(iotDeviceInfo.getModelId());
                            order.setVideoLatitude(iotDeviceInfo.getLatitude());
                            order.setVideoLongitude(iotDeviceInfo.getLongitude());
                            order.setDeviceModelCode(iotDeviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(iotDeviceInfo.getModelFactoryName());
                            order.setVertiDownNeg(iotDeviceInfo.getVertiDownNeg());
                            order.setFocusRange(iotDeviceInfo.getFocusRange());
                        }
                        //查询物联设备联动摄像机信息
                        String key = order.getTenantId() + order.getIndustryCode() + order.getDeviceCode();
                        List<IotRelDeviceVo> relDeviceList = Optional.ofNullable(iotDeviceConfMap.get(key))
                                .map(list -> list.stream()
                                        .map(item -> {
                                            IotRelDeviceVo relDevice = new IotRelDeviceVo();
                                            BeanUtil.copyProperties(item, relDevice);
                                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                                relDevice.setDeviceLabel(deviceLabelMap.get(relDevice.getDeviceCode()));
                                            }
                                            return relDevice;
                                        })
                                        .collect(Collectors.toList()))
                                .orElse(Collections.emptyList());
                        order.setRelDeviceVo(JSON.toJSONString(relDeviceList));
                    } else if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(order.getWarningSource())) { //雷达
                        SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (deviceInfo != null) {
                            BeanUtil.copyProperties(deviceInfo,radarDevice);
                            radarDevice.setDeviceCode(order.getDeviceCode());
                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                radarDevice.setDeviceLabel(deviceLabelMap.get(radarDevice.getDeviceCode()));
                            }
                        }
                        //雷达设备联动摄像机信息
                        if(radarDevice != null){
                            order.setRadarDeviceVo(JSON.toJSONString(radarDevice));
                        } else {
                            order.setRadarDeviceVo("");
                        }
                        //从扩展字段获取雷达编码
                        Object[] radarCodes = null;
                        List<DeviceInfoEntity> deviceSiteList = new ArrayList<>();
                        if (!StringUtils.isEmpty(order.getExtraField())) {
                            try {
                                JSONObject jsonExtraField = JSONObject.parseObject(order.getExtraField());
                                try {
                                    if (jsonExtraField.containsKey(Constants.RADAR_CODES)) {
                                        radarCodes = jsonExtraField.getJSONArray(Constants.RADAR_CODES).toArray();
                                    }
                                } catch (Exception e) {
                                    log.error(Constants.ERR_MSG_RADAR+ order.getExtraField()+ e);
                                }
                            } catch (Exception e) {
                                log.error(Constants.ERR_MSG_JSON+ order.getExtraField()+ e);
                            }
                        }
                        if (radarCodes != null) {
                            for (Object radarCode : radarCodes) {
                                if(ObjectUtil.isEmpty(radarCode)){
                                    continue;
                                }
                                DeviceInfoEntity radarDeviceInfo = radarDeviceInfoMap.get(order.getTenantId() + order.getIndustryCode() + radarCode.toString());
                                if (radarDeviceInfo != null) {
                                    order.setDeviceAddress(radarDeviceInfo.getLocation());
                                    order.setHeight(radarDeviceInfo.getHeight());
                                    order.setModelId(radarDeviceInfo.getModelId());
                                    order.setVideoLatitude(radarDeviceInfo.getLatitude());
                                    order.setVideoLongitude(radarDeviceInfo.getLongitude());
                                    order.setDeviceModelCode(radarDeviceInfo.getDeviceModelCode());
                                    order.setModelFactoryName(radarDeviceInfo.getModelName());
                                    order.setVertiDownNeg(radarDeviceInfo.getVertiDownNeg());
                                    order.setFocusRange(radarDeviceInfo.getFocusRange());
                                }
                                deviceSiteList.add(radarDeviceInfo);
                            }
                        }
                        //雷达设备信息
                        if(!CollectionUtils.isEmpty(deviceSiteList)){
                            order.setDeviceSiteListVo(JSON.toJSONString(deviceSiteList));
                        } else {
                            order.setDeviceSiteListVo("");
                        }
                    } else { //AI、摄像机
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (!CollectionUtils.isEmpty(deviceLabelMap)){
                            extendFieldVO.setDeviceLabel(deviceLabelMap.get(order.getDeviceCode()));
                        }
                        if (deviceInfo != null) {
                            order.setDeviceAddress(deviceInfo.getLocation());
                            order.setHeight(deviceInfo.getHeight());
                            order.setModelId(deviceInfo.getModelId());
                            order.setVideoLatitude(deviceInfo.getLatitude());
                            order.setVideoLongitude(deviceInfo.getLongitude());
                            order.setDeviceModelCode(deviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(deviceInfo.getModelCodeName());
                            order.setVertiDownNeg(deviceInfo.getVertiDownNeg());
                            order.setFocusRange(deviceInfo.getFocusRange());
                            if(Constants.ONE.equals(deviceInfo.getAlarmLocation())){
                                order.setAlarmLocation(deviceInfo.getAlarmLocation());
                            }

                            //1.8.10 添加摄像机类型、摄像机子类型
                            extendFieldVO.setDeviceType(deviceInfo.getDeviceType());
                            extendFieldVO.setDeviceTypeName(deviceInfo.getDeviceTypeName());
                            extendFieldVO.setDeviceSubType(deviceInfo.getDeviceSubType());
                            extendFieldVO.setDeviceSubTypeName(deviceInfo.getDeviceSubTypeName());
                        }
                    }
                }
                //添加卫星告警信息
                if (Constants.ALARM_SOURCE_9.equals(order.getWarningSource()) && Objects.nonNull(order.getExtraField())) {
                    try {
                        SatelliteExtendFieldVO extendFieldVo = JSON.parseObject(order.getExtraField(),SatelliteExtendFieldVO.class);
                        String snapShotTime = extendFieldVo.getSnapShotTime(); //抓图时间
                        String realDeviceCode = extendFieldVo.getRealDeviceCode(); //联动摄像机编号
                        List<DeviceInfoEntity> satelliteRelDeviceVos = new ArrayList<>();
                        if (!StringUtils.isEmpty(realDeviceCode)) {
                            List<String> deviceCodeArray = Arrays.asList(realDeviceCode.split(Constants.COMMA));
                            for(String deviceCode : deviceCodeArray){
                                DeviceInfoEntity deviceInfo = deviceInfoMap.get(deviceCode);
                                if(!StringUtils.isEmpty(snapShotTime)){
                                    deviceInfo.setSnapShotTime(snapShotTime);
                                }
                                if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                    deviceInfo.setDeviceLabel(deviceLabelMap.get(deviceInfo));
                                }

                                satelliteRelDeviceVos.add(deviceInfo);
                            }
                        }
                        if(!CollectionUtils.isEmpty(satelliteRelDeviceVos)){
                            order.setSatelliteDeviceListVo(JSON.toJSONString(satelliteRelDeviceVos));
                        } else {
                            order.setSatelliteDeviceListVo("");
                        }
                    } catch (Exception e) {
                        log.error("运营研判-获取卫星告警信息失败，参数：{}", order.getExtraField(), e);
                    }
                }
                //水平方位角处理转换
                if (!StringUtils.isEmpty(order.getHoriAzimuthAngle())) {
                    order.setHoriAzimuthAngle(AngleUtils.transformAngle(order.getHoriAzimuthAngle()));
                }
                //转义PTZ
                String pNewValue = null;
                String tNewValue = null;
                String zNewValue = null;
                if (!StringUtils.isEmpty(order.getPtzP())
                        && !StringUtils.isEmpty(order.getPtzT())
                        && !StringUtils.isEmpty(order.getPtzZ()) && !StringUtils.isEmpty(order.getModelId())){
                    try{
                        String pValue = order.getPtzP();
                        String tValue = order.getPtzT();
                        String zValue = order.getPtzZ();
                        //获取摄像设备厂家编码
                        String modelId = order.getModelId();
                        //获取摄像机类型的焦距范围
                        String deviceFocusRange = order.getFocusRange();
                        //摄像机水平向下是否为负
                        String vertiDownNeg = order.getVertiDownNeg();
                        DeviceModelVO deviceModel = new DeviceModelVO();
                        deviceModel.setMfgrCode(modelId);
                        deviceModel.setVertiDownNeg(vertiDownNeg);
                        if (Constants.MODELID_DAHUA.equals(modelId) || Constants.MODELID_DHUA.equals(modelId)){
                            deviceModel.setFocusRange(deviceFocusRange);
                        }
                        pNewValue = LongLatAngleUtils.paramTrans(pValue, deviceModel, "P");
                        tNewValue = LongLatAngleUtils.paramTrans(tValue, deviceModel, "T");
                        zNewValue = LongLatAngleUtils.paramTrans(zValue, deviceModel, "Z");
                    }catch(Exception e){
                        log.error(Constants.ERR_MSG_PTZESCAPE, order.getWarningOrderId(), e);
                    }
                }
                //扩展字段设置默认值
                extendFieldVO.setDefaultField(Constants.ZERO);
                if(!StringUtils.isEmpty(pNewValue) && !StringUtils.isEmpty(tNewValue) && !StringUtils.isEmpty(zNewValue)){
                    extendFieldVO.setPtzTrans("P:"+pNewValue + " T:"+tNewValue + " Z:"+zNewValue);
                }
                if (!CollectionUtils.isEmpty(map)) {
                    // 使用流进行过滤，只保留符合条件的元素
                    List<Map> filteredList = map.stream()
                            .filter(m -> order.getTenantId() != null &&  order.getTenantId().equals(m.get(Constants.PARAM_TENANT_ID)) &&
                                    order.getTaskId() != null && order.getTaskId().equals(m.get(Constants.PARAM_TASK_ID))
                            ).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(filteredList) && order.getWarningSource().equals(Constants.ONE)){
                        extendFieldVO.setDescription(buildDescriptionField(filteredList));
                    }
                }
                extendFieldVO.setTransparentFields(order.getTransparentFields());
                order.setExtendFields(JSON.toJSONString(extendFieldVO));
            });
        }
    }

    /**
     * 回收站基础数据整合
     * @param mergedInterceptList
     * @param alarmOrderInfoParam
     */
    private void processMergedInterceptListAsync(List<InterceptOrderEntity> mergedInterceptList, AlarmOrderInfoParam alarmOrderInfoParam) {
        Map<String, String> presetInfoMap = alarmOrderInfoParam.getPresetInfoMap();
        Map<String, String> industryMap = alarmOrderInfoParam.getIndustryConfMap();
        Map<String, String> sourceMap = alarmOrderInfoParam.getWarningSourceMap();
        Map<Object, Object> typeMap = alarmOrderInfoParam.getAlarmTypeMap();
        Map<String, String> importanceLevelMap = alarmOrderInfoParam.getImportanceLevelMap();
        Map<String, String> emergencyLevelMap = alarmOrderInfoParam.getEmergencyLevelMap();
        Map<String, String> msgTypeMap = alarmOrderInfoParam.getMsgTypeMap();
        Map<String, String> noticeResourceMap = alarmOrderInfoParam.getNoticeResourceMap();
        Map<String, String> pressiongReasonMap = alarmOrderInfoParam.getPressiongReasonMap();
        Map<String, Map<String, Object>> gridInfoMap = alarmOrderInfoParam.getGridInfoMap();
        Map<String, DeviceInfoEntity> deviceInfoMap = alarmOrderInfoParam.getDeviceInfoMap();
        Map<String, DeviceInfoEntity> iotDeviceInfoMap = alarmOrderInfoParam.getIotDeviceInfoMap();
        Map<String, List<DeviceInfoEntity>> iotDeviceConfMap = alarmOrderInfoParam.getIotDeviceConfMap();
        Map<String, DeviceInfoEntity> radarDeviceInfoMap = alarmOrderInfoParam.getRadarDeviceInfoMap();
        Map<String, List<Map>> layerInfoMap = alarmOrderInfoParam.getLayerInfoMap();
        Map<String, String> platformDataMap = alarmOrderInfoParam.getPlatformDataMap();
        Map<String, String> algorithmNameAllMap = alarmOrderInfoParam.getAlgorithmNameAllMap();
        Map<String, List<DeviceInfoEntity>> uavDeviceMap = alarmOrderInfoParam.getUavDeviceMap();
        Map<String, List<UavNestDeviceVo>> uavNestDeviceMap = alarmOrderInfoParam.getUavDeviceNestMap();
        String platformCode = getPlatformNode();
        Map<String, List<String>> deviceLabelMap = alarmOrderInfoParam.getDeviceLabelMap();
        //查询算法配置信息拼接
        List<Map> map = synchLocalizationAlarmDAO.queryAlgorithmConfig();
        if (!CollectionUtils.isEmpty(mergedInterceptList)) {
            Map<String, List<Map<String, Object>>> satelliteLayerInfoMap = getSatelliteLayerInfo(null, mergedInterceptList);
            mergedInterceptList.forEach(order -> {
                ExtendFieldVO extendFieldVO = new ExtendFieldVO();
                //获取各属地化下沉节点编码
                if (!StringUtils.isEmpty(platformCode)) {
                    order.setAccessNode(platformCode); //接入节点
                    order.setAcceptNode(platformCode); //受理节点
                    order.setPlatformName(platformDataMap.get(platformCode));
                }
                //网格信息
                if (!StringUtils.isEmpty(order.getGridId())) {
                    Map<String, Object> gridListVO = gridInfoMap.get(order.getGridId());
                    if (gridListVO != null) {
                        order.setGridId(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_ID)) ? gridListVO.get(Constants.GRID_ID).toString() : null);
                        order.setGridCode(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_CODE)) ? gridListVO.get(Constants.GRID_CODE).toString() : null);
                        order.setGridName(!ObjectUtils.isEmpty(gridListVO.get(Constants.GRID_NAME)) ? gridListVO.get(Constants.GRID_NAME).toString() : null);
                        order.setKeeperName(!ObjectUtils.isEmpty(gridListVO.get(Constants.KEEPER_NAME)) ? gridListVO.get(Constants.KEEPER_NAME).toString() : null);
                        extendFieldVO.setBorderColor(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR)) ? gridListVO.get(Constants.BORDER_COLOR).toString() : null);
                        extendFieldVO.setBorderColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.BORDER_COLOR_TRANSPARENCY).toString() : null);
                        extendFieldVO.setBorderColorLineThickness(!ObjectUtils.isEmpty(gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS)) ? gridListVO.get(Constants.BORDER_COLOR_LINE_THICKNESS).toString() : null);
                        extendFieldVO.setColorTransparency(!ObjectUtils.isEmpty(gridListVO.get(Constants.COLOR_TRANSPARENCY)) ? gridListVO.get(Constants.COLOR_TRANSPARENCY).toString() : null);
                    }
                }
                //行业应用名称
                if (!StringUtils.isEmpty(order.getIndustryCode())) {
                    order.setIndustryName(industryMap.get(order.getIndustryCode()));
                }
                //图斑信息
                if ((layerInfoMap != null && !layerInfoMap.isEmpty()) && !StringUtils.isEmpty(order.getTaskId())) {
                    order.setPolygonOperate(JSON.toJSONString(layerInfoMap.get(order.getTaskId())));
                }
                //卫片执法告警-图斑信息
                if(!StringUtils.isEmpty(order.getWarningTypeId()) && Arrays.asList(WPZF_WARNING_TYPE).contains(order.getWarningTypeId())){
                    if((satelliteLayerInfoMap != null && !satelliteLayerInfoMap.isEmpty()) && !StringUtils.isEmpty(order.getExtraField())){
                        //从扩展字段中获取到图层ID
                        ReceiveRegionAlarmDto spot = JSONObject.parseObject(order.getExtraField(), ReceiveRegionAlarmDto.class);
                        if(!StringUtils.isEmpty(spot.getLayerId())){
                            order.setPolygonOperate(JSON.toJSONString(satelliteLayerInfoMap.get(spot.getLayerId())));
                        }
                    }
                }
                //扩展字段设置默认值
                extendFieldVO.setDefaultField(Constants.ZERO);
                //order.setExtendFields(JSON.toJSONString(extendFieldVO));
                //预置位名称
                if (!StringUtils.isEmpty(order.getPresetId())) {
                    order.setPresetName(presetInfoMap.get(order.getPresetId()));
                }
                //告警来源名称
                if (!StringUtils.isEmpty(order.getWarningSource())) {
                    order.setWarningSourceName(sourceMap.get(order.getWarningSource()));
                }
                //联动算法名称
                if (!StringUtils.isEmpty(order.getLinkageAlgorithm())){
                    order.setLinkageAlgorithm(algorithmNameAllMap.get(order.getLinkageAlgorithm()));
                }
                //告警类型名称
                if (!StringUtils.isEmpty(order.getWarningTypeId())) {
                    if(typeMap.containsKey(order.getWarningTypeId())){
                        order.setWarningTypeName(String.valueOf(typeMap.get(order.getWarningTypeId())));
                    }
                }
                //重要等级名称
                if (!StringUtils.isEmpty(order.getImportanceLevel())) {
                    order.setImportanceLevelName(importanceLevelMap.get(order.getImportanceLevel()));
                }
                //告警等级名称
                if (!StringUtils.isEmpty(order.getEmergencyLevel())) {
                    order.setEmergencyLevelName(emergencyLevelMap.get(order.getEmergencyLevel()));
                }
                // 消息类型名称
                if (!StringUtils.isEmpty(order.getMsgType())) {
                    order.setMsgTypeName(msgTypeMap.get(order.getMsgType()));
                }
                // 通知类型名称
                if (!StringUtils.isEmpty(order.getNoticeResource())) {
                    order.setNoticeResourceName(noticeResourceMap.get(order.getNoticeResource()));
                }
                // 告警压制原因
                if (!StringUtils.isEmpty(order.getIntercept())) {
                    order.setInterceptName(pressiongReasonMap.get(order.getIntercept()));
                }
                //物联、卫星、雷达设备信息
                if (!StringUtils.isEmpty(order.getDeviceCode())) {
                    if(WarningSourceEnum.IOT_ALARM.getTypeValue().equals(order.getWarningSource())){ //物联
                        DeviceInfoEntity iotDeviceInfo = iotDeviceInfoMap.get(order.getDeviceCode());
                        if (iotDeviceInfo != null) {
                            order.setDeviceAddress(iotDeviceInfo.getLocation());
                            order.setHeight(iotDeviceInfo.getHeight());
                            order.setModelId(iotDeviceInfo.getModelId());
                            order.setVideoLatitude(iotDeviceInfo.getLatitude());
                            order.setVideoLongitude(iotDeviceInfo.getLongitude());
                            order.setDeviceLatitude(iotDeviceInfo.getLatitude());
                            order.setDeviceLongitude(iotDeviceInfo.getLongitude());
                            order.setDeviceModelCode(iotDeviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(iotDeviceInfo.getModelFactoryName());
                            order.setVertiDownNeg(iotDeviceInfo.getVertiDownNeg());
                            order.setFocusRange(iotDeviceInfo.getFocusRange());
                        }
                        //查询物联设备联动摄像机信息
                        String key = order.getTenantId() + order.getIndustryCode() + order.getDeviceCode();
                        List<IotRelDeviceVo> relDeviceList = Optional.ofNullable(iotDeviceConfMap.get(key))
                                .map(list -> list.stream()
                                        .map(item -> {
                                            IotRelDeviceVo relDevice = new IotRelDeviceVo();
                                            BeanUtil.copyProperties(item, relDevice);
                                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                                relDevice.setDeviceLabel(deviceLabelMap.get(relDevice.getDeviceCode()));
                                            }
                                            return relDevice;
                                        })
                                        .collect(Collectors.toList()))
                                .orElse(Collections.emptyList());
                        order.setRelDeviceVo(JSON.toJSONString(relDeviceList));
                        // 1.6.90 新增返回多组物联设备信息，以及每个物联设备联动的多组摄像机信息
                        WarningOrderEntity interceptOrder = new WarningOrderEntity();
                        interceptOrder.setDeviceCode(order.getDeviceCode());
                        interceptOrder.setIsSecondIdentify(order.getIsSecondIdentify());
                        interceptOrder.setSecondIdentifyResult(order.getSecondIdentifyResult());
                        interceptOrder.setLinkageAlgorithm(order.getLinkageAlgorithm());
                        interceptOrder.setExtraField(order.getExtraField());
                        interceptOrder.setRelDeviceVo(order.getRelDeviceVo());
                        extendFieldVO.setIotDevInfoList(buildMultIotDeviceInfo(interceptOrder, iotDeviceInfoMap, algorithmNameAllMap,deviceLabelMap));
                    } else if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(order.getWarningSource())) { //雷达
                        SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (deviceInfo != null) {
                            BeanUtil.copyProperties(deviceInfo,radarDevice);
                            radarDevice.setDeviceCode(order.getDeviceCode());
                            if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                radarDevice.setDeviceLabel(deviceLabelMap.get(radarDevice.getDeviceCode()));
                            }
                        }
                        //查询雷达设备联动摄像机信息
                        if(radarDevice != null){
                            order.setRadarDeviceVo(JSON.toJSONString(radarDevice));
                        } else {
                            order.setRadarDeviceVo("");
                        }

                        //从扩展字段获取雷达编码
                        Object[] radarCodes = null;
                        List<DeviceInfoEntity> deviceSiteList = new ArrayList<>();
                        if (!StringUtils.isEmpty(order.getExtraField())) {
                            try {
                                JSONObject jsonExtraField = JSONObject.parseObject(order.getExtraField());
                                try {
                                    if (jsonExtraField.containsKey(Constants.RADAR_CODES)) {
                                        radarCodes = jsonExtraField.getJSONArray(Constants.RADAR_CODES).toArray();
                                    }
                                } catch (Exception e) {
                                    log.error(Constants.ERR_MSG_RADAR+ order.getExtraField()+ e);
                                }
                            } catch (Exception e) {
                                log.error(Constants.ERR_MSG_JSON+ order.getExtraField()+ e);
                            }
                        }
                        if (radarCodes != null) {
                            for (Object radarCode : radarCodes) {
                                if(ObjectUtil.isEmpty(radarCode)){
                                    continue;
                                }
                                DeviceInfoEntity radarDeviceInfo = radarDeviceInfoMap.get(order.getTenantId() + order.getIndustryCode() + radarCode.toString());
                                if (radarDeviceInfo != null) {
                                    order.setDeviceAddress(radarDeviceInfo.getLocation());
                                    order.setHeight(radarDeviceInfo.getHeight());
                                    order.setModelId(radarDeviceInfo.getModelId());
                                    order.setVideoLatitude(radarDeviceInfo.getLatitude());
                                    order.setVideoLongitude(radarDeviceInfo.getLongitude());
                                    order.setDeviceModelCode(radarDeviceInfo.getDeviceModelCode());
                                    order.setModelFactoryName(radarDeviceInfo.getModelName());
                                    order.setVertiDownNeg(radarDeviceInfo.getVertiDownNeg());
                                    order.setFocusRange(radarDeviceInfo.getFocusRange());
                                }
                                deviceSiteList.add(radarDeviceInfo);
                            }
                        }
                        //雷达设备信息
                        if(!CollectionUtils.isEmpty(deviceSiteList)){
                            order.setDeviceSiteListVo(JSON.toJSONString(deviceSiteList));
                        }else {
                            order.setDeviceSiteListVo("");
                        }
                    }  else if (WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue().equals(order.getWarningSource())) { //无人机
                        String deviceCode = order.getDeviceCode();
                        extendFieldVO.setUavDevInfoList(buildMultUavDeviceInfo(deviceCode, uavDeviceMap, uavNestDeviceMap));
                    }  else { //AI、摄像机
                        DeviceInfoEntity deviceInfo = deviceInfoMap.get(order.getDeviceCode());
                        if (!CollectionUtils.isEmpty(deviceLabelMap)){
                            extendFieldVO.setDeviceLabel(deviceLabelMap.get(order.getDeviceCode()));
                        }
                        if (deviceInfo != null) {
                            order.setDeviceAddress(deviceInfo.getLocation());
                            order.setHeight(deviceInfo.getHeight());
                            order.setModelId(deviceInfo.getModelId());
                            order.setVideoLatitude(deviceInfo.getLatitude());
                            order.setVideoLongitude(deviceInfo.getLongitude());
                            order.setDeviceModelCode(deviceInfo.getDeviceModelCode());
                            order.setModelFactoryName(deviceInfo.getModelCodeName());
                            order.setVertiDownNeg(deviceInfo.getVertiDownNeg());
                            order.setFocusRange(deviceInfo.getFocusRange());
                            if(Constants.ONE.equals(deviceInfo.getAlarmLocation())){
                                order.setAlarmLocation(deviceInfo.getAlarmLocation());
                            }

                            //1.8.10 添加摄像机类型、摄像机子类型
                            extendFieldVO.setDeviceType(deviceInfo.getDeviceType());
                            extendFieldVO.setDeviceTypeName(deviceInfo.getDeviceTypeName());
                            extendFieldVO.setDeviceSubType(deviceInfo.getDeviceSubType());
                            extendFieldVO.setDeviceSubTypeName(deviceInfo.getDeviceSubTypeName());
                        }
                    }
                }
                //添加卫星告警信息
                if (Constants.ALARM_SOURCE_9.equals(order.getWarningSource()) && Objects.nonNull(order.getExtraField())) {
                    try {
                        SatelliteExtendFieldVO extendFieldVo = JSON.parseObject(order.getExtraField(),SatelliteExtendFieldVO.class);
                        String snapShotTime = extendFieldVo.getSnapShotTime(); //抓图时间
                        String realDeviceCode = extendFieldVo.getRealDeviceCode(); //联动摄像机编号
                        List<DeviceInfoEntity> satelliteRelDeviceVos = new ArrayList<>();
                        if (!StringUtils.isEmpty(realDeviceCode)) {
                            List<String> deviceCodeArray = Arrays.asList(realDeviceCode.split(Constants.COMMA));
                            for(String deviceCode : deviceCodeArray){
                                DeviceInfoEntity deviceInfo = deviceInfoMap.get(deviceCode);
                                if(!StringUtils.isEmpty(snapShotTime)){
                                    deviceInfo.setSnapShotTime(snapShotTime);
                                }
                                if (!CollectionUtils.isEmpty(deviceLabelMap)){
                                    deviceInfo.setDeviceLabel(deviceLabelMap.get(deviceCode));
                                }
                                satelliteRelDeviceVos.add(deviceInfo);
                            }
                        }
                        if(!CollectionUtils.isEmpty(satelliteRelDeviceVos)){
                            order.setSatelliteDeviceListVo(JSON.toJSONString(satelliteRelDeviceVos));
                        }else {
                            order.setSatelliteDeviceListVo("");
                        }
                    } catch (Exception e) {
                        log.error("回收站-获取卫星告警信息失败，参数：{}", order.getExtraField(), e);
                    }
                }
                //水平方位角处理转换
                if (!StringUtils.isEmpty(order.getHoriAzimuthAngle())) {
                    order.setHoriAzimuthAngle(AngleUtils.transformAngle(order.getHoriAzimuthAngle()));
                }
                //转义PTZ
                if (!StringUtils.isEmpty(order.getPan())
                        && !StringUtils.isEmpty(order.getTilt())
                        && !StringUtils.isEmpty(order.getZoom()) && !StringUtils.isEmpty(order.getModelId())){
                    try{
                        String pValue = order.getPan();
                        String tValue = order.getTilt();
                        String zValue = order.getZoom();
                        //获取摄像设备厂家编码
                        String modelId = order.getModelId();
                        //获取摄像机类型的焦距范围
                        String deviceFocusRange = order.getFocusRange();
                        //摄像机水平向下是否为负
                        String vertiDownNeg = order.getVertiDownNeg();
                        DeviceModelVO deviceModel = new DeviceModelVO();
                        deviceModel.setMfgrCode(modelId);
                        deviceModel.setVertiDownNeg(vertiDownNeg);
                        if (Constants.MODELID_DAHUA.equals(modelId) || Constants.MODELID_DHUA.equals(modelId)){
                            deviceModel.setFocusRange(deviceFocusRange);
                        }
                        String pNewValue = LongLatAngleUtils.paramTrans(pValue, deviceModel, "P");
                        String tNewValue = LongLatAngleUtils.paramTrans(tValue, deviceModel, "T");
                        String zNewValue = LongLatAngleUtils.paramTrans(zValue, deviceModel, "Z");
                        order.setPtzTransP(pNewValue);
                        order.setPtzTransT(tNewValue);
                        order.setPtzTransZ(zNewValue);
                    }catch(Exception e){
                        log.error(Constants.ERR_MSG_PTZESCAPE, order.getWarningOrderId(), e);
                    }
                }
                if (!CollectionUtils.isEmpty(map)) {
                    // 使用流进行过滤，只保留符合条件的元素
                    List<Map> filteredList = map.stream()
                            .filter(m -> order.getTenantId() != null &&  order.getTenantId().equals(m.get(Constants.PARAM_TENANT_ID)) &&
                                    order.getTaskId() != null && order.getTaskId().equals(m.get(Constants.PARAM_TASK_ID))
                            ).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(filteredList)  && order.getWarningSource().equals(Constants.ONE)){
                        extendFieldVO.setDescription(buildDescriptionField(filteredList));
                    }
                }
                extendFieldVO.setTransparentFields(order.getTransparentFields());
                order.setExtendFields(JSON.toJSONString(extendFieldVO));
            });
        }
    }

    /**
     * 查询工单表数据
     * @param date 当前月
     * @param lastMonth 上个月
     * @return
     */
    private List<WarningOrderEntity> mergeOrderLists(Date date, String lastMonth, int offset, int pageSize) {
        String currentTableName = TableUtil.getTableName(date, Constants.WARN_ORDER_TEBLE_);
        String lastTableName = TableUtil.getTableName(DateUtils.stringToDateYM(lastMonth), Constants.WARN_ORDER_TEBLE_);
        return localizationAlarmDAO.queryOrderList(currentTableName,lastTableName,
                DateUtils.getStartTimeOfDay(date),DateUtils.getStartTimeOfNextDay(date),offset,pageSize);
    }

    /**
     * 查询工单流转记录数据
     * @param date 当前月
     * @param lastMonth 上个月
     * @return
     */
    private List<OrderFlowRecordEntity> mergeRecordLists(Date date, String lastMonth, List<String> orderIdList) {
        String recordTableName = TableUtil.getTableName(date, Constants.WARN_ORDER_PROCESS_TEBLE_);
        String lastRecordTableName = TableUtil.getTableName(DateUtils.stringToDateYM(lastMonth), Constants.WARN_ORDER_PROCESS_TEBLE_);
        List<OrderFlowRecordEntity> mergedRecordList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderIdList)) {
            List<List<String>> orderIdListArray = Lists.partition(orderIdList, 5000);
            orderIdListArray.forEach(item -> {
                List<OrderFlowRecordEntity> recordList = localizationAlarmDAO.queryFlowRecordList(recordTableName, item);
                List<OrderFlowRecordEntity> lastRecordList = localizationAlarmDAO.queryFlowRecordList(lastRecordTableName, item);
                if(!CollectionUtils.isEmpty(recordList)){
                    mergedRecordList.addAll(recordList);
                }
                if(!CollectionUtils.isEmpty(recordList)){
                    mergedRecordList.addAll(lastRecordList);
                }
            });
        }
        //获取环节选择处理人
        List<String> userNameAllList = new ArrayList<>();
        List<String> processNameAllList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mergedRecordList)) {
            for (OrderFlowRecordEntity record: mergedRecordList) {
                if(!StringUtils.isEmpty(record.getProcessNameList())){
                    userNameAllList.addAll(Arrays.asList(record.getProcessNameList().split(Constants.COMMA)));
                }
                if(!StringUtils.isEmpty(record.getProcessName())){
                    processNameAllList.add(record.getProcessName());
                }
                if (!StringUtils.isEmpty(record.getAcceptProcessId())) {
                    userNameAllList.add(record.getAcceptProcessId());
                }
            }
            List<SysUser> userAllList = new ArrayList<>();
            List<SysUser> processUserAllList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(userNameAllList)){
                userAllList = alarmDetailedInfoDAO.getUserInfoById(userNameAllList);
            }
            if(!CollectionUtils.isEmpty(processNameAllList)){
                processUserAllList = alarmDetailedInfoDAO.getUserInfoByName(processNameAllList);
            }
            List<SysUser> finalUserAllList = userAllList;
            List<SysUser> finalProcessUserAllList = processUserAllList;
            mergedRecordList.forEach(record -> {
                if(!StringUtils.isEmpty(record.getAcceptProcessId())){
                    //匹配受理人姓名
                    if (!CollectionUtils.isEmpty(finalUserAllList)) {
                        for (SysUser sysUser : finalUserAllList) {
                            if(record.getAcceptProcessId().equals(sysUser.getUserId())){
                                record.setAcceptProcessName(!StringUtils.isEmpty(sysUser.getNickName()) ? sysUser.getNickName() : sysUser.getUserName());
                            }
                        }
                    }
                } else {
                    //没有受理人的话，设置受理人值为空
                    record.setAcceptProcessName("");
                }
                //获取环节选择的所有处理人姓名
                String processNameList = this.getProcessNamaList(finalUserAllList,record);
                record.setProcessNameList(processNameList);
                record.setNextLinkProcessName(processNameList); //下个环节处理人
                //获取当前环节处理人姓名
                if (!StringUtils.isEmpty(record.getProcessName())) {
                    finalProcessUserAllList.forEach(processNameDto -> {
                        if(processNameDto.getUserName().equals(record.getProcessName())){
                            //处理研判人 研判人名称
                            record.setProcessName(processNameDto.getUserName());
                            record.setProcessUserName(processNameDto.getNickName());
                        }
                    });
                }
            });
        }
        return mergedRecordList;
    }

    /**
     * 获取环节选择处理人
     * @param finalUserAllList
     * @param record
     * @return
     */
    private String getProcessNamaList(List<SysUser> finalUserAllList, OrderFlowRecordEntity record) {
        List<String> processNames = new ArrayList<>();
        if(!org.springframework.util.StringUtils.isEmpty(record.getProcessNameList())){
            List<String> thisUserIdList = Arrays.asList(record.getProcessNameList().split(Constants.COMMA));
            for (String userId : thisUserIdList) {
                for (SysUser sysInfo: finalUserAllList) {
                    if(userId.equals(sysInfo.getUserId())){
                        processNames.add(!StringUtils.isEmpty(sysInfo.getNickName()) ? sysInfo.getNickName() : sysInfo.getUserName());
                    }
                }
            }
        }
        return String.join(Constants.COMMA, processNames);
    }

    /**
     * 获取日期（根据入参来判断是同步当天还是指定某一天的告警）
     * @param alarmOrderInfoParam
     * @return
     */
    private Date getDateMethod(AlarmOrderInfoParam alarmOrderInfoParam){
        SimpleDateFormat inputFormat = new SimpleDateFormat(YYYYMMDD_FORMAT);
        Date date = null;
        try {
            if (!StringUtils.isEmpty(alarmOrderInfoParam.getDateParam())) {
                date = inputFormat.parse(alarmOrderInfoParam.getDateParam());
            } else {
                date = new Date();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取网格信息
     * @return
     */
    private List<Map<String, Object>> getGrifInfo() {
        List<Map<String, Object>> gridListVOList = alarmDetailedInfoDAO.getGridList();
        if (!CollectionUtils.isEmpty(gridListVOList)) {
            List<GridListVO> keepers = alarmDetailedInfoDAO.getKeeperLeader(
                    getGridIds(JSONArray.parseArray(JSONArray.toJSONString(gridListVOList))));
            if (!CollectionUtils.isEmpty(keepers)) {
                for (Map vo : gridListVOList) {
                    Object gridIdObj = vo.get(Constants.GRID_ID);
                    if (gridIdObj != null) {
                        String gridId = String.valueOf(gridIdObj);
                        keepers.stream()
                                .filter(item -> item.getGridId() != null && gridId.equals(item.getGridId()))
                                .findFirst()
                                .ifPresent(item -> vo.put(Constants.KEEPER_NAME, item.getKeeperName()));
                    }
                }
            }
        }
        return gridListVOList;
    }

    /**
     * 转换数据类型
     * @param json
     * @return
     */
    private List<String> getGridIds(JSONArray json) {
        List<String> list = new ArrayList<>();
        for (Object object : json) {
            JSONObject obj = (JSONObject) object;
            String gridId = obj.getString(Constants.GRID_ID);
            list.add(gridId);
        }
        return list;
    }

    /**
     * 根据枚举类型获取枚举列表
     * @param dictType
     * @return
     */
    public List<SysDictDataResponse> getEnumList(String dictType) {
        List<SysDictDataResponse> list = alarmDetailedInfoDAO.getDictDataByType(dictType);
        return list;
    }

    /**
     *
     * @param algorithmConfigVOS 配置算法列表
     */
    private String buildDescriptionField(List<Map> algorithmConfigVOS) {
        //防止传参问题，匹配多个算法
        List<Map<String, Object>> descList = algorithmConfigVOS.stream().map(algorithmConfigVO -> {
            Map<String, Object> map1 = Maps.newHashMap();
            try {
                //算法分析时段
                Object analysisPeriod = algorithmConfigVO.get("analysisPeriod");
                if (analysisPeriod != null) {
                    map1.put(DESC_ANALYSIS_PERIOD_RESP, JSON.parse(analysisPeriod.toString()));
                }
                //识别频率
                Object timeInterval = algorithmConfigVO.get("timeInterval");
                Object timeIntervalUnit = algorithmConfigVO.get("timeIntervalUnit");
                if (timeInterval != null && timeIntervalUnit != null) {
                    map1.put(DESC_TIMEINTERVAL_RESP, timeUnitConvertInverse(
                            Integer.parseInt(timeIntervalUnit.toString()),
                            Integer.parseInt(timeInterval.toString())
                    ));
                    //识别频率单位 1:小时 2:分钟 3:秒
                    map1.put(DESC_TIMEINTERVAL_UNIT_RESP, timeIntervalUnit);
                }
                //配置参数
                Object modelConfig = algorithmConfigVO.get("modelConfig");
                if (modelConfig != null) {
                    map1.put(DESC_MODEL_CONFIG_RESP, JSON.parse(modelConfig.toString()));
                }
            } catch (NumberFormatException e) { //处理转换异常
                log.error("NumberFormatException occurred: ", e);
            } catch (Exception e) {
                log.error("Exception occurred: ", e);
            }
            return map1;
        }).collect(Collectors.toList());
        //组装json，塞入description字段，转换为JSON时保存null
        return JSON.toJSONString(descList, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 根据时间单位，转换为秒（逆转）
     * @param timeIntervalUnit 抽帧频率单位,1:小时 2:分钟 3:秒
     * @param timeInterval 时间
     */
    private Integer timeUnitConvertInverse(Integer timeIntervalUnit, Integer timeInterval){
        Integer result = null;
        if(!ObjectUtil.isEmpty(timeIntervalUnit)){
            switch (timeIntervalUnit)
            {
                case com.chinatower.platform.constants.Constants.ONE_NUM:
                    result = timeInterval / 60 / 60;
                    break;
                case com.chinatower.platform.constants.Constants.TWO_NUM:
                    result = timeInterval / 60;
                    break;
                case com.chinatower.platform.constants.Constants.THREE_NUM:
                    result = timeInterval;
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
