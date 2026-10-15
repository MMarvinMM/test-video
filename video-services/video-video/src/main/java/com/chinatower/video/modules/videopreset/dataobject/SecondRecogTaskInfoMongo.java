package com.chinatower.video.modules.videopreset.dataobject;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="second_recog_task_info")
public class SecondRecogTaskInfoMongo extends BaseMongoDTO implements Serializable {

    /**
     * 告警编码
     */
    private String warningOrderId;

    /**
     * 任务编码
     */
    private String taskId;
}
