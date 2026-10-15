package com.chinatower.common.core.vo;

import java.io.Serializable;

/**
 * @Description: 行政区域
 * @author: kangweixiang
 * @date: 2021年05月13日 15:31
 */
public class AdmRegionData implements Serializable {
    private String mdm_adm_code;
    private String adm_code;
    private String adm_name;
    private String adm_alias;
    private String adm_level;
    private String parent_adm_code;
    private String adm_status;
    private String last_level;
    private String change_type;
    private String relation_adm_code;
    private String adm_remark;
    private String created_by;
    private String creation_date;
    private String last_updated_by;
    private String last_update_date;

    public String getMdm_adm_code() {
        return mdm_adm_code;
    }

    public void setMdm_adm_code(String mdm_adm_code) {
        this.mdm_adm_code = mdm_adm_code;
    }

    public String getAdm_code() {
        return adm_code;
    }

    public void setAdm_code(String adm_code) {
        this.adm_code = adm_code;
    }

    public String getAdm_name() {
        return adm_name;
    }

    public void setAdm_name(String adm_name) {
        this.adm_name = adm_name;
    }

    public String getAdm_alias() {
        return adm_alias;
    }

    public void setAdm_alias(String adm_alias) {
        this.adm_alias = adm_alias;
    }

    public String getAdm_level() {
        return adm_level;
    }

    public void setAdm_level(String adm_level) {
        this.adm_level = adm_level;
    }

    public String getParent_adm_code() {
        return parent_adm_code;
    }

    public void setParent_adm_code(String parent_adm_code) {
        this.parent_adm_code = parent_adm_code;
    }

    public String getAdm_status() {
        return adm_status;
    }

    public void setAdm_status(String adm_status) {
        this.adm_status = adm_status;
    }

    public String getLast_level() {
        return last_level;
    }

    public void setLast_level(String last_level) {
        this.last_level = last_level;
    }

    public String getChange_type() {
        return change_type;
    }

    public void setChange_type(String change_type) {
        this.change_type = change_type;
    }

    public String getRelation_adm_code() {
        return relation_adm_code;
    }

    public void setRelation_adm_code(String relation_adm_code) {
        this.relation_adm_code = relation_adm_code;
    }

    public String getAdm_remark() {
        return adm_remark;
    }

    public void setAdm_remark(String adm_remark) {
        this.adm_remark = adm_remark;
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
}
