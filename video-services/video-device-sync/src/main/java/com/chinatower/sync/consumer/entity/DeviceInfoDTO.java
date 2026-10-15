package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DeviceInfoDTO {
    @JsonProperty("platformCode")
    private String platformCode;
    @JsonProperty("subsideCode")
    private String subsideCode;
    @JsonProperty("subsideName")
    private String subsideName;
    @JsonProperty("deviceName")
    private String deviceName;
    @JsonProperty("deviceStatus")
    private Integer deviceStatus;
    @JsonProperty("modelCode")
    private String modelCode;
    @JsonProperty("categoryCode")
    private Integer categoryCode;
    @JsonProperty("devUserName")
    private String devUserName;
    @JsonProperty("devPassword")
    private String devPassword;
    @JsonProperty("deviceCode")
    private String deviceCode;
    @JsonProperty("devExInfo")
    private String devExInfo;
    @JsonProperty("gb28181Code")
    private String gb28181Code;
    @JsonProperty("devNameAbbr")
    private String devNameAbbr;
    @JsonProperty("remark")
    private String remark;
    @JsonProperty("enableSnap")
    private Integer enableSnap;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("port")
    private String port;
    @JsonProperty("businStatus")
    private Integer businStatus;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("collectType")
    private Integer collectType;
    @JsonProperty("collectMethod")
    private Integer collectMethod;
    @JsonProperty("siteCode")
    private String siteCode;
    @JsonProperty("siteName")
    private String siteName;
    @JsonProperty("publicSiteCode")
    private String publicSiteCode;
    @JsonProperty("orgCode")
    private String orgCode;
    @JsonProperty("optType")
    private Integer optType;
    @JsonProperty("creatTime")
    private String creatTime;
    @JsonProperty("creator")
    private String creator;
    @JsonProperty("channels")
    private List<ChannelInfoDTO> basicDeviceChannelBo;
    @JsonProperty("netType")
    private Integer netType;
    @JsonProperty("isCascade")
    private String isCascade;
    @JsonProperty("platId")
    private Integer platId;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("areaCode")
    private String areaCode;
    @JsonProperty("admCode")
    private String admCode;
    @JsonProperty("location")
    private String location;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("latitude")
    private String latitude;

    private String provinceId;
    private String cityId;
    private String countyId;
    private String townCode;

    private String deviceModelCode;

    @JsonProperty("collectProtocol")
    private String collectProtocol;

    @JsonProperty("deleteFlag")
    private String deleteFlag;
    @JsonProperty("accessNode")
    private String accessNode;
    /**
     * 海拔 单位：米
     */
    private String altitude;

    @JsonProperty("enableVideoNode")
    private String enableVideoNode;
    @JsonProperty("cusCodeShareProperty")
    private String cusCodeShareProperty;
    @JsonProperty("devicePropertyId")
    private String devicePropertyId;
    @JsonProperty("videoSubId")
    private String videoSubId;
    @JsonProperty("monitorAngle")
    private String monitorAngle;

}
