package com.chinatower.platform.auth.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 微信公众号底部菜单
 * </p>
 *
 * @author zhujun
 * @since 2022-11-20
 */
@Data
@TableName("bs_wx_bottom_menu")
public class WxBttomMenuEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "租户ID")
    @TableField(exist = false)
    private String tenantId;

    @ApiModelProperty(value = "上级菜单名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message = "菜单名称必填")
    private String name;

    @ApiModelProperty(value = "菜单编码")
    @NotBlank(message = "菜单编码必填")
    private String menuValue;

    @ApiModelProperty(value = "类型 text、 view")
    @NotBlank(message = "类型 text、 view必填")
    private String funcType;

    @ApiModelProperty(value = "菜单等级")
    @NotBlank(message = "菜单等级必填")
    private Integer menuLevel;

    @ApiModelProperty(value = "排序")
    @NotBlank(message = "排序必填")
    private Integer orderNum;

    @ApiModelProperty("菜单全路径")
    private String linkUrl;

    @ApiModelProperty(value = "状态 0未发布 1发布")
    private Integer status;

    @ApiModelProperty(value = "父菜单Id")
    private Long parentId;


    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "删除标识符0正常 1删除")
    private String deleteFlag;


}
