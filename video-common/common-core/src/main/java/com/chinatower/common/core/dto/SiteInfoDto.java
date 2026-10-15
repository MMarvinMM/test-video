package com.chinatower.common.core.dto;


/**
 * 推送给南向转换后的对象
 *
 * @author jmx
 */

public class SiteInfoDto {

    /**
     * 站址名称
     */
    private String siteName;

    /**
     * 站址编码
     */
    private String siteCode;


    /**
     * 公共站址库
     */
    private String publicSiteCode;

    /**
     * 类型
     */
    private Integer siteType;



    /**
     * 业务子类
     */
    private Integer bussinessSub;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 地址
     */
    private String location;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;



    /**
     * 组织机构
     */
    private String orgCode;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 站址来源
     */
    private String siteSource;

    /**
     * 行政区域
     */
    private String admCode;

    /**
     * 来源渠道
     */
    private String sourceChannel;

    /**
     * 地形
     */
    private String buildingKind;

    /**
     * 一级业务场景
     */
    private Integer firstCoverScene;

    /**
     * 二级业务场景
     */
    private Integer secondCoverScene;

    /**
     * 创建时间
     */
    private String createTime;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getPublicSiteCode() {
        return publicSiteCode;
    }

    public void setPublicSiteCode(String publicSiteCode) {
        this.publicSiteCode = publicSiteCode;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getBussinessSub() {
        return bussinessSub;
    }

    public void setBussinessSub(Integer bussinessSub) {
        this.bussinessSub = bussinessSub;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getSiteSource() {
        return siteSource;
    }

    public void setSiteSource(String siteSource) {
        this.siteSource = siteSource;
    }

    public String getAdmCode() {
        return admCode;
    }

    public void setAdmCode(String admCode) {
        this.admCode = admCode;
    }

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getBuildingKind() {
        return buildingKind;
    }

    public void setBuildingKind(String buildingKind) {
        this.buildingKind = buildingKind;
    }

    public Integer getFirstCoverScene() {
        return firstCoverScene;
    }

    public void setFirstCoverScene(Integer firstCoverScene) {
        this.firstCoverScene = firstCoverScene;
    }

    public Integer getSecondCoverScene() {
        return secondCoverScene;
    }

    public void setSecondCoverScene(Integer secondCoverScene) {
        this.secondCoverScene = secondCoverScene;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}


