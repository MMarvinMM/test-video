package com.chinatower.event.vo;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ParkDeviceInfoReq extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 公园编号 */
    private Long parkCode;

    /** 公园名称 */
    private String parkName;

    /** 公园承载量 */
    private String parkCarryingCapacity;

    /** 今日人数统计 */
    private String parkTrafficCount;

    /** 摄像机关联数 */
    private String parkDeviceCodes;

    /** 摄像机名称 */
    private String devName;

    /** 公园描述 */
    private String parkDesc;

    /** 摄像机名称 */
    private String parkDeviceName;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private Date creatorDate;

    /** 修改人 */
    private String modifier;

    /** 修改时间 */
    private Date modifierDate;

    /** 0正常 1删除 */
    private Integer deleteFlag;

    /** 租户编码 */
    private String tenantId;

    /** 当日人数修改时间 */
    private Date modifierCountDate;
    /** 行业编码*/
    private String industryCode;
    /**行业版本*/
    private String appVerCode;
    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
    /** sql排序 */
    private String orderSql;

    /**业务表名*/
    private String tableName;

    /**查公园人数标识*/
    private String parkRemark;
}
