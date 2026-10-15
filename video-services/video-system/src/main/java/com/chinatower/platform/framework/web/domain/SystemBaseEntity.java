package com.chinatower.platform.framework.web.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Entity基类
 *
 * @author iwhalecloud
 */
@Data
public class SystemBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String creator;
    private String createBy;

    private Date createTime;

    private String updateBy;
    private String modifier;

    private Date updateTime;

    private String dataScope;

//    private Long userId;

    private String appVerCode;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;


}
