package com.chinatower.system.api.resp;

import lombok.Data;

import java.io.Serializable;


/**
 * @className: obsEncryptResponse
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Data
public class ObsEncryptResponse implements Serializable {

    /**
    * 原始路径
    * @required false
    */
    private String fileUrl;
    /**
     * 加密后路径
     * @required false
     */
    private String encryptUrl;

}
