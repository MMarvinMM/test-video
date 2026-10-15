package com.chinatower.bdm.modules.layer.service;

import com.chinatower.bdm.api.entity.GeoGroupVO;
import com.chinatower.bdm.api.entity.GeoVO;
import org.springframework.web.multipart.MultipartFile;

public interface GeoService {

    String workSpace();

    String workSpace(String industryCode);

    /**
     * 上传geoserver 样式文件
     */
    void uploadGeoStyle(MultipartFile file);

    /**
     * 上传样式
     * @param sldBody
     */
    void uploadGeoStyle(String styleName,String sldBody,String workSpace);

    /**
     * 删除样式
     */
    void deleteGeoStyle(String styleName);

    /**
     * 删除样式
     */
    void deleteGeoStyle(String styleName,String workSpace);
    /**
     * 修改图层样式
     */
    void modifyLayerStyle(GeoVO vo);

    /**
     * 上传 shp
     */
    void publishShp(MultipartFile file,String styleName);

    /**
     * 上传tif文件
     */
    void publishTif(MultipartFile file,String styleName);

    /**
     * 发布postgis 图层
     */
    void publishPostGis(GeoVO vo);

    /**
     *发布图层组
     */
    void publishLayerGroup(GeoGroupVO vo);

    /**
     * 修改图层组
     */
    void modifyLayerGroup(GeoGroupVO vo);


}
