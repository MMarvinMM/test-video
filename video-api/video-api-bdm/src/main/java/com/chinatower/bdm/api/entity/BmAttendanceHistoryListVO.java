package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class BmAttendanceHistoryListVO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 1L;
//    @Excel(name = "网格员ID")
    private String keeperId;
    @Excel(name = "账号")
    private String loginName;
    @Excel(name = "网格员姓名")
    private String keeperName;
    @Excel(name = "所属网格")
    private String gridName;
    @Excel(name = "打卡日期")
    private String date;
    @Excel(name = "打卡时间")
    private String time;
    @Excel(name = "打卡状态",readConverterExp = "1=正常签到,2=迟到,3=未签到,4=正常签退,5=早退,6=未签退,7=缺勤")
    private String status;


    /**
     *     签到结束时间
     */
    private String signInTime;

    /**
     *     签到结束时间
     */
    private String signOutTime;

    /**
     * 上下午标记 AM PM
     */
    private String type;

    private String gridId;
}
