package com.chinatower.platform.project.numtwin.entity.numtwinsvo;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/15 12:05
 */

@Data
public class OfflinUserListReq {

    /**
     * 大屏唯一标识
     */
    private String uniqueIdentifier;

    /**
     * 用户名集合
     */
    List<String> userAccountList;
}
