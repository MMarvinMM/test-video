package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author lc
 * @since 2022-04-20
 */
@Getter
@Setter
@TableName("vm_checkpoint_device_info_t")
public class BaseCheckpointDeviceInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cp_id", type = IdType.AUTO)
    private Long cpId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private Integer channelName;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 地市名称
     */
    private String cityName;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 删除标识：0正常1删除
     */
    private String deleteFlag;

    private List<String> ids;

}
