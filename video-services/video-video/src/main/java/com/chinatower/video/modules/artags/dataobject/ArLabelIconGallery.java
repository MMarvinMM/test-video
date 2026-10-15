package com.chinatower.video.modules.artags.dataobject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * AR标签图标库
 * vm_ar_label_icon_gallery
 * @author LQ
 */
@Data
public class ArLabelIconGallery implements Serializable {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 标签图标id
     */
    private String labelIconId;

    /**
     * 标签图标名称
     */
    private String labelIconName;

    /**
     * 标签图标url
     */
    private String labelIconUrl;

    /**
     * 删除标志位：0正常、1：删除
     */
    private Byte deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    @TableField(exist = false)
    private List<ArLabelIconGallery> arLabelIconGalleryList;

    private static final long serialVersionUID = 1L;
}