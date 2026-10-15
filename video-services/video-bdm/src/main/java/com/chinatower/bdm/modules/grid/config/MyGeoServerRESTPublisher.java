package com.chinatower.bdm.modules.grid.config;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.HTTPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
@SuppressWarnings("all")
public class MyGeoServerRESTPublisher extends GeoServerRESTPublisher {
    private final String restURL;
    private final String gsuser;
    private final String gspass;

    public MyGeoServerRESTPublisher(String restURL, String username, String password) {
        super(restURL, username, password);

        this.restURL = HTTPUtils.decurtSlash(restURL);
        this.gsuser = username;
        this.gspass = password;
    }

    /**
     *
     * @param workspace 命名空间
     * @param layerName 图层名称/文件名称/存储仓库名称
     * @param zipFile shp zip 文件
     * @param defaultStyle 默认样式
     * @param nameValuePairs 存储仓库属性设置
     */
    public boolean publishShp(String workspace, String layerName, File zipFile, String defaultStyle, NameValuePair[] nameValuePairs) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, layerName, nameValuePairs, layerName, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), GeoServer.GEO_CODE, defaultStyle);
    }

    /**
     *
     * @param workspace 命名空间
     * @param layerName 图层名称/文件名称/存储仓库名称
     * @param zipFile shp zip 文件
     * @param defaultStyle 默认样式
     */
    public boolean publishShp(String workspace, String layerName, File zipFile, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
        return this.publishShp(workspace, layerName, (NameValuePair[])null, layerName, GeoServerRESTPublisher.UploadMethod.FILE, zipFile.toURI(), GeoServer.GEO_CODE, defaultStyle);
    }

    /**
     * 删除图层
     * @param workspace 工作空间
     * @param layerName 图层名称
     * @param resource 是否关联删除其他数据
     */
    public boolean removeLayer(String workspace, String layerName,boolean resource) {
        String fqLayerName;
        if (workspace == null) {
            fqLayerName = layerName;
        } else {
            fqLayerName = workspace + ":" + layerName;
        }

        if (layerName == null) {
            return false;
        } else {
            String url = this.restURL + "/rest/layers/" + fqLayerName + "?recurse" + resource;
            boolean result = HTTPUtils.delete(url, this.gsuser, this.gspass);
            if (result) {
                log.info("Layer successfully removed: " + fqLayerName);
            } else  {
                log.warn("Error removing layer " + fqLayerName);
            }

            return result;
        }
    }
}
