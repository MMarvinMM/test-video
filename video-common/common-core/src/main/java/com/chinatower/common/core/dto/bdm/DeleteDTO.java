package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/4/6 14:17
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DeleteDTO {
    private List<String> ids;

    private String modifier;
    /**
     * 图层种类 1-矢量  2-非矢量
     */
    private String category = "1";
}
