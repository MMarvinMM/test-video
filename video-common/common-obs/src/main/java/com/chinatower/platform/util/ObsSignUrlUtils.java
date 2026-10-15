package com.chinatower.platform.util;



import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.props.HwObsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * @description: obs加密url--以下为华为云提供方法 封装一个对外方法
 * @author: yongqi.han
 * @create: 2022-06-30
 **/
@Component
@Slf4j
@SuppressWarnings("all")
public class ObsSignUrlUtils {
    private static final String SIGN_SEP = "\n";

    private static final String OBS_PREFIX = "x-obs-";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private String AK;
    private String SK;
    private String ENDPOINT;
    private long EXPIRES;

    private static final String X_OBS_DATE_STR = "x-obs-date";


    public ObsSignUrlUtils(HwObsProperties hwObsProperties) {
        this.AK = hwObsProperties.getAk();
        this.SK = hwObsProperties.getSk();
        this.ENDPOINT = hwObsProperties.getEndPoint();
        this.EXPIRES = hwObsProperties.getExpires();
    }



    private static final List<String> SUB_RESOURCES = Collections.unmodifiableList(Arrays.asList(
            "CDNNotifyConfiguration", "acl", "append", "attname", "backtosource", "cors", "customdomain", "delete",
            "deletebucket", "directcoldaccess", "encryption", "inventory", "length", "lifecycle", "location", "logging",
            "metadata", "modify", "name", "notification", "orchestration", "partNumber", "policy", "position", "quota",
            "rename", "replication", "response-cache-control", "response-content-disposition",
            "response-content-encoding", "response-content-language", "response-content-type", "response-expires",
            "restore", " storageClass", "storagePolicy", "storageinfo", "tagging", "torrent", "truncate",
            "uploadId", "uploads", "versionId", "versioning", "versions", "website", "x-image-process",
            "x-image-save-bucket", "x-image-save-object", "x-obs-security-token"));


    public static String urlEncode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, DEFAULT_ENCODING)
                .replace("%7E", "~") //for browser
                .replace("%2F", "/");
    }

    private static String join(List<?> items, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i).toString();
            sb.append(item);
            if (i < items.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    private static boolean isValid(String input) {
        return input != null && !input.equals("");
    }

    public String hamcSha1(String input) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec signingKey = new SecretKeySpec(SK.getBytes(DEFAULT_ENCODING), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return Base64.getEncoder().encodeToString(mac.doFinal(input.getBytes(DEFAULT_ENCODING)));
    }

    private static String stringToSign(String httpMethod, Map<String, String[]> headers, Map<String, String> queries,
                                       String bucketName, String objectName) throws Exception {
        String contentMd5 = "";
        String contentType = "";
        String date = "";

        TreeMap<String, String> canonicalizedHeaders = new TreeMap<String, String>();

        String key;
        List<String> temp = new ArrayList<String>();
        for (Map.Entry<String, String[]> entry : headers.entrySet()) {
            key = entry.getKey();
            if (key == null || entry.getValue() == null || entry.getValue().length == 0) {
                continue;
            }

            key = key.trim().toLowerCase(Locale.ENGLISH);
            if (key.equals("content-md5")) {
                contentMd5 = entry.getValue()[0];
                continue;
            }

            if (key.equals("content-type")) {
                contentType = entry.getValue()[0];
                continue;
            }

            if (key.equals("date")) {
                date = entry.getValue()[0];
                continue;
            }

            if (key.startsWith(OBS_PREFIX)) {

                for (String value : entry.getValue()) {
                    if (value != null) {
                        temp.add(value.trim());
                    }
                }
                canonicalizedHeaders.put(key, join(temp, ","));
                temp.clear();
            }
        }

        if (canonicalizedHeaders.containsKey(X_OBS_DATE_STR)) {
            date = "";
        }


        // handle method/content-md5/content-type/date
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(httpMethod).append(SIGN_SEP)
                .append(contentMd5).append(SIGN_SEP)
                .append(contentType).append(SIGN_SEP)
                .append(date).append(SIGN_SEP);

        // handle canonicalizedHeaders
        for (Map.Entry<String, String> entry : canonicalizedHeaders.entrySet()) {
            stringToSign.append(entry.getKey()).append(":").append(entry.getValue()).append(SIGN_SEP);
        }

        // handle CanonicalizedResource
        stringToSign.append("/");
        if (isValid(bucketName)) {
            stringToSign.append(bucketName).append("/");
            if (isValid(objectName)) {
                stringToSign.append(urlEncode(objectName));
            }
        }

        TreeMap<String, String> canonicalizedResource = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            key = entry.getKey();
            if (key == null) {
                continue;
            }

            if (SUB_RESOURCES.contains(key)) {
                canonicalizedResource.put(key, entry.getValue());
            }
        }

        if (canonicalizedResource.size() > 0) {
            stringToSign.append("?");
            for (Map.Entry<String, String> entry : canonicalizedResource.entrySet()) {
                stringToSign.append(entry.getKey());
                if (isValid(entry.getValue())) {
                    stringToSign.append("=").append(entry.getValue());
                }
                stringToSign.append("&");
            }
            stringToSign.deleteCharAt(stringToSign.length() - 1);
        }

//		System.out.println(String.format("StringToSign:%s%s", SIGN_SEP, stringToSign.toString()));

        return stringToSign.toString();
    }

    public String headerSignature(String httpMethod, Map<String, String[]> headers, Map<String, String> queries,
                                  String bucketName, String objectName) throws Exception {

        //1. stringToSign
        String stringToSign = stringToSign(httpMethod, headers, queries, bucketName, objectName);

        //2. signature
        return String.format("OBS %s:%s", AK, hamcSha1(stringToSign));
    }

    public String querySignature(String httpMethod, Map<String, String[]> headers, Map<String, String> queries,
                                 String bucketName, String objectName, long expires) throws Exception {
        if (headers.containsKey(X_OBS_DATE_STR)) {
            headers.put(X_OBS_DATE_STR, new String[]{String.valueOf(expires)});
        } else {
            headers.put("date", new String[]{String.valueOf(expires)});
        }
        //1. stringToSign
        String stringToSign = stringToSign(httpMethod, headers, queries, bucketName, objectName);

        //2. signature
        return urlEncode(hamcSha1(stringToSign));
    }

    public String getURL(String endpoint, Map<String, String> queries,
                         String bucketName, String objectName, String signature, long expires) {
        StringBuilder URL = new StringBuilder();
        URL.append("https://").append(bucketName).append(".").append(endpoint).append("/").
                append(objectName).append("?");
        String key;
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            key = entry.getKey();
            if (key == null) {
                continue;
            }
            if (SUB_RESOURCES.contains(key)) {
                String value = entry.getValue();
                URL.append(key);
                if (value != null) {
                    URL.append("=").append(value).append("&");
                } else {
                    URL.append("&");
                }
            }
        }
        URL.append("AccessKeyId=").append(AK).append("&Expires=").append(expires).
                append("&Signature=").append(signature);
        return URL.toString();
    }

    /**
     * 功能说明： 将obsurl加密
     *
     * @param url
     * @return String
     * @throws
     * @requestID R-ZBZB-202109030777599 url下载加密
     * @date 2021/9/10 16:40
     */
    public String signUrl(String url) {
        Map<String, String[]> headers = new HashMap<String, String[]>();
        Map<String, String> queries = new HashMap<String, String>();

        try {
            String bucketName = "";
            String objectName = "";
            if (StringUtils.isNotEmpty(url)) {
                bucketName = url.substring(url.indexOf("//") + 2, url.indexOf("."));
                if (url.indexOf(":443/") > -1) {
                    objectName = url.substring(url.indexOf(":443/") + 5, url.length());
                } else {
                    objectName = url.substring(url.indexOf(".com/") + 5, url.length());
                }

            }
            //单位是秒
            long timeOut = (System.currentTimeMillis() +( EXPIRES*1000)) / 1000;
            String signature = querySignature("GET", headers, queries, bucketName, objectName, timeOut);
            url = getURL(ENDPOINT, queries, bucketName, objectName, signature, timeOut);
//            url= URLDecoder.decode(url,"UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("发起请求出现异常:", e);
        }
        return url;
    }


}
