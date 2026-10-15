package com.chinatower.bdm.modules.grid.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.common.ShapeUtil;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.config.IServer;
import com.chinatower.bdm.modules.grid.dao.BboxDTO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.file.FileUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.file.SysFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 解析网格
 * @data 创建时间：2023/2/17 17:10
 */
@RefreshScope
@Service
@Slf4j
@SuppressWarnings("all")
public class WgsAnalysisService {
    @Resource
    private SystemFeignClient systemFeignClient;

    @Value("${wgs84.root:/app/video-platform/video-service}")
    private String wgs84Root;

    public String getWgs84Root() {
        return wgs84Root;
    }

    public void setWgs84Root(String wgs84Root) {
        this.wgs84Root = wgs84Root;
    }

    @Value("${analysisGrid:GeoServer}")
    private String analysisConfig;

    @Resource
    GeoServer geoServer;
    @Resource
    IServer iServer;


    /**
     * 校验文件名称是否包含中文
     * @param str
     */
    public void checkStrChinaese(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if(m.find()) {
            // 如果字符串中含有中文，m.find()判断为true
           throw new CustomException("上传文件名称不是英文，请重新上传。");
        }

    }

    /**
     * @param fileId wgs84投影文件zip
     * @return 解析坐标网格
     */
    public Result analysisGrid(String fileUrl,String fileName,String style,String workSpace,boolean isLayer) {

        log.info("解析网格文件ID 【{}】", fileUrl);
        SysFileInfo fileDTO = new SysFileInfo();
        if (StringUtils.isEmpty(fileUrl)){
            return Result.fail("文件地址不能为空");
        }
        fileDTO.setFileUrl(fileUrl);
        fileDTO.setFileName(fileName);
        checkStrChinaese(fileDTO.getFileName());
        log.info("下载ZIP文件地址 {}", fileDTO.getFileUrl());
        Path path = null;
        //下载保存文件地址
        String saveFileAddress = wgs84Root + GeoServer.PATH + fileDTO.getFileName();
        try {
            createDir(wgs84Root);
            GeoServer.downloadNet(fileDTO.getFileUrl(), saveFileAddress);
            File file = new File(saveFileAddress);
            if (!file.exists()) {
                return Result.fail("文件下载失败");
            }
            //判断文件是否符合规则
            wgs84ZipRule(saveFileAddress);
            //判断是否是4326坐标系
            List<Map<String, Object>> list = judge4326(fileDTO, saveFileAddress);

            BboxDTO dto = geoServer.releaseShpByHttp(saveFileAddress, fileDTO.getFileName(),style,workSpace);
//            if ("GeoServer".equals(analysisConfig)){
//                dto = geoServer.releaseShpByHttp(saveFileAddress, fileDTO.getFileName(),style,workSpace);
//            }else {
//                dto = iServer.pushIServer(saveFileAddress, fileDTO.getFileName());
//            }
            if (isLayer){
                dto.setList(list);
            }
            path = Paths.get(saveFileAddress);
            return Result.data(dto);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
           log.error("解析错误",e);
        } finally {
            if (path != null){
               try {
                   //使用完成删除文件
                   Files.delete(path);
               }catch (Exception e){
                   log.error("清除文件报错",e);
               }
            }
        }
        return Result.fail("上传文件不符合规则，请重新上传。");
    }

    /**
     * 判断文件是否是4326
     * @param fileDTO
     * @param saveFileAddress
     */
    private List<Map<String, Object>> judge4326(SysFileInfo fileDTO,String saveFileAddress){
        //解压文件
        String filePath = UUID.randomUUID().toString();
        //解压上传的文件
        String fileBaseName = FilenameUtils.getBaseName(fileDTO.getFileName());
        //解压路径
        String decompressZipPath = wgs84Root + ShapeUtil.PATH + filePath + ShapeUtil.PATH;
        ShapeUtil.decompressZip(saveFileAddress, decompressZipPath);
        log.info("解析地址 {}",decompressZipPath + fileBaseName + ShapeUtil.SHP);
        List<Map<String, Object>> list = ShapeUtil.readShp(decompressZipPath + fileBaseName + ShapeUtil.SHP);
        try {
            FileUtils.deleteDir(decompressZipPath);
        }catch (Exception e){
            log.error("清楚解析shp目录报错",e);
        }

        return list;
    }

    @SuppressWarnings("all")
    public void wgs84ZipRule(String fileAddress) {

        ZipFile zipFile = null;
        try {
            File f = new File(fileAddress);
            zipFile = new ZipFile(f, Charset.forName("GBK"));
            Enumeration<? extends ZipEntry> entries = zipFile.entries(); // Sensitive
            //循环遍历
            int count = 0;
            while(entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                log.info("zip文件 {}",ze.toString());
                if (ze.isDirectory()){
                    throw new CustomException("解压后的文件不能是目录，请检查后上传!");
                }
                if (
                        (ze.toString().endsWith("shp") ||
                                ze.toString().endsWith("shx") ||
                                ze.toString().endsWith("prj") ||
                                ze.toString().endsWith("dbf"))) {
                    count ++;
                }
            }
            if (count != 4){
                throw new CustomException("上传文件未包含 .shp、.shx、.dbf、.prj。请重新上传。");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件解析异常", e);
            throw new CustomException("上传文件不符合规则，请重新上传。");
        }finally {
            if (zipFile != null){
                try {
                    zipFile.close();
                }catch (Exception e){}
            }
        }
    }

    public void createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            log.info("目录" + destDirName + "已存在！");
            return;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            log.info("创建目录" + destDirName + "成功！");
        } else {
            log.info("创建目录" + destDirName + "失败！");
        }
    }
}
