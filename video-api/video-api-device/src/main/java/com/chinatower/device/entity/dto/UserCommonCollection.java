package com.chinatower.device.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.Serializable;
import java.util.List;

/**
 * 用户设备收藏表
 * @TableName vm_user_common_collection_t
 */
@TableName(value ="vm_user_common_collection_t")
@Data
public class UserCommonCollection implements Serializable {
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
    private List<String> collObjCodeList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}