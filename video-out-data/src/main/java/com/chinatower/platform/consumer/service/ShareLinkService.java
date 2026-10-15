package com.chinatower.platform.consumer.service;

import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.consumer.dao.*;
import com.chinatower.platform.consumer.item.ShareLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class ShareLinkService {

    @Autowired
    private ShareLinkDAO shareLinkDAO;

    public String getShortUrl(ShareLinkVO param) {
        if (param == null || StringUtils.isBlank(param.getLongUrl())) {
            throw new CustomException("参数不能为空");
        }
        String shortUrl = UUID.randomUUID().toString().replace("-", "");
        try {
            URL url = new URL(param.getLongUrl());

            // 构建带协议的域名/IP端口部分
            StringBuilder protocolHostPort = new StringBuilder();
            protocolHostPort.append(url.getProtocol()).append("://"); // 协议部分
            protocolHostPort.append(url.getHost()); // 域名或IP

            // 处理端口（非默认端口才添加）
            int port = url.getPort();
            if (port != -1) {
                protocolHostPort.append(":").append(port);
            }

            // 构建路径及后面的部分（路径+查询参数+片段）
            StringBuilder pathAndBeyond = new StringBuilder();
            pathAndBeyond.append(url.getPath());

            String query = url.getQuery();
            if (query != null) {
                pathAndBeyond.append("?").append(query);
            }

            String fragment = url.getRef();
            if (fragment != null) {
                pathAndBeyond.append("#").append(fragment);
            }
            ShareLinkVO shareLinkVO = new ShareLinkVO();
            shareLinkVO.setShortUrl(shortUrl);
            shareLinkVO.setLongUrl(pathAndBeyond.toString());
            shareLinkVO.setDomain(protocolHostPort.toString());
            shareLinkVO.setCreateTime(new Date());
            shareLinkDAO.save(shareLinkVO);
            return String.format("%s/api/outdata/link/%s", protocolHostPort, shortUrl);
        } catch (Exception e) {
            log.error("ShareLinkService getShortUrl error:", e);
            throw new CustomException("短链接生成失败: " + e.getMessage());
        }
    }

    public String getLongUrl(String shortUrl) {
        ShareLinkVO shareLinkVO = shareLinkDAO.getShareLinkInfoByShortUrl(shortUrl);
        if (shareLinkVO == null) {
            throw new CustomException("短链接不存在");
        }
        return String.format("%s%s", shareLinkVO.getDomain(), shareLinkVO.getLongUrl());
    }

    public static void main(String[] args) {
        String longUrl = "https://www.example.com:8443/path1/path2?query=param#fragment";
        try {
            URL url = new URL(longUrl);

            // 构建带协议的域名/IP端口部分
            StringBuilder protocolHostPort = new StringBuilder();
            protocolHostPort.append(url.getProtocol()).append("://"); // 协议部分
            protocolHostPort.append(url.getHost()); // 域名或IP

            // 处理端口（非默认端口才添加）
            int port = url.getPort();
            if (port != -1) {
                protocolHostPort.append(":").append(port);
            }

            // 构建路径及后面的部分（路径+查询参数+片段）
            StringBuilder pathAndBeyond = new StringBuilder();
            pathAndBeyond.append(url.getPath());

            String query = url.getQuery();
            if (query != null) {
                pathAndBeyond.append("?").append(query);
            }

            String fragment = url.getRef();
            if (fragment != null) {
                pathAndBeyond.append("#").append(fragment);
            }
            System.out.println(protocolHostPort.toString());
            System.out.printf(pathAndBeyond.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
