package com.chinatower.platform.project.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SmsRecordQuery {

    private String phone;

    private String sendStat;

    private Date sendTimeBegin;

    private Date sendTimeEnd;

}
