package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.common.ShapeUtil;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.config.GeoServerProperties;
import com.chinatower.bdm.modules.grid.config.MyGeoServerRESTManager;
import com.chinatower.bdm.modules.grid.config.MyGeoServerRESTStyleManager;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.bdm.modules.layer.service.GeoserverService;
import com.chinatower.common.core.text.UUID;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.file.FileUtils;
import com.chinatower.platform.model.dto.GeoserverWfsDTO;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoserverServiceImpl implements GeoserverService {

    private final GeoServerProperties geoServerProperties;
    private final WgsAnalysisService wgsAnalysisService;
    private final GeoServer geoServer;

    private static final String ROUTE_P = "/";
    private static final String CQL_FILTER = "CQL_FILTER";
    private static final String[] suffix = new String[]{"sld", "zip"};
    private static final String[] tiff_suffix = new String[]{"tiff", "tif"};

    private static final String UPLOAD_STYLE_ERROR_MSG = "上传样式文件错误";
    private static final String tableName = "tableName";

    @Override
    public Result<Object> wfs(GeoserverWfsDTO dto) {
        log.info("转发wfs参数 {}",JSON.toJSONString(dto));
        if (StringUtils.isEmpty(dto.getWorkspace())) {
            return Result.fail("命名空间不能为空");
        }
        if (dto.getParams() == null) {
            return Result.fail("参数错误");
        }
        String url = geoServerProperties.getUrl();
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        int maxFeatures = 0;
        if (dto.getPageNum() != null && dto.getPageSize() != null){
            startIndex = (dto.getPageNum() - 1) * dto.getPageSize();
            maxFeatures = dto.getPageSize();
        }

        Map<String, Object> params = dto.getParams();
        if (startIndex != 0){
            params.put("startIndex",startIndex);
            params.put("maxFeatures",maxFeatures);
        }
        Object service = params.get("service");
        if (Objects.isNull(service)) {
            service = params.getOrDefault("SERVICE", "WFS");
        }
        sb.append(url).append(ROUTE_P).append(dto.getWorkspace()).append(ROUTE_P).append("ows?").append("service=").append(service);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!"service".equalsIgnoreCase(entry.getKey())) {
                if (CQL_FILTER.equalsIgnoreCase(entry.getKey())) {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue().toString().replace("also", "and").replace("either", "or"));
                } else {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }

            }
        }
        String reqUrl = sb.toString();
        log.info("geoserver wfs {}", reqUrl);
        String body = HttpRequest.get(reqUrl)
                .header("X-Credentials", "private-user=" + geoServerProperties.getGeoUsername())
                .header("Y-Credentials", "private-pw=" + geoServerProperties.getGeoPassword())
                .execute()
                .body();
        try {
            return Result.data(JSON.parseObject(body));
        } catch (Exception e) {
            log.error("转发 WFS 错误", e);
        }
        Result<Object> result = Result.fail("转发geoserver错误");
        result.setData(body);
        return result;
    }


    @Override
    public Result<Object> uploadStyle(MultipartFile file, String workSpaceName) {
        if (file == null) {
            return Result.fail("请上传文件");
        }
        try {
            String filePath = UUID.fastUUID().toString();
            String wgs84Root = wgsAnalysisService.getWgs84Root();
            wgsAnalysisService.createDir(wgs84Root);
            //下载保存文件地址
            String saveFileAddress = wgs84Root + GeoServer.PATH + file.getOriginalFilename();
            //文件夹名称
            String decompressZipPath = wgs84Root + GeoServer.PATH + filePath + ShapeUtil.PATH;

            String extension = FileUtil.extName(file.getOriginalFilename());
            if (!equalsAnyIgnoreCase(extension, suffix)) {
                return Result.fail("文件格式不正确，请上传" + Arrays.toString(suffix) + "格式");
            }
            File dest = new File(saveFileAddress);
            file.transferTo(dest);
            //需要上传的样式文件
            Map<String, File> map = new HashMap<>();
            //上传单个文件
            if (suffix[0].equals(extension)) {
                map.put(FileUtil.getPrefix(file.getOriginalFilename()), dest);
            } else {//上传多个文件 先解压
                log.info("上传文件地址 {}", saveFileAddress);
                log.info("解压路径 {}", decompressZipPath);
                ShapeUtil.decompressZip(saveFileAddress, decompressZipPath);
                List<File> files = getAllFiles(decompressZipPath);
                if (files.isEmpty()) {
                    throw new CustomException("解压文件错误");
                }
                files.forEach(f -> map.put(FileUtil.getPrefix(f), f));

            }

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTStyleManager styleManager = manager.getStyleManager();
            //先删除
            for (Map.Entry<String, File> entry : map.entrySet()) {
                styleManager.removeStyle(entry.getKey());
            }
            //在上传
            for (Map.Entry<String, File> entry : map.entrySet()) {
                styleManager.publishStyle(entry.getValue(), entry.getKey(), workSpaceName, true);
            }
            boolean delete = dest.delete();
            if (delete){
                log.info("文件删除成功{}",saveFileAddress);
            }
            if (suffix[1].equals(extension)) {
                FileUtils.deleteDir(decompressZipPath);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(UPLOAD_STYLE_ERROR_MSG, e);
            throw new CustomException(UPLOAD_STYLE_ERROR_MSG);
        }
        return Result.success("上传成功");
    }

    @Override
    public Result<Object> uploadLayerTiff(MultipartFile file,String workSpace) {
        if (file == null) {
            return Result.fail("请上传文件");
        }
        try {
            String wgs84Root = wgsAnalysisService.getWgs84Root();
            wgsAnalysisService.createDir(wgs84Root);
            //下载保存文件地址
            String saveFileAddress = wgs84Root + GeoServer.PATH + file.getOriginalFilename();

            String extension = FileUtil.extName(file.getOriginalFilename());
            String fileName = FileUtil.getPrefix(file.getOriginalFilename());
            if (!equalsAnyIgnoreCase(extension, tiff_suffix)) {
                return Result.fail("文件格式不正确，请上传" + Arrays.toString(tiff_suffix) + "格式");
            }
            File dest = new File(saveFileAddress);
            file.transferTo(dest);
            geoServer.uploadLayerTiff(workSpace, fileName, dest);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error(UPLOAD_STYLE_ERROR_MSG, e);
            throw new CustomException(UPLOAD_STYLE_ERROR_MSG);
        }
        return Result.success("上传成功");
    }

    @Override
    public Result<Object> releaseLayers(List<Map<String, String>> maps) {
        for (Map<String, String> map : maps){
            try {
                if (StringUtils.isEmpty(map.get(tableName)) || StringUtils.isEmpty(map.get("geoType"))){
                    continue;
                }
                geoServer.deleteLayer(geoServerProperties.getGuotuWorkspace(),map.get(tableName));
                geoServer.publisherPostGis(geoServerProperties.getGuotuWorkspace(), map.get(tableName), map.get("geoType"));
            }catch (Exception e){
                log.error("发布失败 {}",map.get(tableName),e);
            }
        }
        return Result.success("发布完成");
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> layerSld(String styleName, String workSpace, HttpServletResponse response) {
        try {
            URL u = new URL(geoServerProperties.getUrl());
            GeoServerRESTManager manager = new GeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            GeoServerRESTStyleManager styleManager = manager.getStyleManager();
            String sld = "初始化";
            if (StringUtils.isEmpty(workSpace)){
                sld = styleManager.getSLD(styleName);
            }else {
                sld = styleManager.getSLD(workSpace,styleName);
            }
            File file = new File(styleName + ".sld");
            FileWriter writer = new FileWriter(file);
            writer.write(sld);
            writer.close();

            // 确定文件的MediaType
            MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
            // 创建FileSystemResource
            org.springframework.core.io.Resource resource = new FileSystemResource(file);
            // 设置HttpHeaders
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            // 返回ResponseEntity
            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(mediaType)
                    .body(resource);
        }catch (Exception e){
            log.error("查询样式文件错误");
            throw new CustomException(e.getMessage());
        }
    }

    public Result shpTest(MultipartFile file) {
        File dest = null;
        //解压文件
        String filePath = java.util.UUID.randomUUID().toString();
        String decompressZipPath = wgsAnalysisService.getWgs84Root() + ShapeUtil.PATH + filePath + ShapeUtil.PATH;
        try {
            String saveFileAddress = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + file.getOriginalFilename();
            log.info("保存文件地址 {}", saveFileAddress);
            dest = new File(saveFileAddress);
            file.transferTo(dest);

            log.info("解压路径 {}", decompressZipPath);

            ShapeUtil.decompressZip(saveFileAddress, decompressZipPath);
            String fileName = FileUtil.getPrefix(file.getOriginalFilename());
            log.info("解析地址 {}", decompressZipPath + fileName + ShapeUtil.SHP);

            File shpFile = new File(decompressZipPath + fileName + ShapeUtil.SHP);
            Map<String, Object> params = new HashMap<>();
            params.put("url", shpFile.toURI().toURL());
            params.put("memory mapped buffer", true);
            ShapefileDataStore shapefileDataStore = (ShapefileDataStore) DataStoreFinder.getDataStore(params);
            Charset charset = Charset.forName("UTF-8");
            shapefileDataStore.setCharset(charset);
            FeatureSource featuresource = shapefileDataStore.getFeatureSource(shapefileDataStore.getTypeNames()[0]); //默认是文件名称
            SimpleFeatureCollection simpleFeatureCollection = (SimpleFeatureCollection) featuresource.getFeatures(); //读取要素
            SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features();

            int count = 0;
            long now = System.currentTimeMillis();
            while (simpleFeatureIterator.hasNext()) {
                count++;
                SimpleFeature f = simpleFeatureIterator.next();
            }
            long end = System.currentTimeMillis();
            log.info("shp文件解析时间 {} 解析数量 {}", (end - now), count);

            simpleFeatureIterator.close();
            shapefileDataStore.dispose();
            return Result.data(String.format("shp文件解析时间 %s 毫秒 解析数量 %s", (end - now), count));
        } catch (IOException e) {
            log.error("解析错误");
        } finally {
            try {
                if (dest != null && dest.exists()) {
                    boolean delete = dest.delete();
                    if (delete){
                        log.info("路径{}",decompressZipPath);
                    }
                }
                FileUtils.deleteDir(decompressZipPath);
            } catch (Exception ee) {
                log.error("删除解析shp目录报错", ee);
            }
        }
        return Result.data("解析完成");
    }


    public static List<File> getAllFiles(String folderPath) {
        File folderFile = new File(folderPath);
        List<File> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(folderFile.toPath())) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> files.add(path.toFile()));
        } catch (Exception e) {
            log.error("获得文件夹内文件错误", e);
            throw new CustomException("获得文件夹内文件错误");
        }
        return files;
    }

    /**
     * 字符串匹配
     */
    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... arr) {
        if (ArrayUtils.isNotEmpty(arr)) {
            for (CharSequence next : arr) {
                if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 文件转化
     */
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            System.out.println("File is empty or its size is zero, please check!");
        } else {
            InputStream inputStream = file.getInputStream();
            convFile = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            inputStream.close();
        }
        return convFile;
    }
}
