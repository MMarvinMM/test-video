package com.chinatower.platform.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @className: BaseRequest
 * @packageName: com.chinatower.platform.model.req
 * @description: 基础请求实体
 * @date: 2022-03-10 13:43
 * @author: 李峥
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataScope;

    @ApiModelProperty(value = "创建人",example = "createBy")
    private String createBy;

    @ApiModelProperty(value = "更新人",example = "updateBy")
    private String updateBy;

}
