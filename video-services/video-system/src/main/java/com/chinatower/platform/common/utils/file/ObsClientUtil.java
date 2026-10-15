package com.chinatower.platform.common.utils.file;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Description: obs工具类
 * @Author: zs
 * @Date: 2021/03/14 22:45
 **/
@Slf4j
@Component
public class ObsClientUtil {

    @Value("${obs.bucketName}")
    private String bucketName;
    @Value("${obs.suffixRules}")
    private String suffixRules;

    @Value("${obs.customPath:slyy2}")
    private String rootDir ;

    @Autowired
    private ObsClient obsClient;


    /**
     * 上传文件
     *
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws Exception {
        StringBuilder relativePath = new StringBuilder(rootDir + "/");
        String url = "";
        try {
            String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
            if (fileExt == null || "".equals(fileExt)) {
                fileExt = file.getContentType().split("/")[1];
            }
            List<String> list = Arrays.asList(suffixRules.split(","));
            if(!list.contains(fileExt)){
                throw new Exception(fileExt+"格式文件不能上传");
            }
            //20220617修改增加在上传路径最前端加行业版本编码    by  liangdl
//            log.info("upload SecurityUtils user对象---->"+JSON.toJSONString(SecurityUtils.getLoginUser().getUser()));
            if(SecurityUtils.getLoginUser() != null){
                String appVer = Optional.ofNullable(SecurityUtils.getLoginUser().getUser().getAppVerCode()).orElse("baseplatform");
                relativePath.append(appVer).append("/");
            }else{
                relativePath.append("baseplatform").append("/");
            }

            relativePath.append(AESUtil.encrypt(DateUtils.getMonth())).append("/").append(AESUtil.encrypt(DateUtils.getDate()));
            relativePath.append("/").append(IdUtil.simpleUUID()).append(".").append(fileExt);
            //测试环境调试用，调试后删除
            log.info("upload path=" + relativePath);
            PutObjectResult result = obsClient.putObject(bucketName, relativePath.toString(), file.getInputStream());
            log.info("upload result=" + JSON.toJSONString(result));
            url= URLDecoder.decode(result.getObjectUrl(),"UTF-8");
        } catch (Exception e) {
            log.info("obs 上传异常{}", e.getMessage());
            log.error(e.getMessage(), e);
            throw e;
        }
        return url;
    }


}
