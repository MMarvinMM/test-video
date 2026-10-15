package com.chinatower.file.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.file.FileOperation;
import com.chinatower.file.config.ProjectConfig;
import com.chinatower.file.config.runner.FileConfigRunner;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.domain.SysConfig;
import com.chinatower.file.domain.server.AjaxResult;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.request.SysFileRequest;
import com.chinatower.file.request.SysFileUrlRequest;
import com.chinatower.file.service.FileInfoService;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.file.tool.FileInfoObsUrlDeal;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * video-file
 * 通用请求处理
 *
 * @author chinatower
 */
@Slf4j
@RestController
public class CommonController {

    @Autowired
    private FileInfoService sysFileInfoService;

    @Autowired
    private FileInfoObsUrlDeal fileInfoObsUrlDeal;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private SysConfigService sysConfigService;
    @Resource
    FileConfigRunner.FileStrategeManager fileStrategeManager;

    private static final String FILE_NAME_NOT_NULL_MSG = "文件名不能为空";

    public static final String DEPENDENCE_MANAGE = "dependence_manage";

    public static final String PLATFORM_CODE = "platform_code";

    public static final String HTTP_STR = "http";
    public static final String HTTPS_STR = "https";

    public static final String SLASH = "/";
    public static final String ERR_MSG = "入参错误";

    private static final ExecutorService THREAD_POOL_PUSH = Executors.newFixedThreadPool(20);

    /**
     * 通用上传请求
     */
    @PostMapping("/base/common/upload")
    public AjaxResult uploadFile(MultipartFile file) {
        return uploadV2(file, null, null);
    }

    /**
     * 通用上传请求异步
     * @param file
     * @param useOfs
     * @param colonyCode
     * @return
     */
    @PostMapping("/base/common/upload/async")
    public AjaxResult uploadAsync(MultipartFile file, Boolean useOfs, String colonyCode) {
        String uploadKey = String.format("%s-%s", "uploadKey", CommonUtil.getUuid());
        log.info("CommonController uploadAsync step1 uploadKey:{}", uploadKey);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        log.info("CommonController uploadAsync step2 uploadKey:{}", uploadKey);
        //异步上传文件
//        CompletableFutureHelper.orTimeout(
//                CompletableFuture.runAsync(() -> this.uploadAsyncLogic(file, useOfs, colonyCode, loginUser, uploadKey), THREAD_POOL_PUSH)
//                        .exceptionally(e -> {
//                            log.error("异步上传文件失败, e :", e);
//                            return null;
//                        }),
//                10, TimeUnit.SECONDS).exceptionally(e -> {
//                    log.error("异步上传文件超时, e:", e);
//                    return null;
//                }
//        );
        uploadAsyncLogic(file, useOfs, colonyCode, loginUser, uploadKey);
        log.info("CommonController uploadAsync step3 uploadKey:{}", uploadKey);
//        RedisUtil.set(uploadKey, null, RedisDBType.PRIMARY);
        return AjaxResult.success("处理成功", uploadKey);
    }

    private void uploadAsyncLogic(MultipartFile file, Boolean useOfs,String colonyCode, LoginUser loginUser, String uploadKey) {
        try {
            SysFileInfo fileInfo = uploadLogic(file, useOfs, colonyCode, loginUser);
            // 设置10分钟超时
            RedisUtil.set(uploadKey, fileInfo, 600, RedisDBType.PRIMARY);
            log.info("CommonController uploadAsyncLogic uploadKey:{} value:{}", uploadKey, JSON.toJSONString(fileInfo));
        } catch (Exception e) {
            log.error("CommonController uploadAsyncLogic error, e:", e);
        }
    }

    /**
     * 获取异步上传文件结果
     * @param uploadKey
     * @return
     */
    @GetMapping("/base/common/getAsyncUploadResult")
    public AjaxResult getAsyncUploadResult(String uploadKey) {
        try {
            log.info("CommonController getAsyncUploadResult uploadKey:{}",uploadKey);
            SysFileInfo fileInfo = RedisUtil.get(uploadKey, SysFileInfo.class, RedisDBType.PRIMARY);
            log.info("CommonController getAsyncUploadResult result:{}", JSON.toJSONString(fileInfo));
            return AjaxResult.success(fileInfo);
        } catch (Exception e) {
            log.error("CommonController getAsyncUploadResult error, e:", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 上传文件至ofs（属地化下沉节点同步使用）
     * @param file
     * @return
     */
    @PostMapping("/base/common/uploadToOFS")
    public AjaxResult uploadFileToOFS(MultipartFile file) {
        return uploadV3(file);
    }

    /**
     * 上传文件v2
     * @param file
     * @param useOfs
     * @param colonyCode
     * @return
     */
    @PostMapping("/base/common/upload/v2")
    public AjaxResult uploadV2(MultipartFile file, Boolean useOfs, String colonyCode) {
        try {
            log.info("CommonController uploadV2 start");
            SysFileInfo fileInfo = uploadLogic(file, useOfs, colonyCode, SecurityUtils.getLoginUser());
            log.info("CommonController uploadV2 end");
            return AjaxResult.success(fileInfo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 小程序app 调用上传ofs新增
     * @param file
     * @param useOfs
     * @param colonyCode
     * @return
     */
    @PostMapping("/base/common/upload/v4")
    public AjaxResult uploadV4(MultipartFile file, String useOfs, String colonyCode) {
        Boolean useOfsNew = new Boolean(useOfs);
        try {
            log.info("CommonController uploadV4 start");
            SysFileInfo fileInfo = uploadLogic(file, useOfsNew, colonyCode, SecurityUtils.getLoginUser());
            log.info("CommonController uploadV4 end");
            return AjaxResult.success(fileInfo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return AjaxResult.error(e.getMessage());
        }
    }

    private SysFileInfo uploadLogic(MultipartFile file, Boolean useOfs, String colonyCode, LoginUser loginUser) throws Exception {
        // 上传并返回新文件名称
        String originalName = file.getOriginalFilename();
        if(originalName == null){
            throw new CustomException(FILE_NAME_NOT_NULL_MSG);
        }
        if (StringUtils.isEmpty(colonyCode)) {
            SysConfig sysConfig = sysConfigService.selectConfigByKey(PLATFORM_CODE);
            if (sysConfig != null) {
                colonyCode = sysConfig.getConfigValue();
            }
        }
        // 纯属地化节点不能使用ofsV2调用级联网关，需要用原始ofsV1的方式
        if (BooleanUtils.isTrue(useOfs)) {
            SysConfig sysConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
            if (sysConfig != null && Constants.ZERO.equals(sysConfig.getConfigValue())) {
                useOfs = false;
            }
        }
        // 纯属地化节点不能使用ofsV2调用级联网关，需要用原始ofsV1的方式
        SysConfig sysConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
        if (sysConfig != null && Constants.ZERO.equals(sysConfig.getConfigValue())) {
            useOfs = false;
        } else if (sysConfig != null && useOfs == null && Constants.TWO.equals(sysConfig.getConfigValue())){
            useOfs = true;
        }

        FileOperation.FileInfo fileInfo1 = fileStrategeManager.fileUpload(file, colonyCode, useOfs, loginUser);
        String url = fileInfo1.getOriginUrl();
        String proxyAddr = fileInfo1.getProxyAddr();
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileId(IdUtil.simpleUUID());
        fileInfo.setFileUrl(fileInfo1.getUrl());
        fileInfo.setOriginUrl(url);
        fileInfo.setFileName(originalName);
        fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
        fileInfo.setFileSize(file.getSize() + "");
        String decryptHttpUrl = AESUtil.decryptHttp(fileInfo.getFileUrl());
        fileInfo.setFileUrl(decryptHttpUrl);
        fileInfo.setProxyAddr(null == proxyAddr ? decryptHttpUrl : proxyAddr);
        // 处理Obs文件URL加密返回
        fileInfoObsUrlDeal.dealObsFileUrl(fileInfo, useOfs, colonyCode);
        return fileInfo;
    }

    /**
     * 根据文件名获取文件地址
     * @param request
     * @return
     */
    @PostMapping("/base/common/getFileUrlByFileName")
    public Result<String> getFileUrlByFileName(@RequestBody SysFileRequest request) {
        // 上传并返回新文件名称
        if(request == null || StringUtils.isEmpty(request.getFileName())) {
            return Result.fail("文件名称不能为空");
        }
        if (StringUtils.isEmpty(request.getColonyCode())) {
            if (SecurityUtils.getLoginUser() != null) {
                request.setColonyCode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
            }
        }
        String fileUrl = fileStrategeManager.getFileUrlByFileName(request.getFileName(), request.getColonyCode(), request.getUseOfs());
        return Result.data(fileUrl);
    }

    /**
     * 获取加密后的文件Url
     * @param request
     * @return
     */
    @PostMapping("/base/common/getFileUrlByOldUrl")
    public Result<String> getFileUrlByOldUrl(@RequestBody SysFileUrlRequest request) {
        // 上传并返回新文件名称
        if(request == null || StringUtils.isEmpty(request.getOldUrl())) {
            return Result.data("");
        }
        if (StringUtils.isEmpty(request.getColonyCode())) {
            if (SecurityUtils.getLoginUser() != null) {
                request.setColonyCode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
            }
        }
        String fileUrl = fileStrategeManager.getFileUrlByOldUrl(request.getOldUrl(), request.getColonyCode(), request.getUseOfs());
        return Result.data(fileUrl);
    }

    /**
     * 文件上传v3
     * @param file
     * @return
     */
    @PostMapping("/base/common/upload/v3")
    public AjaxResult uploadV3(MultipartFile file) {
        try {
            // 上传并返回新文件名称
            String originalName = file.getOriginalFilename();
            if(originalName == null){
                return AjaxResult.error(FILE_NAME_NOT_NULL_MSG);
            }
            //获取节点编码
            String colonyCode = null;
            int index = originalName.indexOf(Constants.UNDER_LINE);
            if (index != -1) {
                colonyCode = originalName.substring(0, index);
            }
            FileOperation.FileInfo fileInfo1 = fileStrategeManager.fileUploadV3(file, colonyCode, true);
            String url = fileInfo1.getOriginUrl();
            String proxyAddr = fileInfo1.getProxyAddr();
            SysFileInfo fileInfo = new SysFileInfo();
            fileInfo.setFileId(IdUtil.simpleUUID());
            fileInfo.setFileUrl(url);
            fileInfo.setOriginUrl(url);
            fileInfo.setFileName(originalName);
            fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
            fileInfo.setFileSize(file.getSize()+"");
            fileInfo.setProxyAddr(null == proxyAddr ? url : proxyAddr);
            //fileInfo.setFileUrl(AESUtil.decryptHttp(fileInfo.getFileUrl()));
            // 处理OFS文件URL加密返回
            //fileInfoObsUrlDeal.dealObsFileUrl(fileInfo, true, colonyCode);
            return AjaxResult.success(fileInfo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("/base/common/editorUpload")
    public Map<String,Object> uploadEditorFile(MultipartFile file) {
        Map<String,Object> result = new HashMap<>();
        List<String> urls = new ArrayList<>();
        List<String> msgs = new ArrayList<>();
        try {
            // 上传并返回新文件名称
            String originalName = file.getOriginalFilename();
            if(originalName == null){
                msgs.add("上传失败：文件名不能为空");
                urls.add("");
            }else {
                FileOperation.FileInfo fileInfo1 = fileStrategeManager.fileUpload(file);
                String url = fileInfo1.getOriginUrl();
                SysFileInfo fileInfo = new SysFileInfo();
                fileInfo.setFileId(IdUtil.simpleUUID());
                fileInfo.setFileUrl(url);
                fileInfo.setOriginUrl(url);
                fileInfo.setFileName(originalName);
                fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
                fileInfo.setFileSize(file.getSize()+"");
//                sysFileInfoService.insertSysFileInfo(fileInfo);
                String decryptHttpUrl = AESUtil.decryptHttp(fileInfo.getFileUrl());
                fileInfo.setFileUrl(decryptHttpUrl);
                fileInfo.setProxyAddr(null == fileInfo1.getProxyAddr() ? decryptHttpUrl : fileInfo1.getProxyAddr());

                msgs.add("上传成功");
                // 处理Obs文件URL加密返回
                fileInfoObsUrlDeal.dealObsFileUrl(fileInfo);
                urls.add(fileInfo.getFileUrl());
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            msgs.add("上传失败："+e.getMessage());
            urls.add("");
        }
        result.put("errno",0);
        result.put("msg",msgs);
        result.put("data",urls);
        return result;
    }


    /**
     * 新增文件信息,视联使用
     * @auth 李峥
     * @param file 文件信息
     * @return 结果
     */
    @TowerLog(title = "视联上传文件接口", businessType = BusinessType.INSERT)
    @PostMapping("/base/common/uploadFileVideo")
    public Result uploadFile2Video(@RequestParam("uploadFile") MultipartFile file) {
        try {
            // 上传并返回新文件名称
            String originalName = file.getOriginalFilename();
            if(StringUtils.isEmpty(originalName)){
                return Result.fail(FILE_NAME_NOT_NULL_MSG);
            }
            log.info("上传文件名称: {}, mimeType:{}", originalName, file.getContentType());
            FileOperation.FileInfo fileInfo1 = fileStrategeManager.fileUpload(file);
            String url = fileInfo1.getOriginUrl();
            SysFileInfo fileInfo = new SysFileInfo();
            fileInfo.setFileId(IdUtil.simpleUUID());
            fileInfo.setFileUrl(fileInfo1.getUrl());
            fileInfo.setOriginUrl(url);
            fileInfo.setFileName(originalName);
            fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
            fileInfo.setFileSize(file.getSize()+ StringUtils.EMPTY);
//            sysFileInfoService.insertSysFileInfo2Video(fileInfo);
            String decryptHttpUrl = AESUtil.decryptHttp(fileInfo.getFileUrl());
            fileInfo.setFileUrl(decryptHttpUrl);
            fileInfo.setProxyAddr(null == fileInfo1.getProxyAddr() ? decryptHttpUrl : fileInfo1.getProxyAddr());
            // 处理Obs文件URL加密返回
            fileInfoObsUrlDeal.dealObsFileUrl(fileInfo);
            return Result.data(fileInfo,"上传成功");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/base/common/download/name")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.isValidFilename(fileName)) {
                throw new CustomException("文件名称("+fileName+")非法，不允许下载。");
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = ProjectConfig.getDownloadPath() + fileName;

            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete != null && delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

//    @GetMapping("/base/common/download/id/{fileId}")
//    public void downloadById(@PathVariable("fileId") String fileId, HttpServletResponse response)throws Exception{
//        SysFileInfo file= sysFileInfoService.selectSysFileInfoById(fileId);
//        if(file == null){
//            throw new CustomException("文件未找到");
//        }
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        HttpUtil.download(file.getFileUrl(), os, true);
//        FileUtils.download(response, os.toByteArray(), file.getFileName());
//    }

    /**
     * 获取服务器时间
     * @return
     */
    @GetMapping("/getServerTime")
    public Date getServerTime(){
        return new Date();
    }

    /**
     * 下载文件根据redisKey
     * @param key
     * @param response
     * @throws Exception
     */
    @GetMapping("/base/common/download/configkey/{key}")
    public void downloadByUrl(@PathVariable("key") String key, HttpServletResponse response) throws Exception{
        SysConfig sysConfig = sysConfigService.selectConfigByKey(key);
        if(Objects.isNull(sysConfig)){
            throw new CustomException("未找到该系统参数配置："+key);
        }
        String encryptFileUrl = fileUrlEncryptUtil.dealObsFileUrl(sysConfig.getConfigValue());
        /*SysFileInfo file = sysFileInfoService.selectSysFileInfoById(configVal);
        if(file == null){
            throw new CustomException("编码["+configVal+"]的文件获取失败");
        }*/
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpUtil.download(encryptFileUrl,os,true);
        FileUtils.download(response, os.toByteArray(), sysConfig.getConfigName());
    }


    @GetMapping("/common/temp/syncNoticeData")
    @TowerLog(title = "通知管理")
    public AjaxResult syncNoticeData(){
//        SysNotice notice = new SysNotice();
//        notice.setStatus("1");
//        List<SysNotice> notices = sysFileInfoMapper.selectNoticeList(notice);
        return AjaxResult.success();
    }

    /**
     * 下载文件接口,视联使用
     * @auth 李峥
     * @param fileId 文件id
     * @return 结果
     */
//    @GetMapping("/common/downloadById2Video")
//    public Result<SysFileInfoResponse> downloadById2Video(@RequestParam("fileId") String fileId)throws Exception{
//        SysFileInfo file = sysFileInfoService.selectSysFileInfoById(fileId);
//        if(file == null){
//            throw new CustomException("文件未找到");
//        }
//        SysFileInfoResponse sysFileInfoResponse = BeanUtil.copyProperties(file, SysFileInfoResponse.class);
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        HttpUtil.download(file.getFileUrl(), os, true);
//        byte[] responseBytes = os.toByteArray();
//        sysFileInfoResponse.setFileContent(responseBytes);
//        return Result.data(sysFileInfoResponse, "下载成功");
//    }

    /**
     * 下载文件接口，林业使用
     * @param url
     * @return
     */
    @GetMapping("/common/downloadByUrl")
    public Result<Object> downloadByUrl(@RequestParam("url") String url) {
        log.info("入参url:{}", url);
        if (!url.startsWith(HTTP_STR) && !url.startsWith(HTTPS_STR)) {
            throw new CustomException(ERR_MSG);
        }

        int twoSlashIndex = url.indexOf(SLASH, url.indexOf(SLASH) + 1) + 1;
        int thirdSlashIndex = url.indexOf(SLASH, twoSlashIndex);

        if (-1 == thirdSlashIndex) {
            throw new CustomException(ERR_MSG);
        }

        String secondEncryptSwitch = "0";
        SysConfig sysConfig = sysConfigService.selectConfigByKey("second_encrypt_switch");
        if (sysConfig != null) {
            secondEncryptSwitch = sysConfig.getConfigValue();
        }
        if(!url.contains("https://obs-slwai-img") && secondEncryptSwitch.equals("1")){
            //二次加密，防止非法url入侵
            String newUrl = fileUrlEncryptUtil.dealObsFileUrl(url);
            if(!StringUtils.isEmpty(newUrl)){
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                HttpUtil.download(newUrl,os,false);
                byte[] responseBytes = os.toByteArray();
                return Result.data(responseBytes);
            }else{
                throw new CustomException(ERR_MSG);
            }
        }else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HttpUtil.download(url,os,false);
            byte[] responseBytes = os.toByteArray();
            return Result.data(responseBytes);
        }
    }

    /**
     * 获取序列值
     * @param name
     * @return
     */
    @GetMapping("/common/getSeqByName/{name}/{number}")
    public Result<String> getSeqByName(@PathVariable("name") String name,@PathVariable("number") String number) {
        String seqId = null;
        try {
            seqId = sysFileInfoService.getSeqByName(name,number);
        } catch (Exception e) {
            log.error("获取序列异常", e);
            return Result.fail(ResultCode.ERROR.getCode(), "获取序列异常");
        }
        return Result.data(seqId);
    }

    /**
     * 对入参fileUrl进行obs加密返回
     * @param fileUrl
     * @return
     */
    @GetMapping("/common/getObsFileUrlEncrypt")
    public Result<String> getObsFileUrlEncrypt(@RequestParam("fileUrl") String fileUrl) {
        try {
            String activeProfile = SpringUtil.getActiveProfile();
            if (!activeProfile.equals("ter")) {
                if (fileUrl.contains("?")) {
                    fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
                }
            }
            fileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileUrl);
        } catch (Exception e) {
            log.error("对入参fileUrl进行obs加密异常", e);
            return Result.fail(ResultCode.ERROR.getCode(), "obs加密异常");
        }
        return Result.data(fileUrl);
    }

    /**
     * 获取中心节点文件的解密地址
     */
    @GetMapping("/common/getCenterNodeFileUrlEncrypt")
    public Result<String> getCenterNodeFileUrlEncrypt(@RequestParam("fileUrl") String fileUrl) {
        try {
            String centerColonyCode = sysConfigService.getCenterColonyCode();
            fileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileUrl, true, centerColonyCode);
        } catch (Exception e) {
            log.error("对入参fileUrl进行ofs加密异常", e);
            return Result.fail(ResultCode.ERROR.getCode(), "ofs加密异常");
        }
        return Result.data(fileUrl);
    }

    /**
     * 获取中心节点文件的解密地址 - 批量
     */
    @PostMapping("/common/getCenterNodeFileUrlEncryptBatch")
    public Result<Map<String,String>> getCenterNodeFileUrlEncryptBatch(@RequestBody FileObsEncryptRequest param) {
        Map<String,String> result = null;
        try {
            String centerColonyCode = sysConfigService.getCenterColonyCode();
            result = fileUrlEncryptUtil.dealObsFileUrlBatch(param.getFileUrls(), true, centerColonyCode);
        } catch (Exception e) {
            log.error("getCenterNodeFileUrlEncryptBatch ofs加密异常", e);
            return Result.fail(ResultCode.ERROR.getCode(), "ofs加密异常");
        }
        return Result.data(result);
    }

    /**
     * 通用上传请求，免登录
     */
    @PostMapping("/base/common/uploadNoLogin")
    public AjaxResult uploadNoLogin(MultipartFile file) {
        try {
            log.info("CommonController uploadNoLogin start");
            SysFileInfo fileInfo = uploadLogic(file, null, null, null);
            log.info("CommonController uploadNoLogin end");
            return AjaxResult.success(fileInfo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return AjaxResult.error(e.getMessage());
        }
    }
}
