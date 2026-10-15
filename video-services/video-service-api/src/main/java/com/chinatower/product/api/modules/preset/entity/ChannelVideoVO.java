package com.chinatower.product.api.modules.preset.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: channelVideoVO
 * @Description: 通道录像实体
 * @Author: liuxt
 * @Date: 2021-05-11 11:51
 **/
@Data
@ToString
public class ChannelVideoVO {
    //当前页数 查询起始行
    private int currentPage;

    //一页条数
    private int pageSize;

    //存储类型 0-前端存储；1-中心存储
    private int storageType;

    //录像类型：0-普通录像，1-报警录像，2-全部查询
    private int recordType;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    private String startTime;

    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    private String endTime;

    //图片路径 告警联动图片抓拍返回的图片路径
    private String imagePath;

    //设备编号
    private String deviceCode;

    //通道编号
    private String channelCode;

    private String customerCode;
    private String userCode;
    private String orderCode;
    
    // 请求来源，如果值为api则表示来自api的请求
    private String requestFrom;
}
