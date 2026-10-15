package com.chinatower.platform.modules.screen.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大屏 iframe URL 参数替换工具
 */
public final class ScreenUrlUtils {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("([?&])token=[^&]*");
    private static final Pattern REFRESH_TOKEN_PATTERN = Pattern.compile("([?&])refreshToken=[^&]*");

    private ScreenUrlUtils() {
    }

    /**
     * 替换 URL 中的 token 与 refreshToken 参数，保留其余部分不变
     */
    public static String replaceTokenParams(String url, String token, String refreshToken) {
        if (StrUtil.isBlank(url)) {
            return url;
        }
        if (!url.contains("token=") || !url.contains("refreshToken=")) {
            return null;
        }
        String result = TOKEN_PATTERN.matcher(url).replaceAll("$1token=" + Matcher.quoteReplacement(token));
        return REFRESH_TOKEN_PATTERN.matcher(result).replaceAll("$1refreshToken=" + Matcher.quoteReplacement(refreshToken));
    }
}
