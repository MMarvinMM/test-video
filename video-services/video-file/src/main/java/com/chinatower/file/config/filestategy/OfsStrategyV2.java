package com.chinatower.file.config.filestategy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.constant.CompTechConstant;
import com.chinatower.common.entity.Result;
import com.chinatower.file.FileOperation;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.ofs.client.OfsSDKClient;
import com.chinatower.ofs.client.impl.OfsSDKClientImpl;
import com.chinatower.ofs.model.request.FileStoreFileUploadRequest;
import com.chinatower.ofs.model.request.OfsDownloadRequest;
import com.chinatower.ofs.model.request.OfsRefreshUrlRequest;
import com.chinatower.ofs.model.response.OfsUrlVO;
import com.chinatower.ofs.model.response.TFileStoreLog;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class OfsStrategyV2 implements FileOperation {
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
    private Pattern pattern;
    @Setter
    private SysConfigService sysConfigService;

    private final String PLUS_STR = "+";

    @Override
    public Pattern pattern() {
        return pattern;
    }

    private static final String FILE_UPLOAD_FAIL_MSG = "上传文件时发生错误";

    private static final OfsSDKClient ofsClient = OfsSDKClientImpl.builder().build();


   /* private static final OfsSDKClient ofsClient;

     static {
        String sk = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI5GGzKYxK+3VaaWNZHjdBlyacBJb2+hkTfJfQhmefT6302WUhCLcKYeYTUNieiLtNYtYtSoz4l5TZLc4Yrvbfd1/+xakhlXEc3l+GxSxAf+ZpTyLNyVDs+gnhuFY+tO9nwujszxYyVXilQvD/3nYIAvPt74jVysa+v+JAK9dxlVAgMBAAECgYEAhLaWtjrU9ggxaESj0eN3MCmOxeR6g4oV2RElhcl5p/DAjjJTeMVpnjumd4OaigaP+F273B1gAp/Zoc5SwXQYgaFSRC6DZm+9lp3PQxgJrtOdYnCwxS9HEU3PepaMY8ivL9MmJo+iTJTrXnUsAZN7pRy7QUE8Axztc246idAso+ECQQDNnRIKcyBOdxyooEyb2k9huWKFCODgPBJ1dNvxWgzQlCZSoEqSDJW3es18EsXJYHE9I2SERVHwgJzPowix5dkJAkEAsSN+D68gatUdudM38VF4ImottOPOAtqM8MyxSs1HCEtPVEmOnRfWoQ2mB0mfc8tvQ0/CrgO9qWUlUMIpHWrM7QJALfsyTaPxVZrNjehLOY9YsUIEBjw2De9hMWMwzIiW1Q5Mg63U/1RRJvcPfO+1y5MFv+ZqVCosaNxaNRUAnMgKAQJAb4jKijOZtMrFsci108OZYgn7B4fcol1cGw63n0LLtAQ/SfIa0v08jiHa5YHM3Ys9XmOtUAZY+H0QVt46JySlLQJBAK5PWx13gkF39cHgeaizAiGgZnqhYuadZ4I6JHflSncnACqp+VNyx8MXFyBub3XXQFCEZK/qFPlH1jNAG+yxPOo=";
        ofsClient = OfsSDKClientImpl.builder()
                .strategyCode("base_obs_prod")
                .skAndTenantId(sk, "ct-comp-prod")
                .requestDomain("http://10.46.17.122:18081/ofs/v2").build();
    }*/

    @Override
    public FileInfo fileUpload(MultipartFile file) {
        return fileUpload(file, null, false, null);
    }

    @Override
    public FileInfo fileUpload(MultipartFile file, String colonyCode, Boolean useOfs, LoginUser loginUser) {
        try {

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

            FileStoreFileUploadRequest request = new FileStoreFileUploadRequest();
            // 属地编码（可以是中心、也可以是属地）
            request.setColonyCode(colonyCode);
            request.setStoreType(storeType);
            request.setFilePath(filePath);
            Result<List<TFileStoreLog>> result = ofsClient.uploadFile(new MultipartFile[]{multipartFile}, request);
            log.info("OfsStrategyV2 fileUpload request:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
            if (result == null || (CompTechConstant.SUCCESS != result.getCode()) || CollectionUtils.isEmpty(result.getData())) {
                throw new FileOperationException(FILE_UPLOAD_FAIL_MSG);
            }
            TFileStoreLog resultInfo = result.getData().get(0);
            String url = resultInfo.getTempUrl();
            String prefix = resultInfo.getPrefix();
            String relativePath = "/" + colonyCode + (StringUtils.isEmpty(prefix) ? "" : prefix + "/") + resultInfo.getName();
//            String relativePath = (StringUtils.isEmpty(prefix) ? "" : prefix) + resultInfo.getName();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginUrl(relativePath);
            fileInfo.setUrl(url);
            fileInfo.setProxyAddr(resultInfo.getProxyAddr());
            return fileInfo;
        } catch (Exception e) {
            log.error("上传文件错误", e);
            if (e instanceof FileOperationException) {
                throw (FileOperationException) e;
            }
            throw new FileOperationException(FILE_UPLOAD_FAIL_MSG);
        }
    }

    @Override
    public FileInfo fileUploadV3(MultipartFile file, String colonyCode, Boolean useOfs) {
        try {
            FileStoreFileUploadRequest request = new FileStoreFileUploadRequest();
            // 属地编码（可以是中心、也可以是属地）
            request.setColonyCode(colonyCode);
            request.setStoreType(storeType);
            request.setFilePath(filePath);
            Result<List<TFileStoreLog>> result = ofsClient.uploadFile(new MultipartFile[]{file}, request);
            log.info("OfsStrategyV2 fileUpload request:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
            if (result == null || (CompTechConstant.SUCCESS != result.getCode()) || CollectionUtils.isEmpty(result.getData())) {
                throw new FileOperationException(FILE_UPLOAD_FAIL_MSG);
            }
            TFileStoreLog resultInfo = result.getData().get(0);
            String url = resultInfo.getTempUrl();
            String prefix = resultInfo.getPrefix();
            String relativePath = (StringUtils.isEmpty(prefix) ? "" : prefix + "/") + resultInfo.getName();
//            String relativePath = (StringUtils.isEmpty(prefix) ? "" : prefix) + resultInfo.getName();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginUrl(relativePath);
            fileInfo.setUrl(url);
            return fileInfo;
        } catch (Exception e) {
            log.error("上传文件错误", e);
            if (e instanceof FileOperationException) {
                throw (FileOperationException) e;
            }
            throw new FileOperationException(FILE_UPLOAD_FAIL_MSG);
        }
    }

    @Override
    public String encrypt(String url) {
        return encrypt(url, null);
    }

    @Override
    public String encrypt(String url, String colonyCode) {
        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }
//            String whiteAddr = "";
//            //获取文件白名单调用域名集合
//            SysConfig sysConfig = sysConfigService.selectConfigByKey("third_whiteAddr");
//            if (sysConfig != null) {
//                whiteAddr = sysConfig.getConfigValue();
//            }
//            //url中包含白名单配置中的域名，直接返回原地址
//            if (!StringUtils.isEmpty(whiteAddr)) {
//                String[] whiteAddrs = whiteAddr.split(Constants.COMMA);
//                for (String addr : whiteAddrs) {
//                    if (url.contains(addr)) {
//                        return url;
//                    }
//                }
//            }
            // 中心节点存储类型取0
//            String finalStoreType = StringUtils.equals(colonyCode, "10000000001") ? "0" : storeType;

            if (url.contains(PLUS_STR)) {
                log.info("替换+号前url:{}", url);
                url = url.replace(PLUS_STR, "%2B");
                log.info("替换+号后url:{}", url);
            }

            if (url.startsWith("http") || url.startsWith("https")) {
                OfsRefreshUrlRequest request = new OfsRefreshUrlRequest();
                //request.setStoreType(finalStoreType);
                request.setOldUrl(url);
//            request.setTenantId(appId);
//                request.setColonyCode(colonyCode);
                log.info("OfsStrategyV2 refreshOfsTempUrlWithColonyCode request:{}", JSON.toJSONString(request));
                Result<OfsUrlVO> result = ofsClient.refreshOfsTempUrlWithColonyCode(request);
                log.info("OfsStrategyV2 refreshOfsTempUrlWithColonyCode request:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
                if (result != null && CompTechConstant.SUCCESS == result.getCode()) {
                    return result.getData().getProxyAddr();
                }
            } else {
                OfsDownloadRequest request = new OfsDownloadRequest();
                //request.setStoreType(finalStoreType);
//                request.setTargetTenantId(appId);
                request.setFileName(url);
//                request.setColonyCode(colonyCode);
                log.info("OfsStrategyV2 getOfsTempUrlWithColonyCode request:{}", JSON.toJSONString(request));
                Result<OfsUrlVO> result = ofsClient.getOfsTempUrlWithColonyCode(request);
                log.info("OfsStrategyV2 getOfsTempUrlWithColonyCode request:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
                if (result != null && CompTechConstant.SUCCESS == result.getCode()) {
                    return result.getData().getProxyAddr();
                }
            }
        } catch (Exception e) {
            log.error("OfsStrategyV2 encrypt e:", e);
        }
        return null;
    }

    @Override
    public void fileDownload(String url, OutputStream out, boolean isCloseOut) {
        FileOperation.super.fileDownload(url, out, isCloseOut);
    }

    @Override
    public String getFileUrlByFileName(String fileName, String colonyCode, Boolean useOfs) {
        try {
            OfsDownloadRequest request = new OfsDownloadRequest();
            request.setFileName(fileName);
            /*request.setStoreType(storeType);
            request.setColonyCode(colonyCode);
            Result<String> result = ofsClient.getOfsTempUrl(request);*/
            Result<OfsUrlVO> result = ofsClient.getOfsTempUrlWithColonyCode(request);
            log.info("OfsStrategyV2 getOfsTempUrl:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
            if (result != null && CompTechConstant.SUCCESS == result.getCode()) {
                return result.getData().getProxyAddr();
            }
        } catch (Exception e) {
            log.error("OfsStrategyV2 getOfsTempUrl e:{}", e);
        }
        return null;
    }

    @Override
    public String getFileUrlByOldUrl(String oldUrl, String colonyCode, Boolean useOfs) {
        try {
            OfsRefreshUrlRequest request = new OfsRefreshUrlRequest();
            request.setOldUrl(oldUrl);
            //request.setStoreType(storeType);
            //request.setColonyCode(colonyCode);
            //Result<String> result = ofsClient.refreshOfsTempUrl(request);
            Result<OfsUrlVO> result = ofsClient.refreshOfsTempUrlWithColonyCode(request);
            log.info("OfsStrategyV2 refreshOfsTempUrl:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));
            if (result != null && CompTechConstant.SUCCESS == result.getCode()) {
                return result.getData().getProxyAddr();
            }
        } catch (Exception e) {
            log.error("OfsStrategyV2 getOfsTempUrl e:{}", e);
        }
        return null;
    }

    public String refreshOfsTempUrlAndProxyUrlV2(String url) {
        try {
            log.info("ofs v2 request:{}", url);
            OfsRefreshUrlRequest request = new OfsRefreshUrlRequest();
            request.setOldUrl(url);
            log.info("OfsStrategyV2 1.9.21 refreshOfsTempUrlAndProxyUrlV2 request:{}", JSON.toJSONString(request));
            Result<OfsUrlVO> result = ofsClient.refreshOfsTempUrlAndProxyUrlV2(request);
            log.info("OfsStrategyV2 1.9.21 refreshOfsTempUrlAndProxyUrlV2 response:{} result:{}", JSON.toJSON(request), (result != null ? JSON.toJSON(result) : null));

            if (result != null && CompTechConstant.SUCCESS == result.getCode()) {
                String proxyAddr = result.getData().getProxyAddr();
                String url443 = ":443/";
                if (proxyAddr.contains(url443)) {
                    proxyAddr = proxyAddr.replace(url443, "/");
                }
                return proxyAddr;
            }
        } catch (Exception e) {
            log.error("OfsStrategyV2 1.9.21  refreshOfsTempUrlAndProxyUrlV2 e:{}", e);
        }
        return null;
    }
}
