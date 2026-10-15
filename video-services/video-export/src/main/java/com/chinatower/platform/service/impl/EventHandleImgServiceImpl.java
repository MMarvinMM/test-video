package com.chinatower.platform.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.HandleAlarmImgDTO;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.platform.constant.TableConstant;
import com.chinatower.platform.mapper.IWarningOrderMapper;
import com.chinatower.platform.mongodb.InterceptMongoMapper;
import com.chinatower.platform.mongodb.OperateMongoMapper;
import com.chinatower.platform.service.EventHandleImgService;
import com.chinatower.platform.utils.BASE64DecodedMultipartFile;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EventHandleImgServiceImpl implements EventHandleImgService {

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private FileFeignClient fileFeignClient;
    @Resource
    private IWarningOrderMapper iWarningOrderMapper;
    @Resource
    private OperateMongoMapper operateMongoMapper;
    @Resource
    private InterceptMongoMapper interceptMongoMapper;
    @Override
    @Async("taskExecutor")
    public void handleReceiveAlarmImg(HandleAlarmImgDTO handleAlarmImgDTO) {
        log.info("AI生成告警后原图/框图复合油管线图处理入参：{}", JSON.toJSONString(handleAlarmImgDTO));
        //事件表,压制表,研判表标识,true更新原图框图。
        boolean listRemark = handleAlarmImgDTO.isListRemark();
        boolean interceptsRemark = handleAlarmImgDTO.isInterceptsRemark();
        boolean operationsRemark = handleAlarmImgDTO.isOperationsRemark();
        //原图
        if (!StringUtils.isEmpty(handleAlarmImgDTO.getFileImgUrlSrc())){
            List<String> srcList = new ArrayList<>();
            String[] splitSrc = handleAlarmImgDTO.getFileImgUrlSrc().split(",");
            String firstSrc = splitSrc[0];
            srcList.add(firstSrc);
            this.handleUrl(srcList,handleAlarmImgDTO,"1",splitSrc);
        }
        //框图
        if (!StringUtils.isEmpty(handleAlarmImgDTO.getFileImgUrlIcon())){
            List<String> iconList = new ArrayList<>();
            String[] splitIcon = handleAlarmImgDTO.getFileImgUrlIcon().split(",");
            String firstIcon = splitIcon[0];
            iconList.add(firstIcon);
            this.handleUrl(iconList,handleAlarmImgDTO,"2",splitIcon);
        }
        if (listRemark) {
            // 获取表名
            String tableName = TableConstant.WARN_ORDER_TEBLE + handleAlarmImgDTO.getWarningOrderId().substring(0, 6);
            handleAlarmImgDTO.setTableName(tableName);
            iWarningOrderMapper.updateWarningOrder(handleAlarmImgDTO);
        }
        if (interceptsRemark) {
            //更新压制表mysql
            iWarningOrderMapper.updateIntercept(handleAlarmImgDTO);
            //更新压制表mongo
            try{
                InterceptVO vo = new InterceptVO();
                vo.setWarningOrderId(handleAlarmImgDTO.getWarningOrderId());
                vo.setFileImgUrlSrc(handleAlarmImgDTO.getFileImgUrlSrc());
                vo.setFileImgUrlIcon(handleAlarmImgDTO.getFileImgUrlIcon());
                interceptMongoMapper.updateIntercept(vo);
            }catch (Exception e){
                log.error("checkAlarmOrder-修改回收站mongo异常:",e);
            }
        }
        if (operationsRemark) {
            //更新研判表mysql
            iWarningOrderMapper.updateAlarmOperate(handleAlarmImgDTO);
            //更新研判表mongo
            try{
               OperateMongo operateMongo = new OperateMongo();
                operateMongo.setWarningOrderId(handleAlarmImgDTO.getWarningOrderId());
                operateMongo.setFileImgUrlSrc(handleAlarmImgDTO.getFileImgUrlSrc());
                operateMongo.setFileImgUrlIcon(handleAlarmImgDTO.getFileImgUrlIcon());
                    operateMongoMapper.updateOperate(operateMongo);

            }catch (Exception e){
                log.error("checkAlarmOrder-修改研判表mongo异常:",e);
            }
        }
    }

    //处理图片路径加密访问
    public void handleUrl(List<String>imageUrls,HandleAlarmImgDTO handleAlarmImgDTO,String remark,String[] split){
        log.info("AI生成告警后原图/框图路径：{}", JSON.toJSONString(imageUrls));
        //图片路径集合
        if (!CollectionUtils.isEmpty(imageUrls)){
            //调用obs加密方法获取加密后的路径
            log.info("obs开始获取原图/框图加密路径");
            Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
            log.info("obs获取原图/框图加密路径：{}", JSON.toJSONString(map));
            //加密后的图片
            String picUrl="";
            for (String key: map.keySet()) {
                    //相同则赋值加密后图片 否则用加密前图片
                    if (key.equals(imageUrls.get(0))) {
                        if(!StringUtils.isEmpty(map.get(key))){
                            // 仅加密成功  使用原地址进行处理
                             picUrl = map.get(key);
                        }else{
                            picUrl = key;
                        }
                        break;
                    }
            }
            this.handleImg(picUrl,handleAlarmImgDTO,remark);

        }

    }


    //处理原图片与管线合成一张图
    public void handleImg(String picUrl,HandleAlarmImgDTO handleAlarmImgDTO,String remark){
        log.info("开始处理图片与管线合成一张图");
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
        // 读取图片
        BufferedImage image = null;
        image = ImageIO.read(new URL(picUrl));
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // 创建一个用于绘制的Graphics2D对象
        Graphics2D g2d = image.createGraphics();
        // 设置透明度为完全不透明
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        g2d.setComposite(alphaComposite);

        //获取管线JSON
         JSONArray jsonArray = new JSONArray(handleAlarmImgDTO.getPipelines());
        // 提取color值
        String colorString = jsonArray.getJSONObject(0).getStr("color");
        // 解析颜色值并设置Graphics2D的颜色
        String[] rgba = colorString.substring(colorString.indexOf("(") + 1, colorString.indexOf(")")).split(",");
        int red = Integer.parseInt(rgba[0].trim());
        int green = Integer.parseInt(rgba[1].trim());
        int blue = Integer.parseInt(rgba[2].trim());
        float alpha = Float.parseFloat(rgba[3].trim());
        Color transparentColor = new Color(red, green, blue, (int) (alpha * 255));
        // 提取定义点的坐标
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONArray directionArray = jsonObject.getJSONArray("direction");

        double[] pointsX = new double[directionArray.size()];
        double[] pointsY =new double[directionArray.size()];
        for (int i = 0; i < directionArray.size(); i++) {
            JSONObject directionObject = directionArray.getJSONObject(i);
            double percentX = directionObject.getDouble("percentX");
            pointsX[i] = percentX;
            double percentY = directionObject.getDouble("percentY");
            pointsY[i] = percentY;
        }
        // 将百分比坐标转换为像素坐标
        int[] pixelsX = new int[pointsX.length];
        int[] pixelsY = new int[pointsY.length];
        for (int i = 0; i < pointsX.length; i++) {
            pixelsX[i] = (int) (pointsX[i] * imageWidth);
            pixelsY[i] = (int) (pointsY[i] * imageHeight);
        }

        // 设置线段的画笔大小
        g2d.setStroke(new BasicStroke(24,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(transparentColor);
        // 绘制线段
        g2d.drawPolyline(pixelsX,pixelsY,pointsX.length);

            // 绘制实心三角形
            int arrowSize = 42;
            double dx = pixelsX[pixelsX.length - 2] - pixelsX[pixelsX.length - 1];
            double dy = pixelsY[pixelsY.length - 2] - pixelsY[pixelsY.length - 1];
            double length = Math.sqrt(dx * dx + dy * dy);
            double unitX = dx / length;
            double unitY = dy / length;
            double extendedX =  (pixelsX[pixelsX.length - 1] - 31 * unitX);
            double extendedY =  (pixelsY[pixelsY.length - 1] - 31 * unitY);

            double angle = Math.atan2(pixelsY[pixelsY.length - 1] - pixelsY[pixelsY.length - 2], pixelsX[pixelsX.length - 1] - pixelsX[pixelsX.length - 2]);
            int x1 = (int) (extendedX - arrowSize * Math.cos(angle - Math.PI / 4));
            int y1 = (int) (extendedY - arrowSize * Math.sin(angle - Math.PI / 4));
            int x2 = (int) (extendedX - arrowSize * Math.cos(angle + Math.PI / 4));
            int y2 = (int) (extendedY - arrowSize * Math.sin(angle + Math.PI / 4));
            int[] xs = {(int) extendedX, x1, x2};
            int[] ys = {(int) extendedY, y1, y2};
            g2d.fillPolygon(xs, ys, 3);
        // 释放资源
        g2d.dispose();

        // 保存绘制完成的图片


        // 将图像写入字节输出流中
        ImageIO.write(image, "png", baos);

        // 将字节输出流转换为 byte[]
        byte[] imageBytes = baos.toByteArray();
        log.info("油管线合成图片输出byte成功,开始调用obs上传图片");
        this.getObsPicUrl(handleAlarmImgDTO,imageBytes,remark);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 关闭字节输出流
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //obs管线合成图片上传
    private String getObsPicUrl(HandleAlarmImgDTO handleAlarmImgDTO,byte[] imageBytes,String remark) {
        //读取图片,转为Base64
        String base64 = "";
        //默认为原始路径，调用成功会覆盖为新路径
        String postPicUrl = "";
        try {
            base64 = Base64.getEncoder().encodeToString(imageBytes);
            //获取到图片，转成MultipartFile流
            String relativePath = "data:image/png;base64," + base64;
            MultipartFile mPathPose = BASE64DecodedMultipartFile.base64ToMultipart(relativePath);
            //上传图片地址到obs
            log.info("开始调用obs上传图片,上传管线合成图片");
            AjaxResult postResult = fileFeignClient.uploadFile(mPathPose);
            Map postFileInfo = (Map) postResult.get(AjaxResult.DATA_TAG);
            log.info("调用obs上传图片,上传管线合成图片结束：{}", JSON.toJSONString(postFileInfo));
            if (postFileInfo != null) {
                //得到obs地址入库
                postPicUrl = (String) postFileInfo.get(VideoConstants.FILE_URL);
                if (!StringUtils.isEmpty(postPicUrl) && postPicUrl.contains(Constants.QUESTION_MARK)) {
                    postPicUrl = postPicUrl.substring(Constants.ZERO_NUM, postPicUrl.indexOf(Constants.QUESTION_MARK));
                }
                //判断原图/框图 1原图
                if ("1".equals(remark)){
                    String[] splitSrc = handleAlarmImgDTO.getFileImgUrlSrc().split(",");
                    postPicUrl = handleAlarmImgDTO.getFileImgUrlSrc().replaceFirst(splitSrc[0], postPicUrl);

                    handleAlarmImgDTO.setFileImgUrlSrc(postPicUrl);
                }else {
                    String[] splitSrc = handleAlarmImgDTO.getFileImgUrlIcon().split(",");
                    postPicUrl = handleAlarmImgDTO.getFileImgUrlIcon().replaceFirst(splitSrc[0], postPicUrl);
                    handleAlarmImgDTO.setFileImgUrlIcon(postPicUrl);
                }
            }
        } catch (Exception e) {
            log.error("图片上传obs失败！", e);
        }
        return postPicUrl;
    }




}
