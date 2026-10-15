package com.chinatower.airline.enums;

import lombok.Getter;

/**
 * 直播视频流镜头类型
 *
 * @author ljy
 * @date 2025/12/30
 */
@Getter
public enum LiveVideoTypeEnum {
    /**
     * 0-默认，1-广角，2-变焦，3-红外
     */
    DEFAULT(0, "默认"),
    WIDE(1, "广角"),
    ZOOM(2, "变焦"),
    INFRARED(3, "红外");
    
    private final Integer code;
    private final String desc;
    LiveVideoTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
