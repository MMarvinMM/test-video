package com.chinatower.file.config.filestategy;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chinatower.file.FileOperation;
import com.chinatower.file.domain.SysConfig;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.platform.constants.Constants;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
public class OfsStrategy implements FileOperation {
    @Setter
    private String serverPrefix;
    @Setter
    private String appId;
    @Setter
    private String privateKey;
    @Setter
    private String filePath;
    @Setter
    private String storeType;
    @Setter
    private SysConfigService sysConfigService;
    public void setRegexPattern(String regexPattern) {
        this.pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
    }

    private Pattern pattern;

    private static final String TENANT_ID = "tenantId";
    private static final String TIMESTAMP = "timestamp";
    private static final String STORE_TYPE = "storeType";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public Pattern pattern() {
        return pattern;
    }

    private static final String UPLOAD_FILE_URL = "/api/upload/file";
    private static final String TEMP_URL = "/api/refresh/ofsTempUrl";
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(100, 100, TimeUnit.SECONDS))
            .build();

    @Override
    public FileInfo fileUpload(MultipartFile file) {
        try {
            String time = DateUtil.date().getTime() + "";
            String toSignStr = String.format("filePath=%s&storeType=%s&tenantId=%s&timestamp=%s&key=%s",
                    filePath, storeType, appId, time, privateKey);
            String sign = sign(toSignStr, privateKey);
            MultipartFile multipartFile = new MultipartFile() {
                private String name() {
                    String mainName = FileUtil.mainName(file.getOriginalFilename());
                    String extName = FileUtil.extName(file.getOriginalFilename());
                    return mainName + "_" + RandomUtil.randomString(10) + "." + extName;
                }

                @Override
                public String getName() {
                    return name();
                }

                @Override
                public String getOriginalFilename() {
                    return name();
                }

                @Override
                public String getContentType() {
                    return file.getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return file.getSize();
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return file.getBytes();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return file.getInputStream();
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    file.transferTo(dest);
                }
            };

            ImmutableMap<Object, Object> immutableMap = ImmutableMap.builder()
                    .put("filePath", filePath)
                    .put(TENANT_ID, appId)
                    .put("sign", sign)
                    .put(TIMESTAMP, time)
                    .put(STORE_TYPE, storeType)
                    .build();
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("files", multipartFile.getOriginalFilename(), RequestBody.create(MediaType.parse(multipartFile.getContentType()), multipartFile.getBytes()))
                    .addFormDataPart("request", JSON.toJSONString(immutableMap))
                    .build();
            log.info("上传入参 request = {} fileName = {} content = {}", JSON.toJSONString(immutableMap), multipartFile.getOriginalFilename(), multipartFile.getContentType());

            Request request = new Request.Builder()
                    .header(CONTENT_TYPE, "application/form-data")
                    .header("Accept", "*/*")
                    .url(serverPrefix + UPLOAD_FILE_URL)
                    .post(multipartBody)
                    .build();

            String string = parseResponsebodyString(request);
            FileUploadResponse fileUploadResponse = JSON.parseObject(string, FileUploadResponse.class);
            String url = fileUploadResponse.data.get(0).getTempUrl();
            String proxyAddr = fileUploadResponse.data.get(0).getProxyAddr();
            String prefix = fileUploadResponse.data.get(0).getPrefix();
            String relativePath = (StringUtils.isEmpty(prefix) ? "" : prefix + "/") + fileUploadResponse.data.get(0).getName();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginUrl(relativePath);
            fileInfo.setProxyAddr(proxyAddr);
            fileInfo.setUrl(url);
            return fileInfo;
        } catch (Exception e) {
            log.error("上传文件错误", e);
            if (e instanceof FileOperationException) {
                throw (FileOperationException) e;
            }
            throw new FileOperationException("上传文件时发生错误");
        }
    }

    @Override
    public FileInfo fileUploadV3(MultipartFile file, String colonyCode, Boolean useOfs) {
        try {
            String time = DateUtil.date().getTime() + "";
            String toSignStr = String.format("filePath=%s&storeType=%s&tenantId=%s&timestamp=%s&key=%s",
                    filePath, storeType, appId, time, privateKey);
            String sign = sign(toSignStr, privateKey);
            MultipartFile multipartFile = new MultipartFile() {
                private String name() {
                    String mainName = FileUtil.mainName(file.getOriginalFilename());
                    String extName = FileUtil.extName(file.getOriginalFilename());
                    return mainName + "." + extName;
                }

                @Override
                public String getName() {
                    return name();
                }

                @Override
                public String getOriginalFilename() {
                    return name();
                }

                @Override
                public String getContentType() {
                    return file.getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return file.getSize();
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return file.getBytes();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return file.getInputStream();
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    file.transferTo(dest);
                }
            };

            ImmutableMap<Object, Object> immutableMap = ImmutableMap.builder()
                    .put("filePath", filePath)
                    .put(TENANT_ID, appId)
                    .put("sign", sign)
                    .put(TIMESTAMP, time)
                    .put(STORE_TYPE, storeType)
                    .build();
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("files", multipartFile.getOriginalFilename(), RequestBody.create(MediaType.parse(multipartFile.getContentType()), multipartFile.getBytes()))
                    .addFormDataPart("request", JSON.toJSONString(immutableMap))
                    .build();
            log.info("上传入参 request = {} fileName = {} content = {}", JSON.toJSONString(immutableMap), multipartFile.getOriginalFilename(), multipartFile.getContentType());

            Request request = new Request.Builder()
                    .header(CONTENT_TYPE, "application/form-data")
                    .header("Accept", "*/*")
                    .url(serverPrefix + UPLOAD_FILE_URL)
                    .post(multipartBody)
                    .build();

            String string = parseResponsebodyString(request);
            FileUploadResponse fileUploadResponse = JSON.parseObject(string, FileUploadResponse.class);
            String url = fileUploadResponse.data.get(0).getTempUrl();
            String prefix = fileUploadResponse.data.get(0).getPrefix();
            String relativePath = (StringUtils.isEmpty(prefix) ? "" : prefix + "/") + fileUploadResponse.data.get(0).getName();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginUrl(relativePath);
            fileInfo.setUrl(url);
            return fileInfo;
        } catch (Exception e) {
            log.error("上传文件错误", e);
            if (e instanceof FileOperationException) {
                throw (FileOperationException) e;
            }
            throw new FileOperationException("上传文件时发生错误");
        }
    }

    private static String parseResponsebodyString(Request request) throws IOException {
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new FileOperationException("调用文件服务错误:  + " + response.code());
        }
        if (response.body() == null) {
            throw new FileOperationException("调用文件服务无返回数据");
        }
        String string = response.body().string();
        log.info("调用文件服务返回数据 {}", string);
        response.close();
        return string;
    }

    /**
     * 私钥签名
     *
     * @param preStr     待签名字符串
     * @param privateKey
     * @return base64字符串
     * @throws Exception
     */
    private static String sign(String preStr, String privateKey)
            throws Exception {
        String strSign = "";
        String suite = "SHA256WithRSA";
        //初始化算法SHA256
        Signature signature = Signature.getInstance(suite);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        //初始化私钥+RSA
        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        PrivateKey privateKey2 = keyFac.generatePrivate(keySpec);
        signature.initSign(privateKey2);
        //待签名字符串转byte数组使用UTF8
        byte[] msgBuf = preStr.getBytes(StandardCharsets.UTF_8);
        signature.update(msgBuf);
        byte[] byteSign = signature.sign();
        //签名值byte数组转字符串用BASE64
        strSign = Base64.encodeBase64String(byteSign);
        return strSign;
    }

    @Override
    public String encrypt(String url) {
        return tempUrl(url);
    }

    @Override
    public void fileDownload(String url, OutputStream out, boolean isCloseOut) {
        FileOperation.super.fileDownload(url, out, isCloseOut);
    }

    private String tempUrl(String oldUrl) {
        try {
            if (StringUtils.isEmpty(oldUrl)) {
                return null;
            }
//            String whiteAddr ="";
//            //获取文件白名单调用域名集合
//            SysConfig sysConfig = sysConfigService.selectConfigByKey("third_whiteAddr");
//            if (sysConfig != null) {
//                whiteAddr = sysConfig.getConfigValue();
//            }
//            //url中包含白名单配置中的域名，直接返回原地址
//            if(!StringUtils.isEmpty(whiteAddr)){
//                String[] whiteAddrs = whiteAddr.split(Constants.COMMA);
//                for (String addr:whiteAddrs) {
//                    if(oldUrl.contains(addr)){
//                        return oldUrl;
//                    }
//                }
//            }
            if (oldUrl.startsWith("http") || oldUrl.startsWith("https")) {
                String time = DateUtil.date().getTime() + "";
                String toSignStr;
                if ("ter".equals(SpringUtil.getActiveProfile())) {
                    toSignStr = String.format("oldUrl=%s&storeType=%s&tenantId=%s&timestamp=%s&key=%s",
                            oldUrl, storeType, appId, time, privateKey);
                } else {
                    toSignStr = String.format("oldUrl=%s&tenantId=%s&timestamp=%s&key=%s",
                            oldUrl, appId, time, privateKey);
                }
                String sign = sign(toSignStr, privateKey);

                ImmutableMap<Object, Object> immutableMap;
                if ("ter".equals(SpringUtil.getActiveProfile())) {
                    immutableMap = ImmutableMap.builder()
                            .put("oldUrl", oldUrl)
                            .put(TENANT_ID, appId)
                            .put(STORE_TYPE, storeType)
                            .put("sign", sign)
                            .put(TIMESTAMP, time)
                            .build();
                } else {
                    immutableMap = ImmutableMap.builder()
                            .put("oldUrl", oldUrl)
                            .put(TENANT_ID, appId)
                            .put("sign", sign)
                            .put(TIMESTAMP, time)
                            .build();
                }


                Request request = new Request.Builder()
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .url(serverPrefix + TEMP_URL)
                        .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), JSON.toJSONString(immutableMap)))
                        .build();
                log.info("入参 body  = {}", JSON.toJSONString(immutableMap));
                String string = parseResponsebodyString(request);
                JSONObject jsonObject = JSON.parseObject(string);
                return (String) JSONPath.eval(jsonObject, "$data");
            } else {
                String time = DateUtil.date().getTime() + "";
                String toSignStr =
                        String.format("fileName=%s&storeType=%s&targetTenantId=%s&tenantId=%s&timestamp=%s&key=%s",
                                oldUrl, storeType, appId, appId, time, privateKey);
                String sign = sign(toSignStr, privateKey);

                ImmutableMap<Object, Object> body = ImmutableMap
                        .builder()
                        .put("fileName", oldUrl)
                        .put("targetTenantId", appId)
                        .put(TENANT_ID, appId)
                        .put(STORE_TYPE, storeType)
                        .put("sign", sign)
                        .put(TIMESTAMP, time)
                        .build();
                log.info("入参 body  = {}", JSON.toJSONString(body));
                RequestBody requestBody = RequestBody.create(MediaType.parse(APPLICATION_JSON), JSON.toJSONString(body));
                Request request = new Request.Builder()
                        .url(serverPrefix + "/api/download/ofsTempUrl")
                        .post(requestBody)
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .build();
                String responsebodyString = parseResponsebodyString(request);
                JSONObject jsonObject = JSON.parseObject(responsebodyString);
                return (String) JSONPath.eval(jsonObject, "$.data");
            }
        } catch (Exception e) {
            log.error("加密文件错误", e);
            log.error("oldUrl = " + oldUrl);

            return null;
        }
    }

    @Data
    static class FileUploadResponse {
        private Integer code;
        private String msg;
        private Long time;
        private List<FileUploadResponseItem> data;

        @Data
        static class FileUploadResponseItem {
            private String createBy;
            private Long createTime;
            private Object delFlag;
            private String guid;
            private String name;
            private String prefix;
            private Long size;
            private String tenantId;
            private String tenantName;
            private String type;
            private String updateBy;
            private Long updateTime;
            private String nodeName;
            private String nodeIp;
            private String tempUrl;
            private String proxyAddr;
        }
    }


}
