package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.util.List;

/**
 * 用于接收固定目标信息的实体类
 */
@Data
public class FixedTargetDTO {

    // 雷达ID集合
    private List<String> deviceId;

    // 是否确认
    private Integer isVerified;

    // 分页信息
    private Integer current;

    // 分页信息
    private Integer size;

    // 确认目标集合
    private List<Integer> idList;

}
