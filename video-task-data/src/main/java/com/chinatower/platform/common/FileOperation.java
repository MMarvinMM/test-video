package com.chinatower.platform.common;

import cn.hutool.http.HttpUtil;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface FileOperation {
    Pattern pattern();

    FileInfo fileUpload(MultipartFile file);

    String encrypt(String url);

    default Map<String, String> encryptBatch(List<String> urlList) {
        Map<String, String> result = new HashMap<>();
        for (String url : urlList) {
            result.put(url, encrypt(url));
        }
        return result;
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
    }
}
