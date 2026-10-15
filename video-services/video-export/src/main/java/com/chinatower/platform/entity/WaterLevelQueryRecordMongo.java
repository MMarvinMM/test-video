package com.chinatower.platform.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName: WaterLevelQueryMongo
 * @Author: wangyt
 * @CreateTime: 2024-12-16  18:55
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "water_level_record")
public class WaterLevelQueryRecordMongo extends BaseMongoDTO {
    private String id;
    private String picAddr;
    private String msgid;
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date gatherTime;
    private String deviceCode;
    private String waterLevel;
}
