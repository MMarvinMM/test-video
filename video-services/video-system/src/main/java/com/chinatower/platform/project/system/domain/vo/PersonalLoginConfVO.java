package com.chinatower.platform.project.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * 个性化登录页面配置表 vm_personal_login_conf_t
 *
 * @author liuxt
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalLoginConfVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 系统logo url
     */
    private String systemLogoUrl;

    /**
     * 背景图url,多个图片用逗号分割
     */
    private String backgroundImageUrl;

    /**
     * 客户id
     */
    private String tenantId;

    /**
     * 配置类型：1-客户配置 2-铁塔配置（由当前用户 userType：0→1，非0→2）
     */
    private Integer configType;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除状态：0正常 1删除
     */
    private String deleteFlag;


    /**
     *  加密的客户id
     */
    private String loginId;
}