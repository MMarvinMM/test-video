package com.chinatower.platform.project.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @description 用户教程
 * @author luo_ji
 * @date 2022-12-15
 */
@Data
public class SysTutorialUser extends PageDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 教程id
     */
    private String tutorialId;

    /**
     * 教程标题
     */
//    @Size(min = 0, max = 30, message = "操作手册名称长度不能超过30个文字")
    private String tutorailTitle;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 租户是否可见（0：不可见 1：可见）
     */
    private Integer tenantIsLook;

    /**
     * 铁塔用户是否可见（0：不可见 1：可见）
     */
    private Integer towerIsLook;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 可见范围：1 只铁塔可见 2 只客户可见 3铁塔客户均可见
     */
    private String visibility;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 创建时间开始日期
     */
    private String createTimeStart;

    /**
     * 创建时间结束日期
     */
    private String createTimeEnd;

    /**
     * 修改时间开始日期
     */
    private String updateTimeStart;

    /**
     * 修改时间结束日期
     */
    private String updateTimeEnd;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 行业编码列表
     */
    private List<String> industryCodeList;

}