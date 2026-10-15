package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/5/8 10:43
 */
@Data
public class LabelAndColStatusReq {

    private List<String> orderIds;
}
