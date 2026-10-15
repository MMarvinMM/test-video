package com.chinatower.bdm.api.entity;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class GridKeeperRefVO implements Serializable {
    /**
     * 关系ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refId;

    /**
     * 网格员ID
     */
    private Long keeperId;

    /**
     * 责任网格ID
     */
    private String gridId;

    /**
     * 创建人
     */

    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 最后修改人
     */

    private String modifier;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 删除状态
     */
    private Integer deleteFlag;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    private String gridName;

    private String gridLevel;

    /**
     * 登录名
     */
    private String loginName;

    private static final long serialVersionUID = 1L;
}
