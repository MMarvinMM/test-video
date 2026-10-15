package com.chinatower.platform.project.system.domain;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;
/**
 * 文件信息对象
 *
 * @author wangzic
 * @date 2020-04-03
 */
@Data
public class SysFileInfo extends PageDTO
{
    private static final long serialVersionUID = 1L;

    /** 唯一主键 */
    private String fileId;

    /** 文件分类 */
    private String fileClassify;

    /** 文件大小 */
    private String fileSize;

    /** 文件名称 */
    private String fileName;

    /** 文件路径 */
    private String fileUrl;

    /** 文件后缀 */
    private String fileSuffix;

    /** 删除标志（0未删除 2已删除） */
    private String delFlag;

    /** 创建者所属部门ID */
    private String deptId;


    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;
    /**
     * 原始字符串
     */
    private String originUrl;
}
