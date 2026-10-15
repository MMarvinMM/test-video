package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName StreetDeptDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/5/18 21:12
 * @Version 1.0
 **/
@Data
public class StreetDeptDTO implements Serializable {
    
    /**
     * 组织id
     */
    private String deptId;
    
    /**
     * 省编码
     */
    private String provinceId;
    
    /**
     * 市编码
     */
    private String cityId;
    
    /**
     * 区县编码
     */
    private String countyId;
    
    /**
     * 乡镇编码
     */
    private String townCode;
}
