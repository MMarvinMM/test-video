package com.chinatower.message.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgKafkaConsumerModel<T extends Serializable> implements Serializable {

    /**
     * 1- 告警
     * 2- 雷达
     * 3- 预置位到达通知
     * 4- 告警声音开关
     * 5- 低空轨迹通知
     */
    private Integer type;

    private T t;
}
