package com.chinatower.platform.modules;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 属地告警同步状态记录
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "alarm_sync_status")
public class AlarmSyncStatusEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 属地编码
     */
    private String platformCode;

    /**
     * 拉取日期，格式：yyyyMMdd
     */
    private String pullDate;

    /**
     * 同步类型：1-工单，2-运营研判，3-回收站
     */
    private Integer syncType;

    /**
     * 拉取状态：1-成功，2-失败
     */
    private Integer syncStatus;
}
