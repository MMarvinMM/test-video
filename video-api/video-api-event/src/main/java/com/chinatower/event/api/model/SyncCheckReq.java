package com.chinatower.event.api.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/2/2 16:53
 */
@Data
public class SyncCheckReq {
   private List<Map> list;
}
