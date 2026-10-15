package com.chinatower.bdm.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 周边网格员搜索入参
 */
@Data
public class SaveLngLatParam {

    /**
     * 经度
     */
    @NotBlank(message = "经度不能为空")
    private String longitude;
    /**
     * 纬度
     */
    @NotBlank(message = "纬度不能为空")
    private String latitude;

    /**
     * 网格员id
     */
    @NotBlank(message = "网格员id不能为空")
    private String keeperId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;


}
