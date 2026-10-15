package com.chinatower.airline.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.chinatower.airline.dto.request.AlgorithmReq;
import com.chinatower.airline.dto.request.CompareImageReq;
import com.chinatower.airline.dto.response.AirMapAnnotationRes;
import com.chinatower.airline.dto.response.CorrespondPointRes;
import com.chinatower.airline.dto.response.DeviceAirLineRes;
import com.chinatower.airline.dto.response.FlyAreaRes;
import com.chinatower.airline.enums.AirMapEnum;
import com.chinatower.airline.mapper.AirMapMapper;
import com.chinatower.airline.service.AirMapService;
import com.chinatower.common.core.cache.MongoTemplateDelegate;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondRecogBatchVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 13:20
 */
@Service
@Slf4j
public class AirMapServiceImpl implements AirMapService {
    @Resource
    private AirMapMapper airMapMapper;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private MongoTemplateDelegate mongoTemplate;

    /**
     * @param annotationMapReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    @Transactional
    public Result addAnnotationMap(AnnotationMapReq annotationMapReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        annotationMapReq.setTenantId(loginUser.getUser().getTenantId());
        annotationMapReq.setCreateUser(loginUser.getUser().getUserId());
        annotationMapReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (ObjectUtil.isEmpty(annotationMapReq.getId())) {
            annotationMapReq.setAnnotationName(AirMapEnum.getByCode(annotationMapReq.getType()).getName());
            airMapMapper.addAnnotationMap(annotationMapReq);
        }else {
            airMapMapper.updateAnnotationMap(annotationMapReq);
        }
        AnnotationMapReq mapReq = airMapMapper.queryAnnotationMapById(annotationMapReq.getId());
        return Result.data(mapReq);
    }

    /**
     * @param annotationMapReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public List<AirMapAnnotationRes> queryAnnotationMap(AnnotationMapReq annotationMapReq) {
        Integer pageNum = annotationMapReq.getPageNum();
        Integer pageSize = annotationMapReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<AirMapAnnotationRes> list = airMapMapper.queryAnnotationMap(annotationMapReq);
        return list;
    }

    /**
     * @param annotationMapReq
     * @return
     */
    @Override
    public Result delAnnotationMap(AnnotationMapReq annotationMapReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        annotationMapReq.setUpdateUser(loginUser.getUser().getUserId());
        airMapMapper.delAnnotationMap(annotationMapReq);
        return Result.success("");
    }

    /**
     *
     * @param annotationMapReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result defaultAnnotationMapName(AnnotationMapReq annotationMapReq) {
        int count = airMapMapper.defaultAnnotationMapName(annotationMapReq);
        return Result.data(count);
    }

    /**
     *
     * @param annotationMapReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result checkAnnotationMapName(AnnotationMapReq annotationMapReq) {
        AirMapAnnotationRes annotationRes = airMapMapper.checkAnnotationMapName(annotationMapReq);
        return Result.data(annotationRes);
    }

    /**
     *
     * @param flyAreaReq
     * @return
     */
    @Override
    @Transactional
    public Result addFlyArea(FlyAreaReq flyAreaReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        flyAreaReq.setTenantId(loginUser.getUser().getTenantId());
        flyAreaReq.setCreateUser(loginUser.getUser().getUserId());
        flyAreaReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (CollectionUtil.isNotEmpty(flyAreaReq.getDeviceCodes())){
            String devicecodes = flyAreaReq.getDeviceCodes().stream()
                    .collect(Collectors.joining(","));
            flyAreaReq.setDeviceCodesStr(devicecodes);
        }
        airMapMapper.addFlyArea(flyAreaReq);
        FlyAreaRes flyAreaRes = airMapMapper.queryFlyMapById(flyAreaReq.getId());
        if (ObjectUtil.isNotEmpty(flyAreaRes)  && StringUtils.isNotEmpty(flyAreaRes.getDeviceCodesStr())){
            List<String> deviceCodes = Arrays.asList(flyAreaRes.getDeviceCodesStr().split(","));
            flyAreaRes.setDeviceCodes(deviceCodes);
        }
        return Result.data(flyAreaRes);
    }

    /**
     *
     * @param flyAreaReq
     * @return
     */
    @Override
    public Result delFlyArea(FlyAreaReq flyAreaReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        flyAreaReq.setUpdateUser(loginUser.getUser().getUserId());
        airMapMapper.delFlyArea(flyAreaReq);
        return Result.success("");
    }

    /**
     *
     * @param flyAreaReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public List<FlyAreaRes> queryFlyAreaList(FlyAreaReq flyAreaReq) {
        Integer pageNum = flyAreaReq.getPageNum();
        Integer pageSize = flyAreaReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<FlyAreaRes> list = airMapMapper.queryFlyAreaList(flyAreaReq);
        Set<String> allDeviceCodes = new HashSet<>();
        list.forEach(flyAreaRes -> {
            if (StringUtils.isNotEmpty(flyAreaRes.getDeviceCodesStr())){
                List<String> deviceCodes = Arrays.asList(flyAreaRes.getDeviceCodesStr().split(","));
                flyAreaRes.setDeviceCodes(deviceCodes);
                allDeviceCodes.addAll(deviceCodes);
            }
        });
        flyAreaReq.setAllDeviceCodes(allDeviceCodes);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        flyAreaReq.setTenantId(loginUser.getUser().getTenantId());
        flyAreaReq.setCreateUser(loginUser.getUser().getUserId());
        flyAreaReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        //批量查询无人机名称
        if (CollectionUtil.isNotEmpty(allDeviceCodes)){
            List<DeviceAirLineRes> deviceAirLineResList = airMapMapper.queryDevice(flyAreaReq);
            list.forEach(flyAreaRes -> {
                if (CollectionUtil.isNotEmpty(flyAreaRes.getDeviceCodes())){
                    List<DeviceAirLineRes> deviceResList = new ArrayList<>();
                    flyAreaRes.getDeviceCodes().stream().forEach(f -> {
                        deviceAirLineResList.stream().forEach(deviceAirLineRes -> {
                            if(f.equals(deviceAirLineRes.getUavNestCode())){
                                deviceResList.add(deviceAirLineRes);
                            }
                        });
                    });
                    flyAreaRes.setDeviceResList(deviceResList);
                    flyAreaRes.setDeviceNames(
                            deviceResList.stream()
                                    .map(device ->
                                            device.getUavNestName() != null && !device.getUavNestName().isEmpty()
                                                    ? device.getUavNestName()
                                                    : device.getUavName()
                                    )
                                    .collect(Collectors.toList())
                    );
                }
            });
        }
        return list;
    }

    /**
     *
     * @param flyAreaReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result checkFlyAreaName(FlyAreaReq flyAreaReq) {
        FlyAreaRes flyAreaRes = airMapMapper.checkFlyAreaName(flyAreaReq);
        return Result.data(flyAreaRes);
    }

    /**
     *
     * @param flyAreaReq
     * @return
     */
    @Override
    public Result editFlyArea(FlyAreaReq flyAreaReq) {
        if (CollectionUtil.isNotEmpty(flyAreaReq.getDeviceCodes())){
            String devicecodes = flyAreaReq.getDeviceCodes().stream()
                    .collect(Collectors.joining(","));
            flyAreaReq.setDeviceCodesStr(devicecodes);
        }
        airMapMapper.updateFlyArea(flyAreaReq);
        return Result.success("");
    }

    /**
     * 对照点 新增/编辑
     * @param correspondPointReq
     * @return
     */
    @Override
    public Result editCorrespondPoint(CorrespondPointReq correspondPointReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        correspondPointReq.setTenantId(loginUser.getUser().getTenantId());
        correspondPointReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        correspondPointReq.setAppVerCode(loginUser.getUser().getAppVerCode());
        if (StringUtils.isNotEmpty(correspondPointReq.getId())) {
            correspondPointReq.setUpdateUser(loginUser.getUser().getUserId());
            airMapMapper.updateCorrespondPoint(correspondPointReq);
        } else {
            correspondPointReq.setCreateUser(loginUser.getUser().getUserId());
            airMapMapper.addCorrespondPoint(correspondPointReq);
        }
        return Result.success("");
    }

    /**
     * 对照点 删除
     * @param correspondPointReq
     * @return
     */
    @Override
    public Result delCorrespondPoint(CorrespondPointReq correspondPointReq) {
        airMapMapper.delCorrespondPoint(correspondPointReq);
        return Result.success("");
    }

    /**
     * 对照点 查询
     * @param correspondPointReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public List<CorrespondPointRes> queryCorrespondPointList(CorrespondPointReq correspondPointReq) {
        Integer pageNum = correspondPointReq.getPageNum();
        Integer pageSize = correspondPointReq.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<CorrespondPointRes> airCorrespondPhotoReps = airMapMapper.queryCorrespondPointList(correspondPointReq);
        //处理url
        if (CollectionUtil.isNotEmpty(airCorrespondPhotoReps)){
            //转换accessUrl
            List<String> url = airCorrespondPhotoReps.stream().map(CorrespondPointRes::getPhotoUrl).collect(Collectors.toList());
            Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(url);
            airCorrespondPhotoReps.stream().forEach(airCorrespondPhotoRep -> {
                if (urlMap.containsKey(airCorrespondPhotoRep.getPhotoUrl())){
                    airCorrespondPhotoRep.setPhotoAccessUrl(urlMap.get(airCorrespondPhotoRep.getPhotoUrl()));
                }
            });
        }
        return airCorrespondPhotoReps;
    }

    /**
     * 校验对照点名称唯一
     * @param correspondPointReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code")
    public Result checkCorrespondPointName(CorrespondPointReq correspondPointReq) {
        if (correspondPointReq == null || StringUtils.isEmpty(correspondPointReq.getPointName())) {
            return Result.fail("对照点名称不能为空");
        }
        // 查询是否存在同名记录
        int count = airMapMapper.countByPointName(correspondPointReq);
        if (count > 0) {
            return Result.fail("对照点名称已存在");
        }
        return Result.success("对照点名称可用");
    }

    /**
     * 查询对应航线
     * @param flyAreaReq
     * @return
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    public List<AirLineVo> queryAirLineByFlyArea(FlyAreaReq flyAreaReq) {
        try {
            // 参数校验
            if (flyAreaReq == null || flyAreaReq.getId() == null) {
                return new ArrayList<>();
            }

            // 1. 查询限飞区信息
            FlyAreaRes flyAreaRes = airMapMapper.queryFlyMapById(flyAreaReq.getId());
            if (ObjectUtil.isEmpty(flyAreaRes) || StringUtils.isBlank(flyAreaRes.getGeometry())) {
                return new ArrayList<>();
            }

            // 2. 解析限飞区几何图形
            Geometry flyAreaGeometry = parseFlyAreaGeometry(flyAreaRes);
            if (flyAreaGeometry == null) {
                return new ArrayList<>();
            }

            // 3. 设置设备编码
            if (StringUtils.isNotBlank(flyAreaRes.getDeviceCodesStr())) {
                List<String> deviceCodesList = Arrays.asList(flyAreaRes.getDeviceCodesStr().split(","));
                flyAreaReq.setDeviceCodes(deviceCodesList);
            }

            // 4. 查询航线列表
            List<AirLineVo> airLineList = airMapMapper.queryAirLineList(flyAreaReq);
            List<String> flightRouteIds = findIntersectedRoutes(airLineList, flyAreaGeometry);

            // 5. 分页查询最终结果
            return queryFinalResult(flyAreaReq, flightRouteIds);
        } catch (Exception e) {
            log.error("查询限飞区航线异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 收藏
     * @param compareImageReq
     * @return
     */
    @Override
    public Result compareImageCollectEdit(CompareImageReq compareImageReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        compareImageReq.setUserId(loginUser.getUser().getUserId());
        compareImageReq.setTenantId(loginUser.getUser().getTenantId());
        compareImageReq.setIndustryCode(loginUser.getUser().getIndustryCode());
        compareImageReq.setAppVerCode(loginUser.getUser().getAppVerCode());
        if(Constants.ONE.equals(compareImageReq.getCollectType())){
            //新增收藏
            if (Constants.ZERO.equals(compareImageReq.getPhotoType())){
                compareImageReq.setCollectType(Constants.FIVE);
            }
            if (Constants.ONE.equals(compareImageReq.getPhotoType())){
                compareImageReq.setCollectType(Constants.SIX);
            }
            airMapMapper.insertCompareImageCollect(compareImageReq);
        }else{
            //取消收藏
            airMapMapper.deleteCompareImageCollect(compareImageReq);
        }
        return Result.success("");
    }

    /**
     * 查询算法结果
     * @param algorithmReq 请求参数
     * @return 算法结果封装对象，null表示无数据
     */
    @Override
    public JSONObject queryAlgorithmsResult(AlgorithmReq algorithmReq) {
        if (algorithmReq == null || StringUtils.isEmpty(algorithmReq.getTaskId())) {
            return new JSONObject();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("taskBatchId").is(algorithmReq.getTaskId()));
        query.addCriteria(Criteria.where("analyType").is("1"));
        List<SecondRecogBatchVO> secondRecogBatchVO = mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
        if (CollectionUtil.isNotEmpty(secondRecogBatchVO)) {
            SecondRecogBatchVO secondRecogBatchOne = secondRecogBatchVO.get(0);
            if (StringUtils.isNotEmpty(secondRecogBatchOne.getResultJson())) {
                JSONObject jsonObject = JSON.parseObject(secondRecogBatchOne.getResultJson());
                jsonObject.put("longitude", secondRecogBatchOne.getLongitude());
                jsonObject.put("latitude", secondRecogBatchOne.getLatitude());
                jsonObject.put("algorithmCode", secondRecogBatchOne.getAlgorithmCode());
                jsonObject.put("algorithmType", secondRecogBatchOne.getAlgorithmCode().split("-")[1].substring(0, 7));
                if (jsonObject.containsKey(Constants.PARAM_ALARM_PICTURE_URL)){
                    String url = jsonObject.get(Constants.PARAM_ALARM_PICTURE_URL).toString();
                    String desUrl = fileUrlEncryptUtil.dealObsFileUrl(url);
                    jsonObject.put(Constants.PARAM_ALARM_PICTURE_URL, desUrl);
                }
                return jsonObject;
            }
        }
        return null;
    }

    private Geometry parseFlyAreaGeometry(FlyAreaRes flyAreaRes) {
        try {
            String geometry = flyAreaRes.getGeometry();
            JSONObject jsonObject = JSON.parseObject(geometry);
            String type = jsonObject.getString("type");
            JSONArray coordinates = jsonObject.getJSONArray(com.chinatower.airline.constants.Constants.COORDINATES);

            if (coordinates == null) {
                return null;
            }
            String wkt = "";
            if (StringUtils.equals("Polygon", type)) {
                JSONArray linearRing = coordinates.getJSONArray(0);
                if (linearRing == null) {
                    return null;
                }

                List<List<Double>> polygonPoints = new ArrayList<>();
                for (int i = 0; i < linearRing.size(); i++) {
                    JSONArray point = linearRing.getJSONArray(i);
                    if (point == null || point.size() < 2) {
                        continue;
                    }
                    polygonPoints.add(Arrays.asList(point.getDouble(0), point.getDouble(1)));
                }

                // 确保闭合
                if (!polygonPoints.isEmpty() && !polygonPoints.get(0).equals(polygonPoints.get(polygonPoints.size() - 1))) {
                    polygonPoints.add(polygonPoints.get(0));
                }

                wkt = convertPolygonToWKT(polygonPoints);
                log.info("多边形wkt{}", wkt);
            } else if (StringUtils.equals("Circle", type)) {
                String properties = flyAreaRes.getProperties();
                if (StringUtils.isBlank(properties)) {
                    return null;
                }

                JSONObject jsonObjectCircle = JSON.parseObject(properties);
                String radiusStr = jsonObjectCircle.getString("radius");
                if (StringUtils.isBlank(radiusStr)) {
                    return null;
                }

                Double radiusMeters = Double.valueOf(radiusStr);
                Double lon = coordinates.getDouble(0);
                Double lat = coordinates.getDouble(1);
                wkt = convertCircleToWKT(lon, lat, radiusMeters, 36);
                log.info("圆形wkt{}", wkt);
            }

            if (StringUtils.isBlank(wkt)) {
                return null;
            }

            WKTReader reader = new WKTReader(new GeometryFactory());
            Geometry geom = reader.read(wkt);
            return geom.buffer(0);
        } catch (Exception e) {
            log.error("解析限飞区几何图形异常", e);
            return null;
        }
    }

    private List<String> findIntersectedRoutes(List<AirLineVo> airLineList, Geometry flyAreaGeometry) {
        List<String> flightRouteIds = new ArrayList<>();
        if (CollectionUtil.isEmpty(airLineList)) {
            return flightRouteIds;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);

        for (AirLineVo airLineVo : airLineList) {
            try {
                if (airLineVo == null || StringUtils.isBlank(airLineVo.getKmzJson())) {
                    continue;
                }

                // 解析航线JSON
                JSONObject kmzJson = parseKmzJson(airLineVo.getKmzJson());
                if (kmzJson == null) {
                    continue;
                }

                // 获取起飞点
                List<Double> takeOffPoint = getTakeOffPoint(kmzJson);
                if (CollectionUtil.isEmpty(takeOffPoint)) {
                    continue;
                }

                // 根据航线类型处理
                switch (airLineVo.getType()) {
                    case Constants.ONE: // 航点航线
                    case Constants.TWO: // 正射影像
                        handleWaypointRoute(airLineVo, kmzJson, takeOffPoint, flyAreaGeometry, reader, flightRouteIds);
                        break;
                    case Constants.THREE: // 倾斜摄影
                        handleObliquePhotography(airLineVo, kmzJson, takeOffPoint, flyAreaGeometry, reader, flightRouteIds);
                        break;
                    case Constants.FOUR: // 环绕飞行
                        handleSurroundFlight(airLineVo, kmzJson, takeOffPoint, flyAreaGeometry, reader, flightRouteIds);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("处理航线异常, flightRouteId: {}", airLineVo != null ? airLineVo.getFlightRouteId() : "null", e);
            }
        }

        return flightRouteIds;
    }

    private JSONObject parseKmzJson(String kmzJson) {
        if (!JSONValidator.from(kmzJson).validate()) {
            return null;
        }
        return JSON.parseObject(kmzJson);
    }

    private List<Double> getTakeOffPoint(JSONObject kmzJson) {
        try {
            JSONObject templateXml = kmzJson.getJSONObject("template.xml");
            if (templateXml == null) {
                return null;
            }

            JSONObject kml = templateXml.getJSONObject(com.chinatower.airline.constants.Constants.KML);
            JSONObject document = kml.getJSONObject(com.chinatower.airline.constants.Constants.DOCUMENT);
            JSONObject missionConfig = document.getJSONObject("wpml:missionConfig");
            String takeOffRefPoint = missionConfig.getString("wpml:takeOffRefPoint");

            if (StringUtils.isBlank(takeOffRefPoint) || "{}".equals(takeOffRefPoint)) {
                return null;
            }

            String[] coords = takeOffRefPoint.split(",");
            if (coords.length < 2) {
                return null;
            }

            return Arrays.asList(Double.valueOf(coords[0]), Double.valueOf(coords[1]));
        } catch (Exception e) {
            log.error("解析起飞点异常", e);
            return null;
        }
    }

    private void handleWaypointRoute(AirLineVo airLineVo, JSONObject kmzJson, List<Double> takeOffPoint,
                                     Geometry flyAreaGeometry, WKTReader reader, List<String> flightRouteIds) {
        List<List<Double>> waypoints = new ArrayList<>();
        waypoints.add(takeOffPoint);

        try {
            JSONObject waylineWpml = kmzJson.getJSONObject(com.chinatower.airline.constants.Constants.WAYLINE_WPML);
            JSONObject kml = waylineWpml.getJSONObject(com.chinatower.airline.constants.Constants.KML);
            JSONObject document = kml.getJSONObject(com.chinatower.airline.constants.Constants.DOCUMENT);
            JSONObject folder = document.getJSONObject(com.chinatower.airline.constants.Constants.FOLDER);

            if (folder == null) {
                return;
            }

            JSONArray placemark = folder.getJSONArray(com.chinatower.airline.constants.Constants.PLACEMARK);
            if (placemark == null) {
                return;
            }

            for (Object pointObj : placemark) {
                JSONObject point = ((JSONObject) pointObj).getJSONObject(com.chinatower.airline.constants.Constants.POINT);
                if (point == null) {
                    continue;
                }

                String coordinate = point.getString(com.chinatower.airline.constants.Constants.COORDINATES);
                if (StringUtils.isBlank(coordinate)) {
                    continue;
                }

                String[] coords = coordinate.split(",");
                if (coords.length < 2) {
                    continue;
                }

                waypoints.add(Arrays.asList(Double.valueOf(coords[0]), Double.valueOf(coords[1])));
            }

            waypoints.add(takeOffPoint);
            checkIntersection(airLineVo, waypoints, flyAreaGeometry, reader, flightRouteIds);
        } catch (Exception e) {
            log.error("处理航点航线异常, flightRouteId: {}", airLineVo.getFlightRouteId(), e);
        }
    }

    private void handleObliquePhotography(AirLineVo airLineVo, JSONObject kmzJson, List<Double> takeOffPoint,
                                          Geometry flyAreaGeometry, WKTReader reader, List<String> flightRouteIds) {
        List<List<Double>> waypoints = new ArrayList<>();
        waypoints.add(takeOffPoint);

        try {
            JSONObject waylineWpml = kmzJson.getJSONObject(com.chinatower.airline.constants.Constants.WAYLINE_WPML);
            JSONObject kml = waylineWpml.getJSONObject(com.chinatower.airline.constants.Constants.KML);
            JSONObject document = kml.getJSONObject(com.chinatower.airline.constants.Constants.DOCUMENT);
            JSONArray folders = document.getJSONArray(com.chinatower.airline.constants.Constants.FOLDER);

            if (folders == null) {
                return;
            }

            for (Object folderObj : folders) {
                JSONObject folder = (JSONObject) folderObj;
                JSONArray placemark = folder.getJSONArray(com.chinatower.airline.constants.Constants.PLACEMARK);

                if (placemark == null) {
                    continue;
                }

                for (Object pointObj : placemark) {
                    JSONObject point = ((JSONObject) pointObj).getJSONObject(com.chinatower.airline.constants.Constants.POINT);
                    if (point == null) {
                        continue;
                    }

                    String coordinate = point.getString(com.chinatower.airline.constants.Constants.COORDINATES);
                    if (StringUtils.isBlank(coordinate)) {
                        continue;
                    }

                    String[] coords = coordinate.split(",");
                    if (coords.length < 2) {
                        continue;
                    }

                    waypoints.add(Arrays.asList(Double.valueOf(coords[0]), Double.valueOf(coords[1])));
                }
            }

            waypoints.add(takeOffPoint);
            checkIntersection(airLineVo, waypoints, flyAreaGeometry, reader, flightRouteIds);
        } catch (Exception e) {
            log.error("处理倾斜摄影航线异常, flightRouteId: {}", airLineVo.getFlightRouteId(), e);
        }
    }

    private void handleSurroundFlight(AirLineVo airLineVo, JSONObject kmzJson, List<Double> takeOffPoint,
                                      Geometry flyAreaGeometry, WKTReader reader, List<String> flightRouteIds) {
        List<List<Double>> waypoints = new ArrayList<>();
        waypoints.add(takeOffPoint);

        try {
            // 处理航点
            JSONObject waylineWpml = kmzJson.getJSONObject(com.chinatower.airline.constants.Constants.WAYLINE_WPML);
            JSONObject kml = waylineWpml.getJSONObject(com.chinatower.airline.constants.Constants.KML);
            JSONObject document = kml.getJSONObject(com.chinatower.airline.constants.Constants.DOCUMENT);
            JSONObject folder = document.getJSONObject(com.chinatower.airline.constants.Constants.FOLDER);

            if (folder == null) {
                return;
            }

            JSONArray placemark = folder.getJSONArray(com.chinatower.airline.constants.Constants.PLACEMARK);
            if (placemark == null) {
                return;
            }

            for (Object pointObj : placemark) {
                JSONObject point = ((JSONObject) pointObj).getJSONObject(com.chinatower.airline.constants.Constants.POINT);
                if (point == null) {
                    continue;
                }

                String coordinate = point.getString(com.chinatower.airline.constants.Constants.COORDINATES);
                if (StringUtils.isBlank(coordinate)) {
                    continue;
                }

                String[] coords = coordinate.split(",");
                if (coords.length < 2) {
                    continue;
                }

                waypoints.add(Arrays.asList(Double.valueOf(coords[0]), Double.valueOf(coords[1])));
            }

            // 处理环绕圆形
            JSONObject uosInfo = kmzJson.getJSONObject(com.chinatower.airline.constants.Constants.UOSINFO);
            if (uosInfo != null) {
                Double surroundRadius = uosInfo.getDouble("surroundRadius");
                String surroundCenterPoint = uosInfo.getString("surroundCenterPoint");

                if (surroundRadius != null && StringUtils.isNotBlank(surroundCenterPoint)) {
                    String[] centerCoords = surroundCenterPoint.split(",");
                    if (centerCoords.length >= 2) {
                        Double lon = Double.valueOf(centerCoords[1]);
                        Double lat = Double.valueOf(centerCoords[0]);
                        String surroundWkt = convertCircleToWKT(lon, lat, surroundRadius, 36);

                        Geometry surroundGeom = reader.read(surroundWkt).buffer(0);
                        if (checkGeometryIntersection(flyAreaGeometry, surroundGeom)) {
                            flightRouteIds.add(airLineVo.getFlightRouteId());
                            return;
                        }
                    }
                }
            }

            waypoints.add(takeOffPoint);
            checkIntersection(airLineVo, waypoints, flyAreaGeometry, reader, flightRouteIds);
        } catch (Exception e) {
            log.error("处理环绕飞行航线异常, flightRouteId: {}", airLineVo.getFlightRouteId(), e);
        }
    }

    private void checkIntersection(AirLineVo airLineVo, List<List<Double>> waypoints,
                                   Geometry flyAreaGeometry, WKTReader reader, List<String> flightRouteIds) {
        if (CollectionUtil.isEmpty(waypoints)) {
            return;
        }

        try {
            String routeWkt = convertPolygonToWKT(waypoints);
            Geometry routeGeom = reader.read(routeWkt);

            if (checkGeometryIntersection(flyAreaGeometry, routeGeom)) {
                flightRouteIds.add(airLineVo.getFlightRouteId());
            }
        } catch (Exception e) {
            log.error("检查航线相交异常, flightRouteId: {}", airLineVo.getFlightRouteId(), e);
        }
    }

    private boolean checkGeometryIntersection(Geometry geom1, Geometry geom2) {
        if (geom1 == null || geom2 == null) {
            return false;
        }
        return geom1.crosses(geom2) || geom1.overlaps(geom2) || geom2.crosses(geom1);
    }

    private List<AirLineVo> queryFinalResult(FlyAreaReq flyAreaReq, List<String> flightRouteIds) {
        try {
            if (CollectionUtil.isEmpty(flightRouteIds)) {
                flightRouteIds.add("1"); // 防止SQL查询空列表
            }

            flyAreaReq.setFlightRouteIds(flightRouteIds);

            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null) {
                flyAreaReq.setTenantId(loginUser.getUser().getTenantId());
                flyAreaReq.setIndustryCode(loginUser.getUser().getIndustryCode());
                flyAreaReq.setAppVerCode(loginUser.getUser().getAppVerCode());
            }

            Integer pageNum = flyAreaReq.getPageNum();
            Integer pageSize = flyAreaReq.getPageSize();
            if (pageNum != null && pageSize != null) {
                PageHelper.startPage(pageNum, pageSize, true, false, null);
            }

            return airMapMapper.queryAirLineByFlyArea(flyAreaReq);
        } catch (Exception e) {
            log.error("查询最终结果异常", e);
            return new ArrayList<>();
        }
    }

    // 将多边形坐标列表转换为 WKT 格式
    public String convertPolygonToWKT(List<List<Double>> polygonPoints) {
        if (polygonPoints == null || polygonPoints.isEmpty()) {
            return "";
        }

        StringBuilder wktBuilder = new StringBuilder("POLYGON ((");
        for (List<Double> point : polygonPoints) {
            double lon = point.get(0);
            double lat = point.get(1);
            wktBuilder.append(lon).append(" ").append(lat).append(", ");
        }

        // 移除末尾多余的逗号和空格，并闭合括号
        wktBuilder.setLength(wktBuilder.length() - 2);
        wktBuilder.append("))");
        return wktBuilder.toString();
    }

    /**
     * 将圆形转换为WKT格式的多边形（考虑地球曲率）
     * @param lon      圆心经度
     * @param lat      圆心纬度
     * @param radiusM  半径（米）
     * @param segments 边数（默认36）
     * @return WKT字符串
     */
    public static String convertCircleToWKT(double lon, double lat, double radiusM, int segments) {
        List<String> points = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            // 计算偏移后的经纬度（更精确的方法）
            double[] offsetPoint = calculateOffset(lon, lat, radiusM, angle);
            points.add(offsetPoint[0] + " " + offsetPoint[1]);
        }
        // 闭合多边形
        points.add(points.get(0));
        return "POLYGON ((" + String.join(", ", points) + "))";
    }

    /**
     * 根据圆心、半径和角度计算偏移点坐标（使用Haversine公式）
     */
    private static double[] calculateOffset(double lon, double lat, double radiusM, double angle) {
        double earthRadius = 6378137; // 地球半径（米）
        double angularDistance = radiusM / earthRadius;
        double latRad = Math.toRadians(lat);
        double lonRad = Math.toRadians(lon);
        double newLat = Math.asin(
                Math.sin(latRad) * Math.cos(angularDistance) +
                        Math.cos(latRad) * Math.sin(angularDistance) * Math.cos(angle)
        );
        double newLon = lonRad + Math.atan2(
                Math.sin(angle) * Math.sin(angularDistance) * Math.cos(latRad),
                Math.cos(angularDistance) - Math.sin(latRad) * Math.sin(newLat)
        );
        return new double[] {
                Math.toDegrees(newLon),
                Math.toDegrees(newLat)
        };
    }
}
