package com.chinatower.event.dto;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 环节视图URL配置模型
 *
 * @author chmod
 * @version 1.0
 * @date 2022/3/25 17:44
 */
@Data
public class SysLinkViewUrlModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = -7071379696805229790L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 环节ID
     */
    private Long linkId;
    /**
     * 行业应用版本(多个,隔开)
     */
    private String appVerCode;
    /**
     * 环节专属客户客户
     */
    private String custVerCode;
    /**
     * 大屏环节URL
     */
    private String screenUrl;
    /**
     * PC环节URL
     */
    private String pcUrl;
    /**
     * app环节URL
     */
    private String appUrl;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建人
     */
    private String creator;
    private String creatorName;
    /**
     * 修改人
     */
    private String modifier;
    private String modifierName;
    /**
     * 状态：1有效 0无效
     */
    private String status;
    /**
     * 查询开启分页：true-开启，false-关闭
     */
    private boolean enablePage;
    private String linkName;
    private String appVerName;
    private String custVerName;

    /**
     * 创建时间查询
     */
    //时间(格式：yyyy-MM-dd HH:mm:ss）
    private String startDate;
    private String endDate;

    /**
     * 环节查询
     */
    private List<Long> linkIdList;

    /**
     * 行业应用版本(多个,隔开)
     */
    private List<String> appVerCodes;
    /**
     * 环节专属客户客户
     */
    private List<String> custVerCodes;
}
