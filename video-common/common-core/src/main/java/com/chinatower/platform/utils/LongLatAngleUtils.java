package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.model.dto.PTZPointDto;
import com.chinatower.platform.model.dto.PercentPointDto;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zheng.yan on 2022/4/19.
 */
@Slf4j
@SuppressWarnings("all")
public class LongLatAngleUtils {

    public static final String TYPE_P = "P";
    public static final String TYPE_T = "T";
    public static final String TYPE_Z = "Z";
    public static final String MODELID_DAHUA = "dahua";
    public static final String MODELID_DHUA = "dhua";
    public static final String MODELID_HIKVISION = "hikvision";
    public static final String MODELID_HAOHAI = "haohai";
    public static final String MODELID_YOUHUA = "youhua";
    public static final String MODELID_HUAWEI = "huawei";
    public static final  String MODELID_UNIVIEW = "uniview";

    public static final String MODELID_GAODE = "gaode";

    public static final int HORI_PAZIMUTH_ANGLE_360 = 360;
    public static final int TWO_NUM = 2;
    public static final int FOUR_NUM = 4;
//    /**
//     * 参数进制转换
//     * @param value
//     * @param config
//     * @param type
//     * @return
//     */
//    public static String paramTrans(String value, DeviceModelVO config, String type) {
//        String result = value;
//        // dahua 大华的设备，上报的是十进制，在十进制下，把p、t都除个10就行
//        // Z=摄像机的最大放大倍数 *（Z原始值/128）=（最大焦距/最小焦距）*（Z原始值/128）
//        if (MODELID_DAHUA.equals(config.getMfgrCode())) {
//            BigDecimal num = new BigDecimal(value);
//            if (TYPE_Z.equals(type) && org.apache.commons.lang3.StringUtils.isNotBlank(config.getFocusRange())) {
//                String[] focusRange = config.getFocusRange().split("~");
//                if (focusRange.length >= 2) {
//                    BigDecimal minFocus = new BigDecimal(focusRange[0]);
//                    BigDecimal maxFocus = new BigDecimal(focusRange[1]);
//                    result = maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP).multiply(num.divide(new BigDecimal(128), 2, BigDecimal.ROUND_HALF_UP)).setScale(2).toString();
//                }
//            }
//            else {
//                result = String.valueOf(num.divide(new BigDecimal(10)));
//            }
//        }
//        // 海康威视设备 10进制转16进制 p、t值接口传入为10进制转为16进制再除以10（16进制的10），t再减去360（16进制的360）
//        if (MODELID_HIKVISION.equals(config.getMfgrCode())) {
//            // 转为正数
//            value = String.valueOf(Math.abs(new Double(value)));
//            // 如果包含小数，去除小数点后数据，转16进制
//            if (value.contains(".")) {
//                value = Integer.toHexString(Integer.valueOf(value.substring(0, value.indexOf("."))));
//            }
//            else {
//                value = Integer.toHexString(Integer.valueOf(value));
//            }
//            // 除以10，小数点左移一位
//            if ("0".equals(value)) {
//                result = "0";
//            }
//            else if (value.endsWith("0")) {
//                result = value.substring(0, value.length() - 1);
//            }
//            else {
//                result = value.substring(0, value.length() - 1) + "." + value.substring(value.length() - 1);
//            }
//            // 如果是T：0~90,T=T  T值不进行操作；  270~360，T=(T-360)  T值减360（按照10进制处理）
//            if (TYPE_T.equals(type)) {
//                BigDecimal bigDecimal = new BigDecimal(result);
//                //0~90,T=T  T值不进行操作，  270~360，T=(T-360)  T值减360
//                if ((bigDecimal.compareTo(new BigDecimal("270")) > -1) && (bigDecimal.compareTo(new BigDecimal("360")) < 1)) {
//                    result = String.valueOf(bigDecimal.subtract(new BigDecimal("360")));
//                }
//            }
//        }
//        if (TYPE_T.equals(type)) {
//            // 根据配置对t值进行翻转转换
//            if (!DeviceConstants.YES_FLAG.equals(config.getVertiDownNeg())) {
//                result = new BigDecimal(result).multiply(new BigDecimal("-1")).toPlainString();
//            }
//        }
//        log.info("paramTrans 参数进制转换：value：{},modelId：{},type：{},result：{}", value, JSON.toJSONString(config), type, result);
//        return result;
//    }

    /**
     * 参数进制转换
     * @param value
     * @param config
     * @param type
     * @return
     */
    public static String paramTrans(String value, DeviceModelVO config, String type) {
        String result = value;
        if (MODELID_DAHUA.equals(config.getMfgrCode()) || MODELID_DHUA.equals(config.getMfgrCode())) {
            result = paramTransDahua(value, config, type);
        } else if (MODELID_HIKVISION.equals(config.getMfgrCode())) {
            result = paramTransHaiKang(value, type);
        }else if (MODELID_YOUHUA.equals(config.getMfgrCode())) {
            result = paramTransYouhua(value);
        }else if (MODELID_HUAWEI.equals(config.getMfgrCode())) {
            result = paramTransHuawei(value,type);
        }else if(MODELID_UNIVIEW.equals(config.getMfgrCode())){
            log.info("宇视设备ptz转义");
            result = paramTransUniview(value,config,type);
        } else if (MODELID_GAODE.equals(config.getMfgrCode())) {
            log.info("高德设备ptz转义");
            result = paramTransGaode(value, type);
        }
        if ("T".equals(type) && !DeviceConstants.YES_FLAG.equals(config.getVertiDownNeg())) {
            result = new BigDecimal(result).multiply(new BigDecimal("-1")).toPlainString();
            log.info("水平向下参数：{},取反后：{}" , config.getVertiDownNeg(),result);
        }

        //保留两位小数
        result = new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

        log.info("paramTrans 参数进制转换：value：{},type：{},result：{},modelId：{}", value, type, result, JSON.toJSONString(config));
        return result;
    }

    /**
     * 大华 PTZ转P’T’Z’: P’=360-P/10。
     * T’=T/10。
     * Z’=摄像机的最大放大倍数 *（Z /128）=（最大焦距/最小焦距）*（Z /128）
     * @param value
     * @param config
     * @param type
     * @return
     */
    public static String paramTransDahua(String value, DeviceModelVO config, String type){
        String result = value;
        BigDecimal num = new BigDecimal(value);
        if ("Z".equals(type) && org.apache.commons.lang3.StringUtils.isNotBlank(config.getFocusRange())) {
            String[] focusRange = config.getFocusRange().split("~");
            if (focusRange.length >= 2) {
                BigDecimal minFocus = new BigDecimal(focusRange[0]);
                BigDecimal maxFocus = new BigDecimal(focusRange[1]);
                result = maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP).multiply(num.divide(BigDecimal.valueOf(128), 2, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
            }
        } else {
            if("P".equals(type)){
                //0915 review修改
                result = String.valueOf(new BigDecimal(360).subtract(num.divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP));
            }else {
                result = (num.divide(BigDecimal.valueOf(10)).setScale(2, BigDecimal.ROUND_HALF_UP)).toPlainString();
            }
        }
        return result;
    }

    /**
     * 友华
     * P’=P/100
     * T’=T/100
     * Z’=Z/100
     * @param value
     * @return
     */
    public static String paramTransYouhua(String value){
        return (new BigDecimal(value).divide(BigDecimal.valueOf(100))).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 华为
     * P’=P/10
     * T’=T/10
     * Z’=Z
     * @param value
     * @return
     */
    public static String paramTransHuawei(String value, String type){
        if ("Z".equals(type)) {
            return value;
        }else {
            return (new BigDecimal(value).divide(BigDecimal.valueOf(10))).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
    }

    /**
     * 高德
     * P’=P/100
     * T’=T/100
     * Z’=Z/10
     * @param value
     * @return
     */
    public static String paramTransGaode(String value, String type) {
        if (TYPE_Z.equals(type)) {
            return new BigDecimal(value).divide(BigDecimal.valueOf(10)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        } else {
            return (new BigDecimal(value).divide(BigDecimal.valueOf(100))).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
    }

    /**
     * 宇视
     * P’=（P*360+360）/2
     * T’=（T*135+45）/2
     * Z’= Z*（摄像机的最大放大倍数-1）+1  摄像机的最大放大倍数  = （最大焦距/最小焦距）
     * @param value
     * @param config
     * @param type
     * @return  加法add 减法:subtract  乘法:multiply  除法:divide
     */
    public static String paramTransUniview(String value, DeviceModelVO config, String type){
        String result = value;
        BigDecimal num = new BigDecimal(value);
        if (TYPE_Z.equals(type) && org.apache.commons.lang3.StringUtils.isNotBlank(config.getFocusRange())) {
            String[] focusRange = config.getFocusRange().split("~");
            if (focusRange.length >= 2) {
                BigDecimal minFocus = new BigDecimal(focusRange[0]);
                BigDecimal maxFocus = new BigDecimal(focusRange[1]);
                BigDecimal num1 = maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1));
                result =num.multiply(num1).add(new BigDecimal(1)).toPlainString();
            }
        } else {
            if(TYPE_P.equals(type)){
                result = String.valueOf((num.multiply(new BigDecimal(360)).add(new BigDecimal(360))).divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
                log.info("宇视设备转义后的值P："+result);
            }else {
                result = String.valueOf((num.multiply(new BigDecimal(135)).add(new BigDecimal(45))).divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
                log.info("宇视设备转义后的值T："+result);
            }
        }
        return result;
    }

    /**
     * 海康威视 PTZ转P’T’Z’:转为16进制，除以10。
     * @param value
     * @param type
     * @return
     */
    public static String paramTransHaiKang(String value, String type){
        String result;
        // 转为正数
        value = String.valueOf(Math.abs(new Double(value)));
        // 如果包含小数，去除小数点后数据，转16进制
        if (value.contains(".")) {
            value = Integer.toHexString(Integer.valueOf(value.substring(0, value.indexOf("."))));
        }
        else {
            value = Integer.toHexString(Integer.valueOf(value));
        }
        // 除以10，小数点左移一位
        if ("0".equals(value)) {
            result = "0";
        }
        else if (value.endsWith("0")) {
            result = value.substring(0, value.length() - 1);
        }
        else {
            result = value.substring(0, value.length() - 1) + "." + value.substring(value.length() - 1);
        }
        // 如果是T：0~90,T=T  T值不进行操作；  270~360，T=(T-360)  T值减360（按照10进制处理）
        if ("T".equals(type)) {
            BigDecimal bigDecimal = new BigDecimal(result);
            //0~90,T=T  T值不进行操作，  270~360，T=(T-360)  T值减360
            if ((bigDecimal.compareTo(new BigDecimal("90")) == 1) && (bigDecimal.compareTo(new BigDecimal("360")) < 1)) {
                result = String.valueOf(bigDecimal.subtract(new BigDecimal("360")));
            }
        }
        return result;
    }


    /**
     * 根据实时p值获取有效参考点的水平角偏差p0,俯仰角偏差t0
     * @param: pointsValid
     * @param: lnglat
     * @return: DeviceModelVO
     * @author: zheng.yan
     * @date: 2022/5/24
     * @version:
     */
    public static DeviceModelVO getValidOffset(List<DeviceRefPointVO> pointsValid, DeviceModelVO lnglat, List<SysDictData> enumList ) {
        //N=1
        if (pointsValid.size() == 1) {
            lnglat.setP0(pointsValid.get(0).getHorOffset());
            lnglat.setT0(pointsValid.get(0).getPitchOffset());
        }
        // N>1
        else if (pointsValid.size() > 1) {
            BigDecimal horOffset;
            BigDecimal pitchOffset;
            BigDecimal pn = BigDecimal.ZERO;
            BigDecimal pn1 = BigDecimal.ZERO;
            DeviceRefPointVO dn = new DeviceRefPointVO();
            DeviceRefPointVO dn1 = new DeviceRefPointVO();
            //c  PB <P1
            if (Float.parseFloat(lnglat.getRealPan()) < pointsValid.get(0).getPan()) {
                pn1 = BigDecimal.valueOf(pointsValid.get(0).getPan());
                pn = BigDecimal.valueOf(pointsValid.get(pointsValid.size()-1).getPan()).subtract(new BigDecimal(HORI_PAZIMUTH_ANGLE_360));
                dn = pointsValid.get(pointsValid.size()-1);
                dn1 = pointsValid.get(0);
            }
            //b  PB>PN
            else if (Float.parseFloat(lnglat.getRealPan()) > pointsValid.get(pointsValid.size() - 1).getPan()) {
                pn1 = BigDecimal.valueOf(pointsValid.get(0).getPan()).add(new BigDecimal(HORI_PAZIMUTH_ANGLE_360));
                pn = BigDecimal.valueOf(pointsValid.get(pointsValid.size()-1).getPan());
                dn = pointsValid.get(pointsValid.size()-1);
                dn1 = pointsValid.get(0);
            }
            else{
                List<DeviceRefPointVO> point = pointsValid.stream().filter(temp ->
                        new BigDecimal(temp.getPan().toString()).setScale(TWO_NUM,BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(lnglat.getRealPan()).setScale(TWO_NUM,BigDecimal.ROUND_HALF_UP)) == 0)
                        .collect(Collectors.toList());
                // a  PB = Pn
                if (point.size() > 0) {
                    lnglat.setP0(point.get(0).getHorOffset());
                    lnglat.setT0(point.get(0).getPitchOffset());
                } else {
                    // N>1时 Pn<PB<Pn+1
                    for (int i = 0; i < pointsValid.size(); i++) {
                        if (i != pointsValid.size() - 1 && Float.parseFloat(lnglat.getRealPan()) > pointsValid.get(i).getPan()
                                && Float.parseFloat(lnglat.getRealPan()) < pointsValid.get(i + 1).getPan()) {
                            pn = BigDecimal.valueOf(pointsValid.get(i).getPan());
                            pn1 = BigDecimal.valueOf(pointsValid.get(i + 1).getPan());
                            dn = pointsValid.get(i);
                            dn1 = pointsValid.get(i + 1);
                            break;
                        }
                    }
                }
            }
            if (pn.compareTo(BigDecimal.ZERO) != 0 || pn1.compareTo(BigDecimal.ZERO) != 0) {
                // 查询字典表中配置的水平角偏差范围w(默认为3) 若Dn参考点的水平角偏差在 0-w之间 并且Dn1参考点的水平较偏差在 360-w到360之间  则对Dn1的水平角偏差-360处理
                BigDecimal w = new BigDecimal(Constants.THREE);
                if(!CollectionUtils.isEmpty(enumList)){
                    w = new BigDecimal(enumList.get(0).getDictValue());
                }
                log.info("计算告警点B的水平角偏差,字典值:{}",w);
                if(dn.getHorOffset().compareTo(new BigDecimal(Constants.ZERO)) > 0 && dn.getHorOffset().compareTo(w) < 0
                        && dn1.getHorOffset().compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360).subtract(w)) > 0
                        && dn1.getHorOffset().compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) < 0){
                    dn1.setHorOffset(dn1.getHorOffset().subtract(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)));
                }
                log.info("参数信息：pn:{},pn1:{},dn:{},dn1{}",pn,pn1,JSON.toJSONString(dn),JSON.toJSONString(dn1));
                horOffset = pn1.subtract(new BigDecimal(lnglat.getRealPan())).divide(
                        pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                ).multiply(dn.getHorOffset())
                        .add(new BigDecimal(lnglat.getRealPan()).subtract(pn).divide(
                                pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                        ).multiply(dn1.getHorOffset())).setScale(TWO_NUM, BigDecimal.ROUND_HALF_UP);
                pitchOffset = pn1.subtract(new BigDecimal(lnglat.getRealPan())).divide(
                        pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                ).multiply(dn.getPitchOffset())
                        .add(new BigDecimal(lnglat.getRealPan()).subtract(pn).divide(
                                pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                        ).multiply(dn1.getPitchOffset())).setScale(TWO_NUM, BigDecimal.ROUND_HALF_UP);
                log.info("计算结果：horOffset:{},pitchOffset:{}",horOffset,pitchOffset);
                lnglat.setP0(horOffset);
                lnglat.setT0(pitchOffset);
            }
        }
        return lnglat;
    }

    /**
     * 根据实时p值获取有效参考点的水平角偏差p0,俯仰角偏差t0
     * @param: pointsValid
     * @param: lnglat
     * @return: DeviceModelVO
     * @author: hued
     * @date: 2022年11月11日12:40:04
     * @version:
     */
    public static DeviceModelVO getValidOffsetByRealHorAngle(List<DeviceRefPointVO> pointsValid, DeviceModelVO lnglat, List<SysDictData> enumList) {
        //N=1
        if (pointsValid.size() == 1) {
            lnglat.setP0(pointsValid.get(0).getHorOffset());
            lnglat.setT0(pointsValid.get(0).getPitchOffset());
        }
        // N>1
        else if (pointsValid.size() > 1) {
            BigDecimal horOffset;
            BigDecimal pitchOffset;
            BigDecimal pn = BigDecimal.ZERO;
            BigDecimal pn1 = BigDecimal.ZERO;
            DeviceRefPointVO dn = new DeviceRefPointVO();
            DeviceRefPointVO dn1 = new DeviceRefPointVO();
            //c  PB <P1
            if (Float.parseFloat(lnglat.getRealPan()) < pointsValid.get(0).getRealHorAngle().floatValue()) {
                pn1 = BigDecimal.valueOf(pointsValid.get(0).getRealHorAngle().floatValue());
                pn = BigDecimal.valueOf(pointsValid.get(pointsValid.size()-1).getRealHorAngle().floatValue()).subtract(new BigDecimal(HORI_PAZIMUTH_ANGLE_360));
                dn = pointsValid.get(pointsValid.size()-1);
                dn1 = pointsValid.get(0);
            }
            //b  PB>PN
            else if (Float.parseFloat(lnglat.getRealPan()) > pointsValid.get(pointsValid.size() - 1).getRealHorAngle().floatValue()) {
                pn1 = BigDecimal.valueOf(pointsValid.get(0).getRealHorAngle().floatValue()).add(new BigDecimal(HORI_PAZIMUTH_ANGLE_360));
                pn = BigDecimal.valueOf(pointsValid.get(pointsValid.size()-1).getRealHorAngle().floatValue());
                dn = pointsValid.get(pointsValid.size()-1);
                dn1 = pointsValid.get(0);
            }
            else{
                List<DeviceRefPointVO> point = pointsValid.stream().filter(temp ->
                        new BigDecimal(temp.getRealHorAngle().toString()).setScale(TWO_NUM,BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(lnglat.getRealPan()).setScale(TWO_NUM,BigDecimal.ROUND_HALF_UP)) == 0)
                        .collect(Collectors.toList());
                // a  PB = Pn
                if (point.size() > 0) {
                    lnglat.setP0(point.get(0).getHorOffset());
                    lnglat.setT0(point.get(0).getPitchOffset());
                } else {
                    // N>1时 Pn<PB<Pn+1
                    for (int i = 0; i < pointsValid.size(); i++) {
                        if (i != pointsValid.size() - 1 && Float.parseFloat(lnglat.getRealPan()) > pointsValid.get(i).getRealHorAngle().floatValue()
                                && Float.parseFloat(lnglat.getRealPan()) < pointsValid.get(i + 1).getRealHorAngle().floatValue()) {
                            pn = BigDecimal.valueOf(pointsValid.get(i).getRealHorAngle().floatValue());
                            pn1 = BigDecimal.valueOf(pointsValid.get(i + 1).getRealHorAngle().floatValue());
                            dn = pointsValid.get(i);
                            dn1 = pointsValid.get(i + 1);
                            break;
                        }
                    }
                }
            }
            if (pn.compareTo(BigDecimal.ZERO) != 0 && pn1.compareTo(BigDecimal.ZERO) != 0) {
                // 查询字典表中配置的水平角偏差范围w(默认为3) 若Dn参考点的水平角偏差在 0-w之间 并且Dn1参考点的水平较偏差在 360-w到360之间  则对Dn1的水平角偏差-360处理
                BigDecimal w = new BigDecimal(Constants.THREE);
                log.info("计算告警点B的水平角偏差,字典值:{}",w);
                if(!CollectionUtils.isEmpty(enumList)){
                    w = new BigDecimal(enumList.get(0).getDictValue());
                }
                if(dn.getHorOffset().compareTo(new BigDecimal(Constants.ZERO)) > 0 && dn.getHorOffset().compareTo(w) < 0
                        && dn1.getHorOffset().compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)) < 0
                        && dn1.getHorOffset().compareTo(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360).subtract(w)) > 0){
                    dn1.setHorOffset(dn1.getHorOffset().subtract(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)));
                }

                log.info("计算后的N+1点的水平方位角:{}",dn1.getHorOffset());

                pitchOffset = pn1.subtract(new BigDecimal(lnglat.getRealPan())).divide(
                        pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                ).multiply(dn.getPitchOffset())
                        .add(new BigDecimal(lnglat.getRealPan()).subtract(pn).divide(
                                pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                        ).multiply(dn1.getPitchOffset())).setScale(TWO_NUM, BigDecimal.ROUND_HALF_UP);

                horOffset = pn1.subtract(new BigDecimal(lnglat.getRealPan())).divide(
                        pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                ).multiply(dn.getHorOffset())
                        .add(new BigDecimal(lnglat.getRealPan()).subtract(pn).divide(
                                pn1.subtract(pn), FOUR_NUM, BigDecimal.ROUND_HALF_UP
                        ).multiply(dn1.getHorOffset())).setScale(TWO_NUM, BigDecimal.ROUND_HALF_UP);


                lnglat.setP0(horOffset);
                lnglat.setT0(pitchOffset);
            }
        }
        return lnglat;
    }

    /**
     * 参数进制转换
     * PTZ逆转换
     * 假设：接口获取的值为PTZ，转义后的P’T’Z’。已知P’T’Z’，求PTZ。
     * @param paramValue
     * @param deviceModel
     * @param type
     * @return
     */
    public static String paramTransReverse(String paramValue, DeviceModelVO deviceModel, String type) {
        String resultValue = paramValue;
        if ("T".equals(type) && !DeviceConstants.YES_FLAG.equals(deviceModel.getVertiDownNeg())) {
            resultValue = new BigDecimal(resultValue).multiply(new BigDecimal("-1")).toPlainString();
            log.info("水平向下参数：{},取反后：{}" , deviceModel.getVertiDownNeg(),resultValue);
        }
        if (MODELID_DAHUA.equals(deviceModel.getMfgrCode()) || MODELID_DHUA.equals(deviceModel.getMfgrCode())) {
            resultValue = paramTransReverseDahua(resultValue, deviceModel, type);
        } else if (MODELID_HIKVISION.equals(deviceModel.getMfgrCode())) {
            resultValue = paramTransReverseHaiKang(resultValue, type);
        }else if (MODELID_YOUHUA.equals(deviceModel.getMfgrCode())) {
            resultValue = paramTransReverseYouhua(resultValue);
        }else if (MODELID_HUAWEI.equals(deviceModel.getMfgrCode())) {
            resultValue = paramTransReverseHuawei(resultValue,type);
        }else if(MODELID_UNIVIEW.equals(deviceModel.getMfgrCode())){
            resultValue = paramTransReverseUniview(resultValue, deviceModel, type);
        } else if (MODELID_GAODE.equals(deviceModel.getMfgrCode())){
            resultValue = paramTransReverseGaode(resultValue, type);
        }

        log.info("paramTransReverse 参数进制转换：paramValue：{},type：{},resultValue：{},deviceModel：{}", paramValue, type, resultValue, JSON.toJSONString(deviceModel));
        return resultValue;
    }

    /**
     *
     * 高德
     * P=P'*100
     * T=T'*100
     * Z=Z'*10
     */
    private static String paramTransReverseGaode(String paramValue, String type) {
        if (TYPE_Z.equals(type)) {
            return new BigDecimal(paramValue).multiply(new BigDecimal(10)).toPlainString();
        }else {
            return new BigDecimal(paramValue).multiply(new BigDecimal(100)).toPlainString();
        }
    }

    /**
     * 大华
     * P=（360-P’）*10。
     * T= T’*10。
     * Z= Z’*128/（最大焦距/最小焦距）
     * @param paramValue
     * @param deviceModel
     * @param type
     * @return
     */
    public static String paramTransReverseDahua(String paramValue, DeviceModelVO deviceModel, String type){
        String resultValue = paramValue;
        BigDecimal num = new BigDecimal(paramValue);
        if("P".equals(type)){
            // P=（360-P’）*10。
            BigDecimal numBig = new BigDecimal(10).multiply(new BigDecimal(360).subtract(num));
            resultValue = String.valueOf(numBig);
        }else if("T".equals(type)){
            resultValue = String.valueOf(num.multiply(new BigDecimal(10)));
        }else if("Z".equals(type)) {
            // Z= Z’*128/（最大焦距/最小焦距）。
        	if( org.apache.commons.lang3.StringUtils.isNotBlank(deviceModel.getFocusRange()) )
        	{
	            String[] focusRange = deviceModel.getFocusRange().split("~");
	            if (focusRange.length >= 2) {
	                BigDecimal minFocus = new BigDecimal(focusRange[0]);
	                BigDecimal maxFocus = new BigDecimal(focusRange[1]);
	                BigDecimal numBigZ = num.multiply(new BigDecimal(128)).divide(maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP),2,BigDecimal.ROUND_HALF_UP);
	                resultValue = String.valueOf(numBigZ.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	            }
        	}
        	else
        	{
        		resultValue = String.valueOf(num.multiply(new BigDecimal(10)));
        	}
        }
        return resultValue;
    }

    /**
     * 友华
     * P=P’*100
     * T=T’*100
     * Z=Z’*100
     * @param paramValue
     * @return
     */
    public static String paramTransReverseYouhua(String paramValue){
        return String.valueOf(new BigDecimal(paramValue).multiply(new BigDecimal(100)));
    }

    /**
     * 华为
     * P=P’*10
     * T=T’*10
     * Z=Z’
     * @param paramValue
     * @return
     */
    public static String paramTransReverseHuawei(String paramValue, String type){
        String resultRever = paramValue;
        if (!TYPE_Z.equals(type)) {
            BigDecimal numPT = new BigDecimal(10).multiply(new BigDecimal(paramValue));
            resultRever = String.valueOf(numPT);
        }
        return  resultRever;
    }

    /**
     * 宇视
     * P=（2* P’-360）/360
     * T=（2* T’-45）/135
     * Z= （Z’-1）/（摄像机的最大放大倍数-1）  摄像机的最大放大倍数  = （最大焦距/最小焦距）
     * @param value
     * @param config
     * @param type
     * @return
     */
    public static String paramTransReverseUniview(String value, DeviceModelVO config, String type){
        String result = value;
        BigDecimal num = new BigDecimal(value);
        if (TYPE_Z.equals(type) && org.apache.commons.lang3.StringUtils.isNotBlank(config.getFocusRange())) {
            String[] focusRange = config.getFocusRange().split("~");
            if (focusRange.length >= 2) {
                BigDecimal minFocus = new BigDecimal(focusRange[0]);
                BigDecimal maxFocus = new BigDecimal(focusRange[1]);
                BigDecimal num1 = maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1));
                result =(num.subtract(new BigDecimal(1))).divide(num1,2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
            }
        } else {
            if(TYPE_P.equals(type)){
                result = String.valueOf((num.multiply(new BigDecimal(2)).subtract(new BigDecimal(360))).divide(new BigDecimal(360),2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
            }else {
                result = String.valueOf((num.multiply(new BigDecimal(2)).subtract(new BigDecimal(45))).divide(new BigDecimal(135),2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        return result;
    }

    /**
     * 海康威视
     * P’T’Z’乘以10，作为16进制转为10进制。
     * @param paramValue
     * @param type
     * @return
     */
    public static String paramTransReverseHaiKang(String paramValue, String type){
        if (TYPE_T.equals(type) && new BigDecimal(paramValue).compareTo(BigDecimal.ZERO) < 0 ) {
            paramValue = new BigDecimal(paramValue).add(new BigDecimal(Constants.HORI_PAZIMUTH_ANGLE_360)).toString();
        }
        String valueNew = String.valueOf(new BigDecimal(paramValue).multiply(new BigDecimal(10)));
        return String.valueOf(Integer.parseInt((valueNew.contains(".")?valueNew.substring(0, valueNew.indexOf(".")):valueNew),16));
    }


    /**
     * 获取alpha
     * @param deviceModel 摄像机基本信息
     * @param ptzCenterPoint 中心点ptz
     * @return  alpha
     */
    private static String getAlpha(DeviceModelVO deviceModel, PTZPointDto ptzCenterPoint) {
        return String.valueOf(getCoordinateTransParam(deviceModel.getPicSensorLength(), deviceModel.getMinFocus(), String.valueOf(ptzCenterPoint.getZoom())));
    }

    /**
     * 获取beta
     * @param deviceModel 摄像机基本信息
     * @param ptzCenterPoint 中心点ptz
     * @return beta
     */
    private static String getBeta(DeviceModelVO deviceModel, PTZPointDto ptzCenterPoint) {
        return String.valueOf(getCoordinateTransParam(deviceModel.getPicSensorWidth(), deviceModel.getMinFocus(), String.valueOf(ptzCenterPoint.getZoom())));
    }

    /**
     * 百分比 转 PT
     * @param deviceModel 设备信息
     * @param percentPoint 百分比点
     * @param ptzCenterPoint  中心点的P'T'Z'
     * @return PT坐标
     */
    public static PTZPointDto percentTransPt(DeviceModelVO deviceModel, PercentPointDto percentPoint, PTZPointDto ptzCenterPoint) {
        String alpha = getAlpha(deviceModel, ptzCenterPoint);
        String beta = getBeta(deviceModel, ptzCenterPoint);
        log.info("水平视场角 alpha：{},俯仰视场角 beta：{}", alpha, beta);
        BigDecimal pa = (new BigDecimal(percentPoint.getPercentX()).subtract(new BigDecimal(Constants.PERCENT_50))).multiply(new BigDecimal(alpha)).add(new BigDecimal(ptzCenterPoint.getPan())).setScale(Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP);
        BigDecimal ta = (new BigDecimal(percentPoint.getPercentY()).subtract(new BigDecimal(Constants.PERCENT_50))).multiply(new BigDecimal(beta)).add(new BigDecimal(ptzCenterPoint.getTilt())).setScale(Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP);
        PTZPointDto pt = new PTZPointDto();
        pt.setMonitorPointId(percentPoint.getMonitorPointId());
        pt.setPan(pa.floatValue());
        pt.setTilt(ta.floatValue());
        log.info("百分比点位值：{},转义后的PT坐标点对应的：{}", percentPoint, pt);
        return pt;
    }

    /**
     * PT 转 百分比
     * @param deviceModel 设备信息
     * @param ptzPoint 查询的点的P'T'Z'
     * @param ptzCenterPoint 中心点的P'T'Z'
     * @return 百分比坐标
     */
    public static PercentPointDto ptTransPercent(DeviceModelVO deviceModel, PTZPointDto ptzPoint, PTZPointDto ptzCenterPoint) {
        String alpha = getAlpha(deviceModel, ptzCenterPoint);
        String beta = getBeta(deviceModel, ptzCenterPoint);
        log.info("水平视场角 alpha：{},俯仰视场角 beta：{}", alpha, beta);
        BigDecimal u = (new BigDecimal(ptzPoint.getPan()).subtract(new BigDecimal(ptzCenterPoint.getPan()))).divide(new BigDecimal(alpha), Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(Constants.PERCENT_50)).setScale(Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP);
        BigDecimal v = (new BigDecimal(ptzPoint.getTilt()).subtract(new BigDecimal(ptzCenterPoint.getTilt()))).divide(new BigDecimal(beta), Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(Constants.PERCENT_50)).setScale(Constants.ZOOM_DIGITS, BigDecimal.ROUND_HALF_UP);
        PercentPointDto percentPoint = new PercentPointDto();
        percentPoint.setMonitorPointId(ptzPoint.getMonitorPointId());
        percentPoint.setPercentX(u.floatValue());
        percentPoint.setPercentY(v.floatValue());
        log.info("PTZ点位值：{},转义后的百分比坐标点对应的：{}", ptzPoint, percentPoint);
        return percentPoint;
    }

    /**
     * 获取百分比坐标系转换ptz坐标系重要参数 alpha/beta
     * 公式：
     *  f=f0*Z'
     *  alpha=2arctan(c/2f)
     *  beta=2arctan(d/2f)
     *
     * @Param cOrd  摄像机图像传感器宽度/高度（单位：mm）
     * @Param d  摄像机图像传感器高度（单位：mm）
     * @param f0 摄像机最小焦距（单位：mm）
     * @param z1 Z转义后的值 Z'
     * @return alpha/beta
     */
    private static BigDecimal getCoordinateTransParam(String cOrd, String f0, String z1) {
        // zoom为null 为空 为0  当1处理
        if(StringUtils.isEmpty(z1) ||  Constants.ZOOM_STR_ZERO.equals(z1) || String.valueOf(Constants.ZOOM_ZERO).equals(z1)){
            z1 = "1";
        }
        BigDecimal f = new BigDecimal(f0).multiply(new BigDecimal(z1));
        double arcTan = Math.toDegrees(Math.atan(new BigDecimal(cOrd).divide((new BigDecimal("2").multiply(f)),4,BigDecimal.ROUND_HALF_UP).doubleValue()));
        return new BigDecimal(arcTan).multiply(new BigDecimal(Constants.ZOOM_STR_DIGITS));
    }

}
