package com.chinatower.common.core.vo;

import java.io.Serializable;

/**
 * @Description: 管理区域
 * @author: kangweixiang
 * @date: 2021年05月13日 15:43
 */
public class OrgAreasData implements Serializable {
    private String mdm_area_code;
    private String area_code;
    private String area_name;
    private String area_level;
    private String area_parent_code;
    private String area_status;
    private String area_change;
    private String area_relation;
    private String area_remarks;
    private String area_path;
    private String created_by;
    private String creation_date;
    private String last_updated_by;
    private String last_update_date;
    private String enterp_type;

    public String getMdm_area_code() {
        return mdm_area_code;
    }

    public void setMdm_area_code(String mdm_area_code) {
        this.mdm_area_code = mdm_area_code;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getArea_level() {
        return area_level;
    }

    public void setArea_level(String area_level) {
        this.area_level = area_level;
    }

    public String getArea_parent_code() {
        return area_parent_code;
    }

    public void setArea_parent_code(String area_parent_code) {
        this.area_parent_code = area_parent_code;
    }

    public String getArea_status() {
        return area_status;
    }

    public void setArea_status(String area_status) {
        this.area_status = area_status;
    }

    public String getArea_change() {
        return area_change;
    }

    public void setArea_change(String area_change) {
        this.area_change = area_change;
    }

    public String getArea_relation() {
        return area_relation;
    }

    public void setArea_relation(String area_relation) {
        this.area_relation = area_relation;
    }

    public String getArea_remarks() {
        return area_remarks;
    }

    public void setArea_remarks(String area_remarks) {
        this.area_remarks = area_remarks;
    }

    public String getArea_path() {
        return area_path;
    }

    public void setArea_path(String area_path) {
        this.area_path = area_path;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getLast_updated_by() {
        return last_updated_by;
    }

    public void setLast_updated_by(String last_updated_by) {
        this.last_updated_by = last_updated_by;
    }

    public String getLast_update_date() {
        return last_update_date;
    }

    public void setLast_update_date(String last_update_date) {
        this.last_update_date = last_update_date;
    }

    public String getEnterp_type() {
        return enterp_type;
    }

    public void setEnterp_type(String enterp_type) {
        this.enterp_type = enterp_type;
    }
}
