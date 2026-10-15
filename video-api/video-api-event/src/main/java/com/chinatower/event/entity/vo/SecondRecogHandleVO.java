package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 二次识别配置下发参数
 * @author cuisj
 * @date 2023-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogHandleVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警id
     */
    private String taskId;

    /**
     * 抓图图片
     */
    private String picUrl;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 二次识别配置信息
     */
    private List<SecondRecogReceiveVO> configList;

    /**
     * 表名标识  1:压制 2.告警 3.研判
     * */
    private String secondRecogTableType;
}
