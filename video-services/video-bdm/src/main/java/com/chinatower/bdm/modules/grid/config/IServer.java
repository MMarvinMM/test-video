package com.chinatower.bdm.modules.grid.config;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.chinatower.bdm.modules.grid.dao.IServerDTO;
import com.chinatower.platform.common.utils.security.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IServer {

    @Autowired
    IServerProperties iServerProperties;


    //iserver登录地址
    private static final String LOGIN_URL = "/services/security/tokens.rjson";
    //创建任务地址
    private static final String CREATE_TASK_URL = "/manager/filemanager/uploadtasks.json?token=";
    //发布服务接口地址
    private static final String PROVIDERS_URL = "/manager/providers.rjson?token=";
    private static final String COMPONENTS_URL = "/manager/components.rjson?token=";
    private static final String WMS_ADDRESS = "%s/services/%s/wms130";
    private static final String WFS_ADDRESS = "%s/services/%s/wfs100";

    private static final String WMS = "wms-";
    private static final String WFS = "wfs-";

    //获取token 令牌发放方式为IP验证
    public  String geToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", iServerProperties.getAccount());
        map.put("password", iServerProperties.getPassword());
        map.put("clientType", "NONE");
        map.put("expiration", 10);
        return HttpRequest.post(iServerProperties.getUrl() + LOGIN_URL)
                .body(JSON.toJSONString(map))
                .execute().body();
    }

    /**
     * @param fileAddress 本地文件地址
     * @param fileName    文件名称 xx.zip
     */
    public  IServerDTO pushIServer(String fileAddress, String fileName) {
        String token = geToken();
        String body = HttpRequest.post(iServerProperties.getUrl() + CREATE_TASK_URL + token)
                .execute().body();
        JSONObject json = JSON.parseObject(body);
        String uploadUrl = json.getString("newResourceLocation");
        log.info("uploadUrl == " + uploadUrl);
        return startUpFile(uploadUrl, fileAddress, fileName);
    }

    /**
     * 上传文件
     *
     * @param uploadUrl    上传接口
     * @param localZipPath 本地文件地址
     * @param fileName     文件名称
     */
    public  IServerDTO startUpFile(String uploadUrl, String localZipPath, String fileName) {
        String token = geToken();
        File file = new File(localZipPath);

        String zipPath = iServerProperties.getSavaDirectory() + fileName;
        HttpRequest.post(uploadUrl + ".json?&toFile=" + zipPath + "&unzip=true&overwrite=true&token=" + token)
                .form("file", file)
                .execute();
        //文件名称
        String[] fileNameArr = fileName.split("\\.");
        String shapefileName = fileNameArr[0];
        String name = isContainChinese(shapefileName) ? MD5Util.hash(shapefileName) : fileNameArr[0];

        String wmsName = WMS + name;
        String wfsName = WFS + name;
        serviceProvidersWms(iServerProperties.getSavaDirectory() + shapefileName, wmsName, token);
        serviceProvidersWfs(iServerProperties.getSavaDirectory() + shapefileName, wfsName, token);

        String wmsAddress = String.format(WMS_ADDRESS, iServerProperties.getShowUrl(), wmsName);
        String wfsAddress = String.format(WFS_ADDRESS, iServerProperties.getShowUrl(), wfsName);
        IServerDTO serverDTO = IServerDTO.builder().wfsUrl(wfsAddress).wmsUrl(wmsAddress).wmsName(shapefileName).build();
        return setBbox(serverDTO, wfsAddress);
    }


    /**
     * 发布WMS服务
     */
    public  void serviceProvidersWms(String savaPath, String name, String token) {
        String body = serviceProviders(savaPath, name, token, "ShapeFileMapProvider");
        JSONObject object = JSON.parseObject(body);
        if (Boolean.TRUE.equals(object.getBoolean("succeed"))) {
            Map<String, Object> componentsMap = new HashMap<>();
            componentsMap.put("editable", false);
            serviceComponents(name, token, componentsMap, "wms130,wms111", "MapImpl");
        }
        log.info("serviceProvidersWms == " + body);
    }

    /**
     * 发布WFS服务
     */
    public  void serviceProvidersWfs(String savaPath, String name, String token) {
        String body = serviceProviders(savaPath, name, token, "ShapeFileDataProvider");
        JSONObject object = JSON.parseObject(body);
        if (Boolean.TRUE.equals(object.getBoolean("succeed"))) {
            Map<String, Object> componentsMap = new HashMap<>();
            componentsMap.put("editable", false);
            serviceComponents(name, token, componentsMap, "wfs100,wfs200", "DataImpl");
        }
        log.info("serviceProvidersWfs == " + body);
    }

    /**
     * 发布服务接口
     */
    private  String serviceProviders(String savaPath, String name, String token, String type) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("shpDir", savaPath);
        configMap.put("charset", "GBK");

        map.put("config", JSON.toJSONString(configMap));
        map.put("name", name);
        map.put("type", "com.supermap.services.providers." + type);

        return HttpRequest.post(iServerProperties.getUrl() + PROVIDERS_URL + token)
                .body(JSON.toJSONString(map))
                .execute().body();

    }

    /**
     * 创建服务接口
     *
     * @param name           服务名称
     * @param token          token
     * @param configMap      配置
     * @param interfaceNames 接口
     * @param service        服务组件
     */
    public  void serviceComponents(String name, String token, Map<String, Object> configMap, String interfaceNames, String service) {
        Map<String, Object> map = new HashMap<>();
        map.put("config", JSON.toJSONString(configMap));
        map.put("interfaceNames", interfaceNames);
        map.put("name", name);
        map.put("providers", name);
        map.put("type", "com.supermap.services.components.impl." + service);

        String body = HttpRequest.post(iServerProperties.getUrl() + COMPONENTS_URL + token)
                .body(JSON.toJSONString(map))
                .execute().body();
        log.info("serviceComponents == " + body);
    }

    private  String getInfo(String wfsUrl) {
        return HttpRequest.get(wfsUrl)
                .form("VERSION", "1.0.0")
                .form("SERVICE", "WFS")
                .form("REQUEST", "GetCapabilities")
                .execute().body();
    }


    public  IServerDTO setBbox(IServerDTO serverDTO, String wfsUrl) {
        try {
            Element rootElement = DocumentHelper.parseText(getInfo(wfsUrl)).getRootElement();
            Iterator<Element> iterator = rootElement.elementIterator("FeatureTypeList");
            while (iterator.hasNext()) {
                Element next = iterator.next();
                List<Element> list = next.elements();
                for (Element element : list) {
                    if (!"FeatureType".equals(element.getName())) {
                        continue;
                    }
                    List<Element> elements = element.elements();
                    for (Element element1 : elements) {
                        if ("Name".equals(element1.getName())){
                            serverDTO.setTypeName(element1.getStringValue());
                        }
                        if ("Title".equals(element1.getName())){
                            serverDTO.setWfsName(element1.getStringValue());
                        }
                        if ("SRS".equals(element1.getName())){
                            serverDTO.setSrs(element1.getStringValue());
                        }

                        if ("LatLongBoundingBox".equals(element1.getName())) {
                            serverDTO.setMaxx(Double.parseDouble(element1.attributeValue("maxx")));
                            serverDTO.setMaxy(Double.parseDouble(element1.attributeValue("maxy")));
                            serverDTO.setMinx(Double.parseDouble(element1.attributeValue("minx")));
                            serverDTO.setMiny(Double.parseDouble(element1.attributeValue("miny")));
                            break;
                        }
                    }
                }
                break;
            }
        } catch (Exception e) {
            log.error("获得bbox报错", e);
        }
        return serverDTO;
    }


    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public  boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

}
