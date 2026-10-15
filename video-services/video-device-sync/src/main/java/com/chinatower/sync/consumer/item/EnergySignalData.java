package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class EnergySignalData implements Serializable {

    private String collectTime;
    private String mid;
    private String value;
}
