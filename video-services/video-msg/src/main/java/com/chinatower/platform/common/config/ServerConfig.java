package com.chinatower.platform.common.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @title
 * @description 服务Ip端口配置类
 * @author LiZe
 * @updateTime 2022/1/19 18:02
 * @throws
 */
@Component
@Slf4j
public class ServerConfig  implements ApplicationListener<WebServerInitializedEvent> {
    /**
     * 服务端口号
     */
    private int serverPort;
    /**
     * 请求冒号
     */
    public static final String COLON = ":";

    /**
     * @title
     * @description  获取当前服务ip及端口号
     * @author LiZe
     * @updateTime 2022/1/19 18:02
     * @throws
     */
    public String getUrl() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        log.info("当前消息服务ip为:: " +address);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        log.info("当前消息服务port为:: " + serverPort);
        log.info("==========MessageService--LocalAddress:: " + address.getHostAddress() + COLON +this.serverPort);
        return address.getHostAddress() + COLON +this.serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

}
