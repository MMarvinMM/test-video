package com.chinatower.platform.entity.device;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/5/8 12:00
 */

@Data
public class MonitorTreeByLayerIdReq {

    /**
     * 图层id集合
     */
    private List<String> layerIds;
}
