package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.io.FileUtil;
import com.chinatower.bdm.api.entity.GeoGroupVO;
import com.chinatower.bdm.api.entity.GeoLayerGroupVO;
import com.chinatower.bdm.api.entity.GeoVO;
import com.chinatower.bdm.modules.grid.config.*;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.bdm.modules.layer.service.GeoService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerGroupEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {

    private final GeoServerProperties geoServerProperties;
    private final WgsAnalysisService wgsAnalysisService;
    private final GeoServer geoServer;
    private final SystemFeignClient systemFeignClient;
    private static final String INDUSTRY_CODE_WORK_SPACE = "INDUSTRY_CODE_WORK_SPACE";
    private static final String INDUSTRY_CODE_WORK_SPACE_ERROR_MSG = "未配置行业的命名空间";
    private static final String CUSTOM_EXCEPTION_MSG = "自定义错误";
    private static final String PUBLISH_LAYER_ERROR_MSG = "图层发布失败";
    private static final String MODIFY_LAYER_STYLE_ERROR_MSG = "修改图层样式失败";
    private static final String UPLOAD_STYLE_ERROR = "上传样式失败";

    private void existsStore(MyGeoServerRESTManager manager, String workSpace, String storeName) {
        boolean existsStore = manager.getReader().existsDatastore(workSpace, storeName);
        if (!existsStore) {
            throw new CustomException("存储仓库不存在");
        }
    }

    private void existsStyle(MyGeoServerRESTManager manager, String workSpace, String styleName) {
        boolean existsStyle = manager.getReader().existsStyle(workSpace, styleName);
        if (existsStyle) {
            throw new CustomException("样式名称已存在");
        }
    }

    private void existsNotStyle(MyGeoServerRESTManager manager, String workSpace, String styleName) {
        boolean existsStyle = false;
        if (StringUtils.isEmpty(workSpace)){
            existsStyle = manager.getReader().existsStyle(styleName);
        }else {
            existsStyle = manager.getReader().existsStyle(workSpace, styleName);
        }
        if (!existsStyle) {
            throw new CustomException("样式不存在");
        }
    }

    private void existsLayer(MyGeoServerRESTManager manager, String workSpace, String layerName) {
        boolean existsLayer = manager.getReader().existsLayer(workSpace, layerName);
        if (existsLayer) {
            throw new CustomException("图层名称已存在");
        }
    }

    private void existsNotLayer(MyGeoServerRESTManager manager, String workSpace, String layerName) {
        boolean existsLayer = manager.getReader().existsLayer(workSpace, layerName);
        if (!existsLayer) {
            throw new CustomException("图层不存在");
        }
    }

    /**
     * 获得行业命名空间
     */
    public String workSpace() {
        LoginUser user = SecurityUtils.getLoginUser();
        String industryCode = user.getUser().getIndustryCode();
        return workSpace(industryCode);
    }

    public String workSpace(String industryCode) {
        Result<List<SysDictDataResponse>> result = systemFeignClient.dictDataType(INDUSTRY_CODE_WORK_SPACE);
        if (result.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new CustomException(INDUSTRY_CODE_WORK_SPACE_ERROR_MSG);
        }
        List<SysDictDataResponse> dict = result.getData().stream().filter(d -> d.getDictLabel() != null && d.getDictLabel().equals(industryCode)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dict)) {
            throw new CustomException(INDUSTRY_CODE_WORK_SPACE_ERROR_MSG);
        }
        String industryCodeWorkSpace = dict.get(0).getDictValue();
        try {
            geoServer.judgeWorkSpace(industryCodeWorkSpace);
            return industryCodeWorkSpace;
        } catch (Exception e) {
            log.error("查询命名空间错误", e);
        }
        throw new CustomException(INDUSTRY_CODE_WORK_SPACE_ERROR_MSG);
    }

    @Override
    public void uploadGeoStyle(MultipartFile file) {
        if (file == null) {
            throw new CustomException("请上传样式文件");
        }
        if (file.getSize() > geoServerProperties.getFileSize() * 1024 * 1024){
            throw new CustomException("上传文件不能大于" + geoServerProperties.getFileSize() + "M");
        }
        //下载保存文件地址
        String sldDirPath = geoServerProperties.getGeoTemDir() + GeoServer.PATH + file.getOriginalFilename();
        String extension = FileUtil.extName(file.getOriginalFilename());
        if (!"sld".equals(extension)) {
            throw new CustomException("文件格式错误，请上传sld文件");
        }
        File dest = new File(sldDirPath);
        try {
            file.transferTo(dest);
            String workSpace = workSpace();

            String styleName = FileUtil.getPrefix(file.getOriginalFilename());

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTStyleManager styleManager = manager.getStyleManager();
            deleteGeoStyle(styleName);

            styleManager.publishStyle(dest, styleName, workSpace, true);
        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(UPLOAD_STYLE_ERROR, e);
            throw new CustomException(UPLOAD_STYLE_ERROR);
        }finally {
            if (dest.exists()){
                dest.delete();
            }
        }
    }

    @Override
    public void uploadGeoStyle(String styleName, String sldBody,String workSpace) {
        try {
            log.info("发布样式{}",styleName);
            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTStyleManager styleManager = manager.getStyleManager();
            //样式是否存在
            deleteGeoStyle(styleName,workSpace);
            if (!styleManager.checkSLD10Version(sldBody)){
                styleManager.publishStyleInWorkspace110(workSpace, sldBody, styleName);
            }else {
                styleManager.publishStyleInWorkspace(workSpace, sldBody, styleName);
            }
        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(UPLOAD_STYLE_ERROR, e);
            throw new CustomException(UPLOAD_STYLE_ERROR);
        }
    }

    @Override
    public void deleteGeoStyle(String styleName) {
        deleteGeoStyle(styleName,workSpace());
    }

    @Override
    public void deleteGeoStyle(String styleName, String workSpace) {
        try {
            log.info("删除样式 {} {}",workSpace,styleName);
            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTStyleManager styleManager = manager.getStyleManager();
            styleManager.removeStyle(styleName, workSpace);
        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error("样式删除失败", e);
            throw new CustomException("样式删除失败");
        }
    }

    @Override
    public void modifyLayerStyle(GeoVO vo) {
        String workSpace = workSpace();
        try {
            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();

            existsNotLayer(manager, workSpace, vo.getLayerName());

            //样式命名空间
            String styleWorkSpace = null;
            if (!Boolean.TRUE.equals(vo.getWorkspaceFilterFlag())){
                styleWorkSpace = workSpace;
            }

            existsNotStyle(manager, styleWorkSpace, vo.getStyleName());

            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setDefaultStyle(vo.getStyleName());
            boolean b = publisher.configureLayer(workSpace, vo.getLayerName(), layerEncoder);
            if (!b) {
                throw new CustomException(MODIFY_LAYER_STYLE_ERROR_MSG);
            }
        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(MODIFY_LAYER_STYLE_ERROR_MSG, e);
            throw new CustomException(MODIFY_LAYER_STYLE_ERROR_MSG);
        }
    }

    @Override
    public void publishShp(MultipartFile file, String styleName) {
        if (file == null) {
            throw new CustomException("请上传shp文件");
        }
        if (file.getSize() > geoServerProperties.getFileSize() * 1024 * 1024){
            throw new CustomException("上传文件不能大于" + geoServerProperties.getFileSize() + "M");
        }
        String workSpace = workSpace();
        //下载保存文件地址
        String sldDirPath = geoServerProperties.getGeoTemDir() + GeoServer.PATH + file.getOriginalFilename();
        log.info("上传shp {}", sldDirPath);
        String extension = FileUtil.extName(file.getOriginalFilename());
        if (!"zip".equals(extension)) {
            throw new CustomException("文件格式错误，请上传zip文件");
        }
        File zipFile = new File(sldDirPath);
        try {
            file.transferTo(zipFile);

            String layerName = FileUtil.getPrefix(file.getOriginalFilename());
            wgsAnalysisService.checkStrChinaese(layerName);
            //文件校验
            wgsAnalysisService.wgs84ZipRule(sldDirPath);

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();
            existsLayer(manager, workSpace, layerName);
            if (StringUtils.isNotEmpty(styleName)){
                existsNotStyle(manager, workSpace, styleName);
            }

            if (!publisher.publishShp(workSpace, layerName, layerName, zipFile, styleName)) {
                throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
            }
        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(PUBLISH_LAYER_ERROR_MSG, e);
            throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
        }finally {
            if (zipFile.exists()){
                zipFile.delete();
            }
        }
    }

    @Override
    public void publishTif(MultipartFile file, String styleName) {
        String workSpace = workSpace();
        //下载保存文件地址
        String sldDirPath = geoServerProperties.getGeoTemDir() + GeoServer.PATH + file.getOriginalFilename();
        log.info("上传tif {}", sldDirPath);
        String extension = FileUtil.extName(file.getOriginalFilename());
        if (!"tif".equals(extension)) {
            throw new CustomException("文件格式错误，请上传tif文件");
        }
        File zipFile = new File(sldDirPath);
        try {
            file.transferTo(zipFile);

            String layerName = FileUtil.getPrefix(file.getOriginalFilename());
            wgsAnalysisService.checkStrChinaese(layerName);

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();
            existsLayer(manager, workSpace, layerName);

            if (StringUtils.isNotEmpty(styleName)) {
                existsStyle(manager, workSpace, styleName);
                if (!publisher.publishGeoTIFF(workSpace, layerName, layerName, zipFile, GeoServer.GEO_CODE, GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED, styleName, null)) {
                    throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
                }
            } else if (!publisher.publishGeoTIFF(workSpace, layerName, layerName, zipFile)) {
                throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
            }

        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(PUBLISH_LAYER_ERROR_MSG, e);
            throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
        }finally {
            if (zipFile.exists()){
                zipFile.delete();
            }
        }
    }

    @Override
    public void publishPostGis(GeoVO vo) {
        String workSpace = workSpace();
        try {

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();
            existsLayer(manager, workSpace, vo.getLayerName());

            existsNotStyle(manager, workSpace, vo.getStyleName());

            //存储仓库是否存在
            existsStore(manager, workSpace, vo.getStoreName());

            if (!publisher.publishDBLayer(workSpace, vo.getStoreName(), vo.getLayerName(), GeoServer.GEO_CODE, vo.getStyleName())) {
                throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
            }

        } catch (CustomException e) {
            log.error(CUSTOM_EXCEPTION_MSG, e);
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            log.error(PUBLISH_LAYER_ERROR_MSG, e);
            throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
        }
    }

    @Override
    public void publishLayerGroup(GeoGroupVO vo) {
        String workSpace = workSpace();
        try {
            if (StringUtils.isEmpty(vo.getLayerName())) {
                throw new CustomException("图层组名称不能为空");
            }
            if (CollectionUtils.isEmpty(vo.getLayers())) {
                throw new CustomException("图层组图层不能为空");
            }

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();
            existsLayer(manager, workSpace, vo.getLayerName());
            vo.getLayers().forEach(layer -> {
                existsLayer(manager, workSpace, layer.getLayerName());
                existsNotStyle(manager, workSpace, layer.getDefaultStyle());
            });

            GSLayerGroupEncoder encoder = new GSLayerGroupEncoder();
            for (GeoLayerGroupVO groupVO : vo.getLayers()){
                encoder.addLayer(groupVO.getLayerName(),groupVO.getDefaultStyle());
            }
            publisher.createLayerGroup(workSpace,vo.getLayerName(),encoder);
        } catch (Exception e) {
            log.error(PUBLISH_LAYER_ERROR_MSG, e);
            throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
        }
    }

    @Override
    public void modifyLayerGroup(GeoGroupVO vo) {
        String workSpace = workSpace();
        try {
            if (StringUtils.isEmpty(vo.getLayerName())) {
                throw new CustomException("图层组名称不能为空");
            }
            if (CollectionUtils.isEmpty(vo.getLayers())) {
                throw new CustomException("图层组图层不能为空");
            }

            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            MyGeoServerRESTPublisher publisher = manager.getPublisher();
            existsNotLayer(manager, workSpace, vo.getLayerName());
            vo.getLayers().forEach(layer -> {
                existsLayer(manager, workSpace, layer.getLayerName());
                existsNotStyle(manager, workSpace, layer.getDefaultStyle());
            });

            GSLayerGroupEncoder encoder = new GSLayerGroupEncoder();
            for (GeoLayerGroupVO groupVO : vo.getLayers()){
                encoder.addLayer(groupVO.getLayerName(),groupVO.getDefaultStyle());
            }
            publisher.configureLayerGroup(workSpace,vo.getLayerName(),encoder);
        } catch (Exception e) {
            log.error(PUBLISH_LAYER_ERROR_MSG, e);
            throw new CustomException(PUBLISH_LAYER_ERROR_MSG);
        }
    }
}
