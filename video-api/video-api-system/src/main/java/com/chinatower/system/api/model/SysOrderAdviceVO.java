package com.chinatower.system.api.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 意见反馈同步工单
 */

@Data
public class SysOrderAdviceVO {


    /**
     * 工单分类
     */
    private String proClassify;

    /**
     * 工单来源
     */
    private String systemSource;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 问题类型
     */
    private String proCls;
    /**
     * 工单类型
     */
    private String proType;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 工单优先级
     */
    private String proPri;
    /**
     * 工单标题
     */
    private String proTitle;

    /**
     * 提出时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date askTime;
    /**
     * 问题内容
     */
    @Size(min = 0, max = 200, message = "意见反馈内容不能超过200个字符")
    private String proContent;
    /**
     * 工单省份编码
     */
    private String province;

    /**
     * 创建人省份编码
     */
    private String creatorPro;

    /**
     * 文件地址
     */
    private List<FileDTO> files;

}
