package com.chinatower.event.dataobject;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 告警事件图片信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Getter
@Setter
@TableName("vm_warning_img_t")
public class WarningImgDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一编码
     */
    private String fileCode;

    /**
     * 文件类型：1框图；2原图
     */
    private String fileType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 图片URL
     */
    private String fileUrl;

    /**
     * 告警工单id
     */
    private String warningOrderId;


}
