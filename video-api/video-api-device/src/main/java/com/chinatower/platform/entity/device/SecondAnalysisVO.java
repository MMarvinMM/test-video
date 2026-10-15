package com.chinatower.platform.entity.device;

import lombok.Data;

@Data
public class SecondAnalysisVO {
    //开启
    private AlarmAlgorithmVO enable;

    //关闭
    private AlarmAlgorithmVO disable;
}
