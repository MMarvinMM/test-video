package com.chinatower.video.modules.artags.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签收藏表
 * @TableName vm_ar_tags_user_rel
 */
@TableName(value ="vm_ar_tags_user_rel")
@Data
public class ArTagsUserRel implements Serializable {
    /**
     * 表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户id
     */
    @TableField(value = "tag_id")
    private String tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}