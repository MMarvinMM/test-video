package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户设备收藏表
 * @TableName vm_user_device_collection_t
 */
@TableName(value ="vm_user_device_collection_t")
@Data
public class UserDeviceCollection implements Serializable {
    /**
     * 表id
     */
    @TableId(value = "user_device_coll_id", type = IdType.AUTO)
    private Long userDeviceCollId;

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