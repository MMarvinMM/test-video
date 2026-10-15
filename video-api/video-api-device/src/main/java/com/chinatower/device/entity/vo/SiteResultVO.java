package com.chinatower.device.entity.vo;

import com.chinatower.industry.vo.IndChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SiteResultVO
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/10
 **/
@Data
public class SiteResultVO {
    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 站址上最高挂高
     */
    private String height;

    /**
     * 摄像头是否在线
     */
    private String allLine;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    /**
     * 距离
     */
    private String distance;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道列表
     */
    private List<IndChannelInfoVO> channelInfoVOList;
    /**
     * 通道信息
     */
    private List<ChannelInfoVO> channelInfoList;


}
