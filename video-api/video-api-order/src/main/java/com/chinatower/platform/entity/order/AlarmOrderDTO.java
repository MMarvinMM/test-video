package com.chinatower.platform.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmOrderDTO implements Serializable {

    private List<String> warningOrderIds;

    private String iotDeviceCodeOrName;
}
