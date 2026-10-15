package com.chinatower.video.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="vm_video_preset_snapshot")
public class VmVideoPresetSnapshotMongo extends BaseMongoDTO implements Serializable {

    private String id;

    /**
     * 预置点id
     */
    private Integer presetId;
    
    /**
     * 预置位图片Url
     */
    private String imgUrl;
    
    /**
     * 删除标识 正常0  删除1
     */
    private Integer deleteFlag;
    
    // 创建时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    
    // 抓图时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date snapshotTime;
    
    private String deviceCode;

    private String channelCode;
    
    private String tenantId;
    
    private String industryCode;

    /**
     * 预置位名称
     */
    @TableField(exist = false)
    private String presetName;

    /**
     * 通道名称
     */
    @TableField(exist = false)
    private String channelName;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 任务编码
     */
    private String taskId;

    /**
     * 告警编码
     */
    private String warningOrderId;

    /**
     * 告警类型名称
     */
    private String typeName;

    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
}
