package com.chinatower.platform.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.props.ObsProperties;
import com.chinatower.platform.util.DateUtils;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: obs工具类
 * @Author: zs
 * @Date: 2021/03/14 22:45
 **/
@Slf4j
@Service
public class ObsClientService {

    @Autowired
    private ObsProperties obsProperties;

    @Autowired(required = false)
    private ObsClient obsClient;

    private static final String SEPARATOR = "/";

    /**
     * @param in       文件输入流
     * @param fileName 文件名
     * @return 文件上传地址
     */
    public String uploadFile(InputStream in, String fileName){
        String relativePath = AESUtil.encrypt(DateUtils.getCurrentMonth()) + SEPARATOR + AESUtil.encrypt(DateUtils.getDate()) + SEPARATOR + fileName;
        try {
            PutObjectResult result = obsClient.putObject(obsProperties.getBucketName(), relativePath, in);
            log.info("upload result=" + JSON.toJSONString(result));
            return URLDecoder.decode(result.getObjectUrl(), "UTF-8");
        } catch (Exception e) {
            log.error("obs 上传异常{}", e.getMessage());
            throw new CustomException("obs uploadFile 上传异常");
        }
    }

    /**
     * @param file 文件
     * @return 文件上传地址
     */
    public String uploadFile(MultipartFile file){
        StringBuilder relativePath = new StringBuilder("");
        String url = "";
        try {
            String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
            if (fileExt == null || "".equals(fileExt)) {
                String contentType = file.getContentType();
                if (StrUtil.isNotEmpty(contentType) && contentType.contains(SEPARATOR)) {
                    String[] contentSplit = contentType.split(SEPARATOR);
                    if (contentSplit.length > 2) {
                        fileExt = contentType.split(SEPARATOR)[1];
                    }
                }
            }
            List<String> list = Arrays.asList(obsProperties.getSuffixRules().split(","));
            if (!list.contains(fileExt)) {
                throw new CustomException(fileExt + "格式文件不能上传");
            }
            relativePath.append(AESUtil.encrypt(DateUtils.getCurrentMonth())).append(SEPARATOR).append(AESUtil.encrypt(DateUtils.getDate()));
            relativePath.append(SEPARATOR).append(IdUtil.simpleUUID()).append(".").append(fileExt);
            log.info("obsClient == null : " + (obsClient == null));
            log.info("obsProperties: " + JSON.toJSONString(obsProperties));
            log.info("relativePath: " + relativePath.toString());
            log.info("file == null : " + (file == null));
            PutObjectResult result = obsClient.putObject(obsProperties.getBucketName(), relativePath.toString(), file.getInputStream());
            log.info("upload result=" + JSON.toJSONString(result));
            url = URLDecoder.decode(result.getObjectUrl(), "UTF-8");
        } catch (Exception e) {
            log.info("obs 上传异常{}", e.getMessage());
            log.error(e.getMessage(), e);
            throw new CustomException("obs uploadMultipartFile 上传异常");
        }
        return url;
    }
}
