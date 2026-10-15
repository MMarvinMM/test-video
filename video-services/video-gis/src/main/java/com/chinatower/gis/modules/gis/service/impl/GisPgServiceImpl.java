package com.chinatower.gis.modules.gis.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.bdm.LayerClassifyAreaStatisticsVO;
import com.chinatower.common.core.dto.bdm.LayerSlzyStatisticsDTO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.CameraCoordRes;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.gis.GisFireCombustibleRes;
import com.chinatower.gis.api.entity.gis.GisFireGradeRes;
import com.chinatower.gis.api.entity.gis.GisSlopeAspectRes;
import com.chinatower.gis.api.entity.gis.GisVegetationDataRes;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.LayerInfo;
import com.chinatower.gis.modules.gis.mapper.GisPgMapper;
import com.chinatower.gis.modules.gis.mapper.LayerInfoMapper;
import com.chinatower.gis.modules.gis.mapper.PgLayerCommonMapper;
import com.chinatower.gis.modules.gis.service.GisPgService;
import com.chinatower.gis.modules.gis.service.PgService;
import com.chinatower.gis.utils.EqualityCheck;
import com.chinatower.gis.utils.Point;
import com.chinatower.gis.utils.StreamUtils;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.PGLayerTempEnums;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisPgServiceImpl implements GisPgService {

    private final GisPgMapper gisPgMapper;
    private final PgLayerCommonMapper pgLayerCommonMapper;
    private final LayerInfoMapper layerInfoMapper;
    private final SystemFeignClient systemFeignClient;
    private static final String LAYER_ERROR_MSG = "图层不存在";
    //平方千米
    private static final double PM = 0.000001;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Override
    public Integer altitudeHeightQuery(String lon, String lat) {
        try {
            List<Integer> h = gisPgMapper.altitudeHeightQuery(lon, lat);
            if (h == null || h.isEmpty()) {
                return 0;
            }
            List<Integer> list = h.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (!list.isEmpty()) {
                return list.get(0);
            }
        } catch (Exception e) {
            log.error("查询海拔高度错误", e);
        }
        return 0;
    }

    @Override
    public Result<List<AltitudesRes>> altitudeHeightQuerys(AltitudesReq req) {
        try {
            List<LonLat> lat = req.getLonLat();
            StringBuilder sb = new StringBuilder();
            sb.append("MULTIPOINT(");
            lat.forEach(lonLat -> sb.append(lonLat.getLon() + " " + lonLat.getLat()).append(","));
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            List<AltitudesRes> list = gisPgMapper.altitudeHeightQuerys(sb.toString());
            if (list == null || list.isEmpty()) {
                return returnError(req);
            }

            for (AltitudesRes res : list) {
                String[] check = res.getLonLat().replace("(", "").replace(")", "").split(" ");
                Point p1 = new Point(Double.parseDouble(check[0]), Double.parseDouble(check[1]));
                for (LonLat lonLat : lat) {
                    Point p2 = new Point(Double.parseDouble(lonLat.getLon()), Double.parseDouble(lonLat.getLat()));
                    if (EqualityCheck.arePointsEqual(p1, p2)) {
                        res.setDeviceId(lonLat.getDeviceId());
                        res.setLonLat(lonLat.getLon() + "," + lonLat.getLat());
                    }
                }
            }
            //deviceId,经纬度
            Map<String, String> deviceMap = StreamUtils.toMap(req.getLonLat(), lonLat -> lonLat.getDeviceId() == null ? "" : lonLat.getDeviceId(), lonLat -> lonLat.getLon() + "," + lonLat.getLat());
            //设备ID
            Map<String, List<AltitudesRes>> listMap = StreamUtils.groupByKey(list, AltitudesRes::getDeviceId);

            List<AltitudesRes> collect = new ArrayList<>();
            for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
                AltitudesRes res = new AltitudesRes();
                List<AltitudesRes> one = listMap.get(entry.getKey());
                if (one == null || one.isEmpty()) {
                    res.setAltitudesHeight(0);
                } else {
                    List<AltitudesRes> resList = one.stream().filter(altitudesRes -> altitudesRes.getAltitudesHeight() != null).collect(Collectors.toList());
                    res.setAltitudesHeight(resList.get(0).getAltitudesHeight());
                }
                res.setLonLat(entry.getValue());
                res.setDeviceId(entry.getKey());
                collect.add(res);
            }

            return Result.data(collect);
        } catch (Exception e) {
            log.error("批量查询海拔高度错误", e);
        }
        return returnError(req);
    }

    @Override
    public List<Map<String, Object>> tableNameList(TableNameListReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerId());
        if (layerInfo == null) {
            throw new CustomException(LAYER_ERROR_MSG);
        }
        //查询字段
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        if (CollectionUtils.isEmpty(fields)) {
            throw new CustomException("告警模板已被删除");
        }
        StringBuilder insertFields = new StringBuilder();
        String[] split = PgService.FIXED_FIELDS.split(",");
        fields.forEach(field -> {
            boolean b = Arrays.stream(split).distinct().anyMatch(s -> s.equals(field.getCode()));
            if (!b) {
                insertFields.append(field.getCode()).append(",");
            }
        });
        if (insertFields.length() >= 1) {
            insertFields.deleteCharAt(insertFields.length() - 1);
        }
        return pgLayerCommonMapper.tableNameList(req.getBsm(), layerInfo.getLayerName(), insertFields.toString(), (req.getPageNum() - 1) * req.getPageSize(), req.getPageSize());
    }

    /**
     * 获取摄像头信息
     *
     * @param camaraDeviceParamsInfoVO 设备查询参数
     */
    @Override
    public CameraCoordRes getCameraCoord(CamaraDeviceParamsInfoVO camaraDeviceParamsInfoVO) {
        log.info("get camera coord 查询到的设备信息为: {}", JSON.toJSONString(camaraDeviceParamsInfoVO));
        return gisPgMapper.getCameraCoord(camaraDeviceParamsInfoVO);
    }

    @Override
    public List<LayerSlzyStatisticsDTO> layerClassifyAreaStatistics(LayerClassifyAreaStatisticsVO vo) {
        return pgLayerCommonMapper.layerClassifyAreaStatistics(vo);
    }

    @Override
    public List<LayerXztbDlbmAreaRes> layerXztbDlbmAreaRes(LayerYearTableName vo) {
        //面积字段
        String areaCode = pgLayerCommonMapper.getLayerAreaField(PGLayerTempEnums.XZTB_CESHITABLE.getLayerType());
        if (StringUtils.isEmpty(areaCode)) {
            throw new CustomException("耕地类型未配置面积字段");
        }
        int count = gisPgMapper.selectTableNameCount(Collections.singletonList(vo.getTableName()));
        if (count <= 0) {
            throw new CustomException(LAYER_ERROR_MSG);
        }
        List<LayerXztbDlbmAreaRes> res = gisPgMapper.layerXztbDlbmAreaRes(vo.getTableName(), areaCode);
        return res;
    }

    @Override
    public LayerXztbGddbAreaRes layerXztbGddbAreaRes(LayerYearTableName vo) {
        //面积字段
        String areaCode = pgLayerCommonMapper.getLayerAreaField(PGLayerTempEnums.XZTB_CESHITABLE.getLayerType());
        if (StringUtils.isEmpty(areaCode)) {
            throw new CustomException("耕地类型未配置面积字段");
        }
        int count = gisPgMapper.selectTableNameCount(Collections.singletonList(vo.getTableName()));
        if (count <= 0) {
            throw new CustomException(LAYER_ERROR_MSG);
        }
        List<LayerXztbGddbAreaChildRes> childRes = gisPgMapper.layerXztbGddbAreaRes(vo.getTableName(), areaCode);
//        double totalCount = childRes.stream().map(LayerXztbGddbAreaChildRes::getArea).mapToDouble(value -> value).sum();
//        double totalMj = BigDecimalUtils.multiply(totalCount, PM);
//        double totalMj = totalCount;
        BigDecimal totalMj = new BigDecimal("0");
        List<LayerXztbGddbAreaChildRes> resultChildRes = new ArrayList<>();
        //低等底
        BigDecimal floorArea = new BigDecimal("0");
        //中等地
        BigDecimal middleArea = new BigDecimal("0");
        //高等地
        BigDecimal highArea = new BigDecimal("0");
        //优等地
        BigDecimal ampleArea = new BigDecimal("0");
        //平均耕地等别
        BigDecimal avgArea = new BigDecimal("0");
        BigDecimal oneHundred = new BigDecimal("100");
        BigDecimal qt = new BigDecimal("0");

        for (LayerXztbGddbAreaChildRes res : childRes) {
            avgArea = avgArea.add(BigDecimal.valueOf(res.getValue()).multiply(new BigDecimal(res.getArea())));
            totalMj = totalMj.add(new BigDecimal(res.getArea()));
            if (res.getValue() >= 1 && res.getValue() <= 4) {
                ampleArea = ampleArea.add(new BigDecimal(res.getArea()));
            }
            if (res.getValue() >= 5 && res.getValue() <= 8) {
                highArea = highArea.add(new BigDecimal(res.getArea()));
            }
            if (res.getValue() >= 9 && res.getValue() <= 12) {
                middleArea = middleArea.add(new BigDecimal(res.getArea()));
            }
            if (res.getValue() >= 13 && res.getValue() <= 15) {
                floorArea = floorArea.add(new BigDecimal(res.getArea()));
            }
        }
        if (totalMj.doubleValue() <= 0) {
            totalMj = totalMj.add(new BigDecimal("1"));
        }
        LayerXztbGddbAreaChildRes res_1 = new LayerXztbGddbAreaChildRes();
        res_1.setName("优等地");
        res_1.setArea(ampleArea.toString());
        resultChildRes.add(res_1);
        if (floorArea.doubleValue() <= 0 && middleArea.doubleValue() <= 0 && highArea.doubleValue() <= 0) {
            res_1.setRatio(oneHundred.toString());
        } else {
            res_1.setRatio(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(ampleArea.doubleValue(), totalMj.doubleValue()), BigDecimalUtils.DATA_10000), 2));
        }
        if (StringUtils.isNotEmpty(res_1.getRatio())) {
            qt = qt.add(new BigDecimal(res_1.getRatio()));
        }

        LayerXztbGddbAreaChildRes res_2 = new LayerXztbGddbAreaChildRes();
        res_2.setName("高等地");
        res_2.setArea(highArea.toString());
        resultChildRes.add(res_2);
        if (floorArea.doubleValue() <= 0 && middleArea.doubleValue() <= 0) {
            res_2.setRatio(oneHundred.subtract(qt).toString());
        } else {
            res_2.setRatio(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(highArea.doubleValue(), totalMj.doubleValue()), BigDecimalUtils.DATA_10000), 2));
        }
        if (StringUtils.isNotEmpty(res_2.getRatio())) {
            qt = qt.add(new BigDecimal(res_2.getRatio()));
        }

        LayerXztbGddbAreaChildRes res_3 = new LayerXztbGddbAreaChildRes();
        res_3.setName("中等地");
        res_3.setArea(middleArea.toString());
        resultChildRes.add(res_3);
        if (floorArea.doubleValue() <= 0) {
            res_3.setRatio(oneHundred.subtract(qt).toString());
        } else {
            res_3.setRatio(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(middleArea.doubleValue(), totalMj.doubleValue()), BigDecimalUtils.DATA_10000), 2));
        }
        if (StringUtils.isNotEmpty(res_3.getRatio())) {
            qt = qt.add(new BigDecimal(res_3.getRatio()));
        }

        LayerXztbGddbAreaChildRes res_4 = new LayerXztbGddbAreaChildRes();
        res_4.setName("低等地");
        res_4.setArea(floorArea.toString());
        if (floorArea.doubleValue() <= 0) {
            res_4.setRatio("0");
        } else {
            res_4.setRatio(oneHundred.subtract(qt).toString());
        }

        resultChildRes.add(res_4);
        LayerXztbGddbAreaRes res = new LayerXztbGddbAreaRes();
        res.setChildRes(resultChildRes);
        //平均耕地等别
        res.setAvgGddb(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(avgArea.doubleValue(), totalMj.doubleValue()), BigDecimalUtils.DATA_100), 1));
        return res;
    }

    @Override
    public List<LayerAnnualChangesRes> layerAnnualChangesRes(LayerAnnualChangesReq vo) {
        if (CollectionUtils.isEmpty(vo.getTableNames())) {
            throw new CustomException("查询数据不能为空");
        }
        vo.getTableNames().forEach(v -> {
            if (v.getYear() == null) {
                throw new CustomException("年份不能为空");
            }
            if (StringUtils.isEmpty(v.getTableName())) {
                throw new CustomException(v.getYear() + "年对应图层不能为空");
            }
        });
        List<String> tableNames = vo.getTableNames().stream().map(LayerYearTableName::getTableName).distinct().collect(Collectors.toList());
        int count = gisPgMapper.selectTableNameCount(tableNames);
        if (count <= 0) {
            throw new CustomException(LAYER_ERROR_MSG);
        }
        String areaCode = null;
        if (vo.getType() == 1) {
            areaCode = pgLayerCommonMapper.getLayerAreaField(PGLayerTempEnums.XZTB_CESHITABLE.getLayerType());
        } else if (vo.getType() == 2) {
            areaCode = pgLayerCommonMapper.getLayerAreaField(PGLayerTempEnums.YJJBNT_CESHITABLE.getLayerType());
        }
        if (StringUtils.isEmpty(areaCode)) {
            throw new CustomException("未配置面积");
        }
        List<LayerAnnualChangesRes> res = gisPgMapper.layerAnnualChangesRes(vo.getTableNames(), areaCode, vo.getType());
        return res;
    }

    @Override
    public GisSlopeAspectRes GisSlopeAspectRes(String lon, String lat) throws ExecutionException, InterruptedException {
        GisSlopeAspectRes res = new GisSlopeAspectRes();
        Map<String, String> config = getSlopeAndAspectConfig();
        Map<Integer, Double> params = new HashMap<>();
        ThreadParallelUtil.run(Arrays.asList(1,2),d->{
            if (d == 1){
                params.put(d,gisPgMapper.selectSlope(config.get(slope_table_name), lon, lat));
            }else {
                params.put(d,gisPgMapper.selectAspect(config.get(aspect_table_name), lon, lat));
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.DEFAULT_SINGLE);
        res.setSlope(params.get(1));
        res.setAspect(params.get(2));
        return res;
    }

    private static final String slope_table_name = "slope_table_name";
    private static final String aspect_table_name = "aspect_table_name";
    private static final String default_slope_table_name = "t_slope_part";
    private static final String default_aspect_table_name = "t_aspect_part";

    private Map<String, String> getSlopeAndAspectConfig() {
        Map<String, String> map = new HashMap<>();
        try {
            AjaxResult ajaxResult_1 = systemFeignClient.getConfValue(aspect_table_name);
            if ("200".equals(String.valueOf(ajaxResult_1.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult_1.get(AjaxResult.DATA_TAG)), SysConfig.class);
                String url = com.chinatower.platform.common.utils.StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
                map.put(aspect_table_name, url);
            }

            AjaxResult ajaxResult_2 = systemFeignClient.getConfValue(slope_table_name);
            if ("200".equals(String.valueOf(ajaxResult_2.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult_2.get(AjaxResult.DATA_TAG)), SysConfig.class);
                String key = com.chinatower.platform.common.utils.StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
                map.put(slope_table_name, key);
            }

            return map;
        } catch (Exception e) {
            log.error("查询高德地址错误", e);
        }
        map.put(slope_table_name, default_slope_table_name);
        map.put(aspect_table_name, default_aspect_table_name);
        return map;
    }

    @Override
    public GisFireCombustibleRes gisFireCombustibleRes(String lon, String lat) throws ExecutionException, InterruptedException {
        GisFireCombustibleRes res = gisPgMapper.selectGlobalLinCao(lon, lat);
        if (res == null) {
            res = new GisFireCombustibleRes();
        }

        GisSlopeAspectRes aspectRes = GisSlopeAspectRes(lon, lat);
        res.setSlope(aspectRes.getSlope());
        res.setAspect(aspectRes.getAspect());
        return res;
    }

    @Override
    public GisFireGradeRes gisFireGradeRes(String lon, String lat) throws ExecutionException, InterruptedException {
        GisFireCombustibleRes linCao = gisPgMapper.selectGlobalLinCao(lon, lat);
        if (linCao == null) {
            linCao = new GisFireCombustibleRes();
        }
        Integer altituded = this.altitudeHeightQuery(lon, lat);
        GisFireGradeRes res = new GisFireGradeRes();
        Integer fireLevel = gisPgMapper.selectFireLevel(lon, lat);
        res.setFireLevel(fireLevel);
        res.setPlantsSpecies(linCao.getFuelModelNumber());
        res.setAltitude(altituded);
        res.setPlantsDensity(linCao.getPlantsDensity());
        res.setPlantsDegreeOfDrought(linCao.getPlantsDegreeOfDrought());
        res.setAltitude(altituded);

        GisSlopeAspectRes aspectRes = GisSlopeAspectRes(lon, lat);
        res.setSlope(aspectRes.getSlope());
        res.setAspect(aspectRes.getAspect());
        return res;
    }

    @Override
    public List<GisVegetationDataRes> gisVegetationDataRes(String lon, String lat,Integer distance) {
        if (distance == null || distance <= 0){
            distance = 3000;
        }
        return gisPgMapper.gisVegetationDataRes(lon, lat,distance);
    }

    private Result<List<AltitudesRes>> returnError(AltitudesReq req) {
        List<AltitudesRes> collect = req.getLonLat().stream().map(lonLat -> {
            AltitudesRes res = new AltitudesRes();
            res.setAltitudesHeight(0);
            res.setDeviceId(lonLat.getDeviceId());
            res.setLonLat(lonLat.getLon() + "," + lonLat.getLat());
            return res;
        }).collect(Collectors.toList());
        return Result.data(collect);
    }
}
