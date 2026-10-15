package com.chinatower.device.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnergyMonitorDataMapVO extends HashMap<String, Object> implements Serializable {

}