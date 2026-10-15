package com.chinatower.video.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @ClassName: VideoPlayerQueryVideoListData
 * @Description: 视频查询通道录像列表返回实体
 * @Author: ldl
 * @Date: 2022/1/18
 **/
@Data
public class VideoPlayerQueryVideoListData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 当前查询数量
     */
    private int totalNum;

    /**
     * 文件列表
     */
    private List<VideoPlayerVideos> videos;
}
