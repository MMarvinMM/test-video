package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DictListVO {
    /*字典小类编码*/
    private String itemCode;

    /*字典小类名称*/
    private String itemName;
}
