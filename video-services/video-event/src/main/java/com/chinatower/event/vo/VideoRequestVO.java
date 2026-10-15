package com.chinatower.event.vo;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoRequestVO {

    //设备编码
    private String deviceCode;

    //通道编码
    private String channelCode;

    //客户编码
    private String customerCode;

    //订购关系编码，用于鉴权
    private String orderCode;
    //套餐编码
    private String packageCode;

    //用户编码
    private String userCode;
    //告警事件id
    private String alarmId;
    //云台控制令牌，解锁时必填。
    private String lockToken;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    private String startTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    private String endTime;
    //协议类型  1-rtsp；2-rtmp；3-hls；4-http-flv；
    private int protocolType;
    //存储类型  0-前端存储；1-中心存储
    private int storageType;
    //码流类型  1-主码流；2-子码流
    private int streamType;
    //码流是否可切换标识  1-可切换；2-不可切换
    private String streamSwitchFlag;

    //云台操作方向,见 7.1云台命令类型值
    private int operType;
    //转动开始/结束标识:1:表示动作开始;0:表示动作结束
    private int operContent;
    //步长(控制云台转动速度）,与具体厂家设备有关；默认值为1；
    private int step;
    //根据指定的站址编码（多个），查询
    //  站址信息
    private Set<String> siteCodes;
    //每页数量:第页返回最多数据条数，默认为10，取值范围[5,100]
    private int pageSize;
    //当前页数:查询起始页，默认为1。
    private int currentPage;

    //登陆人
    private String creator;
    //区县
    private String provinceId;

    private String cityId;

    private String countyId;

    //维度标识:1当年；2当月；3前一天;4当天
    private String dimensionFlag;

    //趋势标识:year年；month月；day天
    private String trendFlag;

    private List<VideoRequestVO> channels;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    private String queryStartTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    private String queryEndTime;

    //告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
    private String orderStatus;

    //权限类型：//1全部摄像头权限;2组织下摄像头权限;3仅本人可见
    private String anthType;

    private String userId;

    private String orgId;

    private String tenantId;

    private List provinceIdList;

    private List cityIdList;

    private List countyIdList;

    private String authority; //1全部；2部分

    private List<String> tenantList; //租户ID列表

    private List<String> typeList; //告警类型ID列表

    private String otherFlag; //1包含其他

    private String tenantListAuthority; //1省份地市无下属租户或租户下无设备

    private String  plugId; //插件id

    private List devList;
    //倍速回放流控制标识:倍速回放流控制标识,在获取录像回放地址时返回
    private String streamControlId;
    //控制类型:1：暂停，2：播放，3:倍速
    private int controlType;
    //播放速度:当controlType为3时，必传，取之范围为[-2,2]整数范围，即-2代表1/4倍速播放，-1代表1/2倍速播放，0代表正常倍速播放，
    //  1代表2倍速播放，2代表4倍速播放
    private int speed;
}
