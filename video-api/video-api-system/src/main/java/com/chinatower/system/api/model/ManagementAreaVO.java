package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * @ClassName: ManagementAreaVO
 * @Description: 管理区域实体
 * @Author: liuxt
 * @Date: 2021-05-11 11:51
 **/
@Data
@ToString
public class ManagementAreaVO {


    /**
     * 页码
     */
    private int currentPage;            
    /**
     * 每页数量
     */
    private int pageSize;           
    /**
     * 管理区域编码
     */
    private String areaCode; 
    /**
     * 管理区域名称
     */
    private String areaName;          
    /**
     * 管理区域等级 0--中国铁塔集团\r\n1--总部；2--省分；3--地市；5-区域\r\n
     */
    private String areaLevel;          
    /**
     * 上级管理区域
     */
    private String parentAreaCode;          
    /**
     * 上级管理区域名
     */
    private String parentAreaName;          
    /**
     * 管理区域状态 1--正常；0--停用；\r\n2—挂起；
     */
    private String areaStatus;          
    /**
     * 是否末级区划
     */
    private String areaPath;          
    /**
     * 创建人
     */
    private String creator; 
    /**
     * 创建时间
     */
    private String creatorDate; 
    /**
     * 最后修改人
     */
    private String modifier; 
    /**
     * 最后修改时间
     */
    private String modifierDate; 
    /**
     * 有效状态
     */
    private String deleteFlag; 

    /**
     * 行政区域编码
     */
    private String regionCode;
    /**
     * 管理区域编码集合
     */
    private Set<String> managementAreaCodeSet;

}
