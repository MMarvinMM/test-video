package com.chinatower.common.core.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.chinatower.common.core.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 设备通道算法信息body消息体.
 *4.10. 设备/通道/算法关系推送.
 * @author: fangjun
 */
@Getter
@Setter
@SuppressWarnings("all")
public class DeviceAlgorithmsDTO implements Serializable {

    /**
     * 启用时间，N. yyyy-MM-dd HH:mm:ss.
     */
    private Date startTime;

    /**
     * 结束时间，N. yyyy-MM-dd HH:mm:ss.
     */
    private Date endTime;

    /**
     * 操作类型，1-新增或变更；2-删除.
     */
    private Integer optType;

    /**
     * 算法关系列表.
     */
    private List<DeviceAlgorithms> list;

    @Override
    public String toString() {
        try {
            return JsonUtils.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
