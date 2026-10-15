package com.chinatower.system.api.resp;

import com.chinatower.platform.model.resp.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther liuhuanhuan
 * @date 2022/3/10
 */
@Data
@Deprecated
@ApiModel(value = "SysAreaResponse", description = "区域信息返回参数")
public class SysAreaResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    /**
     * 区域编码
     */
    @ApiModelProperty(value = "区域编码")
    private String areaCode;

    /**
     * 集合
     */
    private List<String> areaCodeList;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String areaName;

    /**
     * 区域级别：1=国家级,2=省份级,3=地市级,4=区县级
     */
    @ApiModelProperty(value = "区域级别：1=国家级,2=省份级,3=地市级,4=区县级")
    private String areaLevel;

    /**
     * 上级区域编码
     */
    @ApiModelProperty(value = "上级区域编码")
    private String areaParentCode;

    /**
     * 上级区域名称
     */
    @ApiModelProperty(value = "上级区域名称")
    private String areaParentName;

    /**
     * 区域状态：0=正常,1=停用
     */
    @ApiModelProperty(value = "区域状态：0=正常,1=停用")
    private String status;

    /**
     * 所属分公司编码
     */
    @ApiModelProperty(value = "所属分公司编码")
    private String deptId;

    /**
     * 所属分公司名称
     */
    @ApiModelProperty(value = "所属分公司名称")
    private String deptName;

    /**
     * 归属主体
     **/
    private String bussnessUnit;

    /**
     * 经度坐标
     */
    @ApiModelProperty(value = "经度坐标")
    private String longitude;

    /**
     * 纬度坐标
     */
    @ApiModelProperty(value = "纬度坐标")
    private String latitude;

    /**
     * 下辖区域
     */
    @ApiModelProperty(value = "下辖区域")
    private List<SysAreaResponse> children = new ArrayList<SysAreaResponse>();
}
