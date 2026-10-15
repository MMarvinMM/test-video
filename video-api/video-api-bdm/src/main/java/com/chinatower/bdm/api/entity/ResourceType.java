package com.chinatower.bdm.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能说明：资源类型管理对应表 resource_type
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Data
public class ResourceType extends BaseDataScopeEntity implements Serializable {
    //主键ID
    private Long id;
    
    // 资源类型编码
    private String resourceTypeId;
    
    //资源类型名称
    @ApiModelProperty(value = "资源类型名称")
    private String resourceTypeName;

    //上级资源类型编码
    private String parentId; //上级资源类型编码

    //上级资源类型名称
    private String parentName;

    //显示顺序
    private Long orderNum;

    //类型状态（0正常 1停用）
    private String status;

    //类型状态（0正常 1停用）
    private String oppositeStatus;

    //删除标志（0代表存在 2代表删除）
//    private short deleteFlag;


    //租户ID
//    private String tenantId;

    //租户名称
    private String tenantName;

    //备注
    private String remark;

    //图标
    private String icon;

    // 默认状态（0是 1否）
    private String approve;

    //省份名称
    private String provinceName;

    //地市名称
    private String cityName;
    
    //子类型
    private List<ResourceType> children = new ArrayList<ResourceType>();

    //资源类型层级路径
    private String typeLevelUrl;
    
    //行业编码
    private String industryCode;

    //创建人
    private String creator;

    //修改人
    private String modifier;

    //删除标志位
    private Integer deleteFlag;
    
    //排序字段
    private String prop;
    
    //排序方式
    private String order;

    private static final long serialVersionUID = 1L;

    private Set<String> resourceTypeIdList;

    //1:分页查询 2:查询数量
    private String queryType;

    private List<String> parentIdList;

    /**
     * 资源属性
     */
    private String resourceProperty;

    /**
     * 可调资源接口的行业列表
     */
    private List<String> resourceIndustry;


    // 默认状态标识（该参数代表页面上直接更新默认展示状态）
    private String approveFlag;
}
