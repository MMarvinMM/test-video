package com.chinatower.file.request;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/29 15:28
 */
@Data
public class SysFileUrlRequest {

    /**
     * 文件名称 必填
     */
    private String oldUrl;
    /**
     * 节点编码【非必填，默认当前登录节点】
     */
    private String colonyCode;
    /**
     * 是否使用ofs【非必填，默认不使用】
     */
    private Boolean useOfs;
}
