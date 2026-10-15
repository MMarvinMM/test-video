package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 周边网格员搜索入参
 * @author zl
 */
@Data
public class KeeperPeripheryParam extends BaseDataScopeEntity implements Serializable {

    /**
     * 经度
     * @required true
     */
    @NotBlank(message = "经度不能为空")
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    @NotBlank(message = "纬度不能为空")
    private String latitude;

    /**
     * 检索半径，单位：米
     * @required true
     */
    @NotBlank(message = "检索半径不能为空")
    private String radius;

    /**
     * 类型
     */
    private String type;


}
