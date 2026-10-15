package com.chinatower.event.lntlagservice;

import com.chinatower.common.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Created by zheng.yan on 2022/4/26.
 */
@Slf4j
public class AngleUtils {

    public static final int HORI_AZIMUTH_ANGLE_0 = 0;
    public static final int HORI_AZIMUTH_ANGLE_90 = 90;
    public static final int HORI_AZIMUTH_ANGLE_180 = 180;
    public static final int HORI_AZIMUTH_ANGLE_270 = 270;
    public static final int HORI_PAZIMUTH_ANGLE_360 = 360;

    public static String transformAngle(String angle) {

        if (StringUtils.isEmpty(angle)) {
            return angle;
        }
        if(angle.contains(Constants.ANGLE_VALUE_S) || angle.contains(Constants.ANGLE_VALUE_N) || angle.contains(Constants.ANGLE_VALUE_E) || angle.contains(Constants.ANGLE_VALUE_W)){
            return angle;
        }
        String angleValue = angle;
        try {
            BigDecimal angleKey = new BigDecimal(angle);
            if (HORI_AZIMUTH_ANGLE_0 == angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_N;
            } else if (HORI_AZIMUTH_ANGLE_0 < angleKey.doubleValue() && HORI_AZIMUTH_ANGLE_90 > angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_N + BigDecimal.valueOf(angleKey.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP) + "°E";
            } else if (HORI_AZIMUTH_ANGLE_90 == angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_E;
            } else if (HORI_AZIMUTH_ANGLE_90 < angleKey.doubleValue() && HORI_AZIMUTH_ANGLE_180 > angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_S + BigDecimal.valueOf(180 - angleKey.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP) + "°E";
            } else if (HORI_AZIMUTH_ANGLE_180 == angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_S;
            } else if (HORI_AZIMUTH_ANGLE_180 < angleKey.doubleValue() && HORI_AZIMUTH_ANGLE_270 > angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_S + BigDecimal.valueOf(angleKey.doubleValue() - 180).setScale(2, BigDecimal.ROUND_HALF_UP) + "°W";
            } else if (HORI_AZIMUTH_ANGLE_270 == angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_W;
            } else if (HORI_AZIMUTH_ANGLE_270 < angleKey.doubleValue() && HORI_PAZIMUTH_ANGLE_360 > angleKey.doubleValue()) {
                angleValue = Constants.ANGLE_VALUE_N + BigDecimal.valueOf(360 - angleKey.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP) + "°W";
            }
        }
        catch (Exception e) {
            log.info("水平方位角转换异常："+ e);
        }
        return angleValue;
    }
}
