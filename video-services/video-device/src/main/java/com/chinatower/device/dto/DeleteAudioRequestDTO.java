package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class DeleteAudioRequestDTO {
    //音频id列表 平台说 多个用逗号隔开
    private List<String> audioIdList;
    //接收节点
    private String acceptNode;
}
