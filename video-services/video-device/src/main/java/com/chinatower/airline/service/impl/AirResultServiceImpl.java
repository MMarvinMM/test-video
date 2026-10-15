package com.chinatower.airline.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.request.*;
import com.chinatower.airline.dto.request.AirResultTaskReq;
import com.chinatower.airline.dto.response.*;
import com.chinatower.airline.mapper.AirResultMapper;
import com.chinatower.airline.mapper.AirTaskMapper;
import com.chinatower.airline.service.AirResultService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.cache.MongoTemplateDelegate;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.OrderTypeDto;
import com.chinatower.device.entity.dto.*;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondRecogBatchVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.IVideoPresetService;
import com.chinatower.video.api.entity.PresetImgJobDTO;
import com.chinatower.video.api.entity.VmVideoPresetSnapshotMongo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

/**
 * 飞控结果处理类
 */
@Slf4j
@Service
public class AirResultServiceImpl implements AirResultService {
    @Resource
    private AirResultMapper airResultMapper;
    @Resource
    private UavOkhttpUtil okhttpUtil;

    @Autowired
    private MongoTemplateDelegate mongoTemplate;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Resource
    private AirTaskMapper airTaskMapper;

    private static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String TIME_FORMAT_STR = "yyyyMMddHHmm";

    private static String DATE_CHAR= "saveDateTime";
    private static String RECORDS= "records";

    private static String TOTAL = "totalCount";

    private static String TASK_RECORD_ID = "taskRecordId";
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private SystemFeignClient systemFeignClient;
    @Resource
    private IVideoPresetService iVideoPresetService;

    @Override
    public Result photoVideoDelete(AirResultReq airResultReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airResultReq.setAcceptNode(platformCode);
        String result = "";
        if (ObjectUtil.isNotNull(airResultReq)) {
            log.info("调用飞控批量删除图片/视频成果物入参接口：" + JSON.toJSONString(airResultReq));
            //删除指定图片和视频就不要传任务架次记录ID taskRecordId
            if (CollUtil.isNotEmpty(airResultReq.getId())) {
                airResultReq.setTaskRecordId(null);
            }
            String joIn = String.valueOf(JSONObject.toJSON(airResultReq));
            NetConnectUtils.RequestParamter<AirResultReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(airResultReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PHOTO_VIDEO_DELETE);
            path.setDataType(airResultReq.getDataType());
            paramter.setPath(path);
            try {
                log.info("调用飞控批量删除图片/视频成果物入参：" + joIn);
                ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, airResultReq.getAccessNode(), platformCode);
                if (ObjectUtil.isNotNull(cr)) {
                    result = JSON.parseObject(JSON.toJSONString(cr.getData()), String.class);
                    String dataType = "";
                    if (com.chinatower.airline.constants.Constants.PHOTO.equals(airResultReq.getDataType())) {
                        dataType = Constants.FOUR;
                    }
                    if (com.chinatower.airline.constants.Constants.VIDEO.equals(airResultReq.getDataType())) {
                        dataType = Constants.TWO;
                    }
                    airResultMapper.updateCollectStatus(airResultReq.getId(), dataType);
                }
            } catch (Exception e) {
                log.error("调用飞控批量删除图片/视频成果物失败:{}", e);
            }
        }
        return Result.data(result);
    }

    @Override
    @Transactional
    public Result photoVideoRename(AirResultRenameReq airResultRenameReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airResultRenameReq.setAcceptNode(platformCode);
        String result = "";
        if (ObjectUtil.isNotNull(airResultRenameReq.getAltitude())){
            airResultRenameReq.setHeight(Double.valueOf(airResultRenameReq.getAltitude()));
        }
        if (ObjectUtil.isNotNull(airResultRenameReq)) {
            String joIn = String.valueOf(JSONObject.toJSON(airResultRenameReq));
            NetConnectUtils.RequestParamter<AirResultRenameReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(airResultRenameReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PHOTO_VIDEO_RENAME);
            path.setDataType(airResultRenameReq.getDataType());
            paramter.setPath(path);
            try {
                log.info("调用飞控重命名图片/视频成果物入参：" + joIn);
                String watermarkUrl = null;
                ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, airResultRenameReq.getAccessNode(), platformCode);
                if (ObjectUtil.isNotNull(resultEntity)) {
                    JSONObject jsonObject = (JSONObject) resultEntity.getData();
                    if (jsonObject != null && jsonObject.containsKey("url")) {
                        Object urlObj = jsonObject.get("url");
                        // 避免url为null或空字符串
                        if (urlObj != null && StringUtils.isNotBlank(urlObj.toString())) {
                            watermarkUrl = urlObj.toString();
                            airResultRenameReq.setWatermarkUrl(watermarkUrl); // 设置水印URL
                        } else {
                            log.warn("jsonObject中url字段为空，不设置水印URL");
                        }
                    } else {
                        log.warn("jsonObject中不包含url字段，无法获取水印URL");
                    }
                    airResultMapper.updateCollectName(airResultRenameReq);
                }
            } catch (Exception e) {
                log.error("调用飞控重命名图片/视频成果物失败:{}", e);
            }
        }
        return Result.data(result);
    }

    /**
     * 获取飞行结果任务列表
     *
     * @param airResultTaskReq
     * @param flag             该标识为1，则查询正常和假删的数据
     * @return
     */
    @Override
    public TableDataInfo queryTaskList(AirResultTaskReq airResultTaskReq, String flag) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        //参数拼装
        airResultTaskReq.setFlag(flag);
        boolean queryParamCheck = this.taskParamConvert(airResultTaskReq);
        if (!queryParamCheck) {
            tableDataInfo.setTotal(Long.parseLong(Constants.ZERO));
            tableDataInfo.setCode(Constants.CODE_200);
            tableDataInfo.setRows(new ArrayList<>());
            return tableDataInfo;
        }
        //调用飞控接口 获取飞行结果任务列表
        JSONObject jsonObject = (JSONObject) JSON.parse(JSON.toJSONString(airResultTaskReq));
        ResultEntity resultEntity = this.requestGateWay(jsonObject, NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_LIST, airResultTaskReq.getAccessNode(), airResultTaskReq.getAcceptNode());
        if (ObjectUtil.isNotNull(resultEntity)) {
            //封装返回参数
            tableDataInfo = this.taskResultConvert(resultEntity, airResultTaskReq);
            return tableDataInfo;
        }
        return tableDataInfo;
    }

    /**
     * 飞行成果任务参数拼装
     *
     * @param airResultTaskReq
     */
    public boolean taskParamConvert(AirResultTaskReq airResultTaskReq) {
        //校验查询类型
        if (StringUtils.isEmpty(airResultTaskReq.getDeviceCodeType())) {
            airResultTaskReq.setDeviceCodeType(Constants.NEST); //默认机巢
        }
        //校验收藏标识
        if (StringUtils.isEmpty(airResultTaskReq.getIsCollect())) {
            airResultTaskReq.setIsCollect(Constants.ZERO);
        }
        //校验复飞标识
        if (StringUtils.isEmpty(airResultTaskReq.getIsReFly())) {
            airResultTaskReq.setIsReFly(Constants.ZERO);
        }
//        if (CollectionUtil.isEmpty(airResultTaskReq.getFlightRouteId())) {
//            //查询租户权限下的航线id集合
//            List<String> flouteIds = airResultMapper.queryFlightIds(airResultTaskReq);
//            airResultTaskReq.setFlightRouteId(flouteIds);
//        }
        //判断航线成果任务查询权限  没有航线权限就不去查询数据
        if ("".equals(airResultTaskReq.getFlightRouteName())) {
            airResultTaskReq.setFlightRouteName(null);
        }
        //一键复飞和收藏入参标识判断
        if (!(Constants.ZERO.equals(airResultTaskReq.getIsCollect()) && Constants.ZERO.equals(airResultTaskReq.getIsReFly())
                && Constants.ZERO.equals(airResultTaskReq.getTaskStatus()))) {
            List<AirResultTaskReq> taskReqs = airResultMapper.queryReqTaskRecordIds(airResultTaskReq);
            if (CollectionUtil.isNotEmpty(taskReqs)) {
                List<String> ids = taskReqs.stream().map(AirResultTaskReq::getTaskRecordId).collect(Collectors.toList());
                airResultTaskReq.setTaskRecordIds(ids);
            }
            if (CollectionUtil.isEmpty(taskReqs)) {
                return false;
            }
        }
        //查询租户权限下的任务ids集合
        if (StringUtils.isEmpty(airResultTaskReq.getTaskRecordIds())) {
            List<String> ids = airResultMapper.queryTaskIds(airResultTaskReq);
            if (CollectionUtil.isNotEmpty(ids)) {
                airResultTaskReq.setTaskRecordIds(ids);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 飞行成果任务返回参数拼装
     *
     * @param
     */
    public TableDataInfo taskResultConvert(ResultEntity resultEntity, AirResultTaskReq airResultTaskReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        if (ObjectUtil.isNull(resultEntity.getData())) {
            tableDataInfo.setTotal(Long.parseLong(Constants.ZERO));
            tableDataInfo.setCode(Constants.CODE_200);
            tableDataInfo.setRows(new ArrayList<>());
            return tableDataInfo;
        }
        JSONObject jsonObject = (JSONObject) resultEntity.getData();
        Integer totalCount = Integer.valueOf(jsonObject.get("total").toString());
        JSONArray jsonArray = (JSONArray) jsonObject.get(RECORDS);
        List<AirResultTaskRep> airResultTaskReps = jsonArray.toJavaList(AirResultTaskRep.class);
        List<String> mappedList = airResultTaskReps.stream()
                .map(rep -> rep.getTaskRecordId())
                .collect(Collectors.toList());
        List<AirTaskRes> airTaskRes = new ArrayList<>();
        List<AirResultCollectReq> airResultCollectReqs = new ArrayList<>();
        List<AirTaskRes> airTaskResList = new ArrayList<>();
        List<AirTaskRes> airPlanList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(mappedList)) {
            airTaskRes = airTaskMapper.findAirTaskByTaskIds(mappedList);
            //根据id查询 收藏状态
            if (StringUtils.isNotEmpty(airResultTaskReq.getUserId())) {
                airResultCollectReqs = airResultMapper.queryCollectStatus(mappedList, airResultTaskReq.getUserId());
            }
            //根据id查询 复飞状态
            airTaskResList = airResultMapper.queryReFlyTaskIds(mappedList);
        }
        List<String> flightRouteId = airResultTaskReps.stream().map(AirResultTaskRep::getFlightRouteId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(flightRouteId)) {
            airPlanList = airResultMapper.queryLineRouteIds(flightRouteId);
        }
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_STR);
        List<AirTaskRes> finalAirTaskRes = airTaskRes;
        List<AirResultCollectReq> finalAirResultCollectReqs = airResultCollectReqs;
        List<AirTaskRes> finalAirTaskResList = airTaskResList;
        List<AirTaskRes> finalAirPlanList = airPlanList;

        //根据taskRecordId 匹配任务列表的设置操作人，执行时间
        List<String> taskRecordIds = airResultTaskReps.stream()
                .map(AirResultTaskRep::getTaskRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<String, Map<String, Object>> taskRecordMap = CollectionUtils.isEmpty(taskRecordIds)
                ? Collections.emptyMap()
                : airTaskMapper.queryCreateInfoByTaskIds(taskRecordIds).stream()
                .filter(map -> map.get(TASK_RECORD_ID) != null)
                .collect(Collectors.toMap(
                        map -> String.valueOf(map.get(TASK_RECORD_ID)),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        airResultTaskReps.stream().forEach(airResultTaskRep -> {
            //设置操作人，执行时间
            if (airResultTaskRep.getTaskRecordId() != null) {
                Map<String, Object> taskInfo = taskRecordMap.get(airResultTaskRep.getTaskRecordId());
                if (taskInfo != null) {
                    airResultTaskRep.setNickName(Optional.ofNullable(taskInfo.get("nickName"))
                            .map(String::valueOf).orElse(null));
                    airResultTaskRep.setRealityTime(Optional.ofNullable(taskInfo.get("realityTime"))
                            .map(String::valueOf).orElse(null));
                    airResultTaskRep.setFlightMainType(Optional.ofNullable(taskInfo.get("flightMainType"))
                            .map(String::valueOf).orElse(null));
                }
            }

            //            String airName = airResultTaskRep.getAirlineName().split("-")[0];
            //            LocalDateTime dateTime = LocalDateTime.parse(airResultTaskRep.getStartTime(), inputFormat);
            // 解析航线名称（始终返回非null值）
            String airName = Optional.ofNullable(airResultTaskRep)
                    .map(AirResultTaskRep::getAirlineName)
                    .filter(StringUtils::isNotBlank)
                    .map(name -> {
                        String[] parts = name.split("-");
                        return parts.length > 0 && StringUtils.isNotBlank(parts[0]) ? parts[0].trim() : name;
                    })
                    .orElse("");
            LocalDateTime dateTime = Optional.ofNullable(airResultTaskRep)
                    .map(AirResultTaskRep::getStartTime)
                    .filter(StringUtils::isNotBlank)
                    .filter(startTime -> inputFormat != null)
                    .map(startTime -> {
                        try {
                            return LocalDateTime.parse(startTime, inputFormat);
                        } catch (DateTimeParseException e) {
                            return LocalDateTime.now(); // 解析失败返回当前时间
                        }
                    })
                    .orElse(LocalDateTime.now()); // 任何条件不满足时返回当前时间
            String formattedDate = dateTime.format(outputFormat);
            airName = airName + formattedDate;
            airResultTaskRep.setPlanTaskName(airName);
            finalAirTaskRes.stream().forEach(airTaskRes1 -> {
                if (airTaskRes1.getTaskId().equals(airResultTaskRep.getTaskRecordId())) {
                    airResultTaskRep.setSnapshotId(airTaskRes1.getSnapshotId());
                    airResultTaskRep.setPlanType(String.valueOf(airTaskRes1.getPlanType()));
                    airResultTaskRep.setStartNestCode(airTaskRes1.getStartNestCode());
                    airResultTaskRep.setEndNestCode(airTaskRes1.getEndNestCode());
                }
            });
            finalAirResultCollectReqs.stream().forEach(airResultCollectReq -> {
                if (airResultCollectReq.getCollectId().equals(airResultTaskRep.getTaskRecordId())) {
                    airResultTaskRep.setCollectStatus(Constants.ONE);
                }
            });
            finalAirTaskResList.stream().forEach(taskRes -> {
                if (taskRes.getTaskRecordId().equals(airResultTaskRep.getTaskRecordId())) {
                    airResultTaskRep.setReFlyStatus(Constants.ONE);
                    airResultTaskRep.setOriginalTaskRecordId(taskRes.getOriginalTaskRecordId());
                }
            });
            finalAirPlanList.stream().forEach(taskRes -> {
                if (taskRes.getTaskRecordId().equals(airResultTaskRep.getTaskRecordId())) {
                    airResultTaskRep.setDeleteFlag(taskRes.getDeleteFlag());
                    airResultTaskRep.setIsExpandWatermark(taskRes.getIsExpandWatermark());
                    // 处理飞行里程（保留一位小数）
                    if (!StringUtils.isEmpty(taskRes.getAirMileage())) {
                        BigDecimal airMileage = new BigDecimal(taskRes.getAirMileage()).setScale(1, RoundingMode.HALF_UP);
                        airResultTaskRep.setAirMileage(airMileage.toString());
                    }
                    airResultTaskRep.setAirTime(taskRes.getAirTime());
                    airResultTaskRep.setPlanName(taskRes.getPlanName());
                    airResultTaskRep.setAirlineName(taskRes.getFlightRouteName());
                    airResultTaskRep.setId(taskRes.getId());
                    airResultTaskRep.setPlanId(taskRes.getPlanId());
                }
            });
        });
        tableDataInfo.setRows(airResultTaskReps);
        tableDataInfo.setTotal(totalCount);
        tableDataInfo.setCode(Constants.CODE_200);
        return tableDataInfo;
    }

    /**
     * 请求网关通用方法
     *
     * @param jsonObject
     * @return
     */
    public ResultEntity requestGateWay(JSONObject jsonObject, NetConnectUtils.RequestParamter.URLMappingEnum url, String accessNode, String acceptNode) {
        NetConnectUtils.RequestParamter<JSONObject> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(jsonObject);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(url);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, accessNode, acceptNode);
        return resultEntity;
    }

    /**
     * 根据任务获取图片集合
     *
     * @param airResultTaskReq
     * @return
     */
    @Override
    public TableDataInfo queryTaskPhotos(AirResultTaskReq airResultTaskReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        //查询参数封装
        boolean queryParamCheck = this.taskPhotoParamConvert(airResultTaskReq);
        if (!queryParamCheck) {
            tableDataInfo.setTotal(Long.parseLong(Constants.ZERO));
            tableDataInfo.setRows(new ArrayList<>());
            tableDataInfo.setCode(Constants.CODE_200);
            return tableDataInfo;
        }
        //调用飞控接口 查询飞行任务成果照片
        JSONObject requestJson = (JSONObject) JSON.parse(JSON.toJSONString(airResultTaskReq));
        ResultEntity resultEntity = this.requestGateWay(requestJson, NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_PHOTO, airResultTaskReq.getAccessNode(), airResultTaskReq.getAcceptNode());
        if (ObjectUtil.isNotNull(resultEntity)) {
            //照片结果封装
            tableDataInfo = this.taskPhotoResultConvert(resultEntity, airResultTaskReq);
        }
        return tableDataInfo;
    }

    /**
     * 飞行成果图片结果封装
     *
     * @param resultEntity
     * @param airResultTaskReq
     * @return
     */
    private TableDataInfo taskPhotoResultConvert(ResultEntity resultEntity, AirResultTaskReq airResultTaskReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        if (ObjectUtil.isNull(resultEntity.getData())) {
            tableDataInfo.setTotal(Long.parseLong(Constants.ZERO));
            tableDataInfo.setCode(Constants.CODE_200);
            tableDataInfo.setRows(new ArrayList<>());
            return tableDataInfo;
        }
        JSONObject jsonObject = (JSONObject) resultEntity.getData();
        Integer totalCount = Integer.valueOf(jsonObject.get(TOTAL).toString());
        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
        //AirTaskPhotoRep 添加道路和桩号
        List<AirTaskPhotoRep> airTaskPhotoRespons = jsonArray.toJavaList(AirTaskPhotoRep.class);
        List<SecondRecogBatchVO> secondRecogBatchVOS = new ArrayList<>();
        List<AirResultCollectReq> airResultCollectReqs = new ArrayList<>();
        List<String> photoIds = new ArrayList<>();
        List<AirTaskRes> airTaskRes = new ArrayList<>();
        List<AirTaskRes> airTaskList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(airTaskPhotoRespons)) {
            List<String> idList = airTaskPhotoRespons.stream().map(AirTaskPhotoRep::getId) // 提取每个对象的 id
                    .collect(Collectors.toList());
            secondRecogBatchVOS = getUavSynAnalysisResult(idList, com.chinatower.airline.constants.Constants.PHOTO);
            //根据id查询 收藏状态
            if (StringUtils.isNotEmpty(airResultTaskReq.getUserId())) {
                airResultCollectReqs = airResultMapper.queryCollectStatus(idList, airResultTaskReq.getUserId());
            }
            //根据id查询 复飞状态
            photoIds = airResultMapper.queryReFlyStatus(idList);
            //查询此任务是否为复飞产生的任务
            airTaskRes = airResultMapper.queryReFlyTaskIds(airResultTaskReq.getTaskRecordIds());
            //查询任务详情
            airTaskList = airTaskMapper.findAirTaskByTaskIds(airResultTaskReq.getTaskRecordIds());
        }
        //根据任务id查询航线sn等
        List<String> taskRecordIds = new ArrayList<>();
        taskRecordIds.add(airResultTaskReq.getTaskRecordId());
        List<UavInfo> uavInfos = this.queryDeviceInfo(taskRecordIds);
        if (CollectionUtils.isEmpty(uavInfos)) {
            tableDataInfo.setCode(500);
            tableDataInfo.setRows(new ArrayList<>());
            return tableDataInfo;
        }
        UavInfo uavInfo = uavInfos.get(0);
        //查询此任务的历史轨迹 匹配视频开始时间的经纬度
        JSONArray objects = this.queryHisRecord(airResultTaskReq);
        if (ObjectUtil.isNotEmpty(airTaskPhotoRespons)) {
            List<SecondRecogBatchVO> finalSecondRecogBatchVOS = secondRecogBatchVOS;
            List<AirResultCollectReq> finalAirResultCollectReqs = airResultCollectReqs;
            List<String> finalPhotoIds = photoIds;
            List<AirTaskRes> finalAirTaskRes = airTaskRes;
            List<AirTaskRes> finalAirTaskList = airTaskList;
            airTaskPhotoRespons.stream().forEach(taskPhotoRep -> {
                taskPhotoRep.setUavCode(airResultTaskReq.getUavCode());
                taskPhotoRep.setAccessNode(airResultTaskReq.getAccessNode());
                taskPhotoRep.setSnCode(uavInfo.getUavSnCode());
                taskPhotoRep.setUavModelName(uavInfo.getUavModelName());
                finalAirResultCollectReqs.stream().forEach(airResultCollectReq -> {
                    if (airResultCollectReq.getCollectId().equals(taskPhotoRep.getId())) {
                        taskPhotoRep.setCollectStatus(Constants.ONE);
                    }
                });
                finalPhotoIds.stream().forEach(photoId -> {
                    if (photoId.equals(taskPhotoRep.getId())) {
                        taskPhotoRep.setReFlyStatus(Constants.ONE);
                    }
                });
                if (CollectionUtil.isNotEmpty(finalAirTaskRes)) {
                    AirTaskRes airTaskRe = finalAirTaskRes.get(0);
                    taskPhotoRep.setOriginalTaskRecordId(airTaskRe.getOriginalTaskRecordId());
                    if (StringUtils.isNotNull(airTaskRe.getOriginalTaskRecordId())) {
                        taskPhotoRep.setReFlyStatus(Constants.ONE);
                        taskPhotoRep.setOriginalId(airTaskRe.getOriginalId());
                    }
                }

                //起始降落机巢编码
                if (!CollectionUtils.isEmpty(finalAirTaskList)){
                    AirTaskRes airTaskRe = finalAirTaskList.get(0);
                    taskPhotoRep.setStartNestCode(airTaskRe.getStartNestCode());
                    taskPhotoRep.setEndNestCode(airTaskRe.getEndNestCode());
                }
                Object o = new Object();
                if (ObjectUtil.isNotNull(objects)) {
                    String createTime = taskPhotoRep.getTimeCreated();
                    try {
                        o = this.findClosestObject(objects, createTime, DATE_CHAR);
                        if (ObjectUtil.isNotNull(o)) {
                            JSONObject object = (JSONObject) o;
                            JSONObject aircraftLocation = (JSONObject) object.get("aircraftLocation");
                            taskPhotoRep.setAltitude(((BigDecimal) aircraftLocation.get("altitude")).doubleValue());
                        }
                    } catch (Exception e) {
                        log.error("查询任务照片经纬度数据参数封装异常", e);
                    }
                }
                taskPhotoRep.setEventStatus(0);
                List<OrderTypeDto> alarmCodes = new ArrayList<>();
                Set<String> al = new HashSet<>();
                if (ObjectUtil.isNotEmpty(finalSecondRecogBatchVOS)) {
                    finalSecondRecogBatchVOS.stream().forEach(secondRecogBatchVO -> {
                        if (secondRecogBatchVO.getPicId().equals(taskPhotoRep.getId())) {
                            OrderTypeDto dto = new OrderTypeDto();
                            if ("1".equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                taskPhotoRep.setEventStatus(2);
                                //告警事件编号
                                if (StringUtils.isNotEmpty(secondRecogBatchVO.getWarningOrderId())) {
                                    dto.setWarningOrderId(secondRecogBatchVO.getWarningOrderId());
                                    dto.setAlgorithmCode(secondRecogBatchVO.getAlgorithmCode());
                                }
                                //告警事件来源（0：告警  1：回收站）
                                if (StringUtils.isNotEmpty(secondRecogBatchVO.getOrderType())) {
                                    dto.setOrderType(secondRecogBatchVO.getOrderType());
                                } else {
                                    dto.setOrderType(Constants.ZERO);
                                }
                                if (StringUtils.isNotEmpty(secondRecogBatchVO.getAlgorithmCode())) {
                                    al.add(secondRecogBatchVO.getAlgorithmCode());
                                }
                            } else if ("0".equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                taskPhotoRep.setEventStatus(1);
                            } else {
                                taskPhotoRep.setEventStatus(0);
                            }
                            if (!StringUtils.isEmpty(dto.getWarningOrderId())) {
                                alarmCodes.add(dto);
                            }
                        }
                    });
                    if (!CollectionUtils.isEmpty(alarmCodes)) {
                        taskPhotoRep.setEventStatus(2);
                    }
                    taskPhotoRep.setAlarmCodes(alarmCodes);
                    taskPhotoRep.setAlgorithmCodes(al);
                }
            });
        }
        tableDataInfo.setTotal(totalCount);
        tableDataInfo.setRows(airTaskPhotoRespons);
        tableDataInfo.setCode(Constants.CODE_200);
        return tableDataInfo;
    }

    /**
     * 查询飞机信息
     *
     * @param taskRecordId
     * @return
     */
    private List<UavInfo> queryDeviceInfo(List<String> taskRecordId) {
        List<UavInfo> uavInfo = airResultMapper.queryUavInfoByTaskRecordId(taskRecordId);
        return uavInfo;
    }

    /**
     * 查询任务照片参数封装
     *
     * @param airResultTaskReq
     */
    private boolean taskPhotoParamConvert(AirResultTaskReq airResultTaskReq) {
        String uavCode = airResultMapper.queryUavInfo(airResultTaskReq.getTaskRecordId());
        airResultTaskReq.setUavCode(uavCode);
        List<String> ids = new ArrayList<>();
        ids.add(airResultTaskReq.getTaskRecordId());
        airResultTaskReq.setTaskRecordIds(ids);
        if (CollectionUtil.isNotEmpty(airResultTaskReq.getPicTypes())) {
            if (airResultTaskReq.getPicTypes().size() == 1) {
                String picType = airResultTaskReq.getPicTypes().get(0);
                airResultTaskReq.setPicType(picType);
            }
        }
        if (StringUtils.isNotEmpty(airResultTaskReq.getIsReFly()) && !Constants.ZERO.equals(airResultTaskReq.getIsReFly())) {
            if (StringUtils.isEmpty(airResultTaskReq.getOriginalTaskRecordId())) {
                //查询此任务下复飞的图片id
                List<String> stringList = airResultMapper.queryReFlyTaskByOrgTaskId(airResultTaskReq.getTaskRecordId());
                if (ObjectUtil.isNotEmpty(stringList)) {
                    airResultTaskReq.setPhotoIds(stringList);
                } else {
                    if (Constants.ONE.equals(airResultTaskReq.getIsReFly())) {
                        return false;
                    }
                }
            } else {
                //筛选没有复飞任务的图片id
                if (Constants.TWO.equals(airResultTaskReq.getIsReFly())) {
                    return false;
                }
            }
            //筛选有复飞任务的图片id
            if (Constants.ONE.equals(airResultTaskReq.getIsReFly())) {
                airResultTaskReq.setRefly(Constants.ZERO);
            }
            //筛选没有复飞任务的图片id
            if (Constants.TWO.equals(airResultTaskReq.getIsReFly())) {
                airResultTaskReq.setRefly(Constants.ONE);
            }
        }
        if (CollectionUtil.isNotEmpty(airResultTaskReq.getPicSeIds())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("algorithmCode").in(airResultTaskReq.getPicSeIds()));
            query.addCriteria(Criteria.where("airTaskId").is(airResultTaskReq.getTaskRecordId()));
            query.addCriteria(Criteria.where("secondJudgeResult").is("1"));
            List<SecondRecogBatchVO> secondRecogBatchVOS = mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
            if (CollectionUtil.isNotEmpty(secondRecogBatchVOS)) {
                List<String> picId = secondRecogBatchVOS.stream()
                        .map(SecondRecogBatchVO::getPicId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (CollectionUtil.isEmpty(picId)) {
                    return false;
                }
                if (CollectionUtil.isNotEmpty(airResultTaskReq.getPhotoIds())) {
                    // --- 取交集逻辑 ---
                    List<String> intersection = airResultTaskReq.getPhotoIds().stream()
                            .filter(picId::contains)
                            .collect(Collectors.toList());
                    airResultTaskReq.setPhotoIds(intersection);
                } else {
                    airResultTaskReq.setPhotoIds(picId);
                    airResultTaskReq.setRefly(Constants.ZERO);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据任务获取视频集合
     *
     * @param airResultTaskReq
     * @return
     */
    @Override
    public TableDataInfo queryTaskVideos(AirResultTaskReq airResultTaskReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        //调用飞控查询视频接口
        JSONObject requestJsonObject = (JSONObject) JSON.parse(JSON.toJSONString(airResultTaskReq));
        ResultEntity resultEntity = this.requestGateWay(requestJsonObject,
                NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_VIDEO,
                airResultTaskReq.getAccessNode(), airResultTaskReq.getAcceptNode());
        if (ObjectUtil.isNotNull(resultEntity)) {
            //照片结果封装
            tableDataInfo = this.taskVideoResultConvert(resultEntity, airResultTaskReq);
        }
        return tableDataInfo;
    }

    /**
     *
     * @param resultEntity
     * @param airResultTaskReq
     * @return
     */
    private TableDataInfo taskVideoResultConvert(ResultEntity resultEntity, AirResultTaskReq airResultTaskReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        if (ObjectUtil.isNull(resultEntity.getData())) {
            tableDataInfo.setTotal(Long.parseLong(Constants.ZERO));
            tableDataInfo.setCode(Constants.CODE_200);
            tableDataInfo.setRows(new ArrayList<>());
            return tableDataInfo;
        }
        JSONObject jsonObject = (JSONObject) resultEntity.getData();
        Integer totalCount = Integer.valueOf(jsonObject.get(TOTAL).toString());
        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
        List<AirTaskVideoRep> airTaskPhotoResponses = jsonArray.toJavaList(AirTaskVideoRep.class);
        List<SecondRecogBatchVO> secondRecogBatchVOS = new ArrayList<>();
        List<AirResultCollectReq> airResultCollectReqs = new ArrayList<>();
        AirTaskVideoRep airTaskVideoRep = airResultMapper.queryInfo(airResultTaskReq.getTaskRecordId());
        if (ObjectUtil.isNotEmpty(airTaskPhotoResponses)) {
            List<String> idList = airTaskPhotoResponses.stream().map(AirTaskVideoRep::getId).collect(Collectors.toList());
            secondRecogBatchVOS = getUavSynAnalysisResult(idList, com.chinatower.airline.constants.Constants.VIDEO);
            //根据id查询 收藏状态
            if (StringUtils.isNotEmpty(airResultTaskReq.getUserId())) {
                airResultCollectReqs = airResultMapper.queryCollectStatus(idList, airResultTaskReq.getUserId());
            }
        }
        //查询此任务的历史轨迹 匹配视频开始时间的经纬度
        JSONArray objects = this.queryHisRecord(airResultTaskReq);
        if (ObjectUtil.isNotEmpty(airTaskPhotoResponses)) {
            List<SecondRecogBatchVO> finalSecondRecogBatchVOS = secondRecogBatchVOS;
            List<AirResultCollectReq> finalAirResultCollectReqs = airResultCollectReqs;
            airTaskPhotoResponses.stream().forEach(taskVideoResponse -> {
                finalAirResultCollectReqs.stream().forEach(airResultCollectReq -> {
                    if (airResultCollectReq.getCollectId().equals(taskVideoResponse.getId())) {
                        taskVideoResponse.setCollectStatus(Constants.ONE);
                    }
                });
                Object o = new Object();
                if (ObjectUtil.isNotNull(objects)) {
                    String createTime = taskVideoResponse.getCreateTime();
                    o = this.findClosestObject(objects, createTime, DATE_CHAR);
                }
                if (ObjectUtil.isNotNull(o)) {
                    JSONObject object = (JSONObject) o;
                    JSONObject aircraftLocation = (JSONObject) object.get("aircraftLocation");
                    taskVideoResponse.setLongitude(((BigDecimal) aircraftLocation.get("longitude")).doubleValue());
                    taskVideoResponse.setLatitude(((BigDecimal) aircraftLocation.get("latitude")).doubleValue());
                    taskVideoResponse.setAltitude(((BigDecimal) aircraftLocation.get("altitude")).doubleValue());
                }
                taskVideoResponse.setEventStatus(0);
                if (ObjectUtil.isNotEmpty(finalSecondRecogBatchVOS)) {
                    finalSecondRecogBatchVOS.stream().forEach(secondRecogBatchVO -> {
                        if (secondRecogBatchVO.getVideoId().equals(taskVideoResponse.getId())) {
                            if ("1".equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                taskVideoResponse.setEventStatus(2);
                            } else if ("0".equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                taskVideoResponse.setEventStatus(1);
                            } else {
                                taskVideoResponse.setEventStatus(0);
                            }
                        }
                    });
                }
                if (ObjectUtil.isNotEmpty(airTaskVideoRep) && StringUtils.isNotEmpty(airTaskVideoRep.getTaskName())) {
                    taskVideoResponse.setTaskName(airTaskVideoRep.getTaskName());
                }
                if (ObjectUtil.isNotEmpty(airTaskVideoRep) && StringUtils.isNotEmpty(airTaskVideoRep.getDeviceName())) {
                    taskVideoResponse.setDeviceName(airTaskVideoRep.getDeviceName());
                }
            });
        }
        tableDataInfo.setTotal(totalCount);
        tableDataInfo.setRows(airTaskPhotoResponses);
        tableDataInfo.setCode(Constants.CODE_200);
        return tableDataInfo;
    }

    // 辅助方法：安全获取BigDecimal并转为double
    private Double getDoubleValue(JSONObject obj, String key) {
        Object value = obj.get(key);
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        return null;
    }

    /**
     * 计算最匹配的时间
     *
     * @param objects
     * @param createTime
     * @return
     */
    public static Object findClosestObject(JSONArray objects, String createTime, String timeCharacter) {
        if (objects == null || objects.isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        Date targetDate;
        try {
            targetDate = sdf.parse(createTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        AtomicReference<Object> closestObject = new AtomicReference<>();
        if (ObjectUtil.isNotNull(objects)) {
            Map<Object, Long> map = new HashMap();
            for (Object object : objects) {
                JSONObject json = JSON.parseObject(object.toString());
                String saveDateTimeStr = json.getString(timeCharacter);
                if (saveDateTimeStr == null) {
                    continue;
                }
                Date saveDate;
                try {
                    saveDate = sdf.parse(saveDateTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }

                long diff = Math.abs(targetDate.getTime() - saveDate.getTime());
                map.put(object, diff);
            }
            Collection<Long> collections = map.values();
            Optional<Long> minValue = collections.stream().min(Long::compare);
            minValue.ifPresent(min -> {
                for (Object key : map.keySet()) {
                    long value = map.get(key);
                    if (value == min) {
                        closestObject.set(key);
                    }
                }
            });
        }
        return closestObject.get();
    }


    //通过图片id/视频id 查询算法识别结果
    public List<SecondRecogBatchVO> getUavSynAnalysisResult(List<String> idList, String type) {
        log.info("无人机二次识别结果入参：{},type:{}", JSON.toJSONString(idList), type);
        Query query = new Query();
        if (com.chinatower.airline.constants.Constants.PHOTO.equals(type)) {
            query.addCriteria(Criteria.where("picId").in(idList));
            query.addCriteria(Criteria.where("uavRecogSource").ne(Constants.ONE));
        } else {
            query.addCriteria(Criteria.where("videoId").in(idList));
        }
        return mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
    }

    /**
     * 同步数据源
     *
     * @param airResultTaskReq
     * @return
     */
    @Override
    public Result mediaSync(AirResultTaskReq airResultTaskReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airResultTaskReq.setAcceptNode(platformCode);
        //调用飞控接口 同步数据源
        NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airResultTaskReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_DATA_SOURCE);
        pathEdit.setDeviceCode(airResultTaskReq.getDeviceCode());
        paramterEdit.setPath(pathEdit);
        okhttpUtil.doPostUavGateWay(paramterEdit, airResultTaskReq.getAcceptNode(), platformCode);
        return Result.success("");
    }

    /**
     * 通过视频时间查询轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    @Override
    public Result queryHisRecordByVideoTime(AirResultTaskReq airResultTaskReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airResultTaskReq.setAcceptNode(platformCode);
        log.info("通过视频时间查询轨迹" + JSON.toJSONString(airResultTaskReq));
        JSONArray jsonArray1 = this.queryHisRecord(airResultTaskReq);
        //开始截取时间
        String startTimeStr = airResultTaskReq.getVideoStartTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        if (ObjectUtil.isEmpty(airResultTaskReq.getDuration())) {
            throw new CustomException("入参视频时长为空");
        }
        double durationInSeconds = airResultTaskReq.getDuration();
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        Duration duration = Duration.ofSeconds((long) durationInSeconds);
        LocalDateTime endTime = startTime.plus(duration);
        String endTimeStr = endTime.format(formatter);
        log.info("endTime: {}", endTimeStr);
        // 过滤数据
        String finalEndTimeStr = endTimeStr;
        log.info("通过视频时间查询轨迹结束时间" + JSON.toJSONString(finalEndTimeStr));
        JSONArray filteredArray = new JSONArray();
        filteredArray = jsonArray1.stream().filter(obj -> startTimeStr.compareTo(((JSONObject) obj).getString(DATE_CHAR)) <= 0).filter(obj -> ((JSONObject) obj).getString(DATE_CHAR).compareTo(finalEndTimeStr) <= 0).collect(Collectors.toCollection(JSONArray::new));
        //通过任务ID查询绑定的无人机信息
        String uavName = airResultMapper.queryUavInfoByTaskId(airResultTaskReq.getTaskRecordId());
        JSONArray filterArray = new JSONArray();
        filteredArray.stream().forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("uavName", uavName);
            filterArray.add(jsonObject);
        });
        // 构建结果 JSON 对象
        return Result.data(filterArray);
    }


    /**
     * 查询历史轨迹
     *
     * @param airResultTaskReq
     * @return
     */
    @Override
    public JSONArray queryHisRecord(AirResultTaskReq airResultTaskReq) {
        String platformCode = okhttpUtil.getSelfPlatFormCode();
        airResultTaskReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airResultTaskReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_AIR_HISTORY);
        pathEdit.setCurrentPage(1);
        pathEdit.setPageSize(99999);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airResultTaskReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject1 = (JSONObject) resultEntity.getData();
            JSONArray jsonArray1 = (JSONArray) jsonObject1.get(RECORDS);
            return jsonArray1;
        }
        return new JSONArray();
    }

    /**
     * 无人机树默认选中有最新执行任务的设备
     *
     * @return
     */
    @Override
    @DataScope(tenantColumn = "pt.tenant_id", industryCode = "pt.industry_code", acceptColumn = "pt.accept_node")
    public Result queryDefultUav(AirUavDeviceReq airUavDeviceReq) {
        String uavCode = airResultMapper.queryDefultUav(airUavDeviceReq);
        return Result.data(uavCode);
    }

    /**
     * 视频抽帧
     *
     * @param videoExtractReq
     * @return
     */
    @Override
    public Result doVideoExtraction(AirVideoExtractReq videoExtractReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        videoExtractReq.setAcceptNode(platformCode);
        //调用飞控平台视频抽帧api
        NetConnectUtils.RequestParamter<AirVideoExtractReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(videoExtractReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_VIDEO_EXTRACTION);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, videoExtractReq.getAccessNode(), platformCode);
        JSONObject jsonObject = (JSONObject) resultEntity.getData();
        log.info("调用飞控平台-视频抽帧结果：{}", JSON.toJSONString(jsonObject));
        String taskId = jsonObject.getString(Constants.DESC_TASK_ID_PARAM);

        //保存视频抽帧信息
        AirVideoExtractRep videoExtractRep = new AirVideoExtractRep();
        videoExtractRep.setVideoId(videoExtractReq.getId());
        videoExtractRep.setVideoName(videoExtractReq.getVideoName());
        videoExtractRep.setTaskId(taskId);
        videoExtractRep.setTaskName(videoExtractReq.getTaskName());
        videoExtractRep.setFrequency(videoExtractReq.getFrequency());
        videoExtractRep.setExtractTime(new Date());
        videoExtractRep.setExtractTotalCount(videoExtractReq.getExtractCount());
        videoExtractRep.setCreateUser(loginUser.getUser().getUserName());
        videoExtractRep.setAcceptNode(platformCode);
        videoExtractRep.setAccessNode(videoExtractReq.getAccessNode());
        videoExtractRep.setIndustryCode(loginUser.getUser().getIndustryCode());
        videoExtractRep.setTenantId(loginUser.getUser().getTenantId());
        log.info("保存视频抽帧信息接口入参：{}", JSON.toJSONString(videoExtractRep));
        airResultMapper.addVideoExtraction(videoExtractRep);

        //添加缓存，后续消费
        String cacheKey = Constants.EXTRACTION_TASK_ID_ + taskId;
        RedisUtil.set(cacheKey, videoExtractRep, Constants.EXTRACTION_TASK_CACHE_TIME, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        return Result.success("请求成功！");
    }

    /**
     * 查询抽帧列表
     *
     * @param videoExtractReq
     * @return
     */
    @Override
    public Result queryVideoExtractionList(AirVideoExtractReq videoExtractReq) {
        log.info("查询抽帧列表入参：{}", JSON.toJSONString(videoExtractReq));
        //查询抽帧任务列表
        List<AirVideoExtractRep> extractRepList = airResultMapper.queryVideoExtractionList(videoExtractReq);
        if (CollectionUtils.isEmpty(extractRepList)) {
            return Result.data(new JSONArray());
        }

        //获取视频ID、以及所有的任务ID
        String videoId = extractRepList.get(0).getVideoId();
        List<String> taskIds = extractRepList.stream().map(AirVideoExtractRep::getTaskId).distinct().collect(Collectors.toList());
        //批量查询所有任务对应的图片列表
        Map<String, List<AirVideoExtractTaskRep.ExtractPhotoResponse>> photoMap = queryPhotosForTasks(videoId, taskIds, videoExtractReq.getAccessNode());

        JSONArray resultArray = new JSONArray();
        extractRepList.forEach(res -> {
            JSONObject object = new JSONObject();
            object.put(Constants.DESC_TASK_ID_PARAM, res.getTaskId());
            object.put("extractStatus", res.getExtractStatus());
            String extractTime = DateUtil.format(res.getExtractTime(), "yyyyMMddHHmmss");
            int frequency = res.getFrequency() != null ? Integer.parseInt(res.getFrequency()) : Constants.ZERO_NUM;
            //int extractCount = res.getExtractCount() != null ? res.getExtractCount() : Constants.ZERO_NUM;
            //构建抽帧展示内容格式：任务名称-抽帧时间_抽帧配置_抽帧结果
            object.put("extractContent", String.format("%s-%s_%d秒/帧_", res.getTaskName(), extractTime, frequency));
            // 从图片映射中获取当前任务的图片列表
            List<AirVideoExtractTaskRep.ExtractPhotoResponse> photoList = photoMap.getOrDefault(res.getTaskId(), new ArrayList<>());
            object.put("extractPhotoList", photoList);
            resultArray.add(object);
        });
        return Result.data(resultArray);
    }

    private Map<String, List<AirVideoExtractTaskRep.ExtractPhotoResponse>> queryPhotosForTasks(String videoId, List<String> taskIds, String accessNode) {
        if (CollectionUtils.isEmpty(taskIds) || videoId == null) {
            return new HashMap<>();
        }
        // 构建批量查询请求对象
        AirVideoExtractTaskReq extractTaskReq = new AirVideoExtractTaskReq();
        extractTaskReq.setVideoId(videoId); // 设置视频ID
        extractTaskReq.setTaskIds(taskIds); // 设置任务ID列表
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        extractTaskReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirVideoExtractTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(extractTaskReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PHOTOS);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, accessNode, platformCode);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            Object data = resultEntity.getData();
            JSONArray taskArray = (JSONArray) data;
            List<AirVideoExtractTaskRep> extractTaskList = taskArray.toJavaList(AirVideoExtractTaskRep.class);
            log.info("调用飞控平台-查询视频抽帧任务结果：{}", JSON.toJSONString(extractTaskList));
            //查询图片分析结果
            List<SecondRecogBatchVO> secondRecogBatchVOS;
            if (!CollectionUtils.isEmpty(extractTaskList)) {
                List<String> idList = extractTaskList.stream()
                        .flatMap(task -> task.getList().stream())
                        .map(AirVideoExtractTaskRep.ExtractPhotoResponse::getId)
                        .collect(Collectors.toList());
                secondRecogBatchVOS = getUavSynAnalysisResult(idList);

                List<SecondRecogBatchVO> finalSecondRecogBatchVOS = secondRecogBatchVOS;
                extractTaskList.forEach(task -> {
                    //获取抽帧图片列表
                    List<AirVideoExtractTaskRep.ExtractPhotoResponse> photoResponseList = task.getList();
                    if (!CollectionUtils.isEmpty(photoResponseList)) {
                        photoResponseList.forEach(photoResponse -> {
                            photoResponse.setEventStatus(Constants.ZERO_NUM);
                            if (ObjectUtil.isNotEmpty(finalSecondRecogBatchVOS)) {
                                finalSecondRecogBatchVOS.stream().forEach(secondRecogBatchVO -> {
                                    if (secondRecogBatchVO.getPicId().equals(photoResponse.getId())) {
                                        if (Constants.ONE.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                            photoResponse.setEventStatus(Constants.TWO_NUM);
                                        } else if (Constants.ZERO.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                            photoResponse.setEventStatus(Constants.ONE_NUM);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
            // 构建抽帧任务ID对应图片列表的映射
            return extractTaskList.stream().collect(Collectors.toMap(AirVideoExtractTaskRep::getTaskId, AirVideoExtractTaskRep::getList, (existing, replacement) -> existing));
        } else {
            throw new CustomException("调用飞控查询视频抽帧任务结果返回参数为空");
        }
    }

    /**
     * 根据图片id获取二次识别结果
     *
     * @param idList
     * @return
     */
    private List<SecondRecogBatchVO> getUavSynAnalysisResult(List<String> idList) {
        log.info("查询抽帧图片列表-二次识别结果入参：{}", JSON.toJSONString(idList));
        Query query = new Query();
        query.addCriteria(Criteria.where("picId").in(idList));
        return mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
    }

    /**
     * 抽帧进度
     *
     * @param extractReq
     * @return
     */
    @Override
    public Result queryExtractionProgress(AirVideoExtractReq extractReq) {
        List<AirVideoExtractRep> extractRepList = airResultMapper.queryVideoExtractionList(extractReq);
        return Result.data(extractRepList);
    }

    /**
     * 删除抽帧任务图片
     *
     * @param extractTaskPhotoReq
     * @return
     */
    @Override
    @Transactional
    public Result delExtractTaskPhotos(AirExtractTaskPhotoReq extractTaskPhotoReq) {
        List<String> taskIdList = extractTaskPhotoReq.getExtractionTaskIds();
        List<String> photoIdList = extractTaskPhotoReq.getExtractionPhotoIds();

        //本地删除抽帧任务信息
        if (!CollectionUtils.isEmpty(taskIdList)) {
            AirVideoExtractTaskReq taskReq = new AirVideoExtractTaskReq();
            taskReq.setTaskIds(taskIdList);
            airResultMapper.delVideoExtraction(taskReq);
        }
        //调用飞控api删除抽帧任务图片
        if (!CollectionUtils.isEmpty(taskIdList) || !CollectionUtils.isEmpty(photoIdList)) {
            AirExtractTaskPhotoReq apiRequest = new AirExtractTaskPhotoReq();
            String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
            apiRequest.setAcceptNode(platformCode);
            if (!CollectionUtils.isEmpty(taskIdList)) {
                apiRequest.setExtractionTaskIds(taskIdList);
            }
            if (CollectionUtils.isEmpty(taskIdList) && !CollectionUtils.isEmpty(photoIdList)) {
                apiRequest.setExtractionPhotoIds(photoIdList);
            }
            log.info("调用飞控平台-删除视频抽帧任务图片入参：{}", JSON.toJSONString(apiRequest));
            NetConnectUtils.RequestParamter<AirExtractTaskPhotoReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
            paramterEdit.setBody(apiRequest);
            NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
            pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DEL_EXTRACT_TASK_PHOTOS);
            paramterEdit.setPath(pathEdit);
            okhttpUtil.doPostUavGateWay(paramterEdit, extractTaskPhotoReq.getAccessNode(), platformCode);
        }
        return Result.success("删除成功！");
    }

    /**
     * 取消抽帧
     *
     * @param extractTaskReq
     * @return
     */
    @Override
    @Transactional
    public Result cancelExtractTask(AirVideoExtractTaskReq extractTaskReq) {
        if (StringUtils.isEmpty(extractTaskReq.getVideoId())) {
            return Result.fail("抽帧视频ID不能为空!");
        }
        if (StringUtils.isEmpty(extractTaskReq.getTaskId())) {
            return Result.fail("抽帧任务ID不能为空!");
        }
        extractTaskReq.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserName());
        airResultMapper.cancelExtractTask(extractTaskReq);
        return Result.success("取消成功！");
    }

    /**
     * 获取遥测数据
     *
     * @param telemetryReq
     * @return
     */
    @Override
    public Result queryTelemetryData(TelemetryReq telemetryReq) {
        if (ObjectUtil.isEmpty(telemetryReq) || StringUtils.isEmpty(telemetryReq.getDeviceCode())
                || StringUtils.isEmpty(telemetryReq.getAlarmTime())) {
            return Result.fail("参数不能为空");
        }
        log.info("获取遥测数据入参：{}", JSON.toJSONString(telemetryReq));
        try {
            String startNestCode = telemetryReq.getDeviceCode();
            if (StringUtils.isNotEmpty(telemetryReq.getTaskRecordId())){
                //根据任务ID查询起始机巢编码，以起始机巢编码查询遥测数据
                startNestCode = airTaskMapper.queryStartNestCode(telemetryReq.getTaskRecordId());
                log.info("根据任务ID查询起始机巢编码：{}", startNestCode);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT); // 根据实际格式调整
            Date alarmTime = sdf.parse(telemetryReq.getAlarmTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarmTime);
            calendar.add(Calendar.MINUTE, -360);
            String startTimeStr = sdf.format(calendar.getTime());
            calendar.setTime(alarmTime);
            calendar.add(Calendar.MINUTE, 360);
            String endTimeStr = sdf.format(calendar.getTime());
            Query query = new Query();
            query.addCriteria(Criteria.where("deviceCode").is(startNestCode));
            query.addCriteria(
                    Criteria.where(com.chinatower.airline.constants.Constants.TIME_STAMP)
                            .gte(startTimeStr)
                            .lte(endTimeStr)
            );
            List<UavFlyReq> uavFlyReqs = mongoTemplate.find(query, UavFlyReq.class, Constants.UAV_SYNC_UFNC);
            //筛选时间最接近的一条数据
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(uavFlyReqs));
            Object o = this.findClosestObject(jsonArray, telemetryReq.getAlarmTime(), com.chinatower.airline.constants.Constants.TIME_STAMP);
            if (o != null) {
                UavFlyReq uavFlyReq = JSON.parseObject(JSON.toJSONString(o), UavFlyReq.class);
                if (ObjectUtil.isNotNull(uavFlyReq)) {
                    //查询无人机名称返回
                    AirUavInfo airUavInfo = airResultMapper.queryUavInfoByDeviceCode(uavFlyReq);
                    if (ObjectUtil.isNotNull(airUavInfo)) {
                        uavFlyReq.setUavName(airUavInfo.getUavName());
                    }
                }
                return Result.data(uavFlyReq);
            }
        } catch (Exception e) {

        }
        return Result.data("暂无数据");
    }

    /**
     * 获取最近时间的数据
     *
     * @param airAlTaskLogReq
     * @return
     */
    @Override
    public Result queryTaskNode(AirAlTaskLogReq airAlTaskLogReq) {
        String taskId = airAlTaskLogReq.getTaskId();
        if (StringUtils.isNotBlank(taskId)) {
            return Result.data(airResultMapper.queryTaskNode(taskId));
        }
        return null;
    }

    /**
     * 添加采集结果
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result addCollectResult(AirResultReq airResultReq) {
        if (!CollectionUtils.isEmpty(airResultReq.getVideoIds()) || !CollectionUtils.isEmpty(airResultReq.getTaskRecordIds()) || !CollectionUtils.isEmpty(airResultReq.getPhotoIds())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<AirResultCollectReq> airResultCollectReqs = new ArrayList<>();
            // 处理VideoIds，类型为2
            if (!CollectionUtils.isEmpty(airResultReq.getVideoIds())) {
                List<CollectReq> collectReqs = airResultReq.getVideoIds();
                collectReqs.forEach(collectReq -> {
                    AirResultCollectReq collect = createAirResultCollectReq(collectReq, Constants.TWO, loginUser);
                    airResultCollectReqs.add(collect);
                });
            }
            // 处理TaskRecordIds，类型为3
            if (!CollectionUtils.isEmpty(airResultReq.getTaskRecordIds())) {
                List<CollectReq> collectReqs = airResultReq.getTaskRecordIds();
                collectReqs.forEach(collectReq -> {
                    AirResultCollectReq collect = createAirResultCollectReq(collectReq, Constants.THREE, loginUser);
                    airResultCollectReqs.add(collect);
                });
            }
            // 处理PhotoIds，类型为4
            if (!CollectionUtils.isEmpty(airResultReq.getPhotoIds())) {
                List<CollectReq> collectReqs = airResultReq.getPhotoIds();
                collectReqs.forEach(collectReq -> {
                    AirResultCollectReq collect = createAirResultCollectReq(collectReq, Constants.FOUR, loginUser);
                    airResultCollectReqs.add(collect);
                });
            }
            return Result.data(airResultMapper.addCollectResult(airResultCollectReqs));
        }
        return Result.success("");
    }

    private AirResultCollectReq createAirResultCollectReq(CollectReq collectReq, String type, LoginUser loginUser) {
        AirResultCollectReq collect = new AirResultCollectReq();
        collect.setCollectId(collectReq.getId());
        collect.setAccessNode(collectReq.getAccessNode());
        collect.setType(type);
        collect.setUserId(String.valueOf(loginUser.getUser().getUserId()));
        collect.setDeviceCode(collectReq.getDeviceCode());
        collect.setTaskRecordId(collectReq.getTaskRecordId());
        collect.setLenType(collectReq.getLenType());
        collect.setPlanTime(collectReq.getPlanTime());
        collect.setUrl(collectReq.getUrl());
        collect.setUavCode(collectReq.getUavCode());
        collect.setMediumCreateTime(collectReq.getMediumCreateTime());
        collect.setTaskRecordName(collectReq.getTaskRecordName());
        collect.setName(collectReq.getName());
        collect.setIndustryCode(loginUser.getUser().getIndustryCode());
        collect.setLatitude(collectReq.getLatitude());
        collect.setLongitude(collectReq.getLongitude());
        collect.setHeight(collectReq.getAltitude());
        collect.setWatermarkUrl(collectReq.getWatermarkUrl());
        collect.setIsWatermark(collectReq.getIsWatermark());
        collect.setRemark(collectReq.getRemark());
        //收藏照片添加道路信息
        if (type.equals(Constants.FOUR)){
            collect.setRoadInfo(collectReq.getRoadInfo());
        }
        return collect;
    }

    /**
     * 删除采集结果
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result delCollectResult(AirResultReq airResultReq) {
        if (!CollectionUtils.isEmpty(airResultReq.getVideoIds()) || !CollectionUtils.isEmpty(airResultReq.getTaskRecordIds()) || !CollectionUtils.isEmpty(airResultReq.getPhotoIds())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<CollectReq> taskRecordIds = airResultReq.getTaskRecordIds();
            List<CollectReq> videoIds = airResultReq.getVideoIds();
            List<CollectReq> photoIds = airResultReq.getPhotoIds();
            List<CollectReq> ids = new ArrayList<>();
            if (ObjectUtil.isNotNull(taskRecordIds)) {
                ids.addAll(taskRecordIds);
            }
            if (ObjectUtil.isNotNull(videoIds)) {
                ids.addAll(videoIds);
            }
            if (ObjectUtil.isNotNull(photoIds)) {
                ids.addAll(photoIds);
            }
            List<String> idStrings = new ArrayList<>();
            for (CollectReq collectReq : ids) {
                idStrings.add(collectReq.getId());
            }
            return Result.data(airResultMapper.delCollectResult(idStrings, loginUser.getUser().getUserId()));
        }
        return Result.success("");
    }

    /**
     * 查询采集结果
     *
     * @param airResultReq
     * @return
     */
    @Override
    @DataScope(industryCode = "ct.industry_code")
    public List<AirTaskPhotoRep> queryCollectResultList(AirResultReq airResultReq) {
        Integer pageNum = airResultReq.getPageNum();
        Integer pageSize = airResultReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        airResultReq.setUserId(String.valueOf(loginUser.getUser().getUserId()));
        List<AirTaskPhotoRep> airTaskPhotoRepList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(airResultReq.getPicSeIds())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("algorithmCode").in(airResultReq.getPicSeIds()));
            query.addCriteria(Criteria.where("airTaskId").is(airResultReq.getTaskRecordId()));
            List<SecondRecogBatchVO> secondRecogBatchVOS = mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
            if (CollectionUtil.isNotEmpty(secondRecogBatchVOS)) {
                List<String> picId = secondRecogBatchVOS.stream().map(SecondRecogBatchVO::getPicId).collect(Collectors.toList());
                airResultReq.setPicSeIds(picId);
            } else {
                List<String> picId = new ArrayList<>();
                picId.add("NaN");
                airResultReq.setPicSeIds(picId);
            }
        }
        if (StringUtils.isNotNull(airResultReq.getDataType())) {
            airTaskPhotoRepList = airResultMapper.queryCollectResultList(airResultReq);
        }
        if (CollectionUtil.isNotEmpty(airTaskPhotoRepList)) {
            //转换accessUrl
            List<String> allUrls = airTaskPhotoRepList.stream()
                    // 对每个 AirTaskPhotoRep 对象，生成包含两个 URL 的流（过滤 null，避免存空值）
                    .flatMap(rep -> Stream.of(rep.getPhotoUrl(), rep.getWatermarkUrl())
                            .filter(url -> url != null && !url.trim().isEmpty()) // 可选：过滤 null 和空字符串
                    )
                    .collect(Collectors.toList());
            Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(allUrls);
            List<String> taskIds = new ArrayList<>();
            //查询图片/视频的算法识别状态
            List<SecondRecogBatchVO> secondRecogBatchVOS = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            List<String> photoIds = new ArrayList<>();
            if (Constants.TWO.equals(airResultReq.getDataType())) {
                ids = airTaskPhotoRepList.stream()
                        .filter(airTaskPhotoRep -> Constants.TWO.equals(airTaskPhotoRep.getColType()))
                        .map(AirTaskPhotoRep::getId)
                        .collect(Collectors.toList());
                secondRecogBatchVOS = getUavSynAnalysisResult(ids, com.chinatower.airline.constants.Constants.VIDEO);
                taskIds = airTaskPhotoRepList.stream()
                        .filter(airTaskPhotoRep -> Constants.TWO.equals(airTaskPhotoRep.getColType()))
                        .map(AirTaskPhotoRep::getTaskRecordId)
                        .collect(Collectors.toList());
            }
            if (Constants.FOUR.equals(airResultReq.getDataType())) {
                ids = airTaskPhotoRepList.stream()
                        .filter(airTaskPhotoRep -> Constants.FOUR.equals(airTaskPhotoRep.getColType()))
                        .map(AirTaskPhotoRep::getId)
                        .collect(Collectors.toList());
                //根据id查询 复飞状态
                photoIds = airResultMapper.queryReFlyStatus(ids);
                secondRecogBatchVOS = getUavSynAnalysisResult(ids, com.chinatower.airline.constants.Constants.PHOTO);
                taskIds = airTaskPhotoRepList.stream()
                        .filter(airTaskPhotoRep -> Constants.FOUR.equals(airTaskPhotoRep.getColType()))
                        .map(AirTaskPhotoRep::getTaskRecordId)
                        .collect(Collectors.toList());
            }
            //查询此任务是否为复飞产生的任务
            List<AirTaskRes> airTaskRes = airResultMapper.queryReFlyTaskIds(taskIds);
            List<UavInfo> uavInfos = this.queryDeviceInfo(taskIds);
            List<SecondRecogBatchVO> finalSecondRecogBatchVOS = secondRecogBatchVOS;
            List<String> finalPhotoIds = photoIds;
            airTaskPhotoRepList.stream().forEach(taskPhotoRep -> {
                if (urlMap.containsKey(taskPhotoRep.getPhotoUrl())) {
                    taskPhotoRep.setPhotoAccessUrl(urlMap.get(taskPhotoRep.getPhotoUrl()));
                }
                if (urlMap.containsKey(taskPhotoRep.getWatermarkUrl())) {
                    taskPhotoRep.setWatermarkUrl(urlMap.get(taskPhotoRep.getWatermarkUrl()));
                }
                taskPhotoRep.setEventStatus(Integer.valueOf(Constants.ZERO));
                taskPhotoRep.setCollectStatus(Constants.ONE);
                if (Constants.TWO.equals(taskPhotoRep.getColType())) {
                    if (ObjectUtil.isNotEmpty(finalSecondRecogBatchVOS)) {
                        finalSecondRecogBatchVOS.stream().forEach(secondRecogBatchVO -> {
                            if (secondRecogBatchVO.getVideoId().equals(taskPhotoRep.getId())) {
                                if (Constants.ONE.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.TWO));
                                } else if (Constants.ZERO.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.ONE));
                                } else {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.ZERO));
                                }
                            }
                        });
                    }
                }
                if (Constants.FOUR.equals(taskPhotoRep.getColType())) {
                    List<OrderTypeDto> alarmCodes = new ArrayList<>();
                    Set<String> al = new HashSet<>();
                    if (ObjectUtil.isNotEmpty(finalSecondRecogBatchVOS)) {
                        finalSecondRecogBatchVOS.stream().forEach(secondRecogBatchVO -> {
                            if (secondRecogBatchVO.getPicId().equals(taskPhotoRep.getId())) {
                                OrderTypeDto dto = new OrderTypeDto();
                                if (Constants.ONE.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.TWO));
                                    //告警事件编号
                                    if (StringUtils.isNotEmpty(secondRecogBatchVO.getWarningOrderId())) {
                                        dto.setWarningOrderId(secondRecogBatchVO.getWarningOrderId());
                                        dto.setAlgorithmCode(secondRecogBatchVO.getAlgorithmCode());
                                    }
                                    //告警事件来源（0：告警  1：回收站）
                                    if (StringUtils.isNotEmpty(secondRecogBatchVO.getOrderType())) {
                                        dto.setOrderType(secondRecogBatchVO.getOrderType());
                                    } else {
                                        dto.setOrderType(Constants.ZERO);
                                    }
                                    if (StringUtils.isNotEmpty(secondRecogBatchVO.getAlgorithmCode())) {
                                        al.add(secondRecogBatchVO.getAlgorithmCode());
                                    }
                                } else if (Constants.ZERO.equals(secondRecogBatchVO.getSecondJudgeResult())) {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.ONE));
                                } else {
                                    taskPhotoRep.setEventStatus(Integer.valueOf(Constants.ZERO));
                                }
                                if (!StringUtils.isEmpty(dto.getWarningOrderId())) {
                                    alarmCodes.add(dto);
                                }
                            }
                        });
                        if (!CollectionUtils.isEmpty(alarmCodes)) {
                            taskPhotoRep.setEventStatus(Integer.valueOf(Constants.TWO));
                        }
                        taskPhotoRep.setAlarmCodes(alarmCodes);
                        taskPhotoRep.setAlgorithmCodes(al);
                    }
                    finalPhotoIds.stream().forEach(photoId -> {
                        if (photoId.equals(taskPhotoRep.getId())) {
                            taskPhotoRep.setReFlyStatus(Constants.ONE);
                        }
                    });
                    if (CollectionUtil.isNotEmpty(airTaskRes)) {
                        airTaskRes.stream().forEach(taskRe -> {
                            if (taskRe.getTaskRecordId().equals(taskPhotoRep.getTaskRecordId())) {
                                if (StringUtils.isNotEmpty(taskRe.getOriginalTaskRecordId())) {
                                    taskPhotoRep.setReFlyStatus(Constants.ONE);
                                    taskPhotoRep.setOriginalTaskRecordId(taskRe.getOriginalTaskRecordId());
                                    taskPhotoRep.setOriginalId(taskRe.getOriginalId());
                                }
                            }
                        });
                    }
                    if (CollectionUtil.isNotEmpty(uavInfos)) {
                        uavInfos.stream().forEach(uavInfo -> {
                            if (uavInfo.getUavCode().equals(taskPhotoRep.getUavCode())) {
                                taskPhotoRep.setUavModelName(uavInfo.getUavModelName());
                                taskPhotoRep.setSnCode(uavInfo.getUavSnCode());
                            }
                        });
                    }
                }
            });
        }
        return airTaskPhotoRepList;
    }

    /**
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result delCollectResultById(AirResultReq airResultReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airResultReq.setAcceptNode(platformCode);
        String result = "";
        if (!CollectionUtils.isEmpty(airResultReq.getCollectIds())) {
            // 按照 accessNode 分组
            Map<String, List<CollectReq>> groupedByAccessNode = airResultReq.getCollectIds().stream()
                    .collect(Collectors.groupingBy(CollectReq::getAccessNode));
            // 遍历分组后的数据
            for (Map.Entry<String, List<CollectReq>> entry : groupedByAccessNode.entrySet()) {
                String accessNode = entry.getKey();
                List<CollectReq> collectReqs = entry.getValue();
                try {
                    // 设置当前 accessNode
                    AirResultReq airResult = new AirResultReq();
                    airResult.setAccessNode(accessNode);
                    List<String> ids = collectReqs.stream().map(CollectReq::getId).collect(Collectors.toList());
                    airResult.setId(ids);
                    airResult.setAcceptNode(platformCode);
                    airResult.setDataType(airResultReq.getDataType());
                    // 构建请求参数
                    String joIn = JSON.toJSONString(collectReqs);
                    log.info("调用飞控批量删除图片/视频成果物入参，accessNode: {}，数据: {}", accessNode, joIn);
                    NetConnectUtils.RequestParamter<AirResultReq> paramter = new NetConnectUtils.RequestParamter<>();
                    paramter.setBody(airResult);
                    NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                    path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PHOTO_VIDEO_DELETE);
                    path.setDataType(airResult.getDataType());
                    paramter.setPath(path);
                    // 调用接口
                    ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
                    if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                        result = JSON.parseObject(JSON.toJSONString(cr.getData()), String.class);
                        log.info("accessNode: {} 处理结果: {}", accessNode, result);
                    }
                } catch (Exception e) {
                    log.error("accessNode: {} 调用飞控批量删除图片/视频成果物失败: {}", accessNode, e.getMessage());
                }
            }
            //删除本地的收藏状态
            List<String> ids = airResultReq.getCollectIds().stream().map(CollectReq::getId).collect(Collectors.toList());
            String dataType = "";
            if (com.chinatower.airline.constants.Constants.PHOTO.equals(airResultReq.getDataType())) {
                dataType = Constants.FOUR;
            }
            if (com.chinatower.airline.constants.Constants.VIDEO.equals(airResultReq.getDataType())) {
                dataType = Constants.TWO;
            }
            airResultMapper.updateCollectStatus(ids, dataType);
        }
        return Result.data(result);
    }

    /**
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result secretJson(AirResultReq airResultReq) {
        String serectJson = "";
        if (StringUtils.isNotNull(airResultReq.getSecretJson())) {
            try {
                if (Constants.ZERO.equals(airResultReq.getIsSecret())) {
                    serectJson = stringEncryptor.encrypt(airResultReq.getSecretJson());
                } else {
                    serectJson = stringEncryptor.decrypt(airResultReq.getSecretJson());
                }
            } catch (Exception e) {
                return Result.fail("加解密失败");
            }
        }
        return Result.data(serectJson);
    }

    /**
     * 查询图片/视频属性
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result queryPhotoVideoAttr(AirResultReq airResultReq) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String platformCode = sysUser.getPlatformCode();
        String userId = sysUser.getUserId();
        airResultReq.setAcceptNode(platformCode);
        AirResultPhotoVideoAttrReq airResultPhotoVideoAttrReq = new AirResultPhotoVideoAttrReq();
        List<String> photoIds = new ArrayList<>();
        photoIds.add(airResultReq.getPhotoId());
        List<String> queryId = new ArrayList<>();
        queryId.add(airResultReq.getPhotoId());
        if (StringUtils.isNotEmpty(airResultReq.getDataType()) && Constants.PARAM_VIDEO.equals(airResultReq.getDataType())) {
            airResultPhotoVideoAttrReq.setVideoIds(photoIds);
        } else {
            airResultPhotoVideoAttrReq.setPhotoIds(photoIds);
        }
        //调用飞控接口 查询飞行任务成果照片
        NetConnectUtils.RequestParamter<AirResultPhotoVideoAttrReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airResultPhotoVideoAttrReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PHOTO_VIDEOA_TTR);
        paramterEdit.setPath(pathEdit);
        ResultEntity cr = okhttpUtil.doPostUavGateWay(paramterEdit, airResultReq.getAccessNode(), platformCode);
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<AirResultCollectReq> collectReqs = new ArrayList<>();
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            //根据媒体文件id 查询收藏状态
            collectReqs = airResultMapper.queryCollectStatus(queryId, userId);
            result = (JSONObject) cr.getData();
            if (StringUtils.isNotEmpty(airResultReq.getDataType()) && Constants.PARAM_VIDEO.equals(airResultReq.getDataType())) {
                jsonArray = (JSONArray) result.get("videos");
            } else {
                jsonArray = (JSONArray) result.get("photos");
            }
        }
        if (!jsonArray.isEmpty()) {
            result = (JSONObject) jsonArray.get(0);
            result.put(com.chinatower.airline.constants.Constants.IS_COLLECT, Constants.ZERO);
            if (!CollectionUtils.isEmpty(collectReqs)) {
                result.put(com.chinatower.airline.constants.Constants.IS_COLLECT, Constants.ONE);
            }
        }
        return Result.data(result);
    }

    /**
     * 查询任务
     *
     * @param airResultReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", acceptColumn = "t.accept_node")
    public Result queryReFlightTask(AirResultReq airResultReq) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String platformCode = sysUser.getPlatformCode();
        String userId = sysUser.getUserId();
        AirUavInfo airUavInfo = airResultMapper.queryUavByDeviceCode(airResultReq.getTaskRecordId());
        if (!Constants.ONE.equals(airResultReq.getIsReFly())) {
            return Result.fail("当前任务非复飞任务");
        }
        String orgPhotoId = "";
        if (StringUtils.isEmpty(airResultReq.getOriginalTaskRecordId())) {
            //需要查询的源图片id
            orgPhotoId = airResultReq.getQueryPhotoId();
        } else {
            orgPhotoId = airResultMapper.queryReOrgFlyTask(airResultReq.getTaskRecordId());
        }
        List<String> taskRecordIds = airResultMapper.queryReFlyTask(orgPhotoId);
        //源任务id
        String originalTaskRecordId = "";
        if (StringUtils.isNotEmpty(airResultReq.getOriginalTaskRecordId())) {
            originalTaskRecordId = airResultReq.getOriginalTaskRecordId();
        } else {
            originalTaskRecordId = airResultReq.getTaskRecordId();
        }
        //查询任务名称参数
        AirResultTaskReq resultTaskReq = new AirResultTaskReq();
        resultTaskReq.setAccessNode(airResultReq.getAccessNode());
        List<String> taskIds = new ArrayList();
        taskIds.add(originalTaskRecordId);
        taskIds.addAll(taskRecordIds);
        resultTaskReq.setTaskRecordIds(taskIds);
        resultTaskReq.setPageNo(1);
        resultTaskReq.setDeviceCode(airResultReq.getDeviceCode());
        resultTaskReq.setSort(0);
        resultTaskReq.setPageSize(10000);
        AirResultTaskReq airLineReq = new AirResultTaskReq();
        airLineReq.setDeviceCode(airResultReq.getDeviceCode());
        List<String> flouteIds = airResultMapper.queryFlightIds(airLineReq);
        if (ObjectUtil.isNotEmpty(flouteIds)) {
            resultTaskReq.setFlightRouteId(flouteIds);
        }
        //调用飞控接口 获取飞行结果任务列表
        NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEditTask = new NetConnectUtils.RequestParamter<>();
        paramterEditTask.setBody(resultTaskReq);
        NetConnectUtils.RequestParamter.Path pathEditTask = new NetConnectUtils.RequestParamter.Path();
        pathEditTask.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_LIST);
        paramterEditTask.setPath(pathEditTask);
        ResultEntity taskRes = okhttpUtil.doPostUavGateWay(paramterEditTask, resultTaskReq.getAccessNode(), platformCode);
        List<AirResultTaskRep> airResultTaskReps = new ArrayList<>();
        if (ObjectUtil.isNotNull(taskRes) && ObjectUtil.isNotNull(taskRes.getData())) {
            JSONObject jsonObject = (JSONObject) taskRes.getData();
            JSONArray jsonArray = (JSONArray) jsonObject.get(RECORDS);
            airResultTaskReps = jsonArray.toJavaList(AirResultTaskRep.class);
        }
        AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
        airResultTaskReq.setTaskRecordIds(taskRecordIds);
        airResultTaskReq.setAccessNode(airResultReq.getAccessNode());
        airResultTaskReq.setPage(1);
        airResultTaskReq.setLimit(10000);
        //调用飞控接口 查询飞行任务成果照片
        NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airResultTaskReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_PHOTO);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWayNoException(paramterEdit, airResultTaskReq.getAccessNode(), platformCode);
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_STR);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = (JSONObject) resultEntity.getData();
            JSONArray jsonArray = (JSONArray) jsonObject.get("list");
            List<AirTaskPhotoRep> airTaskPhotoRespons = jsonArray.toJavaList(AirTaskPhotoRep.class);
            List<AirResultTaskRep> finalAirResultTaskReps = airResultTaskReps;
            airTaskPhotoRespons.stream().forEach(airTaskPhotoRep -> {
                if (CollectionUtil.isNotEmpty(finalAirResultTaskReps)) {
                    finalAirResultTaskReps.stream().forEach(airResultTaskRep -> {
                        if (airTaskPhotoRep.getTaskRecordId().equals(airResultTaskRep.getTaskRecordId())) {
                            String airName = airResultTaskRep.getAirlineName().split("-")[0];
                            LocalDateTime dateTime = LocalDateTime.parse(airResultTaskRep.getStartTime(), inputFormat);
                            String formattedDate = dateTime.format(outputFormat);
                            airName = airName + formattedDate;
                            airTaskPhotoRep.setTaskRecordName(airName);
                        }
                    });
                }
                airTaskPhotoRep.setIsMainTaskStatus(Constants.ONE);
                airTaskPhotoRep.setAccessNode(airResultReq.getAccessNode());
                airTaskPhotoRep.setDeviceCode(airResultReq.getDeviceCode());
            });
            AirResultPhotoVideoAttrReq airResultPhotoVideoAttrReq = new AirResultPhotoVideoAttrReq();
            List<String> photoIds = new ArrayList<>();
            photoIds.add(orgPhotoId);
            airResultPhotoVideoAttrReq.setPhotoIds(photoIds);
            //调用飞控接口 查询飞行任务成果照片
            NetConnectUtils.RequestParamter<AirResultPhotoVideoAttrReq> paramterEdit2 = new NetConnectUtils.RequestParamter<>();
            paramterEdit2.setBody(airResultPhotoVideoAttrReq);
            NetConnectUtils.RequestParamter.Path pathEdit2 = new NetConnectUtils.RequestParamter.Path();
            pathEdit2.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PHOTO_VIDEOA_TTR);
            paramterEdit2.setPath(pathEdit2);
            ResultEntity cr = okhttpUtil.doPostUavGateWay(paramterEdit2, airResultReq.getAccessNode(), platformCode);
            JSONObject result = new JSONObject();
            JSONArray jsonArray2 = new JSONArray();
            if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                result = (JSONObject) cr.getData();
                jsonArray2 = (JSONArray) result.get("photos");
                List<AirTaskPhotoRep> airTaskPhotoRespons2 = jsonArray2.toJavaList(AirTaskPhotoRep.class);
                String finalOriginalTaskRecordId = originalTaskRecordId;
                airTaskPhotoRespons2.stream().forEach(airTaskPhotoRep -> {
                    if (CollectionUtil.isNotEmpty(finalAirResultTaskReps)) {
                        finalAirResultTaskReps.stream().forEach(airResultTaskRep -> {
                            if (finalOriginalTaskRecordId.equals(airResultTaskRep.getTaskRecordId())) {
                                String airName = airResultTaskRep.getAirlineName().split("-")[0];
                                LocalDateTime dateTime = LocalDateTime.parse(airResultTaskRep.getStartTime(), inputFormat);
                                String formattedDate = dateTime.format(outputFormat);
                                airName = airName + formattedDate;
                                airTaskPhotoRep.setTaskRecordName(airName);
                            }
                        });
                    }
                    airTaskPhotoRep.setTaskRecordId(finalOriginalTaskRecordId);
                    airTaskPhotoRep.setIsMainTaskStatus(Constants.ZERO);
                    airTaskPhotoRep.setId(airTaskPhotoRep.getPhotoId());
                    airTaskPhotoRep.setPhotoAccessUrl(airTaskPhotoRep.getPhotoUrl());
                    airTaskPhotoRep.setAccessNode(airResultReq.getAccessNode());
                    airTaskPhotoRep.setDeviceCode(airResultReq.getDeviceCode());
                    airTaskPhotoRep.setCreateTime(airTaskPhotoRep.getOriginalDate());
                    airTaskPhotoRep.setTimeCreated(airTaskPhotoRep.getOriginalDate());
                });
                airTaskPhotoRespons.add(airTaskPhotoRespons2.get(0));
            }
            //处理图片的收藏状态
            if (CollectionUtil.isNotEmpty(airTaskPhotoRespons)) {
                List<String> collectIds = airTaskPhotoRespons.stream().map(AirTaskPhotoRep::getId).collect(Collectors.toList());
                List<AirResultCollectReq> airResultCollectReqs = airResultMapper.queryCollectStatus(collectIds, userId);
                airTaskPhotoRespons.stream().forEach(airTaskPhotoRep -> {
                    if (ObjectUtil.isNotNull(airUavInfo)) {
                        airTaskPhotoRep.setUavName(airUavInfo.getUavName());
                    }
                    airResultCollectReqs.stream().forEach(airResultCollectReq -> {
                        if (airTaskPhotoRep.getId().equals(airResultCollectReq.getCollectId())) {
                            airTaskPhotoRep.setCollectStatus(Constants.ONE);
                        }
                    });
                });
            }
            return Result.data(airTaskPhotoRespons);
        }
        return Result.success("");
    }

    /**
     * 查询任务照片视频信息
     *
     * @param airResultReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public TableDataInfo queryTaskPhotoVideoInfo(QueryTaskPhotoVideoReq airResultReq) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_STR);
        List<String> taskRecordIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(airResultReq.getTaskRecordId())) {
            taskRecordIds.add(airResultReq.getTaskRecordId());
        } else {
            List<AirTaskRes> airTaskRes = airResultMapper.queryTaskPhotoVideoInfo(airResultReq);
            taskRecordIds = airTaskRes.stream().map(AirTaskRes::getTaskRecordId).collect(Collectors.toList());
        }
        airResultReq.setTaskRecordIds(taskRecordIds);
        NetConnectUtils.RequestParamter<QueryTaskPhotoVideoReq> paramterEdit2 = new NetConnectUtils.RequestParamter<>();
        paramterEdit2.setBody(airResultReq);
        NetConnectUtils.RequestParamter.Path pathEdit2 = new NetConnectUtils.RequestParamter.Path();
        pathEdit2.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PHOTO_VIDEO_APP);
        paramterEdit2.setPath(pathEdit2);
        ResultEntity cr = okhttpUtil.doPostUavGateWay(paramterEdit2, airResultReq.getAccessNode(), platformCode);
        TableDataInfo tableDataInfo = new TableDataInfo();
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            //查询任务名称参数
            AirResultTaskReq resultTaskReq = new AirResultTaskReq();
            resultTaskReq.setAccessNode(airResultReq.getAccessNode());
            resultTaskReq.setTaskRecordIds(taskRecordIds);
            resultTaskReq.setPageNo(1);
            resultTaskReq.setDeviceCode(airResultReq.getDeviceCode());
            resultTaskReq.setSort(0);
            resultTaskReq.setPageSize(10000);
            AirResultTaskReq airLineReq = new AirResultTaskReq();
            airLineReq.setDeviceCode(airResultReq.getDeviceCode());
            List<String> flouteIds = airResultMapper.queryFlightIds(airLineReq);
            if (ObjectUtil.isNotEmpty(flouteIds)) {
                resultTaskReq.setFlightRouteId(flouteIds);
            }
            //调用飞控接口 获取飞行结果任务列表
            NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEditTask = new NetConnectUtils.RequestParamter<>();
            paramterEditTask.setBody(resultTaskReq);
            NetConnectUtils.RequestParamter.Path pathEditTask = new NetConnectUtils.RequestParamter.Path();
            pathEditTask.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_LIST);
            paramterEditTask.setPath(pathEditTask);
            ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEditTask, resultTaskReq.getAccessNode(), platformCode);
            List<AirResultTaskRep> airResultTaskRepArrayList = new ArrayList<>();
            if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
                JSONObject jsonObject = (JSONObject) resultEntity.getData();
                JSONArray jsonArray = (JSONArray) jsonObject.get(RECORDS);
                airResultTaskRepArrayList = jsonArray.toJavaList(AirResultTaskRep.class);
            }
            //根据任务id查询复飞源任务id
            List<AirTaskRes> taskRes = airResultMapper.queryReFlyTaskIds(taskRecordIds);
            JSONObject result = (JSONObject) cr.getData();
            JSONArray jsonArray = (JSONArray) result.get("list");
            List<QueryTaskPhotoVideoRep> jsonArrayJavaList = jsonArray.toJavaList(QueryTaskPhotoVideoRep.class);
            List<AirResultTaskRep> finalAirResultTaskRepArrayList = airResultTaskRepArrayList;
            jsonArrayJavaList.stream().forEach(queryTaskPhotoVideoRep -> {
                taskRes.stream().forEach(airTask -> {
                    if (queryTaskPhotoVideoRep.getTaskRecordsId().equals(airTask.getTaskRecordId())) {
                        queryTaskPhotoVideoRep.setOriginalTaskRecordId(airTask.getOriginalTaskRecordId());
                        queryTaskPhotoVideoRep.setFlightRouteId(airTask.getFlightRouteId());
                    }
                });
                finalAirResultTaskRepArrayList.stream().forEach(airResultTaskRep -> {
                    if (queryTaskPhotoVideoRep.getTaskRecordsId().equals(airResultTaskRep.getTaskRecordId())) {
                        String airName = airResultTaskRep.getAirlineName().split("-")[0];
                        LocalDateTime dateTime = LocalDateTime.parse(airResultTaskRep.getStartTime(), inputFormat);
                        String formattedDate = dateTime.format(outputFormat);
                        airName = airName + formattedDate;
                        queryTaskPhotoVideoRep.setTaskRecordName(airName);
                    }
                });
            });
            tableDataInfo.setRows(jsonArrayJavaList);
            tableDataInfo.setTotal(Long.parseLong(result.get("totalSize").toString()));
            tableDataInfo.setCode(200);
            return tableDataInfo;
        }
        return tableDataInfo;
    }

    /**
     * 查询对应照片列表
     *
     * @param correspondPointReq
     * @return
     */
    @Override
    public Result queryCorrespondPhotoList(CorrespondPointReq correspondPointReq) {
        List<AirCorrespondPhotoRep> airCorrespondPhotoReps = new ArrayList<>();
        String platformCode = getSelfPlatFormCode();
        //根据基准点维度筛选数据
        if (ObjectUtil.isNotNull(correspondPointReq) && ObjectUtil.isNotEmpty(correspondPointReq.getId())) {
            airCorrespondPhotoReps = airResultMapper.queryCorrespondPhotoList(correspondPointReq);
            //处理url
            if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)) {
                //转换accessUrl
                List<String> url = airCorrespondPhotoReps.stream().map(AirCorrespondPhotoRep::getPhotoUrl).collect(Collectors.toList());
                Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(url);
                airCorrespondPhotoReps.stream().forEach(airCorrespondPhotoRep -> {
                    if (urlMap.containsKey(airCorrespondPhotoRep.getPhotoUrl())) {
                        airCorrespondPhotoRep.setPhotoUrl(urlMap.get(airCorrespondPhotoRep.getPhotoUrl()));
                    }
                });
            }
            return Result.data(airCorrespondPhotoReps);
        } else {
            //根据任务维度筛选数据 调用飞控接口 构造查询条件参数
            List<String> taskRecordIds = new ArrayList<>();
            if ((ObjectUtil.isNotNull(correspondPointReq) && ObjectUtil.isNotEmpty(correspondPointReq.getTaskRecordId()))) {
                taskRecordIds.add(correspondPointReq.getTaskRecordId());
            } else {
                //查询租户设备权限下所有任务ids
                AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
                airResultTaskReq.setDeviceCode(correspondPointReq.getDeviceCode());
                List<String> flouteIds = airResultMapper.queryFlightIds(airResultTaskReq);
                if (CollectionUtil.isEmpty(flouteIds)) {
                    return Result.data(airCorrespondPhotoReps);
                }
                airResultTaskReq.setFlightRouteId(flouteIds);
                taskRecordIds = airResultMapper.queryTaskIds(airResultTaskReq);
            }
            AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
            airResultTaskReq.setDeviceCode(correspondPointReq.getDeviceCode());
            airResultTaskReq.setTaskRecordIds(taskRecordIds);
            airResultTaskReq.setAcceptNode(platformCode);
            airResultTaskReq.setTaskRecordId(correspondPointReq.getTaskRecordId());
            airResultTaskReq.setAccessNode(correspondPointReq.getAccessNode());
            airResultTaskReq.setPage(1);
            airResultTaskReq.setLimit(1000);
            if (StringUtils.isNotEmpty(correspondPointReq.getTimeCreatedEndTime()) && StringUtils.isNotEmpty(correspondPointReq.getTimeCreatedStartTime())) {
                airResultTaskReq.setCreateStartTime(correspondPointReq.getTimeCreatedStartTime());
                airResultTaskReq.setCreateEndTime(correspondPointReq.getTimeCreatedEndTime());
            }
            if (StringUtils.isNotEmpty(correspondPointReq.getLatitude()) && StringUtils.isNotEmpty(correspondPointReq.getLongitude())) {
                airResultTaskReq.setLatitude(Double.valueOf(correspondPointReq.getLatitude()));
                airResultTaskReq.setLongitude(Double.valueOf(correspondPointReq.getLongitude()));
                airResultTaskReq.setRangeValue(3.0);
            }
            //调用飞控接口 查询飞行任务成果照片
            NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
            paramterEdit.setBody(airResultTaskReq);
            NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
            pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_PHOTO);
            paramterEdit.setPath(pathEdit);
            ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airResultTaskReq.getAccessNode(), platformCode);
            if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
                JSONObject jsonObject = (JSONObject) resultEntity.getData();
                JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                airCorrespondPhotoReps = jsonArray.toJavaList(AirCorrespondPhotoRep.class);
                if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)) {
                    airCorrespondPhotoReps.stream().forEach(airCorrespondPhotoRep -> {
                        airCorrespondPhotoRep.setPhotoId(airCorrespondPhotoRep.getId());
                        airCorrespondPhotoRep.setPhotoUrl(airCorrespondPhotoRep.getPhotoAccessUrl());
                    });
                    //查询飞行任务名称
                    AirResultReq airResultReq = new AirResultReq();
                    airResultReq.setDeviceCode(airResultTaskReq.getDeviceCode());
                    airResultReq.setAccessNode(airResultTaskReq.getAccessNode());
                    Result result = queryFlightTaskList(airResultReq);
                    if (ObjectUtil.isNotNull(result) && ObjectUtil.isNotEmpty(result.getData())) {
                        List<AirResultTaskListRep> airResultTaskReps = Optional.ofNullable(result.getData())
                                .filter(data -> data instanceof Collection)
                                .map(data -> (Collection<?>) data)
                                .map(data -> JSON.parseArray(JSON.toJSONString(data), AirResultTaskListRep.class))
                                .orElseGet(ArrayList::new);
                        airCorrespondPhotoReps.forEach(photoRep ->
                                airResultTaskReps.stream()
                                        .filter(task -> photoRep.getTaskRecordId().equals(task.getTaskRecordId()))
                                        .findFirst()
                                        .ifPresent(task -> photoRep.setTaskRecordName(task.getTaskName()))
                        );
                    }
                }
                return Result.data(airCorrespondPhotoReps);
            }
            return Result.data(airCorrespondPhotoReps);
        }
    }

    /**
     * 查询飞行任务下拉列表 租户全量权限下
     *
     * @param airResultReq
     * @return
     */
    @Override
    public Result queryFlightTaskList(AirResultReq airResultReq) {
        String platformCode = getSelfPlatFormCode();
        AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
        airResultTaskReq.setDeviceCodeType(airResultReq.getDeviceCodeType());
        airResultTaskReq.setDeviceCode(airResultReq.getDeviceCode());
        airResultTaskReq.setAcceptNode(platformCode);
        airResultTaskReq.setPageNo(1);
        airResultTaskReq.setPageSize(10000);
        airResultTaskReq.setSort(1);
        //查询租户权限下任务
        List<AirResultTaskListRep> airResultTaskReps = new ArrayList<>();
//        List<String> flouteIds = airResultMapper.queryFlightIds(airResultTaskReq);
//        if (CollectionUtil.isEmpty(flouteIds)) {
//            return Result.data(airResultTaskReps);
//        }
//        airResultTaskReq.setFlightRouteId(flouteIds);
        //校验查询类型
        if (StringUtils.isEmpty(airResultTaskReq.getDeviceCodeType())) {
            airResultTaskReq.setDeviceCodeType(Constants.NEST); //默认机巢
        }
        //查询用户权限下的任务ids集合
        List<String> ids = airResultMapper.queryTaskIds(airResultTaskReq);
        if (CollectionUtil.isNotEmpty(ids)) {
            airResultTaskReq.setTaskRecordIds(ids);
        }
//        List<AirResultTaskListRep> airTaskInfos = airResultMapper.queryTaskInfoList(airResultReq);
        //调用飞控接口 获取飞行结果任务列表
        NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airResultTaskReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_LIST);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airResultReq.getAccessNode(), platformCode);
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_STR);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = (JSONObject) resultEntity.getData();
            JSONArray jsonArray = (JSONArray) jsonObject.get(RECORDS);
            airResultTaskReps = jsonArray.toJavaList(AirResultTaskListRep.class);
            if (CollectionUtil.isNotEmpty(airResultTaskReps)) {
                airResultTaskReps.stream().forEach(task -> {
                    String airName = task.getAirLineName().split("-")[0];
                    LocalDateTime dateTime = LocalDateTime.parse(task.getStartTime(), inputFormat);
                    String formattedDate = dateTime.format(outputFormat);
                    airName = airName + formattedDate;
                    task.setTaskName(airName);
                });
            }
            return Result.data(airResultTaskReps);
        }
        return Result.data(airResultTaskReps);
    }

    /**
     * 查询mongo数据分页
     *
     * @param airHistoryReq
     * @return
     */
    @Override
    public List<UavFlyReq> queryHisRecordLimit(AirHistoryReq airHistoryReq) {
        if (ObjectUtil.isEmpty(airHistoryReq) || StringUtils.isEmpty(airHistoryReq.getTaskRecordId())) {
            return new ArrayList<>();
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(TASK_RECORD_ID).is(airHistoryReq.getTaskRecordId()));
            List<UavFlyReq> uavFlyReqs = mongoTemplate.find(query, UavFlyReq.class, Constants.UAV_SYNC_UFNC);
            return uavFlyReqs;
        } catch (Exception e) {
            log.error("查询mongo数据异常", e);
        }
        return new ArrayList<>();
    }

    /**
     * 查询mongo数据
     *
     * @param airHistoryReq
     * @return
     */
    @Override
    public List<UavFlyReq> queryHisRecordsBySelf(AirHistoryReq airHistoryReq) {
        if (ObjectUtil.isEmpty(airHistoryReq) || StringUtils.isEmpty(airHistoryReq.getTaskRecordId())) {
            return new ArrayList<>();
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT); // 根据实际格式调整
            Date alarmTime = sdf.parse(airHistoryReq.getVideoStartTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarmTime);
            calendar.add(Calendar.SECOND, (int) Math.ceil(airHistoryReq.getDuration()));
            String endTimeStr = sdf.format(calendar.getTime());
            Query query = new Query();
            query.addCriteria(Criteria.where(TASK_RECORD_ID).is(airHistoryReq.getTaskRecordId()));
            query.addCriteria(
                    Criteria.where(com.chinatower.airline.constants.Constants.TIME_STAMP)
                            .gte(airHistoryReq.getVideoStartTime())
                            .lte(endTimeStr)
            );
            List<UavFlyReq> uavFlyReqs = mongoTemplate.find(query, UavFlyReq.class, Constants.UAV_SYNC_UFNC);
            log.info("查询mongo数据结果{}", uavFlyReqs.size());
            return uavFlyReqs;
        } catch (Exception e) {
            log.error("查询mongo数据异常", e);
        }
        return new ArrayList<>();
    }

    /**
     * 根据设备类型查询照片(摄像机/无人机)
     *
     * @param queryPhotosTypesReq
     * @return
     */
    @Override
    public TableDataInfo queryPhotosByType(QueryPhotosTypesReq queryPhotosTypesReq) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(0);
        tableDataInfo.setCode(Constants.CODE_200);

        if (ObjectUtil.isNull(queryPhotosTypesReq)) {
            return tableDataInfo;
        }

        List<QueryPhotosTypesRep> resultList = new ArrayList<>();
        long total = 0;
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();

        // 根据是否收藏走不同查询路径
        if (Constants.ZERO.equals(queryPhotosTypesReq.getIsCollect())) {
            if (Constants.ZERO.equals(queryPhotosTypesReq.getType())) {
                // 摄像机照片查询
                Page<VmVideoPresetSnapshotMongo> pageResult = queryCameraPhotos(queryPhotosTypesReq);
                resultList = convertCameraPhotos(pageResult.getList(), queryPhotosTypesReq);
                total = pageResult.getCount();
            } else if (Constants.ONE.equals(queryPhotosTypesReq.getType())) {
                // 无人机照片查询
                TableDataInfo droneResult = queryDronePhotos(queryPhotosTypesReq);
                List<AirCorrespondPhotoRep> airCorrespondPhotoReps = (List<AirCorrespondPhotoRep>) droneResult.getRows();
                if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)) {
                    List<QueryPhotosTypesRep> finalResultList = resultList;
                    airCorrespondPhotoReps.forEach(airCorrespondPhotoRep -> {
                        QueryPhotosTypesRep queryPhotosTypesRep = new QueryPhotosTypesRep();
                        BeanUtils.copyProperties(airCorrespondPhotoRep, queryPhotosTypesRep);
                        if (StringUtils.isEmpty(queryPhotosTypesRep.getPhotoId())) {
                            queryPhotosTypesRep.setPhotoId(airCorrespondPhotoRep.getPhotoId());
                        }
                        queryPhotosTypesRep.setDeviceCode(queryPhotosTypesReq.getDeviceCode());
                        queryPhotosTypesRep.setPhotoName(airCorrespondPhotoRep.getName());
                        queryPhotosTypesRep.setIsCollect(Constants.ZERO);
                        queryPhotosTypesRep.setType(Constants.ONE);
                        finalResultList.add(queryPhotosTypesRep);
                    });
                }
                total = droneResult.getTotal();
            } else {
                resultList = new ArrayList<>();
            }
        } else {
            // 查询收藏照片
            PageInfo<QueryPhotosTypesRep> pageInfo = queryCollectedPhotos(queryPhotosTypesReq, currentUser);
            resultList = pageInfo.getList();
            total = pageInfo.getTotal();
        }
        // 处理结果集
        if (CollectionUtil.isNotEmpty(resultList)) {
            updateCollectStatus(resultList, currentUser);
            tableDataInfo.setRows(resultList);
            tableDataInfo.setTotal(total);
        }

        return tableDataInfo;
    }

    /**
     * 根据登陆用户信息查询收藏的成果物的任务id集合
     *
     * @param queryClollectReq
     * @return
     */
    @Override
    public Result<List<String>> queryTaskRecordIds(QueryCollectReq queryClollectReq) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        queryClollectReq.setUserId(currentUser.getUserId());
        queryClollectReq.setIndustryCode(currentUser.getIndustryCode());
        List<String> stringList = airResultMapper.queryCollectTaskIds(queryClollectReq);
        return Result.data(stringList);
    }

    /**
     * 查询摄像机照片
     */
    private Page<VmVideoPresetSnapshotMongo> queryCameraPhotos(QueryPhotosTypesReq req) {
        PresetImgJobDTO dto = new PresetImgJobDTO();
        dto.setDeviceCode(req.getDeviceCode());
        dto.setPageSize(req.getPageSize());
        dto.setPageNum(req.getPageNum());
        dto.setSort(req.getSort());
        dto.setChannelCode(req.getChannelCode());

        // 设置时间范围
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
            String startStr = req.getTimeCreatedStartTime();
            String endStr = req.getTimeCreatedEndTime();
            if (!StringUtils.isEmpty(startStr) && !StringUtils.isEmpty(endStr)) {
                Date startDate = dateFormat.parse(startStr);
                Date endDate = dateFormat.parse(endStr);
                dto.setStartTime(startDate);
                dto.setEndTime(endDate);
            }
        } catch (Exception e) {
            log.error("时间格式转换异常", e);
        }

        // 设置预置点ID
        if (StringUtils.isNotEmpty(req.getPresetId())) {
            dto.setPresetId(Long.valueOf(req.getPresetId()));
        }

        // 执行查询
        Result result = iVideoPresetService.queryPresetImgJob(dto);
        return (Page<VmVideoPresetSnapshotMongo>) result.getData();
    }

    /**
     * 转换摄像机照片结果
     */
    private List<QueryPhotosTypesRep> convertCameraPhotos(List<VmVideoPresetSnapshotMongo> list, QueryPhotosTypesReq req) {
        return list.stream().map(snapshot -> {
            QueryPhotosTypesRep rep = new QueryPhotosTypesRep();
            rep.setPhotoUrl(snapshot.getImgUrl());
            rep.setLatitude(snapshot.getLatitude());
            rep.setLongitude(snapshot.getLongitude());
            rep.setIsCollect(Constants.ZERO);
            rep.setPhotoId(snapshot.getId());
            rep.setTimeCreated(new SimpleDateFormat(TIME_FORMAT).format(snapshot.getSnapshotTime()));
            rep.setDeviceCode(req.getDeviceCode());
            rep.setType(Constants.ZERO);
            rep.setPhotoName(snapshot.getChannelName() + snapshot.getPresetName());
            return rep;
        }).collect(Collectors.toList());
    }

    /**
     * 查询无人机照片
     */
    private TableDataInfo queryDronePhotos(QueryPhotosTypesReq req) {
        CorrespondPointReq pointReq = new CorrespondPointReq();
        pointReq.setDeviceCode(req.getDeviceCode());
        pointReq.setTaskRecordId(req.getTaskRecordId());
        pointReq.setId(req.getCorrelationId());
        pointReq.setPageSize(req.getPageSize());
        pointReq.setPageNum(req.getPageNum());
        pointReq.setSort(req.getSort());
        pointReq.setAccessNode(req.getAccessNode());
        pointReq.setTimeCreatedStartTime(req.getTimeCreatedStartTime());
        pointReq.setTimeCreatedEndTime(req.getTimeCreatedEndTime());

        return queryCorrespondPhotoListLimit(pointReq);
    }

    /**
     * 查询收藏照片
     */
    private PageInfo<QueryPhotosTypesRep> queryCollectedPhotos(QueryPhotosTypesReq req, SysUser user) {
        // 设置分页
        if (StringUtils.isNotNull(req.getPageNum()) && StringUtils.isNotNull(req.getPageSize())) {
            PageHelper.startPage(req.getPageNum(), req.getPageSize(), true, false, null);
        }

        // 设置用户信息
        req.setUserId(user.getUserId());
        req.setIndustryCode(user.getIndustryCode());
        req.setSort(StringUtils.isEmpty(req.getSort()) ? Constants.ONE : req.getSort());

        // 执行查询
        List<QueryPhotosTypesRep> list = airResultMapper.queryCollectPhotoByUserId(req);

        // 处理结果
        if (CollectionUtil.isNotEmpty(list)) {
            // 处理照片URL加密
            Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(
                    list.stream().map(QueryPhotosTypesRep::getPhotoUrl).collect(Collectors.toList()));

            list.forEach(photo -> {
                // 更新加密URL
                if (urlMap.containsKey(photo.getPhotoUrl())) {
                    String encryptedUrl = urlMap.get(photo.getPhotoUrl());
                    if (encryptedUrl != null) {
                        photo.setPhotoUrl(encryptedUrl);
                    }
                }
                // 设置照片类型
                if (Constants.FIVE.equals(photo.getCollectType())) {
                    photo.setType(Constants.ZERO);
                } else if (Constants.SIX.equals(photo.getCollectType())) {
                    photo.setType(Constants.ONE);
                }
            });
        }

        return new PageInfo<>(list);
    }

    /**
     * 更新收藏状态
     */
    private void updateCollectStatus(List<QueryPhotosTypesRep> photos, SysUser user) {
        List<String> photoIds = photos.stream()
                .map(QueryPhotosTypesRep::getPhotoId)
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(photoIds)) {
            return;
        }

        List<AirCollectPhotoRep> collectedPhotos = airResultMapper.queryCollectPhotos(
                photoIds, user.getUserId(), user.getIndustryCode());

        if (CollectionUtil.isNotEmpty(collectedPhotos)) {
            // 构建照片ID到收藏状态的映射
            Set<String> collectedPhotoIds = collectedPhotos.stream()
                    .map(AirCollectPhotoRep::getPhotoId)
                    .collect(Collectors.toSet());

            // 更新收藏状态
            photos.forEach(photo -> {
                if (collectedPhotoIds.contains(photo.getPhotoId())) {
                    photo.setIsCollect(Constants.ONE);
                }
            });
        }
    }

    /**
     * 查询对应照片列表
     *
     * @param correspondPointReq
     * @return
     */
    public TableDataInfo queryCorrespondPhotoListLimit(CorrespondPointReq correspondPointReq) {
        List<AirCorrespondPhotoRep> airCorrespondPhotoReps = new ArrayList<>();
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        Integer pageNum = correspondPointReq.getPageNum();
        Integer pageSize = correspondPointReq.getPageSize();
        TableDataInfo tableDataInfo = new TableDataInfo();
        //根据基准点维度筛选数据
        if (ObjectUtil.isNotNull(correspondPointReq) && ObjectUtil.isNotEmpty(correspondPointReq.getId())) {
            if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
                PageHelper.startPage(pageNum, pageSize, true, false, null);
            }
            airCorrespondPhotoReps = airResultMapper.queryCorrespondPhotoList(correspondPointReq);
            //处理url
            if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)) {
                //转换accessUrl
                List<String> url = airCorrespondPhotoReps.stream().map(AirCorrespondPhotoRep::getPhotoUrl).collect(Collectors.toList());
                Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(url);
                airCorrespondPhotoReps.stream().forEach(airCorrespondPhotoRep -> {
                    if (urlMap.containsKey(airCorrespondPhotoRep.getPhotoUrl())) {
                        airCorrespondPhotoRep.setPhotoUrl(urlMap.get(airCorrespondPhotoRep.getPhotoUrl()));
                    }
                });
            }
            tableDataInfo = getDataTable(airCorrespondPhotoReps);
            return tableDataInfo;
        } else {
            //根据任务维度筛选数据 调用飞控接口 构造查询条件参数
            List<String> taskRecordIds = new ArrayList<>();
            if ((ObjectUtil.isNotNull(correspondPointReq) && ObjectUtil.isNotEmpty(correspondPointReq.getTaskRecordId()))) {
                taskRecordIds.add(correspondPointReq.getTaskRecordId());
            } else {
                //查询租户设备权限下所有任务ids
                AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
                airResultTaskReq.setDeviceCode(correspondPointReq.getDeviceCode());
                List<String> flouteIds = airResultMapper.queryFlightIds(airResultTaskReq);
                if (CollectionUtil.isEmpty(flouteIds)) {
                    return new TableDataInfo();
                }
                airResultTaskReq.setFlightRouteId(flouteIds);
                taskRecordIds = airResultMapper.queryTaskIds(airResultTaskReq);
            }
            AirResultTaskReq airResultTaskReq = new AirResultTaskReq();
            airResultTaskReq.setDeviceCode(correspondPointReq.getDeviceCode());
            airResultTaskReq.setTaskRecordIds(taskRecordIds);
            airResultTaskReq.setAcceptNode(platformCode);
            airResultTaskReq.setAccessNode(correspondPointReq.getAccessNode());
            airResultTaskReq.setPage(correspondPointReq.getPageNum());
            airResultTaskReq.setLimit(correspondPointReq.getPageSize());
            airResultTaskReq.setSort(Integer.valueOf(correspondPointReq.getSort()));
            if (StringUtils.isNotEmpty(correspondPointReq.getTimeCreatedEndTime()) && StringUtils.isNotEmpty(correspondPointReq.getTimeCreatedStartTime())) {
                airResultTaskReq.setCreateStartTime(correspondPointReq.getTimeCreatedStartTime());
                airResultTaskReq.setCreateEndTime(correspondPointReq.getTimeCreatedEndTime());
            }
            if (StringUtils.isNotEmpty(correspondPointReq.getLatitude()) && StringUtils.isNotEmpty(correspondPointReq.getLongitude())) {
                airResultTaskReq.setLatitude(Double.valueOf(correspondPointReq.getLatitude()));
                airResultTaskReq.setLongitude(Double.valueOf(correspondPointReq.getLongitude()));
                airResultTaskReq.setRangeValue(3.0);
            }
            //调用飞控接口 查询飞行任务成果照片
            NetConnectUtils.RequestParamter<AirResultTaskReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
            paramterEdit.setBody(airResultTaskReq);
            NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
            pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_TASK_PHOTO);
            paramterEdit.setPath(pathEdit);
            ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airResultTaskReq.getAccessNode(), platformCode);
            if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
                JSONObject jsonObject = (JSONObject) resultEntity.getData();
                JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                Integer totalCount = (Integer) jsonObject.get(TOTAL);
                tableDataInfo.setTotal(totalCount);
                airCorrespondPhotoReps = jsonArray.toJavaList(AirCorrespondPhotoRep.class);
                if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)) {
                    airCorrespondPhotoReps.stream().forEach(airCorrespondPhotoRep -> {
                        airCorrespondPhotoRep.setPhotoId(airCorrespondPhotoRep.getId());
                        airCorrespondPhotoRep.setPhotoUrl(airCorrespondPhotoRep.getPhotoAccessUrl());
                    });
                    //查询飞行任务名称
                    AirResultReq airResultReq = new AirResultReq();
                    airResultReq.setDeviceCode(airResultTaskReq.getDeviceCode());
                    airResultReq.setAccessNode(airResultTaskReq.getAccessNode());
                    Result result = queryFlightTaskList(airResultReq);
                    if (ObjectUtil.isNotNull(result) && ObjectUtil.isNotEmpty(result.getData())) {
                        List<AirResultTaskListRep> airResultTaskReps = Optional.ofNullable(result.getData())
                                .filter(data -> data instanceof Collection)
                                .map(data -> (Collection<?>) data)
                                .map(data -> JSON.parseArray(JSON.toJSONString(data), AirResultTaskListRep.class))
                                .orElseGet(ArrayList::new);
                        airCorrespondPhotoReps.forEach(photoRep ->
                                airResultTaskReps.stream()
                                        .filter(task -> photoRep.getTaskRecordId().equals(task.getTaskRecordId()))
                                        .findFirst()
                                        .ifPresent(task -> photoRep.setTaskRecordName(task.getTaskName()))
                        );
                    }
                }
                tableDataInfo.setRows(airCorrespondPhotoReps);
                return tableDataInfo;
            }
            tableDataInfo.setCode(Constants.CODE_200);
            return tableDataInfo;
        }
    }

    /**
     * 获取当前节点
     *
     * @return
     */
    public String getSelfPlatFormCode() {
        String configValue = "";
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if (result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                configValue = sysConfigInfo.getConfigValue();
            } else {
                log.info("getSelfPlatFormCode-获取当前节点失败:{}", JSON.toJSONString(result));
            }
        } else {
            log.info("getSelfPlatFormCode-获取当前节点失败:{}", JSON.toJSONString(result));
        }
        return configValue;
    }
}
