package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ComprehensiveQueryAIResult {
    private int total;
    private List<DeviceTreeNewVO> list = new ArrayList<>();
}
