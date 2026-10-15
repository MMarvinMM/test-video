package com.chinatower.event.utils;


import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.TranslnglatDTO;
import com.chinatower.event.vo.alarm.AlarmPTZVo;
import com.chinatower.event.vo.entity.SuppressPointPTVO;
import com.chinatower.event.vo.entity.SuppressPointVO;
import com.chinatower.event.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


/**
 * @className: PTZUtils
 * @description: PTZ计算工具类
 * @author: li.xufu
 * @date: 2022/5/15
 * @requestID 需求单号及需求名称
 */
@Slf4j
public class PTZUtils {

    /**
     * @description: 告警压制区域绘图百分比坐标转化为PTZ
     * @author: li.xufu
     * @date: 2022/5/15
     * @param pointTemp
     * @param areaTemp
     * @param modelVo
     * @return: SuppressPointPTVo
     */
    public static SuppressPointPTVO pointTranformPT(SuppressPointVO pointTemp, VmAlarmAreaSuppressVO areaTemp, DeviceModelVO modelVo) {
        log.info("告警压制区域绘图百分比坐标转化为PTZ,百分比坐标点位值：{}", pointTemp.toString());
        Float percentX = pointTemp.getPercentX();
        Float percentY = pointTemp.getPercentY();
        String p0 = areaTemp.getPointsP();
        String t0 = areaTemp.getPointsT();
        String z0 = areaTemp.getPointsZ();
        double f0 = Double.valueOf(modelVo.getMinFocus());
        double c = Double.valueOf(modelVo.getPicSensorLength());
        double d = Double.valueOf(modelVo.getPicSensorWidth());
        // zoom为null 为空 为0  当1处理
        if(z0 == null || "".equals(z0) || Constants.ZERO_STRING.equals(z0) ||  Constants.ZERO_ZERO_STRING.equals(z0) || String.valueOf(Constants.ZERO_NUM).equals(z0)){
            z0 = "1";
        }
        double f = f0*Double.valueOf(z0);
        BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
        BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));
        log.info(Constants.FIELDOFVIEW_MSG, a, b);
        BigDecimal pc = ((BigDecimal.valueOf(percentX).subtract(BigDecimal.valueOf(0.5))).multiply(a) ).add(new BigDecimal(p0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        BigDecimal tc = ((BigDecimal.valueOf(0.5).subtract(new BigDecimal(percentY))).multiply(b) ).add(new BigDecimal(t0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        SuppressPointPTVO pointPTVo = new SuppressPointPTVO();
        pointPTVo.setSuppressPointId(pointTemp.getSuppressPointId());
        pointPTVo.setPointP(String.valueOf(pc));
        pointPTVo.setPointT(String.valueOf(tc));
        log.info("百分比坐标点位值：{},转义后的PTZ对应的：{}", pointTemp.toString(), pointPTVo.toString());
        return pointPTVo;
    }

    public static SuppressPointVO ptTranformPoint(SuppressPointPTVO pointPtTemp, VmAlarmAreaSuppressVO areaSuppressVo, DeviceModelVO modelVo) {
        log.info("告警压制区域绘图PTZ转化为百分比坐标,PTZ坐标点位值：{}", pointPtTemp.toString());
        String pointP = pointPtTemp.getPointP();
        String pointT = pointPtTemp.getPointT();
        String p0 = areaSuppressVo.getPointsP();
        String t0 = areaSuppressVo.getPointsT();
        String z0 = areaSuppressVo.getPointsZ();
        double f0 = Double.valueOf(modelVo.getMinFocus());
        double c = Double.valueOf(modelVo.getPicSensorLength());
        double d = Double.valueOf(modelVo.getPicSensorWidth());
        // zoom为null 为空 为0  当1处理
        if(z0 == null || "".equals(z0) || Constants.ZERO_STRING.equals(z0) || Constants.ZERO_ZERO_STRING.equals(z0) ||  String.valueOf(Constants.ZERO_NUM).equals(z0)){
            z0 = "1";
        }
        double f = f0*Double.valueOf(z0);
        BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
        BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));
        log.info(Constants.FIELDOFVIEW_MSG, a, b);
        BigDecimal uc = ((new BigDecimal(pointP).subtract(new BigDecimal(p0))).divide(a,Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP) ).add(BigDecimal.valueOf(0.5)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
        BigDecimal vc = ((new BigDecimal(pointT).subtract(new BigDecimal(t0))).divide(b,Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP) ).add(BigDecimal.valueOf(0.5)).setScale(Constants.FOUR_NUM,BigDecimal.ROUND_HALF_UP);
        SuppressPointVO pointVo = new SuppressPointVO();
        pointVo.setSuppressPointId(pointPtTemp.getSuppressPointId());
        pointVo.setPercentX(Float.valueOf(String.valueOf(uc)) );
        pointVo.setPercentY(Float.valueOf(String.valueOf(vc)) );
        log.info("PTZ点位值：{},转义后的百分比坐标点对应的：{}", pointPtTemp.toString(), pointVo.toString());
        return pointVo;
    }

    /**
     * @description: 告警定位ptz计算
     * @author: yongqi.han
     * @date: 2022/7/1
     * @param lnglat
     * @return: AlarmPTZVo
     */
    public static AlarmPTZVo ptzComputing(TranslnglatDTO lnglat) {
        String percentX = lnglat.getUx();
        String percentY = lnglat.getVy();
        String p0 = lnglat.getPan();
        String t0 = lnglat.getTilt();
        String z0 = lnglat.getZoom();
        double f0 = Double.valueOf(lnglat.getF0());
        double c = Double.valueOf(lnglat.getC());  //图像传感器长度
        double d = Double.valueOf(lnglat.getD());  //图像传感器宽度
        // zoom为null 为空 为0  当1处理
        if(z0 == null || "".equals(z0) || Constants.ZERO_STRING.equals(z0) ||  Constants.ZERO_ZERO_STRING.equals(z0) || String.valueOf(Constants.ZERO_NUM).equals(z0)){
            z0 = "1";
        }
        //1、摄像机告警时的水平视场角和垂直视场角计算
        double f = f0*Double.valueOf(z0);
        BigDecimal a = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(c/(2*f)) * 180 / Math.PI));
        BigDecimal b = BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(Math.atan(d/(2*f)) * 180 / Math.PI));
        log.info(Constants.FIELDOFVIEW_MSG, a, b);
        //2、告警点A点的PTZ计算
        BigDecimal pc = ((new BigDecimal(percentX).subtract(BigDecimal.valueOf(0.5))).multiply(a) ).add(new BigDecimal(p0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        BigDecimal tc = ((BigDecimal.valueOf(0.5).subtract(new BigDecimal(percentY))).multiply(b) ).add(new BigDecimal(t0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        if(pc.compareTo(new BigDecimal(0))<0){
            pc = pc.add(new BigDecimal(360)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        }else if(pc.compareTo(new BigDecimal(360))>=0){
            pc = pc.subtract(new BigDecimal(360)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        }

        AlarmPTZVo alarmPTZVo = new AlarmPTZVo();
        alarmPTZVo.setAlarmP(String.valueOf(pc));
        alarmPTZVo.setAlarmT(String.valueOf(tc));
        alarmPTZVo.setAlarmZ(z0);
        log.info("告警定位ptz转义入参值：{},转义后的PTZ对应的：{}", lnglat.toString(), alarmPTZVo.toString());
        return alarmPTZVo;
    }

}
