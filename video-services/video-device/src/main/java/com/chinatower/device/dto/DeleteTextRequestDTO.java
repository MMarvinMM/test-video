package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class DeleteTextRequestDTO {
    //id列表
    private List<String> txtIds;
    //接收节点
    private String acceptNode;
}
