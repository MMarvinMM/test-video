package com.chinatower.video.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PlayerGetVideoPlayBackUrlData
 * @Description: 视频获取通道的录像回放流URL地址返回实体
 * @Author: ldl
 * @Date: 2022/1/18
 **/
@Data
public class PlayerGetVideoPlayBackUrlData  implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *回放流url地址（公网地址）
     * */
    private String streamUrl;
    /**
     *本次流ID , 长度限制32字符 , 不传或传空平台自动生成
     * */
    private String streamUuid;
    /**
     *当protocolType=6时，会返回倍速回放控制唯一标识，用于倍速回放的时候回传这个id，每路流一个id
     * */
    private String streamControlId;
}
