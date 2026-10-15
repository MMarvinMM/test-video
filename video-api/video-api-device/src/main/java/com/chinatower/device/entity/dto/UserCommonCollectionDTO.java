package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 功能描述: 摄像机收藏实体类
 *
 * @auther: LQ
 * @requestID:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommonCollectionDTO extends BaseDataScopeEntity {
    /**
     * 收藏对象类型 1.资源2.网格3.网格员
     */
    @ApiModelProperty(value = "收藏对象类型 1.资源2.网格3.网格员")
    @NotEmpty(message = "收藏对象类型不能为空")
    private  String collObjType;
    /**
     * 收藏对象编码
     */
    @ApiModelProperty(value = "收藏对象编码")
    @NotEmpty(message = "收藏对象编码不能为空")
    private  List<String> collObjCode;
    /**
     * 操作标识 1：新增，2：删除
     */
    @ApiModelProperty(value = "操作标识 1：新增，2：删除")
    @NotNull(message = "操作标识不能为空")
    private Integer optType;


}
