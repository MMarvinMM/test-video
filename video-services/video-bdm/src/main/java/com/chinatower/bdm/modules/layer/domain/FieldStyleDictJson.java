package com.chinatower.bdm.modules.layer.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldStyleDictJson implements Serializable {

    private String filterAttrKey;

    private String filterAttrValue;

    private String fillColor;

    private Integer fillTransparency;

    private String borderColor;

    private Integer borderTransparency;

    private String borderWidth;

    private RangeJson range;

    private String rangeLabel;
}
