package com.chinatower.bdm.modules.grid.config;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.Util;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 自定义管理
 * @data 创建时间：2024/1/29 13:59
 */
@Slf4j
@SuppressWarnings("all")
public class MyGeoServerRESTStyleManager extends GeoServerRESTStyleManager {


    public MyGeoServerRESTStyleManager(URL restURL, String username, String password) throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     *
     * @param sldFile 样式文件
     * @param name 样式名称
     * @param workspace 命名空间
     * @param raw
     * @return 上传样式文件结果
     */
    public boolean publishStyle(File sldFile, String name, String workspace, boolean raw) {
        StringBuilder sUrl = new StringBuilder(this.buildPostUrl(workspace, name));
        Util.appendParameter(sUrl, "raw", "" + raw);
        String contentType = GeoServerRESTPublisher.Format.SLD.getContentType();
        if (!this.checkSLD10Version(sldFile)) {
            contentType = GeoServerRESTPublisher.Format.SLD_1_1_0.getContentType();
        }
        log.debug("POSTing new style " + name + " to " + sUrl + " using version: " + contentType);
        String result = HTTPUtils.post(sUrl.toString(), sldFile, contentType, this.gsuser, this.gspass);
        return result != null;
    }

    /**
     *
     * @param name 样式名称
     * @param workspace 命名空间
     */
    public boolean removeStyle(String name, String workspace) {
        log.info("准备删除样式 {} {}", workspace , name);
        StringBuilder sUrl = new StringBuilder(this.buildXmlUrl(workspace, name));
        boolean result = HTTPUtils.delete(sUrl.toString(), this.gsuser, this.gspass);
        return result ;
    }

    private boolean checkSLD10Version(File fileSLD) {
        boolean result = false;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileSLD);
            result = this.checkSLD10Version(doc);
        } catch (SAXException | ParserConfigurationException | IOException var6) {
            log.error("Error parsing SLD file: " + var6);
        }
        return result;
    }

    public boolean checkSLD10Version(String sldBody) {
        boolean result = false;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(sldBody)));
            result = this.checkSLD10Version(doc);
        } catch (SAXException | ParserConfigurationException | IOException var6) {
            log.error("Error parsing SLD file: " + var6);
        }
        return result;
    }

    private boolean checkSLD10Version(Document doc) {
        boolean result = false;
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//@version='1.0.0'");
            result = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException var6) {
            log.error("Error parsing SLD file: " + var6);
        }
        return result;
    }

    public boolean publishStyleInWorkspace110(String workspace, String sldBody, String name) throws IllegalArgumentException {
        if (sldBody != null && !sldBody.isEmpty()) {
            String sUrl = this.buildPostUrl(workspace, name);
            String result = HTTPUtils.post(sUrl, sldBody, "application/vnd.ogc.se+xml", this.gsuser, this.gspass);
            return result != null;
        } else {
            throw new IllegalArgumentException("The style body may not be null or empty");
        }
    }
}
