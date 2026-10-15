package com.chinatower.video.modules.videopreset.dataobject;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "vm_device_snapshot_task")
public class YjbSnapshotTaskMongo extends BaseMongoDTO implements Serializable {
    @Id
    private String uniqueId;
    @Indexed
    private String taskId;
    private String deviceCode;
    private String channelCode;
    private String imgUrl;
    private Date createTime;
    private Date updateTime;
    private String snapshotStatus;
    private String errorMessage;
}
