package com.chinatower.event.entity.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

@Data
@Component
public class VirtualCheckMongo extends BaseMongoDTO {


    /**
     * 告警时间
     */
    @Field("alarmTime")
    private String alarmTime;

    /**
     * 事件编号
     */
    @Field("warningOrderId")
    private String warningOrderId;

    /**
     * 虚拟研判状态 (1:虚拟研判 0:取消虚拟研判)
     */
    @Field("virtualJudgeStatus")
    private String virtualJudgeStatus;

    /**
     * 虚拟研判处理人
     */
    @Field("virtualJudgeProcess")
    private String virtualJudgeProcess;

    /**
     * 虚拟研判时间
     */
    @Field("virtualJudgeTime")
    private String virtualJudgeTime;

    /**
     * 租户ID
     */
    @Field("tenantId")
    private String tenantId;

    /**
     * 行业编码
     */
    @Field("industryCode")
    private String industryCode;

    /**
     * 创建时间
     */
    @Field("createTime")
    private String createTime;

    /**
     * 修改时间
     */
    @Field("updateTime")
    private String updateTime;

    /**
     * 创建时间
     */
    @Field("createUser")
    private String createUser;

    /**
     * 修改时间
     */
    @Field("updateUser")
    private String updateUser;

}
