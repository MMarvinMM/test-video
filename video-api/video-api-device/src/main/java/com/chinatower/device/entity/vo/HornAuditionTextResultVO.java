package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class HornAuditionTextResultVO implements Serializable {

    /**
     * 内容类别
     */
    private String audioType;

    /**
     * 具体类容
     */
    private List<String> contentValue;
}
