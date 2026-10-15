package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class BigDataPlatformHttpUtil {

    @Value("${bigDataPlatform.url:https://bigdataplatform.chinatowercom.cn:29099/api/admin/tool/report/selectByReportId}")
    private String url;

    @Value("${bigDataPlatform.systemCode:videocommon}")
    private String systemCode;

    @Value("${bigDataPlatform.priKey:MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKf3YKo4O5ynD8TnBwhbrlm8PDhk9oc5BA3iWrBOWaMgmd3WYE2Tgelnw3Ea/IcQQeonSikgASaxTywrWMSjWswZVs2V3bSOHMz+IRw7dsfKX0iqqji3fqMuxWWDOV8djcki4xqzpTIPPMdFDX7CWv2Q9+9koNfBcd2EBEchxLdzAgMBAAECgYBnEg5nZgpunrW2hm5yWpAq0K/Di3EcmYg7XON5vRqUV7UTxDFkaVgW+m2cQosj9n38Sg/gHJuYOsXyvMbEI9na0/v6jIPT8o8eBUFSWZAZaHvkYZBcVrikE3j0hgOOIQmiFYDVKa+/SdVvpn2NLOxP18T2QDIkX0WD+bpYcZveAQJBAO1zvOjxHpl84SBXkJRZpKu8dWOGT2Mi0a6UM+sf3L+iYf++2F5oxBJvk90GfrK2UWe+RpBghYjQSVY9tsNVUpMCQQC1Fib6PRhigFga5sdrnimIUpZjRYkJQ3VZ6lto/lcAHgChd89hEgC+SVi1mpvGJiT652CVqXPFlF5bHA2ehbOhAkEAnqXkgtKO+kgIdj5rF+Mo9aFN4TgclITwCa91XNQF/ne0+FV/ZNyknBi2d0deoXLUwvbLWK1UqstXJGrJUIsSRwJAOOMYo4wF2QvQocLV6uzoxzaKn+z8O7jBaNnClZ0w34yNTixBLZFmQmDwSTykqv7YpymAogN1pyAQ9DY/4QjHgQJAOdRd9YRlxP+w9g5lDml3fHuZmyE6h73d5gkEHD18LxH35qDzEXCQrICeRBAtnsLPhnuYy+ov+3m+4zHelXPPnQ==}")
    private String priKey;

    public Map<String, Object> getParams(String reportId, String params) {
        Map<String, Object> body = new HashMap<>();
        try {
            body.put("pageNum", 1);
            body.put("pageSize", 10000);
            body.put("reportId", reportId);
            body.put("paramsPri", JSONObject.parseObject(params));
            Map<String, Object> stringObjectMap = new HashMap<>();
            Long ts = System.currentTimeMillis();
            stringObjectMap.put("ts",ts);
            String signContent = SignUtils.getSignContent(stringObjectMap);
            String localStr = signContent + "&key=chinatower";

            String sign = RsaUtils.sign(localStr, priKey);
//            boolean flag = RsaUtils.verifySign(localStr, pubKey, sign);
            stringObjectMap.put("sign",sign);
            stringObjectMap.put("systemCode",systemCode);
//            System.out.println(JSONObject.toJSONString(stringObjectMap));
//            System.out.println(flag);
            body.put("params",stringObjectMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    /**
     * post请求第三方接口(该工具类仅针对组件库第三方调用)
     * @param params
     * @return
     */
    public JSONObject doPost(String reportId, String params){
        //前置 使用自定义信任管理器
        disableCertificateValidation();
        try {
            params = JSONObject.toJSONString(this.getParams(reportId, params));
            log.info("BigDataPlatformSignUtil.doPost入参:{},{}", params, url);
            // 1. 创建连接
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // 2. 设置请求属性
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", "Java Client");

            // 3. 启用输出流并设置超时
            con.setDoOutput(true);
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);

            // 4. 发送请求体
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 5. 获取响应码
            int status = con.getResponseCode();
            System.out.println("响应码: " + status);

            // 6. 读取响应
            StringBuilder response = new StringBuilder();
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            (status >= 200 && status < 300) ?
                                    con.getInputStream() : con.getErrorStream(),
                            StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }
            log.info("BigDataPlatformSignUtil.doPost出参:{}", JSON.toJSONString(response));
            return JSONObject.parseObject(response.toString());
        } catch (Exception e) {
            log.error("BigDataPlatformSignUtil.doPost返回错误信息:",e);
        }
        return null;
    }

    public void disableCertificateValidation() {
        try {
            // 创建信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                            // Do nothing because of X and Y.
                            log.info("checkClientTrusted");
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException{
                            // Do nothing because of X and Y.
                            log.info("checkServerTrusted");
                        }
                    }
            };

            // 创建 SSLContext 并初始化
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            // 设置全局 SSL 套接字工厂
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 使用默认的 HostnameVerifier，它会严格验证 hostname 与证书是否匹配
//            HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

//            // 使用示例
//            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://example.com").openConnection();
//            connection.setHostnameVerifier(defaultHostnameVerifier); // 显式设置默认验证器
//
//            // 进行正常的连接操作
//            connection.connect();
//            connection.disconnect();
        } catch (Exception e) {
            log.error("禁用证书验证异常:", e);
        }
    }


}

