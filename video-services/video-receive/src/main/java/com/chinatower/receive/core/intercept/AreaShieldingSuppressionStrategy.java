package com.chinatower.receive.core.intercept;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.receive.core.util.StrategyTypeEnum;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.StrategyService;
import com.chinatower.receive.util.PTZUtils;
import com.chinatower.receive.vo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @className: AreaShieldingSuppressionStrategy
 * @description: 压制方式-区域屏蔽压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("areaShieldingStrategy")
@Slf4j
public class AreaShieldingSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Resource
    private DeviceRepository deviceRepository;

    @Resource
    private StrategyService strategyService;

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        Boolean suppressFlag = false;
        if(strategy.getAreaSuppressVoList()!=null && !strategy.getAreaSuppressVoList().isEmpty()){
            suppressFlag = doInterceptByArea(strategy,receiveAlarmVO);
        }
        return suppressFlag;
    }

    @Override
    public int getOrder() {
        return Constants.THREE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.AREA_SHIELDING_STRATEGY.getDisplayName().equals(stategyType);
    }

    private boolean doInterceptByArea(VmAlarmSuppressStrategyVO item, ReceiveAlarmVO receiveAlarmVO) {
        // 该告警策略下所有匹配的压制区域
        List<VmAlarmAreaSuppressVO> areaSuppressVoList = item.getAreaSuppressVoList();

        //DeviceRepository deviceRepository = SpringUtil.getBean(DeviceRepository.class);
        log.info("请求设备配置，deviceCode: {}", receiveAlarmVO.getDeviceCode());
        if(deviceRepository != null){
            log.info("doInterceptByArea-is not null");
        }else{
            log.info("doInterceptByArea-isnull");
        }
        SysLntlagConfigVO config = deviceRepository.getLntlagConfig(receiveAlarmVO.getDeviceCode());
        if (config == null) {
            return false;
        }
        // 设备厂商对应参数信息不全则跳过区域屏蔽压制
        if (!Objects.isNull(config) && !Objects.isNull(config.getF0()) && !Objects.isNull(config.getC()) && !Objects.isNull(config.getD())) {
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            deviceModelVO.setMinFocus(config.getF0());
            deviceModelVO.setPicSensorLength(config.getC());
            deviceModelVO.setPicSensorWidth(config.getD());
            deviceModelVO.setMfgrCode(config.getModelId());
            deviceModelVO.setFocusRange(config.getFocusRange());
            deviceModelVO.setVertiDownNeg(config.getVertiDownNeg());

            String pan = LongLatAngleUtils.paramTrans(receiveAlarmVO.getPan(), deviceModelVO, Constants.TYPE_P);
            String tile = LongLatAngleUtils.paramTrans(receiveAlarmVO.getTilt(), deviceModelVO, Constants.TYPE_T);
            String zoom = LongLatAngleUtils.paramTrans(receiveAlarmVO.getZoom(), deviceModelVO, Constants.TYPE_Z);
            // zoom为null 为空 为0  当1处理
            if(zoom == null || "".equals(zoom) || Constants.ZERO_STRING.equals(zoom) || Constants.ZERO_ZERO_STRING.equals(zoom) ||  String.valueOf(Constants.ZERO_NUM).equals(zoom)){
                zoom = "1";
            }
            VmAlarmAreaSuppressVO alarmAreaSuppressVo = new VmAlarmAreaSuppressVO();
            alarmAreaSuppressVo.setPointsP(pan);
            alarmAreaSuppressVo.setPointsT(tile);
            alarmAreaSuppressVo.setPointsZ(zoom);

            boolean flag = false;
            for (VmAlarmAreaSuppressVO vmAlarmAreaSuppressVo : areaSuppressVoList) {
                // 获取压制区域的所有点位  PT
                List<SuppressPointPTVO> pointPTVos = JSON.parseArray(vmAlarmAreaSuppressVo.getPointsPt(), SuppressPointPTVO.class);
                if(pointPTVos == null || pointPTVos.size() < 3 ){   //没有查到标绘点位或者点位不足三个则不进行区域压制判断
                    // 未获取到压制区域的PT坐标信息 或者信息有误 则尝试重新获取
                    log.info("未获取到该压制区域的pointPT:{}",vmAlarmAreaSuppressVo);

                    List<SuppressPointVO> pointsList = JSON.parseArray(Constants.PARAM_NULL.equals(vmAlarmAreaSuppressVo.getPoints())?"":vmAlarmAreaSuppressVo.getPoints(), SuppressPointVO.class);
                    if(Constants.ONE.equals(vmAlarmAreaSuppressVo.getAreaType()) && pointsList!=null && !pointsList.isEmpty()){

                        DeviceModelVO modelVo = new DeviceModelVO();
                        modelVo.setMinFocus(config.getF0());
                        modelVo.setPicSensorLength(config.getC());
                        modelVo.setPicSensorWidth(config.getD());

                        List<SuppressPointPTVO> listPT = new ArrayList<>();
                        for(SuppressPointVO pointTemp:pointsList){
                            SuppressPointPTVO pointPTVo = PTZUtils.pointTranformPT(pointTemp,vmAlarmAreaSuppressVo,modelVo);
                            listPT.add(pointPTVo);
                        }
                        String jsonPointPT = JSON.toJSONString(listPT);
                        vmAlarmAreaSuppressVo.setPointsPt(jsonPointPT);
                    }else if(Constants.TWO.equals(vmAlarmAreaSuppressVo.getAreaType())){
                        strategyService.groupPT(vmAlarmAreaSuppressVo);
                        if(StringUtils.isEmpty(vmAlarmAreaSuppressVo.getPointsPt())){
                            continue;
                        }
                    }else{
                        continue;
                    }
                    // 重新获取后重新验证
                    pointPTVos = JSON.parseArray(vmAlarmAreaSuppressVo.getPointsPt(), SuppressPointPTVO.class);
                    log.info("重新获取到该压制区域的pointPT:{}",vmAlarmAreaSuppressVo);
                    if(pointPTVos == null || pointPTVos.size() < 3 ){
                        continue;
                    }
                }
                StringBuilder suppressPointPt = new StringBuilder();
                suppressPointPt.append(Constants.PARAM_POLYGON).append("((");
                for (SuppressPointPTVO suppressPointPTVo : pointPTVos) {
                    suppressPointPt.append(suppressPointPTVo.getPointP()).append(" ").append(suppressPointPTVo.getPointT()).append(Constants.COMMA);
                }
                suppressPointPt = new StringBuilder(suppressPointPt.substring(0,suppressPointPt.length()-1));
                suppressPointPt.append("))");
                log.info("告警编码:{},压制区域点位拼接：{}",receiveAlarmVO.getAlarmCode(),suppressPointPt);



                // 循环转义坐标为PTZ 若框图存在获取框图的所有点位
                StringBuilder pointPtIcon = new StringBuilder();
                // 告警框图点位 x y
                boolean isHasIcon = false;

                if(receiveAlarmVO.getAlarmArea() != null && !receiveAlarmVO.getAlarmArea().isEmpty()) {
                    // 获取置信度最大的告警区域的框图点位
                    AlarmOriginalAreaVO alarmOriginalAreaVO = receiveAlarmVO.getAlarmArea().stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO());
                    if(alarmOriginalAreaVO.getAlarmPointList() != null && alarmOriginalAreaVO.getAlarmPointList().size() > 2){
                        List<AlarmOriginalPointVO> alarmPointList = alarmOriginalAreaVO.getAlarmPointList();
                        pointPtIcon.append(Constants.PARAM_POLYGON).append("((");
                        String pointPOne = "";
                        String pointTOne = "";
                        for (AlarmOriginalPointVO alarmOriginalPointVO : alarmPointList) {
                            SuppressPointVO suppressPointVo = new SuppressPointVO();
                            suppressPointVo.setSuppressPointId(alarmOriginalPointVO.getAlarmPointId());
                            suppressPointVo.setPercentX(Float.parseFloat(alarmOriginalPointVO.getPercentX()));
                            suppressPointVo.setPercentY(Float.parseFloat(alarmOriginalPointVO.getPercentY()));

                            SuppressPointPTVO suppressPointPTVo = PTZUtils.pointTranformPT(suppressPointVo, alarmAreaSuppressVo, deviceModelVO);
                            if(alarmPointList.indexOf(alarmOriginalPointVO) == 0){
                                pointPOne = suppressPointPTVo.getPointP();
                                pointTOne = suppressPointPTVo.getPointT();
                            }
                            pointPtIcon.append(suppressPointPTVo.getPointP()).append(" ").append(suppressPointPTVo.getPointT()).append(Constants.COMMA);
                        }
                        // 重新增加第一个点位 形成闭环图形
                        pointPtIcon.append(pointPOne).append(" ").append(pointTOne).append("))");
                        isHasIcon = true;
                    }
                }
                log.info("告警编码:{},告警框图点位拼接：{}",receiveAlarmVO.getAlarmCode(),pointPtIcon);
                // 获取告警区域与框图区域相交面积
                GeometryFactory geometryFactory = new GeometryFactory();
                WKTReader reader = new WKTReader(geometryFactory);
                try {
                    Polygon iconArea = (Polygon) reader.read(pointPtIcon.toString());
                    Polygon suppressArea = (Polygon) reader.read(suppressPointPt.toString());
                    if (!suppressArea.isValid()) { //若压制区域为不规则几何图形，则进行拓扑修复
                        log.info("多边形无效，原因: {}，修复前面积: {}", new IsValidOp(suppressArea).getValidationError().getMessage(), suppressArea.getArea());
                        suppressArea = (Polygon) suppressArea.buffer(0); // 自动修复拓扑，存在略微的面积精度丢失
                        log.info("修复后是否有效: {}，修复后面积: {},修复后的数据为 {}", suppressArea.isValid(),suppressArea.getArea(),suppressArea);
                    }
                    if(isHasIcon && iconArea != null){  //存在框图  则对比重叠面积 对比覆盖率  完全覆盖完全被覆盖都要压制
                        OverlayOp op = new OverlayOp(iconArea,suppressArea);
                        Geometry g = op.getResultGeometry(OverlayOp.INTERSECTION);
                        double overlapArea = g.getArea();
                        BigDecimal scalingRate = BigDecimal.valueOf(overlapArea).divide(BigDecimal.valueOf(iconArea.getArea()),Constants.TWO_NUM,BigDecimal.ROUND_HALF_UP);
                        log.info("告警区域压制，alarmCode:{},框图与压制区域的重叠面积:{},框图面积:{},压制区域面积:{},比例:{}",receiveAlarmVO.getAlarmCode(),overlapArea,iconArea.getArea(),suppressArea.getArea(),scalingRate);
                        if(scalingRate.compareTo(new BigDecimal(vmAlarmAreaSuppressVo.getOverlapRate())) > -1 || overlapArea == iconArea.getArea() || overlapArea == suppressArea.getArea()){
                            flag = true;
                            break;
                        }
                    }else{
                        Coordinate coordinate = new Coordinate(Double.parseDouble(pan),Double.parseDouble(tile));
                        Point point = geometryFactory.createPoint(coordinate);
                        log.info("告警区域压制，alarmCode:{},点位实体:{},PT值:{}",receiveAlarmVO.getAlarmCode(),point,pan+Constants.COMMA+tile);
                        if(point.within(suppressArea) || point.intersects(suppressArea)){
                            flag = true;
                            break;
                        }
                    }
                } catch (ClassCastException e){
                    // 压制区域自动修复后为多个图形，则按照多图形逻辑进行计算
                    try {
                        //计算多边形重叠面积逻辑
                        flag = polygonCalculation(flag,reader,pointPtIcon,suppressPointPt,isHasIcon,receiveAlarmVO,vmAlarmAreaSuppressVo,pan,tile,geometryFactory);
                        if(flag){
                            break;
                        }
                    } catch (Exception e2){
                        log.info("多边形计算逻辑出错：", e);
                    }

                }catch (Exception e) {
                    log.info("通过坐标获取告警框图面积以及重叠区域面积出错: ",e);
                }
            }
            if(flag){
                receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_AREA);
                log.info("告警编码:{},区域告警压制",receiveAlarmVO.getAlarmCode());
                return true;
            }
        }
        return false;
    }

    /**
     * 计算告警压制区域为多个多边形的重叠面积逻辑
     * @param flag
     * @param reader
     * @param pointPtIcon
     * @param suppressPointPt
     * @param isHasIcon
     * @param receiveAlarmVO
     * @param vmAlarmAreaSuppressVo
     * @param pan
     * @param tile
     * @param geometryFactory
     * @return
     * @throws ParseException
     */
    public boolean polygonCalculation(boolean flag, WKTReader reader, StringBuilder pointPtIcon, StringBuilder suppressPointPt,
                                      boolean isHasIcon, ReceiveAlarmVO receiveAlarmVO, VmAlarmAreaSuppressVO vmAlarmAreaSuppressVo, String pan, String tile, GeometryFactory geometryFactory) throws ParseException {
        Polygon iconArea = (Polygon) reader.read(pointPtIcon.toString());
        Polygon suppressArea = (Polygon) reader.read(suppressPointPt.toString());
        MultiPolygon suppressAreaMulti = null;
        if (!suppressArea.isValid()) {
            log.info("多边形无效，原因: {}，修复前面积: {}", new IsValidOp(suppressArea).getValidationError().getMessage(), suppressArea.getArea());
            suppressAreaMulti = (MultiPolygon) suppressArea.buffer(0); // 自动修复拓扑，存在略微的面积精度丢失
            log.info("修复后是否有效: {}，修复后面积: {},修复后的数据为 {}", suppressArea.isValid(), suppressArea.getArea(), suppressArea);
        }
        Geometry multiPolygonGeometry = reader.read(suppressAreaMulti.toString());
        if (multiPolygonGeometry instanceof MultiPolygon) {
            MultiPolygon multiPolygon = (MultiPolygon) multiPolygonGeometry;
            if (isHasIcon && iconArea != null) {
                // 获取重叠总面积
                double totalArea = 0;
                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                    Geometry geometry = multiPolygon.getGeometryN(i);
                    if (geometry instanceof Polygon) {
                        Polygon polygon = (Polygon) geometry;
                        OverlayOp op = new OverlayOp(iconArea, polygon);
                        Geometry g = op.getResultGeometry(OverlayOp.INTERSECTION);
                        double overlapArea = g.getArea();
                        totalArea = totalArea + overlapArea;
                        log.info("处理第 " + (i + 1) + " 个多边形: " + polygon + "，重叠面积：" + overlapArea);
                    }
                }
                if (!Double.isNaN(totalArea) && totalArea > 0) {
                    BigDecimal scalingRate = BigDecimal.valueOf(totalArea).divide(BigDecimal.valueOf(iconArea.getArea()), Constants.TWO_NUM, BigDecimal.ROUND_HALF_UP);
                    log.info("告警区域压制，alarmCode:{},框图与压制区域的重叠面积:{},框图面积:{},压制区域面积:{},比例:{}", receiveAlarmVO.getAlarmCode(), totalArea, iconArea.getArea(), suppressArea.getArea(), scalingRate);
                    if (scalingRate.compareTo(new BigDecimal(vmAlarmAreaSuppressVo.getOverlapRate())) > -1 || totalArea == iconArea.getArea() || totalArea == suppressArea.getArea()) {
                        flag = true;
                    }
                } else {
                    log.info("重叠总面积为空，则不压制");
                }
            } else {//不存在框图，只处理当前点位
                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                    Geometry geometry = multiPolygon.getGeometryN(i);
                    Coordinate coordinate = new Coordinate(Double.parseDouble(pan), Double.parseDouble(tile));
                    Point point = geometryFactory.createPoint(coordinate);
                    log.info("告警区域压制，alarmCode:{},点位实体:{},PT值:{}", receiveAlarmVO.getAlarmCode(), point, pan + Constants.COMMA + tile);
                    if (point.within(geometry) || point.intersects(geometry)) {
                        flag = true;
                        break;
                    }
                }
            }

        }
        return flag;
    }
}
