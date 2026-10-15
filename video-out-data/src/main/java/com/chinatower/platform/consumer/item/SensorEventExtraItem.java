package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

@Data
public class SensorEventExtraItem extends BaseMongoDTO {

    /**
     * 昆虫种类
     */
    private String species;

    /**
     * 昆虫数量
     */
    private String speciesNum;

    /**
     * 噪声报警瞬时声级
     */
    private String soundLaAla;
}
