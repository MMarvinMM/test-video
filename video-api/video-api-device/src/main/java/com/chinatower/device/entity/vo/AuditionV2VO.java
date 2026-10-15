package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class AuditionV2VO implements Serializable {

    private String audioType;

    private List<String> dataList;
}
