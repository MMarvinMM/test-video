package com.chinatower.bdm.common;


import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.platform.common.exception.CustomException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传工具类
 *
 * @author
 */
public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;


    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 返回上传成功的文件名
     */
    public static final String upload(String baseDir, MultipartFile file)throws Exception {
        return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)throws Exception {
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new CustomException("文件名称太长");
        }

        String fileName = extractFilename(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc);
        return baseDir + GeoServer.PATH + fileName;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
//        String extension = getExtension(file);
//        fileName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
        return fileName;
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    public static final File getAbsoluteFile(String uploadDir) throws IOException {
        File desc = new File(uploadDir);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filenameWithoutExtension = "";
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            filenameWithoutExtension = originalFilename.replace(extension, "");
        }
        return filenameWithoutExtension;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }
}
