package com.chinatower.receive.vo.dto;

import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class ReceiveAlarmDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作表名
     */
    private String tableName;

    /**
     * 业务生成的告警数据，用户保存告警表
     */
    private List<ReceiveAlarmVO> list;

    /** 2022-07-18 封装接收告警处理流程参数，为保证与其他部分兼容，同时不去创建过多新类，在此类基础上添加以下成员变量**/
    /**
     * 接收告警信息
     */
    private ReceiveAlarmVO receiveAlarmVO;

    /**
     * 接收告警租户信息
     */
    private List<OrderCustomerVO> customers;

    /**
     * 告警压制数据
     */
    private List<ReceiveAlarmVO> intercepts;

    /**
     * 运营研判数据
     */
    private List<ReceiveAlarmVO> operations;

    //物联设备批量上报节点信息
    private List<ReceiveIotAlarmVO> receiveIotAlarmVOList;

    //接收物联设备包含此设备的物联告警配置
    private List<ReceiveIotAlarmConfigDto> iotConfigList;

    //mongo累计值入参
    private List<Map<String,String>> mongoFactoryList;

    //告警防抖需要存储集合
    List<AntiShakeValueVO> antiShakeSaveList;

    //存入设备池需要存储集合
    List<IotDeviceGroupsVO> deviceGroupsSaveList;

    //存入各个配置的告警压制时效需要存储集合
    List<IotAlarmSuppressVO> iotAlarmSuppressVOList;

    //卫星告警接收入参
    List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos;

    //告警框图的点位信息
    List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();

    //无人机告警接收入参
    ReceiveUavAlarmVO receiveUavAlarmVOS;

}
