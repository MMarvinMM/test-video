package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: fzw
 * @Description:
 * 告警接收Redis记录实体
 */
@Data
public class RedisPTVo  implements Serializable {

    /** 告警时间 */
    private String at;
    /** 告警点P值 */
    private String p;
    /** 告警点T值 */
    private String t;

}
