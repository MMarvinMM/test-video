package com.chinatower.video.modules.artags.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签收藏表
 * @TableName vm_ar_tags_tree_user_rel
 */
@TableName(value ="vm_ar_tags_tree_user_rel")
@Data
public class ArTagsTreeUserRel implements Serializable {
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
     * 收藏对象编码
     */
    @TableField(value = "coll_obj_code")
    private String collObjCode;

    /**
     * 收藏对象类型
     */
    @TableField(value = "coll_obj_type")
    private String collObjType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}