package com.chinatower.platform.controller;

import com.alibaba.excel.EasyExcel;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.platform.easy.ExcelInterceptListener;
import com.chinatower.platform.easy.dto.InterceptOrderEntityVo;
import com.chinatower.platform.elasticsearch.repository.WarningRepository;
import com.chinatower.platform.service.CentralNodeService;
import com.chinatower.platform.task.SynchLocalizationAlarmTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * video-task-data
 * @ClassName TestController
 * @Description 看守卫服务
 * @Author lyy
 * @Date 2023/11/01
 **/
@RestController
@Slf4j
@Api(value = "task-data测试模块",tags = "task-data测试模块")
public class TestController {
    @Autowired
    private ApplicationContext applicationContext;

    private static final String SUCCESS = "success";

    @Autowired
    private SynchLocalizationAlarmTask localizationAlarmTask;

    @Autowired
    private FileObsFeignClient fileClient;


    @Autowired
    private WarningRepository warningRepository;

    @Autowired
    private CentralNodeService centralNodeService;


    /**
     * 功能描述：属地化下沉节点告警数据同步定时任务
     */
    @ApiOperation(value = "属地化下沉节点告警数据同步定时任务")
    @PostMapping("/task/test/sinkingNodeAlarmHandler")
    public String sinkingNodeAlarmHandler(String methodName, String param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {

        Class<?> cls = Class.forName("com.chinatower.platform.task.SynchLocalizationAlarmTask");
        // 获取spring中的bean对象
        Object bean = applicationContext.getBean(cls);
        // 获取mybatis方法.形参是待执行方法名
        Method method = cls.getMethod(methodName, String.class);
        // 执行方法
        method.invoke(bean, param);

        return SUCCESS;
    }

    @Autowired
    private StringEncryptor encryptor;

    /**
     * 功能描述：上传下沉节点工单数据至es
     * 下载ofs上传的下沉节点工单上传至es
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/downOfs")
    public String downLoadOfs(String param) throws Exception {
        centralNodeService.pushEsWarningOrderByOfs(param);
        return "OK";
    }

    /**
     * 功能描述：加密
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/task/test/decrypt")
    public String decrypt(String param) throws Exception {

        return encryptor.encrypt(param);
    }

    //@Resource
    private ExcelInterceptListener excelListener;

   /* @PostMapping("/task/test2")
    public void test(String param) throws Exception {
        Path tempFile = Files.createTempFile("new_url", ".xlsx");
        try (BufferedInputStream inStream = new BufferedInputStream(new URL(param).openStream());
             BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {

            byte[] buffer = new byte[16 * 1024]; // Increase the buffer size to 16KB
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            File file = tempFile.toFile();
            MultipartFile multipartFile = new CommonsMultipartFile(creatFileItem(file,tempFile));
            EasyExcel.read(multipartFile.getInputStream(), InterceptOrderEntityVo.class, excelListener).doReadAll();
        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*@PostMapping("/task/test")
    public void test2(String param) throws Exception {
        Path tempFile = Files.createTempFile("new_url", ".xlsx");
        BufferedInputStream inStream = null;
        BufferedOutputStream os = null;
        try {
            inStream = new BufferedInputStream(new URL(param).openStream());
            os = new BufferedOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE));

            byte[] buffer = new byte[16 * 1024]; // Increase the buffer size to 16KB
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            File file = tempFile.toFile();
            MultipartFile multipartFile = new CommonsMultipartFile(creatFileItem(file,tempFile));
            EasyExcel.read(multipartFile.getInputStream(), InterceptOrderEntityVo.class, excelListener).doReadAll();
        } catch (IOException e) {
            // 处理或抛出异常
            e.printStackTrace();
        } finally {

            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                // 处理或抛出异常
                e.printStackTrace();
            }
        }
    }*/
    public static FileItem creatFileItem(File file,Path path) throws IOException {

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory(16, null);
        FileItem fileItem = diskFileItemFactory.createItem("textField", "application/zip", true, file.getName());

        int bytesRead = 0;
        byte[] buffer = new byte[16 * 1024];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = fileItem.getOutputStream();
            while ((bytesRead = fileInputStream.read(buffer, 0, 16 * 1024)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Files.copy(file.toPath(), outputStream);
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Files.deleteIfExists(path);
        }
        return fileItem;
    }
    private static FileItem createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }
    /*private File getFile(String url) throws Exception {

        String fileName = url.substring(url.lastIndexOf("."), url.length());
        File file = null;

        URL urlfile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("new_url", ".xlsx");
            //获取文件
            urlfile = new URL(url);
            inStream = urlfile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }
            } catch (Exception e) {
                log.error("获取文件失败:",e);
            }
        }

        return file;
    }*/
}
