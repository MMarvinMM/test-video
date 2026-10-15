package com.chinatower.platform.project.common.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.UserCommonCollectionDTO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.file.ObsClientUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.config.ProjectConfig;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.common.service.ICommonService;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.domain.SysFileInfo;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.platform.project.system.mapper.SysNoticeMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.project.system.service.ISysFileInfoService;
import com.chinatower.platform.props.HwObsProperties;
import com.chinatower.platform.util.ObsSignUrlUtils;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * video-system
 * 通用请求处理
 *
 * @author chinatower
 */
@Slf4j
@RestController
public class CommonController{

    @Autowired
    private ISysFileInfoService sysFileInfoService;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private ObsClientUtil obsClientUtil;

    @Autowired
    private ObsSignUrlUtils obsSignUtils;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private HwObsProperties obsProperties;

    @Value("${maxPageSize.oracle:1000}")
    private long maxPageSize;

    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private ICommonService iCommonService;

    @Resource
    FileFeignClient fileFeignClient;

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
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("/base/common/upload")
    public AjaxResult uploadFile(MultipartFile file) {
        try {
            // 上传并返回新文件名称
            String originalName = file.getOriginalFilename();
            if(originalName == null){
                return AjaxResult.error("文件名不能为空");
            }
            SysFileInfo fileInfo = new SysFileInfo();
            com.chinatower.common.core.web.domain.AjaxResult ajaxResult = fileFeignClient.uploadFile(file);
            if (ajaxResult != null && ajaxResult.ok()) {
                com.chinatower.file.vo.SysFileInfo  sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
                String url = sysFileInfo.getOriginUrl();
                fileInfo.setFileUrl(url);
                fileInfo.setOriginUrl(url);
            }
            fileInfo.setFileId(IdUtil.simpleUUID());
            fileInfo.setFileName(originalName);
            fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
            fileInfo.setFileSize(file.getSize()+"");
//            sysFileInfoService.insertSysFileInfo(fileInfo);
            fileInfo.setFileUrl(AESUtil.decryptHttp(fileInfo.getFileUrl()));
            // 处理Obs文件URL加密返回
            String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileInfo.getFileUrl());
            fileInfo.setFileUrl(obsFileUrl);
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

                SysFileInfo fileInfo = new SysFileInfo();
                com.chinatower.common.core.web.domain.AjaxResult ajaxResult = fileFeignClient.uploadFile(file);
                if (ajaxResult != null && ajaxResult.ok()) {
                    com.chinatower.file.vo.SysFileInfo sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
                    String url = sysFileInfo.getOriginUrl();
                    fileInfo.setFileUrl(url);
                    fileInfo.setOriginUrl(url);
                }

                fileInfo.setFileId(IdUtil.simpleUUID());
                fileInfo.setFileName(originalName);
                fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
                fileInfo.setFileSize(file.getSize()+"");
//                sysFileInfoService.insertSysFileInfo(fileInfo);
                fileInfo.setFileUrl(AESUtil.decryptHttp(fileInfo.getFileUrl()));
                msgs.add("上传成功");
                // 处理Obs文件URL加密返回
                String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileInfo.getFileUrl());
                urls.add(obsFileUrl);
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

//    @GetMapping("/base/common/download/id/{fileId}")
//    public void downloadById(@PathVariable("fileId") String fileId, HttpServletResponse response)throws Exception{
//        SysFileInfo file= sysFileInfoService.selectSysFileInfoById(fileId);
//        if(file == null){
//            throw new CustomException("文件未找到");
//        }
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        HttpUtil.download(file.getFileUrl(),os,true);
//        FileUtils.download(response, os.toByteArray(), file.getFileName());
//    }

    @GetMapping("/getServerTime")
    public Date getServerTime(){
        return new Date();
    }

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
        SysNotice notice = new SysNotice();
        notice.setStatus("1");
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();
        notice.setPlatformCode(platformCode);
        List<SysNotice> notices = noticeMapper.selectNoticeList(notice);
        return AjaxResult.success();
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
                return Result.fail("文件名不能为空");
            }
            Result result = fileFeignClient.uploadFile2Video(file);
            com.chinatower.file.vo.SysFileInfo data =
                    JSON.parseObject(JSON.toJSONString(result.getData()), com.chinatower.file.vo.SysFileInfo.class);
            SysFileInfo fileInfo = new SysFileInfo();
            if (data != null) {
                String url = data.getOriginUrl();
                fileInfo.setOriginUrl(url);
                fileInfo.setFileUrl(data.getFileUrl());
            }
            fileInfo.setFileId(IdUtil.simpleUUID());
            fileInfo.setFileName(originalName);
            fileInfo.setFileSuffix(originalName.substring(originalName.lastIndexOf(".")));
            fileInfo.setFileSize(file.getSize()+ StringUtils.EMPTY);
//            sysFileInfoService.insertSysFileInfo2Video(fileInfo);
            fileInfo.setFileUrl(AESUtil.decryptHttp(fileInfo.getFileUrl()));
            // 处理Obs文件URL加密返回
            String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileInfo.getFileUrl());
            fileInfo.setFileUrl(obsFileUrl);
            return Result.data(fileInfo,"上传成功");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 下载文件接口,视联使用
     * @auth 李峥
     * @param fileId 文件id
     * @return 结果
     */
//    @GetMapping("/common/downloadById2Video")
//    public Result<SysFileInfoResponse> downloadById2Video(@RequestParam("fileId") String fileId) throws Exception {
//        SysFileInfo file = sysFileInfoService.selectSysFileInfoById(fileId);
//        if (file == null) {
//            throw new CustomException("文件未找到");
//        }
//        SysFileInfoResponse sysFileInfoResponse = BeanUtil.copyProperties(file, SysFileInfoResponse.class);
//        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//            String s = fileUrlEncryptUtil.dealObsFileUrl(file.getFileUrl());
//            HttpUtil.download(s, os, true);
//            byte[] responseBytes = os.toByteArray();
//            sysFileInfoResponse.setFileContent(responseBytes);
//        } catch (Exception e) {
//            log.error("文件下载异常", e);
//            return Result.fail("文件异常");
//        }
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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpUtil.download(url,os,false);
        byte[] responseBytes = os.toByteArray();
        return Result.data(responseBytes);
    }

    /**
     * 获取序列值
     * @param name
     * @return
     */
    @GetMapping("/common/getSeqByName/{name}/{number}")
    public Result<String> getSeqByName(@PathVariable("name") String name,@PathVariable("number") String number) {
        Result<String> result = null;
        String seqId = null;
        try {
           seqId = iCommonService.getSeqByName(name,number);
        } catch (Exception e) {
            log.error("获取序列异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "获取序列异常");
        }
        return result.data(seqId);
    }

    /**
     * 对入参fileUrl进行obs加密返回
     * @param fileUrl
     * @return
     */
    @GetMapping("/common/getObsFileUrlEncrypt")
    public Result<String> getObsFileUrlEncrypt(@RequestParam("fileUrl") String fileUrl) {
        try {
            fileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileUrl);
        } catch (Exception e) {
            log.error("对入参fileUrl进行obs加密异常", e);
            return Result.fail(ResultCode.ERROR.getCode(), "对入参fileUrl进行obs加密异常");
        }
        return Result.data(fileUrl);
    }


    /**
     * 新增/取消收藏
     * @param userCommonCollectionDTO
     * @return Result
     * @author LQ
     * @date 2024/5/29
     */
    @PostMapping("/common/addOrCancelCollections")
    Result addOrCancelCollections(@RequestBody UserCommonCollectionDTO userCommonCollectionDTO) {
        if (userCommonCollectionDTO.getOptType() == 1) {
            iCommonService.addCollections(userCommonCollectionDTO.getCollObjCode(), userCommonCollectionDTO.getCollObjType());
            return Result.success("新增收藏成功");
        } else {
            iCommonService.cancelCollections(userCommonCollectionDTO.getCollObjCode(), userCommonCollectionDTO.getCollObjType());
            return Result.success("取消收藏成功");
        }
    }

}
