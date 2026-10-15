package com.chinatower.common.core.domain;


import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 意见反馈
 */

@Data
public class SysAdvice extends PageDTO {

    /**
     * ID
     */
    private Long adviceId;

    /**
     * 客户ID
     */
    private String tenantId;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 行业应用
     */
    private String industryName;

    /**
     * 应用版本
     */
    private String appVerCode;

    /**
     * 登录账号
     */
    private String userName;
    /**
     * 用户名
     */
    private String nickName;
    /**
     * 手机号码
     */
    private String phoneNumber;
    /**
     * 反馈类型
     */
    private String adviceType;
    /**
     * 提出端
     */
    private String adviceSource;

    /**
     * 提出时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 意见反馈内容
     */
    @Size(min = 0, max = 200, message = "意见反馈内容不能超过200个字符")
    private String adviceText;
    /**
     * 意见反馈附件json
     */
    private String adviceAttachJson;
    /**
     * 附件路径（废弃）
     *
     * @deprecated use adviceAttach
     */
    @Deprecated
    private String fileUrl;

    /**
     * 附件
     */
    private List<Attach> adviceAttach;

    private String userId;

    private String nodeCode;

    private String creatorPro;

    private String province;

    private String orderIndustryCode;

    private String orderIndustryName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attach {
        /**
         * 附件类型 1:图片,2:视频
         */
        private String type;
        /**
         * 附件地址
         */
        private String fileUrl;
        /**
         * 视频封面
         */
        private String coverUrl;


        private String originUrl;
    }

}
