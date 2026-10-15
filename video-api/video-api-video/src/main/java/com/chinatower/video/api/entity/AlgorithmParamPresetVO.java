package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @ClassName AlgorithmParamPresetVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@Alias("AlgorithmParamPresetVOVideo")
public class AlgorithmParamPresetVO {
    public final static int PID = 157158;
    float pan;
    float tilt;
    float zoom;
    long presetId;
    String presetName;

}
