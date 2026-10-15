package com.chinatower.bdm.modules.layer.domain;

import lombok.Data;

import java.util.List;

/**
 * @author LQ
 * 图层树形结构
 */
@Data
public class layerTree {
    
    private String id;

    private String label;
    
    private String parentId;

    private int category;
    
    private List<layerTree> children;
    
}
