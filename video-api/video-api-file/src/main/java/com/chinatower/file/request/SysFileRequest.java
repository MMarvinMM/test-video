package com.chinatower.file.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysFileRequest implements Serializable {

    /**
     * 文件名称 必填
     */
    private String fileName;
    /**
     * 节点编码【非必填，默认当前登录节点】
     */
    private String colonyCode;
    /**
     * 是否使用ofs【非必填，默认不使用】
     */
    private Boolean useOfs;
}

