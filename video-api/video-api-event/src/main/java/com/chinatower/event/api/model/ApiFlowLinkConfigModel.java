package com.chinatower.event.api.model;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程环节配置实体
 * @author chmod
 * @version 1.0
 * @date 2022/3/25 15:37
 */
@Data
public class ApiFlowLinkConfigModel extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = 6009358923491257564L;
    /** 主键 */
    private Long linkId;
    /** 流程环节名称 */
    private String linkName;
    /** 环节优先级 */
    private Integer linkPriority;
    /** 创建时间 */
    private Date createDate;
    /** 修改时间 */
    private Date updateDate;
    /** 状态：1生效 0失效 */
    private String status;

    private String turnFlag;

    private String realLinkType;
    /** 流程ID */
    private Long flowId;
    /** 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开 */
    private String msgNotifyType;


    /**
     * 网格id
     */
    private String gridId;

    /**
     * 是否有网格员（1：有）
     */
    private String gridFlag;

    /**
     * 是否过滤数据权限（0否,1是）
     */
    private String dataFilterPermiss;
    /**
     * 是否展示客户管理员（0否,1是）
     */
    private String customAdmin;

    /**
     * 用户昵称（用于选择处理人模糊查询）
     */
    private String userName;

    /**
     * 流程环节属性id
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     */
    private String timeLimitAcceptance;
}
