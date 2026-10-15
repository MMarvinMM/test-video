package com.chinatower.bdm.api.entity;




import lombok.Data;


import java.io.Serializable;
import java.util.List;

/**
 * resource_conf
 * @author 
 */
@Data
public class ResourceConfTree  implements Serializable {
    private String id;
    private String label;
    private String pid;
    private List<TreeSelectByBdm> children;
    private String icon;
    private String type;
    /**
     * 资源属性
     */
    private String resourceProperty;

    private Integer isMonitor;

    /**
     * 是否是应急资源 1：是 0：否
     */
    private String isEmergency;
}