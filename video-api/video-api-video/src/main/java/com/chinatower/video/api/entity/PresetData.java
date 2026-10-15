package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("PresetDataVideo")
public class PresetData {
    private String id;
    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private float pan;
    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private float tilt;
    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private float zoom;

    @Data
    public static class PresetDataEntity extends CommonResultEntiry {
        private PresetData data;
    }

    // 打点经度
    private double pointLon;
    // 打点纬度
    private double pointLat;
}
