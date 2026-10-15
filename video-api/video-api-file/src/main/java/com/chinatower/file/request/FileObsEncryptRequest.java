package com.chinatower.file.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: ObsEncryptRequest
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Data
public class FileObsEncryptRequest implements Serializable {

    /**
     * 原始路径(批量)
     */
    private List<String> fileUrls;

    /**
     * 是否使用ofs
     */
    private Boolean useOfs;

    /**
     * 节点编码
     */
    private String colonyCode;
}
