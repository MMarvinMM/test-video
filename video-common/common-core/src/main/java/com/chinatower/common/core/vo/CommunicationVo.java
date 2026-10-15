package com.chinatower.common.core.vo;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @author WLF
 * @description
 * @date 2024/10/21 11:26
 */
@Data
@ToString
public class CommunicationVo extends PageDTO {
    private String userId;
    private String communicationId;
    @Excel(name = "用户名")
    private String nickName;
    @Excel(name = "账号")
    private String userName;
    @Excel(name = "调度台号码")
    private String dispatchDeskNum;
    @Excel(name = "移动端号码")
    private String mobileTerminalNum;
    @Excel(name = "修改人")
    private String updateUser;
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String updateTimeSort;
    private String updateStartTime;
    private String updateEndTime;
    private String checkPhoneNumber;
    private String telNumberType;
    private String avatar;
}
