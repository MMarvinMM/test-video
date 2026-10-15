//import cn.hutool.core.date.DateUtil;
//import com.alibaba.fastjson.JSON;
//import com.chinatower.file.config.filestategy.OfsStrategy;
//import com.google.common.collect.ImmutableMap;
//import okhttp3.*;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//public class OfsTest {
//    private static final String UPLOAD_FILE_URL = "/api/upload/file";
//    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .callTimeout(10, TimeUnit.SECONDS)
//            .build();
//
//    public static void main(String[] args) throws Exception {
//        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKTm6SVh6OG2AbZ1DwiWUVugoftrVFROm6mGwafSiN+qBw2U6jXs/gk1XKWSMWvpOlHW5SN6TryYURgIbRzWzeYa2PJ8xnOI4hdLjghpC9VVdeT6xv8VZpVxFUvJf09Gxbo0OQDHIBZpccuQebWpfPfnHnHuyCToRm/ERyXJfVH1AgMBAAECgYBVqGj1dS2eiYfMOnRLtqGeYguiaTdN7+NmJUzVG0TkQhMV2APtbSTsrZ5pl046ytTzjBhM3Iyn5MtDG6VqNJ6REm1QUMfQlAjj73sO9igvfYmKlv7XJnwgWWhVOPSCAdQ7rPyOrRCuACRAjIx3Yh4/VYT3QfNArqcxaMNmHTbawQJBAM6LsvJQcS/SYb4vfjNAtjKWmtQUF/2mq18qlCeRtoZvEi4+61MmHXu7ishHcmXOuLrskMYgbLBuuUjTemnlPJECQQDMYqTuVPEEAfxPy+A8dmXF0hs3Ji6DrFdeJBbkeUd1anmZ8bllsYhdUBj3NAAXm/o/ObeJats1fWX5eMa/rwElAkEAhXt/EF6wZ4l9JZ4n3xOpLBRsxzadMo4uHniz+saOwV6yzzRWfQwa7cYMMNcLtYPdmuAbyA9YbsV/9PLHH/zIIQJBALds0BgUozi1j3sNvXCF0VB3/3S/D3+ca8xRFzp0nW0C4Z8HERvy+C7Gc6FnSa3mje+wHMd23sRiheIeQkBFDwECQCh6uPlsFFdr2q2RDggasTXQnR/1x2Kc2dyuR4iHURRa/Cn2etKCsj94DyZXQK1bCZt9LO5kw3veGyGoz5uwWRk=";
//        String tenantId = "file-test";
//        String filePath = "/test1";
//        String s1 = DateUtil.date().getTime() + "";
//        File t = new File("C:\\Users\\tower\\Documents\\WeChat Files\\zhaoxiuhuan\\FileStorage\\File\\2023-01\\RsaUtils.java");
////        DiskFileItem files = new DiskFileItem("files", "text/plain", true, "test.txt", 12223618, t);
////        MultipartFile file = new CommonsMultipartFile(files);
//
////        String s = RsaUtils.sign("filePath=/test1&tenantId=file-test&timestamp=" + s1 + "&key=" + privateKey, privateKey);
////        System.out.println(s);
//
//        String time = DateUtil.date().getTime() + "";
//        String toSignStr = String.format("filePath=%s&tenantId=%s&timestamp=%s&key=%s", filePath, tenantId, time, privateKey);
//        System.out.println(toSignStr);
//        String sign = sign(toSignStr, privateKey);
//        ImmutableMap<Object, Object> immutableMap = ImmutableMap.builder()
//                .put("filePath", filePath)
//                .put("tenantId", tenantId)
//                .put("sign", sign)
//                .put("timestamp", time)
//                .build();
//
//        MultipartBody multipartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("files", t.getName(), RequestBody.create(MediaType.parse("text/plain"), FileUtils.readFileToByteArray(t)))
//                .addFormDataPart("request", JSON.toJSONString(immutableMap))
//                .build();
//
//        Request request = new Request.Builder()
//                .header("Content-Type", "application/form-data")
//                .header("Accept", "*/*")
//                .url("http://comp.tower0788.cn:8091/ofs" + UPLOAD_FILE_URL)
//                .post(multipartBody)
//                .build();
//        String s2 = JSON.toJSONString(immutableMap);
//        System.out.println(s2);
//        Response response = CLIENT.newCall(request).execute();
//        if (response.isSuccessful()) {
//            String string = response.body().string();
//            System.out.println(string);
//            OfsStrategy.FileUploadResponse fileUploadResponse = JSON.parseObject(string, OfsStrategy.FileUploadResponse.class);
//            System.out.println(fileUploadResponse);
//        }
//    }
//}
