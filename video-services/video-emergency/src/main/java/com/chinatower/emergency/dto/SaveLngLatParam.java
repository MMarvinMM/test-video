package com.chinatower.emergency.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;


@Data
public class SaveLngLatParam  extends CommonFeignEntity {

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
    private String userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;


    private List<SaveLngLatParam> paramList;


}
