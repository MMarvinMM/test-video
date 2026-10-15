package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class QueryGuardListData {

    private long presetId;
    /**
     * 看守位id
     * @required true
     */
    private long guardId;
    /**
     * 看守位名称
     * @required true
     */
    private String guardName;
    /**
     * 停留时间
     * @required true
     */
    private int staySeconds;
    /**
     * 是否已经启动
     * @required true
     */
    private int isStart;
    private int isGuard;

    @Data
    public static class QueryGuardListDataEntity extends CommonResultEntiry {
        private QueryGuardListData data;
    }
}
