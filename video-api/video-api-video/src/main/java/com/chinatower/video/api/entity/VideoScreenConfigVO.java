package com.chinatower.video.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 *
 * 功能描述: 视频管理--视频设置VO
 *
 * @auther: zhaozj
 * @date: 2022/10/12
 * @version: releae_v1.5
 * @requestID: 视频管理--视频设置
 */
@Data
@ToString
@Alias("VideoScreenConfigVO")
public class VideoScreenConfigVO {
    private Long id;
    private String userId;
    private String screenNum;
    private String tenantId;
    private String industryCode;
    private String appVerCode;
    private String creator;
    private String modifier;
    /**
     * 是否设置水印（0未设置 1设置）
     */
    private String isWatermark;

    /**
     * 水印内容类型（1客户名称 2客户ID 3用户账号 4用户姓名）
     */
    private String watermarkContentType;

    /**
     * 水印密度（1小 2中 3大）
     */
    private String watermarkDensity;

    /**
     * 水印字号（单位：px）
     */
    private String watermarkFontSize;

    /**
     * 水印透明度
     */
    private String watermarkTransparency;

    /**
     * 实时视频播放窗口的3D放大功能默认打开
     */
    private Integer videoAmplification;


    /**
     * 视频播放组件的3D放大功能默认打开
     */
    private Integer videoAssemblyAmplification;


    /**
     * 简约模式下自动加载放大图片
     */
    private Integer automaticEnlargedImages;

    /**
     * 视频播放占位图
     */
    private String videoInitUrl;

    /**
     * 是否是铁塔账号 0否 1是
     */
    private String isTower;

    /**
     * 视频加载动画（1: 是, 0: 否）
     */
    private String videoLoading;


    /**
     * 视频预加载 0：是 1否
     */
    private Integer videoPreloader;

    /**
     * 视频预加载数量
     */
    private Integer videoPreloaderNum;

    /**
     * 1：根据上次浏览记录加载，2：根据经常查看加载；3：自定义选择通道
     */
    private Integer videoPreloaderModel;

    @TableField(exist = false)
    private List<VideoScreenConfigChannelVO> deviceList;
}
