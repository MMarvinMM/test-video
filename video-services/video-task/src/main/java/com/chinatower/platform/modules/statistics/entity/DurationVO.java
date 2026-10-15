package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *@description: 事件完结处理时长沉淀表字段vo
 *@param:
 *@return:
 *@author: zhaozj@strongdata.com.cn
 *@date: 2021/11/9 11:25
 */
@Data
public class DurationVO implements Serializable {
    private static final long serialVersionUID = 1L;
    //租户编码
    private String tenantId;
    //省编码
    private String provinceId;
    //市编码
    private String cityId;
    //区县编码
    private String countyId;
    //乡镇编码
    private String townId;
    //省编码
    private String provinceCode;
    //市编码
    private String cityCode;
    //区县编码
    private String countyCode;
    //设备编号
    private String deviceCode;
    //设备名称
    private String deviceName;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //更新时间
    private String modifierDate;
    private int todayAll;
    private int warningNum;
    private int lessOne;
    private int oneFive;
    private int fiveTen;
    private int moreTen;
    private String handleYear;
    private String alarmTime;
    private int yesterdayAll;
    private int falseAll;
    private int todayProcess;
    private int todayComplete;
    private int penddingReceive;
    private int penddingJudged;
    private int pendingDispatched;
    private int peddingDisposal;
    private int pendingApprove;
    private int penddingLeaderApprove;
    private int overtime;

    //组织ID
    private String orgId;
    //应用版本
    private String appVerCode;
    //告警来源
    private String warningSource;
    //行业编码
    private String industryCode;

    private String yearStr;
    private String monthStr;
    private String dayStr;
    private int handleDuration;
    private int durationNum;
    private String regionName;
    private String  handleDateTime;
    public DurationVO() {
    }

    public DurationVO(String tenantId, String provinceId, String cityId, String countyId, String deviceCode, String warningSource,
                      int lessOne, int oneFive, int fiveTen, int moreTen, String industryCode) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.lessOne = lessOne;
        this.oneFive = oneFive;
        this.fiveTen = fiveTen;
        this.moreTen = moreTen;
        this.warningSource = warningSource;
        this.industryCode = industryCode;
    }
    public DurationVO(String tenantId, String provinceId, String cityId, String countyId, String deviceCode, String warningSource,
                      int lessOne, int oneFive, int fiveTen, int moreTen, String industryCode, String modifierDate,String alarmTime, String yearStr, String monthStr, String dayStr) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.lessOne = lessOne;
        this.oneFive = oneFive;
        this.fiveTen = fiveTen;
        this.moreTen = moreTen;
        this.warningSource = warningSource;
        this.industryCode = industryCode;
        this.modifierDate = modifierDate;
        this.alarmTime = alarmTime;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
    }

    public DurationVO(String tenantId, String provinceId, String cityId, String countyId, String deviceCode, String warningSource,
                       String industryCode, String modifierDate,String alarmTime, String yearStr, String monthStr, String dayStr,String regionName,int handleDuration,int durationNum) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.lessOne = lessOne;
        this.oneFive = oneFive;
        this.fiveTen = fiveTen;
        this.moreTen = moreTen;
        this.warningSource = warningSource;
        this.industryCode = industryCode;
        this.modifierDate = modifierDate;
        this.alarmTime = alarmTime;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
        this.regionName = regionName;
        this.handleDuration = handleDuration;
        this.durationNum = durationNum;
    }

    public DurationVO(String tenantId, String provinceCode, String cityCode, String countyCode,
                     String modifierDate,String alarmTime, String yearStr, String monthStr, String dayStr,String regionName,int handleDuration,int durationNum) {
        this.tenantId = tenantId;
        this.provinceCode = provinceCode;
        this.cityCode= cityCode;
        this.countyCode = countyCode;
        this.modifierDate = modifierDate;
        this.alarmTime = alarmTime;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
        this.regionName = regionName;
        this.handleDuration = handleDuration;
        this.durationNum = durationNum;
    }


    public DurationVO(int moreTen, String provinceId, String cityId, String countyId,
                      int lessOne, int oneFive, int fiveTen) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.lessOne = lessOne;
        this.oneFive = oneFive;
        this.fiveTen = fiveTen;
        this.moreTen = moreTen;
    }

    public DurationVO(String tenantId, String provinceId, String cityId, String countyId, String deviceCode, String warningSource, String appVerCode, int todayAll, String industryCode,String handleYear) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.warningSource = warningSource;
        this.todayAll = todayAll;
        this.appVerCode = appVerCode;
        this.industryCode = industryCode;
        this.handleYear = handleYear;
    }

    public DurationVO(int todayAll, String provinceId, String cityId, String countyId) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.todayAll = todayAll;
    }
    public DurationVO(int todayAll, String provinceId, String cityId, String countyId,String handleYear) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.todayAll = todayAll;
        this.handleYear = handleYear;
    }
    public DurationVO(String tenantId, String countyId, String deviceCode, String longitude, String latitude, int warningNum) {
        this.tenantId = tenantId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.warningNum = warningNum;
    }

    public DurationVO(int warningNum, String provinceId, String cityId, String countyId, String longitude, String latitude) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.warningNum = warningNum;
    }

    public DurationVO(String tenantId, String countyId, String deviceCode, String modifierDate,
                      int lessOne, int oneFive, int fiveTen, int moreTen) {
        this.tenantId = tenantId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.modifierDate = modifierDate;
        this.lessOne = lessOne;
        this.oneFive = oneFive;
        this.fiveTen = fiveTen;
        this.moreTen = moreTen;
    }


    public DurationVO(String tenantId, String provinceId, String cityId, String countyId,String townId, String deviceCode, String appVerCode, String warningSource,int yesterdayAll, int todayAll, int falseAll,
                      int todayProcess, int todayComplete, int penddingReceive, int penddingJudged,
                      int pendingDispatched, int peddingDisposal, int pendingApprove, int overtime, String industryCode) {
        this.yesterdayAll = yesterdayAll;
        this.todayAll = todayAll;
        this.falseAll = falseAll;
        this.todayProcess = todayProcess;
        this.todayComplete = todayComplete;
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.townId = townId;
        this.deviceCode = deviceCode;
        this.penddingReceive = penddingReceive;
        this.penddingJudged = penddingJudged;
        this.pendingDispatched = pendingDispatched;
        this.peddingDisposal = peddingDisposal;
        this.pendingApprove = pendingApprove;
        this.overtime = overtime;
        this.appVerCode = appVerCode;
        this.warningSource = warningSource;
        this.industryCode = industryCode;
    }

    public DurationVO(String handleDateTime,String tenantId, String provinceId, String cityId, String countyId,String townId, String deviceCode, String appVerCode, String warningSource,int yesterdayAll, int todayAll, int falseAll,
                      int todayProcess, int todayComplete, int penddingReceive, int penddingJudged,
                      int pendingDispatched, int peddingDisposal, int pendingApprove, int overtime, String industryCode) {
        this.yesterdayAll = yesterdayAll;
        this.todayAll = todayAll;
        this.falseAll = falseAll;
        this.todayProcess = todayProcess;
        this.todayComplete = todayComplete;
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.townId = townId;
        this.deviceCode = deviceCode;
        this.penddingReceive = penddingReceive;
        this.penddingJudged = penddingJudged;
        this.pendingDispatched = pendingDispatched;
        this.peddingDisposal = peddingDisposal;
        this.pendingApprove = pendingApprove;
        this.overtime = overtime;
        this.appVerCode = appVerCode;
        this.warningSource = warningSource;
        this.industryCode = industryCode;
        this.handleDateTime = handleDateTime;
    }




    public DurationVO(String handleDateTime,int overtime, String tenantId, String provinceId, String cityId, String countyId,String townId, int yesterdayAll, int todayAll,
                      int falseAll, int todayProcess, int todayComplete, int penddingReceive,
                      int penddingJudged, int pendingDispatched, int peddingDisposal, int pendingApprove) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.townId = townId;
        this.yesterdayAll = yesterdayAll;
        this.todayAll = todayAll;
        this.falseAll = falseAll;
        this.todayProcess = todayProcess;
        this.todayComplete = todayComplete;
        this.penddingReceive = penddingReceive;
        this.penddingJudged = penddingJudged;
        this.pendingDispatched = pendingDispatched;
        this.peddingDisposal = peddingDisposal;
        this.pendingApprove = pendingApprove;
        this.overtime = overtime;
        this.handleDateTime =handleDateTime;
    }


    public DurationVO(String tenantId, String orgId, int yesterdayAll, int todayAll, int falseAll,
                      int todayProcess, int todayComplete, int penddingReceive, int penddingJudged,
                      int pendingDispatched, int peddingDisposal, int pendingApprove, int overtime) {
        this.yesterdayAll = yesterdayAll;
        this.todayAll = todayAll;
        this.falseAll = falseAll;
        this.todayProcess = todayProcess;
        this.todayComplete = todayComplete;
        this.tenantId = tenantId;
        this.orgId = orgId;
        this.penddingReceive = penddingReceive;
        this.penddingJudged = penddingJudged;
        this.pendingDispatched = pendingDispatched;
        this.peddingDisposal = peddingDisposal;
        this.pendingApprove = pendingApprove;
        this.overtime = overtime;
    }

}
