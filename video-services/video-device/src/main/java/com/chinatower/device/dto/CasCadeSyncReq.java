package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CasCadeSyncReq implements Serializable {

    /**
     * 上级平台编码
     */
    private String platAsId;
    /**
     * 访问权限
     */
    private String[] permissions;

    private List<CasCadeSyncDeviceReq> deviceList;

    private List<CasCadeSyncOrgDTO> orgList;

    /**
     * 域资源类型：2-行政区域域资源（默认），3-自定义域资源
     */
    private String regionType;

    /**
     *唯一标识
     */
    private String permissionFlag;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;
}
