package com.chinatower.file.xssFilter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XSSMultipartHttpServletRequest extends DefaultMultipartHttpServletRequest {
    public XSSMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
        super(request);
        setMultipartFiles(mpFiles);
        setMultipartParameters(mpParams);
        setMultipartParameterContentTypes(mpParamContentTypes);
    }

    public XSSMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
        MultiValueMap<String, MultipartFile> result = super.getMultipartFiles();
        Set<String> keySet = result.keySet();
        for (String key : keySet) {
            List<MultipartFile> fileList = result.get(key);
            List<MultipartFile> multipartFiles = new ArrayList<>();
            for (MultipartFile file : fileList) {

                if (file!= null && file.getOriginalFilename()!= null && !file.getContentType().contains("image")) {
                    String fileName = file.getOriginalFilename();

                    try {
                        // 1. 读取文件内容（二进制文件也读取，但不解析）
                        byte[] fileBytes;
                        try (InputStream inputStream = file.getInputStream()) {
                            fileBytes = IOUtils.toByteArray(inputStream); // 读取全部字节
                        }

                        // 2. 尝试检测 XSS（仅对文本文件进行严格检查，二进制文件简单检查）
                        String content = new String(fileBytes, StandardCharsets.UTF_8);

                        // 3. 检查 XSS
                        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (StringUtils.hasText(line) && !validatorXSS(line)) {
                                    throw new Exception("检测到潜在 XSS 攻击");
                                }
                            }
                        }

                        // 4. 如果未检测到 XSS，直接返回原文件（不修改）
                        DiskFileItemFactory factory = new DiskFileItemFactory();
                        FileItem fileItem = factory.createItem("file", file.getContentType(), false, fileName);
                        try (OutputStream outputStream = fileItem.getOutputStream()) {
                            outputStream.write(fileBytes); // 直接写入原始字节
                        }
                        multipartFiles.add(new CommonsMultipartFile(fileItem));

                    } catch (Exception e) {
                        log.error("处理文件时出错：", e);
                        throw new RuntimeException("文件验证失败: " + e.getMessage());
                    }
                } else if (file!= null && file.getOriginalFilename()!= null && file.getContentType().startsWith("image")) {
                    try {
                        // 1. 检查图片是否真的是图片（防止伪造）
                        if (!isValidImage(file)) {
                            throw new IllegalArgumentException("无效图片，请换一张");
                        }
                        multipartFiles.add(file);
                    } catch (Exception e) {
                        log.error("图片验证失败: {}", e.getMessage());
                        throw new RuntimeException("图片验证失败: " + e.getMessage());
                    }
                } else {
                    multipartFiles.add(file);
                }
            }

            result.put(key, multipartFiles);

        }
        return result;
    }

    private boolean validatorXSS(String value) {
        value = ESAPI.encoder().canonicalize(value,false,false);

        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid anything between pdf script tags
        scriptPattern = Pattern.compile("javascript", Pattern.CASE_INSENSITIVE);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }
        // Avoid php:... expressions
        scriptPattern = Pattern.compile("<\\?php(.*?)\\?>", Pattern.CASE_INSENSITIVE);
        matcher = scriptPattern.matcher(value);
        if (matcher.find()){
            return false;
        }

        // Avoid οnlοad= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        matcher = scriptPattern.matcher(value);
        return !matcher.find();

    }


    /**
     * 验证图片是否合法（检查文件头，防止伪造）
     */
    private boolean isValidImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[8]; // 读取前 8 字节（足够判断常见图片格式）
            int bytesRead = inputStream.read(header, 0, 8);
            if (bytesRead < 8) {
                return false; // 文件太小，不可能是合法图片
            }
            // 检查是否是 PNG、JPEG、GIF、TIFF 等格式
            return isPng(header) || isJpeg(header) || isGif(header) || isTiff(header);
        }
    }

    // PNG 文件头：89 50 4E 47 0D 0A 1A 0A
    private boolean isPng(byte[] header) {
        return header[0] == (byte) 0x89 &&
                header[1] == (byte) 0x50 &&
                header[2] == (byte) 0x4E &&
                header[3] == (byte) 0x47;
    }

    // JPEG 文件头：FF D8
    private boolean isJpeg(byte[] header) {
        return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8;
    }

    // GIF 文件头：47 49 46 38 (GIF8)
    private boolean isGif(byte[] header) {
        return header[0] == (byte) 0x47 &&
                header[1] == (byte) 0x49 &&
                header[2] == (byte) 0x46 &&
                header[3] == (byte) 0x38;
    }

    // TIFF 文件头：49 49 2A 00 (小端) 或 4D 4D 00 2A (大端)
    private boolean isTiff(byte[] header) {
        // 小端 TIFF：II*
        boolean littleEndianTiff = header[0] == (byte) 0x49 &&
                header[1] == (byte) 0x49 &&
                header[2] == (byte) 0x2A &&
                header[3] == (byte) 0x00;
        // 大端 TIFF：MM*
        boolean bigEndianTiff = header[0] == (byte) 0x4D &&
                header[1] == (byte) 0x4D &&
                header[2] == (byte) 0x00 &&
                header[3] == (byte) 0x2A;
        return littleEndianTiff || bigEndianTiff;
    }

}

