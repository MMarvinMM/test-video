package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import com.chinatower.common.page.TableDataInfo;

/**
 * @ClassName TenantOrderInfoVO
 * @Deacription 租户订购信息对象
 * @Author chenmf
 * @Date 2022/3/8 19:03
 * @Version 1.0
 **/
@Data
@ApiModel(value = "租户订购信息对象",description = "租户订购信息对象")
public class TenantOrderInfoVO implements Serializable {
    
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    
    @ApiModelProperty(value = "客户名称")
    private String tenantName;
    
    @ApiModelProperty(value = "客户账号")
    private String tenantManager;
    
    @ApiModelProperty(value = "订购信息列表")
    private TableDataInfo orderInfoVOList;
}
