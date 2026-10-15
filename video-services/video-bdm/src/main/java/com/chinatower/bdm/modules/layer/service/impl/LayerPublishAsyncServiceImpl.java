package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.kafka.MessageKafkaProducer;
import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.config.GeoServerProperties;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.bdm.modules.layer.domain.layerTree;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerPolygonMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeMapper;
import com.chinatower.bdm.modules.layer.service.ILayerService;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.bdm.modules.layer.service.LayerPublishAsyncService;
import com.chinatower.bdm.modules.layer.service.LegendService;
import com.chinatower.common.core.comparator.ChineseComparator;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IPolygonPatchConfigService;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.GisPgAPI;
import com.chinatower.gis.api.entity.DeleteLayerReq;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.PGLayerTempEnums;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.file.FileUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.request.file.SysFileInfo;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LayerPublishAsyncServiceImpl implements LayerPublishAsyncService {

    private final WgsAnalysisService wgsAnalysisService;

    private final GeoServer geoServer;

    private final LayerMapper layerMapper;

    @Value("${geoserver.tiffKeyword:GeoTIFF}")
    private String tiffFile;

    private final String MIN_X = "minx";
    private final String MAX_X = "maxx";
    private final String MIN_Y = "miny";
    private final String MAX_Y = "maxy";

    private final String LAYER_URL_VERSION_PRE = "version=";
    private final String LAYER_TIFF_TYPE_ERR_MSG = "请和铁塔管理员确认url是否正确";

    @Async
    public void publishAndSaveResult(String layerId, String fileUrl, String fileName) {
        // 根据结果更新数据库
        Layer layer = layerMapper.selectById(layerId);
        log.info("查询图层信息layerId:" + layerId);
        String oldLayerName = null;
        if (layer != null) {
            oldLayerName = layer.getLayerName();
        }
        //新的图层名称
        String newLayerName = "tif_" + System.currentTimeMillis() + "_" + RandomUtil.randomString(6);
        Pair<Boolean, String> result = analysisTif(newLayerName, oldLayerName, fileUrl);
        log.info("发布图层信息返回:"+result);
        if (layer != null) {
            log.info("查询图层信息:" + layer);
            Layer layerUpdate = new Layer();
            if (result.getKey()) {
                // 发布成功
                layerUpdate.setPublishStatus("1");
                layerUpdate.setFileUrl(result.getValue());
                String fileUrl1 = result.getValue() + "&REQUEST=GetCapabilities&FORMAT=";
                //请求geoserver
                Map<String, Double> xyFromXml = getXYFromXml(fileUrl1);
                if (!Objects.requireNonNull(xyFromXml).isEmpty()) {
                    layerUpdate.setMinx(xyFromXml.get(MIN_X));
                    layerUpdate.setMaxx(xyFromXml.get(MAX_X));
                    layerUpdate.setMiny(xyFromXml.get(MIN_Y));
                    layerUpdate.setMaxy(xyFromXml.get(MAX_Y));
                }
            } else {
                // 发布失败
                layerUpdate.setPublishStatus("2");
                log.info("图层发布失败:" + result.getValue());
            }
            // 更新操作
            layerUpdate.setId(layerId);
            layerUpdate.setLayerName(newLayerName);
            layerUpdate.setFileName(fileName);
            log.info("更新图层信息:" + layerUpdate);
            layerMapper.updateById(layerUpdate);
        }
    }

    public Pair<Boolean, String> analysisTif(String newLayerName, String oldLayerName, String fileUrl) {
        try {
            String tifGeoserverUrl = analysis(newLayerName, oldLayerName, fileUrl);
            return Pair.of(true, tifGeoserverUrl);
        } catch (Exception e) {
            log.error("解析tif地址异常", e);
            return Pair.of(false, e.getMessage());
        }
    }

    public String analysis(String newLayerName, String oldLayerName, String fileUrl) {
        StopWatch st = new StopWatch();
        log.info("解析tif地址 【{}】", fileUrl);

        //下载保存文件地址
        String saveFileAddress = wgsAnalysisService.getWgs84Root() + GeoServer.PATH + newLayerName + ".tif";
        if (StringUtils.isEmpty(fileUrl)) {
            throw new CustomException("文件地址不能为空");
        }

        log.info("saveFileAddress == " + saveFileAddress);
        Path path = null;

        try {
            st.start("下载网络文件tif到本地");
            wgsAnalysisService.createDir(wgsAnalysisService.getWgs84Root());
            File file = new File(saveFileAddress);
            if (!file.exists()) {
                GeoServer.downloadNet(fileUrl, saveFileAddress);
                file = FileUtils.getFile(saveFileAddress);
            }
            st.stop();
            if (!file.exists()) {
                throw new CustomException("文件下载失败");
            }
            //解析文件数据
            path = Paths.get(saveFileAddress);
            st.start("判断文件是否符合规则");
            //判断文件是否符合规则
            if (!hasSuffix(file.getName(), ".tif")) {
                throw new CustomException("上传文件不符合规则，请重新上传。");
            }
            st.stop();
            st.start("解析图层");

            String tifAddress = geoServer.publisherTiff(newLayerName, oldLayerName, file);
            st.stop();
            return tifAddress;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析错误", e);
        } finally {
            if (path != null) {//使用完成删除文件
                try {
                    Files.delete(path);
                } catch (Exception e) {
                    log.error("清除文件报错", e);
                }
            }
            System.out.println(st.prettyPrint());
        }
        throw new CustomException("上传文件不符合规则，请重新上传。");
    }

    public static boolean hasSuffix(String filename, String suffix) {
        // 忽略大小写判断
        return filename.toLowerCase().endsWith(suffix.toLowerCase());
    }

    private Map<String, Double> getXYFromXml(String fileUrl) {
        try {
            //获取版本号
            String lowerCasedInput = fileUrl.toLowerCase();

            int versionStartIndex = lowerCasedInput.indexOf(LAYER_URL_VERSION_PRE);
            // 如果未找到 "version="
            if (versionStartIndex == -1) {
                // 直接在字符串末尾添加
                fileUrl += "&VERSION=1.3.0";
                log.info("Added version: " + fileUrl);
            } else {
                int versionEndIndex = lowerCasedInput.indexOf("&", versionStartIndex + LAYER_URL_VERSION_PRE.length());
                String extractedVersion = fileUrl.substring(versionStartIndex + LAYER_URL_VERSION_PRE.length(), versionEndIndex != -1 ? versionEndIndex : fileUrl.length());
                log.info("Extracted version: " + extractedVersion);
                // 替换为 1.3.0
                fileUrl = fileUrl.substring(0, versionStartIndex + LAYER_URL_VERSION_PRE.length()) + "1.3.0" +
                        (versionEndIndex != -1 ? fileUrl.substring(versionEndIndex) : "");
                log.info("Replaced version: " + fileUrl);
            }
            log.info("替换tiff版本号1.3.0后的url: {}", fileUrl);

            RestTemplate restTemplate = new RestTemplate();
            Pattern pattern = Pattern.compile("layers=([^&]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(fileUrl);
            if (matcher.find()) {
                String layersValue = matcher.group(1);
                log.info("Extracted layers value: " + layersValue);
                //请求geoserver
                String xmlObj = restTemplate.getForObject(fileUrl, String.class);
                Document document = XmlUtil.readXML(xmlObj);
                Element root = document.getDocumentElement();
                Element capability = XmlUtil.getElement(root, "Capability");
                String layerElement = "Layer";
                Element layer1 = XmlUtil.getElement(capability, layerElement);
                List<Element> layers = XmlUtil.getElements(layer1, layerElement);
                Map<String, Double> map = new HashMap<>();
                layers.forEach(item -> {
                    Element nameElement = XmlUtil.getElement(item, "Name");
                    String name = nameElement.getTextContent();
                    if (layersValue.equals(name)) {

                        Element keywordList = XmlUtil.getElement(item, "KeywordList");
                        List<Element> keyword = XmlUtil.getElements(keywordList, "Keyword");
                        //判断是否包含tiff文件后缀
                        List<String> files = Arrays.asList(tiffFile.split(","));
                        log.info("配置的tiff keyword{}", JSON.toJSONString(files));
                        System.out.println("tiff:" + tiffFile);
                        boolean foundMatch = keyword.stream()
                                .map(Node::getTextContent)
                                .anyMatch(files::contains);

                        if (foundMatch) {
                            Element exGeographicBoundingBox = XmlUtil.getElement(item, "EX_GeographicBoundingBox");
                            log.info("解析EX_GeographicBoundingBox:{}", JSON.toJSONString(exGeographicBoundingBox));
                            //minx
                            Element westBoundLongitude = XmlUtil.getElement(exGeographicBoundingBox, "westBoundLongitude");
                            //maxx
                            Element eastBoundLongitude = XmlUtil.getElement(exGeographicBoundingBox, "eastBoundLongitude");
                            //miny
                            Element southBoundLatitude = XmlUtil.getElement(exGeographicBoundingBox, "southBoundLatitude");
                            //maxy
                            Element northBoundLatitude = XmlUtil.getElement(exGeographicBoundingBox, "northBoundLatitude");

                            map.put(MIN_X, Double.valueOf(westBoundLongitude.getTextContent()));
                            map.put(MAX_X, Double.valueOf(eastBoundLongitude.getTextContent()));
                            map.put(MIN_Y, Double.valueOf(southBoundLatitude.getTextContent()));
                            map.put(MAX_Y, Double.valueOf(northBoundLatitude.getTextContent()));
                            log.info("最后获得的图层定位边界:{}", JSON.toJSONString(map));
                        } else {
                            throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);
                        }
                    }
                });
                return map;
            } else {
                System.out.println("No matching layers parameter found in the URL.");
                throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(LAYER_TIFF_TYPE_ERR_MSG);

        }
    }
}
