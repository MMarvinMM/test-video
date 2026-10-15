package com.chinatower.common.core.vo;

import com.chinatower.common.core.annotation.Excel;

import java.io.Serializable;

/**
 * @Description:管理区域和行政区域关系
 * @author: kangweixiang
 * @date: 2021年05月13日 15:53
 */
public class RegionAreaRelationData implements Serializable {
    @Excel(name = "管理区域编码")
    private String area_code;
    @Excel(name = "行政区域编码")
    private String adm_code;

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getAdm_code() {
        return adm_code;
    }

    public void setAdm_code(String adm_code) {
        this.adm_code = adm_code;
    }
}
