package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: WaterLevelVO
 * @Author: wangyt
 * @CreateTime: 2024-12-16  21:02
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WaterLevelVO extends BaseMongoDTO {
    private String id;
    private String picAddr;
    private String msgid;
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date gatherTime;
    private String deviceCode;
    private String waterLevel;
}
