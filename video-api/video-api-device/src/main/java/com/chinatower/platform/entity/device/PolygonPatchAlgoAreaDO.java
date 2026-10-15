package com.chinatower.platform.entity.device;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 算法与图斑识别区域关系表
 * @author zhaozj
 * @since 2022-04-10
 */
@Getter
@Setter
@TableName("vm_polygon_patch_algo_area_rela")
public class PolygonPatchAlgoAreaDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一编码
     */
    @TableId(value = "unique_id")
    private Long uniqueId;

    /**
     *算法配置唯一编号
     */
    @TableField(value = "config_id")
    private String configId;

    //识别区域与图斑关系唯一编码
    @TableField(value = "polygon_id")
    private String polygonId;

    /**
     *识别区域唯一编码
     */
    @TableField(value = "monitor_area_id")
    private String monitorAreaId;

    @TableField(value = "creator")
    private String creator;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "modifier")
    private String modifier;

    @TableField(value = "update_time")
    private String updateTime;
    /**
     * 删除标识 正常0  删除1
     */
    @TableField(value = "delete_flag")
    private int deleteFlag;

}
