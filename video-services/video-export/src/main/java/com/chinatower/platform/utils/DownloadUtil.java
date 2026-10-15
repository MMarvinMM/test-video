package com.chinatower.platform.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.text.UUID;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmOrderImgDto;
import com.chinatower.event.entity.dto.LocalDownloadVideoReq;
import com.chinatower.event.api.model.DownLoadUrlsReq;
import com.chinatower.platform.entity.PresiteVO;
import com.chinatower.platform.entity.VmVideoPresetSnapshotMongo;
import com.chinatower.platform.entity.WatermarkFormatVo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.video.api.VideoScreenConfigAPI;
import com.chinatower.video.api.entity.VideoScreenConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class DownloadUtil {
    @Value("${path.temp}")
    private String path;

    private String watermarkType;
    @Value("${watermark.type:gpu}")
    public void setWatermarkType(String type) {
        watermarkType = type;
    }

    private static String TFF_PATH = "/root/font/HarmonyOS_Sans_SC_Bold.ttf";

    private static String SAVE_SUCCESS = "目录创建成功！";

    private static String SAVE_ERROR = "目录创建失败！";

    private static String SAVE_WARN = "目录已存在！";

    private static String SAVE_VIDEO_ERROR = "合成视频文件异常";

    private static String SAVE_VIDEO_SUCCESS = "合成视频文件完成";

    private static String DELETE_ERROR= "无法删除新文件。";

    private static String QUERY_ORGFILE = "原文件已并被删除。";

    private static String DELETE_ORG_ERROR = "无法删除原文件。";

    private static String DELETE_ORGFILE_ERROR= "删除源文件异常";

    private static VideoScreenConfigAPI videoScreenConfigAPI;

    @Autowired
    public DownloadUtil(VideoScreenConfigAPI videoScreenConfigAPI) {
        DownloadUtil.videoScreenConfigAPI = videoScreenConfigAPI;
    }
    /**
     * 下载告警图片添加水印并生成zip包
     *
     * @param imgUrlList
     * @param watermarkText
     */
    public static void downLoad2(List<AlarmOrderImgDto> imgUrlList, String watermarkText, HttpServletResponse response) throws IOException {
        //List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        int fontSize = 90; // 水印字体大小
        // 逐个读取图片并添加水印
        //List<ZipOutputStream> list = new LinkedList<>();
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        ThreadParallelUtil.run(imgUrlList, o -> {
            getMarkImgs(o, watermarkText, fontSize, zipOut, response);
            return null;
        }, ThreadParallelUtil.ConfigFactory.DOWNLOAD_IMG);
        // 关闭 ZIP 输出流
        //zipOut.close();
        //log.info("一共几张图片:"+String.valueOf(list.size()));

    }

    public static void downLoad(List<AlarmOrderImgDto> imgUrlList, String watermarkText, HttpServletResponse response) throws IOException {
        //List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 逐个读取图片并添加水印
            for (AlarmOrderImgDto dto : imgUrlList) {
                // 逐个读取图片并添加水印
                try {
                    BufferedImage image = ImageIO.read(new URL(dto.getImgUrl()));
/*
                    try {
                        if (!Constants.NINE.equals(dto.getAlarmSource())) {
                            log.info("downLoadPhotoByFlush-图片宽度:{}，图片高度:{}", image.getWidth(), image.getHeight());
                            BigDecimal img = BigDecimal.valueOf(image.getWidth()).divide(BigDecimal.valueOf(image.getHeight()), 2, RoundingMode.HALF_UP);
                            BigDecimal ratio = BigDecimal.valueOf(16).divide(BigDecimal.valueOf(9), 2, RoundingMode.HALF_UP);
                            if (img.compareTo(ratio) > 0) {
                                fontSize = Math.round(90 * image.getWidth() / 1920);
                            } else {
                                fontSize = Math.round(90 * image.getHeight() / 1080);
                            }
                        }
                    } catch (Exception e) {
                        log.error("downLoadPhotoByFlush计算字体大小异常:" + e);
                    }
*/

                    BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, dto.getAlarmSource(),watermarkFormatVo);

                    String suffix = "";
                    if (dto.getImgUrl().contains("?")) {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1, dto.getImgUrl().lastIndexOf("?"));
                    } else {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1);
                    }
                    ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC + dto.getOrderId() + "-" + dto.getSerNumber() + "." + suffix);
                    zipOut.putNextEntry(zipEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(watermarkedImage, suffix, baos);
                    byte[] bytes = baos.toByteArray();
                    zipOut.write(bytes, 0, bytes.length);
                    zipOut.closeEntry();
                } catch (Exception e) {
                    log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                }
            }
            // 关闭 ZIP 输出流
            // zipOut.close();
        } catch (IOException e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        }
    }

    //根据水印配置处理水印后端对应数值
    public static WatermarkFormatVo handleWatermarkFormat(){
        WatermarkFormatVo voData = new WatermarkFormatVo();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 客户视角判断是否为铁塔切租户
        boolean switchFlag = SecurityUtils.isSwitch();
        SysUser user = loginUser.getUser();
        //如果是铁塔视角或者铁塔切租户视角 走默认水印配置
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(user.getUserType()) || switchFlag) {
           setWatermarkFormatValue(null,voData,Constants.ONE,user);
        }else {
            VideoScreenConfigVO vo = new VideoScreenConfigVO();
            vo.setUserId(user.getUserId());
            vo.setTenantId(user.getTenantId());
            vo.setIndustryCode(user.getIndustryCode());
            log.info("videoScreenConfigAPI.queryWaterMark入参:{}", JSONObject.toJSONString(vo));
            Result<VideoScreenConfigVO> result = videoScreenConfigAPI.queryWaterMark(vo);
            log.info("videoScreenConfigAPI.queryWaterMark结果:{}", JSONObject.toJSONString(result));
            //查询客户行业水印配置不为空，且已设置水印配置，否则走默认水印配置
            //是否设置水印（0未设置 1设置）
            if (ObjectUtil.isNotNull(result) && ObjectUtil.isNotNull(result.getData()) && Constants.ONE.equals(result.getData().getIsWatermark())){
                VideoScreenConfigVO data = result.getData();
                setWatermarkFormatValue(data,voData,Constants.TWO,user);
            }else{
                setWatermarkFormatValue(null,voData,Constants.THREE,user);
            }
        }
        log.info("handleWatermarkFormat结果:{}", JSONObject.toJSONString(voData));
        return voData;
    }

    //处理后端水印配置数值
    public static void setWatermarkFormatValue(VideoScreenConfigVO data, WatermarkFormatVo voData, String type,SysUser user){
        log.info("setWatermarkFormatValue入参:{}", JSONObject.toJSONString(type));
        // type：1 铁塔默认配置。 type：2 客户自定义配置   type：3 客户默认配置
        if (Constants.ONE.equals(type)){
            voData.setFontSize(60);
            voData.setTransparency(0.5f);
            voData.setWidth(850);
            voData.setHeight(470);
            voData.setWatermarkText("中国铁塔");
            voData.setDensityType(Constants.TWO);

        }else if (Constants.TWO.equals(type)){
            //字号
            voData.setFontSize(Integer.parseInt(data.getWatermarkFontSize())*3);
            //透明度
            voData.setTransparency(Float.parseFloat(data.getWatermarkTransparency().replace("%", ""))/100);
            //水印密度（1小 2中 3大） 存值密度水平高度和宽度
            voData.setDensityType(data.getWatermarkDensity());
            if (Constants.ONE.equals(data.getWatermarkDensity())){
                voData.setHeight(490);
                voData.setWidth(1110);
            }else if (Constants.TWO.equals(data.getWatermarkDensity())){
                voData.setHeight(470);
                voData.setWidth(850);
            }else if (Constants.THREE.equals(data.getWatermarkDensity())){
                voData.setHeight(290);
                voData.setWidth(780);
            }
            //水印内容类型（1客户名称 2客户ID 3用户账号 4用户姓名）
            if (Constants.ONE.equals(data.getWatermarkContentType())){
                voData.setWatermarkText(user.getTenantInfo().getTenantName());
            }else if (Constants.TWO.equals(data.getWatermarkContentType())){
                voData.setWatermarkText(user.getTenantId());
            }else if (Constants.THREE.equals(data.getWatermarkContentType())){
                voData.setWatermarkText(user.getUserName());
            }else if (Constants.FOUR.equals(data.getWatermarkContentType())){
                voData.setWatermarkText(user.getNickName());
            }
        }else if (Constants.THREE.equals(type)){
            voData.setDensityType(Constants.TWO);
            voData.setFontSize(60);
            voData.setTransparency(0.5f);
            voData.setWidth(850);
            voData.setHeight(470);
            voData.setWatermarkText(user.getNickName());
        }
        if (voData.getFontSize() * voData.getWatermarkText().length() >= voData.getWidth()){
            voData.setWidth(voData.getFontSize() * (voData.getWatermarkText().length()+2));
        }
    }

    public static void downLoadPresite(List<VmVideoPresetSnapshotMongo> imgUrlList, PresiteVO presiteVO, HttpServletResponse response) throws IOException {
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小
        // 多个元素时，生成压缩包
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; i < imgUrlList.size(); i++) {
                VmVideoPresetSnapshotMongo presite = imgUrlList.get(i);
                try {
                    BufferedImage image = ImageIO.read(new URL(presite.getImgUrl()));
                    BufferedImage watermarkedImage = addWatermark(image, presiteVO.getName(), fontSize, "",watermarkFormatVo);

                    String suffix = "";
                    if (presite.getImgUrl().contains("?")) {
                        suffix = presite.getImgUrl().substring(presite.getImgUrl().lastIndexOf(".") + 1, presite.getImgUrl().lastIndexOf("?"));
                    } else {
                        suffix = presite.getImgUrl().substring(presite.getImgUrl().lastIndexOf(".") + 1);
                    }

                    Date snapshotTime = presite.getSnapshotTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                    String snapshotTimeStr = formatter.format(snapshotTime);

                    ZipEntry zipEntry = null;
                    //下载来源  0:单屏模式预置位抓图，1:视频抓图
                    String downloadType = presiteVO.getDownloadType();
                    if(Constants.ONE.equals(downloadType)){
                        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
                        zipEntry = new ZipEntry(presite.getChannelName() + "-"
                                + presite.getPresetName() + "-"
                                + sdf.format(presite.getSnapshotTime()) + "." + suffix);
                    }else{
                        if (presiteVO.getSingleFlag() == 1) {
                            zipEntry = new ZipEntry(Constants.PARAM_PRESITE_PIC + snapshotTimeStr + "-" + presiteVO.getIndex() + "." + suffix);
                        } else {
                            zipEntry = new ZipEntry(Constants.PARAM_PRESITE_PIC + snapshotTimeStr + "-" + (i + 1) + "." + suffix);
                        }
                    }

                    zipOut.putNextEntry(zipEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(watermarkedImage, suffix, baos);
                    byte[] bytes = baos.toByteArray();
                    zipOut.write(bytes, 0, bytes.length);
                    zipOut.closeEntry();
                } catch (Exception e) {
                    log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                }
            }
        } catch (IOException e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        }
    }


    public static ResponseEntity<byte[]> downLoadStream(List<AlarmOrderImgDto> imgUrlList, String watermarkText) {
        //List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小
        ByteArrayOutputStream zipOutStream = new ByteArrayOutputStream();

        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(zipOutStream);
            // 逐个读取图片并添加水印
            for (AlarmOrderImgDto dto : imgUrlList) {
                // 逐个读取图片并添加水印
                try {
                    BufferedImage image = ImageIO.read(new URL(dto.getImgUrl()));

                    BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, dto.getAlarmSource(),watermarkFormatVo);

                    String suffix = "";
                    if (dto.getImgUrl().contains("?")) {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1, dto.getImgUrl().lastIndexOf("?"));
                    } else {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1);
                    }
                    ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC + dto.getOrderId() + "-" + dto.getSerNumber() + "." + suffix);
                    zipOut.putNextEntry(zipEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(watermarkedImage, suffix, baos);
                    byte[] bytes = baos.toByteArray();
                    zipOut.write(bytes, 0, bytes.length);
                    zipOut.flush();
                    zipOutStream.flush();
                    zipOut.closeEntry();
                    baos.close();
                } catch (Exception e) {
                    log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                }
            }
            // 关闭 ZIP 输出流
            zipOut.close();

            byte[] zipBytes = zipOutStream.toByteArray();
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "告警图片下载.zip");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            zipOutStream.close();
            return ResponseEntity.ok().headers(headers).body(zipBytes);
        } catch (Exception e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        }
        return null;
    }

    public static HttpServletResponse downLoadResponse(HttpServletResponse response, List<AlarmOrderImgDto> imgUrlList, String watermarkText) {
        //List<String> imageUrls = imgUrlList.stream().map(AlarmOrderImgDto::getImgUrl).collect(Collectors.toList());
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(response.getOutputStream());
            // 逐个读取图片并添加水印
            for (AlarmOrderImgDto dto : imgUrlList) {
                // 逐个读取图片并添加水印
                try {
                    BufferedImage image = ImageIO.read(new URL(dto.getImgUrl()));

                    BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, dto.getAlarmSource(),watermarkFormatVo);

                    String suffix = "";
                    if (dto.getImgUrl().contains("?")) {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1, dto.getImgUrl().lastIndexOf("?"));
                    } else {
                        suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1);
                    }
                    ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC + dto.getOrderId() + "-" + dto.getSerNumber() + "." + suffix);
                    zipOut.putNextEntry(zipEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(watermarkedImage, suffix, baos);
                    byte[] bytes = baos.toByteArray();
                    zipOut.write(bytes, 0, bytes.length);
                    zipOut.flush();
                    zipOut.closeEntry();
                } catch (Exception e) {
                    log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                }
            }
            // 关闭 ZIP 输出流
            zipOut.close();
            return response;
        } catch (Exception e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        }
        return null;
    }

    public static void getMarkImgs(AlarmOrderImgDto imageDto, String watermarkText, int fontSize, ZipOutputStream zipOut, HttpServletResponse response) {
        // 逐个读取图片并添加水印
        try {
            //ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
            BufferedImage image = ImageIO.read(new URL(imageDto.getImgUrl()));
            BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, "",null);
            getZip(imageDto, zipOut, watermarkedImage);
            /*ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC+imageDto.getOrderId()+"-"+imageDto.getSerNumber()+".jpg");
            zipOut.putNextEntry(zipEntry);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            log.info("写入zip开始:"+imageDto.getOrderId()+"-"+imageDto.getSerNumber());
            ImageIO.write(watermarkedImage, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            zipOut.write(bytes, 0, bytes.length);
            log.info("写入zip结束:"+imageDto.getOrderId()+"-"+imageDto.getSerNumber());
            zipOut.closeEntry();
            // 关闭 ZIP 输出流
            zipOut.close();
            list.add(zipOut);*/
        } catch (Exception e) {
            log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
        }
    }

    public synchronized static void getZip(AlarmOrderImgDto dto, ZipOutputStream zipOut, BufferedImage watermarkedImage) throws IOException {
        ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC + dto.getOrderId() + "-" + dto.getSerNumber() + ".jpg");
        zipOut.putNextEntry(zipEntry);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        log.info("写入zip开始:" + dto.getOrderId() + "-" + dto.getSerNumber());
        ImageIO.write(watermarkedImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        zipOut.write(bytes, 0, bytes.length);
        log.info("写入zip结束:" + dto.getOrderId() + "-" + dto.getSerNumber());
        zipOut.closeEntry();
    }

    private static BufferedImage addWatermark(BufferedImage image, String watermarkText, int fontSize, String alarmSource,WatermarkFormatVo watermarkFormatVo) throws IOException, FontFormatException {
        int width = image.getWidth();
        int height = image.getHeight();
        /*
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);*/
        if (Constants.NINE.equals(alarmSource)) {
            width = image.getWidth() * 2 / 3; // 将宽度缩小到原来的一半
            height = image.getHeight() * 2 / 3; // 将高度缩小到原来的一半
        }
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        // 创建一个新的 BufferedImage，用于绘制水印图片
        BufferedImage watermarkedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = watermarkedImage.createGraphics();

        // 将原始图片绘制到水印图片上
        g2d.drawImage(image, 0, 0, null);
        File fontFile = new File(TFF_PATH);

        // 设置水印字体和颜色
        Font font = Font.createFont(Font.PLAIN, fontFile).deriveFont((float) fontSize);

        /*InputStream is = TypeData.ClassName.class.getResourceAsStream("/HarmonyOS_Sans_SC_Bold.ttf");
        Font font = Font.createFont(Font.PLAIN,is).deriveFont((float) fontSize);*/
        //Font font = new Font("SimSun", Font.PLAIN, fontSize);
        g2d.setFont(font);
        //g2d.setColor(Color.GRAY);
        Color color = new Color(255, 255, 255); // 白色（R=255, G=255, B=255）
        g2d.setColor(color);

        // 计算水印文字的倾斜角度
        /*double angle = Math.atan((double) height / (double) width);
        angle = angle * 45 / Math.PI;*/
        double angle = Math.toRadians(30);

        // 旋转坐标系，以便实现斜着的水印
        g2d.rotate(-angle, width / 2, height / 2);
        // 设置水印透明度
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, watermarkFormatVo.getTransparency());
        g2d.setComposite(alpha);
        // 绘制水印文字
        for (int y = -height; y < height * 5; y += watermarkFormatVo.getHeight()) {
            for (int x = -width; x < width * 3; x += watermarkFormatVo.getWidth()) {
                g2d.drawString(watermarkFormatVo.getWatermarkText(), x, y);
            }
        }

        // 恢复坐标系
        g2d.rotate(angle, width / 2, height / 2);


        // 释放资源
        g2d.dispose();

        return watermarkedImage;
    }


    public void downLoadImgsAndVideo(List<AlarmOrderImgDto> imgUrlList, String watermarkText, HttpServletResponse response) throws IOException {
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 逐个读取图片并添加水印
            for (AlarmOrderImgDto dto : imgUrlList) {
                if (dto.getType().equals("1")) {
                    // 如果 dto 的 type 等于 1，则执行以下操作
                    // Your code for type 1
                    // 逐个读取图片并添加水印
                    try {
                        BufferedImage image = ImageIO.read(new URL(dto.getImgUrl()));

                        BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, dto.getAlarmSource(),watermarkFormatVo);

                        String suffix = "";
                        if (dto.getImgUrl().contains("?")) {
                            suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1, dto.getImgUrl().lastIndexOf("?"));
                        } else {
                            suffix = dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf(".") + 1);
                        }
                        ZipEntry zipEntry = new ZipEntry(Constants.PARAM_ALARM_PIC + dto.getOrderId() + "-" + dto.getSerNumber() + "." + suffix);
                        zipOut.putNextEntry(zipEntry);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(watermarkedImage, suffix, baos);
                        byte[] bytes = baos.toByteArray();
                        zipOut.write(bytes, 0, bytes.length);
                        zipOut.closeEntry();
                    } catch (Exception e) {
                        log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                    }
                } else if (dto.getType().equals("2")) {
                    LocalDownloadVideoReq localDownloadVideoReq = new LocalDownloadVideoReq();
                    localDownloadVideoReq.setVideoUrls(dto.getVideoUrls());
                    localDownloadVideoReq.setWarningOrderId(dto.getOrderId());
                    localDownloadVideoReq.setSerNumber(dto.getSerNumber());
                    downloadLocalVideosByFfmpeg(zipOut, response, localDownloadVideoReq, watermarkText);
                }
            }
            // 关闭 ZIP 输出流
            // zipOut.close();
        } catch (IOException e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载图片到临时路径
     *
     * @param pidUrl 图片地址
     * @param path   下载到的路径
     */
    public static void downloadPic(String pidUrl, String path) throws IOException {
        InputStream input = null;
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
        } finally {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    private static void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
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
        } catch (Exception e) {
            log.error("addToZip");
        } finally {
            // 关闭文件输入流和 ZIP 包条目
            bis.close();
            fis.close();
        }
    }

    public void downloadLocalVideosByFfmpeg(ZipOutputStream zos, HttpServletResponse response, LocalDownloadVideoReq req, String tenantName) throws Exception {
        // 获取当前的用户
        List<String> realUrls = new ArrayList<>();
        int j = 1;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info(SAVE_SUCCESS);
            } else {
                log.info(SAVE_ERROR);
            }
        } else {
            log.info(SAVE_WARN);
        }
        for (String url : req.getVideoUrls()) {
            String beforPath = path + "/" + req.getWarningOrderId() + "old" + j + ".mp4";
            //调用ofs获取可直接访问路径
            //url = getRealUrl(req.getPlatformCode(),url);
            downloadPic(url, beforPath);
            realUrls.add(beforPath);
            j++;
        }
        int i = Objects.isNull(req.getSerNumber()) ? 1 : req.getSerNumber();
        //初始化 从1开始
        i = (i == 0 ? 1 : i);
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            for (String videoUrl : realUrls) {
                String newFilePath = path + "告警视频-" + req.getWarningOrderId() + "-" + i + ".mp4";
                i++;
                // 第二象限+第四象限增加水印
                this.waterPrintByGpu(tenantName, videoUrl, newFilePath,"0",watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    try {
                        addToZip(file, file.getName(), zos);
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功添加到 ZIP 包并被删除。");
                        } else {
                            log.info(DELETE_ERROR);
                        }
                    } catch (Exception e) {
                        log.error("将文件添加到ZIP包中异常:" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                //将原文件删除
                File fileOld = new File(videoUrl);
                if (fileOld.exists()) {
                    try {
                        boolean deletedOld = fileOld.delete();
                        if (deletedOld) {
                            log.info(QUERY_ORGFILE);
                        } else {
                            log.info(DELETE_ORG_ERROR);
                        }
                    } catch (Exception e) {
                        log.error(DELETE_ORGFILE_ERROR);
                    } finally {
                        if (fileOld.exists()) {
                            fileOld.delete();
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error(SAVE_VIDEO_ERROR + e);
        } finally {
            zos.flush();
            for (String videoUrl : realUrls) {
                if (!StringUtils.isEmpty(videoUrl)) {
                    //将原文件删除
                    File fileOld = new File(videoUrl);
                    if (fileOld.exists()) {
                        fileOld.delete();
                    }
                }
            }
            log.info(SAVE_VIDEO_SUCCESS);
        }
    }

    public void downLoadVideoByFlush(InputStream videoStream, String watermarkText, HttpServletResponse response, String fileName, String isH265) {
        try {
            // 调用 downloadLocalVideoByFfmpeg 获取新文件的输入流
            downloadLocalVideoByFfmpeg(response, watermarkText, videoStream, fileName, isH265);
        } catch (IOException e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
            throw new IllegalStateException("下载视频失败", e);
        } catch (Exception e) {
            throw new IllegalStateException("下载视频失败", e);
        }
    }

    private void downloadLocalVideoByFfmpeg(HttpServletResponse response, String watermarkText, InputStream videoStream, String fileName, String isH265) throws IOException {
        // 说明：此处不能用固定文件名，否则并发请求会互相覆盖/删除导致 ffmpeg 报 “No such file or directory”
        String realUrls = "";
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info(SAVE_SUCCESS);
            } else {
                log.info(SAVE_ERROR);
            }
        } else {
            log.info(SAVE_WARN);
        }

        String safeFileName = new File(StringUtils.isEmpty(fileName) ? "video.mp4" : fileName).getName();
        String jobId = java.util.UUID.randomUUID().toString().replace("-", "");
        String beforPath = path + "/" + "old_" + jobId + "_" + safeFileName;
        String newFilePath = path + "/" + "wm_" + jobId + "_" + safeFileName;

        log.info("downloadLocalVideoByFfmpeg-start jobId={},fileName={},safeFileName={},isH265={},beforePath={},newPath={}",
                jobId, fileName, safeFileName, isH265, beforPath, newFilePath);
        downloadVedioByFlush(videoStream, beforPath);
        realUrls = beforPath;
        File beforeFile = new File(beforPath);
        log.info("downloadLocalVideoByFfmpeg-sourceSaved jobId={},exists={},sizeBytes={}",
                jobId, beforeFile.exists(), beforeFile.exists() ? beforeFile.length() : -1L);

        // 将生成的带水印视频并删除所生成视频
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            // 第二象限+第四象限增加水印
            long ffmpegStartMs = System.currentTimeMillis();
            this.waterPrintByGpu(watermarkText, realUrls, newFilePath, isH265,watermarkFormatVo);
            long ffmpegCostMs = System.currentTimeMillis() - ffmpegStartMs;

            File newFile = new File(newFilePath);
            log.info("downloadLocalVideoByFfmpeg-watermarkResult jobId={},exists={},sizeBytes={},costMs={}",
                    jobId, newFile.exists(), newFile.exists() ? newFile.length() : -1L, ffmpegCostMs);
            if (!newFile.exists()) {
                throw new FileNotFoundException(newFilePath + " (No such file or directory)");
            }

            // 返回新文件的输出
            try (FileInputStream fileInputStream = new FileInputStream(newFile)) {
                // 将文件内容写入响应输出流
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    response.getOutputStream().write(buffer, 0, length);
                }
                response.getOutputStream().flush();
            }
        } catch (Exception e) {
            log.error(SAVE_VIDEO_ERROR, e);
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(SAVE_VIDEO_ERROR, e);
        } finally {
            if (!StringUtils.isEmpty(realUrls)) {
                //将原文件删除
                File fileOld = new File(realUrls);
                if (fileOld.exists()) {
                    fileOld.delete();
                }
            }
            if (!StringUtils.isEmpty(newFilePath)) {
                //将原文件删除
                File fileNew = new File(newFilePath);
                if (fileNew.exists()) {
                    fileNew.delete();
                }
            }
            log.info(SAVE_VIDEO_SUCCESS);
        }
    }

    /**
     * 视频输入流方式保存原文件
     *
     * @param videoStream
     * @param path
     * @throws IOException
     */
    private void downloadVedioByFlush(InputStream videoStream, String path) throws IOException {
        // 注意：不关闭传入的 videoStream，避免影响上层对流的生命周期管理
        try (FileOutputStream output = new FileOutputStream(path)) {
            byte[] data = new byte[8192];
            int len;
            while ((len = videoStream.read(data)) != -1) {
                output.write(data, 0, len);
            }
            output.flush();
            log.info("视频流接口-视频保存成功：{}", path);
        } catch (IOException e) {
            log.error("视频保存失败：{}", path, e);
            throw e;
        }
    }


    /**
     * 视频添加水印方法
     *
     * @param tenantName
     * @param videoUrl
     * @param newFilePath
     * @throws IOException
     * @throws InterruptedException
     */
    public static void waterPrint(String tenantName, String videoUrl, String newFilePath,WatermarkFormatVo watermarkFormatVo) throws IOException, InterruptedException {
        String fontFilePath = TFF_PATH;
        // 第二象限+第四象限增加水印
        String drawText = "drawtext=fontcolor=white:fontfile=" + fontFilePath + ":fontsize="+(watermarkFormatVo.getFontSize()*2/3) + ":text='" + watermarkFormatVo.getWatermarkText() + "'";
        //初始值 默认中
        String x = "100";
        String y = "0";
        Integer size = 4;
        Integer xRate = 400;
        Integer yRate = 300;
        StringBuffer buffer = new StringBuffer();
        if (Constants.ONE.equals(watermarkFormatVo.getDensityType())){
            size = 3;
            xRate = 600;
            yRate = 450;
        }else if (Constants.THREE.equals(watermarkFormatVo.getDensityType())){
            size = 8;
            xRate = 200;
            yRate = 150;
        }
        for (int i = 1; i <= size; i++) {
            String command ="";
            command = drawText + ":x="+(100+xRate*(i-1))+":y="+yRate*(i-1)+Constants.ALPHA+watermarkFormatVo.getTransparency()+",";
            if (i == 1){
                command = "ffmpeg" + " -i " + videoUrl + " -vf " + drawText + ":x=100:y=0:alpha="+watermarkFormatVo.getTransparency()+",";
            }
            if (i == size){
                // 关键：避免默认CFR策略导致大量dup补帧（会把15分钟拉长到几十分钟）
                // ffmpeg 7 推荐使用 -fps_mode passthrough，尽量保持与输入一致的时间轴
                command = drawText + ":x="+(100+xRate*(i-1))+":y="+yRate*(i-1)+Constants.ALPHA+watermarkFormatVo.getTransparency()
                        + " -fps_mode passthrough -y " +  newFilePath;
            }
            buffer.append(command);
        }
        log.info("downloadVideosByFfmpeg-执行命令:" + buffer.toString());
        Process process = Runtime.getRuntime().exec(buffer.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        int exitCode = process.waitFor();
        File out = new File(newFilePath);
        log.info("waterPrint-finished exitCode={},outputExists={},outputSizeBytes={}",
                exitCode, out.exists(), out.exists() ? out.length() : -1L);
        log.info("水印添加完成！");
    }

    /**
     * 视频添加水印方法使用gpu
     *
     * @param tenantName
     * @param videoUrl
     * @param newFilePath
     * @throws IOException
     * @throws InterruptedException
     */
    public void waterPrintByGpu(String tenantName, String videoUrl, String newFilePath, String isH265,WatermarkFormatVo watermarkFormatVo) throws IOException, InterruptedException {
        if ("cpu".equals(watermarkType)){
            DownloadUtil.waterPrint(tenantName, videoUrl, newFilePath,watermarkFormatVo);
        }else {
            String fontFilePath = TFF_PATH;
            // 第二象限+第四象限增加水印hevc_cuvid h264_cuvid
            String cuvid = "h264_cuvid";
            //判断是否是h264_cuvid
            boolean encoded = isH264Encoded(videoUrl);
            if (Constants.ONE.equals(isH265) || !encoded) {
                cuvid = "hevc_cuvid";
            }
            log.info("waterPrintByGpu-videoUrl:{},cuvid:{}", videoUrl, cuvid);
            String drawText = "drawtext=fontcolor=white:fontfile=" + fontFilePath + ":fontsize=" + (watermarkFormatVo.getFontSize() * 2 / 3) + ":text='" + watermarkFormatVo.getWatermarkText() + "'";
            Integer size = 4;
            Integer xRate = 400;
            Integer yRate = 300;
            StringBuffer buffer = new StringBuffer();
            if (Constants.ONE.equals(watermarkFormatVo.getDensityType())) {
                size = 3;
                xRate = 600;
                yRate = 450;
            } else if (Constants.THREE.equals(watermarkFormatVo.getDensityType())) {
                size = 8;
                xRate = 200;
                yRate = 150;
            }
            for (int i = 1; i <= size; i++) {
                String command = "";
                command = drawText + ":x=" + (100 + xRate * (i - 1)) + ":y=" + yRate * (i - 1) + Constants.ALPHA + watermarkFormatVo.getTransparency() + ",";
                if (i == 1) {
                    command = "ffmpeg" + " -c:v " + cuvid + " -i " + videoUrl + " -vf " + drawText + ":x=100:y=0:alpha=" + watermarkFormatVo.getTransparency() + ",";
                }
                if (i == size) {
                    // 关键：避免默认CFR策略导致大量dup补帧（会把15分钟拉长到几十分钟）
                    command = drawText + ":x=" + (100 + xRate * (i - 1)) + ":y=" + yRate * (i - 1) + Constants.ALPHA + watermarkFormatVo.getTransparency()
                            + " -c:v h264_nvenc -fps_mode passthrough -y " + newFilePath;
                }
                buffer.append(command);
            }
            log.info("downloadVideosByFfmpeg-执行命令:" + buffer.toString());
            Process process = Runtime.getRuntime().exec(buffer.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            int exitCode = process.waitFor();
            File out = new File(newFilePath);
            log.info("waterPrintByGpu-finished exitCode={},outputExists={},outputSizeBytes={},cuvid={}",
                    exitCode, out.exists(), out.exists() ? out.length() : -1L, cuvid);
            log.info("水印添加完成！");
        }
    }

    public static boolean isH264Encoded(String videoFilePath) {
        try {
            // 构建 ffprobe 命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-select_streams", "v:0",
                    "-show_entries", "stream=codec_name",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoFilePath
            );

            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String codecName = reader.readLine();

            // 等待进程执行完毕
            int exitCode = process.waitFor();

            // 检查进程是否正常退出
            if (exitCode == 0 && codecName != null) {
                return codecName.trim().equalsIgnoreCase("h264");
            }
        } catch (IOException | InterruptedException e) {
            log.error("判断是否是isH264失败：" + e);
        }
        return false;
    }

    public void downLoadPhotoByFlush(InputStream photoStream, String waterPrintn, HttpServletResponse response, String fileName) {
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        Integer fontSize = watermarkFormatVo.getFontSize();
        try {
            BufferedImage image = ImageIO.read(photoStream);
            /*try {
                log.info("downLoadPhotoByFlush-图片宽度:{}，图片高度:{}", image.getWidth(), image.getHeight());
                BigDecimal img = BigDecimal.valueOf(image.getWidth()).divide(BigDecimal.valueOf(image.getHeight()), 2, RoundingMode.HALF_UP);
                BigDecimal ratio = BigDecimal.valueOf(16).divide(BigDecimal.valueOf(9), 2, RoundingMode.HALF_UP);
                if (img.compareTo(ratio) > 0) {
                    fontSize = Math.round(90 * image.getWidth() / 1920);
                } else {
                    fontSize = Math.round(90 * image.getHeight() / 1080);
                }
            } catch (Exception e) {
                log.error("downLoadPhotoByFlush计算字体大小异常:" + e);
            }*/

            BufferedImage watermarkedImage = addWatermark(image, waterPrintn, fontSize, null,watermarkFormatVo);
            String suffix = "jpg";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(watermarkedImage, suffix, baos);
            byte[] bytes = baos.toByteArray();
            // 将字节数组写入响应的输出流
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException | FontFormatException e) {
            log.error("图片加水印保存失败：" + e);
        }
    }

    /**
     * 通过地址下载
     *
     * @param req
     * @param watermarkText
     * @param response
     */
    public void downLoadByUrlsZip(DownLoadUrlsReq req, String watermarkText, HttpServletResponse response) throws IOException{
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 逐个读取图片并添加水印
            if (ObjectUtil.isNotEmpty(req.getPhotoUrls())){
                for (String url : req.getPhotoUrls()) {
                    try {
                        log.info("飞控图片下载开始"+url);
                        BufferedImage image = ImageIO.read(new URL(url));
                        BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, "1",watermarkFormatVo);
                        String suffix = "";
                        if (url.contains("?")) {
                            suffix = url.substring(url.lastIndexOf(".") + 1, url.lastIndexOf("?"));
                        } else {
                            suffix = url.substring(url.lastIndexOf(".") + 1);
                        }
                        ZipEntry zipEntry = new ZipEntry("飞控图片" + UUID.randomUUID() + "-" + "1" + "." + suffix);
                        zipOut.putNextEntry(zipEntry);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(watermarkedImage, suffix, baos);
                        byte[] bytes = baos.toByteArray();
                        zipOut.write(bytes, 0, bytes.length);
                        zipOut.closeEntry();
                        log.info("飞控图片下载结束"+url);
                    } catch (Exception e) {
                        log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(req.getVideoUrls())){
                try {
                    downloadVideosByUrlsZip(zipOut, response, req.getVideoUrls(), watermarkText);
                } catch (Exception e) {
                    log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
                }
            }
        } catch (IOException e) {
            log.error(Constants.DOWNLOAD_ERROR_MSG, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过地址下载
     *
     * @param req
     * @param watermarkText
     * @param response
     */
    public void downLoadByUrls(DownLoadUrlsReq req, String watermarkText, HttpServletResponse response) throws IOException{
        //获取水印配置处理水印后端对应数值
        WatermarkFormatVo watermarkFormatVo = handleWatermarkFormat();
        int fontSize = watermarkFormatVo.getFontSize(); // 水印字体大小
        // 逐个读取图片并添加水印
        if (ObjectUtil.isNotEmpty(req.getPhotoUrls())){
            for (String url : req.getPhotoUrls()) {
                try {
                    BufferedImage image = ImageIO.read(new URL(url));
/*
                    try {
                        log.info("downLoadByUrls-图片宽度:{}，图片高度:{}", image.getWidth(), image.getHeight());
                        BigDecimal img = BigDecimal.valueOf(image.getWidth()).divide(BigDecimal.valueOf(image.getHeight()), 2, RoundingMode.HALF_UP);
                        BigDecimal ratio = BigDecimal.valueOf(16).divide(BigDecimal.valueOf(9), 2, RoundingMode.HALF_UP);
                        if (img.compareTo(ratio) > 0) {
                            fontSize = Math.round(90 * image.getWidth() / 1920);
                        } else {
                            fontSize = Math.round(90 * image.getHeight() / 1080);
                        }
                    } catch (Exception e) {
                        log.error("downLoadByUrls计算字体大小异常:" + e);
                    }
*/

                    BufferedImage watermarkedImage = addWatermark(image, watermarkText, fontSize, null,watermarkFormatVo);
                    String suffix = "jpg";
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(watermarkedImage, suffix, baos);
                    byte[] bytes = baos.toByteArray();
                    // 将字节数组写入响应的输出流
                    response.getOutputStream().write(bytes);
                    response.getOutputStream().flush();
                } catch (IOException | FontFormatException e) {
                    log.error("图片加水印保存失败：" + e);
                }
            }
        }
        if (ObjectUtil.isNotEmpty(req.getVideoUrls())){
            try {
                downloadVideosByUrls(response, req.getVideoUrls(), watermarkText);
            } catch (Exception e) {
                log.error(Constants.DOWNLOAD_PIC_ERROR_MSG, e);
            }
        }
}

    /**
     * @param zipOut
     * @param response
     * @param videoUrls
     * @param watermarkText
     */
    private void downloadVideosByUrlsZip(ZipOutputStream zipOut, HttpServletResponse response, List<String> videoUrls, String watermarkText) throws IOException {
        // 获取当前的用户
        List<String> realUrls = new ArrayList<>();
        int j = 1;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info(SAVE_SUCCESS);
            } else {
                log.info(SAVE_ERROR);
            }
        } else {
            log.info(SAVE_WARN);
        }
        for (String url : videoUrls) {
            String beforPath = path + "/" + "old" +UUID.randomUUID()+ j + ".mp4";
            downloadPic(url, beforPath);
            realUrls.add(beforPath);
            j++;
        }
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            int i = 1;
            for (String videoUrl : realUrls) {
                
                String newFilePath = path + Constants.PARAM_AIR_VIDEO +UUID.randomUUID()+ "-" + i + ".mp4";
                i++;
                // 第二象限+第四象限增加水印
                this.waterPrintByGpu(watermarkText, videoUrl, newFilePath,"0",watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    try {
                        addToZip(file, file.getName(), zipOut);
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功添加到 ZIP 包并被删除。");
                        } else {
                            log.info(DELETE_ERROR);
                        }
                    } catch (Exception e) {
                        log.error("将文件添加到ZIP包中异常:" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                //将原文件删除
                File fileOld = new File(videoUrl);
                if (fileOld.exists()) {
                    try {
                        boolean deletedOld = fileOld.delete();
                        if (deletedOld) {
                            log.info(QUERY_ORGFILE);
                        } else {
                            log.info(DELETE_ORG_ERROR);
                        }
                    } catch (Exception e) {
                        log.error(DELETE_ORGFILE_ERROR);
                    } finally {
                        if (fileOld.exists()) {
                            fileOld.delete();
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error(SAVE_VIDEO_ERROR + e);
        } finally {
            zipOut.flush();
            for (String videoUrl : realUrls) {
                if (!StringUtils.isEmpty(videoUrl)) {
                    //将原文件删除
                    File fileOld = new File(videoUrl);
                    if (fileOld.exists()) {
                        fileOld.delete();
                    }
                }
            }
            log.info(SAVE_VIDEO_SUCCESS);
        }
    }

    /**
     * @param
     * @param response
     * @param videoUrls
     * @param watermarkText
     */
    private void downloadVideosByUrls(HttpServletResponse response, List<String> videoUrls, String watermarkText) throws IOException {
        // 获取当前的用户
        List<String> realUrls = new ArrayList<>();
        int j = 1;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info(SAVE_SUCCESS);
            } else {
                log.info(SAVE_ERROR);
            }
        } else {
            log.info(SAVE_WARN);
        }
        for (String url : videoUrls) {
            String beforPath = path + "/" + "old" +UUID.randomUUID()+ j + ".mp4";
            downloadPic(url, beforPath);
            realUrls.add(beforPath);
            j++;
        }
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            int i = 1;
            for (String videoUrl : realUrls) {
                String newFilePath = path + Constants.PARAM_AIR_VIDEO +UUID.randomUUID()+ "-" + i + ".mp4";
                i++;
                // 第二象限+第四象限增加水印
                this.waterPrintByGpu(watermarkText, videoUrl, newFilePath,"0",watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    // 返回文件流
                    try (FileInputStream fis = new FileInputStream(file);
                         OutputStream os = response.getOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        // 处理异常
                        e.printStackTrace();
                    }
                    try {
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功被删除。");
                        } else {
                            log.info(DELETE_ERROR);
                        }
                    } catch (Exception e) {
                        log.error(":" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                //将原文件删除
                File fileOld = new File(videoUrl);
                if (fileOld.exists()) {
                    try {
                        boolean deletedOld = fileOld.delete();
                        if (deletedOld) {
                            log.info(QUERY_ORGFILE);
                        } else {
                            log.info(DELETE_ORG_ERROR);
                        }
                    } catch (Exception e) {
                        log.error(DELETE_ORGFILE_ERROR);
                    } finally {
                        if (fileOld.exists()) {
                            fileOld.delete();
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error(SAVE_VIDEO_ERROR + e);
        } finally {
            for (String videoUrl : realUrls) {
                if (!StringUtils.isEmpty(videoUrl)) {
                    //将原文件删除
                    File fileOld = new File(videoUrl);
                    if (fileOld.exists()) {
                        fileOld.delete();
                    }
                }
            }
            log.info(SAVE_VIDEO_SUCCESS);
        }
    }

    public void downLoadDemo(String watermarkText, HttpServletResponse response) {
        // 获取当前的用户
        List<String> realUrls = new ArrayList<>();
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info(SAVE_SUCCESS);
            } else {
                log.info(SAVE_ERROR);
            }
        } else {
            log.info(SAVE_WARN);
        }
        String beforPath = path + "/" + "demo"  + ".mp4";
        realUrls.add(beforPath);
        // 创建 ZIP 输出流
        //将生成的带水印视频加入到zip中并删除所生成视频
        try {
            WatermarkFormatVo watermarkFormatVo = DownloadUtil.handleWatermarkFormat();
            int i = 1;
            for (String videoUrl : realUrls) {
                String newFilePath = path + Constants.PARAM_AIR_VIDEO +UUID.randomUUID()+ "-" + i + ".mp4";
                i++;
                // 第二象限+第四象限增加水印
                this.waterPrintByGpu(watermarkText, videoUrl, newFilePath,"0",watermarkFormatVo);
                // 将新文件添加到 ZIP 包中并删除源文件
                File file = new File(newFilePath);
                if (file.exists()) {
                    // 返回文件流
                    try (FileInputStream fis = new FileInputStream(file);
                         OutputStream os = response.getOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        // 处理异常
                        e.printStackTrace();
                    }
                    try {
                        // 删除原始文件
                        boolean deleted = file.delete();
                        if (deleted) {
                            log.info("新文件已成功被删除。");
                        } else {
                            log.info(DELETE_ERROR);
                        }
                    } catch (Exception e) {
                        log.error(":" + e);
                    } finally {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(SAVE_VIDEO_ERROR + e);
        } finally {
            log.info(SAVE_VIDEO_SUCCESS);
        }
    }
}
