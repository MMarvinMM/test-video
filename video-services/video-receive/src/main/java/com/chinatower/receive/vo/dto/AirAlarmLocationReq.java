package com.chinatower.receive.vo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: songxinfeng
 * @Date: 2024/12/9 10:09
 */
@Data
public class AirAlarmLocationReq implements Serializable {
    // 设置参数
    private double f; // 焦距
    private double k; // 相机放大倍数
    private double cx; // 主点x偏移
    private double cy; // 主点y偏移
    private double yaw; // 无人机航向角
    private double pitch; // 无人机俯仰角
    private double roll; // 无人机横滚角
    private double yaw1; // 云台航向角
    private double pitch1; // 云台俯仰角
    private double roll1; // 云台横滚角
    private double Lat_d; // 无人机纬度
    private double Lon_d; // 无人机经度
    private double u; // 像素坐标u(同主点x偏移)
    private double v; // 像素坐标v(主点y偏移)
}
