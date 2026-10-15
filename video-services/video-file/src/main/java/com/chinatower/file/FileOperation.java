package com.chinatower.file;

import cn.hutool.http.HttpUtil;
import com.chinatower.file.request.ToEncryptUrl;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface FileOperation {
    Pattern pattern();

    FileInfo fileUpload(MultipartFile file) throws Exception;

    default FileInfo fileUploadWithLoginInfo(MultipartFile file, LoginUser loginUser) throws Exception {
        return fileUpload(file);
    }

    default FileInfo fileUploadV3(MultipartFile file, String colonyCode, Boolean useOfs) throws Exception {
        return fileUpload(file);
    }

    default FileInfo fileUpload(MultipartFile file, String colonyCode, Boolean useOfs, LoginUser loginUser) throws Exception {
        return fileUpload(file);
    }

    String encrypt(String url);

    default String encrypt(String url, boolean useOfs, String colonyCode) {
        return encrypt(url);
    }

    default String encrypt(String url, String colonyCode) {
        return encrypt(url);
    }

    default String getFileUrlByFileName(String fileName, String colonyCode, Boolean useOfs) {
        return null;
    }

    default String getFileUrlByOldUrl(String oldUrl, String colonyCode, Boolean useOfs) {
        return null;
    }

    default Map<String, String> encryptBatch(List<String> urlList) {
        Map<String, String> result = new HashMap<>();
        for (String url : urlList) {
            result.put(url, encrypt(url));
        }
        return result;
    }

    default <T extends ToEncryptUrl> List<T> genericEncryptBatch(List<T> urlList) {
        for (T t : urlList) {
            t.setResultUrl(encrypt(t.getOriginalUrl()));
        }
        return urlList;
    }


    default void fileDownload(String url, OutputStream out, boolean isCloseOut) {
        HttpUtil.download(url, out, isCloseOut);
    }

    class FileOperationException extends RuntimeException {
        public FileOperationException(String message) {
            super(message);
        }
    }

    @Data
    class FileInfo {
        /**
         * 未加密的url
         */
        private String originUrl;
        /**
         * 加密的url
         */
        private String url;


        private String proxyAddr;
    }
}
