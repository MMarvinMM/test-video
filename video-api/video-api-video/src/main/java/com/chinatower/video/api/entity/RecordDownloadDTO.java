package com.chinatower.video.api.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="vm_record_download")
public class RecordDownloadDTO extends BaseMongoDTO implements Serializable {

    private String id;

    private String batchId;

    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    private String startTime;

    private String endTime;

    /**
     * 0-下载中  1-下载完成
     */
    private Integer downloadStatus;

    private Integer fileSize;

    private String fileName;

    private Integer order;

    private String localUrl;

    private String creator;

    private String modifier;

    private String deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private String warningOrderId;

    private String tableType;

    /**
     * 存储时间(单位秒)   5年：157680000  默认2天:172800
     */
    private Integer memoryCycle;

    /**
     * 操作来源类型  1-前端下载  2-告警下载 3-一键告警
     */
    private Integer operateSourceType;

    private String accessNode;
}
