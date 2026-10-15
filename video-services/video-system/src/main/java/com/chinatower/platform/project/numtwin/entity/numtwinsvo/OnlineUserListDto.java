package com.chinatower.platform.project.numtwin.entity.numtwinsvo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/15 14:33
 */

@Data
public class OnlineUserListDto {

    /**
     * 大屏唯一标识
     */
    private String uniqueIdentifier;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 当前页码
     */
    private String pageNum;

    /**
     * 当夜条数
     */
    private String pageSize;

    /**
     * 排序规则:有值是升序,没值时降序
     */
    private String sortDirection;
}
