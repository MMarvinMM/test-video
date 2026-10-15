package com.chinatower.platform.project.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @program: video-platform
 * @ClassName: WhiteIp
 * @description: 用户登录Ip白名单
 * @author: fengty
 * @create: 2024-04-29 10:23
 */

@Data
public class WhiteIp {

    /** 主键 */
    private int id;

    /** 创建者 */
    private String creator;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /** 更新者 */
    private String modifier;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    /** ip起始号段 */
    private String ipStart;

    /** ip结束号段 */
    private String ipEnd;

    /** 绑定用户标识 */
    private String userId;

    /** 逻辑删除标识 0正常1删除 */
    private int deleteFlag;

    private int pageNum;

    private int pageSize;

}
