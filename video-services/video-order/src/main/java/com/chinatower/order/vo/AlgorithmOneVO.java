package com.chinatower.order.vo;

import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.AlgorithmParamPresetVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: AlgorithmOneVO
 * @Description: 算法配置和算法区域
 * @Author: peige
 * @Date: 2021-07-06
 **/
@Data
public class AlgorithmOneVO {
    // 配置编号
    String configId;
    // 设备编号
    String deviceCode;
    // 渠道编号
    String channelCode;
    // 位置类型：1通道，2预置位
    Integer posType;
    // 预置位编号
    Integer presetId;
    // 客户编号
    String customerCode;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    Date createTime;
    // 设备名称
    String deviceName;
    // 渠道名称
    String channelName;
    // 预置位名称
    String presetName;
    //预置位ptz参数
    AlgorithmParamPresetVO preset;
    // 区域编号
    String areaId;
    // 算法编号
    String algorithmCode;
    // 坐标点json
    String points;
    // 截图
    String img;
    // 算法名称
    String algorithmName;
    //操作类型
    int optType;//1-新增；2-修改；3-删除（删除也传全字段数据）
    //监控任务唯一标识
    String taskId;//32位uuid唯一主键
    //状态：0:停用 1:启用
    String status;
    //删除标识 0:删除 1:正常
    String deleteFlag;
    // 关联区域
    List<AlgorithmMonitorAreaVO> monitorArea;

    String orderCode;

    int ordStatus;
    //当前行业编码，多个逗号分隔
    String industryCode;
    //历史行业编码，多个逗号分隔
    String industryCodeHis;
    //当前行业版本编码，多个逗号分隔
    String appVerCode;
    //历史行业版本编码，多个逗号分隔
    String appVerCodeHis;
}
