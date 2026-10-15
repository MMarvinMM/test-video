package com.chinatower.platform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: PresiteVO
 * @description: 预置位传参实体类
 * @author: fengty
 * @create: 2024-07-10 10:50
 */
@Data
public class PresiteVO {

    /**
     *  1：下载单个，2：全部下载
     */
    private int singleFlag;

    private String id;

    private int index;

    private List<String> ids;

    private int pageNum;

    private int pageSize;

    private Long presetId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    private String name;

    @JsonFormat(pattern = "yyyyMMddHHmmss", timezone="GMT+8")
    private Date nowTime;

    /**
     * 下载来源  0:单屏模式预置位抓图，1:视频抓图
     */
    private String downloadType;

    /**
     * downloadType=1时，批量下载的id集合
     */
    private List<String> idList;
}
