package com.chinatower.platform.project.system.domain;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName SysMessagePermission
 * @Description 用户消息权限表 -- sys_message_permission
 * @Author D
 * @Date 2022/2/28 20:08
 */
@Data
public class SysMessagePermission extends SystemBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long permissionId;

    /** 用户ID */
    private String userId;

    /** 权限设置 */
    private String permissionType;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;
}
