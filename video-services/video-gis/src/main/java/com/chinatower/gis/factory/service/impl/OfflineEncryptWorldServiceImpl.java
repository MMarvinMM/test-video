package com.chinatower.gis.factory.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.JsonUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.api.entity.DriveNavigationPaths;
import com.chinatower.gis.api.entity.DriveNavigationRes;
import com.chinatower.gis.api.entity.DriveNavigationSteps;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.gis.utils.AESUtil;
import com.chinatower.gis.utils.PositionUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.SysCapitalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @desription: 高德离线加密请求
 * @data 创建时间：2024/7/16 10:37
 * @author chuan hao
 * @version 1.0
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class OfflineEncryptWorldServiceImpl extends AbstractPoiBase implements GisMapService {

    private final OfflineWorldServiceImpl offlineWorldService;

    @Override
    public PoiKeyWordQueryRes poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiKeyWordQuery(req);
    }

    @Override
    public PoiRadiusQueryResTableInfo poiRadiusQuery(PoiRadiusQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiRadiusQuery(req);
    }

    @Override
    public List<PoiPolygonQueryRes> poiPolygonQuery(PoiPolygonQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiPolygonQuery(req);
    }

    @Override
    public List<PoiGeocodingQueryRes> poiGeocodingQuery(PoiGeocodingQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiGeocodingQuery(req);
    }

    @Override
    public List<PoiContraryGeocodingQueryRes> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiContraryGeocodingQuery(req);
    }

    @Override
    public PoiAreaQueryRes poiAreaQuery(PoiAreaQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.poiAreaQuery(req);
    }

    @Override
    public TrajectoryQueryRes trajectoryQuery(TrajectoryQueryReq req) {
        req.setEncryptRequestFlag(true);
        return offlineWorldService.trajectoryQuery(req);
    }
}
