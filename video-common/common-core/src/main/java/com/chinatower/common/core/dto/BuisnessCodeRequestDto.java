package com.chinatower.common.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Mr.lin  on 2021/4/10.
 */
public class BuisnessCodeRequestDto implements Serializable {

    private String action;

    /**
     * 合同编码
     */
    private String contract_id;

    /**
     * 合同名称
     */
    private String contract_name;

    /**
     * 项目等级
     */
    private String prj_level;

    /**
     * 产品大类
     */
    private String product_class;

    /**
     * 产品型号
     */
    private String product_model;

    /**
     * 服务协议编码
     */
    private String agreement_code;

    /**
     * 商机编号
     */
    private String niche_code;

    /**
     * 商机名称
     */
    private String niche_name;

    /**
     * 客商编码
     */
    private String cus_id;

    /**
     * 客商名称
     */
    private String cus_name;

    /**
     * 创建用户ID
     */
    private String create_user_id;

    /**
     * 铁塔组织归属
     */
    private String create_org_id;

    private String sys_id;
    private String msg_id;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContract_id() {
        return contract_id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }

    public String getContract_name() {
        return contract_name;
    }

    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }

    public String getPrj_level() {
        return prj_level;
    }

    public void setPrj_level(String prj_level) {
        this.prj_level = prj_level;
    }

    public String getProduct_class() {
        return product_class;
    }

    public void setProduct_class(String product_class) {
        this.product_class = product_class;
    }

    public String getProduct_model() {
        return product_model;
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public String getAgreement_code() {
        return agreement_code;
    }

    public void setAgreement_code(String agreement_code) {
        this.agreement_code = agreement_code;
    }

    public String getNiche_code() {
        return niche_code;
    }

    public void setNiche_code(String niche_code) {
        this.niche_code = niche_code;
    }

    public String getNiche_name() {
        return niche_name;
    }

    public void setNiche_name(String niche_name) {
        this.niche_name = niche_name;
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }

    public String getCreate_org_id() {
        return create_org_id;
    }

    public void setCreate_org_id(String create_org_id) {
        this.create_org_id = create_org_id;
    }

    public String getSys_id() {
        return sys_id;
    }

    public void setSys_id(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getCus_id() {
        return cus_id;
    }

    public void setCus_id(String cus_id) {
        this.cus_id = cus_id;
    }

    public String getCus_name() {
        return cus_name;
    }

    public void setCus_name(String cus_name) {
        this.cus_name = cus_name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
