/*
package com.chinatower.platform.controller;

import com.chinatower.event.entity.dto.DownloadVideoReq;
import com.chinatower.platform.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

*/
/**
 * @Author: songxinfeng
 * @Date: 2025/3/26 16:14
 *//*


@RestController
@Slf4j
@RequestMapping(value = "/alarm/ffmpeg")
public class TestFfmpegController {

    @Value("${path.temp}")
    private String path;

    private static final String FFMPEG_PATH = "ffmpeg"; // 假设 ffmpeg 已在系统路径中
    //private static final String INPUT_VIDEO = "/root/888.mp4";
    private static final String FONT_PATH = "/root/font/HarmonyOS_Sans_SC_Bold.ttf";
    private static final int SEGMENT_DURATION = 60; // 每个视频片段时长为 60 秒
    private static final String SEGMENT_PATTERN = "segment%03d.mp4";
    private static final String PROCESSED_PATTERN = "processed_segment%03d.mp4";
    //private static final String FINAL_OUTPUT = "out9.mp4";
    */
/**
     *
     * 导出告警事件列表
     * 功能描述 导出告警事件列表
     *
     * @author lyy
     * @date 2022/12/09 15:39
     * @version:
     *//*

    @PostMapping("/test")
    public void test(@RequestBody DownloadVideoReq req, HttpServletResponse response) throws InterruptedException, IOException {
        String beforPath = path + "/" + "ceshi-" + "old" + ".mp4";
        String newFilePath = path + "告警视频-" + "new" + "-" + ".mp4";
        log.info("开始下载视频:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        this.downloadPic(req.getVideoUrls().get(0), beforPath);
        log.info("下载视频结束，开始分割视频:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        // 分割视频
        splitVideo(beforPath);
        log.info("分割视频结束，开始合成所有视频水印:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        // 获取所有分割后的视频片段
        List<File> segmentFiles = getSegmentFiles();
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(30);

        // 并行处理每个视频片段
        for (int i = 0; i < segmentFiles.size(); i++) {
            log.info("第"+i+"个视频开始合成:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
            File segment = segmentFiles.get(i);
            String output = String.format(PROCESSED_PATTERN, i);
            executorService.submit(() -> processVideoSegment(segment.getAbsolutePath(), output));
            log.info("第"+i+"个视频合成结束:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        }

        // 关闭线程池并等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        log.info("全部视频合成水印结束，开始合成切分合成水印文件:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        //将生成的带水印视频加入到zip中并删除所生成视频
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        // 合并处理后的视频片段
        mergeVideoSegments(newFilePath);
        log.info("切分视频合并完成，开始删除临时文件视频:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));

        // 删除分割后的原始视频片段和处理后的视频片段
        deleteSegments();
        log.info("删除临时文件视频完成，开始放置zip包:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));

        // 将新文件添加到 ZIP 包中并删除源文件
        File file = new File(newFilePath);
        if (file.exists()) {
            try {
                addToZip(file, file.getName(), zos);
                log.info("放置zip包完成:{}",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));

                // 删除原始文件
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("新文件已成功添加到 ZIP 包并被删除。");
                } else {
                    log.info("无法删除新文件。");
                }
            } catch (Exception e) {
                log.error("将文件添加到ZIP包中异常:" + e);
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // 将新文件添加到 ZIP 包中并删除源文件
        File file2 = new File(beforPath);
        if (file2.exists()) {
            try {
                // 删除原始文件
                boolean deleted = file2.delete();
                if (deleted) {
                    log.info("原文件已成功删除。");
                } else {
                    log.info("无法删除新文件。");
                }
            } catch (Exception e) {
                log.error("将文件添加到ZIP包中异常:" + e);
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private static void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        FileInputStream fis =null;
        BufferedInputStream bis = null;
        try{
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            // 添加 ZIP 包条目
            zos.putNextEntry(new ZipEntry(fileName));

            // 将文件内容写入 ZIP 包
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }catch (Exception e){
            log.error("addToZip");
        }finally {
            // 关闭文件输入流和 ZIP 包条目
            bis.close();
            fis.close();
        }
    }
    public void downloadPic(String pidUrl,String path) throws IOException {
        InputStream input =null;
        FileOutputStream output = null;
        try {
            // 打开连接
            URL url = new URL(pidUrl);
            URLConnection connection = url.openConnection();
            // 设置请求超时为5秒
            connection.setConnectTimeout(5 * 1000);
            // 读取数据流并保存到本地
            input = connection.getInputStream();
            byte[] data = new byte[1024];
            int len;
            output = new FileOutputStream(path);
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
            log.info("图片保存成功：" + path);
        } catch (IOException e) {
            log.error("图片保存失败：" + e);
        }finally {
            if(output != null){
                output.close();
            }
            if(input != null){
                input.close();
            }
        }

    }

    private static void splitVideo(String beforPath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                FFMPEG_PATH,
                "-i", beforPath,
                "-c", "copy",
                "-map", "0",
                "-segment_time", String.valueOf(SEGMENT_DURATION),
                "-f", "segment",
                SEGMENT_PATTERN
        );
        processBuilder.inheritIO().start().waitFor();
    }

    private static List<File> getSegmentFiles() {
        List<File> segmentFiles = new ArrayList<>();
        File directory = new File(".");
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(SEGMENT_PATTERN.split("%")[0])) {
                    segmentFiles.add(file);
                }
            }
        }
        return segmentFiles;
    }

    private static void processVideoSegment(String segmentPath, String outputPath) {
        try {
            log.info("个视频开始合成:{},时间:{}",outputPath,DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
            String drawtextFilter = "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=100:y=0:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=300:y=150:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=500:y=300:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=700:y=450:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=900:y=600:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=1100:y=750:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=1300:y=900:alpha=0.5," +
                    "drawtext=fontcolor=white:fontfile=" + FONT_PATH + ":fontsize=90:text='中国铁塔':x=1500:y=1050:alpha=0.5";

            ProcessBuilder processBuilder = new ProcessBuilder(
                    FFMPEG_PATH,
                    "-c:v", "h264_cuvid",
                    "-i", segmentPath,
                    "-vf", drawtextFilter,
                    "-c:v", "h264_nvenc",
                    "-y", outputPath
            );
            processBuilder.inheritIO().start().waitFor();
            log.info("个视频开始合成完毕:{},时间:{}",outputPath,DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",new Date()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mergeVideoSegments(String newFilePath) throws IOException, InterruptedException, IOException {
        // 创建包含所有视频片段路径的文本文件
        List<File> processedFiles = getProcessedFiles();
        StringBuilder listContent = new StringBuilder();
        for (File file : processedFiles) {
            listContent.append("file '").append(file.getName()).append("'\n");
        }
        java.io.FileWriter writer = new java.io.FileWriter("list.txt");
        writer.write(listContent.toString());
        writer.close();

        // 合并视频片段
        ProcessBuilder processBuilder = new ProcessBuilder(
                FFMPEG_PATH,
                "-f", "concat",
                "-safe", "0",
                "-i", "list.txt",
                "-c", "copy",
                newFilePath
        );
        processBuilder.inheritIO().start().waitFor();

        // 删除临时的文本文件
        new File("list.txt").delete();
    }

    private static List<File> getProcessedFiles() {
        List<File> processedFiles = new ArrayList<>();
        File directory = new File(".");
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(PROCESSED_PATTERN.split("%")[0])) {
                    processedFiles.add(file);
                }
            }
        }
        return processedFiles;
    }

    private static void deleteSegments() {
        List<File> segmentFiles = getSegmentFiles();
        List<File> processedFiles = getProcessedFiles();
        for (File file : segmentFiles) {
            file.delete();
        }
        for (File file : processedFiles) {
            file.delete();
        }
    }
}
*/
