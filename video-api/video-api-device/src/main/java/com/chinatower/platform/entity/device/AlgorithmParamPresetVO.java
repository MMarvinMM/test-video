package com.chinatower.platform.entity.device;

import lombok.Data;

/**
 * @ClassName AlgorithmParamPresetVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
public class AlgorithmParamPresetVO {
    public final static int PID = 157158;
    Float pan;
    Float tilt;
    Float zoom;
    long presetId;
    String presetName;

}
