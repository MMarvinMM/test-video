package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryOptAuthEnumResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<QueryGetEnumInfoReq> cameraList;
    private List<QueryGetEnumInfoReq> presetList;
    private List<QueryGetEnumInfoReq> patrolList;
    private List<QueryGetEnumInfoReq> guardList;

    // 构造函数
    public QueryOptAuthEnumResponse(List<QueryGetEnumInfoReq> cameraList, List<QueryGetEnumInfoReq> presetList, List<QueryGetEnumInfoReq> patrolList, List<QueryGetEnumInfoReq> guardList) {
        this.cameraList = cameraList;
        this.presetList = presetList;
        this.patrolList = patrolList;
        this.guardList = guardList;
    }

    // Getters and Setters
    public List<QueryGetEnumInfoReq> getCameraList() {
        return cameraList;
    }

    public void setCameraList(List<QueryGetEnumInfoReq> CameraList) {
        this.cameraList = CameraList;
    }

    public List<QueryGetEnumInfoReq> getPresetList() {
        return presetList;
    }

    public void setPresetList(List<QueryGetEnumInfoReq> presetList) {
        this.presetList = presetList;
    }

    public List<QueryGetEnumInfoReq> getPatrolList() {
        return patrolList;
    }

    public void setPatrolList(List<QueryGetEnumInfoReq> patrolList) {
        this.patrolList = patrolList;
    }

    public List<QueryGetEnumInfoReq> getGuardList() {
        return guardList;
    }

    public void setGuardList(List<QueryGetEnumInfoReq> guardList) {
        this.guardList = guardList;
    }
}
