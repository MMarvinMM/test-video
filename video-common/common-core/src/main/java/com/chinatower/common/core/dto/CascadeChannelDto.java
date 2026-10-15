package com.chinatower.common.core.dto;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.common.core.web.domain.BaseEntity;
import lombok.Data;

/**
 * 通道关系对象
 *
 * @author vlop
 * @date 2021-11-18
 */
@Data
public class CascadeChannelDto extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 通道编码
     */
    @Excel(name = "通道编码")
    private String channelCode;

    /**
     * 通道名称
     */
    @Excel(name = "通道名称")
    private String channelName;


}
