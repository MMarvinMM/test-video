package com.chinatower.event.api.factory;

import com.chinatower.event.api.ITradeExportWarningService;
import com.chinatower.event.api.model.DownLoadUrlsReq;
import com.chinatower.event.entity.dto.*;
import feign.Response;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TradeExportWarningFallbackFactory.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2023/09/15 09:53
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Component
public class TradeExportWarningFallbackFactory implements FallbackFactory<ITradeExportWarningService> {

    @Override
    public ITradeExportWarningService create(Throwable throwable) {
        return new ITradeExportWarningService() {

            @Override
            public ResponseEntity<byte[]> downloadImgs(HttpServletRequest request, AlarmDownloadReq req) {
                log.info("调用video-export服务下载告警图片请求失败！");
                return null;
            }

            @Override
            public Response downloadImgsResponse(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) {
                log.info("调用video-export服务下载告警图片请求失败！");
                return null;
            }
            @Override
            public void handleReceiveAlarmImg(@RequestBody HandleAlarmImgDTO handleAlarmImgDTO) {
                log.info("调用video-export管线处理图片请求失败！");
            }

            @Override
            public void downloadByUrls(DownLoadUrlsReq req) {
                log.info("调用video-export飞行成果图片/视频下载失败！");
            }
        };
    }
}
