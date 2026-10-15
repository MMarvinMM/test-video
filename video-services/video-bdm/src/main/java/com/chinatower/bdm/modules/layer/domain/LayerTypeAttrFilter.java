package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @desription:
 * @data 创建时间：2024/1/22 13:35
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer_type_attr_filter")
@ApiModel(value="LayerTypeAttrFilter对象", description="图层类别字段字典管理")
public class LayerTypeAttrFilter {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;


    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 筛选类型
     */
    private String filterType;

    /**
     * 筛选属性编号
     */
    private String filterAttrKey;

    /**
     * 筛选属性值
     */
    private String filterAttrValue;

    /**
     * 0文本 1下拉
     */
    private String showType;

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
     * 0正常1删除
     */
    private Integer deleteFlag;

    /**
     * 是否显示 0否 1是
     */
    private String isShow;


}
