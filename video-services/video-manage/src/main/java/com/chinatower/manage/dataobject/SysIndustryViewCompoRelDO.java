package com.chinatower.manage.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 大屏组件关联表
 * @author peng.haibo
 * @date 2022/7/25
 */
@Data
@TableName("sys_industry_view_compo_rel")
public class SysIndustryViewCompoRelDO implements Serializable {
    private static final long serialVersionUID = -1345625228071712577L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 大屏ID */
    private Long industryViewId;

    /** 组件ID */
    private Long compoId;

    /** 创建时间 */
    private Date createTime;
}
