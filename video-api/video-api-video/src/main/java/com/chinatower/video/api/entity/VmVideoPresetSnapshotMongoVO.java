package com.chinatower.video.api.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanglei
 */
@Data
@Document(collection="vm_video_preset_snapshot")
public class VmVideoPresetSnapshotMongoVO extends BaseMongoDTO implements Serializable {

    /**
     * 预置点id
     */
    private Integer presetId;
    
    private String deviceCode;

    private String originUrl;

    private String tenantId;

    private String industryCode;

    /**
     * 预置位图片Url
     */
    private String imgUrl;
    
    private String channelCode;
    
    // 抓图时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date snapshotTime;
    
    /**
     * 删除标识 正常0  删除1
     */
    private Integer deleteFlag;
    
}
