package com.chinatower.receive.core.intercept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.chinatower.receive.vo.dto.LatLng;
import com.chinatower.receive.vo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @className: UavAreaShieldingSuppressionStrategy
 * @description: 压制方式-无人机区域屏蔽压制策略实现类
 * @author: lyy
 * @date: 2024/08/01 15:35
 */
@Component("uavAreaShieldingStrategy")
@Slf4j
public class UavAreaShieldingSuppressionStrategy extends BaseAlarmSuppressStategy {

    @Resource
    private StrategyService strategyService;

    @Override
    public boolean apply(ReceiveAlarmVO receiveAlarmVO, VmAlarmSuppressStrategyVO strategy) {
        log.info("UavAreaShieldingSuppressionStrategy-开始无人机区域屏蔽压制策略:{},{}",JSON.toJSONString(receiveAlarmVO),JSON.toJSONString(strategy));
        Boolean suppressFlag = false;
        LatLng alarmLatLng = new LatLng();
        if(!StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude())){
            alarmLatLng.setLatitude(Double.valueOf(receiveAlarmVO.getLatitude()));
            alarmLatLng.setLongitude(Double.valueOf(receiveAlarmVO.getLongitude()));
        }else{
            return suppressFlag;
        }
        if(!CollectionUtils.isEmpty(strategy.getUavAreaSuppressVoList())){
            for (VmAlarmUavAreaSuppressVO uavSupVo:strategy.getUavAreaSuppressVoList()) {
                if(!StringUtils.isEmpty(uavSupVo.getPoint())){
                    log.info("压制区域经纬度点位:{}",uavSupVo.getPoint());
                    List<List<LatLng>> list = parseJsonToLngLatList(uavSupVo.getPoint());
                    log.info("压制区域经纬度点位集合:{}",JSON.toJSONString(list));
                    for (List<LatLng> listLatLng:list) {
                        boolean flag = isPointInPolygon(alarmLatLng,listLatLng);
                        if(flag){
                            log.info("被无人机告警区域压制-压制配置:{}",JSON.toJSONString(strategy));
                            receiveAlarmVO.setIntercept(AlgorithmEventVO.FOR_AREA);
                            return true;
                        }
                    }
                }
            }

        }
        return suppressFlag;
    }
    /**
     * FastJSON解析JSON字符串为 List<List<LngLat>>
     * @param jsonStr 输入的JSON字符串
     * @return 解析后的嵌套List对象
     */
    public List<List<LatLng>> parseJsonToLngLatList(String jsonStr) {
        // FastJSON可直接通过TypeReference指定嵌套泛型类型
        return JSON.parseObject(
                jsonStr,
                new com.alibaba.fastjson.TypeReference<List<List<LatLng>>>() {}
        );
    }

    @Override
    public int getOrder() {
        return Constants.THREE_NUM;
    }

    @Override
    public boolean getStrategyBeanName(String stategyType) {
        return StrategyTypeEnum.UAV_AREA_SHIELDING_STRATEGY.getDisplayName().equals(stategyType);
    }


    /**
     * 判断一个经纬度点是否在多边形区域内
     *
     * @param point 要判断的经纬度点
     * @param polygon 多边形区域的顶点列表（需按顺时针或逆时针顺序排列）
     * @return 如果点在区域内（包括边界），返回true；否则返回false
     */
    public boolean isPointInPolygon(LatLng point, List<LatLng> polygon) {
        // 多边形顶点数量不足3个，无法构成有效区域
        if (polygon == null || polygon.size() < 3) {
            return false;
        }

        double pointLng = point.getLongitude(); // 点的经度
        double pointLat = point.getLatitude();  // 点的纬度
        int vertexCount = polygon.size();
        boolean isInside = false;

        for (int i = 0, j = vertexCount - 1; i < vertexCount; j = i++) {
            LatLng vertexI = polygon.get(i);
            LatLng vertexJ = polygon.get(j);

            double lngI = vertexI.getLongitude();
            double latI = vertexI.getLatitude();
            double lngJ = vertexJ.getLongitude();
            double latJ = vertexJ.getLatitude();

            // 检查点是否在多边形的边上
            if (isPointOnEdge(point, vertexI, vertexJ)) {
                return true;
            }

            // 射线法核心判断逻辑
            // 1. 检查点的纬度是否在当前边的两个顶点纬度之间
            // 2. 计算射线与边的交点经度，判断是否在点的经度右侧
            if (((latI > pointLat) != (latJ > pointLat)) &&
                    (pointLng < (lngJ - lngI) * (pointLat - latI) / (latJ - latI) + lngI)) {
                isInside = !isInside;
            }
        }

        return isInside;
    }

    /**
     * 判断点是否在多边形的边上
     */
    private boolean isPointOnEdge(LatLng point, LatLng edgeStart, LatLng edgeEnd) {
        double pointLng = point.getLongitude();
        double pointLat = point.getLatitude();
        double startLng = edgeStart.getLongitude();
        double startLat = edgeStart.getLatitude();
        double endLng = edgeEnd.getLongitude();
        double endLat = edgeEnd.getLatitude();

        // 1. 检查点是否在边的 bounding box 范围内
        if (pointLng < Math.min(startLng, endLng) - 1e-9 ||
                pointLng > Math.max(startLng, endLng) + 1e-9 ||
                pointLat < Math.min(startLat, endLat) - 1e-9 ||
                pointLat > Math.max(startLat, endLat) + 1e-9) {
            return false;
        }

        // 2. 检查点是否在直线上（使用向量叉乘）
        // 叉乘结果为0表示三点共线
        double crossProduct = (pointLng - startLng) * (endLat - startLat)
                - (pointLat - startLat) * (endLng - startLng);

        // 考虑浮点数精度误差，使用极小值作为阈值
        return Math.abs(crossProduct) < 1e-9;
    }

    /*public static void main(String[] args) {
        // 1. 创建多边形区域（例如：一个四边形区域）
        List<LatLng> polygon = new ArrayList<>();
        polygon.add(new LatLng(39.9042, 116.4074));  // 点1
        polygon.add(new LatLng(39.9042, 116.5074));  // 点2
        polygon.add(new LatLng(39.8042, 116.5074));  // 点3
        polygon.add(new LatLng(39.8042, 116.4074));  // 点4（闭合多边形）

        // 2. 创建待判断的点
        LatLng pointInArea = new LatLng(39.8542, 116.4574);  // 区域内的点
        LatLng pointOutOfArea = new LatLng(40.0042, 116.4574); // 区域外的点
        LatLng pointOnEdge = new LatLng(39.9042, 116.4574);   // 边界上的点

        // 3. 执行判断
        boolean inArea = isPointInPolygon(pointInArea, polygon);
        boolean outOfArea = isPointInPolygon(pointOutOfArea, polygon);
        boolean onEdge = isPointInPolygon(pointOnEdge, polygon);

        System.out.println("点是否在区域内: " + inArea);       // 输出 true
        System.out.println("点是否在区域外: " + !outOfArea);  // 输出 true
        System.out.println("点是否在边界上: " + onEdge);
    }*/
}
