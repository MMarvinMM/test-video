package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/4/2 14:11
 */
@Data
public class CorrespondPointReq extends PageDTO {
    private String id; // 主键id
    private String pointName; // 对照点名称
    private String longitude; // 经度
    private String latitude; // 纬度
    private String taskRecordId; // 飞行成果物照片所属任务的id
    private String taskRecordName; // 飞行成果物照片所属任务的名称
    private String deviceCode; // 设备编码
    private String uavCode; // 无人机编码
    private String uavName; // 无人机名称
    private String photoId; // 飞行成果物照片的id
    private String timeCreated; // 飞行成果物照片的拍摄时间
    private String timeCreatedStartTime; //查询条件 开始时间
    private String timeCreatedEndTime; //查询条件 结束时间
    private String photoUrl; // 飞行成果物照片的访问地址
    private String photoAccessNode; // 飞行成果物照片所属节点
    private String photoHeight; // 飞行成果物照片的高度
    private String photoJsonInfo; // 飞行成果物照片类型
    private String tenantId; // 租户id
    private String industryCode; // 行业编码
    private String createUser; // 创建人
    private String updateUser; // 更新人
    private String appVerCode; // 行业版本
    private String pointType; // 对照点类型 0选择成果物图片为对照点 1手动上传
    private String status; // 0 1 停用启用
    private String accessNode;
    private String sort; //
}
