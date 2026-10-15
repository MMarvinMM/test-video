package com.chinatower.platform.entity.msg;

import com.chinatower.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * 短信记录表
 */
@Data
@ApiModel(value = "bs_sms_record",description = "短信实体")
public class BsSmsRecord extends BaseEntity
{
    private String id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 短信内容
     */
    private String smsTxt;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送结果（0失败 1成功）
     */
    private String sendResult;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 删除标志（0未删除 2已删除）
     */
    private String delFlag;

    /**
     * 创建者所属部门ID
     */
    private String deptId;

    /**
     * 动态密钥
     */
    private String keyId;

    /**
     * 短信网关  Rongyu 融昱   AIPai艾派   ZhongShang中商盛达
     */
    private String gateWay;

    /**
     * 返回消息ID
     */
    private String msgId;

    /**
     * 回执状态，DELIVRD 成功  失败 UNDELIV
     */
    private String sendStat;


    public BsSmsRecord(){}
    public BsSmsRecord(String phone, Date sendTime){
        this.phone=phone;
        this.sendTime=sendTime;
    }
}
