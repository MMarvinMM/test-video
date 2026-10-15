package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_alarm_sound_info")
public class AlarmSoundDO implements Serializable {
    /**
     * 告警声音ID
     */
    @TableId(value = "alarm_sound_id", type = IdType.AUTO)
    private Long alarmSoundId;
    /**
     * 客户ID
     */
    private String tenantId;
    /**
     * 告警声音名称
     */
    private String alarmSoundName;
    /**
     * 文件名称
     */
    private String docName;
    /**
     * 文件地址
     */
    private String docUrl;
    /**
     * 播放时间
     */
    private String playTime;
    /**
     * 创建人
     */
    private String createPerson;
    /**
     * 更新人
     */
    private String updatePerson;
    /**
     * 状态 0启用 1停用
     */
    private String statusCd;
    /**
     * 0 默认声音，1 其他声音
     */
    private String soundType;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 文件名称
     */
    private String appVerCode;
}
