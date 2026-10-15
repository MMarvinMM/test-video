package com.chinatower.bdm.modules.layer.domain;

import com.chinatower.common.core.dto.bdm.LayerTypeAttrFilterVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LayerTypeFieldDictDto implements Serializable {
    /**
     * 传了ID就是修改 没传就是新增
     */
    private String id;


    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 字段代码
     */
    private String code;

    /**
     * 字段显示名称
     */
    private String name;

    private Integer type;


    private List<LayerTypeAttrFilterVO> filters = new ArrayList<>();
}
