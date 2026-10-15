package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIS信息
 */
@Data
public class AisDetailsInfo implements Serializable {

    private String id;

    private String targetId;

    private String type;

    private String displayId;

    private String vesselName;

    private String mmsi;

    private String callSign;

    private String imo;

    private String latitude;

    private String longitude;

    private String course;

    private String heading;

    private String speed;

    private String len;

    private String wid;

    private String maxLen;

    private String minLen;

    private String shiptype;

    private String sClass;

    private String state;

    private String lastTm;

    private String receiveTime;

    private String status;

    private String province;

    private String quality;

    private String period;

    private String vendorId;

    private String nationality;

    private String duration;

    private String displacement;

    private String idLink;

    private List<Sources> sources;

    private AisInfo aisInfo;

    @Data
    private static class Sources implements Serializable {
        private List<String> ids;
        private String type;
    }

    @Data
    private static class AisInfo implements Serializable {
        private String callSign;
        private String destination;
        private String mmsi;
        private String shipClass;
        private String shipType;
        private String vesselName;
        private int wide;
        private String draught;
        private String etaTime;
        private int length;
        private String imo;
        private String nationality;
    }
}
