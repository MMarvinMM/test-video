package com.chinatower.stream.controller;


import cn.hutool.json.JSONUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.stream.model.BroadcastVO;
import com.obs.services.internal.ServiceException;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioInputStream;
import javax.websocket.server.PathParam;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用请求处理
 *
 * @author chinatower
 */
@Slf4j
@RestController
public class StreamUpdateController {

    @Value("${stream.rtsp:rtsp://127.0.0.1/}")
    private String rtsp;

    private static ConcurrentHashMap<String, FFmpegFrameRecorder> recorderHashMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> broadcastHashMap = new ConcurrentHashMap<>();

    /**
     * 通用上传请求
     */
    @PostMapping("/stream/update/voice")
    public AjaxResult uploadVoiceFile(@RequestPart(value = "file") MultipartFile file, @PathParam(value = "id") String id) {
        try {
            // 上传并返回新文件名称
            byte[] fileBytes = file.getBytes();
//            ByteBuffer message = ByteBuffer.wrap(fileBytes);
//
//            byte[] bytes = message.array();
            // 实例化帧抓取器对象，将文件路径传入
            MpegAudioFileReader mp = new MpegAudioFileReader();
            AudioInputStream inputStream = mp.getAudioInputStream(new ByteArrayInputStream(fileBytes));

            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
            grabber.setFormat("mp3");
            grabber.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
            log.info("开始初始化帧抓取器");
            grabber.start();
            log.info("grabber.start()");
            if (recorderHashMap.get(id) == null) {
                createRecorder(grabber, id);
            }
            Thread.sleep(200);
            // 持续从视频源取帧
            Frame frame;
            while (null != (frame = grabber.grab())) {
                recorderHashMap.get(id).record(frame);
            }
            // 关闭帧抓取器
            grabber.close();
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 创建RTSP帧推送
     * @param grabber
     * @param id
     */
    private void createRecorder (FFmpegFrameGrabber grabber, String id) {
        String url = rtsp + id + ".sdp";
        log.info("url=================>{}", url);
        int frameRate = (int)grabber.getAudioFrameRate();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(url, 1);
        //缓存
        recorder.setOption("buffer_size", "102400");
        //超时时间
        recorder.setOption("stimeout", "200000");
        //rtsp tcp
        recorder.setOption("rtsp_transport", "tcp");
        // 音频编/解码器
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.setAudioCodecName(grabber.getAudioCodecName());
        // 音频比特率
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.setAudioChannels(grabber.getAudioChannels());
        recorder.setAudioOption("tune", "zerolatency");
        recorder.setAudioOption("preset", "ultrafast");
        recorder.setAudioOption("crf", "0");
        recorder.setSampleRate(grabber.getSampleRate());
        //封装格式
        recorder.setFormat("rtsp");
        //关键帧间隔，一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(frameRate);
        try {
            log.info("recorder.start()");
            recorder.start();
            log.info("recorder.end()");
        } catch (FFmpegFrameRecorder.Exception e) {
            throw new ServiceException("FFmpegFrameRecorder启动异常", e);
        }
        log.info("url=================>{}", url);
        recorderHashMap.put(id,recorder);
//
//        //通知广播
//        noticeBroadcast(id, url);
    }
}
