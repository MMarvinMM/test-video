package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

/**
 * 4.13.	同步应用级联平台信息
 */
@Data
public class SynCobtainVO implements Serializable {
    /**
     * 平台编码
     */
    private String platCode;
    /**
     * 平台名称
     */
    private String platName;
    /**
     * 接入下沉节点编码
     */
    private String accessCode;
    /**
     * SIP链接协议（信令）sip连接方式 1：TCP，2：UDP
     */
    private int sipType;
    /**
     * 取流链接协议取流方式 1：TCP  2：UDP
     */
    private int protocolType;
    /**
     * 上级级平台ip
     */
    private String superPlatIp;
    /**
     * 上级平台密码
     */
    private String superPlatPassWord;
    /**
     * 上级级平台ID
     */
    private String superPlatCode;
    /**
     * 上级平台端口
     */
    private int superPlatPort;
    /**
     * 网域类型 网域类型 0：局域网 1：公网
     */
    private int netType;
    /**
     * 对接厂家 对接平台厂家 1：本家，2：海康，3：大华，4：宇视，5：其它
     */
    private int platModelCode;
    /**
     * 操作类型 0:新增， 1:修改, 2:删除
     */
    private int optType;
    /**
     * 是否同步离线设备是否同步在线设备：0：否；1：是
     */
    private int synchronizeOnlineDevice;
    /**
     * 是否启用流保活：0：否；1：是
     */
    private int keepAliveStream;
    /**
     * 服务端口
     */
    private int servicePort;

    /**
     * 属地化同步新增--接入节点
     */
    private String accessNode;

    /**
     * 级联方式 0：上级平台 1：下级平台
     */
    private Integer cascadeType;

    /**
     * 基础级联平台id
     */
    private String platId;

    /**
     * 创建人
     */
    private String creator;
}
