package com.chinatower.platform.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @desription: 请求参数
 * @data 创建时间：2023/12/19 13:56
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeoserverWfsDTO {

    /**
     * 请求工作空间
     */
    @NotBlank(message = "命名空间不能为空")
    private String workspace;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 其他参数
     */
    @NotNull(message = "参数错误")
    private Map<String,Object> params;
}
