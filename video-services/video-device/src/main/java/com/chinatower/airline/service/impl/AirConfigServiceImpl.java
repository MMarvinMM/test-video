package com.chinatower.airline.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.dto.request.AIrCarReq;
import com.chinatower.airline.dto.request.AirPwdRes;
import com.chinatower.airline.dto.request.RoadsNameMilepostReq;
import com.chinatower.airline.mapper.AirConfigMapper;
import com.chinatower.airline.service.AirConfigService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.AirDeviceInfoReq;
import com.chinatower.device.entity.dto.AirPwdReq;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCameraModelVO;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import com.chinatower.device.entity.vo.UavDetailVO;
import com.chinatower.device.mapper.UavDeviceMapper;
import com.chinatower.event.constants.Constants;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WLF
 * @description
 * @date 2025/5/20 9:43
 */
@Slf4j
@Service
public class AirConfigServiceImpl implements AirConfigService {
    @Resource
    private AirConfigMapper airConfigMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    private GisMapApi gisMapApi;
    @Resource
    private UavOkhttpUtil okhttpUtil;
    @Resource
    private UavDeviceMapper uavDeviceMapper;

    /**
     * 查询租户密码配置
     * @param airPwdReq
     * @return
     */
    @Override
    public Result getUavPwdConfig(AirPwdReq airPwdReq) {
        // 参数校验
        if (ObjectUtil.isEmpty(airPwdReq) ){
            return Result.fail("参数不能为空");
        }
        //查询租户是否配置了密码
        AirPwdRes airPwdRes = airConfigMapper.getUavPwdConfig(airPwdReq);
        AirPwdRes pwdRes = new AirPwdRes();
        if(ObjectUtil.isEmpty(airPwdRes)){
            pwdRes.setHavePwd(Constants.ZERO);
            pwdRes.setNeedPwd(Constants.ZERO);
        }else {
            //查询管理元名子
            com.chinatower.platform.model.dto.LoginUser loginUser = com.chinatower.platform.utils.SecurityUtils.getLoginUser();
            airPwdReq.setTenantId(loginUser.getUser().getTenantId());
            String name = airConfigMapper.getUavAdminName(airPwdReq);
            pwdRes.setAdminName(name);
            pwdRes.setHavePwd(Constants.ONE);
            // 检查是否有免密缓存
            String freeKey = "uav:pwd:free:" + loginUser.getUser().getUserId() + loginUser.getUser().getIndustryCode() + airPwdReq.getDeviceCode();
            if (redisTemplate.hasKey(freeKey)) {
                pwdRes.setNeedPwd(Constants.ONE);
            } else {
                pwdRes.setNeedPwd(Constants.ZERO);
            }
        }
        return Result.data(pwdRes);
    }

    /**
     * 校验无人机密码
     * @param airPwdReq
     * @return
     */
    @Override
    public Result checkUavPwd(AirPwdReq airPwdReq) {
        // 参数校验
        if (ObjectUtil.isEmpty(airPwdReq) || StrUtil.isBlank(airPwdReq.getDeviceCode())) {
            return Result.fail("设备编码不能为空");
        }
        // 检查是否被锁定
        com.chinatower.platform.model.dto.LoginUser loginUser = com.chinatower.platform.utils.SecurityUtils.getLoginUser();
        String lockKey = "uav:pwd:lock:" + loginUser.getUser().getUserId() + loginUser.getUser().getIndustryCode() + airPwdReq.getDeviceCode();
        AirPwdRes result = new AirPwdRes();
        if (redisTemplate.hasKey(lockKey)) {
            Long expire = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            result.setIsCheck(Constants.ONE);
            result.setErrorStr("已锁定该无人机，请稍后再试");
            return Result.data(result);
        }
        // 查询租户配置的密码
        AirPwdRes airPwdRes = airConfigMapper.getUavPwdConfig(airPwdReq);
        if (ObjectUtil.isEmpty(airPwdRes)) {
            // 没有配置密码，默认通过
            result.setIsCheck(Constants.ZERO);
            return Result.data(result);
        }
        // 错误次数key
        String errorKey = "uav:pwd:error:" + loginUser.getUser().getUserId() + loginUser.getUser().getIndustryCode() + airPwdReq.getDeviceCode();
        String freeKey = "uav:pwd:free:" + loginUser.getUser().getUserId() + loginUser.getUser().getIndustryCode() + airPwdReq.getDeviceCode();
        // 校验密码是否正确
        if (SecurityUtils.encryptPassword(airPwdReq.getPassword()).equals(airPwdRes.getPassword())) {
            // 密码正确
            result.setIsCheck(Constants.ZERO);
            // 清除错误计数
            redisTemplate.delete(errorKey);
            // 设置免密缓存（假设免密有效期为30分钟）
            redisTemplate.opsForValue().set(freeKey, "1", 30, TimeUnit.MINUTES);
        } else {
            // 密码错误
            result.setIsCheck(Constants.ONE);
            redisTemplate.delete(freeKey);
            // 增加错误计数
            Long errorCount = redisTemplate.opsForValue().increment(errorKey);
            if (errorCount == 1) {
                // 第一次错误，设置过期时间为1小时
                redisTemplate.expire(errorKey, 1, TimeUnit.HOURS);
            }
            int lockTime = Integer.parseInt(airPwdRes.getLockTime());
            TimeUnit timeUnit = "1".equals(airPwdRes.getLockTimeUnit()) ? TimeUnit.MINUTES :
                    "2".equals(airPwdRes.getLockTimeUnit()) ? TimeUnit.SECONDS : TimeUnit.HOURS;
            // 获取配置的错误次数限制
            int errorLimit = Integer.parseInt(airPwdRes.getErrorTimesLimit());
            String timeUnitDesc = getTimeUnitDescription(timeUnit);
            int remainTimes = errorLimit - errorCount.intValue();
            if (remainTimes > 0) {
                result.setErrorStr("密码错误，输错" + remainTimes + "次后该无人机将锁定" + lockTime + timeUnitDesc);
            } else {
                // 达到错误次数限制，锁定账号
//                redisTemplate.opsForValue().set(lockKey, "1", lockTime, timeUnit);
                RedisUtil.setIfAbsent(lockKey, com.chinatower.airline.constants.Constants.LOCK, lockTime, timeUnit, RedisDBType.PRIMARY);                result.setErrorStr("已锁定该无人机，请稍后再试");
                redisTemplate.delete(errorKey);
            }
        }
        return Result.data(result);
    }

    // 工具方法：将TimeUnit转换为中文描述
    private String getTimeUnitDescription(TimeUnit timeUnit) {
        if (timeUnit == null) {
            return "";
        }
        switch (timeUnit) {
            case SECONDS:
                return "秒";
            case MINUTES:
                return "分钟";
            case HOURS:
                return "小时";
            default:
                return timeUnit.name();
        }
    }

    /**
     * 校验无人机缓存
     */
    @Override
    public boolean checkUavCache(String deviceCode) {
        // 参数校验
        if (StringUtils.isEmpty(deviceCode) ){
            return false;
        }
        //查询deviceCode对应的无人机编码
        String uavCode = airConfigMapper.getUavCodeByDeviceCode(deviceCode);
        com.chinatower.platform.model.dto.LoginUser loginUser = com.chinatower.platform.utils.SecurityUtils.getLoginUser();
        String lockKey = "uav:pwd:lock:" + loginUser.getUser().getUserId() + loginUser.getUser().getIndustryCode() + uavCode;
        if (redisTemplate.hasKey(lockKey)) {
            return false;
        }else {
            return true;
        }
    }


    @Override
    public Result queryRoadsNameMilepost(List<RoadsNameMilepostReq> roadsNameMilepostReqList) {
        List<Map<String, Object>> resList = new ArrayList<>();

        if (CollectionUtils.isEmpty(roadsNameMilepostReqList)) {
            return Result.data(resList);
        }

        for (RoadsNameMilepostReq roadsNameMilepostReq : roadsNameMilepostReqList) {
            PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
            req.setLocation(String.format("%s,%s", roadsNameMilepostReq.getLongitude(), roadsNameMilepostReq.getLatitude()));

            log.info("逆地理编码查询接口poiContraryGeocodingQuery req: {}", JSONObject.toJSONString(req));

            Result<List<PoiContraryGeocodingQueryRes>> result;
            try {
                result = gisMapApi.poiContraryGeocodingQuery(req);
            } catch (Exception e) {
                log.error("调用GIS地图API失败，请求参数={}", JSONObject.toJSONString(req), e);
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("photoId", roadsNameMilepostReq.getPhotoId());
                errorMap.put("roadInfo", "-");
                resList.add(errorMap);
                continue;
            }

            log.info("逆地理编码查询接口poiContraryGeocodingQuery resp: {}", JSONObject.toJSONString(result));


            String roadInfo = "-";
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                String address = result.getData().get(0).getAddress();

                // 桩号提取逻辑
                String stationNumber = airConfigMapper.queryMilepostByLocation(roadsNameMilepostReq);
                // 1.逆地理为空取默认值'-' 2.逆地理不为空，桩号为空取逆地理名称 3.逆地理不为空，桩号为非空的拼接
                if (ObjectUtil.isNull(address)){
                    roadInfo = "-";
                }else if (ObjectUtil.isNotNull(address) && ObjectUtil.isNull(stationNumber)) {
                    roadInfo = address;
                }
                if (ObjectUtil.isNotNull(address) && ObjectUtil.isNotNull(stationNumber) ) {
                    roadInfo = String.format("%s(%s桩号附近)", address, stationNumber);
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("photoId", roadsNameMilepostReq.getPhotoId());
            map.put("roadInfo", roadInfo);
            resList.add(map);
        }

        return Result.data(resList);
    }

    @Override
    public Result<List<FlightRadiusVo>> queryFlightRadiusByUavCode(List<String> uavCodeList) {
        // 参数校验
        if (CollectionUtils.isEmpty(uavCodeList)) {
            return Result.fail("无人机编码列表不能为空");
        }

        // 1. 根据无人机编码获取设备型号
        List<Map<String, String>> modelCodeList = airConfigMapper.queryModelCodeByUavCode(uavCodeList);
        if (CollectionUtils.isEmpty(modelCodeList)) {
            return Result.fail("未查询到无人机型号");
        }

        try {
            // 2. 调用飞控接口获取飞行时间数据
            Map<String, BigDecimal> flightTimeMap = getFlightTimeData();

            // 3. 计算飞行半径
            List<FlightRadiusVo> resultList = calculateFlightRadius(modelCodeList, flightTimeMap);

            return Result.data(resultList);
        } catch (Exception e) {
            log.error("查询无人机飞行半径失败", e);
            return Result.fail("查询失败");
        }
    }

    /**
     * @param aIrCarReq
     * @return
     */
    @Override
    public Result<UavDetailVO> queryUavCameraInfoByModelCode(AIrCarReq aIrCarReq) {
        UavDeviceDTO uavDeviceDTO = airConfigMapper.queryUavCameraInfoByModelCode(aIrCarReq.getCameraModeCode());
        List<String> codes = new ArrayList<>();
        // 通过无人机型号编码查询镜头类型
        Map<String, String> cameraModelMap = new HashMap<String, String>() {{
            put("210", "M3D");
            put("211", "M3TD");
            put("220", "M4D相机");
            put("221", "M4TD");
        }};
        String carCode = cameraModelMap.get(aIrCarReq.getCameraModeCode());
        codes.add(carCode);
        List<DeviceCameraModelVO> cameraModelVOList = uavDeviceMapper.queryUavCameraModelInfo(codes);
        UavDetailVO uavDetailVO = new UavDetailVO();
        uavDetailVO.setBaseInfo(uavDeviceDTO);
        uavDetailVO.setCameraModelVOList(cameraModelVOList);
        return Result.data(uavDetailVO);
    }

    /**
     * 调用飞控接口获取飞行时间数据
     */
    private Map<String, BigDecimal> getFlightTimeData() {
        NetConnectUtils.RequestParamter<AirDeviceInfoReq> parameter = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_ENDURANCE_TIME);
        parameter.setPath(path);

        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(parameter, null, null);

        if (resultEntity == null || resultEntity.getData() == null) {
            log.warn("飞控接口返回数据为空");
            return Collections.emptyMap();
        }

        // 使用TypeReference确保正确的泛型类型
        return JSON.parseObject(JSON.toJSONString(resultEntity.getData()),
                new TypeReference<Map<String, BigDecimal>>() {});
    }

    /**
     * 计算飞行半径
     */
    private List<FlightRadiusVo> calculateFlightRadius(List<Map<String, String>> modelCodeList,
                                                       Map<String, BigDecimal> flightTimeMap) {
        // 常量配置化
        final double BATTERY_FACTOR = 0.7;    // 电池系数 70%
        final double REMAINING_BATTERY = 1;   // 剩余电量 (当前假设为100%)
        final double FLIGHT_SPEED = 12;       // 飞行速度 12 m/s

        List<FlightRadiusVo> resultList = new ArrayList<>();

        for (Map<String, String> modelMap : modelCodeList) {
            String modelCode = modelMap.get("modelCode");
            String uavCode = modelMap.get("uavCode");

            double flightRadius = calculateSingleFlightRadius(modelCode, flightTimeMap,
                    BATTERY_FACTOR, REMAINING_BATTERY, FLIGHT_SPEED);

            FlightRadiusVo flightRadiusVo = new FlightRadiusVo();
            flightRadiusVo.setUavCode(uavCode);
            flightRadiusVo.setFlightRadius(flightRadius);
            flightRadiusVo.setModelCode(modelCode); // 可选：返回型号编码用于调试

            resultList.add(flightRadiusVo);
        }

        return resultList;
    }

    /**
     * 计算单个无人机的飞行半径
     */
    private double calculateSingleFlightRadius(String modelCode, Map<String, BigDecimal> flightTimeMap,
                                               double batteryFactor, double remainingBattery, double flightSpeed) {
        double flightRadius = 0.0; // 默认值

        if (flightTimeMap.containsKey(modelCode)) {
            BigDecimal maxFlightTime = flightTimeMap.get(modelCode);
            if (maxFlightTime != null && maxFlightTime.compareTo(BigDecimal.ZERO) > 0) {
                // 飞行半径 = 最大飞行时间（分钟） * 电池系数 * 剩余电量 * 飞行速度（m/s）
                flightRadius = maxFlightTime.doubleValue() * 60 * batteryFactor * remainingBattery * flightSpeed;
                // 保留两位小数
                flightRadius = Math.round(flightRadius * 100.0) / 100.0;
            }
        } else {
            log.debug("未找到型号 {} 对应的飞行时间数据", modelCode);
        }

        return flightRadius;
    }
}
