package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DateVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String year;
    private String month;
    private String day;
    private String startDate;
    private String endDate;
    private String tableName;
    private String handleDate;
    private String handleDateTime;
    private String handleMonth;
    private String handleYear;
    private String nowYear;
    private Date date;
    private String industryCode;
    private String mongoTableName;
    private List<String> orderIdList;
    private List<JudgeStatisticsVO> alarmTypeClassList;
    private String platFormCode;
    private List<String> upgradeColonyCodeList;
}
