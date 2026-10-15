package com.chinatower.system.api.model;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区域对象 sys_area
 *
 * @author wangzic
 * @date 2019-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysArea extends PageDTO {
    private static final long serialVersionUID = 1L;

    private long id;

    /**
     * 区域编码
     */
    @Excel(name = "区域编码")
    private String areaCode;

    /**
     * 区域编码集合
     */
    private List<String> areaCodeList;

    /**
     * 区域名称
     */
    @Excel(name = "区域名称")
    private String areaName;

    /**
     * 区域级别
     */
    @Excel(name = "区域级别", readConverterExp = "1=国家级,2=省份级,3=地市级,4=区县级")
    private String areaLevel;

    /**
     * 上级区域编码
     */
    @Excel(name = "上级区域编码")
    private String areaParentCode;

    /**
     * 上级区域名称
     */
    @Excel(name = "上级区域名称")
    private String areaParentName;

    /**
     * 区域状态
     */
    @Excel(name = "区域状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 所属分公司编码
     */
    @Excel(name = "所属分公司编码")
    private String deptId;

    /**
     * 所属分公司名称
     */
    @Excel(name = "所属分公司名称")
    private String deptName;

    /**
     * 归属主体
     **/
    private String bussnessUnit;

    /**
     * 经度坐标
     */
    @Excel(name = "经度坐标")
    private String longitude;

    /**
     * 纬度坐标
     */
    @Excel(name = "纬度坐标")
    private String latitude;

    /**
     * 下辖区域
     */
    private List<SysArea> children = new ArrayList<SysArea>();

}
