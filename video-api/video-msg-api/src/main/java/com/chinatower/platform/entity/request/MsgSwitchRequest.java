package com.chinatower.platform.entity.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSwitchRequest implements Serializable {

    private String id;

    private String userId;

    /**
     * 超期提醒 1-开启  0-关闭
     */
    private Integer alarmOverdueSwitch;

    /**
     * 公告 1-开启  0-关闭
     */
    private Integer  noticeSwitch;

    /**
     * 其他 1-开启  0-关闭
     */
    private Integer  otherSwitch;

    private Integer deleteFlag;

    private String industryCode;

    private String tenantId;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

}
