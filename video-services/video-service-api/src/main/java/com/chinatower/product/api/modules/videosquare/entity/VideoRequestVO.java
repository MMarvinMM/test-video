package com.chinatower.product.api.modules.videosquare.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: VideoRequestVO
 * @Description: 视频监控查询实体
 * @Author: zhaozj
 * @Date: 2021-05-06 11:51
 **/
@Data
@ToString
public class VideoRequestVO {
    //设备编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deviceCode;

    //通道编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String channelCode;

    //客户编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customerCode;

    //订购关系编码，用于鉴权
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderCode;

    //用户编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userCode;
    //告警事件id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alarmId;

    //云台控制令牌，解锁时必填。
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lockToken;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String startTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String endTime;
    //协议类型  1-rtsp；2-rtmp；3-hls；4-http-flv；
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int protocolType;
    //存储类型  0-前端存储；1-中心存储
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int storageType;
    //码流类型  1-主码流；2-子码流
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int streamType;


    //云台操作方向,见 7.1云台命令类型值
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int operType;
    //转动开始/结束标识:1:表示动作开始;0:表示动作结束
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int operContent;
    //步长(控制云台转动速度）,与具体厂家设备有关；默认值为1；
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int step;

    //根据指定的站址编码（多个），查询
    //  @JsonInclude(JsonInclude.Include.NON_NULL)站址信息
    private Set<String> siteCodes;
    //每页数量:第页返回最多数据条数，默认为10，取值范围[5,100]
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int pageSize;
    //当前页数:查询起始页，默认为1。
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int currentPage;

    //登陆人
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creator;
    //区县
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyId;

    //维度标识:1当年；2当月；3前一天;4当天
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dimensionFlag;

    //趋势标识:year年；month月；day天
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trendFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VideoRequestVO> channels;


    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryStartTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryEndTime;


    //告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderStatus;

    //权限类型：//1全部摄像头权限;2组织下摄像头权限;3仅本人可见
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String anthType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List provinceIdList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List cityIdList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List countyIdList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String authority;//1全部；2部分

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> tenantList;//租户ID列表

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> typeList;//告警类型ID列表

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otherFlag; //1包含其他

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantListAuthority;//1省份地市无下属租户或租户下无设备

    private String  plugId;//插件id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List devList;

    // 流UUID
    private String streamUuid;
    
    // 预置点Id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer presetId;
}
