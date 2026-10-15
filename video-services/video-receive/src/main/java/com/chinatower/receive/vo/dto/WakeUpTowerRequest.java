package com.chinatower.receive.vo.dto;

import lombok.Data;

import java.util.List;

/**
 * 唤起小塔请求参数
 */
@Data
public class WakeUpTowerRequest {

    /**
     * 规则编码列表
     */
    private List<String> ruleCodes;
}