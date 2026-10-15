package com.chinatower.bdm.api.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * resource_conf
 * @author 
 */
@Data
public class GridKeeperTree implements Serializable {
    private String id;
    private String label;
    private String pid;
    private List<TreeSelectByBdm> children;
    private String allTotal;
    private String type;
    private String lineTotal;
    private String center;
    private String isLine;
    private String tel;
    private String gridLevelCode;
    private String color;
    private String userId;
    private String userName;
    private String locationFlag;


}