package com.chinatower.video.modules.arButton.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: video-platform
 * @ClassName: ArButtonAttachInfo
 * @description: AR大屏配置附件实体类
 * @author: fengty
 * @create: 2024-04-07 13:59
 */
@Data
public class ArButtonAttachInfo {

    private long id;
    private long buttonId;
    private String attachPath;
    private String fileName;
    private Date createTime;
    private Date updateTime;
    private String creator;
    private String modifier;
    private int deleteFlag;
    // 附件类型 1是图片  2是视频  3是文件
    private Integer attachType;
}
