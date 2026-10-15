package com.chinatower.platform.auth.project.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ButtomTreeVo implements TreeNode<Long, ButtomTreeVo> {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("菜单id")
    private Long id;

    @ApiModelProperty(value = "类型 view ")
    private String funcType;

    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单名称")
    private String name;

    /**
     * 菜单值（前端路由）
     */
    @ApiModelProperty("菜单值")
    private String menuValue;

    /**
     * 上级菜单id
     */
    @ApiModelProperty("上级菜单")
    private Long pid;

    /**
     * 菜单全路径（用/隔开）
     */
    @ApiModelProperty("菜单全路径")
    private String linkUrl;


    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer orderNum;



    @ApiModelProperty("子菜单")
    private List<ButtomTreeVo> children;


    @Override
    @ApiModelProperty("父节点")
    public Boolean root() {
        return pid == null;
    }


}
