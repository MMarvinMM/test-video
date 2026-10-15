package com.chinatower.analysis.api.entity;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CheckpointBatchVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = -7317528570182022512L;
    /**
     * 卡口摄像机编码
     * @required true
     */
    private List<String> deviceCodes;

    /**
     * 卡口摄像机名称
     * @required true
     */
    private String deviceName;

    /**
     * 通道编码
     * @required true
     */
    private List<String> channelCodes;

    /**
     * 关键字（车牌号或摄像机名称）
     * @required true
     */
    private String keywords;

    /**
     * 开始时间
     * @required true
     */
    private String queryStartTime;

    /**
     * 结束时间
     * @required true
     */
    private String queryEndTime;

    /**
     * 车牌号
     * @required true
     */
    private String vehicleNumber;

    /**
     * 车牌号
     * @required true
     */
    private List<String> vehicleNumberList;

    /**
     * 当前页
     * @required true
     */
    protected Integer pageNum;

    /**
     * 每页条数
     * @required true
     */
    protected Integer pageSize;

    private List<String> validDeviceCodes;

}
