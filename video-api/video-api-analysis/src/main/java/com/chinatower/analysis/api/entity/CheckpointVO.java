package com.chinatower.analysis.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CheckpointVO extends CommonFeignEntity {

    private static final long serialVersionUID = -730280966792464426L;

    /**
     * 卡口摄像机编码
     */
    private String deviceCode;

    /**
     * 卡口摄像机名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 关键字（车牌号或摄像机名称）
     */
    private String keywords;

    /**
     * 开始时间
     */
    private String queryStartTime;

    /**
     * 结束时间
     */
    private String queryEndTime;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    protected Integer pageNum;
    protected Integer pageSize;

    private List<String> validDeviceCodes;

}
