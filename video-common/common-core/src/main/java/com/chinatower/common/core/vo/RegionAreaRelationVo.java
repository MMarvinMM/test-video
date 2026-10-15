package com.chinatower.common.core.vo;

import com.chinatower.common.core.annotation.Excel;

import java.io.Serializable;

/**
 * @Description:
 * @author: kangweixiang
 * @date: 2021年05月13日 15:53
 */
public class RegionAreaRelationVo implements Serializable {
    @Excel(name = "管理区域编码")
    private String areaCode;
    @Excel(name = "行政区域编码")
    private String admCode;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAdmCode() {
        return admCode;
    }

    public void setAdmCode(String admCode) {
        this.admCode = admCode;
    }
}
