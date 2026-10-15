package com.chinatower.emergency.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应急用户表
 * vm_emergency_user
 * @author Liqi
 */
@Data
public class EmergencyUserVO implements Serializable {

    /**
     * 用户ID
     */
    private String emergencyUserId;

    /**
     * 用户昵称
     */
    private String emergencyUserAccount;


    /**
     * 用户昵称
     */
    private String emergencyUserName;


    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 移动端号码
     */
    private String mobileTerminalNum;
    /**
     * 控制台号码
     */
    private String dispatchDeskNum;


    //融合通信标识
    private boolean correspondenceFlag;


    private static final long serialVersionUID = 1L;
}