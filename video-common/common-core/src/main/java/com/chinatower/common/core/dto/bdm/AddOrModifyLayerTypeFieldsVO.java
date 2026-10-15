package com.chinatower.common.core.dto.bdm;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @desription: 新增或修改图层类型字段
 * @data 创建时间：2024/1/18 10:34
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class AddOrModifyLayerTypeFieldsVO {

    /**
     * 传了ID就是修改 没传就是新增
     */
    private String id;


    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 字段代码
     */
    private String code;

    /**
     * 字段显示名称
     */
    private String name;

    /**
     * 备注，说明
     */
    private String remark;

    /**
     * 类型 1:int 2:double 3:varchart 4:timestamp
     */
    private Integer type;

    /**
     * 字符类型长度
     */
    private Integer chartLength;

    /**
     * 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空）
     */
    private Integer notNullFlag;

    /**
     * 筛选类型（0文本，1下拉，默认：2空）
     */
    private Integer filterType;

    /**
     * 属性查询功能的顺序号（默认为0，起始为1）
     */
    private Integer filterIndex;

    /**
     * 图斑信息顺序号
     */
    private Integer identifyIndex;

    /**
     * 列表的顺序号
     */
    private Integer resultIndex;
    /**
     * 是否匹配字典 1-是
     */
    private Integer matchDictFlag;
    /**
     * 是否需要弹窗 1-是
     */
    private Integer needAlertFlag;
    /**
     * 是否面积字段
     */
    private int hasArea;
    /**
     * 是否有 图层 1-是
     */
    private int hasLayer;

    /**
     * 是否图斑编号
     */
    private Integer hasTbbh;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 需要删除字段字典的ID
     */
    private List<String> ids = new ArrayList<>();
    /**
     * 字典值
     */
    private List<LayerTypeAttrFilterVO> filters = new ArrayList<>();
}
