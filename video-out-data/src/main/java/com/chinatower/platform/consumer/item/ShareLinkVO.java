package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShareLinkVO implements Serializable {
    /**
     * 长链接
     */
    private String longUrl;

    /**
     * 短连接
     */
    private String shortUrl;

    /**
     * 域名地址
     */
    private String domain;

    /**
     * 创建时间
     */
    private Date createTime;
}
