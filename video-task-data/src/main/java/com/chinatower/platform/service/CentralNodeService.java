package com.chinatower.platform.service;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:53
 */
public interface CentralNodeService {

    /**
     * 去ofs上传的下沉节点工单上传至es
     */
    void pushEsWarningOrderByOfs(String param);
}
