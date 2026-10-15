package com.chinatower.bdm.modules.grid.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.enums.PgLayerStyleEnums;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.*;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.dimensions.GSCoverageDimensionEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@SuppressWarnings("all")
@Slf4j
public class GeoServer {

    private static Logger logger = LoggerFactory.getLogger(GeoServer.class);

    public static final String GEO_CODE = "EPSG:4326";
    public static final String CHINATOWER_STYLE = "chinatower_style";
    public static final String LAYER_STYLE = "layer_style";
    public static final String PATH = "\\";
    /**
     * geoServer配置
     */
    @Resource
    private GeoServerProperties geoServerProperties;
    @Resource
    private PgSqlProperties pgSqlProperties;


    /**
     * 判断工作区（workspace）是否存在，不存在则创建
     */
    public void judgeWorkSpace(String workspace) throws MalformedURLException {

        URL u = new URL(geoServerProperties.getUrl());
        GeoServerRESTManager manager = new GeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
        GeoServerRESTPublisher publisher = manager.getPublisher();
        List<String> workspaces = manager.getReader().getWorkspaceNames();
        if (!workspaces.contains(workspace)) {
            boolean createWorkspace = publisher.createWorkspace(workspace);
            logger.info("create workspace : " + createWorkspace);
        } else {
            logger.info("workspace已经存在了,workspace :" + workspace);
        }
    }

    public static String getStyleBody(String styleName) {
        if (CHINATOWER_STYLE.equals(styleName)) {
            return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<StyledLayerDescriptor version=\"1.0.0\"\n" +
                    "xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\"\n" +
                    "xmlns=\"http://www.opengis.net/sld\"\n" +
                    "xmlns:ogc=\"http://www.opengis.net/ogc\"\n" +
                    "xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "<!-- a Named Layer is the basic building block of an SLD document -->\n" +
                    "<NamedLayer>\n" +
                    "<Name>default_polygon</Name>\n" +
                    "<UserStyle>\n" +
                    "<!-- Styles can have names, titles and abstracts -->\n" +
                    "<Title>Default Polygon</Title>\n" +
                    "<Abstract>A sample style that draws a polygon</Abstract>\n" +
                    "<!-- FeatureTypeStyles describe how to render different features -->\n" +
                    "<!-- A FeatureTypeStyle for rendering polygons -->\n" +
                    "<FeatureTypeStyle>\n" +
                    "<Rule>\n" +
                    "<Name>rule1</Name>\n" +
                    "<Title>Gray Polygon with Black Outline</Title>\n" +
                    "<Abstract>A polygon with a gray fill and a 1 pixel black outline</Abstract>\n" +
                    "<PolygonSymbolizer>\n" +
                    "<Fill>\n" +
                    "\t<CssParameter name=\"fill\">#606266</CssParameter>\n" +
                    "   <CssParameter name=\"fill-opacity\">0.2</CssParameter>\n" +
                    "</Fill>\n" +
                    "<Stroke>\n" +
                    "\t<CssParameter name=\"stroke\">#606266</CssParameter>\n" +
                    "\t<CssParameter name=\"stroke-width\">1</CssParameter>\n" +
                    "   <CssParameter name=\"stroke-opacity\">0.3</CssParameter>\n" +
                    "</Stroke>\n" +
                    "</PolygonSymbolizer>\n" +
                    "</Rule>\n" +
                    "</FeatureTypeStyle>\n" +
                    "</UserStyle>\n" +
                    "</NamedLayer>\n" +
                    "</StyledLayerDescriptor>\n";
        } else if (LAYER_STYLE.equals(styleName)) {
            return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<StyledLayerDescriptor version=\"1.0.0\"\n" +
                    "xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\"\n" +
                    "xmlns=\"http://www.opengis.net/sld\"\n" +
                    "xmlns:ogc=\"http://www.opengis.net/ogc\"\n" +
                    "xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "<!-- a Named Layer is the basic building block of an SLD document -->\n" +
                    "<NamedLayer>\n" +
                    "<Name>default_polygon</Name>\n" +
                    "<UserStyle>\n" +
                    "<!-- Styles can have names, titles and abstracts -->\n" +
                    "<Title>Default Polygon</Title>\n" +
                    "<Abstract>A sample style that draws a polygon</Abstract>\n" +
                    "<!-- FeatureTypeStyles describe how to render different features -->\n" +
                    "<!-- A FeatureTypeStyle for rendering polygons -->\n" +
                    "<FeatureTypeStyle>\n" +
                    "<Rule>\n" +
                    "<Name>rule1</Name>\n" +
                    "<Title>Gray Polygon with Black Outline</Title>\n" +
                    "<Abstract>A polygon with a gray fill and a 1 pixel black outline</Abstract>\n" +
                    "<PolygonSymbolizer>\n" +
                    "<Fill>\n" +
                    "\t<CssParameter name=\"fill\">#E1CF20</CssParameter>\n" +
                    "   <CssParameter name=\"fill-opacity\">0.8</CssParameter>\n" +
                    "</Fill>\n" +
                    "<Stroke>\n" +
                    "\t<CssParameter name=\"stroke\">#606266</CssParameter>\n" +
                    "\t<CssParameter name=\"stroke-width\">1</CssParameter>\n" +
                    "   <CssParameter name=\"stroke-opacity\">0.3</CssParameter>\n" +
                    "</Stroke>\n" +
                    "</PolygonSymbolizer>\n" +
                    "</Rule>\n" +
                    "</FeatureTypeStyle>\n" +
                    "</UserStyle>\n" +
                    "</NamedLayer>\n" +
                    "</StyledLayerDescriptor>\n";
        } else if (PgLayerStyleEnums.POINT.getStyleName().equals(styleName)) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" version=\"1.1.0\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:se=\"http://www.opengis.net/se\">\n" +
                    "  <NamedLayer>\n" +
                    "    <se:Name>default_point</se:Name>\n" +
                    "    <UserStyle>\n" +
                    "      <se:Name>Default point</se:Name>\n" +
                    "      <se:FeatureTypeStyle>\n" +
                    "        <se:Rule>\n" +
                    "          <se:Name>Single symbol</se:Name>\n" +
                    "          <se:PointSymbolizer>\n" +
                    "            <se:Graphic>\n" +
                    "              <se:Mark>\n" +
                    "                <se:WellKnownName>circle</se:WellKnownName>\n" +
                    "                <se:Fill>\n" +
                    "                  <se:SvgParameter name=\"fill\">#E1CF20</se:SvgParameter>\n" +
                    "                </se:Fill>\n" +
                    "                <se:Stroke>\n" +
                    "                  <se:SvgParameter name=\"stroke\">#606266</se:SvgParameter>\n" +
                    "                  <se:SvgParameter name=\"stroke-width\">0.5</se:SvgParameter>\n" +
                    "                </se:Stroke>\n" +
                    "              </se:Mark>\n" +
                    "              <se:Size>7</se:Size>\n" +
                    "            </se:Graphic>\n" +
                    "          </se:PointSymbolizer>\n" +
                    "        </se:Rule>\n" +
                    "      </se:FeatureTypeStyle>\n" +
                    "    </UserStyle>\n" +
                    "  </NamedLayer>\n" +
                    "</StyledLayerDescriptor>\n";
        } else if (PgLayerStyleEnums.MULTILINESTRING.getStyleName().equals(styleName)) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:se=\"http://www.opengis.net/se\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "  <NamedLayer>\n" +
                    "    <se:Name>default_line</se:Name>\n" +
                    "    <UserStyle>\n" +
                    "      <se:Name>default_line</se:Name>\n" +
                    "      <se:FeatureTypeStyle>\n" +
                    "        <se:Rule>\n" +
                    "          <se:Name>Single symbol</se:Name>\n" +
                    "          <se:LineSymbolizer>\n" +
                    "            <se:Stroke>\n" +
                    "              <se:SvgParameter name=\"stroke\">#E1CF20</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-opacity\">0.8</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-width\">1</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-linejoin\">bevel</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-linecap\">square</se:SvgParameter>\n" +
                    "            </se:Stroke>\n" +
                    "          </se:LineSymbolizer>\n" +
                    "        </se:Rule>\n" +
                    "      </se:FeatureTypeStyle>\n" +
                    "    </UserStyle>\n" +
                    "  </NamedLayer>\n" +
                    "</StyledLayerDescriptor>\n";
        }
        return null;
    }

    public void publishStyle(String styleName) throws Exception {
        URL u = new URL(geoServerProperties.getUrl());
        MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
        MyGeoServerRESTStyleManager styleManager = manager.getStyleManager();
        boolean existsStyle = manager.getStyleManager().existsStyle(styleName);
        if (!existsStyle) {
            String styleBody = getStyleBody(styleName);
            if (StringUtils.isEmpty(styleBody)) {
                throw new CustomException("样式文件名称错误");
            }
            boolean b = false;
            if (!styleManager.checkSLD10Version(styleBody)){
                styleManager.publishStyleInWorkspace110(null, styleBody, styleName);
            }else {
                styleManager.publishStyleInWorkspace(null, styleBody, styleName);
            }
            logger.info("create styleName : " + styleName + " 创建 " + b);
        } else {
            logger.info(styleName + " styleName 已存在");
        }
    }

    /**
     * 文件下载
     *
     * @param downUrl  下载地址
     * @param savePath 保存地址
     */
    public static void downloadNet(String downUrl, String savePath) {
        try (InputStream inStream = new URL(downUrl).openStream()) {
            byte[] buffer = new byte[8192];
            try (FileOutputStream fs = new FileOutputStream(savePath)) {
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, bytesRead);
                }
                fs.flush();
            }
        } catch (Exception e) {
            log.error("下载网络文件报错, url={}, path={}", downUrl, savePath, e);
            throw new com.chinatower.platform.common.exception.CustomException("文件下载错误");
        }
    }

    /**
     * 方法一
     * 直接发布shp文件到geoServer
     * 将shp.zip通过http传递过去
     * 不主动设置样式和编码
     * <p>
     *
     * @param fileUrl 本地文件地址 zip格式
     * @return boolean
     */
    public Wgs84DTO releaseShpByHttp(String fileUrl, String fileName, String style, String workspace) {
        try {
            if (StringUtils.isEmpty(workspace)) {
                workspace = geoServerProperties.getShpWorkspace();
            }
            //判断创建工作空间
            judgeWorkSpace(workspace);
            //创建样式文件
            publishStyle(style);
            logger.info("文件路径地址 {}", fileUrl);
            File zipFile = new File(fileUrl);
            //存储名/图层名
            String[] fileNameArr = fileName.split("\\.");
            String storeName = fileNameArr[0];

            Wgs84DTO dto = new Wgs84DTO();
            dto.setUrl(geoServerProperties.getShowUrl() + "/" + workspace + "/wms");
            dto.setLayer_name(workspace + ":" + storeName);
            dto.setWorkspace(workspace);

            logger.info("图层名称 {} url =  {}", storeName, geoServerProperties.getUrl());

            GeoServerRESTReader reader = new GeoServerRESTReader(geoServerProperties.getUrl(), geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            RESTLayer layer = reader.getLayer(workspace, storeName);
            if (layer == null) {
                GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoServerProperties.getUrl(), geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
                if (publisher.publishShp(workspace, storeName, storeName, zipFile, GeoServer.GEO_CODE, style)) {
                    layer = reader.getLayer(workspace, storeName);
                    return getWgs84DTO(dto, reader, layer);
                } else {
                    throw new CustomException("图层发布失败:" + storeName);
                }
            } else {
                return getWgs84DTO(dto, reader, layer);
            }
        } catch (Exception e) {
            logger.error("解析格式异常", e);
            throw new CustomException("上传文件不符合规则，请重新上传。");
        }
    }
//
//    public static void main(String[] args)throws Exception {
////        String url = "http://10.43.82.110:9239/geoserver";
//        String url = "http://10.38.26.137:9239/geoserver";
//        String username = "admin";
////        String password = "geoserver";
//        String password = "d7E0xek8seFiwx";
//        File zipFile = new File("D:\\chinatower\\pg\\geoserver\\china_vector.zip");
//        String workspace = "chinatower";
//        String storeName = "china_vector";
//        String tableName = "china_vector";
//        GeoServerRESTReader reader = new GeoServerRESTReader(url, username, password);
//        RESTLayer layer = reader.getLayer(workspace, storeName);
//        if (layer == null) {
//            GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(url, username, password);
//            if (publisher.publishShp(workspace, storeName, storeName, zipFile, GeoServer.GEO_CODE, "chinatower_style")) {
//                System.out.println("发布图层成功");
//            } else {
//                System.out.println("发布图层失败");
//            }
//        }
//        File tiffFile = new File("D:\\chinatower\\pg\\tiff\\liaoning_17_img_tif4326.tif");
//        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(url, username, password);
//        publisher.publishGeoTIFF(workspace,"liaoning_17_img_tif4326","liaoning_17_img_tif4326",tiffFile);
//
//        URL deleteFtUrl = new URL(url + "/rest/layers/" + workspace + ":" + tableName+"?recurse=true");
//        boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(), username, password);
//    }

    /**
     * 上传tif地址
     * @param storeName
     * @param tiffFile
     * @return
     */
    public String publisherTiff(String newLayerName,String oldLayerName, File tiffFile) {
        try {
            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            //避免存在重复图层
            boolean removeLayer = manager.getPublisher().unpublishFeatureType(geoServerProperties.getShpWorkspace(), newLayerName , newLayerName);
            if (removeLayer){
                logger.info("删除图层成功 {}",newLayerName);
            }
            //删除旧图层
            if (StringUtils.isNotEmpty(oldLayerName)){
                boolean removeLayerOld = manager.getPublisher().unpublishFeatureType(geoServerProperties.getShpWorkspace(), oldLayerName , oldLayerName);
                if (removeLayerOld){
                    logger.info("删除图层成功 {}",oldLayerName);
                }
            }


            String tifAddress= "/"+geoServerProperties.getShpWorkspace()+"/wms?LAYERS="+newLayerName;
            GeoServerRESTPublisher publisher = manager.getPublisher();
            boolean pushFlag = publisher.publishGeoTIFF(geoServerProperties.getShpWorkspace(), newLayerName, newLayerName, tiffFile);
            if (!pushFlag) {
                throw new CustomException("图层发布失败:" + newLayerName);
            }
            logger.info("上传tif [{}] [{}]",newLayerName,tifAddress);
            return geoServerProperties.getShowUrl() + tifAddress;
        } catch (Exception e) {
            logger.error("发布tiff图层异常", e);
            throw new CustomException("图层发布失败:" + e.getMessage());
        }
    }

    /**
     * 发布图层
     *
     * @param tableName 表名称
     * @param workspace 命名空间
     */
    public Wgs84DTO publisherPostGis(String workspace, String tableName, String name) {
        try {
            PgLayerStyleEnums styleEnums = PgLayerStyleEnums.get(name);
            if (styleEnums == null) {
                throw new CustomException("不存在的类型名称-" + name);
            }
            judgeWorkSpace(workspace);
            String viewLayerName = workspace + ":" + "view_" + tableName;
            String layerName = workspace + ":" + tableName;
            Wgs84DTO dto = new Wgs84DTO();
            dto.setWorkspace(workspace);
            dto.setLayerName(tableName);
            dto.setLayer_name(layerName);
            String url = geoServerProperties.getShowUrl() + "/gwc/service/tms/1.0.0/" + dto.getLayer_name() + "@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf";
            String viewUrl = geoServerProperties.getShowUrl() + "/gwc/service/tms/1.0.0/" + viewLayerName + "@EPSG%3A900913@pbf/{z}/{x}/{-y}.pbf";
            dto.setUrl(url);
            dto.setViewUrl(viewUrl);
            dto.setRootUrl(geoServerProperties.getShowUrl());
            URL u = new URL(geoServerProperties.getUrl());
            GeoServerRESTManager manager = new GeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            //创建样式文件
            publishStyle(styleEnums.getStyleName());
            //创建存储桶
            boolean createStoreFlag = createStore(manager, workspace);
            if (!createStoreFlag) {
                throw new CustomException("geoserver 创建 储存仓库失败");
            }
            //发布图层全部字段
            RESTLayer layer = pushLayer(manager, workspace, tableName, styleEnums.getStyleName());
            //发布视图表
//            RESTLayer layerView = pushLayer(manager, workspace, "view_" + tableName,styleEnums.getStyleName());
//            if (layer == null) {
//                throw new CustomException("geoserver 获得 图层失败");
//            }
//            CompletableFuture.runAsync(() -> layerSlice(layerName));
//            CompletableFuture.runAsync(() -> layerSlice(viewLayerName));
            return getWgs84DTO(dto, manager.getReader(), layer);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            logger.error("PG图层发布失败", e);
            throw new CustomException("PG图层发布失败！");
        }
    }

    private boolean createStore(GeoServerRESTManager manager, String workspace) {
        //存储名/图层名
        RESTDataStore restStore = manager.getReader().getDatastore(workspace, geoServerProperties.getGuotuStore());
        boolean createStoreFlag = true;
        if (restStore == null) {
            GSPostGISDatastoreEncoder store = new GSPostGISDatastoreEncoder(geoServerProperties.getGuotuStore());
            store.setHost(pgSqlProperties.getHost());
            store.setPort(pgSqlProperties.getPort());
            store.setUser(pgSqlProperties.getUser());
            store.setPassword(pgSqlProperties.getPassword());
            store.setDatabase(pgSqlProperties.getDatabase());
            store.setMinConnections(10);
            store.setMaxConnections(100);
            store.setSchema("public");
            createStoreFlag = manager.getStoreManager().create(workspace, store);
            logger.info("pgsql 创建储存 == {}", createStoreFlag);
        }
        return createStoreFlag;
    }

    /**
     * @param workspace 命名空间
     * @param tableName 表名称
     * @return 发布GIS图层
     */
    private RESTLayer pushLayer(GeoServerRESTManager manager, String workspace, String tableName, String styleName) {
        RESTLayer layer = manager.getReader().getLayer(workspace, tableName);
        if (layer == null) {
            GSFeatureTypeEncoder pds = new GSFeatureTypeEncoder();
            pds.setTitle(tableName);
            pds.setName(tableName);
            pds.setSRS(GEO_CODE);
            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setDefaultStyle(styleName);
            boolean createLayerFlag = manager.getPublisher().publishDBLayer(workspace, geoServerProperties.getGuotuStore(), pds, layerEncoder);
            logger.info("pgsql 表发布 : {} tableName {}", createLayerFlag, tableName);
            if (!createLayerFlag) {
                throw new CustomException("发布图层失败");
            }
            layer = manager.getReader().getLayer(workspace, tableName);
            return layer;
        }
        return layer;
    }

    /**
     * @return 指定图层进行切片操作
     */
    public void layerSlice(String layerName) {
        try {
            logger.info("切片图层 {}", layerName);
            String entity = "{'seedRequest':{'name':'" + layerName + "','GridSet':'EPSG:900913','zoomStart':0,'zoomStop':18,'format':'application/vnd.mapbox-vector-tile','type':'seed','threadCount':1}}}";
            HttpResponse res = HttpRequest.post(geoServerProperties.getUrl() + "/gwc/rest/seed/" + layerName + ".json")
                    .header("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((geoServerProperties.getGeoUsername() + ":" + geoServerProperties.getGeoPassword()).getBytes()))
                    .body(entity).execute();
            logger.info("切片返回状态 {} {}", res.getStatus(), res.body());
        } catch (Exception e) {
            logger.info("切片失败：{}", layerName);
            throw new CustomException("请求切片失败");
        }
    }


    /**
     * 删除图层
     *
     * @param workspace 工作空间
     * @param layerName 图层名称
     */
    public void deleteLayer(String workspace, String layerName) {
        try {
            URL u = new URL(geoServerProperties.getUrl());
            MyGeoServerRESTManager manager = new MyGeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            boolean removeLayer = manager.getPublisher().unpublishFeatureType(workspace,geoServerProperties.getGuotuStore() , layerName);
            if (removeLayer){
                logger.info("删除图层成功 {}",layerName);
            }
        } catch (Exception e) {
            logger.error("删除geoserver储存错误", e);
            throw new CustomException("删除geoserver储存错误");
        }

    }


    /**
     * @param dto
     * @param reader
     * @param layer
     * @return 获得基础数据
     */
    private Wgs84DTO getWgs84DTO(Wgs84DTO dto, GeoServerRESTReader reader, RESTLayer layer) {
        RESTWms wms = reader.getWms(layer);
        dto.setMinx(wms.getMinX());
        dto.setMaxx(wms.getMaxX());
        dto.setMiny(wms.getMinY());
        dto.setMaxy(wms.getMaxY());
        RESTFeatureType featureType = reader.getFeatureType(layer);
        Iterable<RESTFeatureType.Attribute> attributes = featureType.getAttributes();
        Iterator<RESTFeatureType.Attribute> iterator = attributes.iterator();
        while (iterator.hasNext()) {
            RESTFeatureType.Attribute next = iterator.next();
            if (next.getBinding().equals("org.locationtech.jts.geom.MultiPolygon")) {
                dto.setThe_geom(next.getName());
                break;
            }
        }
        return dto;
    }

    /**
     * 上传tif 图层
     */
    public void uploadLayerTiff(String workspace, String storeName, File tiffFile) {
        try {

            URL u = new URL(geoServerProperties.getUrl());
            GeoServerRESTManager manager = new GeoServerRESTManager(u, geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            GeoServerRESTReader reader = new GeoServerRESTReader(geoServerProperties.getUrl(), geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
            RESTLayer layer = reader.getLayer(workspace, storeName);
            if (layer == null) {
                GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoServerProperties.getUrl(), geoServerProperties.getGeoUsername(), geoServerProperties.getGeoPassword());
                if (publisher.publishGeoTIFF(workspace, storeName, storeName, tiffFile)) {
                    logger.info("发布成功 {}" + storeName);
                    layer = reader.getLayer(workspace, storeName);
                } else {
                    throw new CustomException("tif图层发布失败:" + storeName);
                }
            } else {
                logger.info("tif图层已存在 {}" + storeName);
            }
        } catch (Exception e) {
            logger.error("上传tif报错", e);
            throw new com.chinatower.platform.common.exception.CustomException("上传tiff报错");
        }
    }
}
