package com.chinatower.product.api.modules.videosquare.api;

import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.product.api.modules.videosquare.entity.VideoRequestVO;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * @ClassName: VideoSquareAPI
 * @Description: 视频广场相关接口
 * @Author: zhaozj
 * @Date: 2021/5/6 16:07
 **/
public interface VideoSquareAPI {

    /**
     * 云台加锁申请
     * @param videoRequestVO
     * @return
     */
    @PostMapping("/video/doLock")
    public ProcessResult doLock(VideoRequestVO videoRequestVO);
}
