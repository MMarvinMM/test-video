package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

@Data
public class SensorFileItem extends BaseMongoDTO {
    /**
     * 文件序号
     */
    private Integer sequence;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件业务属性 0-录音 1-原图 2-算法识别框图  3-视频
     */
    private Integer businessProp;
}
