package com.chinatower.video.modules.common.util;

/**
 * @ClassName InterfaceLogUtil
 * @Description 记录业务交易日志
 * @Author Chensw
 * @Date 2022/1/24
 **/
public class InterfaceLogUtil {
    public static String reqTransLog(String body) {
        return new StringBuffer("REQ").append("#\n").append(body).toString();

    }

    public static String rspTransLog(String body) {
        return new StringBuffer("RSP").append("#\n").append(body).toString();
    }

}
