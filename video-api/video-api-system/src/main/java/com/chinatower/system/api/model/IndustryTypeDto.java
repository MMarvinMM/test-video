package com.chinatower.system.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 所属行业信息dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndustryTypeDto implements Serializable {

    private String industryTypeId;

    private String industryTypeName;

    private List<IndustryConfDto> list;
}
