package com.chinatower.common.core.vo;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 18:20
 */
@Data
public class TowerCommunicationVo extends PageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String deptId;
    private String communicationId;
    @Excel(name = "节点编号")
    private String nodeCode;
    @Excel(name = "节点名称")
    private String nodeName;
    @Excel(name = "客户ID")
    private String tenantId;
    @Excel(name = "客户名称")
    private String tenantName;
    @Excel(name = "行业应用")
    private String industryName;
    @Excel(name = "应用版本")
    private String industryVersion;
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
    private String avatar;
    private String operationNode;
}
