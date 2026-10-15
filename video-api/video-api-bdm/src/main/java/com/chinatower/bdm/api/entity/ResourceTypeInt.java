package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能说明：资源类型初始化信息
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Data
public class ResourceTypeInt extends BaseDataScopeEntity implements Serializable {
    //主键ID
    private Long id;

    //资源类型编码
    private String resourceTypeId;

    //资源类型名称
    @ApiModelProperty(value = "资源类型名称")
    private String resourceTypeName;

    //上级资源类型编码
    private String parentId;

    //上级资源类型名称
    private String parentName;

    //显示顺序
    private Long orderNum;

    //租户ID
//    private String tenantId;

    //租户名称
    private String tenantName;

    /**
     * 属性类型 1-点资源 2-线资源 3-面资源
     */
    private Integer attrType;

    //备注
    private String remark;

    //图标
    private String icon;

    // 默认状态（0是 1否）
    private String approve;

    //资源类型层级路径
    private String typeLevelUrl;

    //创建人
    private String creator;

    // 修改人
    private String modifier;


    //删除标志位
    private Integer deleteFlag;

    //行业版本编码集合
    private String appVerCode;

    // 状态
    private Integer status;

    private List<String> codeList;

    //删除状态: 原字典值：normal正常，删除deleted 现字典值：0正常，删除1
//    private short deleteFlag;

    private static final long serialVersionUID = 1L;
}
