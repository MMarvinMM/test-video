package com.chinatower.stdu.common.util;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.stdu.common.entity.ResultEntity;
import com.chinatower.video.api.entity.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * video模块工具类
 * Created by zhujun on 2022/1/14.
 */
@Component
public class VideoCommonUtil {

    private static final String SUCCESS_CODE = "0000";
    private static final String ERROR_CODE = "8888";

    private static final String VIDEO_CODE = "设备编码:";

    private static final String CHANNEL_CODE = "通道编码:";

    /**
     * 判断统一网关接口返回是否为成功状态
     *
     * @param cr
     * @return
     */
    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && SUCCESS_CODE.equals(cr.getReturnCode());
    }

    public static boolean crIsSuccess(CommonResultEntiry cr) {
        return cr != null && SUCCESS_CODE.equals(cr.getReturnCode());
    }

    /**
     * 获取视频控制拼接日志输出信息
     *
     * @return
     */
    public static String getLogData(VideoBaseReq videoBaseReq) {
        return new StringBuilder(VIDEO_CODE).append(videoBaseReq.getDeviceCode())
                .append(CHANNEL_CODE).append(videoBaseReq.getChannelCode()).toString();
    }
    public static String getLogData(VideoBaseControlReq videoBaseControlReq) {
        return new StringBuilder(VIDEO_CODE).append(videoBaseControlReq.getDeviceCode())
                .append(CHANNEL_CODE).append(videoBaseControlReq.getChannelCode()).toString();
    }

    /**
     * 获取视频播放查询通道录像列表拼接日志输出信息
     *
     * @return
     */
    public static String getLogData(VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
        return new StringBuilder(VIDEO_CODE).append(videoPlayerQueryVideoListReq.getDeviceCode())
                .append(CHANNEL_CODE).append(videoPlayerQueryVideoListReq.getChannelCode()).toString();
    }

    /**
     * 调用接口返回token无效
     *
     * @param cr
     * @return
     */
    public static boolean bpTokenError(ResultEntity cr) {
        return cr != null && cr.getData() != null
                && ERROR_CODE.equals(cr.getReturnCode())
                && ("3006".equals(cr.getErrorCode()) || "1003".equals(cr.getErrorCode()));
    }
    /**
     * 调用接口返回token无效
     *
     * @param cr
     * @return
     */
    public static boolean bpTokenError(DoControlPresetReq.DoControlPresetReqA cr) {
        return cr != null && cr.getData() != null
                && ERROR_CODE.equals(cr.getReturnCode())
                && ("3006".equals(cr.getErrorCode()) || "1003".equals(cr.getErrorCode()));
    }

    /**
     * 获取南向接口返回的错误信息描述
     *
     * @param cr
     * @return 南向接口返回的错误信息描述
     */
    public static String getOmErrorMsg(ResultEntity cr) {
        String msg = "";
        if (cr != null && ERROR_CODE.equals(cr.getReturnCode())){
            msg = cr.getErrorMsg();
        }
        return msg;
    }

    /**
     * 获取南向接口返回的错误信息描述
     *
     * @param cr
     * @return 南向接口返回的错误信息描述
     */
    public static String getOmErrorCode(ResultEntity cr) {
        String code = "";
        if (cr != null && ERROR_CODE.equals(cr.getReturnCode())){
            if(StringUtils.isNotEmpty(cr.getErrorCode())){
                code = cr.getErrorCode();
            }
        }
        return code;
    }

    /**
     * 获取南向接口返回的错误信息描述
     *
     * @param cr
     * @return 南向接口返回的错误信息描述
     */
    public static String getOmErrorCode(CommonResultEntiry cr) {
        String code = "";
        if (cr != null && ERROR_CODE.equals(cr.getReturnCode())){
            if(StringUtils.isNotEmpty(cr.getErrorCode())){
                code = cr.getErrorCode();
            }
        }
        return code;
    }

    /**
     * 设置公共头部门数据
     * @param commonHeader
     * @param orderCode
     * @param currentUser
     */
    public static void setCommonHeader(Map<String,String> commonHeader, String orderCode, LoginUser currentUser) {
        commonHeader.put("X-OrderCode", orderCode);
        commonHeader.put("X-CustomerCode",currentUser.getUser().getTenantId());
        commonHeader.put("X-UserCode",String.valueOf(currentUser.getUser().getUserId()));
        commonHeader.put("X-TowerCode", VideoConstants.TOWERCODETEST);
    }


}
