package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 广播子平台
 * 4.15.删除文本请求参数实体类
 * @author huang.xiangyu
 */
@Data
public class HornDeleteTxtReqVO implements Serializable {


    private List<String> txtIds;

    private String acceptNode;
}
