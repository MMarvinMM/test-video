package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/5/8 10:45
 */
@Data
public class LabelAndColStatusResp {

    private List<LabelAndColStatusDto> resList;

    @Data
    public static class LabelAndColStatusDto{
        private String orderId;
        private List<LabelDto> labelList;
        private String status;
    }

    @Data
    public static class LabelDto{
        private String labelId;
        private String labelName;
    }
}
