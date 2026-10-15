package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二次识别扩展字段参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogBackExtendVO {

    /**
     * 告警区域id
     */
    private String alarmAreaId;

    /**
     * 属性列表
     */
    private List<Attr> attrList;


    @Data
    public static class Attr{
        //属性类型
        private String attrType;

        //具体值
        private String attrValue;
    }
}
