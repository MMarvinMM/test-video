package com.chinatower.event.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmDetailConfigVo extends BaseIndustryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 展示内容
     */
    private String displayContent;

    /**
     * 展示顺序
     */
    private Integer displayOrder;
}
