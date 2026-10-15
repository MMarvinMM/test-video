package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("vm_horn_device_info_t")
public class HornDeviceDo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 乡镇编码
     */
    private String townCode;

    /**
     * 设备地址
     */
    private String location;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     *行政区域
     */
    private String admCode;

    /**
     * 设备挂高
     */
    private String height;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;

    /**
     * 删除标识
     */
    private String deleteFlag;

    /**
     *创建人
     */
    private String creator;

    /**
     *创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 设备型号编码
     */
    private String modelCode;

    /**
     * 设备类型
     */
    private String deviceType;

}
