package com.chinatower.product.api.modules.videosquare.mapper;

import com.chinatower.product.api.modules.videosquare.entity.VideoRequestVO;

import java.util.List;

/**
 * @ClassName: VideoSquareDao
 * @Description: VideoSquareDao
 * @Author: zhaozj
 * @Date: 2021-05-06 16:16
 **/
public interface VideoSquareDao {

    List<VideoRequestVO> queryOrderDevInfo(VideoRequestVO videoRequestVO);
}
