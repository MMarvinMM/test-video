package com.chinatower.platform.data.sdk.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstactEntity {

    protected URL bindMapping;
    protected String requestBody;

    public AbstactEntity(URL bindMapping, String requestBody) {
        this.bindMapping = bindMapping;
        this.requestBody = requestBody;
    }

    public AbstactEntity(){

    }



    public enum URL {
        MONGODB_EXECUTE_SCRIPT("executeScript","/outdata/mogo/executeScript"),
        QUERY_DEVICE_CODE_LIST_BY_INDUSTRY_CODE("queryDeveceCodeListByIndustryCode","/outdata/queryDeveceCodeListByIndustryCode"),
        CAMERA_DO_LOCK("doLock","/video-video/video/control/sdk/doLock"),
        CAMERA_DO_RELEASE("doRelease","/video-video/video/control/sdk/doRelease"),
        CAMERA_DO_CONTROL_PRESET("doControlPreset","/video-video/video/control/sdk/doControlPresetV2"),
        CAMERA_SNAP_SHOT_PRESET("snapShot","/video-video/video/player/sdk/snapShot");

        @Getter
        private String url;
        @Getter
        private String name;
        URL(String name,String url) {
            this.url = url;
            this.name = name;
        }
    }
}
