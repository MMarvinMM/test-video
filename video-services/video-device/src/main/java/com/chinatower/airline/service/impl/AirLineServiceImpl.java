package com.chinatower.airline.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.constants.DJINestTypeCode;
import com.chinatower.airline.constants.UavConstants;
import com.chinatower.airline.dto.request.*;
import com.chinatower.airline.dto.request.AirPointToFlyReq;
import com.chinatower.airline.dto.response.*;
import com.chinatower.airline.enums.AirLineTypeEnum;
import com.chinatower.airline.mapper.*;
import com.chinatower.airline.service.AirAlgorithmsService;
import com.chinatower.airline.service.AirConfigService;
import com.chinatower.airline.service.AirLineService;
import com.chinatower.airline.service.AirPlanService;
import com.chinatower.airline.util.DistanceUtils;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.device.entity.vo.TenantInfoRes;
import com.chinatower.device.entity.vo.UavInfo;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 飞控模块处理类
 */
@Slf4j
@Service
public class AirLineServiceImpl implements AirLineService {
    private static String KMZ_PATH = "kmzPath";
    private String FILE_NAME = "fileName";
    private static String UOS_INFO = "uosInfo";
    @Resource
    private SystemFeignClient systemFeignClient;
    private static String FILE_PATH = "filePath";
    private static String CLOSE = "close";
    private static String TASK_RECORD_ID = "taskRecordId";
    private static String ERR_MSG = "调用飞控飞行失败";

    @Resource
    private AirAlgorithmsMapper airAlgorithmsMapper;
    @Resource
    private AirAlgorithmsService airAlgorithmsService;
    @Resource
    private AirPlanService airPlanService;
    @Resource
    private AirLineMapper airLineMapper;
    @Resource
    private UavOkhttpUtil okhttpUtil;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private FileFeignClient fileFeignClient;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private AirLineSnapshotMapper airLineSnapshotMapper;
    @Resource
    private AirTaskMapper airTaskMapper;
    @Resource
    private AirConfigService airConfigService;
    @Resource
    private UavOkhttpUtil uavOkhttpUtil;

    @Resource
    private AirLineSensorInfoMapper airLineSensorInfoMapper;
    /**
     * 获取无人机算法配置
     *
     * @param alarmTypeDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code")
    public Result getAirAlarmType(AirAlarmTypeReq alarmTypeDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            return Result.fail("未查到登录信息！");
        }
        alarmTypeDTO.setTenantId(loginUser.getUser().getTenantId());
        alarmTypeDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (CollectionUtil.isEmpty(alarmTypeDTO.getAppScenarios())){
            List<String> deviceType = new ArrayList<>();
            deviceType.add(Constants.TWO);
            alarmTypeDTO.setAppScenarios(deviceType);
        }
        List<Map<String, Object>> results = airAlgorithmsMapper.getUavAlgorithmsList(alarmTypeDTO);
        if ("1".equals(alarmTypeDTO.getIsPhotoContrast())) {
            results = results.stream()
                    .filter(this::hasSupportBaselinePic)
                    .collect(Collectors.toList());
        }

        return Result.data(results);
    }

    /**
     * 检查 extendJson 是否包含 supportBaselinePic:1
     */
    private boolean hasSupportBaselinePic(Map<String, Object> map) {
        Object extendJsonObj = map.get("extendJson");
        if (extendJsonObj == null) {
            return false;
        }
        String extendJson = extendJsonObj.toString();
        if (extendJson.trim().isEmpty()) {
            return false;
        }
        try {
            // 使用 JSON 解析检查
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(extendJson);
            JsonNode baselinePicNode = jsonNode.get("supportBaselinePic");
            if (baselinePicNode != null) {
                if (baselinePicNode.isTextual()) {
                    return "1".equals(baselinePicNode.asText());
                } else if (baselinePicNode.isNumber()) {
                    return baselinePicNode.asInt() == 1;
                } else if (baselinePicNode.isBoolean()) {
                    return baselinePicNode.asBoolean();
                }
            }
            return false;
        } catch (Exception e) {
            // JSON 解析失败时，降级到字符串匹配
            log.warn("JSON解析失败，使用字符串匹配: {}", extendJson, e);
            return extendJson.contains("\"supportBaselinePic\":\"1\"") ||
                    extendJson.contains("\"supportBaselinePic\":1");
        }
    }

    /**
     * 新增/编辑航线（包含上传文件）
     *
     * @param airEditDto
     * @return
     */
    @Override
    @Transactional
    public Result editAirLine(AirEditDto airEditDto, MultipartFile kmzFile) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airEditDto.setAcceptNode(platformCode);
        String flightRouteId = "";
        AjaxResult ajaxResult = fileFeignClient.uploadFile(kmzFile);
        if (ajaxResult != null && ajaxResult.ok()) {
            com.chinatower.file.vo.SysFileInfo  sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
            log.info("新增编辑航线调用file服务返回文件信息：{}", JSONObject.toJSON(sysFileInfo));
            if (ObjectUtil.isEmpty(sysFileInfo.getFileUrl())){
                return Result.fail("文件上传获取fileUrl失败！");
            }
            String url = sysFileInfo.getProxyAddr();
            String fileName = sysFileInfo.getFileName();
            airEditDto.setFileName(fileName);
            airEditDto.setStoragePath(url);
            airEditDto.setFileAccessPath(sysFileInfo.getFileUrl());
            airEditDto.setKmzUrl(url);
            flightRouteId = this.editAir(airEditDto, loginUser);
        }else {
            log.info("新增编辑航线调用文件服务返回信息异常");
        }
        return Result.data(flightRouteId);
    }

    /**
     * 新增/编辑航线
     *
     * @return
     */
    private String editAir(AirEditDto airEditDto, LoginUser loginUser) {
        String oldKmzJson = airEditDto.getKmzJson();
        //调用飞控接口 新增/编辑航线 处理航点航线的 uosInfo定向拍照字段
        if (Constants.ONE.equals(airEditDto.getType().toString())) {
            JSONObject oldKmzJsonObject = JSONObject.parseObject(oldKmzJson);
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            JSONArray jsonArray = uosInfoObject.getJSONArray("pointInfoList");
            List<String> photoAccessUrl = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String virtualPhotoUrl = null;
                JSONObject actionInfo = (JSONObject) jsonArray.get(i);
                if ((com.chinatower.airline.constants.Constants.ORIENTED_SHOOT).equals(actionInfo.getString("actionType"))) {
                    virtualPhotoUrl = actionInfo.getString(com.chinatower.airline.constants.Constants.VIRTUAL_PHOTO_URL);
                    photoAccessUrl.add(virtualPhotoUrl);
                }
            }
            Map<String,String> accessUrlList = fileUrlEncryptUtil.dealObsFileUrlBatch(photoAccessUrl);
            //替换可访问呢的路径
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject actionInfo = (JSONObject) jsonArray.get(i);
                if ((com.chinatower.airline.constants.Constants.ORIENTED_SHOOT).equals(actionInfo.getString("actionType"))) {
                    actionInfo.put(com.chinatower.airline.constants.Constants.VIRTUAL_PHOTO_URL, accessUrlList.get(actionInfo.getString(com.chinatower.airline.constants.Constants.VIRTUAL_PHOTO_URL)));
                }
            }
            airEditDto.setKmzJson(oldKmzJsonObject.toJSONString());
        }
        //调用飞控接口 新增/编辑航线 处理倾斜摄影和环绕飞行的 kmzJson字段
        if (Constants.THREE.equals(airEditDto.getType().toString())) {
            JSONObject oldKmzJsonObject = JSONObject.parseObject(oldKmzJson);
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            String usoInfo = airEditDto.getUosInfo();
            JSONObject jsonObject = JSONObject.parseObject(usoInfo);
            JSONArray jsonArray = jsonObject.getJSONArray("multiList");
            JSONObject object = (JSONObject) jsonArray.get(1);
            JSONObject object1 = (JSONObject) jsonArray.get(0);
            object.put(KMZ_PATH,airEditDto.getFileAccessPath());
            object.put(FILE_NAME,airEditDto.getFileName());
            object1.put(KMZ_PATH,airEditDto.getFileAccessPath());
            object1.put(FILE_NAME,airEditDto.getFileName());
            JSONObject uosInfo = new JSONObject();
            uosInfo.put(UOS_INFO,jsonObject);
            uosInfoObject = uosInfo;
            oldKmzJsonObject.put(UOS_INFO, jsonObject);
            oldKmzJson = oldKmzJsonObject.toJSONString();
            airEditDto.setKmzJson(uosInfo.toJSONString());
        }
        if (Constants.FOUR.equals(airEditDto.getType().toString())){
            JSONObject oldKmzJsonObject = JSONObject.parseObject(oldKmzJson);
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            String usoInfo = airEditDto.getUosInfo();
            JSONObject jsonObject = JSONObject.parseObject(usoInfo);
            jsonObject.put("filePath",airEditDto.getFileAccessPath());
            jsonObject.put(FILE_NAME,airEditDto.getFileName());
            JSONObject uosInfo = new JSONObject();
            uosInfo.put(UOS_INFO,jsonObject);
            uosInfoObject = uosInfo;
            oldKmzJsonObject.put(UOS_INFO, jsonObject);
            oldKmzJson = oldKmzJsonObject.toJSONString();
            airEditDto.setKmzJson(uosInfo.toJSONString());
        }
        if (ObjectUtil.isNotEmpty(airEditDto.getFrogLeap())){
            if(airEditDto.getFrogLeap() == 1){
                airEditDto.setMultiDock(1);
                airEditDto.setTakeOffDeviceCode(airEditDto.getStartNestCode());
                airEditDto.setLandDeviceCode(airEditDto.getEndNestCode());
            }else {
                airEditDto.setEndNestCode(airEditDto.getStartNestCode());
            }
        }
        NetConnectUtils.RequestParamter<AirEditDto> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airEditDto);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_AIR_EDIT);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airEditDto.getAccessNode(), loginUser.getUser().getPlatformCode());
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            //判断新增还是编辑航线
            boolean addOrEdit = false;
            if (StringUtils.isEmpty(airEditDto.getFlightRouteId())) {
                addOrEdit = true;
            }
            JSONObject jsonObject = (JSONObject) resultEntity.getData();
            String id = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
            airEditDto.setFlightRouteId(id);
            //是否切换客户视角
            String industryCode = loginUser.getUser().getIndustryCode();
            String platformCode = loginUser.getUser().getPlatformCode();
            String tenantId = loginUser.getUser().getTenantId();
            String userId = loginUser.getUser().getUserId();
            String appCode = loginUser.getUser().getAppVerCode();
            airEditDto.setIndustryCode(industryCode);
            airEditDto.setAcceptNode(platformCode);
            airEditDto.setFlightMainType(Constants.ZERO);
            airEditDto.setTenantId(tenantId);
            airEditDto.setAppVerCode(appCode);
            airEditDto.setKmzJson(oldKmzJson);
            if (ObjectUtil.isNotEmpty(airEditDto.getWarningTypeId())) {
                List<String> warningTypeIds = airEditDto.getWarningTypeId();
                String warningTypeIdsString = String.join(",", warningTypeIds.stream().map(Object::toString).collect(Collectors.toList()));
                airEditDto.setWarningTypeIds(warningTypeIdsString);
            }
            //新增编辑航线 生成航线快照记录
            airLineSnapshotMapper.addAirLineSnapshot(airEditDto);
            airEditDto.setSnapshotId(airEditDto.getId());
            //新增航点图片拍照对比
            JSONObject oldKmzJsonObject = JSONObject.parseObject(oldKmzJson);
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            List<PointsCompareTask> insertTasks = new ArrayList<>();
            if(uosInfoObject.containsKey("pointsCompareTaskList")){
                JSONArray jsonArray = uosInfoObject.getJSONArray("pointsCompareTaskList");
                if (ObjectUtil.isNotEmpty(jsonArray)) {
                    List<PointsCompareTask> tasks = jsonArray.toJavaList(PointsCompareTask.class);
                    for (PointsCompareTask task : tasks) {
                        if (Constants.ONE.equals(task.getIsOpen())){
                            task.setFlightRouteId(id);
                            task.setAlgorithmCode(task.getAlgorithm().get(0));
                            task.setExecutionStartDate(task.getExecutionDate().get(0));
                            task.setExecutionEndDate(task.getExecutionDate().get(1));
                            task.setUserId(userId);
                            insertTasks.add(task);
                        }
                    }
                }
            }
            //新增航点自动二次识别算法任务
            List<AirEditDto.PointAlarmCodes> pointAlarmCodesList = new ArrayList<>();
            if (StringUtils.isNotEmpty(airEditDto.getPointAlarmCodes())){
                pointAlarmCodesList = this.addPointAlarmCodes(airEditDto);
            }
            log.info("新增/编辑航线editAir-uosInfo:{}", uosInfoObject);
            if (ObjectUtil.isNotNull(uosInfoObject)
                    && uosInfoObject.containsKey(Constants.TASK_MILES) && ObjectUtil.isNotNull(uosInfoObject.get(Constants.TASK_MILES))) {
                airEditDto.setAirMileage(String.valueOf(uosInfoObject.get(Constants.TASK_MILES)));
            }
            //喇叭配置
            String uosInfoForHorn = airEditDto.getUosInfo();
            if (StringUtils.isBlank(uosInfoForHorn)) {
                uosInfoForHorn = ObjectUtil.isNotNull(uosInfoObject) ? uosInfoObject.toJSONString() : null;
            }
            List<VmAirLineSensorInfo> airLineHornInfoList = buildHornInfo(uosInfoForHorn, userId, airEditDto.getFlightRouteId(), airEditDto.getDeviceCode());
            //本地新增/编辑航线
            if (addOrEdit) {
                airEditDto.setCreateUser(userId);
                airLineMapper.addAirLine(airEditDto);
                if (CollectionUtil.isNotEmpty(insertTasks)){
                    airLineMapper.addAirPhotoCompare(insertTasks);
                }
                if (CollectionUtil.isNotEmpty(pointAlarmCodesList)){
                    airLineMapper.addAirPointInfo(pointAlarmCodesList,id);
                }
                if (CollectionUtil.isNotEmpty(airLineHornInfoList)){
                    airLineSensorInfoMapper.batchInsert(airLineHornInfoList);
                }
            } else {
                airEditDto.setUpdateUser(userId);
                airLineMapper.editAirLine(airEditDto);
                //先删除
                airLineMapper.delAirPhotoCompare(id);
                airLineMapper.delPointInfo(id);
                airLineSensorInfoMapper.logicDeleteByFlightRouteId(id);
                if (CollectionUtil.isNotEmpty(insertTasks)){
                    airLineMapper.addAirPhotoCompare(insertTasks);
                }
                if (CollectionUtil.isNotEmpty(pointAlarmCodesList)){
                    airLineMapper.addAirPointInfo(pointAlarmCodesList,id);
                }
                if (CollectionUtil.isNotEmpty(airLineHornInfoList)){
                    airLineSensorInfoMapper.batchInsert(airLineHornInfoList);
                }
            }
            //编辑航线需要对航线原来的算法进行关闭下发，并将下发算法表删除标记位记为删除
            if(!addOrEdit){
                //查询该航线下发算法表中的所有正常航线，将其置为关闭状态
                airEditDto.setAlgorithmType(Constants.ONE);
                airEditDto.setStatus(Constants.ZERO);
                List<SyncAlgorithmsReq> airAlgorithms = airLineMapper.getAirAlgorithm(airEditDto);
                log.info("编辑航线时该航线原始算法任务:{}",JSON.toJSONString(airAlgorithms));
                if(!CollectionUtils.isEmpty(airAlgorithms)){
                    log.info("editAir-AirLineServiceImpl-edit-关闭无人机算法任务:{}",JSON.toJSONString(airAlgorithms));
                    airEditDto.setOptType(Constants.THREE);
                    airEditDto.setStatus(Constants.ONE);
                    airEditDto.setAlgorithmType(Constants.ONE);
                    //下发算法仓关闭算法任务
                    syncAlgorithms(airEditDto,userId,platformCode,tenantId,industryCode,airAlgorithms.stream().map(a -> a.getAlgorithmCode()).collect(Collectors.toList()),airAlgorithms,CLOSE);
                    AirLineOperateDto dto = new AirLineOperateDto();
                    dto.setTaskIdList(airAlgorithms.stream().map(a -> a.getTaskId()).collect(Collectors.toList()));
                    dto.setUpdateUser(userId);
                    //假删无人机算法下发表数据
                    airLineMapper.delAirAlgorithm(dto);
                    //假删电子围栏数据
                    Query query = Query.query(Criteria.where("flightRoutId").is(airEditDto.getFlightRouteId())
                            .and(Constants.DEL_FLAG).is(Constants.ZERO));
                    Update update = new Update();
                    update.set(Constants.DEL_FLAG, Constants.ONE);
                    update.set("deleteTime", new Date());
                    mongoTemplate.updateMulti(
                            query,
                            update,
                            com.chinatower.event.constants.Constants.UAV_FLIGHT_FENCE_INFO
                    );
                }

                //补偿  编辑航线，如果存在临时算法，则需要补偿删除。
                AirEditDto airEditDtoSelect =  new AirEditDto();
                airEditDtoSelect.setAlgorithmType(Constants.TWO);
                airEditDtoSelect.setFlightRouteId(airEditDto.getFlightRouteId());
                airEditDtoSelect.setStatus(Constants.ZERO);
                List<SyncAlgorithmsReq> list = airLineMapper.getAirAlgorithm(airEditDtoSelect);
                AirLineOperateDto airLineOperateDto = new AirLineOperateDto();
                airLineOperateDto.setTaskIdList(list.stream().map(a -> a.getTaskId()).collect(Collectors.toList()));
                airLineOperateDto.setUpdateUser(loginUser.getUser().getUserId());
                airLineOperateDto.setAlgorithmType(Constants.TWO);
                airLineMapper.delAirAlgorithm(airLineOperateDto);
            }

            //调用算法仓4.47接口实时流任务下发,下发后将下发报文及结果存入无人机下发日志表
            if (ObjectUtil.isNotEmpty(airEditDto.getWarningTypeId())) {
                log.info("AirLineServiceImpl-edit-下发无人机算法任务:{}",JSON.toJSONString(airEditDto.getWarningTypeId()));
                airEditDto.setOptType(Constants.ONE);
                airEditDto.setStatus(Constants.ZERO);
                airEditDto.setAlgorithmType(Constants.ONE);
                syncAlgorithms(airEditDto,userId,platformCode,tenantId,industryCode,airEditDto.getWarningTypeId(),null,"open");
            }
        }else {
            throw new CustomException("调用飞控新增航线返回数据为空");
        }
        return airEditDto.getFlightRouteId();
    }

    /**
     * 喇叭配置管理
     * @param uosInfo
     * @param userId
     * @param flightRouteId
     * @param deviceCode
     * @return
     */
    private List<VmAirLineSensorInfo> buildHornInfo(String uosInfo, String userId, String flightRouteId, String deviceCode) {
        if (StringUtils.isBlank(uosInfo)) {
            return new ArrayList<>();
        }
        JSONObject uosInfoObj = JSONObject.parseObject(uosInfo);
        String hornInfo= uosInfoObj.getString("pointShoutConfig");
        if (StringUtils.isBlank(hornInfo)) {
            return new ArrayList<>();
        }

        List<VmAirLineSensorInfo> pointHornsList = new ArrayList<>();
        Date currentCreateTime = new Date();

        try {
            // 2. 解析外层JSON数组，捕获整体解析异常
            JSONArray outerArray = JSON.parseArray(hornInfo);
            if (outerArray == null || outerArray.isEmpty()) {
                return pointHornsList;
            }

            // 3. 流式处理嵌套数组，解析并转换为目标对象
            pointHornsList = outerArray.stream()
                    // 过滤：仅保留内层JSONArray类型的元素
                    .filter(JSONArray.class::isInstance)
                    .map(JSONArray.class::cast)
                    // 过滤：跳过空的内层数组
                    .filter(innerArray -> !innerArray.isEmpty())
                    // 解析内层数组为VmAirLineHornInfo列表（单个内层解析失败不影响整体）
                    .flatMap(innerArray -> {
                        try {
                            List<VmAirLineSensorInfo> innerHornList = JSON.parseArray(innerArray.toJSONString(), VmAirLineSensorInfo.class);
                            return innerHornList != null ? innerHornList.stream() : new ArrayList<VmAirLineSensorInfo>().stream();
                        } catch (Exception e) {
                            log.error("解析内层喇叭信息数组失败，innerArray:{}", innerArray.toJSONString(), e);
                            return new ArrayList<VmAirLineSensorInfo>().stream();
                        }
                    })
                    // 过滤：移除null对象，避免后续NPE
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 4. 为每个对象补充公共字段（创建时间、创建人、航线ID、设备编码）
            pointHornsList.forEach(hornInfoObj -> {
                hornInfoObj.setCreateTime(currentCreateTime);
                hornInfoObj.setCreateUser(userId);
                hornInfoObj.setFlightRouteId(flightRouteId);
                hornInfoObj.setDeviceCode(deviceCode);
            });

            log.info("喇叭信息解析完成，共解析出{}条有效记录", pointHornsList.size());
        } catch (Exception e) {
            // 捕获整体解析异常，保证方法不崩溃
            log.error("解析喇叭信息JSON字符串整体失败，hornInfo:{}", hornInfo, e);
            return new ArrayList<>();
        }

        return pointHornsList;
    }

    private List<AirEditDto.PointAlarmCodes> addPointAlarmCodes(AirEditDto airEditDto) {
        try {
            String pointAlarmCodes = airEditDto.getPointAlarmCodes();
            List<AirEditDto.PointAlarmCodes> pointAlarmCodeList = new ArrayList<>();

            // 第一步：解析JSON数据
            Optional.ofNullable(JSON.parseArray(pointAlarmCodes))
                    .ifPresent(outerArray -> {
                        pointAlarmCodeList.addAll(
                                outerArray.stream()
                                        .filter(JSONArray.class::isInstance)
                                        .map(JSONArray.class::cast)
                                        .filter(innerArray -> !innerArray.isEmpty())
                                        .map(innerArray -> JSON.parseArray(innerArray.toJSONString(), AirEditDto.PointAlarmCodes.class))
                                        .filter(Objects::nonNull)
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList())
                        );
                    });

            // 第二步：为每个点设置algorithmCodeStr
            pointAlarmCodeList.stream()
                    .filter(Objects::nonNull)
                    .forEach(pointAlarmCode -> {
                        if (pointAlarmCode.getAlgorithm() != null) {
                            String algorithmCodeStr = pointAlarmCode.getAlgorithm().stream()
                                    .filter(StringUtils::isNotEmpty)
                                    .collect(Collectors.joining(","));
                            pointAlarmCode.setAlgorithmCodeStr(algorithmCodeStr);
                        }
                    });

            // 第三步：合并相同坐标的点
            List<AirEditDto.PointAlarmCodes> mergedList = new ArrayList<>(pointAlarmCodeList.stream()
                    .filter(item -> item.getLongitude() != null && item.getLatitude() != null)
                    .collect(Collectors.groupingBy(item ->
                                    item.getLongitude() + "," + item.getLatitude(),
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> {
                                        AirEditDto.PointAlarmCodes first = list.get(0);
                                        Set<String> mergedAlgorithms = list.stream()
                                                .filter(p -> p.getAlgorithm() != null)
                                                .flatMap(p -> p.getAlgorithm().stream())
                                                .filter(StringUtils::isNotEmpty)
                                                .collect(Collectors.toSet());

                                        AirEditDto.PointAlarmCodes merged = new AirEditDto.PointAlarmCodes();
                                        merged.setLongitude(first.getLongitude());
                                        merged.setLatitude(first.getLatitude());
                                        merged.setAlgorithm(new ArrayList<>(mergedAlgorithms));

                                        // 确保设置algorithmCodeStr
                                        String algorithmCodeStr = String.join(",", mergedAlgorithms);
                                        merged.setAlgorithmCodeStr(algorithmCodeStr);

                                        return merged;
                                    }
                            )
                    ))
                    .values());
            return mergedList;
        } catch (Exception e) {
            log.info("处理航点自动二次识别任务失败: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 下发算法仓
     */
    public void syncAlgorithms(AirEditDto airEditDto,String userId,String platformCode,String tenantId,String industryCode,List<String> warningTypeId,List<SyncAlgorithmsReq> airAlgorithms,String optType){
        log.info("新增编辑航线实时流任务下发" + airEditDto.getFlightRouteId());
        Map<String,UavMonitorGeoJson> map = new HashMap<>();
        Map<String,List<Map<String, Object>>> configMap = new HashMap<>();
        Map<String,Map<String,String>> timeMap = new HashMap<>();
        if (StringUtils.isNotEmpty(airEditDto.getAlgorithmDto())) {
            try {
                String json = airEditDto.getAlgorithmDto();
                if (StringUtils.isBlank(json)) {
                    return;
                }
                List<AlgorithmDto> algorithmDtos = JSONObject.parseArray(json, AlgorithmDto.class);
                if (CollectionUtils.isEmpty(algorithmDtos)) {
                    return;
                }
                for (AlgorithmDto algorithmDto : algorithmDtos) {
                    if (algorithmDto == null || CollectionUtils.isEmpty(algorithmDto.getAlgorithmCode())) {
                        continue;
                    }
                    UavMonitorGeoJson geoJson = algorithmDto.getUavMonitorGeoJson();
                    List<Map<String, Object>> superConfig = algorithmDto.getSuperConfig();
                    String timeStart = algorithmDto.getTimeStart();
                    String timeEnd = algorithmDto.getTimeEnd();
                    for (String algorithmCode : algorithmDto.getAlgorithmCode()) {
                        if (StringUtils.isBlank(algorithmCode)) {
                            continue;
                        }
                        if (ObjectUtil.isNotEmpty(geoJson)) {
                            map.put(algorithmCode, geoJson);
                        }
                        configMap.put(algorithmCode, superConfig);
                        Map<String, String> mapTime = new HashMap<>();
                        if (StringUtils.isNotEmpty(timeStart)) {
                            mapTime.put("waitTimeForStartAnalysis", timeStart);
                        }
                        if (StringUtils.isNotEmpty(timeEnd)) {
                            mapTime.put("waitTimeForEndAnalysis", timeEnd);
                        }
                        if (!mapTime.isEmpty()) {
                            timeMap.put(algorithmCode, mapTime);
                        } else {
                            log.info("algorithmCode: {} 的时间参数均为空，未放入timeMap", algorithmCode);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("电子围栏数据转换异常, flightRouteId: {}, 异常信息: {}", airEditDto.getFlightRouteId(), e.getMessage(), e);
            }
        }

        warningTypeId.stream().forEach(s -> {
            SyncAlgorithmsReq syncAlgorithmsReq = setSyncAlParam(airEditDto, s,airAlgorithms,map,configMap,timeMap);
            log.info("航线实时流任务下发算法仓入参" + JSON.toJSONString(syncAlgorithmsReq));
            if (ObjectUtil.isNotEmpty(syncAlgorithmsReq)){
                syncAlgorithmsReq.setAcceptNode(platformCode);
                NetConnectUtils.RequestParamter<SyncAlgorithmsReq> paramterAl = new NetConnectUtils.RequestParamter<>();
                NetConnectUtils.RequestParamter.Path pathAl = new NetConnectUtils.RequestParamter.Path();
                pathAl.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_ALGORITHMS_TASK);
                paramterAl.setPath(pathAl);
                paramterAl.setBody(syncAlgorithmsReq);
                ResultEntity result = okhttpUtil.doPostAlgGateWay(paramterAl, airEditDto.getAccessNode(), platformCode);
                if ("open".equals(optType) && ObjectUtil.isNotEmpty(result) && "0000".equals(result.getReturnCode())) {
                    //任务存入本地
                    syncAlgorithmsReq.setCreateUser(userId);
                    syncAlgorithmsReq.setTeanantId(tenantId);
                    syncAlgorithmsReq.setIndustryCode(industryCode);
                    syncAlgorithmsReq.setStatus(airEditDto.getStatus());
                    syncAlgorithmsReq.setAlgorithmType(airEditDto.getAlgorithmType());
                    airAlgorithmsService.edit(syncAlgorithmsReq);
                }
                log.info("新增编辑航线实时流任务{}下发结果{}", airEditDto.getFlightRouteId(), JSON.toJSONString(result));
                //调用信息存入mongo
                AirAlTaskLogReq airAlTaskLogReq = new AirAlTaskLogReq();
                airAlTaskLogReq.setTaskId(syncAlgorithmsReq.getTaskId());
                airAlTaskLogReq.setTaskJson(JSONObject.toJSONString(syncAlgorithmsReq));
                airAlTaskLogReq.setReturnMsg(result.getReturnMsg());
                airAlTaskLogReq.setReturnCode(result.getReturnCode());
                airAlTaskLogReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
                airAlTaskLogReq.setErrMsg(result.getErrorMsg());
                airAlTaskLogReq.setAccessNode(airEditDto.getAccessNode());
                airAlTaskLogReq.setAcceptNode(platformCode);
                BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.AIR_TASK_AL_SYNC_LOG);
                bulkOperations.insert(airAlTaskLogReq);
                bulkOperations.execute();
            }else {
                log.info("下发算法任务失败{}",airEditDto.getFlightRouteId());
            }
        });
    }


    /**
     * 实时任务下发请求类转换
     *
     * @param airEditDto
     * @param s
     * @return
     */
    public SyncAlgorithmsReq setSyncAlParam(AirEditDto airEditDto, String s,List<SyncAlgorithmsReq> airAlgorithms,Map map,Map<String,List<Map<String, Object>>> configMap,Map<String,Map<String,String>> timeMap) {
        SyncAlgorithmsReq syncAlgorithmsReq = new SyncAlgorithmsReq();
        syncAlgorithmsReq.setFlightRouteId(airEditDto.getFlightRouteId());
        syncAlgorithmsReq.setDeviceCode(airEditDto.getDeviceCode());
        syncAlgorithmsReq.setDeviceName(airEditDto.getUavName());
        //关闭算法时会有
        if(Constants.THREE.equals(airEditDto.getOptType()) && !CollectionUtils.isEmpty(airAlgorithms)){
            for (SyncAlgorithmsReq req:airAlgorithms) {
                if(s.equals(req.getAlgorithmCode())){
                    syncAlgorithmsReq.setTaskId(req.getTaskId());
                    break;
                }
            }
        }else{
            syncAlgorithmsReq.setTaskId(IdUtil.simpleUUID());
        }
        syncAlgorithmsReq.setAlgorithmCode(s);
        //3航线 待定
        syncAlgorithmsReq.setAnalyType("3");
        syncAlgorithmsReq.setArriveIdx("");
        syncAlgorithmsReq.setAreaId("");
        syncAlgorithmsReq.setChannelCode(airEditDto.getDeviceCode());
        syncAlgorithmsReq.setChannelName(airEditDto.getUavName());
        syncAlgorithmsReq.setOptType(airEditDto.getOptType());
        syncAlgorithmsReq.setTaskVersion(1L);
        syncAlgorithmsReq.setAccessNode(airEditDto.getAccessNode());
        syncAlgorithmsReq.setAlarmInterval(30);
        if (CollectionUtil.isNotEmpty(timeMap)) {
            Map<String,String> timeMaps =  timeMap.get(s);
            log.info("无人机航线{}算法延迟时间{}",airEditDto.getFlightRouteId(),JSON.toJSONString(timeMaps));
            if (CollectionUtil.isNotEmpty(timeMaps)){
                String startTimeStr = timeMaps.get("waitTimeForStartAnalysis");
                String endTimeStr = timeMaps.get("waitTimeForEndAnalysis");
                if (StringUtils.isNotEmpty(startTimeStr)) {
                    syncAlgorithmsReq.setWaitTimeForStartAnalysis(Integer.valueOf(startTimeStr));
                }
                if (StringUtils.isNotEmpty(endTimeStr)) {
                    syncAlgorithmsReq.setWaitTimeForEndAnalysis(Integer.valueOf(endTimeStr));
                }
            }
        }else {
            syncAlgorithmsReq.setWaitTimeForStartAnalysis(null);
            syncAlgorithmsReq.setWaitTimeForEndAnalysis(null);
        }
        if (CollectionUtil.isNotEmpty(configMap)) {
            List<Map<String, Object>> modelConfig = configMap.get(s);
            log.info("无人机航线{}高级参数{}",airEditDto.getFlightRouteId(),JSONObject.toJSONString(modelConfig));
            if (CollectionUtil.isNotEmpty(modelConfig)) {
                syncAlgorithmsReq.setModelConfig(modelConfig);
            }
        }
        if (CollectionUtil.isNotEmpty(map)) {
            log.info("无人机航线{}电子围栏{}",airEditDto.getFlightRouteId(),s);
            UavMonitorGeoJson uavMonitorGeoJson = (UavMonitorGeoJson) map.get(s);
            ExtraInfo extraInfo = new ExtraInfo();
            UavInfo uavInfo = new UavInfo();
            UavInfo.UavCamera uavCamera = new UavInfo.UavCamera();
            try{
                //查询相机默认可见光镜头尺寸
                UavCamera queryUavCamera = airLineMapper.queryUavCamera(airEditDto.getCarmeCode());
                //查询设备机型
                AirUavInfo airUavInfo = airLineMapper.getDeviceInfo(airEditDto.getDeviceCode());
                if (ObjectUtil.isNotEmpty(queryUavCamera)){
                    List<String> dimensionList = Arrays.stream(queryUavCamera.getSensorSize().split("x"))
                            .collect(Collectors.toList());
                    uavCamera.setSensorSize(dimensionList);
                    uavInfo.setUavCamera(uavCamera);
                    uavInfo.setFlightRoutId(airEditDto.getFlightRouteId());
                    uavInfo.setAlgorithmCode(s);
                    uavInfo.setDelFlag(Constants.ZERO);
                    uavInfo.setMergePitch(false);
                    uavInfo.setMergeRoll(false);
                    uavInfo.setMergeHeading(true);
                    if (ObjectUtil.isNotEmpty(airUavInfo)){
                        if ("dji".equals(airUavInfo.getUavNestType())) {
                            // 检查型号是否在大疆枚举范围内
                            if (DJINestTypeCode.fromCode(airUavInfo.getUavNestTypeCode()) != null) {
                                uavInfo.setMergeHeading(false);
                            }
                        }
                    }
                    //无人机电子围栏存入mongo
                    if (ObjectUtil.isNotEmpty(uavMonitorGeoJson.getGeometries())){
                        uavInfo.setGeometries(uavMonitorGeoJson.getGeometries().toJSONString());
                    }
                    if (ObjectUtil.isNotEmpty(uavMonitorGeoJson.getGeometriesBuffer())){
                        uavInfo.setGeometriesBuffer(uavMonitorGeoJson.getGeometriesBuffer().toJSONString());
                    }
                    BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_FLIGHT_FENCE_INFO);
                    bulkOperations.insert(uavInfo);
                    bulkOperations.execute();
                    //处理下发字段问题
                    uavInfo.setGeometries("");
                    uavInfo.setGeometriesBuffer("");
                    uavInfo.setUavMonitorGeoJson(uavMonitorGeoJson);
                    extraInfo.setUavInfo(uavInfo);
                    syncAlgorithmsReq.setExtraInfo(extraInfo);
                }else {
                    return syncAlgorithmsReq;
                }
            }catch (Exception e){
                log.info("无人机航线{}电子围栏{}错误",airEditDto.getFlightRouteId(),s);
            }
        }

        return syncAlgorithmsReq;
    }

    /**
     * 航线详情接口
     *
     *
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result getAirLineInfo(AirEditDto airEditDto) {
        AirLineVo airLineVo = new AirLineVo();
        if (ObjectUtil.isNotEmpty(airEditDto) && StringUtils.isNotEmpty(airEditDto.getFlightRouteId())) {
            airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        }
        if (ObjectUtil.isNotEmpty(airEditDto) && StringUtils.isNotEmpty(airEditDto.getTaskRecordId())){
            airLineVo = airLineMapper.getAirLineInfoByTaskId(airEditDto);
        }
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airEditDto.setAcceptNode(platformCode);
        //添加航线的算法配置ID
        String alIds = airAlgorithmsMapper.queryAlId(airEditDto.getFlightRouteId());
        int count = airLineMapper.getMediumStatusByFlightRouteId(airEditDto.getFlightRouteId());
        airLineVo.setRetakeFlag(Constants.ZERO);
        if(count > 0){
            //该航线得任务有上传媒体文件，可以进行精准复拍
            airLineVo.setRetakeFlag(Constants.ONE);
        }
        airLineVo.setAlgorithmsTaskIds(alIds);
        //查询快照表记录
        if (ObjectUtil.isNotNull(airEditDto.getSnapshotId())){
            AirLineSnapshotRsp airLineSnapshotRsp = airLineSnapshotMapper.getAirLineSnapshot(airEditDto);
            if (ObjectUtil.isNotNull(airLineSnapshotRsp) && ObjectUtil.isNotEmpty(airLineSnapshotRsp.getId())) {
                airLineVo.setKmzJson(airLineSnapshotRsp.getExtraField());
                airLineVo.setKmzAccessUrl(airLineSnapshotRsp.getKmzAccessUrl());
            }
        }
        //喇叭配置相关信息
        setHornInfo(airLineVo);
        //格外查询电子围栏信息
        Query query = new Query();
        query.addCriteria(Criteria.where("flightRoutId").is(airLineVo.getFlightRouteId()));
        query.addCriteria(Criteria.where("delFlag").is(Constants.ZERO));
        List<UavInfo> uavInfoList = mongoTemplate.find(query, UavInfo.class,com.chinatower.event.constants.Constants.UAV_FLIGHT_FENCE_INFO);
        airLineVo.setUavInfo(uavInfoList);
        return Result.data(airLineVo);
    }

    /**
     * 喇叭配置相关信息
     * @param airLineVo
     */
    private void setHornInfo(AirLineVo airLineVo) {
        if(StringUtils.isNotEmpty(airLineVo.getFlightRouteId())) {
            List<VmAirLineSensorInfo> airLineHornInfoList = airLineSensorInfoMapper.selectByFlightRouteId(airLineVo.getFlightRouteId());
            if(!CollectionUtils.isEmpty(airLineHornInfoList)) {
                // 2. 按pointIndex分组（核心步骤：还原嵌套结构）
                // 分组后得到：key=pointIndex，value=该点位下的所有VmAirLineHornInfo对象
                Map<Integer, List<VmAirLineSensorInfo>> groupByPointIndex = airLineHornInfoList.stream()
                        .filter(Objects::nonNull) // 过滤null对象，避免序列化异常
                        .collect(Collectors.groupingBy(
                                VmAirLineSensorInfo::getPointIndex,
                                // 按actIndex升序排序，保证同一点位下actIndex 0在前、1在后
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparing(VmAirLineSensorInfo::getActIndex))
                                                .collect(Collectors.toList())
                                )
                        ));

                // 3. 按pointIndex升序排列分组后的内层数组，构建外层列表
                List<List<VmAirLineSensorInfo>> nestedList = groupByPointIndex.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey()) // 按pointIndex升序
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
                airLineVo.setHornInfo(JSON.toJSONString(nestedList));
            }
        }
    }

    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public List<AirLineVo> getAirLineList(AirEditDto airEditDto) {
        Integer pageNum = airEditDto.getPageNum();
        Integer pageSize = airEditDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String userId = sysUser.getUserId();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        airEditDto.setUserId(userId);
        airEditDto.setIndustryCode(industryCode);
        airEditDto.setTenantId(tenantId);
        List<AirLineVo> airLineVos = airLineMapper.getAirLineList(airEditDto);
        return airLineVos;
    }

    /**
     * 查询租户行业下航线下拉列表sdk
     *
     * @param airEditDto
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    public List<AirLineVo> getAirLinesList(AirEditDto airEditDto) {
        List<AirLineVo> airLineVos = airLineMapper.getAirLinesList(airEditDto);
        return airLineVos;
    }


    /**
     * 添加航线收藏
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result addAirLineCollect(AirEditDto airEditDto) {
        //校验航线是否已经被用户收藏
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String userId = sysUser.getUserId();
        String industryCode = sysUser.getIndustryCode();
        airLineMapper.addAirLineCollect(airEditDto, userId,industryCode);
        return Result.success("");
    }

    /**
     * 查询航线收藏列表
     *
     * @param airEditDto
     * @return
     */
    @Override
    @DataScope(industryCode = "ct.industry_code")
    public List<AirLineVo> queryAirLineCollectList(AirEditDto airEditDto) {
        Integer pageNum = airEditDto.getPageNum();
        Integer pageSize = airEditDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String userId = sysUser.getUserId();
        String tenantId = sysUser.getTenantId();
        String industryCode = sysUser.getIndustryCode();
        airEditDto.setUserId(userId);
        airEditDto.setIndustryCode(industryCode);
        airEditDto.setTenantId(tenantId);
        List<AirLineVo> airLineVos = airLineMapper.queryAirLineCollectList(airEditDto, userId);
        return airLineVos;
    }

    /**
     * 取消航线收藏
     *
     * @param airEditDto
     * @return
     */
    @Override
    @Transactional
    public Result deleteAirLineCollect(AirEditDto airEditDto) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        airLineMapper.deleteAirLineCollect(airEditDto, userId);
        return Result.success("");
    }

    /**
     * 修改航线名称
     *
     * @param airEditDto
     * @return
     */
    @Override
    @Transactional
    public Result editAirLineName(AirEditDto airEditDto) {
        if (StringUtils.isEmpty(airEditDto.getFlightRouteName())) {
            throw new CustomException("航线名称不能为空");
        }
        AirLineVo airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        airLineVo.setFlightRouteName(airEditDto.getFlightRouteName());
        airEditDto.setType(Integer.valueOf(airLineVo.getType()));
        airEditDto.setFileName(airLineVo.getKmzFileName());
        airEditDto.setStoragePath(airLineVo.getKmzUrl());
        airEditDto.setDeviceCode(airLineVo.getDeviceCode());
        airEditDto.setModifiedTime(String.valueOf(new Date()));
        String oldKmzJson = airLineVo.getKmzJson();
        airEditDto.setKmzJson(airLineVo.getKmzJson());
        airEditDto.setAccessNode(airLineVo.getAccessNode());
        String accessUrl = fileUrlEncryptUtil.dealObsFileUrl(airLineVo.getKmzAccessUrl());
        airEditDto.setFileAccessPath(accessUrl);
        if (Constants.THREE.equals(airLineVo.getType().toString()) || Constants.FOUR.equals(airLineVo.getType().toString())){
            JSONObject oldKmzJsonObject = JSON.parseObject(airLineVo.getKmzJson());
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            airEditDto.setUosInfo(uosInfoObject.toJSONString());
            JSONObject uosInfo = new JSONObject();
            uosInfo.put(UOS_INFO,uosInfoObject);
            airEditDto.setKmzJson(uosInfo.toJSONString());
        }
        //调用飞控接口 编辑航线
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airEditDto.setAcceptNode(platformCode);
        log.info("编辑航线入参{}", JSONObject.toJSON(airEditDto));
        NetConnectUtils.RequestParamter<AirEditDto> paramter = new NetConnectUtils.RequestParamter<>();
        paramter.setBody(airEditDto);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_AIR_EDIT);
        paramter.setPath(path);
        okhttpUtil.doPostUavGateWay(paramter, airEditDto.getAccessNode(), platformCode);
        //本地编辑
        airEditDto.setKmzJson(oldKmzJson);
        airLineMapper.editAirLineName(airEditDto);
        return Result.success("");
    }

    /**
     * 删除航线
     *
     * @param airEditDto
     * @return
     */
    @Override
    @Transactional
    public Result delAirLine(AirEditDto airEditDto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        AirLineVo airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        String platformCode = user.getPlatformCode();
        NetConnectUtils.RequestParamter<AirEditDto> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airEditDto);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_AIR_DELETE);
        path.setFlightRouteId(airEditDto.getFlightRouteId());
        paramter.setPath(path);
        okhttpUtil.doPostUavGateWay(paramter, airLineVo.getAccessNode(), platformCode);
        //本地删除 航线需要假删除
        airLineMapper.delAirLine(airEditDto);
        //本地删除航线下的计划
        airLineMapper.delAirPlanByFlightRoutId(airEditDto);
        //本地假删除航线下的任务
        airLineMapper.delAirTaskByFlightRoutId(airEditDto);
        //关闭航线下所有下发任务并删除算发表这些数据
        AirEditDto dto = new AirEditDto();
        dto.setFlightRouteId(airEditDto.getFlightRouteId());
        dto.setStatus(Constants.ZERO);
        List<SyncAlgorithmsReq> airAlgorithms= airLineMapper.getAirAlgorithm(dto);
        log.info("编辑航线时该航线原始算法任务:",JSON.toJSONString(airAlgorithms));
        if(!CollectionUtils.isEmpty(airAlgorithms)){
            log.info("delAirLine-AirLineServiceImpl-edit-关闭无人机算法任务:{}",JSON.toJSONString(airAlgorithms));
            airEditDto.setOptType(Constants.ZERO);
            //下发算法仓关闭算法任务
            airEditDto.setAcceptNode(airLineVo.getAccessNode());
            airEditDto.setDeviceCode(airLineVo.getDeviceCode());
            airEditDto.setOptType(Constants.THREE);
            airEditDto.setStatus(Constants.ONE);
            airEditDto.setAlgorithmType(Constants.ONE);
            syncAlgorithms(airEditDto,user.getUserId(),platformCode,user.getTenantId(),user.getIndustryCode(),airAlgorithms.stream().map(a -> a.getAlgorithmCode()).collect(Collectors.toList()),airAlgorithms,CLOSE);
            AirLineOperateDto airLineOperateDto = new AirLineOperateDto();
            airLineOperateDto.setFlightRouteId(airEditDto.getFlightRouteId());
            airLineOperateDto.setUpdateUser(user.getUserId());
            //假删无人机算法下发表所有数据
            airLineMapper.delAirAlgorithm(airLineOperateDto);
        }
        return Result.success("");
    }

    /**
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result addTempAlgorithm(AirEditDto airEditDto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        AirLineVo airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        airEditDto.setAccessNode(airLineVo.getAccessNode());
        airEditDto.setDeviceCodes(airLineVo.getDeviceCode());
        airEditDto.setAcceptNode(user.getPlatformCode());
        if(!CollectionUtils.isEmpty(airEditDto.getWarningTypeId())){
            log.info("AirLineServiceImpl-addTempAlgorithm-下发临时无人机算法任务:{}",JSON.toJSONString(airEditDto.getWarningTypeId()));
            //将此航线下下发临时任务前的航线任务停止，并修改数据库状态为待恢复
            AirEditDto airEditDtoSelect =  new AirEditDto();
            airEditDtoSelect.setFlightRouteId(airEditDto.getFlightRouteId());
            airEditDtoSelect.setStatus(Constants.ZERO);
            List<SyncAlgorithmsReq> list = airLineMapper.getAirAlgorithm(airEditDtoSelect);
            airEditDto.setOptType(Constants.THREE);
            airEditDto.setStatus(Constants.ONE);
            //下发算法仓关闭任务
            syncAlgorithms(airEditDto,user.getUserId(),user.getPlatformCode(),user.getTenantId(),user.getIndustryCode(),list.stream().map(a -> a.getAlgorithmCode()).collect(Collectors.toList()),list,CLOSE);
            //下发算法仓开启算法任务
            airEditDto.setDeviceCode(airLineVo.getDeviceCode());
            airEditDto.setOptType(Constants.ONE);
            airEditDto.setStatus(Constants.ZERO);
            airEditDto.setAlgorithmType(Constants.TWO);
            syncAlgorithms(airEditDto,user.getUserId(),user.getPlatformCode(),user.getTenantId(),user.getIndustryCode(),airEditDto.getWarningTypeId(),null,"open");
            //将该航线原有任务关闭且置为待恢复状态,如当前也有临时任务，将临时任务delete_flag置为删除
            AirLineOperateDto airLineOperateDto = new AirLineOperateDto();
            airLineOperateDto.setTaskIdList(list.stream().map(a -> a.getTaskId()).collect(Collectors.toList()));
            airLineOperateDto.setUpdateUser(user.getUserId());
            //将正常航线置为待回复且暂时关闭
            airLineMapper.updateAirAlgorithm(airLineOperateDto);
            airLineOperateDto.setAlgorithmType(Constants.TWO);
            //删除原有临时航线的数据
            airLineMapper.delAirAlgorithmByType(airLineOperateDto);
        }
        return Result.success("");
    }

    /**
     * 校验航线名称唯一性
     *
     * @param airEditDto
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", acceptColumn = "t.accept_node")
    public Result checkAirLineName(AirEditDto airEditDto) {
        int count = airLineMapper.checkAirLineName(airEditDto);
        if (count > 0) {
            throw new CustomException("当前航线名称已存在");
        }
        return Result.success("");
    }


    @Override
    public AirDeviceInfoRep getAirDeviceInfo(AirDeviceInfoReq airDeviceInfoDto) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        airDeviceInfoDto.setAcceptNode(platformCode);
        AirDeviceInfoRep deviceInfoReps = new AirDeviceInfoRep();
        if (ObjectUtil.isNotNull(airDeviceInfoDto)) {
            String joIn = String.valueOf(JSONObject.toJSON(airDeviceInfoDto));
            NetConnectUtils.RequestParamter<AirDeviceInfoReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(airDeviceInfoDto);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_AIR_DEVICE_INFO);
            path.setDeviceCode(airDeviceInfoDto.getDeviceCode());
            paramter.setPath(path);
            try {
                log.info("获取设备详细信息(航线规划使用)入参：" + joIn);
                ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, null, platformCode);
                if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                    deviceInfoReps = JSON.parseObject(JSON.toJSONString(cr.getData()), AirDeviceInfoRep.class);
                }
            } catch (Exception e) {
                log.error("调用获取设备详细信息(航线规划使用)失败:{}", e);
            }
        }
        deviceInfoReps.setDeviceCode(airDeviceInfoDto.getDeviceCode());
        deviceInfoReps.setDeviceName(airLineMapper.queryUavInfo(airDeviceInfoDto.getDeviceCode()));
        return deviceInfoReps;
    }

    /**
     * 查询默认航线名称
     *
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", acceptColumn = "t.accept_node")
    public Result queryDefultAirLineName(AirEditDto airEditDto) {
        String airName = airLineMapper.queryDefultAirLineName(airEditDto);
        airName = AirLineTypeEnum.generateName(airEditDto.getType(), airName);
        return Result.data(airName);
    }

    /**
     * 校验航线是否被关联
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result checkAirLineBind(AirEditDto airEditDto) {
        List<String> planName = new ArrayList<>();
        if (ObjectUtil.isNotNull(airEditDto.getIsEdit() ) ) {
            if (Constants.ONE.equals(airEditDto.getIsEdit())){
                planName = airLineMapper.checkAirLineBindIsEdit(airEditDto);
            }
        }else {
            planName = airLineMapper.checkAirLineBind(airEditDto);
        }
        return Result.data(planName);
    }

    /**
     * 复制航线
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result copyAirLine(AirEditDto airEditDto) {
        log.info("AirLineServiceImpl-copyAirLine-req:{}",JSON.toJSONString(airEditDto));
        if (StringUtils.isEmpty(airEditDto.getFlightRouteName())) {
            throw new CustomException("航线名称不能为空");
        }
        AirLineVo airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        if (Constants.THREE.equals(airLineVo.getType().toString()) || Constants.FOUR.equals(airLineVo.getType().toString())){
            JSONObject oldKmzJsonObject = JSON.parseObject(airLineVo.getKmzJson());
            JSONObject uosInfoObject = (JSONObject) oldKmzJsonObject.get(UOS_INFO);
            airEditDto.setUosInfo(uosInfoObject.toJSONString());
        }
        airEditDto.setFlightRouteId("");
        airEditDto.setFlightRouteName(airEditDto.getFlightRouteName());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        String industryCode = loginUser.getUser().getIndustryCode();
        String tenantId = loginUser.getUser().getTenantId();
        airEditDto.setIndustryCode(industryCode);
        airEditDto.setAcceptNode(platformCode);
        airEditDto.setAccessNode(airLineVo.getAccessNode());
        airEditDto.setTenantId(tenantId);
        airEditDto.setFileName(airLineVo.getKmzFileName());
        airEditDto.setStoragePath(airLineVo.getKmzUrl());
        airEditDto.setFrogLeap(Integer.valueOf(airLineVo.getFrogLeap()));
        airEditDto.setStartNestCode(airLineVo.getStartNestCode());
        airEditDto.setEndNestCode(airLineVo.getEndNestCode());
        airEditDto.setUavModelCode(airLineVo.getUavModelCode());
        //复制航线  重新加密kmz文件
        String accessUrl = fileUrlEncryptUtil.dealObsFileUrl(airLineVo.getKmzAccessUrl());
        airEditDto.setFileAccessPath(accessUrl);
        airEditDto.setType(Integer.valueOf(airLineVo.getType()));
        airEditDto.setDeviceCode(airLineVo.getDeviceCode());
        airEditDto.setKmzJson(airLineVo.getKmzJson());
        if (StringUtils.isNotEmpty(airLineVo.getWarningTypeId())) {
            String warningTypeId = airLineVo.getWarningTypeId();
            String[] warningTypeIdArray = warningTypeId.split(",");
            List<String> warningTypeIdList = Arrays.stream(warningTypeIdArray).filter(s -> s != null && !s.isEmpty() && !s.equalsIgnoreCase("null")).collect(Collectors.toList());
            airEditDto.setWarningTypeId(warningTypeIdList);
        }
        airEditDto.setAcceptNode(platformCode);
        //调用飞控接口 新增航线
        this.editAir(airEditDto, loginUser);
        return Result.success("");
    }

    /**
     * 获取航点航线详细信息(航线规划使用)
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result getPointInfo(AirEditDto airEditDto) {
        AirLineVo airLineVo = airLineMapper.getAirLineInfo(airEditDto);
        String accessNode = airLineVo.getAccessNode();
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        NetConnectUtils.RequestParamter<AirEditDto> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airEditDto);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_UAV_POINT_INFO);
        paramter.setPath(path);
        ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
            return Result.data(jsonObject);
        }else {
            throw new CustomException("调用飞控获取航点信息失败");
        }
    }

    /**
     * 上传kmz文件
     * @param kmzFile
     * @return
     */
    @Override
    public Result uploadKmzFile(MultipartFile kmzFile) {
        if (kmzFile == null) {
            throw new CustomException("文件不能为空");
        }
        AjaxResult ajaxResult = fileFeignClient.uploadFile(kmzFile);
        if (ajaxResult != null && ajaxResult.ok()) {
            com.chinatower.file.vo.SysFileInfo  sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
            log.info("航线文件信息：{}", JSONObject.toJSON(sysFileInfo));
            String url = sysFileInfo.getProxyAddr();
            String fileName = sysFileInfo.getFileName();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("filename", fileName);
            jsonObject.put("storagePath", url);
            jsonObject.put("bucketName", null);
            return Result.data(jsonObject);
        }else {
            throw new CustomException("调用文件服务上传文件失败");
        }
    }

    /**
     * 飞行
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result pointToFly(AirPointToFlyReq airPointToFlyReq) {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(com.chinatower.airline.constants.Constants.UAV_FLY_CONFIG_KEY);
        String kaiguan = Constants.ZERO;
        if ((Constants.SUCCESS.toString()).equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            kaiguan = sysConfig.getConfigValue();
        }
        if (Constants.ONE.equals(kaiguan)){
            return Result.fail("指点飞行功能已关闭");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airPointToFlyReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airPointToFlyReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.POINTT_TO_FLY_ON);
        path.setDeviceCode(airPointToFlyReq.getDeviceCode());
        paramter.setPath(path);
        ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, airPointToFlyReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            //本地新建 指点飞行大类的航线
            AirEditDto airEditDto = new AirEditDto();
            JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
            if (ObjectUtil.isEmpty(jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                throw new CustomException("飞控平台错误:"+cr.getErrorMsg());
            }
            String flightRouteId = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
            airEditDto.setFlightRouteId(flightRouteId);
            //是否切换客户视角
            String industryCode = loginUser.getUser().getIndustryCode();
            String tenantId = loginUser.getUser().getTenantId();
            String userId = loginUser.getUser().getUserId();
            String appCode = loginUser.getUser().getAppVerCode();
            airEditDto.setIndustryCode(industryCode);
            airEditDto.setDeviceCode(airPointToFlyReq.getDeviceCode());
            airEditDto.setAcceptNode(platformCode);
            airEditDto.setAccessNode(airPointToFlyReq.getAccessNode());
            airEditDto.setTenantId(tenantId);
            airEditDto.setAppVerCode(appCode);
            airEditDto.setCreateUser(userId);
            airEditDto.setFlightRouteName("指点飞行"+ new Date().getTime());
            airEditDto.setFlightMainType(Constants.ONE);
            airLineMapper.addAirLine(airEditDto);
            String taskRecordId = jsonObject.get(TASK_RECORD_ID).toString();
            //本地新建一条立即飞行的计划 任务本地入库
            AirTaskReq airTaskReq = new AirTaskReq();
            List<AirTaskReq> airTaskReqs = new ArrayList<>();
            airTaskReq.setPlanId("");
            airTaskReq.setTaskRecordId(taskRecordId);
            airTaskReq.setTenantId(tenantId);
            airTaskReq.setScheduleExecTime(DateUtil.format(new Date(),com.chinatower.airline.constants.Constants.DATE_YMDHMS));
            airTaskReq.setIndustryCode(industryCode);
            airTaskReq.setCreateUser(loginUser.getUsername());
            airTaskReq.setAcceptNode(platformCode);
            airTaskReq.setAccessNode(airPointToFlyReq.getAccessNode());
            airTaskReq.setStatus(Constants.ZERO);
            airTaskReq.setMediumStatus(Constants.ZERO);
            Date currentTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM);
            String formattedTime = dateFormat.format(currentTime);
            airTaskReq.setAcceptNode(platformCode);
            airTaskReq.setPlanName("指点飞行"+formattedTime);
            airTaskReq.setFlightRouteId(flightRouteId);
            airTaskReq.setIsItEnabled(Constants.ZERO);
            airTaskReq.setScheduleExecTime(airTaskReq.getScheduleExecTime());
            airTaskReqs.add(airTaskReq);
            airTaskMapper.insertBath(airTaskReqs);
            return Result.data(jsonObject);
        }else {
            throw new CustomException(ERR_MSG);
        }
    }

    /**
     *
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result flyReturn(AirPointToFlyReq airPointToFlyReq) {
        //默认一键返航的参数
        airPointToFlyReq.setReturnMode(Constants.ZERO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airPointToFlyReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airPointToFlyReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_RETURN);
        paramter.setPath(path);
        okhttpUtil.doPostUavGateWay(paramter, airPointToFlyReq.getAccessNode(), platformCode);
        return Result.success("");
    }

    /**
     * 查询航线算法
     * @param airEditDto
     * @return
     */
    @Override
    public Result queryAirLineAlgorithm(AirEditDto airEditDto) {
        List<AirLineAlgorithms> airLineAlgorithms = airLineMapper.queryAirLineAlgorithm(airEditDto);
        return Result.data(airLineAlgorithms);
    }

    /**
     *
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result immediatelyFly(AirPointToFlyReq airPointToFlyReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airPointToFlyReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airPointToFlyReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_IMMEDIATELY_FLY);
        paramter.setPath(path);
        ResultEntity cr = okhttpUtil.doPostUavGateWay(paramter, airPointToFlyReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            //本地新建 指点飞行大类的航线
            AirEditDto airEditDto = new AirEditDto();
            JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
            if (ObjectUtil.isEmpty(jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                throw new CustomException("飞控平台错误:"+cr.getErrorMsg());
            }
            String flightRouteId = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
            airEditDto.setFlightRouteId(flightRouteId);
            //是否切换客户视角
            String industryCode = loginUser.getUser().getIndustryCode();
            String tenantId = loginUser.getUser().getTenantId();
            String userId = loginUser.getUser().getUserId();
            String appCode = loginUser.getUser().getAppVerCode();
            airEditDto.setIndustryCode(industryCode);
            airEditDto.setDeviceCode(airPointToFlyReq.getDeviceCode());
            airEditDto.setAcceptNode(platformCode);
            airEditDto.setAccessNode(airPointToFlyReq.getAccessNode());
            airEditDto.setFlightRouteName("一键起飞"+new Date().getTime());
            airEditDto.setTenantId(tenantId);
            airEditDto.setAppVerCode(appCode);
            airEditDto.setCreateUser(userId);
            //查询deviceCode所属的无人机编码
            String uavCode = airLineMapper.getUavCodeByDeviceCode(airEditDto.getDeviceCode());
            airEditDto.setUavCode(uavCode);
            airEditDto.setFlightMainType(Constants.TWO);
            //根据设备编码查询机巢经纬度坐标
            airEditDto.setAirMileage(String.valueOf(airPointToFlyReq.getHeight()));
            airLineMapper.addAirLine(airEditDto);
            String taskRecordId = jsonObject.get(TASK_RECORD_ID).toString();
            //本地新建一条立即飞行的计划 任务本地入库
            AirTaskReq airTaskReq = new AirTaskReq();
            List<AirTaskReq> airTaskReqs = new ArrayList<>();
            airTaskReq.setPlanId("");
            airTaskReq.setTaskRecordId(taskRecordId);
            airTaskReq.setTenantId(tenantId);
            airTaskReq.setIndustryCode(industryCode);
            airTaskReq.setCreateUser(loginUser.getUsername());
            airTaskReq.setAcceptNode(platformCode);
            airTaskReq.setAccessNode(airPointToFlyReq.getAccessNode());
            airTaskReq.setStatus(Constants.ZERO);
            airTaskReq.setMediumStatus(Constants.ZERO);
            airTaskReq.setFlightRouteId(flightRouteId);
            Date currentTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM);
            String formattedTime = dateFormat.format(currentTime);
            airTaskReq.setAcceptNode(platformCode);
            airTaskReq.setPlanName("一键起飞"+formattedTime);
            airTaskReq.setIsItEnabled(Constants.ZERO);
            airTaskReq.setScheduleExecTime(DateUtil.format(new Date(), com.chinatower.airline.constants.Constants.DATE_YMDHMS));
            airTaskReqs.add(airTaskReq);
            airTaskMapper.insertBath(airTaskReqs);
            return Result.data(jsonObject);
        }else {
            throw new CustomException(ERR_MSG);
        }
    }

    /**
     * 退订设备后 关闭设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @returnloginUser
     */
    @Override
    public Result closeAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq) {
        //根据设备编码 行业 客户查询航线
        List<AirLineVo> airLineVoList = airLineMapper.queryFlightRoutes(airPointToFlyReq);
        if (CollectionUtil.isEmpty(airLineVoList)){
            throw new CustomException("该租户该行业下没有航线任务");
        }
        log.info("退订设备所有航线:",JSON.toJSONString(airLineVoList));
        //根据航线id 和设备编码 查询 需要关闭的算法列表
        List<SyncAlgorithmsReq> airLineAlgorithms = airAlgorithmsMapper.queryAlList(airLineVoList,"",Constants.ZERO);
        log.info("退订设备航线原始算法任务:",JSON.toJSONString(airLineAlgorithms));
        if(!CollectionUtils.isEmpty(airLineAlgorithms)){
            log.info("closeAirTaskAndAlgorithms-AirLineServiceImpl-edit-关闭无人机算法任务:{}",JSON.toJSONString(airLineAlgorithms));
            airLineAlgorithms.stream().forEach(s -> {
                //下发算法仓关闭算法
                s.setOptType(Constants.THREE);
                s.setStatus(Constants.ONE);
                s.setChannelCode(s.getDeviceCode());
                s.setAreaId("");
                s.setArriveIdx("");
                syncAlgorithm(s,Constants.ONE);
            });
            //关闭算法任务
            AirLineOperateDto dto = new AirLineOperateDto();
            dto.setTaskIdList(airLineAlgorithms.stream().map(a -> a.getTaskId()).collect(Collectors.toList()));
            dto.setUpdateUser("system");
            //假删无人机算法下发表数据
            airLineMapper.delAirAlgorithm(dto);
        }
        //根据航线id 和设备编码 查询 需要关闭的计划任务列表
        if (!CollectionUtil.isEmpty(airLineVoList)){
            List<AirTaskReq> airTasks = airTaskMapper.queryTaskByFlightRouteId(airLineVoList);
            AirTaskEnableReq airTaskEnableReq = new AirTaskEnableReq();
            // 0关闭计划
            airTaskEnableReq.setEnabledType(Constants.ZERO);
            // 1关闭 0开启
            airTaskEnableReq.setIsItEnabled(Constants.ONE);
            List<AirTaskEnableReq.enableTask> enableTaskList = new ArrayList<>();
            airTasks.stream().forEach(s -> {
                AirTaskEnableReq.enableTask enableTask = new AirTaskEnableReq.enableTask();
                enableTask.setId(s.getId());
                enableTask.setTaskRecordId(s.getTaskRecordId());
                enableTask.setStatus(s.getStatus());
                enableTaskList.add(enableTask);
            });
            if (CollectionUtil.isEmpty(enableTaskList)){
                throw new CustomException("该租户该行业下没有需要关闭的飞行任务");
            }
            //关闭任务
            airTaskEnableReq.setEnableTaskList(enableTaskList);
            log.info("退订设备航线原始飞行任务:",JSON.toJSONString(airTaskEnableReq));
            airTaskEnableReq.setIsSystem(Constants.ONE);
            airPlanService.updateIsItEnabled(airTaskEnableReq);
        }
        return Result.success("关闭成功");
    }

    /**
     * 下发算法仓关闭任务
     */
    public void syncAlgorithm(SyncAlgorithmsReq syncAlgorithmsReq,String type){
        log.info("航线实时流关闭任务下发" + JSON.toJSONString(syncAlgorithmsReq));
        NetConnectUtils.RequestParamter<SyncAlgorithmsReq> paramterAl = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path pathAl = new NetConnectUtils.RequestParamter.Path();
        pathAl.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_ALGORITHMS_TASK);
        paramterAl.setPath(pathAl);
        paramterAl.setBody(syncAlgorithmsReq);
        ResultEntity result = okhttpUtil.doPostAlgGateWay(paramterAl, syncAlgorithmsReq.getAccessNode(), syncAlgorithmsReq.getAcceptNode());
        if (Constants.TWO.equals(type) && ObjectUtil.isNotEmpty(result) && "0000".equals(result.getReturnCode())) {
            //任务存入本地
            syncAlgorithmsReq.setCreateUser(syncAlgorithmsReq.getCreateUser());
            syncAlgorithmsReq.setTeanantId(syncAlgorithmsReq.getTeanantId());
            syncAlgorithmsReq.setIndustryCode(syncAlgorithmsReq.getIndustryCode());
            syncAlgorithmsReq.setStatus(syncAlgorithmsReq.getStatus());
            syncAlgorithmsReq.setAlgorithmType(syncAlgorithmsReq.getAlgorithmType());
            airAlgorithmsService.edit(syncAlgorithmsReq);
        }
        log.info("航线实时流任务{}下发结果{}", syncAlgorithmsReq.getFlightRouteId(), JSONObject.toJSON(result));
        //调用信息存入mongo
        AirAlTaskLogReq airAlTaskLogReq = new AirAlTaskLogReq();
        airAlTaskLogReq.setTaskId(syncAlgorithmsReq.getTaskId());
        airAlTaskLogReq.setTaskJson(JSONObject.toJSONString(syncAlgorithmsReq));
        airAlTaskLogReq.setReturnMsg(result.getReturnMsg());
        airAlTaskLogReq.setReturnCode(result.getReturnCode());
        airAlTaskLogReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        airAlTaskLogReq.setErrMsg(result.getErrorMsg());
        airAlTaskLogReq.setAccessNode(syncAlgorithmsReq.getAccessNode());
        airAlTaskLogReq.setAcceptNode(syncAlgorithmsReq.getAcceptNode());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.AIR_TASK_AL_SYNC_LOG);
        bulkOperations.insert(airAlTaskLogReq);
        bulkOperations.execute();
    }


    /**
     * 续订设备后 重新开启设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result openAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq) {
        //根据设备编码 行业 客户查询航线
        List<AirLineVo> airLineVoList = airLineMapper.queryFlightRoutes(airPointToFlyReq);
        if (CollectionUtil.isEmpty(airLineVoList)){
            throw new CustomException("该租户该行业下没有航线任务");
        }
        log.info("续订设备所有航线:",JSON.toJSONString(airLineVoList));
        //根据航线id 和设备编码 查询 需要开启的算法列表
        List<SyncAlgorithmsReq> airLineAlgorithms = airAlgorithmsMapper.queryAlList(airLineVoList,"open",Constants.ONE);
        log.info("续订设备航线原始算法任务:",JSON.toJSONString(airLineAlgorithms));
        if(!CollectionUtils.isEmpty(airLineAlgorithms)){
            log.info("openAirTaskAndAlgorithms-AirLineServiceImpl-edit-关闭无人机算法任务:{}",JSON.toJSONString(airLineAlgorithms));
            airLineAlgorithms.stream().forEach(s -> {
                //开启算法
                s.setOptType(Constants.ONE);
                s.setStatus(Constants.ZERO);
                s.setChannelCode(s.getDeviceCode());
                s.setAreaId("");
                s.setArriveIdx("");
                s.setTaskId(UUID.randomUUID().toString());
                syncAlgorithm(s,Constants.TWO);
            });
        }
        //根据航线id 和设备编码 查询 需要开启的计划任务列表
        if (!CollectionUtil.isEmpty(airLineVoList)){
            List<AirTaskReq> airTasks = airTaskMapper.queryTaskByFlightRouteId(airLineVoList);
            AirTaskEnableReq airTaskEnableReq = new AirTaskEnableReq();
            // 0关闭计划
            airTaskEnableReq.setEnabledType(Constants.ZERO);
            // 1关闭 0开启
            airTaskEnableReq.setIsItEnabled(Constants.ZERO);
            List<AirTaskEnableReq.enableTask> enableTaskList = new ArrayList<>();
            airTasks.stream().forEach(s -> {
                AirTaskEnableReq.enableTask enableTask = new AirTaskEnableReq.enableTask();
                enableTask.setId(s.getId());
                enableTask.setTaskRecordId(s.getTaskRecordId());
                enableTask.setStatus(s.getStatus());
                enableTaskList.add(enableTask);
            });
            if (CollectionUtil.isEmpty(enableTaskList)){
                throw new CustomException("该租户该行业下没有需要开启的飞行任务");
            }
            //开启任务
            airTaskEnableReq.setEnableTaskList(enableTaskList);
            log.info("续订设备航线原始飞行任务:",JSON.toJSONString(airTaskEnableReq));
            airTaskEnableReq.setIsSystem(Constants.ONE);
            airPlanService.updateIsItEnabled(airTaskEnableReq);
        }
        return Result.success("开启成功");
    }

    /**
     * 批量点飞
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result pointToFlyBatch(List<AirPointToFlyReq> airPointToFlyReq) {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(com.chinatower.airline.constants.Constants.UAV_FLY_CONFIG_KEY);
        String kaiguan = Constants.ZERO;
        if ((Constants.SUCCESS.toString()).equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            kaiguan = sysConfig.getConfigValue();
        }
        if (Constants.ONE.equals(kaiguan)){
            return Result.fail(com.chinatower.airline.constants.Constants.UAV_TASK_ERROR_JSON2);
        }
        Map map = new HashMap<>();
        List<AirPointToFlyReq> airPointToFlyReqs = new ArrayList<>();
        List<AirPointToFlyReq> errorAirPointToFlyReqs = new ArrayList<>();
        List<AirPointToFlyReq> executeAirPointToFlyReqs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(airPointToFlyReq)){
            airPointToFlyReq.stream().forEach(s -> {
                ResultEntity cr = new ResultEntity();
                try{
                    cr = singlePointToFly(s);
                }catch (Exception e){
                }
                log.info("批量指点飞行结果:{}",cr);
                if (ObjectUtil.isNotNull(cr)) {
                    AirPointToFlyReq airPointToFlyReq1 = new AirPointToFlyReq();
                    airPointToFlyReq1.setDeviceCode(s.getDeviceCode());
                    if ("成功".equals(cr.getReturnMsg()) && "0000".equals(cr.getReturnCode()) && ObjectUtil.isNotNull(cr.getData())){
                        JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
                        if (!ObjectUtil.isEmpty(jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                            String flightRouteId = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
                            airPointToFlyReq1.setFlightRouteId(flightRouteId);
                            airPointToFlyReqs.add(airPointToFlyReq1);
                        }else {
                            executeAirPointToFlyReqs.add(airPointToFlyReq1);
                        }
                    }
                    if ("成功".equals(cr.getReturnMsg()) && "0000".equals(cr.getReturnCode()) && ObjectUtil.isNull(cr.getData())){
                        executeAirPointToFlyReqs.add(airPointToFlyReq1);
                    }
                    if ("8888".equals(cr.getReturnCode())){
                        errorAirPointToFlyReqs.add(airPointToFlyReq1);
                    }
                }
            });
        }
        map.put("success",airPointToFlyReqs);
        map.put("error",errorAirPointToFlyReqs);
        map.put("execute",executeAirPointToFlyReqs);
        return Result.data(map);
    }

    /**
     * 查询设备下的航线
     * @param airEditDto
     * @return
     */
    @Override
    public Result getAirLineByDeviceCodes(AirEditDto airEditDto) {
        //查询租户权限下的设备(机巢)
        List<AirUavInfo> uavInfoList = airLineMapper.getDeviceCodeByTenantId(airEditDto);
        List<AirUavInfo> distinctUavInfoList = uavInfoList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                AirUavInfo::getUavCode,
                                uav -> uav,
                                (existing, replacement) -> existing
                        ),
                        map -> new ArrayList<>(map.values())
                ));
        List<String> uavNestCode = distinctUavInfoList.stream().map(AirUavInfo::getUavNestCode).collect(Collectors.toList());
        airEditDto.setUavCodes(uavNestCode);
        if (CollectionUtil.isEmpty(uavNestCode)){
            return Result.data(new ArrayList<>());
        }
        List<AirLineVo> airLineRes = airLineMapper.getAirLineByDeviceCodes(airEditDto);
        distinctUavInfoList.stream().forEach(airUavInfo -> {
            airUavInfo.setAirLineVoList(airLineRes.stream().filter(airLineVo -> airUavInfo.getUavNestCode().equals(airLineVo.getDeviceCode())).collect(Collectors.toList()));
        });
        return Result.data(distinctUavInfoList);
    }

    /**
     * 转换
     * @param airLineVos
     * @param deviceCode
     * @return
     */
    private static DeviceAirLineRes getDeviceAirLineRes(List<AirLineVo> airLineVos, String deviceCode) {
        AirLineVo firstAirLine = airLineVos.get(0);
        // 创建 DeviceAirLineRes 对象
        DeviceAirLineRes deviceAirLineRes = new DeviceAirLineRes();
        deviceAirLineRes.setDeviceCode(deviceCode);
        deviceAirLineRes.setUavName(firstAirLine.getUavName()); // 假设 AirLineVo 中有 uavName
        deviceAirLineRes.setUavCode(firstAirLine.getUavCode()); // 假设 AirLineVo 中有 uavCode
        deviceAirLineRes.setUavNestCode(firstAirLine.getUavNestCode()); // 假设 AirLineVo 中有 uavNestCode
        deviceAirLineRes.setUavNestName(firstAirLine.getUavNestName()); // 假设 AirLineVo 中有 uavNestName
        deviceAirLineRes.setUavNestAltitude(firstAirLine.getUavNestAltitude()); // 假设 AirLineVo 中有 uavNestAltitude
        deviceAirLineRes.setAirLineVos(airLineVos);
        return deviceAirLineRes;
    }

    /**
     *
     * @param airEditDto
     * @return
     */
    @Override
    public Result reFly(AirEditDto airEditDto, MultipartFile kmzFile) {
        String photoId  =  airEditDto.getPhotoId();
        //查询此条任务是否有源复飞任务 绑定到源复飞任务上面
        AirTaskRes airTaskRes = airTaskMapper.queryTaskInfo(airEditDto.getOriginalTaskRecordIds());
        if (ObjectUtil.isNotEmpty(airTaskRes)){
            if (StringUtils.isNotEmpty(airTaskRes.getOriginalTaskRecordId())){
                airEditDto.setOriginalTaskRecordIds(airTaskRes.getOriginalTaskRecordId());
            }
        }
        //通过源复飞任务id 查询航线任务信息
        AirTaskRes taskRes = airTaskMapper.queryTaskInfo(airEditDto.getOriginalTaskRecordIds());
        if (ObjectUtil.isNotNull(taskRes)){
            int count = airTaskMapper.queryTaskInfoCount(airEditDto.getOriginalTaskRecordIds());
            airEditDto.setFlightRouteName(taskRes.getFlightRouteName()+ "复飞任务_" + count);
        }
        //新增航线成功后 新增一条定时飞行的任务 拼装一个定时的 计划参数
        AirPlanAddOrEditReq airPlanAddOrEditReq = new AirPlanAddOrEditReq();
        airPlanAddOrEditReq.setName(airEditDto.getTaskRecordName());
        airPlanAddOrEditReq.setGainDataMode(Integer.valueOf(Constants.TWO));
        airPlanAddOrEditReq.setGainVideo(Integer.valueOf(Constants.ONE));
        airPlanAddOrEditReq.setAuto(Integer.valueOf(Constants.ONE));
        airPlanAddOrEditReq.setPlanType(Integer.valueOf(Constants.ZERO));
        airPlanAddOrEditReq.setCycleExecutionUnit(Integer.valueOf(Constants.ZERO));
        airPlanAddOrEditReq.setOriginalId(airEditDto.getOriginalId());
        airPlanAddOrEditReq.setDeviceCode(airEditDto.getDeviceCode());
        airPlanAddOrEditReq.setOriginalTaskRecordIds(airEditDto.getOriginalTaskRecordIds());
        airPlanAddOrEditReq.setRegularExecutionDate(airEditDto.getTaskStartTime());
        airPlanAddOrEditReq.setAccessNode(airEditDto.getAccessNode());
        airPlanAddOrEditReq.setDefinition(Constants.ZERO);
        airPlanAddOrEditReq.setPhotoId(photoId);
        //调用飞控接口 上传文件
        airEditDto.setStartNestCode(airTaskRes.getStartNestCode());
        airEditDto.setEndNestCode(airTaskRes.getEndNestCode());
        airEditDto.setFrogLeap(airTaskRes.getFrogLeap());
        airEditDto.setUavModelCode(airTaskRes.getUavModelCode());
        Result result = editAirLine(airEditDto,kmzFile);
        String flightRouteId = result.getData().toString();
        airPlanAddOrEditReq.setFlightRouteId(flightRouteId);
        airPlanAddOrEditReq.setUavCode(airEditDto.getUavCode());
        airPlanAddOrEditReq.setUavNestCode(airEditDto.getDeviceCode());

        //起始 降落 蛙跳 无人机型号
        airPlanAddOrEditReq.setStartNestCode(airTaskRes.getStartNestCode());
        airPlanAddOrEditReq.setEndNestCode(airTaskRes.getEndNestCode());
        airPlanAddOrEditReq.setFrogLeap(airTaskRes.getFrogLeap());
        airPlanAddOrEditReq.setUavModelCode(airTaskRes.getUavModelCode());

        airPlanService.addOrEditAirPlan(airPlanAddOrEditReq,Constants.ONE);
        return Result.success("");
    }

    /**
     * 批量飞行返航
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result flyReturnBatch(List<AirPointToFlyReq> airPointToFlyReq) {
        if(CollectionUtil.isNotEmpty(airPointToFlyReq)){
            airPointToFlyReq.stream().forEach(airPointToFly -> {
                airPointToFly.setReturnMode(Constants.ZERO);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                String platformCode = loginUser.getUser().getPlatformCode();
                airPointToFly.setAcceptNode(platformCode);
                NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(airPointToFly);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_RETURN);
                paramter.setPath(path);
                okhttpUtil.doPostUavGateWayNoException(paramter, airPointToFly.getAccessNode(), platformCode);
            });
        }
        return Result.success("");
    }

    /**
     * 查询航线所在行业
     * @param airEditDto
     * @return
     */
    @Override
    public Result getAirLineInfoByFlightRouteId(AirEditDto airEditDto) {
        if (ObjectUtil.isNotNull(airEditDto) && ObjectUtil.isNotEmpty(airEditDto.getFlightRouteId())){
            AirLineVo airLineVo = airLineMapper.queryAirLineInfoByFlightRouteId(airEditDto);
            //通过租户id查询租户下所有userId
            List<String> userIds = airLineMapper.queryUserIdByTenantId(airLineVo.getTenantId());
            airLineVo.setUserIds(userIds);
            return Result.data(airLineVo);
        }
        return Result.fail("");
    }

    /**
     *
     * @param telemetryReq
     * @return
     */
    @Override
    public Result queryUserDeviceOrder(TelemetryReq telemetryReq) {
        List<TenantInfoRes> tenantInfoRes = airLineMapper.queryUserDeviceOrder(telemetryReq.getDeviceCode());
        return Result.data(tenantInfoRes);
    }

    /**
     * 新指点飞行
     *
     * @param airPointToFlyReq
     * @return
     */
    @Override
    public Result pointToFlyNew(AirPointToFlyReq airPointToFlyReq,MultipartFile kmzFile) {
        //上传文件
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        AjaxResult ajaxResult = fileFeignClient.uploadFile(kmzFile);
        if (ajaxResult != null && ajaxResult.ok()) {
            com.chinatower.file.vo.SysFileInfo sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
            log.info("新指点飞行航线文件信息：{}", JSONObject.toJSON(sysFileInfo));
            if (ObjectUtil.isEmpty(sysFileInfo.getFileUrl())) {
                return Result.fail("文件上传获取fileUrl失败！");
            }
            String fileName = sysFileInfo.getFileName();
            airPointToFlyReq.setFileName(fileName);
            airPointToFlyReq.setKmzAccessUrl(sysFileInfo.getFileUrl());
        }
        airPointToFlyReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airPointToFlyReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.POINTT_TO_FLY_NEW);
        path.setDeviceCode(airPointToFlyReq.getDeviceCode());
        paramter.setPath(path);
        ResultEntity cr = new ResultEntity();
        log.info("新指点飞行下发参数{}",JSONObject.toJSONString(airPointToFlyReq));
        try {
            cr = okhttpUtil.doPostUavGateWayNoException(paramter, airPointToFlyReq.getAccessNode(), platformCode);
        }catch (Exception e){
            log.info("新指点飞行出错{}", JSONObject.toJSONString(airPointToFlyReq));
        }
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
            if (!ObjectUtil.isEmpty(jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                // 创建航线
                String flightRouteId = createFlightRoute(jsonObject, loginUser, airPointToFlyReq, platformCode);
                // 创建飞行任务
                createFlightTask(jsonObject, loginUser, airPointToFlyReq, platformCode, flightRouteId);
                // 创建航点自动算法配置任务
                if (StringUtil.isNotEmpty(airPointToFlyReq.getAlgorithmCodes())) {
                    createFlightPointTask(jsonObject,airPointToFlyReq,flightRouteId);
                }
                //创建喇叭配置
                List<VmAirLineSensorInfo> airLineHornInfoList = buildHornInfo(airPointToFlyReq.getUosInfo(),loginUser.getUser().getUserId(),flightRouteId,airPointToFlyReq.getDeviceCode());
                if(!CollectionUtils.isEmpty(airLineHornInfoList)) {
                    airLineSensorInfoMapper.batchInsert(airLineHornInfoList);
                }
            }
            Map map = new HashMap<>();
            List<AirPointToFlyReq> airPointToFlyReqs = new ArrayList<>();
            List<AirPointToFlyReq> errorAirPointToFlyReqs = new ArrayList<>();
            List<AirPointToFlyReq> executeAirPointToFlyReqs = new ArrayList<>();
            log.info("新指点飞行结果:{}",cr);
            if (ObjectUtil.isNotNull(cr)) {
                AirPointToFlyReq airPointToFlyReq1 = new AirPointToFlyReq();
                airPointToFlyReq1.setDeviceCode(airPointToFlyReq.getDeviceCode());
                if ("成功".equals(cr.getReturnMsg()) && "0000".equals(cr.getReturnCode()) && ObjectUtil.isNotNull(cr.getData())){
                    JSONObject crObject = JSON.parseObject(cr.getData().toString());
                    if (!ObjectUtil.isEmpty(crObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                        String flightRouteId = crObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
                        airPointToFlyReq1.setFlightRouteId(flightRouteId);
                        airPointToFlyReqs.add(airPointToFlyReq1);
                    }else {
                        executeAirPointToFlyReqs.add(airPointToFlyReq1);
                    }
                }
                if ("成功".equals(cr.getReturnMsg()) && "0000".equals(cr.getReturnCode()) && ObjectUtil.isNull(cr.getData())){
                    executeAirPointToFlyReqs.add(airPointToFlyReq1);
                }
                if ("8888".equals(cr.getReturnCode())){
                    errorAirPointToFlyReqs.add(airPointToFlyReq1);
                }
            }
            map.put("success",airPointToFlyReqs);
            map.put("error",errorAirPointToFlyReqs);
            map.put("execute",executeAirPointToFlyReqs);
            return Result.data(map);
        }else {
            return Result.data("指点飞行调用飞控返回数据为空");
        }
    }

    /**
     * 根据任务ID查询航线信息
     *
     * @param airTaskReq 任务查询参数
     * @return 航线详情
     */
    @Override
    public Result<AirLineVo> queryAirLineByTaskId(AirTaskReq airTaskReq) {
        if (ObjectUtil.isNull(airTaskReq) || ObjectUtil.isNull(airTaskReq.getTaskRecordId())) {
            return Result.fail("任务ID不能为空");
        }
        String taskId = airTaskReq.getTaskRecordId();
        log.info("AirLineServiceImpl-queryAirLineByTaskId-taskId:{}", taskId);
        AirLineVo airLineVo = airLineMapper.queryAirLineByTaskId(taskId);
        if (ObjectUtil.isNull(airLineVo)) {
            return Result.fail("未查询到对应航线信息");
        }
        return Result.data(airLineVo);
    }

    @Override
    public Result getAlgorithmInfoByLineId(String flightRouteId) {
        List<Map<String, Object>> resMap =airLineMapper.getAlgorithmInfoByLineId(flightRouteId);
        log.info("airLine-getAlgorithmInfoByLineId-rsp{}",JSONObject.toJSON(resMap));
        return Result.data(resMap);
    }

    /**
     * 查询航线列表
     *
     * @param airEditDto
     * @return
     */
    @Override
    public List<AirLineVo> getAirLinesListNologin(AirEditDto airEditDto) {
        List<AirLineVo> airLineVos = airLineMapper.getAirLinesListNologin(airEditDto);
        return airLineVos;
    }

    @Override
    public Result uavRealTimeAlgorithm(UavRealTimeAlgorithmReq uavRealTimeAlgorithmReq) {
        log.info("航线实时流任务下发算法仓入参" + JSON.toJSONString(uavRealTimeAlgorithmReq));
        if (ObjectUtil.isNotEmpty(uavRealTimeAlgorithmReq)) {
            UavMonitorGeoJson uavMonitorGeoJson = uavRealTimeAlgorithmReq.getUavMonitorGeoJson();
                if (ObjectUtil.isNotEmpty(uavMonitorGeoJson)) {
                    if (StringUtils.isNotEmpty(uavMonitorGeoJson.getGeometriesStr())){
                        try {
                            uavMonitorGeoJson.setGeometries(JSONObject.parseObject(uavMonitorGeoJson.getGeometriesStr()));
                        } catch (Exception e) {
                            return Result.fail("电子围栏线融合参数错误");
                        }
                    }
                    if (StringUtils.isNotEmpty(uavMonitorGeoJson.getGeometriesBufferStr())){
                        try {
                            uavMonitorGeoJson.setGeometriesBuffer(JSONObject.parseObject(uavMonitorGeoJson.getGeometriesBufferStr()));
                        } catch (Exception e) {
                            return Result.fail("电子围栏面融合参数错误");
                        }
                    }
                    UavCamera queryUavCamera = new UavCamera();
                    queryUavCamera.setSensorSize(uavMonitorGeoJson.getSensorSize());
                    if (Constants.ZERO.equals(uavMonitorGeoJson.getUavType())){
                        queryUavCamera.setMaufacturerCode("dji");
                    }else {
                        queryUavCamera.setMaufacturerCode("else");
                    }
                    ExtraInfo extraInfo = new ExtraInfo();
                    UavInfo uavInfo = new UavInfo();
                    UavInfo.UavCamera uavCamera = new UavInfo.UavCamera();
                    List<String> dimensionList = Arrays.stream(queryUavCamera.getSensorSize().split("x"))
                            .collect(Collectors.toList());
                    uavCamera.setSensorSize(dimensionList);
                    uavInfo.setUavCamera(uavCamera);
                    uavInfo.setFlightRoutId(uavRealTimeAlgorithmReq.getFlightRouteId());
                    uavInfo.setAlgorithmCode(uavRealTimeAlgorithmReq.getAlgorithmCode());
                    uavInfo.setDelFlag(Constants.ZERO);
                    uavInfo.setMergePitch(false);
                    uavInfo.setMergeRoll(false);
                    uavInfo.setMergeHeading(true);
                    if (ObjectUtil.isNotEmpty(queryUavCamera.getMaufacturerCode())){
                        if ("dji".equals(queryUavCamera.getMaufacturerCode())) {
                            // 检查型号是否在大疆枚举范围内
                            if (DJINestTypeCode.fromCode(queryUavCamera.getMaufacturerCode()) != null) {
                                uavInfo.setMergeHeading(false);
                            }
                        }
                    }
                    //无人机电子围栏存入mongo
                    if (ObjectUtil.isNotEmpty(uavMonitorGeoJson.getGeometriesStr())){
                        uavInfo.setGeometries(uavMonitorGeoJson.getGeometriesStr());
                    }
                    if (ObjectUtil.isNotEmpty(uavMonitorGeoJson.getGeometriesBufferStr())){
                        uavInfo.setGeometriesBuffer(uavMonitorGeoJson.getGeometriesBufferStr());
                    }
                    BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_FLIGHT_FENCE_INFO);
                    bulkOperations.insert(uavInfo);
                    bulkOperations.execute();
                    //处理下发字段问题
                    uavInfo.setGeometries("");
                    uavInfo.setGeometriesBuffer("");
                    uavInfo.setUavMonitorGeoJson(uavMonitorGeoJson);
                    extraInfo.setUavInfo(uavInfo);
                    uavRealTimeAlgorithmReq.setExtraInfo(extraInfo);
                }
//            String platformCode = uavOkhttpUtil.getSelfPlatFormCode();
            uavRealTimeAlgorithmReq.setAcceptNode("");
            uavRealTimeAlgorithmReq.setChannelCode(uavRealTimeAlgorithmReq.getDeviceCode());
            NetConnectUtils.RequestParamter<UavRealTimeAlgorithmReq> paramterAl = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path pathAl = new NetConnectUtils.RequestParamter.Path();
            pathAl.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_ALGORITHMS_TASK);
            paramterAl.setPath(pathAl);
            paramterAl.setBody(uavRealTimeAlgorithmReq);
            ResultEntity result = uavOkhttpUtil.doPostAlgGateWay(paramterAl, uavRealTimeAlgorithmReq.getAccessNode(), "");

            //插入日志表
            log.info("新增编辑航线实时流任务{}下发结果{}", uavRealTimeAlgorithmReq.getFlightRouteId(), JSON.toJSONString(result));
            //调用信息存入mongo
            AirAlTaskLogReq airAlTaskLogReq = new AirAlTaskLogReq();
            airAlTaskLogReq.setTaskId(uavRealTimeAlgorithmReq.getTaskId());
            airAlTaskLogReq.setTaskJson(JSONObject.toJSONString(uavRealTimeAlgorithmReq));
            airAlTaskLogReq.setReturnMsg(result.getReturnMsg());
            airAlTaskLogReq.setReturnCode(result.getReturnCode());
            airAlTaskLogReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
            airAlTaskLogReq.setErrMsg(result.getErrorMsg());
            airAlTaskLogReq.setAccessNode(uavRealTimeAlgorithmReq.getAccessNode());
            airAlTaskLogReq.setAcceptNode("");
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UavConstants.AIR_TASK_AL_SYNC_LOG);
            bulkOperations.insert(airAlTaskLogReq);
            bulkOperations.execute();
            if (ObjectUtil.isEmpty(result) || "8888".equals(result.getReturnCode())) {
                return Result.fail("任务下发失败");
            }

            //任务存入本地
            SyncAlgorithmsReq syncAlgorithmsReq = new SyncAlgorithmsReq();
            BeanUtils.copyProperties(uavRealTimeAlgorithmReq, syncAlgorithmsReq);
            syncAlgorithmsReq.setCreateUser(uavRealTimeAlgorithmReq.getUserId());
            syncAlgorithmsReq.setTeanantId(uavRealTimeAlgorithmReq.getTenantId());
            syncAlgorithmsReq.setIndustryCode(uavRealTimeAlgorithmReq.getIndustryCode());
            syncAlgorithmsReq.setStatus(Constants.ZERO);
            syncAlgorithmsReq.setAlgorithmType(Constants.ZERO);
            airLineMapper.editAlgorithms(syncAlgorithmsReq);

            //算法下发成功后 新增航线
            AirEditDto airEditDto = new AirEditDto();
            airEditDto.setFlightRouteId(uavRealTimeAlgorithmReq.getFlightRouteId());
            airEditDto.setTenantId(uavRealTimeAlgorithmReq.getTenantId());
            airEditDto.setIndustryCode(uavRealTimeAlgorithmReq.getIndustryCode());
            // 检测航线是否存在
            Boolean airLineExist = airLineMapper.checkAirLineExist(airEditDto);
            if (!airLineExist) {
                airEditDto.setDeviceCode(uavRealTimeAlgorithmReq.getDeviceCode());
                airEditDto.setCreateUser(uavRealTimeAlgorithmReq.getUserId());
                airEditDto.setAcceptNode(uavRealTimeAlgorithmReq.getAcceptNode());
                airEditDto.setFlightMainType(Constants.THREE);//第三方航线(伪航线)
                airLineMapper.addAirLine(airEditDto);
            }
            return Result.success("");
        } else {
            log.info("下发算法任务失败{}", uavRealTimeAlgorithmReq.getFlightRouteId());
        }
        return Result.fail("算法任务失败");
    }

    private void createFlightPointTask(JSONObject jsonObject, AirPointToFlyReq airPointToFlyReq,String flightRouteId) {
        List<AirEditDto.PointAlarmCodes> pointAlarmCodesList = new ArrayList<>();
        AirEditDto.PointAlarmCodes pointAlarmCodes = new AirEditDto.PointAlarmCodes();
        pointAlarmCodes.setLatitude(airPointToFlyReq.getLatitude());
        pointAlarmCodes.setLongitude(airPointToFlyReq.getLongitude());
        pointAlarmCodes.setAlgorithmCodeStr(airPointToFlyReq.getAlgorithmCodes());
        pointAlarmCodesList.add(pointAlarmCodes);
        airLineMapper.addAirPointInfo(pointAlarmCodesList,flightRouteId);
    }

    /**
     * 创建航线
     */
    private String createFlightRoute(JSONObject jsonObject, LoginUser loginUser,
                                     AirPointToFlyReq airPointToFlyReq, String platformCode) {
        String flightRouteId = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
        AirEditDto airEditDto = new AirEditDto();
        // 设置基本信息
        setBasicFlightRouteInfo(airEditDto, loginUser, airPointToFlyReq, platformCode, flightRouteId);
        // 设置无人机和位置信息
        setUavAndLocationInfo(airEditDto, airPointToFlyReq);
        airLineMapper.addAirLine(airEditDto);
        return flightRouteId;
    }


    /**
     * 设置航线基本信息
     */
    private void setBasicFlightRouteInfo(AirEditDto airEditDto, LoginUser loginUser,
                                         AirPointToFlyReq airPointToFlyReq, String platformCode, String flightRouteId) {
        String industryCode = loginUser.getUser().getIndustryCode();
        String tenantId = loginUser.getUser().getTenantId();
        String userId = loginUser.getUser().getUserId();
        String appCode = loginUser.getUser().getAppVerCode();

        airEditDto.setFlightRouteId(flightRouteId);
        airEditDto.setIndustryCode(industryCode);
        airEditDto.setDeviceCode(airPointToFlyReq.getDeviceCode());
        airEditDto.setAcceptNode(platformCode);
        airEditDto.setAccessNode(airPointToFlyReq.getAccessNode());
        airEditDto.setTenantId(tenantId);
        airEditDto.setAppVerCode(appCode);
        airEditDto.setCreateUser(userId);
        airEditDto.setFlightRouteName("指点飞行" + new Date().getTime());
        airEditDto.setFlightMainType(Constants.ONE);
    }

    /**
     * 设置无人机和位置信息
     */
    private void setUavAndLocationInfo(AirEditDto airEditDto, AirPointToFlyReq airPointToFlyReq) {
        // 查询deviceCode所属的无人机编码
        String uavCode = airLineMapper.getUavCodeByDeviceCode(airEditDto.getDeviceCode());

        // 根据设备编码查询机巢经纬度坐标
        AirUavInfo uavInfo = airLineMapper.getDeviceInfo(airEditDto.getDeviceCode());
        String pointLongitude = airPointToFlyReq.getLongitude();
        String pointLatitude = airPointToFlyReq.getLatitude();
        String nestLongitude = uavInfo.getUavNestLongitude();
        String nestLatitude = uavInfo.getUavNestLatitude();

        double distance = DistanceUtils.calculateDistance(pointLongitude, pointLatitude, nestLongitude, nestLatitude);

        airEditDto.setAirMileage(String.valueOf(distance));
        airEditDto.setUavCode(uavCode);
    }

    /**
     * 创建飞行任务
     */
    private void createFlightTask(JSONObject jsonObject, LoginUser loginUser,
                                  AirPointToFlyReq airPointToFlyReq, String platformCode, String flightRouteId) {
        String taskRecordId = jsonObject.get(TASK_RECORD_ID).toString();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();

        AirTaskReq airTaskReq = buildAirTaskReq(loginUser, airPointToFlyReq, platformCode,
                flightRouteId, taskRecordId, tenantId, industryCode);

        List<AirTaskReq> airTaskReqs = new ArrayList<>();
        airTaskReqs.add(airTaskReq);
        airTaskMapper.insertBath(airTaskReqs);
    }

    /**
     * 构建飞行任务请求对象
     */
    private AirTaskReq buildAirTaskReq(LoginUser loginUser, AirPointToFlyReq airPointToFlyReq,
                                       String platformCode, String flightRouteId, String taskRecordId,
                                       String tenantId, String industryCode) {
        AirTaskReq airTaskReq = new AirTaskReq();

        airTaskReq.setPlanId("");
        airTaskReq.setTaskRecordId(taskRecordId);
        airTaskReq.setTenantId(tenantId);
        airTaskReq.setScheduleExecTime(DateUtil.format(new Date(), com.chinatower.airline.constants.Constants.DATE_YMDHMS));
        airTaskReq.setIndustryCode(industryCode);
        airTaskReq.setCreateUser(loginUser.getUsername());

        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM);
        String formattedTime = dateFormat.format(currentTime);

        airTaskReq.setAcceptNode(platformCode);
        airTaskReq.setPlanName(Constants.POINT_FLY + formattedTime);
        airTaskReq.setAccessNode(airPointToFlyReq.getAccessNode());
        airTaskReq.setStatus(Constants.ZERO);
        airTaskReq.setMediumStatus(Constants.ZERO);
        airTaskReq.setFlightRouteId(flightRouteId);
        airTaskReq.setIsItEnabled(Constants.ZERO);
        airTaskReq.setStartNestCode(airPointToFlyReq.getDeviceCode());
        airTaskReq.setEndNestCode(airPointToFlyReq.getDeviceCode());

        return airTaskReq;
    }

    /**
     * 飞行
     * @param airPointToFlyReq
     * @return
     */
    public ResultEntity singlePointToFly(AirPointToFlyReq airPointToFlyReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airPointToFlyReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirPointToFlyReq> paramter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        paramter.setBody(airPointToFlyReq);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.POINTT_TO_FLY_ON);
        path.setDeviceCode(airPointToFlyReq.getDeviceCode());
        paramter.setPath(path);
        ResultEntity cr = new ResultEntity();
        try {
            cr = okhttpUtil.doPostUavGateWayNoException(paramter, airPointToFlyReq.getAccessNode(), platformCode);
        }catch (Exception e){
        }
        if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
            //本地新建 指点飞行大类的航线
            AirEditDto airEditDto = new AirEditDto();
            JSONObject jsonObject = JSON.parseObject(cr.getData().toString());
            if (!ObjectUtil.isEmpty(jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID))){
                String flightRouteId = jsonObject.get(com.chinatower.airline.constants.Constants.FLIGHT_ROUTE_ID).toString();
                airEditDto.setFlightRouteId(flightRouteId);
                //是否切换客户视角
                String industryCode = loginUser.getUser().getIndustryCode();
                String tenantId = loginUser.getUser().getTenantId();
                String userId = loginUser.getUser().getUserId();
                String appCode = loginUser.getUser().getAppVerCode();
                airEditDto.setIndustryCode(industryCode);
                airEditDto.setDeviceCode(airPointToFlyReq.getDeviceCode());
                airEditDto.setAcceptNode(platformCode);
                airEditDto.setAccessNode(airPointToFlyReq.getAccessNode());
                airEditDto.setTenantId(tenantId);
                airEditDto.setAppVerCode(appCode);
                airEditDto.setCreateUser(userId);
                airEditDto.setFlightRouteName("指点飞行"+ new Date().getTime());
                airEditDto.setFlightMainType(Constants.ONE);
                //查询deviceCode所属的无人机编码
                String uavCode = airLineMapper.getUavCodeByDeviceCode(airEditDto.getDeviceCode());
                //根据设备编码查询机巢经纬度坐标
                AirUavInfo uavInfo = airLineMapper.getDeviceInfo(airEditDto.getDeviceCode());
                String pointLongitude = airPointToFlyReq.getLongitude();
                String pointLatitude = airPointToFlyReq.getLatitude();
                String nestLongitude = uavInfo.getUavNestLongitude();
                String nestLatitude = uavInfo.getUavNestLatitude();
                double distance = DistanceUtils.calculateDistance(pointLongitude,pointLatitude,nestLongitude,nestLatitude);
                airEditDto.setAirMileage(String.valueOf(distance));
                airEditDto.setUavCode(uavCode);
                airLineMapper.addAirLine(airEditDto);
                String taskRecordId = jsonObject.get(TASK_RECORD_ID).toString();
                //本地新建一条立即飞行的计划 任务本地入库
                AirTaskReq airTaskReq = new AirTaskReq();
                List<AirTaskReq> airTaskReqs = new ArrayList<>();
                airTaskReq.setPlanId("");
                airTaskReq.setTaskRecordId(taskRecordId);
                airTaskReq.setTenantId(tenantId);
                airTaskReq.setScheduleExecTime(DateUtil.format(new Date(), com.chinatower.airline.constants.Constants.DATE_YMDHMS));
                airTaskReq.setIndustryCode(industryCode);
                airTaskReq.setCreateUser(loginUser.getUsername());
                Date currentTime = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.YYYY_MM);
                String formattedTime = dateFormat.format(currentTime);
                airTaskReq.setAcceptNode(platformCode);
                airTaskReq.setPlanName(Constants.POINT_FLY + formattedTime);
                airTaskReq.setAccessNode(airPointToFlyReq.getAccessNode());
                airTaskReq.setStatus(Constants.ZERO);
                airTaskReq.setMediumStatus(Constants.ZERO);
                airTaskReq.setFlightRouteId(flightRouteId);
                airTaskReq.setIsItEnabled(Constants.ZERO);
                airTaskReq.setStartNestCode(airPointToFlyReq.getDeviceCode());
                airTaskReq.setEndNestCode(airPointToFlyReq.getDeviceCode());
                airTaskReqs.add(airTaskReq);
                airTaskMapper.insertBath(airTaskReqs);
            }
            return cr;
        }else {
            return cr;
        }
    }
}
