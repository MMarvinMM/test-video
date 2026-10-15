package com.chinatower.event.utils;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.TranslnglatDTO;
import com.chinatower.event.vo.TranslnglatVO;
import com.chinatower.event.vo.alarm.AlarmPTZVo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @className: AlarmLocationUtils
 * @description: 告警定位计算方法
 * @author: yongqi.han
 * @date: 2022/7/1
 * @requestID 需求单号及需求名称
 */
@Slf4j
public class AlarmLocationUtils {

    public static TranslnglatVO getAlarmLocation(TranslnglatDTO lnglat){
        TranslnglatVO translnglatVO = new TranslnglatVO();
        TranslnglatVO translnglat = new TranslnglatVO();
        //1、告警点PTZ计算
        AlarmPTZVo alarmPTZVo = PTZUtils.ptzComputing(lnglat);
        if(alarmPTZVo!=null){
            //2、摄像机到告警点A的水平角和俯仰角计算
            try{
                angleComputing(alarmPTZVo,lnglat,translnglat);
            }catch(Exception e){
                log.info("告警点A的水平角和俯仰角计算异常：{}",e);
                translnglatVO.setErrCode(Constants.ERROR_CODE_LNGLAT1002+":告警点A的水平角和俯仰角计算异常");
                return translnglatVO;
            }
            //3、告警点A的经纬度计算
            try{
                translnglatVO = translnglat(alarmPTZVo,lnglat);
            }catch(Exception e){
                log.info("告警点A的经纬度计算异常：{}",e);
                translnglatVO.setErrCode(Constants.ERROR_CODE_LNGLAT1003+":告警点A的经纬度计算异常");
                return translnglatVO;
            }
            translnglatVO.setDevicePitch(translnglat.getDevicePitch());
            return translnglatVO;
        }else{
            log.info("告警点PTZ计算为空，AlarmLocationUtils入参：{}",lnglat.toString());
            translnglatVO.setErrCode(Constants.ERROR_CODE_LNGLAT1001+":告警点PTZ计算为空");
            return translnglatVO;
        }
    }

    /**
     * @description: 计算告警定位经纬度
     * @param alarmPTZVo
     * @param lnglat
     * @return: void
     */
    private static TranslnglatVO translnglat(AlarmPTZVo alarmPTZVo, TranslnglatDTO lnglat) {
        Double longitudeA;
        Double latitudeA;
        Double longitude = Double.valueOf(lnglat.getLongitude());
        Double latitude = Double.valueOf(lnglat.getLatitude());
        Double flagA = Double.valueOf(alarmPTZVo.getFlatA()) * Math.PI / 180;
        Double towerLength = Double.valueOf(alarmPTZVo.getTowerLength());
        if(flagA<Math.PI/2){
            longitudeA = BigDecimal.valueOf(towerLength*Math.sin(flagA)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
            latitudeA = BigDecimal.valueOf(towerLength*Math.sin(Math.PI/2-flagA)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else if(flagA<Math.PI){
            longitudeA = BigDecimal.valueOf(towerLength*Math.sin(Math.PI-flagA)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
            latitudeA =  BigDecimal.valueOf(towerLength*Math.sin(flagA-Math.PI/2)).multiply(new BigDecimal("-1")).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else if(flagA<3*Math.PI/2){
            longitudeA =  BigDecimal.valueOf(towerLength*Math.sin(flagA-Math.PI)).multiply(new BigDecimal("-1")).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
            latitudeA =  BigDecimal.valueOf(towerLength*Math.sin(3*Math.PI/2-flagA)).multiply(new BigDecimal("-1")).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else{
            longitudeA =  BigDecimal.valueOf(towerLength*Math.sin(2*Math.PI-flagA)).multiply(new BigDecimal("-1")).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
            latitudeA = BigDecimal.valueOf(towerLength*Math.sin(flagA-3*Math.PI/2)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        log.info("经度值差值：{}，纬度值差值：{}",longitudeA ,latitudeA );
        double longitudeDouble = longitude + lngLength(longitudeA, latitude);
        double latitudeDouble = latitude + latLength(latitudeA);
        log.info("原始经纬度值：{}，计算后的经纬度值：{}",longitude+":"+latitude ,longitudeDouble +":"+ latitudeDouble );
        TranslnglatVO translnglatVO = new TranslnglatVO();
        BeanUtil.copyProperties(alarmPTZVo,translnglatVO);
        translnglatVO.setLongitude(String.valueOf(longitudeDouble));
        translnglatVO.setLatitude(String.valueOf(latitudeDouble));
        return translnglatVO;
    }

    private static double latLength(double latitudeA) {
        double latNum = latitudeA/6371393;
        latNum = latNum * 180/Math.PI;
        return latNum;
    }

    private static double lngLength(double longitudeA, double latitude) {
        double lngNum = 2* Math.asin(Math.sin(longitudeA/12742786)/Math.cos(latitude*Math.PI/180));
        lngNum = lngNum * 180/Math.PI;
        return lngNum;
    }

    /**
     * @description: 摄像机到告警点A的水平角和俯仰角计算
     * @param alarmPTZVo
     * @param lnglat
     * @return: null
     */
    private static void angleComputing(AlarmPTZVo alarmPTZVo, TranslnglatDTO lnglat,TranslnglatVO translnglatVO) {
        String horizontalAngle0 = lnglat.getP0(); //初始水平方位角
        String verticalAngle0 = lnglat.getT0(); //初始垂直俯仰角
        String h = lnglat.getH();
        String alarmP = alarmPTZVo.getAlarmP();
        String alarmT = alarmPTZVo.getAlarmT();
        String alarmZ = alarmPTZVo.getAlarmZ();
        BigDecimal horizontalAngle = new BigDecimal(alarmP).subtract(new BigDecimal(horizontalAngle0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        BigDecimal pitchAngle = new BigDecimal(alarmT).subtract(new BigDecimal(verticalAngle0)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP);
        Double horizontalAngle2 ;
        Double pitchAngle2 ;
        if(horizontalAngle.compareTo(new BigDecimal(0))<0){
            horizontalAngle2 = horizontalAngle.add(new BigDecimal(360)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else if(horizontalAngle.compareTo(new BigDecimal(360))>=0){
            horizontalAngle2 = horizontalAngle.subtract(new BigDecimal(360)).setScale(Constants.SIX_NUM,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else{
            horizontalAngle2 = horizontalAngle.doubleValue();
        }
        if(pitchAngle.compareTo(new BigDecimal(0))>0){
            pitchAngle2 = - pitchAngle.doubleValue();
        }else{
            pitchAngle2 = pitchAngle.doubleValue();
        }
        log.info("水平方位角：{}，俯仰角：{}",horizontalAngle2,pitchAngle2);
        translnglatVO.setDevicePitch(String.valueOf(pitchAngle2));
        double lao ;
        if(pitchAngle.compareTo(new BigDecimal(0))==0){
            lao = 3000;
        }else{
            lao = Math.abs(Double.valueOf(h)*(Math.cos(pitchAngle2 * Math.PI / 180 )/Math.sin(pitchAngle2 * Math.PI / 180 )*100)/100);
        }
        log.info("lao实际距离：{}",lao);
        if(lao>=3000){
            lao = 3000;
        }
        //double xao = Math.abs(Math.round(Double.valueOf(h)*(Math.cos(pitchAngle2 * Math.PI / 180 )/Math.sin(pitchAngle2 * Math.PI / 180 ))*Math.sin(horizontalAngle2 * Math.PI / 180)*100)/100);
        //double yao = Math.abs(Math.round(Double.valueOf(h)*(Math.cos(pitchAngle2 * Math.PI / 180 )/Math.sin(pitchAngle2 * Math.PI / 180 ))*Math.cos(horizontalAngle2 * Math.PI / 180)*100)/100);
        alarmPTZVo.setFlatA(String.valueOf(horizontalAngle2));
        alarmPTZVo.setTowerLength(String.valueOf(lao));
        //alarmPTZVo.setXao(String.valueOf(xao));
        //alarmPTZVo.setYao(String.valueOf(yao));

    }


}
