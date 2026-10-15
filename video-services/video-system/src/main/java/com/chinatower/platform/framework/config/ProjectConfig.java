package com.chinatower.platform.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author chinatower
 */
@Component
@ConfigurationProperties(prefix = "chinatower")
public class ProjectConfig {

    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 上传路径 */
    private static String profile;

    /** 获取地址开关 */
    private static boolean addressEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public static String getProfile() {
        return profile;
    }

    @Value("${chinatower.profile:}")
    public static void setProfile(String profile) {
        ProjectConfig.profile = profile;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    @Value("${chinatower.addressEnabled}")
    public static void setAddressEnabled(boolean addressEnabled) {
        ProjectConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }
}
