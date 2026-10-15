package com.chinatower.file.config.filestategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.file.FileOperation;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.IdUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.props.HwObsProperties;
import com.chinatower.platform.util.ObsSignUrlUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ObsStrategy implements FileOperation {
    private final ObsClientUtil obsClientUtil;
    private final Pattern pattern;

    public ObsStrategy(ObsClientUtil obsClientUtil) {
        this.obsClientUtil = obsClientUtil;
        RedisTemplate<String,String> redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        String obsRegex = redisTemplate.opsForValue().get("obsRegex");
        if (StringUtils.isEmpty(obsRegex)) {
            throw new FileOperation.FileOperationException("未配置 obsRegex");
        }
        this.pattern = Pattern.compile(obsRegex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public FileInfo fileUpload(MultipartFile file) {
        return fileUploadWithLoginInfo(file, SecurityUtils.getLoginUser());
    }

    @Override
    public FileInfo fileUploadWithLoginInfo(MultipartFile file, LoginUser loginUser) {
        log.info("upload fileInfo  size = {} , name = {},contentType = {}",
                file.getSize(), file.getName(), file.getContentType());
        String originUrl = obsClientUtil.uploadFile(file, loginUser);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginUrl(originUrl);
        fileInfo.setUrl(encrypt(originUrl));
        return fileInfo;
    }

    @Override
    public String encrypt(String url) {
        String result = obsClientUtil.filterUrl(url);
        log.info("encrypt input {} , output = {}", url, result);
        return result;
    }

    public static class ObsClientUtil {

        @Getter
        @Setter
        private String bucketName;

        @Getter
        @Setter
        private String suffixRules;


        @Getter
        @Setter
        private String rootDir;


        @Getter
        @Setter
        private String ak;

        @Getter
        @Setter
        private String sk;

        @Getter
        @Setter
        private String endPoint;

        @Getter
        @Setter
        private ObsClient obsClient;

        @Setter
        @Getter
        private HwObsProperties hwObsProperties;
        private static Pattern p = Pattern.compile("((https?|ftp|gopher|telnet|file|Unsure):((//)|(\\\\))[\\w\\d:#@%/;$~_?\\u4e00-\\u9fa5\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE);


        public ObsClientUtil(ObsClient obsClient) {
            this.obsClient = obsClient;
        }

        /**
         * 对整个字符串中涉及指定桶的obs文件进行加密处理(支持批量配置多个桶数据)
         *
         * @param url
         * @return
         */
        public String filterUrl(String url) {
            if (url.contains("?")) {
                url = url.split("\\?")[0];
            }
            List<Map<String, String>> bucketList = hwObsProperties.getBucketList();
            Matcher m = p.matcher(url);
            while (url != null && m.find()) {
                String tempObsUrl = m.group(0);
                for (Map<String, String> bucketMap : bucketList) {
                    HwObsProperties hp = new HwObsProperties();
                    BeanUtil.copyProperties(hwObsProperties, hp, true);
                    hp.setAk(bucketMap.get("ak"));
                    hp.setSk(bucketMap.get("sk"));
                    hp.setBucketName(bucketMap.get("bucketName"));
                    log.info("{}-桶名:{}",tempObsUrl, hp.getBucketName());
                    if (tempObsUrl.contains(hp.getBucketName())) {
                        String encyyptUrl = new ObsSignUrlUtils(hp).signUrl(tempObsUrl);
                        if (!url.contains(encyyptUrl)) {
                            url = url.replaceAll(tempObsUrl, encyyptUrl);
                            return url;
                        }
                    }
                }
            }
            return url;
        }

        public String uploadFile(MultipartFile file, LoginUser loginUser) {
            StringBuilder relativePath = new StringBuilder(rootDir + "/");
            String url = "";
            try {
                String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
                if (fileExt == null || "".equals(fileExt)) {
                    fileExt = file.getContentType().split("/")[1];
                }
                List<String> list = Arrays.asList(suffixRules.split(","));
                if (!list.contains(fileExt)) {
                    throw new Exception(fileExt + "格式文件不能上传");
                }
                if (loginUser != null) {
                    String appVer = Optional.ofNullable(loginUser.getUser().getAppVerCode()).orElse("baseplatform");
                    relativePath.append(appVer).append("/");
                } else {
                    relativePath.append("baseplatform").append("/");
                }

                relativePath.append(AESUtil.encrypt(DateUtils.getMonth())).append("/").append(AESUtil.encrypt(DateUtils.getDate()));
                relativePath.append("/").append(IdUtil.simpleUUID()).append(".").append(fileExt);
                //测试环境调试用，调试后删除
                log.info("upload path=" + relativePath);
                PutObjectResult result = obsClient.putObject(bucketName, relativePath.toString(), file.getInputStream());
                log.info("upload result=" + JSON.toJSONString(result));
                url = URLDecoder.decode(result.getObjectUrl(), "UTF-8");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new FileOperationException(e.getMessage());
            }
            return url;
        }
    }
}
