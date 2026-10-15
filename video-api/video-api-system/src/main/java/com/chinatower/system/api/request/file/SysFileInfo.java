package com.chinatower.system.api.request.file;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件信息对象 sys_file_info
 *
 * @author wangzic
 * @date 2020-04-03
 */
@Data
@SuppressWarnings("all")
public class SysFileInfo implements Serializable
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


    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String dataScope;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;

    /** 请求参数 */
    private Map<String,Object> params = new HashMap<>();

}
