package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BlackWhiteReq extends BaseIndustryEntity implements Serializable {
    private Integer id;

    //black黑名单 white白名单
    private String blackOrWhite;

    private String key;

    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 条数限制
     */
    private Integer pageSize;

    private int blackOrWhiteInt;

    private List<Integer> ids;
}
