package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 锁信息实体类
 */
@Data
public class HornLockInfo implements Serializable {

    private String deviceCode;

    private String lockId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
