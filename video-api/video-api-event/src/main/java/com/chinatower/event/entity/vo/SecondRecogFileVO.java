package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cuisj
 * @date 2023-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogFileVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 视频url
     */
    private String videoUrls;

    /**
     * 图片url
     */
    private String imgUrls;

    /**
     * 基准图URL
     */
    private String baselinePicUrl;

    /**
     * 分析类型video – 分析视频     image – 分析图片
     */
    private String type;
}
