package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;
/**
 * @ClassName AlgorithmAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@Alias("AlgorithmAreaVOVideo")
public class AlgorithmAreaVO {

    public final static String INVOKESTATUS = "1";
    public final static String ALGORITHMOPENSTATUS = "0";
    public final static String ALGORITHMCLOSESTATUS = "1";

    public final static String ALGORITHMSUCCESSSTATUS = "1";
    public final static String ALGORITHMFAILSTATUS = "2";
    public final static String ALGORITHMLOADINGSTATUS = "3";

    public final static String SECOND = "s";
    public final static String MINUTES = "M";
    public final static String HOUR = "h";
    public final static String DAY = "d";
    public final static String ONSTATUS = "5";
    public final static String STOPSTATUS = "6";
    public final static String ERRRORSTATUS = "2";
    /**
     * 删除标识
     */
    public final static String ONE = "1";

    // 配置编号
    String configId;
    // 算法编号
    String algorithmCode;
    // 坐标点json
    String points;
    // 截图
    String img;
    // 通道编号
    String channelCode;
    String channelName;
    //设备编号
    String deviceCode;
    String deviceName;
    // 预置位ID
    String presetId;
    // 预置位名称
    String presetName;
    // 预置位图片
    String imgUrl;
    // 算法名称
    String algorithmName;
    //客户编码
    String tenantId;
    //客户名称
    String tenantName;
    //客户省份
    String provinceName;
    //客户地市
    String cityName;

    //操作类型
    Integer optType;//1-新增；2-修改；3-删除（删除也传全字段数据）
    //监控任务唯一标识
    String taskId;//32位uuid唯一主键
    //状态：0:停用 1:启用
    String status;
    //删除标识 0:删除 1:正常
    String deleteFlag;
    // 关联区域
    List<AlgorithmMonitorAreaVO> monitorArea;

    String orderCode;
    String ordStatus;

    Integer posType;
    //铁塔视角算法状态：1:启动成功 2:启动失败 3:停用成功 4:停用失败 5:启用中 6:停用中 7:该配置不存在
    String algorithmStatus;

    String algorithmDescription;

    String logoUrl;
    //置信度阈值
    Float thresh;
    //重复告警间隔
    Integer alarmInterval;
    //重复告警间隔单位:秒s,分M,时h,天d
    String alarmIntervalType;
    //个性化参数
    Object modelConfig;
    //算法运行时间
    String[] algorithmTime;
    String algorithmTimeJosn;
    //算法操作 1.新增2.启用3.停用
    Integer operate;

    Object preset;
    /**
     * 行业编码
     */
    private String appVerCode;
}
