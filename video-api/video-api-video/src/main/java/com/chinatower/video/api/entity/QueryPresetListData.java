package com.chinatower.video.api.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class QueryPresetListData {
    /**
     * 预置点id
     * @required true
     */
    private int presetId;
    /**
     * 预置点名称
     * @required true
     */
    private String presetName;
    /**
     * PTZ坐标
     * @required false
     */
    private PresetData preset;
    /**
     * 预置点图片
     * @required false
     */
    private String imgUrl;

    private int sort;

    private int mark;
    /**
     * P参数
     * @required true
     */
    private float pan;
    /**
     * T参数
     * @required true
     */
    private float tilt;
    /**
     * Z参数
     * @required true
     */
    private float zoom;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     * 受理节点编码
     */
    private String acceptNode;

    /**
     * 接入节点编码
     */
    private String accessNode;

    @Data
    public static class QueryPresetListDataEntity extends CommonResultEntiry {
        private List<QueryPresetListData> data;
    }

}
